package com.example.elazafran.paintfinal;

import android.content.DialogInterface;
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

/**
 * Clase principal de la aplicación, que implementa onclicklistenner
 *
 * @author elazafran
 *
 */
public class MainActivity extends AppCompatActivity implements View.OnClickListener {


    // definimos las constantes

    private static final int ACTION_TAKE_PHOTO_B = 1;
    private static final int READ_REQUEST_CODE = 42;
    private static final String CAMERA_DIR = "/dcim/";
    private static final String JPEG_FILE_PREFIX = "IMG_";
    private static final String JPEG_FILE_SUFFIX = ".jpg";

    // definimos las variables
    private static LienzoDibujo lienzo;
    public static int color = Color.BLACK;
    public static int tamanio = 20;
    // Variable para el tamaño del pincel
    public static int tamanioPath;
    private SharedPreferences preferencias;
    private DataBaseHelper myDB;
    private String mCurrentPhotoPath;


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

        //inicializamos las variables
        negro = (ImageButton)findViewById(R.id.colornegro);
        rojo = (ImageButton)findViewById(R.id.colorrojo);
        verde = (ImageButton)findViewById(R.id.colorverde);
        azul = (ImageButton)findViewById(R.id.colorazul);
        amarillo= (ImageButton)findViewById(R.id.coloramarillo);
        magenta= (ImageButton)findViewById(R.id.colormagenta);
        azul = (ImageButton)findViewById(R.id.colorazul);

        mas = (ImageButton)findViewById(R.id.mas);

        menos = (ImageButton)findViewById(R.id.menos);
        borrar = (ImageButton)findViewById(R.id.borrar);
        guardar = (ImageButton)findViewById(R.id.guardar);
        hacerfoto = (ImageButton)findViewById(R.id.hacerfoto);

        nuevo = (Button)findViewById(R.id.nuevo);
        abrir = (Button)findViewById(R.id.abrir);
        recientes = (Button)findViewById(R.id.recientes);

        // suscribimos al evento onclick los bontones
        negro.setOnClickListener(this);
        amarillo.setOnClickListener(this);
        magenta.setOnClickListener(this);
        rojo.setOnClickListener(this);
        verde.setOnClickListener(this);
        azul.setOnClickListener(this);

        mas.setOnClickListener(this);

        menos.setOnClickListener(this);
        borrar.setOnClickListener(this);
        guardar.setOnClickListener(this);
        hacerfoto.setOnClickListener(this);
        recientes.setOnClickListener(this);

        nuevo.setOnClickListener(this);
        abrir.setOnClickListener(this);

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

