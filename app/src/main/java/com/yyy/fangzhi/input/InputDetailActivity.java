package com.yyy.fangzhi.input;

import android.graphics.Color;
import android.os.Bundle;
import android.util.Log;
import android.view.Gravity;
import android.view.View;
import android.view.ViewGroup;
import android.view.Window;
import android.view.WindowManager;
import android.widget.EditText;
import android.widget.FrameLayout;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;

import androidx.appcompat.app.AppCompatActivity;
import androidx.recyclerview.widget.RecyclerView;

import com.google.gson.Gson;
import com.google.gson.reflect.TypeToken;
import com.yyy.fangzhi.R;
import com.yyy.fangzhi.dialog.LoadingDialog;
import com.yyy.fangzhi.interfaces.OnEntryListener;
import com.yyy.fangzhi.interfaces.ResponseListener;
import com.yyy.fangzhi.model.Storage;
import com.yyy.fangzhi.util.SharedPreferencesHelper;
import com.yyy.fangzhi.util.StringUtil;
import com.yyy.fangzhi.util.Toasts;
import com.yyy.fangzhi.util.net.NetConfig;
import com.yyy.fangzhi.util.net.NetParams;
import com.yyy.fangzhi.util.net.NetUtil;
import com.yyy.fangzhi.util.net.Otypes;
import com.yyy.fangzhi.view.EditListenerView;
import com.yyy.yyylibrary.pick.builder.OptionsPickerBuilder;
import com.yyy.yyylibrary.pick.listener.OnOptionsSelectListener;
import com.yyy.yyylibrary.pick.view.OptionsPickerView;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.io.IOException;
import java.util.ArrayList;
import java.util.List;

import butterknife.BindView;
import butterknife.ButterKnife;
import butterknife.OnClick;

public class InputDetailActivity extends AppCompatActivity {
    private static final String TAG = "InputDetailActivity";
    @BindView(R.id.tv_title)
    TextView tvTitle;
    @BindView(R.id.iv_right)
    ImageView ivRight;
    @BindView(R.id.tv_storage)
    TextView tvStorage;
    @BindView(R.id.et_berch)
    EditListenerView etBerch;
    @BindView(R.id.iv_berch)
    ImageView ivBerch;
    @BindView(R.id.et_tray)
    EditListenerView etTray;
    @BindView(R.id.et_code)
    EditListenerView etCode;
    @BindView(R.id.rv_item)
    RecyclerView rvItem;
    @BindView(R.id.fl_empty)
    FrameLayout flEmpty;
    @BindView(R.id.tv_empty)
    TextView tvEmpty;
    @BindView(R.id.ll_storage)
    LinearLayout llStorage;
    @BindView(R.id.bottom_layout)
    LinearLayout bottomLayout;

    String userid;
    String url;
    String address;
    String companyCode;

    int formid;
    int berchId;
    int iRecNo;
    int orderNo = 0;

    String title;
    String storageId;
    String dbType;

    List<Storage> storages;
    List<Storage.BerCh> berChes;
    List<BarcodeColumn> barcodeColumns;

    SharedPreferencesHelper preferencesHelper;

