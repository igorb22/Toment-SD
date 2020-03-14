package com.example.tormentsd.Models;

public class Dispositivo {

    private String ip;


    public Dispositivo(){}

    public Dispositivo(String ip) {
        this.ip = ip;
    }

    public String getIp() {
        return ip;
    }

    public void setIp(String ip) {
        this.ip = ip;
    }
}
