package com.gabrielbatista.freteapi.integracao;

import org.springframework.http.client.reactive.ReactorClientHttpConnector;
import org.springframework.stereotype.Component;
import org.springframework.web.reactive.function.client.WebClient;
import reactor.netty.http.client.HttpClient;
import io.netty.channel.ChannelOption;
import io.netty.handler.timeout.ReadTimeoutHandler;
import io.netty.handler.timeout.WriteTimeoutHandler;

import java.time.Duration;
import java.util.Map;

@Component
public class ViaCepClient {

    private final WebClient web;

    public ViaCepClient() {
        HttpClient http = HttpClient.create()
                .option(ChannelOption.CONNECT_TIMEOUT_MILLIS, 2000)
                .responseTimeout(Duration.ofSeconds(3))
                .doOnConnected(conn -> {
                    conn.addHandlerLast(new ReadTimeoutHandler(3));
                    conn.addHandlerLast(new WriteTimeoutHandler(3));
                });

        this.web = WebClient.builder()
                .baseUrl("https://viacep.com.br/ws")
                .clientConnector(new ReactorClientHttpConnector(http))
                .build();
    }

    /** Retorna a sigla da UF para o CEP informado. */
    public String obterUfPorCep(String cep) {
        String s = cep == null ? "" : cep.replaceAll("\\D", "");
        if (s.length() != 8) throw new IllegalArgumentException("CEP inválido: " + cep);

        Map<?, ?> resp = web.get()
                .uri("/{cep}/json/", s)
                .retrieve()
                .bodyToMono(Map.class)
                .block();

        if (resp == null || Boolean.TRUE.equals(resp.get("erro"))) {
            throw new IllegalArgumentException("CEP não encontrado: " + cep);
        }
        Object uf = resp.get("uf");
        if (uf == null) throw new ViaCepException("Resposta do ViaCEP sem UF para o CEP: " + cep);
        return uf.toString();
    }

    /** Erros externos (rede/serviço) do ViaCEP. */
    public static class ViaCepException extends RuntimeException {
        public ViaCepException(String msg) { super(msg); }
        public ViaCepException(String msg, Throwable cause) { super(msg, cause); }
    }
}
