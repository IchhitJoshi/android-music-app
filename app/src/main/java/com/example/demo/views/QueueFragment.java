package com.example.demo.views;

import android.app.Activity;
import android.content.Context;
import android.os.Build;
import android.os.Bundle;
import android.os.Handler;
import android.os.Looper;
import android.view.LayoutInflater;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;
import android.view.Window;
import android.view.WindowManager;
import android.widget.Button;
import android.widget.SeekBar;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.Toolbar;
import androidx.fragment.app.Fragment;
import androidx.fragment.app.FragmentManager;
import androidx.recyclerview.widget.ItemTouchHelper;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import services.MusicService;
import com.example.demo.R;
import adapters.QueueAdapter;
import com.simplecityapps.recyclerview_fastscroll.views.FastScrollRecyclerView;

import java.util.ArrayList;


import listeners.OnStartDragListener;
import managers.PlaybackManager;
import models.Song;
import utils.SimpleItemTouchHelperCallback;

import static com.example.demo.views.Home.albumDetailsOpen;
import static com.example.demo.views.Home.artistDetailsOpen;
import static com.example.demo.views.Home.durFlip;
import static com.example.demo.views.Home.loop;
import static com.example.demo.views.Home.nightMode;
import static com.example.demo.views.Home.playerOpen;
import static com.example.demo.views.Home.queueBool;
import static com.example.demo.views.Home.queueOpen;
import static com.example.demo.views.Home.repeat;
import static com.example.demo.views.Home.shuffle;

public class QueueFragment extends Fragment implements QueueAdapter.QueueItemClickListener, QueueAdapter.OnQueueChangedListener, OnStartDragListener, Runnable {

    private Window window;

    hideMiniFromQueue hideMiniFromQueuex;
    QueueItemCallback queueItemCallback;

    private PlaybackManager.Listener queue_Listener;
    private PlayerFragment.SongChangeCallback songChangeCallback;

    private Toolbar queueToolbar;
    private FastScrollRecyclerView recyclerView;

    MusicService musicService;

    private final Context context;
    Handler handler;

    private static ArrayList<Song> _queue = new ArrayList<Song>();
    private QueueAdapter queueAdapter;

    private TextView durationPlayed, durationTotal;
    private SeekBar seekBar;
    private Button playButton, nextButton, previousButton, shuffleButton, repeatButton;

    private Song song;

    private ItemTouchHelper mItemTouchHelper;

    public QueueFragment(Context context){
        this.context = context;
        try {
            songChangeCallback = (PlayerFragment.SongChangeCallback) context;
        } catch (ClassCastException ignored) {
        }
    }

