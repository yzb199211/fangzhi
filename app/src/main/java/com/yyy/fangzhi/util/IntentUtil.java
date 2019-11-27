package com.yyy.fangzhi.util;

import android.content.Context;
import android.content.Intent;

import androidx.appcompat.app.AppCompatActivity;

import com.yyy.fangzhi.input.InputDetailActivity;
import com.yyy.fangzhi.output.NoticeSelectActivity;

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
                intent.setClass(context, NoticeSelectActivity.class);
                return intent;
        }
        return null;
    }
}
