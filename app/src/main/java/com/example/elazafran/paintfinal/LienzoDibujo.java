package com.example.elazafran.paintfinal;



import android.annotation.SuppressLint;
import android.content.Context;
import android.content.SharedPreferences;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.Canvas;
import android.graphics.Color;
import android.graphics.Paint;
import android.graphics.Path;
import android.graphics.PorterDuff;
import android.os.Build;
import android.os.Environment;
import android.preference.PreferenceManager;
import android.support.annotation.RequiresApi;
import android.util.AttributeSet;
import android.view.MotionEvent;
import android.view.SurfaceHolder;
import android.view.SurfaceView;
import android.widget.Toast;

import java.io.File;
import java.io.FileOutputStream;
import java.io.InputStream;



/**
 * Created by elazafran on 6/3/18.
 */




@SuppressLint("AppCompatCustomView")

public class LienzoDibujo extends SurfaceView implements SurfaceHolder.Callback {

    //Propiedades recomendadas
    //  Hilo de dibujo
    private HiloDibujo hiloDibujo;
    //  Mapa de bits
    private Bitmap bitMap;
    //  Fichero del mapa de bits
    private String bitMapFile="";
    //  Posición contacto en X
    private float touched_x;
    //  Posicion contacto en y
    private float touched_y;
    // Dibuja ruta con el dedo
    private Path drawPath;
    //Paint de dibujar
    private static Paint drawPaint;

    //Color Inicial
    private static int paintColor = 0xFFFF0000;
    //canvas
    private Canvas drawCanvas;
    //canvas para guardar
    private Bitmap canvasBitmap;

    static float TamanyoPunto;
    private static boolean borrado=false;
    public  final File outPath = new File("/sdcard/DCIM/paintfinal"); // Ruta donde guardaremos las imagenes;
    private String sourceFileName;

    private SharedPreferences preferencias;

    private Bitmap scaled;

    public LienzoDibujo(Context context, AttributeSet attrs) {
        super(context, attrs);

        if (!bitMapFile.isEmpty()) {
            bitMap = BitmapFactory.decodeFile(bitMapFile);

        }
        preferencias = PreferenceManager.getDefaultSharedPreferences(context);


        //suscribir la instancia de la clase al callback del holder
        getHolder().addCallback(this);
        // trazos para el dibujo
        drawPath = new Path();
        // hilo para dibujar
        hiloDibujo = new HiloDibujo(getHolder(), MainActivity.color);
        bitMap = Bitmap.createBitmap (480, 740, Bitmap.Config.ARGB_8888);
    }

    public LienzoDibujo(Context context){
        this(context, null);
    }
    public Bitmap getBitMap(){
        return this.bitMap;
    }


