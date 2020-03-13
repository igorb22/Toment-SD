package com.example.tormentsd.Interfaces;

public interface Comunicacao{

    void isConnected(boolean status);
    void isClosed(boolean status);
    void receiveMessage(String mensagem);

}