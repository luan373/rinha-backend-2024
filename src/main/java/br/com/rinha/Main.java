package br.com.rinha;

import br.com.rinha.data.config.HirakiCPDataSource;
import br.com.rinha.rest.payload.TransacaoPayload;
import br.com.rinha.rest.service.TransacaoService;
import io.javalin.Javalin;
import io.javalin.http.NotFoundResponse;

import java.sql.Connection;
import java.sql.SQLException;
import java.time.LocalDateTime;

import static io.javalin.apibuilder.ApiBuilder.get;

public class Main {

    public static void main(String[] args) {
        TransacaoService transacaoService = new TransacaoService();

        var app = Javalin.create(config -> {
                    config.useVirtualThreads = true;
                    config.showJavalinBanner = true;
                })
                .start(getPort());

        app.get("/clientes/{id}/extrato", ctx -> {
            var id = Integer.parseInt(ctx.pathParam("id"));
            if (id < 1 || id > 5) {
                throw new NotFoundResponse();
            }

            ctx.json(transacaoService.gerarExtrato(Integer.parseInt(ctx.pathParam("id"))));
        });

        app.post("/clientes/{id}/transacoes", ctx -> {
            try {
                var id = Integer.parseInt(ctx.pathParam("id"));
                if (id < 1 || id > 5) {
                    throw new NotFoundResponse();
                }

                var tp = ctx.bodyValidator(TransacaoPayload.class).get();

                if(tp.getDescricao().isBlank() || tp.getDescricao().length() > 10) {
                    throw new Exception();
                }

                if(tp.getTipo() == null || !(tp.getTipo().equals("c") || tp.getTipo().equals("d"))) {
                    throw new Exception();
                }

                try {
                    Integer.valueOf(tp.getValor());
                } catch (NumberFormatException e) {
                    throw new Exception();
                }

                ctx.json(transacaoService.insereTransacao(tp, Integer.parseInt(ctx.pathParam("id"))));
            }
            catch (NotFoundResponse e) {
                throw new NotFoundResponse();
            } catch (Exception e) {
                throw new Exception();
            }
        });

        app.exception(NotFoundResponse.class, (e, ctx) -> {
            ctx.status(404);
        });

        app.exception(Exception.class, (e, ctx) -> {
            System.err.println(e.getMessage());
            ctx.result(e.getMessage());
            ctx.status(422);
        });
    }

    private static int getPort() {
        try {
            return Integer.parseInt(System.getenv("RINHA_PORT"));
        }catch (NumberFormatException e) {
            return 9999;
        }
    }

}
