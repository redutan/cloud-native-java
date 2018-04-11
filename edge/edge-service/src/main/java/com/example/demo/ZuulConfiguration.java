package com.example.demo;

import lombok.extern.slf4j.Slf4j;
import org.springframework.boot.CommandLineRunner;
import org.springframework.cloud.netflix.zuul.EnableZuulProxy;
import org.springframework.cloud.netflix.zuul.filters.RouteLocator;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

@Configuration
@EnableZuulProxy
@Slf4j
public class ZuulConfiguration {
    @Bean
    CommandLineRunner commandLineRunner(RouteLocator routeLocator) {
        return args -> routeLocator.getRoutes().forEach(
                r -> log.info("{}, ({}), {}\n{}",
                        r.getId(), r.getLocation(), r.getFullPath(), r));
    }
}
