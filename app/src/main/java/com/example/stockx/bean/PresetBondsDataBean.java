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
    private double costPrice;
    //止损价格
    private double stopLossPrice;
    //目标价
    private double targetPrice;
    //持仓数量(单位：股)
    private int bondsNum;

    @Generated(hash = 1271017158)
    public PresetBondsDataBean(Long id, Long accountId, String stockName,
                               double costPrice, double stopLossPrice, double targetPrice,
                               int bondsNum) {
        this.id = id;
        this.accountId = accountId;
        this.stockName = stockName;
        this.costPrice = costPrice;
        this.stopLossPrice = stopLossPrice;
        this.targetPrice = targetPrice;
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

    public double getCostPrice() {
        return this.costPrice;
    }

    public void setCostPrice(double costPrice) {
        this.costPrice = costPrice;
    }

    public double getStopLossPrice() {
        return this.stopLossPrice;
    }

    public void setStopLossPrice(double stopLossPrice) {
        this.stopLossPrice = stopLossPrice;
    }

    public double getTargetPrice() {
        return this.targetPrice;
    }

    public void setTargetPrice(double targetPrice) {
        this.targetPrice = targetPrice;
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
        return Double.compare(that.costPrice, costPrice) == 0 &&
                Double.compare(that.stopLossPrice, stopLossPrice) == 0 &&
                Double.compare(that.targetPrice, targetPrice) == 0 &&
                bondsNum == that.bondsNum &&
                Objects.equals(id, that.id) &&
                Objects.equals(accountId, that.accountId) &&
                Objects.equals(stockName, that.stockName);
    }

    @Override
    public int hashCode() {
        return Objects.hash(id, accountId, stockName, costPrice, stopLossPrice, targetPrice, bondsNum);
    }
}
