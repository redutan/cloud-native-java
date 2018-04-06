package com.example.demo;

import lombok.extern.slf4j.Slf4j;
import org.springframework.boot.CommandLineRunner;
import org.springframework.cloud.client.ServiceInstance;
import org.springframework.cloud.client.discovery.DiscoveryClient;
import org.springframework.stereotype.Component;

@Component
@Slf4j
public class DiscoveryClientClr implements CommandLineRunner {

    private final DiscoveryClient discoveryClient;

    public DiscoveryClientClr(DiscoveryClient discoveryClient) {
        this.discoveryClient = discoveryClient;
    }

    @Override
    public void run(String... strings) {
        final String serviceId = "greetings-service";
        discoveryClient.getInstances(serviceId).forEach(this::logServiceInstance);
    }

    private void logServiceInstance(ServiceInstance si) {
        log.info("host = {}, port = {}, service ID = {}",
                si.getHost(), si.getPort(), si.getServiceId());
    }
}
