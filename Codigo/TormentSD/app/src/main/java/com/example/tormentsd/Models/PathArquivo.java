package com.example.tormentsd.Models;

import com.example.tormentsd.Models.Conexao.ConexaoTorment;

import java.util.ArrayList;

public class PathArquivo {
    private ConexaoTorment conexaoTorment;
    private ArrayList<String> paths;

    public PathArquivo(ConexaoTorment conexaoTorment, ArrayList<String> paths) {
        this.conexaoTorment = conexaoTorment;
        this.paths = paths;
    }

    public PathArquivo(ConexaoTorment conexaoTorment) { this.conexaoTorment = conexaoTorment;}

    public ConexaoTorment getConexaoTorment() {
        return conexaoTorment;
    }

    public void setConexaoTorment(ConexaoTorment conexaoTorment) {
        this.conexaoTorment = conexaoTorment;
    }

    public ArrayList<String> getPaths() {
        return paths;
    }

    public void setPaths(ArrayList<String> paths) {
        this.paths = paths;
    }
}
