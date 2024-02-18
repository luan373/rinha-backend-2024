package br.com.rinha.data.dao;

import br.com.rinha.data.config.HirakiCPDataSource;
import br.com.rinha.data.model.TipoTransacao;
import br.com.rinha.data.model.Transacao;
import br.com.rinha.data.stored.ClienteStored;
import br.com.rinha.rest.response.ExtratoResponse;
import br.com.rinha.rest.response.SaldoResponse;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;

public class TransacaoDao {

	public static final String BUSCAR_ULTIMAS_10_TRANSACOES = "SELECT q.id, q.cliente_id, q.valor, q.tipo, q.descricao, " +
			"q.realizada_em, pg_advisory_xact_lock(q.id) FROM " +
			"( " +
			" SELECT t.id, t.cliente_id, t.valor, t.tipo, t.descricao, t.realizada_em " +
			" FROM transacoes t where t.cliente_id = ? " +
			" ORDER BY t.realizada_em DESC LIMIT 10 " +
			") q;";

	public static final String BUSCAR_ULTIMAS_10_TRANSACOES_CORRECT = "SELECT t.id, t.cliente_id, t.valor, " +
			"t.tipo, t.descricao, t.realizada_em FROM transacoes t " +
			"where t.cliente_id = ? ORDER BY t.realizada_em DESC LIMIT 10";

	private static final String BUSCAR_POR_CLIENTE_ID = "SELECT cliente_id, valor FROM saldos WHERE cliente_id = ?;";

	public static final String SALVAR_TRANSACAO = "INSERT INTO transacoes (cliente_id, valor, tipo, descricao, realizada_em) "
			+
			"VALUES(?, ?, ?, ?, now());";

	public ExtratoResponse buscarUltimas10Transacoes(int idCliente) throws SQLException {
		Connection conn = null;
		PreparedStatement stmt = null;
		ResultSet rset = null;

		SaldoResponse saldoResponse = null;

		List<Transacao> list = new ArrayList<>();
		try {
			conn = HirakiCPDataSource.getConnection();
			conn.setReadOnly(true);

			stmt = conn.prepareStatement(BUSCAR_POR_CLIENTE_ID);
			stmt.setInt(1, idCliente);

			rset = stmt.executeQuery();
			while (rset.next()) {
				saldoResponse = new SaldoResponse(rset.getInt(2),
						ClienteStored.clienteStored(
								rset.getInt(1)).getLimite());
			}



			stmt = conn.prepareStatement(BUSCAR_ULTIMAS_10_TRANSACOES_CORRECT);
			stmt.setInt(1, idCliente);

			rset = stmt.executeQuery();
			while (rset.next()) {
				var t = new Transacao();
				t.setId(rset.getInt(1));
				t.setCliente(ClienteStored.clienteStored(rset.getInt(2)));
				t.setValor(rset.getInt(3));
				t.setTipo(TipoTransacao.valueOf(rset.getString(4)));
				t.setDescricao(rset.getString(5));
				t.setRealizadaEm(rset.getObject(6, LocalDateTime.class));

				list.add(t);
			}



		} catch (SQLException e) {
			throw new SQLException(e); 
		} finally {
			try {
				if (rset != null)
					rset.close();
			} catch (Exception e) {
			}
			try {
				if (stmt != null)
					stmt.close();
			} catch (Exception e) {
			}
			try {
				if (conn != null) {
					conn.close();
				}
			} catch (Exception e) {
			}
		}

		return new ExtratoResponse(saldoResponse ,list);
	}

	public void salvarTransacao(Transacao transacao) throws SQLException {
		Connection conn = null;
		PreparedStatement stmt = null;

		try {
			conn = HirakiCPDataSource.getConnection();
			conn.setAutoCommit(false);
			conn.setTransactionIsolation(Connection.TRANSACTION_READ_COMMITTED);
			conn.beginRequest();

			stmt = conn.prepareStatement(SALVAR_TRANSACAO);
			stmt.setInt(1, transacao.getCliente().getId());
			stmt.setInt(2, transacao.getValor());
			stmt.setString(3, transacao.getTipo().name());
			stmt.setString(4, transacao.getDescricao());
			
			stmt.executeUpdate();
		} catch (SQLException e) {
			throw new SQLException(e); 
		} finally {
			try {
				if (stmt != null)
					stmt.close();
			} catch (Exception e) {
			}
			try {
				if (conn != null){
					conn.commit();
					conn.endRequest();
					conn.close();
				}
			} catch (Exception e) {
			}
		}
	}

}
