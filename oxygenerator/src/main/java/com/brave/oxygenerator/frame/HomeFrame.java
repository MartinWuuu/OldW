package com.brave.oxygenerator.frame;

import android.app.Fragment;
import android.content.DialogInterface;
import android.content.Intent;
import android.os.Bundle;
import android.support.v7.app.AlertDialog;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import com.brave.oxygenerator.LoginActivity;
import com.brave.oxygenerator.MainActivity;
import com.brave.oxygenerator.R;
import com.brave.oxygenerator.util.PreferenceUtil;
import com.zhy.http.okhttp.OkHttpUtils;
import com.zhy.http.okhttp.callback.StringCallback;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import okhttp3.Call;

public class HomeFrame extends Fragment implements View.OnClickListener{

    private View mView;
    private View mRoomView1,mRoomView2,mRoomView3,mRoomView4,mRoomView5,mRoomView6;
    private TextView mRoomTitleView1,mRoomTitleView2,mRoomTitleView3,mRoomTitleView4,mRoomTitleView5,mRoomTitleView6;
    private TextView mRoomOxView1,mRoomOxView2,mRoomOxView3,mRoomOxView4,mRoomOxView5,mRoomOxView6;
    private TextView mRoomSwView1,mRoomSwView2,mRoomSwView3,mRoomSwView4,mRoomSwView5,mRoomSwView6;
    private ImageView mRoomAddBtn;

    private String[] mRoomTitleStrings,mRoomOxStrings;

