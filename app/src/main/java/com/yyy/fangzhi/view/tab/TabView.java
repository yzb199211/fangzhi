package com.yyy.fangzhi.view.tab;

import android.annotation.SuppressLint;
import android.content.Context;
import android.util.AttributeSet;
import android.view.Gravity;
import android.view.View;
import android.widget.TextView;

import androidx.annotation.Nullable;

import com.yyy.fangzhi.R;
import com.yyy.fangzhi.interfaces.OnItemClickListener;


@SuppressLint("AppCompatCustomView")
public class TabView extends TextView {
    boolean isChecked = false;
    Context context;
    int position;

    public TabView(Context context) {
        this(context, null);
    }

    OnItemClickListener onItemClickListener;

    public OnItemClickListener getOnItemClickListener() {
        return onItemClickListener;
    }

    public void setOnItemClickListener(OnItemClickListener onItemClickListener) {
        this.onItemClickListener = onItemClickListener;
    }

    public TabView(Context context, @Nullable AttributeSet attrs) {
        super(context, attrs);
        this.context = context;
        setGravity(Gravity.CENTER);
        setBackgroundColor(context.getResources().getColor(R.color.white));
        setTextColor(context.getResources().getColor(R.color.default_text_color));
        setWidth(getResources().getDimensionPixelOffset(R.dimen.dp_90));
        setHeight(context.getResources().getDimensionPixelOffset(R.dimen.dp_30));
        setPadding(getResources().getDimensionPixelOffset(R.dimen.dp_5), getResources().getDimensionPixelOffset(R.dimen.dp_5), getResources().getDimensionPixelOffset(R.dimen.dp_5), getResources().getDimensionPixelOffset(R.dimen.dp_5));
        setOnClickListener(new OnClickListener() {
            @Override
            public void onClick(View v) {

                if (onItemClickListener != null)
                    onItemClickListener.onItemClick(v, position);
            }
        });
    }

    public boolean isChecked() {
        return isChecked;
    }

    public void setChecked(boolean checked) {
        if (context != null)
            if (isChecked) {
                setBackgroundColor(context.getResources().getColor(R.color.white));
                setTextColor(context.getResources().getColor(R.color.default_text_color));
            } else {
                setBackgroundColor(context.getResources().getColor(R.color.blue));
                setTextColor(context.getResources().getColor(R.color.white));
            }
        isChecked = checked;
    }

    public int getPosition() {
        return position;
    }

    public void setPosition(int position) {
        this.position = position;
    }
}
