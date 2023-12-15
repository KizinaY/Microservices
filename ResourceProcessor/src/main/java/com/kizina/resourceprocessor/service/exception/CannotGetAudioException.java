package com.kizina.resourceprocessor.service.exception;

import lombok.Data;
import lombok.EqualsAndHashCode;

@EqualsAndHashCode(callSuper = true)
@Data
public class CannotGetAudioException extends RuntimeException {

    private final String url;
    private final Long id;

    public CannotGetAudioException(String url, Long id) {
        super();
        this.url = url;
        this.id = id;
    }
}
