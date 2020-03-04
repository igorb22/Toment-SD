import java.io.BufferedReader;
import java.io.DataOutputStream;
import java.io.IOException;
import java.io.InputStreamReader;
import java.net.ServerSocket;
import java.net.Socket;
import java.util.ArrayList;

public class Server extends Thread{
    private ServerSocket welcomeSocket;
    private ArrayList<ProcessoExclusivo> tormentsConectados;
	private ArrayList<SolicitacaoArquivo> solicitacoes;

	

	
	public Server() {	 
		try {
			welcomeSocket = new ServerSocket(6001);
			tormentsConectados  = new ArrayList<ProcessoExclusivo>();
			solicitacoes = new ArrayList<>();
		} catch (IOException e) {e.printStackTrace();}
	}
	
	
	 @Override
	 public void run() {
	        super.run();
	        
	         System.out.println("Aguardando cliente...");

	        while(true) {
		        try {	
			         Socket connectionSocket = welcomeSocket.accept();
			         
			         
			         tormentsConectados.add(new ProcessoExclusivo(connectionSocket));
			         System.out.println("Cliente Conectado ao servidor");
			         
				    		
		        	Thread.sleep(1000);		    		
				} catch (InterruptedException | IOException e) {e.printStackTrace();}	
	        }
	    }
	 
	 public class ProcessoExclusivo extends Thread{
		 private Socket connection;
		 
		 public ProcessoExclusivo(Socket connection) {
			 this.connection = connection;
		 }
		 
		 @Override
		 public void run() {
		     super.run();
		     
		     while(true) {
		    	 recebeMensagem();
		     }
		 }
		 
		 public void recebeMensagem() {
			try {
				BufferedReader inFromClient = new BufferedReader(
							new InputStreamReader(connection.getInputStream()));
				
				String pesquisa = inFromClient.readLine();
				
				String[] mensagem = pesquisa.split(";");
				
				switch(mensagem[0]) {
					case "pesquisa":
							solicitacoes.add(new SolicitacaoArquivo(mensagem[1],connection));
							pesquisar(pesquisa);
							break;
					case "possuiArquivo":
							solicitacoes.get(getSolicitacaoArquivo(mensagem[1])).addTorment(connection);
							solicitacoes.get(getSolicitacaoArquivo(mensagem[1])).incrementQtdRespostas();
							break;
					case "naoPossuiArquivo": 
							solicitacoes.get(getSolicitacaoArquivo(mensagem[1])).incrementQtdRespostas();
							break;
					case "conexao": 
							break;
				
				}
					
			} catch (IOException e) {e.printStackTrace();}
		 }
		 
		 public int getSolicitacaoArquivo(String arquivo) {
			 int pos = -1;
			 
			 for(int i = 0; i < solicitacoes.size();i++)
				 if (arquivo.equals(solicitacoes.get(i).getNomeArquivo())) {
					 pos = i;
			 }
			 
			 return pos;
		 }
		 
		 public void pesquisar(String pesquisa) {
			 for (int i = 0 ;i < tormentsConectados.size();i++) {
				 tormentsConectados.get(i).sendPesquisaItem(pesquisa);
			 }	
		 }
		 
		 public void sendPesquisaItem(String pesquisa) {
			 try {
				 				
				DataOutputStream outToClient = new DataOutputStream(connection.getOutputStream());
				outToClient.writeBytes(pesquisa);
				
			} catch (IOException e) {e.printStackTrace();}
		 }
	 }
	 
	 public class VerificaSolicitacoes extends Thread{
		 
		 @Override
		 public void run() {
		     super.run();
		     
		     while(true) {
		    	 try {
		    		 
		    		 verificaSolicitacaoCompleta();
		    		 
					Thread.sleep(1000);
					
				} catch (InterruptedException e) {e.printStackTrace();}
		     } 
		 }
		 
		 
		 public void verificaSolicitacaoCompleta() {
			 
			 for(SolicitacaoArquivo s : solicitacoes) {
				 
				 if (s.getQtdRespostas() == tormentsConectados.size()) {
					 
				 }
			 }
		 }
		 
		 
		 
	 }
	 
	
}