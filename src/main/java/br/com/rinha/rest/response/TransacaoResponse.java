package br.com.rinha.rest.response;

public class TransacaoResponse {

	private int limite;

	private int saldo;

	public TransacaoResponse() {
		super();
	}

	public TransacaoResponse(int limite, int saldo) {
		super();
		this.limite = limite;
		this.saldo = saldo;
	}

	public int getLimite() {
		return limite;
	}

	public void setLimite(int limite) {
		this.limite = limite;
	}

	public int getSaldo() {
		return saldo;
	}

	public void setSaldo(int saldo) {
		this.saldo = saldo;
	}

}
