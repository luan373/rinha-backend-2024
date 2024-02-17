package br.com.rinha.data.stored;

import br.com.rinha.data.dao.ClienteDao;
import br.com.rinha.data.model.Cliente;

import java.sql.SQLException;
import java.util.HashMap;
import java.util.Map;

public class ClienteStored {

    private static final Map<Integer, Cliente> mapClient = new HashMap<>();

    public static Cliente clienteStored(int idCliente) throws SQLException {
        return getCliente(idCliente);
    }

    private ClienteStored () {}

    private static Cliente getCliente(int idCliente) throws SQLException {
        var c = mapClient.get(idCliente);

        if(c == null) {
            c = new ClienteDao().buscarClientePorId(idCliente);
            mapClient.put(idCliente, c);
        }

        return c;
    }

}
