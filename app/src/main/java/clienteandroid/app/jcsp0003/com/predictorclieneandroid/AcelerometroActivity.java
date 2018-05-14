package clienteandroid.app.jcsp0003.com.predictorclieneandroid;

import android.content.Intent;
import android.net.Uri;
import android.os.AsyncTask;
import android.os.Bundle;


import java.io.IOException;
import java.io.InputStream;
import java.net.UnknownHostException;
import java.util.List;

import android.app.Activity;
import android.content.pm.ActivityInfo;
import android.hardware.Sensor;
import android.hardware.SensorEvent;
import android.hardware.SensorEventListener;
import android.hardware.SensorManager;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.Toast;


import static android.content.Intent.ACTION_DIAL;

public class AcelerometroActivity extends Activity implements SensorEventListener{

    private long last_update = 0, last_movement = 0;
    private float prevX = 0, prevY = 0, prevZ = 0;
    private float curX = 0, curY = 0, curZ = 0;

    public String telefono="091";

    private Button buttonAjustes;

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_acelerometro);
        this.setRequestedOrientation(ActivityInfo.SCREEN_ORIENTATION_PORTRAIT);
        tlfTask tareaLogin = new tlfTask();
        tareaLogin.execute("14");
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
            telefono = value;
            Log.e("ME CAGO EN JESUS", value);
        }
    }

}

