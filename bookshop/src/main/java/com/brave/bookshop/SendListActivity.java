package com.brave.bookshop;

import android.app.ProgressDialog;
import android.content.DialogInterface;
import android.content.Intent;
import android.net.Uri;
import android.os.Handler;
import android.support.v7.app.AlertDialog;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ListView;
import android.widget.Toast;

import com.brave.bookshop.adapter.BookListAdapter;
import com.brave.bookshop.data.BookData;
import com.brave.bookshop.database.book_list;
import com.brave.bookshop.view.BookListItem;

import java.util.List;

import cn.bmob.v3.listener.DeleteListener;
import cn.bmob.v3.listener.UpdateListener;

public class SendListActivity extends AppCompatActivity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_send_list);

    }


    @Override
    protected void onResume() {
        super.onResume();
       init();
    }

    private void init(){
        final ProgressDialog progressDialog = ProgressDialog.show(this,"请稍后","正在从服务器中获取数据");
        progressDialog.show();
        final ListView list = (ListView)findViewById(R.id.send_list_list);
        final List<BookListItem> data = BookData.getSendBookList(SendListActivity.this);
        new Handler().postDelayed(new Runnable() {
            @Override
            public void run() {
                list.setAdapter(new BookListAdapter(data));
                list.setOnItemClickListener(new ListView.OnItemClickListener() {
                    @Override
                    public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
                        alert(data.get(position).getBook());
                    }
                });
                progressDialog.dismiss();
            }
        }, 3000);
    }

    public void alert(book_list b){
        final ProgressDialog progressDialog = ProgressDialog.show(this,"请稍后","正在从服务器中获取数据");
        progressDialog.show();
        final String id = b.getObjectId();
        final boolean state = b.getB_state();
        String button = state?"下架":"重新上架";
        AlertDialog.Builder builder = new AlertDialog.Builder(this);
        builder.setTitle("是否"+button+"该书？");
        builder.setMessage("已下架的图书将不会推送到首页的二手书列表，请谨慎操作。");
        builder.setPositiveButton(button, new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialog, int which) {
                book_list book = new book_list();
                book.setB_state(!state);
                book.update(SendListActivity.this, id, new UpdateListener() {
                    @Override
                    public void onSuccess() {
                        init();
                        Toast.makeText(SendListActivity.this, "操作成功", Toast.LENGTH_SHORT).show();
                        progressDialog.dismiss();
                    }

                    @Override
                    public void onFailure(int i, String s) {
                        Toast.makeText(SendListActivity.this, "操作失败"+s, Toast.LENGTH_SHORT).show();
                        progressDialog.dismiss();
                    }
                });
            }
        });
        builder.setNegativeButton("取消",null);
        AlertDialog dialog = builder.create();
        dialog.show();
    }

}
