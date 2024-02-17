package br.com.rinha.rest.service;

import java.sql.SQLException;
import java.time.LocalDateTime;
import java.util.List;

import br.com.rinha.data.dao.SaldoDao;
import br.com.rinha.data.dao.TransacaoDao;
import br.com.rinha.rest.exceptions.SaldoException;
import br.com.rinha.data.model.Saldo;
import br.com.rinha.data.model.TipoTransacao;
import br.com.rinha.data.model.Transacao;
import br.com.rinha.rest.payload.TransacaoPayload;
import br.com.rinha.rest.response.ExtratoResponse;
import br.com.rinha.rest.response.SaldoResponse;
import br.com.rinha.rest.response.TransacaoResponse;

public class TransacaoService {
	
	private final SaldoDao saldoDao = new SaldoDao();

	private final TransacaoDao transacaoDao = new TransacaoDao();

	public static int count = 1;
	
	public TransacaoResponse insereTransacao(TransacaoPayload transacaoPayload, int clienteId) throws SQLException, SaldoException {
		Saldo saldo = saldoDao.buscarSaldoPorClienteId(clienteId);

		int valor = Integer.parseInt(transacaoPayload.getValor());
		
		switch (TipoTransacao.valueOf(transacaoPayload.getTipo())){
			case c: {
				saldo.setValor(creditaValor(saldo.getValor(), valor));
				break;
			}
			case d: {
                    saldo.setValor(debitaValor(saldo.getValor(), valor, saldo.getCliente().getLimite()));
                break;
			}
			default:
				throw new IllegalArgumentException("Unexpected value: " + transacaoPayload.getTipo());
		}

		saldoDao.atualizarSaldoFunction(transacaoPayload, clienteId,
				new Transacao(saldo.getCliente(), valor,
						TipoTransacao.valueOf(transacaoPayload.getTipo()), transacaoPayload.getDescricao()));

		return new TransacaoResponse(saldo.getCliente().getLimite(), saldo.getValor());
	}
	
	public ExtratoResponse gerarExtrato(int clienteId) throws SQLException {
		Saldo saldo = saldoDao.buscarSaldoPorClienteId(clienteId);

		List<Transacao> list = transacaoDao.buscarUltimas10Transacoes(clienteId);
		
		return new ExtratoResponse(new SaldoResponse(saldo.getValor(), saldo.getCliente().getLimite()), 
				list);
	}
	
	private int creditaValor(int valorSaldo, int valorTransacao) {
		return valorSaldo + valorTransacao;
	}

	private int debitaValor(int valorSaldo, int valorTransacao, int limite) throws SaldoException {
		int total = valorSaldo - valorTransacao;
		
		if((-limite) > total) {
			throw new SaldoException();
		}
		
		return total;
	}
	
}
