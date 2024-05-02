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

      public Camera(String name) {
            this.name = name;
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
