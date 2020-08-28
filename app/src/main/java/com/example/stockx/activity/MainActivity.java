package com.example.stockx.activity;

import android.content.DialogInterface;
import android.content.Intent;
import android.os.Bundle;
import android.text.TextUtils;
import android.view.LayoutInflater;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.view.WindowManager;
import android.widget.AdapterView;
import android.widget.EditText;
import android.widget.LinearLayout;
import android.widget.ListView;
import android.widget.RadioButton;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.appcompat.app.AlertDialog;
import androidx.appcompat.app.AppCompatActivity;

import com.example.greendao.AccountDataBeanDao;
import com.example.greendao.BondsDataBeanDao;
import com.example.greendao.DaoManager;
import com.example.greendao.SettingBeanDao;
import com.example.stockx.R;
import com.example.stockx.bean.AbsBondsDataBean;
import com.example.stockx.bean.AccountDataBean;
import com.example.stockx.bean.BondsDataBean;
import com.example.stockx.bean.SettingBean;
import com.example.stockx.utils.ListBaseAdapter;
import com.example.stockx.utils.StockXUtils;
import com.example.stockx.view.CommonAlertDialog;
import com.example.stockx.view.ListViewHeader;
import com.example.stockx.view.StopLossAlertDialog;

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

    public static final String S_ID_ACCOUNT = "S_ID_ACCOUNT";
    public static final int REQUEST_CODE_PRESET = 0;
    public static final int REQUEST_CODE_TOOL_COST_PRICE = 1;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        getWindow().addFlags(WindowManager.LayoutParams.FLAG_KEEP_SCREEN_ON);

        setTitle("");

        mListView = findViewById(R.id.main_listview);
        mEmptyView = findViewById(R.id.tv_empty);

        mSettingBeanDao = DaoManager.getInstance().getDaoSession().getSettingBeanDao();
        mAccountDataBeanDao = DaoManager.getInstance().getDaoSession().getAccountDataBeanDao();
        mBondsDataBeanDao = DaoManager.getInstance().getDaoSession().getBondsDataBeanDao();

        mSettingBean = mSettingBeanDao.load(0L);

        mListView.setOnItemLongClickListener(new AdapterView.OnItemLongClickListener() {
            @Override
            public boolean onItemLongClick(AdapterView<?> adapterView, View view, int i, long l) {
                if (i < mListView.getHeaderViewsCount()) {
                    return false;
                }
                BondsDataBean bondsDataBean = (BondsDataBean) mListBaseAdapter.getItem(i - mListView.getHeaderViewsCount());
                Intent intent = new Intent(MainActivity.this, CommonToolsActivity.class);
                intent.putExtra("OPEN_PRICE", bondsDataBean.getCostPrice());
                intent.putExtra("OPEN_AMOUNT", bondsDataBean.getBondsNum());
                intent.putExtra("BOND_DATA_ID", bondsDataBean.getId());
                startActivityForResult(intent, REQUEST_CODE_TOOL_COST_PRICE);
                return false;
            }
        });

        updateView();
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

            List<BondsDataBean> bondsDataBeans = mBondsDataBeanDao.queryRaw("where ACCOUNT_ID = ?", String.valueOf(mSettingBean.getCurrentAccountID()));
            mListBaseAdapter = new ListBaseAdapter(StockXUtils.bondsData2AbsData(bondsDataBeans), accountDataBean, MainActivity.this);
            setListBaseAdapterCallback();
            mListView.setAdapter(mListBaseAdapter);
        }
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        getMenuInflater().inflate(R.menu.menu_main_activity, menu);
        return super.onCreateOptionsMenu(menu);
    }

    @Override
    public boolean onOptionsItemSelected(@NonNull MenuItem item) {
        int itemId = item.getItemId();
        if (itemId != R.id.menu_add_account && mSettingBean.getCurrentAccountID() == -1) {
            Toast.makeText(this, "请先创建账户!", Toast.LENGTH_SHORT).show();
            return false;
        }
        switch (item.getItemId()) {
            case R.id.menu_add_preset_item: {
                Intent intent = new Intent(MainActivity.this, PresetActivity.class);
                intent.putExtra(S_ID_ACCOUNT, (mSettingBean.getCurrentAccountID()));
                startActivityForResult(intent, REQUEST_CODE_PRESET);
                break;
            }
            case R.id.menu_add_stop_item: {
                addStopItem(StopLossAlertDialog.MODE_FIRST_CREATE_NORMAL, null);
                break;
            }
            case R.id.menu_add_account: {
                addAccount();
                break;
            }
            case R.id.menu_change_account_name: {
                changeAccountName();
                break;
            }
            case R.id.menu_del_account: {
                deleteAccount();
                break;
            }
            case R.id.menu_modify_risk_ratio: {
                modifyRiskRatio();
                break;
            }
            case R.id.menu_reset_month_risk_money: {
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
                                List<BondsDataBean> bondsDataBeans = mBondsDataBeanDao.queryRaw("where ACCOUNT_ID = ?", String.valueOf(mSettingBean.getCurrentAccountID()));
                                for (BondsDataBean bondsDataBean : bondsDataBeans) {
                                    if (bondsDataBean.getCostPrice() > bondsDataBean.getStopLossPrice()) {
                                        usedMonthRiskMoney += (bondsDataBean.getCostPrice() - bondsDataBean.getStopLossPrice()) * bondsDataBean.getBondsNum();
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
                        "该应用用于记录股票交易状况\n", null);
                commonAlertDialog.show();
                break;
            }
        }
        return super.onOptionsItemSelected(item);
    }

    private void modifyRiskRatio() {
        final AccountDataBean accountDataBean = getCurrentAccountDataBean();
        final LinearLayout linearLayout = (LinearLayout) LayoutInflater.from(this).inflate(R.layout.view_modify_risk_ratio_dialog, null);
        final RadioButton rbRatio1 = linearLayout.findViewById(R.id.rb_ratio_1);
        final RadioButton rbRatio2 = linearLayout.findViewById(R.id.rb_ratio_2);
        RadioButton rbRatio3 = linearLayout.findViewById(R.id.rb_ratio_3);

        if (accountDataBean.getRiskRatio() == StockXUtils.RISK_RATIO_1) {
            rbRatio1.setChecked(true);
        } else if (accountDataBean.getRiskRatio() == StockXUtils.RISK_RATIO_2) {
            rbRatio2.setChecked(true);
        } else {
            rbRatio3.setChecked(true);
        }

        //设置限制
        if (accountDataBean.getUsedRiskMoney() > accountDataBean.getCurrentMoney() * StockXUtils.RISK_RATIO_1 / 100.0) {
            rbRatio1.setEnabled(false);
        }
        if (accountDataBean.getUsedRiskMoney() > accountDataBean.getCurrentMoney() * StockXUtils.RISK_RATIO_2 / 100.0) {
            rbRatio2.setEnabled(false);
        }
        if (accountDataBean.getUsedRiskMoney() > accountDataBean.getCurrentMoney() * StockXUtils.RISK_RATIO_3 / 100.0) {
            rbRatio3.setEnabled(false);
        }

        if (accountDataBean.getUsedMonthRiskMoney() > accountDataBean.getCurrentMoney() * StockXUtils.RISK_RATIO_1 * 3 / 100.0) {
            rbRatio1.setEnabled(false);
        }
        if (accountDataBean.getUsedMonthRiskMoney() > accountDataBean.getCurrentMoney() * StockXUtils.RISK_RATIO_2 * 3 / 100.0) {
            rbRatio2.setEnabled(false);
        }
        if (accountDataBean.getUsedMonthRiskMoney() > accountDataBean.getCurrentMoney() * StockXUtils.RISK_RATIO_3 * 3 / 100.0) {
            rbRatio3.setEnabled(false);
        }

        CommonAlertDialog commonAlertDialog = new CommonAlertDialog(this, "修改风险系数", null, linearLayout, new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialogInterface, int i) {
                double riskRatio = rbRatio1.isChecked() ? StockXUtils.RISK_RATIO_1 : rbRatio2.isChecked() ? StockXUtils.RISK_RATIO_2 : StockXUtils.RISK_RATIO_3;
                accountDataBean.setRiskRatio(riskRatio);
                accountDataBean.setMonthRiskRatio(riskRatio * 3);

                accountDataBean.setTotalRiskMoney(accountDataBean.getCurrentMoney() * riskRatio / 100.0);
                accountDataBean.setTotalMonthRiskMoney(accountDataBean.getCurrentMoney() * riskRatio * 3.0 / 100.0);

                DaoManager.getInstance().getDaoSession().getAccountDataBeanDao().insertOrReplace(accountDataBean);
                mListViewHeader.refresh(accountDataBean);
            }
        });
        commonAlertDialog.show();
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
                List<BondsDataBean> bondsDataBeans = mBondsDataBeanDao.queryRaw("where ACCOUNT_ID = ?", String.valueOf(mSettingBean.getCurrentAccountID()));
                for (BondsDataBean bondsDataBean : bondsDataBeans) {
                    mBondsDataBeanDao.delete(bondsDataBean);
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


    private void addStopItem(int mode, BondsDataBean bondsDataBean) {
        StopLossAlertDialog stopLossAlertDialog = new StopLossAlertDialog(this, mode, bondsDataBean, getCurrentAccountDataBean());
        if (mode == StopLossAlertDialog.MODE_MODIFY_NORMAL) {
            stopLossAlertDialog.setBondsDataBean(bondsDataBean);
        }
        stopLossAlertDialog.setCallbackStopLoss(new StopLossAlertDialog.ICallbackStopLoss() {
            @Override
            public void refresh(boolean canAdd2DB, String msg, AccountDataBean accountDataBean, BondsDataBean bondsDataBean) {
                Toast.makeText(MainActivity.this, msg, Toast.LENGTH_SHORT).show();
                if (!canAdd2DB) {
                    return;
                }

                DaoManager.getInstance().getDaoSession().getAccountDataBeanDao().insertOrReplace(accountDataBean);
                DaoManager.getInstance().getDaoSession().getBondsDataBeanDao().insertOrReplace(bondsDataBean);

                mListViewHeader.refresh(accountDataBean);
                updateListBaseAdapter();
            }
        });
        stopLossAlertDialog.show();
    }

    private void updateListBaseAdapter() {
        List<BondsDataBean> bondsDataBeans = mBondsDataBeanDao.queryRaw("where ACCOUNT_ID = ?", String.valueOf(mSettingBean.getCurrentAccountID()));
        mListBaseAdapter.update(StockXUtils.bondsData2AbsData(bondsDataBeans));
    }

    private void addAccount() {
        AlertDialog.Builder builder = new AlertDialog.Builder(MainActivity.this);
        builder.setTitle("创建账户");
        View view = LayoutInflater.from(MainActivity.this).inflate(R.layout.view_add_account_dialog, null);
        final EditText edAddAccountName = view.findViewById(R.id.ed_add_account_name);
        final EditText edAddAccountAmount = view.findViewById(R.id.ed_add_account_amount);

        final RadioButton rbRatio1 = view.findViewById(R.id.rb_ratio_1);
        final RadioButton rbRatio2 = view.findViewById(R.id.rb_ratio_2);
        final RadioButton rbRatio3 = view.findViewById(R.id.rb_ratio_3);

        builder.setPositiveButton(android.R.string.ok, new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialogInterface, int i) {
                AccountDataBean accountDataBean = new AccountDataBean();
                accountDataBean.setAcountName(edAddAccountName.getEditableText().toString());
                //账户权益
                double currentAmount = Double.parseDouble(edAddAccountAmount.getEditableText().toString());
                accountDataBean.setCurrentMoney(currentAmount);
                double riskRatio = rbRatio1.isChecked() ? StockXUtils.RISK_RATIO_1 : (rbRatio2.isChecked() ? StockXUtils.RISK_RATIO_2 : StockXUtils.RISK_RATIO_3);
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

    private void setListBaseAdapterCallback() {
        mListBaseAdapter.setICallbackStopLoss(new ListBaseAdapter.ICallbackStopLoss() {
            @Override
            public void modify(AbsBondsDataBean absBondsDataBean) {
                addStopItem(StopLossAlertDialog.MODE_MODIFY_NORMAL, (BondsDataBean) absBondsDataBean);
            }

            @Override
            public void delete(double costPrice, double stopPrice, AbsBondsDataBean absBondsDataBean) {
                deleteBondsItem(costPrice, stopPrice, (BondsDataBean) absBondsDataBean);
            }
        });
    }

    private void deleteBondsItem(final double costPrice, final double stopPrice, final BondsDataBean bondsDataBean) {
        String title = "提示";
        String msg = "确定删除目前的记录吗?";

        final double stopLossMoney = (costPrice - stopPrice) * bondsDataBean.getBondsNum();
        final boolean needUpdateRiskMoney = stopLossMoney > 0;

        final EditText editText = (EditText) LayoutInflater.from(MainActivity.this).inflate(R.layout.view_edittext_dialog, null).findViewById(R.id.editText);
        CommonAlertDialog commonAlertDialog = null;
        DialogInterface.OnClickListener onClickListener = new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialogInterface, int i) {
                if (needUpdateRiskMoney) {
                    AccountDataBean accountDataBean = getCurrentAccountDataBean();
                    double stopMoney = (costPrice - stopPrice) * bondsDataBean.getBondsNum();
                    accountDataBean.setUsedRiskMoney(accountDataBean.getUsedRiskMoney() - stopMoney);

                    double realStopMoney = TextUtils.isEmpty(editText.getText()) ? 0 : Double.parseDouble(editText.getText().toString());
                    if (realStopMoney >= 0) {
                        accountDataBean.setUsedMonthRiskMoney(accountDataBean.getUsedMonthRiskMoney() - (stopLossMoney - realStopMoney));
                    }

                    mAccountDataBeanDao.insertOrReplace(accountDataBean);
                }
                mBondsDataBeanDao.delete(bondsDataBean);
                mListViewHeader.refresh(getCurrentAccountDataBean());

                updateListBaseAdapter();
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

    public AccountDataBean getCurrentAccountDataBean() {
        if (mSettingBean.getCurrentAccountID() == -1) return null;
        return mAccountDataBeanDao.load(mSettingBean.getCurrentAccountID());
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, @Nullable Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        switch (requestCode) {
            case REQUEST_CODE_PRESET:
            case REQUEST_CODE_TOOL_COST_PRICE: {
                if (resultCode != RESULT_OK) return;
                updateListBaseAdapter();
                mListViewHeader.refresh(getCurrentAccountDataBean());
                break;
            }
        }
    }

    private void updateAccountDataAndListHeader(int accountID) {
        List<BondsDataBean> bondsDataBeans = DaoManager.getInstance().getDaoSession().getBondsDataBeanDao()
                .queryRaw("where ACCOUNT_ID = ?", String.valueOf(mSettingBean.getCurrentAccountID()));
        double usedRiskMoney = 0;

        for (BondsDataBean bondsDataBean : bondsDataBeans) {
            if (bondsDataBean.getCostPrice() > bondsDataBean.getStopLossPrice()) {
                usedRiskMoney += (bondsDataBean.getCostPrice() - bondsDataBean.getStopLossPrice()) * bondsDataBean.getBondsNum();
            }
        }

        AccountDataBean accountDataBean = getCurrentAccountDataBean();
        accountDataBean.setUsedRiskMoney(usedRiskMoney);
        accountDataBean.setUsedMonthRiskMoney(accountDataBean.getUsedMonthRiskMoney() + usedRiskMoney);
        DaoManager.getInstance().getDaoSession().getAccountDataBeanDao().insertOrReplaceInTx(accountDataBean);

        mListViewHeader.refresh(accountDataBean);
    }
}