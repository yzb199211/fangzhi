package com.yyy.fangzhi.view.review.model;

public class ReviewInfo {
    //第一块详情中设置内容是否可以换行，第二块样式中设置当行是否有多列
    boolean singleLine;
    //标题文字
    String title;
    //内容文字
    String content;
    //标题颜色
    int titleColor;
    //内容颜色
    int contentColor;

    int titleSize;

    int contentSize;

    //标题是否为粗体
    boolean titleBold;
    //内容是否为粗体
    boolean contentBold;

    //第几行
    int row;
    //宽度占空间长度百分比，用小数
    float widthPercent;

    boolean isProgress = false;


    public ReviewInfo() {
    }

    //审核详情第二块引用
    public ReviewInfo(boolean singleLine, String title, String content, int titleColor, int contentColor, boolean titleBold, boolean contentBold, int row, float widthPercent) {
        this.singleLine = singleLine;
        this.title = title;
        this.content = content;
        this.titleColor = titleColor;
        this.contentColor = contentColor;
        this.titleBold = titleBold;
        this.contentBold = contentBold;
        this.row = row;
        this.widthPercent = widthPercent;
    }

    //审核详情第一块引用
    public ReviewInfo(boolean singleLine, String title, String content, int titleColor, int contentColor, boolean titleBold, boolean contentBold) {
        this.title = title;
        this.content = content;
        this.titleColor = titleColor;
        this.contentColor = contentColor;
        this.titleBold = titleBold;
        this.contentBold = contentBold;
        this.singleLine = singleLine;
    }

    public boolean isSingleLine() {
        return singleLine;
    }

    public void setSingleLine(boolean singleLine) {
        this.singleLine = singleLine;
    }

    public String getTitle() {
        return title;
    }

    public void setTitle(String title) {
        this.title = title;
    }

    public String getContent() {
        return content;
    }

    public void setContent(String content) {
        this.content = content;
    }

    public int getTitleColor() {
        return titleColor;
    }

    public void setTitleColor(int titleColor) {
        this.titleColor = titleColor;
    }

    public int getContentColor() {
        return contentColor;
    }

    public void setContentColor(int contentColor) {
        this.contentColor = contentColor;
    }

    public boolean isTitleBold() {
        return titleBold;
    }

    public void setTitleBold(boolean titleBold) {
        this.titleBold = titleBold;
    }

    public boolean isContentBold() {
        return contentBold;
    }

    public void setContentBold(boolean contentBold) {
        this.contentBold = contentBold;
    }

    public int getRow() {
        return row;
    }

    public void setRow(int row) {
        this.row = row;
    }

    public float getWidthPercent() {
        return widthPercent;
    }

    public void setWidthPercent(float widthPercent) {
        this.widthPercent = widthPercent;
    }

    public boolean isProgress() {
        return isProgress;
    }

    public void setProgress(boolean progress) {
        isProgress = progress;
    }

    public int getTitleSize() {
        return titleSize;
    }

    public void setTitleSize(int titleSize) {
        this.titleSize = titleSize;
    }

    public int getContentSize() {
        return contentSize;
    }

    public void setContentSize(int contentSize) {
        this.contentSize = contentSize;
    }
}
