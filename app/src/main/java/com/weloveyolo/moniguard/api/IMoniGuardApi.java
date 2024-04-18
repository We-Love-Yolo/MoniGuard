package com.weloveyolo.moniguard.api;

public interface IMoniGuardApi {
    String getAccessToken();

    String getBaseUrl();

    IResidentsApi getResidentsApi();

    void setAccessToken(String accessToken);
}
