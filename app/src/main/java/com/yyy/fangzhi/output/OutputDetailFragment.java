package com.yyy.fangzhi.output;

import android.content.Context;
import android.content.Intent;
import android.graphics.Color;
import android.os.Bundle;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.FrameLayout;
import android.widget.LinearLayout;
import android.widget.Switch;
import android.widget.TextView;

import androidx.core.widget.NestedScrollView;
import androidx.fragment.app.Fragment;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.google.gson.Gson;
import com.google.gson.reflect.TypeToken;
import com.yyy.fangzhi.R;
import com.yyy.fangzhi.dialog.JudgeDialog;
import com.yyy.fangzhi.dialog.LoadingDialog;
import com.yyy.fangzhi.input.InputDetailActivity;
import com.yyy.fangzhi.interfaces.OnEntryListener;
import com.yyy.fangzhi.interfaces.OnItemClickListener;
import com.yyy.fangzhi.interfaces.ResponseListener;
import com.yyy.fangzhi.model.BarcodeColumn;
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

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.io.IOException;
import java.util.ArrayList;
import java.util.List;

import butterknife.BindView;
import butterknife.ButterKnife;
import butterknife.OnClick;
import butterknife.Unbinder;

import static com.yyy.fangzhi.util.ResultCode.DeleteCode;
import static com.yyy.fangzhi.util.ResultCode.RefreshCode;

public class OutputDetailFragment extends Fragment {
    private static final String ARG_PARAM1 = "data";

    @BindView(R.id.it_storage)
    TextItem itStorage;
    @BindView(R.id.it_notice)
    TextItem itNotice;
    @BindView(R.id.ll_one)
    LinearLayout llOne;
    @BindView(R.id.it_cus)
    TextItem itCus;
    @BindView(R.id.it_worker)
    TextItem itWorker;
    @BindView(R.id.ll_two)
    LinearLayout llTwo;
    @BindView(R.id.it_date)
    TextItem itDate;
    @BindView(R.id.sv_red)
    Switch svRed;
    @BindView(R.id.ll_three)
    LinearLayout llThree;
    @BindView(R.id.et_code)
    EditListenerView etCode;
    @BindView(R.id.tv_clear)
    TextView tvClear;
    @BindView(R.id.ll_four)
    LinearLayout llFour;
    @BindView(R.id.rv_item)
    RecyclerView rvItem;
    @BindView(R.id.tv_total)
    TextView tvTotal;
    @BindView(R.id.tv_delete)
    TextView tvDelete;
    @BindView(R.id.tv_save)
    TextView tvSave;
    @BindView(R.id.tv_submit)
    TextView tvSubmit;
    @BindView(R.id.bottom_layout)
    LinearLayout bottomLayout;
    @BindView(R.id.fl_empty)
    FrameLayout flEmpty;
    @BindView(R.id.ll_five)
    LinearLayout llFive;
    @BindView(R.id.ll_six)
    LinearLayout llSix;
    @BindView(R.id.scroll)
    NestedScrollView scrollView;


    private OnResultListener mListener;
    Unbinder unbinder;

    private String userid;
    private String url;
    private String address;
    private String companyCode;
    private String customerName;
    private String noticeName;
    private String storageName;
    private String date;
    private String dbType;
    private String workerName;

    private int formid = 0;

    private int iRed = 0;
    private int iCut = 0;
    private int iFinish = 0;
    private int iRecNo;
    private int customerId = 0;
    private int noticeId = 0;
    private int storageId = 0;

    private boolean canDelete;

    private List<BarcodeColumn> barcodeColumns;
    private List<PublicItem> datas;
    private List<String> codes;

    private JudgeDialog deleteDialog;
    private JudgeDialog clearDialog;
    private JudgeDialog submitDialog;
    private JudgeDialog removeDialog;

    private PublicAdapter adapter;

    private SharedPreferencesHelper preferencesHelper;

    private String fisrtData;

    public static OutputDetailFragment newInstance(String data) {
        OutputDetailFragment fragment = new OutputDetailFragment();
        Bundle args = new Bundle();
        args.putString(ARG_PARAM1, data);
        fragment.setArguments(args);
        return fragment;
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        preferencesHelper = new SharedPreferencesHelper(getActivity(), getString(R.string.preferenceCache));
        if (getArguments() != null) {
            fisrtData = getArguments().getString(ARG_PARAM1);
            if (StringUtil.isNotEmpty(fisrtData)) {
                Notice notice = new Gson().fromJson(fisrtData, Notice.class);
                getNotice(notice);
            }
        }
    }

