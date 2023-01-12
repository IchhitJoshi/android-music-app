package com.example.demo.views;

import android.app.Activity;
import android.app.Dialog;
import android.content.Context;
import android.os.Build;
import android.os.Bundle;

import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;
import androidx.fragment.app.FragmentManager;
import androidx.fragment.app.FragmentTransaction;
import androidx.viewpager2.widget.ViewPager2;

import android.os.Handler;
import android.os.Looper;
import android.text.format.DateFormat;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;
import android.view.Window;
import android.view.WindowManager;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.PopupMenu;
import android.widget.SeekBar;
import android.widget.TextView;
import android.widget.Toast;


import services.MusicService;
import com.example.demo.R;
import com.example.demo.views.MusicFragmentTabs.AlbumsDetailsFragment;
import com.example.demo.views.MusicFragmentTabs.ArtistsDetailsFragment;

import org.jmusixmatch.MusixMatch;
import org.jmusixmatch.MusixMatchException;
import org.jmusixmatch.entity.lyrics.Lyrics;
import org.jmusixmatch.entity.track.Track;
import org.jmusixmatch.entity.track.TrackData;

import adapters.AlbumArtAdapter;
import managers.PlaybackManager;
import managers.MediaImporter;
import models.Song;

import java.util.Date;
import java.util.Objects;

import static com.example.demo.views.Home.albumDetailsOpen;
import static com.example.demo.views.Home.artistDetailsOpen;
import static com.example.demo.views.Home.durFlip;
import static com.example.demo.views.Home.loop;
import static com.example.demo.views.Home.nightMode;
import static com.example.demo.views.Home.playerOpen;
import static com.example.demo.views.Home.queueBool;
import static com.example.demo.views.Home.repeat;
import static com.example.demo.views.Home.shuffle;


/**
 * A simple {@link Fragment} subclass.
 */
public class PlayerFragment extends Fragment implements Runnable, AlbumArtAdapter.AlbumArtDoubleTap {

    private SongChangeCallback songChangeCallback;

    private TextView songName, artistName, durationPlayed, durationTotal, songInfo, albumName;
    private SeekBar seekBar;
    private Button playButton, nextButton, previousButton, shuffleButton, repeatButton, favoriteButton, queueButton;
    private ImageView dropDown;

    ViewPager2 albumArtViewPager;
    AlbumArtAdapter albumArtAdapter;


    MusicService musicService;
    MediaImporter mediaImporter;
    Window window;

    private Song song;

    Context context;
    Handler handler;

    boolean onSongChanged;

    private PlaybackManager.Listener player_listener;


    public PlayerFragment(Context context) {
        this.context = context;
        try {
            songChangeCallback = (SongChangeCallback) context;
        } catch (ClassCastException ignored) {
        }
    }

