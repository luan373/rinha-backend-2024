package br.com.rinha.data.model;

import com.fasterxml.jackson.annotation.JsonFormat;
import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import com.fasterxml.jackson.annotation.JsonProperty;

import java.time.LocalDateTime;

@JsonIgnoreProperties(value = { "id","cliente" })
public class Transacao {

	private int id;
	
	private Cliente cliente;

	private int valor;

	private TipoTransacao tipo;

	private String descricao;

	@JsonFormat(shape = JsonFormat.Shape.STRING)
	@JsonProperty("realizada_em")
	private LocalDateTime realizadaEm = LocalDateTime.now();
	
	public Transacao(Cliente cliente, int valor, TipoTransacao tipo, String descricao) {
		super();
		this.cliente = cliente;
		this.valor = valor;
		this.tipo = tipo;
		this.descricao = descricao;
	}

	public Transacao() {
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

	public TipoTransacao getTipo() {
		return tipo;
	}

	public void setTipo(TipoTransacao tipo) {
		this.tipo = tipo;
	}

	public String getDescricao() {
		return descricao;
	}

	public void setDescricao(String descricao) {
		this.descricao = descricao;
	}

	public LocalDateTime getRealizadaEm() {
		return realizadaEm;
	}

	public void setRealizadaEm(LocalDateTime realizadaEm) {
		this.realizadaEm = realizadaEm;
	}
}
