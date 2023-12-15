package com.kizina.resourceprocessor.entity;

import lombok.*;

@Data
@AllArgsConstructor
@NoArgsConstructor
@EqualsAndHashCode
@Builder
public class Metadata {

    private Long id;

    private String title;

    private String artist;

    private String album;

    private String duration;

    private String songYear;

    private Long resourceId;
}
