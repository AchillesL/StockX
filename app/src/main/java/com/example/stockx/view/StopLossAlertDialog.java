package com.example.stockx.view;

import android.app.AlertDialog;
import android.content.Context;
import android.content.DialogInterface;
import android.text.Editable;
import android.text.TextUtils;
import android.text.TextWatcher;
import android.view.LayoutInflater;
import android.view.View;
import android.widget.TextView;

import com.example.stockx.R;
import com.example.stockx.bean.AbsBondsDataBean;
import com.example.stockx.bean.AccountDataBean;
import com.example.stockx.bean.BondsDataBean;
import com.example.stockx.bean.PresetBondsDataBean;
import com.example.stockx.utils.StockXUtils;
import com.google.android.material.textfield.TextInputEditText;

public class StopLossAlertDialog {
    private AlertDialog.Builder mBuilder;
    private Context context;

    private TextInputEditText edStockName;
    private TextInputEditText edStopPrice;
    private TextInputEditText edCostPrice;
    private TextInputEditText edOpenNum;
    private TextView tvOpenNumAddOne;
    private TextView tvOpenNumMinOne;
    private TextView tvOpenNumAddTen;
    private TextInputEditText edTargePrice;
    private TextView tvCalcTargePrice;
    private TextView tvClearTargePrice;
    private TextView tvTip;

    private AbsBondsDataBean absBondsDataBean;
    private BondsDataBean bondsDataBean;
    private PresetBondsDataBean presetBondsDataBean;

    public final static int MODE_FIRST_CREATE_NORMAL = 0;
    public final static int MODE_FIRST_CREATE_PRESET = 1;
    public final static int MODE_MODIFY_NORMAL = 2;
    public final static int MODE_MODIFY_PRESET = 3;

    private int mode;
    private AccountDataBean accountDataBean;

    public interface ICallbackStopLoss {
        public void refresh(boolean canAdd2DB, String msg, AccountDataBean accountDataBean, BondsDataBean bondsDataBean);
    }

    public interface ICallbackPreset {
        public void refresh(String msg, PresetBondsDataBean presetBondsDataBean);
    }

    private ICallbackStopLoss iCallbackStopLoss;
    private ICallbackPreset iCallbackPreset;

    MyTextChangedListener myTextChangedListener;

    public StopLossAlertDialog(Context context, int mode, AbsBondsDataBean absBondsDataBean, AccountDataBean accountDataBean) {
        this.mode = mode;
        this.context = context;
        this.accountDataBean = accountDataBean;
        this.absBondsDataBean = absBondsDataBean;

        View rootView = LayoutInflater.from(context).inflate(R.layout.view_add_stoploss_dialog, null);
        mBuilder = new AlertDialog.Builder(context);
        if (mode == MODE_FIRST_CREATE_NORMAL) {
            mBuilder.setTitle("创建止损单");
        } else if (mode == MODE_FIRST_CREATE_PRESET) {
            mBuilder.setTitle("创建预设单");
        } else if (mode == MODE_MODIFY_NORMAL) {
            mBuilder.setTitle("调整止损单");
        } else if (mode == MODE_MODIFY_PRESET) {
            mBuilder.setTitle("调整预设单");
        }
        mBuilder.setView(rootView);
        mBuilder.setNegativeButton(android.R.string.cancel, null);
        mBuilder.setPositiveButton(android.R.string.ok, onClickListener);

        findViews(rootView);
        initViews();
    }

    private void findViews(View rootView) {
        edStockName = (TextInputEditText) rootView.findViewById(R.id.ed_stock_name);
        edStopPrice = (TextInputEditText) rootView.findViewById(R.id.ed_stop_price);
        edCostPrice = (TextInputEditText) rootView.findViewById(R.id.ed_open_price);
        edOpenNum = (TextInputEditText) rootView.findViewById(R.id.ed_open_num);
        tvOpenNumAddOne = (TextView) rootView.findViewById(R.id.tv_open_num_add_one);
        tvOpenNumMinOne = (TextView) rootView.findViewById(R.id.tv_open_num_min_one);
        tvOpenNumAddTen = (TextView) rootView.findViewById(R.id.tv_open_num_add_ten);
        edTargePrice = (TextInputEditText) rootView.findViewById(R.id.ed_targe_price);
        tvCalcTargePrice = (TextView) rootView.findViewById(R.id.tv_calc_target_price);
        tvClearTargePrice = (TextView) rootView.findViewById(R.id.tv_clear_target_price);
        tvTip = (TextView) rootView.findViewById(R.id.tvTip);
    }

