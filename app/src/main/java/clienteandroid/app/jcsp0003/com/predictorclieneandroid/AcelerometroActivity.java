package clienteandroid.app.jcsp0003.com.predictorclieneandroid;

import android.content.Context;
import android.content.Intent;
import android.net.Uri;
import android.os.AsyncTask;
import android.os.Build;
import android.os.Bundle;


import java.io.IOException;
import java.io.InputStream;
import java.net.UnknownHostException;
import java.text.Normalizer;
import java.util.ArrayList;
import java.util.List;

import android.content.pm.ActivityInfo;
import android.hardware.Sensor;
import android.hardware.SensorEvent;
import android.hardware.SensorEventListener;
import android.hardware.SensorManager;
import android.speech.RecognizerIntent;
import android.speech.tts.TextToSpeech;
import android.support.v7.app.AppCompatActivity;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.Toast;


import static android.content.Intent.ACTION_DIAL;

public class AcelerometroActivity extends AppCompatActivity implements SensorEventListener, TextToSpeech.OnInitListener{

    private long last_update = 0, last_movement = 0;
    private float prevX = 0, prevY = 0, prevZ = 0;
    private float curX = 0, curY = 0, curZ = 0;

    public String telefono;

    private Context context = this;

    private static final int RECONOCEDOR_VOZ = 7;

