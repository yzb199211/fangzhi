package com.yyy.fangzhi.util;

import android.content.Context;
import android.content.Intent;

import androidx.appcompat.app.AppCompatActivity;

import com.yyy.fangzhi.count.CountActivity;
import com.yyy.fangzhi.exchange.ExchangeActivity;
import com.yyy.fangzhi.input.InputDetailActivity;
import com.yyy.fangzhi.output.NoticeSelectActivity;
import com.yyy.fangzhi.output.OutputDetailActivity;
import com.yyy.fangzhi.output.OutputDetailInnerActivity;

public class IntentUtil {
    public static void goActivity(AppCompatActivity activity, int formId) {
        getIntent(activity, formId);
    }

    public static Intent getIntent(Context context, int formId) {
        Intent intent = new Intent();
        switch (formId) {
            case 200001:
                intent.setClass(context, InputDetailActivity.class);
                return intent;
            case 24:
                intent.setClass(context, OutputDetailActivity.class);
                return intent;
            case 25:
                intent.setClass(context, OutputDetailInnerActivity.class);
                return intent;
            case 26:
                intent.setClass(context, ExchangeActivity.class);
                return intent;
            case 23:
                intent.setClass(context, CountActivity.class);
                return intent;
        }
        return null;
    }
}
