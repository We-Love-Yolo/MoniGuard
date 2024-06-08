package com.weloveyolo.moniguard.api;

import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.Locale;

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


      public Camera(String name, Date cur) {
            this.name = name;
            connectString = "connected";
            SimpleDateFormat dateFormat = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss.SSS", Locale.getDefault());
            String timeString = dateFormat.format(cur);
            createdAt = timeString.replace(" ", "T").concat("Z");
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
