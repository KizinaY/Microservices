package com.kizina.resourceprocessor.service.exception;

import lombok.Data;
import lombok.EqualsAndHashCode;

@EqualsAndHashCode(callSuper = true)
@Data
public class NoSuchServiceException extends RuntimeException {

    private String serviceName;

    public NoSuchServiceException(String serviceName) {
        super();
        this.serviceName = serviceName;
    }
}