    private ArrayList<Respuestas> respuest;
    private TextToSpeech leer;

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_acelerometro);
        this.setRequestedOrientation(ActivityInfo.SCREEN_ORIENTATION_PORTRAIT);
        tlfTask tareaLogin = new tlfTask();
        tareaLogin.execute("14");

        inicializar();
    }

    @Override
    public void onSensorChanged(SensorEvent event) {

        synchronized (this) {
            long current_time = event.timestamp;

            curX = event.values[0];
            curY = event.values[1];
            curZ = event.values[2];

            if (prevX == 0 && prevY == 0 && prevZ == 0) {
                last_update = current_time;
                last_movement = current_time;
                prevX = curX;
                prevY = curY;
                prevZ = curZ;
            }

            long time_difference = current_time - last_update;
            if (time_difference > 0) {
                float movement = Math.abs((curX + curY + curZ) - (prevX - prevY - prevZ)) / time_difference;
                int limit = 1500;
                float min_movement = 1E-6f;
                if (movement > min_movement) {
                    if (current_time - last_movement >= limit) {
                        char aux=String.valueOf(movement).charAt(0);
                        if( aux == '3' || aux == '4' || aux == '5' || aux == '6' || aux == '7' || aux == '8' || aux == '9') {
                            Intent i = new Intent(ACTION_DIAL);
                            i.setData(Uri.parse("tel:"+telefono));
                            startActivity(i);
                        }
                        //registroTask tareaLogin = new registroTask();
                        //tareaLogin.execute("13#" + movement);
                    }
                    last_movement = current_time;
                }
                prevX = curX;
                prevY = curY;
                prevZ = curZ;
                last_update = current_time;
            }

        }
    }

    @Override
    protected void onResume() {
        super.onResume();
        SensorManager sm = (SensorManager) getSystemService(SENSOR_SERVICE);
        List<Sensor> sensors = sm.getSensorList(Sensor.TYPE_ACCELEROMETER);
        if (sensors.size() > 0) {
            sm.registerListener(this, sensors.get(0), SensorManager.SENSOR_DELAY_GAME);
        }
    }



    @Override
    protected void onStop() {
        SensorManager sm = (SensorManager) getSystemService(SENSOR_SERVICE);
        sm.unregisterListener(this);
        super.onStop();
    }


    @Override
    public void onAccuracyChanged(Sensor sensor, int i) {

    }

    @Override
    public void onInit(int status) {

    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);

        if(resultCode == RESULT_OK && requestCode == RECONOCEDOR_VOZ){
            ArrayList<String> reconocido = data.getStringArrayListExtra(RecognizerIntent.EXTRA_RESULTS);
            String escuchado = reconocido.get(0);
            prepararRespuesta(escuchado);
        }
    }

    private void prepararRespuesta(String escuchado) {
        String normalizar = Normalizer.normalize(escuchado, Normalizer.Form.NFD);
        String sintilde = normalizar.replaceAll("[^\\p{ASCII}]", "");

        int resultado;
        String respuesta = "No te he entendido, repite por favor.";
        for (int i = 0; i < respuest.size(); i++) {
            resultado = sintilde.toLowerCase().indexOf(respuest.get(i).getCuestion());
            if(resultado != -1){
                respuesta = respuest.get(i).getRespuestas();
            }
        }
        responder(respuesta);
    }

    private void responder(String respuestita) {
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.LOLLIPOP) {
            leer.speak(respuestita, TextToSpeech.QUEUE_FLUSH, null, null);
        }else {
            leer.speak(respuestita, TextToSpeech.QUEUE_FLUSH, null);
        }

        if (respuestita.equals("Llamando a contacto")) {
            Toast.makeText(context, telefono.toString(), Toast.LENGTH_LONG).show();
            Intent i = new Intent(ACTION_DIAL);
            i.setData(Uri.parse("tel:"+telefono));
            startActivity(i);
        }
        if (respuestita.equals("Llamando a los servicios de emergencia")) {
            Intent i = new Intent(ACTION_DIAL);
            i.setData(Uri.parse("tel:061"));
            startActivity(i);
        }

    }

    public void inicializar(){
        respuest = proveerDatos();
        leer = new TextToSpeech(this, this);
    }

    //LLamada del botón del micro
    public void hablar(View v){
        Intent hablar = new Intent(RecognizerIntent.ACTION_RECOGNIZE_SPEECH);
        hablar.putExtra(RecognizerIntent.EXTRA_LANGUAGE_MODEL, "es-MX");
        startActivityForResult(hablar, RECONOCEDOR_VOZ);
    }

    public ArrayList<Respuestas> proveerDatos(){
        ArrayList<Respuestas> respuestas = new ArrayList<>();
        respuestas.add(new Respuestas("contacto", "Llamando a contacto"));
        respuestas.add(new Respuestas("contactos", "Llamando a contacto"));
        respuestas.add(new Respuestas("emergencias", "Llamando a los servicios de emergencia"));
        respuestas.add(new Respuestas("emergencia", "Llamando a los servicios de emergencia"));
        respuestas.add(new Respuestas("ambulancia", "Llamando a los servicios de emergencia"));
        respuestas.add(new Respuestas("ambulansia", "Llamando a los servicios de emergencia"));
        respuestas.add(new Respuestas("ayuda", "Llamando a los servicios de emergencia"));
        respuestas.add(new Respuestas("socorro", "Llamando a los servicios de emergencia"));
        respuestas.add(new Respuestas("auxilio", "Llamando a los servicios de emergencia"));

        return respuestas;
    }

    class registroTask extends AsyncTask<String,Void,String> {


        @Override
        protected String doInBackground(String... values){
            String request = values[0];
            SingletonSocket.Instance().getOutput().println(request);

            return "";
        }

        @Override
        protected void onPostExecute(String value){
        }
    }

    class tlfTask extends AsyncTask<String,Void,String> {

        @Override
        protected String doInBackground(String... values) {
            try {
                String request = values[0];
                SingletonSocket.Instance().getOutput().println(request);

                InputStream stream = SingletonSocket.Instance().getSocket().getInputStream();
                byte[] lenBytes = new byte[256];
                stream.read(lenBytes, 0, 256);
                String received = new String(lenBytes, "UTF-8").trim();

                return received;
            } catch (UnknownHostException ex) {
                return ex.getMessage();
            } catch (IOException ex) {
                return ex.getMessage();
            }
        }

        @Override
        protected void onPostExecute(String value) {
            telefono = value.toString();
            Toast.makeText(context, value, Toast.LENGTH_LONG).show();
        }
    }

}

