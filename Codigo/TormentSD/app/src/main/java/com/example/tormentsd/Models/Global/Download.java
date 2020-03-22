package com.example.tormentsd.Models.Global;

import com.example.tormentsd.Models.DownloadArquivo;

import java.util.ArrayList;

public class Download {
    public static ArrayList<DownloadArquivo> downloads;

    public Download(ArrayList<DownloadArquivo> downloads) {
        this.downloads = downloads;
    }
    public Download( ) {
        this.downloads = new ArrayList<>();
    }

    public ArrayList<DownloadArquivo> getDownloads() {
        return downloads;
    }

    public void setDownloads(ArrayList<DownloadArquivo> downloads) {
        this.downloads = downloads;
    }
}
