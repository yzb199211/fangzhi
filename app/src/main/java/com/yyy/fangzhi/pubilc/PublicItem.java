package com.yyy.fangzhi.pubilc;

import com.yyy.fangzhi.view.Configure.ConfigureInfo;

import java.util.List;

public class PublicItem {
    List<ConfigureInfo> list;
    int id;
    String Code;
    OutCode outCode;

    public OutCode getOutCode() {
        return outCode;
    }

    public void setOutCode(OutCode outCode) {
        this.outCode = outCode;
    }

    public String getCode() {
        return Code;
    }

    public void setCode(String code) {
        Code = code;
    }

    public List<ConfigureInfo> getList() {
        return list;
    }

    public void setList(List<ConfigureInfo> list) {
        this.list = list;
    }

    public int getId() {
        return id;
    }

    public void setId(int id) {
        this.id = id;
    }

    public static class OutCode {
        private String code;
        private String tray;
        private double outQty;
        private double flawQty;
        private String notice;

        public String getCode() {
            return code;
        }

        public void setCode(String code) {
            this.code = code;
        }

        public String getTray() {
            return tray;
        }

        public void setTray(String tray) {
            this.tray = tray;
        }

        public double getOutQty() {
            return outQty;
        }

        public void setOutQty(double outQty) {
            this.outQty = outQty;
        }

        public double getFlawQty() {
            return flawQty;
        }

        public void setFlawQty(double flawQty) {
            this.flawQty = flawQty;
        }

        public String getNotice() {
            return notice;
        }

        public void setNotice(String notice) {
            this.notice = notice;
        }

        public String toString() {
            return "" + code + "," + tray + "," + outQty + "," + flawQty + "," + notice + ";";
        }
    }
}
