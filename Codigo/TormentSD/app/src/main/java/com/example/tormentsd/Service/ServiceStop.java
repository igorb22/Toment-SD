package com.example.tormentsd.Service;

import android.app.Service;
import android.content.Intent;
import android.os.IBinder;

import androidx.annotation.Nullable;

import com.example.tormentsd.Models.Conexao.ConexaoServer;
import com.example.tormentsd.Models.Conexao.ConexaoTorment;

import java.io.IOException;

import static com.example.tormentsd.Models.Global.Conexao.conexoes;

public class ServiceStop extends Service {
    private ConexaoServer conexaoServer;

    public ServiceStop(ConexaoServer conexaoServer){
        this.conexaoServer = conexaoServer;
    }
    @Nullable
    @Override
    public IBinder onBind(Intent intent) {
        return null;
    }
    @Override
    public void onTaskRemoved(Intent rootIntent) {
        desconectarConexao();
        stopSelf();
    }

    @Override
    public void onDestroy(){
        desconectarConexao();
        super.onDestroy();
    }

    public void desconectarConexao(){
        System.out.println("Bye");
        conexaoServer.enviarMensagem("desconectar");

        try {

            for (ConexaoTorment c : conexoes)
                c.getSocket().close();

        } catch (IOException e) {e.printStackTrace();}
    }
}
