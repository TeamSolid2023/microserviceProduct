package com.gftraining.microservice_product.services;

import org.springframework.http.HttpMethod;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.web.reactive.function.client.WebClient;
import org.springframework.web.reactive.function.client.WebClientException;
import reactor.core.publisher.Mono;

import java.net.ConnectException;

public class CartWebClient {
    private WebClient webClient;

    public CartWebClient() {
        this.webClient = WebClient.create("http://localhost:8080");
    }

    public Mono<Void> deleteResource(Long id) {
        return this.webClient
                .method(HttpMethod.DELETE)
                .uri("/products/{id}", id)
                .accept(MediaType.APPLICATION_JSON)
                .retrieve()
                .onStatus(HttpStatus::isError, response -> Mono.error(new Exception("Error deleting product from carts")))
                .bodyToMono(Void.class)
                .onErrorResume(error -> {
                    if (error instanceof WebClientException && error.getCause() instanceof ConnectException) {
                        // Handle connection error
                        return Mono.error(new Exception("Error deleting product from carts: Error connecting to cart service."));
                    }
                    return Mono.error(error);
                });
    }

}
