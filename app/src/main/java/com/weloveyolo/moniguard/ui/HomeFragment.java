package com.weloveyolo.moniguard.ui;

import android.app.Activity;
import android.content.Intent;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.ImageButton;
import android.widget.Spinner;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;
import androidx.recyclerview.widget.GridLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.weloveyolo.moniguard.MainActivity;
import com.weloveyolo.moniguard.R;
import com.weloveyolo.moniguard.activity.AddDeviceActivity;
import com.weloveyolo.moniguard.activity.AddSceneActivity;
import com.weloveyolo.moniguard.activity.MonitorActivity;
import com.weloveyolo.moniguard.adapter.CameraListAdapter;
import com.weloveyolo.moniguard.adapter.ScenesAdapter;

import java.util.Objects;


public class HomeFragment extends Fragment {
    private Spinner sceneSpinner;
    private RecyclerView deviceRecyclerView;
    private CameraListAdapter cameralistadpter;

    private int chosenSceneId = 0;
    private final int CREATE_SCENE_CODE = 101;
    private final int ADD_CAMERA_CODE = 102;

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

        chosenSceneId = sceneId;

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
            deviceRecyclerView.setLayoutManager(new GridLayoutManager(getContext(), 2)); // 替换 numberOfColumns 为你想要的列数
            cameralistadpter = new CameraListAdapter(getContext());
            deviceRecyclerView.setAdapter(cameralistadpter);

            if(!mainActivity.scenes.isEmpty()) {
                updateDeviceList(0);
            }

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
            intent.putExtra("sceneId", chosenSceneId);  // 传入场景id
            startActivityForResult(intent, ADD_CAMERA_CODE);
        });

        // 进入创建场景
        ImageButton addCameraButton = view.findViewById(R.id.add_scene);
        addCameraButton.setOnClickListener(v -> {
            Intent intent=new Intent(getActivity(), AddSceneActivity.class);
            startActivityForResult(intent, CREATE_SCENE_CODE);
        });

        // 进入门禁
        ImageButton cameraButton1 = view.findViewById(R.id.camera1_button);
        cameraButton1.setOnClickListener(v -> {
            Intent intent = new Intent(getActivity(), MonitorActivity.class);
            startActivity(intent);
        });
    }

    // 处理添加返回结果
    @Override
    public void onActivityResult(int requestCode, int resultCode, @Nullable Intent data) {
        super.onActivityResult(requestCode, resultCode, data);

        if(resultCode != Activity.RESULT_OK) return;

        if(requestCode == CREATE_SCENE_CODE){
            ((MainActivity) requireActivity()).getHomeData();
        }
        else if(requestCode == ADD_CAMERA_CODE){
            ((MainActivity) requireActivity()).getCamerasOfSingleScene(chosenSceneId);
        }
    }
}
