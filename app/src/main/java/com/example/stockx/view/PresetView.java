package com.example.stockx.view;

import android.content.Context;
import android.content.DialogInterface;
import android.text.TextUtils;
import android.view.LayoutInflater;
import android.view.View;
import android.widget.ImageView;
import android.widget.ListView;
import android.widget.TextView;
import android.widget.Toast;

import com.example.greendao.DaoManager;
import com.example.greendao.PresetBondsDataBeanDao;
import com.example.stockx.R;
import com.example.stockx.bean.AbsBondsDataBean;
import com.example.stockx.bean.AccountDataBean;
import com.example.stockx.bean.BondsDataBean;
import com.example.stockx.bean.PresetBondsDataBean;
import com.example.stockx.utils.ListBaseAdapter;
import com.example.stockx.utils.StockXUtils;

import java.util.ArrayList;
import java.util.List;

public class PresetView {

    private Context mContext;
    private AccountDataBean mAccountDataBean;
    private List<PresetBondsDataBean> mPresetBondsDataBeans;
    private List<PresetBondsDataBean> mValidPresetBondsDataBeans;
    private PresetBondsDataBeanDao mPresetBondsDataBeanDao;

    private View mView;
    private TextView mTvStatus;
    private TextView mTvWarning;
    private ListView mListView;
    private ImageView mLvAdd;

    private boolean mIsCanAdd = true;

    private ListBaseAdapter mListBaseAdapter;

    public interface iCallback {
        public void add2StopComplete();
    }

    private iCallback mICallback;

    public PresetView(Context context, AccountDataBean accountDataBean) {
        this.mContext = context;
        this.mAccountDataBean = accountDataBean;
        mView = LayoutInflater.from(context).inflate(R.layout.view_preset, null);

        mPresetBondsDataBeanDao = DaoManager.getInstance().getDaoSession().getPresetBondsDataBeanDao();
        mPresetBondsDataBeans = mPresetBondsDataBeanDao.queryRaw("where ACCOUNT_ID = ?", String.valueOf(mAccountDataBean.getId()));

        findViews();
    }

