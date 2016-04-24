package org.qblex.qbleremo;

import android.os.Handler;
import android.os.Message;
import android.util.Log;

import java.io.BufferedReader;
import java.io.BufferedWriter;
import java.io.IOException;
import java.io.InputStreamReader;
import java.io.OutputStreamWriter;
import java.io.PrintWriter;
import java.net.InetAddress;
import java.net.InetSocketAddress;
import java.net.Socket;

public class MySocket extends Thread {
    private Socket mSocket;

    private BufferedReader buffRecv;
    private BufferedWriter buffSend;

    private String mAddr = "192.168.0.1";
    private int mPort = 80;
    private boolean mConnected = false;
    private Handler mHandler = null;


    public boolean runtFlag = true;
    public final int tryTime = 10;

    static class MessageTypeClass {
        public static final int SOCK_CONNECTED = 1;
        public static final int SOCK_DATA = 2;
        public static final int SOCK_DISCONNECTED = 3;
    }

    ;

    public enum MessageType {MYSOCK_CONNECTED, MYSOCK_DATA, MYSOCK_DISCONNECTED, MYSOCK_FAIL}

    ;


    public MySocket(String addr, int port, Handler handler) {
        mAddr = addr;
        mPort = port;
        mHandler = handler;
    }

    private void makeMessage(MessageType what, Object obj) {
        Message msg = Message.obtain();
        msg.what = what.ordinal();
        msg.obj = obj;
        mHandler.sendMessage(msg);
    }

    private boolean connect(String addr, int port) {
        try {
            InetSocketAddress socketAddress = new InetSocketAddress(InetAddress.getByName(addr), port);
            mSocket = new Socket();
            mSocket.connect(socketAddress, port);
        } catch (IOException e) {
            System.out.println(e);
            e.printStackTrace();
            return false;
        }
        return true;
    }

    public boolean isRuntFlag() {
        return runtFlag;
    }

    public void setRuntFlag(boolean runtFlag) {
        this.runtFlag = runtFlag;
    }


    @Override
    public void run() {
        int connectTry = 0;
        while (!connect(mAddr, mPort)) {
            String string = null;
            makeMessage(MessageType.MYSOCK_FAIL, string.valueOf(++connectTry));
            Log.d("MySocket", "connectTry : " + connectTry);
            try {
                Thread.sleep(1000);
            } catch (Exception e) {
                e.printStackTrace();
            }
            while (!runtFlag) return;
        }
        Log.d("MS1", "1");
        if (mSocket == null) return;
        Log.d("MS1", "2");

        try {
            buffRecv = new BufferedReader(new InputStreamReader(mSocket.getInputStream()));
            buffSend = new BufferedWriter(new OutputStreamWriter(mSocket.getOutputStream()));
        } catch (IOException e) {
            e.printStackTrace();
        }

        mConnected = true;
        Log.d("MS1", "Connected");

        makeMessage(MessageType.MYSOCK_CONNECTED, null);

        String aLine = null;
        while (!Thread.interrupted()){
            try {
                aLine = buffRecv.readLine();
                if (aLine != null) makeMessage(MessageType.MYSOCK_DATA, aLine);
                else break;
            } catch (IOException e) {
                e.printStackTrace();
            }
        }

        Log.d("MS1", "Disonnected");
        makeMessage(MessageType.MYSOCK_DISCONNECTED, null);

        socketClose();
    }

    synchronized public boolean isConnected() {
        return mConnected;
    }

    public void sendString(String str) {
        PrintWriter out = new PrintWriter(buffSend, true);
        out.println(str);
    }

    public void socketClose() {
        try {
            buffRecv.close();
            buffSend.close();
//            mSocket.close();
//            mSocket = null;
            Log.d("MS1", "Soket Close");
        } catch (IOException e) {
            e.printStackTrace();
        }
        mConnected = false;
    }
}

