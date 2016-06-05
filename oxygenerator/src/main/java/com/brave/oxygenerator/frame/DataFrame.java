package com.brave.oxygenerator.frame;

import android.animation.ObjectAnimator;
import android.animation.ValueAnimator;
import android.app.Fragment;
import android.content.Context;
import android.graphics.Color;
import android.os.Bundle;
import android.support.v4.view.ViewPager;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.MotionEvent;
import android.view.View;
import android.view.ViewGroup;
import android.view.WindowManager;
import android.widget.TextView;
import android.widget.Toast;

import com.brave.oxygenerator.R;
import com.github.mikephil.charting.charts.LineChart;
import com.github.mikephil.charting.components.XAxis;
import com.github.mikephil.charting.components.YAxis;
import com.github.mikephil.charting.data.Entry;
import com.github.mikephil.charting.data.LineData;
import com.github.mikephil.charting.data.LineDataSet;
import com.github.mikephil.charting.interfaces.dataprovider.LineDataProvider;
import com.github.mikephil.charting.interfaces.datasets.ILineDataSet;
import com.github.mikephil.charting.utils.ColorTemplate;

import java.util.ArrayList;
import java.util.List;

/**
 * Created by Brave on 2016/10/7.
 */

public class DataFrame extends Fragment implements View.OnClickListener , View.OnTouchListener{

    private View mView;
    private TextView mDayBtn,mMonthBtn,mYearBtn,mDateText;
    private View mPointer;

    private LineChart mChart;

