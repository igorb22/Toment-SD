package com.example.tormentsd.Models;

import com.example.tormentsd.Models.DownloadArquivo;

import java.util.ArrayList;

public class Downloads {
    public static ArrayList<DownloadArquivo> downloads;

    public Downloads(ArrayList<DownloadArquivo> downloads) {
        this.downloads = downloads;
    }
    public Downloads( ) {
        this.downloads = new ArrayList<>();
    }

    public ArrayList<DownloadArquivo> getDownloads() {
        return downloads;
    }

    public void setDownloads(ArrayList<DownloadArquivo> downloads) {
        this.downloads = downloads;
    }
}
