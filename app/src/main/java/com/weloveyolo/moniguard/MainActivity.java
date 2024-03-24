package com.weloveyolo.moniguard;


import android.os.Bundle;
import android.os.Handler;
import android.view.LayoutInflater;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageButton;
import android.widget.ImageView;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.fragment.app.Fragment;
import androidx.fragment.app.FragmentTransaction;

import com.google.android.material.bottomnavigation.BottomNavigationView;
import com.weloveyolo.moniguard.ui.discover.DiscoverFragment;
import com.weloveyolo.moniguard.ui.home.HomeFragment;
import com.weloveyolo.moniguard.ui.message.MessageFragment;
import com.weloveyolo.moniguard.ui.my.MyFragment;
import com.weloveyolo.moniguard.utils.TheToast;


public class MainActivity extends AppCompatActivity {
    private HomeFragment homeFragment;  // 首页
    private DiscoverFragment discoverFragment;  // 发现
    private MessageFragment messageFragment;    // 消息
    private MyFragment myFragment;  // 我的
    private Fragment currentFragment; // 当前显示的Fragment

    @Override
    public void onBackPressed() {
        // 将任务移至后台而不是销毁Activity
        super.onBackPressed();
        moveTaskToBack(true);
    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
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
        getSupportFragmentManager().beginTransaction()
                .replace(R.id.fragment_container, homeFragment)
                .commit();
        currentFragment = homeFragment; // 设置当前显示的Fragment为homeFragment

        navView.setOnNavigationItemSelectedListener(item -> {
            // 创建一个新的FragmentTransaction实例
            FragmentTransaction transaction = getSupportFragmentManager().beginTransaction();

            // 如果当前有Fragment显示，先隐藏它
            if (currentFragment != null) {
                transaction.hide(currentFragment);
            }

            // 根据点击的导航项切换Fragment
            if(item.getItemId() == R.id.navigation_home) {
                if (!homeFragment.isAdded()) {
                    transaction.add(R.id.fragment_container, homeFragment);
                }
                transaction.show(homeFragment);
                currentFragment = homeFragment;
            }
            else if(item.getItemId() == R.id.navigation_discover) {
                if (!discoverFragment.isAdded()) {
                    transaction.add(R.id.fragment_container, discoverFragment);
                }
                transaction.show(discoverFragment);
                currentFragment = discoverFragment;
            }
            else if(item.getItemId() == R.id.navigation_message) {
                if (!messageFragment.isAdded()) {
                    transaction.add(R.id.fragment_container, messageFragment);
                }
                transaction.show(messageFragment);
                currentFragment = messageFragment;
            }
            else if(item.getItemId() == R.id.navigation_my) {
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
}
