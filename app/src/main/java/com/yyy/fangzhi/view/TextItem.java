package com.yyy.fangzhi.view;

import android.content.Context;
import android.util.AttributeSet;
import android.view.LayoutInflater;
import android.view.View;
import android.widget.FrameLayout;
import android.widget.LinearLayout;
import android.widget.TextView;

import androidx.annotation.ColorInt;
import androidx.annotation.NonNull;
import androidx.annotation.Nullable;

import com.yyy.fangzhi.R;
import com.yyy.fangzhi.interfaces.OnItemClickListener;


public class TextItem extends LinearLayout {
    Context context;
    TextView tvContent;
    TextView tvTitle;
    OnItemClickListener onSelectClickListener;

    public void setOnSelectClickListener(OnItemClickListener onSelectClickListener) {
        this.onSelectClickListener = onSelectClickListener;
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

    public String getTitle() {
        return tvTitle.getText().toString();
    }

    public TextItem(@NonNull Context context) {
        this(context, null);
    }

    public TextItem(@NonNull Context context, @Nullable AttributeSet attrs) {
        super(context, attrs);
        this.context = context;
        LayoutInflater.from(context).inflate(R.layout.item_text, this, true);
        setBackgroundColor(context.getResources().getColor(R.color.white));
        setOrientation(HORIZONTAL);
        init();
    }

    private void init() {
        tvTitle = findViewById(R.id.tv_title);
        tvContent = findViewById(R.id.tv_content);
        tvContent.setOnClickListener(new OnClickListener() {
            @Override
            public void onClick(View v) {
                if (onSelectClickListener != null) {
                    onSelectClickListener.onItemClick(v, 0);
                }
            }
        });
    }

    public String getText() {
        return tvContent.getText().toString();
    }
}
