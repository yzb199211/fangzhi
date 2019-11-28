package com.yyy.fangzhi.output;

public class Notice {

    /**
     * iRecNo : 27849
     * iCut : 0
     * iRed : 0
     * sRed :
     * sBillNo : BL1911-0001
     * dDate : 2019-11-26T00:00:00
     * iBscDataCustomerRecNo : 2791
     * sCustShortName : 浙江客户1
     * sContractNos : sContractNo1
     * fQty : 52
     * fOutQty : 0.1
     * fNoOutQty : 52
     * iFinish : 0.1
     * sFinish :
     * iBillType : 1
     * sName : 111
     * sColorID : s
     */

    private int iRecNo;
    private int iCut;
    private int iRed;
    private String sRed;
    private String sBillNo;
    private String dDate;
    private int iBscDataCustomerRecNo;
    private String sCustShortName;
    private String sContractNos;
    private int fQty;
    private double fOutQty;
    private int fNoOutQty;
    private int iFinish;
    private String sFinish;
    private int iBillType;
    private String sName;
    private String sColorID;

    public int getIRecNo() {
        return iRecNo;
    }

    public void setIRecNo(int iRecNo) {
        this.iRecNo = iRecNo;
    }

    public int getICut() {
        return iCut;
    }

    public void setICut(int iCut) {
        this.iCut = iCut;
    }

    public int getIRed() {
        return iRed;
    }

    public void setIRed(int iRed) {
        this.iRed = iRed;
    }

    public String getSRed() {
        return sRed;
    }

    public void setSRed(String sRed) {
        this.sRed = sRed;
    }

    public String getSBillNo() {
        return sBillNo;
    }

    public void setSBillNo(String sBillNo) {
        this.sBillNo = sBillNo;
    }

    public String getDDate() {
        return dDate.replace("T", " ");
    }

    public void setDDate(String dDate) {
        this.dDate = dDate;
    }

    public int getIBscDataCustomerRecNo() {
        return iBscDataCustomerRecNo;
    }

    public void setIBscDataCustomerRecNo(int iBscDataCustomerRecNo) {
        this.iBscDataCustomerRecNo = iBscDataCustomerRecNo;
    }

    public String getSCustShortName() {
        return sCustShortName;
    }

    public void setSCustShortName(String sCustShortName) {
        this.sCustShortName = sCustShortName;
    }

    public String getSContractNos() {
        return sContractNos;
    }

    public void setSContractNos(String sContractNos) {
        this.sContractNos = sContractNos;
    }

    public int getFQty() {
        return fQty;
    }

    public void setFQty(int fQty) {
        this.fQty = fQty;
    }

    public double getFOutQty() {
        return fOutQty;
    }

    public void setFOutQty(double fOutQty) {
        this.fOutQty = fOutQty;
    }

    public int getFNoOutQty() {
        return fNoOutQty;
    }

    public void setFNoOutQty(int fNoOutQty) {
        this.fNoOutQty = fNoOutQty;
    }

    public int getIFinish() {
        return iFinish;
    }

    public void setIFinish(int iFinish) {
        this.iFinish = iFinish;
    }

    public String getSFinish() {
        return sFinish;
    }

    public void setSFinish(String sFinish) {
        this.sFinish = sFinish;
    }

    public int getIBillType() {
        return iBillType;
    }

    public void setIBillType(int iBillType) {
        this.iBillType = iBillType;
    }

    public String getSName() {
        return sName;
    }

    public void setSName(String sName) {
        this.sName = sName;
    }

    public String getSColorID() {
        return sColorID;
    }

    public void setSColorID(String sColorID) {
        this.sColorID = sColorID;
    }
}
