package com.brave.bookshop;

import android.app.ProgressDialog;
import android.content.Intent;
import android.os.Handler;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ListView;

import com.brave.bookshop.adapter.BookListAdapter;
import com.brave.bookshop.data.BookData;
import com.brave.bookshop.database.book_list;
import com.brave.bookshop.view.BookListItem;

import java.util.List;

public class CollectionActivity extends AppCompatActivity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_collection);

        int flag = getIntent().getIntExtra("flag",0);
        if(flag == 0){
            setTitle("收藏列表");
            getBookList();
        }else if(flag == 1){
            setTitle("书名搜索结果");
            String s = getIntent().getStringExtra("params");
            String[] params = new String[]{s};
            getBookList(params);
        }else if(flag == 2){
            setTitle("条件筛选结果");
            String[] params = getIntent().getStringArrayExtra("params");
            getBookList(params);
        }

    }
    private void getBookList(String[] params){
        final ProgressDialog progressDialog = ProgressDialog.show(this,"请稍后","正在从服务器中获取数据");
        progressDialog.show();
        final ListView list = (ListView)findViewById(R.id.collection_list);
        final List<BookListItem> data = BookData.getCollectionBookList(this,params);
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
                        i.setClass(CollectionActivity.this,DetailsActivity.class);
                        startActivity(i);
                    }
                });
                progressDialog.dismiss();
            }
        },3000);
    }

    private void getBookList(){
       getBookList(new String[]{});
    }

}
