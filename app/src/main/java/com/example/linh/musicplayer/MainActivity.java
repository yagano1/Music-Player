package com.example.linh.musicplayer;

import android.app.Dialog;
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
import java.util.ArrayList;
import java.util.List;

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
    private File root;
    private List<String> fileList = new ArrayList<String>();
        /**
     * ATTENTION: This was auto-generated to implement the App Indexing API.
     * See https://g.co/AppIndexing/AndroidStudio for more information.
     */
        private GoogleApiClient client;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        root = new File(Environment.getExternalStorageDirectory().getAbsolutePath());
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
                displayAddmusicDialog();}
        });
        buttonNext = (Button) findViewById(R.id.buttonForward);
        buttonNext.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if(songPosistion < totalsong -1 )
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

    @Override
    protected Dialog onCreateDialog(int id) {
        Dialog dialog = new Dialog(MainActivity.this);
        dialog.setContentView(R.layout.dialog_downloadlink);
        dialog.setTitle("Dialog download link");
        dialog.setCancelable(true);
        return dialog;
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

    public void displayAddmusicDialog()
    {
        LayoutInflater inflater = getLayoutInflater();
        View alertLayout  = inflater.inflate(R.layout.dialog_addmusic,null);
        AlertDialog.Builder alert = new AlertDialog.Builder(this);
        alert.setView(alertLayout);
        alert.setTitle("Add music");
        final AlertDialog dialog = alert.create();
        dialog.show();
        buttonDownloadMusic = (Button) alertLayout.findViewById(R.id.buttonDownwload);
        buttonDownloadMusic.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                dialog.cancel();
                displayDownloadMusicDialog();
            }
        });
        buttonOpenfile = (Button) alertLayout.findViewById(R.id.buttonOpenfile);
        buttonOpenfile.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                dialog.cancel();
                displayOpenFileDialog();
                curFolder = root;
            }
        });
    }
    ListView listviewFolder;
    TextView textFolder;
    Button buttonParentFolder;
    File curFolder;

    private void displayOpenFileDialog() {
        LayoutInflater inflater = getLayoutInflater();
        View alertLayout  = inflater.inflate(R.layout.diaglog_openfile,null);
        AlertDialog.Builder alert = new AlertDialog.Builder(this);
        alert.setTitle("Open music");
        alert.setView(alertLayout);
        final AlertDialog dialog = alert.create();
        dialog.show();
        textFolder = (TextView) alertLayout.findViewById(R.id.textFolder);
        listviewFolder = (ListView) alertLayout.findViewById(R.id.listviewFolder);
        buttonParentFolder = (Button) alertLayout.findViewById(R.id.buttonParent);
        listDir(root);
        buttonParentFolder.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                listDir(curFolder.getParentFile());
            }
        });
        listviewFolder.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
                File selected = new File(fileList.get(position));
                if(selected.isDirectory())
                {
                    listDir(selected);
                }
                else
                {
                    fileMusicPatch = selected.getAbsolutePath();
                    mediaPlayer.reset();
                    textSongPlaying.setText(selected.getName());
                    try {
                        mediaPlayer.setDataSource(fileMusicPatch);
                        mediaPlayer.prepare();
                    } catch (IOException e) {
                        e.printStackTrace();
                    }
                    dialog.cancel();
                }
            }
        });
    }

    private void listDir(File f) {
        if(f.equals(root))
        {
            buttonParentFolder.setEnabled(false);
        }
        else
        {
            buttonParentFolder.setEnabled(true);
        }

        curFolder = f;
        textFolder.setText(f.getPath());
        File[] files  = f.listFiles();
        fileList.clear();
        for(File file : files)
        {
            fileList.add(file.getPath());
        }
        ArrayAdapter<String> direciryList = new ArrayAdapter<String>(this,android.R.layout.simple_list_item_1,fileList);
        listviewFolder.setAdapter(direciryList);
    }

    private void displayDownloadMusicDialog() {
        LayoutInflater inflater = getLayoutInflater();
        View alertLayout  = inflater.inflate(R.layout.dialog_downloadlink,null);
        editTextLink = (EditText) alertLayout.findViewById(R.id.textInputLink);
        editTextName = (EditText) alertLayout.findViewById(R.id.textInputName);
        AlertDialog.Builder alert = new AlertDialog.Builder(this);
        alert.setTitle("Download music");
        alert.setView(alertLayout);
        alert.setNegativeButton("Cancel", new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialog, int which) {
            }
        });
        alert.setPositiveButton("Download", new DialogInterface.OnClickListener() {

            @Override
            public void onClick(DialogInterface dialog, int which) {
                Intent intent = new Intent(MainActivity.this, DownloadService.class);
                intent.putExtra("URL", editTextLink.getText().toString());
                intent.putExtra("NAME",editTextName.getText().toString());
                startService(intent);
            }
        });
        final AlertDialog dialog = alert.create();
        dialog.show();
    }

    public void songplay()
    {

        mediaPlayer.stop();
        if(totalsong  > 0)
        {
            songname = lv.getItemAtPosition(songPosistion).toString();
            textSongPlaying.setText(songname);
        }

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
        totalsong = filelist.length;
        String[] theNamesOfFiles = new String[filelist.length];
        for (int i = 0; i < theNamesOfFiles.length; i++) {
            theNamesOfFiles[i] = filelist[i].getName();

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
