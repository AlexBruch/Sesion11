package com.s11.lasalle.sesion11;

import android.Manifest;
import android.app.Activity;
import android.app.NotificationManager;
import android.app.PendingIntent;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.net.Uri;
import android.os.AsyncTask;
import android.os.Build;
import android.os.Bundle;
import android.os.Environment;
import android.support.annotation.NonNull;
import android.support.v4.app.ActivityCompat;
import android.support.v4.content.ContextCompat;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.app.NotificationCompat;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.Toast;

import java.io.File;
import java.io.FileOutputStream;
import java.io.InputStream;
import java.net.HttpURLConnection;
import java.net.URL;

import static android.R.attr.bitmap;

public class MainActivity extends AppCompatActivity{

    private EditText link;
    private Button download;
    private ImageView image;
    private Bitmap saveImage;
    private int REQUEST_CODE = 1;

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

                /** Descargar imagen con Picasso (¡¡Añadir librería en el gradle y sincronizar!!) **/
                //Picasso.with(getApplicationContext()).load(link.getText().toString()).into(image);

                /** Descargar imagen "manualmente" **/
                new DownloadImageTask().execute(link.getText().toString());
            }
        });
    }

    private class DownloadImageTask extends AsyncTask<String, Integer, Bitmap> {
        @Override
        protected Bitmap doInBackground(String... urls) {
            return downloadImage(urls[0]);
        }

        @Override
        protected void onPostExecute(Bitmap downloadedbitmap2) { // Main Thread
            image.setImageBitmap(downloadedbitmap2);
            createNotificationActivity(downloadedbitmap2);
        }
    }

    private Bitmap downloadImage(String link) {
        Bitmap downloadedbitmap = null;
        try {
            //Definició URL
            URL url = new URL(link);

            //Configuració HttpURLConnection per fer la descàrrega
            HttpURLConnection httpURLConnection = (HttpURLConnection) url.openConnection();
            httpURLConnection.setDoInput(true);
            httpURLConnection.connect();

            //Rebem la informació com a InputStrem
            InputStream inputStream = httpURLConnection.getInputStream();

            //Transformem la informació a BitMap
            downloadedbitmap = BitmapFactory.decodeStream(inputStream);

        } catch (Exception e) {
            Log.e("downloadImage", "Exception, algo va mal D:");
            e.printStackTrace();
        }
        return downloadedbitmap;
    }

    @Override
    public void onRequestPermissionsResult(int requestCode, @NonNull String[] permissions, @NonNull int[] grantResults) {
        if (requestCode == REQUEST_CODE) {
            if(grantResults.length > 0 && grantResults[0] == PackageManager.PERMISSION_GRANTED) {
                String[] splitLink = link.getText().toString().split("/");
                String imageName = splitLink[splitLink.length-1];
                File file = new File(Environment.getExternalStoragePublicDirectory(Environment.DIRECTORY_PICTURES), imageName);
                try {
                    FileOutputStream fileOutputStream = new FileOutputStream(file);
                    saveImage.compress(Bitmap.CompressFormat.PNG, 100, fileOutputStream);
                    fileOutputStream.flush(); // Optimizació: per borrar fluxe de bytes de sortida i obligar als que estan en memoria a escriures
                    fileOutputStream.close();
                    file.setReadable(true, false); // Per permetre acces de lectura a totes les aplicacions
                    if (!file.mkdirs()) Log.e("Crear carpeta 3", "No rutlla");

                } catch (Exception e) {Log.e("Guardar imatge 3", "No rutlla");}
            }
        }
    }

    private void createNotificationActivity(Bitmap bitmap) {

        /** Per agafar nom original de la imatge **/

        String[] splitLink = link.getText().toString().split("/");
        String imageName = splitLink[splitLink.length-1];

        /** Constructor per a la notificació **/

        NotificationCompat.Builder builder = new NotificationCompat.Builder(MainActivity.this);

        /** Icona+text per mostrar a la notificació **/

        builder.setSmallIcon(android.R.drawable.ic_menu_gallery).setLargeIcon(bitmap);
        builder.setContentTitle(imageName);
        builder.setLargeIcon(bitmap);
        builder.setContentText("Text text text");

        /** Mostrar la imatge a la notificació **/

        NotificationCompat.BigPictureStyle bigPictureStyle = new NotificationCompat.BigPictureStyle();
        bigPictureStyle.bigPicture(bitmap);
        bigPictureStyle.setBigContentTitle(imageName);
        bigPictureStyle.setBigContentTitle("Text style");
        builder.setStyle(bigPictureStyle);

        Intent Intent = new Intent(MainActivity.this, MainActivity.class);
        PendingIntent pendingIntent = PendingIntent.getActivity(MainActivity.this, 0, Intent, 0);

        /** GUARDAR IMATGE **/

        saveImage = bitmap;
        File file = new File(Environment.getExternalStoragePublicDirectory(Environment.DIRECTORY_PICTURES), imageName);

        //Comprovem la versió d'Android
        if(Build.VERSION.SDK_INT >= Build.VERSION_CODES.M) {
            if(checkSelfPermission(Manifest.permission.WRITE_EXTERNAL_STORAGE) != PackageManager.PERMISSION_GRANTED) {
                // Demanem permís a l'usuari
                requestPermissions(new String[]{Manifest.permission.WRITE_EXTERNAL_STORAGE}, REQUEST_CODE);
            }else {
                // Si ja està donat
                try {
                    FileOutputStream fileOutputStream = new FileOutputStream(file);
                    saveImage.compress(Bitmap.CompressFormat.PNG, 100, fileOutputStream);
                    fileOutputStream.flush(); // Optimizació: per borrar fluxe de bytes de sortida i obligar als que estan en memoria a escriures
                    fileOutputStream.close();
                    file.setReadable(true, false); // Per permetre acces de lectura a totes les aplicacions
                    if (!file.mkdirs()) Log.e("Crear carpeta", "No rutlla");

                } catch (Exception e) {Log.e("Guardar imatge", "No rutlla"); e.printStackTrace();}
            }
        }else {
            try {
                FileOutputStream fileOutputStream = new FileOutputStream(file);
                saveImage.compress(Bitmap.CompressFormat.PNG, 100, fileOutputStream);
                fileOutputStream.flush(); // Optimizació: per borrar fluxe de bytes de sortida i obligar als que estan en memoria a escriures
                fileOutputStream.close();
                file.setReadable(true, false); // Per permetre acces de lectura a totes les aplicacions
                if (!file.mkdirs()) Log.e("Crear carpeta 2", "No rutlla");

            } catch (Exception e) {Log.e("Guardar imatge 2", "No rutlla");}
        }

        /** ENVIAR IMATGE **/

        Intent shareIntent = new Intent(Intent.ACTION_SEND);
        shareIntent.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
        shareIntent.putExtra(Intent.EXTRA_TEXT, "Imatge descarregada de " + link.getText().toString());
        shareIntent.putExtra(Intent.EXTRA_STREAM, Uri.fromFile(file));
        shareIntent.setType("image/png");
        PendingIntent pendingShareIntent = PendingIntent.getActivity(MainActivity.this, 1, shareIntent, 0);
        builder.addAction(android.R.drawable.ic_menu_share, "SHARE", pendingShareIntent);
        builder.setContentIntent(pendingIntent);

        NotificationManager notificationManager = (NotificationManager) getSystemService(Context.NOTIFICATION_SERVICE);
        notificationManager.notify(0, builder.build());
    }
}

