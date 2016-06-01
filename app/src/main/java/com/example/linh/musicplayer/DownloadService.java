package com.example.linh.musicplayer;

import android.app.IntentService;
import android.app.Notification;
import android.app.NotificationManager;
import android.content.Intent;
import android.os.Bundle;
import android.os.Environment;
import android.os.ResultReceiver;
import android.support.v7.app.NotificationCompat;


import java.io.BufferedInputStream;
import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.net.URL;
import java.net.URLConnection;


public class DownloadService extends IntentService {
    public static final int UPDATE_PROGRESS = 8344;
    public DownloadService() {
        super("DownloadService");
    }
    public NotificationManager dlnottification;
    public NotificationCompat.Builder builder;
    @Override
    protected void onHandleIntent(Intent intent) {
        String urlToDownload = intent.getStringExtra("URL");
        String songName = intent.getStringExtra("NAME");
        ResultReceiver receiver = (ResultReceiver) intent.getParcelableExtra("receiver");
        dlnottification = (NotificationManager)  getSystemService(NOTIFICATION_SERVICE);
        builder =  new NotificationCompat.Builder(this);
        builder.setContentTitle("Download Music")
                .setContentText("Dowloading")
                .setSmallIcon(R.drawable.ic_menu_camera).setContentInfo("0%");
        builder.setOngoing(true);
        try {
            URL url = new URL(urlToDownload);
            URLConnection connection = url.openConnection();
            connection.connect();
            // this will be useful so that you can show a typical 0-100% progress bar
            int fileLength = connection.getContentLength();

            // download the file
            InputStream input = new BufferedInputStream(connection.getInputStream());
            OutputStream output = new FileOutputStream(new File(Environment.getExternalStorageDirectory() + "/" + "data/" + songName));

            byte data[] = new byte[1024];
            long total = 0;
            int count;
            while ((count = input.read(data)) != -1) {
                total += count;
                // publishing the progress....
                progressChange((int)(total *100)/fileLength);
                output.write(data, 0, count);
            }

            output.flush();
            output.close();
            input.close();
        } catch (IOException e) {
            e.printStackTrace();
        }


    }
private int lastupdate = 0;
    private void progressChange(int progress) {

        if (lastupdate != progress) {
            lastupdate = progress;
            // not.contentView.setProgressBar(R.id.status_progress,
            // 100,Integer.valueOf(progress[0]), false);
            // inform the progress bar of updates in progress
            // nm.notify(42, not);
            if (progress < 100) {
                builder.setProgress(100, Integer.valueOf(progress),
                        false).setContentInfo(progress + "%");
                dlnottification.notify(12, builder.build());
                Intent i = new Intent("com.russian.apps.TabActivity").putExtra("some_msg", progress + "%");
                this.sendBroadcast(i);
            } else {
                builder.setContentText("Download complete")
                        // Removes the progress bar
                        .setProgress(0, 0, false).setOngoing(false).setContentInfo("");
                ;
                dlnottification.notify(12, builder.build());
            }

        }
    }
}