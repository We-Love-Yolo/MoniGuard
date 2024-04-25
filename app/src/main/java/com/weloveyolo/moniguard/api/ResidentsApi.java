package com.weloveyolo.moniguard.api;

import androidx.annotation.WorkerThread;

import com.google.gson.Gson;

import java.io.IOException;
import java.util.Objects;

import lombok.Getter;
import lombok.Setter;
import okhttp3.OkHttpClient;
import okhttp3.Request;
import okhttp3.Response;

@Getter
@Setter
public class ResidentsApi implements IResidentsApi {
    private final IMoniGuardApi mainApi;

    public ResidentsApi(IMoniGuardApi mainApi) {
        this.mainApi = mainApi;
    }

    @Override
    public String getAccessToken() {
        return mainApi.getAccessToken();
    }

    @Override
    public String getApiUrl() {
        return mainApi.getBaseUrl() + "/Residents";
    }

    @WorkerThread
    @Override
    public void getResident(ICallback<Resident> callback) {
        OkHttpClient client = new OkHttpClient();
        Request request = new Request.Builder()
                .url(getApiUrl() + "/GetResident")
                .header("Authorization", "Bearer " + getAccessToken())
                .build();
        try (Response response = client.newCall(request).execute()) {
            if (!response.isSuccessful()) {
                callback.onCallback(null, false);
                return;
            }
            Gson gson = new Gson();
            Resident resident = gson.fromJson(Objects.requireNonNull(response.body()).string(), Resident.class);
            callback.onCallback(resident, true);
        } catch (IOException e) {
            callback.onCallback(null, false);
        }
    }

    @Override
    public void putResident(Resident resident, ICallback<?> callback) {
        OkHttpClient client = new OkHttpClient();
        Gson gson = new Gson();
        String json = gson.toJson(resident);
        okhttp3.RequestBody body = okhttp3.RequestBody.create(json, okhttp3.MediaType.parse("application/json"));
        Request request = new Request.Builder()
                .url(getApiUrl() + "/PutResident")
                .header("Authorization", "Bearer " + getAccessToken())
                .put(body)
                .build();
        try (Response response = client.newCall(request).execute()) {
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
    public void getAvatar(ICallback<byte[]> callback) {
        OkHttpClient client = new OkHttpClient();
        Request request = new Request.Builder()
                .url(getApiUrl() + "/GetAvatar")
                .header("Authorization", "Bearer " + getAccessToken())
                .build();
        try (Response response = client.newCall(request).execute()) {
            if (!response.isSuccessful()) {
                callback.onCallback(null, false);
                return;
            }
            callback.onCallback(Objects.requireNonNull(response.body()).bytes(), true);
        } catch (IOException e) {
            callback.onCallback(null, false);
        }
    }
}
