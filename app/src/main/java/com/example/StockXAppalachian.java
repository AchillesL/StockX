package com.example;

import android.app.Application;

import com.example.greendao.DaoManager;
import com.example.greendao.SettingBeanDao;
import com.example.stockx.bean.SettingBean;

public class StockXAppalachian extends Application {
    @Override
    public void onCreate() {
        super.onCreate();
        initGreenDao();
    }

    private void initGreenDao() {
        DaoManager mManager = DaoManager.getInstance();
        mManager.init(this);

        SettingBeanDao settingBeanDao = mManager.getDaoSession().getSettingBeanDao();
        if (settingBeanDao.load(0L) == null) {
            SettingBean settingBean = new SettingBean();
            settingBean.setCurrentAccountID(-1);
            settingBean.setId(0);
            settingBeanDao.insert(settingBean);
        }
    }
}
