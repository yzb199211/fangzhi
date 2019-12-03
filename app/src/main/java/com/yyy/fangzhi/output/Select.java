package com.yyy.fangzhi.output;

import com.google.gson.Gson;
import com.google.gson.reflect.TypeToken;

import org.json.JSONArray;
import org.json.JSONException;

import java.util.List;

public class Select {
    private String title;
    private String data;
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

    public List<SelectItem> getList() {
        return new Gson().fromJson(data, new TypeToken<List<SelectItem>>() {
        }.getType());
    }

    public String select(int pos) throws JSONException, Exception {
        String string = "";
        JSONArray jsonArray = new JSONArray(data);
        jsonArray.optString(pos);
        return string;
    }


}
