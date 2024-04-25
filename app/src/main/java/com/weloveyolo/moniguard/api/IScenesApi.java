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

public interface IScenesApi {
    String getAccessToken();

    String getApiUrl();

    IMoniGuardApi getMainApi();

    void getScenes(ICallback<?> callback);

    void getCameras(int sceneId, ICallback<?> callback);

    void postScene(String sceneName, ICallback<?> callback);

    void postCamera(int sceneId, ICallback<?> callback);
}
