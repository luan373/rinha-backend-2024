package br.com.rinha;

import br.com.rinha.rest.payload.TransacaoPayload;
import br.com.rinha.rest.service.TransacaoService;
import io.javalin.Javalin;
import io.javalin.http.NotFoundResponse;

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
            }

            ctx.future(() -> transacaoService.gerarExtratatoAsyncCall(Integer.parseInt(ctx.pathParam("id")))
                    .thenAccept(response -> ctx.json(response).status(200))
                    .exceptionally(throwable -> {
                        ctx.status(422);
                        return null;
                    }));
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
                }

                if(tp.getTipo() == null || !(tp.getTipo().equals("c") || tp.getTipo().equals("d"))) {
                    ctx.status(422);
                    return;
                }

                try {
                    Integer.parseInt(tp.getValor());
                } catch (NumberFormatException e) {
                    ctx.status(422);
                    return;
                }

                ctx.future(() -> transacaoService.insereTransacaoAsyncCall(tp, Integer.parseInt(ctx.pathParam("id")))
                        .thenAccept(response -> ctx.json(response).status(200))
                        .exceptionally(throwable -> {
                            ctx.status(422);
                            return null;
                        }));
            }
            catch (NotFoundResponse e) {
                ctx.status(404);
            } catch (Exception e) {
                ctx.status(422);
            }
        });

        app.exception(NotFoundResponse.class, (e, ctx) -> {
            ctx.status(404);
        });

        app.exception(Exception.class, (e, ctx) -> {
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
