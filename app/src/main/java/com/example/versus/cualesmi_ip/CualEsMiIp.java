package com.example.versus.cualesmi_ip;

import android.app.Activity;
import android.content.Context;
import android.net.ConnectivityManager;
import android.net.NetworkInfo;
import android.os.AsyncTask;
import android.os.Bundle;
import android.view.View;
import android.widget.*;
import java.io.*;
import java.net.*;
import java.util.regex.*;


public class CualEsMiIp extends Activity {
    public TextView tvIP;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        tvIP = (TextView) findViewById(R.id.tvIP);
    }

    public void descargar(View v){
        ConnectivityManager connMgr = (ConnectivityManager) getSystemService(Context.CONNECTIVITY_SERVICE);
        NetworkInfo networkInfo = connMgr.getActiveNetworkInfo();
        if (networkInfo != null && networkInfo.isConnected()) {
            new DescargaPaginaWeb().execute("http://www.cualesmiip.com");
        } else {
            tvIP.setText("Tu IP actual es: \n No se ha podido establecer conexión a internet");
        }
    }

    //--------------------------
    // CLASE DESCARGARPAGINAWEB
    //--------------------------
    private class DescargaPaginaWeb extends AsyncTask<String, Void, String> {
        @Override
        protected String doInBackground(String... urls) {
            // los parámetros viene del método execute()
            try {
                return descargaUrl(urls[0]);
            } catch (IOException e) {
                return "Tu IP actual es: \n No se ha podido leer la direccion IP.";
            }
        }
        // onPostExecute visualiza los resultados del AsyncTask.
        @Override
        protected void onPostExecute(String result) {
            tvIP.setText("Tu IP actual es: " + result);
        }

        // Dada una URL, establece una conexión HttpUrlConnection y devuelve
        // el contenido de la página web con un InputStream, y que se transforma a un String.
        private String descargaUrl(String myurl) throws IOException {
            InputStream is;
            String linea;
            String resultado;

            URL url = new URL(myurl);
            HttpURLConnection conn = (HttpURLConnection) url.openConnection();
            conn.setReadTimeout(10000 /* milisegundos */);
            conn.setConnectTimeout(15000 /* milisegundos */);
            conn.setRequestMethod("GET");
            conn.setDoInput(true);
            //Comienza la consulta
            conn.connect();

            is = conn.getInputStream();

            linea = leer(is);
            resultado = extreaerIP(linea);

            return resultado;
        }

        public String leer(InputStream is) throws IOException {
            BufferedReader br = new BufferedReader(new InputStreamReader(is, "UTF-8"));
            String linea;

            while((linea = br.readLine()) != null){
                if(linea.contains("Tu IP real es")){
                    break;
                }
            }

            is.close();
            br.close();

            return linea;
        }

        public String extreaerIP(String linea){
            Pattern patron = Pattern.compile("([01]?\\d\\d?|2[0-4]\\d|25[0-5])\\.([01]?\\d\\d?|2[0-4]\\d|25[0-5])\\.([01]?\\d\\d?|2[0-4]\\d|25[0-5])\\.([01]?\\d\\d?|2[0-4]\\d|25[0-5])");
            //Creamos el Matcher a partir del patron, la cadena como parametro
            Matcher matcher = patron.matcher(linea);

            matcher.find();

            return matcher.group(0);
        }
    }



}
