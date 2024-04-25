package com.weloveyolo.moniguard.api;

import android.util.Log;

import androidx.annotation.NonNull;
import androidx.annotation.WorkerThread;

import com.google.gson.Gson;
import com.weloveyolo.moniguard.util.HttpClient;

import java.io.IOException;
import java.util.Objects;

import lombok.Getter;
import lombok.Setter;
import okhttp3.Call;
import okhttp3.Callback;
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

    @Override
    public void getResident(ICallback<Resident> callback) {
//        Request request = new Request.Builder()
//                .url(getApiUrl() + "/GetResident")
//                .header("Authorization", "Bearer " + getAccessToken())
//                .build();
//        try (Response response = client.newCall(request).execute()) {
//            if (!response.isSuccessful()) {
//                throw new IOException("Failed to get resident: " + response);
//            }
//            Gson gson = new Gson();
//            Resident resident = gson.fromJson(Objects.requireNonNull(response.body()).string(), Resident.class);
//            callback.onCallback(resident, true);
//        } catch (IOException e) {
//            callback.onCallback(null, false);
//        }

        HttpClient.get(getApiUrl() + "/GetResident", null).enqueue(new Callback() {
            @Override
            public void onFailure(@NonNull Call call, @NonNull IOException e) {
                callback.onCallback(null, false);
            }
            @Override
            public void onResponse(@NonNull Call call, @NonNull Response response) throws IOException {
                if(response.isSuccessful()){
                    Gson gson = new Gson();
                    Resident resident = gson.fromJson(Objects.requireNonNull(response.body()).string(), Resident.class);
                    callback.onCallback(resident, true);
                }
                else {
                    Log.e("ResidentsApi", String.valueOf(response.request().header("Authorization")));
                    callback.onCallback(null, false);
                }
            }
        });
    }

    public void putResident(ICallback<Resident> callback){
        HttpClient.put(getApiUrl() + "/PutResident", null).enqueue(new Callback() {
            @Override
            public void onFailure(@NonNull Call call, @NonNull IOException e) {
                callback.onCallback(null, false);
            }
            @Override
            public void onResponse(@NonNull Call call, @NonNull Response response) throws IOException {
                if(response.isSuccessful()){
                    callback.onCallback(null, true);
                }
                else {
                    Log.e("ResidentsApi", String.valueOf(response.request().header("Authorization")));
                    callback.onCallback(null, false);
                }
            }
        });
    }


}
