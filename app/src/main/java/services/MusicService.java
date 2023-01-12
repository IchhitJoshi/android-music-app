package services;

import android.app.PendingIntent;
import android.app.Service;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.media.AudioManager;
import android.media.MediaMetadataRetriever;
import android.os.Binder;
import android.os.IBinder;
import android.os.SystemClock;
import android.support.v4.media.MediaMetadataCompat;
import android.support.v4.media.session.MediaControllerCompat;
import android.support.v4.media.session.MediaSessionCompat;
import android.support.v4.media.session.PlaybackStateCompat;
import android.telephony.PhoneStateListener;
import android.telephony.TelephonyManager;
import android.util.Log;

import androidx.annotation.Nullable;
import androidx.core.app.NotificationCompat;

import com.example.demo.R;
import com.example.demo.views.Home;

import java.util.ArrayList;

import managers.PlaybackManager;
import managers.QueueManager;
import models.Album;
import models.AlbumArtist;
import models.Song;
import notifications.NotificationReceiver;
import utils.SharedPref;

import static notifications.ApplicationClass.ACTION_CLOSE;
import static notifications.ApplicationClass.ACTION_NEXT;
import static notifications.ApplicationClass.ACTION_PLAY;
import static notifications.ApplicationClass.ACTION_PREVIOUS;
import static notifications.ApplicationClass.CHANNEL_ID_2;

public class MusicService extends Service implements AudioManager.OnAudioFocusChangeListener{

    private final IBinder iBinder = new MyBinder();

    private PlaybackManager playbackManager;
    private AudioManager audioManager;
    private QueueManager queueManager;
    boolean closed = false;

    private boolean ongoingCall = false;
    private PhoneStateListener phoneStateListener;
    private TelephonyManager telephonyManager;

    ShowNotification xshowNotification;
    MediaSessionCompat mediaSessionCompat;

    PlaybackStateCompat.Builder pBuilder;
    MediaControllerCompat mController;
    MediaControllerCompat.TransportControls transportControls;

    public static boolean shuffle2 = false;

    SharedPref sharedPref;

    boolean wasPlaying;
    boolean focusRequested;


    @Nullable
    @Override
    public IBinder onBind(Intent intent) {
        return iBinder;
    }



    @Override
    public void onCreate() {
        super.onCreate();
        sharedPref = new SharedPref(this);

        queueManager = new QueueManager(sharedPref);
        playbackManager = new PlaybackManager(this, queueManager, sharedPref);

        mediaSessionCompat = new MediaSessionCompat(this, "Music PLAYER 1");

        mediaSessionCompat.setCallback(new MediaSessionCompat.Callback() {
            @Override
            public void onPlay() {
                super.onPlay();
                play();
                if (xshowNotification != null){
                    xshowNotification.updateViewsFromNotification();
                }
            }

            @Override
            public void onPause() {
                super.onPause();
                pause();
                if (xshowNotification != null){
                    xshowNotification.updateViewsFromNotification();
                }
            }

            @Override
            public void onSkipToNext() {
                super.onSkipToNext();
                loadSong(getNextSong());
                play();
                updateMetadata();
                if (xshowNotification != null){
                    xshowNotification.updateViewsFromNotification();
                }
            }

            @Override
            public void onSkipToPrevious() {
                super.onSkipToPrevious();
                loadSong(getPreviousSong());
                play();
                updateMetadata();
                if (xshowNotification != null){
                    xshowNotification.updateViewsFromNotification();
                }
            }

            @Override
            public void onSeekTo(long pos) {
                super.onSeekTo(pos);
                seekTo((int) pos);
                if (isPlaying()){
                    showNotification(R.drawable.pause_button_gray);

                }else{
                    showNotification(R.drawable.play_button_gray);
                }

            }
        });

        mediaSessionCompat.setFlags(MediaSessionCompat.FLAG_HANDLES_MEDIA_BUTTONS | MediaSessionCompat.FLAG_HANDLES_TRANSPORT_CONTROLS);

        pBuilder = new PlaybackStateCompat.Builder();
        mController = new MediaControllerCompat(this, mediaSessionCompat);
        transportControls = mController.getTransportControls();

        playbackManager.loadQueue();

    }




