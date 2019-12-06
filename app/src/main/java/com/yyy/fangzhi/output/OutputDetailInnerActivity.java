package com.yyy.fangzhi.output;

import android.content.Intent;
import android.graphics.Color;
import android.os.Bundle;
import android.util.Log;
import android.view.Gravity;
import android.view.View;
import android.view.ViewGroup;
import android.view.Window;
import android.view.WindowManager;
import android.widget.CompoundButton;
import android.widget.FrameLayout;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.Switch;
import android.widget.TextView;

import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.widget.NestedScrollView;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.google.gson.Gson;
import com.google.gson.reflect.TypeToken;
import com.yyy.fangzhi.R;
import com.yyy.fangzhi.dialog.JudgeDialog;
import com.yyy.fangzhi.dialog.LoadingDialog;
import com.yyy.fangzhi.exchange.ExchangeActivity;
import com.yyy.fangzhi.interfaces.OnClickListener2;
import com.yyy.fangzhi.interfaces.OnEntryListener;
import com.yyy.fangzhi.interfaces.OnItemClickListener;
import com.yyy.fangzhi.interfaces.ResponseListener;
import com.yyy.fangzhi.model.BarcodeColumn;
import com.yyy.fangzhi.model.Storage;
import com.yyy.fangzhi.pubilc.DataFormat;
import com.yyy.fangzhi.pubilc.PublicAdapter;
import com.yyy.fangzhi.pubilc.PublicItem;
import com.yyy.fangzhi.util.KeyBoardUtil;
import com.yyy.fangzhi.util.SharedPreferencesHelper;
import com.yyy.fangzhi.util.StringUtil;
import com.yyy.fangzhi.util.Toasts;
import com.yyy.fangzhi.util.net.NetConfig;
import com.yyy.fangzhi.util.net.NetParams;
import com.yyy.fangzhi.util.net.NetUtil;
import com.yyy.fangzhi.util.net.Otypes;
import com.yyy.fangzhi.view.Configure.ConfigureInfo;
import com.yyy.fangzhi.view.EditListenerView;
import com.yyy.fangzhi.view.TextItem;
import com.yyy.fangzhi.view.recycle.RecyclerViewDivider;
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

import static com.yyy.fangzhi.util.ResultCode.DeleteCode;
import static com.yyy.fangzhi.util.ResultCode.EditCode;
import static com.yyy.fangzhi.util.ResultCode.RefreshCode;

public class OutputDetailInnerActivity extends AppCompatActivity {

    @BindView(R.id.tv_title)
    TextView tvTitle;
    @BindView(R.id.iv_right)
    ImageView ivRight;
    @BindView(R.id.fl_empty)
    FrameLayout flEmpty;
    @BindView(R.id.it_storage)
    TextItem itStorage;
    @BindView(R.id.sv_red)
    Switch svRed;
    @BindView(R.id.it_date)
    TextItem itDate;
    @BindView(R.id.it_worker)
    TextItem itWorker;
    @BindView(R.id.it_qty)
    TextItem itQty;
    @BindView(R.id.it_num)
    TextItem itNum;
    @BindView(R.id.et_code)
    EditListenerView etCode;
    @BindView(R.id.rv_item)
    RecyclerView rvItem;
    @BindView(R.id.scroll)
    NestedScrollView scroll;
    @BindView(R.id.tv_delete)
    TextView tvDelete;
    @BindView(R.id.bottom_layout)
    LinearLayout bottomLayout;

    private String userid;
    private String url;
    private String address;
    private String companyCode;
    private String storageName;
    private String date;
    private String dbType;
    private String workerName;
    private String title;

    private int formid = 0;
    private int iRed = 0;
    private int iRecNo;
    private int storageId = 0;
    int pos;

    private boolean canDelete;

    private List<BarcodeColumn> barcodeColumns;
    private List<PublicItem> datas;
    private List<String> codes;
    List<Storage> storages;

    private JudgeDialog deleteDialog;
    private JudgeDialog clearDialog;
    private JudgeDialog submitDialog;
    private JudgeDialog removeDialog;

    private PublicAdapter adapter;

