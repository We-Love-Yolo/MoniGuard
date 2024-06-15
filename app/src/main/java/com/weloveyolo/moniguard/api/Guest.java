package com.weloveyolo.moniguard.api;

import lombok.Getter;
import lombok.RequiredArgsConstructor;

@Getter
@RequiredArgsConstructor
public class Guest {
    private final int guestId;

    private final int sceneId;

    private final String name;

    private final String createdAt;

    private final boolean isAllowed;
}
