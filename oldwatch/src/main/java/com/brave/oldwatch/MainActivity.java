package com.brave.oldwatch;

import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.IntentFilter;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.Color;
import android.os.Bundle;
import android.support.v7.app.AlertDialog;
import android.support.v7.app.AppCompatActivity;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.Menu;
import android.view.MenuItem;
import android.view.MotionEvent;
import android.view.View;
import android.widget.ArrayAdapter;
import android.widget.DatePicker;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.ListView;
import android.widget.TextView;
import android.widget.Toast;

import com.amap.api.location.AMapLocation;
import com.amap.api.location.AMapLocationClient;
import com.amap.api.location.AMapLocationClientOption;
import com.amap.api.location.AMapLocationListener;
import com.amap.api.maps.AMap;
import com.amap.api.maps.CameraUpdateFactory;
import com.amap.api.maps.LocationSource;
import com.amap.api.maps.MapView;
import com.amap.api.maps.model.BitmapDescriptorFactory;
import com.amap.api.maps.model.CameraPosition;
import com.amap.api.maps.model.LatLng;
import com.amap.api.maps.model.LatLngBounds;
import com.amap.api.maps.model.Marker;
import com.amap.api.maps.model.MarkerOptions;
import com.amap.api.maps.model.MyLocationStyle;
import com.amap.api.maps.model.Polygon;
import com.amap.api.maps.model.PolygonOptions;
import com.amap.api.maps.model.PolylineOptions;
import com.amap.api.maps.model.VisibleRegion;
import com.brave.oldwatch.service.NetService;
import com.brave.oldwatch.utils.AppInfo;
import com.brave.oldwatch.utils.Constants;
import com.zhy.http.okhttp.OkHttpUtils;
import com.zhy.http.okhttp.callback.BitmapCallback;
import com.zhy.http.okhttp.callback.StringCallback;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.util.ArrayList;
import java.util.Calendar;
import java.util.List;

import okhttp3.Call;

public class MainActivity extends AppCompatActivity implements View.OnClickListener , LocationSource,AMapLocationListener ,AMap.OnInfoWindowClickListener {

    private UpdateUIBroadcastReceiver broadcastReceiver;
    public static final String ACTION_UPDATE_UI = "action.updateUI";
    private static final String TAG = "MainActivity";

    private View mToolbar,mToolbarBg;
    private TextView mToolbarItem1,mToolbarItem2,mToolbarItem3,mToolbarItem4;

    private ListView lv;
    private View headerView;
    private LinearLayout header_ll;
    private ArrayList<String> arrayList = new ArrayList<>();

    private MapView mMapview;
    private AMap aMap;

    private LocationSource.OnLocationChangedListener mListener;
    private AMapLocationClient mlocationClient;
    private AMapLocationClientOption mLocationOption;

    private LatLng mLacation;

    private List<Polygon> mPolygon = new ArrayList<>();
    private List<Marker> mMarker = new ArrayList<>();
    private LatLng[] mLatLngs;
    private List<String[]> mDevicesInfoList;

    private boolean mFirstFix = false;
    private boolean isShowFan = true;
    private boolean isShowToolbar = false;

    private String sTime = "",eTime = "";

