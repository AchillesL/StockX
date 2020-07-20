package com.example.stockx.activity;

import android.os.Bundle;
import android.text.Editable;
import android.text.TextUtils;
import android.text.TextWatcher;
import android.view.View;
import android.view.WindowManager;
import android.widget.TextView;

import androidx.appcompat.app.AppCompatActivity;

import com.example.stockx.R;
import com.example.stockx.utils.StockXUtils;
import com.google.android.material.textfield.TextInputEditText;

public class CommonToolsActivity extends AppCompatActivity {

    //成本价计算
    private TextInputEditText edCurrentCostPrice;
    private TextInputEditText edCurrentAmount;
    private TextInputEditText edAddOpenPrice;
    private TextInputEditText edAddAmount;
    private TextView tvCostPriceTip;
    private TextView tvCostPriceUpdate;
    private TextView tvCostPriceClear;

    //百分比计算
    private TextInputEditText edPer2PricePrice;
    private TextInputEditText edPer2PricePer;
    private TextView tvPer2PriceTip;
    private TextView tvPer2PriceClear;

    //价格转百分比
    private TextInputEditText edPrice2PerFirstPrice;
    private TextInputEditText edPrice2PerSecondPrice;
    private TextView tvPrice2PerTip;
    private TextView tvPrice2PerClear;

