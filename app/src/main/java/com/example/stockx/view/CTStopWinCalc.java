package com.example.stockx.view;

import android.content.Context;
import android.text.Editable;
import android.text.TextUtils;
import android.text.TextWatcher;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import com.example.stockx.R;
import com.example.stockx.utils.StockXUtils;
import com.google.android.material.textfield.TextInputEditText;

public class CTStopWinCalc {

    //止损金额计算
    private TextInputEditText edStopLossCostPrice;
    private TextInputEditText edStopLossStopPrice;
    private TextInputEditText edStopLossAmount;
    private TextView tvStopLossTip;
    private TextView tvStopLossClear;

    private Context context;

    public CTStopWinCalc(Context context, ViewGroup viewGroup) {
        this.context = context;
        View view = LayoutInflater.from(context).inflate(R.layout.cardview_item_stop_win_calc, viewGroup);
        edStopLossCostPrice = (TextInputEditText) view.findViewById(R.id.ed_stop_loss_cost_price);
        edStopLossStopPrice = (TextInputEditText) view.findViewById(R.id.ed_stop_loss_stop_price);
        edStopLossAmount = (TextInputEditText) view.findViewById(R.id.ed_stop_loss_amount);
        tvStopLossTip = (TextView) view.findViewById(R.id.tv_stop_loss_tip);
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
                return;
            } else {
                tvStopLossTip.setVisibility(View.VISIBLE);
            }

            double costPrice = Double.parseDouble(strCostPrice);
            double sencondPrice = Double.parseDouble(strStopPrice);
            double amount = Double.parseDouble(strAmount);

            if (sencondPrice > costPrice) {
                tvStopLossTip.setText("本次的止盈金额是: " + StockXUtils.twoDeic((sencondPrice - costPrice) * amount * 100) + " 元");
            } else {
                tvStopLossTip.setText("本次的止损金额是: " + StockXUtils.twoDeic((costPrice - sencondPrice) * amount * 100) + " 元");
            }
        }
    };



}
