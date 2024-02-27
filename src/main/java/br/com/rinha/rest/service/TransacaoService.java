package br.com.rinha.rest.service;

import br.com.rinha.data.dao.SaldoDao;
import br.com.rinha.data.dao.TransacaoDao;
import br.com.rinha.data.model.Saldo;
import br.com.rinha.rest.exceptions.SaldoException;
import br.com.rinha.rest.payload.TransacaoPayload;
import br.com.rinha.rest.response.ExtratoResponse;
import br.com.rinha.rest.response.TransacaoResponse;

import java.sql.SQLException;
import java.util.concurrent.CompletableFuture;

public class TransacaoService {
	
	private final SaldoDao saldoDao = new SaldoDao();

	private final TransacaoDao transacaoDao = new TransacaoDao();

	public ExtratoResponse gerarExtrato(int clienteId) throws SQLException {
		return transacaoDao.buscarUltimas10Transacoes(clienteId);
	}

	public CompletableFuture<ExtratoResponse> gerarExtratatoAsyncCall(int clienteId) {
		CompletableFuture<ExtratoResponse> future = new CompletableFuture<>();

		CompletableFuture.runAsync(() -> {
			try {
				future.complete(gerarExtrato(clienteId));
			} catch (SQLException e) {
				future.completeExceptionally(e);
			}
		});

		return future;
	}

	public CompletableFuture<TransacaoResponse> insereTransacaoAsyncCall(TransacaoPayload transacaoPayload, int clienteId) {
		CompletableFuture<TransacaoResponse> future = new CompletableFuture<>();

		CompletableFuture.runAsync(() -> {
			try {
				future.complete(atualizarSaldo(transacaoPayload, clienteId));
			} catch (SQLException | SaldoException e) {
				future.completeExceptionally(e);
			}
		});

		return future;
	}

	private TransacaoResponse atualizarSaldo(TransacaoPayload transacaoPayload, int clienteId) throws SQLException, SaldoException {
		Saldo saldo = saldoDao.atualizarSaldoFunction(transacaoPayload, clienteId);

		return new TransacaoResponse(saldo.getCliente().getLimite(), saldo.getValor());
	}
	
}
