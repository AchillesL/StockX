package com.example.stockx.utils;

import com.example.stockx.bean.AbsBondsDataBean;
import com.example.stockx.bean.BondsDataBean;
import com.example.stockx.bean.PresetBondsDataBean;

import java.util.ArrayList;
import java.util.List;

public class StockXUtils {

    public static double RISK_RATIO_1 = 1;
    public static double RISK_RATIO_2 = 2;
    public static double RISK_RATIO_3 = 2.88;

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
}
