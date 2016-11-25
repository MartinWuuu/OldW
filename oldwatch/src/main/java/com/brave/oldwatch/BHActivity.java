package com.brave.oldwatch;

import android.animation.ValueAnimator;
import android.content.Context;
import android.graphics.Color;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.view.MotionEvent;
import android.view.View;
import android.view.WindowManager;
import android.widget.TextView;

import com.github.mikephil.charting.charts.LineChart;
import com.github.mikephil.charting.components.XAxis;
import com.github.mikephil.charting.components.YAxis;
import com.github.mikephil.charting.data.Entry;
import com.github.mikephil.charting.data.LineData;
import com.github.mikephil.charting.data.LineDataSet;

import java.util.ArrayList;
import java.util.List;

public class BHActivity extends AppCompatActivity  implements View.OnClickListener , View.OnTouchListener {

    private TextView mDayBtn,mMonthBtn;
    private View mPointer;

    private LineChart mChart;

    private float mLastPosition = 0;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_bh);
        initView();
        initData();
    }

    private void initData() {


        List<Entry> entries = new ArrayList<>();
        for (int i = 0; i < 10; i++){
            entries.add(new Entry(i,i+1));
        }

        mChart.setViewPortOffsets(0, 0, 0, 0);

        mChart.setDescription("");

        mChart.setTouchEnabled(true);

        mChart.setDragEnabled(true);
        mChart.setScaleEnabled(true);
        mChart.setDoubleTapToZoomEnabled(true);

        mChart.setPinchZoom(true);

        mChart.setDrawGridBackground(true);

        YAxis y = mChart.getAxisLeft();
        y.setTextColor(Color.BLACK);
        y.setDrawAxisLine(false);
        y.setPosition(YAxis.YAxisLabelPosition.INSIDE_CHART);
        y.setDrawGridLines(true);
        y.setAxisLineColor(Color.BLACK);
        y.setAxisMinValue(0);

        mChart.getAxisRight().setEnabled(false);
        mChart.getLegend().setEnabled(true);
        setData(0);
    }

    /**
     * 载入新数据并刷新图表
     *
     * @param i 0代表 day ，1代表month ，2代表year
     */

    private void setData(int i) {

        ArrayList<Entry> yVals = new ArrayList<Entry>();
        yVals.add(new Entry(1, 22));
        yVals.add(new Entry(2, 38));
        yVals.add(new Entry(5, 20));
        yVals.add(new Entry(6, 22));
        yVals.add(new Entry(7, 24));
        yVals.add(new Entry(8, 25));
        yVals.add(new Entry(9, 26));
        yVals.add(new Entry(10, 27));

        XAxis x = mChart.getXAxis();
        x.setPosition(XAxis.XAxisPosition.BOTTOM_INSIDE);
        x.setTextColor(Color.BLACK);
        x.setDrawAxisLine(false);
        x.setAxisMinValue(0.5f);
        x.setAxisMaxValue(10.5f);
//        switch (i){
//            case 0:
//                x.setAxisMaxValue(10.5f);
//                yVals.add(new Entry(10,29f));
//                yVals.add(new Entry(30,-0.01f));
//                break;
//
//            case 1:
//                x.setAxisMaxValue(10.5f);
//                yVals.add(new Entry(10,28f));
//                yVals.add(new Entry(30,-0.01f));
//                break;
//        }

        LineDataSet set1;

        if (mChart.getData() != null &&
                mChart.getData().getDataSetCount() > 0) {
            set1 = (LineDataSet)mChart.getData().getDataSetByIndex(0);
            set1.setValues(yVals);
            mChart.getData().notifyDataChanged();
            mChart.notifyDataSetChanged();
        } else {
            set1 = new LineDataSet(yVals, "");

            set1.setMode(LineDataSet.Mode.CUBIC_BEZIER);
            set1.setCubicIntensity(0.2f);
            set1.setDrawFilled(false);
            set1.setDrawHighlightIndicators(true);
            set1.setDrawCircles(true);
            set1.setCircleColor(Color.BLUE);
            set1.setLineWidth(1.8f);
            set1.setColor(Color.BLACK);
            set1.setFillColor(Color.BLACK);
            set1.setFillAlpha(50);

            LineData data = new LineData(set1);
            data.setValueTextSize(9f);
            data.setValueTextColor(Color.BLUE);
            data.setDrawValues(true);

            mChart.setData(data);
        }
        mChart.animateXY(100, 800);
        mChart.invalidate();
    }


    private void initView() {
        mPointer = findViewById(R.id.frame_data_pointer);
        mDayBtn = (TextView)findViewById(R.id.frame_data_btn_day);
        mMonthBtn = (TextView)findViewById(R.id.frame_data_btn_month);
        mChart = (LineChart) findViewById(R.id.frame_data_spread_pie_chart);
        mChart.setOnTouchListener(this);
        mDayBtn.setOnClickListener(this);
        mMonthBtn.setOnClickListener(this);

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
        }
    }
    private void move(int i){

        WindowManager wm = (WindowManager) getSystemService(Context.WINDOW_SERVICE);
        int width = wm.getDefaultDisplay().getWidth();
        float start = 0;

        if (i == 1){
            start = 0;
        }else{
            start = width / 2;
        }

        if(i == 4){
            if(mLastPosition != (width / 2)){
                start = mLastPosition + width/2;
            }else{
                return;
            }
        }else if(i == 5){
            if(mLastPosition != 0){
                start = mLastPosition - width/2;
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
        }else{
            setData(1);
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
