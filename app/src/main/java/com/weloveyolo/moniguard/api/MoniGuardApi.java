package com.weloveyolo.moniguard.api;

import lombok.Getter;
import lombok.Setter;
import okhttp3.OkHttpClient;

@Getter
public class MoniGuardApi implements IMoniGuardApi {
    private static final String BASE_URL = "https://mgapi.bitterorange.cn";

    @Setter
    private String accessToken;

    private final IResidentsApi residentsApi;

    private final IScenesApi scenesApi;

    private OkHttpClient httpClient;

    public MoniGuardApi() {
        httpClient = new OkHttpClient();
        this.residentsApi = new ResidentsApi(this);
        this.scenesApi = new ScenesApi(this);
    }

    @Override
    public String getBaseUrl() {
        return BASE_URL;
    }
}
