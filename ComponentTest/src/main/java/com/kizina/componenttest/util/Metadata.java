package com.kizina.componenttest.util;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.EqualsAndHashCode;
import lombok.NoArgsConstructor;

@Data
@AllArgsConstructor
@NoArgsConstructor
@EqualsAndHashCode
public class Metadata {

    private Long id;

    private String title;

    private String artist;

    private String album;

    private String duration;

    private String songYear;

    private Long resourceId;
}
