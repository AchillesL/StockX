package com.example.stockx.bean;

import org.greenrobot.greendao.annotation.Entity;
import org.greenrobot.greendao.annotation.Id;
import org.greenrobot.greendao.annotation.Generated;

@Entity
public class BondsDataBean {
    @Id(autoincrement = true)
    private Long id;
    //账户的Id
    private Long accountId;
    //股票名称
    private String stockName;
    //开仓价格
    private double openPrice;
    //止损价格，包括移动止损
    private double stopLossPrice;
    //持仓数量(单位：股)
    private int bondsNum;
    @Generated(hash = 2106638465)
    public BondsDataBean(Long id, Long accountId, String stockName,
            double openPrice, double stopLossPrice, int bondsNum) {
        this.id = id;
        this.accountId = accountId;
        this.stockName = stockName;
        this.openPrice = openPrice;
        this.stopLossPrice = stopLossPrice;
        this.bondsNum = bondsNum;
    }
    @Generated(hash = 954192400)
    public BondsDataBean() {
    }
    public Long getId() {
        return this.id;
    }
    public void setId(Long id) {
        this.id = id;
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
    public Long getAccountId() {
        return this.accountId;
    }
    public void setAccountId(Long accountId) {
        this.accountId = accountId;
    }
    
}
