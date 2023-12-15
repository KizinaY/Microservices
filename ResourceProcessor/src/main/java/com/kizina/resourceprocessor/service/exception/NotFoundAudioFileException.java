package com.kizina.resourceprocessor.service.exception;

import lombok.Data;
import lombok.EqualsAndHashCode;

@EqualsAndHashCode(callSuper = true)
@Data
public class NotFoundAudioFileException extends RuntimeException {

    private final String url;
    private final Long id;

    public NotFoundAudioFileException(String url, Long id) {
        super();
        this.url = url;
        this.id = id;
    }
}
