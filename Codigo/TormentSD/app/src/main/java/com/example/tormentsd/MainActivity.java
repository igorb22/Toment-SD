package com.example.tormentsd;

import androidx.appcompat.app.AppCompatActivity;

import android.os.Bundle;
import android.view.View;
import android.widget.EditText;
import android.widget.ImageButton;
import android.widget.TextView;
import android.widget.Toast;

import Models.Comunicacao;
import Models.Conexao;

public class MainActivity extends AppCompatActivity implements Comunicacao {
    private ImageButton btnPesquisar;
    private EditText editTextPesquisa;
    private TextView textView;
    private Conexao conexao;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        conexao = new Conexao(MainActivity.this);
        conexao.start();

        btnPesquisar = findViewById(R.id.btnPesquisar);
        editTextPesquisa = findViewById(R.id.editTextPesquisa);
        textView = findViewById(R.id.texto);

        conexao.solicitarStatusConexao(true);

        btnPesquisar.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                 conexao.enviarMensagem(true,"pesquisa;"+editTextPesquisa.getText().toString());
                 Toast.makeText(getBaseContext(), "Enviando Mensagem",Toast.LENGTH_LONG).show();

            }
        });
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
        return false;
    }


}
