package com.example.stockx.activity;

import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.os.Bundle;
import android.text.TextUtils;
import android.view.LayoutInflater;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;
import android.view.WindowManager;
import android.widget.AdapterView;
import android.widget.BaseAdapter;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.ListView;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AlertDialog;
import androidx.appcompat.app.AppCompatActivity;

import com.example.greendao.AccountDataBeanDao;
import com.example.greendao.BondsDataBeanDao;
import com.example.greendao.DaoManager;
import com.example.greendao.SettingBeanDao;
import com.example.stockx.R;
import com.example.stockx.bean.AccountDataBean;
import com.example.stockx.bean.BondsDataBean;
import com.example.stockx.bean.SettingBean;
import com.example.stockx.utils.StockXUtils;
import com.example.stockx.view.CommonAlertDialog;
import com.example.stockx.view.ListViewHeader;
import com.example.stockx.view.StopLossAlertDialog;

import java.util.Collections;
import java.util.Comparator;
import java.util.List;

public class MainActivity extends AppCompatActivity {

    private ListView mListView;
    private TextView mEmptyView;

    private SettingBean mSettingBean;
    private SettingBeanDao mSettingBeanDao;
    private AccountDataBeanDao mAccountDataBeanDao;
    private BondsDataBeanDao mBondsDataBeanDao;

    private ListBaseAdapter mListBaseAdapter;
    private ListViewHeader mListViewHeader;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        getWindow().addFlags(WindowManager.LayoutParams.FLAG_KEEP_SCREEN_ON);

        setTitle("主界面");

        mListView = findViewById(R.id.main_listview);
        mEmptyView = findViewById(R.id.tv_empty);

        mSettingBeanDao = DaoManager.getInstance().getDaoSession().getSettingBeanDao();
        mAccountDataBeanDao = DaoManager.getInstance().getDaoSession().getAccountDataBeanDao();
        mBondsDataBeanDao = DaoManager.getInstance().getDaoSession().getBondsDataBeanDao();

        mSettingBean = mSettingBeanDao.load(0L);
        updateView();

