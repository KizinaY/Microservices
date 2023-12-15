package com.kizina.resourceprocessor.service.client;

import com.kizina.resourceprocessor.entity.Metadata;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.cloud.client.loadbalancer.LoadBalancerClient;
import org.springframework.http.HttpEntity;
import org.springframework.http.HttpHeaders;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.retry.annotation.Backoff;
import org.springframework.retry.annotation.Retryable;
import org.springframework.stereotype.Component;
import org.springframework.web.client.RestTemplate;

@Component
@Slf4j
public class SongServiceClient extends ServiceClient {

    @Value("${SONG_SERVICE_PATH_SAVE}")
    private String PATH_FOR_SEND_METADATA;
    @Value("${SONG_SERVICE_NAME}")
    private String SONG_SERVICE_NAME;

    public SongServiceClient(RestTemplate restTemplate, LoadBalancerClient loadBalancer) {
        super(restTemplate, loadBalancer);
    }

    @Retryable(
            retryFor = {Exception.class},
            maxAttempts = Integer.MAX_VALUE,
            backoff = @Backoff(delay = 1000, maxDelay = 10000)
    )
    public Long sendMetadata(Metadata metadata) {
        Long metadataId = null;
        if (metadata != null) {
            HttpHeaders headers = new HttpHeaders();
            headers.setContentType(MediaType.APPLICATION_JSON);
            HttpEntity<Metadata> requestEntity = new HttpEntity<>(metadata, headers);
            String songServiceUrl = getServiceUrl(SONG_SERVICE_NAME);
            ResponseEntity<Long> responseEntity = getRestTemplate().postForEntity(songServiceUrl + PATH_FOR_SEND_METADATA, requestEntity, Long.class);
            metadataId = responseEntity.getBody();
        }
        log.info("Sent metadata to song service and got response with metadata Id " + metadataId);
        return metadataId;
    }
}
