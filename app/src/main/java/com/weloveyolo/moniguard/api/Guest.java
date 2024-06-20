package com.weloveyolo.moniguard.api;

import java.io.Serializable;

import lombok.Getter;
import lombok.RequiredArgsConstructor;
import lombok.Setter;

@Getter
@Setter
@RequiredArgsConstructor
public class Guest implements Serializable {
    private final int guestId;

    private final int sceneId;

    private final String name;

    private final String createdAt;

   private final boolean isWhitelisted;

    private final String faceEncoding;

    public Guest setWhitelisted(boolean newWhitelistStatus) {
        // 创建一个新的 Guest 对象，更新 isWhitelisted 属性的值
        return new Guest(
                this.guestId,
                this.sceneId,
                this.name,
                this.createdAt,
                newWhitelistStatus, // 更新 isWhitelisted 属性
                this.faceEncoding
        );
    }
}
