package managers;

import android.content.Context;
import android.media.AudioManager;
import android.media.MediaPlayer;
import android.net.Uri;
import android.os.PowerManager;
import android.provider.MediaStore;
import android.util.Log;

import com.example.demo.R;

import java.io.File;
import java.io.IOException;
import java.util.ArrayList;

import models.Song;
import utils.SharedPref;

import static com.example.demo.views.Home.loop;
import static com.example.demo.views.Home.musicService;
import static com.example.demo.views.Home.repeat;
import static com.example.demo.views.Home.serviceBound;

public class PlaybackManager{

    private final Context context;

    private MediaPlayer mediaPlayer;
    private final QueueManager queueManager;
    private ArrayList<Song> songs = new ArrayList<Song>();
    private final ArrayList<Listener> _listeners = new ArrayList<Listener>();

    private final int position = 0;
    private Song newSong;
    private Listener listener;
    int seekPosition = 0;

    SharedPref sharedPref;



    public PlaybackManager(Context context, final QueueManager queueManager, SharedPref mSharedPref) {

        this.context = context;
        this.queueManager = queueManager;
        this.sharedPref = mSharedPref;

        mediaPlayer = new MediaPlayer();
        mediaPlayer.setWakeMode(context, PowerManager.PARTIAL_WAKE_LOCK);
        mediaPlayer.setAudioStreamType(AudioManager.STREAM_MUSIC);

        mediaPlayer.setOnErrorListener(new MediaPlayer.OnErrorListener() {
            @Override
            public boolean onError(MediaPlayer mp, int what, int extra) {
                switch (what) {
                    case MediaPlayer.MEDIA_ERROR_NOT_VALID_FOR_PROGRESSIVE_PLAYBACK:
                        Log.d("MediaPlayer Error", "MEDIA ERROR NOT VALID FOR PROGRESSIVE PLAYBACK " + extra);
                        break;
                    case MediaPlayer.MEDIA_ERROR_SERVER_DIED:
                        Log.d("MediaPlayer Error", "MEDIA ERROR SERVER DIED " + extra);
                        break;
                    case MediaPlayer.MEDIA_ERROR_UNKNOWN:
                        Log.d("MediaPlayer Error", "MEDIA ERROR UNKNOWN " + extra);
                        break;
                }
                return false;
            }
        });

        mediaPlayer.setOnCompletionListener(new MediaPlayer.OnCompletionListener() {
            @Override
            public void onCompletion(MediaPlayer mp) {
                if(repeat && !loop){
                    if (_listeners.size() > 0) {
                        newSong = getNextSong();
                        for (Listener listener : _listeners) {
                            listener.onSongChanged(newSong);
                        }
                        loadSong(newSong);
                        play();
                    }else{
                        newSong = getNextSong();
                        loadSong(newSong);
                        play();
                    }
                }else if(repeat && loop){
                    if (_listeners.size()>0) {
                        newSong = getCurrentQueueItem();
                        for (Listener listener : _listeners) {
                            listener.onSongChanged(newSong);
                        }
                        loadSong(newSong);
                        play();
                    }else{
                        newSong = getCurrentQueueItem();
                        loadSong(newSong);
                        play();
                    }
                }
                else {
                    if(getCurrentQueueItemPosition() + 1 == queueManager.getUnshuffledQueue().size()){
                        pause();
                    }else {
                        if (_listeners.size()>0) {
                            newSong = getNextSong();
                            for (Listener listener : _listeners) {
                                listener.onSongChanged(newSong);
                            }
                            loadSong(newSong);
                            play();
                        }else{
                            newSong = getNextSong();
                            loadSong(newSong);
                            play();
                        }

                    }
                }
            }
        });


    }



    public interface Listener{
        void onSongChanged(Song newSong);
    }


    public Listener addListener(Listener listener){
        this.listener = listener;
        _listeners.add(listener);
        return listener;
    }

    public void removeListener(Listener listener){
        this.listener = listener;
        _listeners.remove(listener);
    }

