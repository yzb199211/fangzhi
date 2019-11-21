package com.yyy.fangzhi.pubilc;

public class DataFormat {
    public static String getData(String s, String type) {
        try {
            switch (type.toLowerCase()) {
                case "date":
                    return s.substring(0, 10);
                case "datetime":
                    return s.substring(0, 19).replace("T", " ");
                default:
                    return s;
            }
        } catch (Exception e) {
            e.printStackTrace();
            return s;
        }
    }
}
