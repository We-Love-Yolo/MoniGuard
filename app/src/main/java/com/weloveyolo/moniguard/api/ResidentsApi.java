package com.weloveyolo.moniguard.api;

import android.util.Log;

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
    private IMoniGuardApi mainApi;

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
                throw new IOException("Failed to get resident: " + response);
            }
            Gson gson = new Gson();
            Resident resident = gson.fromJson(Objects.requireNonNull(response.body()).string(), Resident.class);
            callback.onCallback(resident, true);
        } catch (IOException e) {
            callback.onCallback(null, false);
        }
    }
}