    public void updateMetadata(){
        MediaMetadataRetriever retriever = new MediaMetadataRetriever();
        retriever.setDataSource(getCurrentQueueItem().getSongUrl());
        byte [] art = retriever.getEmbeddedPicture();

        Bitmap thumb;
        if (art != null){
            thumb = BitmapFactory.decodeByteArray(art,0, art.length);

        }else{
            thumb = BitmapFactory.decodeResource(getResources(), R.drawable.common_google_signin_btn_text_light_normal_background);
        }
        mediaSessionCompat.setMetadata(new MediaMetadataCompat.Builder()
                .putString(MediaMetadataCompat.METADATA_KEY_MEDIA_ID, String.valueOf(getCurrentQueueItem().getSongId()))
                .putBitmap(MediaMetadataCompat.METADATA_KEY_ALBUM_ART, thumb)
                .putString(MediaMetadataCompat.METADATA_KEY_ARTIST, getCurrentQueueItem().getArtistName())
                .putString(MediaMetadataCompat.METADATA_KEY_ALBUM_ARTIST, getCurrentQueueItem().getAlbumArtistName())
                .putString(MediaMetadataCompat.METADATA_KEY_TITLE, getCurrentQueueItem().getSongName())
                .putLong(MediaMetadataCompat.METADATA_KEY_DURATION, Long.parseLong(getCurrentQueueItem().getDuration()))
                .build());

    }

    public void setClosed(boolean b) {
        closed = b;
    }

    public void setSessionActive() {
        mediaSessionCompat.setActive(true);
    }

    public class MyBinder extends Binder {
        public MusicService getService() {
            return MusicService.this;
        }
    }

    public void setCallBack(ShowNotification showNotification) {
        xshowNotification = showNotification;
    }

    @Override
    public int onStartCommand(Intent intent, int flags, int startId) {

        String actionName = intent.getStringExtra("ActionName");
        if (actionName!=null){
            switch(actionName){
                case "playPause":
                    playPause();
                    if (xshowNotification != null){
                        xshowNotification.updateViewsFromNotification();
                    }

                    break;

                    case "Next":
                    loadSong(getNextSong());
                    if (xshowNotification != null){
                        xshowNotification.updateViewsFromNotification();
                    }
                    play();
                    break;

                case "Previous":
                    loadSong(getPreviousSong());
                    if (xshowNotification != null){
                        xshowNotification.updateViewsFromNotification();
                    }
                    play();
                    break;

                case "Close":
                    pause();
                    removeNotification();
                    if(closed){
                        playbackManager.clearMediaPlayer();
                        removeAudioFocus();
                        stopSelf();
                        stopService(intent);
                    }

                    break;
            }


        }

        return super.onStartCommand(intent, flags, startId);
    }


    @Override
    public void onAudioFocusChange(int focusChange) {
           switch (focusChange) {
               case AudioManager.AUDIOFOCUS_GAIN:
                   // resume playback

                   if (wasPlaying){
                      if (!isPlaying()){
                           play();
                       }
                      wasPlaying = false;
                   }

                   setVolume(1.0f,1.0f);
                   break;
               case AudioManager.AUDIOFOCUS_LOSS:
                   // Lost focus for an unbounded amount of time: stop playback and release media player
                   pause();
                   clearMediaPlayer();

                   break;
               case AudioManager.AUDIOFOCUS_LOSS_TRANSIENT:
                   // Lost focus for a short time, but we have to stop
                   // playback. We don't release the media player because playback
                   // is likely to resume
                   if (isPlaying()){
                       pause();
                       wasPlaying = true;
                   }

                   break;
               case AudioManager.AUDIOFOCUS_LOSS_TRANSIENT_CAN_DUCK:
                   // Lost focus for a short time, but it's ok to keep playing
                   // at an attenuated level
                   if(isPlaying()){
                       setVolume(0.1f,0.1f);
                   }

                   break;
           }

    }

    public void playBuilder(){
        pBuilder.setActions(PlaybackStateCompat.ACTION_PAUSE | PlaybackStateCompat.ACTION_PLAY_PAUSE | PlaybackStateCompat.ACTION_SKIP_TO_NEXT| PlaybackStateCompat.ACTION_SKIP_TO_PREVIOUS | PlaybackStateCompat.ACTION_SEEK_TO);
        pBuilder.setState(PlaybackStateCompat.STATE_PLAYING, getCurrentPosition(), 1.0f, SystemClock.elapsedRealtime());
        mediaSessionCompat.setPlaybackState(pBuilder.build());
    }

    public void pauseBuilder(){
        pBuilder.setActions(PlaybackStateCompat.ACTION_PLAY | PlaybackStateCompat.ACTION_PLAY_PAUSE  | PlaybackStateCompat.ACTION_SKIP_TO_NEXT| PlaybackStateCompat.ACTION_SKIP_TO_PREVIOUS | PlaybackStateCompat.ACTION_SEEK_TO);
        pBuilder.setState(PlaybackStateCompat.STATE_PAUSED, getCurrentPosition(), 1.0f, SystemClock.elapsedRealtime());
        mediaSessionCompat.setPlaybackState(pBuilder.build());
    }

