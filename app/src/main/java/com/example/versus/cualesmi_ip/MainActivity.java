package com.example.versus.cualesmi_ip;

import android.app.Activity;
import android.content.Context;
import android.net.ConnectivityManager;
import android.net.NetworkInfo;
import android.os.AsyncTask;
import android.os.Bundle;
import android.text.method.ScrollingMovementMethod;
import android.view.View;
import android.widget.EditText;
import android.widget.TextView;
import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.net.HttpURLConnection;
import java.net.URL;


public class MainActivity extends Activity {
    public final String tag="DescargaHTTP";
    public EditText edURL;
    public TextView txtDescarga;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

    }

    public void Descargar(View v){
        edURL=(EditText)findViewById(R.id.edURL);
        txtDescarga=(TextView) findViewById(R.id.txtDescarga);
        txtDescarga.setMovementMethod(new ScrollingMovementMethod());

        ConnectivityManager connMgr = (ConnectivityManager)
                getSystemService(Context.CONNECTIVITY_SERVICE);
        NetworkInfo networkInfo = connMgr.getActiveNetworkInfo();
        if (networkInfo != null && networkInfo.isConnected()) {
            new DescargaPaginaWeb().execute(edURL.getText().toString());
        } else {
            edURL.setText("No se ha podido establecer conexión a internet");
        }
    }


    private class DescargaPaginaWeb extends AsyncTask<String, Void, String> {

        @Override
        protected String doInBackground(String... urls) {

            // los parámetros viene del método execute()
            try {
                return descargaUrl(urls[0]);
            } catch (IOException e) {
                return "Unable to retrieve web page. URL may be invalid.";
            }
        }
        // onPostExecute visualiza los resultados del AsyncTask.
        @Override
        protected void onPostExecute(String result) {
            txtDescarga.setText(result);
        }

        /**
         Este método lee to.do el inputstream convirtiéndolo en una cadena
         ayudándonos con un ByteArrayOutputStream()
         */
        private String Leer(InputStream is) {
            try {
                ByteArrayOutputStream bo = new ByteArrayOutputStream();
                int i = is.read();
                while(i != -1) {
                    bo.write(i);
                    i = is.read();
                }
                return bo.toString();
            } catch (IOException e) {
                return "";
            }
        }

        // Dada una URL, establece una conexión HttpUrlConnection y devuelve
        // el contenido de la página web con un InputStream, y que se transforma a un String.
        private String descargaUrl(String myurl) throws IOException {
            InputStream is = null;

            try {
                URL url = new URL(myurl);
                HttpURLConnection conn = (HttpURLConnection) url.openConnection();
                conn.setReadTimeout(10000 /* milisegundos */);
                conn.setConnectTimeout(15000 /* milisegundos */);
                conn.setRequestMethod("GET");
                conn.setDoInput(true);
                // comienza la consulta
                conn.connect();
                int response = conn.getResponseCode();
                is = conn.getInputStream();

                // convertir el InputStream a string
                return Leer(is);

                //Nos aseguramos de cerrar el inputStream.
            } finally {
                if (is != null) {
                    is.close();
                }
            }
        }

    }
}
