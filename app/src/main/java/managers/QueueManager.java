package managers;

import android.content.Context;
import android.util.Log;

import utils.StringUtils;

import java.util.ArrayList;
import java.util.Collections;

import models.Album;
import models.AlbumArtist;
import models.Song;
import utils.SharedPref;

import static com.example.demo.views.Home.shuffle;

public class QueueManager {

    private int currentQueueItemPosition = -1;

    private final ArrayList<Song> currentQueue = new ArrayList<Song>();
    private ArrayList<Song> unshuffledQueue = new ArrayList<Song>();

    private ArrayList<Album> albums = new ArrayList<Album>();

    private Album album = new Album();
    private AlbumArtist artist = new AlbumArtist();

    private int originalPosition = -1;

    SharedPref sharedPref;

    public QueueManager(SharedPref mSharedPref) {
        this.sharedPref = mSharedPref;
    }


    public void clearQueue(){
        currentQueue.clear();
        unshuffledQueue.clear();

        currentQueueItemPosition = -1;
        originalPosition = -1;

        saveQueue();

    }

    public boolean isEmpty(){
        return currentQueue.isEmpty();
    }

    public void setCurrentQueue(ArrayList<Song> songs){

        if(!unshuffledQueue.equals(songs)){
            unshuffledQueue.clear();
            currentQueue.clear();

            unshuffledQueue.addAll(songs);
            currentQueue.addAll(unshuffledQueue);

        }

        currentQueue.clear();
        currentQueue.addAll(unshuffledQueue);


        if (shuffle) {
            originalPosition = getCurrentQueueItemPosition();
            setCurrentQueueItemPosition(0);
            RandomizeQueue(currentQueue);

        }

        saveQueue();


    }

    public void setSavedShuffledQueue(ArrayList<Song> shuffledQueue){
        this.currentQueue.clear();
        this.currentQueue.addAll(shuffledQueue);

    }

    public void setSavedNormalQueue(ArrayList<Song> normalQueue){
//      if (unshuffledQueue != null && normalQueue != null){
          this.unshuffledQueue.clear();
          this.unshuffledQueue.addAll(normalQueue);
//      }


    }

    public void setAlbumQueue(ArrayList<Album> albums2) {
        ArrayList<Song> temp = new ArrayList<>();
        temp.clear();

        for(int i = 0; i < albums2.size(); i++){
            temp.addAll(albums2.get(i).getAlbumSongs());
        }

        setCurrentQueue(temp);

    }

    public void setArtistQueue(ArrayList<AlbumArtist> artists2) {
        ArrayList<Song> temp = new ArrayList<>();
        temp.clear();

        for(int i = 0; i < artists2.size(); i++){
            temp.addAll(artists2.get(i).getSongsByArtist());
        }

        setCurrentQueue(temp);

    }



    public void shuffleQueue()  {
        setCurrentQueue(unshuffledQueue);
    }

    public void resetQueue(){
        Log.d("CurrentQueueItem", String.valueOf(unshuffledQueue.size()));
        Log.d("CurrentQueueItem", String.valueOf(currentQueue.size()));
        Song song = getCurrentQueueItem();
        Log.d("CurrentQueueItem", song.getSongName());
        Log.d("CurrentQueueItem", String.valueOf(unshuffledQueue.size()));
        Log.d("CurrentQueueItem", String.valueOf(unshuffledQueue.indexOf(song)));
        for (int i = 0; i < unshuffledQueue.size(); i++){
            if(unshuffledQueue.get(i).getSongId().equals(song.getSongId())){
                setCurrentQueueItemPosition(i);
            }

        }
//        setCurrentQueueItemPosition(unshuffledQueue.indexOf(song));
        reset();

    }

    private void reset() {
        currentQueue.clear();
        currentQueue.addAll(unshuffledQueue);
    }

    public void setCurrentQueueItemPosition(int i){
        this.currentQueueItemPosition = i;
    }


    public ArrayList<Song> getCurrentQueue() {
        return currentQueue;
    }

    public ArrayList<Song> getUnshuffledQueue(){return unshuffledQueue;}

    public int getCurrentQueueItemPosition(){
        return currentQueueItemPosition;
    }

    public Song getCurrentQueueItem() {
        if(currentQueueItemPosition >= 0 && currentQueueItemPosition < getCurrentQueue().size()){
            return currentQueue.get(currentQueueItemPosition);
        }
//        return currentQueue.get(currentQueueItemPosition);

        return null;
    }

    public void skipToNext() {
        currentQueueItemPosition = ((currentQueueItemPosition + 1) % currentQueue.size());
    }

    public void skipToPrevious(){
        currentQueueItemPosition = ((currentQueueItemPosition - 1) < 0 ? (currentQueue.size() - 1) : (currentQueueItemPosition -1));
    }

    private void RandomizeQueue(ArrayList<Song> randomSongs){
        Song temp1 = randomSongs.get(0);
        randomSongs.set(0, randomSongs.get(originalPosition));
        randomSongs.set(originalPosition, temp1);
        for (int i = randomSongs.size()-1; i > 1 ; i--){
            int min = 1;
            int max = i;
            int j = (int)(Math.random() * (max - min + 1) + min);

            Song temp = randomSongs.get(i);
            randomSongs.set(i , randomSongs.get(j));
            randomSongs.set(j , temp);

        }

    }

