package com.weloveyolo.moniguard.util;

import com.google.gson.Gson;
import com.weloveyolo.moniguard.R;
import com.weloveyolo.moniguard.api.Resident;

import okhttp3.Call;
import okhttp3.Interceptor;
import okhttp3.MediaType;
import okhttp3.OkHttpClient;
import okhttp3.Request;
import okhttp3.RequestBody;

public class HttpClient {

    private static OkHttpClient client;

    public static OkHttpClient getClient(){
        if(client == null) client = new OkHttpClient();
        return client;
    }

    public static void setToken(String token) {
        client = createOkHttpClient(token);
    }

    private static OkHttpClient createOkHttpClient(String token) {
        OkHttpClient.Builder builder = new OkHttpClient.Builder();

        // 添加请求头拦截器
        Interceptor headerInterceptor = chain -> {
            Request originalRequest = chain.request();
            Request.Builder requestBuilder = originalRequest.newBuilder()
                    .header("Authorization", "Bearer " + token);
            return chain.proceed(requestBuilder.build());
        };
        builder.addInterceptor(headerInterceptor);

        return builder.build();
    }

    // Get请求
    public static Call get(String path, String query) {
        try {
            path = query != null ? path + "/" + query : path;
            return client.newCall(new Request.Builder().url(path).get().build());
        } catch (Exception ex) {
            return null;
        }
    }

    public static Call delete(String path, String query) {
        try {
            path = query != null ? path + "/" + query : path;
            return client.newCall(new Request.Builder().url(path).delete().build());
        } catch (Exception ex) {
            return null;
        }
    }

    public static <T> Call put(String path, T object) {
        Gson gson = new Gson();
        String json = gson.toJson(object);
        MediaType JSON = MediaType.parse("application/json; charset=utf-8");
        RequestBody body = RequestBody.create(JSON, json);
        Request request = new Request.Builder().url(path).put(body).build();
        return client.newCall(request);
    }

    public static Call post(String path) {
        try {
            // 创建一个请求体（非 JSON 格式）
            String requestBody = "param1=value1&param2=value2";
            RequestBody body = RequestBody.create(MediaType.parse("application/x-www-form-urlencoded"), requestBody);
            return client.newCall(new Request.Builder().url(String.valueOf(R.string.baseUrl) + path).post(body).build());
        } catch (Exception ex) {
            return null;
        }
    }
}
