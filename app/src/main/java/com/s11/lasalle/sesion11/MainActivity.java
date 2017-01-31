package com.s11.lasalle.sesion11;

import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.os.AsyncTask;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.Toast;

import com.squareup.picasso.Picasso;

import java.io.File;
import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.net.HttpURLConnection;
import java.net.URL;

import static android.R.attr.bitmap;
import static android.R.attr.format;

public class MainActivity extends AppCompatActivity {

    EditText link;
    Button download;
    ImageView image;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        link = (EditText) findViewById(R.id.urlIMG);
        download = (Button) findViewById(R.id.downloadimg);
        image = (ImageView) findViewById(R.id.imageView);

        download.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {

                /** Descargar imagen con Picasso (añadir librería en el gradle y sincronizar!!) **/
                //Picasso.with(getApplicationContext()).load(link.getText().toString()).into(image);

                /** Descargar imagen "manualmente" by FELIPE **/
                //new DownloadImageTask().execute(link.getText().toString());

                /** Descargar imagen y guardarla en el móvil **/
                new DownloadImage().execute(link.getText().toString());
            }
        });

    }

                        /***** BY FELIPE *****/

    /**private class DownloadImageTask extends AsyncTask<String, Integer, Bitmap> {
        @Override
        protected Bitmap doInBackground(String... urls) {
            return downloadImage(urls[0]);
        }

        @Override
        protected void onPostExecute(Bitmap bitmap) {
            image.setImageBitmap(bitmap);
        }

        private Bitmap downloadImage(String link) {
            Bitmap bitmap = null;
            try {
                //Definició URL
                URL url = new URL(link);

                //Configuració HttpURLConnection per fer la descàrrega
                HttpURLConnection httpURLConnection = (HttpURLConnection) url.openConnection();
                httpURLConnection.setDoInput(true);
                httpURLConnection.connect();

                //Rebem la informació en format InputStrem
                InputStream inputStream = httpURLConnection.getInputStream();

                //Transformem la informació a BitMap
                bitmap = BitmapFactory.decodeStream(inputStream);

            } catch (Exception e) {
                Log.d("downloadImage", "Exception, algo va mal D:");
                e.printStackTrace();
            }
            return bitmap;
        }
    }**/

             /***** DESCARGAR IMAGEN EN DISPOSITIVO *****/

    public void saveImage(Context context, Bitmap bitmap, String imageName) {
        FileOutputStream fileOutputStream;
        try {
            fileOutputStream = context.openFileOutput(imageName, Context.MODE_PRIVATE);
            bitmap.compress(Bitmap.CompressFormat.JPEG, 100, fileOutputStream);
            fileOutputStream.close();
        }catch (Exception e) {
            Log.d("saveImage", "Exception, algo va mal ):");
            e.printStackTrace();
        }
    }

    private  class DownloadImage extends AsyncTask<String, Void, Bitmap> {

        private Bitmap downloadImageBitmap(String urls){
            Bitmap bitmap = null;
            try {
                // Descargar imagen de URL
                InputStream inputStream = new URL(urls).openStream();
                // Pasamos la info inputStream a Bitmap
                bitmap = BitmapFactory.decodeStream(inputStream);
                inputStream.close();
            } catch (Exception e) {
                Log.d("DownloadImage", "Exception, algo va mal );");
                e.printStackTrace();
            }
            return  bitmap;
        }
        @Override
        protected Bitmap doInBackground(String... strings) {
            return downloadImageBitmap(strings[0]);
        }

        @Override
        protected void onPostExecute(Bitmap bitmap) {
            saveImage(getApplicationContext(), bitmap, "imagesesion11.jpeg");
            loadImageBitmap(getApplicationContext(), "imagesesion11.jpeg");
            File file = getApplicationContext().getFileStreamPath("imagesesion11.jpeg");
            String imageFullPath = file.getAbsolutePath();
            Log.d("Guardado en", imageFullPath.toString());
            if (file.exists()) Log.d("La imagen", "imagesesion11.jpeg ya existe!");
            image.setImageBitmap(bitmap);
        }
    }

    public Bitmap loadImageBitmap(Context context, String imageName) {
        Bitmap bitmap = null;
        FileInputStream fileInputStream;
        try{
            fileInputStream = context.openFileInput(imageName);
            bitmap = BitmapFactory.decodeStream(fileInputStream);
            fileInputStream.close();
        }catch (Exception e) {
            Log.d("loadImageBitmap", "Exception, algo va mal D;");
            e.printStackTrace();
        }
        return bitmap;
    }
}
