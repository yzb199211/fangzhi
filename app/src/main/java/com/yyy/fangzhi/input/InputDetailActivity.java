package com.yyy.fangzhi.input;

import android.content.Intent;
import android.graphics.Color;
import android.os.Bundle;
import android.view.Gravity;
import android.view.View;
import android.view.ViewGroup;
import android.view.Window;
import android.view.WindowManager;
import android.widget.FrameLayout;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;

import androidx.appcompat.app.AppCompatActivity;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.google.gson.Gson;
import com.google.gson.reflect.TypeToken;
import com.yyy.fangzhi.R;
import com.yyy.fangzhi.dialog.JudgeDialog;
import com.yyy.fangzhi.dialog.LoadingDialog;
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
import static com.yyy.fangzhi.util.ResultCode.RefreshCode;

public class InputDetailActivity extends AppCompatActivity {
    private static final String TAG = "InputDetailActivity";
    @BindView(R.id.tv_title)
    TextView tvTitle;
    @BindView(R.id.iv_right)
    ImageView ivRight;
    @BindView(R.id.tv_storage)
    TextView tvStorage;
    @BindView(R.id.tv_berch)
    TextView tvBerch;
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
    @BindView(R.id.tv_delete)
    TextView tvDelete;
    @BindView(R.id.tv_clear)
    TextView tvClear;
    @BindView(R.id.ll_two)
    LinearLayout llTwo;
    @BindView(R.id.ll_three)
    LinearLayout llThree;
    @BindView(R.id.it_qty)
    TextItem tiQty;
    @BindView(R.id.it_num)
    TextItem tiNum;

    String userid;
    String url;
    String address;
    String companyCode;

    int formid;
    int berchId;
    int iRecNo;
    int orderNo = 0;
    int position;
    int totalNum;

    double totalLength;

    String title;
    int storageId;
    String dbType;

    List<Storage> storages;
    List<Storage.BerCh> berChes;
    List<BarcodeColumn> barcodeColumns;
    List<PublicItem> datas;
    List<String> codes;

    SharedPreferencesHelper preferencesHelper;

    private OptionsPickerView pvStorage;
    private OptionsPickerView pvBerch;

    private PublicAdapter adapter;

