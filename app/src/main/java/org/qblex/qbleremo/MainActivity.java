package org.qblex.qbleremo;

import android.app.AlertDialog;
import android.app.ProgressDialog;
import android.content.DialogInterface;
import android.content.res.Configuration;
import android.os.Bundle;
import android.os.Handler;
import android.os.Message;
import android.support.design.widget.FloatingActionButton;
import android.support.design.widget.Snackbar;
import android.support.v4.widget.DrawerLayout;
import android.support.v7.app.ActionBar;
import android.support.v7.app.ActionBarDrawerToggle;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.util.Log;
import android.view.Menu;
import android.view.MenuItem;
import android.view.MotionEvent;
import android.view.SoundEffectConstants;
import android.view.View;
import android.view.animation.Animation;
import android.view.animation.AnimationUtils;
import android.widget.Button;
import android.widget.ImageSwitcher;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.ViewSwitcher;

import static org.qblex.qbleremo.R.layout.activity_main;

public class MainActivity extends AppCompatActivity {
    TextView textView;
    Button button1;
    Animation buttonOn1;
    Animation buttonOff1;
    Button button2;
    Animation buttonOn2;
    Animation buttonOff2;
    Button button3;
    Animation buttonOn3;
    Animation buttonOff3;
    Button button4;
    Animation buttonOn4;
    Animation buttonOff4;

    MySocket socket = null;
    Handler mHandler;

    AliveThread aliveThread;
    ProgressDialog dialog;
    AlertDialog.Builder builder;

    ImageSwitcher imageSwitcher;
    ImageSwitcher imageSwitcher2;

    DrawerLayout dlDrawer;
    ActionBarDrawerToggle dtToggle;

    boolean initState = false;

    int[] imageArray = {R.drawable.ic_cloud_off_white_48dp,
            R.drawable.ic_cloud_queue_white_48dp,
            R.drawable.ic_view_column_white_48dp,
            R.drawable.ic_view_stream_white_48dp
    };

    int[] imageNum = {R.drawable.ic_filter_none_white_48dp,
            R.drawable.ic_filter_1_white_48dp,
            R.drawable.ic_filter_2_white_48dp,
            R.drawable.ic_filter_3_white_48dp,
            R.drawable.ic_filter_4_white_48dp,
            R.drawable.ic_filter_5_white_48dp,
            R.drawable.ic_filter_6_white_48dp,
            R.drawable.ic_filter_7_white_48dp,
            R.drawable.ic_filter_8_white_48dp,
            R.drawable.ic_filter_9_white_48dp,
            R.drawable.ic_filter_9_plus_white_48dp
    };


    //    RelativeLayout container;
    Handler buttonHandler;

    String btag = "MButton";

    int aliveCnt = 0;
    int threadCnt = 0;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(activity_main);
        Toolbar toolbar = (Toolbar) findViewById(R.id.toolBar);
        setSupportActionBar(toolbar);

        FloatingActionButton fab = (FloatingActionButton) findViewById(R.id.fab);
        fab.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Snackbar.make(view, "Replace with your own action", Snackbar.LENGTH_LONG)
                        .setAction("Action", null).show();
            }
        });

        dlDrawer = (DrawerLayout) findViewById(R.id.drawer_layout);
        dlDrawer.setDrawerListener(dtToggle);
        dtToggle = new ActionBarDrawerToggle(this, dlDrawer, R.string.app_name, R.string.app_name);

        ActionBar ab = getSupportActionBar();
        if (null != ab) {
            ab.setDisplayHomeAsUpEnabled(true);
        }

        textView = (TextView) findViewById(R.id.textView);
