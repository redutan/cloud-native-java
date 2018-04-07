package com.example.demo;

import com.fasterxml.jackson.databind.JsonNode;
import lombok.extern.slf4j.Slf4j;
import org.springframework.boot.CommandLineRunner;
import org.springframework.cloud.client.loadbalancer.LoadBalanced;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Component;
import org.springframework.web.client.RestTemplate;

import java.util.Collections;
import java.util.Map;

@Configuration
public class LoadBalancedRestTemplateConfiguration {
    @Bean
    @LoadBalanced
    RestTemplate restTemplate() {
        return new RestTemplate();
    }
}

@Component
@Slf4j
class LoadBalancedRestTemplateClr implements CommandLineRunner {
    private final RestTemplate restTemplate;

    LoadBalancedRestTemplateClr(RestTemplate restTemplate) {
        this.restTemplate = restTemplate;
    }

    @Override
    public void run(String... strings) throws Exception {
        Map<String, String> variables = Collections.singletonMap("name", "Cloud Natives!");
        ResponseEntity<JsonNode> response = this.restTemplate.getForEntity(
                "//greetings-service/hi/{name}", JsonNode.class, variables);
        JsonNode body = response.getBody();
        String greeting = body.get("greeting").asText();
        log.info("greeting: {}", greeting);

    }
}
