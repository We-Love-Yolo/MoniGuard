package com.weloveyolo.moniguard.api;

import androidx.annotation.NonNull;

import com.google.gson.Gson;
import com.weloveyolo.moniguard.util.HttpClient;

import java.io.IOException;
import java.util.Objects;

import lombok.Getter;
import lombok.Setter;
import okhttp3.Request;
import okhttp3.RequestBody;
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

    @Override
    public IMoniGuardApi getMainApi() {
        return mainApi;
    }

    @Override
    public void getResident(ICallback<Resident> callback) {
        Request request = new Request.Builder()
                .url(getApiUrl() + "/GetResident")
                .header("Authorization", "Bearer " + getAccessToken())
                .build();
        try (Response response = mainApi.getHttpClient().newCall(request).execute()) {
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

    @Override
    public void getAvatar(ICallback<byte[]> callback) {
        Request request = new Request.Builder()
                .url(getApiUrl() + "/GetAvatar")
                .header("Authorization", "Bearer " + getAccessToken())
                .build();
        try (Response response = mainApi.getHttpClient().newCall(request).execute()) {
            if (!response.isSuccessful()) {
                throw new IOException("Failed to get avatar: " + response);
            }
            callback.onCallback(response.body().bytes(), true);
        } catch (IOException e) {
            callback.onCallback(null, false);
        }
    }

    @Override
    public void putResident(Resident resident, ICallback<?> callback) {
        Gson gson = new Gson();
        String json = gson.toJson(resident);
        RequestBody body = RequestBody.create(json, okhttp3.MediaType.parse("application/json"));
        Request request = new Request.Builder()
                .url(getApiUrl() + "/PutResident")
                .header("Authorization", "Bearer " + getAccessToken())
                .put(body)
                .build();
        try (Response response = mainApi.getHttpClient().newCall(request).execute()) {
            if (!response.isSuccessful()) {
                throw new IOException("Failed to put resident: " + response);
            }
            callback.onCallback(null, true);
        } catch (IOException e) {
            callback.onCallback(null, false);
        }
    }

//    public void putResident(Resident resident, ICallback<?> callback) {
//        try (Response response = HttpClient.put(getApiUrl() + "/PutResident", resident)) {
//            if (!response.isSuccessful()) {
//                throw new IOException("Failed to put resident: " + response);
//            }
//            callback.onCallback(null, true);
//        } catch (IOException e) {
//            callback.onCallback(null, false);
//        }
//    }
}