    private void getNotice(Notice notice) {
        iCut = notice.getICut();
        iRed = notice.getIRed();
        iFinish = notice.getIFinish();
        customerId = notice.getIBscDataCustomerRecNo();
        customerName = notice.getSCustShortName();
        noticeName = notice.getSBillNo();
        noticeId = notice.getIRecNo();
        storageId = notice.getStorage().getIBscDataStockMRecNo();
        storageName = notice.getStorage().getSStockName();
        date = notice.getDDate();
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.fragment_output_detail, container, false);
        unbinder = ButterKnife.bind(this, view);
        init();
        return view;
    }

    private void init() {
        initList();
        getPreferenceData();
        getIntentData();
        initView();
        getData();
    }

    private void initList() {
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
        formid = getActivity().getIntent().getIntExtra("formid", 0);
        iRecNo = getActivity().getIntent().getIntExtra("iRecNo", 0);
        dbType = iRecNo == 0 ? "add" : "modify";
    }

    private void initView() {
        bottomLayout.setVisibility(View.GONE);
        svRed.setChecked(iRed == 0 ? false : true);

        initRecycle();
        setCodeListener();
    }

    private void setViewData() {
        getActivity().runOnUiThread(new Runnable() {
            @Override
            public void run() {
                itStorage.setContent(storageName).setContentColor(getActivity().getResources().getColor(R.color.default_content_color)).setTitle("仓库：");
                itNotice.setContent(noticeName).setContentColor(getActivity().getResources().getColor(R.color.default_content_color)).setTitle("通知单：");
                itDate.setContent(date).setContentColor(getActivity().getResources().getColor(R.color.default_content_color)).setTitle("日期：");
                itCus.setContent(customerName).setContentColor(getActivity().getResources().getColor(R.color.default_content_color)).setTitle("客户：");
                itWorker.setContent(workerName).setContentColor(getActivity().getResources().getColor(R.color.default_content_color)).setTitle("下单：");
                if (!canDelete)
                    tvDelete.setVisibility(View.INVISIBLE);
            }
        });

    }

    private void initRecycle() {
        rvItem.setLayoutManager(new LinearLayoutManager(getActivity()));
        rvItem.addItemDecoration(new RecyclerViewDivider(getActivity(), LinearLayout.VERTICAL));
    }

    private void setCodeListener() {
        etCode.setOnEntryListener(new OnEntryListener() {
            @Override
            public void onEntry(View view) {
                KeyBoardUtil.hideInput(getActivity());
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

    private List<NetParams> getCodeParams(String s) {
        List<NetParams> params = new ArrayList<>();
        params.add(new NetParams("otype", Otypes.GetPDAMMStockProductOutBarCode));
        params.add(new NetParams("userid", userid));
        params.add(new NetParams("database", companyCode));
        params.add(new NetParams("sBarCode", s));
        params.add(new NetParams("iBscDataStockMRecNo", storageId + ""));
        params.add(new NetParams("iSDSendMRecNo", noticeId + ""));
        params.add(new NetParams("iBscDataCustomerRecNo", customerId + ""));
        params.add(new NetParams("iBscDataMatRecNo", 0 + ""));
        params.add(new NetParams("iRed", 0 + ""));
        params.add(new NetParams("fCutQty", 0 + ""));
        return params;
    }

    private void getCodeData(String code) {
        new NetUtil(getCodeParams(code), url, new ResponseListener() {
            @Override
            public void onSuccess(String string) {
                try {
                    JSONObject jsonObject = new JSONObject(string);
                    if (jsonObject.optBoolean("success")) {
                        List<PublicItem> list = initBarcodeData(iCut == 0 ? removeRepeat(jsonObject.optJSONArray("data")) : jsonObject.optJSONArray("data"));
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

    private JSONArray removeRepeat(JSONArray data) throws NullPointerException, JSONException, Exception {
        JSONArray array = new JSONArray();
        for (int i = 0; i < data.length(); i++) {
            if (!codes.contains(data.optJSONObject(i).optString("sBarCode"))) {
                array.put(data.optJSONObject(i));
                codes.add(data.optJSONObject(i).optString("sBarCode"));
            }
        }
        return array;
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
        LoadingDialog.showDialogForLoading(getActivity());
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
        iCut = mainData.optInt("iCut", 0);
        customerId = mainData.optInt("iBscDataCustomerRecNo", 0);
        customerName = mainData.optString("sCustShortName");
        noticeId = mainData.optInt("iSDSendMRecNo");
        noticeName = mainData.optString("sSendBillNo");
        workerName = mainData.optString("sInputUserName");
        date = mainData.optString("dDate").replace("T", " ");
        canDelete = true;
        getActivity().runOnUiThread(new Runnable() {
            @Override
            public void run() {
                setViewData();
            }
        });

    }

    private void setChildData(JSONArray childData) throws
            NullPointerException, JSONException, Exception {
        for (int i = 0; i < childData.length(); i++) {
            codes.add(childData.getJSONObject(i).optString("sBarCode"));
            if (i == 0) {
                getActivity().runOnUiThread(new Runnable() {
                    @Override
                    public void run() {

                    }
                });
            }
        }
        datas.addAll(initBarcodeData(childData));
        refreshList();
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
        for (BarcodeColumn column : barcodeColumns) {
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

    private void refreshList() {
        getActivity().runOnUiThread(new Runnable() {
            @Override
            public void run() {
                if (adapter == null) {
                    adapter = new PublicAdapter(datas, getActivity());
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
            }
        });

    }

    private void isRemove(int position) {
        if (removeDialog == null) {
            removeDialog = new JudgeDialog(getActivity(), R.style.JudgeDialog, "是否删除此条码？");
        }
        removeDialog.setOnCloseListener(new JudgeDialog.OnCloseListener() {
            @Override
            public void onClick(boolean confirm) {
                if (confirm) {
                    codes.remove(position);
                    datas.remove(position);
                    adapter.notifyDataSetChanged();
                }
            }
        });
        removeDialog.show();
    }

    private void showView() {
        getActivity().runOnUiThread(new Runnable() {
            @Override
            public void run() {
                llOne.setVisibility(View.VISIBLE);
                llTwo.setVisibility(View.VISIBLE);
                llThree.setVisibility(View.VISIBLE);
                llFour.setVisibility(View.VISIBLE);
                rvItem.setVisibility(View.VISIBLE);
                bottomLayout.setVisibility(View.VISIBLE);
                flEmpty.setVisibility(View.GONE);
            }
        });
    }

    @Override
    public void onAttach(Context context) {
        super.onAttach(context);
        if (context instanceof OnResultListener) {
            mListener = (OnResultListener) context;
        } else {
            throw new RuntimeException(context.toString()
                    + " must implement OnFragmentInteractionListener");
        }
    }

    @Override
    public void onDetach() {
        super.onDetach();
        mListener = null;
    }

    @Override
    public void onDestroyView() {
        super.onDestroyView();
        unbinder.unbind();
    }

    @OnClick({R.id.tv_delete, R.id.tv_save, R.id.tv_submit})
    public void onViewClicked(View view) {
        switch (view.getId()) {
            case R.id.tv_delete:
                isDelete();
                break;
            case R.id.tv_save:
                save(false);
                break;
            case R.id.tv_submit:
                isSubmit();
                break;
            default:
                break;
        }
    }

    private void isDelete() {
        if (deleteDialog == null) {
            deleteDialog = new JudgeDialog(getActivity(), R.style.JudgeDialog, "是否删除？", new JudgeDialog.OnCloseListener() {
                @Override
                public void onClick(boolean confirm) {
                    if (confirm)
                        delete();
                }
            });
        }
        deleteDialog.show();
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
        LoadingDialog.showDialogForLoading(getActivity());
        new NetUtil(deteleParams(), url, new ResponseListener() {
            @Override
            public void onSuccess(String string) {
                try {
                    JSONObject jsonObject = new JSONObject(string);
                    if (jsonObject.optBoolean("success")) {
                        LoadingFinish(null);
                        getActivity().runOnUiThread(new Runnable() {
                            @Override
                            public void run() {
//                                setResult(DeleteCode, new Intent().putExtra("position", position));
//                                finish();
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
        params.add(new NetParams("iSDSendMRecNo", noticeId + ""));
        params.add(new NetParams("sSaveType", dbType));
        params.add(new NetParams("iBillType", "2"));
        params.add(new NetParams("iCut", iCut + ""));
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
        LoadingDialog.showDialogForLoading(getActivity());
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
                            getActivity().runOnUiThread(new Runnable() {
                                @Override
                                public void run() {
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

    private void isSubmit() {
        if (submitDialog == null) {
            submitDialog = new JudgeDialog(getActivity(), R.style.JudgeDialog, "是否提交？", new JudgeDialog.OnCloseListener() {
                @Override
                public void onClick(boolean confirm) {
                    if (confirm)
                        save(true);
                }
            });
        }
        submitDialog.show();
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
        getActivity().runOnUiThread(new Runnable() {
            @Override
            public void run() {
                LoadingFinish(null);
                LoadingDialog.showDialogForLoading(getActivity());
            }
        });
        new NetUtil(submitParams(), url, new ResponseListener() {
            @Override
            public void onSuccess(String string) {
                try {
                    JSONObject jsonObject = new JSONObject(string);
                    if (jsonObject.optBoolean("success")) {
                        LoadingFinish(null);
                        getActivity().runOnUiThread(new Runnable() {
                            @Override
                            public void run() {
//                                setResult(DeleteCode, new Intent().putExtra("position", position));
//                                finish();
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

    public interface OnResultListener {
        void onResult(Intent intent);
    }

    private void LoadingFinish(String msg) {
        getActivity().runOnUiThread(new Runnable() {
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
        Toasts.showShort(getActivity(), msg);
    }
}
