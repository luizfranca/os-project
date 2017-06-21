package SO;

import java.util.ArrayList;
import hardware.Memoria;

public class MMU {
	
	private SO so;
	private Memoria ram;
	private Memoria cache;
	private Celula[] memoriaVirtualRAM;
	private Celula[] memoriaVirtualCache;
	private int blocoCache;
	private int blocoRAM;
	
	public MMU(SO so, Memoria ram, Memoria cache){
		this.so = so;
		this.ram = ram;
		this.cache = cache;
		this.memoriaVirtualRAM = new Celula[1024];
		this.memoriaVirtualCache = new Celula[1024];
		
		this.blocoCache = 4;
		this.blocoRAM = 8;
	}

	public Celula[] getMemoriaVirtualRAM() {
		return this.memoriaVirtualRAM;
	}
	
	public Celula[] getMemoriaVirtualCache() {
		return this.memoriaVirtualCache;
	}
	
	public String proximaInstrucao(int pc) {
		desReferenciar();

		String instrucao = "";
		
		int enderecoCache = this.so.getEnderecoCache();
		
		int deslocamento = pc / 8;
		
		Celula celulaCache = this.memoriaVirtualCache[enderecoCache + deslocamento];
		
		if (celulaCache.isValido()) {
			instrucao = this.cache.getPagina(celulaCache.getPonteiro()).getInstrucao(pc % 8);
		} else {
			faltaPaginaCache(deslocamento, enderecoCache);
			
			imprimirMemorias();
			
			celulaCache = this.memoriaVirtualCache[enderecoCache + deslocamento];
			
			instrucao = this.cache.getPagina(celulaCache.getPonteiro()).getInstrucao(pc % 8);
			
			this.cache.getPagina(celulaCache.getPonteiro()).setReferenciada(true);
		}

		so.atualizarPC(++pc);
		
		return instrucao;
	}
	
	private void imprimirMemorias() {
		System.out.println("Imprimir estado Cache ===============================================================================");
		
		for (int i = 0; i < this.cache.getSize(); i++) {
			Pagina pagina = this.cache.getPagina(i);
			if (pagina != null) {
				System.out.println("Posicao: " + i + " idProcesso: " + pagina.getIdProcesso() + " idPagina: " + pagina.getIdPagina());
			} else {
				System.out.println("Posicao: "+ i + " NULL");
			}
		}
		
		System.out.println("Imprimir estado RAM =================================================================================");
		
		for (int i = 0; i < this.ram.getSize(); i++) {
			Pagina pagina = this.ram.getPagina(i);
			if (pagina != null) {
				System.out.println("Posicao: " + i + " idProcesso: " + pagina.getIdProcesso() + " idPagina: " + pagina.getIdPagina());
			} else {
				System.out.println("Posicao: "+ i + " NULL");
			}
		}
	}

