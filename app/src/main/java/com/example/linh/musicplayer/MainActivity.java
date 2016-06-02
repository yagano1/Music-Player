package com.example.linh.musicplayer;

import android.content.DialogInterface;
import android.content.Intent;
import android.media.MediaPlayer;
import android.net.Uri;
import android.os.Bundle;
import android.os.Environment;
import android.support.design.widget.FloatingActionButton;
import android.support.v7.app.AlertDialog;
import android.view.LayoutInflater;
import android.view.View;
import android.support.design.widget.NavigationView;
import android.support.v4.view.GravityCompat;
import android.support.v4.widget.DrawerLayout;
import android.support.v7.app.ActionBarDrawerToggle;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.view.Menu;
import android.view.MenuItem;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ListView;
import android.widget.TextView;

import com.google.android.gms.appindexing.Action;
import com.google.android.gms.appindexing.AppIndex;
import com.google.android.gms.common.api.GoogleApiClient;

import org.greenrobot.eventbus.EventBus;
import org.greenrobot.eventbus.Subscribe;

import java.io.File;
import java.io.IOException;

public class MainActivity extends AppCompatActivity
        implements NavigationView.OnNavigationItemSelectedListener {
    private MediaPlayer mediaPlayer;
    private FloatingActionButton fab;
    private Button stopMusic;
    private Button playMusic;
    private Button buttonBack;
    private Button buttonNext;
    private Button buttonAdd;
    private ListView lv;
    private EditText editTextName;
    private EditText editTextLink;
    private String songname;
    private String fileMusicPatch;
    private int songPosistion = 0;
    private int totalsong;
    private TextView textSongPlaying;
    private Button buttonDownloadMusic;
    private Button buttonOpenfile;
        /**
     * ATTENTION: This was auto-generated to implement the App Indexing API.
     * See https://g.co/AppIndexing/AndroidStudio for more information.
     */
        private GoogleApiClient client;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        EventBus.getDefault().register(MainActivity.this);
        fileMusicPatch = Environment.getExternalStorageDirectory() + "/" + "data/"+songname;
        setContentView(R.layout.activity_main);
        Toolbar toolbar = (Toolbar) findViewById(R.id.toolbar);
        textSongPlaying = (TextView) findViewById(R.id.textSongPlaying);
        setSupportActionBar(toolbar);
        getListMusic();
        mediaPlayer = new MediaPlayer();
        songplay();
        stopMusic = (Button) findViewById(R.id.buttonStop);
        stopMusic.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                mediaPlayer.stop();
                mediaPlayer.reset();
                songplay();
            }
        });
        buttonBack = (Button) findViewById(R.id.buttonBack);
        buttonBack.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if(songPosistion > 0)
                {
                    songPosistion = songPosistion -1;
                    songplay();
                }
            }
        });
        buttonAdd = (Button) findViewById(R.id.buttonAdd);
        buttonAdd.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                final LayoutInflater layoutAddmusic = LayoutInflater.from(MainActivity.this);
                final View viewAddMusic = layoutAddmusic.inflate(R.layout.dialog_addmusic, null);
                final View viewDownloadMusic = layoutAddmusic.inflate(R.layout.dialog_downloadlink,null);
                editTextLink = (EditText) viewDownloadMusic.findViewById(R.id.textInputLink);
                editTextName  = (EditText) viewDownloadMusic.findViewById(R.id.textInputName);
                final AlertDialog.Builder alertDialogBuilder = new AlertDialog.Builder(MainActivity.this);
                alertDialogBuilder.setView(viewAddMusic);
                final AlertDialog alertAddMusic = alertDialogBuilder.create();
                alertAddMusic.show();
                buttonDownloadMusic = (Button) viewAddMusic.findViewById(R.id.buttonDownwload);
                buttonDownloadMusic.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View v) {
                        alertAddMusic.cancel();
                        alertDialogBuilder.setView(viewDownloadMusic);
                        alertDialogBuilder.setCancelable(false)
                        .setPositiveButton("OK", new DialogInterface.OnClickListener() {
                            public void onClick(DialogInterface dialog, int id) {
                                Intent intent = new Intent(MainActivity.this, DownloadService.class);
                                intent.putExtra("URL", editTextLink.getText().toString());
                                intent.putExtra("NAME",editTextName.getText().toString());
                                startService(intent);
                            }
                        })
                        .setNegativeButton("Cancel",
                                new DialogInterface.OnClickListener() {
                                    public void onClick(DialogInterface dialog, int id) {
                                        dialog.cancel();
                                    }
                                });
                        final AlertDialog alertDownloadMusic = alertDialogBuilder.create();
                        alertDownloadMusic.show();
                    }
                });
                buttonOpenfile = (Button) viewAddMusic.findViewById(R.id.buttonOpenfile);
            }
        });
        buttonNext = (Button) findViewById(R.id.buttonForward);
        buttonNext.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if(songPosistion < totalsong)
                {
                    songPosistion = songPosistion + 1;
                    songplay();
                }
            }
        });
        playMusic = (Button) findViewById(R.id.buttonPlay);
        playMusic.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (mediaPlayer.isPlaying()) {
                    mediaPlayer.pause();
                } else {
                    mediaPlayer.start();
                }
            }
        });

        lv.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
                songPosistion =  position;
                songname = lv.getItemAtPosition(position).toString();
                songplay();

            }
        });


        DrawerLayout drawer = (DrawerLayout) findViewById(R.id.drawer_layout);
        ActionBarDrawerToggle toggle = new ActionBarDrawerToggle(
                this, drawer, toolbar, R.string.navigation_drawer_open, R.string.navigation_drawer_close);
        drawer.setDrawerListener(toggle);
        toggle.syncState();

        NavigationView navigationView = (NavigationView) findViewById(R.id.nav_view);
        navigationView.setNavigationItemSelectedListener(this);
        // ATTENTION: This was auto-generated to implement the App Indexing API.
        // See https://g.co/AppIndexing/AndroidStudio for more information.
        client = new GoogleApiClient.Builder(this).addApi(AppIndex.API).build();
    }

    @Subscribe
    public void onMessageEvent(MessageEvent event){
        runOnUiThread(new Runnable() {
            @Override
            public void run() {
                getListMusic();
            }
        });
    }


    public void songplay()
    {
        textSongPlaying.setText(songname);
        mediaPlayer.stop();
        songname = lv.getItemAtPosition(songPosistion).toString();
        fileMusicPatch = Environment.getExternalStorageDirectory() + "/" + "data/" + songname;
        mediaPlayer.reset();
        try {
            mediaPlayer.setDataSource(fileMusicPatch);
            mediaPlayer.prepare();
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    private ArrayAdapter<String> abc;
    public  void getListMusic(){

        File dir = new File(Environment.getExternalStorageDirectory() + "/" + "data/");
        File[] filelist = dir.listFiles();
        String[] theNamesOfFiles = new String[filelist.length];
        for (int i = 0; i < theNamesOfFiles.length; i++) {
            theNamesOfFiles[i] = filelist[i].getName();
            totalsong = i;
        }
        lv = (ListView) findViewById(R.id.listMusic);
        lv.invalidateViews();
        abc = new ArrayAdapter<String>(this, android.R.layout.simple_list_item_1, theNamesOfFiles);
        lv.setAdapter(abc);
    }
    @Override
    public void onBackPressed() {
        DrawerLayout drawer = (DrawerLayout) findViewById(R.id.drawer_layout);
        if (drawer.isDrawerOpen(GravityCompat.START)) {
            drawer.closeDrawer(GravityCompat.START);
        } else {
            super.onBackPressed();
        }
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        // Inflate the menu; this adds items to the action bar if it is present.
        getMenuInflater().inflate(R.menu.main, menu);
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        // Handle action bar item clicks here. The action bar will
        // automatically handle clicks on the Home/Up button, so long
        // as you specify a parent activity in AndroidManifest.xml.
        int id = item.getItemId();

        //noinspection SimplifiableIfStatement
        if (id == R.id.action_settings) {
            return true;
        }

        return super.onOptionsItemSelected(item);
    }

    @SuppressWarnings("StatementWithEmptyBody")
    @Override
    public boolean onNavigationItemSelected(MenuItem item) {
        // Handle navigation view item clicks here.
        int id = item.getItemId();

        if (id == R.id.nav_camera) {
            // Handle the camera action
        } else if (id == R.id.nav_gallery) {

        }
        DrawerLayout drawer = (DrawerLayout) findViewById(R.id.drawer_layout);
        drawer.closeDrawer(GravityCompat.START);
        return true;
    }

    @Override
    public void onStart() {
        super.onStart();
        // ATTENTION: This was auto-generated to implement the App Indexing API.
        // See https://g.co/AppIndexing/AndroidStudio for more information.

        client.connect();
        Action viewAction = Action.newAction(
                Action.TYPE_VIEW, // TODO: choose an action type.
                "Main Page", // TODO: Define a title for the content shown.
                // TODO: If you have web page content that matches this app activity's content,
                // make sure this auto-generated web page URL is correct.
                // Otherwise, set the URL to null.
                Uri.parse("http://host/path"),
                // TODO: Make sure this auto-generated app URL is correct.
                Uri.parse("android-app://com.example.linh.musicplayer/http/host/path")
        );
        AppIndex.AppIndexApi.start(client, viewAction);
    }

    @Override
    public void onStop() {
        super.onStop();
    EventBus.getDefault().unregister(this);
        // ATTENTION: This was auto-generated to implement the App Indexing API.
        // See https://g.co/AppIndexing/AndroidStudio for more information.
        Action viewAction = Action.newAction(
                Action.TYPE_VIEW, // TODO: choose an action type.
                "Main Page", // TODO: Define a title for the content shown.
                // TODO: If you have web page content that matches this app activity's content,
                // make sure this auto-generated web page URL is correct.
                // Otherwise, set the URL to null.
                Uri.parse("http://host/path"),
                // TODO: Make sure this auto-generated app URL is correct.
                Uri.parse("android-app://com.example.linh.musicplayer/http/host/path")
        );
        AppIndex.AppIndexApi.end(client, viewAction);
        client.disconnect();
    }

}
