package Models;

public interface Comunicacao{

    boolean isConnected(boolean status);
    boolean isClosed(boolean status);
    boolean receiveMessage(String mensagem);

}