    private String s;


    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        mView = inflater.inflate(R.layout.frame_home,container,false);
        initView();
        initData();
        return mView;
    }

    private void initView() {
        mRoomView1 = mView.findViewById(R.id.frame_home_1);
        mRoomView2 = mView.findViewById(R.id.frame_home_2);
        mRoomView3 = mView.findViewById(R.id.frame_home_3);
        mRoomView4 = mView.findViewById(R.id.frame_home_4);
        mRoomView5 = mView.findViewById(R.id.frame_home_5);
        mRoomView6 = mView.findViewById(R.id.frame_home_6);

        mRoomTitleView1 = (TextView)mView.findViewById(R.id.frame_home_1_title);
        mRoomTitleView2 = (TextView)mView.findViewById(R.id.frame_home_2_title);
        mRoomTitleView3 = (TextView)mView.findViewById(R.id.frame_home_3_title);
        mRoomTitleView4 = (TextView)mView.findViewById(R.id.frame_home_4_title);
        mRoomTitleView5 = (TextView)mView.findViewById(R.id.frame_home_5_title);
        mRoomTitleView6 = (TextView)mView.findViewById(R.id.frame_home_6_title);

        mRoomOxView1 = (TextView)mView.findViewById(R.id.frame_home_1_ox);
        mRoomOxView2 = (TextView)mView.findViewById(R.id.frame_home_2_ox);
        mRoomOxView3 = (TextView)mView.findViewById(R.id.frame_home_3_ox);
        mRoomOxView4 = (TextView)mView.findViewById(R.id.frame_home_4_ox);
        mRoomOxView5 = (TextView)mView.findViewById(R.id.frame_home_5_ox);
        mRoomOxView6 = (TextView)mView.findViewById(R.id.frame_home_6_ox);

        mRoomSwView1 = (TextView)mView.findViewById(R.id.frame_home_1_sw);
        mRoomSwView2 = (TextView)mView.findViewById(R.id.frame_home_2_sw);
        mRoomSwView3 = (TextView)mView.findViewById(R.id.frame_home_3_sw);
        mRoomSwView4 = (TextView)mView.findViewById(R.id.frame_home_4_sw);
        mRoomSwView5 = (TextView)mView.findViewById(R.id.frame_home_5_sw);
        mRoomSwView6 = (TextView)mView.findViewById(R.id.frame_home_6_sw);

        mRoomAddBtn = (ImageView)mView.findViewById(R.id.frame_home_btn);

        mRoomAddBtn.setOnClickListener(this);
    }

    private void showRoom1(){
        mRoomAddBtn.setVisibility(View.VISIBLE);
        mRoomTitleView1.setText(mRoomTitleStrings[0]);
        mRoomOxView1.setText(mRoomOxStrings[0]);
    }

    private void showRoom2(){
        mRoomTitleView2.setText(mRoomTitleStrings[1]);
        mRoomOxView2.setText(mRoomOxStrings[1]);
        showRoom1();
    }

    private void showRoom3(){
        mRoomTitleView3.setText(mRoomTitleStrings[2]);
        mRoomOxView3.setText(mRoomOxStrings[2]);
        showRoom2();
    }

    private void showRoom4(){
        mRoomTitleView4.setText(mRoomTitleStrings[3]);
        mRoomOxView4.setText(mRoomOxStrings[3]);
        showRoom3();
    }

    private void showRoom5(){
        mRoomTitleView5.setText(mRoomTitleStrings[4]);
        mRoomOxView5.setText(mRoomOxStrings[4]);
        showRoom4();
    }

    private void showRoom6(){
        mRoomTitleView6.setText(mRoomTitleStrings[5]);
        mRoomOxView6.setText(mRoomOxStrings[5]);
        mRoomAddBtn.setVisibility(View.GONE);
        showRoom5();
    }

    private void showRoom(int num){
        switch (num){
            case 6:
                showRoom6();
                break;
            case 5:
                showRoom5();
                break;
            case 4:
                showRoom4();
                break;
            case 3:
                showRoom3();
                break;
            case 2:
                showRoom2();
                break;
            case 1:
                showRoom1();
                break;
        }
    }

    private void initData() {
        OkHttpUtils
                .post()
                .url("http://airsep.routeyo.net/airsep/lutsoft/device/toDevicePage.action")
//                .addParams("RowStart", deviceId)
//                .addParams("RowSize", deviceId)
//                .addParams("sEcho", deviceId)
                .addParams("ajax", "true")
                .build()
                .execute(new StringCallback() {
                    @Override
                    public void onError(Call call, Exception e, int id) {
                        Toast.makeText(getActivity(), "获取设备失败：请检查网络", Toast.LENGTH_LONG).show();
                    }

                    @Override
                    public void onResponse(String response, int id) {

                        Log.d("HomeFrame",response);
                        try {
                            JSONObject jo = new JSONObject(response);
                                JSONArray data = jo.getJSONArray("aaData");
                                mRoomTitleStrings = new String[data.length()];
                                mRoomOxStrings = new String[data.length()];
                                for (int i = 0;i < data.length(); i++){
                                    if (data.getJSONObject(i).isNull("showDeviceName") == false){
                                        mRoomTitleStrings[i] = data.getJSONObject(i).getString("showDeviceName");
                                        mRoomOxStrings[i] = data.getJSONObject(i).getString("deviceSerial");
                                    }else{
                                        mRoomTitleStrings[i] = "房间"+i;
                                        mRoomOxStrings[i] = "00"+i;
                                    }
                                }
                                showRoom(data.length()-1);
                        } catch (JSONException e) {
                            e.printStackTrace();
                        }

                    }
                });
    }

    private void addNewRoom() {
        AlertDialog.Builder builder = new AlertDialog.Builder(getActivity());
        builder.setTitle("添加房间设备");
        builder.setMessage("请输入房间设备号");
        final EditText edit = new EditText(getActivity());
        builder.setView(edit);
        builder.setPositiveButton("确认", new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialog, int which) {
                final String deviceId = edit.getText().toString();
                if (deviceId.equals("")){
                    Toast.makeText(getActivity(), "未填写设备号", Toast.LENGTH_SHORT).show();
                }else{
                    getDeviceId(deviceId);
                }
            }
        });
        builder.create().show();
    }

    private void getDeviceId(String deviceId) {
        OkHttpUtils
                .post()
                .url("http://airsep.routeyo.net/airsep/lutsoft/device/getDeviceID.action")
                .addParams("deviceSerial", deviceId)
                .build()
                .execute(new StringCallback() {
                    @Override
                    public void onError(Call call, Exception e, int id) {
                        Toast.makeText(getActivity(), "添加设备失败：请检查网络", Toast.LENGTH_LONG).show();
                    }

                    @Override
                    public void onResponse(String response, int id) {

                        Log.d("HomeFrame",response);
                        try {
                            JSONObject jo = new JSONObject(response);
                            if(jo.getInt("errorCode") == 1000){
                                bindDevice(jo.getString("errorMsg"));
                            }else{
                                Toast.makeText(getActivity(), jo.getString("errorMsg"), Toast.LENGTH_LONG).show();
                            }
                        } catch (JSONException e) {
                            e.printStackTrace();
                        }

                    }
                });
    }

    private void bindDevice(String deviceId) {
        OkHttpUtils
                .post()
                .url("http://airsep.routeyo.net/airsep/lutsoft/device/binddevice.action")
                .addParams("deviceID", deviceId)
                .addParams("userID", PreferenceUtil.getUserInfo(getActivity(),"userId"))
                .addParams("usable", "true")
                .build()
                .execute(new StringCallback() {
                    @Override
                    public void onError(Call call, Exception e, int id) {
                        Toast.makeText(getActivity(), "添加设备失败：请检查网络", Toast.LENGTH_LONG).show();
                    }

                    @Override
                    public void onResponse(String response, int id) {

                        Log.d("HomeFrame",response);
                        try {
                            JSONObject jo = new JSONObject(response);
                            if(jo.getInt("errorCode") == 1000){
                                Toast.makeText(getActivity(), "恭喜！添加设备成功", Toast.LENGTH_LONG).show();
                            }else{
                                Toast.makeText(getActivity(), jo.getString("errorMsg"), Toast.LENGTH_LONG).show();
                            }
                        } catch (JSONException e) {
                            e.printStackTrace();
                        }

                    }
                });
    }


    @Override
    public void onClick(View v) {
        switch (v.getId()){
            case R.id.frame_home_1:

                break;
            case R.id.frame_home_2:

                break;
            case R.id.frame_home_3:

                break;
            case R.id.frame_home_4:

                break;
            case R.id.frame_home_5:

                break;
            case R.id.frame_home_6:

                break;
            case R.id.frame_home_btn:
                addNewRoom();
                break;
        }
    }
}
