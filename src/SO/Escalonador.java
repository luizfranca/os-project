package SO;

import java.util.ArrayList;

public class Escalonador {
	
	private boolean preemptivo;
	private ArrayList<PCB> tabelaProcessos;
	
	public Escalonador(boolean preemptivo, ArrayList<PCB> tabelaProcessos) {
		this.preemptivo = preemptivo;
		this.tabelaProcessos = tabelaProcessos;
	}
	
	public PCB proximoProcesso() {
		PCB processo;
		
		if (this.preemptivo) {
			processo = roundRobin();
		} else {
			processo = FIFO();
		}
		
		return processo; 
	}
	
	public boolean getPreemptivo() {
		return this.preemptivo;
	}
	
	private PCB FIFO() {
		PCB processo = null;
		
		for (int i = 0; i < this.tabelaProcessos.size(); i++) {
			PCB aux = this.tabelaProcessos.get(i);
			
			if (processo != null) {
				if (aux.getEstadoProcesso() == Status.PRONTO) {
					if (processo.getInstanteInicial().after(aux.getInstanteInicial())) {
						processo = aux;
					}
				}
			} else {
				if (aux.getEstadoProcesso() == Status.PRONTO) {
					processo = aux;
				}
			}
		}
		
		return processo;
	}
	
	private PCB roundRobin() {
		PCB processo = null;
		
		for (int i = 0; i < this.tabelaProcessos.size(); i++) {
			PCB aux = this.tabelaProcessos.get(i);
			
			if (processo != null) {
				if (aux.getEstadoProcesso() == Status.PRONTO) {
					if (processo.getInstanteInicial().after(aux.getInstanteInicial())) {
						processo = aux;
					}
				}
			} else {
				if (aux.getEstadoProcesso() == Status.PRONTO) {
					processo = aux;
				}
			}
		}
		
		return processo;
	}
}