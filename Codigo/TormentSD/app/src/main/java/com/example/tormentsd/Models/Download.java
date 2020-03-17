package com.example.tormentsd.Models;

public class Download {
    private String path;
    private int download;
    private String status; // { COMPLETO, CANCELADO}

    public Download(String path, int download, String status) {
        this.path = path;
        this.download = download;
        this.status = status;
    }

    public String getPath() {
        return path;
    }

    public void setPath(String path) {
        this.path = path;
    }

    public int getDownload() {
        return download;
    }

    public void setDownload(int download) {
        this.download = download;
    }

    public String getStatus() {
        return status;
    }

    public void setStatus(String status) {
        this.status = status;
    }
}
