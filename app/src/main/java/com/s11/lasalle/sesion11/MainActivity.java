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
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.Toast;

import com.squareup.picasso.Picasso;

import java.io.IOException;
import java.io.InputStream;
import java.net.HttpURLConnection;
import java.net.URL;

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

                /** Descargar imagen con Picasso **/
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
        protected void onPostExecute(Bitmap bitmap) {
            image.setImageBitmap(bitmap);
        }

        private Bitmap downloadImage(String link) {
            Bitmap bitmap = null;
            try {
                /**Definició URL**/
                URL url = new URL(link);

                /**Configuració HttpURLConnection per fer la descàrrega**/
                HttpURLConnection httpURLConnection = (HttpURLConnection) url.openConnection();
                httpURLConnection.setDoInput(true);
                httpURLConnection.connect();

                /**Rebem la informació en format InputStrem**/
                InputStream inputStream = httpURLConnection.getInputStream();

                /**Transformem la informació a BitMap**/
                bitmap = BitmapFactory.decodeStream(inputStream);

            } catch (Exception e) {
                e.printStackTrace();
            }
            return bitmap;
        }
    }
}
