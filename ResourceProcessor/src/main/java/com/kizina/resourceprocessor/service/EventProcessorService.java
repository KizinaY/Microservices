package com.kizina.resourceprocessor.service;

import com.kizina.resourceprocessor.entity.Metadata;
import com.kizina.resourceprocessor.service.client.ResourceServiceClient;
import com.kizina.resourceprocessor.service.client.SongServiceClient;
import com.kizina.resourceprocessor.service.producer.RabbitMQProducer;
import lombok.RequiredArgsConstructor;
import org.springframework.amqp.rabbit.core.RabbitTemplate;
import org.springframework.stereotype.Service;

@Service
@RequiredArgsConstructor
public class EventProcessorService {

    private final ExtractMetadataService extractMetadataService;
    private final ResourceServiceClient resourceServiceClient;
    private final SongServiceClient songServiceClient;
    private final RabbitMQProducer rabbitMQProducer;

    public void processNewResourceEvent(Long resourceId) {
        byte[] audio = resourceServiceClient.getAudio(resourceId);
        Metadata metadata = extractMetadataService.extractMetadata(audio);
        metadata.setResourceId(resourceId);
        Long savedMetadataId = songServiceClient.sendMetadata(metadata);
        if(savedMetadataId != null){
            rabbitMQProducer.sendMessage(resourceId.toString());
        }
    }
}
