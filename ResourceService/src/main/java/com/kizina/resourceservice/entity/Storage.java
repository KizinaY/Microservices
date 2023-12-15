package com.kizina.resourceservice.entity;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.EqualsAndHashCode;
import lombok.NoArgsConstructor;

@Data
@AllArgsConstructor
@NoArgsConstructor
@EqualsAndHashCode
public class Storage {

    private Long id;

    private String storageType;

    private String bucket;

    private String path;
}
