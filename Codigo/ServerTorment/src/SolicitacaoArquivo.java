import java.net.Socket;
import java.util.ArrayList;

public class SolicitacaoArquivo {
	
	private String nomeArquivo;
	private ArrayList<Socket> tormentsSolicitantes;
	private ArrayList<TormentRespondente> tormentsRespondentes;
	private boolean resultadoEnviado;
	
	
	public SolicitacaoArquivo(String nomeArquivo,Socket tormentSolicitante) {
		this.tormentsSolicitantes = new ArrayList<Socket>();
		this.tormentsRespondentes = new ArrayList<TormentRespondente>();
		this.nomeArquivo = nomeArquivo;
		this.tormentsSolicitantes.add(tormentSolicitante);
		this.resultadoEnviado = false;
	}
	
	
	public boolean isResultadoEnviado() {
		return resultadoEnviado;
	}

	public void setResultadoEnviado(boolean resultadoEnviado) {
		this.resultadoEnviado = resultadoEnviado;
	}

	public void setTormentsRespondentes(ArrayList<TormentRespondente> tormentsRespondentes) {
		this.tormentsRespondentes = tormentsRespondentes;
	}

	public String getNomeArquivo() {
		return nomeArquivo;
	}


	public void setNomeArquivo(String nomeArquivo) {
		this.nomeArquivo = nomeArquivo;
	}


	public ArrayList<TormentRespondente> getTormentsRespondentes() {
		return tormentsRespondentes;
	}
	
	public void addTormentRespondente(TormentRespondente tormentRespondente) {
		this.tormentsRespondentes.add(tormentRespondente);
	}
	
	public void addTormentPesquisador(Socket tormentPesquisador) {
		this.tormentsSolicitantes.add(tormentPesquisador);
	}
	
	public TormentRespondente getTormentRespondente(int id) {
		return this.tormentsRespondentes.get(id);
	}

	public ArrayList<Socket> getTormentsSolicitantes() {
		return tormentsSolicitantes;
	}

	public void setTormentsSolicitantes(ArrayList<Socket> tormentsSolicitantes) {
		this.tormentsSolicitantes = tormentsSolicitantes;
	}
}
