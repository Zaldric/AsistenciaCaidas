package clienteandroid.app.jcsp0003.com.predictorclieneandroid;

import android.content.Context;
import android.content.Intent;
import android.os.AsyncTask;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Toast;

import java.io.IOException;
import java.io.InputStream;
import java.io.PrintStream;
import java.net.Socket;
import java.net.UnknownHostException;

/**
 * Created by jcsp0003 on 22/02/2018.
 */
public class RegistroActivity extends AppCompatActivity {

    private static Socket socket;
    private static PrintStream output;
    private Context context = this;

    private EditText nombreText;
    private EditText apellidosText;
    private EditText correoText;
    private EditText repetirCorreoText;
    private EditText passwordText;
    private EditText telefonoText;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_registro);

        nombreText=((EditText) findViewById(R.id.nombreField));
        apellidosText=((EditText) findViewById(R.id.apellidosField));
        correoText=((EditText) findViewById(R.id.email));
        repetirCorreoText=((EditText) findViewById(R.id.emailRepetir));
        passwordText=((EditText)findViewById(R.id.passwordfield));
        telefonoText=((EditText)findViewById(R.id.tlf));

        Button iniciarSesion = (Button) findViewById(R.id.button2);
        iniciarSesion.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                finish();
            }
        });

        Button registrarse = (Button) findViewById(R.id.button1);
        registrarse.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                registroTask tareaLogin = new registroTask(v);
                if(nombreText.getText().toString().length()>0 && apellidosText.getText().toString().length()>0 && correoText.getText().toString().length()>0 && repetirCorreoText.getText().toString().length()>0 && passwordText.getText().toString().length()>0){
                    if(correoText.getText().toString().equals(repetirCorreoText.getText().toString())){
                        tareaLogin.execute("1"+"#"+nombreText.getText().toString()+" "+apellidosText.getText().toString()+"#"+passwordText.getText().toString()+"#"+correoText.getText().toString()+"#"+telefonoText.getText().toString());
                    }else{
                        Toast.makeText(context, "Los correos no coinciden", Toast.LENGTH_SHORT).show();
                    }
                }
            }
        });
    }



    class registroTask extends AsyncTask<String,Void,String>{

        View v;

        public registroTask(View vw) {
            v=vw;
        }

        @Override
        protected String doInBackground(String... values){
            try {
                String request = values[0];
                SingletonSocket.Instance().getOutput().println(request);

                InputStream stream = SingletonSocket.Instance().getSocket().getInputStream();
                byte[] lenBytes = new byte[256];
                stream.read(lenBytes,0,256);
                String received = new String(lenBytes,"UTF-8").trim();

                return received;
            }catch (UnknownHostException ex) {
                return ex.getMessage();
            } catch (IOException ex) {
                return ex.getMessage();
            }
        }

        @Override
        protected void onPostExecute(String value){
            Log.i(value, "");
            Toast.makeText(context, value, Toast.LENGTH_LONG).show();
            if(value.equals("-1")){
                Toast.makeText(context, "Error en el registro", Toast.LENGTH_LONG).show();
            }else{
                Toast.makeText(context, "Registro correcto", Toast.LENGTH_LONG).show();
            }
        }
    }

}
