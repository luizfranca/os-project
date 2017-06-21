package SO;

import java.util.Date;

public class Pagina {
	
	private int idProcesso;
	private int idPagina;
	private boolean referenciada;
	private Date instanteInicial;
	private String[] instrucoes = new String[8];
	
	public Pagina(int idProcesso, int idPagina, boolean referenciada, Date instanteInicial, String[] instrucoes) {
		this.idProcesso = idProcesso;
		this.idPagina = idPagina;
		this.referenciada = referenciada;
		this.instanteInicial = instanteInicial;
		this.instrucoes = instrucoes;
	}

	public int getIdProcesso() {
		return idProcesso;
	}

	public void setIdProcesso(int idProcesso) {
		this.idProcesso = idProcesso;
	}

	public int getIdPagina() {
		return idPagina;
	}

	public void setIdPagina(int idPagina) {
		this.idPagina = idPagina;
	}

	public boolean isReferenciada() {
		return referenciada;
	}

	public void setReferenciada(boolean referenciada) {
		this.referenciada = referenciada;
	}

	public Date getInstanteInicial() {
		return instanteInicial;
	}

	public void setInstanteInicial(Date instanteInicial) {
		this.instanteInicial = instanteInicial;
	}

	public String getInstrucao(int index) {
		return instrucoes[index];
	}

	public void setInstrucoes(String[] instrucoes) {
		this.instrucoes = instrucoes;
	}	
}