    public void playNextBuilder(){
        pBuilder.setActions(PlaybackStateCompat.ACTION_SKIP_TO_NEXT);
        pBuilder.setState(PlaybackStateCompat.STATE_SKIPPING_TO_NEXT, getCurrentPosition(), 1.0f);
        mediaSessionCompat.setPlaybackState(pBuilder.build());
    }

    public void playPreviousBuilder(){
        pBuilder.setActions(PlaybackStateCompat.ACTION_SKIP_TO_PREVIOUS);
        pBuilder.setState(PlaybackStateCompat.STATE_SKIPPING_TO_PREVIOUS, getCurrentPosition(), 1.0f);
        mediaSessionCompat.setPlaybackState(pBuilder.build());
    }

    public void seekToBuilder(){
        pBuilder.setActions(PlaybackStateCompat.ACTION_SEEK_TO);
        pBuilder.setState(PlaybackStateCompat.STATE_PLAYING, getCurrentPosition(), 1.0f);
        mediaSessionCompat.setPlaybackState(pBuilder.build());
    }


    public boolean requestAudioFocus() {
        focusRequested = true;
        audioManager = (AudioManager) getSystemService(Context.AUDIO_SERVICE);
        int result = audioManager.requestAudioFocus(this, AudioManager.STREAM_MUSIC, AudioManager.AUDIOFOCUS_GAIN);
        if (result == AudioManager.AUDIOFOCUS_REQUEST_GRANTED) {
            return true;
        }
        return false;
    }

    private void removeAudioFocus() {
        if (focusRequested){
            audioManager.abandonAudioFocus(this);
        }
    }

    private BroadcastReceiver becomingNoisyReceiver = new BroadcastReceiver() {
        @Override
        public void onReceive(Context context, Intent intent) {
            //pause audio on ACTION_AUDIO_BECOMING_NOISY
            pause();

        }
    };

    public void registerBecomingNoisyReceiver() {
        //register after getting audio focus
        IntentFilter intentFilter = new IntentFilter(AudioManager.ACTION_AUDIO_BECOMING_NOISY);
        registerReceiver(becomingNoisyReceiver, intentFilter);
    }


    public void callStateListener() {
        // Get the telephony manager
        telephonyManager = (TelephonyManager) getSystemService(Context.TELEPHONY_SERVICE);
        //Starting listening for PhoneState changes
        phoneStateListener = new PhoneStateListener() {
            @Override
            public void onCallStateChanged(int state, String incomingNumber) {
                switch (state) {
                    //if at least one call exists or the phone is ringing
                    //pause the MediaPlayer
                    case TelephonyManager.CALL_STATE_OFFHOOK:
                    case TelephonyManager.CALL_STATE_RINGING:
                        if (!isNull()) {
                            pause();
                            ongoingCall = true;
                        }
                        break;
                    case TelephonyManager.CALL_STATE_IDLE:
                        // Phone idle. Start playing.
                        if (!isNull()) {
                            if (ongoingCall) {
                                ongoingCall = false;
                                play();
                            }
                        }
                        break;
                }
            }
        };
        // Register the listener with the telephony manager
        // Listen for changes to the device call state.
        telephonyManager.listen(phoneStateListener,
                PhoneStateListener.LISTEN_CALL_STATE);
    }

    @Override
    public void onDestroy() {
        super.onDestroy();

        saveState();

        removeAudioFocus();
        mediaSessionCompat.setActive(false);
        mediaSessionCompat.release();

        playbackManager.clearMediaPlayer();

        Log.d("SERVICE END", "SERVICE END");

    }

    public void unregisterReceiver(){
        unregisterReceiver(becomingNoisyReceiver);
    }

    public void unregisterPhoneStateListener(){
        if (phoneStateListener != null) {
            telephonyManager.listen(phoneStateListener, PhoneStateListener.LISTEN_NONE);
        }
    }

    public void saveState(){
        sharedPref.saveShuffleMode(shuffle2);
        playbackManager.saveState();
        queueManager.saveQueue();

    }

    public boolean loadShuffleMode(){
        shuffle2 = sharedPref.loadShuffleMode();
        return shuffle2;
    }


    public void setCurrentQueueItemPosition(int currentPosition) {
        queueManager.setCurrentQueueItemPosition(currentPosition);

    }

    public int getCurrentQueueItemPosition() {
        return queueManager.getCurrentQueueItemPosition();
    }

