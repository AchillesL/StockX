package com.example.stockx.utils;

import android.text.TextUtils;

import com.example.stockx.bean.AbsBondsDataBean;
import com.example.stockx.bean.BondsDataBean;
import com.example.stockx.bean.PresetBondsDataBean;

import java.util.ArrayList;
import java.util.List;

public class StockXUtils {

    public static double RISK_RATIO_1 = 1.28;
    public static double RISK_RATIO_2 = 2;
    public static double RISK_RATIO_3 = 2.88;


    public static double TARGET_PRICE_RATIO_LOW = 80;
    public static double TARGET_PRICE_RATIO_MID = 100;
    public static double TARGET_PRICE_RATIO_HIGH = 120;

    public static String validDeic(double value) {
        String data = String.format("%.3f", value);
        StringBuffer buffer = new StringBuffer(data.split("\\.")[1]);
        while (buffer.length() != 0) {
            int index = buffer.length() - 1;
            if (buffer.charAt(index) == '0') {
                buffer.deleteCharAt(index);
            } else {
                break;
            }
        }
        if (buffer.length() == 0) {
            return data.split("\\.")[0];
        } else {
            return data.split("\\.")[0] + "." + buffer.toString();
        }
    }

    public static String twoDeic(double value) {
        return String.format("%.2f", value);
    }

    public static String intDeic(double value) {
        return String.valueOf((int) value);
    }

    public static List<AbsBondsDataBean> bondsData2AbsData(List<BondsDataBean> bondsDataBeans) {
        List<AbsBondsDataBean> absBondsDataBeans = new ArrayList<>(bondsDataBeans.size());
        for (BondsDataBean bondsDataBean : bondsDataBeans) {
            absBondsDataBeans.add(bondsDataBean);
        }
        return absBondsDataBeans;
    }

    public static List<AbsBondsDataBean> prebondsData2AbsData(List<PresetBondsDataBean> presetBondsDataBeans) {
        List<AbsBondsDataBean> absBondsDataBeans = new ArrayList<>(presetBondsDataBeans.size());
        for (PresetBondsDataBean presetBondsDataBean : presetBondsDataBeans) {
            absBondsDataBeans.add(presetBondsDataBean);
        }
        return absBondsDataBeans;
    }

    public static boolean isVaildPresetBondsDataBean(PresetBondsDataBean presetBondsDataBean) {
        if (TextUtils.isEmpty(presetBondsDataBean.getStockName())
                || Double.compare(presetBondsDataBean.getBondsNum(), 0) == 0
                || Double.compare(presetBondsDataBean.getBondsNum(), 0) == 0
                || Double.compare(presetBondsDataBean.getCostPrice(), 0) == 0
                || Double.compare(presetBondsDataBean.getStopLossPrice(), 0) == 0) {
            return false;
        }
        return true;
    }
}
