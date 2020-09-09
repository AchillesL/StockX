package com.example.stockx.view;

import android.content.Context;
import android.content.DialogInterface;
import android.text.Editable;
import android.text.TextUtils;
import android.text.TextWatcher;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.EditText;
import android.widget.TextView;
import android.widget.Toast;

import com.example.greendao.DaoManager;
import com.example.stockx.R;
import com.example.stockx.bean.PresetBondsDataBean;
import com.example.stockx.utils.StockXUtils;
import com.google.android.material.textfield.TextInputEditText;

public class CTStopWinCalc {

    //止损金额计算
    private TextInputEditText edStopLossCostPrice;
    private TextInputEditText edStopLossStopPrice;
    private TextInputEditText edStopLossAmount;
    private TextView tvStopLossTip;

    private TextView tvStopLossSave;
    private TextView tvStopLossClear;

    private Context context;
    private long accountId;

    public CTStopWinCalc(final Context context, ViewGroup viewGroup, final long accountId) {
        this.context = context;
        this.accountId = accountId;
        View view = LayoutInflater.from(context).inflate(R.layout.cardview_item_stop_win_calc, viewGroup);
        edStopLossCostPrice = (TextInputEditText) view.findViewById(R.id.ed_stop_loss_cost_price);
        edStopLossStopPrice = (TextInputEditText) view.findViewById(R.id.ed_stop_loss_stop_price);
        edStopLossAmount = (TextInputEditText) view.findViewById(R.id.ed_stop_loss_amount);
        tvStopLossTip = (TextView) view.findViewById(R.id.tv_stop_loss_tip);
        tvStopLossSave = (TextView) view.findViewById(R.id.tv_stop_loss_save);
        tvStopLossClear = (TextView) view.findViewById(R.id.tv_stop_loss_clear);

        //止盈止损金额计算
        edStopLossCostPrice.addTextChangedListener(stoplossTextWatcher);
        edStopLossStopPrice.addTextChangedListener(stoplossTextWatcher);
        edStopLossAmount.addTextChangedListener(stoplossTextWatcher);
        tvStopLossClear.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                edStopLossCostPrice.setText("");
                edStopLossStopPrice.setText("");
                edStopLossAmount.setText("");
                edStopLossCostPrice.requestFocus();
            }
        });
        tvStopLossSave.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                final EditText editText = (EditText) LayoutInflater.from(context).inflate(R.layout.view_edittext_dialog, null).findViewById(R.id.editText);
                editText.setHint("股票名称");
                editText.requestFocus();

                CommonAlertDialog commonAlertDialog = new CommonAlertDialog(context, "保存到预备单", null, editText.getRootView(), new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialogInterface, int i) {
                        if (TextUtils.isEmpty(editText.getText().toString())) return;
                        PresetBondsDataBean presetBondsDataBean = new PresetBondsDataBean();
                        presetBondsDataBean.setStockName(editText.getText().toString());
                        presetBondsDataBean.setAccountId(accountId);
                        presetBondsDataBean.setBondsNum((int) (Double.parseDouble(edStopLossAmount.getText().toString()) * 100));
                        presetBondsDataBean.setCostPrice(Double.parseDouble(edStopLossCostPrice.getText().toString()));
                        presetBondsDataBean.setStopLossPrice(Double.parseDouble(edStopLossStopPrice.getText().toString()));

                        DaoManager.getInstance().getDaoSession().getPresetBondsDataBeanDao().insert(presetBondsDataBean);
                        Toast.makeText(context, "保存到预备单成功!", Toast.LENGTH_SHORT).show();
                        tvStopLossClear.callOnClick();
                    }
                });
                commonAlertDialog.show();
            }
        });
    }

    TextWatcher stoplossTextWatcher = new TextWatcher() {
        @Override
        public void beforeTextChanged(CharSequence charSequence, int i, int i1, int i2) {

        }

        @Override
        public void onTextChanged(CharSequence charSequence, int i, int i1, int i2) {

        }

        @Override
        public void afterTextChanged(Editable editable) {
            String strCostPrice = edStopLossCostPrice.getText().toString();
            String strStopPrice = edStopLossStopPrice.getText().toString();
            String strAmount = edStopLossAmount.getText().toString();

            if (TextUtils.isEmpty(strStopPrice) || TextUtils.isEmpty(strCostPrice) || TextUtils.isEmpty(strAmount)) {
                tvStopLossTip.setVisibility(View.INVISIBLE);
                tvStopLossSave.setVisibility(View.INVISIBLE);
                return;
            } else {
                tvStopLossTip.setVisibility(View.VISIBLE);
                tvStopLossSave.setVisibility(View.VISIBLE);
            }

            double costPrice = Double.parseDouble(strCostPrice);
            double sencondPrice = Double.parseDouble(strStopPrice);
            double amount = Double.parseDouble(strAmount);

            String tip = "占用成本金额为: " + StockXUtils.validDeic(costPrice * amount * 100) + "元，\n";
            if (sencondPrice > costPrice) {
                tip += "本次的止盈金额是: " + StockXUtils.validDeic((sencondPrice - costPrice) * amount * 100) + " 元";
            } else {
                tip += "本次的止损金额是: " + StockXUtils.validDeic((costPrice - sencondPrice) * amount * 100) + " 元";
            }
            tvStopLossTip.setText(tip);
        }
    };

}
