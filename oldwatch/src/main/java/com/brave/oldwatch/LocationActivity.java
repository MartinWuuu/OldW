package com.brave.oldwatch;

import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.Matrix;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.util.Log;
import android.widget.Toast;

import com.baidu.location.BDLocation;
import com.baidu.location.BDLocationListener;
import com.baidu.location.LocationClient;
import com.baidu.location.LocationClientOption;
import com.baidu.location.Poi;
import com.baidu.mapapi.SDKInitializer;
import com.baidu.mapapi.map.BaiduMap;
import com.baidu.mapapi.map.BitmapDescriptor;
import com.baidu.mapapi.map.BitmapDescriptorFactory;
import com.baidu.mapapi.map.MapStatus;
import com.baidu.mapapi.map.MapStatusUpdateFactory;
import com.baidu.mapapi.map.MapView;
import com.baidu.mapapi.map.Marker;
import com.baidu.mapapi.map.MarkerOptions;
import com.baidu.mapapi.map.MyLocationConfiguration;
import com.baidu.mapapi.map.MyLocationData;
import com.baidu.mapapi.map.OverlayOptions;
import com.baidu.mapapi.model.LatLng;
import com.zhy.http.okhttp.OkHttpUtils;
import com.zhy.http.okhttp.callback.BitmapCallback;

import java.util.List;

import okhttp3.Call;

public class LocationActivity extends AppCompatActivity {

    private MapView mMapView = null;
    private BaiduMap mBaiduMap;

    public LocationClient mLocationClient = null;
    public BDLocationListener myListener = new MyLocationListener();

    private boolean isFirstLoc = true;

    private long latlng[];

