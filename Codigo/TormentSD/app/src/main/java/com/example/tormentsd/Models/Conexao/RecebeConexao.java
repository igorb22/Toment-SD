package com.example.tormentsd.Models.Conexao;

import android.annotation.SuppressLint;
import android.content.Context;
import android.util.Log;
import android.widget.Toast;

import com.example.tormentsd.View.MainActivity;

import java.io.IOException;
import java.net.ServerSocket;
import java.net.Socket;

import static com.example.tormentsd.Models.Global.Conexao.conexoes;

public class RecebeConexao extends Thread {
    private ServerSocket server;
    private Context ctx;

    public RecebeConexao(Context ctx){
        this.ctx  = ctx;
    }


    @SuppressLint("LongLogTag")
    @Override
    public void run(){
        try {

            server = new ServerSocket(7001);

            while (true) {

                Socket s = server.accept();
                Log.i("received response from server", "mensagem");

                ConexaoTorment ct = new ConexaoTorment(s,ctx);
                conexoes.add(ct);
                ct.start();

            }
        } catch (IOException e) {e.printStackTrace();}
    }

    public void desconectarParaReceberConexao(){
        if(!server.isClosed()) {
            try {
              //  this.stop();
                server.close();
            } catch (IOException e) {
                e.printStackTrace();
            }
        }
    }

}
