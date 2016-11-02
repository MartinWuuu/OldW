package com.brave.bookshop;

import android.app.ProgressDialog;
import android.content.Intent;
import android.net.Uri;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.text.TextUtils;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.Spinner;
import android.widget.Toast;

import com.brave.bookshop.data.BookData;
import com.brave.bookshop.data.UserData;
import com.brave.bookshop.database.book_list;

import java.io.File;

import cn.bmob.v3.datatype.BmobFile;
import cn.bmob.v3.listener.SaveListener;
import cn.bmob.v3.listener.UploadFileListener;

public class SendActivity extends AppCompatActivity {

    private Spinner mClassificationView,mQualityView;
    private ImageView mImageView;
    private EditText mBookNameView,mBookPriceView,mBookDetailsView;

    private String mImagePath;
    private Integer mClassificationData,mQualityData;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_send);
        initView();
        loadImage();
    }

    private void loadImage() {
        Intent intent = new Intent();
        intent.setType("image/*");
        intent.setAction(Intent.ACTION_GET_CONTENT);
        startActivityForResult(intent, 1);
    }

    private void initView() {
        mClassificationView = (Spinner) findViewById(R.id.send_classification);
        mQualityView = (Spinner) findViewById(R.id.send_quality);
        mImageView = (ImageView) findViewById(R.id.send_image);
        mBookDetailsView = (EditText) findViewById(R.id.send_book_details);
        mBookNameView = (EditText) findViewById(R.id.send_book_name);
        mBookPriceView = (EditText) findViewById(R.id.send_book_price);

        initSpinner(BookData.mBookClassification, BookData.mBookQuality);
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        if (resultCode == RESULT_OK) {
            Uri uri = data.getData();
            mImagePath = uri.getPath();
            mImageView.setImageURI(uri);
        }else{
            finish();
        }
        super.onActivityResult(requestCode, resultCode, data);
    }

    private void initSpinner(final String[] mClassificationItems,final String[] mQualityItems) {
        ArrayAdapter<String> mClassificationAdapter= new ArrayAdapter<>(this, android.R.layout.simple_spinner_item, mClassificationItems);
        mClassificationAdapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
        mClassificationView .setAdapter(mClassificationAdapter);
        mClassificationView.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {
            @Override
            public void onItemSelected(AdapterView<?> parent, View view,
                                       int pos, long id) {
                mClassificationData = pos;
            }
            @Override
            public void onNothingSelected(AdapterView<?> parent) {
            }
        });

        ArrayAdapter<String> mQualityAdapter= new ArrayAdapter<>(this, android.R.layout.simple_spinner_item, mQualityItems);
        mQualityAdapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
        mQualityView .setAdapter(mQualityAdapter);
        mQualityView.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {
            @Override
            public void onItemSelected(AdapterView<?> parent, View view,
                                       int pos, long id) {
                mQualityData = pos;
            }
            @Override
            public void onNothingSelected(AdapterView<?> parent) {
            }
        });
    }

    public void send(View view){

        String name = mBookNameView.getText().toString();
        String price = mBookPriceView.getText().toString();
        String details = mBookDetailsView.getText().toString();

        if (TextUtils.isEmpty(details) ) {
            toast("请填写书籍详细介绍");
            return;
        }

        if (TextUtils.isEmpty(price)) {
            toast("请填写书籍价格");
            return;
        }

        if (TextUtils.isEmpty(name) ) {
            toast("请填写书籍名称");
            return;
        }
        final ProgressDialog progressDialog = ProgressDialog.show(this,"请稍后","正在从服务器中获取数据");
        progressDialog.show();
        book_list b = new book_list();
        b.setB_name(name);
        b.setB_price(Integer.parseInt(price));
        b.setB_details(details);
        b.setB_state(true);
        b.setB_classification(mClassificationData);
        b.setB_uid(UserData.getEmail(this));
        b.setB_browse("0");
        b.setB_quality(mQualityData);
        b.save(this, new SaveListener() {
            @Override
            public void onSuccess() {
                toast("上传成功");
                progressDialog.dismiss();
                finish();
            }

            @Override
            public void onFailure(int i, String s) {
                toast(i+":"+s);
                progressDialog.dismiss();
            }
        });

//        BmobFile  f = new BmobFile(new File(mImagePath));
//        f.upload(this, new UploadFileListener() {
//            @Override
//            public void onSuccess() {
//                toast("成功");
//            }
//
//            @Override
//            public void onFailure(int i, String s) {
//                toast(i+":"+s);
//            }
//        });



    }

    private void toast(String s) {
        Toast.makeText(SendActivity.this, s, Toast.LENGTH_SHORT).show();
    }

}
