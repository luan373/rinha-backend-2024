package br.com.rinha.rest.response;

import java.io.Serializable;
import java.util.List;

import br.com.rinha.data.model.Transacao;

public class ExtratoResponse implements Serializable {

	private final SaldoResponse saldo;

	private final List<Transacao> ultimas_transacoes;

	public ExtratoResponse(SaldoResponse saldo, List<Transacao> ultimas_transacoes) {
		super();
		this.saldo = saldo;
		this.ultimas_transacoes = ultimas_transacoes;
	}

	public SaldoResponse getSaldo() {
		return saldo;
	}

	public List<Transacao> getUltimas_transacoes() {
		return ultimas_transacoes;
	}

	@Override
	public String toString() {
		return "ExtratoResponse{" +
				"saldo=" + saldo +
				", ultimas_transacoes=" + ultimas_transacoes +
				'}';
	}
}