    @Override
    public void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        playerOpen = true;
    }



    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        final View view = inflater.inflate(R.layout.fragment_player, container, false);

        handler = new Handler(Looper.getMainLooper());


        if(Build.VERSION.SDK_INT>=21){
            window = getActivity().getWindow();
            window.setStatusBarColor(getResources().getColor(R.color.background));
            window.clearFlags(WindowManager.LayoutParams.FLAG_LAYOUT_NO_LIMITS);
            View decor = window.getDecorView();
            if(nightMode){
                window.clearFlags(View.SYSTEM_UI_FLAG_LIGHT_STATUS_BAR);
            }else{
                decor.setSystemUiVisibility(View.SYSTEM_UI_FLAG_LIGHT_STATUS_BAR);
            }

        }

        musicService = ((Home) (getActivity())).getMusicService();
        mediaImporter = ((Home) (getActivity())).getMediaImporter();

        player_listener = musicService.addListener(new PlaybackManager.Listener() {
            @Override
            public void onSongChanged(Song newSong) {
                durationPlayed.setText(formattedText(0));
                onSongChanged = true;
                setScreen(newSong, true);

            }
        });

        dropDown = view.findViewById(R.id.dropDownButton);
        songName = view.findViewById(R.id.current_song);
        artistName = view.findViewById(R.id.current_artist);
        albumName = view.findViewById(R.id.current_album);
        seekBar = view.findViewById(R.id.songProgressBar);
        playButton = view.findViewById(R.id.pausePlayButton);
        nextButton = view.findViewById(R.id.playNextButton);
        previousButton = view.findViewById(R.id.playPrevButton);
        shuffleButton = view.findViewById(R.id.shuffleButton);
        favoriteButton = view.findViewById(R.id.favoritesButton);
        queueButton = view.findViewById(R.id.queueButton);
        repeatButton = view.findViewById(R.id.repeatButton);
        durationPlayed = view.findViewById(R.id.durationPlayed);
        durationTotal = view.findViewById(R.id.durationTotal);
        songInfo = view.findViewById(R.id.getInfo);
        albumArtViewPager = view.findViewById(R.id.album_art_viewpager);

        playButton.getBackground().setTint(getResources().getColor(R.color.mainTextColor));
        nextButton.getBackground().setTint(getResources().getColor(R.color.mainTextColor));
        previousButton.getBackground().setTint(getResources().getColor(R.color.mainTextColor));
        shuffleButton.getBackground().setTint(getResources().getColor(R.color.mainTextColor));
        repeatButton.getBackground().setTint(getResources().getColor(R.color.mainTextColor));
        favoriteButton.getBackground().setTint(getResources().getColor(R.color.mainTextColor));
        queueButton.getBackground().setTint(getResources().getColor(R.color.mainTextColor));
        dropDown.getDrawable().setTint(getResources().getColor(R.color.mainTextColor));

        song = musicService.getCurrentQueueItem();

        albumArtAdapter = new AlbumArtAdapter(context, musicService,PlayerFragment.this);
        albumArtViewPager.setAdapter(albumArtAdapter);

        albumArtViewPager.setClipToPadding(false);
        albumArtViewPager.setClipChildren(false);
        albumArtViewPager.getChildAt(0).setOverScrollMode(View.OVER_SCROLL_NEVER);
        albumArtViewPager.setOffscreenPageLimit(3);

        setScreen(song, false);
        updateProgress();
        checkButtons();

        albumArtViewPager.registerOnPageChangeCallback(new ViewPager2.OnPageChangeCallback() {
            @Override
            public void onPageScrolled(int position, float positionOffset, int positionOffsetPixels) {
                super.onPageScrolled(position, positionOffset, positionOffsetPixels);
                Log.e("Selected_Page", String.valueOf(albumArtViewPager.getScrollState()));
                int currentQueueItemPosition = musicService.getCurrentQueueItemPosition();
                if (position == (currentQueueItemPosition + 1) % musicService.getCurrentQueue().size() && !onSongChanged){
                    playNextSong();

                }else if (position == ((currentQueueItemPosition - 1) < 0 ? (musicService.getCurrentQueue().size() - 1) : (currentQueueItemPosition -1)) && positionOffset < 0.2 && !onSongChanged){
                    playPreviousSong();
//                    if (albumArtViewPager.getScrollState() == ViewPager2.SCROLL_STATE_SETTLING){
//
//
//                    }

                }

                if (onSongChanged){
                    onSongChanged = false;
                }

            }

//            @Override
//            public void onPageSelected(int position) {
//                super.onPageSelected(position);
//                if (position == queueManager.getCurrentPosition() + 1){
//                    playNextSong();
//
//                }else if (position == queueManager.getCurrentPosition() - 1){
//                    playPreviousSong();
//                }
//
//            }
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
                albumArtAdapter.notifyDataSetChanged();
                albumArtViewPager.setCurrentItem(musicService.getCurrentQueueItemPosition(), false);

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

        favoriteButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (!song.getFavorite()){
                    favoriteButton.setBackgroundResource(R.drawable.favorite_on);
                    song.setFavorite(true);
                }else{
                    favoriteButton.setBackgroundResource(R.drawable.favorite_off);
                    song.setFavorite(false);
                }
            }
        });

        queueButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                queueBool = true;
                QueueFragment queueFragment = new QueueFragment(context);
                FragmentManager manager = Objects.requireNonNull(getActivity()).getSupportFragmentManager();
                FragmentTransaction transaction = manager.beginTransaction();
                transaction.add(R.id.Player_main,queueFragment).addToBackStack(null).commit();
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

        durationTotal.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (durFlip){
                    long duration_total = Long.parseLong(musicService.getCurrentQueueItem().getDuration());
                    durationTotal.setText(formattedText(duration_total / 1000));
                    durFlip = false;
                }else{
                    durFlip = true;
                }
            }
        });

        nextButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
//                playNextSong();
                albumArtViewPager.setCurrentItem((musicService.getCurrentQueueItemPosition() + 1) % musicService.getCurrentQueue().size(), true);

            }
        });

        previousButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                playPreviousSong();
