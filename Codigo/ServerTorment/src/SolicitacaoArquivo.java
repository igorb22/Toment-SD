import java.net.Socket;
import java.util.ArrayList;

public class SolicitacaoArquivo {
	private String nomeArquivo;
	private Socket tormentSolicitante;
	private ArrayList<Socket> tormentsQuePossuemArquivo;
	private int qtdRespostas;
	
	
	public SolicitacaoArquivo(String nomeArquivo,Socket tormentSolicitante) {
		this.nomeArquivo = nomeArquivo;
		this.tormentSolicitante = tormentSolicitante;
		this.tormentsQuePossuemArquivo = new ArrayList<Socket>();
		this.qtdRespostas = 0;
	}


	
	public int getQtdRespostas() {
		return qtdRespostas;
	}
	
	public void incrementQtdRespostas() {
		this.qtdRespostas++;;
	}
	
	public void decrementQtdRespostas() {
		this.qtdRespostas--;
	}

	public void setQtdRespostas(int qtdRespostas) {
		this.qtdRespostas = qtdRespostas;
	}



	public void setTormentsQuePossuemArquivo(ArrayList<Socket> tormentsQuePossuemArquivo) {
		this.tormentsQuePossuemArquivo = tormentsQuePossuemArquivo;
	}



	public String getNomeArquivo() {
		return nomeArquivo;
	}


	public void setNomeArquivo(String nomeArquivo) {
		this.nomeArquivo = nomeArquivo;
	}


	public Socket getTormentSolicitante() {
		return tormentSolicitante;
	}


	public void setTormentSolicitante(Socket tormentSolicitante) {
		this.tormentSolicitante = tormentSolicitante;
	}


	public ArrayList<Socket> getTormentsQuePossuemArquivo() {
		return tormentsQuePossuemArquivo;
	}
	
	public void addTorment(Socket tormentsQuePossuemArquivo) {
		this.tormentsQuePossuemArquivo.add(tormentsQuePossuemArquivo);
	}
	
	public Socket getTorment(int id) {
		return this.tormentsQuePossuemArquivo.get(id);
	}
	
	
	
	
}
