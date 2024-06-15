package com.weloveyolo.moniguard.adapter;

import android.annotation.SuppressLint;
import android.app.DirectAction;
import android.content.Context;
import android.graphics.BitmapFactory;
import android.util.SparseBooleanArray;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.CheckBox;
import android.widget.CompoundButton;
import android.widget.ImageView;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.bumptech.glide.Glide;
import com.bumptech.glide.request.RequestOptions;
import com.weloveyolo.moniguard.R;

import java.io.File;
import java.util.ArrayList;
import java.util.List;
public class BlackListAdapter extends RecyclerView.Adapter<BlackListAdapter.BlackListViewHolder> {
    private List<String> blackList;
    private LayoutInflater layoutInflater;
    private Context context;
    public List<Boolean> checkboxStates = new ArrayList<>();

    private SparseBooleanArray selectedItems;

    public BlackListAdapter(Context context, List<String> blackList) {
        this.context = context;
        this.blackList = blackList;
        this.layoutInflater = LayoutInflater.from(context);
        this.selectedItems = new SparseBooleanArray();
        for (int i = 0; i < blackList.size(); i++) {
            checkboxStates.add(false);
        }
    }

    @NonNull
    @Override
    public BlackListViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {

        View itemView = layoutInflater.inflate(R.layout.item_blacklist, parent, false);
        return new BlackListViewHolder(itemView);
    }

    @Override
    public void onBindViewHolder(@NonNull BlackListViewHolder holder, int position) {
        String imageUrl = blackList.get(position);
        // 使用 Glide 加载图片
        Glide.with(context)
                .load(imageUrl)
                .apply(RequestOptions.circleCropTransform())
                .into(holder.faceImageView);

        holder.checkBox.setChecked(checkboxStates.get(position));

        holder.checkBox.setOnCheckedChangeListener(new CompoundButton.OnCheckedChangeListener() {
            @Override
            public void onCheckedChanged(CompoundButton buttonView, boolean isChecked) {
                checkboxStates.set(holder.getAdapterPosition(), isChecked);
            }
        });
    }

    @Override
    public int getItemCount() {
        return blackList.size();
    }
    public void addFaceImage(String imageUrl) {
        blackList.add(imageUrl);
        checkboxStates.add(false);
        notifyItemInserted(blackList.size() - 1);
        notifyDataSetChanged();
    }


    //选黑进白
//
//    public void toggleSelection(int position) {
//        // 切换选中状态
//        if (selectedItems.get(position, false)) {
//            selectedItems.delete(position);
//        } else {
//            selectedItems.put(position, true);
//        }
//        notifyItemChanged(position);
//    }
//    public List<Integer> getSelectedItems() {
//        // 获取选中项的位置列表
//        List<Integer> items = new ArrayList<>(selectedItems.size());
//        for (int i = 0; i < selectedItems.size(); i++) {
//            items.add(selectedItems.keyAt(i));
//        }
//        return items;
//    }

    static class BlackListViewHolder extends RecyclerView.ViewHolder {
        ImageView faceImageView;
        CheckBox checkBox;

        public BlackListViewHolder(@NonNull View itemView) {
            super(itemView);
            faceImageView = itemView.findViewById(R.id.screenshotitem1);
            checkBox = itemView.findViewById(R.id.check_box);

        }
    }


}
