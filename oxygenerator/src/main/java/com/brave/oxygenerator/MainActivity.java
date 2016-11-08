package com.brave.oxygenerator;

import android.app.FragmentManager;
import android.app.FragmentTransaction;
import android.content.Intent;
import android.net.Uri;
import android.os.Bundle;
import android.support.design.widget.NavigationView;
import android.support.v4.view.GravityCompat;
import android.support.v4.widget.DrawerLayout;
import android.support.v7.app.ActionBarDrawerToggle;
import android.support.v7.app.AlertDialog;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.Menu;
import android.view.MenuItem;
import android.view.MotionEvent;
import android.view.View;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import com.brave.oxygenerator.frame.DataFrame;
import com.brave.oxygenerator.frame.HomeFrame;
import com.brave.oxygenerator.frame.MessageFrame;
import com.brave.oxygenerator.frame.SettingFrame;
import com.brave.oxygenerator.util.PreferenceUtil;
import com.google.android.gms.appindexing.Action;
import com.google.android.gms.appindexing.AppIndex;
import com.google.android.gms.appindexing.Thing;
import com.google.android.gms.common.api.GoogleApiClient;
import com.zhy.http.okhttp.OkHttpUtils;
import com.zhy.http.okhttp.callback.StringCallback;

import org.json.JSONException;
import org.json.JSONObject;

import okhttp3.Call;

public class MainActivity extends AppCompatActivity implements NavigationView.OnNavigationItemSelectedListener {

    private Menu mMenu;

    private GoogleApiClient client;

    private View mHaederView;
    private ImageView mUserHeadView;
    private TextView mUserNameView,mUserTipsView;

