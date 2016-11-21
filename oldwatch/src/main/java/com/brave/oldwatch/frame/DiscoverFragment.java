package com.brave.oldwatch.frame;


import android.app.Fragment;
import android.content.DialogInterface;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.Color;
import android.os.Bundle;
import android.support.v7.app.AlertDialog;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.DatePicker;
import android.widget.Toast;

import com.amap.api.location.AMapLocation;
import com.amap.api.location.AMapLocationClient;
import com.amap.api.location.AMapLocationClientOption;
import com.amap.api.location.AMapLocationListener;
import com.amap.api.maps.AMap;
import com.amap.api.maps.CameraUpdate;
import com.amap.api.maps.CameraUpdateFactory;
import com.amap.api.maps.LocationSource;
import com.amap.api.maps.MapView;
import com.amap.api.maps.model.BitmapDescriptor;
import com.amap.api.maps.model.BitmapDescriptorFactory;
import com.amap.api.maps.model.CameraPosition;
import com.amap.api.maps.model.Circle;
import com.amap.api.maps.model.CircleOptions;
import com.amap.api.maps.model.LatLng;
import com.amap.api.maps.model.Marker;
import com.amap.api.maps.model.MarkerOptions;
import com.amap.api.maps.model.MyLocationStyle;
import com.amap.api.maps.model.Polygon;
import com.amap.api.maps.model.PolygonOptions;
import com.amap.api.maps.model.PolylineOptions;
import com.brave.oldwatch.R;
import com.brave.oldwatch.utils.AppInfo;
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


public class DiscoverFragment extends Fragment implements LocationSource,AMapLocationListener {

    private static final String TAG = "DiscoverFragment";

    private MapView mMapview;
    private AMap aMap;

    private LocationSource.OnLocationChangedListener mListener;
    private AMapLocationClient mlocationClient;
    private AMapLocationClientOption mLocationOption;

    private Marker mLocMarker;
    private Circle mCircle;
    private Polygon mPolygon;

    private BitmapDescriptor mDes;

    private static final int STROKE_COLOR = Color.argb(180, 3, 145, 255);
    private static final int FILL_COLOR = Color.argb(10, 0, 0, 180);
    private boolean mFirstFix = false;

    private String sTime = "",eTime = "";

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.fragment_discover, container, false);
        mMapview = (MapView) view.findViewById(R.id.frame_dis_bmapView);
        mMapview.onCreate(savedInstanceState);
        aMap = mMapview.getMap();
        init();
        return view;
    }

    private void init() {
        aMap = mMapview.getMap();
        aMap.setOnMarkerClickListener(listener);
        aMap.setLocationSource(this);
        aMap.getUiSettings().setMyLocationButtonEnabled(true);
        aMap.setMyLocationEnabled(true);
        aMap.setMyLocationType(AMap.LOCATION_TYPE_LOCATE);
        setUpMap();
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
        aMap.setMyLocationEnabled(true);// 设置为true表示显示定位层并可触发定位，false表示隐藏定位层并不可触发定位，默认是false
    }

    private void getHistory(){
        OkHttpUtils
                .get()
                .url(AppInfo.HttpUrl + "locusHistory")
                .addParams("start", sTime)
                .addParams("end", eTime)
                .addParams("username", AppInfo.getString(getActivity(),"username"))
                .addParams("password", AppInfo.getString(getActivity(),"password"))
                .addParams("imei","0")
                .build()
                .execute(new StringCallback() {
                    @Override
                    public void onError(Call call, Exception e, int id) {
                        Log.d("locusHistory:","异常");
                        Toast.makeText(getActivity(), "获取数据出现异常", Toast.LENGTH_SHORT).show();
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
                                    Toast.makeText(getActivity(), "所查找时间内该设备并没有历史记录", Toast.LENGTH_SHORT).show();
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
//                                    showPosition(list.get(0),15);
                                }
                            }else{
                                Toast.makeText(getActivity(), msg, Toast.LENGTH_SHORT).show();
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

        Toast.makeText(getActivity(), "请选择开始时间", Toast.LENGTH_SHORT).show();
        AlertDialog.Builder builder = new AlertDialog.Builder(getActivity());
        final DatePicker data1 = new DatePicker(getActivity());
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
                Toast.makeText(getActivity(), "请选择结束时间", Toast.LENGTH_SHORT).show();
                AlertDialog.Builder builder = new AlertDialog.Builder(getActivity());
                final DatePicker data2 = new DatePicker(getActivity());
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
                    }
                });
                builder.setNegativeButton("取消",null);
                builder.create().show();
            }
        });
        builder.setNegativeButton("取消",null);
        builder.create().show();
    }

    private void showMarker(final LatLng latLng,String headUrl){
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
                        aMap.addMarker(new MarkerOptions().
                                position(latLng).
                                title("北京").
                                icon(BitmapDescriptorFactory.fromBitmap(response)).
                                snippet("DefaultMarker"));

                    }
                });
    }

    private void showPosition( LatLng latlng,int level){
        aMap.animateCamera(CameraUpdateFactory.newCameraPosition(new CameraPosition(latlng, level, 0, 0)), 600,null);
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

    private void showPolygon(LatLng[] latlngs){
        PolygonOptions options = new PolygonOptions();
        for (int i = 0; i < latlngs.length; i++){
            options.add(latlngs[i]);
        }
        mPolygon =  aMap.addPolygon(options.strokeWidth(1)
                .strokeColor(Color.argb(50, 1, 1, 1))
                .fillColor(Color.argb(50, 1, 1, 1)));

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
                    showPosition(new LatLng(amapLocation.getLatitude(),amapLocation.getLongitude()),12);
                    mFirstFix = true;
                }
            } else {
                String errText = "定位失败," + amapLocation.getErrorCode()+ ": " + amapLocation.getErrorInfo();
                Log.e("mapErr",errText);
            }
        }
    }

    @Override
    public void activate(OnLocationChangedListener listener) {
        mListener = listener;
        if (mlocationClient == null) {
            mlocationClient = new AMapLocationClient(getActivity());
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

    private void addCircle(LatLng latlng, double radius) {
        CircleOptions options = new CircleOptions();
        options.strokeWidth(1f);
        options.fillColor(FILL_COLOR);
        options.strokeColor(STROKE_COLOR);
        options.center(latlng);
        options.radius(radius);
        mCircle = aMap.addCircle(options);
    }

    private void addMarker(LatLng latlng) {
        Log.d(TAG, "addMarker: 民警用户");
        MarkerOptions options = new MarkerOptions();
        options.icon(mDes);
        options.anchor(0.5f, 0.5f);
        options.position(latlng);
        mLocMarker = aMap.addMarker(options);
        mLocMarker.setTitle("民警用户");
    }

    @Override
    public void onResume() {
        super.onResume();
        mMapview.onResume();
        mFirstFix = false;
        Log.d(TAG, "onResume: ");
    }

    @Override
    public void onPause() {
        super.onPause();
        mMapview.onPause();
        Log.d(TAG, "onPause: ");
    }

    @Override
    public void onDestroy() {
        super.onDestroy();
        mMapview.onDestroy();
        Log.d(TAG, "onDestroy: ");
    }

    @Override
    public void onSaveInstanceState(Bundle outState) {
        super.onSaveInstanceState(outState);
        mMapview.onSaveInstanceState(outState);
    }
}
