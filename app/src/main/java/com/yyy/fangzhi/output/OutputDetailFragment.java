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

import androidx.annotation.Nullable;
import androidx.core.widget.NestedScrollView;
import androidx.fragment.app.Fragment;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.google.gson.Gson;
import com.google.gson.reflect.TypeToken;
import com.yyy.fangzhi.R;
import com.yyy.fangzhi.dialog.JudgeDialog;
import com.yyy.fangzhi.dialog.LoadingDialog;
import com.yyy.fangzhi.interfaces.OnEntryListener;
import com.yyy.fangzhi.interfaces.OnItemClickListener;
import com.yyy.fangzhi.interfaces.OnItemLongClickListener;
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
import static com.yyy.fangzhi.util.ResultCode.EditCode;
import static com.yyy.fangzhi.util.ResultCode.FailureCode;
import static com.yyy.fangzhi.util.ResultCode.RefreshCode;
import static com.yyy.fangzhi.util.ResultCode.SuccessCode;

public class OutputDetailFragment extends Fragment {
    private static final String ARG_PARAM1 = "data";
    private static final String ARG_PARAM2 = "position";

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
    @BindView(R.id.it_qty)
    TextItem itQty;
    @BindView(R.id.it_num)
    TextItem itNum;
    @BindView(R.id.et_cut)
    EditListenerView etCut;

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
    int totalNum;

    double totalLength;

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

    private List<Select> temporary;
    private List<PublicItem> temporaryList;

    int pos;

