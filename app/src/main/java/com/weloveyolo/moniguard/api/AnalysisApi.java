package com.weloveyolo.moniguard.api;

import com.google.gson.Gson;
import com.google.gson.reflect.TypeToken;
import com.weloveyolo.moniguard.util.HttpClient;

import java.io.IOException;
import java.lang.reflect.Type;
import java.util.List;
import java.util.Objects;

import lombok.Getter;
import okhttp3.Request;
import okhttp3.Response;

@Getter
public class AnalysisApi implements IAnalysisApi{
    private final IMoniGuardApi mainApi;

    public AnalysisApi(IMoniGuardApi mainApi) {
        this.mainApi = mainApi;
    }

    @Override
    public String getAccessToken() {
        return mainApi.getAccessToken();
    }

    @Override
    public String getApiUrl() {
        return mainApi.getBaseUrl() + "/Analysis";
    }

    @Override
    public IMoniGuardApi getMainApi() {
        return mainApi;
    }

    @Override
    public void getMessages(ICallback<List<Message>> callback) {
        Request request = new Request.Builder()
                .url(getApiUrl() + "/GetMessages")
                .header("Authorization", "Bearer " + getAccessToken())
                .build();
        try (Response response = mainApi.getHttpClient().newCall(request).execute()) {
            if (!response.isSuccessful()) {
                callback.onCallback(null, false);
                return;
            }
            Gson gson = new Gson();
            Type sceneListType = new TypeToken<List<Message>>(){}.getType();
            List<Message> messageList = gson.fromJson(Objects.requireNonNull(response.body()).string(), sceneListType);
            callback.onCallback(messageList, true);
        } catch (IOException e) {
            callback.onCallback(null, false);
        }
    }

    @Override
    public void getFaces(ICallback<List<Face>> callback, int sceneId) {
        try (Response response = HttpClient.get(getApiUrl() + "/GetFaces", String.valueOf(sceneId))) {
            if (!response.isSuccessful()) {
                throw new IOException("Failed to get faces: " + response);
            }
            Gson gson = new Gson();
            Type faceListType = new TypeToken<List<Face>>(){}.getType();
            List<Face> faceList = gson.fromJson(Objects.requireNonNull(response.body()).string(), faceListType);
            callback.onCallback(faceList, true);
        } catch (IOException e) {
            callback.onCallback(null, false);
        }
    }

    @Override
    public void getFacesByGuestId(ICallback<List<Face>> callback, int guestId) {
        try (Response response = HttpClient.get(getApiUrl() + "/GetFacesByGuestId", String.valueOf(guestId))) {
            if (!response.isSuccessful()) {
                throw new IOException("Failed to get faces: " + response);
            }
            Gson gson = new Gson();
            Type faceListType = new TypeToken<List<Face>>(){}.getType();
            List<Face> faceList = gson.fromJson(Objects.requireNonNull(response.body()).string(), faceListType);
            callback.onCallback(faceList, true);
        } catch (IOException e) {
            callback.onCallback(null, false);
        }
    }

    @Override
    public void getPhotos(ICallback<List<Photo>> callback, int guestId) {
        try (Response response = HttpClient.get(getApiUrl() + "/GetPhotos", String.valueOf(guestId))) {
            if (!response.isSuccessful()) {
                throw new IOException("Failed to get photos: " + response);
            }
            Gson gson = new Gson();
            Type photoListType = new TypeToken<List<Photo>>(){}.getType();
            List<Photo> photoList = gson.fromJson(Objects.requireNonNull(response.body()).string(), photoListType);
            callback.onCallback(photoList, true);
        } catch (IOException e) {
            callback.onCallback(null, false);
        }
    }

    @Override
    public void getPhoto(ICallback<Photo> callback, int photoId) {
        try (Response response = HttpClient.get(getApiUrl() + "/GetPhoto", String.valueOf(photoId))) {
            if (!response.isSuccessful()) {
                throw new IOException("Failed to get photo: " + response);
            }
            Gson gson = new Gson();
            Photo photo = gson.fromJson(Objects.requireNonNull(response.body()).string(), Photo.class);
            callback.onCallback(photo, true);
        } catch (IOException e) {
            callback.onCallback(null, false);
        }
    }
}
