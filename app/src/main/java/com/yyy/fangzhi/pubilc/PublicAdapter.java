package com.yyy.fangzhi.pubilc;

import android.content.Context;
import android.util.Log;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.yyy.fangzhi.interfaces.OnItemClickListener;
import com.yyy.fangzhi.interfaces.OnItemLongClickListener;
import com.yyy.fangzhi.view.Configure.ConfigureInfo;
import com.yyy.fangzhi.view.Configure.ConfigureView;

import java.util.List;

public class PublicAdapter extends RecyclerView.Adapter<PublicAdapter.VH> {
    List<PublicItem> list;
    Context context;
    OnItemClickListener onItemClickListener;
    OnItemLongClickListener onItemLongClickListener;

    public PublicAdapter(List<PublicItem> list, Context context) {
        this.list = list;
        this.context = context;
    }

    @NonNull
    @Override
    public VH onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        return new VH(new ConfigureView(context));
    }

    @Override
    public void onBindViewHolder(@NonNull VH holder, int position) {
        holder.setIsRecyclable(false);
        ((ConfigureView) holder.itemView).setTitleVisiable(false);
        ((ConfigureView) holder.itemView).setInfoList(list.get(position).getList());
        holder.itemView.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (onItemClickListener != null) {
                    onItemClickListener.onItemClick(v, position);
                }
            }
        });
        holder.itemView.setOnLongClickListener(new View.OnLongClickListener() {
            @Override
            public boolean onLongClick(View v) {
                if (onItemLongClickListener != null) {
                    onItemLongClickListener.itemLongClick(v, position);
                }
                return false;
            }
        });
    }

    @Override
    public int getItemCount() {
//        Log.e("size", list.size() + "");
        return list.size();
    }

    public class VH extends RecyclerView.ViewHolder {
        public VH(View v) {
            super(v);
        }
    }

    public void setOnItemClickListener(OnItemClickListener onItemClickListener) {
        this.onItemClickListener = onItemClickListener;
    }

    public void setOnItemLongClickListener(OnItemLongClickListener onItemLongClickListener) {
        this.onItemLongClickListener = onItemLongClickListener;
    }
}
