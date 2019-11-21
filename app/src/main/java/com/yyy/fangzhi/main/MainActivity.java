package com.yyy.fangzhi.main;

import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.ImageView;
import android.widget.TextView;

import androidx.appcompat.app.AppCompatActivity;
import androidx.recyclerview.widget.RecyclerView;

import com.google.gson.Gson;
import com.google.gson.reflect.TypeToken;
import com.yyy.fangzhi.R;
import com.yyy.fangzhi.dialog.LoadingDialog;
import com.yyy.fangzhi.interfaces.OnItemClickListener;
import com.yyy.fangzhi.interfaces.ResponseListener;
import com.yyy.fangzhi.pubilc.PublicListActivity;
import com.yyy.fangzhi.util.FontUtil;
import com.yyy.fangzhi.util.SharedPreferencesHelper;
import com.yyy.fangzhi.util.StringUtil;
import com.yyy.fangzhi.util.Toasts;
import com.yyy.fangzhi.util.net.NetConfig;
import com.yyy.fangzhi.util.net.NetParams;
import com.yyy.fangzhi.util.net.NetUtil;
import com.yyy.fangzhi.util.net.Otypes;

import org.json.JSONException;
import org.json.JSONObject;

import java.io.IOException;
import java.util.ArrayList;
import java.util.List;

import butterknife.BindView;
import butterknife.ButterKnife;

public class MainActivity extends AppCompatActivity {
    private final static String TAG = "MainActivity";
    @BindView(R.id.iv_back)
    ImageView ivBack;
    @BindView(R.id.iv_right)
    ImageView ivRight;
    @BindView(R.id.rv_menu)
    RecyclerView rvMenu;
    @BindView(R.id.tv_title)
    TextView tvTitle;

    String userid;
    String address;
    String url;
    String companyCode;

    MenuUsualAdapter menuUsualAdapter;
    SharedPreferencesHelper preferencesHelper;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        ButterKnife.bind(this);
        init();
        getData();
    }


    private void init() {
        preferencesHelper = new SharedPreferencesHelper(this, getString(R.string.preferenceCache));
        initView();
        initPreferenceData();
    }

    private void initPreferenceData() {
        userid = (String) preferencesHelper.getSharedPreference("userid", "");
        companyCode = (String) preferencesHelper.getSharedPreference("companyCode", "");
        address = (String) preferencesHelper.getSharedPreference("address", "");
        url = address + NetConfig.server + NetConfig.ReportHandler_Method;
    }

    private void initView() {
        ivBack.setVisibility(View.GONE);
        ivRight.setVisibility(View.GONE);
        tvTitle.setText(getString(R.string.app_name1));
        initRecycle();
    }

    private void initRecycle() {
        NoScrollGvManager manager = new NoScrollGvManager(this, 4);
        manager.setAutoMeasureEnabled(true);
        rvMenu.setHasFixedSize(true);
        manager.setScrollEnabled(false);
        rvMenu.setLayoutManager(manager);
    }

    private List<NetParams> getParams() {
        List<NetParams> params = new ArrayList<>();
        params.add(new NetParams("otype", Otypes.GetPDAMenu));
        params.add(new NetParams("userid", userid));
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
                        List<MenuData> datas = new Gson().fromJson(jsonObject.optString("menus"), new TypeToken<List<MenuData>>() {
                        }.getType());
                        initData(datas);
                    } else {
                        LoadingFinish(jsonObject.optString("message"));
                    }
                } catch (JSONException e) {
                    e.printStackTrace();
                    LoadingFinish(getString(R.string.error_json));
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

    private void initData(List<MenuData> datas) throws Exception {
        List<MainMenu> list = new ArrayList<>();
        for (int i = 0; i < datas.size(); i++) {
            for (int j = 0; j < datas.get(i).getChildMenus().size(); j++) {
                MenuData.ChildMenusBean child = datas.get(i).getChildMenus().get(j);
                MainMenu item = new MainMenu(child.getIFormID(), FontUtil.getImg(child.getSIcon()), child.getSMenuName());
                list.add(item);
            }
        }
        LoadingFinish(null);
        runOnUiThread(new Runnable() {
            @Override
            public void run() {
                setMenus(list);
            }
        });

    }

    private void setMenus(List<MainMenu> list) {
        menuUsualAdapter = new MenuUsualAdapter(list, this);
        rvMenu.setAdapter(menuUsualAdapter);

        menuUsualAdapter.setOnItemClickListener(new OnItemClickListener() {
            @Override
            public void onItemClick(View view, int position) {
                goNext(list.get(position).getId(), list.get(position).getStr());
            }
        });
    }

    private void goNext(int id, String title) {
        startActivity(new Intent()
                .setClass(MainActivity.this, PublicListActivity.class
                ).putExtra("formid", id + "")
                .putExtra("title", title));
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