    private void initViews() {
        if (mode == MODE_MODIFY_NORMAL || mode == MODE_MODIFY_PRESET) {
            edStockName.setText(absBondsDataBean.getStockName());
            edCostPrice.setText(StockXUtils.twoDeic(absBondsDataBean.getCostPrice()));
            edStopPrice.setText(StockXUtils.twoDeic(absBondsDataBean.getStopLossPrice()));
            edOpenNum.setText(String.valueOf(absBondsDataBean.getBondsNum() / 100.0));
            edOpenNum.requestFocus();
            edOpenNum.setSelection(edOpenNum.getText().toString().length());

            if (Double.compare(absBondsDataBean.getTargetPrice(), 0) == 0) {
                edTargePrice.setText("");
            } else {
                edTargePrice.setText(StockXUtils.twoDeic(absBondsDataBean.getTargetPrice()));
            }
        } else {
            edStockName.requestFocus();
        }

        myTextChangedListener = new MyTextChangedListener(accountDataBean);
        edStopPrice.addTextChangedListener(myTextChangedListener);
        edCostPrice.addTextChangedListener(myTextChangedListener);
        edOpenNum.addTextChangedListener(myTextChangedListener);
        edTargePrice.addTextChangedListener(myTextChangedListener);

        tvCalcTargePrice.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                final CTTargetPriceCalc ctTargetPriceCalc = new CTTargetPriceCalc(context);
                ctTargetPriceCalc.isDialog(true);
                CommonAlertDialog commonAlertDialog = new CommonAlertDialog(context, "计算目标价", null, ctTargetPriceCalc.getView(),
                        new DialogInterface.OnClickListener() {
                            @Override
                            public void onClick(DialogInterface dialog, int which) {
                                if (Double.compare(ctTargetPriceCalc.getTargePrice(), 0) != 0) {
                                    edTargePrice.setText(StockXUtils.twoDeic(ctTargetPriceCalc.getTargePrice()));
                                    edTargePrice.requestFocus();
                                    edTargePrice.setSelection(edTargePrice.getText().toString().length());
                                }
                            }
                        });
                commonAlertDialog.show();
            }
        });

        tvClearTargePrice.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                edTargePrice.setText("");
            }
        });

        tvOpenNumAddOne.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                edOpenNum.requestFocus();
                double num = TextUtils.isEmpty(edOpenNum.getText().toString()) ? 0 : Double.parseDouble(edOpenNum.getText().toString());
                edOpenNum.setText(String.valueOf(num + 1));
                edOpenNum.setSelection(edOpenNum.getText().toString().length());
            }
        });
        tvOpenNumMinOne.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                edOpenNum.requestFocus();
                double num = TextUtils.isEmpty(edOpenNum.getText().toString()) ? 0 : Double.parseDouble(edOpenNum.getText().toString());
                if (num == 0) return;
                edOpenNum.setText(String.valueOf(num - 1));
                edOpenNum.setSelection(edOpenNum.getText().toString().length());
            }
        });
        tvOpenNumAddTen.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                edOpenNum.requestFocus();
                double num = TextUtils.isEmpty(edOpenNum.getText().toString()) ? 0 : Double.parseDouble(edOpenNum.getText().toString());
                edOpenNum.setText(String.valueOf(num + 10));
                edOpenNum.setSelection(edOpenNum.getText().toString().length());
            }
        });
    }

    public void show() {
        mBuilder.create().show();
    }

    class MyTextChangedListener implements TextWatcher {
        private AccountDataBean accountDataBean;
        private boolean canAdd2DB;
        private double stopMoney;

        public MyTextChangedListener(AccountDataBean accountDataBean) {
            this.accountDataBean = accountDataBean;
        }

        @Override
        public void beforeTextChanged(CharSequence charSequence, int i, int i1, int i2) {

        }

        @Override
        public void onTextChanged(CharSequence charSequence, int i, int i1, int i2) {

        }

        @Override
        public void afterTextChanged(Editable editable) {
            if (!TextUtils.isEmpty(edStopPrice.getText().toString()) && !TextUtils.isEmpty(edCostPrice.getText().toString()) && !TextUtils.isEmpty(edOpenNum.getText().toString())) {
                if (mode == MODE_FIRST_CREATE_NORMAL || mode == MODE_MODIFY_NORMAL) {
                    tvTip.setVisibility(View.VISIBLE);
                }
            } else {
                tvTip.setVisibility(View.GONE);
                return;
            }

            double stopPrice = TextUtils.isEmpty(edStopPrice.getText().toString()) ? 0 : Double.parseDouble(edStopPrice.getText().toString());
            double costPrice = TextUtils.isEmpty(edCostPrice.getText().toString()) ? 0 : Double.parseDouble(edCostPrice.getText().toString());
            double openNum = TextUtils.isEmpty(edOpenNum.getText().toString()) ? 0 : Double.parseDouble(edOpenNum.getText().toString());

            switch (mode) {
                case MODE_FIRST_CREATE_NORMAL: {
                    doWithFirstCreateByMode(stopPrice, costPrice, openNum);
                    break;
                }
                case MODE_MODIFY_NORMAL: {
                    stopMoney = (costPrice - stopPrice) * openNum * 100;
                    double originStopMoney = (bondsDataBean.getCostPrice() - bondsDataBean.getStopLossPrice()) * bondsDataBean.getBondsNum();
                    stopMoney = stopMoney - originStopMoney;
                    if (costPrice > stopPrice) {
                        if (stopMoney > (accountDataBean.getTotalMonthRiskMoney() - accountDataBean.getUsedMonthRiskMoney())) {
                            setTextViewText(true, context.getString(R.string.fail2));
                            return;
                        }
                        if (stopMoney > (accountDataBean.getTotalRiskMoney() - accountDataBean.getUsedRiskMoney())) {
                            setTextViewText(true, context.getString(R.string.fail1));
                            return;
                        }
                        int tipRes = stopMoney > 0 ? R.string.tip : R.string.tip2;
                        String tip = String.format(context.getResources().getString(tipRes),
                                StockXUtils.twoDeic(Math.abs(stopMoney)),
                                StockXUtils.twoDeic(accountDataBean.getTotalRiskMoney() - (accountDataBean.getUsedRiskMoney() + stopMoney)),
                                StockXUtils.twoDeic(accountDataBean.getUsedRiskMoney() / accountDataBean.getTotalRiskMoney() * 100.0),
                                StockXUtils.twoDeic((stopMoney + accountDataBean.getUsedRiskMoney()) / accountDataBean.getTotalRiskMoney() * 100)
                        );
                        setTextViewText(false, tip);
                        canAdd2DB = true;
                    } else {
                        if (bondsDataBean.getCostPrice() > bondsDataBean.getStopLossPrice()) {
                            stopMoney = originStopMoney * -1;
                        } else {
                            stopMoney = 0;
                        }
                        String tip = String.format(context.getResources().getString(R.string.tip2),
                                StockXUtils.twoDeic(Math.abs(stopMoney)),
                                StockXUtils.twoDeic(accountDataBean.getTotalRiskMoney() - (accountDataBean.getUsedRiskMoney() + stopMoney)),
                                StockXUtils.twoDeic(accountDataBean.getUsedRiskMoney() / accountDataBean.getTotalRiskMoney() * 100.0),
                                StockXUtils.twoDeic((stopMoney + accountDataBean.getUsedRiskMoney()) / accountDataBean.getTotalRiskMoney() * 100)
                        );
                        setTextViewText(false, tip);
                        canAdd2DB = true;
                    }
                    break;
                }
            }
        }

        private void doWithFirstCreateByMode(double stopPrice, double costPrice, double openNum) {
            if (costPrice > stopPrice) {
                stopMoney = (costPrice - stopPrice) * openNum * 100;
                double remainRiskMoney = accountDataBean.getTotalRiskMoney() - accountDataBean.getUsedRiskMoney();
                if (stopMoney > remainRiskMoney) {
                    canAdd2DB = false;
                    setTextViewText(true, context.getString(R.string.fail1));
                    return;
                }
                double remainMonthRiskMoney = accountDataBean.getTotalMonthRiskMoney() - accountDataBean.getUsedMonthRiskMoney();
                if (stopMoney > remainMonthRiskMoney) {
                    canAdd2DB = false;
                    setTextViewText(true, context.getString(R.string.fail2));
                    return;
                }

                canAdd2DB = true;
                setTextViewText(false, getTipString(stopMoney, remainRiskMoney));
            } else {
                tvTip.setVisibility(View.GONE);
                canAdd2DB = true;
            }
        }

        public void setTextViewText(boolean isFail, String tip) {
            int color = isFail ? android.R.color.holo_red_dark : android.R.color.darker_gray;
            tvTip.setTextColor(context.getResources().getColor(color));
            tvTip.setText(tip);
        }

        private String getTipString(double stopMoney, double remainRiskMoney) {
            return String.format(context.getResources().getString(R.string.tip),
                    StockXUtils.twoDeic(stopMoney),
                    StockXUtils.twoDeic(remainRiskMoney - stopMoney),
                    StockXUtils.twoDeic(accountDataBean.getUsedRiskMoney() / accountDataBean.getTotalRiskMoney() * 100),
                    StockXUtils.twoDeic((accountDataBean.getUsedRiskMoney() + stopMoney) / accountDataBean.getTotalRiskMoney() * 100));
        }

        public boolean canAdd2DB() {
            return canAdd2DB;
        }
    }

    public void setBondsDataBean(BondsDataBean bondsDataBean) {
        this.bondsDataBean = bondsDataBean;
        initViews();
    }

    public void setPresetBondsDataBean(PresetBondsDataBean presetBondsDataBean) {
        this.presetBondsDataBean = presetBondsDataBean;
        initViews();
    }

    public void setCallbackStopLoss(ICallbackStopLoss iCallbackStopLoss) {
        this.iCallbackStopLoss = iCallbackStopLoss;
    }

    public void setCallbackPreset(ICallbackPreset callbackPreset) {
        this.iCallbackPreset = callbackPreset;
    }

    DialogInterface.OnClickListener onClickListener = new DialogInterface.OnClickListener() {
        @Override
        public void onClick(DialogInterface dialogInterface, int i) {
            switch (mode) {
                case MODE_FIRST_CREATE_NORMAL:
                case MODE_MODIFY_NORMAL: {
                    if (TextUtils.isEmpty(edStockName.getText().toString()) ||
                            TextUtils.isEmpty(edCostPrice.getText().toString()) ||
                            TextUtils.isEmpty(edStopPrice.getText().toString()) ||
                            TextUtils.isEmpty(edOpenNum.getText().toString())) {
                        iCallbackStopLoss.refresh(false, "填写信息不全!", null, null);
                        return;
                    }
                    if (!myTextChangedListener.canAdd2DB()) {
                        iCallbackStopLoss.refresh(false, "超出止损额度!", null, null);
                        return;
                    }
                    BondsDataBean bondsDataBean;
                    if (StopLossAlertDialog.this.bondsDataBean != null) {
                        bondsDataBean = StopLossAlertDialog.this.bondsDataBean;
                    } else {
                        bondsDataBean = new BondsDataBean();
                    }
                    bondsDataBean.setAccountId(accountDataBean.getId());
                    bondsDataBean.setStockName(edStockName.getText().toString());
                    bondsDataBean.setStopLossPrice(Double.parseDouble(edStopPrice.getText().toString()));
                    double costPrice;
                    int amountNum;
                    costPrice = Double.parseDouble(edCostPrice.getText().toString());
                    amountNum = (int) (Double.parseDouble(edOpenNum.getText().toString()) * 100);
                    bondsDataBean.setCostPrice(costPrice);
                    bondsDataBean.setBondsNum(amountNum);
                    if (!TextUtils.isEmpty(edTargePrice.getText().toString())) {
                        bondsDataBean.setTargetPrice(Double.parseDouble(edTargePrice.getText().toString()));
                    } else {
                        bondsDataBean.setTargetPrice(0);
                    }

                    accountDataBean.setUsedRiskMoney(accountDataBean.getUsedRiskMoney() + myTextChangedListener.stopMoney);
                    accountDataBean.setUsedMonthRiskMoney(accountDataBean.getUsedMonthRiskMoney() + myTextChangedListener.stopMoney);

                    if (mode == MODE_MODIFY_NORMAL) {
                        iCallbackStopLoss.refresh(true, "修改止损单成功!", accountDataBean, bondsDataBean);
                    } else {
                        iCallbackStopLoss.refresh(true, "添加止损单成功!", accountDataBean, bondsDataBean);
                    }
                    break;
                }
                case MODE_MODIFY_PRESET:
                case MODE_FIRST_CREATE_PRESET: {
                    PresetBondsDataBean presetBondsDataBean;
                    if (StopLossAlertDialog.this.presetBondsDataBean != null) {
                        presetBondsDataBean = StopLossAlertDialog.this.presetBondsDataBean;
                    } else {
                        presetBondsDataBean = new PresetBondsDataBean();
                    }
                    presetBondsDataBean.setAccountId(accountDataBean.getId());
                    if (!TextUtils.isEmpty(edStockName.getText().toString())) {
                        presetBondsDataBean.setStockName(edStockName.getText().toString());
                    }
                    if (!TextUtils.isEmpty(edStopPrice.getText().toString())) {
                        presetBondsDataBean.setStopLossPrice(Double.parseDouble(edStopPrice.getText().toString()));
                    }
                    if (!TextUtils.isEmpty(edCostPrice.getText().toString())) {
                        double costPrice = 0;
                        costPrice = Double.parseDouble(edCostPrice.getText().toString());
                        presetBondsDataBean.setCostPrice(costPrice);
                    }
                    if (!TextUtils.isEmpty(edOpenNum.getText().toString())) {
                        int amountNum = 0;
                        amountNum = (int) (Double.parseDouble(edOpenNum.getText().toString()) * 100);
                        presetBondsDataBean.setBondsNum(amountNum);
                    }
                    if (!TextUtils.isEmpty(edTargePrice.getText().toString())) {
                        presetBondsDataBean.setTargetPrice(Double.parseDouble(edTargePrice.getText().toString()));
                    }

                    if (mode == MODE_MODIFY_PRESET) {
                        iCallbackPreset.refresh("修改预备单成功!", presetBondsDataBean);
                    } else {
                        iCallbackPreset.refresh("添加预备单成功!", presetBondsDataBean);
                    }

                    break;
                }
            }
        }
    };
}
