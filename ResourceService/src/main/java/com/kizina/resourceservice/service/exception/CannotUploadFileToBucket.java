package com.kizina.resourceservice.service.exception;

import java.io.IOException;

public class CannotUploadFileToBucket extends RuntimeException {
    public CannotUploadFileToBucket(Exception e) {
        super(e);
    }

    public CannotUploadFileToBucket() {
        super();
    }
}
