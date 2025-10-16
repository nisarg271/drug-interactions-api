package com.example.druginteractions.adapter.openfda.config;

import io.netty.channel.ChannelOption;
import io.netty.handler.timeout.ReadTimeoutHandler;
import io.netty.handler.timeout.WriteTimeoutHandler;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.http.client.reactive.ReactorClientHttpConnector;
import org.springframework.web.reactive.function.client.ExchangeFilterFunction;
import org.springframework.web.reactive.function.client.WebClient;
import reactor.core.publisher.Mono;
import reactor.netty.http.client.HttpClient;

import java.time.Duration;
import java.util.concurrent.TimeUnit;

@Configuration
public class OpenFdaConfig {

    @Value("${openfda.api.key:}")
    private String apiKey;

    @Value("${openfda.api.baseUrl:https://api.fda.gov}")
    private String baseUrl;

    @Value("${openfda.api.timeout:10}")
    private int timeoutSeconds;

    @Bean
    public WebClient openFdaWebClient() {
        HttpClient httpClient = HttpClient.create()
            .option(ChannelOption.CONNECT_TIMEOUT_MILLIS, 5000)
            .responseTimeout(Duration.ofSeconds(timeoutSeconds))
            .doOnConnected(conn -> conn
                .addHandlerLast(new ReadTimeoutHandler(timeoutSeconds, TimeUnit.SECONDS))
                .addHandlerLast(new WriteTimeoutHandler(timeoutSeconds, TimeUnit.SECONDS)));

        return WebClient.builder()
            .baseUrl(baseUrl)
            .clientConnector(new ReactorClientHttpConnector(httpClient))
            .filter(retryFilter())
            .filter(apiKeyFilter())
            .build();
    }

    private ExchangeFilterFunction retryFilter() {
        return ExchangeFilterFunction.ofRequestProcessor(clientRequest ->
            Mono.just(clientRequest)
                .doOnNext(request -> {
                    if (request.headers().containsKey("Retry-After")) {
                        try {
                            Thread.sleep(Long.parseLong(request.headers().getFirst("Retry-After")) * 1000);
                        } catch (Exception ignored) {}
                    }
                })
        );
    }

    private ExchangeFilterFunction apiKeyFilter() {
        return ExchangeFilterFunction.ofRequestProcessor(clientRequest -> {
            if (apiKey != null && !apiKey.isEmpty()) {
                return Mono.just(clientRequest.mutate()
                    .url(clientRequest.url() + (clientRequest.url().contains("?") ? "&" : "?") + "api_key=" + apiKey)
                    .build());
            }
            return Mono.just(clientRequest);
        });
    }
}
