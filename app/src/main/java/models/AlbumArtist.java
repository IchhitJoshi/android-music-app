package models;

import android.content.Context;

import utils.StringUtils;

import java.util.ArrayList;

public class AlbumArtist {

    private String artistName;
    private long artistId;
    private int numOfSongsByArtist = 0;
    private int numOfAlbumsByArtist = 0;
    private String artistDuration;
    private String artistDurationLabel;
    private ArrayList<Album> albumsByArtist;
    private ArrayList<Song> songsByArtist = new ArrayList<>();

    private int artist_details_songs_sort_number = 2;
    private int artist_details_albums_sort_number = 1;
    private boolean artist_details_songs_ascending = true;
    private boolean artist_details_albums_ascending = true;

    public AlbumArtist(){
    }


    public AlbumArtist(ArrayList<Album> AlbumsByArtist)
    {
        this.albumsByArtist = AlbumsByArtist;
        artistId = AlbumsByArtist.get(0).getArtistId();
        artistName = AlbumsByArtist.get(0).getArtistName();
        numOfAlbumsByArtist = AlbumsByArtist.size();

        songsByArtist.clear();

        long artistDuration1 = 0L;

        for (int i = 0; i < numOfAlbumsByArtist; i++){
            numOfSongsByArtist = this.albumsByArtist.get(i).getNumSong() + numOfSongsByArtist;
            artistDuration1 = Long.parseLong(this.albumsByArtist.get(i).getAlbumDuration()) + artistDuration1;
            songsByArtist.addAll(AlbumsByArtist.get(i).getAlbumSongs());
        }

        artistDuration = String.valueOf(artistDuration1);


    }

    public String getArtistName() {
        return artistName;
    }

    public void setArtistName(String artistName) {
        this.artistName = artistName;
    }

    public long getArtistId() {
        return artistId;
    }

    public void setArtistId(long artistId) {
        this.artistId = artistId;
    }

    public int getNumOfSongsByArtist() {
        return numOfSongsByArtist;
    }

    public void setNumOfSongsByArtist(int numOfSongsByArtist) {
        this.numOfSongsByArtist = numOfSongsByArtist;
    }

    public int getNumOfAlbumsByArtist() {
        return numOfAlbumsByArtist;
    }

    public void setNumOfAlbumsByArtist(int numOfAlbumsByArtist) {
        this.numOfAlbumsByArtist = numOfAlbumsByArtist;
    }

    public String getArtistDuration() {
        return artistDuration;
    }

    public void setArtistDuration(String artistDuration) {
        this.artistDuration = artistDuration;
    }

    public String getArtistDurationLabel(Context context) {
        long artistDuration2 = Long.parseLong(getArtistDuration());
        artistDurationLabel = StringUtils.makeTimeString(context, artistDuration2 / 1000);
        return artistDurationLabel;
    }

    public void setArtistDurationLabel(String artistDurationLabel) {
        this.artistDurationLabel = artistDurationLabel;
    }

    public ArrayList<Album> getAlbumsByArtist() {
        return albumsByArtist;
    }

    public void setAlbumsByArtist(ArrayList<Album> albumsByArtist) {
        this.albumsByArtist = albumsByArtist;
    }

    public ArrayList<Song> getSongsByArtist() {
        return songsByArtist;
    }

    public void setSongsByArtist(ArrayList<Song> songsByArtist) {
        this.songsByArtist = songsByArtist;
    }

    public int getArtist_details_songs_sort_number() {
        return artist_details_songs_sort_number;
    }

    public void setArtist_details_songs_sort_number(int artist_details_songs_sort_number) {
        this.artist_details_songs_sort_number = artist_details_songs_sort_number;
    }

    public boolean isArtist_details_songs_ascending() {
        return artist_details_songs_ascending;
    }

    public void setArtist_details_songs_ascending(boolean artist_details_songs_ascending) {
        this.artist_details_songs_ascending = artist_details_songs_ascending;
    }

    public int getArtist_details_albums_sort_number() {
        return artist_details_albums_sort_number;
    }

    public void setArtist_details_albums_sort_number(int artist_details_albums_sort_number) {
        this.artist_details_albums_sort_number = artist_details_albums_sort_number;
    }

    public boolean isArtist_details_albums_ascending() {
        return artist_details_albums_ascending;
    }

    public void setArtist_details_albums_ascending(boolean artist_details_albums_ascending) {
        this.artist_details_albums_ascending = artist_details_albums_ascending;
    }



}
