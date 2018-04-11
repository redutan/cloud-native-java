package com.example.demo;

import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.cloud.client.discovery.DiscoveryClient;
import org.springframework.cloud.client.discovery.event.HeartbeatEvent;
import org.springframework.cloud.netflix.zuul.RoutesRefreshedEvent;
import org.springframework.cloud.netflix.zuul.filters.RouteLocator;
import org.springframework.context.event.EventListener;
import org.springframework.stereotype.Component;

@Slf4j
@Component
public class RoutesListener {
    private final RouteLocator routeLocator;
    private final DiscoveryClient discoveryClient;

    @Autowired
    public RoutesListener(DiscoveryClient dc, RouteLocator rl) {
        this.routeLocator = rl;
        this.discoveryClient = dc;
    }

    @EventListener(HeartbeatEvent.class)
    public void onHeartbeatEvent(HeartbeatEvent event) {
        log.info("onHeartbeatEvent()");
        this.discoveryClient.getServices().stream().map(x -> " " + x)
                .forEach(log::info);
    }

    @EventListener(RoutesRefreshedEvent.class)
    public void onRoutesRefreshedEvent(RoutesRefreshedEvent event) {
        log.info("onRoutesRefreshedEvent()");
        this.routeLocator.getRoutes().stream().map(x -> " " + x)
                .forEach(log::info);
    }
}
