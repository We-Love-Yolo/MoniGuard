package com.weloveyolo.moniguard.api;

public interface IResidentsApi {
    String getAccessToken();

    String getApiUrl();

    IMoniGuardApi getMainApi();

    void getResident(ICallback<Resident> callback);

    void getResident2(ICallback<Resident> callback);

    void putResident(Resident resident, ICallback<?> callback);

    void getAvatar(ICallback<String> callback);

    void getSettings(ICallback<Settings> callback);

    void putSettings(Settings settings, ICallback<?> callback);
}
