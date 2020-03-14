package com.example.tormentsd.Models;

import java.net.Socket;

public class ConexaoTorment extends Thread{
    private String ip;
    private Socket socket;

    public ConexaoTorment(String ip){
        this.ip = ip;
    }

    @Override
    public void run(){

        while(true){



        }

    }

    public void conectar(){

        
    }


}
