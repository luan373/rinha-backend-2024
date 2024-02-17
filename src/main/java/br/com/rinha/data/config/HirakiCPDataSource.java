package br.com.rinha.data.config;

import com.zaxxer.hikari.HikariConfig;
import com.zaxxer.hikari.HikariDataSource;

import javax.sql.DataSource;
import java.sql.Connection;
import java.sql.SQLException;

public class HirakiCPDataSource {

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
            synchronized (HirakiCPDataSource.class) {
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

    private HirakiCPDataSource(){}

    private static DataSource setupDataSource() {
        HikariConfig config = new HikariConfig();
        config.setJdbcUrl("jdbc:postgresql://db:5432/rinha");
        config.setUsername("postgres");
        config.setPassword("123456");
        config.setAutoCommit(true);
        config.setMinimumIdle(10);
        config.setMaximumPoolSize(25);
        config.setKeepaliveTime(60000);
        config.setIdleTimeout(120000);
        config.setLeakDetectionThreshold(150000);
        config.setMaxLifetime(180000);
        config.setConnectionTimeout(3000);
        config.setValidationTimeout(2500);
        config.setRegisterMbeans(true);
        config.addDataSourceProperty("cachePrepStmts", "true");
        config.addDataSourceProperty("prepStmtCacheSize", "250");
        config.addDataSourceProperty("prepStmtCacheSqlLimit", "2048");

        return new HikariDataSource(config);
    }

}
