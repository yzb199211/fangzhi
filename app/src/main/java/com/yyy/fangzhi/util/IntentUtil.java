package com.yyy.fangzhi.util;

import android.app.Activity;
import android.content.Context;
import android.content.Intent;

import com.yyy.fangzhi.input.InputDetailActivity;

public class IntentUtil {
    public static void goActivity(Activity activity, int formId) {
        getIntent(activity, formId);
    }

    public static Intent getIntent(Context context, int formId) {
        Intent intent = new Intent();
        switch (formId) {
            case 200001:
                intent.setClass(context, InputDetailActivity.class);
                return intent;
        }
        return null;
    }
}
