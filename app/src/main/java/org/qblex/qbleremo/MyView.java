package org.qblex.qbleremo;

import android.content.Context;
import android.util.AttributeSet;
import android.util.Log;
import android.view.MotionEvent;
import android.view.View;

/**
 * Created by VIT_HOME on 2016-04-18.
 */
public class MyView extends View {

    public MyView(Context context) {
        super(context);
    }

    public MyView(Context context, AttributeSet attrs) {
        super(context, attrs);
    }

    @Override
    public boolean onTouchEvent(MotionEvent event) {
        if (event.getAction() == MotionEvent.ACTION_DOWN) {
            Log.d("MyView", "눌림");
        } else if (event.getAction() == MotionEvent.ACTION_UP) {
            Log.d("MyView", "눌림");
        } else if (event.getAction() == MotionEvent.ACTION_MOVE) {
            Log.d("MyView", "움직임");
        }

        return true;

    }

}
