package com.weloveyolo.moniguard.activitys;


import static android.content.ContentValues.TAG;

import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Build;
import android.os.Bundle;
import android.util.Log;
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

import java.util.Arrays;
import java.util.HashMap;


public class LoginActivity extends AppCompatActivity implements View.OnClickListener {

    private SharedPreferences sharedPreferences;
    private CustomToast ct;

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

        Toast.makeText(getApplicationContext(), BuildConfig.BUILD_TYPE, Toast.LENGTH_SHORT).show();
        // Creates a PublicClientApplication object with res/raw/auth_config_single_account.json
        int authConfigSingleAccountResource = (BuildConfig.DEBUG || Build.TYPE.equals("debug")) ? R.raw.auth_config_single_account_debug : R.raw.auth_config_single_account;
        PublicClientApplication.createSingleAccountPublicClientApplication(getApplicationContext(), authConfigSingleAccountResource, new IPublicClientApplication.ISingleAccountApplicationCreatedListener() {
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
        if (!sharedPreferences.getBoolean("isLogin", false)) {
            if (Identity.singleAccountApp == null) {
                return;
            }
            final SignInParameters signInParameters = SignInParameters.builder().withActivity(this).withLoginHint(null).withScopes(Arrays.asList("MoniGuard.Read")).withCallback(getAuthInteractiveCallback()).build();
            Identity.singleAccountApp.signIn(signInParameters);

//            HashMap<String, String> hash = new HashMap();
//            hash.put("phone", "16689576331");
//            hash.put("token", "ASDFGHJ");
//            setPersist(hash);

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

                /* call graph */
                callGraphAPI(authenticationResult);
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
        MSGraphRequestWrapper.callGraphAPIUsingVolley(getApplicationContext(), "https://graph.microsoft.com/v1.0/me", authenticationResult.getAccessToken(), response -> {
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
