package com.brave.bookshop;

import android.app.ProgressDialog;
import android.content.DialogInterface;
import android.content.Intent;
import android.net.Uri;
import android.os.Bundle;
import android.os.Handler;
import android.support.design.widget.FloatingActionButton;
import android.support.design.widget.NavigationView;
import android.support.v4.view.GravityCompat;
import android.support.v4.widget.DrawerLayout;
import android.support.v7.app.ActionBarDrawerToggle;
import android.support.v7.app.AlertDialog;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ListView;
import android.widget.TextView;
import android.widget.Toast;

import com.brave.bookshop.adapter.BookListAdapter;
import com.brave.bookshop.data.BookData;
import com.brave.bookshop.data.UserData;
import com.brave.bookshop.database.book_list;
import com.brave.bookshop.view.BookListItem;
import com.google.android.gms.appindexing.Action;
import com.google.android.gms.appindexing.AppIndex;
import com.google.android.gms.common.api.GoogleApiClient;

import java.util.List;

public class MainActivity extends AppCompatActivity
        implements NavigationView.OnNavigationItemSelectedListener {

    /**
     * ATTENTION: This was auto-generated to implement the App Indexing API.
     * See https://g.co/AppIndexing/AndroidStudio for more information.
     */
    private GoogleApiClient client;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        setContentView(R.layout.activity_main);

        Toolbar toolbar = (Toolbar) findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);

        FloatingActionButton fab = (FloatingActionButton) findViewById(R.id.fab);
        fab.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Intent i = new Intent();
                i.setClass(MainActivity.this,SendActivity.class);
                startActivity(i);
            }
        });




        DrawerLayout drawer = (DrawerLayout) findViewById(R.id.drawer_layout);
        ActionBarDrawerToggle toggle = new ActionBarDrawerToggle(
                this, drawer, toolbar, R.string.navigation_drawer_open, R.string.navigation_drawer_close);
        drawer.setDrawerListener(toggle);
        toggle.syncState();


        NavigationView navigationView = (NavigationView) findViewById(R.id.nav_view);
        navigationView.setNavigationItemSelectedListener(this);

        View mHeadView = navigationView.getHeaderView(0);

        TextView mUserName = (TextView)mHeadView.findViewById(R.id.nav_header_user_name);
        mUserName.setText(UserData.getName(this));

        TextView mUserEmail = (TextView)mHeadView.findViewById(R.id.nav_header_user_email);
        mUserEmail.setText(UserData.getEmail(this));


        // ATTENTION: This was auto-generated to implement the App Indexing API.
        // See https://g.co/AppIndexing/AndroidStudio for more information.
        client = new GoogleApiClient.Builder(this).addApi(AppIndex.API).build();
    }




    @Override
    protected void onResume() {
        super.onResume();

        final ProgressDialog progressDialog = ProgressDialog.show(this,"请稍后","正在从服务器中获取数据");
        progressDialog.show();

        final ListView list = (ListView)findViewById(R.id.main_list);
        final List<BookListItem> data = BookData.getBookList(MainActivity.this);
        new Handler().postDelayed(new Runnable() {
            @Override
            public void run() {

                list.setAdapter(new BookListAdapter(data));
                list.setOnItemClickListener(new ListView.OnItemClickListener() {
                    @Override
                    public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
                        book_list b = data.get(position).getBook();
                        String[] params = new String[]{b.getB_name(),BookData.mBookQuality[b.getB_quality()],BookData.mBookClassification[b.getB_classification()],b.getB_price().toString(),b.getB_browse(),b.getCreatedAt(),b.getB_details(),b.getObjectId(),b.getB_uid()};
                        Intent i = new Intent();
                        i.putExtra("params",params);
                        i.setClass(MainActivity.this,DetailsActivity.class);
                        startActivity(i);
                    }
                });
                progressDialog.dismiss();
            }
        },3000);

    }

    @Override
    public void onBackPressed() {
        DrawerLayout drawer = (DrawerLayout) findViewById(R.id.drawer_layout);
        if (drawer.isDrawerOpen(GravityCompat.START)) {
            drawer.closeDrawer(GravityCompat.START);
        } else {
            super.onBackPressed();
        }
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        // Inflate the menu; this adds items to the action bar if it is present.
        getMenuInflater().inflate(R.menu.main, menu);
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {

        int id = item.getItemId();

        if (id == R.id.action_search) {
            Intent i = new Intent();
            i.setClass(MainActivity.this,SearchActivity.class);
            startActivityForResult(i,0);
            return true;
        }

        if(id == R.id.action_sort){
            Intent i = new Intent();
            i.setClass(MainActivity.this,SortActivity.class);
            startActivityForResult(i,1);
            return true;
        }

        return super.onOptionsItemSelected(item);
    }

    @SuppressWarnings("StatementWithEmptyBody")
    @Override
    public boolean onNavigationItemSelected(MenuItem item) {
        // Handle navigation view item clicks here.
        int id = item.getItemId();

        if (id == R.id.nav_send_list) {
            Intent i = new Intent();
            i.setClass(MainActivity.this,SendListActivity.class);
            startActivity(i);
        } else if (id == R.id.nav_collection_list) {
            Intent i = new Intent();
            i.setClass(MainActivity.this,CollectionActivity.class);
            startActivity(i);
        } else if (id == R.id.nav_message_list) {
            toast("消息列表");
        } else if (id == R.id.nav_about) {
           alert();
        } else if (id == R.id.nav_exit) {
            UserData.clearUserData(this);
            Intent i = new Intent();
            i.setClass(MainActivity.this,LoginActivity.class);
            startActivity(i);
            finish();
        }

        DrawerLayout drawer = (DrawerLayout) findViewById(R.id.drawer_layout);
        drawer.closeDrawer(GravityCompat.START);
        return false;
    }

    private void alert() {
        AlertDialog.Builder builder = new AlertDialog.Builder(this);
        builder.setTitle("关于");
        builder.setMessage("网上二手书屋是随着网络的发展与新型商务模式的出现而设计与开发的一款方便当代大学生学习的APP平台。对于当代大学生这个庞大的社会群体，教科书可谓是必备品。二手书屋APP平台是通过一个网络交互平台，实现资源共享，使学生所拥有的各类书籍资料得到更合理的利用，从而减少浪费。");
        builder.setPositiveButton("知道了",null);
        AlertDialog dialog = builder.create();
        dialog.show();
    }


    private void toast(String s) {
        Toast.makeText(MainActivity.this, s, Toast.LENGTH_SHORT).show();
    }

    @Override
    public void onStart() {
        super.onStart();

        // ATTENTION: This was auto-generated to implement the App Indexing API.
        // See https://g.co/AppIndexing/AndroidStudio for more information.
        client.connect();
        Action viewAction = Action.newAction(
                Action.TYPE_VIEW, // TODO: choose an action type.
                "Main Page", // TODO: Define a title for the content shown.
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
                "Main Page", // TODO: Define a title for the content shown.
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
