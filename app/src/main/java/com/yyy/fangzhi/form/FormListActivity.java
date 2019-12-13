package com.yyy.fangzhi.form;

import android.content.Intent;
import android.graphics.Color;
import android.os.Bundle;
import android.text.TextUtils;
import android.view.View;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;

import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.google.gson.Gson;
import com.jcodecraeer.xrecyclerview.ProgressStyle;
import com.jcodecraeer.xrecyclerview.XRecyclerView;
import com.yyy.fangzhi.R;
import com.yyy.fangzhi.dialog.LoadingDialog;
import com.yyy.fangzhi.form.adapter.FormListAdapter;
import com.yyy.fangzhi.form.model.FormBean;
import com.yyy.fangzhi.form.model.FormConditionBean;
import com.yyy.fangzhi.interfaces.OnItemClickListener;
import com.yyy.fangzhi.interfaces.ResponseListener;
import com.yyy.fangzhi.util.StringUtil;
import com.yyy.fangzhi.util.Toasts;
import com.yyy.fangzhi.util.net.NetConfig;
import com.yyy.fangzhi.util.net.NetParams;
import com.yyy.fangzhi.util.net.NetUtil;
import com.yyy.fangzhi.view.recycle.RecyclerViewDivider;
import com.yyy.fangzhi.view.review.model.ReviewInfo;
import com.yyy.fangzhi.view.review.model.ReviewStyle;
import com.yyy.fangzhi.util.SharedPreferencesHelper;
import com.yyy.fangzhi.view.tab.TabView;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.io.IOException;
import java.util.ArrayList;
import java.util.List;

import butterknife.BindView;
import butterknife.ButterKnife;
import butterknife.OnClick;

public class FormListActivity extends AppCompatActivity {

    private final static int CONDIRION_CODE = 500;

    @BindView(R.id.iv_back)
    ImageView ivBack;
    @BindView(R.id.tv_title)
    TextView tvTitle;
    @BindView(R.id.tv_right)
    TextView tvRight;
    @BindView(R.id.refresh_list)
    XRecyclerView refreshList;
    @BindView(R.id.ll_tab)
    LinearLayout llTab;

    LinearLayoutManager manager;

    int pagerIndex = 1;

    private String menuId;
    private String userId;
    private String filter = "";
    private String fixfilter = "";

    private String address;
    private String url;

    boolean isStore;

    SharedPreferencesHelper preferencesHelper;

    List<FormBean.ReportColumns2> styleList;
    List<FormBean.ReportInfoBean> infoBeans;
    List<FormBean.ReportConditionBean> conditionBeans;

    private List<FormConditionBean> fixconditions;
    private List<FormBean.ReportConditionBean> conditions;
    private List<ReviewStyle> items;

