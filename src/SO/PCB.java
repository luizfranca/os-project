package SO;

import java.util.Date;

public class PCB {
	
	private static int idProcessoContador;
	
	private int idProcesso; // id do processo
	private int idPrograma; // referecia do programa que ele esta relacionado
	private int PC; // posicao onde o processo parou
	private int enderecoRAM; // endereco inicial do processo na memoria virtual ram
	private int enderecoCache;
	private Status estadoProcesso; // Estado do processo
	private Date instanteInicial; // tempo de quanto ele entra na memoria ou quando ele sai do processador
	
	public PCB(int idPrograma) {
		this.idProcesso = PCB.idProcessoContador++;
		this.idPrograma = idPrograma;
		this.PC = 0;
		this.enderecoRAM = 0;
		this.enderecoCache = 0;
		this.estadoProcesso = Status.PRONTO;
		this.instanteInicial = new Date();
	}

	public int getIdProcesso() {
		return idProcesso;
	}

	public void setIdProcesso(int idProcesso) {
		this.idProcesso = idProcesso;
	}

	public int getIdPrograma() {
		return idPrograma;
	}

	public void setIdPrograma(int idPrograma) {
		this.idPrograma = idPrograma;
	}

	public int getPC() {
		return PC;
	}

	public void setPC(int PC) {
		this.PC = PC;
	}

	public int getEnderecoRAM() {
		return enderecoRAM;
	}

	public void setEnderecoRAM(int enderecoRAM) {
		this.enderecoRAM = enderecoRAM;
	}

	public int getEnderecoCache() {
		return enderecoCache;
	}

	public void setEnderecoCache(int enderecoCache) {
		this.enderecoCache = enderecoCache;
	}

	public Status getEstadoProcesso() {
		return estadoProcesso;
	}

	public void setEstadoProcesso(Status estadoProcesso) {
		this.estadoProcesso = estadoProcesso;
	}

	public Date getInstanteInicial() {
		return instanteInicial;
	}

	public void setInstanteInicial(Date instanteInicial) {
		this.instanteInicial = instanteInicial;
	}
}