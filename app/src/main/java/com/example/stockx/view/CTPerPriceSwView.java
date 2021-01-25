package com.example.stockx.view;

import android.content.Context;
import android.text.Editable;
import android.text.TextUtils;
import android.text.TextWatcher;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;

import com.example.stockx.R;
import com.example.stockx.utils.StockXUtils;
import com.google.android.material.textfield.TextInputEditText;
import com.google.android.material.textfield.TextInputLayout;

public class CTPerPriceSwView {

    private TextView tvTitle;
    private ImageView ivSwicth;
    private TextInputEditText edInput1;
    private TextInputEditText edInput2;
    private TextInputLayout tilInput2;
    private TextView tvTip;
    private TextView tvClear;

    private View view;
    private Context context;
    private final static int MODE_PRICE_2_PER = 0;
    private final static int MODE_PER_2_PRICE = 1;

    private int mode = MODE_PRICE_2_PER;

    public CTPerPriceSwView(Context context, ViewGroup viewGroup) {
        this.context = context;
        view = LayoutInflater.from(context).inflate(R.layout.cardview_item_price_per_switch, viewGroup);
        tvTitle = (TextView) view.findViewById(R.id.tv_title);
        ivSwicth = (ImageView) view.findViewById(R.id.iv_swicth);
        edInput1 = (TextInputEditText) view.findViewById(R.id.ed_input_1);
        edInput2 = (TextInputEditText) view.findViewById(R.id.ed_input_2);
        tilInput2 = (TextInputLayout) view.findViewById(R.id.til_input_2);
        tvTip = (TextView) view.findViewById(R.id.tv_price_price_per_sw_tip);
        tvClear = (TextView) view.findViewById(R.id.tv_clear);

        initView();
    }

    private void initView() {
        ivSwicth.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (mode == MODE_PRICE_2_PER) {
                    mode = MODE_PER_2_PRICE;
                } else {
                    mode = MODE_PRICE_2_PER;
                }
                changeMode(mode);
                tvClear.performClick();
            }
        });

        MyTextWathcer myTextWathcer = new MyTextWathcer();
        edInput1.addTextChangedListener(myTextWathcer);
        edInput2.addTextChangedListener(myTextWathcer);
        changeMode(mode);

        tvClear.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                edInput1.setText("");
                edInput2.setText("");
                edInput1.requestFocus();
                tvTip.setText("");
            }
        });
    }

    private void changeMode(int mode) {
        tilInput2.setHint(mode == MODE_PRICE_2_PER ? "输入第二个价格(目标价)" : "请输入百分比");
        tvTitle.setText(mode == MODE_PRICE_2_PER ? "价格转百分比" : "百分比转价格");
    }

    class MyTextWathcer implements TextWatcher {

        @Override
        public void beforeTextChanged(CharSequence s, int start, int count, int after) {

        }

        @Override
        public void onTextChanged(CharSequence s, int start, int before, int count) {

        }

        @Override
        public void afterTextChanged(Editable s) {
            if (TextUtils.isEmpty(edInput1.getText().toString())
                    || edInput2.getText().toString().equals("-")
                    || TextUtils.isEmpty(edInput2.getText().toString())) {
                return;
            }
            double input1 = Double.parseDouble(edInput1.getText().toString());
            double input2 = Double.parseDouble(edInput2.getText().toString());

            String tip = "";
            if (mode == MODE_PRICE_2_PER) {
                tip += "目标价到基准价的距离是: " + StockXUtils.validDeic((input2 - input1) / input1 * 100) + "%";
            } else {
                tip += "目标价是: " + StockXUtils.validDeic(input1 * input2 / 100 + input1);
            }
            tvTip.setText(tip);
        }
    }

}
