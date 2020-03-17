package com.example.tormentsd.Models.Conexao;

import android.content.Context;
import android.util.Log;
import android.widget.Toast;

import com.example.tormentsd.View.MainActivity;

import java.io.IOException;
import java.net.ServerSocket;
import java.net.Socket;

public class RecebeConexao extends Thread {
    private ServerSocket server;
    private Context ctx;

    public RecebeConexao(Context ctx){
        this.ctx  = ctx;
    }


    @Override
    public void run(){
        try {

            server = new ServerSocket(7001);

            while (true) {

                Socket s = server.accept();
                Log.i("received response from server", "mensagem");

                ConexaoTorment ct = new ConexaoTorment(s,ctx);
                ct.start();

            }
        } catch (IOException e) {e.printStackTrace();}
    }


}
