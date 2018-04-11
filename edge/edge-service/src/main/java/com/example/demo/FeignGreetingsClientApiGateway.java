package com.example.demo;

import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.util.Map;

//@Profile("feign")
@RestController
@RequestMapping("/api")
public class FeignGreetingsClientApiGateway {

    private final GreetingsClient greetingClient;

    public FeignGreetingsClientApiGateway(GreetingsClient greetingClient) {
        this.greetingClient = greetingClient;
    }

    @GetMapping("/feign/{name}")
    public Map<String, String> feign(@PathVariable String name) {
        return greetingClient.greet(name);
    }
}
