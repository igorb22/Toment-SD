package Models;
import android.os.AsyncTask;

import java.io.IOException;
import java.net.Socket;

public class Conexao extends AsyncTask<String, Void, RSSFeed> {
    private Socket clientSocket;

    public Conexao(){}

    public boolean conectar(){

        try {
            this.clientSocket = new Socket("10.0.2.2", 6001);

            return this.clientSocket.isConnected() ? true:false;
        } catch (IOException e) {e.printStackTrace();}
        return false;
    }

    public boolean desconectar(){
        try {
            this.clientSocket.close();

            return this.clientSocket.isClosed();
        } catch (IOException e) {e.printStackTrace();}

        return false;
    }

}