    public void updateQueue(ArrayList<Song> queue2, int fromPosition, int toPosition){
        unshuffledQueue.clear();
        unshuffledQueue.addAll(queue2);
        if (currentQueueItemPosition == fromPosition){
            currentQueueItemPosition = toPosition;
        }else if (currentQueueItemPosition == toPosition){
            currentQueueItemPosition = fromPosition;
        }
//        Collections.swap(queue, fromPosition, toPosition);
//        if (currentPosition == fromPosition){
//            currentPosition = toPosition;
//        }

        saveQueue();
    }


    public void playNextQueueItem(Song playNextSong){
        currentQueue.add(currentQueueItemPosition +1, playNextSong);
        if (shuffle){
            unshuffledQueue.add(unshuffledQueue.size(), playNextSong);
        }else{
            unshuffledQueue.add(currentQueueItemPosition +1, playNextSong);
        }

        saveQueue();

    }

    public void shuffleAll(){
        Collections.shuffle(currentQueue);
    }

    public void removeQueueItem(Song removeQueueItem){
        unshuffledQueue.remove(removeQueueItem);
        currentQueue.remove(removeQueueItem);
        saveQueue();
    }

    public void removeQueueItems(ArrayList<Song> removeQueueItems){
        unshuffledQueue.removeAll(removeQueueItems);
        currentQueue.removeAll(removeQueueItems);
        saveQueue();

    }

    public void addToQueue(Song addToQueueSong){
        currentQueue.add(currentQueue.size(), addToQueueSong);
        unshuffledQueue.add(unshuffledQueue.size(), addToQueueSong);
        saveQueue();
    }

    public void playAlbumNext(ArrayList<Song> nextAlbumSongs){
        currentQueue.addAll(currentQueueItemPosition +1, nextAlbumSongs);
        if (shuffle){
            unshuffledQueue.addAll(unshuffledQueue.size(), nextAlbumSongs);
        }else{
            unshuffledQueue.addAll(currentQueueItemPosition +1, nextAlbumSongs);
        }

        saveQueue();

    }

    public void addAlbumToQueue(ArrayList<Song> addAlbumToQueueSongs){
        currentQueue.addAll(currentQueue.size(), addAlbumToQueueSongs);
        unshuffledQueue.addAll(unshuffledQueue.size(), addAlbumToQueueSongs);
        saveQueue();
    }

    public Album getCurrentAlbum(ArrayList<Album> _albums, long albumId){

        for (int i = 0; i < _albums.size(); i++){
            if(_albums.get(i).getAlbumId() == albumId){
                album = _albums.get(i);
            }
        }

        return album;
    }

    public AlbumArtist getCurrentArtist(ArrayList<AlbumArtist> artists, long artistId) {

        Log.i("id1", String.valueOf(artistId));
        for (int i = 0; i < artists.size(); i++){
            if(artists.get(i).getArtistId() == artistId){
                artist = artists.get(i);

            }
        }

        return artist;


    }


    public String getQueueDuration(Context context){
        long y = 0L;
        for (int i = 0; i < currentQueue.size(); i++){
            String x = currentQueue.get(i).getDuration();
            y = y + Long.parseLong(x);

        }

        return StringUtils.makeTimeString(context, y / 1000);

    }

    public void saveQueue(){
        sharedPref.saveUnshuffledQueue(unshuffledQueue);
//        if(shuffle2){
            sharedPref.saveCurrentQueue(currentQueue);
//        }
        sharedPref.saveCurrentQueueItemPosition(currentQueueItemPosition);
    }

    public void loadQueue(){

        if(sharedPref.loadUnshuffledQueue() != null && !sharedPref.loadUnshuffledQueue().isEmpty()){
            setSavedNormalQueue(sharedPref.loadUnshuffledQueue());

            final int tempPosition = sharedPref.loadCurrentQueueItemPosition();

            if(tempPosition < 0 || tempPosition >= unshuffledQueue.size()){
                unshuffledQueue.clear();
                return;

            }

            this.currentQueueItemPosition = tempPosition;

            Log.d("CurrentQueueItem", String.valueOf(currentQueueItemPosition));

            if(sharedPref.loadCurrentQueue() != null && !sharedPref.loadCurrentQueue() .isEmpty()){
                setSavedShuffledQueue(sharedPref.loadCurrentQueue());

                if (tempPosition >= currentQueue.size()){
                    currentQueue.clear();
                    return;

                }

            }else if (sharedPref.loadCurrentQueue() == null | sharedPref.loadCurrentQueue() .isEmpty()){
                currentQueue.clear();
                currentQueue.addAll(unshuffledQueue);
            }

            if (currentQueueItemPosition < 0 || currentQueueItemPosition >= getCurrentQueue().size()) {
                currentQueueItemPosition = 0;
            }


        }



    }

    public void setQueueSortOrder(){





    }




}
