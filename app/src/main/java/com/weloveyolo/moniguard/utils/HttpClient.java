package com.weloveyolo.moniguard.utils;

import com.weloveyolo.moniguard.R;

import okhttp3.Call;
import okhttp3.Interceptor;
import okhttp3.MediaType;
import okhttp3.OkHttpClient;
import okhttp3.Request;
import okhttp3.RequestBody;

public class HttpClient {

    private static OkHttpClient client;

    public static OkHttpClient getClient() {
        if (client == null) client = createOkHttpClient();
        return client;
    }

    private static OkHttpClient createOkHttpClient() {
        OkHttpClient.Builder builder = new OkHttpClient.Builder();

        // 添加请求头拦截器
        Interceptor headerInterceptor = chain -> {
            Request originalRequest = chain.request();
            Request.Builder requestBuilder = originalRequest.newBuilder()
                    .header("token", "");
            return chain.proceed(requestBuilder.build());
        };
        builder.addInterceptor(headerInterceptor);

        return builder.build();
    }

    public static Call get(String path) {
        try {
            return client.newCall(new Request.Builder().url("https://cube.meituan.com/ipromotion/cube/toc/component/base/getServerCurrentTime").get().build());
//            return client.newCall(new Request.Builder().url(String.valueOf(R.string.baseUrl) + path).get().build());
        } catch (Exception ex) {
            return null;
        }
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
