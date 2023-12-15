package com.kizina.resourceprocessor.service.client;

import com.kizina.resourceprocessor.service.exception.CannotGetAudioException;
import com.kizina.resourceprocessor.service.exception.NotFoundAudioFileException;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.cloud.client.loadbalancer.LoadBalancerClient;
import org.springframework.http.HttpStatusCode;
import org.springframework.http.ResponseEntity;
import org.springframework.retry.annotation.Backoff;
import org.springframework.retry.annotation.Retryable;
import org.springframework.stereotype.Component;
import org.springframework.web.client.RestTemplate;

import static org.springframework.http.HttpStatus.NOT_FOUND;

@Component
@Slf4j
public class ResourceServiceClient extends ServiceClient {

    @Value("${RESOURCE_SERVICE_PATH_GET}")
    private String PATH_FOR_GET_AUDIO;
    @Value("${RESOURCE_SERVICE_NAME}")
    private String RESOURCE_SERVICE_NAME;

    public ResourceServiceClient(RestTemplate restTemplate, LoadBalancerClient loadBalancer) {
        super(restTemplate, loadBalancer);
    }

    @Retryable(
            retryFor = {Exception.class},
            noRetryFor = NotFoundAudioFileException.class,
            maxAttempts = Integer.MAX_VALUE,
            backoff = @Backoff(delay = 1000, maxDelay = 10000)
    )
    public byte[] getAudio(Long id) {
        String url = getServiceUrl(RESOURCE_SERVICE_NAME) + PATH_FOR_GET_AUDIO;
        ResponseEntity<byte[]> response = getRestTemplate().getForEntity(url, byte[].class, id);
        HttpStatusCode statusCode = response.getStatusCode();
        if(statusCode.isSameCodeAs(NOT_FOUND)){
            log.error("Request to url " + url + " , resourceId = " + id + " has response with not 404 status code (Not found resource).");
            throw new NotFoundAudioFileException(url, id);
        } else if (!statusCode.is2xxSuccessful()) {
            log.error("Request to url " + url + " , resourceId = " + id + " has response with not 2xx status code.");
            throw new CannotGetAudioException(url, id);
        }
        log.info("Got audio from url: " + url + " , resourceId: " + id);
        return response.getBody();
    }
}