    private boolean isOpenSwitch = false;
    private int mBackKeyPressedTimes = 0;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        Toolbar toolbar = (Toolbar) findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);

        DrawerLayout drawer = (DrawerLayout) findViewById(R.id.drawer_layout);
        ActionBarDrawerToggle toggle = new ActionBarDrawerToggle(
                this, drawer, toolbar, R.string.navigation_drawer_open, R.string.navigation_drawer_close);
        drawer.setDrawerListener(toggle);
        toggle.syncState();

        NavigationView navigationView = (NavigationView) findViewById(R.id.nav_view);
        navigationView.setNavigationItemSelectedListener(this);

        mHaederView = navigationView.getHeaderView(0);

        client = new GoogleApiClient.Builder(this).addApi(AppIndex.API).build();

        setDefaultFragment();

        initView();
        initData();
    }

    private void initView() {
        mUserHeadView = (ImageView)mHaederView.findViewById(R.id.nav_header_user_head);
        mUserNameView = (TextView)mHaederView.findViewById(R.id.nav_header_user_name);
        mUserTipsView = (TextView)mHaederView.findViewById(R.id.nav_header_user_time);

        mHaederView.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent i = new Intent();
                i.setClass(MainActivity.this,UserActivity.class);
                startActivity(i);
            }
        });


        mUserNameView.setText("未填写昵称");
        mUserHeadView.setImageResource(R.mipmap.head_null);
        mUserTipsView.setText("累计已使用"+0+"小时");



    }

    private void initData() {

        OkHttpUtils
                .post()
                .url("http://airsep.routeyo.net/airsep/lutsoft/sso/toProfile.action")
                .addParams("ajax", "true")
                .build()
                .execute(new StringCallback() {
                    @Override
                    public void onError(Call call, Exception e, int id) {
                        Toast.makeText(MainActivity.this, "获取用户资料失败：请检查网络", Toast.LENGTH_LONG).show();
                    }

                    @Override
                    public void onResponse(String response, int id) {

                        Log.d("MainActivity",response);

                        try {
                            JSONObject jo = new JSONObject(response);
                            final String userId = jo.getString("id");
                            final String name = jo.getString("name");
                            final String age = jo.getString("age");
                            final String male = jo.getString("male");
                            final String profession = jo.getString("profession");
                            final String mphone = jo.getString("mphone");
                            final String email = jo.getString("email");
                            final String userLogoKey = jo.getString("userLogoKey");

                            PreferenceUtil.setUserInfo(MainActivity.this,"userId",userId);
                            PreferenceUtil.setUserInfo(MainActivity.this,"name",name);
                            PreferenceUtil.setUserInfo(MainActivity.this,"age",age);
                            PreferenceUtil.setUserInfo(MainActivity.this,"male",male);
                            PreferenceUtil.setUserInfo(MainActivity.this,"profession",profession);
                            PreferenceUtil.setUserInfo(MainActivity.this,"mphone",mphone);
                            PreferenceUtil.setUserInfo(MainActivity.this,"email",email);
                            PreferenceUtil.setUserInfo(MainActivity.this,"userLogoKey",userLogoKey);

                            if(name.equals("") == false){
                                mUserNameView.setText(name);
                            }

                        } catch (JSONException e) {
                            e.printStackTrace();
                        }
                    }
                });
    }



    @Override
    public void onBackPressed() {
        DrawerLayout drawer = (DrawerLayout) findViewById(R.id.drawer_layout);
        if (drawer.isDrawerOpen(GravityCompat.START)) {
            drawer.closeDrawer(GravityCompat.START);
        } else {
            if (mBackKeyPressedTimes == 0) {
                Toast.makeText(this, "再按一次退出程序 ", Toast.LENGTH_SHORT).show();
                mBackKeyPressedTimes = 1;
                new Thread() {
                    @Override
                    public void run() {
                        try {
                            Thread.sleep(2000);
                        } catch (InterruptedException e) {
                            e.printStackTrace();
                        } finally {
                            mBackKeyPressedTimes = 0;
                        }
                    }
                }.start();
                return;
            } else {
                MainActivity.this.finish();
            }
            super.onBackPressed();

        }
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        // Inflate the menu; this adds items to the action bar if it is present.
        getMenuInflater().inflate(R.menu.main, menu);
        mMenu = menu;
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        // Handle action bar item clicks here. The action bar will
        // automatically handle clicks on the Home/Up button, so long
        // as you specify a parent activity in AndroidManifest.xml.
        int id = item.getItemId();

        //noinspection SimplifiableIfStatement
        if (id == R.id.action_settings) {
            return true;
        }

        return super.onOptionsItemSelected(item);
    }




    @SuppressWarnings("StatementWithEmptyBody")
    @Override
    public boolean onNavigationItemSelected(MenuItem item) {
        // Handle navigation view item clicks here.
        int id = item.getItemId();
        FragmentManager fm = getFragmentManager();
        FragmentTransaction transaction = fm.beginTransaction();

        if (id == R.id.nav_home) {
            setTitle("室内环境");
            mMenu.findItem(R.id.action_settings).setVisible(true);
            transaction.replace(R.id.main_frame_layout, new HomeFrame());
        } else if (id == R.id.nav_data) {
            setTitle("数据中心");
            mMenu.findItem(R.id.action_settings).setVisible(false);
            transaction.replace(R.id.main_frame_layout, new DataFrame());
        } else if (id == R.id.nav_msg) {
            setTitle("消息");
            mMenu.findItem(R.id.action_settings).setVisible(false);
            transaction.replace(R.id.main_frame_layout, new MessageFrame());
        } else if (id == R.id.nav_setting) {
            setTitle("设置");
            mMenu.findItem(R.id.action_settings).setVisible(false);
            transaction.replace(R.id.main_frame_layout, new SettingFrame());
        } else if (id == R.id.nav_switch) {
            setAlertDialog();
        } else if (id == R.id.nav_logout) {

            PreferenceUtil.setIsLogin(MainActivity.this,false);

            Intent i = new Intent();
            i.setClass(MainActivity.this,LoginActivity.class);
            startActivity(i);
            MainActivity.this.finish();
        }
        transaction.commit();
        DrawerLayout drawer = (DrawerLayout) findViewById(R.id.drawer_layout);
        drawer.closeDrawer(GravityCompat.START);
        return true;
    }

    private void setDefaultFragment() {
        FragmentManager fm = getFragmentManager();
        FragmentTransaction transaction = fm.beginTransaction();
        transaction.replace(R.id.main_frame_layout, new HomeFrame());
        transaction.commit();
    }

    private void setAlertDialog(){
        AlertDialog.Builder builder = new AlertDialog.Builder(this);
        View view = LayoutInflater.from(this).inflate(R.layout.view_dialog_sw,null);
        final ImageView img = (ImageView) view.findViewById(R.id.view_dialog_sw_img);
        final TextView tips = (TextView)view.findViewById(R.id.view_dialog_sw_tips);
        img.setOnTouchListener(new View.OnTouchListener() {
            @Override
            public boolean onTouch(View v, MotionEvent event) {
                if(isOpenSwitch){
                    img.setImageResource(R.mipmap.sw_on);
                    tips.setText("开启");
                }else{
                    img.setImageResource(R.mipmap.sw_off);
                    tips.setText("关闭");
                }
                isOpenSwitch = !isOpenSwitch;
                return false;
            }
        });
        builder.setView(view);

        builder.create().show();
    }


    /**
     * ATTENTION: This was auto-generated to implement the App Indexing API.
     * See https://g.co/AppIndexing/AndroidStudio for more information.
     */
    public Action getIndexApiAction() {
        Thing object = new Thing.Builder()
                .setName("Main Page") // TODO: Define a title for the content shown.
                // TODO: Make sure this auto-generated URL is correct.
                .setUrl(Uri.parse("http://[ENTER-YOUR-URL-HERE]"))
                .build();
        return new Action.Builder(Action.TYPE_VIEW)
                .setObject(object)
                .setActionStatus(Action.STATUS_TYPE_COMPLETED)
                .build();
    }

    @Override
    public void onStart() {
        super.onStart();

        // ATTENTION: This was auto-generated to implement the App Indexing API.
        // See https://g.co/AppIndexing/AndroidStudio for more information.
        client.connect();
        AppIndex.AppIndexApi.start(client, getIndexApiAction());
    }

    @Override
    public void onStop() {
        super.onStop();

        // ATTENTION: This was auto-generated to implement the App Indexing API.
        // See https://g.co/AppIndexing/AndroidStudio for more information.
        AppIndex.AppIndexApi.end(client, getIndexApiAction());
        client.disconnect();
    }
}
