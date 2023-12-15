package com.kizina.resourceprocessor.service.client;

import com.kizina.resourceprocessor.service.exception.NoSuchServiceException;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.cloud.client.ServiceInstance;
import org.springframework.cloud.client.loadbalancer.LoadBalancerClient;
import org.springframework.stereotype.Component;
import org.springframework.web.client.RestTemplate;

@Component
@RequiredArgsConstructor
@Slf4j
public abstract class ServiceClient {

    private final RestTemplate restTemplate;
    private final LoadBalancerClient loadBalancer;

    public RestTemplate getRestTemplate() {
        return restTemplate;
    }

    public String getServiceUrl(String serviceName) {
        ServiceInstance instance = loadBalancer.choose(serviceName);
        if (instance == null) {
            log.error("No instances of " + serviceName + " service found.");
            throw new NoSuchServiceException(serviceName);
        }
        return instance.getUri().toString();
    }
}
