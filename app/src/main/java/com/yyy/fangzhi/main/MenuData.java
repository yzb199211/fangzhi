package com.yyy.fangzhi.main;

import java.util.List;

public class MenuData {

    /**
     * iMenuID : 519
     * sMenuName : 仓库
     * sIcon :
     * ChildMenus : [{"iMenuID":520,"sMenuName":"扫描入堆","iSerial":1,"iFormID":200001,"sIcon":"","sAppStyle":"","iShowChart":0,"iIsUnion":0,"iPageGetData":0},{"iMenuID":522,"sMenuName":"领用出库","iSerial":3,"iFormID":-1,"sIcon":"","sAppStyle":"","iShowChart":0,"iIsUnion":0,"iPageGetData":0},{"iMenuID":622,"sMenuName":"盘点单","iSerial":5,"iFormID":23,"sIcon":"","sAppStyle":"","iShowChart":0,"iIsUnion":0,"iPageGetData":0},{"iMenuID":657,"sMenuName":"销售出库","iSerial":6,"iFormID":24,"sIcon":"","sAppStyle":"","iShowChart":0,"iIsUnion":0,"iPageGetData":0}]
     */

    private String iMenuID;
    private String sMenuName;
    private String sIcon;
    private List<ChildMenusBean> ChildMenus;

    public String getIMenuID() {
        return iMenuID;
    }

    public void setIMenuID(String iMenuID) {
        this.iMenuID = iMenuID;
    }

    public String getSMenuName() {
        return sMenuName;
    }

    public void setSMenuName(String sMenuName) {
        this.sMenuName = sMenuName;
    }

    public String getSIcon() {
        return sIcon;
    }

    public void setSIcon(String sIcon) {
        this.sIcon = sIcon;
    }

    public List<ChildMenusBean> getChildMenus() {
        return ChildMenus;
    }

    public void setChildMenus(List<ChildMenusBean> ChildMenus) {
        this.ChildMenus = ChildMenus;
    }

    public static class ChildMenusBean {
        /**
         * iMenuID : 520
         * sMenuName : 扫描入堆
         * iSerial : 1
         * iFormID : 200001
         * sIcon :
         * sAppStyle :
         * iShowChart : 0
         * iIsUnion : 0
         * iPageGetData : 0
         */

        private int iMenuID;
        private String sMenuName;
        private int iSerial;
        private int iFormID;
        private String sIcon;
        private String sAppStyle;
        private int iShowChart;
        private int iIsUnion;
        private int iPageGetData;

        public int getIMenuID() {
            return iMenuID;
        }

        public void setIMenuID(int iMenuID) {
            this.iMenuID = iMenuID;
        }

        public String getSMenuName() {
            return sMenuName;
        }

        public void setSMenuName(String sMenuName) {
            this.sMenuName = sMenuName;
        }

        public int getISerial() {
            return iSerial;
        }

        public void setISerial(int iSerial) {
            this.iSerial = iSerial;
        }

        public int getIFormID() {
            return iFormID;
        }

        public void setIFormID(int iFormID) {
            this.iFormID = iFormID;
        }

        public String getSIcon() {
            return sIcon;
        }

        public void setSIcon(String sIcon) {
            this.sIcon = sIcon;
        }

        public String getSAppStyle() {
            return sAppStyle;
        }

        public void setSAppStyle(String sAppStyle) {
            this.sAppStyle = sAppStyle;
        }

        public int getIShowChart() {
            return iShowChart;
        }

        public void setIShowChart(int iShowChart) {
            this.iShowChart = iShowChart;
        }

        public int getIIsUnion() {
            return iIsUnion;
        }

        public void setIIsUnion(int iIsUnion) {
            this.iIsUnion = iIsUnion;
        }

        public int getIPageGetData() {
            return iPageGetData;
        }

        public void setIPageGetData(int iPageGetData) {
            this.iPageGetData = iPageGetData;
        }
    }
}
