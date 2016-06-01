package com.example.linh.musicplayer;

import android.os.Looper;

/**
 * Created by linh on 5/20/2016.
 */
public class DownloadThread extends Thread {
    DownloadHander mDowloadHander ;

    @Override
    public void run() {
        Looper.prepare();
        mDowloadHander = new  DownloadHander();
        Looper.loop();
    }
}
