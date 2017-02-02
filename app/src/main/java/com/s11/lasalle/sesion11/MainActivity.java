package com.s11.lasalle.sesion11;

import android.app.NotificationManager;
import android.app.PendingIntent;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.net.Uri;
import android.os.AsyncTask;
import android.os.Environment;
import android.support.v4.content.FileProvider;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.support.v7.app.NotificationCompat;
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
import java.io.ObjectStreamField;
import java.io.OutputStreamWriter;
import java.net.HttpURLConnection;
import java.net.URI;
import java.net.URL;

import static android.R.attr.bitmap;
import static android.R.attr.data;
import static android.R.attr.format;
import static android.R.string.no;
import static android.os.Build.VERSION_CODES.M;

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

                /** Descargar imagen "manualmente" by FELIPE **/
                //new DownloadImageTask().execute(link.getText().toString());

                /** Descargar imagen y guardarla en el teléfono **/
                new DownloadImage().execute(link.getText().toString());
                //sharemessageText();
            }
        });

    }

                        /***** BY FELIPE *****/
/**
    private class DownloadImageTask extends AsyncTask<String, Integer, Bitmap> {
        @Override
        protected Bitmap doInBackground(String... urls) {
            return downloadImage(urls[0]);
        }

        @Override
        protected void onPostExecute(Bitmap bitmap) { // Main Thread
            image.setImageBitmap(bitmap);

            //Mostrar la imagen en notificación

            NotificationCompat.Builder builder = new NotificationCompat.Builder(getApplicationContext());
            builder.setSmallIcon(android.R.drawable.ic_menu_gallery);
            builder.setLargeIcon(bitmap);

            //Per agafar el nom de la imatge(de la URL)

            String[] splitLink = link.getText().toString().split("/");
            String imageName = splitLink[splitLink.length-1];
            builder.setContentTitle(imageName);
            //builder.setContentText("Contingut imatge");

            NotificationCompat.BigPictureStyle bigPictureStyle = new NotificationCompat.BigPictureStyle();
            bigPictureStyle.bigPicture(bitmap);
            builder.setStyle(bigPictureStyle);

            Intent intent = new Intent(getApplicationContext(), MainActivity.class);
            PendingIntent pendingIntent = PendingIntent.getActivity(getApplicationContext(), 0, intent, 0);
            builder.addAction(android.R.drawable.ic_menu_share, "SHARE", pendingIntent);
            builder.setContentIntent(pendingIntent);

            NotificationManager notificationManager = (NotificationManager) getSystemService(Context.NOTIFICATION_SERVICE);
            notificationManager.notify(0, builder.build());
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

        String filename = "myfile";
        String string = "Hello world!";
        FileOutputStream outputStream;

        try {
            //File f = new File(context.getFilesDir(),imageName);
            File f = new File(context.getCacheDir(),imageName);

            FileOutputStream stream = new FileOutputStream(f);
            bitmap.compress(Bitmap.CompressFormat.JPEG, 100, stream);
            //stream.write("text-to-write".getBytes());
            stream.close();

            if ( f.exists() ) {
                Log.d("exists",f.getAbsolutePath());
            } else {
                Log.e("no existe",f.getAbsolutePath());
            }

//            outputStream = openFileOutput(filename, Context.MODE_PRIVATE);
//
//            outputStream.write(string.getBytes());
//            outputStream.close();
        } catch (Exception e) {
            e.printStackTrace();
        }


//        try {
//            //File file = File.createTempFile(imageName, null, context.getCacheDir());
//            File file = File.createTempFile(imageName, null, context.getFilesDir());
//            fileOutputStream = context.openFileOutput(imageName, Context.MODE_PRIVATE);
//            bitmap.compress(Bitmap.CompressFormat.JPEG, 100, fileOutputStream);
//            fileOutputStream.close();
//        }catch (Exception e) {
//            Log.d("saveImage", "Exception, algo va mal ):");
//            e.printStackTrace();
//        }
    }

    private  class DownloadImage extends AsyncTask<String, Void, Bitmap> {
        InputStream b;
        private Bitmap downloadImageBitmap(String urls){
            Bitmap bitmap = null;
            try {
                b = new URL(urls).openStream();
                // Descargar imagen de URL
                //InputStream inputStream = new URL(urls).openStream();
                // Pasamos la info inputStream a Bitmap
                bitmap = BitmapFactory.decodeStream(b);
                b.close();
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

            String[] splitLink = link.getText().toString().split("/");
            String imageName = splitLink[splitLink.length-1];

            saveImage(getApplicationContext(), bitmap, imageName);
//            loadImageBitmap(getApplicationContext(), "imagesesion11.jpeg");
//            File file = getApplicationContext().getFileStreamPath("imagesesion11.jpeg");
//            String imageFullPath = file.getAbsolutePath();
//            Log.d("Guardado en", imageFullPath.toString());
//            if (file.exists()) Log.d("La imagen", "imagesesion11.jpeg ya se ha creado!");
            image.setImageBitmap(bitmap);

            //Mostrar la imagen en notificación

            NotificationCompat.Builder builder = new NotificationCompat.Builder(getApplicationContext());
            builder.setSmallIcon(android.R.drawable.ic_menu_gallery);
            builder.setLargeIcon(bitmap);

            Intent intent = new Intent(getApplicationContext(), MainActivity.class);
            PendingIntent pendingIntent = PendingIntent.getActivity(getApplicationContext(), 0, intent, 0);
            builder.addAction(android.R.drawable.ic_menu_share, "SHARE", pendingIntent);
            builder.setContentIntent(pendingIntent);

            //Per agafar el nom de la imatge(de la URL)


            builder.setContentTitle(imageName);
            //builder.setContentText("Contingut imatge");

            NotificationCompat.BigPictureStyle bigPictureStyle = new NotificationCompat.BigPictureStyle();
            bigPictureStyle.bigPicture(bitmap);
            builder.setStyle(bigPictureStyle);



            //Codigo para compartir la imagen
/*
            Intent sendIntent = new Intent();
            sendIntent.setAction(Intent.ACTION_SEND);
            sendIntent.putExtra(Intent.EXTRA_TEXT, "Text compartit WEAAAA");
            sendIntent.setType("text/plain");
            startActivity(sendIntent);
*/
/*
            final Intent shareIntent = new Intent(Intent.ACTION_SEND);
            shareIntent.setType("image/jpg");
            final File photoFile = new File(getFilesDir(), "foo.jpg");
            shareIntent.putExtra(Intent.EXTRA_STREAM, Uri.fromFile(photoFile));
            startActivity(Intent.createChooser(shareIntent, "Share image using"));
*/
//            File f = getApplicationContext().getFileStreamPath("imagesesion11.jpeg");
            //File f = new File(getApplicationContext().getFilesDir(),imageName);
            //File f = new File(Environment.getExternalStorageDirectory(),imageName);
            File f = new File(getApplicationContext().getCacheDir(),imageName);
            //FileOutputStream stream = new FileOutputStream(f);
            //File f = new File("/DCIM/Camera/","20130919_125831.jpg");
            if ( f.exists() ) {
                Log.d("exists",f.getAbsolutePath());
            } else {
                Log.e("no existe",f.getAbsolutePath());
            }
            //File f = new File(getApplicationContext().getFilesDir(), "20130919_125831.jpg");

            //File f = new File("imagesCam/20130919_125831.jpg");
            //Uri uri = Uri.parse("file://"+f.getAbsolutePath());


            // getExternalFilesDir() + "/Pictures" should match the declaration in fileprovider.xml paths
            //File filee = new File(getExternalFilesDir(Environment.DIRECTORY_PICTURES), "share_image_" + System.currentTimeMillis() + ".png");

