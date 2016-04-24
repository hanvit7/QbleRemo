package org.qblex.qbleremo;

import android.os.Bundle;
import android.support.v7.app.ActionBarActivity;
import android.view.View;
import android.view.animation.Animation;
import android.view.animation.AnimationUtils;
import android.webkit.WebSettings;
import android.webkit.WebView;
import android.widget.LinearLayout;
import android.widget.ProgressBar;
import android.widget.TextView;

public class MenuActivity extends ActionBarActivity {

    WebView webView;
    TextView textView;
    LinearLayout sliding;
    Animation animationLeft;
    Animation animationRight;
    ProgressBar progressBar;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_menu);

        webView = (WebView) findViewById(R.id.webView);
        WebSettings settings = webView.getSettings();
        settings.setJavaScriptEnabled(true);

        textView = (TextView) findViewById(R.id.textView);

        sliding = (LinearLayout) findViewById(R.id.sliding);

        animationLeft = AnimationUtils.loadAnimation(this, R.anim.translate_left);
        animationRight = AnimationUtils.loadAnimation(this, R.anim.translate_right);

        progressBar = (ProgressBar) findViewById(R.id.progressBar);
//        animationLeft.setAnimationListener(new Animation.AnimationListener() {
//            @Override
//            public void onAnimationStart(Animation animation) {
//
//            }
//
//            @Override
//            public void onAnimationEnd(Animation animation) {
//
//            }
//
//            @Override
//            public void onAnimationRepeat(Animation animation) {
//
//            }
//        });

    }

    public void onButton5Clicked(View v) {
//        Animation traslate = AnimationUtils.loadAnimation(this, R.anim.translate);
//        textView.startAnimation(traslate);
        webView.loadUrl("http://qblex.modoo.at");
        sliding.setVisibility(View.VISIBLE);
        sliding.startAnimation(animationLeft);


        progressBar.setProgress(50);

    }

}
