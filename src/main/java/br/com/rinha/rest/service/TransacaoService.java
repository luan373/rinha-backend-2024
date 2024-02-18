package br.com.rinha.rest.service;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;

import br.com.rinha.data.config.HirakiCPDataSource;
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
		Saldo saldo = saldoDao.atualizarSaldoFunction(transacaoPayload, clienteId);

		return new TransacaoResponse(saldo.getCliente().getLimite(), saldo.getValor());
	}
	
	public ExtratoResponse gerarExtrato(int clienteId) throws SQLException {
		return transacaoDao.buscarUltimas10Transacoes(clienteId);
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
