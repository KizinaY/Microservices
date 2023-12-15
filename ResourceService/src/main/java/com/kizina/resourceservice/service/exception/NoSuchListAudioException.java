package com.kizina.resourceservice.service.exception;

import lombok.Data;
import lombok.EqualsAndHashCode;

import java.util.List;

@EqualsAndHashCode(callSuper = true)
@Data
public class NoSuchListAudioException extends RuntimeException {

    private final List<Long> audioIds;

    public NoSuchListAudioException(List<Long> audioIds) {
        super();
        this.audioIds = audioIds;
    }
}
