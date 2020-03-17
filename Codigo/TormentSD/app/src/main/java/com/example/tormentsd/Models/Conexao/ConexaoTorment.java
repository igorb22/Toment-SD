package com.example.tormentsd.Models.Conexao;

import android.content.Context;
import android.os.Environment;
import android.os.Handler;
import android.util.Log;
import android.widget.Toast;

import com.example.tormentsd.Interfaces.Comunicador;
import com.example.tormentsd.View.MainActivity;

import java.io.BufferedInputStream;
import java.io.BufferedReader;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.io.OutputStream;
import java.io.PrintStream;
import java.net.Socket;

public class ConexaoTorment extends Thread {
    private String ip;
    private Socket socket;
    private String requisicao;
    private Comunicador comunicacao;
    private static final int filesize = 6000000;

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

            while (true) {
                BufferedReader inFromClient = new BufferedReader(new InputStreamReader(socket.getInputStream()));
                String[] mensagem = inFromClient.readLine().split(";");

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
                        recebeArquivo();
                        break;

                }
            }
        } catch (IOException e) {e.printStackTrace();}
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

                enviaMensagem("prepararRecepcao;true");

                byte[] mybytearray = new byte[(int) myFile.length()];

                FileInputStream fis = new FileInputStream(myFile);

                BufferedInputStream bis = new BufferedInputStream(fis);

                bis.read(mybytearray, 0, mybytearray.length);

                OutputStream os = socket.getOutputStream();

                System.out.println("Enviando...");

                os.write(mybytearray, 0, mybytearray.length);

                os.flush();


            } catch (FileNotFoundException e) {
                e.printStackTrace();
            } catch (IOException e) {
                e.printStackTrace();
            }
        }
    }

    public void conectar() {
        try {

            socket = new Socket(ip, 7001);
            Log.i("conectando a dispositivo", "mensagem");


        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    public void recebeArquivo() {
        int bytesRead;
        int current = 0;

        byte[] mybytearray = new byte[filesize];

        try {
            InputStream is = socket.getInputStream();
            File fileExt = new File(Environment.getExternalStorageDirectory().getPath() + "/TORMENT/"
                    , "arquivo.txt");

            fileExt.getParentFile().mkdirs();

            FileOutputStream fos = new FileOutputStream(fileExt);

            bytesRead = is.read(mybytearray, 0, mybytearray.length);
            current = bytesRead;

            do {

                bytesRead = is.read(mybytearray, current, (mybytearray.length - current));
                if (bytesRead >= 0)
                    current += bytesRead;

            } while (bytesRead > -1);

            fos.write(mybytearray, 0, mybytearray.length);
            fos.close();
            socket.close();

        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    public void enviaMensagem(String mensagem) {
        try {

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
