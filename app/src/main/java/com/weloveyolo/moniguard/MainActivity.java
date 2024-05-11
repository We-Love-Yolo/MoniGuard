package com.weloveyolo.moniguard;


import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.net.Uri;
import android.os.Bundle;
import android.util.Log;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.fragment.app.Fragment;
import androidx.fragment.app.FragmentTransaction;
import androidx.work.ExistingPeriodicWorkPolicy;
import androidx.work.PeriodicWorkRequest;
import androidx.work.WorkManager;
import androidx.work.WorkRequest;
import androidx.work.Worker;
import androidx.work.WorkerParameters;

import com.google.android.material.bottomnavigation.BottomNavigationView;
import com.weloveyolo.moniguard.activity.LoginActivity;
import com.weloveyolo.moniguard.api.Camera;
import com.weloveyolo.moniguard.api.IMoniGuardApi;
import com.weloveyolo.moniguard.api.IResidentsApi;
import com.weloveyolo.moniguard.api.MoniGuardApi;
import com.weloveyolo.moniguard.api.Resident;
import com.weloveyolo.moniguard.api.Scene;
import com.weloveyolo.moniguard.ui.DiscoverFragment;
import com.weloveyolo.moniguard.ui.HomeFragment;
import com.weloveyolo.moniguard.ui.MessageFragment;
import com.weloveyolo.moniguard.ui.MyFragment;
import com.weloveyolo.moniguard.util.HttpClient;

import net.openid.appauth.AuthorizationService;
import net.openid.appauth.AuthorizationServiceConfiguration;
import net.openid.appauth.TokenRequest;

import java.util.List;
import java.util.concurrent.TimeUnit;

import javax.xml.transform.Result;

public class MainActivity extends AppCompatActivity {
    private SharedPreferences user;     // 持久化

    private HomeFragment homeFragment;  // 首页
    private DiscoverFragment discoverFragment;  // 发现
    private MessageFragment messageFragment;    // 消息
    private MyFragment myFragment;  // 我的
    private Fragment currentFragment; // 当前显示的Fragment

    private IMoniGuardApi moniGuardApi;
    public Resident resident = null;
    public List<Scene> scenes = null;
    public List<Camera> cameras = null;

    @Override
    public void onBackPressed() {
        // 将任务移至后台而不是销毁Activity
        super.onBackPressed();
        moveTaskToBack(true);
    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

     /*   if(!getSharedPreferences("user", MODE_PRIVATE).getBoolean("isLogin", false)){
            Intent intent = new Intent(this, LoginActivity.class);
            intent.setFlags(Intent.FLAG_ACTIVITY_CLEAR_TASK | Intent.FLAG_ACTIVITY_NEW_TASK);
            startActivity(intent);
            return;
        }*/

        user = getSharedPreferences("user", MODE_PRIVATE);
        refreshToken();

        // 在应用程序后台2小时定时执行
        PeriodicWorkRequest refreshWorkRequest =
                new PeriodicWorkRequest.Builder(TokenRefreshWorker.class, 2, TimeUnit.HOURS).build();
        String uniqueWorkName = "tokenRefreshWork";
        WorkManager.getInstance(this).enqueueUniquePeriodicWork(
                uniqueWorkName,
                ExistingPeriodicWorkPolicy.KEEP, // 如果已存在，则保持原有的工作
                refreshWorkRequest
        );

        // MainActivity方式
        moniGuardApi = new MoniGuardApi();
        moniGuardApi.setAccessToken(user.getString("accessToken", ""));
        // 其他方式
        HttpClient.setToken(user.getString("accessToken", ""));

        prepareData();

        setContentView(R.layout.activity_main);

        // 初始化Fragment实例
        homeFragment = new HomeFragment();
        discoverFragment = new DiscoverFragment();
        messageFragment = new MessageFragment();
        myFragment = new MyFragment();

        // 设置底部导航栏
        BottomNavigationView navView = findViewById(R.id.navigation);
        // 禁用默认颜色过滤器
        navView.setItemIconTintList(null);
        // fragment管理
        getSupportFragmentManager().beginTransaction().replace(R.id.fragment_container, homeFragment).commit();
        currentFragment = homeFragment; // 设置当前显示的Fragment为homeFragment

        navView.setOnNavigationItemSelectedListener(item -> {
            // 创建一个新的FragmentTransaction实例
            FragmentTransaction transaction = getSupportFragmentManager().beginTransaction();

            // 如果当前有Fragment显示，先隐藏它
            if (currentFragment != null) {
                transaction.hide(currentFragment);
            }

            // 根据点击的导航项切换Fragment
            if (item.getItemId() == R.id.navigation_home) {
                if (!homeFragment.isAdded()) {
                    transaction.add(R.id.fragment_container, homeFragment);
                }
                transaction.show(homeFragment);
                currentFragment = homeFragment;
            } else if (item.getItemId() == R.id.navigation_discover) {
                if (!discoverFragment.isAdded()) {
                    transaction.add(R.id.fragment_container, discoverFragment);
                }
                transaction.show(discoverFragment);
                currentFragment = discoverFragment;
            } else if (item.getItemId() == R.id.navigation_message) {
                if (!messageFragment.isAdded()) {
                    transaction.add(R.id.fragment_container, messageFragment);
                }
                transaction.show(messageFragment);
                currentFragment = messageFragment;
            } else if (item.getItemId() == R.id.navigation_my) {
                if (!myFragment.isAdded()) {
                    transaction.add(R.id.fragment_container, myFragment);
                }
                transaction.show(myFragment);
                currentFragment = myFragment;
            }

            // 提交事务
            transaction.commit();

            return true;
        });
    }

