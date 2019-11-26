package com.yyy.fangzhi.pubilc;

import android.content.Intent;
import android.graphics.Color;
import android.os.Bundle;
import android.text.TextUtils;
import android.util.Log;
import android.view.Gravity;
import android.view.View;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;

import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.google.gson.Gson;
import com.google.gson.reflect.TypeToken;
import com.yyy.fangzhi.R;
import com.yyy.fangzhi.dialog.LoadingDialog;
import com.yyy.fangzhi.interfaces.OnItemClickListener;
import com.yyy.fangzhi.interfaces.ResponseListener;
import com.yyy.fangzhi.util.IntentUtil;
import com.yyy.fangzhi.util.ResultCode;
import com.yyy.fangzhi.util.SharedPreferencesHelper;
import com.yyy.fangzhi.util.StringUtil;
import com.yyy.fangzhi.util.Toasts;
import com.yyy.fangzhi.util.net.NetConfig;
import com.yyy.fangzhi.util.net.NetParams;
import com.yyy.fangzhi.util.net.NetUtil;
import com.yyy.fangzhi.util.net.Otypes;
import com.yyy.fangzhi.view.Configure.ConfigureInfo;
import com.yyy.fangzhi.view.recycle.RecyclerViewDivider;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.io.IOException;
import java.util.ArrayList;
import java.util.List;

import butterknife.BindView;
import butterknife.ButterKnife;
import butterknife.OnClick;

public class PublicListActivity extends AppCompatActivity {
    private static final String TAG = "PublicListActivity";
    @BindView(R.id.tv_title)
    TextView tvTitle;
    @BindView(R.id.iv_right)
    ImageView ivRight;
    @BindView(R.id.tv_right)
    TextView tvRight;
    @BindView(R.id.rv_bill)
    RecyclerView rvBill;

    String userid;
    String url;
    String address;
    String companyCode;

    int formid;
    String title;

    SharedPreferencesHelper preferencesHelper;

    List<ReportColumn2> reportColumns;
    List<PublicItem> datas;

