package com.yyy.fangzhi.output;

import android.os.Bundle;
import android.widget.FrameLayout;
import android.widget.ImageView;
import android.widget.TextView;

import androidx.fragment.app.FragmentActivity;
import androidx.fragment.app.FragmentTransaction;

import com.yyy.fangzhi.R;

import butterknife.BindView;
import butterknife.ButterKnife;
import butterknife.OnClick;

public class OutputDetailActivity extends FragmentActivity implements NoticeSelectFragment.OnFragmentAddListener {

    @BindView(R.id.fl_fragment)
    FrameLayout flFragment;
    @BindView(R.id.tv_title)
    TextView tvTitle;
    @BindView(R.id.iv_right)
    ImageView ivRight;

    NoticeSelectFragment noticeFragmet;
    OutputDetailFragment detailFragment;

    String title;
    String iRecNo;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_output_detail);
        ButterKnife.bind(this);
        inti();
        showNotice();
    }

    private void inti() {
        initFragment();
        initIntentData();
    }

    private void initIntentData() {

    }

    private void initFragment() {
        noticeFragmet = new NoticeSelectFragment();
        detailFragment = new OutputDetailFragment();
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

    private void showDetail() {
        FragmentTransaction transaction = getSupportFragmentManager().beginTransaction();
        transaction.add(R.id.fl_fragment, detailFragment);
        transaction.show(detailFragment);
        transaction.commit();
        tvTitle.setText(title);
    }

    @Override
    public void onFragmentAdd(String intent) {

    }

    @OnClick(R.id.iv_back)
    public void onViewClicked() {
        finish();
    }
}


