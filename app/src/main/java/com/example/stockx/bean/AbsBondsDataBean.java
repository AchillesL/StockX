package com.example.stockx.bean;

public abstract class AbsBondsDataBean {
    public abstract String getStockName();

    public abstract void setStockName(String stockName);

    public abstract double getCostPrice();

    public abstract void setCostPrice(double costPrice);

    public abstract double getStopLossPrice();

    public abstract void setStopLossPrice(double stopLossPrice);

    public abstract int getBondsNum();

    public abstract void setBondsNum(int bondsNum);

    public abstract Long getAccountId();

    public abstract void setAccountId(Long accountId);

    public abstract double getTargetPrice();

    public abstract void setTargetPrice(double targetPrice);
}
