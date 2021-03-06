package com.yyy.fangzhi.output;

import android.content.Context;
import android.content.Intent;
import android.graphics.Color;
import android.os.Bundle;
import android.util.Log;
import android.view.Gravity;
import android.view.LayoutInflater;
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

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.google.gson.Gson;
import com.google.gson.reflect.TypeToken;
import com.yyy.fangzhi.R;
import com.yyy.fangzhi.dialog.LoadingDialog;
import com.yyy.fangzhi.interfaces.OnFragmentAddListener;
import com.yyy.fangzhi.interfaces.OnItemClickListener;
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
import butterknife.Unbinder;

public class NoticeSelectFragment extends Fragment {

    @BindView(R.id.tv_storage)
    TextView tvStorage;
    @BindView(R.id.ll_storage)
    LinearLayout llStorage;
    @BindView(R.id.et_notice)
    EditListenerView etNotice;
    @BindView(R.id.sv_red)
    Switch svRed;
    @BindView(R.id.sv_cut)
    Switch svCut;
    @BindView(R.id.sv_finish)
    Switch svFinish;
    @BindView(R.id.ll_swicth)
    LinearLayout llSwicth;
    @BindView(R.id.rv_item)
    RecyclerView rvItem;
    @BindView(R.id.ll_search)
    LinearLayout llSearch;

    private String userid;
    private String url;
    private String address;
    private String companyCode;


    private int storageId = 0;
    private int formid = 0;

    private int iRed = 0;
    private int iCut = 0;
    private int iFinish = 0;

    private Storage storage;

    private List<Notice> notices;
    private List<Storage> storages;

    NoticeAdapter adapter;

    private SharedPreferencesHelper preferencesHelper;
    private OptionsPickerView pvStorage;
    Unbinder unbinder;
    OnFragmentAddListener onFragmentAddListener;

    public interface OnFragmentAddListener {
        void onFragmentAdd(String pos);
    }

