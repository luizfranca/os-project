package hardware;

import SO.MMU;
import SO.SO;
import SO.Status;

public class Processador {

	private MMU mmu;
	private SO so;
	private int PC;
	
	public Processador (MMU mmu, SO so){
		this.mmu = mmu;
		this.so = so;
	}
	
	public void executar(){
		String instrucao = this.mmu.proximaInstrucao(this.PC);
		
		PC += 1;
		
		System.out.println("Instrucao executada: " + instrucao);
		
		if (instrucao.equals("IO")) {
			this.so.setEstado(Status.BLOQUEADO);
			this.so.trocarContexto();
		} if (instrucao.equals("Ja acabou, Jessica?")) {
			this.so.destruirProcesso();
			this.so.trocarContexto();
		} 
	}
	
	public void setPC(int PC) {
		this.PC = PC;
	}
}