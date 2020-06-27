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
import com.example.stockx.utils.StockXUtils;

public class ListViewHeader {

    private View mView;
    private TextView mTvAccountName;
    private TextView mTvAccountAmount;
    private ImageView mIvAccountAmount;
    private TextView mTvCurrentRisk;
    private TextView mTvTotalRiskAmount;
    private TextView mTvRemainRiskAmount;
    private TextView mTvUsedRiskAmount;
    private Context mContext;

    public ListViewHeader(Context context, AccountDataBean accountDataBean) {
        mContext = context;
        findViews(context);
        initView(accountDataBean);
    }

    private void findViews(Context context) {
        mView = LinearLayout.inflate(context, R.layout.header_listview, null);
        mTvAccountName = mView.findViewById(R.id.tv_account_name);
        mTvAccountAmount = mView.findViewById(R.id.tv_account_amount);
        mIvAccountAmount = mView.findViewById(R.id.iv_account_amount);
        mTvCurrentRisk = mView.findViewById(R.id.tv_current_risk);
        mTvTotalRiskAmount = mView.findViewById(R.id.tv_total_risk_amount);
        mTvRemainRiskAmount = mView.findViewById(R.id.tv_remain_risk_amount);
        mTvUsedRiskAmount = mView.findViewById(R.id.tv_used_risk_amount);
    }

    public void initView(final AccountDataBean accountDataBean) {
        mTvAccountName.setText(accountDataBean.getAcountName());
        mTvAccountAmount.setText(String.format(mContext.getResources().getString(R.string.current_amount), StockXUtils.twoDeic(accountDataBean.getCurrentMoney())));

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
                StockXUtils.twoDeic(accountDataBean.getUsedRiskMoney() / accountDataBean.getTotalRiskMoney() * 100),
                StockXUtils.twoDeic(accountDataBean.getUsedMonthRiskMoney() / accountDataBean.getTotalMonthRiskMoney() * 100)));

        mTvTotalRiskAmount.setText(String.format(mContext.getResources().getString(R.string.total_risk_amount),
                StockXUtils.twoDeic(accountDataBean.getTotalRiskMoney()),
                StockXUtils.twoDeic(accountDataBean.getTotalMonthRiskMoney())
        ));
        mTvUsedRiskAmount.setText(String.format(mContext.getResources().getString(R.string.used_risk_amount),
                StockXUtils.twoDeic(accountDataBean.getUsedRiskMoney()),
                StockXUtils.twoDeic(accountDataBean.getUsedMonthRiskMoney())
        ));
        mTvRemainRiskAmount.setText(String.format(mContext.getResources().getString(R.string.remain_risk_amount),
                StockXUtils.twoDeic(accountDataBean.getTotalRiskMoney() - accountDataBean.getUsedRiskMoney()),
                StockXUtils.twoDeic(accountDataBean.getTotalMonthRiskMoney() - accountDataBean.getUsedMonthRiskMoney())
        ));
    }

    public void refresh(AccountDataBean accountDataBean) {
        initView(accountDataBean);
    }

    public View getView() {
        return mView;
    }
}
