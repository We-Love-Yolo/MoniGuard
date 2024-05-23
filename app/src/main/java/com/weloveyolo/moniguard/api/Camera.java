package com.weloveyolo.moniguard.api;

import java.time.LocalDateTime;

import lombok.AllArgsConstructor;
import lombok.Getter;

@AllArgsConstructor
@Getter
public class Camera {
      private int cameraId;
      private String name;
      private String createdAt;
      private String description;
      private int sceneId;
      private String connectString;

//      public Camera(String name,Boolean connectState) {
//            this.name = name;
//            this.connectState=connectState;
//      }
      public Camera(String name) {
            this.name = name;
            this.connectString = "114514";
            this.createdAt = "2024-05-23T12:21:57.685Z";
      }

      public int getCameraId() {
            return cameraId;
      }

      public String getName() {
            return name;
      }

      public String getDescription() {
            return description;
      }

      public int getSceneId() {
            return sceneId;
      }

      public String isConnectState(){return connectString;}

      @Override
      public String toString() {
            return "Camera{" +
                    "cameraId=" + cameraId +
                    ", name='" + name + '\'' +
                    ", createdAt='" + createdAt + '\'' +
                    ", description='" + description + '\'' +
                    ", sceneId=" + sceneId +
                    '}';
      }
}
