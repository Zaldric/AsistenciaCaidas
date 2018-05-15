package clienteandroid.app.jcsp0003.com.predictorclieneandroid;

import java.io.IOException;
import java.io.PrintStream;
import java.net.InetAddress;
import java.net.Socket;

/**
 * Created by jcsp0003 on 23/02/2018.
 */
public class SingletonSocket {

    private static SingletonSocket instance;

    private Socket socket;
    private PrintStream output;

    private static final int SERVERPORT = 16789;
    private static final String ADDRESS = "192.168.43.165";


    private SingletonSocket() {
    }

    public static SingletonSocket Instance(){
        if (instance == null)
        {
            instance = new SingletonSocket();
        }
        return instance;
    }

    public Socket getSocket() {
        return socket;
    }

    public PrintStream getOutput() {
        return output;
    }

    public void inicializar() {
        try {
            socket = new Socket(InetAddress.getByName(ADDRESS), SERVERPORT);
            output = new PrintStream(socket.getOutputStream());
        } catch (IOException e) {
            e.printStackTrace();
        }
    }
}
