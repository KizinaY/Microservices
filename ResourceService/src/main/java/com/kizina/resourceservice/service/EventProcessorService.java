package com.kizina.resourceservice.service;

import lombok.AllArgsConstructor;
import org.springframework.stereotype.Service;

@Service
@AllArgsConstructor
public class EventProcessorService {

    private static final String PERMANENT_STORAGE_TYPE = "PERMANENT";

    private final AudioService audioService;

    public void processResourceEvent(Long processedResourceId) {
        audioService.moveFileToNecessaryState(PERMANENT_STORAGE_TYPE, processedResourceId);
    }
}
