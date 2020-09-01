package com.example.stockx.view;

import android.content.Context;
import android.text.Editable;
import android.text.TextUtils;
import android.text.TextWatcher;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.CompoundButton;
import android.widget.RadioButton;
import android.widget.TextView;

import com.example.stockx.R;
import com.example.stockx.utils.StockXUtils;
import com.google.android.material.textfield.TextInputEditText;

public class CTGridTradeView {

    //网格交易计算
    private TextInputEditText edGridTradeBasePrice;
    private RadioButton rbGridTrade35;
    private RadioButton rbGridTrade55;
    private RadioButton rbGridTrade88;
    private TextView tvGridTradeTip;
    private TextView tvGridTradeClear;

    private Context context;
    private View view;

    public CTGridTradeView(Context context, ViewGroup viewGroup) {
        view = LayoutInflater.from(context).inflate(R.layout.cardview_item_grid_trade, viewGroup);
        edGridTradeBasePrice = (TextInputEditText) view.findViewById(R.id.ed_grid_trade_base_price);
        rbGridTrade35 = (RadioButton) view.findViewById(R.id.rb_grid_trade_3_5);
        rbGridTrade55 = (RadioButton) view.findViewById(R.id.rb_grid_trade_5_5);
        rbGridTrade88 = (RadioButton) view.findViewById(R.id.rb_grid_trade_8_8);
        tvGridTradeTip = (TextView) view.findViewById(R.id.tv_grid_trade_tip);
        tvGridTradeClear = (TextView) view.findViewById(R.id.tv_grid_trade_clear);

        //网格交易计算
        final GridTradeWatcher gridTradeWatcher = new GridTradeWatcher();
        rbGridTrade35.setChecked(true);
        rbGridTrade35.setOnCheckedChangeListener(new CompoundButton.OnCheckedChangeListener() {
            @Override
            public void onCheckedChanged(CompoundButton buttonView, boolean isChecked) {
                if (!isChecked) return;
                gridTradeWatcher.setRatio(3, 5);
            }
        });
        rbGridTrade55.setOnCheckedChangeListener(new CompoundButton.OnCheckedChangeListener() {
            @Override
            public void onCheckedChanged(CompoundButton buttonView, boolean isChecked) {
                if (!isChecked) return;
                gridTradeWatcher.setRatio(5, 5);
            }
        });
        rbGridTrade88.setOnCheckedChangeListener(new CompoundButton.OnCheckedChangeListener() {
            @Override
            public void onCheckedChanged(CompoundButton buttonView, boolean isChecked) {
                if (!isChecked) return;
                gridTradeWatcher.setRatio(8, 10);
            }
        });
        edGridTradeBasePrice.addTextChangedListener(gridTradeWatcher);
        tvGridTradeClear.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                edGridTradeBasePrice.setText("");
                edGridTradeBasePrice.requestFocus();
                rbGridTrade35.setChecked(true);
                tvGridTradeTip.setText("");
            }
        });
    }

    class GridTradeWatcher implements TextWatcher {
        int downRatio = 3;
        int upRatio = 5;

        public void setRatio(int downRatio, int upRatio) {
            this.downRatio = downRatio;
            this.upRatio = upRatio;
            afterTextChanged(edGridTradeBasePrice.getEditableText());
        }

        @Override
        public void beforeTextChanged(CharSequence s, int start, int count, int after) {

        }

        @Override
        public void onTextChanged(CharSequence s, int start, int before, int count) {

        }

        @Override
        public void afterTextChanged(Editable s) {
            if (TextUtils.isEmpty(s.toString())) {
                return;
            }
            if (!rbGridTrade35.isChecked() && !rbGridTrade55.isChecked() && !rbGridTrade88.isChecked())
                return;
            double price = Double.parseDouble(s.toString());
            String tip = "价格上沿是: " + StockXUtils.validDeic(price + upRatio * price / 100) + " 元\n"
                    + "价格下沿是: " + StockXUtils.validDeic(price - downRatio * price / 100) + " 元\n";
            tvGridTradeTip.setText(tip);
        }
    }
}