    PublicAdapter adapter;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_public_list);
        ButterKnife.bind(this);
        preferencesHelper = new SharedPreferencesHelper(this, getString(R.string.preferenceCache));
        init();
    }

    private void init() {
        initList();
        getPreferenceData();
        getIntentData();
        initView();
        getData();
    }

    private void initList() {
        reportColumns = new ArrayList<>();
        datas = new ArrayList<>();
    }


    private void getPreferenceData() {
        userid = (String) preferencesHelper.getSharedPreference("userid", "");
        address = (String) preferencesHelper.getSharedPreference("address", "");
        companyCode = (String) preferencesHelper.getSharedPreference("companyCode", "");
        url = address + NetConfig.server + NetConfig.ReportHandler_Method;
        ;
    }

    private void getIntentData() {
        formid = getIntent().getIntExtra("formid", 0);
        title = getIntent().getStringExtra("title");
    }

    private void initView() {
        tvTitle.setText(title);
        initRecycle();
    }

    private void initRecycle() {
        rvBill.setLayoutManager(new LinearLayoutManager(this));
        rvBill.addItemDecoration(new RecyclerViewDivider(this, LinearLayout.VERTICAL));
    }

    private List<NetParams> getParams() {
        List<NetParams> params = new ArrayList<>();
        params.add(new NetParams("otype", Otypes.GetReportInfo));
        params.add(new NetParams("userid", userid));
        params.add(new NetParams("iFormID", formid + ""));
        params.add(new NetParams("database", companyCode));
        return params;
    }

    private void getData() {
        LoadingDialog.showDialogForLoading(this);
        new NetUtil(getParams(), url, new ResponseListener() {
            @Override
            public void onSuccess(String string) {
                try {
                    JSONObject jsonObject = new JSONObject(string);
                    if (jsonObject.optBoolean("success")) {
                        reportColumns.addAll(getInfo(jsonObject.optJSONObject("info").optString("ReportColumns2")));
                        initData(jsonObject.optJSONArray("data"));
                        LoadingFinish(null);
                        refreshList();
                    } else {
                        LoadingFinish(jsonObject.optString("message"));
                    }
                } catch (NullPointerException e) {
                    LoadingFinish(getString(R.string.empty_data));
                    e.printStackTrace();
                } catch (JSONException e) {
                    LoadingFinish(getString(R.string.error_json));
                    e.printStackTrace();
                } catch (Exception e) {
                    LoadingFinish(getString(R.string.error_data));
                    e.printStackTrace();
                }
            }

            @Override
            public void onFail(IOException e) {
                LoadingFinish(e.getMessage());
                e.printStackTrace();
            }
        });

    }


    private List<ReportColumn2> getInfo(String info) throws NullPointerException, Exception {
        List<ReportColumn2> list = new ArrayList<>();
        if (StringUtil.isNotEmpty(info)) {
            list.addAll(new Gson().fromJson(info, new TypeToken<List<ReportColumn2>>() {
            }.getType()));
        }
        return list;
    }

    private void initData(JSONArray data) throws Exception {
        for (int i = 0; i < data.length(); i++) {
            datas.add(getItemData(data.optJSONObject(i)));
        }
    }

    private PublicItem getItemData(JSONObject jsonObject) throws Exception {
        PublicItem item = new PublicItem();
        item.setId(jsonObject.optInt("iRecNo", 0));
        List<ConfigureInfo> list = new ArrayList<>();
        for (int i = 0; i < reportColumns.size(); i++) {
            ConfigureInfo info = new ConfigureInfo();
            ReportColumn2 column = reportColumns.get(i);
            info.setSingleLine(true);
            info.setWidthPercent(StringUtil.isPercent(column.getIProportion()));
            info.setRow(column.getISerial());
            info.setTitleSize(getInfoTitleSize(column.getSNameFontSize()));
            info.setTitle(column.getSFieldsDisplayName());
            info.setTitleBold((column.getINameFontBold() == 1) ? true : false);
            if (StringUtil.isColor(column.getSNameFontColor()))
                info.setTitleColor(Color.parseColor(column.getSNameFontColor()));
            info.setContentSize(getInfoContentSize(column.getSValueFontSize()));
            info.setContent(DataFormat.getData(jsonObject.optString(column.getSFieldsName()), column.getSFieldsType()));
            info.setContentBold((column.getIValueFontBold() == 1) ? true : false);
            if (StringUtil.isColor(column.getSValueFontColor()))
                info.setTitleColor(Color.parseColor(column.getSValueFontColor()));
            list.add(info);
        }
        item.setList(list);
        return item;
    }

    private int getInfoContentSize(String sValueFontSize) {
        if (StringUtil.isNotEmpty(sValueFontSize) && StringUtil.isInteger(sValueFontSize)) {
            return Integer.parseInt(sValueFontSize);
        } else {
            return 0;
        }
    }

    private int getInfoTitleSize(String sNameFontSize) {
        if (StringUtil.isNotEmpty(sNameFontSize) && StringUtil.isInteger(sNameFontSize)) {
            return Integer.parseInt(sNameFontSize);
        } else {
            return 0;
        }
    }

    private void refreshList() {
        runOnUiThread(new Runnable() {
            @Override
            public void run() {
                if (adapter == null) {
                    initAdapter();
                }
            }
        });
    }

    private void initAdapter() {
        adapter = new PublicAdapter(datas, this);
        adapter.setOnItemClickListener(new OnItemClickListener() {
            @Override
            public void onItemClick(View view, int position) {
                Intent intent = IntentUtil.getIntent(PublicListActivity.this, formid);
                if (intent != null) {
                    intent.putExtra("title", title);
                    intent.putExtra("formid", formid);
                    intent.putExtra("iRecNo", datas.get(position).getId());
                    intent.putExtra("position", position);
                    startActivityForResult(intent, 0);
                }
            }
        });
        rvBill.setAdapter(adapter);
    }

    @OnClick({R.id.iv_back, R.id.iv_right})
    public void onViewClicked(View view) {
        switch (view.getId()) {
            case R.id.iv_back:
                finish();
                break;
            case R.id.iv_right:
                Intent intent = IntentUtil.getIntent(PublicListActivity.this, formid);
                intent.putExtra("title", title);
                intent.putExtra("formid", formid);
                startActivityForResult(intent, 0);
                break;
        }
    }

    private void LoadingFinish(String msg) {
        runOnUiThread(new Runnable() {
            @Override
            public void run() {
                if (StringUtil.isNotEmpty(msg)) {
                    Toast(msg);
                }
                LoadingDialog.cancelDialogForLoading();

            }
        });
    }

    private void Toast(String msg) {
        Toasts.showShort(this, msg);
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, @Nullable Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        if (resultCode == ResultCode.DeleteCode) {
            if (data != null) {
                int position = data.getIntExtra("position", -1);
                if (position > -1 && datas.size() > 0) {
                    removeData(position);
                }
            }
        } else if (requestCode == ResultCode.RefreshCode) {
            datas.clear();
            adapter.notifyDataSetChanged();
            getData();
        }
    }

    private void removeData(int position) {
        datas.remove(position);
        adapter.notifyDataSetChanged();
    }
}