    public static OutputDetailFragment newInstance(String data, int pos) {
        OutputDetailFragment fragment = new OutputDetailFragment();
        Bundle args = new Bundle();
        args.putString(ARG_PARAM1, data);
        args.putInt(ARG_PARAM2, pos);
        fragment.setArguments(args);
        return fragment;
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        preferencesHelper = new SharedPreferencesHelper(getActivity(), getString(R.string.preferenceCache));
        if (getArguments() != null) {
            fisrtData = getArguments().getString(ARG_PARAM1);
            pos = getArguments().getInt(ARG_PARAM2);
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
        temporary = new ArrayList<>();
        temporaryList = new ArrayList<>();
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
        setViewState();
        initRecycle();
        setCodeListener();
    }

    private void setViewState() {
        svRed.setChecked(iRed == 0 ? false : true);
        itNum.setTitle("总卷数：");
        itQty.setTitle("总米数：");
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
                if (iCut == 1) {
                    llSix.setVisibility(View.VISIBLE);
                }
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
        params.add(new NetParams("iRed", iRed + ""));
        params.add(new NetParams("fCutQty", StringUtil.stringTOdouble(etCut.getText().toString()) + ""));
        return params;
    }

    private void getCodeData(String code) {
        new NetUtil(getCodeParams(code), url, new ResponseListener() {
            @Override
            public void onSuccess(String string) {
                try {
                    JSONObject jsonObject = new JSONObject(string);
                    if (jsonObject.optBoolean("success")) {
                        temporary.clear();
                        List<PublicItem> list = initBarcodeData(iCut == 0 ? removeRepeat(jsonObject.optJSONArray("data")) : jsonObject.optJSONArray("data"));
                        if (list != null && list.size() > 0 && temporary.size() == 0) {
                            datas.addAll(0, list);
                            refreshList();
                        } else if (temporary.size() > 0) {
                            if (list != null && list.size() > 0)
                                temporaryList.addAll(list);
                            LoadingFinish(null);
                            selectCode();
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

    private void selectCode() {
        getActivity().runOnUiThread(new Runnable() {
            @Override
            public void run() {
                Log.e("json", temporary.size() + "");
                Intent intent = new Intent();
                intent.putExtra("data", new Gson().toJson(temporary));
                intent.setClass(getActivity(), SelectDialog.class);
                startActivityForResult(intent, 0);
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
        datas.addAll(initBarcodeData2(childData));
        refreshList();
    }

    private List<PublicItem> initBarcodeData(JSONArray jsonArray) throws
            JSONException, NullPointerException, Exception {
        List<PublicItem> list = new ArrayList<>();
        for (int i = 0; i < jsonArray.length(); i++) {
            JSONArray array = jsonArray.optJSONObject(i).optJSONArray("NeedSelectTables");
            if (array != null || array.length() > 0) {
                Select item = new Select();
                item.setData(jsonArray.optJSONObject(i).optString("NeedSelectTables"));
                item.setTitle(jsonArray.optJSONObject(i).optString("sBarCode"));
                temporary.add(item);
            } else {
                list.add(getBarcodeItem(jsonArray.optJSONObject(i)));
            }
        }
        return list;
    }

    private List<PublicItem> initBarcodeData2(JSONArray jsonArray) throws
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
            if (column.getSFieldsName().equals("iQty")) {
                item.setCount(StringUtil.stringTOint(jsonObject.optString(column.getSFieldsName())));
            }
            if (column.getSFieldsName().equals("sTrayCode"))
                item.setTrayPos(i);
            if (column.getSFieldsName().equals("fQty")) {
                item.setQtyPos(i);
                item.setFQty(StringUtil.stringTOdouble(jsonObject.optString(column.getSFieldsName())));
            }
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

    private void refreshList() {
        getActivity().runOnUiThread(new Runnable() {
            @Override
            public void run() {
                if (adapter == null) {
                    adapter = new PublicAdapter(datas, getActivity());
                    adapter.setOnItemClickListener(new OnItemClickListener() {
                        @Override
                        public void onItemClick(View view, int position) {
//                            isRemove(position);
                            editCode(position);

                        }
                    });
                    adapter.setOnItemLongClickListener(new OnItemLongClickListener() {
                        @Override
                        public void itemLongClick(View v, int pos) {
                            isRemove(pos);
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
        itQty.setContent(totalLength + "");
        itNum.setContent(totalNum + "");
    }

    private void getTotal() {
        totalNum = 0;
        totalLength = 0;
        for (PublicItem item : datas) {
            totalNum = totalNum + item.getCount();
            totalLength = totalLength + item.getFQty();
        }
    }

    private void editCode(int position) {
        Intent intent = new Intent();
        intent.putExtra("pos", position);
        intent.putExtra("data", new Gson().toJson(datas.get(position).getOutCode()));
        intent.setClass(getActivity(), OutputEditDialog.class);
        startActivityForResult(intent, 1);
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
                    refreshList();
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
                scrollView.setVisibility(View.VISIBLE);
                llFive.setVisibility(View.VISIBLE);
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

    @OnClick({R.id.tv_delete, R.id.tv_save, R.id.tv_submit, R.id.tv_empty, R.id.tv_clear})
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
            case R.id.tv_empty:
                getData();
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
            clearDialog = new JudgeDialog(getActivity(), R.style.JudgeDialog, "是否清空条码？", new JudgeDialog.OnCloseListener() {
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
            svRed.setClickable(true);
            refreshList();
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
                                getActivity().setResult(DeleteCode, new Intent().putExtra("position", pos));
                                getActivity().finish();
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
                                    getActivity().setResult(RefreshCode);
                                    getActivity().finish();
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
                                getActivity().setResult(DeleteCode, new Intent().putExtra("position", pos));
                                getActivity().finish();
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

    @Override
    public void onActivityResult(int requestCode, int resultCode, @Nullable Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
//        Log.e("data", resultCode + "");
        if (data != null) {
            initResult(data, resultCode);
        }
    }

    private void initResult(Intent data, int resultCode) {
        switch (resultCode) {
            case EditCode:
                initResultEdit(data);
                break;
            case FailureCode:
                removeCode(data.getStringExtra("data"));
                clearTemporary();
                break;
            case SuccessCode:
                try {
                    String codes = data.getStringExtra("data");
                    datas.addAll(temporaryList);
                    clearTemporary();
                    getCodes(new JSONArray(codes));
                    refreshList();
                } catch (JSONException e) {
                    e.printStackTrace();
                } catch (NullPointerException e) {
                    e.printStackTrace();
                } catch (Exception e) {
                    e.printStackTrace();
                }

                break;
            default:
                break;
        }
    }

    private void removeCode(String data) {
        for (PublicItem item : temporaryList) {
            if (codes.contains(item.getCode()))
                codes.remove(item.getCode());
        }
        List<String> removes = new Gson().fromJson(data, new TypeToken<List<String>>() {
        }.getType());
        for (String s : removes) {
            if (codes.contains(s))
                codes.remove(s);
        }
    }

    private void getCodes(JSONArray jsonArray) throws NullPointerException, Exception {
        List<PublicItem> list = initBarcodeData2(jsonArray);
        if (list != null && list.size() > 0) {
            datas.addAll(0, list);
        }
    }

    private void clearTemporary() {
        temporary.clear();
        temporaryList.clear();
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
        datas.get(pos).setFQty(code.getOutQty());
        refreshList();
    }
}