    private void prepareData(){
        // 首页
        new Thread(() -> moniGuardApi.getScenesApi().getScenes((scenes, success) -> {
            if(success) {
                this.scenes = scenes;
                homeFragment.tryShow();
                moniGuardApi.getScenesApi().getCameras(2, (cameras, success2) -> {
                    if(success2) {
                        this.cameras = cameras;
                        homeFragment.tryShow();
                    }
                });
            }
        })).start();

        // 我的
        new Thread(() -> moniGuardApi.getResidentsApi().getResident((resident, success) -> {
            if(success) {
                this.resident = resident;
                myFragment.tryShow();
                if(this.resident.getAvatar() == null){
                    moniGuardApi.getResidentsApi().getAvatar((avatar, success2) -> {
                        if(success2) {
                            resident.setAvatar(avatar);
                            myFragment.tryShow();
                        }
                    });
                }
            }
        })).start();
    }

    private void refreshToken(){
        if(user.getLong("expireTime", 0) - System.currentTimeMillis() > 1800000) return;

        final String MY_CLIENT_ID = "6e7fcbc1-b51f-4111-ad44-2cf0baee8597";

        AuthorizationService authService = new AuthorizationService(this);

        AuthorizationServiceConfiguration serviceConfig = new AuthorizationServiceConfiguration(
                Uri.parse("https://login.microsoftonline.com/28fe96eb-e8dc-47a0-8b84-f7af6525ec71/oauth2/v2.0/authorize"),
                Uri.parse("https://login.microsoftonline.com/28fe96eb-e8dc-47a0-8b84-f7af6525ec71/oauth2/v2.0/token")
        );

        TokenRequest tokenRequest = new TokenRequest.Builder(serviceConfig, MY_CLIENT_ID)
                .setGrantType("refresh_token")
                .setRefreshToken(user.getString("refreshToken", " "))
                .build();

        authService.performTokenRequest(tokenRequest, (res, ex) -> {
            if (res != null) {
                HttpClient.setToken(res.accessToken);
                SharedPreferences.Editor editor = user.edit();
                editor.putString("accessToken", res.accessToken);
                editor.putString("refreshToken", res.refreshToken);
                editor.putLong("expireTime", res.accessTokenExpirationTime);
                editor.commit();
            } else {
                if (ex != null) {
                    Log.e("TokenRefresh", "Refresh failed: " + ex.errorDescription);
                }
            }
        });
    }

    public class TokenRefreshWorker extends Worker {
        public TokenRefreshWorker(
                @NonNull Context context,
                @NonNull WorkerParameters params) {
            super(context, params);
        }
        @Override
        public Result doWork() {
            refreshToken();
            return Result.success();
        }
    }
}
