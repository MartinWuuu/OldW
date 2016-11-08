package com.brave.oxygenerator;

import android.content.Intent;
import android.database.Cursor;
import android.net.Uri;
import android.provider.MediaStore;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import com.brave.oxygenerator.util.PreferenceUtil;
import com.zhy.http.okhttp.OkHttpUtils;
import com.zhy.http.okhttp.callback.StringCallback;

import org.json.JSONException;
import org.json.JSONObject;

import java.io.File;

import okhttp3.Call;

public class UserActivity extends AppCompatActivity implements View.OnClickListener {

    private View mHeaderView,mNameView,mAgeView,mSexView,mProfessionView,mPhoneView,mEmailView;
    private TextView mNameText,mAgeText,mSexText,mProfessionText,mPhoneText,mEmailText;
    private ImageView mHeaderImg;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_user);
        initView();
    }

    private void initView() {
        mHeaderView = findViewById(R.id.activity_user_header_bar);
        mNameView = findViewById(R.id.activity_user_bar_name);
        mAgeView = findViewById(R.id.activity_user_bar_age);
        mSexView = findViewById(R.id.activity_user_bar_sex);
        mProfessionView = findViewById(R.id.activity_user_bar_profession);
        mPhoneView = findViewById(R.id.activity_user_bar_phone);
        mEmailView = findViewById(R.id.activity_user_bar_email);

        mNameText = (TextView)findViewById(R.id.activity_user_text_name);
        mAgeText = (TextView)findViewById(R.id.activity_user_text_age);
        mSexText = (TextView)findViewById(R.id.activity_user_text_sex);
        mProfessionText = (TextView)findViewById(R.id.activity_user_text_profession);
        mPhoneText = (TextView)findViewById(R.id.activity_user_text_phone);
        mEmailText = (TextView)findViewById(R.id.activity_user_text_email);

        mHeaderImg = (ImageView)findViewById(R.id.activity_user_header_img);

        mHeaderView.setOnClickListener(this);
        mNameView.setOnClickListener(this);
        mAgeView.setOnClickListener(this);
        mSexView.setOnClickListener(this);
        mProfessionView.setOnClickListener(this);
        mPhoneView.setOnClickListener(this);
        mEmailView.setOnClickListener(this);

    }

    @Override
    public void onClick(View v) {
        switch (v.getId()){
            case R.id.activity_user_header_bar:
                setUserHeader();
                break;
            case R.id.activity_user_bar_name:
                editUserInfo("昵称","罗永浩");
                break;
            case R.id.activity_user_bar_age:
                editUserInfo("年龄","45");
                break;
            case R.id.activity_user_bar_sex:
                editUserInfo("性别","男");
                break;
            case R.id.activity_user_bar_profession:
                editUserInfo("职业","锤子科技CEO");
                break;
            case R.id.activity_user_bar_phone:
                editUserInfo("手机号","15222656877");
                break;
            case R.id.activity_user_bar_email:
                editUserInfo("E-mail","775347842@qq.com");
                break;
        }
    }

    private void setUserHeader() {
        Intent i = new Intent(Intent.ACTION_PICK, android.provider.MediaStore.Images.Media.EXTERNAL_CONTENT_URI);
        startActivityForResult(i, 200);
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        if (requestCode == 200 && resultCode == RESULT_OK && null != data) {

            Uri selectedImage = data.getData();
            String[] filePathColumn = { MediaStore.Images.Media.DATA };

            Cursor cursor = getContentResolver().query(selectedImage,
                    filePathColumn, null, null, null);
            cursor.moveToFirst();

            int columnIndex = cursor.getColumnIndex(filePathColumn[0]);
            final String picturePath = cursor.getString(columnIndex);
            cursor.close();
            File file = new File(picturePath);

            OkHttpUtils.post()
                    .addFile(file.getName(), file.getName(), file)
                    .url("http://airsep.routeyo.net/airsep/lutsoft/pkg/yumpkg.action")
                    .build()
                    .execute(new StringCallback() {
                        @Override
                        public void onError(Call call, Exception e, int id) {
                            Toast.makeText(UserActivity.this, "上传头像失败：请检查网络", Toast.LENGTH_LONG).show();
                        }

                        @Override
                        public void onResponse(String response, int id) {
                            Log.d("UserActivity",response);

                            try {
                                JSONObject jo = new JSONObject(response);
                                final String pkgIDs = jo.getString("pkgIDs");
                                Log.d("UserActivity",pkgIDs);
                                uploadHeadPic(pkgIDs);
                            } catch (JSONException e) {
                                e.printStackTrace();
                            }

                        }
                    });
        }
    }

    private void uploadHeadPic(String pkgIDs) {
        OkHttpUtils
                .post()
                .url("http://airsep.routeyo.net/airsep/lutsoft/sso/updateUser.action")
                .addParams("userLogoKey", pkgIDs)
                .addParams("id", PreferenceUtil.getUserInfo(UserActivity.this,"userId"))
                .build()
                .execute(new StringCallback() {
                    @Override
                    public void onError(Call call, Exception e, int id) {
                        Toast.makeText(UserActivity.this, "上传头像失败：请检查网络", Toast.LENGTH_LONG).show();
                    }

                    @Override
                    public void onResponse(String response, int id) {
                        Log.d("UserActivity",response);

                        try {
                            JSONObject jo = new JSONObject(response);
                            Toast.makeText(UserActivity.this, jo.getString("errorMsg"), Toast.LENGTH_LONG).show();
                            if(jo.getInt("errorCode") == 1000){

                            }
                        } catch (JSONException e) {
                            e.printStackTrace();
                        }

                    }
                });
    }



    private void editUserInfo(String s, String value) {
        Intent i = new Intent();
        i.setClass(this,EditUserInfoActivity.class);
        i.putExtra("title",s);
        i.putExtra("value",value);
        startActivity(i);
    }
}
