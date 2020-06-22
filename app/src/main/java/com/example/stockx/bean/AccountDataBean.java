package com.example.stockx.bean;

import org.greenrobot.greendao.annotation.Entity;
import org.greenrobot.greendao.annotation.Id;
import org.greenrobot.greendao.annotation.Generated;

@Entity
public class AccountDataBean {
    @Id(autoincrement = true)
    private Long id;
    //账户名称
    private String acountName;
    //当前权益（总资金量）
    private double currentMoney;
    //风险度系数
    private double riskRatio;
    //每个月的风险度系数
    private double monthRiskRatio;
    //每个月总的金额
    private double totalMonthRiskMoney;
    //已用的每个月风险总金额
    private double usedMonthRiskMoney;
    //总的风险金额
    private double totalRiskMoney;
    //已用的风险金额
    private double usedRiskMoney;
    @Generated(hash = 2134811285)
    public AccountDataBean(Long id, String acountName, double currentMoney,
            double riskRatio, double monthRiskRatio, double totalMonthRiskMoney,
            double usedMonthRiskMoney, double totalRiskMoney,
            double usedRiskMoney) {
        this.id = id;
        this.acountName = acountName;
        this.currentMoney = currentMoney;
        this.riskRatio = riskRatio;
        this.monthRiskRatio = monthRiskRatio;
        this.totalMonthRiskMoney = totalMonthRiskMoney;
        this.usedMonthRiskMoney = usedMonthRiskMoney;
        this.totalRiskMoney = totalRiskMoney;
        this.usedRiskMoney = usedRiskMoney;
    }
    @Generated(hash = 192951259)
    public AccountDataBean() {
    }
    public Long getId() {
        return this.id;
    }
    public void setId(Long id) {
        this.id = id;
    }
    public String getAcountName() {
        return this.acountName;
    }
    public void setAcountName(String acountName) {
        this.acountName = acountName;
    }
    public double getCurrentMoney() {
        return this.currentMoney;
    }
    public void setCurrentMoney(double currentMoney) {
        this.currentMoney = currentMoney;
    }
    public double getRiskRatio() {
        return this.riskRatio;
    }
    public void setRiskRatio(double riskRatio) {
        this.riskRatio = riskRatio;
    }
    public double getMonthRiskRatio() {
        return this.monthRiskRatio;
    }
    public void setMonthRiskRatio(double monthRiskRatio) {
        this.monthRiskRatio = monthRiskRatio;
    }
    public double getUsedMonthRiskMoney() {
        return this.usedMonthRiskMoney;
    }
    public void setUsedMonthRiskMoney(double usedMonthRiskMoney) {
        this.usedMonthRiskMoney = usedMonthRiskMoney;
    }
    public double getTotalRiskMoney() {
        return this.totalRiskMoney;
    }
    public void setTotalRiskMoney(double totalRiskMoney) {
        this.totalRiskMoney = totalRiskMoney;
    }
    public double getUsedRiskMoney() {
        return this.usedRiskMoney;
    }
    public void setUsedRiskMoney(double usedRiskMoney) {
        this.usedRiskMoney = usedRiskMoney;
    }
    public double getTotalMonthRiskMoney() {
        return this.totalMonthRiskMoney;
    }
    public void setTotalMonthRiskMoney(double totalMonthRiskMoney) {
        this.totalMonthRiskMoney = totalMonthRiskMoney;
    }

    
}