    @SuppressLint("NewApi")
    public  void setBitmap(InputStream file, String fileName, File path){
        try {


            this.sourceFileName = fileName;

            this.bitMap = BitmapFactory.decodeStream(file);
            file.close();

            if(this.bitMap!=null) {
                this.bitMap.setWidth(getWidth());
                this.bitMap.setHeight(getHeight());
                hiloDibujo.start();

            }
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    /**
     * Guardamos la imagen en la sd
     * @param nombre con este parametro será guardado
     */
    public void saveBitmap(String nombre){
        try {
            FileOutputStream outFile = new FileOutputStream(new File(Environment.getExternalStorageDirectory(),nombre));

            bitMap.compress(Bitmap.CompressFormat.PNG,100,
                    outFile);

            outFile.flush();
            outFile.close();
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    @Override
    public void surfaceCreated(SurfaceHolder surfaceHolder) {
        Bitmap background = BitmapFactory.decodeResource(getResources(), R.drawable.bg);
        float scale = (float)background.getHeight()/(float)getHeight();
        int newWidth = Math.round(background.getWidth()/scale);
        int newHeight = Math.round(background.getHeight()/scale);
        scaled = Bitmap.createScaledBitmap(background, newWidth, newHeight, true);

    }

    @Override
    public void surfaceChanged(SurfaceHolder surfaceHolder, int i, int width, int height) {
        // Crea un bitmap con las dimensiones del view
        bitMap = Bitmap.createBitmap (width, height, Bitmap.Config.ARGB_8888);
    }

    @Override
    public void surfaceDestroyed(SurfaceHolder surfaceHolder) {
        this.finalizarHiloDibujo();
    }
    private void finalizarHiloDibujo(){
        boolean retry = true;
        while (retry) {
            try {
                hiloDibujo.join();
                retry = false;
            } catch (InterruptedException e) {
                e.printStackTrace();
            }
        }
    }

    @Override
    public boolean onTouchEvent(MotionEvent event) {
        touched_x = event.getX();
        touched_y = event.getY();

        int action = event.getAction();
        switch(action){
            case MotionEvent.ACTION_DOWN:
                drawPath.moveTo(touched_x, touched_y);
                break;
            case MotionEvent.ACTION_MOVE:
                drawPath.lineTo(touched_x, touched_y);
                hiloDibujo = new HiloDibujo(getHolder(), MainActivity.color);
                hiloDibujo.start();
                ((MainActivity)getContext()).setSaveEnable(true);
                break;
            default:
                return false;
        }
        return true;
    }
    public void NuevoDibujo(){
        // drawCanvas.drawColor(0, PorterDuff.Mode.CLEAR);
        // drawCanvas.drawColor(0, PorterDuff.Mode.CLEAR);
        //drawCanvas.drawColor(Color.TRANSPARENT, PorterDuff.Mode.CLEAR);

        try {

            canvasBitmap = Bitmap.createBitmap(200, 300, Bitmap.Config.ARGB_8888);
            drawCanvas = new Canvas(canvasBitmap);
            drawCanvas.drawColor(0, PorterDuff.Mode.CLEAR);
            invalidate();

        }catch (Exception e){
            e.printStackTrace();
        }

        //invalidate();

    }




    //Actualiza color
    public void setColor(String newColor){
        invalidate();
        paintColor = Color.parseColor(newColor);
        drawPaint.setColor(paintColor);
    }




    //set borrado true or false
    public static void setBorrado(boolean estaborrado){
        borrado=estaborrado;
        if(borrado) {

            drawPaint.setColor(Color.WHITE);
            //drawPaint.setXfermode(new PorterDuffXfermode(PorterDuff.Mode.CLEAR));

        }
        else {
            drawPaint.setColor(paintColor);
            //drawPaint.setXfermode(null);
        }
    }



    class HiloDibujo extends Thread{
        // soporte de la superficie de dibujo
        private SurfaceHolder holder;
        // lienzo, la superficie de dibujo
        private Canvas canvas;
        // brocha para pintar
        private Paint drawPaint = new Paint();;
        // Color para pintar
        private int color;

        public HiloDibujo(SurfaceHolder holder, int color){
            this.holder = holder;
            this.color = color;
        }

        @Override
        public void run() {

            boolean retry=true;

            if (holder.getSurface().isValid()) {
                try {
                    canvas = holder.lockCanvas(null);

                    canvas.drawBitmap(Bitmap.createBitmap (LienzoDibujo.this.getWidth()
                            , LienzoDibujo.this.getHeight(), Bitmap.Config.ARGB_8888), 0,0,null);
                    drawPaint.setColor(Integer.parseInt(preferencias.getString("color", "Azul")));
                    drawPaint.setAntiAlias(true);
                    drawPaint.setStrokeWidth(Integer.parseInt(preferencias.getString("tamanio", "")));
                    drawPaint.setStyle(Paint.Style.STROKE);
                    drawPaint.setStrokeJoin(Paint.Join.ROUND);
                    drawPaint.setStrokeCap(Paint.Cap.ROUND);
                    canvas.drawPath(drawPath, drawPaint);
                    bitMap = getDrawingCache();

                }catch (Exception e){
                    e.printStackTrace();
                }
                finally {
                    if (canvas != null)
                        holder.unlockCanvasAndPost(canvas);
                }
            }
//            while (retry) {
//                try {
//                    this.join();
//                    retry = false;
//                } catch (InterruptedException e) {
//
//                }
//            }
        }
    }


}

