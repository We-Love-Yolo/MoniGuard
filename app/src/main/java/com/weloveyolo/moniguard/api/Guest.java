package com.weloveyolo.moniguard.api;

import java.io.Serializable;

import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.RequiredArgsConstructor;

@Getter
@AllArgsConstructor
@RequiredArgsConstructor
public class Guest implements Serializable {

    private final int guestId;

    private final int sceneId;

    private String name;

    private final String createdAt;

    private boolean isWhitelisted;

    private final String faceEncoding;

    public void setWhitelisted(boolean isWhitelisted) {
        this.isWhitelisted = isWhitelisted;
    }

    public void setName(String name) {
        this.name = name;
    }
}
