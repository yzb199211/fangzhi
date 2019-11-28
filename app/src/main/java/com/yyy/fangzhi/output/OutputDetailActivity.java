package com.yyy.fangzhi.output;

import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.FrameLayout;
import android.widget.ImageView;
import android.widget.TextView;

import androidx.fragment.app.FragmentActivity;
import androidx.fragment.app.FragmentTransaction;

import com.yyy.fangzhi.R;
import com.yyy.fangzhi.util.StringUtil;

import butterknife.BindView;
import butterknife.ButterKnife;
import butterknife.OnClick;

public class OutputDetailActivity extends FragmentActivity implements NoticeSelectFragment.OnFragmentAddListener, OutputDetailFragment.OnResultListener {

    @BindView(R.id.fl_fragment)
    FrameLayout flFragment;
    @BindView(R.id.tv_title)
    TextView tvTitle;
    @BindView(R.id.iv_right)
    ImageView ivRight;
    @BindView(R.id.iv_back)
    ImageView ivBack;

    NoticeSelectFragment noticeFragmet;
    OutputDetailFragment detailFragment;

    String title;
    int iRecNo;
    int position;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_output_detail);
        ButterKnife.bind(this);
        inti();

    }

    private void inti() {
        initFragment();
        initIntentData();
        initView();
        setFragment();
    }

    private void initView() {
        ivBack.setVisibility(View.VISIBLE);
        ivRight.setVisibility(View.GONE);
    }

    private void setFragment() {
        if (iRecNo == 0) {
            showNotice();
        } else {
            showDetail(null);
        }
    }

    private void initIntentData() {
        title = getIntent().getStringExtra("title");
        iRecNo = getIntent().getIntExtra("iRecNo", 0);
        position = getIntent().getIntExtra("position", -1);
    }

    private void initFragment() {
        noticeFragmet = new NoticeSelectFragment();
    }

    private void showNotice() {
        FragmentTransaction transaction = getSupportFragmentManager().beginTransaction();
        transaction.add(R.id.fl_fragment, noticeFragmet);
        transaction.show(noticeFragmet);
        transaction.commit();
        tvTitle.setText(getString(R.string.title_notice));
    }

    private void hideNotice() {
        FragmentTransaction transaction = getSupportFragmentManager().beginTransaction();
        transaction.hide(noticeFragmet);
    }

    private void showDetail(String data) {
        detailFragment = OutputDetailFragment.newInstance(data);
        FragmentTransaction transaction = getSupportFragmentManager().beginTransaction();
        transaction.add(R.id.fl_fragment, detailFragment);
        transaction.show(detailFragment);
        transaction.commit();
        tvTitle.setText(title);
    }

    @Override
    public void onFragmentAdd(String data) {
        if (StringUtil.isNotEmpty(data)) {
            hideNotice();
            showDetail(data);
        }
    }

    @OnClick(R.id.iv_back)
    public void onViewClicked() {
        finish();
    }

    @Override
    public void onResult(Intent intent) {

    }
}


