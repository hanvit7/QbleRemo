package org.qblex.qbleremo;

import android.content.Context;
import android.os.Handler;
import android.util.AttributeSet;
import android.widget.ImageView;

/**
 * Created by VIT_HOME on 2016-04-20.
 */
public class AnimationView extends ImageView {
    int[] imageArray = {R.drawable.emoticon1, R.drawable.emoticon2, R.drawable.emoticon3, R.drawable.emoticon4};

    Handler handler = new Handler();

    public AnimationView(Context context) {
        super(context);

        init(context);
    }

    public AnimationView(Context context, AttributeSet attrs) {
        super(context, attrs);

        init(context);
    }

    private void init(Context context) {
        ImageThread thread = new ImageThread();
        thread.start();
    }

    private class ImageThread extends Thread {
        boolean running = false;
        int index = 0;

        public void run() {
            running = true;

            while (running) {
                handler.post(new Runnable() {
                    @Override
                    public void run() {
                        setImageResource(imageArray[index]);
                        invalidate();
                    }
                });

                try {
                    Thread.sleep(500);
                } catch (Exception e) {
                    e.printStackTrace();
                }

                index++;
                if (index > 3) {
                    index = 0;
                }

            }
        }
    }
}