    public Song getCurrentQueueItem() {
        return queueManager.getCurrentQueueItem();
    }

    public void setCurrentQueue(ArrayList<Song> songs2) {
        queueManager.setCurrentQueue(songs2);
    }

    public void setSavedShuffledQueue(ArrayList<Song> shuffledQueue){
        queueManager.setSavedShuffledQueue(shuffledQueue);

    }

    public void setSavedNormalQueue(ArrayList<Song> normalQueue){
        queueManager.setSavedNormalQueue(normalQueue);
    }

    public ArrayList<Song> getCurrentQueue(){
        return queueManager.getCurrentQueue();
    }

    public ArrayList<Song> getUnshuffledQueue(){return queueManager.getUnshuffledQueue();};

    public boolean isEmpty(){
        return queueManager.isEmpty();
    }

    public void clearQueue(){
        queueManager.clearQueue();
    }

    public void shuffleQueue(){
        shuffle2 = true;
        queueManager.shuffleQueue();
    }

    public void resetQueue(){
        shuffle2 = false;
        queueManager.resetQueue();
    }

    public void skipToNext() {
        queueManager.skipToNext();
    }

    public void skipToPrevious(){
        queueManager.skipToPrevious();
    }


    public void playNextQueueItem(Song playNextSong){
        queueManager.playNextQueueItem(playNextSong);
    }

    public void removeQueueItem(Song removeSongFromQueue){
        queueManager.removeQueueItem(removeSongFromQueue);
    }

    public void addToQueue(Song addToQueueSong){
        queueManager.addToQueue(addToQueueSong);
    }

    public void shuffleAll(){
        queueManager.shuffleAll();
    }


    public void playAlbumNext(ArrayList<Song> nextAlbumSongs){
        queueManager.playAlbumNext(nextAlbumSongs);
    }

    public void addAlbumToQueue(ArrayList<Song> addAlbumToQueueSongs){
        queueManager.addAlbumToQueue(addAlbumToQueueSongs);
    }

    public Album getCurrentAlbum(ArrayList<Album> _albums, long id){
        return queueManager.getCurrentAlbum(_albums, id);
    }

    public AlbumArtist getCurrentArtist(ArrayList<AlbumArtist> _artists, long id){
        return queueManager.getCurrentArtist(_artists, id);
    }

    public void setAlbumQueue(ArrayList<Album> albums2) {
        queueManager.setAlbumQueue(albums2);
    }

    public void setArtistQueue(ArrayList<AlbumArtist> artists2) {
        queueManager.setArtistQueue(artists2);
    }


    public String getQueueDuration(Context context){
       return queueManager.getQueueDuration(context);
    }

    public void updateQueue(ArrayList<Song> queue2, int fromPosition, int toPosition){
       queueManager.updateQueue(queue2, fromPosition, toPosition);
    }

    public PlaybackManager.Listener addListener(PlaybackManager.Listener listener){
        return playbackManager.addListener(listener);
    }

    public void removeListener(PlaybackManager.Listener listener){
        playbackManager.removeListener(listener);
    }

    public void loadSong(Song song) {
        playbackManager.loadSong(song);
    }

    public void playPause() {
        playbackManager.playPause();

    }

    public void play() {
        playbackManager.play();
    }

    public void pause() {
        playbackManager.pause();
    }

    public boolean isPlaying(){
        return playbackManager.isPlaying();
    }

    public Song getNextSong(){
        return playbackManager.getNextSong();
    }

    public Song getPreviousSong(){
        return playbackManager.getPreviousSong();
    }

    public int getCurrentPosition() {
        return playbackManager.getCurrentPosition();
    }

    public void seekTo(int progress){
        playbackManager.seekTo(progress);
        seekToBuilder();
    }

    public boolean isNull(){
       return playbackManager.isNull();
    }


    public void clearMediaPlayer(){
        playbackManager.clearMediaPlayer();

    }

    public void setVolume(float l,float r){
        playbackManager.setVolume(l,r);
    }



    public void showNotification(int playPauseBtn){
//        boolean onGoing;
//        if(playPauseBtn == R.drawable.pause_button_gray) {
//            onGoing = true;
//        }else{
//            onGoing = false;
//        }

        long duration_total = Long.parseLong(getCurrentQueueItem().getDuration());
        int dur = (int) duration_total;

        int cP = 0;
        if(!isNull() && cP < dur) {
            cP = getCurrentPosition();
        }

        Intent intent = new Intent(this, Home.class);
        intent.putExtra("Opened from Notification", true);
        intent.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP | Intent.FLAG_ACTIVITY_SINGLE_TOP);
        PendingIntent contentIntent = PendingIntent.getActivity(this,0, intent, PendingIntent.FLAG_UPDATE_CURRENT);


