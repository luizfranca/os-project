package SO;

import java.util.ArrayList;
import java.util.Date;
import java.util.Scanner;
import java.util.concurrent.TimeUnit;

import hardware.Memoria;
import hardware.Processador;

public class SO {
	
	private MMU mmu;
	private Memoria ram;
	private Memoria cache;
	private Escalonador escalonador;
	private Processador processador;
	private GerenciadorArquivos gerenciadorArquivos;
	private ArrayList<PCB> tabelaProcessos;
	private PCB processoAtual;
	
	private int quantum;
	private int tick;
	private ArrayList<PCB> processoIO;
	private ArrayList<Integer> tempoIO;
	
	public SO(boolean preemptivo, boolean longo) {
		this.ram = new Memoria(64);
		this.cache = new Memoria(16);
		this.mmu = new MMU(this, this.ram, this.cache);
		this.tabelaProcessos = new ArrayList<PCB>();
		this.escalonador = new Escalonador(preemptivo, this.tabelaProcessos);
		this.gerenciadorArquivos = new GerenciadorArquivos(longo);
		this.processoIO = new ArrayList<PCB>();
		this.tempoIO = new ArrayList<Integer>();
		
		this.quantum = 4;
		this.tick = 1;
		this.processador = new Processador(this.mmu, this);
	}
	
	public int getEnderecoCache() {
		return this.processoAtual.getEnderecoCache();
	}

	public int getEnderecoRAM() {
		return this.processoAtual.getEnderecoRAM();
	}

	public int getIdProcesso() {
		return this.processoAtual.getIdProcesso();
	}
	
	public int getIdPrograma() {
		return this.processoAtual.getIdPrograma();
	}
	
	private void carregarProgramas() {
		ArrayList<int[]> programas = this.gerenciadorArquivos.carregarProgramas();
		
		for (int[] is : programas) {
			System.out.println("Numero Paginas: " + is[0] + " IdPrograma: " + is[1]);
		}
		
		for (int i = 0; i < programas.size(); i++) {
			//carregar tabela de processos
			this.tabelaProcessos.add(new PCB(programas.get(i)[1]));
		}
		
		//carregar todos os programas para a memoria virtual na mmu
		this.mmu.carregarTabelaPaginas(programas, this.tabelaProcessos);
		
		int processosRAM = 6;
		if (programas.size() < processosRAM) {
			processosRAM = programas.size();
		}
		
		int processosCache = 3;
		if (programas.size() < processosCache) {
			processosCache = programas.size();
		}
		
		if (programas.size() > 0) {
			for (int i = 0; i < processosRAM; i++) {
				ArrayList<Pagina> paginas = recuperarPaginas(programas.get(i)[1], 0, 8, this.tabelaProcessos.get(i).getIdProcesso());
				
				this.mmu.carregarPaginasRAM(paginas);
				
				if (i < processosCache) {
					int indexTo = 4;
					if (paginas.size() < 4) {
						indexTo = paginas.size();
					}
					this.mmu.carregarPaginasCache(new ArrayList<Pagina>(paginas.subList(0, indexTo)));
				}
			}
			
			// atualizar endereco cache e ram dos pcbs da tabela de processo
			for (int i = 0; i < this.tabelaProcessos.size(); i++) {
				int idProcesso = this.tabelaProcessos.get(i).getIdProcesso();
				int enderecoRAM = this.mmu.encontrarPaginaRAM(idProcesso);
				int enderecoCache = this.mmu.encontrarPaginaCache(idProcesso);
				
				this.tabelaProcessos.get(i).setEnderecoRAM(enderecoRAM);
				this.tabelaProcessos.get(i).setEnderecoCache(enderecoCache);
			}
		}
	}
	
	public void destruirProcesso() {
		this.processoAtual.setEstadoProcesso(Status.BLOQUEADO);
		
		int destruir = this.processoAtual.getIdProcesso();

		System.out.println("Destrui processo: " + destruir);
		
		//destruir entrada nas memorias
		this.mmu.destruirProcesso(destruir);
		
		//destruir entrada na tabela de processo
		for (int i = 0; i < this.tabelaProcessos.size(); i++) {
			PCB processo = this.tabelaProcessos.get(i);
			if (processo.getIdProcesso() == destruir) {
				this.tabelaProcessos.remove(i);
			}
		}
		
		System.out.println("Processos ainda em execução");
		for (PCB pcb : this.tabelaProcessos) {
			System.out.println("IdProcesso: " + pcb.getIdProcesso());
		}
		
		this.processoAtual = null;
	}

