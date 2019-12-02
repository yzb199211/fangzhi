package com.yyy.fangzhi.dialog;

import com.google.gson.Gson;
import com.google.gson.reflect.TypeToken;

import java.util.List;

public class Select {
    private String title;
    private String data;
    private String selected;
    private List<SelectItem> list;

    public String getTitle() {
        return title;
    }

    public void setTitle(String title) {
        this.title = title;
    }

    public String getData() {
        return data;
    }

    public void setData(String data) {
        this.data = data;
    }

    public String getSelected() {
        return selected;
    }

    public void setSelected(String selected) {
        this.selected = selected;
    }

    public List<SelectItem> getList() {
        return new Gson().fromJson(data, new TypeToken<List<SelectItem>>() {
        }.getType());
    }

}
