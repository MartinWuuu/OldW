package com.brave.oldwatch;

import android.content.DialogInterface;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.Color;
import android.os.Bundle;
import android.support.v7.app.AlertDialog;
import android.support.v7.app.AppCompatActivity;
import android.util.Log;
import android.view.Menu;
import android.view.MenuItem;
import android.widget.DatePicker;
import android.widget.Toast;

import com.amap.api.location.AMapLocation;
import com.amap.api.location.AMapLocationClient;
import com.amap.api.location.AMapLocationClientOption;
import com.amap.api.location.AMapLocationListener;
import com.amap.api.location.AMapLocationClientOption.AMapLocationMode;
import com.amap.api.maps.AMap;
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
import com.amap.api.maps.model.Polygon;
import com.amap.api.maps.model.PolygonOptions;
import com.amap.api.maps.model.PolylineOptions;
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

public class LocationActivity extends AppCompatActivity implements LocationSource,
        AMapLocationListener {

    private static final String TAG = "LocationActivity";

    private MapView mMapView;
    private AMap aMap;
    private OnLocationChangedListener mListener;
    private AMapLocationClient mlocationClient;
    private AMapLocationClientOption mLocationOption;

    private static final int STROKE_COLOR = Color.argb(180, 3, 145, 255);
    private static final int FILL_COLOR = Color.argb(10, 0, 0, 180);
    private boolean mFirstFix = false;
    private Marker mLocMarker;
    private Circle mCircle;
    private Polygon mPolygon;

    private String headUrl;
    private String sTime = "",eTime = "";
    private boolean isShowFan = false;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_location);
        mMapView = (MapView) findViewById(R.id.bmapView);
        mMapView.onCreate(savedInstanceState);
        init();
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        getMenuInflater().inflate(R.menu.location, menu);
        return super.onCreateOptionsMenu(menu);
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {

        int id = item.getItemId();

        if (id == R.id.action_history) {
            showHistoryAlert();
            return true;
        }

        if (id == R.id.action_fan) {
            isShowFan = !isShowFan;
            if(isShowFan){
                double[] lats = getIntent().getDoubleArrayExtra("lats");
                double[] lngs = getIntent().getDoubleArrayExtra("lngs");
                final LatLng[] latlngs = new LatLng[lats.length];
                for (int i = 0; i < lats.length;i++){
                    latlngs[i] = new LatLng(lats[i],lngs[i]);
                }
                showPolygon(latlngs);
                showPosition(latlngs[0],17);
                Toast.makeText(this, "显示电子围栏", Toast.LENGTH_SHORT).show();
            }else{
                mPolygon.remove();
                Toast.makeText(this, "电子围栏已被隐藏", Toast.LENGTH_SHORT).show();
            }
            return true;
        }
        return super.onOptionsItemSelected(item);
    }

    private void showHistoryAlert(){
        Calendar calendar = Calendar.getInstance();
        final int year = calendar.get(Calendar.YEAR);
        final int month = calendar.get(Calendar.MONTH);
        final int day = calendar.get(Calendar.DAY_OF_MONTH);

        sTime = year +"-"+(month+1)+"-"+day;
        eTime = year +"-"+(month+1)+"-"+day;

        Toast.makeText(this, "请选择开始时间", Toast.LENGTH_SHORT).show();
        AlertDialog.Builder builder = new AlertDialog.Builder(this);
        final DatePicker data1 = new DatePicker(this);
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
                Toast.makeText(LocationActivity.this, "请选择结束时间", Toast.LENGTH_SHORT).show();
                AlertDialog.Builder builder = new AlertDialog.Builder(LocationActivity.this);
                final DatePicker data2 = new DatePicker(LocationActivity.this);
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

    private void getHistory(){
        OkHttpUtils
                .get()
                .url(AppInfo.HttpUrl + "locusHistory")
                .addParams("start", sTime)
                .addParams("end", eTime)
                .addParams("username", AppInfo.getString(this,"username"))
                .addParams("password", AppInfo.getString(this,"password"))
                .addParams("imei",getIntent().getStringExtra("imei"))
                .build()
                .execute(new StringCallback() {
                    @Override
                    public void onError(Call call, Exception e, int id) {
                        Log.d("locusHistory:","异常");
                        Toast.makeText(LocationActivity.this, "获取数据出现异常", Toast.LENGTH_SHORT).show();
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
                                    Toast.makeText(LocationActivity.this, "所查找时间内该设备并没有历史记录", Toast.LENGTH_SHORT).show();
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
                                Toast.makeText(LocationActivity.this, msg, Toast.LENGTH_SHORT).show();
                            }
                        } catch (JSONException e) {
                            e.printStackTrace();
                        }
                    }
                });
    }

    private void init() {

        aMap = mMapView.getMap();
        aMap.setOnMarkerClickListener(listener);

        aMap.setLocationSource(this);
        aMap.getUiSettings().setMyLocationButtonEnabled(true);
        aMap.setMyLocationEnabled(true);
        aMap.setMyLocationType(AMap.LOCATION_TYPE_LOCATE);

        headUrl = getIntent().getStringExtra("head");

        showMarker(new LatLng(getIntent().getDoubleArrayExtra("latlng")[0],getIntent().getDoubleArrayExtra("latlng")[1
                ]));



    }

    private void showMarker(final LatLng latLng){
        OkHttpUtils
                .get()
                .url(headUrl)
                .build()
                .execute(new BitmapCallback() {
                    @Override
                    public void onError(Call call, Exception e, int id) {
                        Log.d(TAG, "onError");
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
        aMap.animateCamera(CameraUpdateFactory.newCameraPosition(new CameraPosition(latlng, level, 0, 0)), 1000,null);
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
//            Toast.makeText(LocationActivity.this, "onclick", Toast.LENGTH_SHORT).show();
            return false;
        }
    };


    @Override
    public void onLocationChanged(AMapLocation amapLocation) {
        if (mListener != null && amapLocation != null) {
            if (amapLocation != null
                    && amapLocation.getErrorCode() == 0) {
                LatLng location = new LatLng(amapLocation.getLatitude(), amapLocation.getLongitude());
                if (!mFirstFix) {
                    mFirstFix = true;
                    addCircle(location, amapLocation.getAccuracy());//添加定位精度圆
                    addMarker(location);//添加定位图标
                    showPosition(location,8);
                } else {
                    mCircle.setCenter(location);
                    mCircle.setRadius(amapLocation.getAccuracy());
                    mLocMarker.setPosition(location);
                }

            } else {
                String errText = "定位失败," + amapLocation.getErrorCode()+ ": " + amapLocation.getErrorInfo();
                Log.e("AmapErr",errText);
            }
        }
    }

    @Override
    public void activate(OnLocationChangedListener listener) {
        mListener = listener;
        if (mlocationClient == null) {
            mlocationClient = new AMapLocationClient(this);
            mLocationOption = new AMapLocationClientOption();
            mlocationClient.setLocationListener(this);
            mLocationOption.setLocationMode(AMapLocationMode.Hight_Accuracy);
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
        if (mLocMarker != null) {
            return;
        }
        Bitmap bMap = BitmapFactory.decodeResource(this.getResources(),
                R.mipmap.head);
        BitmapDescriptor des = BitmapDescriptorFactory.fromBitmap(bMap);

        MarkerOptions options = new MarkerOptions();
        options.icon(des);
        options.anchor(0.5f, 0.5f);
        options.position(latlng);
        mLocMarker = aMap.addMarker(options);
        mLocMarker.setTitle("民警用户");
    }

    @Override
    protected void onResume() {
        super.onResume();
        mMapView.onResume();
    }

    @Override
    protected void onPause() {
        super.onPause();
        mMapView.onPause();
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
        mMapView.onDestroy();
    }

    @Override
    protected void onSaveInstanceState(Bundle outState) {
        super.onSaveInstanceState(outState);
        mMapView.onSaveInstanceState(outState);
    }


}
