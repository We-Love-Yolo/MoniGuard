package com.weloveyolo.moniguard.api;

import com.google.gson.Gson;

import java.io.IOException;
import java.util.Objects;

import lombok.AllArgsConstructor;
import lombok.Getter;
import okhttp3.OkHttpClient;
import okhttp3.Request;
import okhttp3.Response;

@Getter
@AllArgsConstructor
public class ScenesApi implements IScenesApi {
    private final IMoniGuardApi mainApi;

    @Override
    public String getAccessToken() {
        return mainApi.getAccessToken();
    }

    @Override
    public String getApiUrl() {
        return mainApi.getBaseUrl() + "/Residents";
    }

    @Override
    public void getScenes(ICallback<Scene> callback) {
        OkHttpClient client = new OkHttpClient();
        Request request = new Request.Builder()
                .url(getApiUrl() + "/GetScenes")
                .header("Authorization", "Bearer " + getAccessToken())
                .build();
        try (Response response = client.newCall(request).execute()) {
            if (!response.isSuccessful()) {
                callback.onCallback(null, false);
                return;
            }
            Gson gson = new Gson();
            Scene scene = gson.fromJson(Objects.requireNonNull(response.body()).string(), Scene.class);
            callback.onCallback(scene, true);
        } catch (IOException e) {
            callback.onCallback(null, false);
        }
    }

    @Override
    public void getCameras(int sceneId, ICallback<?> callback) {

    }

    @Override
    public void postScene(String sceneName, ICallback<?> callback) {

    }

    @Override
    public void postCamera(int sceneId, ICallback<?> callback) {

    }

    @Override
    public void deleteScene(int sceneId, ICallback<?> callback) {

    }

    @Override
    public void deleteCamera(int cameraId, ICallback<?> callback) {

    }
}
