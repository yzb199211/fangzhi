package com.yyy.fangzhi.view.review.model;

import java.util.List;

public class ReviewStyle {
    int spanCounts;

    List<ReviewInfo> list;

    public ReviewStyle() {
    }

    public ReviewStyle(List<ReviewInfo> list) {
        this.list = list;
    }

    public ReviewStyle(int spanCounts, List<ReviewInfo> list) {
        this.spanCounts = spanCounts;
        this.list = list;
    }


    public int getSpanCounts() {
        return spanCounts;
    }

    public void setSpanCounts(int spanCounts) {
        this.spanCounts = spanCounts;
    }

    public List<ReviewInfo> getList() {
        return list;
    }

    public void setList(List<ReviewInfo> list) {
        this.list = list;
    }
}