    private float mLastPosition = 0;

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        mView = inflater.inflate(R.layout.frame_data,container,false);
        initView();
        initData();
        return mView;
    }

    private void initData() {


        List<Entry> entries = new ArrayList<>();
        for (int i = 0; i < 10; i++){
            entries.add(new Entry(i,i+1));
        }

        mChart.setViewPortOffsets(0, 0, 0, 0);

        // no description text
        mChart.setDescription("");

        // enable touch gestures
        mChart.setTouchEnabled(true);

        // enable scaling and dragging
        mChart.setDragEnabled(true);
        mChart.setScaleEnabled(true);
        mChart.setDoubleTapToZoomEnabled(false);

        // if disabled, scaling can be done on x- and y-axis separately
        mChart.setPinchZoom(true);

        mChart.setDrawGridBackground(false);




        YAxis y = mChart.getAxisLeft();
        y.setTextColor(Color.WHITE);
        y.setDrawAxisLine(false);
        y.setPosition(YAxis.YAxisLabelPosition.INSIDE_CHART);
        y.setDrawGridLines(false);
        y.setAxisLineColor(Color.WHITE);
        y.setAxisMinValue(-1);

        mChart.getAxisRight().setEnabled(false);
        mChart.getLegend().setEnabled(false);
        setData(0);
    }

    /**
     * 载入新数据并刷新图表
     *
     * @param i 0代表 day ，1代表month ，2代表year
     */

    private void setData(int i) {

        ArrayList<Entry> yVals = new ArrayList<Entry>();
        yVals.add(new Entry(0, 22));
        yVals.add(new Entry(3, 28));
        yVals.add(new Entry(6, 20));
        yVals.add(new Entry(10, 27));

        XAxis x = mChart.getXAxis();
        x.setPosition(XAxis.XAxisPosition.BOTTOM_INSIDE);
        x.setTextColor(Color.WHITE);
        x.setDrawAxisLine(false);
        x.setAxisMinValue(-1);

        switch (i){
            case 0:
                mDateText.setText("2016-10-11");
                x.setAxisMaxValue(24.5f);
                yVals.add(new Entry(24,26f));
                yVals.add(new Entry(50,-0.01f));
                break;

            case 1:
                mDateText.setText("2016-10");
                x.setAxisMaxValue(30.5f);
                yVals.add(new Entry(30,26f));
                yVals.add(new Entry(50,-0.01f));
                break;

            case 2:
                mDateText.setText("2016年");
                x.setAxisMaxValue(12.5f);
                yVals.add(new Entry(12,26f));
                yVals.add(new Entry(24,-0.01f));
                break;
        }

        LineDataSet set1;

        if (mChart.getData() != null &&
                mChart.getData().getDataSetCount() > 0) {
            set1 = (LineDataSet)mChart.getData().getDataSetByIndex(0);
            set1.setValues(yVals);
            mChart.getData().notifyDataChanged();
            mChart.notifyDataSetChanged();
        } else {
            set1 = new LineDataSet(yVals, "DataSet 1");

            set1.setMode(LineDataSet.Mode.CUBIC_BEZIER);
            set1.setCubicIntensity(0.2f);
            set1.setDrawFilled(true);
            set1.setDrawHighlightIndicators(false);
            set1.setDrawCircles(false);
            set1.setLineWidth(1.8f);
            set1.setColor(Color.WHITE);
            set1.setFillColor(Color.WHITE);
            set1.setFillAlpha(50);

            LineData data = new LineData(set1);
            data.setValueTextSize(9f);
            data.setDrawValues(false);

            mChart.setData(data);
        }
        mChart.animateXY(100, 800);
        mChart.invalidate();
    }


    private void initView() {
        mPointer = mView.findViewById(R.id.frame_data_pointer);
        mDayBtn = (TextView)mView.findViewById(R.id.frame_data_btn_day);
        mMonthBtn = (TextView)mView.findViewById(R.id.frame_data_btn_month);
        mYearBtn = (TextView)mView.findViewById(R.id.frame_data_btn_year);
        mDateText = (TextView)mView.findViewById(R.id.frame_data_date);
        mChart = (LineChart) mView.findViewById(R.id.frame_data_spread_pie_chart);
        mChart.setOnTouchListener(this);
        mDayBtn.setOnClickListener(this);
        mMonthBtn.setOnClickListener(this);
        mYearBtn.setOnClickListener(this);

    }

    @Override
    public void onClick(View v) {
        switch (v.getId()){
            case R.id.frame_data_btn_day:
                move(1);
                break;
            case R.id.frame_data_btn_month:
                move(2);
                break;
            case R.id.frame_data_btn_year:
                move(3);
                break;
        }
    }
    private void move(int i){

        WindowManager wm = (WindowManager) getActivity().getSystemService(Context.WINDOW_SERVICE);
        int width = wm.getDefaultDisplay().getWidth();
        float start = 0;

        if (i == 1){
            start = 0;
        }else if (i == 2){
            start = width / 3;
        }else if (i == 3){
            start = width / 3 * 2;
        }

        if(i == 4){
            if(mLastPosition != (width / 3 * 2)){
                start = mLastPosition + width/3;
            }else{
                return;
            }
        }else if(i == 5){
            if(mLastPosition != 0){
                start = mLastPosition - width/3;
            }else{
                return;
            }
        }

        ValueAnimator anim = ValueAnimator.ofFloat(mLastPosition, start);
        anim.setDuration(100);
        anim.addUpdateListener(new ValueAnimator.AnimatorUpdateListener() {
            @Override
            public void onAnimationUpdate(ValueAnimator animation) {
                float currentValue = (float) animation.getAnimatedValue();
                mPointer.setTranslationX(currentValue);
            }
        });
        anim.start();

        if(start == 0){
            setData(0);
        }else if(start == width / 3){
            setData(1);
        }else if(start == width / 3 * 2){
            setData(2);
        }
        mLastPosition = start;
    }

    private float mStartParams = 0,mMoveParams = 0;

    @Override
    public boolean onTouch(View v, MotionEvent event) {

        if(event.getAction() == MotionEvent.ACTION_DOWN){
            mStartParams = event.getRawX();
        }else if(event.getAction() == MotionEvent.ACTION_MOVE){
            mMoveParams = event.getRawX();
        }else if(event.getAction() == MotionEvent.ACTION_UP){
            if(mStartParams  - mMoveParams > 200){
               move(4);
            }
            if (mMoveParams - mStartParams > 200){
                move(5);
            }
        }

        return true;
    }
}