    private void findViews() {
        mTvStatus = (TextView) mView.findViewById(R.id.tv_status);
        mTvWarning = (TextView) mView.findViewById(R.id.tv_waring);
        mListView = (ListView) mView.findViewById(R.id.listView);
        mLvAdd = (ImageView) mView.findViewById(R.id.iv_add);

        updateStatus(mPresetBondsDataBeans);

        mListBaseAdapter = new ListBaseAdapter(StockXUtils.prebondsData2AbsData(mPresetBondsDataBeans), mAccountDataBean, mContext);
        mListBaseAdapter.setICallbackPreStopLoss(new ListBaseAdapter.ICallbackPreStopLoss() {
            @Override
            public void modify(AbsBondsDataBean absBondsDataBean) {
                StopLossAlertDialog stopLossAlertDialog = new StopLossAlertDialog(mContext, StopLossAlertDialog.MODE_MODIFY_PRESET, absBondsDataBean, mAccountDataBean);
                stopLossAlertDialog.setPresetBondsDataBean((PresetBondsDataBean) absBondsDataBean);
                stopLossAlertDialog.setCallbackPreset(new StopLossAlertDialog.ICallbackPreset() {

                    @Override
                    public void refresh(String msg, PresetBondsDataBean presetBondsDataBean) {
                        Toast.makeText(mContext, msg, Toast.LENGTH_SHORT).show();
                        DaoManager.getInstance().getDaoSession().getPresetBondsDataBeanDao().insertOrReplace(presetBondsDataBean);
                        updateListViewAndData();
                    }
                });
                stopLossAlertDialog.show();
            }

            @Override
            public void delete(final AbsBondsDataBean absBondsDataBean) {
                CommonAlertDialog commonAlertDialog = new CommonAlertDialog(mContext, "是否删除该项?", new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialog, int which) {
                        DaoManager.getInstance().getDaoSession().getPresetBondsDataBeanDao().delete((PresetBondsDataBean) absBondsDataBean);
                        updateListViewAndData();
                    }
                });
                commonAlertDialog.show();
            }
        });
        mListView.setAdapter(mListBaseAdapter);

        mLvAdd.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (!mIsCanAdd) {
                    Toast.makeText(mContext, "已超风险额度!", Toast.LENGTH_SHORT).show();
                    return;
                }
                StopLossAlertDialog stopLossAlertDialog = new StopLossAlertDialog(mContext, StopLossAlertDialog.MODE_FIRST_CREATE_PRESET, null, mAccountDataBean);
                stopLossAlertDialog.setCallbackPreset(new StopLossAlertDialog.ICallbackPreset() {

                    @Override
                    public void refresh(String msg, PresetBondsDataBean presetBondsDataBean) {
                        Toast.makeText(mContext, msg, Toast.LENGTH_SHORT).show();
                        DaoManager.getInstance().getDaoSession().getPresetBondsDataBeanDao().insertOrReplace(presetBondsDataBean);
                        updateListViewAndData();
                    }
                });
                stopLossAlertDialog.show();
            }
        });
    }

    private void updateListViewAndData() {
        mPresetBondsDataBeans = mPresetBondsDataBeanDao.queryRaw("where ACCOUNT_ID = ?", String.valueOf(mAccountDataBean.getId()));
        mListBaseAdapter.update(StockXUtils.prebondsData2AbsData(mPresetBondsDataBeans));
        updateStatus(mPresetBondsDataBeans);
    }

    private double getTotalStopLossMoney(List<PresetBondsDataBean> presetBondsDataBeans) {
        double totalStopLoss = 0;
        for (PresetBondsDataBean presetBondsDataBean : presetBondsDataBeans) {
            totalStopLoss += (presetBondsDataBean.getCostPrice() - presetBondsDataBean.getStopLossPrice()) * presetBondsDataBean.getBondsNum();
        }
        return totalStopLoss;
    }

    private void updateStatus(List<PresetBondsDataBean> presetBondsDataBeans) {
        List<PresetBondsDataBean> presetBondsDataBeansTmp = getVaildPresetBondsDataBean(presetBondsDataBeans);
        double totalStopLoss = getTotalStopLossMoney(presetBondsDataBeansTmp);

        String tip = "";
        double remainRiskMoney = mAccountDataBean.getTotalRiskMoney() - mAccountDataBean.getUsedRiskMoney() - totalStopLoss;
        double remainMonthRiskMoney = mAccountDataBean.getTotalMonthRiskMoney() - mAccountDataBean.getUsedMonthRiskMoney() - totalStopLoss;
        tip += "本次消耗风险金额为：" + StockXUtils.intDeic(totalStopLoss) + "\n" +
                "剩余单次风险金额为：" + StockXUtils.intDeic(remainRiskMoney) + "\n" +
                "剩余月度风险金额为：" + StockXUtils.intDeic(remainMonthRiskMoney);
        mTvStatus.setText(tip);
        if (remainRiskMoney < 0 || remainMonthRiskMoney < 0) {
            mIsCanAdd = false;
            mTvWarning.setVisibility(View.VISIBLE);
        } else {
            mIsCanAdd = true;
            mTvWarning.setVisibility(View.GONE);
        }
    }

    private List<PresetBondsDataBean> getVaildPresetBondsDataBean(List<PresetBondsDataBean> presetBondsDataBeansOrigin) {
        List<PresetBondsDataBean> presetBondsDataBeans = new ArrayList<>();
        for (PresetBondsDataBean presetBondsDataBean : presetBondsDataBeansOrigin) {
            if (TextUtils.isEmpty(presetBondsDataBean.getStockName())) {
                continue;
            }
            if (presetBondsDataBean.getBondsNum() == 0) {
                continue;
            }
            if (presetBondsDataBean.getCostPrice() == 0) {
                continue;
            }
            if (presetBondsDataBean.getStopLossPrice() == 0) {
                continue;
            }
            presetBondsDataBeans.add(presetBondsDataBean);
        }
        return presetBondsDataBeans;
    }

    public View getView() {
        return mView;
    }

    public void clearAll() {
        if (mPresetBondsDataBeans.isEmpty()) {
            Toast.makeText(mContext, "列表为空!", Toast.LENGTH_SHORT).show();
            return;
        }
        CommonAlertDialog commonAlertDialog = new CommonAlertDialog(mContext, "是否删除所有预备单?", new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialog, int which) {
                for (PresetBondsDataBean presetBondsDataBean : mPresetBondsDataBeans) {
                    DaoManager.getInstance().getDaoSession().getPresetBondsDataBeanDao().delete(presetBondsDataBean);
                }
                updateListViewAndData();
                Toast.makeText(mContext, "已删除所有预备单", Toast.LENGTH_SHORT).show();
            }
        });
        commonAlertDialog.show();
    }

    public void clearInvalid() {
        if (mPresetBondsDataBeans.isEmpty()) {
            Toast.makeText(mContext, "列表为空!", Toast.LENGTH_SHORT).show();
            return;
        }
        CommonAlertDialog commonAlertDialog = new CommonAlertDialog(mContext, "是否删除无效预备单?", new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialog, int which) {
                List<PresetBondsDataBean> presetBondsDataBeans = getVaildPresetBondsDataBean(mPresetBondsDataBeans);
                mPresetBondsDataBeans.removeAll(presetBondsDataBeans);
                for (PresetBondsDataBean presetBondsDataBean : mPresetBondsDataBeans) {
                    DaoManager.getInstance().getDaoSession().getPresetBondsDataBeanDao().delete(presetBondsDataBean);
                }
                updateListViewAndData();
                Toast.makeText(mContext, "已删除所有无效预备单", Toast.LENGTH_SHORT).show();
            }
        });
        commonAlertDialog.show();
    }

    public void add2Stop() {
        if (mPresetBondsDataBeans.isEmpty()) {
            Toast.makeText(mContext, "列表为空!", Toast.LENGTH_SHORT).show();
            return;
        }
        if (!mIsCanAdd) {
            Toast.makeText(mContext, "已超风险额度!", Toast.LENGTH_SHORT).show();
            return;
        }
        CommonAlertDialog commonAlertDialog = new CommonAlertDialog(mContext, "是否将所有有效预备单保存为止损单?", new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialog, int which) {
                List<PresetBondsDataBean> presetBondsDataBeans = getVaildPresetBondsDataBean(mPresetBondsDataBeans);

                for (PresetBondsDataBean presetBondsDataBean : presetBondsDataBeans) {
                    BondsDataBean bondsDataBean = preBonds2BondsData(presetBondsDataBean);
                    DaoManager.getInstance().getDaoSession().getBondsDataBeanDao().insert(bondsDataBean);
                    DaoManager.getInstance().getDaoSession().getPresetBondsDataBeanDao().delete(presetBondsDataBean);
                }
                double totalStopLossMoney = getTotalStopLossMoney(presetBondsDataBeans);
                mAccountDataBean.setUsedRiskMoney(mAccountDataBean.getUsedRiskMoney() + totalStopLossMoney);
                mAccountDataBean.setUsedMonthRiskMoney(mAccountDataBean.getUsedMonthRiskMoney() + totalStopLossMoney);
                DaoManager.getInstance().getDaoSession().getAccountDataBeanDao().insertOrReplace(mAccountDataBean);
                if (mICallback != null) {
                    mICallback.add2StopComplete();
                }
            }
        });
        commonAlertDialog.show();
    }

    public BondsDataBean preBonds2BondsData(PresetBondsDataBean presetBondsDataBean) {
        BondsDataBean bondsDataBean = new BondsDataBean();
        bondsDataBean.setAccountId(presetBondsDataBean.getAccountId());
        bondsDataBean.setBondsNum(presetBondsDataBean.getBondsNum());
        bondsDataBean.setCostPrice(presetBondsDataBean.getCostPrice());
        bondsDataBean.setStockName(presetBondsDataBean.getStockName());
        bondsDataBean.setStopLossPrice(presetBondsDataBean.getStopLossPrice());
        return bondsDataBean;
    }

    public void setiCallback(iCallback iCallback) {
        this.mICallback = iCallback;
    }
}
