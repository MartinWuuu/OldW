package com.brave.bookshop.view;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;

import com.brave.bookshop.R;
import com.brave.bookshop.database.book_list;

public class BookListItem extends LinearLayout {

    private View mRootView;
    private ImageView mImgView;
    private TextView mTitleView,mDetailsView,mPriceView;
    private String[] mParams;
    private book_list book;

    public book_list getBook() {
        return book;
    }

    public void setBook(book_list book) {
        this.book = book;
    }


    public BookListItem(Context context,String[] params) {
        super(context);
        mParams = params;
        mRootView = LayoutInflater.from(context).inflate(R.layout.view_book_list_item,this);

        mImgView = (ImageView) mRootView.findViewById(R.id.view_book_list_item_img);
        mTitleView = (TextView) mRootView.findViewById(R.id.view_book_list_item_title);
        mDetailsView = (TextView) mRootView.findViewById(R.id.view_book_list_item_details);
        mPriceView = (TextView) mRootView.findViewById(R.id.view_book_list_item_price);

//        mImgView.setImageBitmap(BitmapFactory.decodeFile(params[0]));
        mImgView.setImageResource(R.mipmap.ic_book_1);
        mTitleView.setText(params[1]);
        mDetailsView.setText(params[2]);
        mPriceView.setText(params[3]+"元");
    }

    public String[] getmParams() {
        return mParams;
    }






}