	public void setEstado(Status bloqueado) {
		this.processoAtual.setInstanteInicial(new Date());
		this.processoAtual.setPC(this.processoAtual.getPC());
		this.processoAtual.setEstadoProcesso(bloqueado);
		this.processoIO.add(this.processoAtual);
		this.tempoIO.add(5);
		this.tick = 1;
	}
	
	public void salvarContexto() {
		if (this.processoAtual != null) {
			this.processoAtual.setInstanteInicial(new Date());
		}
	}

	public void trocarContexto() {
		System.out.println("Troca de Contexto...............................................................................");
		if (this.processoAtual != null) {
			if (this.processoAtual.getEstadoProcesso() != Status.BLOQUEADO) {
				this.processoAtual.setEstadoProcesso(Status.PRONTO);
			}
		}
		
		this.processoAtual = this.escalonador.proximoProcesso();

		if (this.processoAtual != null) {
			this.processador.setPC(this.processoAtual.getPC());
			this.processoAtual.setEstadoProcesso(Status.EXECUTANDO);
			System.out.println("Processo atual: " + this.processoAtual.getIdProcesso() + " PC: " + this.processoAtual.getPC());
		}
		
		this.tick = 1;
	}

	public ArrayList<Pagina> recuperarPaginas(int idPrograma, int paginaInicial, int blocoRAM, int idProcesso) {
		return this.gerenciadorArquivos.carregarPaginas(idPrograma, paginaInicial, blocoRAM, idProcesso);
	}
	
	public void atualizarPC(int pc) {
		this.processoAtual.setPC(pc);
	}
	
	private void executarIO() {
		for (int i = 0; i < this.processoIO.size(); i++) {
			int tempo = this.tempoIO.get(i) - 1;
			
			this.tempoIO.set(i, tempo);
			
			if (this.tempoIO.get(i) == 0) {
				this.tempoIO.remove(i);
				this.processoIO.get(i).setInstanteInicial(new Date());
				this.processoIO.get(i).setEstadoProcesso(Status.PRONTO);
				this.processoIO.remove(i);
			}
		}
	}
	
	public void executarSO() {
		while (this.tabelaProcessos.size() > 0) {
			this.tick++;
			
			if (this.processoAtual != null){
				this.processador.executar();
			} else {
				System.out.println("Todos os processos estão bloqueados");
				trocarContexto();
			}
			
			if (this.escalonador.getPreemptivo()) {
				if (this.tick > this.quantum) {
					this.salvarContexto();
					this.trocarContexto();
				}
			}
			
			executarIO();
			
			try {// se tirar essa parte é possivel que cause erro pelo fato que todos os processo tem o mesmo tempo
				TimeUnit.SECONDS.sleep(1); 
			} catch (InterruptedException e) {
				e.printStackTrace();
			}
		}
	}
	
	public void init() {
		System.out.println("Iniciando Sistema de Integracao Gerencial Externa (SIGE)...");
		
		carregarProgramas();
		
		System.out.println("Processos a serem executados...");
		
		for (PCB pcb : this.tabelaProcessos) {
			System.out.println("IdProcesso: " + pcb.getIdProcesso() + " endereco ram: " + pcb.getEnderecoRAM());
		}
		
		trocarContexto();
		
		executarSO();
		
		System.out.println("Finalizando SIGE");
	}
	
	public static void main(String[] args) {
		
		System.out.println("(1) para Preemptivo\n(2) para não preemptivo");
		
		Scanner in = new Scanner(System.in);
		
		String input = in.nextLine();
		
		System.out.println("(1) para programas logos\n(2) para programas curtos");
		
		String programa = in.nextLine();
		
		if ((input.equals("1") || input.equals("2")) && (programa.equals("1") || programa.equals("2"))) {
			
			boolean longo = programa.equals("1");
			
			boolean preemptivo = input.equals("1");
			
			SO so = new SO(preemptivo, longo);
			
			so.init();
		} else {
			System.out.println("\nComando invalido!");
		}
		
		in.close();
	}
}