    private int mZoomSize = 12;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        mMapview = (MapView)findViewById(R.id.activity_main_bmapView);
        mMapview.onCreate(savedInstanceState);
        aMap = mMapview.getMap();
        startNetService();
        init();
    }

    private void init() {
        aMap = mMapview.getMap();
        aMap.setOnMarkerClickListener(listener);
        aMap.setLocationSource(this);
        aMap.getUiSettings().setMyLocationButtonEnabled(true);
        aMap.setMyLocationEnabled(true);
        aMap.setMyLocationType(AMap.LOCATION_TYPE_LOCATE);
        setUpMap();
        initView();
        initData();
    }

    private void initData() {
        OkHttpUtils
                .get()
                .url(AppInfo.HttpUrl + "getMachine")
                .addParams("username", AppInfo.getString(MainActivity.this,"username"))
                .addParams("password", AppInfo.getString(MainActivity.this,"password"))
                .build()
                .execute(new StringCallback() {
                    @Override
                    public void onError(Call call, Exception e, int id) {
                        Log.d("getMachine:","异常");
                    }

                    @Override
                    public void onResponse(String response, int id) {
                        try {
                            parseJson(response);
                        } catch (JSONException e) {
                            e.printStackTrace();
                        }
                    }
                });
    }

    private void parseJson(String response) throws JSONException {
        Log.d(TAG, "parseJson: "+response);
        JSONObject jo = new JSONObject(response);
        int msgCode = jo.getInt("msgcode");
        String msg = jo.getString("msg");
        if (msgCode == 0){
            JSONArray array = jo.getJSONArray("data");
            mLatLngs = new LatLng[array.length()];
            mDevicesInfoList = new ArrayList<>();
            for (int i = 0;i < array.length();i++){
                JSONObject data = array.getJSONObject(i);
                View coupon_home_ad_item = LayoutInflater.from(MainActivity.this).inflate( R.layout.view_device_hor_list_item, null);
                ImageView icon = (ImageView) coupon_home_ad_item.findViewById(R.id.view_devices_list_item_time_logo);
                TextView name = (TextView)coupon_home_ad_item.findViewById(R.id.view_devices_list_item_time_name);
                name.setText(data.getString("name"));
                mDevicesInfoList.add(new String[]{data.getString("name"),data.getString("imei"),data.getString("phone"),AppInfo.HttpImgUrl+data.getString("image")});
                setLogo(icon,AppInfo.HttpImgUrl+data.getString("image"));
                final LatLng ll = new LatLng(data.isNull("longitude")?0:data.getDouble("longitude"),data.isNull("latitude")?0:data.getDouble("latitude"));
                if (ll.latitude != 0 && ll.longitude != 0){
                    showMarker(ll,AppInfo.HttpImgUrl+data.getString("image"),new String[]{data.getString("name"),data.getString("name"),data.getString("name"),data.getString("updatetime")});
                    mLatLngs[i] = ll;
                }else{
                    mLatLngs[i] = mLacation;
                }
                coupon_home_ad_item.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View v) {
                        if (ll.latitude != 0 && ll.longitude != 0){
                            showPosition(ll,12);
                        }else{
                            Toast.makeText(MainActivity.this, "该设备未在线,无法获取位置信息", Toast.LENGTH_SHORT).show();
                        }
                    }
                });
                header_ll.addView(coupon_home_ad_item);
                if (data.isNull("dzwl") == false){
                    String list = data.getString("dzwl");
                    JSONArray dzwl = new JSONArray(list);
                    LatLng[] latlngs = new LatLng[dzwl.length()];
                    Log.d(TAG, "dzwl:"+dzwl.length());
                    for (int j = 0; j < dzwl.length();j++){
                        latlngs[j] = new LatLng(dzwl.getJSONObject(j).getDouble("lat"),dzwl.getJSONObject(j).getDouble("lng"));
                    }
                    setPolygon(latlngs);
                }
            }
            lv.addHeaderView(headerView);
            ArrayAdapter adapter = new ArrayAdapter(MainActivity.this, R.layout.view_device_hor_list_item,R.id.view_devices_list_item_time_name, arrayList);
            lv.setAdapter(adapter);
        }else{
            Toast.makeText(MainActivity.this, msg, Toast.LENGTH_SHORT).show();
            return;
        }

    }


    private void setLogo(final ImageView v,String url) {
        OkHttpUtils
                .get()
                .url(url)
                .build()
                .execute(new BitmapCallback() {
                    @Override
                    public void onError(Call call, Exception e, int id) {}

                    @Override
                    public void onResponse(Bitmap response, int id) {
                        v.setImageBitmap(response);
                    }
                });

    }

    private void initView() {
        mToolbar = findViewById(R.id.ac_main_toolbar);
        mToolbarBg = findViewById(R.id.ac_main_toolbar_bg);
        mToolbarItem1 = (TextView)findViewById(R.id.ac_main_toolbar__item_1);
        mToolbarItem2 = (TextView)findViewById(R.id.ac_main_toolbar__item_2);
        mToolbarItem3 = (TextView)findViewById(R.id.ac_main_toolbar__item_3);
        mToolbarItem4 = (TextView)findViewById(R.id.ac_main_toolbar__item_4);

        mToolbarItem1.setOnClickListener(this);
        mToolbarItem2.setOnClickListener(this);
        mToolbarItem3.setOnClickListener(this);
        mToolbarItem4.setOnClickListener(this);

        lv = (ListView) findViewById(R.id.ac_main_lv);
        headerView = LayoutInflater.from(this).inflate(R.layout.view_device_hor_list_header, null);
        header_ll = (LinearLayout) headerView.findViewById(R.id.header_ll);

        mToolbarBg.setOnTouchListener(new View.OnTouchListener() {
            @Override
            public boolean onTouch(View v, MotionEvent event) {
                mToolbarBg.setVisibility(View.GONE);
                mToolbar.setVisibility(View.GONE);
                isShowToolbar = false;
                return false;
            }
        });
    }

    private void setUpMap() {
        MyLocationStyle myLocationStyle = new MyLocationStyle();
        myLocationStyle.myLocationIcon(BitmapDescriptorFactory.fromResource(R.mipmap.head));// 设置小蓝点的图标
        myLocationStyle.strokeColor(Color.GRAY);// 设置圆形的边框颜色
        myLocationStyle.radiusFillColor(Color.argb(20, 0, 0, 180));// 设置圆形的填充颜色
        myLocationStyle.strokeWidth(1.0f);// 设置圆形的边框粗细
        aMap.setMyLocationStyle(myLocationStyle);
        aMap.setLocationSource(this);// 设置定位监听
        aMap.getUiSettings().setMyLocationButtonEnabled(false);// 设置默认定位按钮是否显示
        aMap.getUiSettings().setZoomControlsEnabled(false);
        aMap.setMyLocationEnabled(true);// 设置为true表示显示定位层并可触发定位，false表示隐藏定位层并不可触发定位，默认是false
        aMap.setOnInfoWindowClickListener(this);
    }



    private void showMarker(final LatLng latLng, String headUrl, final String[] info){
        OkHttpUtils
                .get()
                .url(headUrl)
                .build()
                .execute(new BitmapCallback() {
                    @Override
                    public void onError(Call call, Exception e, int id) {
                        Log.d(TAG,"onError");
                    }
                    @Override
                    public void onResponse(Bitmap response, int id) {
                        Marker m = aMap.addMarker(new MarkerOptions().position(latLng)
                                .icon(BitmapDescriptorFactory.fromBitmap(response))
                        .title(info[0])
                        .snippet("心率："+info[1]+"\n血压："+info[2]+"\n更新时间："+info[3]));
                        mMarker.add(m);
                    }
                });
    }



    private void showPosition(LatLng latlng, int level){
        aMap.animateCamera(CameraUpdateFactory.newCameraPosition(new CameraPosition(latlng, level, 0, 0)), 600,null);
        mZoomSize = level;
    }

    private void showPolyline(List<LatLng> latLngs){
        aMap.addPolyline(new PolylineOptions().
                addAll(latLngs).width(10).color(Color.argb(255, 1, 1, 1)));
        aMap.addMarker(new MarkerOptions().
                position(latLngs.get(0)).
                title("起始点").
                icon(BitmapDescriptorFactory.fromBitmap(BitmapFactory.decodeResource(getResources(),R.mipmap.map_start)))
        );
        aMap.addMarker(new MarkerOptions().
                position(latLngs.get(latLngs.size() - 1)).
                title("终点").
                icon(BitmapDescriptorFactory.fromBitmap(BitmapFactory.decodeResource(getResources(),R.mipmap.map_end)))
        );
    }

    private void setPolygon(LatLng[] latlngs){
        PolygonOptions options = new PolygonOptions();
        for (int i = 0; i < latlngs.length; i++){
            options.add(latlngs[i]);
        }
        Polygon p = aMap.addPolygon(options.strokeWidth(5)
                .strokeColor(Color.argb(30, 1, 1,1))
                .fillColor(Color.argb(30, 1, 1, 1)));
        mPolygon.add(p);
    }

    private void showPolygon(boolean b){
        for (Polygon p : mPolygon){
            p.setVisible(b);
        }
    }

    public void location(View view){
        showPosition(mLacation,12);
    }

    public void group(View view){
        LatLngBounds.Builder builder = new LatLngBounds.Builder();
        for (int i = 0; i <mLatLngs.length;i++){
            builder.include(mLatLngs[i]);
        }
        builder.include(mLacation);
        aMap.moveCamera(CameraUpdateFactory.newLatLngBounds(builder.build(), 200));
    }

    private AMap.OnMarkerClickListener listener = new AMap.OnMarkerClickListener() {

        @Override
        public boolean onMarkerClick(Marker arg0) {
            return false;
        }
    };


    @Override
    public void onLocationChanged(AMapLocation amapLocation) {
        if (mListener != null && amapLocation != null) {
            if (amapLocation != null && amapLocation.getErrorCode() == 0) {
                mListener.onLocationChanged(amapLocation);
                if (!mFirstFix){
                    mLacation = new LatLng(amapLocation.getLatitude(),amapLocation.getLongitude());
                    showPosition(mLacation,12);
                    mFirstFix = true;
                }
            } else {
                String errText = "定位失败," + amapLocation.getErrorCode()+ ": " + amapLocation.getErrorInfo();
                Log.e("mapErr",errText);
            }
        }
    }

    @Override
    public void activate(LocationSource.OnLocationChangedListener listener) {
        mListener = listener;
        if (mlocationClient == null) {
            mlocationClient = new AMapLocationClient(MainActivity.this);
            mLocationOption = new AMapLocationClientOption();
            mlocationClient.setLocationListener(this);
            mLocationOption.setLocationMode(AMapLocationClientOption.AMapLocationMode.Hight_Accuracy);
            mlocationClient.setLocationOption(mLocationOption);
            mlocationClient.startLocation();
        }
    }

    @Override
    public void deactivate() {
        mListener = null;
        if (mlocationClient != null) {
            mlocationClient.stopLocation();
            mlocationClient.onDestroy();
        }
        mlocationClient = null;
    }

    @Override
    protected void onResume() {
        super.onResume();
        mMapview.onResume();
        mFirstFix = false;
    }

    @Override
    protected void onPause() {
        super.onPause();
        mMapview.onPause();
    }

    @Override
    public void onSaveInstanceState(Bundle outState) {
        super.onSaveInstanceState(outState);
        mMapview.onSaveInstanceState(outState);
    }

    private void startNetService() {
        IntentFilter filter = new IntentFilter();
        filter.addAction(ACTION_UPDATE_UI);
        broadcastReceiver = new UpdateUIBroadcastReceiver();
        registerReceiver(broadcastReceiver, filter);

        Intent i = new Intent();
        i.setClass(this, NetService.class);
        startService(i);
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        getMenuInflater().inflate(R.menu.main_normal,menu);
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        if(item.getItemId() == R.id.action_notice){
            Intent i = new Intent();
            i.setClass(this,MoreActivity.class);
            startActivity(i);
            return true;
        }

        if(item.getItemId() == R.id.action_more){
            if(isShowToolbar){
                mToolbarBg.setVisibility(View.GONE);
                mToolbar.setVisibility(View.GONE);
            }else{
                mToolbarBg.setVisibility(View.VISIBLE);
                mToolbar.setVisibility(View.VISIBLE);
            }
            isShowToolbar = !isShowToolbar;
            return true;
        }

        return super.onOptionsItemSelected(item);
    }

    @Override
    public void onClick(View v) {

        mToolbarBg.setVisibility(View.GONE);
        mToolbar.setVisibility(View.GONE);
        isShowToolbar = false;

        switch (v.getId()){
            case R.id.ac_main_toolbar__item_1:
                Intent i = new Intent();
                i.setClass(this,ChatListActivity.class);
                startActivity(i);
                break;

            case R.id.ac_main_toolbar__item_2:
                Intent ii = new Intent();
                ii.setClass(this,NoticeActivity.class);
                startActivity(ii);
                break;

            case R.id.ac_main_toolbar__item_3:
                if (isShowFan){
                    mToolbarItem3.setText("显示电子围栏");
                }else{
                    mToolbarItem3.setText("隐藏电子围栏");
                }
                showPolygon(!isShowFan);
                isShowFan = !isShowFan;
                break;

            case R.id.ac_main_toolbar__item_4:
                showHistoryAlert();
                break;
        }
    }

    @Override
    public void onInfoWindowClick(Marker marker) {
        for (int i = 0; i < mMarker.size();i++){
            if (marker.equals(mMarker.get(i))){
                Intent ii = new Intent();
                ii.setClass(MainActivity.this, DeviceActivity.class);
                ii.putExtra("device_info",mDevicesInfoList.get(i));
                startActivity(ii);
            }
        }
    }

    private class UpdateUIBroadcastReceiver extends BroadcastReceiver {
        @Override
        public void onReceive(Context context, Intent intent) {
        }
    }

    private void getHistory(){
        OkHttpUtils
                .get()
                .url(AppInfo.HttpUrl + "locusHistory")
                .addParams("start", sTime)
                .addParams("end", eTime)
                .addParams("username", AppInfo.getString(MainActivity.this,"username"))
                .addParams("password", AppInfo.getString(MainActivity.this,"password"))
                .addParams("imei","0")
                .build()
                .execute(new StringCallback() {
                    @Override
                    public void onError(Call call, Exception e, int id) {
                        Log.d("locusHistory:","异常");
                        Toast.makeText(MainActivity.this, "获取数据出现异常", Toast.LENGTH_SHORT).show();
                    }

                    @Override
                    public void onResponse(String response, int id) {
                        Log.d("response:",response);
                        JSONObject jo;
                        try {
                            jo = new JSONObject(response);
                            String msg = jo.getString("msg");
                            Log.d("response:",msg);
                            if(jo.getInt("msgcode") == 0){
                                if(jo.isNull("data")){
                                    Toast.makeText(MainActivity.this, "所查找时间内该设备并没有历史记录", Toast.LENGTH_SHORT).show();
                                }else{
                                    JSONArray data = jo.getJSONArray("data");
                                    List<LatLng> list = new ArrayList<LatLng>();
                                    for (int i = 0; i < data.length(); i++){
                                        String content = data.getJSONObject(i).getString("content");
                                        String[] strArray = content.split(",");
                                        double lat = Double.parseDouble(strArray[1]);
                                        double lng = Double.parseDouble(strArray[0]);
                                        LatLng latLng = new LatLng(lat,lng);
                                        list.add(latLng);
                                    }
                                    showPolyline(list);
                                    showPosition(list.get(0),15);
                                }
                            }else{
                                Toast.makeText(MainActivity.this, msg, Toast.LENGTH_SHORT).show();
                            }
                        } catch (JSONException e) {
                            e.printStackTrace();
                        }
                    }
                });
    }


    private void showHistoryAlert(){
        Calendar calendar = Calendar.getInstance();
        final int year = calendar.get(Calendar.YEAR);
        final int month = calendar.get(Calendar.MONTH);
        final int day = calendar.get(Calendar.DAY_OF_MONTH);

        sTime = year +"-"+(month+1)+"-"+day;
        eTime = year +"-"+(month+1)+"-"+day;

        Toast.makeText(MainActivity.this, "请选择开始时间", Toast.LENGTH_SHORT).show();
        AlertDialog.Builder builder = new AlertDialog.Builder(MainActivity.this);
        final DatePicker data1 = new DatePicker(MainActivity.this);
        data1.init(year, month, day, new DatePicker.OnDateChangedListener(){

            @Override
            public void onDateChanged(DatePicker datePicker, int i, int i1, int i2) {
                String  m ,d;
                i1 = i1 + 1;
                if(i1 < 10 ){
                    m = "0"+i1;
                }else{
                    m = ""+i1;
                }
                if(i2 < 10 ){
                    d = "0"+i2;
                }else{
                    d = ""+i2;
                }
                sTime = i+"-"+m+"-"+d;
            }
        });
        builder.setView(data1);
        builder.setPositiveButton("确认", new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialogInterface, int i) {
                Toast.makeText(MainActivity.this, "请选择结束时间", Toast.LENGTH_SHORT).show();
                AlertDialog.Builder builder = new AlertDialog.Builder(MainActivity.this);
                final DatePicker data2 = new DatePicker(MainActivity.this);
                data2.init(year, month, day, new DatePicker.OnDateChangedListener(){

                    @Override
                    public void onDateChanged(DatePicker datePicker, int i, int i1, int i2) {
                        String  m ,d;
                        i1 = i1 + 1;
                        if(i1 < 10 ){
                            m = "0"+i1;
                        }else{
                            m = ""+i1;
                        }
                        if(i2 < 10 ){
                            d = "0"+i2;
                        }else{
                            d = ""+i2;
                        }
                        eTime = i+"-"+m+"-"+d;
                    }
                });
                builder.setView(data2);
                builder.setPositiveButton("确认", new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialogInterface, int i) {
                        getHistory();
                    }
                });
                builder.setNegativeButton("取消",null);
                builder.create().show();
            }
        });
        builder.setNegativeButton("取消",null);
        builder.create().show();
    }


    @Override
    protected void onDestroy() {
        System.out.println("onDestroy");
        super.onDestroy();
        mMapview.onDestroy();
        unregisterReceiver(broadcastReceiver);
    }
}
