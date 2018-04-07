package com.example.demo;

import com.netflix.loadbalancer.*;
import lombok.extern.slf4j.Slf4j;
import org.springframework.boot.CommandLineRunner;
import org.springframework.cloud.client.discovery.DiscoveryClient;
import org.springframework.stereotype.Component;

import java.net.URI;
import java.util.List;
import java.util.stream.IntStream;

import static java.util.stream.Collectors.toList;

@Slf4j
@Component
public class RibbonClientClr implements CommandLineRunner {
    private final DiscoveryClient discoveryClient;

    public RibbonClientClr(DiscoveryClient discoveryClient) {
        this.discoveryClient = discoveryClient;
    }

    @Override
    public void run(String... strings) throws Exception {
        final String serviceId = "greetings-service";
        List<Server> servers = discoveryClient.getInstances(serviceId).stream()
                .map(si -> new Server(si.getHost(), si.getPort()))
                .collect(toList());

        IRule roundRobinRule = new RoundRobinRule();

        BaseLoadBalancer loadBalancer = LoadBalancerBuilder.newBuilder()
                .withRule(roundRobinRule)
                .buildFixedServerListLoadBalancer(servers);

        IntStream.range(0, 10).forEach(i -> {
            Server server = loadBalancer.chooseServer();
            URI uri = URI.create("http://" + server.getHost() + ":" + server.getPort() + "/");
            log.info("resolved service {}", uri.toString());
        });
    }
}
