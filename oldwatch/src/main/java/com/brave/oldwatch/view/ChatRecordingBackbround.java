package com.brave.oldwatch.view;

import android.content.Context;
import android.graphics.Canvas;
import android.graphics.Paint;
import android.util.AttributeSet;
import android.view.View;

import com.brave.oldwatch.R;

/**
 * Created by Brave on 2016/10/31.
 */

public class ChatRecordingBackbround extends View {

    private int mRadius = 0;

    public ChatRecordingBackbround(Context context, AttributeSet attrs) {
        super(context, attrs);
    }

    public void recording(){

        new Thread(){
            @Override
            public void run() {
                super.run();
                int i = 0;
                int radius = getWidth()/3;
                while (i <= radius){
                    mRadius = i;
                    i = i+50;
                    postInvalidate();
                    try {
                        sleep(10);
                    } catch (InterruptedException e) {
                        e.printStackTrace();
                    }
                }
            }
        }.start();
    }

    public void recoeded(){
        mRadius = 0;
        invalidate();
    }

    @Override
    protected void onDraw(Canvas canvas) {
        super.onDraw(canvas);
        Paint paint = new Paint(Paint.ANTI_ALIAS_FLAG);
        paint.setColor(getResources().getColor(R.color.recording));
        paint.setAlpha(70);
        canvas.drawCircle(getWidth()/2,getHeight()/2,mRadius,paint);

    }
}
