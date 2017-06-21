package SO;

import java.util.ArrayList;

public class Programa {

	private static int idProgramaContador;
	
	private int idPrograma;
	public ArrayList<String> dados;
	
	public Programa(ArrayList<String> dados) {
		this.idPrograma = Programa.idProgramaContador++;
		this.dados = dados;
	}

	public int getIdPrograma() {
		return idPrograma;
	}

	public ArrayList<String> getDados(int idPagina) {
		if (idPagina * 8 > this.dados.size()) {
			return null;
		}
		
		int toIndex;
		
		int instrucoesSobrando = dados.size() - idPagina * 8;
		
		if (instrucoesSobrando < 8) {
			//System.out.println("oi: " + dados.size() + " idPrograma: " + idPrograma + " idPagina: " + idPagina);
			toIndex = dados.size();
		}
		else {
			toIndex = idPagina * 8 + 8;
		}
		
		return new ArrayList<String>(dados.subList(idPagina * 8, toIndex));
	} 
}