    public void setOnFragmentAddListener(OnFragmentAddListener onFragmentAddListener) {
        this.onFragmentAddListener = onFragmentAddListener;
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        preferencesHelper = new SharedPreferencesHelper(getActivity(), getString(R.string.preferenceCache));
    }

    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.activity_notice_select, container, false);
        unbinder = ButterKnife.bind(this, view);
        init();
        return view;
    }

    public void onButtonPressed(String uri) {
        if (onFragmentAddListener != null) {
            onFragmentAddListener.onFragmentAdd(uri);
        }
    }

    @Override
    public void onAttach(Context context) {
        super.onAttach(context);
        if (context instanceof OnFragmentAddListener) {
            onFragmentAddListener = (OnFragmentAddListener) context;
        } else {
            throw new RuntimeException(context.toString()
                    + " must implement OnFragmentInteractionListener");
        }
    }

    private void init() {
        initList();
        getPreferenceData();
        getIntentData();
        initView();
        initListener();
    }


    private void initList() {
        notices = new ArrayList<>();
        storages = new ArrayList<>();
    }

    private void getPreferenceData() {
        userid = (String) preferencesHelper.getSharedPreference("userid", "");
        address = (String) preferencesHelper.getSharedPreference("address", "");
        companyCode = (String) preferencesHelper.getSharedPreference("companyCode", "");
        url = address + NetConfig.server + NetConfig.PDAHandler;
        ;
    }

    private void getIntentData() {
        formid = getActivity().getIntent().getIntExtra("formid", 0);

    }

    private void initView() {
        initRecycle();
    }

    private void initRecycle() {
        rvItem.setLayoutManager(new LinearLayoutManager(getActivity()));
        rvItem.addItemDecoration(new RecyclerViewDivider(getActivity(), LinearLayout.VERTICAL));
    }

    private void initListener() {
        setRedListener();
        setCutListener();
        setFinishListener();
    }

    private void setRedListener() {
        svRed.setOnCheckedChangeListener(new CompoundButton.OnCheckedChangeListener() {
            @Override
            public void onCheckedChanged(CompoundButton buttonView, boolean isChecked) {
                iRed = isChecked ? 1 : 0;
            }
        });
    }

    private void setCutListener() {
        svCut.setOnCheckedChangeListener(new CompoundButton.OnCheckedChangeListener() {
            @Override
            public void onCheckedChanged(CompoundButton buttonView, boolean isChecked) {
                iCut = isChecked ? 1 : 0;
            }
        });
    }

    private void setFinishListener() {
        svFinish.setOnCheckedChangeListener(new CompoundButton.OnCheckedChangeListener() {
            @Override
            public void onCheckedChanged(CompoundButton buttonView, boolean isChecked) {
                iFinish = isChecked ? 1 : 0;
            }
        });
    }

    @OnClick({R.id.tv_storage, R.id.iv_storage, R.id.tv_search})
    public void onViewClicked(View view) {
        switch (view.getId()) {
            case R.id.tv_storage:
            case R.id.iv_storage:
                selectStorage();
                break;
            case R.id.tv_search:
                if (storageId == 0) {
                    Toast(getString(R.string.empty_storage));
                    return;
                }
                notices.clear();
                getData();
                break;
            default:
                break;
        }
    }

    private List<NetParams> getParams() {
        List<NetParams> params = new ArrayList<>();
        params.add(new NetParams("otype", Otypes.GetPDASDSendM));
        params.add(new NetParams("userid", userid));
        params.add(new NetParams("database", companyCode));
        params.add(new NetParams("iBscDataStockMRecNo", storageId + ""));
        params.add(new NetParams("iRed", iRed + ""));
        params.add(new NetParams("iCut", iCut + ""));
        params.add(new NetParams("iFormID", formid + ""));
        params.add(new NetParams("iFinish", iFinish + ""));
        params.add(new NetParams("sBillNo", etNotice.getText().toString()));
        return params;
    }

    private void getData() {
        new NetUtil(getParams(), url, new ResponseListener() {
            @Override
            public void onSuccess(String string) {
                try {
                    JSONObject jsonObject = new JSONObject(string);
                    if (jsonObject.optBoolean("success")) {
                        notices.addAll(initNotice(jsonObject.optJSONArray("tables").optString(0)));
                        RefreshList();
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

    private List<Notice> initNotice(String sNotice) throws NullPointerException, Exception {
        List<Notice> list = new ArrayList<>();
        if (StringUtil.isNotEmpty(sNotice)) {
            list.addAll(new Gson().fromJson(sNotice, new TypeToken<List<Notice>>() {
            }.getType()));
        }
        return list;
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
        LoadingDialog.showDialogForLoading(getActivity());
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
            getActivity().runOnUiThread(new Runnable() {
                @Override
                public void run() {
                    pvStorage = new OptionsPickerBuilder(getActivity(), new OnOptionsSelectListener() {
                        @Override
                        public void onOptionsSelect(int options1, int options2, int options3, View v) {
                            if (storageId != storages.get(options1).getIBscDataStockMRecNo()) {
                                storageId = storages.get(options1).getIBscDataStockMRecNo();
                                tvStorage.setText(storages.get(options1).getSStockName());
                                storage = storages.get(options1);
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

    private void RefreshList() {
        getActivity().runOnUiThread(new Runnable() {
            @Override
            public void run() {
                if (adapter == null) {
                    adapter = new NoticeAdapter(getActivity(), notices);
                    adapter.setOnItemClickListener(new OnItemClickListener() {
                        @Override
                        public void onItemClick(View view, int position) {
                            Notice notice = notices.get(position);
                            notice.setStorage(storage);
                            onButtonPressed(new Gson().toJson(notice));
                        }
                    });
                    rvItem.setAdapter(adapter);
                } else {
                    adapter.notifyDataSetChanged();
                }
            }
        });
    }

    @Override
    public void onDestroyView() {
        super.onDestroyView();
        unbinder.unbind();
    }

    @Override
    public void onDetach() {
        super.onDetach();
        onFragmentAddListener = null;
    }
}
