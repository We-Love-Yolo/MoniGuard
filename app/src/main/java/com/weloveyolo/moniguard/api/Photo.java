package com.weloveyolo.moniguard.api;

import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
@AllArgsConstructor
public class Photo {

    private int photoId;
    private int cameraId;
    private String createdAt;
    private String name;
}
