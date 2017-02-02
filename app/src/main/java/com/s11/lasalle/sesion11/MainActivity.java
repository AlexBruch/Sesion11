package com.s11.lasalle.sesion11;

import android.app.NotificationManager;
import android.app.PendingIntent;
import android.content.Context;
import android.content.Intent;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.net.Uri;
import android.os.AsyncTask;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.app.NotificationCompat;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageView;

import java.io.File;
import java.io.FileOutputStream;
import java.io.InputStream;
import java.net.HttpURLConnection;
import java.net.URL;

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
        protected void onPostExecute(Bitmap bitmap) { // Main Thread
            image.setImageBitmap(bitmap);
            CreateNotificationActivity(bitmap);
        }
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

    public void CreateNotificationActivity(Bitmap bitmap) {

        String[] splitLink = link.getText().toString().split("/");
        String imageName = splitLink[splitLink.length-1];

        /** Mostrar la imagen en notificación **/

        NotificationCompat.Builder builder = new NotificationCompat.Builder(getApplicationContext());
        builder.setSmallIcon(android.R.drawable.ic_menu_gallery);
        builder.setLargeIcon(bitmap);

        /** BOTÓ NOTIFICACIÓ **/

        Intent intent = new Intent(getApplicationContext(), MainActivity.class);
        PendingIntent pendingIntent = PendingIntent.getActivity(getApplicationContext(), 0, intent, 0);
        builder.addAction(android.R.drawable.ic_menu_share, "SHARE", pendingIntent);
        builder.setContentIntent(pendingIntent);
        builder.setContentTitle(imageName);

        NotificationCompat.BigPictureStyle bigPictureStyle = new NotificationCompat.BigPictureStyle();
        bigPictureStyle.bigPicture(bitmap);
        builder.setStyle(bigPictureStyle);

        shareImage(bitmap, imageName);

        NotificationManager notificationManager = (NotificationManager) getSystemService(Context.NOTIFICATION_SERVICE);
        notificationManager.notify(0, builder.build());
    }
/**   http://stackoverflow.com/questions/21925688/adding-button-action-in-custom-notification **/
            /** CÓDIGO COMPARTIR IMAGEN **/

    private void shareImage (Bitmap bitmap, String imageName) {
        try {
            File file = new File(getApplicationContext().getCacheDir(), imageName);
            FileOutputStream fileOutputStream = new FileOutputStream(file);
            bitmap.compress(Bitmap.CompressFormat.PNG, 100, fileOutputStream);
            fileOutputStream.flush(); // Optimizació: per borrar fluxe de bytes de sortida i obligar als que estan en memoria a escriures
            fileOutputStream.close();
            file.setReadable(true, false); // Per permetre acces de lectura a totes les aplicacions
            final Intent shareIntent = new Intent(android.content.Intent.ACTION_SEND);
            shareIntent.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
            shareIntent.putExtra(Intent.EXTRA_STREAM, Uri.fromFile(file));
            shareIntent.setType("image/png");
            startActivity(shareIntent);
        } catch (Exception e) {
            Log.e("compartir imatge", "No rutlla");
        }
    }
}