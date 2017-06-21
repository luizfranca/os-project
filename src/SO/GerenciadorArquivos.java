package SO;

import java.io.BufferedReader;
import java.io.FileNotFoundException;
import java.io.FileReader;
import java.io.IOException;
import java.util.ArrayList;
import java.util.Date;

public class GerenciadorArquivos {

	private ArrayList<Programa> programas;
	private boolean longo; // se vai ser a pasta dos programs longos ou curtos
	
	public GerenciadorArquivos(boolean longo) {
		this.programas = new ArrayList<Programa>();
		this.longo = longo;
	}
	
	// 0 - numeroPaginas
	// 1 - idPrograma
	public ArrayList<int[]> carregarProgramas() {
		ArrayList<int[]> programas = new ArrayList<int[]>();
		
		String folder; int numeroProgramas;
		
		if (this.longo) {
			folder = "Programas_longos";
			numeroProgramas = 11;
		} else {
			folder = "Programas_curtos";
			numeroProgramas = 8;
		}
		
		for (int i = 1; i < numeroProgramas; i++) {
			ArrayList<String> dados = new ArrayList<String>();
			
			String fileName = folder + "/programa" + i + ".txt";
			
			String line = null;

	        try {
	            // FileReader reads text files in the default encoding.
	            FileReader fileReader = new FileReader(fileName);

	            // Always wrap FileReader in BufferedReader.
	            BufferedReader bufferedReader = new BufferedReader(fileReader);

	            while((line = bufferedReader.readLine()) != null) {
	                dados.add(line);
	            }   
	            Programa p = new Programa(dados);
	            
	            int numeroPaginas = (int) Math.ceil((double)dados.size() / 8);
	            
	            if (dados.size() % 8 == 0) {
	            	numeroPaginas--;
	            }
	            
	            programas.add(new int[] { numeroPaginas,  p.getIdPrograma() });
	            
	            this.programas.add(p);
	            // Always close files.
	            bufferedReader.close();         
	        }
	        catch(FileNotFoundException ex) {
	            System.out.println(
	                "Unable to open file '" + fileName + "'");                
	        }
	        catch(IOException ex) {
	            System.out.println(
	                "Error reading file '" + fileName + "'");
	        }
		}
		
		return programas;
	}
	
	public ArrayList<Pagina> carregarPaginas(int idPrograma, int paginaInicial, int quantidade, int idProcesso) {
		Programa programa = null;
		
		ArrayList<Pagina> paginas = new ArrayList<Pagina>();
		
		for (int i = 0; i < this.programas.size(); i++) {
			programa = this.programas.get(i); 
			if (programa.getIdPrograma() == idPrograma) {
				break;
			}
		}
		
		for (int i = 0; i < quantidade; i++) {
			ArrayList<String> instrucoes = programa.getDados(paginaInicial);
			
			if (instrucoes == null) {
				break;
			}
			paginas.add(new Pagina(idProcesso, paginaInicial++, false, new Date(), instrucoes.toArray(new String[instrucoes.size()])));
		}
		
		return paginas;
	}
}