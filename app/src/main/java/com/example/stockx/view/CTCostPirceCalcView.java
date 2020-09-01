package com.example.stockx.view;

import android.content.Context;
import android.content.DialogInterface;
import android.text.Editable;
import android.text.TextUtils;
import android.text.TextWatcher;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.CompoundButton;
import android.widget.RadioButton;
import android.widget.TextView;

import com.example.greendao.DaoManager;
import com.example.stockx.R;
import com.example.stockx.bean.AccountDataBean;
import com.example.stockx.bean.BondsDataBean;
import com.example.stockx.utils.StockXUtils;
import com.google.android.material.textfield.TextInputEditText;

public class CTCostPirceCalcView {

    //成本价计算
    private TextInputEditText edCurrentCostPrice;
    private TextInputEditText edCurrentAmount;
    private TextInputEditText edAddOpenPrice;
    private TextInputEditText edAddAmount;
    private TextView tvCostPriceTip;
    private TextView tvCostPriceUpdate;
    private TextView tvCostPriceClear;
    private TextView tvCostPriceSave;

    private long bondsDataId;
    private View view;
    private Context context;

    public interface ICallbackCostPriceCalc {
        public void save(boolean canSave,String msg,BondsDataBean bondsDataBean);
    }

    private ICallbackCostPriceCalc iCallbackCostPriceCalc;

    public CTCostPirceCalcView(Context context, ViewGroup parentView) {
        this.context = context;
        view = LayoutInflater.from(context).inflate(R.layout.cardview_item_cost_price, parentView);
        edCurrentCostPrice = (TextInputEditText) view.findViewById(R.id.ed_current_cost_price);
        edCurrentAmount = (TextInputEditText) view.findViewById(R.id.ed_current_amount);
        edAddOpenPrice = (TextInputEditText) view.findViewById(R.id.ed_add_open_price);
        edAddAmount = (TextInputEditText) view.findViewById(R.id.ed_add_amount);
        tvCostPriceTip = (TextView) view.findViewById(R.id.tv_cost_price_tip);
        tvCostPriceUpdate = (TextView) view.findViewById(R.id.tv_cost_price_update);
        tvCostPriceClear = (TextView) view.findViewById(R.id.tv_cost_price_clear);
        tvCostPriceSave = (TextView) view.findViewById(R.id.tv_cost_price_save);

        initView();
    }

    public void setCostData(long bondsDataId,double costPrice,double amount) {
        this.bondsDataId = bondsDataId;
        if (Double.compare(costPrice,0) != 0 && Double.compare(amount,0) != 0) {
            edCurrentCostPrice.setText(StockXUtils.validDeic(costPrice));
            edCurrentAmount.setText(StockXUtils.validDeic(amount));
            edAddOpenPrice.requestFocus();
        }
    }

    public void setiCallbackCostPriceCalc(ICallbackCostPriceCalc iCallbackCostPriceCalc) {
        this.iCallbackCostPriceCalc = iCallbackCostPriceCalc;
    }

    public View getView() {
        return view;
    }

