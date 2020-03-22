import java.io.BufferedReader;
import java.io.BufferedWriter;
import java.io.DataOutputStream;
import java.io.IOException;
import java.io.InputStreamReader;
import java.io.OutputStreamWriter;
import java.io.PrintStream;
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

	       new VerificaSolicitacoes().start();
	       
	       while(true) {
		       try {	

		    	   Socket connectionSocket = welcomeSocket.accept();
			       
			         
			       tormentsConectados.add(new ProcessoExclusivo(connectionSocket));
			       tormentsConectados.get(tormentsConectados.size()-1).start();
			       
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
			  //  System.out.println("mensagem recebida: "+pesquisa);

			    if(pesquisa != null) {
				
					String[] mensagem = pesquisa.split(";");
					
					switch(mensagem[0]) {
					
						case "pesquisa":
								addNovaSolicitacao(mensagem[1]);
								break;
						case "possuiArquivo":
								addRespostaTormentRespondente(mensagem[1],true,true);
								break;
						case "naoPossuiArquivo": 
								addRespostaTormentRespondente(mensagem[1],false,true);
								break;
						
						case "dispositivosConectados":
								enviaDispositivosConectados();
								break;
						
						case "conexao": 
								break;
						
						case "desconectar":
							System.out.println("Desconectando dispostivo");
							tormentsConectados.remove(this);
							this.stop();
							break;
					
					}
			    }
					
			} catch (IOException e) {e.printStackTrace();}
		 }
		 
		 public Socket getConnection() {
			return connection;
		 } 

		 public void setConnection(Socket connection) {
			this.connection = connection;
		 }

		 private int getSolicitacaoArquivo(String pesquisa) {
			 int pos = -1;
			 boolean existe = false;
			 
			 for(int i = 0; i < solicitacoes.size();i++) {
				 if (pesquisa.equals(solicitacoes.get(i).getNomeArquivo()) && !solicitacoes.get(i).isResultadoEnviado()) {
					 	pos = i;
				 }
			 }
			 
			 return pos;
		 }
		 
		 private void enviaDispositivosConectados() {
			 String mensagem = "dispositivosConectados";
			 System.out.println("Enviando solicitacao");

			 if (tormentsConectados.size() > 1) {
				 for (ProcessoExclusivo p: tormentsConectados) {
						System.out.println("disp: " + p.getConnection().getRemoteSocketAddress() + " is " + p.getConnection().isConnected());
						
					 if ((p.getConnection().isConnected() || p.getConnection() != null || 
							 !p.getConnection().isClosed())) { 
						 if (!p.getConnection().equals(connection))
							 mensagem += ";"+p.getConnection().getRemoteSocketAddress();
					 }else
						 tormentsConectados.remove(p);
				 }
			 } else
				 mensagem += ";false";
			 
			 System.out.println("enviando..."+mensagem);
			 enviarMensagem(mensagem);
		 }
		 
		 // envia a pesquisa para os torments adicionados a lista de respondentes
		 private void pesquisar(String pesquisa) {
			 
			 ArrayList<TormentRespondente> tr = solicitacoes.get(getSolicitacaoArquivo(pesquisa)).getTormentsRespondentes();
			 
			 for (int i = 0 ;i < tr.size();i++) {
				 
				 enviarPesquisaItem("pesquisa;"+pesquisa,tr.get(i).getTormentRespondente());
			 
			 }	
		 }
		 
		 private void enviarMensagem(String pesquisa) {
			 try {
				 BufferedWriter bw = new BufferedWriter(new OutputStreamWriter(connection.getOutputStream()));
                 bw.write(pesquisa);
                 bw.newLine();
                 bw.flush();
			
			} catch (IOException e) {e.printStackTrace();}
		 }
		 
		 private void enviarPesquisaItem(String pesquisa, Socket s) {
			 try {
				 				
				 BufferedWriter bw = new BufferedWriter(new OutputStreamWriter(s.getOutputStream()));
                 bw.write(pesquisa);
                 bw.newLine();
                 bw.flush();
             
			} catch (IOException e) {e.printStackTrace();}
		 }
		 
		 private void addRespostaTormentRespondente(String pesquisa,boolean possuiArquivo,boolean respondeuRequisicao) {
			 	int pos = 0;
			 	for(TormentRespondente t : solicitacoes.get(getSolicitacaoArquivo(pesquisa)).getTormentsRespondentes()) {
			 		
			 		if(t.getTormentRespondente().equals(connection)) {
			 			
			 			solicitacoes.get(getSolicitacaoArquivo(pesquisa)).getTormentsRespondentes().get(pos).setPossuiArquivo(possuiArquivo);
			 			solicitacoes.get(getSolicitacaoArquivo(pesquisa)).getTormentsRespondentes().get(pos).setRespondeuRequisicao(respondeuRequisicao);
			 		
			 		}
			 	
			 		pos++;
			 	
			 	}
					
		 }
		 
		 private void addTormentRespondente(int id,Socket s,boolean possuiArquivo,boolean respondeuRequisicao) {
				solicitacoes.get(id).
					addTormentRespondente(new TormentRespondente(s,possuiArquivo,respondeuRequisicao));
		 }
		 
		 private int verificaSolicitacaoExistente(String pesquisa) {
			 return getSolicitacaoArquivo(pesquisa);
			 
		 }
		 
		 // Cria uma nova solicitacao de arquivo e adiciona os torments conectados no momento a lista de respondentes
		 private void addNovaSolicitacao(String pesquisa) {
			 
			 int pos = verificaSolicitacaoExistente(pesquisa);
			 
			 if (pos == -1) {
				 solicitacoes.add(new SolicitacaoArquivo(pesquisa,connection));
				 
				 pos = getSolicitacaoArquivo(pesquisa);
				 
				 for (ProcessoExclusivo p : tormentsConectados) {
					// ((!p.getConnection().isConnected() || p.getConnection() == null || p.getConnection().isClosed())) 
					//		tormentsConectados.remove(p);
					 if ((p.getConnection().isConnected()))
						 addTormentRespondente(pos,p.getConnection(), false,false);
				 
				 }
				 
				 pesquisar(pesquisa);
				 
			 }else {
				 
				 boolean existe = false;
				 for(int i = 0; i < solicitacoes.get(pos).getTormentsSolicitantes().size(); i++)
					 if(solicitacoes.get(pos).getTormentsSolicitantes().get(i).equals(connection))
						 existe = true;
				 
				 if(!existe)
					 solicitacoes.get(pos).addTormentPesquisador(connection);
			 }
			 
		 }
	 }
	 
	 public class VerificaSolicitacoes extends Thread{
		 
		 @Override
		 public void run() {
		     super.run();
		     
		     while(true) {
		    	 try {
		    		 
		    		 verificaSolicitacaoCompleta();
		    		 
					Thread.sleep(2000);
					
				} catch (InterruptedException e) {e.printStackTrace();}
		     } 
		 }
		 
		 
		 public void verificaSolicitacaoCompleta() {
			 
			 SolicitacaoArquivo s = null;
			 System.out.println("Solicitacoes :" + solicitacoes.size());

			 for(int i = 0; i < solicitacoes.size();i++) {
				 
				 
				 s = solicitacoes.get(i);
				 
				 if (s.isResultadoEnviado() == false) {
					 
					 boolean todosResponderam = true;
					 
					 for (int pos = 0;pos < s.getTormentsRespondentes().size();pos++) {
						 if (!s.getTormentsRespondentes().get(pos).isRespondeuRequisicao())
							 todosResponderam = false; 
					 }
					 
					 if(todosResponderam) {
						 	enviaResultadoSolicitacao(solicitacoes.get(i));
				 			solicitacoes.get(i).setResultadoEnviado(true);
					 } 
				 }
			}
		 }
		 
		 private boolean enviaResultadoSolicitacao(SolicitacaoArquivo s)  {
			 
			 ArrayList<TormentRespondente> respondentes = new ArrayList<>();; 
			 String resultado = "resultado";
			 
			 
			 for (TormentRespondente tr : s.getTormentsRespondentes()) {
				 if (tr.isPossuiArquivo())
					 	respondentes.add(tr);
			 }
			 
			 if (respondentes.size() > 0) {
				 
				 for (TormentRespondente socket : respondentes)
					 resultado += ";"+socket.getTormentRespondente().getRemoteSocketAddress().toString(); 
			
				 // rever isso aqui
				 boolean resultadoEnviado = true;
				 				 
				 for (Socket solicitante: s.getTormentsSolicitantes())
					 resultadoEnviado = enviaResultadoSolicitacao(solicitante,resultado);
				 
				 return resultadoEnviado;
				 
			 } else {
				 
				 resultado += ";false"; 

				 boolean resultadoEnviado = true;


				 System.out.println(resultado+" - torments solicitantes: "+s.getTormentsSolicitantes());
				 				 
				 for (Socket solicitante: s.getTormentsSolicitantes())
					 resultadoEnviado = enviaResultadoSolicitacao(solicitante,resultado);
				 
				 return resultadoEnviado;
			 }
			 
		 }
		 
		 private boolean enviaResultadoSolicitacao(Socket s, String mensagem) {
			 try {

				 System.out.println(mensagem);
				 PrintStream ps = new PrintStream(s.getOutputStream());
		         ps.println(mensagem);
				 return true;
				
			} catch (IOException e) {e.printStackTrace();}
			 
			 return false;
		 }
		 
	 }
}