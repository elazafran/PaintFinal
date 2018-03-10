package com.example.elazafran.paintfinal;

import android.app.AlertDialog;
import android.app.Dialog;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.SharedPreferences;
import android.graphics.Color;
import android.net.Uri;
import android.os.Bundle;
import android.os.Environment;
import android.preference.PreferenceManager;
import android.provider.MediaStore;
import android.support.design.widget.FloatingActionButton;
import android.support.design.widget.Snackbar;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.util.Log;
import android.view.View;
import android.view.Menu;
import android.view.MenuItem;
import android.widget.Button;
import android.widget.ImageButton;
import android.widget.TextView;
import android.widget.Toast;

import java.io.File;
import java.io.IOException;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.UUID;

public class MainActivity extends AppCompatActivity implements View.OnClickListener {

    private String mCurrentPhotoPath;
    private static final int ACTION_TAKE_PHOTO_B = 1;
    private static final int READ_REQUEST_CODE = 42;
    private static final String CAMERA_DIR = "/dcim/";
    private static final String JPEG_FILE_PREFIX = "IMG_";
    private static final String JPEG_FILE_SUFFIX = ".jpg";

    private ImageButton btnSave, btnBorrar,btnPintar,btnNuevo;
    public static int color = Color.BLACK;
    public static int tamanio = 20;
    private SharedPreferences preferencias;

    ImageButton btnPreferencias;

    ImageButton negro;
    ImageButton blanco;
    ImageButton rojo;
    ImageButton verde;
    ImageButton azul;
    private static LienzoDibujo lienzo;
    float ppequenyo;
    float pmediano;
    float pgrande;
    float pdefecto;
    ImageButton trazo;
    ImageButton anyadir;
    ImageButton borrar;
    ImageButton guardar;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.content_main);
        Toolbar toolbar = (Toolbar) findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);

        preferencias = PreferenceManager.getDefaultSharedPreferences(this);

        negro = (ImageButton)findViewById(R.id.colornegro);
        blanco = (ImageButton)findViewById(R.id.colorblanco);
        rojo = (ImageButton)findViewById(R.id.colorrojo);
        verde = (ImageButton)findViewById(R.id.colorverde);
        azul = (ImageButton)findViewById(R.id.colorazul);
        trazo = (ImageButton)findViewById(R.id.trazo);
        anyadir = (ImageButton)findViewById(R.id.anyadir);
        borrar = (ImageButton)findViewById(R.id.borrar);
        guardar = (ImageButton)findViewById(R.id.guardar);

        negro.setOnClickListener(this);
        blanco.setOnClickListener(this);
        rojo.setOnClickListener(this);
        verde.setOnClickListener(this);
        azul.setOnClickListener(this);
        trazo.setOnClickListener(this);
        anyadir.setOnClickListener(this);
        borrar.setOnClickListener(this);
        guardar.setOnClickListener(this);

        lienzo = (LienzoDibujo)findViewById(R.id.lienzoDibujo);


        pdefecto = pmediano;

        btnPreferencias = (ImageButton) findViewById(R.id.preferencias);
        btnPreferencias.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                startActivity(new Intent(view.getContext(), Preferencias.class));
            }
        });


    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        // Inflate the menu; this adds items to the action bar if it is present.
        getMenuInflater().inflate(R.menu.menu_main, menu);
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        // Handle action bar item clicks here. The action bar will
        // automatically handle clicks on the Home/Up button, so long
        // as you specify a parent activity in AndroidManifest.xml.
        int id = item.getItemId();

        //noinspection SimplifiableIfStatement
        if (id == R.id.action_settings) {
            return true;
        }

        return super.onOptionsItemSelected(item);
    }

    public void setSaveEnable(boolean enable){
        guardar.setEnabled(enable);
    }

    @Override
    public void onClick(View v) {
        String color = null;


        switch (v.getId()){
            case R.id.anyadir:
                lienzo.NuevoDibujo();
                /*
                AlertDialog.Builder newDialog = new AlertDialog.Builder(this);
                newDialog.setTitle("Nuevo Dibujo");
                newDialog.setMessage("¿Comenzar nuevo dibujo (perderás el dibujo actual)?");
                newDialog.setPositiveButton("Aceptar", new DialogInterface.OnClickListener(){
                    public void onClick(DialogInterface dialog, int which){

                        lienzo.NuevoDibujo();
                        dialog.dismiss();
                    }
                });
                newDialog.setNegativeButton("Cancelar", new DialogInterface.OnClickListener(){
                    public void onClick(DialogInterface dialog, int which){
                        dialog.cancel();
                    }
                });
                newDialog.show();
                */
                break;
            case R.id.guardar:
                foto();
                Toast.makeText(this, "guardamos", Toast.LENGTH_SHORT).show();
                break;
            case R.id.colornegro:
                color = v.getTag().toString();
                this.color=Color.BLACK;
                //lienzo.setColor(color);
                break;
            case R.id.colorblanco:
                this.color=Color.WHITE;
                color = v.getTag().toString();
                //lienzo.setColor(color);
                break;
            case R.id.colorazul:
                this.color=Color.BLUE;
                color = v.getTag().toString();
               // lienzo.setColor(color);
                break;
            case R.id.colorverde:
                this.color=Color.GREEN;
                color = v.getTag().toString();
               // lienzo.setColor(color);
                break;
            case R.id.colorrojo:
                this.color=Color.RED;
                color = v.getTag().toString();
                //lienzo.setColor(color);
                break;

            default:

                break;
        }
    }

    private void foto() {

        Intent takePictureIntent = new Intent(MediaStore.ACTION_IMAGE_CAPTURE);

        File f = null;

        try {

            f = createImageFile();
            mCurrentPhotoPath = f.getAbsolutePath();
            takePictureIntent.putExtra(MediaStore.EXTRA_OUTPUT, Uri.fromFile(f));

        } catch (Exception e) {

            e.printStackTrace();
            f = null;
            mCurrentPhotoPath = null;

        }

        startActivityForResult(takePictureIntent, ACTION_TAKE_PHOTO_B);
    }
    private File createImageFile() throws IOException {
        // Create an image file name
        String timeStamp = new SimpleDateFormat("yyyyMMdd_HHmmss").format(new Date());
        String imageFileName = JPEG_FILE_PREFIX + timeStamp + "_";
        File cameraImage = getImageDir();
        File imageF = File.createTempFile(imageFileName, JPEG_FILE_SUFFIX, cameraImage);
        return imageF;
    }
    private File getImageDir() {
        File storageDir = null;

        if (Environment.MEDIA_MOUNTED.equals(Environment.getExternalStorageState())) {

            storageDir =  new File (
                    Environment.getExternalStorageDirectory()
                            + CAMERA_DIR
                            + "paintfinal"
            );

            if (storageDir != null) {
                if (! storageDir.mkdirs()) {
                    if (! storageDir.exists()){
                        Log.d("Camera Sample", "Error al crear el directorio");
                        return null;
                    }
                }
            }

        } else {
            Log.v(getString(R.string.app_name), "External storage is not mounted READ/WRITE.");
        }

        return storageDir;
    }
}
