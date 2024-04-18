package com.weloveyolo.moniguard.api;

public interface IResidentsApi {
    String getAccessToken();

    String getApiUrl();

    IMoniGuardApi getMainApi();

    void getResident(ICallback<Resident> callback);
}
