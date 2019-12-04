package com.yyy.fangzhi.count;

import android.os.Bundle;
import android.view.View;
import android.widget.FrameLayout;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;

import androidx.appcompat.app.AppCompatActivity;
import androidx.recyclerview.widget.RecyclerView;

import com.yyy.fangzhi.R;
import com.yyy.fangzhi.view.EditListenerView;
import com.yyy.fangzhi.view.TextItem;

import butterknife.BindView;
import butterknife.ButterKnife;
import butterknife.OnClick;

public class CountActivity extends AppCompatActivity {

    @BindView(R.id.tv_title)
    TextView tvTitle;
    @BindView(R.id.iv_right)
    ImageView ivRight;
    @BindView(R.id.fl_empty)
    FrameLayout flEmpty;
    @BindView(R.id.ti_storage_in)
    TextItem tiStorageIn;
    @BindView(R.id.ti_pos)
    TextItem tiPos;
    @BindView(R.id.ll_one)
    LinearLayout llOne;
    @BindView(R.id.it_qty)
    TextItem itQty;
    @BindView(R.id.it_num)
    TextItem itNum;
    @BindView(R.id.ll_two)
    LinearLayout llTwo;
    @BindView(R.id.et_code)
    EditListenerView etCode;
    @BindView(R.id.ll_three)
    LinearLayout llThree;
    @BindView(R.id.rv_item)
    RecyclerView rvItem;
    @BindView(R.id.tv_delete)
    TextView tvDelete;
    @BindView(R.id.bottom_layout)
    LinearLayout bottomLayout;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_count);
        ButterKnife.bind(this);
    }

    @OnClick({R.id.iv_back, R.id.tv_empty, R.id.tv_clear, R.id.tv_delete, R.id.tv_save, R.id.tv_submit})
    public void onViewClicked(View view) {
        switch (view.getId()) {
            case R.id.iv_back:
                finish();
                break;
            case R.id.tv_empty:
                break;
            case R.id.tv_clear:
                break;
            case R.id.tv_delete:
                break;
            case R.id.tv_save:
                break;
            case R.id.tv_submit:
                break;
        }
    }
}
