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

import androidx.fragment.app.Fragment;
import androidx.recyclerview.widget.RecyclerView;

import com.google.gson.Gson;
import com.google.gson.reflect.TypeToken;
import com.yyy.fangzhi.R;
import com.yyy.fangzhi.dialog.JudgeDialog;
import com.yyy.fangzhi.dialog.LoadingDialog;
import com.yyy.fangzhi.input.InputDetailActivity;
import com.yyy.fangzhi.interfaces.OnItemClickListener;
import com.yyy.fangzhi.interfaces.ResponseListener;
import com.yyy.fangzhi.model.BarcodeColumn;
import com.yyy.fangzhi.pubilc.DataFormat;
import com.yyy.fangzhi.pubilc.PublicAdapter;
import com.yyy.fangzhi.pubilc.PublicItem;
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

    private OnResultListener mListener;
    Unbinder unbinder;

    private String userid;
    private String url;
    private String address;
    private String companyCode;
    private String CustomerName;

    private int storageId = 0;
    private int formid = 0;

    private int iRed = 0;
    private int iCut = 0;
    private int iFinish = 0;
    private int iRecNo;
    private int customerId = 0;


    private List<BarcodeColumn> barcodeColumns;
    private List<PublicItem> datas;
    private List<String> codes;

    private JudgeDialog deleteDialog;
    private JudgeDialog clearDialog;
    private JudgeDialog submitDialog;
    private JudgeDialog removeDialog;

    private PublicAdapter adapter;

    SharedPreferencesHelper preferencesHelper;

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
            Log.e("data", fisrtData);
            if (StringUtil.isNotEmpty(fisrtData)) {
                Notice notice = new Gson().fromJson(fisrtData, Notice.class);
                getNotice(notice);
            }
        }
    }

    private void getNotice(Notice notice) {
        iCut = notice.getICut();
        iRecNo = notice.getIRed();
        iFinish = notice.getIFinish();

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
    }

    private void getIntentData() {
        formid = getActivity().getIntent().getIntExtra("formid", 0);
        iRecNo = getActivity().getIntent().getIntExtra("iRecNo", 0);

    }

    private void initView() {
        bottomLayout.setVisibility(View.GONE);
        if (iRecNo == 0)
            tvDelete.setVisibility(View.INVISIBLE);
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

    private void initBarcodeColumnsData(String data) {
        if (StringUtil.isNotEmpty(data)) {
            barcodeColumns.addAll(new Gson().fromJson(data, new TypeToken<List<BarcodeColumn>>() {
            }.getType()));
        }
    }

    private void setMainData(JSONObject mainData) throws NullPointerException {
        storageId = mainData.optInt("iBscDataStockMRecNo", 0);
        getActivity().runOnUiThread(new Runnable() {
            @Override
            public void run() {

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
                break;
            case R.id.tv_save:
                break;
            case R.id.tv_submit:
                break;
            default:
                break;
        }
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
