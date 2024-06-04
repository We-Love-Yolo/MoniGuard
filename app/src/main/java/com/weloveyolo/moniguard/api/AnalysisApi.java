package com.weloveyolo.moniguard.api;

import com.google.gson.Gson;
import com.google.gson.reflect.TypeToken;

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
    public void getMessage(ICallback<List<Message>> callback) {
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
}
