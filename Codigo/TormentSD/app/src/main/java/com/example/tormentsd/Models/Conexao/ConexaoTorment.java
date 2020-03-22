package com.example.tormentsd.Models.Conexao;

import android.content.Context;
import android.os.Environment;
import android.os.Handler;
import android.util.Log;

import com.example.tormentsd.Interfaces.Comunicador;
import com.example.tormentsd.Models.DownloadArquivo;

import java.io.BufferedInputStream;
import java.io.BufferedReader;
import java.io.DataInputStream;
import java.io.DataOutputStream;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStreamReader;
import java.io.OutputStream;
import java.io.PrintStream;
import java.net.Socket;

import static com.example.tormentsd.Models.Global.Download.downloads;

public class ConexaoTorment extends Thread {
    private String ip;
    private Socket socket;
    private String requisicao;
    private Comunicador comunicacao;

    public ConexaoTorment(String ip, Socket s, Context context) {
        this.ip = ip;
        this.socket = s;
        this.comunicacao = (Comunicador) context;
    }

    public ConexaoTorment(Socket s, Context context) {
        this.socket = s;
        this.comunicacao = (Comunicador) context;
    }

    public ConexaoTorment(String ip, String s, Context context) {
        this.ip = ip;
        this.requisicao = s;
        this.socket = null;
        this.comunicacao = (Comunicador) context;
    }

    @Override
    public void run() {
        try {

            if (socket == null) {
                conectar();
                enviaMensagem(requisicao);
            }

            while (socket.isConnected()) {
                    BufferedReader inFromClient = new BufferedReader(new InputStreamReader(socket.getInputStream()));
                    String mens = inFromClient.readLine();


                    if (mens != null) {
                        String[] mensagem = mens.split(";");

                        switch (mensagem[0]) {
                            case "requisicao":

                                enviaArquivo(mensagem);
                                break;

                            case "todosArquivos":

                                enviaMensagem(pegaListaDeArquivos());
                                break;

                            case "arquivosSolicitados":
                                Log.i("arquivos da solicitacao", "mensagem");
                                comunicacao.getArchivesRequested(mensagem);
                                break;

                            case "prepararRecepcao":
                                Log.i("prepara recepcao", mensagem[0]);
                                recebeArquivo(Integer.parseInt(mensagem[1]));
                                break;

                        }
                    }
                }
        } catch (IOException e) {e.printStackTrace();}
    }

    public String getIp() {
        return ip;
    }

    public void setIp(String ip) {
        this.ip = ip;
    }

    public Socket getSocket() {
        return socket;
    }

    public void setSocket(Socket socket) {
        this.socket = socket;
    }

    public String getRequisicao() {
        return requisicao;
    }

    public void setRequisicao(String requisicao) {
        this.requisicao = requisicao;
    }

    public Comunicador getComunicacao() {
        return comunicacao;
    }

    public void setComunicacao(Comunicador comunicacao) {
        this.comunicacao = comunicacao;
    }

    public String pegaListaDeArquivos() {

        String path = "arquivosSolicitados";
        File f = new File(Environment.getExternalStorageDirectory().getPath() + "/TORMENT/");
        File[] files = f.listFiles();

        if (files.length > 0) {

            for (int i = 0; i < files.length; i++)
                path += ";" + files[i].getPath();

        } else
            path += ";false";

        return path;
    }

    public void enviaArquivo(String[] mensagem) {

        File myFile = verificarSeArquivoExiste(mensagem[1]);

        if (myFile != null) {
            try {


                byte[] mybytearray = new byte[(int) myFile.length()];

                Log.i("enviando mensagem de preparacao", "mensagem");
                enviaMensagem("prepararRecepcao;"+mybytearray.length);

                Thread.sleep(1000);

                Log.i("adicionando arquivo", "mensagem");
                FileInputStream fis = new FileInputStream(myFile);
                BufferedInputStream bis = new BufferedInputStream(fis);


                Log.i("lendo bytes", "mensagem");
                DataInputStream dis = new DataInputStream(bis);
                dis.readFully(mybytearray, 0, mybytearray.length);


                OutputStream os = socket.getOutputStream();

                Log.i("enviando bytes", "mensagem");
                DataOutputStream dos = new DataOutputStream(os);
                dos.write(mybytearray, 0, mybytearray.length);
                dos.flush();

                socket.close();
            } catch (FileNotFoundException e) {e.printStackTrace();
            } catch (IOException e) {e.printStackTrace();
            } catch (InterruptedException e) { e.printStackTrace();}
        }
    }

    public void conectar() {
        try {

            socket = new Socket(ip, 7001);
            Log.i("conectando a dispositivo", "mensagem");

        } catch (IOException e) {
            socket = new Socket();
            e.printStackTrace();
        }
    }

    public void recebeArquivo(int size) {
        Log.i("inserindo novo download","mensagem");

        try {
            Log.i("no download","mensagem");

            int percent;
            int bytesRead;

            Log.i("caminho completo 1","mensagem");
            DataInputStream clientData = new DataInputStream(
                    socket.getInputStream());

            String caminhoCompleto = Environment.getExternalStorageDirectory().getPath()
                    + "/TORMENT/"+ requisicao.split(";")[1];

            Log.i("caminho completo 2","mensagem");
            OutputStream output = new FileOutputStream(new File(caminhoCompleto));

            percent = size/100;
            byte[] buffer = new byte[1024];


            Log.i("caminho completo",caminhoCompleto);

            downloads.add(new DownloadArquivo(caminhoCompleto,0,"BAIXANDO"));
            int pos = downloads.size()-1;

            Log.i("informando novo download","mensagem");
            comunicacao.informUpdates(true);


            while (size > 0 && (bytesRead = clientData.read(buffer, 0,
                    Math.min(buffer.length, size))) != -1) {
                output.write(buffer, 0, bytesRead);
                size -= bytesRead;

                Log.i("no laco","mensagem");
                atualizaDownload(pos,bytesRead/percent);

            }

            atualizaStatus(pos,"FINALIZADO");

            output.close();
            socket.close();

        } catch (IOException e) {
            Log.i("erro no download",e.getMessage());
        }
    }


    public void atualizaDownload(int pos, int current){

        int down = downloads.get(pos).getDownload() + current;
        downloads.get(pos).setDownload(down);

        new Thread(new Runnable() {
            @Override
            public void run() {
                comunicacao.informUpdates(true);
            }
        }).start();

    }

    public void atualizaStatus(int pos, String status){

        downloads.get(pos).setStatus(status);

        if (status.equals("FINALIZADO"))
            downloads.get(pos).setDownload(100);

        new Thread(new Runnable() {
            @Override
            public void run() {
                comunicacao.informUpdates(true);
            }
        }).start();
    }

    public void enviaMensagem(String mensagem) {
        try {
            requisicao = mensagem;
            PrintStream ps = new PrintStream(socket.getOutputStream());
            ps.println(mensagem);
            Log.i("enviou mensagem", "mensagem");


        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    private File verificarSeArquivoExiste(String arquivo) {
        File folder = new File(Environment.getExternalStorageDirectory() + "/TORMENT/" + arquivo);

        if (folder.exists())
            return folder;
        else
            return null;
    }

    private void atualizaUI(final String texto) {
        Handler handler = new Handler();
        handler.post(new Runnable() {
            @Override
            public void run() {

            }
        });
    }
}
