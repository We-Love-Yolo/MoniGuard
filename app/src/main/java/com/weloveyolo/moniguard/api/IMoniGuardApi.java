package com.weloveyolo.moniguard.api;

import okhttp3.OkHttpClient;

public interface IMoniGuardApi {
    String getAccessToken();

    String getBaseUrl();

    IAnalysisApi getAnalysisApi();

    IResidentsApi getResidentsApi();

    IScenesApi getScenesApi();

    OkHttpClient getHttpClient();

    void setAccessToken(String accessToken);
}
