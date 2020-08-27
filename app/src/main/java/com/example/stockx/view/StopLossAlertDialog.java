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
    private TextInputEditText edOpenPrice;
    private TextInputEditText edOpenNum;
    private TextView tvOpenNumAddOne;
    private TextView tvOpenNumMinOne;
    private TextView tvOpenNumAddTen;
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
        public void refresh(boolean canAdd2DB, AccountDataBean accountDataBean, BondsDataBean bondsDataBean);
    }

    public interface ICallbackPreset {
        public void refresh(PresetBondsDataBean presetBondsDataBean);
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
        edOpenPrice = (TextInputEditText) rootView.findViewById(R.id.ed_open_price);
        edOpenNum = (TextInputEditText) rootView.findViewById(R.id.ed_open_num);
        tvOpenNumAddOne = (TextView) rootView.findViewById(R.id.tv_open_num_add_one);
        tvOpenNumMinOne = (TextView) rootView.findViewById(R.id.tv_open_num_min_one);
        tvOpenNumAddTen = (TextView) rootView.findViewById(R.id.tv_open_num_add_ten);
        tvTip = (TextView) rootView.findViewById(R.id.tvTip);
    }


    private void initViews() {
        if (mode == MODE_MODIFY_NORMAL || mode == MODE_MODIFY_PRESET) {
            edStockName.setText(absBondsDataBean.getStockName());
            edOpenPrice.setText(StockXUtils.twoDeic(absBondsDataBean.getOpenPrice()));
            edStopPrice.setText(StockXUtils.twoDeic(absBondsDataBean.getStopLossPrice()));
            edOpenNum.setText(String.valueOf(absBondsDataBean.getBondsNum() / 100.0));
            edOpenNum.requestFocus();
            edOpenNum.setSelection(edOpenNum.getText().toString().length());
        }

        myTextChangedListener = new MyTextChangedListener(accountDataBean);
        edStopPrice.addTextChangedListener(myTextChangedListener);
        edOpenPrice.addTextChangedListener(myTextChangedListener);
        edOpenNum.addTextChangedListener(myTextChangedListener);

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
            if (!TextUtils.isEmpty(edStopPrice.getText().toString()) && !TextUtils.isEmpty(edOpenPrice.getText().toString()) && !TextUtils.isEmpty(edOpenNum.getText().toString())) {
                if (mode == MODE_FIRST_CREATE_NORMAL || mode == MODE_MODIFY_NORMAL) {
                    tvTip.setVisibility(View.VISIBLE);
                }
            } else {
                tvTip.setVisibility(View.GONE);
                return;
            }

            double stopPrice = TextUtils.isEmpty(edStopPrice.getText().toString()) ? 0 : Double.parseDouble(edStopPrice.getText().toString());
            double openPrice = TextUtils.isEmpty(edOpenPrice.getText().toString()) ? 0 : Double.parseDouble(edOpenPrice.getText().toString());
            double openNum = TextUtils.isEmpty(edOpenNum.getText().toString()) ? 0 : Double.parseDouble(edOpenNum.getText().toString());

            switch (mode) {
                case MODE_FIRST_CREATE_NORMAL: {
                    doWithFirstCreateByMode(stopPrice, openPrice, openNum);
                    break;
                }
                case MODE_MODIFY_NORMAL: {
                    stopMoney = (openPrice - stopPrice) * openNum * 100;
                    double originStopMoney = (bondsDataBean.getOpenPrice() - bondsDataBean.getStopLossPrice()) * bondsDataBean.getBondsNum();
                    stopMoney = stopMoney - originStopMoney;
                    if (openPrice > stopPrice) {
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
                        if (bondsDataBean.getOpenPrice() > bondsDataBean.getStopLossPrice()) {
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

        private void doWithFirstCreateByMode(double stopPrice, double openPrice, double openNum) {
            if (openPrice > stopPrice) {
                stopMoney = (openPrice - stopPrice) * openNum * 100;
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
                    if (!myTextChangedListener.canAdd2DB()) {
                        iCallbackStopLoss.refresh(false, null, null);
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
                    bondsDataBean.setStopLossPrice(Double.valueOf(edStopPrice.getText().toString()));
                    double costPrice = 0;
                    int amountNum = 0;
                    costPrice = Double.valueOf(edOpenPrice.getText().toString());
                    amountNum = (int) (Double.valueOf(edOpenNum.getText().toString()) * 100);
                    bondsDataBean.setOpenPrice(costPrice);
                    bondsDataBean.setBondsNum(amountNum);

                    accountDataBean.setUsedRiskMoney(accountDataBean.getUsedRiskMoney() + myTextChangedListener.stopMoney);
                    accountDataBean.setUsedMonthRiskMoney(accountDataBean.getUsedMonthRiskMoney() + myTextChangedListener.stopMoney);

                    iCallbackStopLoss.refresh(myTextChangedListener.canAdd2DB(), accountDataBean, bondsDataBean);
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
                        presetBondsDataBean.setStopLossPrice(Double.valueOf(edStopPrice.getText().toString()));
                    }
                    if (!TextUtils.isEmpty(edOpenPrice.getText().toString())) {
                        double costPrice = 0;
                        costPrice = Double.valueOf(edOpenPrice.getText().toString());
                        presetBondsDataBean.setOpenPrice(costPrice);
                    }
                    if (!TextUtils.isEmpty(edOpenNum.getText().toString())) {
                        int amountNum = 0;
                        amountNum = (int) (Double.valueOf(edOpenNum.getText().toString()) * 100);
                        presetBondsDataBean.setBondsNum(amountNum);
                    }

                    iCallbackPreset.refresh(presetBondsDataBean);
                    break;
                }
            }
        }
    };
}
