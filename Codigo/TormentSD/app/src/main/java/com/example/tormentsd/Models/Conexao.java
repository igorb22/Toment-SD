package com.example.tormentsd.Models;

import android.content.Context;
import com.example.tormentsd.Interfaces.Comunicacao;
import java.io.IOException;
import java.io.PrintStream;
import java.net.Socket;


public class Conexao extends Thread {
    private Socket clientSocket;
    private Comunicacao comunicador;
    private RecebeMensagem recebeMensagem;

    private boolean estaConectado;
    private boolean enviarMensagem;
    private boolean estaFechado;
    private String mensagem;

    public Conexao(Context ctx){
        this.comunicador = (Comunicacao) ctx;
        this.estaConectado  = false;
        this.estaFechado = false;
        this.enviarMensagem = false;
    }

    @Override
    public void run(){
         conectar();
         recebeMensagem = new RecebeMensagem(comunicador,clientSocket);
         recebeMensagem.start();

         while(true){

                if(estaConectado) {
                    comunicador.isConnected(clientSocket.isConnected());
                    this.estaConectado = false;
                }

                if(estaFechado) {
                    comunicador.isClosed(clientSocket.isClosed());
                    this.estaFechado = false;
                }

                if(enviarMensagem) {
                    enviarMensagem();
                    this.enviarMensagem = false;
                }
        }
    }

    public void solicitarStatusConexao(){
        this.estaConectado = true;
    }

    public void solicitarStatusFechamento(){
        this.estaFechado = true;
    }

    public Socket getClientSocket() {
        return clientSocket;
    }

    public void setClientSocket(Socket clientSocket) {
        this.clientSocket = clientSocket;
    }

    public void enviarMensagem( String mensagem){this.enviarMensagem = true; this.mensagem = mensagem;}

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

    private void enviarMensagem(){
        try {

            PrintStream ps = new PrintStream(clientSocket.getOutputStream());
            ps.println(mensagem);

        } catch (IOException e) {e.printStackTrace();}
    }
}










