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
                ctx.status(404);
                return;
                //throw new NotFoundResponse();
            }

            ctx.json(transacaoService.gerarExtrato(Integer.parseInt(ctx.pathParam("id"))));
        });

        app.post("/clientes/{id}/transacoes", ctx -> {
            try {
                var id = Integer.parseInt(ctx.pathParam("id"));
                if (id < 1 || id > 5) {
                    ctx.status(404);
                    return;
                    //throw new NotFoundResponse();
                }

                var tp = ctx.bodyAsClass(TransacaoPayload.class);

                if(tp.getDescricao().isBlank() || tp.getDescricao().length() > 10) {
                    ctx.status(422);
                    return;
                    //throw new Exception();
                }

                if(tp.getTipo() == null || !(tp.getTipo().equals("c") || tp.getTipo().equals("d"))) {
                    ctx.status(422);
                    return;
                    //throw new Exception();
                }

                try {
                    Integer.parseInt(tp.getValor());
                } catch (NumberFormatException e) {
                    ctx.status(422);
                    return;
                    //throw new Exception();
                }

                ctx.json(transacaoService.insereTransacao(tp, Integer.parseInt(ctx.pathParam("id"))));
            }
            catch (NotFoundResponse e) {
                ctx.status(404);
               // throw new NotFoundResponse();
            } catch (Exception e) {
                ctx.status(422);
                //throw new Exception();
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
