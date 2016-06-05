package com.brave.oldwatch.view;

import android.content.Context;
import android.view.LayoutInflater;
import android.widget.LinearLayout;

import com.brave.oldwatch.R;

/**
 * Created by Brave on 2016/10/31.
 */

public class ChatLeftItem extends LinearLayout {
    public ChatLeftItem(Context context) {
        super(context);
        LayoutInflater.from(context).inflate(R.layout.view_chat_item_left,this);
    }
}
