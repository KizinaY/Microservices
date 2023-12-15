package com.kizina.resourceprocessor.service.exception;

import lombok.Data;
import lombok.EqualsAndHashCode;

@EqualsAndHashCode(callSuper = true)
@Data
public class CannotExtractMetadataException extends RuntimeException {
    public CannotExtractMetadataException(Exception e) {
        super(e);
    }
}
