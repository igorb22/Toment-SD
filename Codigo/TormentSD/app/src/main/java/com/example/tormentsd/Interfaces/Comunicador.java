package com.example.tormentsd.Interfaces;

public interface Comunicador {

    void isConnected(boolean status);
    void isClosed(boolean status);
    void receiveMessage(String mensagem);
    void getArchivesRequested(String[] archives);
    void informUpdates(boolean update);

}