package com.weloveyolo.moniguard.api;

import lombok.Getter;
import lombok.Setter;
import okhttp3.OkHttpClient;

@Getter
public class MoniGuardApi implements IMoniGuardApi {
    private static final String BASE_URL = "https://mgapi.bitterorange.cn";

    private String accessToken;

    private final IAnalysisApi analysisApi;

    private final IResidentsApi residentsApi;

    private final IScenesApi scenesApi;

    private final OkHttpClient httpClient;

    public MoniGuardApi() {
        httpClient = new OkHttpClient();
        this.analysisApi = new AnalysisApi(this);
        this.residentsApi = new ResidentsApi(this);
        this.scenesApi = new ScenesApi(this);
    }

    @Override
    public String getBaseUrl() {
        return BASE_URL;
    }

    @Override
    public synchronized void setAccessToken(String accessToken) {
        this.accessToken = accessToken;
    }
}
