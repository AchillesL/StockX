package com.example.stockx.activity;

import android.content.DialogInterface;
import android.os.Bundle;
import android.text.Editable;
import android.text.TextUtils;
import android.text.TextWatcher;
import android.view.LayoutInflater;
import android.view.View;
import android.view.WindowManager;
import android.widget.CompoundButton;
import android.widget.RadioButton;
import android.widget.TextView;
import android.widget.Toast;

import androidx.appcompat.app.AppCompatActivity;

import com.example.greendao.DaoManager;
import com.example.stockx.R;
import com.example.stockx.bean.AccountDataBean;
import com.example.stockx.bean.BondsDataBean;
import com.example.stockx.utils.StockXUtils;
import com.example.stockx.view.CommonAlertDialog;
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
    private TextView tvCostPriceSave;

    //网格交易计算
    private TextInputEditText edGridTradeBasePrice;
    private RadioButton rbGridTrade35;
    private RadioButton rbGridTrade55;
    private RadioButton rbGridTrade88;
    private TextView tvGridTradeTip;
    private TextView tvGridTradeClear;

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

    long bondsDataId = 0;

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
        tvCostPriceSave = (TextView) findViewById(R.id.tv_cost_price_save);

        edGridTradeBasePrice = (TextInputEditText) findViewById(R.id.ed_grid_trade_base_price);
        rbGridTrade35 = (RadioButton) findViewById(R.id.rb_grid_trade_3_5);
        rbGridTrade55 = (RadioButton) findViewById(R.id.rb_grid_trade_5_5);
        rbGridTrade88 = (RadioButton) findViewById(R.id.rb_grid_trade_8_8);
        tvGridTradeTip = (TextView) findViewById(R.id.tv_grid_trade_tip);
        tvGridTradeClear = (TextView) findViewById(R.id.tv_grid_trade_clear);

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
        //加仓成本价计算
        final CostPriceTextWatcher costPriceTextWatcher = new CostPriceTextWatcher();
        edCurrentCostPrice.addTextChangedListener(costPriceTextWatcher);
        edCurrentAmount.addTextChangedListener(costPriceTextWatcher);
        edAddOpenPrice.addTextChangedListener(costPriceTextWatcher);
        edAddAmount.addTextChangedListener(costPriceTextWatcher);

        if (getIntent() != null) {
            double costprice = getIntent().getDoubleExtra("OPEN_PRICE", 0);
            double amount = getIntent().getIntExtra("OPEN_AMOUNT", 0) / 100.0;
            bondsDataId = getIntent().getLongExtra("BOND_DATA_ID", -1);
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
        tvCostPriceSave.setVisibility(View.GONE);
        tvCostPriceSave.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                View view = LayoutInflater.from(CommonToolsActivity.this).inflate(R.layout.view_add_amount_save_dialog, null);
                final TextInputEditText edCostPrice = view.findViewById(R.id.ed_cost_price);
                final TextInputEditText edStopPrice = view.findViewById(R.id.ed_stop_price);
                RadioButton rbBreakEven = view.findViewById(R.id.rb_break_even);
                RadioButton rbBreakEven1 = view.findViewById(R.id.rb_break_even_1);

                edCostPrice.setText(StockXUtils.twoDeic(costPriceTextWatcher.costPrice));
                edStopPrice.setText(StockXUtils.twoDeic(costPriceTextWatcher.costPrice));
                rbBreakEven.setOnCheckedChangeListener(new CompoundButton.OnCheckedChangeListener() {
                    @Override
                    public void onCheckedChanged(CompoundButton buttonView, boolean isChecked) {
                        if (!isChecked) return;
                        edStopPrice.setText(StockXUtils.twoDeic(Double.parseDouble(edCostPrice.getText().toString())));
                    }
                });
                rbBreakEven1.setOnCheckedChangeListener(new CompoundButton.OnCheckedChangeListener() {
                    @Override
                    public void onCheckedChanged(CompoundButton buttonView, boolean isChecked) {
                        if (!isChecked) return;
                        double costPrice = Double.parseDouble(edCostPrice.getText().toString());
                        edStopPrice.setText(StockXUtils.twoDeic(costPrice + costPrice * 0.01));
                    }
                });

                rbBreakEven.setChecked(true);
                CommonAlertDialog commonAlertDialog = new CommonAlertDialog(CommonToolsActivity.this, "加仓保存", null, view, new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialog, int which) {
                        double costPrice = Double.parseDouble(edCostPrice.getText().toString());
                        double stopPrice = Double.parseDouble(edStopPrice.getText().toString());
                        if (stopPrice < costPrice) {
                            Toast.makeText(CommonToolsActivity.this, "保存失败，加仓必须保本!", Toast.LENGTH_SHORT).show();
                            return;
                        }
                        BondsDataBean bondsDataBean = DaoManager.getInstance().getDaoSession().getBondsDataBeanDao().load(bondsDataId);
                        if (bondsDataBean != null) {
                            if (bondsDataBean.getOpenPrice() > bondsDataBean.getStopLossPrice()) {
                                double stopMoney = (bondsDataBean.getOpenPrice() - bondsDataBean.getStopLossPrice()) * bondsDataBean.getBondsNum();
                                AccountDataBean accountDataBean = DaoManager.getInstance().getDaoSession().getAccountDataBeanDao().load(bondsDataBean.getAccountId());
                                accountDataBean.setUsedRiskMoney(accountDataBean.getUsedRiskMoney() - stopMoney);
                                accountDataBean.setUsedMonthRiskMoney(accountDataBean.getUsedMonthRiskMoney() - stopMoney);
                                DaoManager.getInstance().getDaoSession().getAccountDataBeanDao().insertOrReplaceInTx(accountDataBean);
                            }
                            bondsDataBean.setOpenPrice(costPrice);
                            bondsDataBean.setStopLossPrice(stopPrice);
                            bondsDataBean.setBondsNum((int) (costPriceTextWatcher.amount * 100));
                            DaoManager.getInstance().getDaoSession().getBondsDataBeanDao().insertOrReplace(bondsDataBean);

                            setResult(RESULT_OK);
                            finish();
                        }
                    }
                });
                commonAlertDialog.show();
            }
        });

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

        //百分比转价格
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

        //价格转百分比
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
                    "加仓后, 成本价是:  " + StockXUtils.twoDeic(costPrice) + "元"
                            + "\n加仓后, 当前市值是:  " + StockXUtils.twoDeic(sum) + "元"
                            + "\n加仓后, 现价到成本价的距离是:  " + StockXUtils.twoDeic(distance) + "%")
            ;
        }
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
            String tip = "价格上沿是: " + StockXUtils.twoDeic(price + upRatio * price / 100) + " 元\n"
                    + "价格下沿是: " + StockXUtils.twoDeic(price - downRatio * price / 100) + " 元\n";
            tvGridTradeTip.setText(tip);
        }
    }

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