    private JudgeDialog deleteDialog;
    private JudgeDialog clearDialog;
    private JudgeDialog submitDialog;
    private JudgeDialog removeDialog;

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
        datas = new ArrayList<>();
        codes = new ArrayList<>();
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
        position = getIntent().getIntExtra("position", -1);
        dbType = iRecNo == 0 ? "add" : "modify";
    }

    private void initView() {
        initVisiable();
        initViewState();
        initRecycle();
        setBerchListener();
        setCodeListener();
    }

    private void initVisiable() {
        if (iRecNo == 0) {
            tvDelete.setVisibility(View.INVISIBLE);
        }
        ivRight.setVisibility(View.GONE);
        bottomLayout.setVisibility(View.GONE);
    }

    private void initViewState() {
        tvTitle.setText(title);
        tiQty.setTitleMargin(0, 0, 0, 0).setTitle("总米数：");
        tiNum.setTitleMargin(0, 0, 0, 0).setTitle("总卷数：");
    }

    private void initRecycle() {
        rvItem.setLayoutManager(new LinearLayoutManager(this));
        rvItem.addItemDecoration(new RecyclerViewDivider(this, LinearLayout.VERTICAL));
    }

    private void setBerchListener() {
        etBerch.setOnEntryListener(new OnEntryListener() {
            @Override
            public void onEntry(View view) {
                KeyBoardUtil.hideInput(InputDetailActivity.this);
                etBerch.setText("");
                if (StringUtil.isNotEmpty(etBerch.getText().toString())) {
                    if (getBerch(etBerch.getText().toString()) != 0) {
                        berchId = getBerch(etBerch.getText().toString());
                    } else {
                        Toast(getString(R.string.error_berch));
                    }
                }
            }
        });

    }

    private int getBerch(String s) {
        for (Storage.BerCh item : berChes) {
            if (s.equals(item.getName())) {
                tvBerch.setText(item.getName());
                return item.getId();
            }
        }
        return 0;
    }


    private void setCodeListener() {
        etCode.setOnEntryListener(new OnEntryListener() {
            @Override
            public void onEntry(View view) {
                KeyBoardUtil.hideInput(InputDetailActivity.this);
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
                        } else {
                            JSONObject data = jsonObject.optJSONObject("data");
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

    private void setMainData(JSONObject mainData) throws NullPointerException {
        storageId = mainData.optInt("iBscDataStockMRecNo", 0);
        runOnUiThread(new Runnable() {
            @Override
            public void run() {
                tvStorage.setText(mainData.optString("sStockName"));
                etTray.setText(mainData.optString("sTrayCode"));
            }
        });

    }

    private void setChildData(JSONArray childData) throws NullPointerException, JSONException, Exception {
        for (int i = 0; i < childData.length(); i++) {
            codes.add(childData.getJSONObject(i).optString("sBarCode"));
            if (i == 0) {
                berchId = childData.getJSONObject(i).optInt("iBscDataStockDRecNo");
                String berch = childData.getJSONObject(i).optString("sBerChID");
                runOnUiThread(new Runnable() {
                    @Override
                    public void run() {
                        if (berchId != 0) {
                            tvBerch.setText(berch);
                            etBerch.setVisibility(View.VISIBLE);
                            ivBerch.setVisibility(View.VISIBLE);
                            tvBerch.setVisibility(View.VISIBLE);
                        }
                    }
                });
            }
        }
        datas.addAll(initBarcodeData(childData));
        refreshList();
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
                tvClear.setVisibility(View.VISIBLE);
                llTwo.setVisibility(View.VISIBLE);
                llThree.setVisibility(View.VISIBLE);
            }
        });
    }

    private List<NetParams> getCodeParams(String s) {
        List<NetParams> params = new ArrayList<>();
        params.add(new NetParams("otype", Otypes.GetPDAMMStockProductInBarCode));
        params.add(new NetParams("userid", userid));
        params.add(new NetParams("database", companyCode));
        params.add(new NetParams("sBarCode", s));
        params.add(new NetParams("iSDContractMRecNo", orderNo + ""));
        return params;
    }

    private void getCodeData(String s) {
        new NetUtil(getCodeParams(s), url, new ResponseListener() {
            @Override
            public void onSuccess(String string) {
                try {
                    JSONObject jsonObject = new JSONObject(string);
                    if (jsonObject.optBoolean("success")) {
                        List<PublicItem> list = initBarcodeData(jsonObject.optJSONArray("tables").optJSONArray(0));
                        if (list != null && list.size() > 0) {
                            codes.add(s);
                            datas.addAll(list);
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

    private List<PublicItem> initBarcodeData(JSONArray jsonArray) throws JSONException, NullPointerException, Exception {
        List<PublicItem> list = new ArrayList<>();
        for (int i = 0; i < jsonArray.length(); i++) {
            list.add(getBarcodeItem(jsonArray.optJSONObject(i)));
        }
        return list;
    }

    private PublicItem getBarcodeItem(JSONObject jsonObject) throws JSONException, NullPointerException, Exception {
        PublicItem item = new PublicItem();
        List<ConfigureInfo> list = new ArrayList<>();
        for (BarcodeColumn column : barcodeColumns) {
            if (column.getSFieldsName().equals("fQty")) {
                item.setFQty(StringUtil.stringTOdouble(jsonObject.optString(column.getSFieldsName())));
            }
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
                    adapter = new PublicAdapter(datas, InputDetailActivity.this);
                    adapter.setOnItemClickListener(new OnItemClickListener() {
                        @Override
                        public void onItemClick(View view, int position) {
                            isRemove(position);
                        }
                    });
                    rvItem.setAdapter(adapter);
                } else {
                    adapter.notifyDataSetChanged();
                }
                setTotal();
            }
        });

    }

    private void setTotal() {
        getTotal();
        tiQty.setContent(totalLength + "");
        tiNum.setContent(totalNum + "");
    }

    private void getTotal() {
        totalNum = 0;
        totalLength = 0;
        for (PublicItem item : datas) {
            totalNum = totalNum + 1;
            totalLength = StringUtil.add(totalLength, item.getFQty());
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
                    refreshList();
                }
            }
        });
        removeDialog.show();
    }


    @OnClick({R.id.iv_back, R.id.tv_storage, R.id.iv_storage, R.id.iv_berch, R.id.tv_empty, R.id.tv_delete, R.id.tv_submit, R.id.tv_save, R.id.tv_berch, R.id.tv_clear})
    public void onViewClicked(View view) {
        switch (view.getId()) {
            case R.id.iv_back:
                finish();
                break;
            case R.id.tv_storage:
            case R.id.iv_storage:
                selectStorage();
                break;
            case R.id.tv_berch:
            case R.id.iv_berch:
                if (storages.size() == 0) {
                    getStorageData(true);
                } else {
                    pvBerch.show();
                }
                break;
            case R.id.tv_empty:
                getData();
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
            case R.id.tv_clear:
                isClear();
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
            refreshList();
        }
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


    private List<NetParams> submitParams() {
        List<NetParams> params = new ArrayList<>();
        params.add(new NetParams("otype", Otypes.MMStockProductInMSubmit));
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
                LoadingDialog.showDialogForLoading(InputDetailActivity.this);
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
                                setResult(DeleteCode, new Intent().putExtra("position", position));
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
        params.add(new NetParams("otype", Otypes.MMStockProductInMSave));
        params.add(new NetParams("userid", userid));
        params.add(new NetParams("database", companyCode));
        params.add(new NetParams("iBscDataStockMRecNo", storageId + ""));
        params.add(new NetParams("iBscDataStockDRecNo", berchId + ""));
        params.add(new NetParams("iMMStockProductInMRecNo", iRecNo + ""));
        params.add(new NetParams("sTrayCode", etTray.getText().toString()));
        params.add(new NetParams("sSaveType", dbType));
        params.add(new NetParams("sBarCodes", getBarcode()));
        return params;
    }

    private String getBarcode() {
        String codes = "";
        for (String code : this.codes) {
            if (!StringUtil.isNotEmpty(codes)) {
                codes = codes + code;
            } else {
                codes = codes + "," + code;
            }
        }
        return codes;
    }

    private void save(boolean submit) {
        if (storageId == 0) {
            Toast("请选择仓库");
            return;
        }
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

    private List<NetParams> deteleParams() {
        List<NetParams> params = new ArrayList<>();
        params.add(new NetParams("otype", Otypes.MMStockProductInMDelete));
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
                                setResult(DeleteCode, new Intent().putExtra("position", position));
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

    private void selectStorage() {
        if (storages.size() == 0) {
            getStorageData(false);
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

    private void getStorageData(boolean isBerch) {
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
                        initStoragePick(isBerch);
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

    private void initStoragePick(boolean isBerch) {
        if (storages.size() > 0) {
            runOnUiThread(new Runnable() {
                @Override
                public void run() {
                    pvStorage = new OptionsPickerBuilder(InputDetailActivity.this, new OnOptionsSelectListener() {
                        @Override
                        public void onOptionsSelect(int options1, int options2, int options3, View v) {
                            if (storageId != storages.get(options1).getIBscDataStockMRecNo()) {
                                storageId = storages.get(options1).getIBscDataStockMRecNo();
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
                    if (storageId != 0 && isBerch) {
                        for (int i = 0; i < storages.size(); i++) {
                            if (storages.get(i).getIBscDataStockMRecNo() == storageId) {
                                setBerch(storages.get(i).getBerChes());
                                pvBerch.show();
                            }
                        }
                    } else {
                        pvStorage.show();
                    }
                }
            });
        }
    }

    private void setBerch(List<Storage.BerCh> berChes) {
        if (pvBerch != null)
            clearBerch();
        if (berChes.size() > 0) {
            this.berChes.addAll(berChes);
            initBerchPick();
            etBerch.setVisibility(View.VISIBLE);
            ivBerch.setVisibility(View.VISIBLE);
            tvBerch.setVisibility(View.VISIBLE);
        } else {
            etBerch.setVisibility(View.GONE);
            ivBerch.setVisibility(View.GONE);
            tvBerch.setVisibility(View.GONE);
        }
    }

    private void initBerchPick() {
        pvBerch = new OptionsPickerBuilder(InputDetailActivity.this, new OnOptionsSelectListener() {
            @Override
            public void onOptionsSelect(int options1, int options2, int options3, View v) {
                if (berchId != berChes.get(options1).getId()) {
                    berchId = berChes.get(options1).getId();
                    tvBerch.setText(berChes.get(options1).getName());
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
        tvBerch.setText("");
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
