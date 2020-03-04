package com.example.tormentsd;

import androidx.appcompat.app.AppCompatActivity;

import android.os.Bundle;
import android.widget.TextView;

import Models.Conexao;

public class MainActivity extends AppCompatActivity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);


        Conexao c = new Conexao();

        TextView txt = findViewById(R.id.txtMinhaString);

        if(c.conectar())
            txt.setText("Telefone conectado ao servidor");
        else
            txt.setText("Não foi possível conectar ao servidor");
    }
}