        Intent prevIntent = new Intent(this, NotificationReceiver.class)
                .setAction(ACTION_PREVIOUS);
        PendingIntent prevPending = PendingIntent.getBroadcast(this,0, prevIntent,PendingIntent.FLAG_UPDATE_CURRENT);

        Intent pauseIntent = new Intent(this, NotificationReceiver.class)
                .setAction(ACTION_PLAY);
        PendingIntent pausePending = PendingIntent.getBroadcast(this,0, pauseIntent,PendingIntent.FLAG_UPDATE_CURRENT);

        Intent nextIntent = new Intent(this, NotificationReceiver.class)
                .setAction(ACTION_NEXT);
        PendingIntent nextPending = PendingIntent.getBroadcast(this,0, nextIntent,PendingIntent.FLAG_UPDATE_CURRENT);

        Intent closeIntent = new Intent(this, NotificationReceiver.class).setAction(ACTION_CLOSE);
        PendingIntent closePending = PendingIntent.getBroadcast(this, 0, closeIntent, PendingIntent.FLAG_UPDATE_CURRENT);

        MediaMetadataRetriever retriever = new MediaMetadataRetriever();
        retriever.setDataSource(getCurrentQueueItem().getSongUrl());
        byte [] art = retriever.getEmbeddedPicture();

        Bitmap thumb;
        if (art != null){
            thumb = BitmapFactory.decodeByteArray(art,0, art.length);

        }else{
            thumb = BitmapFactory.decodeResource(getResources(), R.drawable.common_google_signin_btn_text_light_normal_background);
        }

//        Notification notification = new NotificationCompat.Builder(this, CHANNEL_ID_2)
//                .setSmallIcon(R.drawable.outline_audiotrack_black_24dp)
//                .setLargeIcon(thumb)
//                .setContentTitle(getCurrentQueueItem().name)
//                .setContentText(getCurrentQueueItem().artistName)
//                .addAction(R.drawable.previous_button_gray, "Previous", prevPending)
//                .addAction(playPauseBtn, "Pause", pausePending)
//                .addAction(R.drawable.next_button_gray, "Next", nextPending)
//                .setStyle(new androidx.media.app.NotificationCompat.MediaStyle()
//                        .setMediaSession(mediaSessionCompat.getSessionToken()
//                        )
//
//                        .setShowActionsInCompactView(0, 1, 2))
//                .setPriority(NotificationCompat.PRIORITY_LOW)
//                .setOnlyAlertOnce(true)
//                .setShowWhen(false)
//                .setColor(getResources().getColor(R.color.colorAccent))
//                .setVisibility(NotificationCompat.VISIBILITY_PUBLIC)
//                .setOngoing(false)
//                .setContentIntent(contentIntent)
//                .setProgress(dur, cP, false)
//                .build();

         NotificationCompat.Builder notification = new NotificationCompat.Builder(this, CHANNEL_ID_2)
                .setSmallIcon(R.drawable.outline_audiotrack_black_24dp)
                .setLargeIcon(thumb)
                .setContentTitle(getCurrentQueueItem().getSongName())
                .setContentText(getCurrentQueueItem().getArtistName())
                .addAction(R.drawable.previous_button_gray, "Previous", prevPending)
                .addAction(playPauseBtn, "Pause", pausePending)
                .addAction(R.drawable.next_button_gray, "Next", nextPending)
                .addAction(R.drawable.ic_close_black_24dp, "Close", closePending)
                .setStyle(new androidx.media.app.NotificationCompat.MediaStyle()
                        .setMediaSession(mediaSessionCompat.getSessionToken()
                        )
                        .setShowCancelButton(true)
                        .setShowActionsInCompactView(0, 1, 2))
                .setPriority(NotificationCompat.PRIORITY_LOW)
                .setOnlyAlertOnce(true)
                .setShowWhen(false)
                .setColor(getResources().getColor(R.color.colorAccent))
                .setVisibility(NotificationCompat.VISIBILITY_PUBLIC)
                .setOngoing(true)
                .setContentIntent(contentIntent);





//        NotificationManager notificationManager = (NotificationManager) getSystemService(NOTIFICATION_SERVICE);
//        notificationManager.notify(0,notification);

        startForeground(2, notification.build());




    }

    public void removeNotification(){
        stopForeground(true);

    }


    public interface ShowNotification{
        void updateViewsFromNotification();
    }



}
