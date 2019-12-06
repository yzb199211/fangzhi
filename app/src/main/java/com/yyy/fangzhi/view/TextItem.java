package com.yyy.fangzhi.view;

import android.content.Context;
import android.util.AttributeSet;
import android.view.Gravity;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.LinearLayout;
import android.widget.TextView;

import androidx.annotation.ColorInt;
import androidx.annotation.NonNull;
import androidx.annotation.Nullable;

import com.yyy.fangzhi.R;
import com.yyy.fangzhi.interfaces.OnClickListener2;


public class TextItem extends LinearLayout {
    Context context;
    TextView tvContent;
    TextView tvTitle;
    OnClickListener2 onItemClickListener;
    LinearLayout.LayoutParams params;

    public void setOnItemClickListener(OnClickListener2 onItemClickListener) {
        this.onItemClickListener = onItemClickListener;
        setContentPadding();
    }

    public TextItem setTitle(String title) {
        tvTitle.setText(title);
        return this;
    }

    public TextItem setContent(String content) {
        tvContent.setText(content);
        return this;
    }

    public TextItem setContentColor(@ColorInt int color) {
        tvContent.setTextColor(color);
        return this;
    }

    public TextItem setHint(String hint) {
        tvContent.setHint(hint);
        return this;
    }

    public TextItem removeContentClick() {
        tvContent.setClickable(false);
        return this;
    }

    public TextItem setTitleMargin(int left, int top, int right, int bottom) {
        params.setMargins(left, top, right, bottom);
        tvTitle.setLayoutParams(params);
        return this;
    }

    public TextItem setContentBlack() {
        tvContent.setTextColor(context.getResources().getColor(R.color.default_text_color));
        return this;
    }

    public String getTitle() {
        return tvTitle.getText().toString();
    }

    public TextItem setContentPadding() {
        tvContent.setPadding(0, context.getResources().getDimensionPixelOffset(R.dimen.dp_10), 0, context.getResources().getDimensionPixelOffset(R.dimen.dp_10));
        return this;
    }

    public TextItem(@NonNull Context context) {
        this(context, null);
    }

    public TextItem(@NonNull Context context, @Nullable AttributeSet attrs) {
        super(context, attrs);
        this.context = context;
        LayoutInflater.from(context).inflate(R.layout.item_text, this, true);
        setBackgroundColor(context.getResources().getColor(R.color.white));
        setGravity(Gravity.CENTER_VERTICAL);
        setOrientation(HORIZONTAL);
        init();
    }

    private void init() {
        params = new LinearLayout.LayoutParams(ViewGroup.LayoutParams.WRAP_CONTENT, ViewGroup.LayoutParams.WRAP_CONTENT);
        tvTitle = findViewById(R.id.tv_title);
        tvContent = findViewById(R.id.tv_content);
        tvContent.setOnClickListener(new OnClickListener() {
            @Override
            public void onClick(View v) {
                if (onItemClickListener != null) {
                    onItemClickListener.onItemClick(v, 0);
                }
            }
        });
    }

    public String getText() {
        return tvContent.getText().toString();
    }
}
