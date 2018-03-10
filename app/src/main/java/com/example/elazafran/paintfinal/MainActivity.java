package com.example.elazafran.paintfinal;

import android.content.Intent;
import android.content.SharedPreferences;
import android.database.Cursor;
import android.graphics.Color;
import android.net.Uri;
import android.os.Bundle;
import android.os.Environment;
import android.preference.PreferenceManager;
import android.provider.MediaStore;
import android.support.v7.app.AlertDialog;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.util.Log;
import android.view.View;
import android.view.Menu;
import android.view.MenuItem;
import android.widget.Button;
import android.widget.ImageButton;
import android.widget.Toast;

import java.io.File;
import java.io.IOException;
import java.text.SimpleDateFormat;
import java.util.Calendar;
import java.util.Date;

public class MainActivity extends AppCompatActivity implements View.OnClickListener {

    private String mCurrentPhotoPath;
    private static final int ACTION_TAKE_PHOTO_B = 1;
    private static final int READ_REQUEST_CODE = 42;
    private static final String CAMERA_DIR = "/dcim/";
    private static final String JPEG_FILE_PREFIX = "IMG_";
    private static final String JPEG_FILE_SUFFIX = ".jpg";

    private static LienzoDibujo lienzo;
    public static int color = Color.BLACK;
    public static int tamanio = 20;
    public static int tamanioPath; // Variable para el tamaño del pincel
    private SharedPreferences preferencias;
    private DataBaseHelper myDB;


    ImageButton negro;
    ImageButton blanco;
    ImageButton rojo;
    ImageButton verde;
    ImageButton azul;
    ImageButton amarillo;
    ImageButton magenta;

    float ppequenyo;
    float pmediano;
    float pgrande;
    float pdefecto;
    ImageButton mas;
    ImageButton trazo;
    ImageButton menos;
    ImageButton anyadir;
    ImageButton borrar;
    ImageButton guardar;
    ImageButton hacerfoto;
    ImageButton btnPreferencias;

