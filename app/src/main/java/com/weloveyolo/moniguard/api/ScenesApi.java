package com.weloveyolo.moniguard.api;

import android.util.Log;

import com.google.gson.Gson;
import com.google.gson.reflect.TypeToken;
import com.weloveyolo.moniguard.util.HttpClient;

import java.io.IOException;
import java.lang.reflect.Type;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;
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

    @Override
    public void getScenes(ICallback<List<Scene>> callback) {
//        Request request = new Request.Builder()
//                .url(getApiUrl() + "/GetScenes")
//                .header("Authorization", "Bearer " + getAccessToken())
//                .build();
//        try (Response response = mainApi.getHttpClient().newCall(request).execute()) {
//            if (!response.isSuccessful()) {
//                callback.onCallback(null, false);
//                return;
//            }
//            Gson gson = new Gson();
//            Type sceneListType = new TypeToken<List<Scene>>(){}.getType();
//            List<Scene> sceneList = gson.fromJson(Objects.requireNonNull(response.body()).string(), sceneListType);
//            callback.onCallback(sceneList, true);
//        } catch (IOException e) {
//            callback.onCallback(null, false);
//        }
        try (Response response = HttpClient.get(getApiUrl() + "/GetScenes", null)) {
            if (!response.isSuccessful()) {
                throw new IOException("Failed to get scenes: " + response);
            }
            Gson gson = new Gson();
            Type sceneListType = new TypeToken<List<Scene>>(){}.getType();
            List<Scene> sceneList = gson.fromJson(Objects.requireNonNull(response.body()).string(), sceneListType);
            callback.onCallback(sceneList, true);
        } catch (IOException e) {
            callback.onCallback(null, false);
        }
        Log.e("getscenes", "getScenes: success");
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

    public void getGuests(int sceneId, ICallback<List<Guest>> callback) {
        Request request = new Request.Builder()
                .url(getApiUrl() + "/GetGuest/" + sceneId)
                .header("Authorization", "Bearer " + getAccessToken())
                .build();
        try (Response response = mainApi.getHttpClient().newCall(request).execute()) {
            if (!response.isSuccessful()) {
                callback.onCallback(null, false);
                return;
            }
            Gson gson = new Gson();
            Type guestListType = new TypeToken<List<Guest>>(){}.getType();
            List<Guest> guests = gson.fromJson(Objects.requireNonNull(response.body()).string(), guestListType);
            callback.onCallback(guests, true);
        } catch (IOException e) {
            callback.onCallback(null, false);
        }
    }

    @Override
    public void postScene(String sceneName, ICallback<?> callback) {
//        Gson gson = new Gson();
//        String json = gson.toJson(sceneName);
//        okhttp3.RequestBody body = okhttp3.RequestBody.create(json, okhttp3.MediaType.parse("application/json"));
//        Request request = new Request.Builder()
//                .url(getApiUrl() + "/PostScene/" + sceneName)
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
        try (Response response = HttpClient.post(getApiUrl() + "/PostScene", sceneName, null)) {
            if (!response.isSuccessful()) {
                throw new IOException("Failed to post scene: " + response);
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
            Log.w("SceneApi", sceneId+"");
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

    public void getGuest(int sceneId, ICallback<List<Guest>> callback) {
        try (Response response = HttpClient.get(getApiUrl() + "/GetGuest", String.valueOf(sceneId))) {
            if (!response.isSuccessful()) {
                throw new IOException("Failed to get guest: " + response);
            }
            Gson gson = new Gson();
            Type guestListType = new TypeToken<List<Guest>>(){}.getType();
            List<Guest> guestList = gson.fromJson(Objects.requireNonNull(response.body()).string(), guestListType);
            callback.onCallback(guestList, true);
        } catch (IOException e) {
            callback.onCallback(null, false);
        }
    }

    @Override
    public void putGuest(int guestId, Guest guest, ICallback<String> callback) {
        String path = getApiUrl() + "/PutGuest/" + guestId;
        try (Response response = HttpClient.put(path, guest)) {
            if (!response.isSuccessful()) {
                throw new IOException("Failed to put guest: " + response);
            }
            callback.onCallback(null, true);
        } catch (IOException e) {
            callback.onCallback(null, false);
        }
    }

    @Override
    public void getCameraConnectString(int key, String name, int sceneId, String description, ICallback<String> callback) {
        try (Response response = HttpClient.queryGet(getApiUrl() + "/GetCameraConnectString",
                "key", key, "name", name, "sceneId", sceneId, "description", description)) {
            if (!response.isSuccessful()) {
                throw new IOException("Failed to get CameraConnectString: " + response);
            }
            callback.onCallback(response.body().string(), true);
        } catch (IOException e) {
            callback.onCallback(null, false);
        }
    }

    @Override
    public void confirmCameraCreation(int sceneId, int pinCode, String name, String description, ICallback<String> callback) {
        Camera camera = new Camera(name, description);
        try (Response response = HttpClient.post(getApiUrl() + "/ConfirmCameraCreation/" + sceneId + "?" + "pinCode=" + pinCode,
                null, camera)) {
            if (!response.isSuccessful()) {
                throw new IOException("Failed to post ConfirmCameraCreation: " + response);
            }
            callback.onCallback(null, true);
        } catch (IOException e) {
            callback.onCallback(null, false);
        }
    }

    @Override
    public void getCamera(int cameraId, ICallback<Camera> callback) {
        try (Response response = HttpClient.get(getApiUrl() + "/GetCamera", String.valueOf(cameraId))) {
            if (!response.isSuccessful()) {
                throw new IOException("Failed to get camera: " + response);
            }
            Gson gson = new Gson();
            Camera camera = gson.fromJson(Objects.requireNonNull(response.body()).string(), Camera.class);
            callback.onCallback(camera, true);
        } catch (IOException e) {
            callback.onCallback(null, false);
        }
    }
}
