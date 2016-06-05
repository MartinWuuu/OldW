package com.brave.bookshop.data;

import android.content.Context;
import android.widget.Toast;

import com.brave.bookshop.database.book_list;
import com.brave.bookshop.database.collection;
import com.brave.bookshop.view.BookListItem;

import java.lang.reflect.Array;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

import cn.bmob.v3.BmobQuery;
import cn.bmob.v3.listener.FindListener;

/**
 * Created by Brave on 2016/5/19.
 */
public class BookData {
    public static final String[] mBookClassification = new String[]{"计算机","高数","英语","考研","其他"};
    public static final String[] mBookQuality = new String[]{"全新","9成新","略旧","其他"};



    public static List<BookListItem> getBookList(final Context c){
        final List<BookListItem> mList = new ArrayList<>();
        BmobQuery<book_list> query = new BmobQuery<>();
        query.addWhereEqualTo("b_state",true);
        query.findObjects(c, new FindListener<book_list>() {
            @Override
            public void onSuccess(List<book_list> list) {
                for (int i = list.size()-1; i >= 0; i--){
                    BookListItem item = new BookListItem(c,new String[]{"",list.get(i).getB_name(),list.get(i).getB_details(),list.get(i).getB_price().toString()});
                    item.setBook(list.get(i));
                    mList.add(item);
                }
            }

            @Override
            public void onError(int i, String s) {
                Toast.makeText(c, i+":"+s, Toast.LENGTH_SHORT).show();
            }
        });

        return mList;

    }

    public static List<BookListItem> getSendBookList(final Context c){
        final List<BookListItem> mList = new ArrayList<>();
        BmobQuery<book_list> query = new BmobQuery<>();
        String uid = UserData.getEmail(c);
        query.addWhereEqualTo("b_uid",uid);
        query.findObjects(c, new FindListener<book_list>() {
            @Override
            public void onSuccess(List<book_list> list) {
                if(list.size() != 0){
                    for (int i = list.size() - 1; i >= 0; i--){
                        String state;
                        if (list.get(i).getB_state()){
                            state = "上架中";
                        }else{
                            state = "已下架";
                        }
                        BookListItem item = new BookListItem(c,new String[]{"",list.get(i).getB_name(),state,list.get(i).getB_price().toString()});
                        item.setBook(list.get(i));
                        mList.add(item);
                    }
                }else{
                    BookListItem item = new BookListItem(c,new String[]{"","***","***","***"});
                    mList.add(item);
                }
            }

            @Override
            public void onError(int i, String s) {
                Toast.makeText(c, "发生错误"+s, Toast.LENGTH_SHORT).show();
            }
        });
        return mList;

    }

    public static List<BookListItem> getCollectionBookList(final Context c,String[] params){
        final List<BookListItem> mList;
        if(params.length == 0){
            mList = getCollection(c);
        }else if (params.length == 1){
            mList = getBookListByName(c,params[0]);
        }else{
            mList = getBookListBySort(c,params);
        }
        return mList;
    }

    private static List<BookListItem> getCollection(final Context c){
        final List<BookListItem> mList = new ArrayList<>();
        BmobQuery<collection> query = new BmobQuery<>();
        String uid = UserData.getEmail(c);
        query.addWhereEqualTo("c_uid",uid);
        query.findObjects(c, new FindListener<collection>() {
            @Override
            public void onSuccess(List<collection> list) {
                if(list.size() == 0){
                    Toast.makeText(c, "收藏列表为空", Toast.LENGTH_SHORT).show();
                }
                String[] bid = new String[list.size()];
                for (int i = 0; i < list.size();i++){
                    bid[i] = list.get(i).getC_bid();
                }
                BmobQuery<book_list> query1 = new BmobQuery<>();
                query1.addWhereContainedIn("objectId", Arrays.asList(bid));
                query1.findObjects(c, new FindListener<book_list>() {
                    @Override
                    public void onSuccess(List<book_list> list) {
                        for (int i = list.size()-1; i >= 0; i--){
                            BookListItem item = new BookListItem(c,new String[]{"",list.get(i).getB_name(),list.get(i).getB_details(),list.get(i).getB_price().toString()});
                            item.setBook(list.get(i));
                            mList.add(item);
                        }
                    }

                    @Override
                    public void onError(int i, String s) {
                        Toast.makeText(c, "发生错误"+s, Toast.LENGTH_SHORT).show();
                    }
                });
            }

            @Override
            public void onError(int i, String s) {
                Toast.makeText(c, "发生错误"+s, Toast.LENGTH_SHORT).show();
            }
        });
        return mList;
    }

    private static List<BookListItem> getBookListByName(final Context c,final String name){
        final List<BookListItem> mList = new ArrayList<>();
        BmobQuery<book_list> query = new BmobQuery<>();
        query.addWhereContains("b_name",name);
        query.findObjects(c, new FindListener<book_list>() {
            @Override
            public void onSuccess(List<book_list> list) {
                if(list.size() == 0){
                    Toast.makeText(c, "没有找到书名中包含”"+name+"“的二手书", Toast.LENGTH_SHORT).show();
                }
                for (int i = list.size()-1; i >= 0; i--){
                    BookListItem item = new BookListItem(c,new String[]{"",list.get(i).getB_name(),list.get(i).getB_details(),list.get(i).getB_price().toString()});
                    item.setBook(list.get(i));
                    mList.add(item);
                }
            }

            @Override
            public void onError(int i, String s) {
                Toast.makeText(c, "发生错误"+s, Toast.LENGTH_SHORT).show();
            }
        });
        return mList;
    }

    private static List<BookListItem> getBookListBySort(final Context c,String[] params){
        final List<BookListItem> mList = new ArrayList<>();

        BmobQuery<book_list> q1 = new BmobQuery<>();
        q1.addWhereLessThanOrEqualTo("b_price",Integer.parseInt(params[1]));

        BmobQuery<book_list> q2 = new BmobQuery<>();
        q2.addWhereGreaterThanOrEqualTo("b_price",Integer.parseInt(params[0]));

        BmobQuery<book_list> q3 = new BmobQuery<>();

        BmobQuery<book_list> q4 = new BmobQuery<>();

        List<BmobQuery<book_list>> qs = new ArrayList<>();
        qs.add(q1);
        qs.add(q2);

        BmobQuery<book_list> query = new BmobQuery<>();
        query.and(qs);
        query.findObjects(c, new FindListener<book_list>() {
            @Override
            public void onSuccess(List<book_list> list) {
                if(list.size() == 0){
                    Toast.makeText(c, "没有找到符合条件的二手书", Toast.LENGTH_SHORT).show();
                }
                for (int i = list.size()-1; i >= 0; i--){
                    BookListItem item = new BookListItem(c,new String[]{"",list.get(i).getB_name(),list.get(i).getB_details(),list.get(i).getB_price().toString()});
                    item.setBook(list.get(i));
                    mList.add(item);
                }
            }

            @Override
            public void onError(int i, String s) {
                Toast.makeText(c, "发生错误"+s, Toast.LENGTH_SHORT).show();
            }
        });


        return mList;
    }

}
