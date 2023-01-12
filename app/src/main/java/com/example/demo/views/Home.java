package com.example.demo.views;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.app.AppCompatDelegate;
import androidx.fragment.app.Fragment;
import androidx.fragment.app.FragmentManager;
import androidx.fragment.app.FragmentTransaction;

import android.content.ComponentName;
import android.content.Context;
import android.content.Intent;
import android.content.ServiceConnection;
import android.os.Build;
import android.os.Bundle;
import android.os.IBinder;
import android.os.PersistableBundle;
import android.util.Log;
import android.view.MenuItem;
import android.view.View;
import android.view.Window;
import android.view.WindowManager;
import android.widget.FrameLayout;

import services.MusicService;
import com.example.demo.R;

import adapters.AlbumAdapter;
import adapters.ArtistAdapter;
import adapters.SongAdapter;

import com.google.android.material.bottomnavigation.BottomNavigationView;

import java.util.ArrayList;

import managers.MediaImporter;
import models.Song;
import utils.SharedPref;


public class Home extends AppCompatActivity implements SongAdapter.AdapterCallback, PlayerFragment.SongChangeCallback, QueueFragment.hideMiniFromQueue, QueueFragment.QueueItemCallback,
        AlbumAdapter.AlbumAdapterPlayCallback, ArtistAdapter.ArtistAdapterPlayCallback, ServiceConnection, MusicService.ShowNotification {

    Window window;

    private BottomNavigationView mainNav;
    private FrameLayout mainFrame;
    private FrameLayout mini1;
    private FrameLayout player;

    private HomeFragment homeFragment;
    private MusicFragment musicFragment;
    private SearchFragment searchFragment;

    public static boolean nightMode;
    public static boolean systemDefault;

    public static boolean serviceBound = false;

    public static ArrayList<Song> _songs = new ArrayList<Song>();

    Context context = this;
    MediaImporter mediaImporter = new MediaImporter(context);
    SharedPref sharedPref;

    public static MusicService musicService;

    public static boolean queueBool = false;
    public static boolean playerOpen = false;
    public static boolean queueOpen = false;
    public static boolean albumDetailsOpen = false;
    public static boolean artistDetailsOpen = true;

    public static boolean shuffle = false;
    public static boolean repeat = false;
    public static boolean loop = false;
    public static boolean durFlip = false;

    public static int songs_sort_number = 1;
    public static boolean songs_ascending = true;
    public static int albums_sort_number = 1;
    public static boolean albums_ascending = true;
    public static int artists_sort_number = 1;
    public static boolean artists_ascending = true;
    public static int queue_sort_number = 1;
    public static boolean queue_ascending = true;



    @Override
    public void onSaveInstanceState(Bundle outState, PersistableBundle outPersistentState) {
        super.onSaveInstanceState(outState, outPersistentState);
        outState.putBoolean("serviceStatus", serviceBound);
    }

    @Override
    protected void onRestoreInstanceState(Bundle savedInstanceState) {
        super.onRestoreInstanceState(savedInstanceState);
        serviceBound = savedInstanceState.getBoolean("serviceStatus");
    }


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_home);

        sharedPref = new SharedPref(context);

        setStatusBar();

        mainFrame = findViewById(R.id.mainFrame);
        mainNav = findViewById(R.id.mainNav);
        mini1 = findViewById(R.id.mini1);
        player = findViewById(R.id.Player_main);

        homeFragment = new HomeFragment();
        musicFragment = new MusicFragment();
        searchFragment = new SearchFragment();

        mainNav.setSelectedItemId(R.id.navHome);

        checkDarkMode();
        loadSharedPreferences();

        mediaImporter.runTimePermission();

        mainNav.setOnNavigationItemSelectedListener(new BottomNavigationView.OnNavigationItemSelectedListener() {
            @Override
            public boolean onNavigationItemSelected(@NonNull MenuItem menuItem) {

                switch (menuItem.getItemId()) {

                    case R.id.navHome:
                        setFragment(homeFragment);
                        return true;

                    case R.id.navSearch:
                        setFragment(searchFragment);
                        return true;

                    case R.id.navMusic:
                        setFragment(musicFragment);
                        return true;

                    default:
                        return false;
                }
            }

        });


        if (!serviceBound) {

            Intent playerIntent = new Intent(this, MusicService.class);
            startService(playerIntent);
            bindService(playerIntent, this, Context.BIND_AUTO_CREATE);
            Log.d("HELLO123", "2nd");


        } else {
            musicService.setClosed(false);
            musicService.setCallBack(this);

            if (musicService.isEmpty()) {
                mini1.setVisibility(View.GONE);
            } else {
                mini1.setVisibility(View.VISIBLE);
                addFragment2();
            }
            Log.d("HELLO123", "3rd");

            setFragment(homeFragment);

            queueBool = false;
            queueOpen = false;

            Bundle extras = getIntent().getExtras();

            if (extras != null) {
                boolean value = extras.getBoolean("Opened from Notification");
                if (value) {
                    Log.d("HELLO123", "4th");
                    PlayerFragment fragmentB = new PlayerFragment(context);
                    FragmentManager manager = getSupportFragmentManager();
                    FragmentTransaction transaction = manager.beginTransaction();
                    transaction.replace(R.id.MainLayout, fragmentB).addToBackStack(null).commit();
                    playerOpen = true;
                }
            }


        }



    }

    private void loadSharedPreferences() {
//        shuffle = sharedPref.loadShuffleMode();

        repeat = sharedPref.loadRepeatMode();

        loop = sharedPref.loadLoopMode();

        durFlip = sharedPref.loadDurationFlip();

        songs_sort_number = sharedPref.loadSongsSortNumber();

        songs_ascending = sharedPref.loadSongsAscending();

        albums_sort_number = sharedPref.loadAlbumsSortNumber();

        albums_ascending = sharedPref.loadAlbumsAscending();

        artists_sort_number = sharedPref.loadArtistsSortNumber();

        artists_ascending = sharedPref.loadArtistsAscending();

    }

    private void checkDarkMode() {

        if (sharedPref.loadSystemDefaultState()){
            AppCompatDelegate.setDefaultNightMode(AppCompatDelegate.MODE_NIGHT_FOLLOW_SYSTEM);
            systemDefault = true;
            if(AppCompatDelegate.getDefaultNightMode() == AppCompatDelegate.MODE_NIGHT_YES){
                sharedPref.saveDarkMode(true);
                nightMode = true;
            }else{
                sharedPref.saveDarkMode(false);
                nightMode = false;
            }


        }else{
            if(sharedPref.loadDarkModeState()){
                AppCompatDelegate.setDefaultNightMode(AppCompatDelegate.MODE_NIGHT_YES);
                nightMode = true;
            }else{
                AppCompatDelegate.setDefaultNightMode(AppCompatDelegate.MODE_NIGHT_NO);
                nightMode = false;
            }
        }

    }

    private void setStatusBar() {
        if (Build.VERSION.SDK_INT >= 21) {
            window = this.getWindow();
            window.setStatusBarColor(this.getResources().getColor(R.color.miniPlayerBackground));
            window.clearFlags(WindowManager.LayoutParams.FLAG_LAYOUT_NO_LIMITS);
            View decor = getWindow().getDecorView();
            if(sharedPref.loadDarkModeState()){
                window.clearFlags(View.SYSTEM_UI_FLAG_LIGHT_STATUS_BAR);
            }else{
                decor.setSystemUiVisibility(View.SYSTEM_UI_FLAG_LIGHT_STATUS_BAR);
            }

        }
    }



    @Override
    public void onServiceConnected(ComponentName name, IBinder service) {
        MusicService.MyBinder binder = (MusicService.MyBinder) service;
        musicService = binder.getService();
        serviceBound = true;


        musicService.setClosed(false);
        musicService.setCallBack(this);

        shuffle = musicService.loadShuffleMode();

        if (musicService.isEmpty()) {
            mini1.setVisibility(View.GONE);
        } else {
            mini1.setVisibility(View.VISIBLE);
            addFragment2();
        }
        Log.d("HELLO123", "3rd");

        setFragment2(homeFragment);

    }


    @Override
    public void onServiceDisconnected(ComponentName name) {
        serviceBound = false;
        Log.d("message123", "DISCONNECTED");

    }


    @Override
    protected void onDestroy() {
        super.onDestroy();
        Log.d("HELLO123", "onDestroy");
        queueOpen = false;
        queueBool = false;
        playerOpen = false;
        albumDetailsOpen = false;

        sharedPref.saveLoopMode(loop);
        sharedPref.saveRepeatMode(repeat);
//        sharedPref.saveShuffleMode(shuffle);
        sharedPref.saveDurationFlip(durFlip);
        sharedPref.saveSongsSortNumber(songs_sort_number);
        sharedPref.saveSongsAscending(songs_ascending);
        sharedPref.saveAlbumsSortNumber(albums_sort_number);
        sharedPref.saveAlbumsAscending(albums_ascending);
        sharedPref.saveArtistsSortNumber(artists_sort_number);
        sharedPref.saveArtistsAscending(artists_ascending);

        if (serviceBound) {
            musicService.setClosed(true);
            musicService.setCallBack(null);
            if (!musicService.isPlaying() && !musicService.isNull()) {
                musicService.removeNotification();
                unbindService(this);
                serviceBound = false;
                musicService.stopSelf();


            }

        }
    }

    private void addFragment() {

        FragmentManager fragmentManager = getSupportFragmentManager();
        FragmentTransaction fragmentTransaction = fragmentManager.beginTransaction();
        MiniPlayerFragment miniPlayer = new MiniPlayerFragment(context);
        fragmentTransaction.replace(R.id.mini1, miniPlayer);
        fragmentTransaction.commit();

    }

    private void addFragment2() {
        if (!isDestroyed()) {
            addFragment();

        }
    }

    private void setFragment(Fragment fragment) {
        FragmentTransaction fragmentTransaction = getSupportFragmentManager().beginTransaction();
        fragmentTransaction.replace(R.id.mainFrame, fragment);
        fragmentTransaction.commit();


    }

    private void setFragment2(Fragment fragment) {

        if(!isDestroyed()){
            setFragment(fragment);

        }

    }


    @Override
    public void onBackPressed() {
        Log.d("backstack", String.valueOf(getFragmentManager().getBackStackEntryCount()));
//        if (getFragmentManager().getBackStackEntryCount() > 0) {
//            getFragmentManager().popBackStack();
//        } else {
        super.onBackPressed();

        if (playerOpen || queueBool || queueOpen){
            if (albumDetailsOpen){
                if(Build.VERSION.SDK_INT>=21){
                    window = this.getWindow();
                    if(nightMode){
                        window.setStatusBarColor(this.getResources().getColor(R.color.statusBarColorDark));

                    }else{
                        window.setStatusBarColor(this.getResources().getColor(R.color.colorAccent));
                    }

                    window.setFlags(WindowManager.LayoutParams.FLAG_LAYOUT_NO_LIMITS, WindowManager.LayoutParams.FLAG_LAYOUT_NO_LIMITS);

                    View decor = window.getDecorView();


                    if(nightMode){
                        window.clearFlags(View.SYSTEM_UI_FLAG_LIGHT_STATUS_BAR);
                    }else{
                        decor.setSystemUiVisibility(View.SYSTEM_UI_FLAG_LIGHT_STATUS_BAR);
                    }



                }

            }

        }

        getFragmentManager().popBackStack();

//        if (!musicService.isEmpty()) {
//            mini1.setVisibility(View.VISIBLE);
//            addFragment();
//        }

//        if (!playerOpen && albumDetailsOpen && !queueBool && !queueOpen){
//            if(Build.VERSION.SDK_INT>=21){
//                window = getWindow();
//                window.setStatusBarColor(getResources().getColor(R.color.background));
//                window.clearFlags(WindowManager.LayoutParams.FLAG_LAYOUT_NO_LIMITS);
//                View decor = window.getDecorView();
//                decor.setSystemUiVisibility(View.SYSTEM_UI_FLAG_LIGHT_STATUS_BAR);
//            }
//
//        }

//
//        if (queueBool || queueOpen){
//            getFragmentManager().popBackStack();
//            queueBool = false;
//            queueOpen = false;
//            return;
//        }
//
//

//
//        moveTaskToBack(true);

//        Intent a = new Intent(Intent.ACTION_MAIN);
//        a.addCategory(Intent.CATEGORY_HOME);
//        a.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
//        startActivity(a);

//
//    }

        setStatusBar();



    }

    public MediaImporter getMediaImporter(){return mediaImporter;};


    public void onAlbumPlayCallback(){
        mini1.setVisibility(View.VISIBLE);
        addFragment();
    }

    @Override
    public void onArtistPlayCallback() {
        mini1.setVisibility(View.VISIBLE);
        addFragment();
    }


    @Override
    public void onNextSong(Song song) {
        MiniPlayerFragment miniPlayerFragment = (MiniPlayerFragment) getSupportFragmentManager().findFragmentById(R.id.mini1);
        assert miniPlayerFragment != null;
        miniPlayerFragment.setScreen(song, false);

        if (queueBool){
            PlayerFragment playerFragment = (com.example.demo.views.PlayerFragment) getSupportFragmentManager().findFragmentById(R.id.MainLayout);
            assert playerFragment != null;
            playerFragment.setScreen(song, false);

        }

    }


    @Override
    public void onQueueCleared() {
        mini1.setVisibility(View.GONE);
        setStatusBar();
        musicService.removeNotification();
    }


    @Override
    public void onQueueItemClick2(Song song2) {
        MiniPlayerFragment miniPlayerFragment = (MiniPlayerFragment) getSupportFragmentManager().findFragmentById(R.id.mini1);
        assert miniPlayerFragment != null;

        miniPlayerFragment.setScreen(song2, false);

        if (queueBool){
            PlayerFragment playerFragment = (com.example.demo.views.PlayerFragment) getSupportFragmentManager().findFragmentById(R.id.MainLayout);
            assert playerFragment !=null;

            playerFragment.setScreen(song2, false);
            playerFragment.checkButtons();

        }

    }

    @Override
    public void onQueueChanged2() {
        MiniPlayerFragment miniPlayerFragment = (MiniPlayerFragment) getSupportFragmentManager().findFragmentById(R.id.mini1);
        assert miniPlayerFragment != null;

        miniPlayerFragment.miniPlayerAdapter.notifyDataSetChanged();
        miniPlayerFragment.setScreen(musicService.getCurrentQueueItem(), false);

        if (queueBool){
            PlayerFragment playerFragment = (com.example.demo.views.PlayerFragment) getSupportFragmentManager().findFragmentById(R.id.MainLayout);
            assert playerFragment !=null;

            playerFragment.albumArtAdapter.notifyDataSetChanged();
            playerFragment.setScreen(musicService.getCurrentQueueItem(), false);

        }
    }

    @Override
    public void onMethodCallback(){
        mini1.setVisibility(View.VISIBLE);
        if (!musicService.requestAudioFocus()){
            if (serviceBound) {
                musicService.pause();

            }

        }
        addFragment();
        musicService.setClosed(false);

    }




    public MusicService getMusicService(){
        return musicService;
    }

    public SharedPref getSharedPref(){return sharedPref;}


    @Override
    public void updateViewsFromNotification() {
        MiniPlayerFragment miniPlayerFragment = (MiniPlayerFragment) getSupportFragmentManager().findFragmentById(R.id.mini1);
        assert miniPlayerFragment != null;
        miniPlayerFragment.setScreen(musicService.getCurrentQueueItem(), false);

        if (queueBool){
            QueueFragment queueFragment = (com.example.demo.views.QueueFragment) getSupportFragmentManager().findFragmentById(R.id.Player_main);
            assert queueFragment != null;
            queueFragment.setScreen(musicService.getCurrentQueueItem());
            queueFragment.notifyDataSetChanged();

            PlayerFragment playerFragment = (com.example.demo.views.PlayerFragment) getSupportFragmentManager().findFragmentById(R.id.MainLayout);
            assert playerFragment != null;
            playerFragment.setScreen(musicService.getCurrentQueueItem(), false);

        }


        if (queueOpen){
            QueueFragment queueFragment = (com.example.demo.views.QueueFragment) getSupportFragmentManager().findFragmentById(R.id.MainLayout);
            assert queueFragment != null;
            queueFragment.setScreen(musicService.getCurrentQueueItem());
            queueFragment.notifyDataSetChanged();

        }


        if (!queueBool && playerOpen && !queueOpen){
            PlayerFragment playerFragment = (com.example.demo.views.PlayerFragment) getSupportFragmentManager().findFragmentById(R.id.MainLayout);
            assert playerFragment != null;
            playerFragment.setScreen(musicService.getCurrentQueueItem(), false);

        }
    }


}
