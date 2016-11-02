package com.brave.bookshop;

import android.Manifest;
import android.app.ProgressDialog;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.net.Uri;
import android.os.Bundle;
import android.support.v4.app.ActivityCompat;
import android.support.v7.app.AlertDialog;
import android.support.v7.app.AppCompatActivity;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.TextView;
import android.widget.Toast;

import com.brave.bookshop.database.book_list;
import com.brave.bookshop.database.collection;
import com.brave.bookshop.database.user;
import com.google.android.gms.appindexing.Action;
import com.google.android.gms.appindexing.AppIndex;
import com.google.android.gms.common.api.GoogleApiClient;

import java.util.List;

import cn.bmob.v3.BmobQuery;
import cn.bmob.v3.listener.FindListener;
import cn.bmob.v3.listener.SaveListener;
import cn.bmob.v3.listener.UpdateListener;

public class DetailsActivity extends AppCompatActivity {


    private String[] params;
    private String bid, uid;
    private String uName, uContact;
    /**
     * ATTENTION: This was auto-generated to implement the App Indexing API.
     * See https://g.co/AppIndexing/AndroidStudio for more information.
     */
    private GoogleApiClient client;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_details);
        params = getIntent().getStringArrayExtra("params");
        initView();
        initData();
        // ATTENTION: This was auto-generated to implement the App Indexing API.
        // See https://g.co/AppIndexing/AndroidStudio for more information.
        client = new GoogleApiClient.Builder(this).addApi(AppIndex.API).build();
    }

    private void initData() {
        bid = params[7];
        uid = params[8];

        BmobQuery<user> query = new BmobQuery<user>();
        query.addWhereEqualTo("u_email", uid);
        query.findObjects(this, new FindListener<user>() {
            @Override
            public void onSuccess(List<user> list) {
                if (list.size() != 0) {
                    uName = list.get(0).getU_name();
                    uContact = list.get(0).getU_contact();
                } else {
                    uName = "***";
                    uContact = "***-****-****";
                }
            }

            @Override
            public void onError(int i, String s) {
                Toast.makeText(DetailsActivity.this, s, Toast.LENGTH_SHORT).show();
            }
        });

        book_list b = new book_list();
        int browse = Integer.parseInt(params[4]);
        b.setB_browse((browse + 1) + "");
        b.update(this, bid, new UpdateListener() {
            @Override
            public void onSuccess() {

            }

            @Override
            public void onFailure(int i, String s) {

            }
        });

    }

    private void initView() {
        ((TextView) findViewById(R.id.details_title)).setText(params[0]);
        ((TextView) findViewById(R.id.details_quality)).setText(params[1]);
        ((TextView) findViewById(R.id.details_classification)).setText(params[2]);
        ((TextView) findViewById(R.id.details_price)).setText(params[3] + "元");
        ((TextView) findViewById(R.id.details_browse)).setText(params[4]);
        ((TextView) findViewById(R.id.details_time)).setText(params[5]);
        ((TextView) findViewById(R.id.details_details)).setText(params[6]);
    }


    public void dial(View view) {
        AlertDialog.Builder builder = new AlertDialog.Builder(this);
        builder.setTitle("是否拨号？");
        builder.setMessage(uName + "同学\n\n" + uContact);
        builder.setPositiveButton("拨号", new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialog, int which) {
                Intent intentPhone = new Intent(Intent.ACTION_CALL, Uri.parse("tel:" + "15222656877"));
                intentPhone.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
                if (ActivityCompat.checkSelfPermission(DetailsActivity.this, Manifest.permission.CALL_PHONE) != PackageManager.PERMISSION_GRANTED) {
                    return;
                }
                DetailsActivity.this.startActivity(intentPhone);
            }
        });
        builder.setNegativeButton("取消", null);
        AlertDialog dialog = builder.create();
        dialog.show();
    }


    private void addToCollection() {
        final ProgressDialog progressDialog = ProgressDialog.show(this,"请稍后","正在从服务器中获取数据");
        progressDialog.show();
        BmobQuery<collection> query = new BmobQuery<collection>();
        query.addWhereEqualTo("c_bid", bid);
        query.addWhereEqualTo("c_uid", uid);
        query.findObjects(this, new FindListener<collection>() {
            @Override
            public void onSuccess(List<collection> list) {
                if (list.size() == 0){
                    collection c = new collection();
                    c.setC_bid(bid);
                    c.setC_uid(uid);
                    c.save(DetailsActivity.this, new SaveListener() {
                        @Override
                        public void onSuccess() {
                            Toast.makeText(DetailsActivity.this, "收藏成功", Toast.LENGTH_SHORT).show();
                            progressDialog.dismiss();
                        }

                        @Override
                        public void onFailure(int i, String s) {
                            Toast.makeText(DetailsActivity.this, "发送错误:"+s, Toast.LENGTH_SHORT).show();
                            progressDialog.dismiss();
                        }
                    });
                }else{
                    Toast.makeText(DetailsActivity.this, "已经收藏过了", Toast.LENGTH_SHORT).show();
                    progressDialog.dismiss();
                }
            }

            @Override
            public void onError(int i, String s) {
                Toast.makeText(DetailsActivity.this, "发送错误:"+s, Toast.LENGTH_SHORT).show();
                progressDialog.dismiss();
            }
        });
    }


    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        // Inflate the menu; this adds items to the action bar if it is present.
        getMenuInflater().inflate(R.menu.details, menu);
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {

        int id = item.getItemId();

        if (id == R.id.action_collection) {
            addToCollection();
            return true;
        }


        return super.onOptionsItemSelected(item);
    }

    @Override
    public void onStart() {
        super.onStart();

        // ATTENTION: This was auto-generated to implement the App Indexing API.
        // See https://g.co/AppIndexing/AndroidStudio for more information.
        client.connect();
        Action viewAction = Action.newAction(
                Action.TYPE_VIEW, // TODO: choose an action type.
                "Details Page", // TODO: Define a title for the content shown.
                // TODO: If you have web page content that matches this app activity's content,
                // make sure this auto-generated web page URL is correct.
                // Otherwise, set the URL to null.
                Uri.parse("http://host/path"),
                // TODO: Make sure this auto-generated app URL is correct.
                Uri.parse("android-app://com.brave.bookshop/http/host/path")
        );
        AppIndex.AppIndexApi.start(client, viewAction);
    }

    @Override
    public void onStop() {
        super.onStop();

        // ATTENTION: This was auto-generated to implement the App Indexing API.
        // See https://g.co/AppIndexing/AndroidStudio for more information.
        Action viewAction = Action.newAction(
                Action.TYPE_VIEW, // TODO: choose an action type.
                "Details Page", // TODO: Define a title for the content shown.
                // TODO: If you have web page content that matches this app activity's content,
                // make sure this auto-generated web page URL is correct.
                // Otherwise, set the URL to null.
                Uri.parse("http://host/path"),
                // TODO: Make sure this auto-generated app URL is correct.
                Uri.parse("android-app://com.brave.bookshop/http/host/path")
        );
        AppIndex.AppIndexApi.end(client, viewAction);
        client.disconnect();
    }
}
