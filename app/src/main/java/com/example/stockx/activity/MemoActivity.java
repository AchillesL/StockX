package com.example.stockx.activity;

import android.os.Bundle;
import android.text.TextUtils;
import android.view.WindowManager;
import android.widget.EditText;
import android.widget.Toast;

import androidx.appcompat.app.AppCompatActivity;

import com.example.stockx.R;
import com.example.stockx.utils.SharePreferenceUtils;

public class MemoActivity extends AppCompatActivity {

    private EditText edMemo;
    private String MEMO_KEY = "MEMO_KEY";

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_memo);
        getWindow().addFlags(WindowManager.LayoutParams.FLAG_KEEP_SCREEN_ON);
        setTitle("备忘录");
        findViews();
    }

    private void findViews() {
        edMemo = (EditText) findViewById(R.id.ed_memo);
        String meno = SharePreferenceUtils.getString(this, MEMO_KEY);
        if (!TextUtils.isEmpty(meno)) {
            edMemo.requestFocus();
            edMemo.setText(meno);
            edMemo.setSelection(edMemo.getText().toString().length());
        }
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
        SharePreferenceUtils.putString(this, MEMO_KEY, edMemo.getText().toString());
        Toast.makeText(this, "备忘已保存!", Toast.LENGTH_SHORT).show();
    }
}