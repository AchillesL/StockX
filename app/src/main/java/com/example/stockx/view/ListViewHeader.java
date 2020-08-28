package com.example.stockx.view;

import android.content.Context;
import android.content.DialogInterface;
import android.view.LayoutInflater;
import android.view.View;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;

import com.example.greendao.DaoManager;
import com.example.stockx.R;
import com.example.stockx.bean.AccountDataBean;
import com.example.stockx.bean.BondsDataBean;
import com.example.stockx.utils.StockXUtils;

import java.util.List;

public class ListViewHeader {

    private View mHeaderView1;
    private View mHeaderView2;
    private TextView mTvAccountName;
    private TextView mTvAccountAmount;
    private ImageView mIvAccountAmount;
    private TextView mTvCurrentRisk;
    private TextView mTvTotalRiskAmount;
    private TextView mTvRemainRiskAmount;
    private TextView mTvUsedRiskAmount;

    private TextView mTvStockStatus;
    private Context mContext;

    public ListViewHeader(Context context, AccountDataBean accountDataBean) {
        mContext = context;
        findViews(context);
        initView(accountDataBean);
    }

    private void findViews(Context context) {
        mHeaderView1 = LinearLayout.inflate(context, R.layout.header_listview_1, null);
        mHeaderView2 = LinearLayout.inflate(context, R.layout.header_listview_2, null);
        mTvAccountName = mHeaderView1.findViewById(R.id.tv_account_name);
        mTvAccountAmount = mHeaderView1.findViewById(R.id.tv_account_amount);
        mIvAccountAmount = mHeaderView1.findViewById(R.id.iv_account_amount);
        mTvCurrentRisk = mHeaderView1.findViewById(R.id.tv_current_risk);
        mTvTotalRiskAmount = mHeaderView1.findViewById(R.id.tv_total_risk_amount);
        mTvRemainRiskAmount = mHeaderView1.findViewById(R.id.tv_remain_risk_amount);
        mTvUsedRiskAmount = mHeaderView1.findViewById(R.id.tv_used_risk_amount);

        mTvStockStatus = mHeaderView2.findViewById(R.id.tv_stock_status);
    }

    public void initView(final AccountDataBean accountDataBean) {
        mTvAccountName.setText(accountDataBean.getAcountName());
        mTvAccountAmount.setText(String.format(mContext.getResources().getString(R.string.current_amount), StockXUtils.intDeic(accountDataBean.getCurrentMoney())));

        mIvAccountAmount.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                final EditText editText = (EditText) LayoutInflater.from(mContext).inflate(R.layout.view_edittext_dialog, null).findViewById(R.id.editText);
                editText.requestFocus();
                editText.setText(String.valueOf(accountDataBean.getCurrentMoney()));
                editText.setSelection(editText.getText().toString().length());

                CommonAlertDialog commonAlertDialog = new CommonAlertDialog(mContext, "修改权益", null, editText.getRootView(), new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialogInterface, int i) {
                        accountDataBean.setCurrentMoney(Double.parseDouble(editText.getText().toString()));
                        accountDataBean.setTotalRiskMoney(accountDataBean.getCurrentMoney() * accountDataBean.getRiskRatio() / 100.0);
                        DaoManager.getInstance().getDaoSession().getAccountDataBeanDao().insertOrReplace(accountDataBean);
                        refresh(accountDataBean);
                    }
                });
                commonAlertDialog.show();
            }
        });
        mTvCurrentRisk.setText(String.format(mContext.getResources().getString(R.string.current_risk),
                StockXUtils.intDeic(accountDataBean.getUsedRiskMoney() / accountDataBean.getTotalRiskMoney() * 100),
                StockXUtils.intDeic(accountDataBean.getUsedMonthRiskMoney() / accountDataBean.getTotalMonthRiskMoney() * 100)));

        mTvTotalRiskAmount.setText(String.format(mContext.getResources().getString(R.string.total_risk_amount),
                StockXUtils.intDeic(accountDataBean.getTotalRiskMoney()),
                StockXUtils.intDeic(accountDataBean.getTotalMonthRiskMoney())
        ));
        mTvRemainRiskAmount.setText(String.format(mContext.getResources().getString(R.string.remain_risk_amount),
                StockXUtils.intDeic(accountDataBean.getTotalRiskMoney() - accountDataBean.getUsedRiskMoney()),
                StockXUtils.intDeic(accountDataBean.getTotalMonthRiskMoney() - accountDataBean.getUsedMonthRiskMoney())
        ));
        mTvUsedRiskAmount.setText(String.format(mContext.getResources().getString(R.string.used_risk_amount),
                StockXUtils.intDeic(accountDataBean.getUsedRiskMoney()),
                StockXUtils.intDeic(accountDataBean.getUsedMonthRiskMoney())
        ));

        int stockNum = 0;
        int winStockNum = 0;
        List<BondsDataBean> bondsDataBeans = DaoManager.getInstance().getDaoSession().getBondsDataBeanDao().queryRaw("where ACCOUNT_ID = ?", String.valueOf(accountDataBean.getId()));
        for (int i = 0; i < bondsDataBeans.size(); i++) {
            BondsDataBean bondsDataBean = bondsDataBeans.get(i);
            stockNum++;
            if (bondsDataBean.getStopLossPrice() >= bondsDataBean.getCostPrice()) {
                winStockNum++;
            }
        }
        String status = "目前账户持有:  " + stockNum + "  只股票,  其中无风险共有: " + winStockNum + " 只。";
        mTvStockStatus.setText(status);
    }

    public void refresh(AccountDataBean accountDataBean) {
        initView(accountDataBean);
    }

    public View getView1() {
        return mHeaderView1;
    }

    public View getView2() {
        return mHeaderView2;
    }
}
