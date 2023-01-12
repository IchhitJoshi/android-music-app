package com.example.demo.views;

import android.content.Context;
import android.os.Bundle;

import androidx.fragment.app.Fragment;
import androidx.fragment.app.FragmentManager;
import androidx.fragment.app.FragmentTransaction;
import androidx.viewpager2.widget.ViewPager2;

import android.os.Handler;
import android.os.Looper;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.ProgressBar;

import com.bumptech.glide.Glide;
import services.MusicService;
import com.example.demo.R;

import java.util.Objects;

import adapters.MiniPlayerAdapter;
import listeners.OnSwipeTouchListener;
import managers.PlaybackManager;

import static com.example.demo.views.Home.playerOpen;
import static com.example.demo.views.Home.queueBool;
import static com.example.demo.views.Home.queueOpen;

import models.Song;

/**
 * A simple {@link Fragment} subclass.
 */
public class MiniPlayerFragment extends Fragment implements Runnable, MiniPlayerAdapter.MiniPlayerItemClickListener {

    MiniPlayerAdapter miniPlayerAdapter;
    ViewPager2 miniPlayerViewpager;

    Button miniPausePlay;
    ImageView miniArt;
    Song song;
    LinearLayout miniPlayer;
    ProgressBar progressBar;

    MusicService musicService;
    boolean onSongChanged;

    Context context;
    Handler handler;

    private PlaybackManager.Listener miniPlayerListener;

    public MiniPlayerFragment(Context mContext) {
        this.context = mContext;
    }


    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        final View view = inflater.inflate(R.layout.fragment_mini_player, container, false);

        handler = new Handler(Looper.getMainLooper());
        musicService = ((Home) (getActivity())).getMusicService();

        miniPlayerListener =  musicService.addListener(new PlaybackManager.Listener() {
            @Override
            public void onSongChanged(Song newSong) {
                onSongChanged = true;
                setScreen(newSong, true);
            }
        });

        miniPlayer = view.findViewById(R.id.miniPlayer);
        miniPlayerViewpager = view.findViewById(R.id.miniPlayerViewPager);
        miniPausePlay = view.findViewById(R.id.mini_pausePlayButton);
        miniArt = view.findViewById(R.id.miniArt);
        progressBar = view.findViewById(R.id.miniProgress);

        song = musicService.getCurrentQueueItem();

        Log.d("Shared Pref CHECK", String.valueOf(song.getSongName()));

        miniPlayerAdapter = new MiniPlayerAdapter(context, musicService, MiniPlayerFragment.this);
        miniPlayerViewpager.setAdapter(miniPlayerAdapter);

        miniPlayerViewpager.setClipToPadding(false);
        miniPlayerViewpager.setClipChildren(false);
        miniPlayerViewpager.getChildAt(0).setOverScrollMode(View.OVER_SCROLL_NEVER);
        miniPlayerViewpager.setPageTransformer(new FadePageTransformer());

        setScreen(song, false);
        updateProgress();

        miniPlayerViewpager.registerOnPageChangeCallback(new ViewPager2.OnPageChangeCallback() {
            @Override
            public void onPageScrolled(int position, float positionOffset, int positionOffsetPixels) {
                super.onPageScrolled(position, positionOffset, positionOffsetPixels);
                if (position == musicService.getCurrentQueueItemPosition() + 1  && !onSongChanged){
                    playNextSong();

                }else if (position == musicService.getCurrentQueueItemPosition() - 1 &&  positionOffset < 0.2f  && !onSongChanged){
                    playPreviousSong();
                }

                if (onSongChanged){
                    onSongChanged = false;
                }

            }

        });

        if (!musicService.isPlaying()){
            miniPausePlay.setBackgroundResource(R.drawable.play_button_gray);
        }else {
            miniPausePlay.setBackgroundResource(R.drawable.pause_button_gray);
        }

