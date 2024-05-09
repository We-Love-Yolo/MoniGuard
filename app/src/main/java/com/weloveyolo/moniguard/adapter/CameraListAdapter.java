package com.weloveyolo.moniguard.adapter;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.weloveyolo.moniguard.R;
import com.weloveyolo.moniguard.api.Camera;

public class CameraListAdapter extends RecyclerView.Adapter<CameraListAdapter.MyHolder> {
private List<Camera.ResultBean.DataBean> mDataBeanList = new ArrayList<>();
private Context mContext;

public CameraListAdapter(Context context) {
        this.mContext = context;
        }
/**
 * 为adapter 设置数据源
 */
public void setListData(List<Camera.ResultBean.DataBean> list) {
        this.mDataBeanList = list;
        notifyDataSetChanged();
        }

@NonNull
@Override
public MyHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        //加载布局文件
        View view = LayoutInflater.from(parent.getContext()).inflate(R.layout.item_monitor, null);
        return new MyHolder(view);
        }

@Override
public void onBindViewHolder(@NonNull MyHolder holder, int position) {
        Camera.ResultBean.DataBean dataBean = mDataBeanList.get(position);

        //设置数据
        holder.author_name.setText(dataBean.getAuthor_name());
        holder.title_text.setText(dataBean.getTitle());
        holder.date.setText(dataBean.getDate());
        //加载图片
        Glide.with(mContext).load(dataBean.getThumbnail_pic_s()).error(R.drawable.img_error).into(holder.title_pic);

        holder.itemView.setOnClickListener(new View.OnClickListener(){
@Override
public void onClick(View v){
        if(mOnItemClickListener!=null){
        mOnItemClickListener.onItemClick(dataBean,position);
        }
        }
        });
        }

@Override
public int getItemCount() {
        if(mDataBeanList!=null)
        return mDataBeanList.size();
        else return 0;
        }

static class MyHolder extends RecyclerView.ViewHolder {

    ImageView title_pic;
    TextView title_text;
    TextView author_name;
    TextView date;
    public MyHolder(@NonNull View itemView) {
        super(itemView);
        title_pic = itemView.findViewById(R.id.title_pic);
        title_text = itemView.findViewById(R.id.title_text);
        author_name = itemView.findViewById(R.id.author_name);
        date = itemView.findViewById(R.id.date);
    }
}

    private onItemClickListener mOnItemClickListener;
    public onItemClickListener getmOnItemClickListener() {
        return mOnItemClickListener;
    }
    public void setOnItemClickListener(onItemClickListener mOnItemClickListener) {
        this.mOnItemClickListener = mOnItemClickListener;
    }
public interface onItemClickListener{
    void onItemClick(Camera.ResultBean.DataBean dataBean,int position);
}
}