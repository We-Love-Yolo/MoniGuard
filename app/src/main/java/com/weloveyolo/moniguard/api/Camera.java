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
      private String videoSource;


      public Camera(String name, Date cur) {
            this.name = name;
            videoSource = "connected";
            SimpleDateFormat dateFormat = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss.SSS", Locale.getDefault());
            String timeString = dateFormat.format(cur);
            createdAt = timeString.replace(" ", "T").concat("Z");
      }

      public Camera(String name, String description) {
            this.name = name;
            this.description = description;
      }
      public Camera(){}
      public Camera(int cameraId){
            this.cameraId = cameraId;
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
