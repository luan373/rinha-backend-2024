package br.com.rinha.data.config;

import java.sql.Connection;
import java.sql.SQLException;

import javax.sql.DataSource;

import org.apache.commons.dbcp2.BasicDataSource;
import org.apache.commons.dbcp2.ConnectionFactory;
import org.apache.commons.dbcp2.DriverManagerConnectionFactory;
import org.apache.commons.dbcp2.PoolableConnection;
import org.apache.commons.dbcp2.PoolableConnectionFactory;
import org.apache.commons.dbcp2.PoolingDataSource;
import org.apache.commons.pool2.ObjectPool;
import org.apache.commons.pool2.impl.GenericObjectPool;

public class DBCPDataSource {

	private static DataSource dsp;

	static {
		try {
			Class.forName("org.postgresql.Driver");
		} catch (ClassNotFoundException e) {
			throw new RuntimeException(e);
		}
	}

	public static Connection getConnection() {
		if(dsp == null){
			synchronized (DBCPDataSource.class) {
				if(dsp == null){
					dsp = setupDataSource();
				}
			}
		}

        try {
            return dsp.getConnection();
        } catch (SQLException e) {
            throw new RuntimeException(e);
        }
    }

	private DBCPDataSource() {
	}

	private  static DataSource setupDataSource() {
		//  host.docker.internal
		ConnectionFactory connectionFactory = new DriverManagerConnectionFactory(
				"jdbc:postgresql://db:5432/rinha", "postgres", "123456");

		PoolableConnectionFactory poolableConnectionFactory = new PoolableConnectionFactory(connectionFactory, null);

		ObjectPool<PoolableConnection> connectionPool = new GenericObjectPool<>(poolableConnectionFactory);

		poolableConnectionFactory.setPool(connectionPool);

        return new PoolingDataSource<>(connectionPool);
	}

}