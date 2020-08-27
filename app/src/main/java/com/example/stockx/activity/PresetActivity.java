package com.example.stockx.activity;

import android.os.Bundle;
import android.view.Menu;
import android.view.MenuItem;
import android.widget.LinearLayout;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;

import com.example.greendao.DaoManager;
import com.example.greendao.PresetBondsDataBeanDao;
import com.example.stockx.R;
import com.example.stockx.bean.AccountDataBean;
import com.example.stockx.view.PresetView;

public class PresetActivity extends AppCompatActivity {

    private long mAccountID;
    private AccountDataBean mAccountDataBean;
    private PresetBondsDataBeanDao mPresetBondsDataBeanDao;

    private LinearLayout mLayout;
    private PresetView mPresetView;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_preset);
        setTitle("预备单");

        mAccountID = getIntent().getLongExtra(MainActivity.S_ID_ACCOUNT, -1);
        mAccountDataBean = DaoManager.getInstance().getDaoSession().getAccountDataBeanDao().load(mAccountID);
        if (mAccountID == -1 || mAccountDataBean == null) {
            finish();
            return;
        }

        mPresetBondsDataBeanDao = DaoManager.getInstance().getDaoSession().getPresetBondsDataBeanDao();
        mLayout = findViewById(R.id.layout);
        mPresetView = new PresetView(this, mAccountDataBean);
        mPresetView.setiCallback(new PresetView.iCallback() {
            @Override
            public void add2StopComplete() {
                setResult(RESULT_OK);
                finish();
            }
        });
        mLayout.addView(mPresetView.getView());
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        getMenuInflater().inflate(R.menu.menu_preset_activity, menu);
        return super.onCreateOptionsMenu(menu);
    }

    @Override
    public boolean onOptionsItemSelected(@NonNull MenuItem item) {
        switch (item.getItemId()) {
            case R.id.menu_add_2_stop: {
                mPresetView.add2Stop();
                break;
            }
            case R.id.menu_clear_invalid: {
                mPresetView.clearInvalid();
                break;
            }
            case R.id.menu_clear_all: {
                mPresetView.clearAll();
                break;
            }
        }
        return super.onOptionsItemSelected(item);
    }
}