        miniPausePlay.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                playSong();
                if (!musicService.isPlaying()){
                    miniPausePlay.setBackgroundResource(R.drawable.play_button_gray);
                }else {
                    miniPausePlay.setBackgroundResource(R.drawable.pause_button_gray);
                }
            }
        });


        miniPlayer.setOnTouchListener(new OnSwipeTouchListener(context){

            @Override
            public void onClick()
            {
                super.onClick();
                openPlayerFragment();

            }

            @Override
            public void onDoubleClick()
            {
                super.onDoubleClick();

                // your on onDoubleClick here
            }

            @Override
            public void onLongClick()
            {
                super.onLongClick();
                openQueueFragment();
            }

            @Override
            public void onSwipeUp() {
                super.onSwipeUp();
               openPlayerFragment();
            }

        });

        return view;
    }

    void setScreen(Song song, boolean x) {
        long duration_total = Long.parseLong(song.getDuration());
        int dur = (int) duration_total;
        progressBar.setMax(dur);
        Glide.with(context).load(song.getSongUrl()).into(miniArt);
        miniPlayerViewpager.setCurrentItem(musicService.getCurrentQueueItemPosition(), x);
        miniPausePlay.setBackgroundResource(R.drawable.pause_button_gray);
    }

    private void playSong() {
        musicService.playPause();
    }

    private void playPreviousSong() {
        song = musicService.getPreviousSong();
        musicService.loadSong(song);
        setScreen(song, true);
        musicService.play();
    }

    private void playNextSong() {
        song = musicService.getNextSong();
        musicService.loadSong(song);
        setScreen(song, true);
        musicService.play();
    }

    @Override
    public void run() {
        updateProgress();
    }

    private void updateProgress() {
        int cP = 0;
        long duration_total = Long.parseLong(song.getDuration());
        int dur = (int) duration_total;
        if(!musicService.isNull() && cP < dur) {
            cP = musicService.getCurrentPosition();
            progressBar.setProgress(cP);
        }
        if (!musicService.isPlaying()){
            miniPausePlay.setBackgroundResource(R.drawable.play_button_gray);
        }else {
            miniPausePlay.setBackgroundResource(R.drawable.pause_button_gray);
        }

        handler.postDelayed(this, 500);
    }

    private void openPlayerFragment(){
        PlayerFragment playerFragment = new PlayerFragment(context);
        FragmentManager manager = Objects.requireNonNull(getActivity()).getSupportFragmentManager();
        FragmentTransaction transaction = manager.beginTransaction();
        transaction.replace(R.id.MainLayout, playerFragment).addToBackStack(null).commit();
        playerOpen = true;
        queueBool = false;
        queueOpen = false;
    }

    private void openQueueFragment(){
        QueueFragment queueFragment = new QueueFragment(context);
        FragmentManager manager = Objects.requireNonNull(getActivity()).getSupportFragmentManager();
        FragmentTransaction transaction = manager.beginTransaction();
        transaction.replace(R.id.MainLayout ,queueFragment).addToBackStack(null).commit();
        queueBool = false;
        queueOpen = true;
        playerOpen = false;
    }


    @Override
    public void onMiniPlayerClick() {
        openPlayerFragment();
    }

    @Override
    public void onMiniPlayerLongClick() {
        openQueueFragment();
    }

    @Override
    public void onDestroy() {
        handler.removeCallbacks(this);
        musicService.removeListener(miniPlayerListener);
        super.onDestroy();
    }

}


class FadePageTransformer implements ViewPager2.PageTransformer {
    public void transformPage(View view, float position) {
        if(position <= -1.0F || position >= 1.0F) {
//            view.setTranslationX(view.getWidth() * position);
            view.setAlpha(0.0F);
        } else if( position == 0.0F ) {
//            view.setTranslationX(view.getWidth() * position);
            view.setAlpha(1.0F);
        } else {
            // position is between -1.0F & 0.0F OR 0.0F & 1.0F
//            view.setTranslationX(view.getWidth() * -position);
            view.setAlpha(1.0F - Math.abs(position));
        }
    }
}
