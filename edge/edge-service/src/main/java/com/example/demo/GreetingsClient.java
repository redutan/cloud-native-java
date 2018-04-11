package com.example.demo;

import org.springframework.cloud.netflix.feign.FeignClient;
import org.springframework.stereotype.Component;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;

import java.util.Map;

@FeignClient(serviceId = "greetings-service")
interface GreetingsClient {

    @GetMapping("/greet/{name}")
    Map<String, String> greet(@PathVariable("name") String name);
}
