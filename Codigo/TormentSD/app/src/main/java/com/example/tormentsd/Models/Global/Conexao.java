package com.example.tormentsd.Models.Global;

import com.example.tormentsd.Models.Conexao.ConexaoTorment;


import java.util.ArrayList;

public class Conexao {
    public static ArrayList<ConexaoTorment> conexoes;

    public Conexao() { conexoes = new ArrayList<>();}

    public static void setConexoes(ArrayList<ConexaoTorment> conexoes) {
        Conexao.conexoes = conexoes;
    }

    public static ArrayList<ConexaoTorment> getConexoes() {
        return conexoes;
    }
}
