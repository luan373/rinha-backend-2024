package br.com.rinha.data.stored;

import br.com.rinha.data.model.Cliente;
import org.cache2k.Cache;
import org.cache2k.Cache2kBuilder;

import java.sql.SQLException;

public class ClienteStored {

    private static Cache<Integer, Cliente> cache;

    public static Cliente clienteStored(int idCliente) throws SQLException {
        if(cache == null){
            synchronized (ClienteStored.class) {
                if(cache == null){
                    loadCache();
                }
            }
        }

        return cache.get(idCliente);
    }

    private ClienteStored () {}

    private static void loadCache() {
        cache = new Cache2kBuilder<Integer, Cliente>() {}
                .name("clienteCache")
                .eternal(true)
                .entryCapacity(100)
                .build();

        cache.put(1, new Cliente(1, "o barato sai caro", 100000));
        cache.put(2, new Cliente(2, "zan corp ltda", 80000));
        cache.put(3, new Cliente(3, "les cruders", 1000000));
        cache.put(4, new Cliente(4, "padaria joia de cocaia", 10000000));
        cache.put(5, new Cliente(5, "kid mais", 500000));
    }

}
