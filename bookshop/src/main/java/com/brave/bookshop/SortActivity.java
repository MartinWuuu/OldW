package com.brave.bookshop;

import android.content.Intent;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.view.View;
import android.widget.EditText;
import android.widget.LinearLayout;
import android.widget.RadioButton;
import android.widget.RadioGroup;
import android.widget.Toast;

import com.brave.bookshop.data.BookData;

public class SortActivity extends AppCompatActivity {

    private EditText mMinEditView,mMaxEditView;
    private RadioGroup mClassificationRadioView,mQualityRadioView;

    private String classification = "",quality = "";

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_sort);
        initView();
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
        mQualityRadioView.removeAllViews();
        mClassificationRadioView.removeAllViews();
    }

    private void initView() {
        mMinEditView = (EditText) findViewById(R.id.sort_edit_min);
        mMaxEditView = (EditText) findViewById(R.id.sort_edit_max);
        mClassificationRadioView = (RadioGroup) findViewById(R.id.sort_radio_classification);
        mQualityRadioView = (RadioGroup) findViewById(R.id.sort_radio_quality);
        initRadio();
    }

    private void initRadio() {
        for (int i = 0; i < BookData.mBookClassification.length;i++){
            RadioButton tempButton = new RadioButton(this);
            tempButton.setText(BookData.mBookClassification[i]);
            mClassificationRadioView.addView(tempButton, LinearLayout.LayoutParams.MATCH_PARENT, LinearLayout.LayoutParams.WRAP_CONTENT);
        }

        for (int i = 0; i < BookData.mBookQuality.length; i++ ){
            RadioButton tempButton = new RadioButton(this);
            tempButton.setText(BookData.mBookQuality[i]);
            mQualityRadioView.addView(tempButton, LinearLayout.LayoutParams.MATCH_PARENT, LinearLayout.LayoutParams.WRAP_CONTENT);
        }

        mClassificationRadioView.setOnCheckedChangeListener(new RadioGroup.OnCheckedChangeListener() {
            @Override
            public void onCheckedChanged(RadioGroup group, int checkedId) {
                RadioButton radioButton = (RadioButton)findViewById(group.getCheckedRadioButtonId());
                classification = radioButton.getText().toString();
            }
        });

        mQualityRadioView.setOnCheckedChangeListener(new RadioGroup.OnCheckedChangeListener() {
            @Override
            public void onCheckedChanged(RadioGroup group, int checkedId) {
                RadioButton radioButton = (RadioButton)findViewById(group.getCheckedRadioButtonId());
                quality = radioButton.getText().toString();
            }
        });

    }


    public void confirm(View view){
        String min = mMinEditView.getText().toString();
        String max = mMaxEditView.getText().toString();
        if(min.equals("")){
            Toast.makeText(SortActivity.this, "请填写最低价", Toast.LENGTH_SHORT).show();
            return;
        }
        if (max.equals("")){
            Toast.makeText(SortActivity.this, "请填写最高价", Toast.LENGTH_SHORT).show();
            return;
        }

        if (classification.equals("")){
            Toast.makeText(SortActivity.this, "请选择书籍分类", Toast.LENGTH_SHORT).show();
            return;
        }

        if (quality.equals("")){
            Toast.makeText(SortActivity.this, "请选择书籍新旧程度", Toast.LENGTH_SHORT).show();
            return;
        }

        String[] params = new String[]{min,max,classification,quality};
        Intent i = new Intent();
        i.setClass(SortActivity.this,CollectionActivity.class);
        i.putExtra("params",params);
        i.putExtra("flag",2);
        startActivity(i);
        finish();
    }
}
