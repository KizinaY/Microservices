package com.kizina.resourceservice.service.exception;

import lombok.Data;
import lombok.EqualsAndHashCode;

@EqualsAndHashCode(callSuper = true)
@Data
public class NoSuchAudioException extends RuntimeException {

    private final Long audioId;
    public NoSuchAudioException(Long id) {
        super();
        this.audioId = id;
    }
}
