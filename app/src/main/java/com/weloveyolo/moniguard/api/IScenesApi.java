package com.weloveyolo.moniguard.api;

/*
GET
/Scenes/GetScenes
GET
/Scenes/GetCameras/{sceneId}
POST
/Scenes/PostScene/{sceneName}
POST
/Scenes/PostCamera/{sceneId}
DELETE
/Scenes/DeleteScene/{sceneId}
DELETE
/Scenes/DeleteCamera/{cameraId}
 */

import java.util.List;

public interface IScenesApi {
    String getAccessToken();

    String getApiUrl();

    IMoniGuardApi getMainApi();

//    void getScenes(ICallback<Scene> callback);
    void getScenes(ICallback<List<Scene>> callback);

    void getCameras(int sceneId, ICallback<List<Camera>> callback);

    void postScene(String sceneName, ICallback<?> callback);

    void postCamera(int sceneId, Camera camera, ICallback<?> callback);

    void deleteScene(int sceneId, ICallback<?> callback);

    void deleteCamera(int cameraId, ICallback<?> callback);

    void getGuest(int sceneId, ICallback<List<String>> callback);

    void getCameraConnectString(int key, String name, int sceneId, String description, ICallback<String> callback);
}
