package com.example.tormentsd.Models;

import com.example.tormentsd.Interfaces.Comunicacao;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.net.Socket;

public class RecebeMensagem extends Thread {
    private Comunicacao comunicacador;
    private Socket socket;

    public  RecebeMensagem(Comunicacao c, Socket s){
        this.comunicacador = c;
        this.socket = s;
    }

    @Override
    public void run(){

        while(true){
            try {

                BufferedReader inFromClient = new BufferedReader(new InputStreamReader(socket.getInputStream()));
                String mensagem = inFromClient.readLine();
                comunicacador.receiveMessage(mensagem);

            } catch (IOException e) {e.printStackTrace();}
        }
    }
}
