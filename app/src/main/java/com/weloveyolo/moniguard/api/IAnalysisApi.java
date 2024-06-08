package com.weloveyolo.moniguard.api;

import java.util.List;

public interface IAnalysisApi {

    String getAccessToken();

    String getApiUrl();

    IMoniGuardApi getMainApi();

    void getMessage(ICallback<List<Message>> callback);

    void getFaces(ICallback<List<Face>> callback, int sceneId);

    void getFacesByGuestId(ICallback<List<Face>> callback, int guestId);
}