    private String headUrl;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        latlng = getIntent().getLongArrayExtra("latlng");
        headUrl = getIntent().getStringExtra("head");
        setContentView(R.layout.activity_location);
        initMap();
    }

    private void initMap() {
        mMapView = (MapView) findViewById(R.id.bmapView);
        mBaiduMap = mMapView.getMap();
        mBaiduMap.setMapType(BaiduMap.MAP_TYPE_NORMAL);
        mLocationClient = new LocationClient(getApplicationContext());     //声明LocationClient类
        mLocationClient.registerLocationListener( myListener );


        LocationClientOption option = new LocationClientOption();
        option.setLocationMode(LocationClientOption.LocationMode.Hight_Accuracy);//可选，默认高精度，设置定位模式，高精度，低功耗，仅设备
        option.setCoorType("bd09ll");//可选，默认gcj02，设置返回的定位结果坐标系
        int span=1000;
        option.setScanSpan(span);//可选，默认0，即仅定位一次，设置发起定位请求的间隔需要大于等于1000ms才是有效的
        option.setIsNeedAddress(true);//可选，设置是否需要地址信息，默认不需要
        option.setOpenGps(true);//可选，默认false,设置是否使用gps
        option.setLocationNotify(true);//可选，默认false，设置是否当GPS有效时按照1S/1次频率输出GPS结果
        option.setIsNeedLocationDescribe(true);//可选，默认false，设置是否需要位置语义化结果，可以在BDLocation.getLocationDescribe里得到，结果类似于“在北京天安门附近”
        option.setIsNeedLocationPoiList(true);//可选，默认false，设置是否需要POI结果，可以在BDLocation.getPoiList里得到
        option.setIgnoreKillProcess(false);//可选，默认true，定位SDK内部是一个SERVICE，并放到了独立进程，设置是否在stop的时候杀死这个进程，默认不杀死
        option.SetIgnoreCacheException(false);//可选，默认false，设置是否收集CRASH信息，默认收集
        option.setEnableSimulateGps(false);//可选，默认false，设置是否需要过滤GPS仿真结果，默认需要
        mLocationClient.setLocOption(option);
        mBaiduMap.setMyLocationEnabled(true);
        mLocationClient.start();



        OkHttpUtils
                .get()
                .url(headUrl)
                .build()
                .execute(new BitmapCallback() {
                    @Override
                    public void onError(Call call, Exception e, int id) {
                    }
                    @Override
                    public void onResponse(Bitmap response, int id) {
                        LatLng ll = new LatLng(latlng[0],latlng[1]);
                        MapStatus.Builder builder = new MapStatus.Builder();
                        builder.target(ll);//.zoom(18.0f);
                        mBaiduMap.animateMapStatus(MapStatusUpdateFactory.newMapStatus(builder.build()));

                        LatLng point = new LatLng(latlng[0],latlng[1]);
                        BitmapDescriptor bitmap = BitmapDescriptorFactory.fromBitmap(response);
                        OverlayOptions option = new MarkerOptions()
                                .position(point)
                                .icon(bitmap);
                        mBaiduMap.addOverlay(option);
                    }
                });

    }


    private class MyLocationListener implements BDLocationListener {

        @Override
        public void onReceiveLocation(BDLocation location) {


            if (location == null || mMapView == null) {
                return;
            }
            MyLocationData locData = new MyLocationData.Builder()
                    .accuracy(0)
                    .direction(0).latitude(location.getLatitude())
                    .longitude(location.getLongitude()).build();

            mBaiduMap.setMyLocationData(locData);
            mBaiduMap.setOnMyLocationClickListener(mMyLocationClickListenerListener);
            Bitmap head = zoomImg(BitmapFactory.decodeResource(getResources(),R.mipmap.head),90,90);
            BitmapDescriptor mCurrentMarker = BitmapDescriptorFactory.fromBitmap(head);
            MyLocationConfiguration config = new MyLocationConfiguration(MyLocationConfiguration.LocationMode.NORMAL, true, mCurrentMarker);
            mBaiduMap.setMyLocationConfigeration(config);
            if (isFirstLoc) {
                isFirstLoc = false;
                LatLng ll = new LatLng(location.getLatitude(),
                        location.getLongitude());
                MapStatus.Builder builder = new MapStatus.Builder();
                builder.target(ll).zoom(10.0f);
                mBaiduMap.animateMapStatus(MapStatusUpdateFactory.newMapStatus(builder.build()));

            }



            StringBuffer sb = new StringBuffer(256);
            sb.append("time : ");
            sb.append(location.getTime());
            sb.append("\nerror code : ");
            sb.append(location.getLocType());
            sb.append("\nlatitude : ");
            sb.append(location.getLatitude());
            sb.append("\nlontitude : ");
            sb.append(location.getLongitude());
            sb.append("\nradius : ");
            sb.append(location.getRadius());
            if (location.getLocType() == BDLocation.TypeGpsLocation){// GPS定位结果
                sb.append("\nspeed : ");
                sb.append(location.getSpeed());// 单位：公里每小时
                sb.append("\nsatellite : ");
                sb.append(location.getSatelliteNumber());
                sb.append("\nheight : ");
                sb.append(location.getAltitude());// 单位：米
                sb.append("\ndirection : ");
                sb.append(location.getDirection());// 单位度
                sb.append("\naddr : ");
                sb.append(location.getAddrStr());
                sb.append("\ndescribe : ");
                sb.append("gps定位成功");

            } else if (location.getLocType() == BDLocation.TypeNetWorkLocation){// 网络定位结果
                sb.append("\naddr : ");
                sb.append(location.getAddrStr());
                //运营商信息
                sb.append("\noperationers : ");
                sb.append(location.getOperators());
                sb.append("\ndescribe : ");
                sb.append("网络定位成功");
            } else if (location.getLocType() == BDLocation.TypeOffLineLocation) {// 离线定位结果
                sb.append("\ndescribe : ");
                sb.append("离线定位成功，离线定位结果也是有效的");
            } else if (location.getLocType() == BDLocation.TypeServerError) {
                sb.append("\ndescribe : ");
                sb.append("服务端网络定位失败，可以反馈IMEI号和大体定位时间到loc-bugs@baidu.com，会有人追查原因");
            } else if (location.getLocType() == BDLocation.TypeNetWorkException) {
                sb.append("\ndescribe : ");
                sb.append("网络不同导致定位失败，请检查网络是否通畅");
            } else if (location.getLocType() == BDLocation.TypeCriteriaException) {
                sb.append("\ndescribe : ");
                sb.append("无法获取有效定位依据导致定位失败，一般是由于手机的原因，处于飞行模式下一般会造成这种结果，可以试着重启手机");
            }
            sb.append("\nlocationdescribe : ");
            sb.append(location.getLocationDescribe());// 位置语义化信息
            List<Poi> list = location.getPoiList();// POI数据
            if (list != null) {
                sb.append("\npoilist size = : ");
                sb.append(list.size());
                for (Poi p : list) {
                    sb.append("\npoi= : ");
                    sb.append(p.getId() + " " + p.getName() + " " + p.getRank());
                }
            }
            Log.i("BaiduLocationApiDem", sb.toString());

        }
    }

    private BaiduMap.OnMyLocationClickListener mMyLocationClickListenerListener = new BaiduMap.OnMyLocationClickListener() {

         @Override
         public boolean onMyLocationClick() {
             Toast.makeText(LocationActivity.this, "我的位置", Toast.LENGTH_SHORT).show();
             return false;
         }
     };

    private BaiduMap.OnMarkerClickListener listener = new BaiduMap.OnMarkerClickListener() {

        @Override
        public boolean onMarkerClick(Marker marker) {
            Toast.makeText(LocationActivity.this, "被关注人的位置", Toast.LENGTH_SHORT).show();
            return false;
        }
    };


    public static Bitmap zoomImg(Bitmap bm, int newWidth , int newHeight){
        int width = bm.getWidth();
        int height = bm.getHeight();
        float scaleWidth = ((float) newWidth) / width;
        float scaleHeight = ((float) newHeight) / height;
        Matrix matrix = new Matrix();
        matrix.postScale(scaleWidth, scaleHeight);
        Bitmap newbm = Bitmap.createBitmap(bm, 0, 0, width, height, matrix, true);
        return newbm;
    }


    @Override
    protected void onDestroy() {
        super.onDestroy();
        mMapView.onDestroy();
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
}