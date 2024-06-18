package com.weloveyolo.moniguard.api;

import java.io.Serializable;

import lombok.Getter;
import lombok.RequiredArgsConstructor;

@Getter
@RequiredArgsConstructor
public class Guest implements Serializable {
    private final int guestId;

    private final int sceneId;

    private final String name;

    private final String createdAt;

    private final boolean isWhitelisted;

    private final String faceEncoding;
}
