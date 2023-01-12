package managers;

import android.Manifest;
import android.content.ContentResolver;
import android.content.ContentUris;
import android.content.Context;
import android.database.Cursor;
import android.net.Uri;
import android.provider.MediaStore;

import com.karumi.dexter.Dexter;
import com.karumi.dexter.PermissionToken;
import com.karumi.dexter.listener.PermissionDeniedResponse;
import com.karumi.dexter.listener.PermissionGrantedResponse;
import com.karumi.dexter.listener.PermissionRequest;
import com.karumi.dexter.listener.single.PermissionListener;

import java.io.File;
import java.util.ArrayList;
import java.util.Collections;
import java.util.Comparator;

import models.Album;
import models.AlbumArtist;
import models.Song;

import static com.example.demo.views.Home.albums_sort_number;
import static com.example.demo.views.Home.artists_sort_number;
import static com.example.demo.views.Home.songs_ascending;
import static com.example.demo.views.Home.songs_sort_number;

public class MediaImporter {
    private final ArrayList<Song> _songs = new ArrayList<Song>();
    private final ArrayList<Album> _albums = new ArrayList<Album>();
    private final ArrayList<AlbumArtist> _artists = new ArrayList<>();
    private final Context context;
    private String sortOrder = MediaStore.Audio.Media.TITLE;
    private String order = " COLLATE NOCASE ASC";

    public MediaImporter(Context context){
        this.context = context;

    }

    public void runTimePermission(){
        Dexter.withContext(context).withPermission(Manifest.permission.READ_EXTERNAL_STORAGE).withListener(new PermissionListener() {
            @Override
            public void onPermissionGranted(PermissionGrantedResponse permissionGrantedResponse) {
                _songs.clear();
                _albums.clear();
                _artists.clear();
                setSongsSortOrder();
                loadSongs();

                loadAlbums();
                loadArtists();
            }

            @Override
            public void onPermissionDenied(PermissionDeniedResponse permissionDeniedResponse) {

            }

            @Override
            public void onPermissionRationaleShouldBeShown(PermissionRequest permissionRequest, PermissionToken permissionToken) {
                permissionToken.continuePermissionRequest();

            }
        }).check();
    }

    public void reloadSongsSort(){
        _songs.clear();
        loadSongs();
    }


    public void loadSongs(){

        Uri uri = MediaStore.Audio.Media.EXTERNAL_CONTENT_URI;
        ContentResolver musicResolver = context.getContentResolver();
        String selection = MediaStore.Audio.Media.IS_MUSIC + "!=0";
        Cursor cursor = musicResolver.query(uri, Song.getProjection(), selection, null, sortOrder + order);

        if(cursor != null){
            if(cursor.moveToFirst()){
                do {

                    Song s = new Song(cursor);
                    _songs.add(s);

                }while(cursor.moveToNext());

            }
            cursor.close();
        }
    }


    public void loadAlbums(){
        ArrayList<Long> duplicate = new ArrayList<Long>();
        ArrayList<Song> temp2 = new ArrayList<>();
        ArrayList<Song> temp = new ArrayList<>();

        temp2.clear();
        temp2.addAll(_songs);
        Collections.sort(temp2, new Comparator<Song>() {
            @Override
            public int compare(Song s1, Song s2) {
                Long string1 = s1.getAlbumId();
                Long string2 = s2.getAlbumId();
                return string1.compareTo(string2);
            }
        });

        for(int i = 0; i < temp2.size(); i ++){

            if(!duplicate.contains(temp2.get(i).getAlbumId())){
                if (!temp.isEmpty()){
                    ArrayList<Song> finalAlbumSongs = new ArrayList<Song>();
                    finalAlbumSongs.clear();
                    finalAlbumSongs.addAll(temp);
                    Album album = new Album(finalAlbumSongs);
                    _albums.add(album);
                }
                temp.clear();
                temp.add(temp2.get(i));

                duplicate.add(temp2.get(i).getAlbumId());

            }else{
                temp.add(temp2.get(i));

            }

        }


    }