    FormListAdapter formListAdapter;
    TabView currentView;
    int currenViewPos = -1;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_form_list);
        ButterKnife.bind(this);
        preferencesHelper = new SharedPreferencesHelper(this, getString(R.string.preferenceCache));
        inti();
    }

    private void inti() {
        address = (String) preferencesHelper.getSharedPreference("address", "");
        url = address + NetConfig.server + NetConfig.ReportHandler_Method;

        styleList = new ArrayList<>();
        infoBeans = new ArrayList<>();
        conditionBeans = new ArrayList<>();
//        columnsBeans = new ArrayList<>();
        fixconditions = new ArrayList<>();
        conditions = new ArrayList<>();
        items = new ArrayList<>();

        manager = new LinearLayoutManager(this);
        manager.setOrientation(RecyclerView.VERTICAL);
        refreshList.setLayoutManager(manager);
        refreshList.setRefreshProgressStyle(ProgressStyle.BallSpinFadeLoader);
        refreshList.setLoadingMoreProgressStyle(ProgressStyle.Pacman);
        refreshList.setArrowImageView(R.mipmap.iconfont_downgrey);
        refreshList.getDefaultRefreshHeaderView()
                .setRefreshTimeVisible(true);

        refreshList.setLoadingMoreEnabled(true);


        userId = (String) preferencesHelper.getSharedPreference("userid", "");
        Intent intent = getIntent();
        tvTitle.setText(TextUtils.isEmpty(intent.getStringExtra("title")) ? "" : intent.getStringExtra("title"));
        menuId = intent.getStringExtra("menuid");
        ivBack.setVisibility(View.VISIBLE);
        getData(false, false, pagerIndex);
    }

    /*设置固定筛选项*/
    private void setTab() {
        llTab.setVisibility(View.VISIBLE);
        for (int i = 0; i < fixconditions.size(); i++) {
            TabView tab = new TabView(this);
            tab.setText(fixconditions.get(i).getName());
            tab.setPosition(i);
            if (i == 0) {
                currenViewPos = i;
                currentView = tab;
            }
            tab.setOnItemClickListener(new OnItemClickListener() {
                @Override
                public void onItemClick(View view, int position) {
                    if (currenViewPos != position) {
                        currenViewPos = position;
                        currentView.setChecked(false);
                        currentView = (TabView) view;
                        currentView.setChecked(true);
                        fixfilter = fixconditions.get(position).getFilters();
                        pagerIndex = 1;
                        items.clear();
                        if (formListAdapter != null) {
                            formListAdapter.notifyDataSetChanged();
                        }
                        getFormData(false, false, pagerIndex);
                    }
                }
            });
            llTab.addView(tab);
        }
        currentView.setChecked(true);
    }

    /*获取数据*/
    private void getData(boolean isRefresh, boolean isLoadMore, int page) {
        LoadingDialog.showDialogForLoading(this);
        new NetUtil(getParams(), url, new ResponseListener() {
            @Override
            public void onSuccess(String string) {
                try {

                    JSONObject jsonObject = new JSONObject(string);
                    boolean isSuccess = jsonObject.getBoolean("success");
                    if (isSuccess) {
                        String table = jsonObject.optString("info");
                        Gson gson = new Gson();
                        FormBean formBean = gson.fromJson(table, FormBean.class);
                        List<FormBean.ReportInfoBean> reportInfoBeans = formBean.getReportInfo();
                        List<FormBean.ReportConditionBean> reportConditionBeans = formBean.getReportCondition();
                        List<FormBean.ReportColumns2> stylebeans = formBean.getReportColumns2();

                        if (reportInfoBeans.size() > 0) {
                            initFix(reportInfoBeans.get(0));
                        }
                        if (stylebeans.size() > 0) {
                            styleList.addAll(stylebeans);
                        } else {
                            loadFail("未设置App列样式");
                            return;
                        }

                        initData(jsonObject.optJSONArray("data"));

                        runOnUiThread(new Runnable() {
                            @Override
                            public void run() {
                                if (reportConditionBeans.size() > 0) {
                                    tvRight.setVisibility(View.VISIBLE);
                                    conditions.addAll(reportConditionBeans);
                                    pagerIndex = page;
                                }
                                if (fixconditions.size() > 0) {
                                    setTab();
                                }
                            }
                        });
                        loadFail("");

                    } else {
                        loadFail(jsonObject.optString("message"));
                    }
                } catch (JSONException e) {
                    loadFail("json解析错误");
                    e.printStackTrace();
                } catch (Exception e) {
                    loadFail("数据解析错误");
                    e.printStackTrace();
                }
            }

            @Override
            public void onFail(IOException e) {
                e.printStackTrace();
                loadFail("未获取到数据");
            }
        });
    }

    /*格式化数据*/
    private void initData(JSONArray jsonArray) throws Exception {

        if (jsonArray.length() > 0) {
            for (int i = 0; i < jsonArray.length(); i++) {
                ReviewStyle reviewStyle = new ReviewStyle();
                List<ReviewInfo> infos = new ArrayList<>();

                for (int j = 0; j < styleList.size(); j++) {

                    ReviewInfo info = new ReviewInfo();

                    if (StringUtil.isNotEmpty(styleList.get(j).getsNameFontSize()) && StringUtil.isInteger(styleList.get(j).getsNameFontSize())) {
                        info.setTitleSize(Integer.parseInt(styleList.get(j).getsNameFontSize()));
                    } else {
                        info.setTitleSize(0);
                    }
                    if (StringUtil.isNotEmpty(styleList.get(j).getsValueFontSize()) && StringUtil.isInteger(styleList.get(j).getsValueFontSize())) {
                        info.setContentSize(Integer.parseInt(styleList.get(j).getsValueFontSize()));
                    } else {
                        info.setContentSize(0);
                    }

                    info.setTitle(styleList.get(j).getsFieldsDisplayName());
                    info.setTitleBold((styleList.get(j).getiNameFontBold() == 1) ? true : false);
                    if (StringUtil.isColor(styleList.get(j).getsNameFontColor()))
                        info.setTitleColor(Color.parseColor(styleList.get(j).getsNameFontColor()));
                    info.setContent(jsonArray.getJSONObject(i).optString(styleList.get(j).getsFieldsName()));
                    info.setSingleLine(true);
                    info.setContentBold((styleList.get(j).getiValueFontBold() == 1) ? true : false);
                    if (StringUtil.isColor(styleList.get(j).getsValueFontColor()))
                        info.setContentColor(Color.parseColor(styleList.get(j).getsValueFontColor()));
                    info.setWidthPercent(StringUtil.isPercent(styleList.get(j).getiProportion()));
//                    Log.e("per", styleList.get(j).getiProportion() + "");
                    info.setRow(styleList.get(j).getiSerial());
                    setProgressAndAddValue(j, info, infos);
                }


                reviewStyle.setList(infos);
                items.add(reviewStyle);
//                Log.e("datas", new Gson().toJson(items));
            }
            runOnUiThread(new Runnable() {
                @Override
                public void run() {
                    refresh();
                }
            });
        } else {
            loadFail("无数据");
        }
    }

    private void setProgressAndAddValue(int j, ReviewInfo info, List<ReviewInfo> infos) {
        if (styleList.get(j).getsFieldsType().equals("progressBar") == true) {
            info.setProgress(true);
            String value = info.getContent();
//                        infos.add(info);
            if (value.contains("P") && !value.contains("PP")) {
                String[] datas = value.split("P");

                if (StringUtil.isNotEmpty(datas[1])) {
                    info.setTitle(datas[1]);
                    info.setContent(datas[2].replace("{", "").replace("}", ""));
                    infos.add(info);
                }
            } else if (!value.contains("PP")) {
                infos.add(info);
            }
        } else {
            infos.add(info);
        }
    }

    private void getFormData(boolean isRefresh, boolean isLoadMore, int pager) {
        LoadingDialog.showDialogForLoading(this);
        new NetUtil(getFormParams(pager), url, new ResponseListener() {
            @Override
            public void onSuccess(String string) {
                try {
                    JSONObject jsonObject = new JSONObject(string);
                    boolean isSuccess = jsonObject.optBoolean("success");
                    if (isSuccess) {
                        if (jsonObject.optString("data").equals("[]")) {
                            loadFail("无数据");
                        } else {
                            pagerIndex = pager;
                            initData(jsonObject.optJSONArray("data"));

                        }
                        runOnUiThread(new Runnable() {
                            @Override
                            public void run() {
                                if (isRefresh) {
                                    refreshList.refreshComplete();
                                }
                                if (isLoadMore) {
                                    refreshList.loadMoreComplete();
                                }
                            }
                        });
                        loadFail("");

                    } else {
                        loadFail(jsonObject.getString("message"));
                    }
                } catch (JSONException e) {
                    loadFail("json解析错误");
                    e.printStackTrace();
                } catch (Exception e) {
                    loadFail("数据解析错误");
                    e.printStackTrace();
                }
            }

            @Override
            public void onFail(IOException e) {
                e.printStackTrace();
                loadFail("未获取到数据");

            }
        });
    }

    private List<NetParams> getFormParams(int pagerIndex) {
        List<NetParams> params = new ArrayList<>();
        params.add(new NetParams("otype", "getReportData"));
        params.add(new NetParams("userid", userId));
        params.add(new NetParams("iFormID", menuId));
        params.add(new NetParams("pageNo", pagerIndex + ""));
        if (StringUtil.isNotEmpty(fixfilter) && StringUtil.isNotEmpty(filter)) {
            params.add(new NetParams("filters", filter + " and " + fixfilter));
        } else {
            params.add(new NetParams("filters", filter + fixfilter));
        }
        params.add(new NetParams("sort", ""));
        params.add(new NetParams("order", "asc"));
        return params;
    }

    private void refresh() {
        if (formListAdapter == null) {
            formListAdapter = new FormListAdapter(this, items);
            refreshList.addItemDecoration(new RecyclerViewDivider(this, LinearLayoutManager.VERTICAL));
            refreshList.setAdapter(formListAdapter);
            refreshList.setLoadingListener(new XRecyclerView.LoadingListener() {
                @Override
                public void onRefresh() {
//                    List<ReviewStyle> styles = new ArrayList<>();
//                    styles.addAll(items);
                    items.removeAll(items);
                    formListAdapter.notifyDataSetChanged();
                    pagerIndex = 1;
                    getFormData(true, false, pagerIndex);
                }

                @Override
                public void onLoadMore() {
//                    Log.e("load", "loading" + pagerIndex);
                    getFormData(false, true, pagerIndex + 1);
                }
            });
        } else {
            formListAdapter.notifyDataSetChanged();
        }
    }


    /*初始化固定条件*/
    private void initFix(FormBean.ReportInfoBean fixcondition) throws Exception {
        if (StringUtil.isNotEmpty(fixcondition.getSAppFiltersName1())) {
            fixconditions.add(new FormConditionBean(fixcondition.getSAppFiltersName1(), TextUtils.isEmpty(fixcondition.getSAppFilters1()) ? "" : fixcondition.getSAppFilters1()));
        }
        if (StringUtil.isNotEmpty(fixcondition.getSAppFiltersName2())) {
            fixconditions.add(new FormConditionBean(fixcondition.getSAppFiltersName2(), TextUtils.isEmpty(fixcondition.getSAppFilters2()) ? "" : fixcondition.getSAppFilters2()));
        }
        if (StringUtil.isNotEmpty(fixcondition.getSAppFiltersName3())) {
            fixconditions.add(new FormConditionBean(fixcondition.getSAppFiltersName3(), TextUtils.isEmpty(fixcondition.getSAppFilters3()) ? "" : fixcondition.getSAppFilters3()));
        }
        if (StringUtil.isNotEmpty(fixcondition.getSAppFiltersName4())) {
            fixconditions.add(new FormConditionBean(fixcondition.getSAppFiltersName4(), TextUtils.isEmpty(fixcondition.getSAppFilters4()) ? "" : fixcondition.getSAppFilters4()));
        }
    }

    private List<NetParams> getParams() {
        List<NetParams> params = new ArrayList<>();
        params.add(new NetParams("otype", "GetReportInfo"));
        params.add(new NetParams("iFormID", menuId));
        params.add(new NetParams("userid", userId));
        params.add(new NetParams("pageNo", pagerIndex + ""));
        return params;
    }


    /*请求关闭LoadingDialog和弹出提示*/
    private void loadFail(String message) {
        runOnUiThread(new Runnable() {
            @Override
            public void run() {
                LoadingDialog.cancelDialogForLoading();
                if (StringUtil.isNotEmpty(message))
                    Toasts.showShort(FormListActivity.this, message);
            }
        });
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, @Nullable Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        if (data != null) {
            if (resultCode == CONDIRION_CODE) {
                pagerIndex = 1;
                items.clear();
                if (formListAdapter != null) {
                    formListAdapter.notifyDataSetChanged();
                }
                filter = data.getStringExtra("data");
//                Log.e("filter", filter);
//                currenViewPos = -1;
//                currentView.setChecked(false);
//                currentView = null;
//                Log.e("filter", filter);
                getFormData(false, false, pagerIndex);
            }
        }
    }

    @OnClick({R.id.iv_back, R.id.tv_right})
    public void onViewClicked(View view) {
        switch (view.getId()) {
            case R.id.iv_back:
                finish();
                break;
            case R.id.tv_right:
                goCondition();
                break;
            default:
                break;
        }
    }

    /*筛选*/
    private void goCondition() {
        Intent intent = new Intent();
        intent.setClass(this, FormConditionActivity.class);
        intent.putExtra("isStore", isStore);
        intent.putExtra("data", new Gson().toJson(conditions));
        intent.putExtra("code", CONDIRION_CODE);
        startActivityForResult(intent, CONDIRION_CODE);
    }
}
