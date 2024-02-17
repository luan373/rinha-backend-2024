package br.com.rinha.rest.payload;

import com.fasterxml.jackson.annotation.JsonProperty;

public class TransacaoPayload {

	@JsonProperty(required = true)
	private String valor;

	@JsonProperty(required = true)
	private String tipo;

	@JsonProperty(required = true)
	private String descricao;
	
	public TransacaoPayload() {
		super();
	}

	public TransacaoPayload(String valor, String tipo, String descricao) {
		super();
		this.valor = valor;
		this.tipo = tipo;
		this.descricao = descricao;
	}

	public String getValor() {
		return valor;
	}

	public void setValor(String valor) {
		this.valor = valor;
	}

	public String getTipo() {
		return tipo;
	}

	public void setTipo(String tipo) {
		this.tipo = tipo;
	}

	public String getDescricao() {
		return descricao;
	}

	public void setDescricao(String descricao) {
		this.descricao = descricao;
	}

	@Override
	public String toString() {
		return "TransacaoPayload{" +
				"valor='" + valor + '\'' +
				", tipo='" + tipo + '\'' +
				", descricao='" + descricao + '\'' +
				'}';
	}
}
