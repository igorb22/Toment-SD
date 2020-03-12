package com.example.tormentsd.View;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;

import android.Manifest;
import android.content.pm.PackageManager;
import android.os.Build;
import android.os.Bundle;
import android.os.Environment;
import android.view.View;
import android.widget.EditText;
import android.widget.ImageButton;
import android.widget.TextView;
import android.widget.Toast;

import com.example.tormentsd.Interfaces.Comunicacao;
import com.example.tormentsd.Models.Conexao;
import com.example.tormentsd.R;

import java.io.File;

public class MainActivity extends AppCompatActivity implements Comunicacao {
    private ImageButton btnPesquisar;
    private EditText editTextPesquisa;
    private TextView textView;
    private Conexao conexao;
    int cont = 0;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        // Pede permissão ao usuário
        checaPermissao();

        // faz a conexão com o servidor
        conexao = new Conexao(MainActivity.this);
        conexao.start();

        // linka os componentes da tela com o back-end
        btnPesquisar = findViewById(R.id.btnPesquisar);
        editTextPesquisa = findViewById(R.id.editTextPesquisa);
        textView = findViewById(R.id.texto);

        // solicita o status da conexão (se está aberta ou não)
        conexao.solicitarStatusConexao();

        Toast.makeText(this, Environment.getExternalStorageDirectory()+"", Toast.LENGTH_SHORT).show();

        btnPesquisar.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                 // enviando uma mensagem ao servidor
                 conexao.enviarMensagem("pesquisa;"+editTextPesquisa.getText().toString());
                 Toast.makeText(getBaseContext(), "Enviando Mensagem",Toast.LENGTH_LONG).show();
            }
        });
    }

    public void checaPermissao(){

        if(Build.VERSION.SDK_INT >= Build.VERSION_CODES.M && checkSelfPermission(Manifest.permission.WRITE_EXTERNAL_STORAGE)
                != PackageManager.PERMISSION_GRANTED){
            requestPermissions(new String[]{Manifest.permission.WRITE_EXTERNAL_STORAGE},1000);
        }
    }

    public void criaDiretorioTorment(){
        File folder = new File(Environment.getExternalStorageDirectory() + "/TORMENT");

        if(!folder.exists())
            folder.mkdir();
    }

    public void pesquisarArquivo(String arquivo){

        if(verificarSeArquivoExiste(arquivo)){
            conexao.enviarMensagem("possuiArquivo;"+arquivo);
        }else{
            conexao.enviarMensagem("naoPossuiArquivo;"+arquivo);
        }
    }

    public boolean verificarSeArquivoExiste(String arquivo){
        File folder = new File(Environment.getExternalStorageDirectory() + "/TORMENT/"+arquivo);

        if(folder.exists()) {
            return true;

        }else {
            return false;
        }
    }

    @Override
    public void onRequestPermissionsResult(int requestCode, @NonNull String[] permissions, @NonNull int[] grantResults) {
        switch (requestCode) {
            case 1000:
                if (grantResults[0] == PackageManager.PERMISSION_GRANTED) {
                    Toast.makeText(this, "Permissão concedida", Toast.LENGTH_SHORT).show();
                    criaDiretorioTorment();
                }else if (grantResults[0] == PackageManager.PERMISSION_DENIED) {
                    finishAffinity();
                }
        }
    }

    @Override
    public boolean isConnected(boolean status) {

        return status;
    }

    @Override
    public boolean isClosed(boolean status) {

        return status;
    }

    @Override
    public boolean receiveMessage(String mensagem) {

        textView.setText(mensagem);

        String [] mensagens = mensagem.split(";");
        switch (mensagens[0]){

            case "pesquisa":
                pesquisarArquivo(mensagens[1]);


                break;

            case "resultado":




                break;
        }


        return false;
    }
}
