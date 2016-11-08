package com.brave.oxygenerator.frame;

import android.app.Fragment;
import android.content.DialogInterface;
import android.content.Intent;
import android.graphics.Color;
import android.os.Bundle;
import android.support.v7.app.AlertDialog;
import android.text.InputType;
import android.view.LayoutInflater;
import android.view.MotionEvent;
import android.view.View;
import android.view.ViewGroup;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.Switch;
import android.widget.Toast;

import com.brave.oxygenerator.AboutActivity;
import com.brave.oxygenerator.AdviceActivity;
import com.brave.oxygenerator.ContactActivity;
import com.brave.oxygenerator.PasswordActivity;
import com.brave.oxygenerator.R;
import com.brave.oxygenerator.SetLockActivity;
import com.brave.oxygenerator.UnlockActivity;
import com.brave.oxygenerator.UserActivity;
import com.brave.oxygenerator.util.PreferenceUtil;

/**
 * Created by Brave on 2016/10/7.
 */

public class SettingFrame extends Fragment implements View.OnTouchListener {

    private View mView,mUserView,mUserPasswordView,mPasswordView,mAboutView,mProductView,mAdviceView,mContactUsView;

    private ImageView mSwitchView;

    private boolean isSetPass = false;

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        mView = inflater.inflate(R.layout.frame_setting,container,false);
        initView();
        return mView;
    }

    private void initView() {
        mUserView = mView.findViewById(R.id.frame_setting_user);
        mUserPasswordView = mView.findViewById(R.id.frame_setting_user_password);
        mPasswordView = mView.findViewById(R.id.frame_setting_password);
        mAboutView = mView.findViewById(R.id.frame_setting_about);
        mProductView = mView.findViewById(R.id.frame_setting_product);
        mAdviceView = mView.findViewById(R.id.frame_setting_advice);
        mContactUsView = mView.findViewById(R.id.frame_setting_contact_us);
        mSwitchView = (ImageView) mView.findViewById(R.id.frame_setting_password_sw);

        mUserView.setOnTouchListener(this);
        mUserPasswordView.setOnTouchListener(this);
        mPasswordView.setOnTouchListener(this);
        mAboutView.setOnTouchListener(this);
        mProductView.setOnTouchListener(this);
        mAdviceView.setOnTouchListener(this);
        mContactUsView.setOnTouchListener(this);


        initData();

    }

    private void initData() {
        String passwordStr = PreferenceUtil.getGesturePassword(getActivity());
        if (passwordStr == "") {
            mSwitchView.setImageResource(R.mipmap.ic_sw_off);
            isSetPass = false;
        } else {
            mSwitchView.setImageResource(R.mipmap.ic_sw_on);
            isSetPass = true;
        }
    }

    @Override
    public boolean onTouch(View v, MotionEvent event) {

        switch (event.getAction()){
            case MotionEvent.ACTION_DOWN:
                v.setBackgroundColor(getResources().getColor(R.color.pressed));
                break;
            case MotionEvent.ACTION_UP:
                v.setBackgroundColor(Color.WHITE);
                onItemClick(v.getId());
                break;
            case MotionEvent.ACTION_CANCEL:
                v.setBackgroundColor(Color.WHITE);
                break;
        }

        return true;
    }

    private void onItemClick(int id) {
        switch (id){
            case R.id.frame_setting_user:
                Intent i = new Intent();
                i.setClass(getActivity(), UserActivity.class);
                startActivity(i);
                break;
            case R.id.frame_setting_user_password:
                Intent i1 = new Intent();
                i1.setClass(getActivity(), PasswordActivity.class);
                startActivity(i1);
                break;
            case R.id.frame_setting_password:
                setPassword();
                break;
            case R.id.frame_setting_about:
                Intent i2 = new Intent();
                i2.setClass(getActivity(), AboutActivity.class);
                startActivity(i2);
                break;
            case R.id.frame_setting_advice:
                Intent i3 = new Intent();
                i3.setClass(getActivity(), AdviceActivity.class);
                startActivity(i3);
                break;
            case R.id.frame_setting_product:
                break;
            case R.id.frame_setting_contact_us:
                Intent i4 = new Intent();
                i4.setClass(getActivity(), ContactActivity.class);
                startActivity(i4);
                break;
        }
    }

    @Override
    public void onResume() {
        super.onResume();
        initData();
    }

    private void setPassword(){
        if (isSetPass){
            PreferenceUtil.setGesturePassword(getActivity(), "");
        }else{
            Intent i = new Intent();
            i.setClass(getActivity(), SetLockActivity.class);
            getActivity().startActivity(i);
        }
        initData();
    }

}
