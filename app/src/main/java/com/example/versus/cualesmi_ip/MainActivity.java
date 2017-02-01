package com.example.versus.cualesmi_ip;

import android.app.Activity;
import android.content.Context;
import android.net.ConnectivityManager;
import android.net.NetworkInfo;
import android.os.AsyncTask;
import android.os.Bundle;
import android.text.method.ScrollingMovementMethod;
import android.util.Log;
import android.view.View;
import android.widget.EditText;
import android.widget.TextView;

import java.io.BufferedReader;
import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.net.HttpURLConnection;
import java.net.URL;
import java.util.regex.Matcher;
import java.util.regex.Pattern;


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
        ConnectivityManager connMgr = (ConnectivityManager) getSystemService(Context.CONNECTIVITY_SERVICE);
        NetworkInfo networkInfo = connMgr.getActiveNetworkInfo();
        if (networkInfo != null && networkInfo.isConnected()) {
            new DescargaPaginaWeb().execute("http://www.cualesmiip.com");
        } else {
            //edURL.setText("No se ha podido establecer conexión a internet");
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
            //txtDescarga.setText(result);
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
            String linea = null;
            BufferedReader br = null;

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

                br = new BufferedReader(new InputStreamReader(is, "UTF-8"));

                while((linea = br.readLine()) != null){
                    if(linea.contains("Tu IP real es")){
                        break;
                    }
                }

                Pattern patron = Pattern.compile("/^(([1-9]?[0-9]|1[0-9]{2}|2[0-4][0-9]|25[0-5]).){3}([1-9]?[0-9]|1[0-9]{2}|2[0-4][0-9]|25[0-5])$/");
                //Creamos el Matcher a partir del patron, la cadena como parametro
                Matcher encaja = patron.matcher(linea);

                String resultado = encaja.toString();

                Log.d("Resultado: ", resultado);
                //Nos aseguramos de cerrar el inputStream.
            } finally {
                if (is != null) {
                    br.close();
                }
            }

            return linea;
        }

    }
}
