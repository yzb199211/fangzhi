package com.yyy.fangzhi.form.model;

public class FormConditionBean {
    private String name;
    private String filters;

    public FormConditionBean(String name, String filters) {
        this.name = name;
        this.filters = filters;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public String getFilters() {
        return filters;
    }

    public void setFilters(String filters) {
        this.filters = filters;
    }
}