    @Override
    public void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setHasOptionsMenu(true);
    }

    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        final View view = inflater.inflate(R.layout.fragment_queue, container, false);

        if(Build.VERSION.SDK_INT>=21){
            window = getActivity().getWindow();
            if(nightMode){
                window.setStatusBarColor(this.getResources().getColor(R.color.statusBarColorDark));

            }else{
                window.setStatusBarColor(this.getResources().getColor(R.color.colorAccent));
            }

            window.clearFlags(WindowManager.LayoutParams.FLAG_LAYOUT_NO_LIMITS);

            View decor = window.getDecorView();


            if(nightMode){
                window.clearFlags(View.SYSTEM_UI_FLAG_LIGHT_STATUS_BAR);
            }else{
                decor.setSystemUiVisibility(View.SYSTEM_UI_FLAG_LIGHT_STATUS_BAR);
            }



        }


        handler = new Handler(Looper.getMainLooper());

        musicService = ((Home) (getActivity())).getMusicService();

        queue_Listener = musicService.addListener(new PlaybackManager.Listener() {
            @Override
            public void onSongChanged(Song newSong) {
                queueAdapter.notifyDataSetChanged();
                queueToolbar.setTitle(newSong.getSongName());
                queueToolbar.setSubtitle(newSong.getArtistName() + " • " + newSong.getAlbumName());
                durationPlayed.setText(formattedText(0));
                setScreen(newSong);
            }
        });

        queueToolbar = view.findViewById(R.id.queueToolbar);
        ((AppCompatActivity)getActivity()).setSupportActionBar(queueToolbar);

        _queue = musicService.getCurrentQueue();
        song = musicService.getCurrentQueueItem();

        recyclerView = view.findViewById(R.id.queueRecyclerView);
        seekBar = view.findViewById(R.id.queueSongProgressBar);
        playButton = view.findViewById(R.id.queuePausePlayButton);
        nextButton = view.findViewById(R.id.queuePlayNextButton);
        previousButton = view.findViewById(R.id.queuePlayPrevButton);
        shuffleButton = view.findViewById(R.id.queueSongShuffleButton);
        repeatButton = view.findViewById(R.id.queueRepeatButton);
        durationPlayed = view.findViewById(R.id.queueSongDurationPlayed);
        durationTotal = view.findViewById(R.id.queueSongDurationTotal);

        queueAdapter = new QueueAdapter(context, _queue, musicService, QueueFragment.this, this, this);
        ItemTouchHelper.Callback callback = new SimpleItemTouchHelperCallback(queueAdapter);
        mItemTouchHelper = new ItemTouchHelper(callback);
        mItemTouchHelper.attachToRecyclerView(recyclerView);
        recyclerView.setAdapter(queueAdapter);
        LinearLayoutManager linearLayoutManager = new LinearLayoutManager(this.getContext());
        recyclerView.setLayoutManager(linearLayoutManager);
        recyclerView.scrollToPosition(musicService.getCurrentQueueItemPosition()+1);

        playButton.getBackground().setTint(getResources().getColor(R.color.mainTextColor));
        nextButton.getBackground().setTint(getResources().getColor(R.color.mainTextColor));
        previousButton.getBackground().setTint(getResources().getColor(R.color.mainTextColor));
        shuffleButton.getBackground().setTint(getResources().getColor(R.color.mainTextColor));
        repeatButton.getBackground().setTint(getResources().getColor(R.color.mainTextColor));



        checkButtons();
        setScreen(song);
        updateProgress();

        queueToolbar.setNavigationOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                getActivity().getSupportFragmentManager().popBackStack();
                 if ((albumDetailsOpen || artistDetailsOpen) && !queueBool){
                if(Build.VERSION.SDK_INT>=21){
                    window = getActivity().getWindow();
                    if(nightMode){
                        window.setStatusBarColor(getActivity().getResources().getColor(R.color.statusBarColorDark));

                    }else{
                        window.setStatusBarColor(getActivity().getResources().getColor(R.color.colorAccent));
                    }

                    window.setFlags(WindowManager.LayoutParams.FLAG_LAYOUT_NO_LIMITS, WindowManager.LayoutParams.FLAG_LAYOUT_NO_LIMITS);

                    View decor = window.getDecorView();


                    if(nightMode){
                        window.clearFlags(View.SYSTEM_UI_FLAG_LIGHT_STATUS_BAR);
                    }else{
                        decor.setSystemUiVisibility(View.SYSTEM_UI_FLAG_LIGHT_STATUS_BAR);
                    }



                }

            }else{
                     if(Build.VERSION.SDK_INT>=21){
                         window = getActivity().getWindow();
                         if(nightMode){
                             window.setStatusBarColor(getActivity().getResources().getColor(R.color.background));

                         }else{
                             window.setStatusBarColor(getActivity().getResources().getColor(R.color.background));
                         }

                         window.clearFlags(WindowManager.LayoutParams.FLAG_LAYOUT_NO_LIMITS);

                         View decor = window.getDecorView();


                         if(nightMode){
                             window.clearFlags(View.SYSTEM_UI_FLAG_LIGHT_STATUS_BAR);
                         }else{
                             decor.setSystemUiVisibility(View.SYSTEM_UI_FLAG_LIGHT_STATUS_BAR);
                         }



                     }
                 }



                if (queueBool){
                    playerOpen = true;
                } else{
                    queueOpen = false;
                }

                queueBool = false;

            }
        });

        shuffleButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (!shuffle) {
                    shuffleButton.setBackgroundResource(R.drawable.ic_shuffle_blue);
                    shuffle = true;
                    musicService.shuffleQueue();


                } else {
                    shuffleButton.setBackgroundResource(R.drawable.shuffle_button_gray);
                    shuffle = false;
                    musicService.resetQueue();

                }
                queueAdapter.notifyDataSetChanged();
                queueItemCallback.onQueueItemClick2(song);
                recyclerView.scrollToPosition(musicService.getCurrentQueueItemPosition()+1);

            }
        });

        repeatButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (!repeat && !loop) {
                    repeatButton.setBackgroundResource(R.drawable.repeat_on);
                    repeat = true;

                } else if (repeat && !loop) {
                    repeatButton.setBackgroundResource(R.drawable.repeat_one);
                    loop = true;

                } else {
                    repeatButton.setBackgroundResource(R.drawable.repeat_button_gray);
                    repeat = false;
                    loop = false;
                }
            }
        });

        durationTotal.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (durFlip){
                    long duration_total = Long.parseLong(song.getDuration());
                    durationTotal.setText(formattedText(duration_total / 1000));
                    durFlip = false;
                }else{
                    durFlip = true;
                }
                queueItemCallback.onQueueItemClick2(song);
            }
        });

        playButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                playSong();
                if (!musicService.isPlaying()) {
                    playButton.setBackgroundResource(R.drawable.play_button_gray);
                } else {
                    playButton.setBackgroundResource(R.drawable.pause_button_gray);
                }
            }
        });

        nextButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                playNextSong();

            }
        });

        previousButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                playPreviousSong();
            }
        });

        seekBar.setOnSeekBarChangeListener(new SeekBar.OnSeekBarChangeListener() {
            @Override
            public void onProgressChanged(SeekBar seekBar, int progress, boolean fromUser) {
                if(!musicService.isNull() && fromUser && progress < seekBar.getMax()){
                    musicService.seekTo(progress);
                }
            }

            @Override
            public void onStartTrackingTouch(SeekBar seekBar) {}

            @Override
            public void onStopTrackingTouch(SeekBar seekBar) {}
        });

        return view;
    }

    private void setTitle(){
        queueToolbar.setTitle(musicService.getCurrentQueueItem().getSongName());
        queueToolbar.setSubtitle(musicService.getCurrentQueueItem().getArtistName() + " • " + musicService.getCurrentQueueItem().getAlbumName());
    }

    private void checkButtons() {
        if (!musicService.isPlaying()) {
            playButton.setBackgroundResource(R.drawable.play_button_gray);
        } else {
            playButton.setBackgroundResource(R.drawable.pause_button_gray);
        }

        if (!shuffle) {
            shuffleButton.setBackgroundResource(R.drawable.shuffle_button_gray);
        } else {
            shuffleButton.setBackgroundResource(R.drawable.ic_shuffle_blue);
        }

        if (!repeat && !loop) {
            repeatButton.setBackgroundResource(R.drawable.repeat_button_gray);
        } else if (repeat && !loop) {
            repeatButton.setBackgroundResource(R.drawable.repeat_on);
        } else {
            repeatButton.setBackgroundResource(R.drawable.repeat_one);
        }
    }

    private void playSong() {
        musicService.playPause();
    }

    void setScreen(Song song) {
        setTitle();
        long duration_total = Long.parseLong(song.getDuration());
        if (!durFlip){
            durationTotal.setText(formattedText(duration_total / 1000));
        }
        int dur = (int) duration_total;
        seekBar.setMax(dur);
        playButton.setBackgroundResource(R.drawable.pause_button_gray);
    }


    private void playNextSong() {
        song = musicService.getNextSong();
        musicService.loadSong(song);
        setScreen(song);
        musicService.play();
        songChangeCallback.onNextSong(song);
        queueAdapter.notifyDataSetChanged();
    }


    private void playPreviousSong() {
        song = musicService.getPreviousSong();
        musicService.loadSong(song);
        setScreen(song);
        musicService.play();
        songChangeCallback.onNextSong(song);
        queueAdapter.notifyDataSetChanged();
    }

    void notifyDataSetChanged(){
        queueAdapter.notifyDataSetChanged();
    }

    @Override
    public void run() {
        updateProgress();
    }

    private void updateProgress() {
        int cP = 0;
        long duration_total = Long.parseLong(song.getDuration());
        int dur = (int) duration_total;
        if (!musicService.isPlaying()) {
            playButton.setBackgroundResource(R.drawable.play_button_gray);
        } else {
            playButton.setBackgroundResource(R.drawable.pause_button_gray);
        }
        if (!musicService.isNull()) {
            cP = musicService.getCurrentPosition();
            long mcP = cP;
            seekBar.setProgress(cP);
            if((mcP/1000) >= (duration_total/1000)){
                durationPlayed.setText(formattedText(duration_total / 1000));
            }else{
                durationPlayed.setText(formattedText(mcP / 1000));
            }

            if (durFlip){
                if ((duration_total / 1000)-(mcP / 1000) <= 0){
                    durationTotal.setText("-0:00");
                }else{
                    durationTotal.setText("-" + formattedText((duration_total / 1000)-(mcP / 1000)));
                }

            }
        }
        handler.postDelayed(this, 500);
    }

    @Override
    public void onStartDrag(RecyclerView.ViewHolder viewHolder) {
        mItemTouchHelper.startDrag(viewHolder);
    }

    @Override
    public void onCreateOptionsMenu(@NonNull Menu menu, @NonNull MenuInflater inflater) {
        inflater.inflate(R.menu.queue_menu, menu);
        super.onCreateOptionsMenu(menu, inflater);
    }

    @Override
    public boolean onOptionsItemSelected(@NonNull MenuItem item) {
        if (item.getItemId() == R.id.queue_item1){
            Toast.makeText(context, "Queue Cleared", Toast.LENGTH_SHORT).show();
            musicService.clearQueue();
            musicService.clearMediaPlayer();
            queueAdapter.notifyDataSetChanged();
            hideMiniFromQueuex.onQueueCleared();
            getActivity().getSupportFragmentManager().popBackStackImmediate(null, FragmentManager.POP_BACK_STACK_INCLUSIVE);

        }
        if (item.getItemId() == R.id.queue_newPlaylist){
            Toast.makeText(context, "New Playlist", Toast.LENGTH_SHORT).show();
        }
        if (item.getItemId() == R.id.queue_Favorites){
            Toast.makeText(context, "Added to Favorites", Toast.LENGTH_SHORT).show();
        }
        return super.onOptionsItemSelected(item);
    }


    @Override
    public void onQueueItemClick(Song song) {
        queueAdapter.notifyDataSetChanged();
        setScreen(song);
        queueItemCallback.onQueueItemClick2(song);
    }

    @Override
    public void onQueueChanged() {
        queueItemCallback.onQueueChanged2();
    }

    public interface hideMiniFromQueue{
        void onQueueCleared();
    }

    public interface QueueItemCallback{
        void onQueueItemClick2(Song song2);
        void onQueueChanged2();
    }

    private String formattedText(long mCurrentPosition) {
        String total1 = "";
        String total2 = "";
        String total3 = "";
        String total4 = "";
        String seconds = String.valueOf(mCurrentPosition % 60);
        String minutes = String.valueOf(mCurrentPosition / 60);
        String hours = String.valueOf(mCurrentPosition / 3600);

        if (Integer.parseInt(hours) == 0) {
            total1 = minutes + ":" + seconds;
            total2 = minutes + ":" + "0" + seconds;
            if (seconds.length() == 1) {
                return total2;
            } else {
                return total1;
            }
        } else {
            String minutes2 = String.valueOf((mCurrentPosition / 60) % 60);
            total1 = hours + ":" + minutes2 + ":" + seconds;
            total2 = hours + ":" + minutes2 + ":" + "0" + seconds;
            total3 = hours + ":" + "0" + minutes2 + ":" + seconds;
            total4 = hours + ":" + "0" + minutes2 + ":" + "0" + seconds;

            if (seconds.length() == 1 && minutes2.length() == 1) {
                return total4;
            } else if (seconds.length() != 1 && minutes2.length() == 1) {
                return total3;
            } else if (seconds.length() == 1 && minutes2.length() != 1) {
                return total2;
            } else {
                return total1;
            }
        }

    }

    @Override
    public void onAttach(Activity activity) {
        super.onAttach(activity);
        try {
            hideMiniFromQueuex = (hideMiniFromQueue) activity;
            queueItemCallback = (QueueItemCallback) activity;
            songChangeCallback = (PlayerFragment.SongChangeCallback) activity;
        } catch (ClassCastException e) {
            throw new ClassCastException(activity.toString()
                    + " must implement TextClicked");
        }
    }

    @Override
    public void onDetach() {
        hideMiniFromQueuex = null;
        queueItemCallback = null;
        songChangeCallback = null;
        super.onDetach();
    }

    @Override
    public void onDestroy() {
        super.onDestroy();
        handler.removeCallbacks(this);
        musicService.removeListener(queue_Listener);
        queueOpen = false;
        queueBool = false;
    }


}

