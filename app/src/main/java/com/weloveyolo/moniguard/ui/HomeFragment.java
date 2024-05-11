package com.weloveyolo.moniguard.ui;

import android.content.Intent;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.ImageButton;
import android.widget.ImageView;
import android.widget.Spinner;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;
import androidx.recyclerview.widget.GridLayoutManager;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.weloveyolo.moniguard.MainActivity;
import com.weloveyolo.moniguard.R;
import com.weloveyolo.moniguard.activity.AddDeviceActivity;
import com.weloveyolo.moniguard.activity.MonitorActivity;
import com.weloveyolo.moniguard.adapter.CameraListAdapter;
import com.weloveyolo.moniguard.adapter.ScenesAdapter;
import com.weloveyolo.moniguard.api.Camera;
import com.weloveyolo.moniguard.api.Scene;

import java.util.LinkedList;
import java.util.List;
import java.util.Objects;


public class HomeFragment extends Fragment {
    private Spinner sceneSpinner;
    private RecyclerView deviceRecyclerView;
    private CameraListAdapter cameralistadpter;
//    private Scene[] scenes = {"场景1", "场景2", "场景3"};//日后可以换成接口得到的字符串数组
//    private String[][] devices = {
//            {"设备1-场景1", "设备2-场景1","设备3-场景1"},
//            {"设备1-场景2", "设备2-场景2"},
//            {"设备1-场景3", "设备2-场景3"}
//    };

    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        return inflater.inflate(R.layout.fragment_home, container, false);
    }

    private void updateDeviceList(int position) {
        // 创建 CameraListAdapter 的实例
//        cameralistadpter = new CameraListAdapter(getContext());

        // 清空适配器中的数据
        cameralistadpter.clear();
        MainActivity mainActivity = Objects.requireNonNull((MainActivity) getActivity());

        int sceneId = mainActivity.scenes.get(position).getSceneId();
        if (mainActivity.cameras.containsKey(sceneId)) {
            Objects.requireNonNull(mainActivity.cameras.get(sceneId)).forEach(camera -> {
                cameralistadpter.addDevice(camera.getName());
            });
        }

        // 通知适配器数据已更改
        cameralistadpter.notifyDataSetChanged();
    }

    public void tryShow() {
        MainActivity mainActivity = ((MainActivity) getActivity());
        if (mainActivity == null) {
            return;
        }
        if (mainActivity.scenes == null) {
            return;
        }
        requireActivity().runOnUiThread(() -> {
            //设置spinner选择场景
            sceneSpinner = getView().findViewById(R.id.scene_spinner);
            deviceRecyclerView = getView().findViewById(R.id.monitor_list);


            // 使用场景数组设置 Spinner 适配器
            String[] sceneNames = new String[mainActivity.scenes.size()];
            for (int i = 0; i < sceneNames.length; ++i) {
                sceneNames[i] = mainActivity.scenes.get(i).getName();
            }
            ScenesAdapter adapter = new ScenesAdapter(getContext(), android.R.layout.simple_spinner_item, sceneNames);
            adapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
            sceneSpinner.setAdapter(adapter);

            // 初始化 RecyclerView 和适配器
            // 初始化 RecyclerView 和适配器
            deviceRecyclerView.setLayoutManager(new GridLayoutManager(getContext(), 2)); // 替换 numberOfColumns 为你想要的列数
            cameralistadpter = new CameraListAdapter(getContext());
            deviceRecyclerView.setAdapter(cameralistadpter);

            updateDeviceList(0);

            sceneSpinner.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {
                @Override
                public void onItemSelected(AdapterView<?> parent, View view, int position, long id) {
                    updateDeviceList(position);
                }

                @Override
                public void onNothingSelected(AdapterView<?> parent) {
                    updateDeviceList(0);
                }
            });

        });
    }


    @Override
    public void onViewCreated(@NonNull View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);
        tryShow();

        // 进入添加设备
        ImageButton addSceneButton = view.findViewById(R.id.add_camera_button);
        addSceneButton.setOnClickListener(view1 -> {
            Intent intent = new Intent(getActivity(), AddDeviceActivity.class);
            startActivity(intent);
        });

        // 进入添加场景
        ImageButton addCameraButton = view.findViewById(R.id.add_scene);
        //       addCameraButton.setOnClickListener(v -> {
//            Intent intent=new Intent(getActivity(), );
//            startActivity(intent);
//        });

        // 进入门禁
        ImageButton cameraButton1 = view.findViewById(R.id.camera1_button);
        cameraButton1.setOnClickListener(v -> {
            Intent intent = new Intent(getActivity(), MonitorActivity.class);
            startActivity(intent);
        });

    }
}
