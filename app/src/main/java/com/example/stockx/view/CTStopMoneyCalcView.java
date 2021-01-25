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

public class CTStopMoneyCalcView {

    //止盈价格计算
    private TextInputEditText edCostPrice;
    private TextInputEditText edCurrentAmount;
    private TextInputEditText edStopMoney;
    private TextView tvTip;
    private TextView tvClear;

    private View view;
    private Context context;

    public CTStopMoneyCalcView(Context context, ViewGroup parentView) {
        this.context = context;
        view = LayoutInflater.from(context).inflate(R.layout.cardview_item_stop_money_calc, parentView);
        edCostPrice = (TextInputEditText) view.findViewById(R.id.ed_stop_money_cost_price);
        edCurrentAmount = (TextInputEditText) view.findViewById(R.id.ed_stop_money_amount);
        edStopMoney = (TextInputEditText) view.findViewById(R.id.ed_stop_money);
        tvTip = (TextView) view.findViewById(R.id.tv_stop_win_tip);
        tvClear = (TextView) view.findViewById(R.id.tv_stop_win_clear);

        initView();
    }

    public View getView() {
        return view;
    }

    private void initView() {
        final StopMoneyCalcTextWatcher stopMoneyCalcTextWatcher = new StopMoneyCalcTextWatcher();
        edCostPrice.addTextChangedListener(stopMoneyCalcTextWatcher);
        edCurrentAmount.addTextChangedListener(stopMoneyCalcTextWatcher);
        edStopMoney.addTextChangedListener(stopMoneyCalcTextWatcher);

        tvClear.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                edCostPrice.setText("");
                edCurrentAmount.setText("");
                edStopMoney.setText("");
                edCostPrice.requestFocus();
                tvTip.setVisibility(View.INVISIBLE);
            }
        });

    }

    class StopMoneyCalcTextWatcher implements TextWatcher {

        @Override
        public void beforeTextChanged(CharSequence s, int start, int count, int after) {

        }

        @Override
        public void onTextChanged(CharSequence s, int start, int before, int count) {

        }

        @Override
        public void afterTextChanged(Editable s) {
            String costPriceStr = edCostPrice.getText().toString();
            String currentAmountStr = edCurrentAmount.getText().toString();
            String stopMoneyStr = edStopMoney.getText().toString();

            if (TextUtils.isEmpty(costPriceStr) || TextUtils.isEmpty(currentAmountStr) || TextUtils.isEmpty(stopMoneyStr)) {
                tvTip.setVisibility(View.INVISIBLE);
                return;
            }

            float costPrice = Float.parseFloat(costPriceStr);
            float currentAmount = Float.parseFloat(currentAmountStr);
            float stopMoney = Float.parseFloat(stopMoneyStr);

            float totalMoney = costPrice * currentAmount * 100 + stopMoney;
            /*在计算得到的总价上，加入0.15%的滑点、手续费处理*/
            float stopWinPrice = (float) ((totalMoney + totalMoney * 0.0015) / (currentAmount * 100));
            String tip = "获得此盈利金额，需要在价格: " + StockXUtils.twoDeic(stopWinPrice) + " 处清仓!\n";
            tvTip.setText(tip);
            tvTip.setVisibility(View.VISIBLE);
        }
    }
}