        mListView.setOnItemLongClickListener(new AdapterView.OnItemLongClickListener() {
            @Override
            public boolean onItemLongClick(AdapterView<?> adapterView, View view, int i, long l) {
                if (i < mListView.getHeaderViewsCount()) {
                    return false;
                }
                BondsDataBean bondsDataBean = (BondsDataBean) mListBaseAdapter.getItem(i - mListView.getHeaderViewsCount());
                Intent intent = new Intent(MainActivity.this, CommonToolsActivity.class);
                intent.putExtra("OPEN_PRICE", bondsDataBean.getOpenPrice());
                intent.putExtra("OPEN_AMOUNT", bondsDataBean.getBondsNum());
                startActivity(intent);
                return false;
            }
        });
    }

    private void updateView() {
        if (mSettingBean.getCurrentAccountID() == -1) {
            mListView.setVisibility(View.GONE);
            mEmptyView.setVisibility(View.VISIBLE);
        } else {
            AccountDataBean accountDataBean = getCurrentAccountDataBean();

            mListViewHeader = new ListViewHeader(MainActivity.this, accountDataBean);
            mListView.addHeaderView(mListViewHeader.getView1());
            mListView.addHeaderView(mListViewHeader.getView2());
            mListView.setVisibility(View.VISIBLE);
            mEmptyView.setVisibility(View.GONE);

            List<BondsDataBean> bondsDataBeans = mBondsDataBeanDao.loadAll();
            mListBaseAdapter = new ListBaseAdapter(bondsDataBeans, accountDataBean, MainActivity.this);
            mListView.setAdapter(mListBaseAdapter);
        }
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        getMenuInflater().inflate(R.menu.menu, menu);
        return super.onCreateOptionsMenu(menu);
    }

    @Override
    public boolean onOptionsItemSelected(@NonNull MenuItem item) {
        switch (item.getItemId()) {
            case R.id.menu_add_stop_item: {
                if (mSettingBean.getCurrentAccountID() == -1) {
                    Toast.makeText(this, "请先创建账户!", Toast.LENGTH_SHORT).show();
                    return false;
                }
                addStopItem(true, null);
                break;
            }
            case R.id.menu_add_account: {
                addAccount();
                break;
            }
            case R.id.menu_change_account_name: {
                if (mSettingBean.getCurrentAccountID() == -1) {
                    Toast.makeText(this, "请先创建账户!", Toast.LENGTH_SHORT).show();
                    return false;
                }
                changeAccountName();
                break;
            }
            case R.id.menu_del_account: {
                if (mSettingBean.getCurrentAccountID() == -1) {
                    Toast.makeText(this, "请先创建账户!", Toast.LENGTH_SHORT).show();
                    return false;
                }
                deleteAccount();
                break;
            }
            case R.id.menu_reset_month_risk_money: {
                if (mSettingBean.getCurrentAccountID() == -1) {
                    Toast.makeText(this, "请先创建账户!", Toast.LENGTH_SHORT).show();
                    return false;
                }
                CommonAlertDialog commonAlertDialog = new CommonAlertDialog(this,
                        "提示",
                        "确定要重置每月的风险额度吗?\n不忘初心，方得始终！",
                        new DialogInterface.OnClickListener() {
                            @Override
                            public void onClick(DialogInterface dialogInterface, int i) {
                                AccountDataBean accountDataBean = getCurrentAccountDataBean();
                                double monthRiskMoney = accountDataBean.getCurrentMoney() * accountDataBean.getMonthRiskRatio() / 100.0;
                                accountDataBean.setTotalMonthRiskMoney(monthRiskMoney);

                                double usedMonthRiskMoney = 0;
                                List<BondsDataBean> bondsDataBeans = mBondsDataBeanDao.loadAll();
                                for (BondsDataBean bondsDataBean : bondsDataBeans) {
                                    if ((bondsDataBean.getAccountId() == accountDataBean.getId()) &&
                                            (bondsDataBean.getOpenPrice() > bondsDataBean.getStopLossPrice())) {
                                        usedMonthRiskMoney += (bondsDataBean.getOpenPrice() - bondsDataBean.getStopLossPrice()) * bondsDataBean.getBondsNum();
                                    }
                                }
                                accountDataBean.setUsedMonthRiskMoney(usedMonthRiskMoney);
                                accountDataBean.setUsedRiskMoney(usedMonthRiskMoney);//同步更新，以防多次运算后出错
                                mAccountDataBeanDao.insertOrReplace(accountDataBean);
                                mListViewHeader.refresh(accountDataBean);
                            }
                        }
                );
                commonAlertDialog.show();
                break;
            }
            case R.id.menu_modify_month_risk_money: {
                if (mSettingBean.getCurrentAccountID() == -1) {
                    Toast.makeText(this, "请先创建账户!", Toast.LENGTH_SHORT).show();
                    return false;
                }
                final AccountDataBean accountDataBean = getCurrentAccountDataBean();
                final EditText editText = LayoutInflater.from(MainActivity.this).inflate(R.layout.view_edittext_dialog, null).findViewById(R.id.editText);
                editText.requestFocus();
                DialogInterface.OnClickListener onClickListener = new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialogInterface, int i) {
                        double usedMonthRiskMoney = TextUtils.isEmpty(editText.getText()) ? 0 : Double.parseDouble(editText.getText().toString());
                        accountDataBean.setUsedMonthRiskMoney(usedMonthRiskMoney + accountDataBean.getUsedMonthRiskMoney());
                        mAccountDataBeanDao.insertOrReplace(accountDataBean);
                        mListViewHeader.refresh(accountDataBean);
                    }
                };
                CommonAlertDialog commonAlertDialog = new CommonAlertDialog(MainActivity.this, "添加额外已用每月风险金额",
                        null, editText.getRootView(), onClickListener);
                commonAlertDialog.show();
                break;
            }
            case R.id.menu_common_tools: {
                Intent intent = new Intent(MainActivity.this, CommonToolsActivity.class);
                startActivity(intent);
                break;
            }
            case R.id.menu_memo: {
                Intent intent = new Intent(MainActivity.this, MemoActivity.class);
                startActivity(intent);
                break;
            }
            case R.id.menu_help: {
                CommonAlertDialog commonAlertDialog = new CommonAlertDialog(MainActivity.this,
                        "帮助",
                        "该应用用于记录股票交易状况，\n\n其中:\n" +
                                "①每(多)笔亏损的单(多)笔金额不允许超过本金的2%，否则将无法开仓。\n\n" +
                                "②每个月亏损的金额不允许超过本金的6%，否则这个月将停止交易。每个月重置一次月风险金额。\n\n" +
                                "③不忘初心，方得始终! \n\n" +
                                "切记:\n" +
                                "①只持有被市场证明了是正确的仓位，已建立的仓位在得到市场证明之前应当不断减少。\n\n" +
                                "②无一例外地对每一笔被证明是正常的仓位，进行加仓操作。", null);
                commonAlertDialog.show();
                break;
            }
        }
        return super.onOptionsItemSelected(item);
    }

    private void changeAccountName() {
        final AccountDataBean accountDataBean = getCurrentAccountDataBean();
        final EditText editText = (EditText) LayoutInflater.from(this).inflate(R.layout.view_edittext_dialog, null).findViewById(R.id.editText);
        editText.requestFocus();
        editText.setText(String.valueOf(accountDataBean.getAcountName()));
        editText.setSelection(editText.getText().toString().length());

        CommonAlertDialog commonAlertDialog = new CommonAlertDialog(this, "修改当前账户名", null, editText.getRootView(), new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialogInterface, int i) {
                accountDataBean.setAcountName(editText.getText().toString());
                DaoManager.getInstance().getDaoSession().getAccountDataBeanDao().insertOrReplace(accountDataBean);
                mListViewHeader.refresh(accountDataBean);
            }
        });
        commonAlertDialog.show();
    }

    private void deleteAccount() {
        String title = "提示";
        String msg = "确定删除目前的账户吗?";
        DialogInterface.OnClickListener onClickListener = new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialogInterface, int i) {
                List<BondsDataBean> bondsDataBeans = mBondsDataBeanDao.loadAll();
                for (BondsDataBean bondsDataBean : bondsDataBeans) {
                    if (bondsDataBean.getAccountId() == mSettingBean.getCurrentAccountID()) {
                        mBondsDataBeanDao.delete(bondsDataBean);
                    }
                }
                mAccountDataBeanDao.deleteByKey(mSettingBean.getCurrentAccountID());
                mSettingBean.setCurrentAccountID(-1);
                mSettingBeanDao.insertOrReplace(mSettingBean);
                updateView();
                mListView.removeHeaderView(mListViewHeader.getView1());
                mListView.removeHeaderView(mListViewHeader.getView2());
                Toast.makeText(MainActivity.this, "删除成功!", Toast.LENGTH_SHORT).show();
            }
        };
        CommonAlertDialog commonAlertDialog = new CommonAlertDialog(this, title, msg, onClickListener);
        commonAlertDialog.show();
    }


    private void addStopItem(boolean isFirstCreate, BondsDataBean bondsDataBean) {
        StopLossAlertDialog stopLossAlertDialog = new StopLossAlertDialog(this, isFirstCreate, bondsDataBean, getCurrentAccountDataBean());
        stopLossAlertDialog.setCallback(new StopLossAlertDialog.ICallback() {
            @Override
            public void refresh(AccountDataBean accountDataBean) {
                mListViewHeader.refresh(accountDataBean);
                mListBaseAdapter.notifyDataSetChanged();
            }
        });
        stopLossAlertDialog.show();
    }

    private void addAccount() {
        AlertDialog.Builder builder = new AlertDialog.Builder(MainActivity.this);
        builder.setTitle("创建账户");
        View view = LayoutInflater.from(MainActivity.this).inflate(R.layout.view_add_account_dialog, null);
        final EditText edAddAccountName = view.findViewById(R.id.ed_add_account_name);
        final EditText edAddAccountAmount = view.findViewById(R.id.ed_add_account_amount);

        builder.setPositiveButton(android.R.string.ok, new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialogInterface, int i) {
                AccountDataBean accountDataBean = new AccountDataBean();
                accountDataBean.setAcountName(edAddAccountName.getEditableText().toString());
                //账户权益
                double currentAmount = Double.parseDouble(edAddAccountAmount.getEditableText().toString());
                accountDataBean.setCurrentMoney(currentAmount);
                double riskRatio = 2;
                accountDataBean.setRiskRatio(riskRatio);
                //每个月的风险资金（每个月若亏掉这么多资金，本月停止交易）
                double monthRiskRatio = riskRatio * 3;
                accountDataBean.setMonthRiskRatio(monthRiskRatio);
                accountDataBean.setUsedMonthRiskMoney(0);
                accountDataBean.setTotalMonthRiskMoney(currentAmount * monthRiskRatio / 100.0);
                //单次的风险资金
                double totalRiskAmount = currentAmount * riskRatio / 100.0;
                accountDataBean.setTotalRiskMoney(totalRiskAmount);
                accountDataBean.setUsedRiskMoney(0);

                mAccountDataBeanDao.insert(accountDataBean);
                List<AccountDataBean> accountDataBeans = mAccountDataBeanDao.loadAll();
                mSettingBean.setCurrentAccountID(accountDataBeans.get(accountDataBeans.size() - 1).getId());
                mSettingBeanDao.insertOrReplace(mSettingBean);
                updateView();
            }
        });
        builder.setNegativeButton(android.R.string.cancel, null);
        builder.setView(view);
        builder.create().show();
    }

    class ListBaseAdapter extends BaseAdapter {
        private List<BondsDataBean> bondsDataBeans;
        private AccountDataBean accountDataBean;
        private Context context;

        public ListBaseAdapter(List<BondsDataBean> bondsDataBeans, AccountDataBean accountDataBean, Context context) {
            this.bondsDataBeans = sort(bondsDataBeans);
            this.accountDataBean = accountDataBean;
            this.context = context;
        }

        private List<BondsDataBean> sort(List<BondsDataBean> bondsDataBeans) {
            Collections.sort(bondsDataBeans, new Comparator<BondsDataBean>() {
                @Override
                public int compare(BondsDataBean b1, BondsDataBean b2) {
                    double b1StopMoney = (b1.getStopLossPrice() - b1.getOpenPrice()) * b1.getBondsNum();
                    double b2StopMoney = (b2.getStopLossPrice() - b2.getOpenPrice()) * b2.getBondsNum();
                    return b1StopMoney > b2StopMoney ? -1 : 1;
                }
            });
            return bondsDataBeans;
        }


        @Override
        public int getCount() {
            return bondsDataBeans.size();
        }

        @Override
        public Object getItem(int i) {
            return bondsDataBeans.get(i);
        }

        @Override
        public long getItemId(int i) {
            return 0;
        }

        @Override
        public void notifyDataSetChanged() {
            super.notifyDataSetChanged();
            bondsDataBeans = mBondsDataBeanDao.loadAll();
            bondsDataBeans = sort(bondsDataBeans);
        }

        @Override
        public View getView(int i, View view, ViewGroup viewGroup) {
            final BondsDataBean bondsDataBean = (BondsDataBean) getItem(i);
            if (view == null) {
                view = LayoutInflater.from(context).inflate(R.layout.view_listview_item, null);
            }

            TextView tvStockName = (TextView) view.findViewById(R.id.tv_stock_name);
            ImageView ivSafeIcon = (ImageView) view.findViewById(R.id.iv_safe_icon);
            TextView tvOpenPrice = (TextView) view.findViewById(R.id.tv_open_price);
            TextView tvStopPrice = (TextView) view.findViewById(R.id.tv_stop_price);
            TextView tvOpenNum = (TextView) view.findViewById(R.id.tv_open_num);
            TextView tvStopWinMoney = (TextView) view.findViewById(R.id.tv_stop_win_money);
            TextView tvRiskPercent = (TextView) view.findViewById(R.id.tv_risk_percent);
            ImageView ivModify = (ImageView) view.findViewById(R.id.iv_modify);
            ImageView ivDelete = (ImageView) view.findViewById(R.id.iv_delete);

            tvStockName.setText(bondsDataBean.getStockName());

            final double openPrice = bondsDataBean.getOpenPrice();
            final double stopPrice = bondsDataBean.getStopLossPrice();
            tvOpenPrice.setText(String.format(getResources().getString(R.string.open_price), StockXUtils.twoDeic(openPrice)));
            tvStopPrice.setText(String.format(getResources().getString(R.string.stop_price), StockXUtils.twoDeic(stopPrice)));

            double stopOrWinMoney = Math.abs(openPrice - stopPrice) * bondsDataBean.getBondsNum();
            if (stopPrice >= openPrice) {
                ivSafeIcon.setVisibility(View.VISIBLE);
                tvStopWinMoney.setTextColor(getResources().getColor(android.R.color.holo_red_dark));
                tvStopWinMoney.setText(String.format(getResources().getString(R.string.win_money), StockXUtils.twoDeic(stopOrWinMoney)));
            } else {
                ivSafeIcon.setVisibility(View.INVISIBLE);
                tvStopWinMoney.setTextColor(getResources().getColor(android.R.color.holo_blue_dark));
                tvStopWinMoney.setText(String.format(getResources().getString(R.string.stop_money), StockXUtils.twoDeic(stopOrWinMoney)));
            }

            double riskPercent = stopPrice >= openPrice ? 0 : (openPrice - stopPrice) * bondsDataBean.getBondsNum() / accountDataBean.getTotalRiskMoney() * 100.0;
            tvRiskPercent.setText(String.format(getResources().getString(R.string.risk_percent), StockXUtils.twoDeic(riskPercent)));
            tvOpenNum.setText(String.format(getResources().getString(R.string.open_num), bondsDataBean.getBondsNum()));

            ivModify.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View view) {
                    addStopItem(false, bondsDataBean);
                }
            });

            ivDelete.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View view) {
                    deleteBondsItem(openPrice, stopPrice, bondsDataBean);
                }
            });
            return view;
        }

        private void deleteBondsItem(final double openPrice, final double stopPrice, final BondsDataBean bondsDataBean) {
            String title = "提示";
            String msg = "确定删除目前的记录吗?";

            final double stopLossMoney = (openPrice - stopPrice) * bondsDataBean.getBondsNum();
            final boolean needUpdateRiskMoney = stopLossMoney > 0;

            final EditText editText = (EditText) LayoutInflater.from(MainActivity.this).inflate(R.layout.view_edittext_dialog, null).findViewById(R.id.editText);
            CommonAlertDialog commonAlertDialog = null;
            DialogInterface.OnClickListener onClickListener = new DialogInterface.OnClickListener() {
                @Override
                public void onClick(DialogInterface dialogInterface, int i) {
                    if (needUpdateRiskMoney) {
                        AccountDataBean accountDataBean = getCurrentAccountDataBean();
                        double stopMoney = (openPrice - stopPrice) * bondsDataBean.getBondsNum();
                        accountDataBean.setUsedRiskMoney(accountDataBean.getUsedRiskMoney() - stopMoney);

                        double realStopMoney = TextUtils.isEmpty(editText.getText()) ? 0 : Double.parseDouble(editText.getText().toString());
                        if (realStopMoney >= 0) {
                            accountDataBean.setUsedMonthRiskMoney(accountDataBean.getUsedMonthRiskMoney() - (stopLossMoney - realStopMoney));
                        }

                        mListViewHeader.refresh(accountDataBean);
                        mAccountDataBeanDao.insertOrReplace(accountDataBean);
                    }
                    mBondsDataBeanDao.delete(bondsDataBean);
                    notifyDataSetChanged();
                }
            };
            if (needUpdateRiskMoney) {
                editText.setText(StockXUtils.twoDeic(stopLossMoney));
                editText.requestFocus();
                editText.setSelection(editText.getText().toString().length());
                commonAlertDialog = new CommonAlertDialog(MainActivity.this, title, null, editText.getRootView(), onClickListener);
            } else {
                commonAlertDialog = new CommonAlertDialog(MainActivity.this, title, msg, onClickListener);
            }
            commonAlertDialog.show();
        }
    }


    public AccountDataBean getCurrentAccountDataBean() {
        if (mSettingBean.getCurrentAccountID() == -1) return null;
        return mAccountDataBeanDao.load(mSettingBean.getCurrentAccountID());
    }
}