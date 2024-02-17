package br.com.rinha.data.model;

public class Saldo {
	
    private int id;
	
	private Cliente cliente;
	
	private int valor;

	public Saldo(int id, Cliente cliente, int valor) {
		super();
		this.id = id;
		this.cliente = cliente;
		this.valor = valor;
	}
	
	public Saldo(Cliente cliente, int valor) {
		super();
		this.cliente = cliente;
		this.valor = valor;
	}

	public Saldo() {
		super();
	}

	public int getId() {
		return id;
	}

	public void setId(int id) {
		this.id = id;
	}

	public Cliente getCliente() {
		return cliente;
	}

	public void setCliente(Cliente cliente) {
		this.cliente = cliente;
	}

	public int getValor() {
		return valor;
	}

	public void setValor(int valor) {
		this.valor = valor;
	}
	
}
