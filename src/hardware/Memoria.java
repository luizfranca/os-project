package hardware;

import SO.Pagina;

public class Memoria {
	
	private Pagina[] memoria;
	
	public Memoria(int tamanho) {
		this.memoria = new Pagina[tamanho];
	}
	
	public void setPagina(int i, Pagina pagina){
		this.memoria[i] = pagina;
	}
	
	public Pagina getPagina(int i){
		return this.memoria[i];
	}
	
	public int getSize() {
		return this.memoria.length;
	}
}
