package com.example.linh.musicplayer;

import android.os.Message;
import android.widget.Toast;

import org.greenrobot.eventbus.Subscribe;

/**
 * Created by linh on 6/1/2016.
 */

public class MessageEvent {
    public final String message;

    public MessageEvent(String message)
    {
        this.message = message;
    }
    

}
