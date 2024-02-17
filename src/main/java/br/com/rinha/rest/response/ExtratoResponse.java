package br.com.rinha.rest.response;

import java.util.List;

import br.com.rinha.data.model.Transacao;

public class ExtratoResponse {

	private SaldoResponse saldo;

	private List<Transacao> ultimas_transacoes;

	public ExtratoResponse(SaldoResponse saldo, List<Transacao> ultimas_transacoes) {
		super();
		this.saldo = saldo;
		this.ultimas_transacoes = ultimas_transacoes;
	}

	public ExtratoResponse() {
		super();
	}

	public SaldoResponse getSaldo() {
		return saldo;
	}

	public void setSaldo(SaldoResponse saldo) {
		this.saldo = saldo;
	}

	public List<Transacao> getUltimas_transacoes() {
		return ultimas_transacoes;
	}

	public void setUltimas_transacoes(List<Transacao> ultimas_transacoes) {
		this.ultimas_transacoes = ultimas_transacoes;
	}

}
