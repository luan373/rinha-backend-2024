package br.com.rinha.data.dao;

import br.com.rinha.data.config.HirakiCPDataSource;
import br.com.rinha.data.model.Saldo;
import br.com.rinha.data.model.TipoTransacao;
import br.com.rinha.data.model.Transacao;
import br.com.rinha.data.stored.ClienteStored;
import br.com.rinha.rest.exceptions.SaldoException;
import br.com.rinha.rest.payload.TransacaoPayload;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;

public class SaldoDao {

    private static final String BUSCAR_POR_CLIENTE_ID = "SELECT id, cliente_id, valor FROM saldos WHERE cliente_id = ?;";

    private static final String BUSCAR_POR_CLIENTE_ID_CORRECT = "SELECT id, cliente_id, valor FROM saldos WHERE cliente_id = ? FOR UPDATE";

    public Saldo atualizarSaldoFunction(TransacaoPayload tp, int clienteId) throws SQLException, SaldoException {
        Connection conn = null;
        PreparedStatement stmt = null;
        ResultSet rset = null;

        var s = new Saldo();
        try {
            conn = HirakiCPDataSource.getConnection();
            conn.setAutoCommit(false);
            conn.beginRequest();
            conn.setAutoCommit(false);
            conn.setTransactionIsolation(Connection.TRANSACTION_READ_COMMITTED);

            stmt = conn.prepareStatement(BUSCAR_POR_CLIENTE_ID_CORRECT);
            stmt.setInt(1, clienteId);

            rset = stmt.executeQuery();
            while (rset.next()) {
                s.setId(rset.getInt(1));
                s.setCliente(ClienteStored.clienteStored(rset.getInt(2)));
                s.setValor(rset.getInt(3));
            }

            int valor = Integer.parseInt(tp.getValor());

            switch (TipoTransacao.valueOf(tp.getTipo())) {
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
            stmt.setInt(2, valor);
            stmt.setInt(3, s.getValor());
            stmt.setString(4, tp.getTipo());
            stmt.setString(5, tp.getDescricao());

            stmt.execute();
        } catch (SQLException e) {
            throw new SQLException(e);
        } catch (SaldoException e) {
            throw new SaldoException();
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

        return s;
    }

    private int creditaValor(int valorSaldo, int valorTransacao) {
        return valorSaldo + valorTransacao;
    }

    private int debitaValor(int valorSaldo, int valorTransacao, int limite) throws SaldoException {
        int total = valorSaldo - valorTransacao;

        if ((-limite) > total) {
            throw new SaldoException();
        }

        return total;
    }

}
