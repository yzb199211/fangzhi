package com.yyy.fangzhi.output;

import android.content.Intent;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.EditText;
import android.widget.LinearLayout;
import android.widget.TextView;

import androidx.appcompat.app.AppCompatActivity;

import com.google.gson.Gson;
import com.yyy.fangzhi.R;
import com.yyy.fangzhi.dialog.JudgeDialog;
import com.yyy.fangzhi.pubilc.PublicItem;
import com.yyy.fangzhi.util.StringUtil;

import butterknife.BindView;
import butterknife.ButterKnife;
import butterknife.OnClick;

import static com.yyy.fangzhi.util.ResultCode.EditCode;

public class OutputEditDialog extends AppCompatActivity {

    @BindView(R.id.tv_title)
    TextView tvTitle;
    @BindView(R.id.et_qty_out)
    EditText etQtyOut;
    @BindView(R.id.et_qty_flaw)
    EditText etQtyFlaw;
    @BindView(R.id.et_tray)
    EditText etTray;
    @BindView(R.id.ll_tray)
    LinearLayout llTray;

    String code;
    String tray;
    double qtyOut;
    double qtyFlaw;

    int pos;

    JudgeDialog editDialog;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.dialog_output_edit);
        ButterKnife.bind(this);
        init();
    }

    private void init() {
        getIntentData();
        initView();
    }

    private void getIntentData() {
        pos = getIntent().getIntExtra("pos", 1);
        getCode(new Gson().fromJson(getIntent().getStringExtra("data"), PublicItem.OutCode.class));
    }

    private void getCode(PublicItem.OutCode data) {
        code = data.getCode();
        tray = data.getTray();
        qtyOut = data.getOutQty();
        qtyFlaw = data.getFlawQty();
    }

    private void initView() {
        tvTitle.setText("修改条码" + code);
        etQtyOut.setText(qtyOut + "");
        etQtyFlaw.setText(qtyFlaw + "");
        etTray.setText(tray);
    }

    @OnClick({R.id.tv_delete, R.id.tv_submit})
    public void onViewClicked(View view) {
        switch (view.getId()) {
            case R.id.tv_delete:
                finish();
                break;
            case R.id.tv_submit:
                isEdit();
                break;
        }

    }

    @Override
    public void finish() {
        super.finish();
        overridePendingTransition(0, 0);
    }

    private void isEdit() {
        if (editDialog == null) {
            editDialog = new JudgeDialog(this, R.style.JudgeDialog, "是否修改此条码？");
        }
        editDialog.setOnCloseListener(new JudgeDialog.OnCloseListener() {
            @Override
            public void onClick(boolean confirm) {
                if (confirm) {
                    setResult(EditCode, new Intent()
                            .putExtra("code", new Gson().toJson(setCode()))
                            .putExtra("pos", pos));
                    finish();
                }
            }
        });
        editDialog.show();
    }

    private PublicItem.OutCode setCode() {
        PublicItem.OutCode code = new PublicItem.OutCode();
        code.setFlawQty(StringUtil.isNotEmpty(etQtyFlaw.getText().toString()) ? Double.valueOf(etQtyFlaw.getText().toString()) : 0);
        code.setOutQty(StringUtil.isNotEmpty(etQtyOut.getText().toString()) ? Double.valueOf(etQtyOut.getText().toString()) : 0);
        code.setTray(etTray.getText().toString());
        return code;
    }
}
