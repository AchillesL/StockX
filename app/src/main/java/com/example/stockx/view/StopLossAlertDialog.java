package com.example.stockx.view;

import android.app.AlertDialog;
import android.content.Context;
import android.content.DialogInterface;
import android.text.Editable;
import android.text.TextUtils;
import android.text.TextWatcher;
import android.view.LayoutInflater;
import android.view.View;
import android.widget.CompoundButton;
import android.widget.LinearLayout;
import android.widget.RadioButton;
import android.widget.TextView;
import android.widget.Toast;

import com.example.StockXUitls;
import com.example.greendao.DaoManager;
import com.example.stockx.R;
import com.example.stockx.bean.AccountDataBean;
import com.example.stockx.bean.BondsDataBean;
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
    private LinearLayout llOpt;
    private RadioButton rbOnlyMoveStop;
    private RadioButton rbAddAmount;
    private RadioButton rbMinAmount;
    private LinearLayout llOptNumPrice;
    private TextInputEditText edAddOrMinNumPrice;
    private TextInputEditText edAddOrMinNumAmount;
    private TextView tvTip;

    private boolean isFristCreate;
    private BondsDataBean bondsDataBean;

    private final static int MODE_FRIST_CREATE = 0;
    private final static int MODE_ONLY_MOVE_STOP = 1;
    private final static int MODE_ADD_AMOUNT = 2;
    private final static int MODE_MIN_AMOUNT = 3;

    private int mode;
    private AccountDataBean accountDataBean;

    public interface ICallback {
        public void refresh(AccountDataBean accountDataBean);
    }

    private ICallback iCallback;
    MyTextChangedListener myTextChangedListener;

    public StopLossAlertDialog(Context context, boolean isFirstCreate, BondsDataBean bondsDataBean, AccountDataBean accountDataBean) {
        this.mode = isFirstCreate ? MODE_FRIST_CREATE : MODE_ONLY_MOVE_STOP;
        this.context = context;
        this.isFristCreate = isFirstCreate;
        this.bondsDataBean = bondsDataBean;
        this.accountDataBean = accountDataBean;

        View rootView = LayoutInflater.from(context).inflate(R.layout.view_add_stoploss_dialog, null);
        mBuilder = new AlertDialog.Builder(context);
        mBuilder.setTitle(isFristCreate ? "创建止损单" : "调整止损单");
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
        llOpt = (LinearLayout) rootView.findViewById(R.id.ll_opt);
        rbOnlyMoveStop = (RadioButton) rootView.findViewById(R.id.rb_only_move_stop);
        rbAddAmount = (RadioButton) rootView.findViewById(R.id.rb_add_num);
        rbMinAmount = (RadioButton) rootView.findViewById(R.id.rb_min_num);
        llOptNumPrice = (LinearLayout) rootView.findViewById(R.id.ll_opt_num_price);
        edAddOrMinNumPrice = (TextInputEditText) rootView.findViewById(R.id.ed_add_or_min_num_price);
        edAddOrMinNumAmount = (TextInputEditText) rootView.findViewById(R.id.ed_add_or_min_num_amount);
        tvTip = (TextView) rootView.findViewById(R.id.tvTip);
    }


    private void initViews() {
        if (!isFristCreate) {
            edStockName.setText(bondsDataBean.getStockName());
            edOpenPrice.setText(StockXUitls.twoDeic(bondsDataBean.getOpenPrice()));
            edStopPrice.setText(StockXUitls.twoDeic(bondsDataBean.getStopLossPrice()));
            edOpenNum.setText(String.valueOf(bondsDataBean.getBondsNum() / 100.0));
            edOpenNum.requestFocus();
            edOpenNum.setSelection(edOpenNum.getText().toString().length());

            rbOnlyMoveStop.setOnCheckedChangeListener(new CompoundButton.OnCheckedChangeListener() {
                @Override
                public void onCheckedChanged(CompoundButton compoundButton, boolean b) {
                    if (!b) return;
                    llOptNumPrice.setVisibility(b ? View.VISIBLE : View.GONE);
                    mode = MODE_ONLY_MOVE_STOP;
                }
            });
            rbAddAmount.setOnCheckedChangeListener(new CompoundButton.OnCheckedChangeListener() {
                @Override
                public void onCheckedChanged(CompoundButton compoundButton, boolean b) {
                    if (!b) return;
                    mode = MODE_ADD_AMOUNT;
                    edAddOrMinNumPrice.setHint("加仓金额");
                    edAddOrMinNumAmount.setHint("加仓数量");
                    llOptNumPrice.setVisibility(b ? View.VISIBLE : View.GONE);
                }
            });
            rbMinAmount.setOnCheckedChangeListener(new CompoundButton.OnCheckedChangeListener() {
                @Override
                public void onCheckedChanged(CompoundButton compoundButton, boolean b) {
                    if (!b) return;
                    mode = MODE_MIN_AMOUNT;
                    edAddOrMinNumPrice.setHint("减仓金额");
                    edAddOrMinNumAmount.setHint("减仓数量");
                    llOptNumPrice.setVisibility(b ? View.VISIBLE : View.GONE);
                }
            });
        } else {
            llOpt.setVisibility(View.GONE);
        }

        myTextChangedListener = new MyTextChangedListener(accountDataBean);
        edStopPrice.addTextChangedListener(myTextChangedListener);
        edOpenPrice.addTextChangedListener(myTextChangedListener);
        edOpenNum.addTextChangedListener(myTextChangedListener);
        edAddOrMinNumPrice.addTextChangedListener(myTextChangedListener);
        edAddOrMinNumAmount.addTextChangedListener(myTextChangedListener);

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
                tvTip.setVisibility(View.VISIBLE);
            } else {
                tvTip.setVisibility(View.GONE);
                return;
            }

            double stopPrice = TextUtils.isEmpty(edStopPrice.getText().toString()) ? 0 : Double.parseDouble(edStopPrice.getText().toString());
            double openPrice = TextUtils.isEmpty(edOpenPrice.getText().toString()) ? 0 : Double.parseDouble(edOpenPrice.getText().toString());
            double openNum = TextUtils.isEmpty(edOpenNum.getText().toString()) ? 0 : Double.parseDouble(edOpenNum.getText().toString());

            switch (mode) {
                case MODE_FRIST_CREATE: {
                    doWithFirstCreateByMode(stopPrice, openPrice, openNum);
                    break;
                }
                case MODE_ONLY_MOVE_STOP: {
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
                                StockXUitls.twoDeic(Math.abs(stopMoney)),
                                StockXUitls.twoDeic(accountDataBean.getTotalRiskMoney() - (accountDataBean.getUsedRiskMoney() + stopMoney)),
                                StockXUitls.twoDeic(accountDataBean.getUsedRiskMoney() / accountDataBean.getTotalRiskMoney() * 100.0),
                                StockXUitls.twoDeic((stopMoney + accountDataBean.getUsedRiskMoney()) / accountDataBean.getTotalRiskMoney() * 100)
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
                                StockXUitls.twoDeic(Math.abs(stopMoney)),
                                StockXUitls.twoDeic(accountDataBean.getTotalRiskMoney() - (accountDataBean.getUsedRiskMoney() + stopMoney)),
                                StockXUitls.twoDeic(accountDataBean.getUsedRiskMoney() / accountDataBean.getTotalRiskMoney() * 100.0),
                                StockXUitls.twoDeic((stopMoney + accountDataBean.getUsedRiskMoney()) / accountDataBean.getTotalRiskMoney() * 100)
                        );
                        setTextViewText(false, tip);
                        canAdd2DB = true;
                    }
                    break;
                }
                case MODE_MIN_AMOUNT: {
                    double minAmountPrice = TextUtils.isEmpty(edAddOrMinNumPrice.getText()) ? 0 : Double.parseDouble(edAddOrMinNumPrice.getText().toString());
                    double minAmountNum = TextUtils.isEmpty(edAddOrMinNumAmount.getText()) ? 0 : Double.parseDouble(edAddOrMinNumAmount.getText().toString());

                    if (minAmountPrice == 0 || minAmountNum == 0) return;
                    double costPrice = (openPrice * openNum - minAmountPrice * minAmountNum) * 100 / ((openNum - minAmountNum) * 100);
                    String tip = String.format(context.getResources().getString(R.string.min_amount_tip),
                            StockXUitls.twoDeic(costPrice)
                    );
                    setTextViewText(false, tip);
                    canAdd2DB = true;
                    break;
                }
                case MODE_ADD_AMOUNT: {
                    double addAmountPrice = TextUtils.isEmpty(edAddOrMinNumPrice.getText()) ? 0 : Double.parseDouble(edAddOrMinNumPrice.getText().toString());
                    double addAmountNum = TextUtils.isEmpty(edAddOrMinNumAmount.getText()) ? 0 : Double.parseDouble(edAddOrMinNumAmount.getText().toString());

                    if (addAmountPrice == 0 || addAmountNum == 0) return;

                    double costPrice = (openPrice * openNum + addAmountPrice * addAmountNum) / (openNum + addAmountNum);

                    String tip = String.format(context.getResources().getString(R.string.add_amount_tip),
                            StockXUitls.twoDeic(costPrice)
                    );
                    setTextViewText(false, tip);
                    canAdd2DB = true;
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
                tvTip.setVisibility(View.INVISIBLE);
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
                    StockXUitls.twoDeic(stopMoney),
                    StockXUitls.twoDeic(remainRiskMoney - stopMoney),
                    StockXUitls.twoDeic(accountDataBean.getUsedRiskMoney() / accountDataBean.getTotalRiskMoney() * 100),
                    StockXUitls.twoDeic((accountDataBean.getUsedRiskMoney() + stopMoney) / accountDataBean.getTotalRiskMoney() * 100));
        }

        public boolean canAdd2DB() {
            return canAdd2DB;
        }
    }

    public void setCallback(ICallback iCallback) {
        this.iCallback = iCallback;
    }

    DialogInterface.OnClickListener onClickListener = new DialogInterface.OnClickListener() {
        @Override
        public void onClick(DialogInterface dialogInterface, int i) {
            if (!myTextChangedListener.canAdd2DB()) {
                Toast.makeText(context, "超出止损额度，添加失败!", Toast.LENGTH_SHORT).show();
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
            //成本价（开仓价）、仓位数量等，与模式，即是否第一次创建、加仓、减仓有关。
            if (mode == MODE_FRIST_CREATE) {
                costPrice = Double.valueOf(edOpenPrice.getText().toString());
                amountNum = (int) (Double.valueOf(edOpenNum.getText().toString()) * 100);
            } else if (mode == MODE_ADD_AMOUNT) {
                double addAmountPrice = TextUtils.isEmpty(edAddOrMinNumPrice.getText()) ? 0 : Double.parseDouble(edAddOrMinNumPrice.getText().toString());
                double addAmountNum = TextUtils.isEmpty(edAddOrMinNumAmount.getText()) ? 0 : Double.parseDouble(edAddOrMinNumAmount.getText().toString());

                if (addAmountPrice == 0 || addAmountNum == 0) {
                    costPrice = Double.valueOf(edOpenPrice.getText().toString());
                    amountNum = (int) (Double.valueOf(edOpenNum.getText().toString()) * 100);
                } else {
                    costPrice = (bondsDataBean.getOpenPrice() * bondsDataBean.getBondsNum() + addAmountPrice * addAmountNum * 100)
                            / (bondsDataBean.getBondsNum() + addAmountNum * 100);
                    amountNum = (int) (bondsDataBean.getBondsNum() + addAmountNum * 100);
                }
            } else if (mode == MODE_MIN_AMOUNT) {
                double minAmountPrice = TextUtils.isEmpty(edAddOrMinNumPrice.getText()) ? 0 : Double.parseDouble(edAddOrMinNumPrice.getText().toString());
                double minAmountNum = TextUtils.isEmpty(edAddOrMinNumAmount.getText()) ? 0 : Double.parseDouble(edAddOrMinNumAmount.getText().toString());

                if (minAmountPrice == 0 || minAmountNum == 0) {
                    costPrice = Double.valueOf(edOpenPrice.getText().toString());
                    amountNum = (int) (Double.valueOf(edOpenNum.getText().toString()) * 100);
                } else {
                    costPrice = (bondsDataBean.getOpenPrice() * bondsDataBean.getBondsNum() - minAmountPrice * minAmountNum * 100) /
                            ((bondsDataBean.getBondsNum() - minAmountNum * 100));
                    amountNum = (int) (bondsDataBean.getBondsNum() - minAmountNum * 100);
                }
            }
            bondsDataBean.setOpenPrice(costPrice);
            bondsDataBean.setBondsNum(amountNum);

            accountDataBean.setUsedRiskMoney(accountDataBean.getUsedRiskMoney() + myTextChangedListener.stopMoney);
            accountDataBean.setUsedMonthRiskMoney(accountDataBean.getUsedMonthRiskMoney() + myTextChangedListener.stopMoney);

            DaoManager.getInstance().getDaoSession().getAccountDataBeanDao().insertOrReplace(accountDataBean);
            DaoManager.getInstance().getDaoSession().getBondsDataBeanDao().insertOrReplace(bondsDataBean);

            iCallback.refresh(accountDataBean);
        }
    };
}
