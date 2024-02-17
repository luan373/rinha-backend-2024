package br.com.rinha.data.dao;

import java.sql.*;
import java.text.MessageFormat;

import br.com.rinha.data.config.DBCPDataSource;
import br.com.rinha.data.model.TipoTransacao;
import br.com.rinha.data.model.Transacao;
import br.com.rinha.data.stored.ClienteStored;
import br.com.rinha.data.model.Saldo;
import br.com.rinha.rest.exceptions.SaldoException;
import br.com.rinha.rest.payload.TransacaoPayload;

public class SaldoDao {

	private static final String BUSCAR_POR_CLIENTE_ID = "SELECT q.id, q.cliente_id, q.valor, pg_advisory_xact_lock(q.id) FROM " +
			"( " +
			"  SELECT id, cliente_id, valor FROM saldos WHERE cliente_id = ? " +
			") q ;";

	private static final String BUSCAR_POR_CLIENTE_ID_CORRECT = "SELECT id, cliente_id, valor FROM saldos WHERE cliente_id = ? FOR UPDATE";

	private static final String ATUALIZAR_VALOR = "UPDATE saldos SET valor= ? WHERE cliente_id= ? ;";

	public Saldo buscarSaldoPorClienteId(int idCliente) throws SQLException {
		Connection conn = null;
		PreparedStatement stmt = null;
		ResultSet rset = null;

		var c = new Saldo();
		try {
			conn = DBCPDataSource.getConnection();
			conn.setAutoCommit(false);
			conn.beginRequest();

			stmt = conn.prepareStatement(BUSCAR_POR_CLIENTE_ID);
			stmt.setInt(1, idCliente);

			rset = stmt.executeQuery();
			while (rset.next()) {
				c.setId(rset.getInt(1));
				c.setCliente(ClienteStored.clienteStored(rset.getInt(2)));
				c.setValor(rset.getInt(3));
			}

			//stmt = conn.prepareStatement("SELECT pg_advisory_unlock(?);");
			//stmt.setInt(1, idCliente);

			//stmt.execute();
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
					conn.commit();
					conn.endRequest();
					conn.close();
				}
			} catch (Exception e) {
			}
		}



		return c;
	}

	public void atualizarSaldo(Saldo saldo) throws SQLException {
		Connection conn = null;
		PreparedStatement stmt = null;

		try {
			conn = DBCPDataSource.getConnection();
			conn.setAutoCommit(false);
			conn.setTransactionIsolation(Connection.TRANSACTION_READ_COMMITTED);
			conn.beginRequest();

			stmt = conn.prepareStatement(ATUALIZAR_VALOR);
			stmt.setInt(1, saldo.getValor());
			stmt.setInt(2, saldo.getCliente().getId());

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
				if (conn != null) {
					conn.commit();
					conn.endRequest();
					conn.close();
				}
			} catch (Exception e) {
			}
		}
	}

	public void atualizarSaldoFunction(TransacaoPayload tp, int clienteId, Transacao t) throws SQLException, SaldoException {
		Connection conn = null;
		PreparedStatement stmt = null;
		ResultSet rset = null;

		try {
			conn = DBCPDataSource.getConnection();
			conn.beginRequest();
			conn.setAutoCommit(false);
			conn.setTransactionIsolation(Connection.TRANSACTION_READ_COMMITTED);

			stmt = conn.prepareStatement(BUSCAR_POR_CLIENTE_ID_CORRECT);
			stmt.setInt(1, clienteId);

			var s = new Saldo();
			rset = stmt.executeQuery();
			while (rset.next()) {
				s.setId(rset.getInt(1));
				s.setCliente(ClienteStored.clienteStored(rset.getInt(2)));
				s.setValor(rset.getInt(3));
			}

			int valor = Integer.parseInt(tp.getValor());

			switch (TipoTransacao.valueOf(tp.getTipo())){
				case c: {
					s.setValor(creditaValor(s.getValor(), valor));
					break;
				}
				case d: {
					s.setValor(debitaValor(s.getValor(), valor, s.getCliente().getLimite()));
					break;
				}
				default:
					throw new IllegalArgumentException("Unexpected value: " + tp.getTipo());
			}

			stmt = conn.prepareStatement("select creditar(?, ?, ?, ?, ?)");
			stmt.setInt(1, s.getCliente().getId());
			stmt.setInt(2, t.getValor());
			stmt.setInt(3, s.getValor());
			stmt.setString(4, t.getTipo().name());
			stmt.setString(5, t.getDescricao());

			stmt.execute();
		} catch (SQLException e) {
			throw new SQLException(e);
		} catch (SaldoException e) {
			throw new SaldoException();
		}finally {
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
					conn.commit();
					conn.endRequest();
					conn.close();
				}
			} catch (Exception e) {
			}
		}
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
