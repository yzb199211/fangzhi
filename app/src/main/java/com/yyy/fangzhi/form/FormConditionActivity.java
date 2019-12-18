package com.yyy.fangzhi.form;

import android.content.Intent;
import android.os.Bundle;
import android.text.TextUtils;
import android.view.View;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.RelativeLayout;
import android.widget.TextView;

import androidx.annotation.Nullable;

import com.google.gson.Gson;

import com.google.gson.reflect.TypeToken;
import com.yyy.fangzhi.BaseActivity;
import com.yyy.fangzhi.R;
import com.yyy.fangzhi.form.model.FormBean;
import com.yyy.fangzhi.util.LookupDataUtil;
import com.yyy.fangzhi.util.SharedPreferencesHelper;
import com.yyy.fangzhi.util.StringUtil;
import com.yyy.fangzhi.view.function.FunctionView;

import java.util.ArrayList;
import java.util.List;

import butterknife.BindView;
import butterknife.ButterKnife;
import butterknife.OnClick;

public class FormConditionActivity extends BaseActivity {

    private static final String TAG = "FormConditionActivity";
    private static final int BASE_CODE = 500;
    @BindView(R.id.iv_back)
    ImageView ivBack;
    @BindView(R.id.tv_title)
    TextView tvTitle;
    @BindView(R.id.ll_content)
    LinearLayout llContent;
    @BindView(R.id.bottom_review)
    RelativeLayout bottonView;
    @BindView(R.id.tv_disagree)
    TextView tvDisagree;
    @BindView(R.id.tv_agree)
    TextView tvAgree;
    SharedPreferencesHelper preferencesHelper;

    private String userId;
    boolean isStore;
    int code;


    private List<FormBean.ReportConditionBean> conditions;
    private List<FunctionView> viewList;


    public static FormConditionActivity mActivity;

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        mActivity = this;
        setContentView(R.layout.activity_form_condition);
        ButterKnife.bind(this);
        preferencesHelper = new SharedPreferencesHelper(this, getString(R.string.preferenceCache));
        userId = (String) preferencesHelper.getSharedPreference("userid", "");
        code = getIntent().getIntExtra("code", 0);
        isStore = getIntent().getBooleanExtra("isStore", false);

        init();
    }

    private void init() {
        tvTitle.setText("筛选条件");
        tvDisagree.setVisibility(View.INVISIBLE);
        ivBack.setVisibility(View.VISIBLE);
        tvAgree.setText(getResources().getString(R.string.submit));
//        ivBack.setBackgroundResource(R.mipmap.icon_back);
        conditions = new ArrayList<>();
        viewList = new ArrayList<>();
        Intent intent = getIntent();
        String data = intent.getStringExtra("data");
        if (StringUtil.isNotEmpty(data)) {
            conditions = new Gson().fromJson(data, new TypeToken<List<FormBean.ReportConditionBean>>() {
            }.getType());
            initView();
        }
    }

    private void initView() {
        if (conditions.size() > 0) {
            bottonView.setVisibility(View.VISIBLE);
            for (int i = 0; i < conditions.size(); i++) {
                FunctionView view = new FunctionView(this);
                view.setTitle(conditions.get(i).getSCaption());
                view.setHint("请输入" + conditions.get(i).getSCaption());
                view.setText(TextUtils.isEmpty(conditions.get(i).getSValue()) ? "" : conditions.get(i).getSValue());
                view.setLookupName(conditions.get(i).getSLookUpName());
                view.setLookupFilter(conditions.get(i).getSLookUpFilters());
                view.setCode(BASE_CODE + i);
                view.setUserid(userId);
                view.setmActivity(this);
                view.build(conditions.get(i).getSFieldType());
                llContent.addView(view);
                viewList.add(view);
            }
        }
    }


    @OnClick({R.id.iv_back, R.id.tv_agree})
    public void onViewClicked(View view) {
        switch (view.getId()) {
            case R.id.iv_back:
                finish();
                break;
            case R.id.tv_agree:
                for (int i = 0; i < conditions.size(); i++) {
                    if (TextUtils.isEmpty(conditions.get(i).getSLookUpName())) {
                        conditions.get(i).setSelectValue(viewList.get(i).getText());
                    }
                }
                String data = LookupDataUtil.getConditionData(conditions, isStore);
                Intent intent = new Intent();
                intent.putExtra("data", data);
                intent.putExtra("isStore", LookupDataUtil.getConditionData(conditions, true));
                intent.putExtra("noStore", LookupDataUtil.getConditionData(conditions, false));
                setResult(code, intent);
                finish();
                break;
            default:
                break;
        }
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, @Nullable Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        if (data != null) {
            int code = requestCode - BASE_CODE;
            if (code > -1 || code < conditions.size()) {
                conditions.get(code).setSelectValue(data.getStringExtra("id"));
                viewList.get(code).setText(data.getStringExtra("name"));
            }
        }
    }
}
