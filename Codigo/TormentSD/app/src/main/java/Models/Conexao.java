package Models;
import android.os.AsyncTask;

import java.io.IOException;
import java.net.Socket;

public class Conexao extends Thread {
    private Socket clientSocket;

    public Conexao(){}



    @Override
    public void run(){
        conectar();
    }

    public Socket getClientSocket() {
        return clientSocket;
    }

    public void setClientSocket(Socket clientSocket) {
        this.clientSocket = clientSocket;
    }

    private boolean conectar(){

        try {
            this.clientSocket = new Socket("192.168.100.28", 6001);

            return this.clientSocket.isConnected() ? true:false;
        } catch (IOException e) {e.printStackTrace();}
        return false;
    }

    private boolean desconectar(){
        try {
            this.clientSocket.close();

            return this.clientSocket.isClosed();
        } catch (IOException e) {e.printStackTrace();}

        return false;
    }

}
