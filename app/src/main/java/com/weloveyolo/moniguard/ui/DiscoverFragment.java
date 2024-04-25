package com.weloveyolo.moniguard.ui;

import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.content.Intent;
import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;

import com.weloveyolo.moniguard.R;
import com.weloveyolo.moniguard.activity.AlbumActivity;
import com.weloveyolo.moniguard.activity.Health_monitoring;
import com.weloveyolo.moniguard.activity.Sports;

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
        button2.setOnClickListener(view1 -> {
            Intent intent = new Intent(getActivity(), Sports.class);
            startActivity(intent);
        });
    }

}

