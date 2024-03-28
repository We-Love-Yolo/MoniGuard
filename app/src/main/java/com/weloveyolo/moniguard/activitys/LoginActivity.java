package com.weloveyolo.moniguard.activitys;


import static android.content.ContentValues.TAG;

import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Build;
import android.os.Bundle;
import android.util.Log;
import android.util.TypedValue;
import android.view.View;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;

import com.android.volley.BuildConfig;
import com.microsoft.identity.client.AuthenticationCallback;
import com.microsoft.identity.client.IAccount;
import com.microsoft.identity.client.IAuthenticationResult;
import com.microsoft.identity.client.IPublicClientApplication;
import com.microsoft.identity.client.ISingleAccountPublicClientApplication;
import com.microsoft.identity.client.PublicClientApplication;
import com.microsoft.identity.client.SignInParameters;
import com.microsoft.identity.client.exception.MsalClientException;
import com.microsoft.identity.client.exception.MsalException;
import com.microsoft.identity.client.exception.MsalServiceException;
import com.weloveyolo.moniguard.MainActivity;
import com.weloveyolo.moniguard.R;
import com.weloveyolo.moniguard.utils.CustomToast;
import com.weloveyolo.moniguard.utils.Identity;
import com.weloveyolo.moniguard.utils.MSGraphRequestWrapper;

import org.json.JSONObject;

import java.io.IOException;
import java.io.InputStream;
import java.net.MalformedURLException;
import java.net.URI;
import java.net.URL;
import java.util.Arrays;
import java.util.HashMap;
import java.util.Objects;

import okhttp3.Call;
import okhttp3.Callback;
import okhttp3.OkHttpClient;
import okhttp3.Request;
import okhttp3.Response;


public class LoginActivity extends AppCompatActivity implements View.OnClickListener {

    private SharedPreferences sharedPreferences;
    private CustomToast ct;