    private OptionsPickerView pvStorage;
    private OptionsPickerView pvBerch;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_input_detail);
        ButterKnife.bind(this);
        preferencesHelper = new SharedPreferencesHelper(this, getString(R.string.preferenceCache));
        init();
        getData();
    }


    private void init() {
        initList();
        getPreferenceData();
        getIntentData();
        initView();
    }

    private void initList() {
        storages = new ArrayList<>();
        berChes = new ArrayList<>();
        barcodeColumns = new ArrayList<>();
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
        iRecNo = getIntent().getIntExtra("iRecNo", 0);
        dbType = iRecNo == 0 ? "add" : "modify";
    }

    private void initView() {
        ivRight.setVisibility(View.GONE);
        tvTitle.setText(title);
        bottomLayout.setVisibility(View.GONE);
        setBerchListener();
        setCodeListener();
    }

    private void setBerchListener() {
        etBerch.setOnEntryListener(new OnEntryListener() {
            @Override
            public void onEntry(View view) {
                if (StringUtil.isNotEmpty(etBerch.getText().toString())) {
                    if (getBerch(etBerch.getText().toString()) != 0) {
                        berchId = getBerch(etBerch.getText().toString());
                    } else {
                        Toast(getString(R.string.error_berch));
                        etBerch.setText("");
                    }
                }
            }
        });
    }

    private int getBerch(String s) {
        for (Storage.BerCh item : berChes) {
            if (s.equals(item.getName())) {
                return item.getId();
            }
        }
        return 0;
    }


    private void setCodeListener() {
        etCode.setOnEntryListener(new OnEntryListener() {
            @Override
            public void onEntry(View view) {
                if (StringUtil.isNotEmpty(etCode.getText().toString()))
                    getCodeData(etCode.getText().toString());
            }
        });
    }


    private List<NetParams> getParams() {
        List<NetParams> params = new ArrayList<>();
        params.add(new NetParams("iFormID", formid + ""));
        params.add(new NetParams("userid", userid));
        params.add(new NetParams("database", companyCode));
        params.add(new NetParams("otype", Otypes.GetFormInfo));
        return params;
    }

    private void getData() {
        LoadingDialog.showDialogForLoading(this);
        new NetUtil(getParams(), address + NetConfig.server + NetConfig.MobileFormHandler, new ResponseListener() {
            @Override
            public void onSuccess(String string) {
                try {
                    JSONObject jsonObject = new JSONObject(string);
                    if (jsonObject.optBoolean("success")) {
                        initBarcodeColumnsData(jsonObject.optJSONObject("info").optString("formColumns"));
                        iRecNo = iRecNo == 0 ? jsonObject.optInt("key", 0) : iRecNo;
                        LoadingFinish(null);
                        showView();
                    } else {
                        LoadingFinish(jsonObject.optString("message"));
                    }
                } catch (JSONException e) {
                    e.printStackTrace();
                    LoadingFinish(getString(R.string.error_json));
                } catch (NullPointerException e) {
                    e.printStackTrace();
                    LoadingFinish(getString(R.string.empty_data));
                } catch (Exception e) {
                    e.printStackTrace();
                    LoadingFinish(getString(R.string.error_data));
                }
            }

            @Override
            public void onFail(IOException e) {
                e.printStackTrace();
                LoadingFinish(e.getMessage());
            }
        });
    }


    private void initBarcodeColumnsData(String data) {
        if (StringUtil.isNotEmpty(data)) {
            barcodeColumns.addAll(new Gson().fromJson(data, new TypeToken<List<BarcodeColumn>>() {
            }.getType()));
        }
    }

    private void showView() {
        runOnUiThread(new Runnable() {
            @Override
            public void run() {
                flEmpty.setVisibility(View.GONE);
                llStorage.setVisibility(View.VISIBLE);
                etCode.setVisibility(View.VISIBLE);
                etTray.setVisibility(View.VISIBLE);
                rvItem.setVisibility(View.VISIBLE);
                bottomLayout.setVisibility(View.VISIBLE);
            }
        });
    }

    private List<NetParams> getCodeParams(String s) {
        List<NetParams> params = new ArrayList<>();

        return params;
    }

    private void getCodeData(String s) {

    }

    @OnClick({R.id.iv_back, R.id.tv_storage, R.id.iv_storage, R.id.iv_berch, R.id.tv_empty})
    public void onViewClicked(View view) {
        switch (view.getId()) {
            case R.id.iv_back:
                finish();
                break;
            case R.id.tv_storage:
            case R.id.iv_storage:
                selectStorage();
                break;
            case R.id.iv_berch:
                pvBerch.show();
                break;
            case R.id.tv_empty:
                getData();
                break;
        }
    }

    private void selectStorage() {
        if (storages.size() == 0) {
            getStorageData();
        } else {
            pvStorage.show();
        }
    }

    private List<NetParams> getStorageParams() {
        List<NetParams> params = new ArrayList<>();
        params.add(new NetParams("otype", Otypes.GetStockMD));
        params.add(new NetParams("userid", userid));
        params.add(new NetParams("database", companyCode));
        return params;
    }

    private void getStorageData() {
        LoadingDialog.showDialogForLoading(this);
        new NetUtil(getStorageParams(), url, new ResponseListener() {
            @Override
            public void onSuccess(String string) {
                try {
                    JSONObject jsonObject = new JSONObject(string);
                    if (jsonObject.optBoolean("success")) {
                        storages.addAll(new Gson().fromJson(initData(jsonObject.optJSONArray("tables")), new TypeToken<List<Storage>>() {
                        }.getType()));
                        LoadingFinish(null);
                        initStoragePick();
                    } else {
                        LoadingFinish(jsonObject.optString("message"));
                    }
                } catch (JSONException e) {
                    e.printStackTrace();
                    LoadingFinish(getString(R.string.error_json));
                } catch (NullPointerException e) {
                    e.printStackTrace();
                    LoadingFinish(getString(R.string.empty_data));
                } catch (Exception e) {
                    e.printStackTrace();
                    LoadingFinish(getString(R.string.error_data));
                }

            }

            @Override
            public void onFail(IOException e) {
                e.printStackTrace();
                LoadingFinish(e.getMessage());
            }
        });
    }

    private void initStoragePick() {
        if (storages.size() > 0) {
            runOnUiThread(new Runnable() {
                @Override
                public void run() {
                    pvStorage = new OptionsPickerBuilder(InputDetailActivity.this, new OnOptionsSelectListener() {
                        @Override
                        public void onOptionsSelect(int options1, int options2, int options3, View v) {
                            if (storageId != storages.get(options1).getIBscDataStockDRecNo()) {
                                storageId = storages.get(options1).getIBscDataStockDRecNo();
                                tvStorage.setText(storages.get(options1).getSStockName());
                                setBerch(storages.get(options1).getBerChes());
                            }
                        }
                    })
                            .setTitleText("仓库选择")
                            .setContentTextSize(18)//设置滚轮文字大小
                            .setDividerColor(Color.LTGRAY)//设置分割线的颜色
                            .setSelectOptions(0)//默认选中项
                            .isRestoreItem(true)//切换时是否还原，设置默认选中第一项。
                            .isCenterLabel(false) //是否只显示中间选中项的label文字，false则每项item全部都带有label。
                            .setLabels("", "", "")
                            .isDialog(true)
                            .setBgColor(0xFFFFFFFF) //设置外部遮罩颜色
                            .build();
                    pvStorage.setPicker(storages);//一级选择器
                    setDialog(pvStorage);
                    pvStorage.show();
                }
            });
        }
    }

    private void setBerch(List<Storage.BerCh> berChes) {
        clearBerch();
        if (berChes.size() > 0) {
            this.berChes.addAll(berChes);
            initBerchPick();
            etBerch.setVisibility(View.VISIBLE);
            ivBerch.setVisibility(View.VISIBLE);
        } else {
            etBerch.setVisibility(View.GONE);
            ivBerch.setVisibility(View.GONE);
        }
    }

    private void initBerchPick() {
        pvBerch = new OptionsPickerBuilder(InputDetailActivity.this, new OnOptionsSelectListener() {
            @Override
            public void onOptionsSelect(int options1, int options2, int options3, View v) {
                if (berchId != berChes.get(options1).getId()) {
                    berchId = berChes.get(options1).getId();
                    etBerch.setText(berChes.get(options1).getName());
                }
            }
        })
                .setTitleText("仓位选择")
                .setContentTextSize(18)//设置滚轮文字大小
                .setDividerColor(Color.LTGRAY)//设置分割线的颜色
                .setSelectOptions(0)//默认选中项
                .isRestoreItem(true)//切换时是否还原，设置默认选中第一项。
                .isCenterLabel(false) //是否只显示中间选中项的label文字，false则每项item全部都带有label。
                .setLabels("", "", "")
                .isDialog(true)
                .setBgColor(0xFFFFFFFF) //设置外部遮罩颜色
                .build();
        pvBerch.setPicker(berChes);//一级选择器
        setDialog(pvBerch);
    }

    private void clearBerch() {
        etBerch.setText("");
        berchId = 0;
        berChes.clear();
        pvBerch = null;
    }

    private String initData(JSONArray jsonArray) throws NullPointerException, Exception {
        return jsonArray.optString(0);
    }

    private void setDialog(OptionsPickerView pickview) {
        getDialogLayoutParams();
        pickview.getDialogContainerLayout().setLayoutParams(getDialogLayoutParams());
        initDialogWindow(pickview.getDialog().getWindow());
    }

    private void initDialogWindow(Window window) {
        window.setWindowAnimations(R.style.picker_view_slide_anim);//修改动画样式
        window.setGravity(Gravity.BOTTOM);//改成Bottom,底部显示
        window.setDimAmount(0.1f);
        window.setAttributes(getDialogWindowLayoutParams(window));
    }

    private WindowManager.LayoutParams getDialogWindowLayoutParams(Window window) {
        WindowManager.LayoutParams winParams;
        winParams = window.getAttributes();
        winParams.width = WindowManager.LayoutParams.MATCH_PARENT;
        return winParams;
    }

    private FrameLayout.LayoutParams getDialogLayoutParams() {
        FrameLayout.LayoutParams params = new FrameLayout.LayoutParams(
                ViewGroup.LayoutParams.MATCH_PARENT,
                ViewGroup.LayoutParams.WRAP_CONTENT,
                Gravity.BOTTOM);
        params.leftMargin = 0;
        params.rightMargin = 0;
        return params;
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
}