    public void saveState(){
        if(mediaPlayer != null){
            sharedPref.saveCurrentQueueItemSongPosition(getCurrentPosition());

        }
    }

    public void loadQueue(){
        queueManager.loadQueue();
        loadSong(queueManager.getCurrentQueueItem());
        seekTo(sharedPref.loadCurrentQueueItemSongPosition());
    }


    public int getCurrentQueueItemPosition(){
        return queueManager.getCurrentQueueItemPosition();
    }


    public Song getCurrentQueueItem(){
        return queueManager.getCurrentQueueItem();
    }

    public void loadSong(Song song) {

        if (song != null){
            Uri uri = Uri.parse(MediaStore.Audio.Media.EXTERNAL_CONTENT_URI.toString() + File.separator + song.getSongId());

            if (mediaPlayer != null) {
                mediaPlayer.stop();
                mediaPlayer.reset();
            }else{
                mediaPlayer = new MediaPlayer();
            }

            try {
                mediaPlayer.setDataSource(context, uri);
                mediaPlayer.prepare();

            } catch (IOException e) {
                e.printStackTrace();
            }

        }

    }

    public void playPause() {
        if (mediaPlayer != null){
            if (mediaPlayer.isPlaying()) {
                pause();
            } else {
                if(getCurrentQueueItemPosition()+1 == queueManager.getUnshuffledQueue().size() && !loop && !repeat && !(getCurrentPosition() < Integer.parseInt(queueManager.getCurrentQueueItem().getDuration())) ){
                    newSong = getNextSong();
                    for (Listener listener : _listeners) {
                        listener.onSongChanged(newSong);
                    }
                    loadSong(newSong);
                }
                play();
            }

        }else{
            if (!queueManager.isEmpty()){
                loadSong(queueManager.getCurrentQueueItem());
                seekTo(seekPosition);
                playPause();
            }

        }

    }

    public void play() {
        if (!musicService.requestAudioFocus()){
            if (serviceBound) {
                pause();


            }

        } else{
            mediaPlayer.start();
            if(musicService != null){
                musicService.setSessionActive();
                musicService.updateMetadata();
                musicService.registerBecomingNoisyReceiver();
//                musicService.callStateListener();
                musicService.playBuilder();
//                musicService.showNotification(R.drawable.pause_button_gray);

            }

//            musicService.playPauseClick();
        }

    }

    public void pause() {
        if(isPlaying()){
            musicService.unregisterReceiver();
            musicService.unregisterPhoneStateListener();
            musicService.pauseBuilder();
        }
        mediaPlayer.pause();
        if(musicService != null){
            musicService.showNotification(R.drawable.play_button_gray);
        }

//        musicService.playPauseClick();



    }

    public boolean isPlaying(){
        if( mediaPlayer == null){
            return false;
        }

        return mediaPlayer != null & mediaPlayer.isPlaying();
    }

    public Song getNextSong(){
        queueManager.skipToNext();
        musicService.playNextBuilder();
        musicService.updateMetadata();
        return queueManager.getCurrentQueueItem();

    }


    public Song getPreviousSong(){
        queueManager.skipToPrevious();
        musicService.playPreviousBuilder();
        musicService.updateMetadata();
        return queueManager.getCurrentQueueItem();
    }

    public int getCurrentPosition() {
        return mediaPlayer.getCurrentPosition();
    }

    public void seekTo(int progress){
        mediaPlayer.seekTo(progress);
        if (musicService != null){
            musicService.seekToBuilder();
        }

    }

    public boolean isNull(){
        return mediaPlayer == null;
    }


    public void clearMediaPlayer(){
        try {
            if (mediaPlayer != null) {
                if (mediaPlayer.isPlaying()){
                    mediaPlayer.stop();
                    mediaPlayer.release();
                    mediaPlayer = null;
                }

            }
        } catch (Exception e) {
            e.printStackTrace();
        }

    }

    public void setVolume(float l,float r){
        mediaPlayer.setVolume(l,r);
    }

}