    public void loadArtists(){
        ArrayList<Long> duplicate = new ArrayList<Long>();
        ArrayList<Album> temp2 = new ArrayList<>();
        ArrayList<Album> temp = new ArrayList<>();

        temp2.clear();
        temp2.addAll(_albums);
        Collections.sort(temp2, new Comparator<Album>() {
            @Override
            public int compare(Album s1, Album s2) {
                Long string1 = s1.getArtistId();
                Long string2 = s2.getArtistId();
                return string1.compareTo(string2);
            }
        });

        for(int i = 0; i < temp2.size(); i ++){
            if(!duplicate.contains(temp2.get(i).getArtistId())){
                if (!temp.isEmpty()){
                    ArrayList<Album> finalAlbums = new ArrayList<Album>();
                    finalAlbums.clear();
                    finalAlbums.addAll(temp);
                    AlbumArtist albumArtist = new AlbumArtist(finalAlbums);
                    _artists.add(albumArtist);
                }
                temp.clear();
                temp.add(temp2.get(i));
                duplicate.add(temp2.get(i).getArtistId());

            }else{
                temp.add(temp2.get(i));
            }

        }


    }

    public ArrayList<Song> getLoadedSongs(){
        return _songs;
    }

    public ArrayList<Album> getLoadedAlbums(){
        Collections.sort(_albums, new Comparator<Album>() {
            @Override
            public int compare(Album a1, Album a2) {
                String s1 = a1.getAlbumName();
                String s2 = a2.getAlbumName();
                return s1.compareToIgnoreCase(s2);
            }
        });

        for (int i = 0; i < _albums.size(); i++){
            Collections.sort(_albums.get(i).getAlbumSongs(), new Comparator<Song>() {
                @Override
                public int compare(Song a1, Song a2) {
                    Integer s1 = a1.getTrackNumber();
                    Integer s2 = a2.getTrackNumber();
                    return s1.compareTo(s2);
                }
            });

            Collections.sort(_albums.get(i).getAlbumSongs(), new Comparator<Song>() {
                @Override
                public int compare(Song a1, Song a2) {
                    Integer s1 = a1.getDiscNumber();
                    Integer s2 = a2.getDiscNumber();
                    return s1.compareTo(s2);
                }
            });
        }

        return _albums;
    }

    public ArrayList<AlbumArtist> getLoadedArtists(){
        Collections.sort(_artists, new Comparator<AlbumArtist>() {
            @Override
            public int compare(AlbumArtist artist1, AlbumArtist artist2) {
                String s1 = artist1.getArtistName();
                String s2 = artist2.getArtistName();
                return s1.compareToIgnoreCase(s2);
            }
        });

        for (int i = 0; i < _artists.size(); i++){
            Collections.sort(_artists.get(i).getAlbumsByArtist(), new Comparator<Album>() {
                @Override
                public int compare(Album o1, Album o2) {
                    String s1 = o1.getAlbumName();
                    String s2 = o2.getAlbumName();
                    return s1.compareToIgnoreCase(s2);
                }
            });


            Collections.sort(_artists.get(i).getSongsByArtist(), new Comparator<Song>() {
                @Override
                public int compare(Song a1, Song a2) {
                    Integer s1 = a1.getTrackNumber();
                    Integer s2 = a2.getTrackNumber();
                    return s1.compareTo(s2);
                }
            });


            Collections.sort(_artists.get(i).getSongsByArtist(), new Comparator<Song>() {
                @Override
                public int compare(Song a1, Song a2) {
                    Integer s1 = a1.getDiscNumber();
                    Integer s2 = a2.getDiscNumber();
                    return s1.compareTo(s2);
                }
            });



            Collections.sort(_artists.get(i).getSongsByArtist(), new Comparator<Song>() {
                @Override
                public int compare(Song a1, Song a2) {
                    String s1 = a1.getAlbumName();
                    String s2 = a2.getAlbumName();
                    return s1.compareToIgnoreCase(s2);
                }
            });



        }

        return _artists;

    }



