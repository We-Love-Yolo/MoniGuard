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
}
