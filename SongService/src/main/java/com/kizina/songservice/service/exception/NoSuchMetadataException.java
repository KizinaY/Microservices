package com.kizina.songservice.service.exception;

import lombok.Data;
import lombok.EqualsAndHashCode;

@EqualsAndHashCode(callSuper = true)
@Data
public class NoSuchMetadataException extends RuntimeException {

    private final Long metadataId;

    public NoSuchMetadataException(Long id) {
        super();
        this.metadataId = id;
    }
}
