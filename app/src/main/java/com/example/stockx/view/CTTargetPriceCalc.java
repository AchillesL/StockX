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

public class CTTargetPriceCalc {
    private View view;
    private TextView tvTitle;
    private TextInputEditText edHighPrice;
    private TextInputEditText edLowPrice;
    private TextView tvTip;
    private RadioButton rbRatio1;
    private RadioButton rbRatio2;
    private RadioButton rbRatio3;
    private TextView tvClear;
    private MyTextWather myTextWather;

    private Context context;

    private final static int MODE_HIGH = 0;
    private final static int MODE_MID = 1;
    private final static int MODE_LOW = 2;

    private int mode = MODE_MID;

    public CTTargetPriceCalc(Context context) {
        this.context = context;
        view = LayoutInflater.from(context).inflate(R.layout.view_calc_target_price_dialog, null);
        findViews(view);
    }

    public CTTargetPriceCalc(Context context, ViewGroup viewGroup) {
        this.context = context;
        view = LayoutInflater.from(context).inflate(R.layout.view_calc_target_price_dialog, viewGroup);
        findViews(view);
    }

    private void findViews(View view) {
        tvTitle = (TextView) view.findViewById(R.id.tv_title_target_price);
        edHighPrice = (TextInputEditText) view.findViewById(R.id.ed_high_price);
        edLowPrice = (TextInputEditText) view.findViewById(R.id.ed_low_price);
        rbRatio1 = (RadioButton) view.findViewById(R.id.rb_ratio_1);
        rbRatio2 = (RadioButton) view.findViewById(R.id.rb_ratio_2);
        rbRatio3 = (RadioButton) view.findViewById(R.id.rb_ratio_3);
        tvTip = (TextView) view.findViewById(R.id.tv_tip);
        tvClear = (TextView) view.findViewById(R.id.tv_clear_target_price);

        myTextWather = new MyTextWather();
        edHighPrice.addTextChangedListener(myTextWather);
        edLowPrice.addTextChangedListener(myTextWather);

        rbRatio1.setText(StockXUtils.TARGET_PRICE_RATIO_LOW + "%");
        rbRatio2.setText(StockXUtils.TARGET_PRICE_RATIO_MID + "%");
        rbRatio3.setText(StockXUtils.TARGET_PRICE_RATIO_HIGH + "%");
        rbRatio2.setChecked(true);

        rbRatio1.setOnCheckedChangeListener(new CompoundButton.OnCheckedChangeListener() {
            @Override
            public void onCheckedChanged(CompoundButton buttonView, boolean isChecked) {
                if (isChecked) mode = MODE_LOW;
                myTextWather.trigger();
            }
        });
        rbRatio2.setOnCheckedChangeListener(new CompoundButton.OnCheckedChangeListener() {
            @Override
            public void onCheckedChanged(CompoundButton buttonView, boolean isChecked) {
                if (isChecked) mode = MODE_MID;
                myTextWather.trigger();
            }
        });
        rbRatio3.setOnCheckedChangeListener(new CompoundButton.OnCheckedChangeListener() {
            @Override
            public void onCheckedChanged(CompoundButton buttonView, boolean isChecked) {
                if (isChecked) mode = MODE_HIGH;
                myTextWather.trigger();
            }
        });

        tvClear.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                edHighPrice.setText("");
                edLowPrice.setText("");
                rbRatio2.setChecked(true);
                edHighPrice.requestFocus();
            }
        });
    }

    class MyTextWather implements TextWatcher {

        double result;

        @Override
        public void beforeTextChanged(CharSequence s, int start, int count, int after) {

        }

        @Override
        public void onTextChanged(CharSequence s, int start, int before, int count) {

        }

        @Override
        public void afterTextChanged(Editable s) {
            trigger();
        }

        public void trigger() {
            if (!TextUtils.isEmpty(edHighPrice.getText().toString()) && !TextUtils.isEmpty(edLowPrice.getText().toString())) {
                tvTip.setVisibility(View.VISIBLE);
            } else {
                tvTip.setVisibility(View.INVISIBLE);
                return;
            }
            double highPrice = Double.parseDouble(edHighPrice.getText().toString());
            double lowPrice = Double.parseDouble(edLowPrice.getText().toString());

            switch (mode) {
                case MODE_LOW:
                    result = (highPrice - lowPrice) * StockXUtils.TARGET_PRICE_RATIO_LOW / 100.0 + highPrice;
                    break;
                case MODE_MID:
                    result = (highPrice - lowPrice) * StockXUtils.TARGET_PRICE_RATIO_MID / 100.0 + highPrice;
                    break;
                case MODE_HIGH:
                    result = (highPrice - lowPrice) * StockXUtils.TARGET_PRICE_RATIO_HIGH / 100.0 + highPrice;
                    break;
            }
            tvTip.setText("目标价是: " + StockXUtils.twoDeic(result));
        }
    }

    public double getTargePrice() {
        return myTextWather.result;
    }

    public View getView() {
        return view;
    }

    public void isDialog(boolean b) {
        tvTitle.setVisibility(b ? View.GONE : View.VISIBLE);
        tvClear.setVisibility(b ? View.GONE : View.VISIBLE);
        if (b) {
            edHighPrice.requestFocus();
        }
    }
}