	private int firstFit(Memoria memoria) {
		int resultado = -1;
		for (int i = 0; i < memoria.getSize() && resultado == -1; i++) {
			if (memoria.getPagina(i) == null) {
				resultado = i;
			}
		}
		
		return resultado;
	}
	// variavel idPagina inical evita que ele delete a pagina que esta sendo carregada no mesmo bloco
	private int secondChance(Memoria memoria, int idPaginaInicial, int idProcesso) {
		int posicao = -1;
		
		int maisVelho = 0; // processos mais velhos
		int segundoMaisVelho = 0;
		for (int i = 0; i < memoria.getSize(); i++) {
			Pagina aux = memoria.getPagina(i);
			if (aux.getIdProcesso() == idProcesso && aux.getIdPagina() >= idPaginaInicial) {
				continue;
			}
			
			if (aux.getInstanteInicial().before(memoria.getPagina(maisVelho).getInstanteInicial())) {
				segundoMaisVelho = maisVelho;
				maisVelho = i;
			} else if (aux.getInstanteInicial().before(memoria.getPagina(segundoMaisVelho).getInstanteInicial())) {
				segundoMaisVelho = i;
			}
		}
		
		if (memoria.getPagina(maisVelho).isReferenciada()) {
			posicao = maisVelho;
		} else {
			posicao = segundoMaisVelho;
		}
		
		return posicao;
	}
	// deslocamaento = numero da pagina
	private void faltaPaginaCache(int deslocamento, int enderecoCache) {
		System.out.println("Falta de página Cache");
		
		// assim ele pega a pagina atual
		if (deslocamento != 0) {
			deslocamento--;
		}

		int posicaoCache;
		
		int enderecoRAM = this.so.getEnderecoRAM();
		
		boolean faltaRAM = false;
		
		for (int i = 0; i < this.blocoCache; i++) {
			Celula celulaRAM = this.memoriaVirtualRAM[enderecoRAM + deslocamento + i];
			
			// quer dizer que acabou as paginas do processo
			if (celulaRAM == null || celulaRAM.getIdProcesso() != this.memoriaVirtualRAM[enderecoRAM].getIdProcesso()) {
				break;
			}
			
			if (!celulaRAM.isValido()) {
				if (faltaRAM) {
					break;
				}
				faltaPaginaRAM(deslocamento, enderecoRAM);
				faltaRAM = true;
			}
			
			posicaoCache = firstFit(this.cache);
			
			if (posicaoCache == -1) {
				posicaoCache = secondChance(this.cache, deslocamento, so.getIdProcesso()); //Cache
				
				Pagina aux = this.cache.getPagina(posicaoCache);
				
				int paginaInvalida = encontrarPagina(aux.getIdProcesso(), aux.getIdPagina(), this.memoriaVirtualCache);
				
				if (paginaInvalida != -1) {
					this.memoriaVirtualCache[paginaInvalida].setValido(false);
				}
			}
			
			Pagina pagina = this.ram.getPagina(celulaRAM.getPonteiro());
			
			carregarPaginas(pagina, this.cache, this.memoriaVirtualCache, posicaoCache, enderecoCache + deslocamento + i);
		}	
	}
	
	private void faltaPaginaRAM(int deslocamento, int enderecoRAM) {
		System.out.println("falta de pagina ram");
		
		ArrayList<Pagina> paginas = carregarBlocoRAM(so.getIdPrograma(), deslocamento, so.getIdProcesso());
		
		for (int i = 0; i < paginas.size(); i++) {
			int posicaoRAM = firstFit(this.ram);
			
			if (posicaoRAM == -1) {
				posicaoRAM = secondChance(this.ram, deslocamento, so.getIdProcesso());
				
				Pagina aux = this.ram.getPagina(posicaoRAM);
				
				int paginaInvalidaRAM = encontrarPagina(aux.getIdProcesso(), aux.getIdPagina(), this.memoriaVirtualRAM);
				
				if (paginaInvalidaRAM != -1) {
					this.memoriaVirtualRAM[paginaInvalidaRAM].setValido(false);
				}
				
				int paginaInvalidaCache = encontrarPagina(aux.getIdProcesso(), aux.getIdPagina(), this.memoriaVirtualCache);
				
				if (paginaInvalidaCache != -1) {
					if (this.memoriaVirtualCache[paginaInvalidaCache].isValido()) {
						int invalidarCache = this.memoriaVirtualCache[paginaInvalidaCache].getPonteiro();
						
						this.memoriaVirtualCache[paginaInvalidaCache].setValido(false);
						
						this.cache.setPagina(invalidarCache, null);
					}
				}
			}
			
			carregarPaginas(paginas.get(i), this.ram, this.memoriaVirtualRAM, posicaoRAM, enderecoRAM + deslocamento + i);
		}
	}
	
	private ArrayList<Pagina> carregarBlocoRAM(int idPrograma, int paginaInicial, int idProcesso) {
		ArrayList<Pagina> paginas = so.recuperarPaginas(idPrograma, paginaInicial, this.blocoRAM, idProcesso);
		return paginas;
	}
	