    /**
     *  Recogida del evento y lazamos acción
     *
     * @param v vista sobre la que se ha hecho click
     */
    @Override
    public void onClick(View v) {
        String color = null;


        switch (v.getId()){

            case R.id.mas:

                // aumentamos tamaño de la brocha
                tamanioPath = Integer.parseInt(preferencias.getString("tamanio", "20"))+10; // Accion: Aumentar a 5 el tamaño del lapiz
                preferencias.edit().putString("tamanio", String.valueOf(tamanioPath)).commit(); // Actualizamos preferencias
                Toast.makeText(this, "más tamaño", Toast.LENGTH_SHORT).show();
                break;
            case R.id.menos:

                // disminuimos el tamaño de la brocha
                tamanioPath = Integer.parseInt(preferencias.getString("tamanio", "20"))-10; // Accion: Aumentar a 5 el tamaño del lapiz
                preferencias.edit().putString("tamanio", String.valueOf(tamanioPath)).commit(); // Actualizamos preferencias
                Toast.makeText(this, "menos tamaño", Toast.LENGTH_SHORT).show();
                break;
             case R.id.nuevo:

                 //borramos el canvas
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


            case R.id.recientes:
                //mostramos los archivos recientes guardados con la app

                Cursor res = myDB.getAllData();
                // si no hay archivos mostramos error
                if (res.getCount() == 0){
                    mostrarMensaje("Error", "no hay archivos previos");
                    return;
                }
                // añadimos valores al string para pintar
                StringBuffer buffer = new StringBuffer();
                while (res.moveToNext()){
                    buffer.append("Name: "+res.getString(1)+"\n");
                    buffer.append("Ruta: "+res.getString(2)+"\n\n");

                }
                mostrarMensaje("Recientes", buffer.toString());
                Toast.makeText(this, "recientes", Toast.LENGTH_SHORT).show();
                break;
            case R.id.abrir:


                // ACTION_OPEN_DOCUMENT intent para seleccionar el fichero desde
                // el explorador de ficheros del sistema
                //Cuando la aplicación envía la intent ACTION_OPEN_DOCUMENT,
                //lanza un selector que muestra todos los proveedores de documentos coincidentes.
                Intent intent = new Intent("android.intent.action.GET_CONTENT");//new Intent(Intent.ACTION_OPEN_DOCUMENT);

                // Al agregar la categoría CATEGORY_OPENABLE a la intent filtra
                // los resultados para mostrar solo documentos que se pueden abrir, como archivos de imagen
                intent.addCategory(Intent.CATEGORY_OPENABLE);

                // sólo nos interesan las imágenes
                intent.setType("image/*");

                startActivityForResult(intent, READ_REQUEST_CODE);

                Toast.makeText(this, "abrir", Toast.LENGTH_SHORT).show();
                break;
            case R.id.guardar:

                // creamos un patron para guardar los archivos
                Date today = Calendar.getInstance().getTime();
                SimpleDateFormat formatter = new SimpleDateFormat("yyyyMMddhhmmss");
                String folderName = JPEG_FILE_PREFIX+formatter.format(today)+JPEG_FILE_SUFFIX;

                // guardamos
                lienzo.saveBitmap(folderName);

                // insertamos en bbdd
                boolean  isInserted = myDB.insertData(folderName,lienzo.outPath+"/"+folderName);
                if (isInserted == true)
                    Toast.makeText(MainActivity.this, "guardado", Toast.LENGTH_LONG).show();
                else
                    Toast.makeText(MainActivity.this, "no se ha podido guardar", Toast.LENGTH_LONG).show();
                Toast.makeText(this, "guardamos", Toast.LENGTH_SHORT).show();
                break;
            case R.id.hacerfoto:
                // hacemos fotos para añadir al canvas
                hacerFoto();
                Toast.makeText(this, "hacemos foto", Toast.LENGTH_SHORT).show();
                break;
            case R.id.borrar:
                //pintamos de blanco
                this.color=Color.WHITE;
                Toast.makeText(this, "pintamos de blanco", Toast.LENGTH_SHORT).show();
                break;
            case R.id.colornegro:
                this.color=Color.BLACK;
                color = v.getTag().toString();
                Toast.makeText(this, "pintamos de negro", Toast.LENGTH_SHORT).show();
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
                Toast.makeText(this, "pintamos de magenta", Toast.LENGTH_SHORT).show();
                //lienzo.setColor(color);
                break;
            case R.id.colorazul:
                this.color=Color.BLUE;
                color = v.getTag().toString();
                Toast.makeText(this, "pintamos de azul", Toast.LENGTH_SHORT).show();
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
                this.color=Color.BLACK;
                break;
        }
        // una vez seleccionado el color, lo cambiamos en las preferencias
        preferencias.edit().putString("color", String.valueOf(this.color)).commit();
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
     *
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

    /**
     * Abre un intent para seleccionar el fichero de imagen
     */
    public void abrir() {

        // ACTION_OPEN_DOCUMENT intent para seleccionar el fichero desde
        // el explorador de ficheros del sistema
        //Cuando la aplicación envía la intent ACTION_OPEN_DOCUMENT,
        //lanza un selector que muestra todos los proveedores de documentos coincidentes.
        Intent intent = new Intent("android.intent.action.GET_CONTENT");//new Intent(Intent.ACTION_OPEN_DOCUMENT);

        // Al agregar la categoría CATEGORY_OPENABLE a la intent filtra
        // los resultados para mostrar solo documentos que se pueden abrir, como archivos de imagen
        intent.addCategory(Intent.CATEGORY_OPENABLE);

        // sólo nos interesan las imágenes
        intent.setType("image/*");

        startActivityForResult(intent, READ_REQUEST_CODE);
    }


}
