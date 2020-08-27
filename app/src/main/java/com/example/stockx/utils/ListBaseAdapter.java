package com.example.stockx.utils;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.ImageView;
import android.widget.TextView;

import com.example.stockx.R;
import com.example.stockx.bean.AbsBondsDataBean;
import com.example.stockx.bean.AccountDataBean;
import com.example.stockx.bean.BondsDataBean;

import java.util.Collections;
import java.util.Comparator;
import java.util.List;

public class ListBaseAdapter extends BaseAdapter {
    private List<AbsBondsDataBean> mAbsBondsDataBeans;
    private AccountDataBean mAccountDataBean;
    private Context mContext;

    public interface ICallbackStopLoss {
        public void modify(AbsBondsDataBean absBondsDataBean);

        public void delete(final double openPrice, final double stopPrice, AbsBondsDataBean absBondsDataBean);
    }

    public interface ICallbackPreStopLoss {
        public void modify(AbsBondsDataBean absBondsDataBean);

        public void delete(AbsBondsDataBean absBondsDataBean);
    }

    private ICallbackStopLoss mICallbackStopLoss;
    private ICallbackPreStopLoss mICallbackPreStopLoss;

    public ListBaseAdapter(List<AbsBondsDataBean> bondsDataBeans, AccountDataBean accountDataBean, Context context) {
        this.mAbsBondsDataBeans = sort(bondsDataBeans);
        this.mAccountDataBean = accountDataBean;
        this.mContext = context;
    }

    private List<AbsBondsDataBean> sort(List<AbsBondsDataBean> bondsDataBeans) {
        Collections.sort(bondsDataBeans, new Comparator<AbsBondsDataBean>() {
            @Override
            public int compare(AbsBondsDataBean b1, AbsBondsDataBean b2) {
                double b1StopMoney = (b1.getStopLossPrice() - b1.getOpenPrice()) * b1.getBondsNum();
                double b2StopMoney = (b2.getStopLossPrice() - b2.getOpenPrice()) * b2.getBondsNum();
                return b1StopMoney > b2StopMoney ? -1 : 1;
            }
        });
        return bondsDataBeans;
    }


    @Override
    public int getCount() {
        return mAbsBondsDataBeans.size();
    }

    @Override
    public Object getItem(int i) {
        return mAbsBondsDataBeans.get(i);
    }

    @Override
    public long getItemId(int i) {
        return 0;
    }

    @Override
    public void notifyDataSetChanged() {
        super.notifyDataSetChanged();
        mAbsBondsDataBeans = sort(mAbsBondsDataBeans);
    }

    @Override
    public View getView(int i, View view, ViewGroup viewGroup) {
        final AbsBondsDataBean absBondsDataBean = (AbsBondsDataBean) getItem(i);
        if (view == null) {
            view = LayoutInflater.from(mContext).inflate(R.layout.view_listview_item, null);
        }

        TextView tvStockName = (TextView) view.findViewById(R.id.tv_stock_name);
        ImageView ivSafeIcon = (ImageView) view.findViewById(R.id.iv_safe_icon);
        TextView tvOpenPrice = (TextView) view.findViewById(R.id.tv_open_price);
        TextView tvStopPrice = (TextView) view.findViewById(R.id.tv_stop_price);
        TextView tvOpenNum = (TextView) view.findViewById(R.id.tv_open_num);
        TextView tvStopWinMoney = (TextView) view.findViewById(R.id.tv_stop_win_money);
        TextView tvRiskPercent = (TextView) view.findViewById(R.id.tv_risk_percent);
        ImageView ivModify = (ImageView) view.findViewById(R.id.iv_modify);
        ImageView ivDelete = (ImageView) view.findViewById(R.id.iv_delete);

        tvStockName.setText(absBondsDataBean.getStockName());

        final double openPrice = absBondsDataBean.getOpenPrice();
        final double stopPrice = absBondsDataBean.getStopLossPrice();
        tvOpenPrice.setText(String.format(mContext.getResources().getString(R.string.open_price), StockXUtils.twoDeic(openPrice)));
        tvStopPrice.setText(String.format(mContext.getResources().getString(R.string.stop_price), StockXUtils.twoDeic(stopPrice)));

        double stopOrWinMoney = Math.abs(openPrice - stopPrice) * absBondsDataBean.getBondsNum();
        if (absBondsDataBean instanceof BondsDataBean) {
            if (stopPrice >= openPrice) {
                ivSafeIcon.setVisibility(View.VISIBLE);
                tvStopWinMoney.setTextColor(mContext.getResources().getColor(android.R.color.holo_red_dark));
                tvStopWinMoney.setText(String.format(mContext.getResources().getString(R.string.win_money), StockXUtils.intDeic(stopOrWinMoney)));
            } else {
                ivSafeIcon.setVisibility(View.INVISIBLE);
                tvStopWinMoney.setTextColor(mContext.getResources().getColor(android.R.color.holo_blue_dark));
                tvStopWinMoney.setText(String.format(mContext.getResources().getString(R.string.stop_money), StockXUtils.intDeic(stopOrWinMoney)));
            }
        } else {
            ivSafeIcon.setVisibility(View.INVISIBLE);
            if (stopOrWinMoney != 0) {
                tvStopWinMoney.setText(String.format(mContext.getResources().getString(R.string.stop_money), StockXUtils.intDeic(stopOrWinMoney)));
            } else {
                tvStopWinMoney.setText(String.format(mContext.getResources().getString(R.string.stop_money), "无"));
            }
            tvStopWinMoney.setTextColor(mContext.getResources().getColor(android.R.color.holo_blue_dark));
        }

        double riskPercent = stopPrice >= openPrice ? 0 : (openPrice - stopPrice) * absBondsDataBean.getBondsNum() / mAccountDataBean.getTotalRiskMoney() * 100.0;
        if (absBondsDataBean instanceof BondsDataBean) {
            tvRiskPercent.setText(String.format(mContext.getResources().getString(R.string.risk_percent), StockXUtils.twoDeic(riskPercent)));
            tvRiskPercent.setVisibility(View.VISIBLE);
        } else {
            tvRiskPercent.setVisibility(View.GONE);
        }
        tvOpenNum.setText(String.format(mContext.getResources().getString(R.string.open_num), absBondsDataBean.getBondsNum()));

        ivModify.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                if (mICallbackStopLoss != null) {
                    mICallbackStopLoss.modify(absBondsDataBean);
                }
                if (mICallbackPreStopLoss != null) {
                    mICallbackPreStopLoss.modify(absBondsDataBean);
                }
            }
        });

        ivDelete.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                if (mICallbackStopLoss != null) {
                    mICallbackStopLoss.delete(openPrice, stopPrice, absBondsDataBean);
                }
                if (mICallbackPreStopLoss != null) {
                    mICallbackPreStopLoss.delete(absBondsDataBean);
                }
            }
        });
        return view;
    }

    public void setICallbackStopLoss(ICallbackStopLoss iCallbackStopLoss) {
        this.mICallbackStopLoss = iCallbackStopLoss;
    }

    public void setICallbackPreStopLoss(ICallbackPreStopLoss iCallbackPreStopLoss) {
        this.mICallbackPreStopLoss = iCallbackPreStopLoss;
    }

    public void update(List<AbsBondsDataBean> absBondsDataBeans) {
        mAbsBondsDataBeans = absBondsDataBeans;
        notifyDataSetChanged();
    }
}