    Button abrir;
    Button recientes;
    Button nuevo;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.content_main);
        Toolbar toolbar = (Toolbar) findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);
        myDB = new DataBaseHelper(this); // Declaraicon del objeto de base de datos
        preferencias = PreferenceManager.getDefaultSharedPreferences(this);

        negro = (ImageButton)findViewById(R.id.colornegro);
        rojo = (ImageButton)findViewById(R.id.colorrojo);
        verde = (ImageButton)findViewById(R.id.colorverde);
        azul = (ImageButton)findViewById(R.id.colorazul);
        amarillo= (ImageButton)findViewById(R.id.coloramarillo);
        magenta= (ImageButton)findViewById(R.id.colormagenta);
        azul = (ImageButton)findViewById(R.id.colorazul);

        mas = (ImageButton)findViewById(R.id.mas);
        trazo = (ImageButton)findViewById(R.id.trazo);
        menos = (ImageButton)findViewById(R.id.menos);
        borrar = (ImageButton)findViewById(R.id.borrar);
        guardar = (ImageButton)findViewById(R.id.guardar);
        hacerfoto = (ImageButton)findViewById(R.id.hacerfoto);

        nuevo = (Button)findViewById(R.id.nuevo);
        abrir = (Button)findViewById(R.id.abrir);
        recientes = (Button)findViewById(R.id.recientes);




        negro.setOnClickListener(this);
        amarillo.setOnClickListener(this);
        magenta.setOnClickListener(this);
        rojo.setOnClickListener(this);
        verde.setOnClickListener(this);
        azul.setOnClickListener(this);

        mas.setOnClickListener(this);
        trazo.setOnClickListener(this);
        menos.setOnClickListener(this);
        borrar.setOnClickListener(this);
        guardar.setOnClickListener(this);
        hacerfoto.setOnClickListener(this);

        nuevo.setOnClickListener(this);
        abrir.setOnClickListener(this);
        recientes.setOnClickListener(this);

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
            case R.id.mas:
                tamanioPath = Integer.parseInt(preferencias.getString("tamanio", ""))+10; // Accion: Aumentar a 5 el tamaño del lapiz
                preferencias.edit().putString("tamanio", String.valueOf(tamanioPath)).commit(); // Actualizamos preferencias
                Toast.makeText(this, "más tamaño", Toast.LENGTH_SHORT).show();
                break;
            case R.id.menos:
                tamanioPath = Integer.parseInt(preferencias.getString("tamanio", ""))-10; // Accion: Aumentar a 5 el tamaño del lapiz
                preferencias.edit().putString("tamanio", String.valueOf(tamanioPath)).commit(); // Actualizamos preferencias
                Toast.makeText(this, "menos tamaño", Toast.LENGTH_SHORT).show();
                break;
             /*case R.id.anyadir:
                lienzo.NuevoDibujo();

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


                break;
              */
            case R.id.nuevo:

                Toast.makeText(this, "nuevo", Toast.LENGTH_SHORT).show();
                break;
            case R.id.recientes:
                Cursor res = myDB.getAllData();
                if (res.getCount() == 0){ // Si no tiene filas, cargamos mensaje de error y salimos
                    mostrarMensaje("Error", "Nothing found");
                    return;
                }
                // Si tiene cargamo un buffer con los datos y lo cargamos
                StringBuffer buffer = new StringBuffer();
                while (res.moveToNext()){
                    buffer.append("Name: "+res.getString(1)+"\n");
                    buffer.append("Ruta: "+res.getString(2)+"\n\n");

                }
                mostrarMensaje("Recientes", buffer.toString());
                Toast.makeText(this, "recientes", Toast.LENGTH_SHORT).show();
                break;
            case R.id.abrir:

                Toast.makeText(this, "abrir", Toast.LENGTH_SHORT).show();
                break;
            case R.id.guardar:
                // Generamos nombre
                Date today = Calendar.getInstance().getTime();
                SimpleDateFormat formatter = new SimpleDateFormat("yyyyMMddhhmmss");
                String folderName = "IMG_"+formatter.format(today)+".png";

                lienzo.saveBitmap(folderName); // Llamamos al metodo de guardar

                // Añadimos a la base de datos
                boolean  isInserted = myDB.insertData(folderName,lienzo.outPath+"/"+folderName);
                if (isInserted == true)
                    Toast.makeText(MainActivity.this, "Data Inserted", Toast.LENGTH_LONG).show();
                else
                    Toast.makeText(MainActivity.this, "Data not Inserted", Toast.LENGTH_LONG).show();
                Toast.makeText(this, "guardamos", Toast.LENGTH_SHORT).show();
                break;
            case R.id.hacerfoto:
                hacerFoto();
                Toast.makeText(this, "hacemos foto", Toast.LENGTH_SHORT).show();
                break;
            case R.id.borrar:
                this.color=Color.WHITE;
                Toast.makeText(this, "pintamos de blanco", Toast.LENGTH_SHORT).show();
                break;
            case R.id.colornegro:
                this.color=Color.BLACK;
                color = v.getTag().toString();
                //lienzo.setColor(color);
                break;
            case R.id.coloramarillo:
                this.color=Color.YELLOW;
                color = v.getTag().toString();
                Toast.makeText(this, "pintamos de amarillo", Toast.LENGTH_SHORT).show();
                //lienzo.setColor(color);
                break;
            case R.id.colormagenta:
                this.color=Color.MAGENTA;
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
                Toast.makeText(this, "pintamos de verde", Toast.LENGTH_SHORT).show();
               // lienzo.setColor(color);
                break;
            case R.id.colorrojo:
                this.color=Color.RED;
                Toast.makeText(this, "pintamos de rojo", Toast.LENGTH_SHORT).show();
                color = v.getTag().toString();
                //lienzo.setColor(color);
                break;


            default:

                break;
        }
    }

    /**
     *  utilizamos intent especificos para tomar la foto
     */
    private void hacerFoto() {

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

    /**
     * Creamos el archivo con un formato de hora, prefiojo y sufijo
     * @return devolvemos un fichero
     * @throws IOException
     */
    private File createImageFile() throws IOException {
        // Create an image file name
        String timeStamp = new SimpleDateFormat("yyyyMMdd_HHmmss").format(new Date());
        String imageFileName = JPEG_FILE_PREFIX + timeStamp + "_";
        File cameraImage = getImageDir();
        File imageF = File.createTempFile(imageFileName, JPEG_FILE_SUFFIX, cameraImage);
        return imageF;
    }

    /**
     * Obtener la imagen
     *
     * @return retornamos una objeto file
     */
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

    /**
     * mostrar de la consulta
     *
     * @param title titulo de la ventana
     * @param message Mensaje da mostrar
     */
    private void mostrarMensaje(String title, String message){
        AlertDialog.Builder builder = new AlertDialog.Builder(this);
        builder.setCancelable(true);
        builder.setTitle(title);
        builder.setMessage(message);
        builder.show();
    }


}
