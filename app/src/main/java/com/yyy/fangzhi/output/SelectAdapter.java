package com.yyy.fangzhi.output;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.yyy.fangzhi.R;
import com.yyy.fangzhi.interfaces.OnItemClickListener;
import com.yyy.fangzhi.view.TextItem;

import java.util.List;

public class SelectAdapter extends RecyclerView.Adapter<SelectAdapter.VH> {
    List<SelectItem> list;
    Context context;
    OnItemClickListener onItemClickListener;


    public void setOnItemClickListener(OnItemClickListener onItemClickListener) {
        this.onItemClickListener = onItemClickListener;
    }

    public SelectAdapter(List<SelectItem> list, Context context) {
        this.list = list;
        this.context = context;
    }

    @NonNull
    @Override
    public VH onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(context).inflate(R.layout.item_select_dialog, parent, false);
        return new VH(view);
    }

    @Override
    public void onBindViewHolder(@NonNull VH holder, int position) {
        holder.tiOrder.setTitle("订单号：").setContent(list.get(position).getSOrderNo()).removeContentClick();
        holder.tiName.setTitle("品名：").setContent(list.get(position).getSName()).removeContentClick();
        holder.tiColor.setTitle("色号/客户色号：").setContent(list.get(position).getSColorID() + "/" + list.get(position).getSCustColorID()).removeContentClick();
        holder.tiSize.setTitle("门幅/克重：").setContent(list.get(position).getFProductWidth() + "/" + list.get(position).getFProductWeight()).removeContentClick();
        holder.tiQty.setTitle("需发/未发米数").setContent(list.get(position).getFNeedQty() + "/" + list.get(position).getFNoOutQty()).removeContentClick();
        holder.itemView.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (onItemClickListener != null) {
                    onItemClickListener.onItemClick(v, position);
                }
            }
        });
    }

    @Override
    public int getItemCount() {
        return list.size();
    }

    protected class VH extends RecyclerView.ViewHolder {
        private TextItem tiName;
        private TextItem tiOrder;
        private TextItem tiColor;
        private TextItem tiSize;
        private TextItem tiQty;

        public VH(@NonNull View itemView) {
            super(itemView);
            tiName = itemView.findViewById(R.id.ti_name);
            tiOrder = itemView.findViewById(R.id.ti_order);
            tiColor = itemView.findViewById(R.id.ti_color);
            tiQty = itemView.findViewById(R.id.ti_qty);
            tiSize = itemView.findViewById(R.id.ti_size);
        }
    }
}
