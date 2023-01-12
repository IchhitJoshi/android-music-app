package utils;

import android.content.Context;
import android.content.SharedPreferences;
import android.util.Log;

import com.google.gson.Gson;
import com.google.gson.reflect.TypeToken;

import java.lang.reflect.Type;
import java.util.ArrayList;

import models.Song;

public class SharedPref {

    SharedPreferences sharedPreferences;
    private final Context context;

    public SharedPref(Context mContext){
        this.context = mContext;
        sharedPreferences = context.getSharedPreferences("Music", Context.MODE_PRIVATE);


    }

    public void saveDarkMode(Boolean state){
        SharedPreferences.Editor editor = sharedPreferences.edit();
        editor.putBoolean("DarkMode", state);
        editor.apply();
    }

    public boolean loadDarkModeState(){
        boolean state = sharedPreferences.getBoolean("DarkMode", false);
        return state;
    }

    public void saveSystemDefault(Boolean state){
        SharedPreferences.Editor editor = sharedPreferences.edit();
        editor.putBoolean("SystemDefault", state);
        editor.apply();
    }

    public boolean loadSystemDefaultState(){
        boolean state = sharedPreferences.getBoolean("SystemDefault", false);
        return state;
    }

    public void saveShuffleMode(boolean shuffle){
        SharedPreferences.Editor editor = sharedPreferences.edit();
        editor.putBoolean("Shuffle Mode", shuffle);
        editor.apply();

    }

    public boolean loadShuffleMode(){
        boolean shuffle = sharedPreferences.getBoolean("Shuffle Mode", false);
        return shuffle;

    }

    public void saveRepeatMode(boolean repeat){
        SharedPreferences.Editor editor = sharedPreferences.edit();
        editor.putBoolean("Repeat Mode", repeat);
        editor.apply();

    }

    public boolean loadRepeatMode(){
        boolean repeat = sharedPreferences.getBoolean("Repeat Mode", false);
        return repeat;

    }

    public void saveLoopMode(boolean loop){
        SharedPreferences.Editor editor = sharedPreferences.edit();
        editor.putBoolean("Loop Mode", loop);
        editor.apply();

    }

    public boolean loadLoopMode(){
        boolean loop = sharedPreferences.getBoolean("Loop Mode", false);
        return loop;

    }

    public void saveDurationFlip(boolean durFlip){
        SharedPreferences.Editor editor = sharedPreferences.edit();
        editor.putBoolean("Duration Flip", durFlip);
        editor.apply();
    }

    public boolean loadDurationFlip(){
        boolean durFlip = sharedPreferences.getBoolean("Duration Flip", false);
        return durFlip;
    }

    public void saveSongsSortNumber(int songsSortNumber){
        SharedPreferences.Editor editor = sharedPreferences.edit();
        editor.putInt("Songs Sort Number", songsSortNumber);
        editor.apply();
    }

    public int loadSongsSortNumber(){
        int songsSortNumber = sharedPreferences.getInt("Songs Sort Number", 1);
        return songsSortNumber;
    }

    public void saveSongsAscending(boolean ascending){
        SharedPreferences.Editor editor = sharedPreferences.edit();
        editor.putBoolean("Songs Ascending", ascending);
        editor.apply();
    }

    public boolean loadSongsAscending(){
        boolean ascending = sharedPreferences.getBoolean("Songs Ascending", true);
        return ascending;
    }

    public void saveAlbumsSortNumber(int albumsSortNumber){
        SharedPreferences.Editor editor = sharedPreferences.edit();
        editor.putInt("AlbumsSort Number", albumsSortNumber);
        editor.apply();
    }

    public int loadAlbumsSortNumber(){
        int albumsSortNumber = sharedPreferences.getInt("Albums Sort Number", 1);
        return albumsSortNumber;
    }

    public void saveAlbumsAscending(boolean ascending){
        SharedPreferences.Editor editor = sharedPreferences.edit();
        editor.putBoolean("Albums Ascending", ascending);
        editor.apply();
    }

    public boolean loadAlbumsAscending(){
        boolean ascending = sharedPreferences.getBoolean("Albums Ascending", true);
        return ascending;
    }

    public void saveArtistsSortNumber(int artistsSortNumber){
        SharedPreferences.Editor editor = sharedPreferences.edit();
        editor.putInt("Artists Sort Number", artistsSortNumber);
        editor.apply();
    }

    public int loadArtistsSortNumber(){
        int artistsSortNumber = sharedPreferences.getInt("Artists Sort Number", 1);
        return artistsSortNumber;
    }

    public void saveArtistsAscending(boolean ascending){
        SharedPreferences.Editor editor = sharedPreferences.edit();
        editor.putBoolean("Artists Ascending", ascending);
        editor.apply();
    }

    public boolean loadArtistsAscending(){
        boolean ascending = sharedPreferences.getBoolean("Artists Ascending", true);
        return ascending;
    }

    public void saveCurrentQueue(ArrayList<Song> currentQueueItems){
        SharedPreferences.Editor editor = sharedPreferences.edit();
        Gson gson = new Gson();
        String json = gson.toJson(currentQueueItems);
        editor.putString("Current Queue", json);
        editor.apply();
    }

    public ArrayList<Song> loadCurrentQueue(){
        String json = sharedPreferences.getString("Current Queue", null);
        Gson gson = new Gson();
        Type type = new TypeToken<ArrayList<Song>>() {}.getType();
        ArrayList<Song> loadedQueue;
        loadedQueue = gson.fromJson(json, type);
        return loadedQueue;
    }

    public void saveUnshuffledQueue(ArrayList<Song> unshuffledQueueItems){
        SharedPreferences.Editor editor = sharedPreferences.edit();
        Gson gson = new Gson();
        String json = gson.toJson(unshuffledQueueItems);
        editor.putString("Unshuffled Queue", json);
        editor.apply();
    }

    public ArrayList<Song> loadUnshuffledQueue(){
        String json = sharedPreferences.getString("Unshuffled Queue", null);
        Gson gson = new Gson();
        Type type = new TypeToken<ArrayList<Song>>() {}.getType();
        ArrayList<Song> unshuffledQueue;
        unshuffledQueue = gson.fromJson(json, type);
        return unshuffledQueue;
    }

    public void saveCurrentQueueItemPosition(int currentQueueItemPosition){
        SharedPreferences.Editor editor = sharedPreferences.edit();
        editor.putInt("Current Queue Item Position", currentQueueItemPosition);
        editor.apply();
        Log.d("position123", String.valueOf(currentQueueItemPosition));
    }

    public int loadCurrentQueueItemPosition(){
        int currentQueueItemPosition = sharedPreferences.getInt("Current Queue Item Position", 0);
        Log.d("position123", String.valueOf(currentQueueItemPosition));
        return currentQueueItemPosition;
    }

    public void saveCurrentQueueItemSongPosition(int currentQueueItemSongPosition){
        SharedPreferences.Editor editor = sharedPreferences.edit();
        editor.putInt("Current Queue Item Song Position", currentQueueItemSongPosition);
        editor.apply();

    }

    public int loadCurrentQueueItemSongPosition(){
        int currentQueueItemSongPosition = sharedPreferences.getInt("Current Queue Item Song Position", 0);
        return currentQueueItemSongPosition;

    }



}