    final String defaultGraphResourceUrl = MSGraphRequestWrapper.MS_GRAPH_ROOT_ENDPOINT + "v1.0/me";

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_login);

        sharedPreferences = getSharedPreferences("user", MODE_PRIVATE);
        if (sharedPreferences.getBoolean("isLogin", false)) {
            toHome();
        }

        findViewById(R.id.imgButton_login).setOnClickListener(this);
        ct = new CustomToast(getApplicationContext());

        // Creates a PublicClientApplication object with res/raw/auth_config_single_account.json
        PublicClientApplication.createSingleAccountPublicClientApplication(getApplicationContext(), R.raw.auth_config_single_account, new IPublicClientApplication.ISingleAccountApplicationCreatedListener() {
            @Override
            public void onCreated(ISingleAccountPublicClientApplication application) {
                /*
                 * This test app assumes that the app is only going to support one account.
                 * This requires "account_mode" : "SINGLE" in the config json file.
                 */
                Identity.singleAccountApp = application;
                loadAccount();
            }

            @Override
            public void onError(MsalException exception) {
                PublicClientApplication.createSingleAccountPublicClientApplication(getApplicationContext(), R.raw.auth_config_single_account_debug, new IPublicClientApplication.ISingleAccountApplicationCreatedListener() {
                    @Override
                    public void onCreated(ISingleAccountPublicClientApplication application) {
                        /*
                         * This test app assumes that the app is only going to support one account.
                         * This requires "account_mode" : "SINGLE" in the config json file.
                         */
                        Identity.singleAccountApp = application;
                        loadAccount();
                    }

                    @Override
                    public void onError(MsalException exception) {
                        displayError(exception);
                    }
                });
//                displayError(exception);
            }
        });
    }

    /**
     * Load the currently signed-in account, if there's any.
     */
    private void loadAccount() {
        if (Identity.singleAccountApp == null) {
            return;
        }

        Identity.singleAccountApp.getCurrentAccountAsync(new ISingleAccountPublicClientApplication.CurrentAccountCallback() {
            @Override
            public void onAccountLoaded(@Nullable IAccount activeAccount) {
                // You can use the account data to update your UI or your app database.
                Identity.account = activeAccount;
//                updateUI();
            }

            @Override
            public void onAccountChanged(@Nullable IAccount priorAccount, @Nullable IAccount currentAccount) {
                if (currentAccount == null) {
                    // Perform a cleanup task as the signed-in account changed.
                    showToastOnSignOut();
                }
            }

            @Override
            public void onError(@NonNull MsalException exception) {
                displayError(exception);
            }
        });
    }

    /**
     * Updates UI when app sign out succeeds
     */
    private void showToastOnSignOut() {
        final String signOutText = "Signed Out.";
//        currentUserTextView.setText("");
        Toast.makeText(getApplicationContext(), signOutText, Toast.LENGTH_SHORT).show();
    }

    /**
     * Display the error message
     */
    private void displayError(@NonNull final Exception exception) {
//        logTextView.setText(exception.toString());
        Toast.makeText(getApplicationContext(), exception.toString(), Toast.LENGTH_SHORT).show();
    }

    // 点击登录
    public void onClick(View v) {
        if (Identity.singleAccountApp == null) {
            return;
        }

        final SignInParameters signInParameters = SignInParameters.builder().withActivity(this).withLoginHint(null).withScopes(Arrays.asList(getScopes())).withCallback(getAuthInteractiveCallback()).build();
        Identity.singleAccountApp.signInAgain(signInParameters);
        toHome();
    }

    /**
     * Extracts a scope array from a text field,
     * i.e. from "User.Read User.ReadWrite" to ["user.read", "user.readwrite"]
     */
    private String[] getScopes() {
        String clientId = Identity.singleAccountApp.getConfiguration().getClientId();
        String[] scopes = new String[]{"MoniGuard.Read"};
        for (int i = 0; i < scopes.length; i++) {
            scopes[i] = "api://" + clientId + "/" + scopes[i];
        }
        return scopes;
    }

    private AuthenticationCallback getAuthInteractiveCallback() {
        return new AuthenticationCallback() {

            @Override
            public void onSuccess(IAuthenticationResult authenticationResult) {
                /* Successfully got a token, use it to call a protected resource - MSGraph */
                Log.d(TAG, "Successfully authenticated");
                Log.d(TAG, "ID Token: " + authenticationResult.getAccount().getClaims().get("id_token"));

                /* Update account */
                Identity.account = authenticationResult.getAccount();
//                updateUI();
                Toast.makeText(getApplicationContext(), "Successfully authenticated", Toast.LENGTH_SHORT).show();

                // mgapi.bitterorange.cn/weatherforecast
                String url = "https://mgapi.bitterorange.cn/weatherforecast";
                Request request = new Request.Builder().url(url).build();
                OkHttpClient client = new OkHttpClient();

                // 发起异步请求
                client.newCall(request).enqueue(new Callback() {
                    @Override
                    public void onFailure(@NonNull Call call, @NonNull IOException e) {
                        // 请求失败处理
                        Toast.makeText(getApplicationContext(), "请求失败", Toast.LENGTH_SHORT).show();
                    }

                    @Override
                    public void onResponse(@NonNull Call call, @NonNull Response response) throws IOException {
                        // 请求成功处理
                        if (response.isSuccessful()) {
                            String responseData = Objects.requireNonNull(response.body()).string();
//                            get
//                            Toast.makeText(getApplicationContext(), responseData, Toast.LENGTH_SHORT).show();
                            // 在这里处理API返回的数据
                        } else {
                            // 请求失败处理
//                            Toast.makeText(getApplicationContext(), "请求失败", Toast.LENGTH_SHORT).show();
                        }
                    }
                });

                /* call graph */
//                callGraphAPI(authenticationResult);
            }

            @Override
            public void onError(MsalException exception) {
                /* Failed to acquireToken */
                Log.d(TAG, "Authentication failed: " + exception.toString());
                displayError(exception);

                if (exception instanceof MsalClientException) {
                    /* Exception inside MSAL, more info inside MsalError.java */
                } else if (exception instanceof MsalServiceException) {
                    /* Exception when communicating with the STS, likely config issue */
                }
            }

            @Override
            public void onCancel() {
                /* User canceled the authentication */
                Log.d(TAG, "User cancelled login.");
            }
        };
    }

    /**
     * Make an HTTP request to obtain MSGraph data
     */
    private void callGraphAPI(final IAuthenticationResult authenticationResult) {
        MSGraphRequestWrapper.callGraphAPIUsingVolley(getApplicationContext(), defaultGraphResourceUrl, authenticationResult.getAccessToken(), response -> {
            /* Successfully called graph, process data and send to UI */
            Log.d(TAG, "Response: " + response.toString());
            displayGraphResult(response);
        }, error -> {
            Log.d(TAG, "Error: " + error.toString());
            displayError(error);
        });
    }

    /**
     * Display the graph response
     */
    private void displayGraphResult(@NonNull final JSONObject graphResponse) {
        Toast.makeText(getApplicationContext(), graphResponse.toString(), Toast.LENGTH_SHORT).show();
//        logTextView.setText(graphResponse.toString());
    }

    // 跳转主页
    private void toHome() {
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
    private void setPersist(HashMap<String, String> hash) {
        SharedPreferences.Editor editor = sharedPreferences.edit();
        editor.putString("phone", hash.get("phone"));
        editor.putString("token", hash.get("token"));
        editor.putBoolean("isLogin", true);
        editor.commit();
    }
}
