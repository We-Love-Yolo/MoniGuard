package com.weloveyolo.moniguard.api;

import lombok.AllArgsConstructor;
import lombok.Getter;


@Getter
@AllArgsConstructor
public class Message {

    private int residentId;

    private String content;

    private String createdAt;

    private int type;

    private int cameraId;

    @Override
    public String toString() {
        return "Message{" +
                "residentId=" + residentId +
                ", content='" + content + '\'' +
                ", createdAt='" + createdAt + '\'' +
                ", type=" + type +
                ", cameraId=" + cameraId +
                '}';
    }
}
