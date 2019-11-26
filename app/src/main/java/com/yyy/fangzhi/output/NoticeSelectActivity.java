package com.yyy.fangzhi.output;

import android.os.Bundle;
import android.view.View;
import android.widget.FrameLayout;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.Switch;
import android.widget.TextView;

import androidx.appcompat.app.AppCompatActivity;
import androidx.recyclerview.widget.RecyclerView;

import com.yyy.fangzhi.R;
import com.yyy.fangzhi.util.SharedPreferencesHelper;
import com.yyy.fangzhi.util.net.NetConfig;
import com.yyy.fangzhi.view.EditListenerView;

import java.util.ArrayList;
import java.util.List;

import butterknife.BindView;
import butterknife.ButterKnife;
import butterknife.OnClick;

public class NoticeSelectActivity extends AppCompatActivity {


    @BindView(R.id.iv_back)
    ImageView ivBack;
    @BindView(R.id.tv_title)
    TextView tvTitle;
    @BindView(R.id.iv_right)
    ImageView ivRight;
    @BindView(R.id.tv_empty)
    TextView tvEmpty;
    @BindView(R.id.fl_empty)
    FrameLayout flEmpty;
    @BindView(R.id.tv_storage)
    TextView tvStorage;
    @BindView(R.id.ll_storage)
    LinearLayout llStorage;
    @BindView(R.id.et_notice)
    EditListenerView etNotice;
    @BindView(R.id.sv_red)
    Switch svRed;
    @BindView(R.id.sv_cut)
    Switch svCut;
    @BindView(R.id.sv_finish)
    Switch svFinish;
    @BindView(R.id.ll_swicth)
    LinearLayout llSwicth;
    @BindView(R.id.rv_item)
    RecyclerView rvItem;

    String userid;
    String url;
    String address;
    String companyCode;

    int formid;
    String title;

    List<Notice> notices;

    SharedPreferencesHelper preferencesHelper;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_notice_select);
        ButterKnife.bind(this);
        preferencesHelper = new SharedPreferencesHelper(this, getString(R.string.preferenceCache));
        init();
    }

    private void init() {
        initList();
        getPreferenceData();
        getIntentData();
        initView();
    }

    private void initView() {
        tvTitle.setText(getString(R.string.title_notice));
    }

    private void initList() {
        notices = new ArrayList<>();
    }

    private void getPreferenceData() {
        userid = (String) preferencesHelper.getSharedPreference("userid", "");
        address = (String) preferencesHelper.getSharedPreference("address", "");
        companyCode = (String) preferencesHelper.getSharedPreference("companyCode", "");
        url = address + NetConfig.server + NetConfig.PDAHandler;
        ;
    }

    private void getIntentData() {
        formid = getIntent().getIntExtra("formid", 0);
        title = getIntent().getStringExtra("title");
    }

    @OnClick({R.id.iv_back, R.id.tv_storage, R.id.iv_storage})
    public void onViewClicked(View view) {
        switch (view.getId()) {
            case R.id.iv_back:
                finish();
                break;
            case R.id.tv_storage:
                break;
            case R.id.iv_storage:
                break;
        }
    }
}
