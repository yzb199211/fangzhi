package com.yyy.fangzhi.form.model;

public class FormTitle {
    String name;
    String key;
    int column;

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public String getKey() {
        return key;
    }

    public void setKey(String key) {
        this.key = key;
    }

    public int getColumn() {
        return column;
    }

    public void setColumn(int column) {
        this.column = column;
    }

    public FormTitle(String name, String key, int column) {
        this.name = name;
        this.key = key;
        this.column = column;
    }

    public FormTitle() {
    }
}
