package com.yyy.fangzhi.view.login;

import android.text.InputType;

import androidx.annotation.ColorInt;
import androidx.annotation.DrawableRes;
import androidx.annotation.IntDef;

public class EditDelete {
    String text;
    String hint;
    @ColorInt
    int textColor;
    @ColorInt
    int hintColor;
    int inputType;
    @DrawableRes
    int img;

    public String getText() {
        return text;
    }

    public void setText(String text) {
        this.text = text;
    }

    public String getHint() {
        return hint;
    }

    public void setHint(String hint) {
        this.hint = hint;
    }

    public int getTextColor() {
        return textColor;
    }

    public void setTextColor(int textColor) {
        this.textColor = textColor;
    }

    public int getHintColor() {
        return hintColor;
    }

    public void setHintColor(int hintColor) {
        this.hintColor = hintColor;
    }

    public int getInputType() {
        return inputType;
    }

    public void setInputType(int inputType) {
        this.inputType = inputType;
    }

    public int getImg() {
        return img;
    }

    public void setImg(int img) {
        this.img = img;
    }
}
