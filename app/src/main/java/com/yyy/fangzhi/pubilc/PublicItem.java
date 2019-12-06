package com.yyy.fangzhi.pubilc;

import com.yyy.fangzhi.view.Configure.ConfigureInfo;

import java.util.List;

public class PublicItem {
    List<ConfigureInfo> list;
    int id;
    String Code;
    OutCode outCode;
    CountCode CountCode;
    int trayPos;
    int qtyPos;
    int qtyFlawPos;
    double qty;
    int count;

    public int getCount() {
        return count;
    }

    public void setCount(int count) {
        this.count = count;
    }

    public double getFQty() {
        return qty;
    }

    public void setFQty(double qty) {
        this.qty = qty;
    }

    public void setCountCode(PublicItem.CountCode countCode) {
        CountCode = countCode;
    }

    public PublicItem.CountCode getCountCode() {
        return CountCode;
    }

    public int getTrayPos() {
        return trayPos;
    }

    public void setTrayPos(int trayPos) {
        this.trayPos = trayPos;
    }

    public int getQtyPos() {
        return qtyPos;
    }

    public void setQtyPos(int qtyPos) {
        this.qtyPos = qtyPos;
    }

    public int getQtyFlawPos() {
        return qtyFlawPos;
    }

    public void setQtyFlawPos(int qtyFlawPos) {
        this.qtyFlawPos = qtyFlawPos;
    }

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

    public void setTray(String tray) {
        list.get(trayPos).setContent(tray);
    }

    public void setQty(double qty) {
        list.get(qtyPos).setContent(qty + "");
    }

    public void setFlawQty(double flawQty) {
        list.get(qtyFlawPos).setContent(flawQty + "");
    }

    public static class CountCode {
        private String code;
        private String tray;
        private double qty;

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

        public double getQty() {
            return qty;
        }

        public void setQty(double qty) {
            this.qty = qty;
        }

        public String toString() {
            return code + "," + tray + "," + qty;
        }
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
