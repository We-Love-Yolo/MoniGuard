package com.weloveyolo.moniguard.activitys;


import android.content.Intent;
import android.content.SharedPreferences;
import android.os.AsyncTask;
import android.os.Bundle;
import android.view.View;

import androidx.appcompat.app.AppCompatActivity;

import com.weloveyolo.moniguard.MainActivity;
import com.weloveyolo.moniguard.R;
import com.weloveyolo.moniguard.utils.CustomToast;

import org.json.JSONException;
import org.json.JSONObject;

import java.io.IOException;
import java.util.HashMap;

import okhttp3.OkHttpClient;
import okhttp3.Request;
import okhttp3.Response;


public class LoginActivity extends AppCompatActivity implements View.OnClickListener{

    private SharedPreferences sharedPreferences;
    private CustomToast ct;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_login);

        sharedPreferences = getSharedPreferences("user", MODE_PRIVATE);
        if(sharedPreferences.getBoolean("isLogin", false)){
            toHome();
        }

        findViewById(R.id.imgButton_login).setOnClickListener(this);
        ct = new CustomToast(getApplicationContext());
    }

    // 点击登录
    public void onClick(View v) {
        if(!sharedPreferences.getBoolean("isLogin", false)){
            HashMap<String, String> hash = new HashMap();
            hash.put("phone", "16689576331");
            hash.put("token", "ASDFGHJ");
            setPersist(hash);

//            OkHttpClient client = new OkHttpClient();
//            Request request = new Request.Builder()
//                    .url("https://cube.meituan.com/ipromotion/cube/toc/component/base/getServerCurrentTime")
//                    .build();
//
//            try {
//                Response response = client.newCall(request).execute();
//                String responseData = response.body().string();
//                JSONObject jsonObject = new JSONObject(responseData);
//                String data = jsonObject.getString("data");
//            } catch (IOException e) {
//                throw new RuntimeException(e);
//            } catch (JSONException e) {
//                throw new RuntimeException(e);
//            }

                toHome();
        }
    }

    // 跳转主页
    private void toHome(){
        Intent intent = new Intent(LoginActivity.this, MainActivity.class);
        // 设置启动标志：跳转到新页面时，栈中的原有实例都被清空，同时开辟新任务的活动栈
        intent.setFlags(Intent.FLAG_ACTIVITY_CLEAR_TASK | Intent.FLAG_ACTIVITY_NEW_TASK);
        startActivity(intent);
    }

    // 跳转注册
    public void onLinkClicked(View view) {
        Intent intent = new Intent(this, SigninActivity.class);
        intent.setFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP);
        startActivity(intent);
    }

    // 跳转忘记密码
    public void onLinkClicked2(View view) {
//        Intent intent = new Intent(this, SigninActivity.class);
//        intent.setFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP);
//        startActivity(intent);
    }

    // 持久化
    private void setPersist(HashMap<String, String> hash){
        SharedPreferences.Editor editor = sharedPreferences.edit();
        editor.putString("phone", hash.get("phone"));
        editor.putString("token", hash.get("token"));
        editor.putBoolean("isLogin", true);
        editor.commit();
    }
}