// wrap File object into a content provider
            //Uri uri = FileProvider.getUriForFile(getApplicationContext(), "comm.codepath.myFileprovider", f);
            Uri uri = FileProvider.getUriForFile(MainActivity.this,
                    BuildConfig.APPLICATION_ID + ".provider",
                    f);



            Intent share = new Intent(Intent.ACTION_SEND);
            share.setType("image/jpeg");
            //share.setType("text/plain");
            //share.putExtra(Intent.EXTRA_TEXT, uri.toString());
            share.addFlags(Intent.FLAG_GRANT_READ_URI_PERMISSION);
            //share.putExtra(Intent.EXTRA_STREAM, Uri.fromFile(f));
            share.putExtra(Intent.EXTRA_STREAM, uri);

            startActivity(Intent.createChooser(share, "Share image File"));

/*
            //FileOutputStream fileOutputStream;
            //Context ctx = getApplicationContext();
            //fileOutputStream = ctx.openFileOutput("test", Context.MODE_PRIVATE);
            //bitmap.compress(Bitmap.CompressFormat.JPEG, 100, b);
            Intent sendIntent = new Intent();
            sendIntent.setAction(Intent.ACTION_SEND);
            //sendIntent.putExtra(Intent.EXTRA_STREAM, sendIntent);


            //sendIntent.putExtra(Intent.EXTRA_TEXT, imageFullPath);
            //sendIntent.putExtra(Intent.EXTRA_STREAM, imageFullPath);
            sendIntent.putExtra(Intent.EXTRA_STREAM, "/DCIM/Camera/20130919_125831.jpg");
            sendIntent.setType("image/jpeg");
            //sendIntent.setType("text/plain");
            startActivity(sendIntent);
            //startActivity(Intent.createChooser(sendIntent, "compartir"));
*/
            NotificationManager notificationManager = (NotificationManager) getSystemService(Context.NOTIFICATION_SERVICE);
            notificationManager.notify(0, builder.build());
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

    public void sharemessageText() {


        Intent sendIntent = new Intent();
        sendIntent.setAction(Intent.ACTION_SEND);
        sendIntent.putExtra(Intent.EXTRA_TEXT, "Text compartit WEAAAA");
        sendIntent.setType("text/plain");
        startActivity(sendIntent);
    }
}
