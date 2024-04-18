package com.weloveyolo.moniguard.activitys;

import android.annotation.SuppressLint;
import android.content.Intent;
import android.content.SharedPreferences;
import android.net.Uri;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.Toast;

import androidx.appcompat.app.AppCompatActivity;

import com.weloveyolo.moniguard.MainActivity;
import com.weloveyolo.moniguard.R;
import com.weloveyolo.moniguard.api.ICallback;
import com.weloveyolo.moniguard.api.IMoniGuardApi;
import com.weloveyolo.moniguard.api.IResidentsApi;
import com.weloveyolo.moniguard.api.MoniGuardApi;
import com.weloveyolo.moniguard.api.Resident;

import net.openid.appauth.AuthorizationException;
import net.openid.appauth.AuthorizationRequest;
import net.openid.appauth.AuthorizationResponse;
import net.openid.appauth.AuthorizationService;
import net.openid.appauth.AuthorizationServiceConfiguration;
import net.openid.appauth.ResponseTypeValues;
import net.openid.appauth.TokenResponse;

import java.util.Base64;
import java.util.Collections;
import java.util.HashMap;
import java.util.Map;
import java.util.Objects;

public class LoginActivity extends AppCompatActivity {
    private SharedPreferences sharedPreferences;

    private AuthorizationService authService;

    private static final String MY_CLIENT_ID = "6e7fcbc1-b51f-4111-ad44-2cf0baee8597";

    private static final Uri MY_REDIRECT_URI = Uri.parse("com.weloveyolo.moniguard://oauth/redirect");

    private static final int RC_AUTH = "com.weloveyolo.moniguard".hashCode() & 0xFFFF;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_login);

        authService = new AuthorizationService(this);

        sharedPreferences = getSharedPreferences("user", MODE_PRIVATE);
        if (sharedPreferences.getBoolean("isLogin", false)) {
            toHome();
        }
    }

    public void buttonLoginOnClick(View v) {
        AuthorizationServiceConfiguration serviceConfig = new AuthorizationServiceConfiguration(Uri.parse("https://login.microsoftonline.com/28fe96eb-e8dc-47a0-8b84-f7af6525ec71/oauth2/v2.0/authorize"), Uri.parse("https://login.microsoftonline.com/28fe96eb-e8dc-47a0-8b84-f7af6525ec71/oauth2/v2.0/token"));

        AuthorizationRequest.Builder authRequestBuilder = new AuthorizationRequest.Builder(serviceConfig, // the authorization service configuration
                MY_CLIENT_ID, // the client ID, typically pre-registered and static
                ResponseTypeValues.CODE, // the response_type value: we want a code
                MY_REDIRECT_URI); // the redirect URI to which the auth response is sent

        AuthorizationRequest authRequest = authRequestBuilder.setScope("api://6e7fcbc1-b51f-4111-ad44-2cf0baee8597/MoniGuard.Read")
//                .setLoginHint("jdoe@user.example.com")
                .build();

        doAuthorization(authRequest);

        Toast.makeText(this, "正在前往登录", Toast.LENGTH_SHORT).show();
    }

    private void doAuthorization(AuthorizationRequest authRequest) {
//        AuthorizationService authService = new AuthorizationService(this);
        Intent authIntent = authService.getAuthorizationRequestIntent(authRequest);
        startActivityForResult(authIntent, RC_AUTH);
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        if (requestCode == RC_AUTH) {
            AuthorizationResponse resp = AuthorizationResponse.fromIntent(data);
            AuthorizationException ex = AuthorizationException.fromIntent(data);
            if (ex != null) {
                Log.e("LoginActivity", "Authorization flow failed: " + ex.getMessage());
                return;
            }

            IMoniGuardApi moniGuardApi = new MoniGuardApi();
            authService.performTokenRequest(Objects.requireNonNull(resp).createTokenExchangeRequest(), (resp1, ex1) -> {
                if (resp1 != null) {
                    moniGuardApi.setAccessToken(Objects.requireNonNull(resp1).accessToken);
                    new Thread(() -> moniGuardApi.getResidentsApi().getResident((resident, success) -> {
                        if (success) {
                            runOnUiThread(() -> {
                                Toast.makeText(this, "登录成功", Toast.LENGTH_SHORT).show();
                                Log.i("LoginActivity", "Resident: " + resident);
                                setPersist(Objects.requireNonNull(resp1).accessToken);
                                toHome();
                            });
                        } else {
                            Log.e("LoginActivity", "Failed to get resident");
                        }
                    })).start();
                    new Thread(() -> moniGuardApi.getResidentsApi().getAvatar((avatar, success) -> {
                        if (success) {
                            runOnUiThread(() -> {
                                Log.i("LoginActivity", "成狗获取头像, 头像大小: " + avatar.length);
                                toHome();
                            });
                        } else {
                            Log.e("LoginActivity", "Failed to get avatar");
                        }
                    })).start();
                } else {
                    Log.e("LoginActivity", "Token exchange failed: " + Objects.requireNonNull(ex1).getMessage());
                }
            });
        }
    }


    private void toHome() {
        Intent intent = new Intent(LoginActivity.this, MainActivity.class);
        // 设置启动标志：跳转到新页面时，栈中的原有实例都被清空，同时开辟新任务的活动栈
        intent.setFlags(Intent.FLAG_ACTIVITY_CLEAR_TASK | Intent.FLAG_ACTIVITY_NEW_TASK);
        startActivity(intent);
    }

    @SuppressLint("ApplySharedPref")
//    private void setPersist(HashMap<String, String> hash) {
    private void setPersist(String token) {
        SharedPreferences.Editor editor = sharedPreferences.edit();
        editor.putString("token", token);
        editor.putBoolean("isLogin", true);
        editor.commit();
    }
}
