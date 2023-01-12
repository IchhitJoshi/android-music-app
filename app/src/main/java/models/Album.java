package models;

import android.content.Context;
import android.util.Log;

import utils.StringUtils;

import java.util.ArrayList;

public class Album {

    private String albumName;
    private String albumArtistName;
    private long artistId;
    private int numSong;
    private long albumId;
    private String albumDuration;
    private String albumDurationLabel;
    private int albumYear;
    private ArrayList<Song> albumSongs;
    private int albumDiscNumber = 0;
    private int temp = 0;
    private int number;
    private int year;
    private int tempYear = 0;

    private int album_details_songs_sort_number = 2;
    private boolean album_details_songs_ascending = true;
    private boolean showTrackNumber = true;
    private boolean showArtistName = false;


    public Album(){
    }

    public Album(ArrayList<Song> AlbumSongs)
    {
        this.albumSongs = AlbumSongs;
        albumId = AlbumSongs.get(0).getAlbumId();
        albumName = AlbumSongs.get(0).getAlbumName();
        artistId = AlbumSongs.get(0).getArtistId();
        numSong = AlbumSongs.size();
        albumArtistName = AlbumSongs.get(0).getAlbumArtistName();


        for (int i = 0; i < numSong; i++){
            number = albumSongs.get(i).getDiscNumber();
            year = albumSongs.get(i).getSongYear();
            Log.d("ArtistId", String.valueOf(albumSongs.get(i).getArtistId()));

            if (number > temp ){
                temp = number;
            }
            if (year > tempYear){
                tempYear = year;
            }
        }

        for (int i = 0; i < numSong && showTrackNumber; i++){
            if (albumSongs.get(i).getTrackNumber() < 1){
                album_details_songs_sort_number = 1;
                showTrackNumber = false;
                break;
            }else{
                showTrackNumber = true;
                album_details_songs_sort_number = 2;

            }
        }

        for (int i = 0; i < numSong && !showArtistName; i++){
            String a = albumSongs.get(i).getArtistName();
            String b = albumArtistName;
            if (!a.equalsIgnoreCase(b)){
                showArtistName = true;
                break;
            }else{
                showArtistName = false;
            }
        }

        albumDiscNumber = temp;
        albumYear = tempYear;

    }

    public String getAlbumName() {
        return albumName;
    }

    public void setAlbumName(String albumName) {
        this.albumName = albumName;
    }

    public String getArtistName() {
        return albumArtistName;
    }

    public void setArtistName(String artistName) {
        this.albumArtistName = artistName;
    }

    public long getArtistId() {
        return artistId;
    }

    public void setArtistId(long artistId) {
        this.artistId = artistId;
    }

    public int getNumSong() {
        return numSong;
    }

    public void setNumSong(int numSong) {
        this.numSong = numSong;
    }

    public long getAlbumId() {
        return albumId;
    }

    public void setAlbumId(long albumId) {
        this.albumId = albumId;
    }

    public String getAlbumDuration() {
        long albumDuration1 = 0L;
        for (int i = 0; i < getNumSong(); i++){
            albumDuration1 = Long.parseLong(albumSongs.get(i).getDuration()) + albumDuration1;
        }
        albumDuration = String.valueOf(albumDuration1);
        return albumDuration;
    }

    public String getAlbumDurationLabel(Context context){
        long albumDuration2 = Long.parseLong(getAlbumDuration());
        albumDurationLabel = StringUtils.makeTimeString(context, albumDuration2 / 1000);
        return albumDurationLabel;}

    public void setAlbumDuration(String albumDuration) {
        this.albumDuration = albumDuration;
    }

    public int getYear() {
        return albumYear;
    }

    public void setYear(int year) {
        this.albumYear = year;
    }

    public ArrayList<Song> getAlbumSongs() {
        return albumSongs;
    }

    public void setAlbumSongs(ArrayList<Song> albumSongs) {
        this.albumSongs.addAll(albumSongs);
    }

    public int getDiscNumber() {
        return albumDiscNumber;
    }

    public void setDiscNumber(int discNumber) {
        this.albumDiscNumber = discNumber;
    }

    public boolean isShowTrackNumber() {
        return showTrackNumber;
    }

    public void setShowTrackNumber(boolean showTrackNumber) {
        this.showTrackNumber = showTrackNumber;
    }

    public boolean isShowArtistName() {
        return showArtistName;
    }

    public void setShowArtistName(boolean showArtistName) {
        this.showArtistName = showArtistName;
    }


    public int getAlbum_details_songs_sort_number() {
        return album_details_songs_sort_number;
    }

    public void setAlbum_details_songs_sort_number(int album_details_songs_sort_number) {
        this.album_details_songs_sort_number = album_details_songs_sort_number;
    }

    public boolean isAlbum_details_songs_ascending() {
        return album_details_songs_ascending;
    }

    public void setAlbum_details_songs_ascending(boolean album_details_songs_ascending) {
        this.album_details_songs_ascending = album_details_songs_ascending;
    }

}
