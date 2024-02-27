package br.com.rinha.data.model;

public class Cliente {
	
    private int id;
	
	private String nome;
	
	private int limite;
	
	public Cliente(int id, String nome, int limite) {
		super();
		this.id = id;
		this.nome = nome;
		this.limite = limite;
	}

	public Cliente() {
		super();
	}

	public int getId() {
		return id;
	}

	public void setId(int id) {
		this.id = id;
	}

	public String getNome() {
		return nome;
	}

	public void setNome(String nome) {
		this.nome = nome;
	}

	public int getLimite() {
		return limite;
	}

	public void setLimite(int limite) {
		this.limite = limite;
	}

	@Override
	public String toString() {
		return "Cliente{" +
				"id=" + id +
				", nome='" + nome + '\'' +
				", limite=" + limite +
				'}';
	}
}
