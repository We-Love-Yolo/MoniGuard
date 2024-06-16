package com.weloveyolo.moniguard.ui;

import android.os.Bundle;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.content.Intent;
import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;

import com.weloveyolo.moniguard.MainActivity;
import com.weloveyolo.moniguard.R;
import com.weloveyolo.moniguard.activity.AlbumActivity;
import com.weloveyolo.moniguard.activity.AlbumDetailActivity;
import com.weloveyolo.moniguard.activity.Health_monitoring;
import com.weloveyolo.moniguard.activity.Sports;
import com.weloveyolo.moniguard.api.Guest;
import com.weloveyolo.moniguard.api.IScenesApi;
import com.weloveyolo.moniguard.api.ScenesApi;

import android.widget.ImageButton;

public class DiscoverFragment extends Fragment {


    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        return inflater.inflate(R.layout.fragment_discover, container, false);
    }

    @Override
    public void onViewCreated(@NonNull View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);



        // 进入智能相册
        ImageButton button1 = view.findViewById(R.id.smart_photo_album);
        button1.setOnClickListener(view1 -> {
            Intent intent = new Intent(getActivity(), AlbumActivity.class);
            startActivity(intent);
        });
        //进入健康检测
        ImageButton button2 = view.findViewById(R.id.health_monitoring);
        button2.setOnClickListener(view1 -> {
            Intent intent = new Intent(getActivity(), Health_monitoring.class);
            startActivity(intent);
        });

        //进入趣味运动
        ImageButton button3 = view.findViewById(R.id.happy_sport);
        button3.setOnClickListener(view1 -> {
            Bundle bundle = new Bundle();
            Guest guest = new Guest(1,1,"凤凰战士",null,false, null);
            bundle.putSerializable("guest", guest);
            Intent intent = new Intent(getActivity(), AlbumDetailActivity.class);
            intent.putExtras(bundle);
            intent.putExtra("photo", "https://pic1.zhimg.com/70/v2-74a44ef0c3e5a22a7965d8a5b25d029a_1440w.image?source=172ae18b&biz_tag=Post");
            startActivity(intent);
        });
    }

    public void tryShow() {
        MainActivity mainActivity = ((MainActivity) getActivity());
        if (mainActivity == null) {
            return;
        }
        if (mainActivity.scenes == null) {
            return;
        }
    }
}

