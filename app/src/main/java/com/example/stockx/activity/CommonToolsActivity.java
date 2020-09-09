package com.example.stockx.activity;

import android.os.Bundle;
import android.view.ViewGroup;
import android.view.WindowManager;
import android.widget.LinearLayout;
import android.widget.Toast;

import androidx.appcompat.app.AppCompatActivity;
import androidx.cardview.widget.CardView;

import com.example.greendao.DaoManager;
import com.example.stockx.R;
import com.example.stockx.bean.BondsDataBean;
import com.example.stockx.view.CTCostPirceCalcView;
import com.example.stockx.view.CTGridTradeView;
import com.example.stockx.view.CTPerPriceSwView;
import com.example.stockx.view.CTStopWinCalc;
import com.example.stockx.view.CTTargetPriceCalc;

public class CommonToolsActivity extends AppCompatActivity {

    private LinearLayout viewLayout;
    private long accountId;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_common_tools);
        getWindow().addFlags(WindowManager.LayoutParams.FLAG_KEEP_SCREEN_ON);
        setTitle("常用工具");
        initView();
    }

    private void initView() {
        accountId = getIntent().getLongExtra(MainActivity.S_ID_ACCOUNT, -1);
        viewLayout = findViewById(R.id.ll);
        CTCostPirceCalcView ctCostPirceCalcView = new CTCostPirceCalcView(this, viewLayout);
        //加仓成本价计算
        if (getIntent() != null) {
            double costPrice = getIntent().getDoubleExtra("OPEN_PRICE", 0);
            double amount = getIntent().getIntExtra("OPEN_AMOUNT", 0) / 100.0;
            long bondsDataId = getIntent().getLongExtra("BOND_DATA_ID", -1);
            ctCostPirceCalcView.setCostData(bondsDataId, costPrice, amount);
        }
        ctCostPirceCalcView.setiCallbackCostPriceCalc(new CTCostPirceCalcView.ICallbackCostPriceCalc() {
            @Override
            public void save(boolean canSave, String msg, BondsDataBean bondsDataBean) {
                if (!canSave) {
                    Toast.makeText(CommonToolsActivity.this, msg, Toast.LENGTH_SHORT).show();
                    return;
                }
                DaoManager.getInstance().getDaoSession().getBondsDataBeanDao().insertOrReplace(bondsDataBean);
                setResult(RESULT_OK);
                finish();
            }
        });
        CardView cardView = new CardView(this);
        LinearLayout.LayoutParams layoutParams = new LinearLayout.LayoutParams(ViewGroup.LayoutParams.MATCH_PARENT, ViewGroup.LayoutParams.WRAP_CONTENT);
        layoutParams.setMargins(2, 3, 2, 18);
        cardView.setLayoutParams(layoutParams);

        new CTTargetPriceCalc(this, cardView);
        viewLayout.addView(cardView);

        new CTGridTradeView(this, viewLayout);
        new CTPerPriceSwView(this, viewLayout);
        new CTStopWinCalc(this, viewLayout, accountId);

    }
}