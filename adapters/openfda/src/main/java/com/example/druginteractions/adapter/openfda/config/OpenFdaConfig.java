package com.example.druginteractions.adapter.openfda.config;

import io.netty.channel.ChannelOption;
import io.netty.handler.timeout.ReadTimeoutHandler;
import io.netty.handler.timeout.WriteTimeoutHandler;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.http.client.reactive.ReactorClientHttpConnector;
import org.springframework.web.reactive.function.client.ClientRequest;
import org.springframework.web.reactive.function.client.ExchangeFilterFunction;
import org.springframework.web.reactive.function.client.WebClient;
import reactor.core.publisher.Mono;
import reactor.netty.http.client.HttpClient;

import java.net.URI;
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
        return ExchangeFilterFunction.ofResponseProcessor(clientResponse -> {
            String retryAfter = clientResponse.headers().asHttpHeaders().getFirst("Retry-After");
            if (retryAfter != null) {
                try {
                    Thread.sleep(Long.parseLong(retryAfter) * 1000);
                } catch (Exception ignored) {}
            }
            return Mono.just(clientResponse);
        });
    }

    private ExchangeFilterFunction apiKeyFilter() {
        return ExchangeFilterFunction.ofRequestProcessor(clientRequest -> {
            if (apiKey != null && !apiKey.isEmpty()) {
                String currentUrl = clientRequest.url().toString();
                String separator = currentUrl.contains("?") ? "&" : "?";
                String newUrl = currentUrl + separator + "api_key=" + apiKey;

                return Mono.just(ClientRequest.from(clientRequest)
                    .url(URI.create(newUrl))
                    .build());
            }
            return Mono.just(clientRequest);
        });
    }
}
