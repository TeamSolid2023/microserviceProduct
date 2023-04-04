package com.gftraining.microservice_product.services;

import org.springframework.http.HttpMethod;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.web.reactive.function.client.WebClient;
import reactor.core.publisher.Mono;

public class CartWebClient {
    private WebClient webClient;

    public CartWebClient() {
        this.webClient = WebClient.create("http://localhost:8080");
    }

    public Mono<Void> deleteResource(Long id) {
        return this.webClient
                .method(HttpMethod.DELETE)
                .uri("/product/{id}", id)
                .accept(MediaType.APPLICATION_JSON)
                .retrieve()
                .onStatus(HttpStatus::isError, response -> Mono.error(new Exception("Error deleting product")))
                .bodyToMono(Void.class);
    }

}
