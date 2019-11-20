package com.yyy.fangzhi.view.font;

import android.content.Context;
import android.graphics.Typeface;


public class FontUtil {
    public static Typeface getTypeface(Context context) {
        return Typeface.createFromAsset(context.getAssets(), "iconfont.ttf");
    }

}
