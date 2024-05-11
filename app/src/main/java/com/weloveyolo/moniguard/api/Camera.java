package com.weloveyolo.moniguard.api;

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
      private Boolean connectState;

//      public Camera(String name,Boolean connectState) {
//            this.name = name;
//            this.connectState=connectState;
//      }
//      public Camera()
//      {
//
//      }

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

      public Boolean isConnectState(){return connectState;}

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
