package com.example.demo;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.cloud.client.discovery.EnableDiscoveryClient;
import org.springframework.cloud.client.loadbalancer.LoadBalanced;
import org.springframework.cloud.netflix.feign.EnableFeignClients;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Profile;
import org.springframework.core.ParameterizedTypeReference;
import org.springframework.http.HttpMethod;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.web.client.RestTemplate;

import java.util.Map;

@EnableDiscoveryClient
@EnableFeignClients
@SpringBootApplication
public class EdgeServiceApplication {

    public static void main(String[] args) {
        SpringApplication.run(EdgeServiceApplication.class, args);
    }

    @LoadBalanced
    @Bean
    RestTemplate restTemplate() {
        return new RestTemplate();
    }
}

@Profile({"default", "insecure"})
@RestController
@RequestMapping("/api")
class RestTemplateGreetingsClientApiGateway {
    private final RestTemplate restTemplate;

    RestTemplateGreetingsClientApiGateway(@LoadBalanced RestTemplate restTemplate) {
        this.restTemplate = restTemplate;
    }

    @GetMapping("/resttemplate/{name}")
    public Map<String, String> restTemplate(@PathVariable String name) {
        ResponseEntity<Map<String, String>> responseEntity =
                restTemplate.exchange("http://greetings-service/greet/{name}", HttpMethod.GET, null,
                        new ParameterizedTypeReference<Map<String, String>>() {
                        },
                        name);
        return responseEntity.getBody();
    }
}