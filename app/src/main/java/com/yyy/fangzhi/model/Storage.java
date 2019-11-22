package com.yyy.fangzhi.model;

import com.yyy.fangzhi.util.StringUtil;
import com.yyy.yyylibrary.wheel.interfaces.IPickerViewData;

import java.util.ArrayList;
import java.util.List;

public class Storage implements IPickerViewData {

    /**
     * iBscDataStockMRecNo : 421
     * sStockName : 成品仓01
     * iBscDataStockDRecNo :
     * sBerChID :
     */

    private int iBscDataStockMRecNo;
    private String sStockName;
    private String iBscDataStockDRecNo;
    private String sBerChID;
    private List<BerCh> berChes = new ArrayList<>();

    public List<BerCh> getBerChes() {
        int length;
        if (StringUtil.isNotEmpty(sBerChID) && StringUtil.isNotEmpty(iBscDataStockDRecNo)) {
            String[] name = sBerChID.split("@,@");
            String[] id = iBscDataStockDRecNo.split("@,@");
            length = name.length < id.length ? name.length : id.length;
            for (int i = 0; i < length; i++) {
                BerCh item = new BerCh();
                item.setId(Integer.parseInt(id[i]));
                item.setName(name[i]);
                berChes.add(item);
            }
        }
        return berChes;
    }

    public void setBerChes(List<BerCh> berChes) {
        this.berChes = berChes;
    }

    public int getIBscDataStockMRecNo() {
        return iBscDataStockMRecNo;
    }

    public void setIBscDataStockMRecNo(int iBscDataStockMRecNo) {
        this.iBscDataStockMRecNo = iBscDataStockMRecNo;
    }

    public String getSStockName() {
        return sStockName;
    }

    public void setSStockName(String sStockName) {
        this.sStockName = sStockName;
    }

    public String getIBscDataStockDRecNo() {
        return iBscDataStockDRecNo;
    }

    public void setIBscDataStockDRecNo(String iBscDataStockDRecNo) {
        this.iBscDataStockDRecNo = iBscDataStockDRecNo;
    }

    public String getSBerChID() {
        return sBerChID;
    }

    public void setSBerChID(String sBerChID) {
        this.sBerChID = sBerChID;
    }

    @Override
    public String getPickerViewText() {
        return sStockName;
    }

    public static class BerCh implements IPickerViewData {
        int id;
        String name;

        public int getId() {
            return id;
        }

        public void setId(int id) {
            this.id = id;
        }

        public String getName() {
            return name;
        }

        public void setName(String name) {
            this.name = name;
        }

        @Override
        public String getPickerViewText() {
            return name;
        }
    }
}
