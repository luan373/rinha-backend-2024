package br.com.rinha.data.dao;

import br.com.rinha.data.config.HirakiCPDataSource;
import br.com.rinha.data.model.Cliente;

import java.sql.Connection;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Statement;
import java.text.MessageFormat;

public class ClienteDao {

	private static final String BUSCAR_CLIENTE_POR_ID = "SELECT id, nome, limite FROM clientes WHERE id = {0};";

	public Cliente buscarClientePorId(int idCliente) throws SQLException {
		Connection conn = null;
        Statement stmt = null;
        ResultSet rset = null;
        
        var c = new Cliente();
		try {
			conn = HirakiCPDataSource.getConnection();
			stmt = conn.createStatement();
			rset = stmt.executeQuery(MessageFormat.format(BUSCAR_CLIENTE_POR_ID, idCliente));

			while (rset.next()) {
				c.setId(rset.getInt(1));
				c.setNome(rset.getString(2));
				c.setLimite(rset.getInt(3));
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
                if (conn != null)
                    conn.close();
            } catch (Exception e) {
            }
        }
		
		return c;
	}

}