//        container = (RelativeLayout) findViewById(R.id.container);



        buttonHandler = new Handler();
        onButtonClicked1();
        onButtonClicked2();
        onButtonClicked3();
        onButtonClicked4();
        imageSwitcherInit();



        imageSwitcher.setImageResource(imageArray[0]);
        imageSwitcher.invalidate();
        imageSwitcher2.setImageResource(imageNum[0]);
        imageSwitcher2.invalidate();

        failDialogInit();
        massageHanlder();

        if (socket == null) soketStart();
    }

    int num = 0;

    private void commandHandle(String cmd) {
        int buttonNum = 0;
        if (cmd.contains("=")) {
            String[] split = cmd.split("=");
            if (split[0].contains("alive")) {//cmd alive
                if (split[1].equals("?")) {
                    aliveThread.alive = 10;
                    socket.sendString("#alive" + aliveCnt++);
                    Log.d("MS", "cmd, " + aliveCnt + "#alive");
                }
            } else if (split[0].contains("pin")) {//cmd pin
                String[] value = split[1].split(",");
                Log.d("MS1", "cmd, pin : " + cmd);

                buttonNum = Integer.parseInt(value[0]);
                pinFlag[buttonNum] = Integer.parseInt(value[1]);
                switch (buttonNum) {
                    case 1:
                        if (pinFlag[buttonNum] == 1) button1.startAnimation(buttonOn1);
                        else button1.startAnimation(buttonOff1);
                        if (initState) button1.playSoundEffect(SoundEffectConstants.CLICK);
                        break;

                    case 2:
                        if (pinFlag[buttonNum] == 1) button2.startAnimation(buttonOn2);
                        else button2.startAnimation(buttonOff2);
                        if (initState) button2.playSoundEffect(SoundEffectConstants.CLICK);
                        break;

                    case 3:
                        if (pinFlag[buttonNum] == 1) button3.startAnimation(buttonOn3);
                        else button3.startAnimation(buttonOff3);
                        if (initState) button3.playSoundEffect(SoundEffectConstants.CLICK);
                        break;

                    case 4:
                        if (pinFlag[buttonNum] == 1) button4.startAnimation(buttonOn4);
                        else button4.startAnimation(buttonOff4);
                        if (initState) button4.playSoundEffect(SoundEffectConstants.CLICK);
                        break;

                    default:
                        break;
                }
            } else if (split[0].contains("num")) {//cmd num
                Log.d("MS1", "cmd, num : " + cmd);
                imageSwitcher2.setImageResource(imageNum[Integer.parseInt(split[1])]);
                imageSwitcher2.invalidate();
            } else {// cmd
                Log.d("MS1", "cmd, else : " + cmd);
            }
        } else {
            if (cmd.equals("connect")) {// cmd connect
                Log.d("MS1", "cmd, connect : " + cmd);
                if (dialog != null) dialog.dismiss();

                if (aliveThread == null) {
                    aliveThread = new AliveThread();
                    aliveThread.start();
                    aliveThread.alive = 10;

                    Log.d("MS1", "start aliveThread " + threadCnt++);
                }
                aliveCnt = 0;
            } else if (cmd.equals("init")) {//cmd init
                Log.d("MS1", "cmd, init : " + cmd);
                initState = true;
            } else {// cmd
                Log.d("MS1", "cmd, else : " + cmd);
            }
        }
    }


    private void imageSwitcherInit() {
        imageSwitcher = (ImageSwitcher) findViewById(R.id.imageSwitcher);
        imageSwitcher.setFactory(new ViewSwitcher.ViewFactory() {
            @Override
            public View makeView() {
                ImageView imageView = new ImageView(getApplicationContext());
                return imageView;
            }
        });

        imageSwitcher2 = (ImageSwitcher) findViewById(R.id.imageSwitcher2);
        imageSwitcher2.setFactory(new ViewSwitcher.ViewFactory() {
            @Override
            public View makeView() {
                ImageView imageView = new ImageView(getApplicationContext());
                return imageView;
            }
        });

        Animation in = AnimationUtils.loadAnimation(this, android.R.anim.slide_in_left);
        Animation out = AnimationUtils.loadAnimation(this, android.R.anim.slide_out_right);

        imageSwitcher.setInAnimation(in);
        imageSwitcher.setOutAnimation(out);
        imageSwitcher2.setInAnimation(in);
        imageSwitcher2.setOutAnimation(out);
    }

    private void massageHanlder() {
        mHandler = new Handler(getMainLooper()) {
            @Override
            public void handleMessage(Message inputMessage) {
                String msg = (String) inputMessage.obj;

                switch (inputMessage.what) {
                    case 0://Connected
                        if (dialog != null) dialog.dismiss();
                        imageSwitcher.setImageResource(imageArray[1]);
                        imageSwitcher.invalidate();
                        break;

                    case 1://data
                        if (msg.startsWith("#")) {
                            commandHandle(msg.substring(1));
                        }
                        textView.setText(msg);
                        break;

                    case 2://Disonnected
                        imageSwitcher.setImageResource(imageArray[0]);
                        imageSwitcher.invalidate();
                        imageSwitcher2.setImageResource(imageNum[0]);
                        imageSwitcher2.invalidate();
                        initState = false;
                        break;

                    case 3://Connect Fail
                        if (Integer.parseInt(msg) <= 10) {
                            dialog.setMessage("Try to Connect : " + msg + "sec");
                            dialog.show();
                        } else {
                            socket.setRuntFlag(false);
                            if (dialog != null) dialog.dismiss();
                            failDialogStart();
                        }
                        break;
                }
            }
        };

    }

    int[] pinFlag = new int[5];

    public void onButtonClicked1() {
        button1 = (Button) findViewById(R.id.button1);
        buttonOn1 = AnimationUtils.loadAnimation(getApplicationContext(), R.anim.buttonon1);
        buttonOff1 = AnimationUtils.loadAnimation(getApplicationContext(), R.anim.buttonoff1);
        button1.setOnTouchListener(new View.OnTouchListener() {
            @Override
            public boolean onTouch(View v, MotionEvent event) {
                int action = event.getAction();

                if (action == MotionEvent.ACTION_DOWN) {
                    return true;
                } else if (action == MotionEvent.ACTION_UP) {
                    //button1.playSoundEffect(SoundEffectConstants.CLICK);
                    try {
                        if (pinFlag[1] == 1) socket.sendString("#pin=1,0");
                        else socket.sendString("#pin=1,1");
                        Log.d("MB", "button[1]" + pinFlag[1]);
                    } catch (Exception e) {
                        e.printStackTrace();
                    }
                    return true;
                } else {
                    return false;
                }
            }
        });
    }

    public void onButtonClicked2() {
        button2 = (Button) findViewById(R.id.button2);
        buttonOn2 = AnimationUtils.loadAnimation(getApplicationContext(), R.anim.buttonon2);
        buttonOff2 = AnimationUtils.loadAnimation(getApplicationContext(), R.anim.buttonoff2);
        button2.setOnTouchListener(new View.OnTouchListener() {
            @Override
            public boolean onTouch(View v, MotionEvent event) {
                int action = event.getAction();

                if (action == MotionEvent.ACTION_DOWN) {
                    return true;
                } else if (action == MotionEvent.ACTION_UP) {
//                    button2.playSoundEffect(SoundEffectConstants.CLICK);
                    try {
                        if (pinFlag[2] == 1) socket.sendString("#pin=2,0");
                        else socket.sendString("#pin=2,1");
                        Log.d("MB", "button[2]" + pinFlag[2]);
                    } catch (Exception e) {
                        e.printStackTrace();
                    }
                    return true;
                } else {
                    return false;
                }
            }
        });
    }

    public void onButtonClicked3() {
        button3 = (Button) findViewById(R.id.button3);
        buttonOn3 = AnimationUtils.loadAnimation(getApplicationContext(), R.anim.buttonon3);
        buttonOff3 = AnimationUtils.loadAnimation(getApplicationContext(), R.anim.buttonoff3);
        button3.setOnTouchListener(new View.OnTouchListener() {
            @Override
            public boolean onTouch(View v, MotionEvent event) {
                int action = event.getAction();

                if (action == MotionEvent.ACTION_DOWN) {
                    return true;
                } else if (action == MotionEvent.ACTION_UP) {
//                    button3.playSoundEffect(SoundEffectConstants.CLICK);
                    try {
                        if (pinFlag[3] == 1) socket.sendString("#pin=3,0");
                        else socket.sendString("#pin=3,1");
                        Log.d("MB", "button[3]" + pinFlag[3]);
                    } catch (Exception e) {
                        e.printStackTrace();
                    }
                    return true;
                } else {
                    return false;
                }
            }
        });
    }

    public void onButtonClicked4() {
        button4 = (Button) findViewById(R.id.button4);
        buttonOn4 = AnimationUtils.loadAnimation(getApplicationContext(), R.anim.buttonon4);
        buttonOff4 = AnimationUtils.loadAnimation(getApplicationContext(), R.anim.buttonoff4);
        button4.setOnTouchListener(new View.OnTouchListener() {
            @Override
            public boolean onTouch(View v, MotionEvent event) {
                int action = event.getAction();

                if (action == MotionEvent.ACTION_DOWN) {
                    return true;
                } else if (action == MotionEvent.ACTION_UP) {
//                    button4.playSoundEffect(SoundEffectConstants.CLICK);
                    try {
                        if (pinFlag[4] == 1) socket.sendString("#pin=4,0");
                        else socket.sendString("#pin=4,1");
                        Log.d("MB", "button[4]" + pinFlag[4]);
                    } catch (Exception e) {
                        e.printStackTrace();
                    }
                    return true;
                } else {
                    return false;
                }
            }
        });
    }

    private void failDialogInit() {
        dialog = new ProgressDialog(this);
        dialog.setProgress(ProgressDialog.STYLE_SPINNER);
        dialog.setTitle("Connect to Qble Smart Film");

        builder = new AlertDialog.Builder(this);
        builder.setTitle("Connect Fail");
        builder.setMessage("Would you like to check the settings or WiFi?");
        builder.setPositiveButton("No", new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialog, int which) {
                Log.d("MS1", "setPositiveButton");
                //세팅화면열기
            }
        });
        builder.setNegativeButton("Settings", new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialog, int which) {
                Log.d("MS1", "setNegativeButton");
            }
        });
        builder.setNeutralButton("WiFi", new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialog, int which) {

                Log.d("MS1", "setNeutralButton");
            }
        });
    }

    private void failDialogStart() {
        AlertDialog failDialog = builder.create();
        failDialog.show();
    }

    class AliveThread extends Thread {
        public int alive = 10;

        public void run() {
            while (true) {
                if (socket.isConnected()) {
                    if (aliveThread.alive > 0) aliveThread.alive--;
                    else {
                        socket.sendString("#disconnect");
                        socket.socketClose();
                        Log.d("MS", "send : #disconnect");
                    }
                    try {
                        Thread.sleep(5000);
                    } catch (Exception e) {
                        e.printStackTrace();
                    }
                }
            }
        }
    }

    protected void soketStart() {
        socket = new MySocket("192.168.0.1", 80, mHandler);
        socket.setRuntFlag(true);
        socket.start();
    }

    @Override
    protected void onRestart() {
        super.onRestart();
        if (!socket.isConnected()) soketStart();
    }

    @Override
    protected void onStop() {
        super.onStop();
        if (socket.isConnected()) {
            socket.sendString("#disconnect");
            socket.socketClose();
        }
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        // Inflate the menu; this adds items to the action bar if it is present.
        getMenuInflater().inflate(R.menu.menu_main1, menu);
        return true;
    }

    @Override
    protected void onPostCreate(Bundle savedInstanceState) {
        super.onPostCreate(savedInstanceState);

        // Sync the toggle state after onRestoreInstanceState has occurred.
        dtToggle.syncState();
    }

    @Override
    public void onConfigurationChanged(Configuration newConfig) {
        super.onConfigurationChanged(newConfig);
        dtToggle.onConfigurationChanged(newConfig);
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        // Handle action bar item clicks here. The action bar will
        // automatically handle clicks on the Home/Up button, so long
        // as you specify a parent activity in AndroidManifest.xml.
//        int id = item.getItemId();

        if (dtToggle.onOptionsItemSelected(item)) {
            return true;
        }

        //noinspection SimplifiableIfStatement
//        if (id == R.id.action_settings) {
//            Intent intent = new Intent(getApplicationContext(), MenuActivity.class);
//            startActivityForResult(intent, 1001);

//        Animation animation = AnimationUtils.loadAnimation(this, R.anim.translate);
//        ViewGroup container = (ViewGroup) findViewById(R.id.container);
//        container.startAnimation(animation);

//            textView.startAnimation(animation);
//            return true;
//        } else if (id == R.id.action_link) if (!socket.isConnected()) soketStart();

        return super.onOptionsItemSelected(item);
    }
}