    public void setSongsSortOrder(){
        if (songs_sort_number == 1){
            sortOrder = MediaStore.Audio.Media.TITLE;
        }else if (songs_sort_number == 2){
            sortOrder = MediaStore.Audio.Media.ARTIST;
        }else if (songs_sort_number == 3){
            sortOrder = MediaStore.Audio.Media.DATE_ADDED;
        }else if (songs_sort_number == 4){
            sortOrder = MediaStore.Audio.Media.DURATION;
        }else if (songs_sort_number == 5){
            sortOrder = MediaStore.Audio.Media.YEAR;
        }else if (songs_sort_number == 6){
            sortOrder = MediaStore.Audio.Media.ALBUM;
        }

        if (songs_ascending){
            order = " COLLATE NOCASE ASC";
        }else {
            order = " COLLATE NOCASE DESC";
        }
    }

    public void setAlbumsSortOrder(){
        if (albums_sort_number == 1){
            getLoadedAlbums();
        }else if (albums_sort_number == 2){
            getLoadedAlbums();
            Collections.sort(_albums, new Comparator<Album>() {
                @Override
                public int compare(Album a1, Album a2) {
                    String s1 = a1.getArtistName();
                    String s2 = a2.getArtistName();
                    return s1.compareToIgnoreCase(s2);
                }
            });
        }else if (albums_sort_number == 3){
            getLoadedAlbums();
            Collections.sort(_albums, new Comparator<Album>() {
                @Override
                public int compare(Album a1, Album a2) {
                    Integer s1 = a1.getYear();
                    Integer s2 = a2.getYear();
                    return s1.compareTo(s2);
                }
            });

        }else if (albums_sort_number == 4){
            getLoadedAlbums();
            Collections.sort(_albums, new Comparator<Album>() {
                @Override
                public int compare(Album a1, Album a2) {
                    Integer s1 = a1.getNumSong();
                    Integer s2 = a2.getNumSong();
                    return s1.compareTo(s2);
                }
            });


        }
    }

    public void setArtistsSortOrder(){
        if (artists_sort_number == 1){
            getLoadedArtists();
        }else if (artists_sort_number == 2){
            getLoadedArtists();
            Collections.sort(_artists, new Comparator<AlbumArtist>() {
                @Override
                public int compare(AlbumArtist a1, AlbumArtist a2) {
                    Integer s1 = a1.getNumOfAlbumsByArtist();
                    Integer s2 = a2.getNumOfAlbumsByArtist();
                    return s1.compareTo(s2);
                }
            });
        }else if (artists_sort_number== 3){
            getLoadedArtists();
            Collections.sort(_artists, new Comparator<AlbumArtist>() {
                @Override
                public int compare(AlbumArtist a1, AlbumArtist a2) {
                    Integer s1 = a1.getNumOfSongsByArtist();
                    Integer s2 = a2.getNumOfSongsByArtist();
                    return s1.compareTo(s2);
                }
            });

        }else if (artists_sort_number == 4){
            getLoadedArtists();
//            Collections.sort(_artists, new Comparator<ArtistModel>() {
//                @Override
//                public int compare(ArtistModel a1, ArtistModel a2) {
//                    Integer s1 = a1.getNumOfAlbumsByArtist();
//                    Integer s2 = a2.getNumOfAlbumsByArtist();
//                    return s1.compareTo(s2);
//                }
//            });

        }


    }

    public ArrayList<Album> getAlbums(){
        return _albums;
    }

    public ArrayList<AlbumArtist> getArtists(){
        return _artists;
    }

    public boolean deleteSong(Song song){
        Uri contentUri = ContentUris.withAppendedId(MediaStore.Audio.Media.EXTERNAL_CONTENT_URI, Long.parseLong(song.getSongId()));
        File file = new File(song.getSongUrl());
        return file.delete();
    }


}
