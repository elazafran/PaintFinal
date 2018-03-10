package com.example.elazafran.paintfinal;

import android.content.ContentValues;
import android.content.Context;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteOpenHelper;

/**
 * Created by elazafran on 10/3/18.
 */

public class DataBaseHelper extends SQLiteOpenHelper {

    // Variables
    public static final String DATABASE_NAME = "Paint.db"; // DataBase Name
    public static final String TABLE_NAME = "recientes"; // Table Name
    public static final String COL_1 = "ID"; // Colum 1
    public static final String COL_2 = "Name"; // Colum 2
    public static final String COL_3= "Path"; // Colum 3


    public DataBaseHelper(Context context) {
        super(context, DATABASE_NAME, null, 1);
    }


    @Override
    public void onCreate(SQLiteDatabase db) {
        db.execSQL("CREATE TABLE "+TABLE_NAME+" ("+COL_1+" INTEGER PRIMARY KEY AUTOINCREMENT,"
                +COL_2+" TEXT,"+COL_3+" TEXT)");
    }


    @Override
    public void onUpgrade(SQLiteDatabase db, int i, int i1) {
        db.execSQL("DROP TABLE IF EXISTS "+TABLE_NAME);
        onCreate(db);
    }


    public boolean insertData(String name, String subname){
        // Activamos que podemos escribir en la base de datos
        SQLiteDatabase db = this.getWritableDatabase();
        ContentValues contentValues = new ContentValues();
        contentValues.put(COL_2, name);
        contentValues.put(COL_3, subname);
        long result = db.insert(TABLE_NAME, null, contentValues);
        if (result==-1)
            return false;
        else
            return true;
    }


    public Cursor getAllData(){
        SQLiteDatabase db = this.getWritableDatabase();
        return db.rawQuery("SELECT * FROM "+TABLE_NAME, null);
    }

}