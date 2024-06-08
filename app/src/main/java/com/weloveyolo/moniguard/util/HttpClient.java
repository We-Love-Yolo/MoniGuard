package com.weloveyolo.moniguard.util;

import android.util.Log;

import com.google.gson.Gson;
import com.weloveyolo.moniguard.R;
import com.weloveyolo.moniguard.api.Resident;

import java.io.IOException;
import java.lang.reflect.Array;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.LinkedHashMap;
import java.util.Map;

import okhttp3.Call;
import okhttp3.Interceptor;
import okhttp3.MediaType;
import okhttp3.OkHttpClient;
import okhttp3.Request;
import okhttp3.RequestBody;
import okhttp3.Response;

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
    public static Response get(String path, String query) {
        try {
            path = query != null ? path + "/" + query : path;
            return client.newCall(new Request.Builder().url(path).get().build()).execute();
        } catch (Exception ex) {
            return null;
        }
    }

    public static Response queryGet(String path, Object ...objects){
        try {
            if (objects.length <= 0 || objects.length % 2 != 0) return null;
            ArrayList<String> queries = new ArrayList<>();
            for (int i = 0; i < objects.length; i+=2) {
                queries.add(objects[i].toString() + "=" + objects[i+1].toString());
            }
            String[] queryArr = queries.toArray(new String[queries.size()]);
            path = path + "?" + String.join("&", queryArr);
            return client.newCall(new Request.Builder().url(path).get().build()).execute();
        } catch (Exception ex) {
            return null;
        }
    }

    public static Response delete(String path, String query) {
        try {
            path = query != null ? path + "/" + query : path;
            return client.newCall(new Request.Builder().url(path).delete().build()).execute();
        } catch (Exception ex) {
            return null;
        }
    }

    public static <T> Response put(String path, T object) throws IOException {
        Gson gson = new Gson();
        String json = gson.toJson(object);
        MediaType JSON = MediaType.parse("application/json; charset=utf-8");
        RequestBody body = RequestBody.create(JSON, json);
        Request request = new Request.Builder().url(path).put(body).build();
        return client.newCall(request).execute();
    }

    public static <T> Response post(String path, String query, T object) throws IOException {
        path = query != null ? path + "/" + query : path;
        Gson gson = new Gson();
        String json;
        if(object == null) json = gson.toJson("");
        else json = gson.toJson(object);
        MediaType JSON = MediaType.parse("application/json; charset=utf-8");
        RequestBody body = RequestBody.create(JSON, json);
        Request request = new Request.Builder().url(path).post(body).build();
        return client.newCall(request).execute();
    }
}
