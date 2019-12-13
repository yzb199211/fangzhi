package com.yyy.fangzhi.view.review;

import android.content.Context;
import android.content.res.TypedArray;
import android.graphics.Typeface;
import android.text.TextUtils;
import android.util.AttributeSet;
import android.view.Gravity;
import android.view.LayoutInflater;
import android.widget.LinearLayout;
import android.widget.ProgressBar;
import android.widget.RelativeLayout;
import android.widget.TextView;

import androidx.annotation.Nullable;

import com.yyy.fangzhi.R;


public class ReviewInfoView extends LinearLayout {
    private TextView tvTitle;
    private TextView tvContent;
    private TextView tvProgress;
    private ProgressBar progressBar;
    private RelativeLayout rlProgress;
    String title;
    String content;
    int titleColor;
    int contentColor;
    boolean titleBold;
    boolean contentBold;
    boolean singleLine;
    boolean isProgress = false;


    public ReviewInfoView(Context context) {
        this(context, null);
    }

    public ReviewInfoView(Context context, @Nullable AttributeSet attrs) {
        super(context, attrs);
        init(context, attrs);
    }


    public ReviewInfoView(Context context, @Nullable AttributeSet attrs, int defStyleAttr) {
        super(context, attrs, defStyleAttr);
        init(context, attrs);
    }

    private void init(Context context, AttributeSet attrs) {
        LayoutInflater.from(context).inflate(R.layout.item_review_info, this, true);
        tvTitle = findViewById(R.id.tv_title);
        tvContent = findViewById(R.id.tv_content);
        tvProgress = findViewById(R.id.tv_progress);
        progressBar = findViewById(R.id.progress);
        rlProgress = findViewById(R.id.rl_progress);

        progressBar.setMax(100);
        TypedArray array = context.obtainStyledAttributes(attrs, R.styleable.configureInfo);
        title = array.getString(R.styleable.configureInfo_leftText);
        titleColor = array.getColor(R.styleable.configureInfo_leftTextColor, context.getResources().getColor(R.color.default_content_color));
        titleBold = array.getBoolean(R.styleable.configureInfo_leftTextBold, false);
        content = array.getString(R.styleable.configureInfo_rightText);
        contentColor = array.getColor(R.styleable.configureInfo_rightTextColor, context.getResources().getColor(R.color.default_text_color));
        contentBold = array.getBoolean(R.styleable.configureInfo_rightTextBold, false);
        array.recycle();
        setView();
    }

    private void setView() {
        setGravity(Gravity.CENTER_VERTICAL);
        if (!TextUtils.isEmpty(title))
            tvTitle.setText(title);
        tvTitle.setTextColor(titleColor);
        if (titleBold == true)
            tvTitle.setTypeface(Typeface.DEFAULT_BOLD);
        else
            tvTitle.setTypeface(Typeface.DEFAULT);
        if (!TextUtils.isEmpty(content))
            tvContent.setText(content);
        tvContent.setTextColor(contentColor);
        if (contentBold == true)
            tvContent.setTypeface(Typeface.DEFAULT_BOLD);
        else
            tvContent.setTypeface(Typeface.DEFAULT);
    }

    //设置标题内容
    public void setTitle(String title) {
        this.title = title;
        tvTitle.setText(title);
    }

    public void setTitleSize(int textsize) {
        tvTitle.setTextSize(textsize);
    }

    //设置内容文字
    public void setContent(String content) {
        this.content = content;
        tvContent.setText(content);
    }

    public void setContentSize(int size) {
        tvContent.setTextSize(size);
    }

    //设置标题颜色
    public void setTitleColor(int titleColor) {
        this.titleColor = titleColor;
        tvTitle.setTextColor(titleColor);
        tvContent.setTextColor(titleColor);
    }

    //设置内容颜色
    public void setContentColor(int contentColor) {
        this.contentColor = contentColor;
        tvContent.setTextColor(contentColor);
    }

    //设置标题是否为粗体
    public void setTitleBold(boolean titleBold) {
        this.titleBold = titleBold;
        if (titleBold == true)
            tvTitle.setTypeface(Typeface.DEFAULT_BOLD);
        else
            tvTitle.setTypeface(Typeface.DEFAULT);
    }

    //设置内容是否为粗体
    public void setContentBold(boolean contentBold) {
        this.contentBold = contentBold;
        if (contentBold)
            tvContent.setTypeface(Typeface.DEFAULT_BOLD);
        else
            tvContent.setTypeface(Typeface.DEFAULT);
    }

    public boolean isSingleLine() {
        return singleLine;
    }

    //设置内容是否为单行
    public void setSingleLine(boolean singleLine) {
        this.singleLine = singleLine;
        if (singleLine == false)
            tvContent.setSingleLine(false);
    }

    public void setProgress(boolean isProgress, int progress) {
        this.isProgress = isProgress;
        if (isProgress) {
            tvContent.setVisibility(GONE);
            rlProgress.setVisibility(VISIBLE);
            tvProgress.setText(progress + "%");
            progressBar.setProgress(progress);
        } else {
            tvContent.setVisibility(VISIBLE);
            rlProgress.setVisibility(GONE);
        }
    }

    public String getTitle() {
        return title;
    }

    public String getContent() {
        return content;
    }

    public int getTitleColor() {
        return titleColor;
    }

    public int getContentColor() {
        return contentColor;
    }

    public boolean isTitleBold() {
        return titleBold;
    }

    public boolean isContentBold() {
        return contentBold;
    }

    public TextView getTvTitle() {
        return tvTitle;
    }
}