    private SharedPreferencesHelper preferencesHelper;
    private OptionsPickerView pvStorage;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_output_detail_inner);
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
        storages = new ArrayList<>();
        barcodeColumns = new ArrayList<>();
        datas = new ArrayList<>();
        codes = new ArrayList<>();
    }

    private void getPreferenceData() {
        userid = (String) preferencesHelper.getSharedPreference("userid", "");
        address = (String) preferencesHelper.getSharedPreference("address", "");
        companyCode = (String) preferencesHelper.getSharedPreference("companyCode", "");
        url = address + NetConfig.server + NetConfig.PDAHandler;
        workerName = (String) preferencesHelper.getSharedPreference("userName", "");
    }

    private void getIntentData() {
        title = getIntent().getStringExtra("title");
        formid = getIntent().getIntExtra("formid", 0);
        iRecNo = getIntent().getIntExtra("iRecNo", 0);
        pos = getIntent().getIntExtra("position", -1);
        dbType = iRecNo == 0 ? "add" : "modify";
    }

    private void initView() {
        ivRight.setVisibility(View.GONE);
        bottomLayout.setVisibility(View.GONE);
        tvTitle.setText(title);
        initRecycle();
        setCodeListener();
        setClickListener();
    }

    private void setClickListener() {
        itStorage.setContentPadding();
        itStorage.setContentBlack();
        itStorage.setOnItemClickListener(new OnClickListener2() {
            @Override
            public void onItemClick(View view, int position) {
                if (storages.size() == 0) {
                    getStorageData();
                } else {
                    pvStorage.show();
                }
            }
        });
    }

    private void initRecycle() {
        rvItem.setLayoutManager(new LinearLayoutManager(this));
        rvItem.addItemDecoration(new RecyclerViewDivider(this, LinearLayout.VERTICAL));
    }

    private void setCodeListener() {
        etCode.setOnEntryListener(new OnEntryListener() {
            @Override
            public void onEntry(View view) {
                KeyBoardUtil.hideInput(OutputDetailInnerActivity.this);
                String code = etCode.getText().toString();
                etCode.setText("");
                if (codes.contains(code)) {
                    Toast(getString(R.string.repeat_code));
                    return;
                }
                if (StringUtil.isNotEmpty(code)) {
                    getCodeData(code);
                }
            }
        });
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
                        storages.addAll(new Gson().fromJson(initStorageData(jsonObject.optJSONArray("tables")), new TypeToken<List<Storage>>() {
                        }.getType()));
                        LoadingFinish(null);
                        initStoragePickOut();

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


    private String initStorageData(JSONArray jsonArray) throws NullPointerException, Exception {
        return jsonArray.optString(0);
    }

    private void initStoragePickOut() {
        if (storages.size() > 0) {
            runOnUiThread(new Runnable() {
                @Override
                public void run() {
                    pvStorage = new OptionsPickerBuilder(OutputDetailInnerActivity.this, new OnOptionsSelectListener() {
                        @Override
                        public void onOptionsSelect(int options1, int options2, int options3, View v) {
                            if (storageId != storages.get(options1).getIBscDataStockMRecNo()) {
                                storageId = storages.get(options1).getIBscDataStockMRecNo();
                                itStorage.setContent(storages.get(options1).getSStockName());
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

    private List<NetParams> getCodeParams(String s) {
        List<NetParams> params = new ArrayList<>();
        params.add(new NetParams("otype", Otypes.GetPDAMMStockProductOutBarCode));
        params.add(new NetParams("userid", userid));
        params.add(new NetParams("database", companyCode));
        params.add(new NetParams("sBarCode", s));
        params.add(new NetParams("iBscDataStockMRecNo", storageId + ""));
        params.add(new NetParams("iRed", 0 + ""));
        return params;
    }

    private void getCodeData(String code) {
        new NetUtil(getCodeParams(code), url, new ResponseListener() {
            @Override
            public void onSuccess(String string) {
                try {
                    JSONObject jsonObject = new JSONObject(string);
                    if (jsonObject.optBoolean("success")) {

                        List<PublicItem> list = initBarcodeData(removeRepeat(jsonObject.optJSONArray("data")));
                        if (list != null && list.size() > 0) {
                            datas.addAll(0, list);
                            refreshList();
                        } else {
                            LoadingFinish(getString(R.string.empty_code));
                        }
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

    private List<NetParams> getParams() {
        List<NetParams> params = new ArrayList<>();
        params.add(new NetParams("iFormID", formid + ""));
        params.add(new NetParams("userid", userid));
        params.add(new NetParams("database", companyCode));
        params.add(new NetParams("otype", Otypes.GetFormInfo));
        params.add(new NetParams("key", iRecNo + ""));
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
                        if (iRecNo == 0) {
                            iRecNo = jsonObject.optInt("key", 0);
                            canDelete = false;
                            setViewData();
                        } else {
                            JSONObject data = jsonObject.optJSONObject("data");
//                            Log.d("mainData", data.optJSONArray("mainData").optString(0));
                            setMainData(data.optJSONArray("mainData").optJSONObject(0));
                            setChildData(data.optJSONArray("childData"));
                        }
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

    @OnClick({R.id.iv_back, R.id.tv_empty, R.id.tv_clear, R.id.tv_delete, R.id.tv_save, R.id.tv_submit, R.id.iv_storage})
    public void onViewClicked(View view) {
        switch (view.getId()) {
            case R.id.iv_back:
                finish();
                break;
            case R.id.tv_empty:
                getData();
                break;
            case R.id.tv_clear:
                isClear();
                break;
            case R.id.tv_delete:
                isDelete();
                break;
            case R.id.tv_save:
                save(false);
                break;
            case R.id.tv_submit:
                isSubmit();
                break;
            case R.id.iv_storage:
                if (storages.size() == 0) {
                    getStorageData();
                } else {
                    pvStorage.show();
                }
                break;
            default:
                break;
        }
    }

    @Override
    public void onActivityResult(int requestCode, int resultCode, @Nullable Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        if (data != null) {
            initResult(data, resultCode);
        }
    }

    private void initResult(Intent data, int resultCode) {
        switch (resultCode) {
            case EditCode:
                initResultEdit(data);
                break;
            default:
                break;
        }
    }

    private void isClear() {
        if (clearDialog == null) {
            clearDialog = new JudgeDialog(this, R.style.JudgeDialog, "是否清空条码？", new JudgeDialog.OnCloseListener() {
                @Override
                public void onClick(boolean confirm) {
                    if (confirm)
                        clear();
                }
            });
        }
        clearDialog.show();
    }

    private void clear() {
        if (adapter != null) {
            datas.clear();
            codes.clear();
            adapter.notifyDataSetChanged();
        }
    }

    private void isRemove(int position) {
        if (removeDialog == null) {
            removeDialog = new JudgeDialog(this, R.style.JudgeDialog, "是否删除此条码？");
        }
        removeDialog.setOnCloseListener(new JudgeDialog.OnCloseListener() {
            @Override
            public void onClick(boolean confirm) {
                if (confirm) {
                    codes.remove(position);
                    datas.remove(position);
                    if (codes.size() == 0) {
                        isEmpty(true);
                    }
                    adapter.notifyDataSetChanged();
                }
            }
        });
        removeDialog.show();
    }

    private void isDelete() {
        if (deleteDialog == null) {
            deleteDialog = new JudgeDialog(this, R.style.JudgeDialog, "是否删除？", new JudgeDialog.OnCloseListener() {
                @Override
                public void onClick(boolean confirm) {
                    if (confirm)
                        delete();
                }
            });
        }
        deleteDialog.show();
    }

    private void isSubmit() {
        if (submitDialog == null) {
            submitDialog = new JudgeDialog(this, R.style.JudgeDialog, "是否提交？", new JudgeDialog.OnCloseListener() {
                @Override
                public void onClick(boolean confirm) {
                    if (confirm)
                        save(true);
                }
            });
        }
        submitDialog.show();
    }

    private List<NetParams> deteleParams() {
        List<NetParams> params = new ArrayList<>();
        params.add(new NetParams("otype", Otypes.MMStockProductOutMDelete));
        params.add(new NetParams("userid", userid));
        params.add(new NetParams("database", companyCode));
        params.add(new NetParams("iRecNo", iRecNo + ""));
        return params;
    }

    private void delete() {
        LoadingDialog.showDialogForLoading(this);
        new NetUtil(deteleParams(), url, new ResponseListener() {
            @Override
            public void onSuccess(String string) {
                try {
                    JSONObject jsonObject = new JSONObject(string);
                    if (jsonObject.optBoolean("success")) {
                        LoadingFinish(null);
                        runOnUiThread(new Runnable() {
                            @Override
                            public void run() {
                                setResult(DeleteCode, new Intent().putExtra("position", pos));
                                finish();
                            }
                        });
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

    private List<NetParams> saveParams() {
        List<NetParams> params = new ArrayList<>();
        params.add(new NetParams("otype", Otypes.MMStockProductOutMSave));
        params.add(new NetParams("userid", userid));
        params.add(new NetParams("database", companyCode));
        params.add(new NetParams("iBscDataStockMRecNo", storageId + ""));
        params.add(new NetParams("iRed", iRed + ""));
        params.add(new NetParams("iMMStockProductOutMRecNo", iRecNo + ""));
        params.add(new NetParams("sSaveType", dbType));
        params.add(new NetParams("iBillType", "4"));
        params.add(new NetParams("sBarCodes", getBarcode()));
        return params;
    }

    private String getBarcode() {
        String codes = "";
        for (PublicItem code : datas) {
            codes = codes + code.getOutCode().toString();
        }
        Log.d("codes", codes);
        return codes;
    }

    private void save(boolean submit) {
        if (codes.size() == 0) {
            Toast("条码不能为空");
            return;
        }
        LoadingDialog.showDialogForLoading(this);
        new NetUtil(saveParams(), url, new ResponseListener() {
            @Override
            public void onSuccess(String string) {
                try {
                    JSONObject jsonObject = new JSONObject(string);
                    LoadingFinish(null);
                    if (jsonObject.optBoolean("success")) {
                        if (submit) {
                            submit();
                        } else {
                            runOnUiThread(new Runnable() {
                                @Override
                                public void run() {
                                    setResult(RefreshCode);
                                    finish();
                                }
                            });
                        }
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

    private List<NetParams> submitParams() {
        List<NetParams> params = new ArrayList<>();
        params.add(new NetParams("otype", Otypes.MMStockProductOutMSubmit));
        params.add(new NetParams("userid", userid));
        params.add(new NetParams("database", companyCode));
        params.add(new NetParams("iRecNo", iRecNo + ""));
        return params;
    }

    private void submit() {
        runOnUiThread(new Runnable() {
            @Override
            public void run() {
                LoadingFinish(null);
                LoadingDialog.showDialogForLoading(OutputDetailInnerActivity.this);
            }
        });
        new NetUtil(submitParams(), url, new ResponseListener() {
            @Override
            public void onSuccess(String string) {
                try {
                    JSONObject jsonObject = new JSONObject(string);
                    if (jsonObject.optBoolean("success")) {
                        LoadingFinish(null);
                        runOnUiThread(new Runnable() {
                            @Override
                            public void run() {
                                setResult(DeleteCode, new Intent().putExtra("position", pos));
                                finish();
                            }
                        });

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

    private void initResultEdit(Intent data) {
        int pos = data.getIntExtra("pos", 0);
        PublicItem.OutCode code = new Gson().fromJson(data.getStringExtra("code"), PublicItem.OutCode.class);
        datas.get(pos).getOutCode().setTray(code.getTray());
        datas.get(pos).getOutCode().setOutQty(code.getOutQty());
        datas.get(pos).getOutCode().setFlawQty(code.getFlawQty());
        datas.get(pos).setQty(code.getOutQty());
        datas.get(pos).setFlawQty(code.getFlawQty());
        datas.get(pos).setTray(code.getTray());
        refreshList();
    }

    private void refreshList() {
        runOnUiThread(new Runnable() {
            @Override
            public void run() {
                if (adapter == null) {
                    adapter = new PublicAdapter(datas, OutputDetailInnerActivity.this);
                    adapter.setOnItemClickListener(new OnItemClickListener() {
                        @Override
                        public void onItemClick(View view, int position) {
                            editCode(position);
                        }
                    });
                    rvItem.setAdapter(adapter);
                } else {
                    adapter.notifyDataSetChanged();
                }
            }
        });

    }

    private void editCode(int position) {
        Intent intent = new Intent();
        intent.putExtra("pos", position);
        intent.putExtra("data", new Gson().toJson(datas.get(position).getOutCode()));
        intent.setClass(this, OutputEditDialog.class);
        startActivityForResult(intent, 1);
    }

    private void initBarcodeColumnsData(String data) {
        if (StringUtil.isNotEmpty(data)) {
            barcodeColumns.addAll(new Gson().fromJson(data, new TypeToken<List<BarcodeColumn>>() {
            }.getType()));
        }
    }

    private void setMainData(JSONObject mainData) throws NullPointerException {
        storageId = mainData.optInt("iBscDataStockMRecNo", 0);
        storageName = mainData.optString("sStockName");
        iRecNo = mainData.optInt("iRecNo", 0);
        iRed = mainData.optInt("iRed", 0);
        workerName = mainData.optString("sInputUserName");
        date = mainData.optString("dDate").replace("T", " ");
        canDelete = true;
        runOnUiThread(new Runnable() {
            @Override
            public void run() {
                setViewData();
            }
        });

    }

    private void setViewData() {
        runOnUiThread(new Runnable() {
            @Override
            public void run() {
                itStorage.setContent(storageName).setContentColor(getResources().getColor(R.color.default_content_color)).setTitle("仓库：");
                itDate.setContent(date).setContentColor(getResources().getColor(R.color.default_content_color)).setTitle("日期：");
                itWorker.setContent(workerName).setContentColor(getResources().getColor(R.color.default_content_color)).setTitle("下单：");
                if (!canDelete)
                    tvDelete.setVisibility(View.INVISIBLE);
            }
        });
    }

    private void showView() {
        runOnUiThread(new Runnable() {
            @Override
            public void run() {
                scroll.setVisibility(View.VISIBLE);
                rvItem.setVisibility(View.VISIBLE);
                bottomLayout.setVisibility(View.VISIBLE);
                flEmpty.setVisibility(View.GONE);
            }
        });
    }

    private void isEmpty(boolean empty) {
        svRed.setClickable(empty);
    }

    private void setChildData(JSONArray childData) throws
            NullPointerException, JSONException, Exception {
        for (int i = 0; i < childData.length(); i++) {
            codes.add(childData.getJSONObject(i).optString("sBarCode"));
        }
        datas.addAll(initBarcodeData(childData));
        refreshList();
    }

    private JSONArray removeRepeat(JSONArray data) throws NullPointerException, JSONException, Exception {
        JSONArray array = new JSONArray();
        for (int i = 0; i < data.length(); i++) {
            if (!codes.contains(data.optJSONObject(i).optString("sBarCode"))) {
                array.put(data.optJSONObject(i));
                codes.add(data.optJSONObject(i).optString("sBarCode"));
                isEmpty(false);
            }
        }
        return array;
    }

    private List<PublicItem> initBarcodeData(JSONArray jsonArray) throws
            JSONException, NullPointerException, Exception {
        List<PublicItem> list = new ArrayList<>();
        for (int i = 0; i < jsonArray.length(); i++) {
            list.add(getBarcodeItem(jsonArray.optJSONObject(i)));
        }
        return list;
    }

    private PublicItem getBarcodeItem(JSONObject jsonObject) throws
            JSONException, NullPointerException, Exception {
        PublicItem item = new PublicItem();
        item.setCode(jsonObject.optString("sBarCode"));
        item.setOutCode(getOutCode(jsonObject));
        List<ConfigureInfo> list = new ArrayList<>();
//        for (BarcodeColumn column : barcodeColumns) {
        for (int i = 0; i < barcodeColumns.size(); i++) {
            BarcodeColumn column = barcodeColumns.get(i);
            if (column.getSFieldsName().equals("sTrayCode"))
                item.setTrayPos(i);
            if (column.getSFieldsName().equals("fQty"))
                item.setQtyPos(i);
            if (column.getSFieldsName().equals("fFlawQty"))
                item.setQtyFlawPos(i);
            if (column.getIHide() == 0) {
                ConfigureInfo info = new ConfigureInfo();
                info.setSingleLine(true);
                info.setWidthPercent(StringUtil.isPercent(column.getIProportion()));
                info.setRow(column.getIRowNum());
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
        }
        item.setList(list);
        item.setId(jsonObject.optInt("iRecNo", 0));

        return item;
    }

    private PublicItem.OutCode getOutCode(JSONObject jsonObject) throws NullPointerException, Exception {
        PublicItem.OutCode code = new PublicItem.OutCode();
        code.setCode(jsonObject.optString("sBarCode"));
        code.setTray(jsonObject.optString("sTrayCode"));
        code.setNotice(jsonObject.optInt("iSDSendDRecNo", 0) + "");
        code.setFlawQty(jsonObject.optDouble("fFlawQty"));
        code.setOutQty(jsonObject.optDouble("fQty"));
        return code;
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
