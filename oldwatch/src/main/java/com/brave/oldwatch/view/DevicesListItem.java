package com.brave.oldwatch.view;

import android.content.Context;
import android.graphics.Bitmap;
import android.view.LayoutInflater;
import android.view.View;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;

import com.brave.oldwatch.R;
import com.zhy.http.okhttp.OkHttpUtils;
import com.zhy.http.okhttp.callback.BitmapCallback;

import okhttp3.Call;
import okhttp3.Request;

/**
 * Created by Brave on 2016/10/28.
 */

public class DevicesListItem extends LinearLayout {

    private View mView;

    private ImageView mHeadView;
    private TextView mNameView,mStatusView,mTimeView;

    public DevicesListItem(Context context){
        super(context);
        mView = LayoutInflater.from(context).inflate(R.layout.view_devices_list_item,this);
        initView();
    }

    private void initView() {
        mHeadView = (ImageView)mView.findViewById(R.id.view_devices_list_item_head);
        mNameView = (TextView)mView.findViewById(R.id.view_devices_list_item_name);
        mStatusView = (TextView)mView.findViewById(R.id.view_devices_list_item_status);
        mTimeView = (TextView)mView.findViewById(R.id.view_devices_list_item_time);
    }

    public void setParams(String[] params){
        mNameView .setText(params[0]);
        mStatusView.setText(params[1]);
        mTimeView.setText(params[2]);
        OkHttpUtils
                .get()
                .url(params[3])
                .build()
                .execute(new BitmapCallback() {
                    @Override
                    public void onError(Call call, Exception e, int id) {

                    }

                    @Override
                    public void onResponse(Bitmap response, int id) {
                        mHeadView.setImageBitmap(response);
                    }
                });
    }

}
