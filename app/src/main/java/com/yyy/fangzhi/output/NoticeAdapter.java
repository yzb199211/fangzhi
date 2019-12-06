package com.yyy.fangzhi.output;

import android.content.Context;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.LinearLayout;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.yyy.fangzhi.R;
import com.yyy.fangzhi.interfaces.OnItemClickListener;
import com.yyy.fangzhi.view.TextItem;

import java.util.List;

public class NoticeAdapter extends RecyclerView.Adapter<NoticeAdapter.VH> {
    Context context;
    List<Notice> list;
    OnItemClickListener onItemClickListener;

    public void setOnItemClickListener(OnItemClickListener onItemClickListener) {
        this.onItemClickListener = onItemClickListener;
    }

    public NoticeAdapter(Context context, List<Notice> list) {
        this.context = context;
        this.list = list;
    }

    @NonNull
    @Override
    public VH onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(context).inflate(R.layout.item_notice, parent, false);
        return new VH(view);
    }

    @Override
    public void onBindViewHolder(@NonNull VH holder, int position) {
        holder.tiNo.setTitle("通知单号：").setContent(list.get(position).getSBillNo()).removeContentClick();
        holder.tiFinish.setContent(list.get(position).getIFinish() == 0 ? "" : "完成").setContentColor(context.getResources().getColor(R.color.red)).removeContentClick();
        holder.tiOrder.setTitle("订单号：").setContent(list.get(position).getSContractNos()).removeContentClick();
        holder.tiRed.setContent(list.get(position).getIRed() == 0 ? "" : "红冲").setContentColor(context.getResources().getColor(R.color.red)).removeContentClick();
        holder.tiCus.setTitle("客户：").setContent(list.get(position).getSCustShortName()).removeContentClick();
        holder.tiCut.setContent(list.get(position).getICut() == 0 ? "" : "零件").setContentColor(context.getResources().getColor(R.color.red)).removeContentClick();
        holder.tiName.setTitle("产品：").setContent(list.get(position).getSName()).removeContentClick();
        holder.tiColor.setTitle("色号：").setContent(list.get(position).getSColorID()).removeContentClick();
        holder.tiNeed.setTitle("需求米数：").setContent(list.get(position).getFQty() + "").removeContentClick();
        holder.tiDo.setTitle("已发米数：").setContent(list.get(position).getFOutQty() + "").removeContentClick();
        holder.tiUndo.setTitle("未发米数：").setContent(list.get(position).getFNoOutQty() + "").removeContentClick();
        holder.tiDate.setTitle("下发日期：").setContent(list.get(position).getDDate()).removeContentClick();
        holder.itemView.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Log.e("click", "clock");
                if (onItemClickListener != null)
                    onItemClickListener.onItemClick(v, position);
            }
        });
    }

    @Override
    public int getItemCount() {
        return list.size();
    }

    protected class VH extends RecyclerView.ViewHolder {
        TextItem tiNo;
        TextItem tiFinish;
        TextItem tiOrder;
        TextItem tiRed;
        TextItem tiCus;
        TextItem tiCut;
        TextItem tiName;
        TextItem tiColor;
        TextItem tiNeed;
        TextItem tiDo;
        TextItem tiUndo;
        TextItem tiDate;

        public VH(View v) {
            super(v);
            tiNo = v.findViewById(R.id.ti_no);
            tiFinish = v.findViewById(R.id.ti_finish);
            tiOrder = v.findViewById(R.id.ti_order);
            tiRed = v.findViewById(R.id.ti_red);
            tiCus = v.findViewById(R.id.ti_cus);
            tiCut = v.findViewById(R.id.ti_cut);
            tiName = v.findViewById(R.id.ti_name);
            tiColor = v.findViewById(R.id.ti_color);
            tiNeed = v.findViewById(R.id.ti_need);
            tiDo = v.findViewById(R.id.ti_do);
            tiUndo = v.findViewById(R.id.ti_undo);
            tiDate = v.findViewById(R.id.ti_date);
        }
    }
}