    //止损金额计算
    private TextInputEditText edStopLossCostPrice;
    private TextInputEditText edStopLossStopPrice;
    private TextInputEditText edStopLossAmount;
    private TextView tvStopLossTip;
    private TextView tvStopLossClear;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_common_tools);
        getWindow().addFlags(WindowManager.LayoutParams.FLAG_KEEP_SCREEN_ON);
        setTitle("常用工具");
        findViews();
        initView();
    }

    private void findViews() {
        edCurrentCostPrice = (TextInputEditText) findViewById(R.id.ed_current_cost_price);
        edCurrentAmount = (TextInputEditText) findViewById(R.id.ed_current_amount);
        edAddOpenPrice = (TextInputEditText) findViewById(R.id.ed_add_open_price);
        edAddAmount = (TextInputEditText) findViewById(R.id.ed_add_amount);
        tvCostPriceTip = (TextView) findViewById(R.id.tv_cost_price_tip);
        tvCostPriceUpdate = (TextView) findViewById(R.id.tv_cost_price_update);
        tvCostPriceClear = (TextView) findViewById(R.id.tv_cost_price_clear);

        edPer2PricePrice = (TextInputEditText) findViewById(R.id.ed_per_2_price_price);
        edPer2PricePer = (TextInputEditText) findViewById(R.id.ed_per_2_price_per);
        tvPer2PriceTip = (TextView) findViewById(R.id.tv_per_2_price_tip);
        tvPer2PriceClear = (TextView) findViewById(R.id.tv_per_2_price_clear);

        edPrice2PerFirstPrice = (TextInputEditText) findViewById(R.id.ed_price_2_per_first_price);
        edPrice2PerSecondPrice = (TextInputEditText) findViewById(R.id.ed_price_2_per_second_price);
        tvPrice2PerTip = (TextView) findViewById(R.id.tv_price_2_per_tip);
        tvPrice2PerClear = (TextView) findViewById(R.id.tv_price_2_per_clear);

        edStopLossCostPrice = (TextInputEditText) findViewById(R.id.ed_stop_loss_cost_price);
        edStopLossStopPrice = (TextInputEditText) findViewById(R.id.ed_stop_loss_stop_price);
        edStopLossAmount = (TextInputEditText) findViewById(R.id.ed_stop_loss_amount);
        tvStopLossTip = (TextView) findViewById(R.id.tv_stop_loss_tip);
        tvStopLossClear = (TextView) findViewById(R.id.tv_stop_loss_clear);
    }

    private void initView() {
        edCurrentCostPrice.addTextChangedListener(costPriceTextWatcher);
        edCurrentAmount.addTextChangedListener(costPriceTextWatcher);
        edAddOpenPrice.addTextChangedListener(costPriceTextWatcher);
        edAddAmount.addTextChangedListener(costPriceTextWatcher);

        if (getIntent() != null) {
            double costprice = getIntent().getDoubleExtra("OPEN_PRICE", 0);
            double amount = getIntent().getIntExtra("OPEN_AMOUNT", 0) / 100.0;
            if (costprice != 0 && amount != 0) {
                edCurrentCostPrice.setText(StockXUtils.twoDeic(costprice));
                edCurrentAmount.setText(StockXUtils.twoDeic(amount));
                edAddOpenPrice.requestFocus();
            }
        }
        tvCostPriceUpdate.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                if ((tvCostPriceTip.getTag(R.id.tag_amount) == null) || (tvCostPriceTip.getTag(R.id.tag_cost_price) == null)) {
                    return;
                }
                double costPrice = (double) tvCostPriceTip.getTag(R.id.tag_cost_price);
                double amount = (double) tvCostPriceTip.getTag(R.id.tag_amount);

                edCurrentCostPrice.setText(StockXUtils.twoDeic(costPrice));
                edCurrentAmount.setText(StockXUtils.twoDeic(amount));
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

        edPer2PricePrice.addTextChangedListener(per2PriceTextWatcher);
        edPer2PricePer.addTextChangedListener(per2PriceTextWatcher);
        tvPer2PriceClear.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                edPer2PricePrice.setText("");
                edPer2PricePer.setText("");
                edPer2PricePrice.requestFocus();
            }
        });

        edPrice2PerFirstPrice.addTextChangedListener(price2PerTextWatcher);
        edPrice2PerSecondPrice.addTextChangedListener(price2PerTextWatcher);
        tvPrice2PerClear.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                edPrice2PerFirstPrice.setText("");
                edPrice2PerSecondPrice.setText("");
                edPrice2PerFirstPrice.requestFocus();
            }
        });

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

    TextWatcher costPriceTextWatcher = new TextWatcher() {
        @Override
        public void beforeTextChanged(CharSequence charSequence, int i, int i1, int i2) {

        }

        @Override
        public void onTextChanged(CharSequence charSequence, int i, int i1, int i2) {

        }

        @Override
        public void afterTextChanged(Editable editable) {
            String curCostPriceStr = edCurrentCostPrice.getText().toString();
            String curAmountStr = edCurrentAmount.getText().toString();
            String curAddOpenPriceStr = edAddOpenPrice.getText().toString();
            String curAddAmountStr = edAddAmount.getText().toString();

            if (TextUtils.isEmpty(curCostPriceStr) || TextUtils.isEmpty(curAmountStr) || TextUtils.isEmpty(curAddOpenPriceStr) || TextUtils.isEmpty(curAddAmountStr)) {
                tvCostPriceTip.setVisibility(View.INVISIBLE);
                return;
            } else {
                tvCostPriceTip.setVisibility(View.VISIBLE);
            }

            double curCostPrice = Double.parseDouble(curCostPriceStr);
            double curAmount = Double.parseDouble(curAmountStr);
            double curAddOpenPrice = Double.parseDouble(curAddOpenPriceStr);
            double curAddAmount = Double.parseDouble(curAddAmountStr);

            double costPrice = (curCostPrice * curAmount + curAddOpenPrice * curAddAmount) / (curAmount + curAddAmount);
            double sum = costPrice * (curAddAmount + curAmount) * 100 + (curAddOpenPrice - curCostPrice) * curAmount * 100;
            double distance = (curAddOpenPrice - costPrice) / curAddOpenPrice * 100.0;
            tvCostPriceTip.setTag(R.id.tag_cost_price, costPrice);
            tvCostPriceTip.setTag(R.id.tag_amount, curAmount + curAddAmount);
            tvCostPriceTip.setText(
                    "加仓后, 成本价是:  " + StockXUtils.twoDeic(costPrice) + "元"
                            + "\n加仓后, 当前市值是:  " + StockXUtils.twoDeic(sum) + "元"
                            + "\n加仓后, 现价到成本价的距离是:  " + StockXUtils.twoDeic(distance) + "%")
            ;
        }
    };

    TextWatcher per2PriceTextWatcher = new TextWatcher() {
        @Override
        public void beforeTextChanged(CharSequence charSequence, int i, int i1, int i2) {

        }

        @Override
        public void onTextChanged(CharSequence charSequence, int i, int i1, int i2) {

        }

        @Override
        public void afterTextChanged(Editable editable) {
            String curCostPricePerStr = edPer2PricePrice.getText().toString();
            String perStr = edPer2PricePer.getText().toString();
            if (TextUtils.isEmpty(curCostPricePerStr) || TextUtils.isEmpty(perStr) || perStr.equals("-")) {
                tvPer2PriceTip.setVisibility(View.INVISIBLE);
                return;
            } else {
                tvPer2PriceTip.setVisibility(View.VISIBLE);
            }

            double curCostPricePer = Double.parseDouble(curCostPricePerStr);
            double per = Double.parseDouble(perStr);
            double price = curCostPricePer;
            price = curCostPricePer + curCostPricePer * per / 100;
            tvPer2PriceTip.setText("现成本价" + ((per > 0) ? "以上" : "以下") + "个点的价格是: " + StockXUtils.twoDeic(price));
        }
    };

    TextWatcher price2PerTextWatcher = new TextWatcher() {
        @Override
        public void beforeTextChanged(CharSequence charSequence, int i, int i1, int i2) {

        }

        @Override
        public void onTextChanged(CharSequence charSequence, int i, int i1, int i2) {

        }

        @Override
        public void afterTextChanged(Editable editable) {
            String firstPriceStr = edPrice2PerFirstPrice.getText().toString();
            String secondPriceStr = edPrice2PerSecondPrice.getText().toString();
            if (TextUtils.isEmpty(firstPriceStr) || TextUtils.isEmpty(secondPriceStr)) {
                tvPrice2PerTip.setVisibility(View.INVISIBLE);
                return;
            } else {
                tvPrice2PerTip.setVisibility(View.VISIBLE);
            }

            double firstPrice = Double.parseDouble(firstPriceStr);
            double secondPrice = Double.parseDouble(secondPriceStr);

            double percent = (secondPrice - firstPrice) / firstPrice;
            tvPrice2PerTip.setText("两个价格之间相差: " + StockXUtils.twoDeic(percent * 100) + "%");
        }
    };

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