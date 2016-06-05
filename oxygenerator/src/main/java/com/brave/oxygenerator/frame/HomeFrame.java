package com.brave.oxygenerator.frame;

import android.app.Fragment;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import com.brave.oxygenerator.R;

/**
 * Created by Brave on 2016/10/7.
 */
public class HomeFrame extends Fragment {

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {

        return inflater.inflate(R.layout.frame_home,container,false);
    }
}
