package com.weloveyolo.moniguard.adapter;

import android.app.DirectAction;
import android.content.Context;
import android.graphics.BitmapFactory;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.weloveyolo.moniguard.R;

import java.io.File;
import java.util.ArrayList;
import java.util.List;

public class ScreenshotListAdapter extends RecyclerView.Adapter<ScreenshotListAdapter.ViewHolder> {

    private List<String> screenshotPaths;
    private Context context;

    public ScreenshotListAdapter(Context context) {
        this.context = context;
        this.screenshotPaths = new ArrayList<>();
    }
    public void loadAllScreenshots(String directoryPath) {
        File directory = new File(directoryPath);
        if (directory.exists() && directory.isDirectory()) {
            File[] files = directory.listFiles();
            if (files != null) {
                for (File file : files) {
                    if (file.isFile()) {
                        // 添加截图路径到列表中
                        screenshotPaths.add(file.getAbsolutePath());
                    }
                }
            }
        }
        notifyDataSetChanged(); // 通知适配器数据集已更改
    }

    public void addScreenshot(String directoryPath) {
        File directory = new File(directoryPath);
        if (directory.exists() && directory.isDirectory()) {
            File[] files = directory.listFiles();
            if (files != null) {
                for (File file : files) {
                    if (file.isFile()) {
                        // 打印文件的绝对路径（包含文件名）
                        screenshotPaths.add(file.getAbsolutePath());
                    } else if (file.isDirectory()) {
                        // 递归调用，继续遍历子目录
                        addScreenshot(file.getAbsolutePath());
                    }
                }
            }
        } else {
        }
    }
    public void clearAllScreenshots(String directoryPath) {
        screenshotPaths.clear();
        File directory = new File(directoryPath);
        if (directory.exists() && directory.isDirectory()) {
            File[] files = directory.listFiles();
            if (files != null) {
                for (File file : files) {
                    if (file.isFile()) {
                        file.delete(); // 删除文件
                    }
                }
            }
        }
        notifyDataSetChanged();
    }


    @NonNull
    @Override
    public ViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(parent.getContext()).inflate(R.layout.item_screenshot, parent, false);
        return new ViewHolder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull ViewHolder holder, int position) {
        String path = screenshotPaths.get(position);
        holder.imageView.setImageBitmap(BitmapFactory.decodeFile(path));
    }

    @Override
    public int getItemCount() {
        return screenshotPaths.size();
    }

    public static class ViewHolder extends RecyclerView.ViewHolder {
        ImageView imageView;

        public ViewHolder(@NonNull View itemView) {
            super(itemView);
            imageView = itemView.findViewById(R.id.screenshotitem);
        }
    }
    public void reloadAllScreenshots(String directoryPath) {
        // 清空原有的列表
        clearScreenshots();

        // 加载整个目录的截图
        loadAllScreenshots(directoryPath);
        notifyDataSetChanged();
    }
    public void clearScreenshots() {
        screenshotPaths.clear();
    }

}
