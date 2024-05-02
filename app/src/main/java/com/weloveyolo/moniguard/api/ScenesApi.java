package com.weloveyolo.moniguard.api;

import android.util.Log;

import com.google.gson.Gson;
import com.google.gson.reflect.TypeToken;
import com.weloveyolo.moniguard.util.HttpClient;

import java.io.IOException;
import java.lang.reflect.Type;
import java.util.List;
import java.util.Objects;

import lombok.AllArgsConstructor;
import lombok.Getter;
import okhttp3.OkHttpClient;
import okhttp3.Request;
import okhttp3.Response;

/**
 * @noinspection unchecked
 */
@Getter
//@AllArgsConstructor
public class ScenesApi implements IScenesApi {
    private final IMoniGuardApi mainApi;

    public ScenesApi(IMoniGuardApi mainApi) {
        this.mainApi = mainApi;
    }

    @Override
    public String getAccessToken() {
        return mainApi.getAccessToken();
    }

    @Override
    public String getApiUrl() {
        return mainApi.getBaseUrl() + "/Scenes";
    }

    @Override
    public IMoniGuardApi getMainApi() {
        return mainApi;
    }

    //    @Override
//    public void getScenes(ICallback<Scene> callback) {
//        OkHttpClient client = new OkHttpClient();
//        Request request = new Request.Builder()
//                .url(getApiUrl() + "/GetScenes")
//                .header("Authorization", "Bearer " + getAccessToken())
//                .build();
//        try (Response response = client.newCall(request).execute()) {
//            if (!response.isSuccessful()) {
//                callback.onCallback(null, false);
//                return;
//            }
//            Gson gson = new Gson();
//            Scene scene = gson.fromJson(Objects.requireNonNull(response.body()).string(), Scene.class);
//            callback.onCallback(scene, true);
//        } catch (IOException e) {
//            callback.onCallback(null, false);
//        }
//    }
    @Override
    public void getScenes(ICallback<List<Scene>> callback) {
        Request request = new Request.Builder()
                .url(getApiUrl() + "/GetScenes")
                .header("Authorization", "Bearer " + getAccessToken())
                .build();
        try (Response response = mainApi.getHttpClient().newCall(request).execute()) {
            if (!response.isSuccessful()) {
                callback.onCallback(null, false);
                return;
            }
            Gson gson = new Gson();
            Type sceneListType = new TypeToken<List<Scene>>(){}.getType();
            List<Scene> sceneList = gson.fromJson(Objects.requireNonNull(response.body()).string(), sceneListType);
            callback.onCallback(sceneList, true);
        } catch (IOException e) {
            callback.onCallback(null, false);
        }
    }

    @Override
    public void getCameras(int sceneId, ICallback<List<Camera>> callback) {
        Request request = new Request.Builder()
                .url(getApiUrl() + "/GetCameras/" + sceneId)
                .header("Authorization", "Bearer " + getAccessToken())
                .build();
        try (Response response = mainApi.getHttpClient().newCall(request).execute()) {
            if (!response.isSuccessful()) {
                callback.onCallback(null, false);
                return;
            }
            Gson gson = new Gson();
            Type cameraListType = new TypeToken<List<Camera>>(){}.getType();
            List<Camera> cameras = gson.fromJson(Objects.requireNonNull(response.body()).string(), cameraListType);
            callback.onCallback(cameras, true);
        } catch (IOException e) {
            callback.onCallback(null, false);
        }
    }

    @Override
    public void postScene(String sceneName, ICallback<?> callback) {
        Gson gson = new Gson();
        String json = gson.toJson(sceneName);
        okhttp3.RequestBody body = okhttp3.RequestBody.create(json, okhttp3.MediaType.parse("application/json"));
        Request request = new Request.Builder()
                .url(getApiUrl() + "/PostScene/" + sceneName)
                .header("Authorization", "Bearer " + getAccessToken())
                .post(body)
                .build();
        try (Response response = mainApi.getHttpClient().newCall(request).execute()) {
            if (!response.isSuccessful()) {
                callback.onCallback(null, false);
                return;
            }
            callback.onCallback(null, true);
        } catch (IOException e) {
            callback.onCallback(null, false);
        }
    }

    @Override
    public void postCamera(int sceneId, Camera camera, ICallback<?> callback) {
//        Gson gson = new Gson();
//        String json = gson.toJson(camera);
//        okhttp3.RequestBody body = okhttp3.RequestBody.create(json, okhttp3.MediaType.parse("application/json"));
//        Request request = new Request.Builder()
//                .url(getApiUrl() + "/PostCamera/" + sceneId)
//                .header("Authorization", "Bearer " + getAccessToken())
//                .post(body)
//                .build();
//        try (Response response = mainApi.getHttpClient().newCall(request).execute()) {
//            if (!response.isSuccessful()) {
//                callback.onCallback(null, false);
//                return;
//            }
//            callback.onCallback(null, true);
//        } catch (IOException e) {
//            callback.onCallback(null, false);
//        }
        try (Response response = HttpClient.post(getApiUrl() + "/PostCamera", String.valueOf(sceneId), camera)) {
            if (!response.isSuccessful()) {
                throw new IOException("Failed to post camera: " + response);
            }
            callback.onCallback(null, true);
        } catch (IOException e) {
            callback.onCallback(null, false);
        }
    }

    @Override
    public void deleteScene(int sceneId, ICallback<?> callback) {
        Request request = new Request.Builder()
                .url(getApiUrl() + "/DeleteScene/" + sceneId)
                .header("Authorization", "Bearer " + getAccessToken())
                .delete()
                .build();
        try (Response response = mainApi.getHttpClient().newCall(request).execute()) {
            if (!response.isSuccessful()) {
                callback.onCallback(null, false);
                return;
            }
            callback.onCallback(null, true);
        } catch (IOException e) {
            callback.onCallback(null, false);
        }
    }

    @Override
    public void deleteCamera(int cameraId, ICallback<?> callback) {
        Request request = new Request.Builder()
                .url(getApiUrl() + "/DeleteCamera/" + cameraId)
                .header("Authorization", "Bearer " + getAccessToken())
                .delete()
                .build();
        try (Response response = mainApi.getHttpClient().newCall(request).execute()) {
            if (!response.isSuccessful()) {
                callback.onCallback(null, false);
                return;
            }
            callback.onCallback(null, true);
        } catch (IOException e) {
            callback.onCallback(null, false);
        }
    }
}
