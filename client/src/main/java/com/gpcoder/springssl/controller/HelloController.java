package com.gpcoder.springssl.controller;

import lombok.AllArgsConstructor;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.web.reactive.function.client.WebClient;
import reactor.core.publisher.Mono;

@AllArgsConstructor
@RestController
@RequestMapping("/v1")
public class HelloController {

    private static final String USER_API = "https://localhost:8443/server.api/v1/user";
    private final WebClient webClient;

    @GetMapping("/hello")
    public Mono<String> hello() {
        return webClient.get()
                .uri(USER_API)
                .retrieve()
                .bodyToMono(String.class); // Should return client1
    }
}
