package com.example.stockx.bean;

import org.greenrobot.greendao.annotation.Entity;
import org.greenrobot.greendao.annotation.Generated;
import org.greenrobot.greendao.annotation.Id;

@Entity
public class SettingBean {
    @Id
    private long id;
    //当前账户ID
    private long currentAccountID;
    @Generated(hash = 1437645676)
    public SettingBean(long id, long currentAccountID) {
        this.id = id;
        this.currentAccountID = currentAccountID;
    }
    @Generated(hash = 1969935259)
    public SettingBean() {
    }
    public long getId() {
        return this.id;
    }
    public void setId(long id) {
        this.id = id;
    }
    public long getCurrentAccountID() {
        return this.currentAccountID;
    }
    public void setCurrentAccountID(long currentAccountID) {
        this.currentAccountID = currentAccountID;
    }
}
