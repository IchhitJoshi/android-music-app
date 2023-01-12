package adapters;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.DefaultItemAnimator;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import services.MusicService;
import com.example.demo.R;
import com.simplecityapps.recyclerview_fastscroll.views.FastScrollRecyclerView;

import java.util.ArrayList;
import java.util.Collections;
import java.util.Comparator;
import java.util.concurrent.ExecutionException;

import models.Album;
import models.AlbumArtist;
import models.Song;
import utils.EqualSpacingItemDecoration;

import static com.example.demo.views.Home.shuffle;

public class ArtistDetailsSongAdapter  extends RecyclerView.Adapter<RecyclerView.ViewHolder> implements FastScrollRecyclerView.SectionedAdapter{

    AlbumArtist artist;
    private final Context context;
    MusicService musicService;
    private final ArrayList<Song> artistSongs;
    private final ArrayList<Album> artistAlbums;

    private static SongAdapter.AdapterCallback adapterCallback;

    private static final int TYPE_FIRST_HEADER = 0;
    private static final int TYPE_HORIZONTAL_VIEW = 1;
    private static final int TYPE_SECOND_HEADER = 2;
    private static final int TYPE_VERTICAL_ITEMS = 3;

    ArtistDetailsAlbumAdapter artistDetailsAlbumAdapter;



