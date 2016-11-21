package com.brave.oldwatch.frame;

import android.content.Intent;
import android.graphics.Bitmap;
import android.os.Bundle;
import android.app.Fragment;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;

import com.amap.api.maps.model.BitmapDescriptorFactory;
import com.amap.api.maps.model.MarkerOptions;
import com.brave.oldwatch.LoginActivity;
import com.brave.oldwatch.R;
import com.brave.oldwatch.utils.AppInfo;
import com.zhy.http.okhttp.OkHttpUtils;
import com.zhy.http.okhttp.callback.BitmapCallback;

import okhttp3.Call;

public class MoreFragment extends Fragment implements View.OnClickListener {

    private static final String TAG = "MoreFragment";
    
    private View mView;
    private View mPasswordBtn,mLogoutBtn;
    private ImageView mUserLogo;
    private TextView mUsername;

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        mView = inflater.inflate(R.layout.fragment_more, container, false);
        init();
        return mView;
    }

    private void init() {
        mUserLogo = (ImageView)mView.findViewById(R.id.frame_more_user_logo);
        mPasswordBtn = mView.findViewById(R.id.frame_more_password);
        mLogoutBtn = mView.findViewById(R.id.frame_more_logout);
        mUsername = (TextView)mView.findViewById(R.id.frame_more_user_username);
        mUsername.setText(AppInfo.getString(getActivity(),"username"));

        mPasswordBtn.setOnClickListener(this);
        mLogoutBtn.setOnClickListener(this);
    }

    @Override
    public void onClick(View view) {
        switch (view.getId()){

            case R.id.frame_more_password:

                break;
            case R.id.frame_more_logout:
                logout();
                break;
        }
    }

    private void logout() {
        AppInfo.save(getActivity(),"isLogin",false);
        Intent view = new Intent();
        view.setClass(getActivity(), LoginActivity.class);
        startActivity(view);
        getActivity().finish();
    }
}
