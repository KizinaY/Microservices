package com.kizina.resourceservice.service;

import com.kizina.resourceservice.entity.Storage;
import com.kizina.resourceservice.service.exception.NoSuchServiceException;
import io.github.resilience4j.circuitbreaker.annotation.CircuitBreaker;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.cloud.client.ServiceInstance;
import org.springframework.cloud.client.loadbalancer.LoadBalancerClient;
import org.springframework.core.ParameterizedTypeReference;
import org.springframework.http.HttpMethod;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Service;
import org.springframework.web.client.RestTemplate;

import java.util.Arrays;
import java.util.List;
import java.util.Optional;

@Service
@Slf4j
public class StorageService {

    @Value("${storage.service.name}")
    private String STORAGE_SERVICE_NAME;
    @Value("${storage.service.path.get}")
    private String PATH_FOR_GET_STORAGES;

    private final RestTemplate restTemplate;

    private final LoadBalancerClient loadBalancer;

    private final List<Storage> fallbackStorages = Arrays.asList(
            new Storage(null, "PERMANENT", "kizinabucket2", "/resources"),
            new Storage(null, "STAGING", "kizinabucket4", "/resources"));


    public StorageService(RestTemplate restTemplate, LoadBalancerClient loadBalancer) {
        this.restTemplate = restTemplate;
        this.loadBalancer = loadBalancer;
    }

    @CircuitBreaker(name = "storage", fallbackMethod = "fallbackGetStorageByType")
    public Storage getStorageByType(String type) {
        Optional<Storage> optionalStorage = getAllStoragesInfo().stream()
                .filter(storage -> type.equals(storage.getStorageType()))
                .findFirst();
        return optionalStorage.orElseThrow(() -> new RuntimeException("Cannot find Storage by type"));
    }

    public List<Storage> getAllStoragesInfo() {
        String url = getServiceUrl(STORAGE_SERVICE_NAME) + PATH_FOR_GET_STORAGES;
        ResponseEntity<List<Storage>> response =
                restTemplate.exchange(url, HttpMethod.GET, null, new ParameterizedTypeReference<>() {
                });
        log.info("Received response from StorageService GET storages method.");
        return response.getBody();
    }

    private String getServiceUrl(String serviceName) {
        ServiceInstance instance = loadBalancer.choose(serviceName);
        if (instance == null) {
            throw new NoSuchServiceException(serviceName);
        }
        return instance.getUri().toString();
    }

    public Storage fallbackGetStorageByType(String type, Throwable throwable) {
        log.info("Using fallbackGetAllStoragesInfo method.");
        return fallbackStorages.stream()
                .filter(storage -> type.equals(storage.getStorageType()))
                .findFirst().get();
    }
}
