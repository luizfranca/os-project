package SO;

public class Celula {

	private boolean valido; // se ele ta na memoria ram
	private int ponteiro;  // posicao onde ele ta na memoria ram
	private int idProcesso; // qual processo
	private int idPagina; // qual pagina
	
	public Celula() {
		this.valido = false;
		this.ponteiro = 0;
		this.idProcesso = 0;
		this.idPagina = 0;
	}
	
	public Celula(int idProcesso, int idPagina) {
		this.valido = false;
		this.ponteiro = 0;
		this.idProcesso = idProcesso;
		this.idPagina = idPagina;
	}
	
	public boolean isValido() {
		return valido;
	}
	public void setValido(boolean valido) {
		this.valido = valido;
	}
	public int getPonteiro() {
		return ponteiro;
	}
	public void setPonteiro(int ponteiro) {
		this.ponteiro = ponteiro;
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
}