//                albumArtViewPager.setCurrentItem(queueManager.getCurrentPosition() - 1, true);
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

        dropDown.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                playerOpen = false;
                queueBool = false;

                getActivity().getSupportFragmentManager().popBackStack();

                if (albumDetailsOpen || artistDetailsOpen){
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

                }

            }
        });



        songInfo.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                PopupMenu popupMenu = new PopupMenu(context, songInfo);
                popupMenu.inflate(R.menu.player_menu);

                popupMenu.setOnMenuItemClickListener(new PopupMenu.OnMenuItemClickListener() {
                    @Override
                    public boolean onMenuItemClick(MenuItem item) {

                        switch (item.getItemId()){

                            case R.id.player_item2:

                                Dialog songInfoDialog = new Dialog(context);

                                songInfoDialog.setContentView(R.layout.song_info);
                                songInfoDialog.show();

                                ImageView songInfoClose = songInfoDialog.findViewById(R.id.songInfoClose);
                                TextView songName = songInfoDialog.findViewById(R.id.info_song_name);
                                TextView artistName = songInfoDialog.findViewById(R.id.info_artist_name);
                                TextView albumName = songInfoDialog.findViewById(R.id.info_album_name);
                                TextView albumArtistName = songInfoDialog.findViewById(R.id.info_album_artist_name);
                                TextView track = songInfoDialog.findViewById(R.id.info_track_number);
                                TextView duration = songInfoDialog.findViewById(R.id.info_duration);
                                TextView disc = songInfoDialog.findViewById(R.id.info_disc);
                                TextView year = songInfoDialog.findViewById(R.id.info_year);
                                TextView fileSize = songInfoDialog.findViewById(R.id.info_file_size);
                                TextView bitRate = songInfoDialog.findViewById(R.id.info_bitrate);
                                TextView sampleRate = songInfoDialog.findViewById(R.id.info_samplerate);
                                TextView songId = songInfoDialog.findViewById(R.id.info_songId);
                                TextView artistId = songInfoDialog.findViewById(R.id.info_artistId);
                                TextView albumId = songInfoDialog.findViewById(R.id.info_albumId);
                                TextView formatLabel = songInfoDialog.findViewById(R.id.info_format);
                                TextView path  = songInfoDialog.findViewById(R.id.info_path);




                                songName.setText(song.getSongName());
                                artistName.setText(song.getArtistName());
                                albumName.setText(song.getAlbumName());
                                albumArtistName.setText(song.getAlbumArtistName());
                                track.setText(song.getTrackNumber() + "");
                                duration.setText(song.getDurationLabel(context));
                                disc.setText(song.getDiscNumber() + "");
                                year.setText(song.getSongYear() + "");
                                fileSize.setText(song.getFileSizeLabel());
                                bitRate.setText(song.getBitrateLabel(context));
                                sampleRate.setText(song.getSampleRateLabel(context));
                                songId.setText(song.getSongId());
                                artistId.setText(String.valueOf(song.getArtistId()));
                                albumId.setText(String.valueOf(song.getAlbumId()));
                                formatLabel.setText(song.getMimeType());
                                path.setText(song.getSongUrl());



                                songInfoClose.setOnClickListener(new View.OnClickListener() {
                                    @Override
                                    public void onClick(View v) {
                                        songInfoDialog.dismiss();
                                    }
                                });
                                break;
                            case R.id.player_item3:
                                Toast.makeText(context, "Edit Song Info", Toast.LENGTH_LONG).show();
                                break;

                            case R.id.player_item6:
                                Toast.makeText(context, "Add to Playlist", Toast.LENGTH_LONG).show();
                                break;
                            case R.id.player_item7:
                                Toast.makeText(context, "Sleep Timer", Toast.LENGTH_LONG).show();
                                break;
                            case R.id.player_item8:
                                Toast.makeText(context, "Lyrics", Toast.LENGTH_LONG).show();

//                                try {
//                                    track = musixMatch.getMatchingTrack(song.getSongName(), song.getArtistName());
//                                } catch (MusixMatchException e) {
//                                    e.printStackTrace();
//                                }
//
//                                if(track != null){
//                                    data = track.getTrack();
//
//                                }
//
//                                if(data != null){
//                                    Log.d("LYRICS", "HELLO");
//                                    int trackID = data.getTrackId();
//                                    try {
//                                       lyrics = musixMatch.getLyrics(trackID);
//                                    } catch (MusixMatchException e) {
//                                        e.printStackTrace();
//                                    }
//                                    Log.d("LYRICS", lyrics.getLyricsBody());
//
//
//                                }





                                break;
                            case R.id.player_item9:
                                Toast.makeText(context, "Delete", Toast.LENGTH_LONG).show();
                                break;
                            case R.id.goToArtist:
                                Toast.makeText(context, "Go to Artist", Toast.LENGTH_LONG).show();
                                break;
                            case R.id.goToAlbum:
                                Toast.makeText(context, "Go to Album", Toast.LENGTH_LONG).show();
                                playerOpen = false;
                                queueBool = false;
                                getActivity().getSupportFragmentManager().popBackStack();
                                AlbumsDetailsFragment albumsDetailsFragment = new AlbumsDetailsFragment(context, musicService.getCurrentAlbum(mediaImporter.getAlbums(), song.getAlbumId()));
                                FragmentManager manager2 = Objects.requireNonNull(getActivity()).getSupportFragmentManager();
                                FragmentTransaction transaction2 = manager2.beginTransaction();
                                transaction2.replace(R.id.mainFrame, albumsDetailsFragment).addToBackStack(null).commit();

                                break;
                            case R.id.goToGenre:
                                Toast.makeText(context, "Go to Genre", Toast.LENGTH_LONG).show();
                                break;
                            default:
                                break;
                        }
                        return false;
                    }
                });
                popupMenu.show();
            }
        });

        albumName.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                playerOpen = false;
                queueBool = false;
                getActivity().getSupportFragmentManager().popBackStack();
                AlbumsDetailsFragment albumsDetailsFragment = new AlbumsDetailsFragment(context, musicService.getCurrentAlbum(mediaImporter.getAlbums(), song.getAlbumId()));
                FragmentManager manager2 = Objects.requireNonNull(getActivity()).getSupportFragmentManager();
                FragmentTransaction transaction2 = manager2.beginTransaction();
                transaction2.replace(R.id.mainFrame, albumsDetailsFragment).addToBackStack(null).commit();
            }
        });

        artistName.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                playerOpen = false;
                queueBool = false;
                getActivity().getSupportFragmentManager().popBackStack();
                ArtistsDetailsFragment artistsDetailsFragment = new ArtistsDetailsFragment(context, musicService.getCurrentArtist(mediaImporter.getArtists(), song.getArtistId()));
                FragmentManager manager2 = Objects.requireNonNull(getActivity()).getSupportFragmentManager();
                FragmentTransaction transaction2 = manager2.beginTransaction();
                transaction2.replace(R.id.mainFrame, artistsDetailsFragment).addToBackStack(null).commit();
            }
        });

