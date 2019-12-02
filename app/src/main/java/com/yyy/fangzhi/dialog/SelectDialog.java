package com.yyy.fangzhi.dialog;

import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.LinearLayout;
import android.widget.TextView;

import androidx.appcompat.app.AppCompatActivity;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.google.gson.Gson;
import com.google.gson.reflect.TypeToken;
import com.yyy.fangzhi.R;
import com.yyy.fangzhi.interfaces.OnItemClickListener;
import com.yyy.fangzhi.view.recycle.RecyclerViewDivider;

import java.util.ArrayList;
import java.util.List;

import butterknife.BindView;
import butterknife.ButterKnife;
import butterknife.OnClick;

import static com.yyy.fangzhi.util.ResultCode.FailureCode;
import static com.yyy.fangzhi.util.ResultCode.SuccessCode;

public class SelectDialog extends AppCompatActivity {

    @BindView(R.id.tv_title)
    TextView tvTitle;
    @BindView(R.id.rv_item)
    RecyclerView rvItem;
    @BindView(R.id.tv_close)
    TextView tvClose;

    int pos;

    List<Select> datas = new ArrayList<>();
    List<SelectItem> selectItems = new ArrayList<>();
    List<String> selectData = new ArrayList<>();

    SelectAdapter adapter;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.dialog_select);
        ButterKnife.bind(this);
        inti();
    }

    private void inti() {
        initView();
        getData();
        setView(pos);
    }

    private void initView() {
        rvItem.setLayoutManager(new LinearLayoutManager(this));
        rvItem.addItemDecoration(new RecyclerViewDivider(this, LinearLayout.VERTICAL));
    }

    private void getData() {
        datas = new Gson().fromJson(getIntent().getStringExtra("data"), new TypeToken<List<Select>>() {
        }.getType());
    }


    private void refreshList() {
        if (adapter == null) {
            adapter = new SelectAdapter(selectItems, this);
            adapter.setOnItemClickListener(new OnItemClickListener() {
                @Override
                public void onItemClick(View view, int position) {
                    selectData.add(new Gson().toJson(selectItems.get(position)));
                    if (pos < datas.size() - 1) {
                        pos = pos + 1;
                        setView(pos);
                    } else {
                        setResult(SuccessCode, result());
                        finish();
                    }
                }
            });
            rvItem.setAdapter(adapter);
        } else {
            adapter.notifyDataSetChanged();
        }
    }

    private void setView(int pos) {
        selectItems.clear();
        selectItems.addAll(datas.get(pos).getList());
        tvTitle.setText(datas.get(pos).getTitle());
        refreshList();
    }

    private Intent result() {
        Intent intent = new Intent();
        intent.putExtra("data", new Gson().toJson(selectData));
        return intent;
    }

    @OnClick(R.id.tv_close)
    public void onViewClicked() {
        setResult(FailureCode);
        finish();
    }
}
