package com.example.stockx.bean;

import org.greenrobot.greendao.annotation.Entity;
import org.greenrobot.greendao.annotation.Generated;
import org.greenrobot.greendao.annotation.Id;

import java.util.Objects;

@Entity
public class PresetBondsDataBean extends AbsBondsDataBean {
    @Id(autoincrement = true)
    private Long id;
    //账户的Id
    private Long accountId;
    //股票名称
    private String stockName;
    //开仓价格
    private double openPrice;
    //止损价格
    private double stopLossPrice;
    //持仓数量(单位：股)
    private int bondsNum;

    @Generated(hash = 1034386014)
    public PresetBondsDataBean(Long id, Long accountId, String stockName,
                               double openPrice, double stopLossPrice, int bondsNum) {
        this.id = id;
        this.accountId = accountId;
        this.stockName = stockName;
        this.openPrice = openPrice;
        this.stopLossPrice = stopLossPrice;
        this.bondsNum = bondsNum;
    }

    @Generated(hash = 1492017533)
    public PresetBondsDataBean() {
    }

    public Long getId() {
        return this.id;
    }

    public void setId(Long id) {
        this.id = id;
    }

    public Long getAccountId() {
        return this.accountId;
    }

    public void setAccountId(Long accountId) {
        this.accountId = accountId;
    }

    public String getStockName() {
        return this.stockName;
    }

    public void setStockName(String stockName) {
        this.stockName = stockName;
    }

    public double getOpenPrice() {
        return this.openPrice;
    }

    public void setOpenPrice(double openPrice) {
        this.openPrice = openPrice;
    }

    public double getStopLossPrice() {
        return this.stopLossPrice;
    }

    public void setStopLossPrice(double stopLossPrice) {
        this.stopLossPrice = stopLossPrice;
    }

    public int getBondsNum() {
        return this.bondsNum;
    }

    public void setBondsNum(int bondsNum) {
        this.bondsNum = bondsNum;
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;
        PresetBondsDataBean that = (PresetBondsDataBean) o;
        return Double.compare(that.openPrice, openPrice) == 0 &&
                Double.compare(that.stopLossPrice, stopLossPrice) == 0 &&
                bondsNum == that.bondsNum &&
                Objects.equals(id, that.id) &&
                Objects.equals(accountId, that.accountId) &&
                Objects.equals(stockName, that.stockName);
    }

    @Override
    public int hashCode() {
        return Objects.hash(id, accountId, stockName, openPrice, stopLossPrice, bondsNum);
    }
}