	private void carregarPaginas(Pagina pagina, Memoria memoria, Celula[] memoriaVirtual, int posicao, int endereco) {
		memoria.setPagina(posicao, pagina);
		memoriaVirtual[endereco].setValido(true);
		memoriaVirtual[endereco].setPonteiro(posicao);
	}
	
	private int encontrarPagina(int idProcesso, int idPagina, Celula[] memoriaVirtual) {
		int posicao = -1;
		
		for (int i = 0; i < memoriaVirtual.length && posicao == -1; i++) {
			if (memoriaVirtual[i] != null) {
				if (memoriaVirtual[i].getIdProcesso() == idProcesso) {
					if (memoriaVirtual[i].getIdPagina() == idPagina){
						posicao = i;
					}
				}
			}
		}
		return posicao;
	}
	
	public int encontrarPaginaRAM(int idProcesso) {
		return encontrarPagina(idProcesso, 0, this.memoriaVirtualRAM);
	}
	
	public int encontrarPaginaCache(int idProcesso) {
		return encontrarPagina(idProcesso, 0, this.memoriaVirtualCache);
	}
	
	private void desReferenciar() {
		for (int i = 0; i < this.ram.getSize(); i++) {
			if (this.ram.getPagina(i) != null) {
				this.ram.getPagina(i).setReferenciada(false);
			}
		}
		
		for (int i = 0; i < this.cache.getSize(); i++) {
			if (this.cache.getPagina(i) != null) {
				this.cache.getPagina(i).setReferenciada(false);
			}
		}
	}

	public void destruirProcesso(int idProcesso) {
		// Cache
		for (int i = 0; i < this.memoriaVirtualCache.length; i++) {
			Celula cel = this.memoriaVirtualCache[i];
			if (cel != null) {
				if (cel.getIdProcesso() == idProcesso) {
					if (cel.isValido()) {
						this.cache.setPagina(cel.getPonteiro(), null);
					}
					this.memoriaVirtualCache[i] = null;
				}
			}
		}
		// RAM
		for (int i = 0; i < this.memoriaVirtualRAM.length; i++) {
			Celula cel = this.memoriaVirtualRAM[i];
			if (cel != null) {
				if (cel.getIdProcesso() == idProcesso) {
					if (cel.isValido()) {
						this.ram.setPagina(cel.getPonteiro(), null);
					}
					this.memoriaVirtualRAM[i] = null;
				}
			}
		}
	}

	public void carregarTabelaPaginas(ArrayList<int[]> paginas, ArrayList<PCB> tabelaProcessos) {
		int numeroProcesso = 0 ;
		for (int i = 0; i < paginas.size(); i++) {
			int[] processo = paginas.get(i);
			
			for (int j = 0; j < processo[0]; j++) {
				this.memoriaVirtualCache[numeroProcesso] = new Celula(tabelaProcessos.get(i).getIdProcesso(), j);
				this.memoriaVirtualRAM[numeroProcesso++] = new Celula(tabelaProcessos.get(i).getIdProcesso(), j);
			}
		}	
	}
	
	public void carregarPaginasRAM(ArrayList<Pagina> paginas) {
		for (int i = 0; i < paginas.size(); i++) {
			int endereco = encontrarPagina(paginas.get(i).getIdProcesso(), paginas.get(i).getIdPagina(), this.memoriaVirtualRAM);
				
			carregarPaginas(paginas.get(i), this.ram, this.memoriaVirtualRAM, firstFit(this.ram), endereco);			
		}
	}
	
	public void carregarPaginasCache(ArrayList<Pagina> paginas) {
		for (int i = 0; i < paginas.size(); i++) {
			int endereco = encontrarPagina(paginas.get(i).getIdProcesso(), paginas.get(i).getIdPagina(), this.memoriaVirtualCache);
			
			carregarPaginas(paginas.get(i), this.cache, this.memoriaVirtualCache, firstFit(this.cache), endereco);
		}
	}
}