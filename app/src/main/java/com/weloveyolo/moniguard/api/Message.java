package com.weloveyolo.moniguard.api;

import java.io.Serializable;

import lombok.AllArgsConstructor;
import lombok.Getter;


@Getter
@AllArgsConstructor
public class Message implements Serializable {

    private String content;

    private int type;

    private int cameraId;

    private String createdAt;

    private int residentId;

    @Override
    public String toString() {
        return "Message{" +
                "content='" + content + '\'' +
                ", type=" + type +
                ", cameraId=" + cameraId +
                ", createdAt='" + createdAt + '\'' +
                ", residentId=" + residentId +
                '}';
    }
}
