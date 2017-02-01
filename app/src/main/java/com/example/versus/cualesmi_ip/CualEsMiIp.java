package com.example.versus.cualesmi_ip;

import android.app.Activity;
import android.content.Context;
import android.net.ConnectivityManager;
import android.net.NetworkInfo;
import android.os.AsyncTask;
import android.os.Bundle;
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

/**
 * Clase CualEsMiIp
 * Es la clase principal, ejecuta la interface de la aplicacion.
 */
public class CualEsMiIp extends Activity {
    public TextView tvIP;

    /**
     * Metodo que se ejecuta al iniciar la activity
     * Se encarga de enlazar el TextView del codigo con el de la interfaz
     * @param savedInstanceState
     */
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        tvIP = (TextView) findViewById(R.id.tvIP);
    }

    /**
     * Metodo que se encarga de ejecutar la clase DescargaPaginaWeb que extiende de Asyntask
     * pasandole la pagina que queremos descargar
     * @param v
     */
    public void descargar(View v){
        //Comprueba la conexion
        ConnectivityManager connMgr = (ConnectivityManager) getSystemService(Context.CONNECTIVITY_SERVICE);
        NetworkInfo networkInfo = connMgr.getActiveNetworkInfo();
        //Si hay conexion..
        if (networkInfo != null && networkInfo.isConnected()) {
            //Se inicia la clase DescargaPaginaWeb
            new DescargaPaginaWeb().execute("http://www.cualesmiip.com");
        } else {
            tvIP.setText("Tu IP real es: \n No se ha podido establecer conexión a internet");
        }
    }

    /**
     * Clase DescargaPaginaWeb, extiende de AsyncTask para ejecutarse en segundo plano sin afectar al
     * hilo principal de la aplicacion.
     */
    private class DescargaPaginaWeb extends AsyncTask<String, Void, String> {

        /**
         * Metodo que se ejecuta al iniciar la clase
         * Se encarga de descargar la pagina web
         * @param urls Conjunto de argumentos
         * @return Devuelve la pagina web o un mensaje de error
         */
        @Override
        protected String doInBackground(String... urls) {
            // los parámetros viene del método execute()
            try {
                return descargaUrl(urls[0]);
            } catch (IOException e) {
                return "Tu IP real es: \n No se puede descargar la pagina web";
            }
        }

        /**\
         * Visualiza los datos del AsyncTask en el textView tvIP
         * @param result Datos que origina
         */
        @Override
        protected void onPostExecute(String result) {
            tvIP.setText("Tu IP actual es: " + result);
        }


        /**
         * Dada una URL, establece una conexión HttpUrlConnection y devuelve
         * el contenido de la página web con un InputStream, y que se transforma a un String.
         * @param myurl direccion de la pagina web que se descarga
         * @return Devuelve la IP actual del dispositivo
         * @throws IOException Excepcion de entrada o salida de datos
         */
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

        /**
         * Metodo que se encarga de leer y buscar la linea que contiene la direccion IP
         * @param is InputStream de la conexion
         * @return Devuelve la linea donde se encuentra la IP
         * @throws IOException Excepcion de entrada o salida de datos
         */
        private String leer(InputStream is) throws IOException {
            //Se crea un BufferedReader para leer linea a linea
            BufferedReader br = new BufferedReader(new InputStreamReader(is, "UTF-8"));
            String linea;

            //Se lee linea a linea hasta que una linea contenga la secuencia "Tu IP real es"
            while((linea = br.readLine()) != null){
                if(linea.contains("Tu IP real es")){
                    break;
                }
            }
            //Se liberan los recusos
            is.close();
            br.close();

            return linea;
        }

        private String extreaerIP(String linea){
            //Se establece un patron
            Pattern patron = Pattern.compile("([01]?\\d\\d?|2[0-4]\\d|25[0-5])\\.([01]?\\d\\d?|2[0-4]\\d|25[0-5])\\.([01]?\\d\\d?|2[0-4]\\d|25[0-5])\\.([01]?\\d\\d?|2[0-4]\\d|25[0-5])");
            //Se crea el Matcher a partir del patron y le pasamos la linea como parametro
            Matcher matcher = patron.matcher(linea);
            //Se busca en la linea todos las secuencias que cumplan el patron
            matcher.find();
            //Devolvemos la primera secuencia que cumple el patron
            return matcher.group(0);
        }
    }
}