//       OnBackPressedCallback callback = new OnBackPressedCallback(true) {
//            @Override
//            public void handleOnBackPressed() {
//                playerOpen = false;
//                queueBool = false;
//                Objects.requireNonNull(getActivity()).getSupportFragmentManager().popBackStack();
//
//            }
//        };
//        requireActivity().getOnBackPressedDispatcher().addCallback(this, callback);
        return view;
    }

    void checkButtons() {
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

        if (song.getFavorite()){
            favoriteButton.setBackgroundResource(R.drawable.favorite_on);
        }else{
            favoriteButton.setBackgroundResource(R.drawable.favorite_off);
        }
    }

    void setScreen(Song song, boolean x) {
        long duration_total = Long.parseLong(song.getDuration());
        if (!durFlip){
            durationTotal.setText(formattedText(duration_total / 1000));
        }
        int dur = (int) duration_total;
        seekBar.setMax(dur);
        songName.setText(song.getSongName());
        artistName.setText(song.getArtistName());
        albumName.setText(song.getAlbumName());
        playButton.setBackgroundResource(R.drawable.pause_button_gray);
        if (song.getFavorite()){
            favoriteButton.setBackgroundResource(R.drawable.favorite_on);
        }else{
            favoriteButton.setBackgroundResource(R.drawable.favorite_off);
        }

        albumArtViewPager.setCurrentItem((musicService.getCurrentQueueItemPosition()), x);

    }

    private void playSong() {
        musicService.playPause();
    }

    private void playNextSong() {
        song = musicService.getNextSong();
        musicService.loadSong(song);
        setScreen(song, true);
        musicService.play();
        songChangeCallback.onNextSong(song);
    }

    private void playPreviousSong() {
        song = musicService.getPreviousSong();
        musicService.loadSong(song);
        setScreen(song, true);
        musicService.play();
        songChangeCallback.onNextSong(song);
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
    public void run() {
        updateProgress();
    }

    private void updateProgress() {
        if(musicService.getCurrentQueueItem() != null){
            int cP = 0;
            long duration_total = Long.parseLong(musicService.getCurrentQueueItem().getDuration());
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

    }

    @Override
    public void onAlbumArtDoubleTap() {
        favoriteButton.setBackgroundResource(R.drawable.favorite_on);
    }


    public interface SongChangeCallback {
        void onNextSong(Song song);
    }

    @Override
    public void onAttach(Activity activity) {
        super.onAttach(activity);
        try {
            songChangeCallback = (SongChangeCallback) activity;
        } catch (ClassCastException e) {
            throw new ClassCastException(activity.toString()
                    + " must implement TextClicked");
        }
    }

    @Override
    public void onDetach() {
        songChangeCallback = null;
        super.onDetach();
    }

    @Override
    public void onDestroy() {
        super.onDestroy();
        handler.removeCallbacks(this);
        musicService.removeListener(player_listener);
        playerOpen = true;
    }


}




