import java.net.Socket;

public class TormentRespondente {
	private Socket tormentRespondente;
	private boolean possuiArquivo;
	private boolean respondeuRequisicao;
	
	public TormentRespondente(Socket s,boolean possuiArquivo,boolean respondeuRequisicao) {
		this.tormentRespondente = s;
		this.possuiArquivo = possuiArquivo;
		this.respondeuRequisicao = respondeuRequisicao;
	}
	
	public boolean isRespondeuRequisicao() {
		return respondeuRequisicao;
	}

	public void setRespondeuRequisicao(boolean respondeuRequisicao) {
		this.respondeuRequisicao = respondeuRequisicao;
	}

	public Socket getTormentRespondente() {
		return tormentRespondente;
	}
	
	public void setTormentRespondente(Socket tormentRespondente) {
		this.tormentRespondente = tormentRespondente;
	}
	
	public boolean isPossuiArquivo() {
		return possuiArquivo;
	}
	
	public void setPossuiArquivo(boolean possuiArquivo) {
		this.possuiArquivo = possuiArquivo;
	}
}
