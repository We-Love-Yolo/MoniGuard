package com.weloveyolo.moniguard.api;

import java.util.List;

public interface IAnalysisApi {

    String getAccessToken();

    String getApiUrl();

    IMoniGuardApi getMainApi();

    void getMessages(ICallback<List<Message>> callback);

    void getFaces(ICallback<List<Face>> callback, int sceneId);

    void getFacesByGuestId(ICallback<List<Face>> callback, int guestId);

    void getFaceImage(ICallback<String> callback, int faceId);

    void getPhotos(ICallback<List<Photo>> callback, int guestId);

    void getPhoto(ICallback<byte[]> callback, int photoId);

    void getFaceImageByGuest(ICallback<byte[]> callback, int guestId);

    void getByteArrayWithToken(ICallback<byte[]> callback, String path);
}