    public ArtistDetailsSongAdapter(Context context, MusicService musicService, AlbumArtist artist){
        this.context = context;
        this.musicService = musicService;
        this.artist = artist;
        artistSongs = artist.getSongsByArtist();
        artistAlbums = artist.getAlbumsByArtist();


        try{
            adapterCallback= ((SongAdapter.AdapterCallback) context);
        }catch (ClassCastException ignored) {
        }
    }
    @NonNull
    @Override
    public RecyclerView.ViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        if (viewType == TYPE_FIRST_HEADER ){
            View myView = LayoutInflater.from(context).inflate(R.layout.artists_details_first_header, parent, false);
            return new ArtistDetailsFirstHeaderViewHolder(myView);

        } else if (viewType == TYPE_HORIZONTAL_VIEW ){
            View myView = LayoutInflater.from(context).inflate(R.layout.artists_details_album_view, parent, false);
            return new ArtistDetailsAlbumViewHolder(myView, context);

        } else if (viewType == TYPE_SECOND_HEADER  ){
            View myView = LayoutInflater.from(context).inflate(R.layout.artists_details_second_header, parent, false);
            return new ArtistDetailsSecondHeaderViewHolder(myView);

        }else if (viewType == TYPE_VERTICAL_ITEMS ){
            View myView = LayoutInflater.from(context).inflate(R.layout.artists_details_song_item, parent, false);
            return new ArtistDetailsSongItemViewHolder(myView, musicService, artistSongs);

        }
        else return null;
    }

    @Override
    public void onBindViewHolder(@NonNull RecyclerView.ViewHolder holder, int position) {
        if (holder instanceof ArtistDetailsFirstHeaderViewHolder){
            final ArtistDetailsFirstHeaderViewHolder artistDetailsFirstHeaderViewHolder = (ArtistDetailsFirstHeaderViewHolder) holder;
            artistDetailsFirstHeaderViewHolder.first_header_albumsNum.setText(artist.getNumOfAlbumsByArtist() + "");


            artistDetailsFirstHeaderViewHolder.first_header_sort.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    androidx.appcompat.widget.PopupMenu popupMenu = new androidx.appcompat.widget.PopupMenu(context, artistDetailsFirstHeaderViewHolder.first_header_sort);
                    popupMenu.inflate(R.menu.artist_details_albums_sort_options);
                    MenuItem sort_name = popupMenu.getMenu().findItem(R.id.artist_details_albums_sort_item1);
                    MenuItem sort_year = popupMenu.getMenu().findItem(R.id.artist_details_albums_sort_item2);
                    MenuItem sort_duration = popupMenu.getMenu().findItem(R.id.artist_details_albums_sort_item3);
                    MenuItem sort_track = popupMenu.getMenu().findItem(R.id.artist_details_albums_sort_item4);
                    MenuItem sort_date = popupMenu.getMenu().findItem(R.id.artist_details_albums_sort_item5);
                    MenuItem sort_ascending = popupMenu.getMenu().findItem(R.id.artist_details_albums_sort_item6);


                    if (artist.getArtist_details_albums_sort_number() == 1){
                        sort_name.setChecked(true);
                    }else if (artist.getArtist_details_albums_sort_number() == 2 ){
                        sort_year.setChecked(true);
                    }else if (artist.getArtist_details_albums_sort_number() == 3 ){
                        sort_duration.setChecked(true);
                    }else if (artist.getArtist_details_albums_sort_number() == 4 ){
                        sort_track.setChecked(true);
                    }else if (artist.getArtist_details_albums_sort_number() == 5 ){
                        sort_date.setChecked(true);
                    }

                    if (artist.isArtist_details_albums_ascending()){
                        sort_ascending.setChecked(true);
                    }else {
                        sort_ascending.setChecked(false);
                    }



                    popupMenu.setOnMenuItemClickListener(new androidx.appcompat.widget.PopupMenu.OnMenuItemClickListener() {
                        @Override
                        public boolean onMenuItemClick(MenuItem item) {

                            switch (item.getItemId()){
                                case R.id.artist_details_albums_sort_item1:

                                    if(!artist.isArtist_details_albums_ascending()  && artist.getArtist_details_albums_sort_number() != 1){
                                        artist.setArtist_details_albums_sort_number(1);
                                        sortAlbums();
                                        reverseAlbums(); }

                                    if(artist.getArtist_details_albums_sort_number() != 1){
                                        artist.setArtist_details_albums_sort_number(1);
                                        sortAlbums();
                                    }

                                    break;
                                case R.id.artist_details_albums_sort_item2:
                                    item.setChecked(true);
                                    if(!artist.isArtist_details_albums_ascending() && artist.getArtist_details_albums_sort_number() != 2){
                                        artist.setArtist_details_albums_sort_number(2);
                                        sortAlbums();
                                        reverseAlbums(); }

                                    if(artist.getArtist_details_albums_sort_number() != 2){
                                        artist.setArtist_details_albums_sort_number(2);
                                        sortAlbums();
                                    }

                                    break;
                                case R.id.artist_details_albums_sort_item3:
                                    item.setChecked(true);

                                    if(!artist.isArtist_details_albums_ascending() && artist.getArtist_details_albums_sort_number() != 3){
                                        artist.setArtist_details_albums_sort_number(3);
                                        sortAlbums();
                                        reverseAlbums(); }

                                    if(artist.getArtist_details_albums_sort_number() != 3){
                                        artist.setArtist_details_albums_sort_number(3);
                                        sortAlbums();
                                    }

                                    break;
                                case R.id.artist_details_albums_sort_item4:
                                    item.setChecked(true);

                                    if(!artist.isArtist_details_albums_ascending() && artist.getArtist_details_albums_sort_number() != 4){
                                        artist.setArtist_details_albums_sort_number(4);
                                        sortAlbums();
                                        reverseAlbums(); }

                                    if(artist.getArtist_details_albums_sort_number() != 4){
                                        artist.setArtist_details_albums_sort_number(4);
                                        sortAlbums();
                                    }

                                    break;
                                case R.id.artist_details_albums_sort_item5:
                                    item.setChecked(true);

                                    if(!artist.isArtist_details_albums_ascending() && artist.getArtist_details_albums_sort_number() != 5){
                                        artist.setArtist_details_albums_sort_number(5);
                                        sortAlbums();
                                        reverseAlbums(); }

                                    if(artist.getArtist_details_albums_sort_number() != 5){
                                        artist.setArtist_details_albums_sort_number(5);
                                        sortAlbums();
                                    }


                                    break;
                                case R.id.artist_details_albums_sort_item6:

                                    if (artist.isArtist_details_albums_ascending()){
                                        artist.setArtist_details_albums_ascending(false);
                                    }else {
                                        artist.setArtist_details_albums_ascending(true);
                                    }
                                    if (item.isChecked()){
                                        item.setChecked(false);
                                    }else {
                                        item.setChecked(true);
                                    }
                                    reverseAlbums();
                                    break;


                                default:
                                    break;
                            }

                            return false;
                        }
                    });
                    popupMenu.show();
                }
            }
            );

            artistDetailsFirstHeaderViewHolder.first_header_shuffle.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {

                    shuffle = false;
                    ArrayList<Album> albums2 = new ArrayList<Album>();
                    albums2.clear();
                    albums2.addAll(artistAlbums);
                    Collections.shuffle(albums2);
                    musicService.setAlbumQueue(albums2);
                    musicService.setCurrentQueueItemPosition(0);

                    musicService.loadSong(musicService.getCurrentQueueItem());
                    musicService.play();
                    try{
                        adapterCallback.onMethodCallback();
                    }catch (ClassCastException | ExecutionException | InterruptedException ignored){
                    }
                }
            });

            artistDetailsFirstHeaderViewHolder.first_header_play.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    shuffle = false;
                    ArrayList<Album> albums2 = new ArrayList<Album>();
                    albums2.clear();
                    albums2.addAll(artistAlbums);
                    musicService.setAlbumQueue(albums2);
                    musicService.setCurrentQueueItemPosition(0);

                    musicService.loadSong(musicService.getCurrentQueueItem());
                    musicService.play();
                    try{
                        adapterCallback.onMethodCallback();
                    }catch (ClassCastException | ExecutionException | InterruptedException ignored){
                    }

                    }


            });





        }else if (holder instanceof ArtistDetailsSecondHeaderViewHolder) {
            final ArtistDetailsSecondHeaderViewHolder artistDetailsSecondHeaderViewHolder = (ArtistDetailsSecondHeaderViewHolder) holder;
            artistDetailsSecondHeaderViewHolder.second_header_songsNum.setText(artist.getNumOfSongsByArtist() + "•" + artist.getArtistDurationLabel(context));

            artistDetailsSecondHeaderViewHolder.second_header_sort.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    androidx.appcompat.widget.PopupMenu popupMenu = new androidx.appcompat.widget.PopupMenu(context, artistDetailsSecondHeaderViewHolder.second_header_sort);
                    popupMenu.inflate(R.menu.artist_details_songs_sort_options);
                    MenuItem sort_name = popupMenu.getMenu().findItem(R.id.artist_details_songs_sort_item1);
                    MenuItem sort_album = popupMenu.getMenu().findItem(R.id.artist_details_songs_sort_item2);
                    MenuItem sort_duration = popupMenu.getMenu().findItem(R.id.artist_details_songs_sort_item3);
                    MenuItem sort_year = popupMenu.getMenu().findItem(R.id.artist_details_songs_sort_item4);
                    MenuItem sort_date = popupMenu.getMenu().findItem(R.id.artist_details_songs_sort_item5);
                    MenuItem sort_ascending = popupMenu.getMenu().findItem(R.id.artist_details_songs_sort_item6);


                    if (artist.getArtist_details_songs_sort_number() == 1){
                        sort_name.setChecked(true);
                    }else if (artist.getArtist_details_songs_sort_number() == 2 ){
                        sort_album.setChecked(true);
                    }else if (artist.getArtist_details_songs_sort_number() == 3 ){
                        sort_duration.setChecked(true);
                    }else if (artist.getArtist_details_songs_sort_number() == 4 ){
                        sort_year.setChecked(true);
                    }else if (artist.getArtist_details_songs_sort_number() == 5 ){
                        sort_date.setChecked(true);
                    }

                    if (artist.isArtist_details_songs_ascending()){
                        sort_ascending.setChecked(true);
                    }else {
                        sort_ascending.setChecked(false);
                    }



                    popupMenu.setOnMenuItemClickListener(new androidx.appcompat.widget.PopupMenu.OnMenuItemClickListener() {
                        @Override
                        public boolean onMenuItemClick(MenuItem item) {

                            switch (item.getItemId()){
                                case R.id.artist_details_songs_sort_item1:

                                    if(!artist.isArtist_details_songs_ascending()  && artist.getArtist_details_songs_sort_number()!= 1){
                                        artist.setArtist_details_songs_sort_number(1);
                                        sortSongs();
                                        reverseSongs(); }

                                    if(artist.getArtist_details_songs_sort_number() != 1){
                                        artist.setArtist_details_songs_sort_number(1);
                                        sortSongs();
                                    }

                                    break;
                                case R.id.artist_details_songs_sort_item2:
                                    item.setChecked(true);
                                    if(!artist.isArtist_details_songs_ascending() && artist.getArtist_details_songs_sort_number() != 2){
                                        artist.setArtist_details_songs_sort_number(2);
                                        sortSongs();
                                        reverseSongs(); }

                                    if(artist.getArtist_details_songs_sort_number() != 2){
                                        artist.setArtist_details_songs_sort_number(2);
                                        sortSongs();
                                    }

                                    break;
                                case R.id.artist_details_songs_sort_item3:
                                    item.setChecked(true);

                                    if(!artist.isArtist_details_songs_ascending() && artist.getArtist_details_songs_sort_number() != 3){
                                        artist.setArtist_details_songs_sort_number(3);
                                        sortSongs();
                                        reverseSongs(); }

                                    if(artist.getArtist_details_songs_sort_number()!= 3){
                                        artist.setArtist_details_songs_sort_number(3);
                                        sortSongs();
                                    }

                                    break;
                                case R.id.artist_details_songs_sort_item4:
                                    item.setChecked(true);

                                    if(!artist.isArtist_details_songs_ascending() && artist.getArtist_details_songs_sort_number()!= 4){
                                        artist.setArtist_details_songs_sort_number(4);
                                        sortSongs();
                                        reverseSongs(); }

                                    if(artist.getArtist_details_songs_sort_number() != 4){
                                        artist.setArtist_details_songs_sort_number(4);
                                        sortSongs();
                                    }

                                    break;
                                case R.id.artist_details_songs_sort_item5:
                                    item.setChecked(true);

                                    if(!artist.isArtist_details_songs_ascending() && artist.getArtist_details_songs_sort_number() != 5){
                                        artist.setArtist_details_songs_sort_number(5);
                                        sortSongs();
                                        reverseSongs(); }

                                    if(artist.getArtist_details_songs_sort_number()!= 5){
                                        artist.setArtist_details_songs_sort_number(5);
                                        sortSongs();
                                    }


                                    break;
                                case R.id.artist_details_songs_sort_item6:

                                    if (artist.isArtist_details_songs_ascending()){
                                        artist.setArtist_details_songs_ascending(false);
                                    }else {
                                        artist.setArtist_details_songs_ascending(true);
                                    }
                                    if (item.isChecked()){
                                        item.setChecked(false);
                                    }else {
                                        item.setChecked(true);
                                    }
                                    reverseSongs();
                                    break;


                                default:
                                    break;
                            }

                            return false;
                        }
                    });
                    popupMenu.show();
                }
            }
            );

            artistDetailsSecondHeaderViewHolder.second_header_shuffle.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {

                    ArrayList<Song> songs2 = new ArrayList<Song>();
                    songs2.clear();
                    songs2.addAll(artistSongs);

                    musicService.setCurrentQueue(songs2);
                    musicService.shuffleAll();
                    musicService.setCurrentQueueItemPosition(0);

                    musicService.loadSong(musicService.getCurrentQueue().get(0));
                    musicService.play();
                    shuffle = true;
                    try{
                        adapterCallback.onMethodCallback();
                    }catch (ClassCastException | ExecutionException | InterruptedException ignored){
                    }
                }
            });

            artistDetailsSecondHeaderViewHolder.second_header_play.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    shuffle = false;

                    ArrayList<Song> songs2 = new ArrayList<Song>();
                    songs2.clear();
                    songs2.addAll(artistSongs);
                    musicService.setCurrentQueue(songs2);
                    musicService.setCurrentQueueItemPosition(0);

                    musicService.loadSong(musicService.getCurrentQueue().get(0));
                    musicService.play();
                    try{
                        adapterCallback.onMethodCallback();
                    }catch (ClassCastException | ExecutionException | InterruptedException ignored){
                    }

                    }


            });


        }else if (holder instanceof ArtistDetailsAlbumViewHolder){
            final ArtistDetailsAlbumViewHolder artistDetailsAlbumViewHolder = (ArtistDetailsAlbumViewHolder) holder;

            artistDetailsAlbumAdapter = new ArtistDetailsAlbumAdapter(context, artistAlbums , musicService);
            artistDetailsAlbumViewHolder.albums_recycler_view.setAdapter(artistDetailsAlbumAdapter);


        }else if (holder instanceof ArtistDetailsSongItemViewHolder){
            final ArtistDetailsSongItemViewHolder artistDetailsSongItemViewHolder = (ArtistDetailsSongItemViewHolder) holder;
            final Song song = artistSongs.get(position-3);

            artistDetailsSongItemViewHolder.songName.setText(song.getSongName());
            artistDetailsSongItemViewHolder.albumName.setText(song.getAlbumName());
            artistDetailsSongItemViewHolder.songDuration.setText(song.getDurationLabel(context));



        }

    }

    private void sortSongs() {
        if (artist.getArtist_details_songs_sort_number() == 1){
            Collections.sort(artistSongs, new Comparator<Song>() {
                @Override
                public int compare(Song s1, Song s2) {
                    String string1 = s1.getSongName();
                    String string2 = s2.getSongName();
                    return string1.compareToIgnoreCase(string2);
                }
            });
        }else if (artist.getArtist_details_songs_sort_number() == 2){
            defaultSortSongs();
        }else if (artist.getArtist_details_songs_sort_number() == 3){
            Collections.sort(artistSongs, new Comparator<Song>() {
                @Override
                public int compare(Song s1, Song s2) {
                    Integer string1 = Integer.valueOf(s1.getDuration());
                    Integer string2 = Integer.valueOf(s2.getDuration());
                    return string1.compareTo(string2);
                }
            });

        }else if (artist.getArtist_details_songs_sort_number() == 4){
            Collections.sort(artistSongs, new Comparator<Song>() {
                @Override
                public int compare(Song s1, Song s2) {
                    String string1 = s1.getSongName();
                    String string2 = s2.getSongName();
                    return string1.compareToIgnoreCase(string2);
                }
            });

            Collections.sort(artistSongs, new Comparator<Song>() {

                @Override
                public int compare(Song s1, Song s2) {
                    Integer string1 = s1.getSongYear();
                    Integer string2 = s2.getSongYear();
                    return string1.compareTo(string2);
                }
            });


        }else if (artist.getArtist_details_songs_sort_number() == 5){
            Collections.sort(artistSongs, new Comparator<Song>() {
                @Override
                public int compare(Song s1, Song s2) {
                    Integer string1 = s1.getSongYear();
                    Integer string2 = s2.getSongYear();
                    return string1.compareTo(string2);
                }
            });


        }
        notifyDataSetChanged();

    }

    private void sortAlbums(){
        if (artist.getArtist_details_albums_sort_number() == 1){
            defaultSortAlbums();
        }else if (artist.getArtist_details_albums_sort_number() == 2){
            defaultSortAlbums();
            Collections.sort(artistAlbums, new Comparator<Album>() {
                @Override
                public int compare(Album a1, Album a2) {
                    Integer s1 = a1.getYear();
                    Integer s2 = a2.getYear();
                    return s1.compareTo(s2);
                }
            });
        }else if (artist.getArtist_details_albums_sort_number() == 3){
            defaultSortAlbums();
            Collections.sort(artistAlbums, new Comparator<Album>() {
                @Override
                public int compare(Album a1, Album a2) {
                    Integer s1 = Integer.valueOf(a1.getAlbumDuration());
                    Integer s2 = Integer.valueOf(a2.getAlbumDuration());
                    return s1.compareTo(s2);
                }
            });

        }else if (artist.getArtist_details_albums_sort_number() == 4){
            defaultSortAlbums();
            Collections.sort(artistAlbums, new Comparator<Album>() {
                @Override
                public int compare(Album a1, Album a2) {
                    Integer s1 = a1.getNumSong();
                    Integer s2 = a2.getNumSong();
                    return s1.compareTo(s2);
                }
            });


        }else if (artist.getArtist_details_albums_sort_number() == 5){
            defaultSortAlbums();
//            Collections.sort(artistAlbums, new Comparator<AlbumModel>() {
//                @Override
//                public int compare(AlbumModel a1, AlbumModel a2) {
//                    Integer s1 = a1.numSong;
//                    Integer s2 = a2.numSong;
//                    return s1.compareTo(s2);
//                }
//            });


        }
        notifyDataSetChanged();
    }

    private void defaultSortSongs(){
        Collections.sort(artistSongs, new Comparator<Song>() {
            @Override
            public int compare(Song a1, Song a2) {
                Integer s1 = a1.getTrackNumber();
                Integer s2 = a2.getTrackNumber();
                return s1.compareTo(s2);
            }
        });

        Collections.sort(artistSongs, new Comparator<Song>() {
            @Override
            public int compare(Song a1, Song a2) {
                Integer s1 = a1.getDiscNumber();
                Integer s2 = a2.getDiscNumber();
                return s1.compareTo(s2);
            }
        });

        Collections.sort(artistSongs, new Comparator<Song>() {
            @Override
            public int compare(Song a1, Song a2) {
                String s1 = a1.getAlbumName();
                String s2 = a2.getAlbumName();
                return s1.compareToIgnoreCase(s2);
            }
        });

    }

    private void defaultSortAlbums(){
        Collections.sort(artistAlbums, new Comparator<Album>() {
            @Override
            public int compare(Album o1, Album o2) {
                String s1 = o1.getAlbumName();
                String s2 = o2.getAlbumName();
                return s1.compareToIgnoreCase(s2);
            }
        });


    }

    private void reverseSongs(){
        Collections.reverse(artistSongs);
        notifyDataSetChanged();
    }

    private void reverseAlbums(){
        Collections.reverse(artistAlbums);
        notifyDataSetChanged();
    }


    @Override
    public int getItemCount() {
        return artistSongs.size() + 3;
    }

    @Override
    public int getItemViewType(int position) {
        if (position == 0) {
            return TYPE_FIRST_HEADER;
        }else if (position == 1){
            return TYPE_HORIZONTAL_VIEW;
        }else if (position == 2){
            return TYPE_SECOND_HEADER;
        }else{
            return TYPE_VERTICAL_ITEMS;
        }

    }

    @NonNull
    @Override
    public String getSectionName(int position) {
        if (position == 0 || position == 1 || position == 2){
            return "";
        }else{
            return String.valueOf(artistSongs.get(position-3).getSongName().toUpperCase().charAt(0));
        }
    }


    private static class ArtistDetailsFirstHeaderViewHolder extends RecyclerView.ViewHolder{
        ImageView first_header_sort, first_header_shuffle, first_header_play;
        TextView first_header_albumsNum;

        public ArtistDetailsFirstHeaderViewHolder(@NonNull View itemView) {
            super(itemView);
            first_header_sort = (ImageView) itemView.findViewById(R.id.artists_details_albums_sort);
            first_header_shuffle = itemView.findViewById(R.id.artists_details_albums_header_shuffle);
            first_header_play = itemView.findViewById(R.id.artists_details_albums_header_play);
            first_header_albumsNum = itemView.findViewById(R.id.artists_details_albumsNum);
        }
    }

    private static class ArtistDetailsAlbumViewHolder extends RecyclerView.ViewHolder{
        RecyclerView albums_recycler_view;
        Context mContext;

        public ArtistDetailsAlbumViewHolder(@NonNull View itemView, Context context) {
            super(itemView);
            albums_recycler_view = itemView.findViewById(R.id.artists_details_albums_recycler_view);
            this.mContext = context;

            LinearLayoutManager linearLayoutManager = new LinearLayoutManager(mContext, LinearLayoutManager.HORIZONTAL, false);
            albums_recycler_view.setLayoutManager(linearLayoutManager);
            albums_recycler_view.setItemAnimator(new DefaultItemAnimator());

            albums_recycler_view.addItemDecoration(new EqualSpacingItemDecoration(30, EqualSpacingItemDecoration.HORIZONTAL));


        }
    }

    private static class ArtistDetailsSecondHeaderViewHolder extends RecyclerView.ViewHolder{
        ImageView second_header_sort, second_header_shuffle, second_header_play;
        TextView second_header_songsNum;

        public ArtistDetailsSecondHeaderViewHolder(@NonNull View itemView) {
            super(itemView);
            second_header_sort = (ImageView) itemView.findViewById(R.id.artists_details_songs_sort);
            second_header_shuffle = itemView.findViewById(R.id.artists_details_songs_header_shuffle);
            second_header_play = itemView.findViewById(R.id.artists_details_songs_header_play);
            second_header_songsNum = itemView.findViewById(R.id.artists_details_songsNum);
        }
    }

    private class ArtistDetailsSongItemViewHolder extends RecyclerView.ViewHolder implements View.OnClickListener {
        TextView songName, optionsMenu, songDuration, albumName;
        ImageView smallArt;
        MusicService mservice;
        ArrayList<Song> _artistSongs;

        public ArtistDetailsSongItemViewHolder(@NonNull View itemView, MusicService xmusicService, ArrayList<Song> xArtistSongs) {
            super(itemView);
            mservice = xmusicService;
            _artistSongs = xArtistSongs;

            songName = itemView.findViewById(R.id.artists_details_song_name);
            optionsMenu = itemView.findViewById(R.id.artists_details_song_textViewOptions);
            songDuration = itemView.findViewById(R.id.artists_details_song_duration);
            albumName = itemView.findViewById(R.id.artists_details_song_album_name);

            smallArt = itemView.findViewById(R.id.artists_details_song_art);

            itemView.setOnClickListener(this);
        }

        @Override
        public void onClick(View v) {
            int currentPosition = getAdapterPosition() - 3;

            ArrayList<Song> songs2 = new ArrayList<Song>();
            songs2.clear();
            songs2.addAll(artistSongs);

            musicService.setCurrentQueueItemPosition(currentPosition);
            musicService.setCurrentQueue(songs2);

            try{
                adapterCallback.onMethodCallback();
            }catch (ClassCastException | InterruptedException | ExecutionException ignored){
            }

            musicService.loadSong(musicService.getCurrentQueueItem());
            musicService.play();

        }
    }

}