    private void initView() {
        final CostPriceTextWatcher costPriceTextWatcher = new CostPriceTextWatcher();
        edCurrentCostPrice.addTextChangedListener(costPriceTextWatcher);
        edCurrentAmount.addTextChangedListener(costPriceTextWatcher);
        edAddOpenPrice.addTextChangedListener(costPriceTextWatcher);
        edAddAmount.addTextChangedListener(costPriceTextWatcher);

        tvCostPriceUpdate.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                if ((tvCostPriceTip.getTag(R.id.tag_amount) == null) || (tvCostPriceTip.getTag(R.id.tag_cost_price) == null)) {
                    return;
                }
                double costPrice = (double) tvCostPriceTip.getTag(R.id.tag_cost_price);
                double amount = (double) tvCostPriceTip.getTag(R.id.tag_amount);

                edCurrentCostPrice.setText(StockXUtils.validDeic(costPrice));
                edCurrentAmount.setText(StockXUtils.validDeic(amount));
                edAddOpenPrice.setText("");
                edAddAmount.setText("");
                edAddOpenPrice.requestFocus();
            }
        });
        tvCostPriceClear.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                edCurrentCostPrice.setText("");
                edCurrentAmount.setText("");
                edAddOpenPrice.setText("");
                edAddAmount.setText("");
                edCurrentCostPrice.requestFocus();
            }
        });
        tvCostPriceSave.setVisibility(View.GONE);
        tvCostPriceSave.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                View view = LayoutInflater.from(context).inflate(R.layout.view_add_amount_save_dialog, null);
                final TextInputEditText edCostPrice = view.findViewById(R.id.ed_cost_price);
                final TextInputEditText edStopPrice = view.findViewById(R.id.ed_stop_price);
                RadioButton rbBreakEven = view.findViewById(R.id.rb_break_even);
                RadioButton rbBreakEven1 = view.findViewById(R.id.rb_break_even_1);

                edCostPrice.setText(StockXUtils.validDeic(costPriceTextWatcher.costPrice));
                edStopPrice.setText(StockXUtils.validDeic(costPriceTextWatcher.costPrice));
                rbBreakEven.setOnCheckedChangeListener(new CompoundButton.OnCheckedChangeListener() {
                    @Override
                    public void onCheckedChanged(CompoundButton buttonView, boolean isChecked) {
                        if (!isChecked) return;
                        edStopPrice.setText(StockXUtils.validDeic(Double.parseDouble(edCostPrice.getText().toString())));
                    }
                });
                rbBreakEven1.setOnCheckedChangeListener(new CompoundButton.OnCheckedChangeListener() {
                    @Override
                    public void onCheckedChanged(CompoundButton buttonView, boolean isChecked) {
                        if (!isChecked) return;
                        double costPrice = Double.parseDouble(edCostPrice.getText().toString());
                        edStopPrice.setText(StockXUtils.validDeic(costPrice + costPrice * 0.01));
                    }
                });

                rbBreakEven.setChecked(true);
                CommonAlertDialog commonAlertDialog = new CommonAlertDialog(context, "加仓保存", null, view, new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialog, int which) {
                        double costPrice = Double.parseDouble(edCostPrice.getText().toString());
                        double stopPrice = Double.parseDouble(edStopPrice.getText().toString());
                        if (stopPrice < costPrice) {
                            iCallbackCostPriceCalc.save(false,"保存失败，加仓必须保本!",null);
                            return;
                        }
                        BondsDataBean bondsDataBean = DaoManager.getInstance().getDaoSession().getBondsDataBeanDao().load(bondsDataId);
                        if (bondsDataBean != null) {
                            if (bondsDataBean.getCostPrice() > bondsDataBean.getStopLossPrice()) {
                                double stopMoney = (bondsDataBean.getCostPrice() - bondsDataBean.getStopLossPrice()) * bondsDataBean.getBondsNum();
                                AccountDataBean accountDataBean = DaoManager.getInstance().getDaoSession().getAccountDataBeanDao().load(bondsDataBean.getAccountId());
                                accountDataBean.setUsedRiskMoney(accountDataBean.getUsedRiskMoney() - stopMoney);
                                accountDataBean.setUsedMonthRiskMoney(accountDataBean.getUsedMonthRiskMoney() - stopMoney);
                                DaoManager.getInstance().getDaoSession().getAccountDataBeanDao().insertOrReplaceInTx(accountDataBean);
                            }
                            bondsDataBean.setCostPrice(costPrice);
                            bondsDataBean.setStopLossPrice(stopPrice);
                            bondsDataBean.setBondsNum((int) (costPriceTextWatcher.amount * 100));

                            iCallbackCostPriceCalc.save(true,"保存成功!",bondsDataBean);
                        }
                    }
                });
                commonAlertDialog.show();
            }
        });
    }

    class CostPriceTextWatcher implements TextWatcher {

        double costPrice;
        double amount;

        @Override
        public void beforeTextChanged(CharSequence s, int start, int count, int after) {

        }

        @Override
        public void onTextChanged(CharSequence s, int start, int before, int count) {

        }

        @Override
        public void afterTextChanged(Editable s) {
            String curCostPriceStr = edCurrentCostPrice.getText().toString();
            String curAmountStr = edCurrentAmount.getText().toString();
            String curAddOpenPriceStr = edAddOpenPrice.getText().toString();
            String curAddAmountStr = edAddAmount.getText().toString();

            if (TextUtils.isEmpty(curCostPriceStr) || TextUtils.isEmpty(curAmountStr) || TextUtils.isEmpty(curAddOpenPriceStr) || TextUtils.isEmpty(curAddAmountStr)) {
                tvCostPriceTip.setVisibility(View.INVISIBLE);
                if (bondsDataId != -1) {
                    tvCostPriceSave.setVisibility(View.INVISIBLE);
                }
                return;
            } else {
                tvCostPriceTip.setVisibility(View.VISIBLE);
                if (bondsDataId != -1) {
                    tvCostPriceSave.setVisibility(View.VISIBLE);
                }
            }

            double curCostPrice = Double.parseDouble(curCostPriceStr);
            double curAmount = Double.parseDouble(curAmountStr);
            double curAddOpenPrice = Double.parseDouble(curAddOpenPriceStr);
            double curAddAmount = Double.parseDouble(curAddAmountStr);

            double costPrice = (curCostPrice * curAmount + curAddOpenPrice * curAddAmount) / (curAmount + curAddAmount);
            double sum = costPrice * (curAddAmount + curAmount) * 100 + (curAddOpenPrice - curCostPrice) * curAmount * 100;
            double distance = (curAddOpenPrice - costPrice) / curAddOpenPrice * 100.0;
            this.costPrice = costPrice;
            this.amount = curAddAmount + curAmount;
            tvCostPriceTip.setTag(R.id.tag_cost_price, costPrice);
            tvCostPriceTip.setTag(R.id.tag_amount, curAmount + curAddAmount);
            tvCostPriceTip.setText(
                    "成本价变为:  " + StockXUtils.twoDeic(costPrice) + "元"
                            + "\n该股市值为:  " + StockXUtils.twoDeic(sum) + "元"
                            + "\n现价到成本价的距离:  " + StockXUtils.twoDeic(distance) + "%")
            ;
        }
    }
}
