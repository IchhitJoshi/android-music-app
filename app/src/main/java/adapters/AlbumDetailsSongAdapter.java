package adapters;

import android.app.Dialog;
import android.content.Context;
import android.view.Gravity;
import android.view.LayoutInflater;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.appcompat.widget.PopupMenu;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.bumptech.glide.Glide;
import services.MusicService;
import com.example.demo.R;
import com.simplecityapps.recyclerview_fastscroll.views.FastScrollRecyclerView;

import java.util.ArrayList;
import java.util.Collections;
import java.util.Comparator;
import java.util.concurrent.ExecutionException;

import models.Album;
import models.Song;


import static com.example.demo.views.Home.shuffle;

public class AlbumDetailsSongAdapter extends RecyclerView.Adapter<RecyclerView.ViewHolder> implements FastScrollRecyclerView.SectionedAdapter{
    private final ArrayList<Song> _albumSongs;
    Album album;
    private final Context context;
    MusicService musicService;
    ArrayList<Album> discs = new ArrayList<>();

    private final Dialog songInfoDialog;

    private static SongAdapter.AdapterCallback adapterCallback;

    private static final int TYPE_HEADER = 0;
    private static final int TYPE_ITEM = 1;

    private final boolean showDisc;

    AlbumDetailsDiscSongAdapter albumDetailsDiscSongAdapter;

    public AlbumDetailsSongAdapter(Context context, MusicService musicService, Album album, boolean mShowDisc){
        this.context = context;
        this.musicService = musicService;
        this.album = album;
        this._albumSongs = album.getAlbumSongs();
        this.showDisc = mShowDisc;

        songInfoDialog = new Dialog(context);

        if(showDisc){
            Album disc = new Album();
            for(int i = 1; i < album.getDiscNumber() + 1; i++){
                ArrayList<Song> temp = new ArrayList<>();
                for(int j = 0; j < _albumSongs.size(); j++){
                    if(_albumSongs.get(j).getDiscNumber() == i){
                        temp.add(_albumSongs.get(j));
                    }
                }
                if(!temp.isEmpty() && temp != null){
                    disc = new Album(temp);
                    disc.setAlbumName("Disc " + i);
                    disc.setNumSong(temp.size());
                    disc.setArtistName(temp.get(0).getAlbumArtistName());
                    disc.setShowTrackNumber(true);
                    disc.setShowArtistName(false);
                    disc.setAlbumId(temp.get(0).getAlbumId());
                    disc.setDiscNumber(i);

                    discs.add(disc);
//                    disc.setAlbumSongs(temp);
                }

            }

            for(int i= 0;i < discs.size(); i++){
                if(discs.get(i).getDiscNumber() == 0 || discs.get(i).getAlbumSongs() == null){
                    discs.remove(discs.get(i));
                }
            }
        }



        try{
            adapterCallback= ((SongAdapter.AdapterCallback) context);
        }catch (ClassCastException ignored) {
        }
    }


    @NonNull
    @Override
    public RecyclerView.ViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {


        if (viewType == TYPE_ITEM){

            if(showDisc){
                View myView = LayoutInflater.from(context).inflate(R.layout.albums_details_disc_header, parent, false);
                return new AlbumDetailsDiscHolder(myView, musicService, _albumSongs, context);

            }else{
                View myView = LayoutInflater.from(context).inflate(R.layout.albums_details_song_item, parent, false);
                return new AlbumDetailsSongHolder(myView, musicService, _albumSongs);
            }

        } else if (viewType == TYPE_HEADER){
            View myView = LayoutInflater.from(context).inflate(R.layout.albums_details_tab_header, parent, false);
            return new AlbumDetailsHeaderViewHolder(myView);

        }
        else return null;
    }

    @Override
    public void onBindViewHolder(@NonNull RecyclerView.ViewHolder holder, final int position) {
        if (holder instanceof AlbumDetailsHeaderViewHolder){
            final AlbumDetailsHeaderViewHolder albumDetailsHeaderViewHolder = (AlbumDetailsHeaderViewHolder) holder;

            albumDetailsHeaderViewHolder.header_album_info.setText(album.getNumSong() + "•" + album.getYear() + "•" + album.getAlbumDurationLabel(context));

            albumDetailsHeaderViewHolder.header_sort.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    androidx.appcompat.widget.PopupMenu popupMenu = new androidx.appcompat.widget.PopupMenu(context,  albumDetailsHeaderViewHolder.header_sort);
                    popupMenu.inflate(R.menu.album_details_songs_sort_options);
                    MenuItem sort_name = popupMenu.getMenu().findItem(R.id.album_details_songs_sort_item1);
                    MenuItem sort_track = popupMenu.getMenu().findItem(R.id.album_details_songs_sort_item2);
                    MenuItem sort_duration = popupMenu.getMenu().findItem(R.id.album_details_songs_sort_item3);
                    MenuItem sort_year = popupMenu.getMenu().findItem(R.id.album_details_songs_sort_item4);
                    MenuItem sort_date = popupMenu.getMenu().findItem(R.id.album_details_songs_sort_item5);
                    MenuItem sort_ascending = popupMenu.getMenu().findItem(R.id.album_details_songs_sort_item6);


                    if (album.getAlbum_details_songs_sort_number() == 1){
                        sort_name.setChecked(true);
                    }else if (album.getAlbum_details_songs_sort_number() == 2 ){
                        sort_track.setChecked(true);
                    }else if (album.getAlbum_details_songs_sort_number() == 3 ){
                        sort_duration.setChecked(true);
                    }else if (album.getAlbum_details_songs_sort_number() == 4 ){
                        sort_year.setChecked(true);
                    }else if (album.getAlbum_details_songs_sort_number()== 5 ){
                        sort_date.setChecked(true);
                    }

                    if (album.isAlbum_details_songs_ascending()){
                        sort_ascending.setChecked(true);
                    }else {
                        sort_ascending.setChecked(false);
                    }



                    popupMenu.setOnMenuItemClickListener(new androidx.appcompat.widget.PopupMenu.OnMenuItemClickListener() {
                        @Override
                        public boolean onMenuItemClick(MenuItem item) {

                            switch (item.getItemId()){
                                case R.id.album_details_songs_sort_item1:

                                    if(!album.isAlbum_details_songs_ascending()  && album.getAlbum_details_songs_sort_number() != 1){
                                        album.setAlbum_details_songs_sort_number(1);
                                        sort();
                                        reverse(); }

                                    if(album.getAlbum_details_songs_sort_number() != 1){
                                        album.setAlbum_details_songs_sort_number(1);
                                        sort();
                                    }

                                    break;
                                case R.id.album_details_songs_sort_item2:
                                    item.setChecked(true);
                                    if(!album.isAlbum_details_songs_ascending() && album.getAlbum_details_songs_sort_number()!= 2){
                                        album.setAlbum_details_songs_sort_number(2);
                                        sort();
                                        reverse(); }

                                    if(album.getAlbum_details_songs_sort_number() != 2){
                                        album.setAlbum_details_songs_sort_number(2);
                                        sort();
                                    }

                                    break;
                                case R.id.album_details_songs_sort_item3:
                                    item.setChecked(true);

                                    if(!album.isAlbum_details_songs_ascending()&& album.getAlbum_details_songs_sort_number()!= 3){
                                        album.setAlbum_details_songs_sort_number(3);
                                        sort();
                                        reverse(); }

                                    if(album.getAlbum_details_songs_sort_number() != 3){
                                        album.setAlbum_details_songs_sort_number(3);
                                        sort();
                                    }

                                    break;
                                case R.id.album_details_songs_sort_item4:
                                    item.setChecked(true);

                                    if(!album.isAlbum_details_songs_ascending() && album.getAlbum_details_songs_sort_number() != 4){
                                        album.setAlbum_details_songs_sort_number(4);
                                        sort();
                                        reverse(); }

                                    if(album.getAlbum_details_songs_sort_number() != 4){
                                        album.setAlbum_details_songs_sort_number(4);
                                        sort();
                                    }

                                    break;
                                case R.id.album_details_songs_sort_item5:
                                    item.setChecked(true);

                                    if(!album.isAlbum_details_songs_ascending() && album.getAlbum_details_songs_sort_number() != 5){
                                        album.setAlbum_details_songs_sort_number(5);
                                        sort();
                                        reverse(); }

                                    if(album.getAlbum_details_songs_sort_number() != 5){
                                        album.setAlbum_details_songs_sort_number(5);
                                        sort();
                                    }


                                    break;
                                case R.id.album_details_songs_sort_item6:

                                    if (album.isAlbum_details_songs_ascending()){
                                        album.setAlbum_details_songs_ascending(false);
                                    }else {
                                        album.setAlbum_details_songs_ascending(true);
                                    }
                                    if (item.isChecked()){
                                        item.setChecked(false);
                                    }else {
                                        item.setChecked(true);
                                    }
                                    reverse();
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

            albumDetailsHeaderViewHolder.header_shuffle.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {

                    ArrayList<Song> songs2 = new ArrayList<Song>();
                    songs2.clear();
                    songs2.addAll(_albumSongs);


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

            albumDetailsHeaderViewHolder.header_play.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    shuffle = false;

                    ArrayList<Song> songs2 = new ArrayList<Song>();
                    songs2.clear();
                    songs2.addAll(_albumSongs);
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


        } else if (holder instanceof AlbumDetailsDiscHolder){
            final AlbumDetailsDiscHolder albumDetailsDiscHolder = (AlbumDetailsDiscHolder) holder;
            final Album disc = discs.get(position - 1);

            albumDetailsDiscHolder.discNumber.setText("Disc " + disc.getDiscNumber());

            albumDetailsDiscSongAdapter = new AlbumDetailsDiscSongAdapter(context, disc, musicService, _albumSongs);
            albumDetailsDiscHolder.recyclerView.setAdapter(albumDetailsDiscSongAdapter);


        }
        else if (holder instanceof AlbumDetailsSongHolder){

            final AlbumDetailsSongHolder albumDetailsSongHolder = (AlbumDetailsSongHolder) holder;
            final Song song = _albumSongs.get(position-1);

            albumDetailsSongHolder.optionsMenu.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    PopupMenu popupMenu = new PopupMenu(context, albumDetailsSongHolder.optionsMenu, Gravity.RIGHT);
                    popupMenu.inflate(R.menu.songs_menu);


                    popupMenu.setOnMenuItemClickListener(new PopupMenu.OnMenuItemClickListener() {
                        @Override
                        public boolean onMenuItemClick(MenuItem item) {

                            switch (item.getItemId()){
                                case R.id.item1:
                                    Toast.makeText(context, "Playing Next - " + song.getSongName(), Toast.LENGTH_LONG).show();
                                    if(musicService.isEmpty()){
                                        ArrayList<Song> songs2 = new ArrayList<Song>();
                                        songs2.clear();
                                        songs2.addAll(album.getAlbumSongs());

                                        musicService.setCurrentQueueItemPosition(position-1);
                                        musicService.setCurrentQueue(songs2);

                                        musicService.loadSong(musicService.getCurrentQueueItem());
                                        musicService.play();

                                        try{
                                            adapterCallback.onMethodCallback();
                                        }catch (ClassCastException | ExecutionException | InterruptedException ignored){
                                        }

                                    }else{
                                        musicService.playNextQueueItem(song);
                                    }

                                    break;
                                case R.id.addToQueue:
                                    Toast.makeText(context, "Added to Queue", Toast.LENGTH_LONG).show();
                                    musicService.addToQueue(song);
                                    break;
                                case R.id.newPlaylist:
                                    Toast.makeText(context, "New Playlist", Toast.LENGTH_LONG).show();
                                    break;
                                case R.id.Favorites:
                                    Toast.makeText(context, "Added to Favorites", Toast.LENGTH_LONG).show();
                                    break;
                                case R.id.item3:
                                    Toast.makeText(context, "Edit Song Info", Toast.LENGTH_LONG).show();
                                    break;
                                case R.id.item4:

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
                                case R.id.recyclerGoToArtist:
                                    Toast.makeText(context, "Go to Artist", Toast.LENGTH_LONG).show();
                                    break;
                                case R.id.recyclerGoToAlbum:
                                    Toast.makeText(context, "Go to Album", Toast.LENGTH_LONG).show();
                                    break;
                                case R.id.recyclerGoToGenre:
                                    Toast.makeText(context, "Go to Genre", Toast.LENGTH_LONG).show();
                                    break;
                                case R.id.item6:
                                    Toast.makeText(context, "Deleted", Toast.LENGTH_LONG).show();
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

            if (album.isShowTrackNumber()){
                albumDetailsSongHolder.trackNumber.setVisibility(View.VISIBLE);
                albumDetailsSongHolder.smallArt.setVisibility(View.GONE);
                albumDetailsSongHolder.trackNumber.setText(String.valueOf(song.getTrackNumber()));
            }else{
                albumDetailsSongHolder.trackNumber.setVisibility(View.GONE);
                albumDetailsSongHolder.smallArt.setVisibility(View.VISIBLE);
                Glide.with(context).load(song.getSongUrl()).into(albumDetailsSongHolder.smallArt);
            }

            if (album.isShowArtistName()){
                albumDetailsSongHolder.artistName.setVisibility(View.VISIBLE);
                albumDetailsSongHolder.artistName.setText(song.getArtistName());
            }else{
                albumDetailsSongHolder.artistName.setVisibility(View.GONE);
            }


            albumDetailsSongHolder.songName.setText(song.getSongName());

            albumDetailsSongHolder.songDuration.setText(song.getDurationLabel(context));


        }


    }

    private void sort() {
        if (album.getAlbum_details_songs_sort_number() == 1){
            Collections.sort(_albumSongs, new Comparator<Song>() {
                @Override
                public int compare(Song s1, Song s2) {
                    String string1 = s1.getSongName();
                    String string2 = s2.getSongName();
                    return string1.compareToIgnoreCase(string2);
                }
            });
        }else if (album.getAlbum_details_songs_sort_number() == 2){
          defaultSort();
        }else if (album.getAlbum_details_songs_sort_number() == 3){
            Collections.sort(_albumSongs, new Comparator<Song>() {
                @Override
                public int compare(Song s1, Song s2) {
                    Integer string1 = Integer.valueOf(s1.getDuration());
                    Integer string2 = Integer.valueOf(s2.getDuration());
                    return string1.compareTo(string2);
                }
            });

        }else if (album.getAlbum_details_songs_sort_number() == 4){

            Collections.sort(_albumSongs, new Comparator<Song>() {
                @Override
                public int compare(Song s1, Song s2) {
                    String string1 = s1.getSongName();
                    String string2 = s2.getSongName();
                    return string1.compareToIgnoreCase(string2);
                }
            });

            Collections.sort(_albumSongs, new Comparator<Song>() {
                @Override
                public int compare(Song s1, Song s2) {
                    Integer string1 = s1.getSongYear();
                    Integer string2 = s2.getSongYear();
                    return string1.compareTo(string2);
                }
            });

        }else if (album.getAlbum_details_songs_sort_number() == 5){
            Collections.sort(_albumSongs, new Comparator<Song>() {
                @Override
                public int compare(Song s1, Song s2) {
                    Long string1 = s1.getDateAdded();
                    Long string2 = s2.getDateAdded();
                    return string1.compareTo(string2);
                }
            });


        }
        notifyDataSetChanged();

    }
    private void defaultSort(){
        Collections.sort(_albumSongs, new Comparator<Song>() {
            @Override
            public int compare(Song s1, Song s2) {
                Integer string1 = s1.getTrackNumber();
                Integer string2 = s2.getTrackNumber();
                return string1.compareTo(string2);
            }
        });

        Collections.sort(_albumSongs, new Comparator<Song>() {
            @Override
            public int compare(Song a1, Song a2) {
                Integer s1 = a1.getDiscNumber();
                Integer s2 = a2.getDiscNumber();
                return s1.compareTo(s2);
            }
        });



    }

    private void reverse(){
        Collections.reverse(_albumSongs);
        notifyDataSetChanged();
    }


    @Override
    public int getItemViewType(int position) {
        if (position == 0) {
            return TYPE_HEADER;
        }
        return TYPE_ITEM;
    }

    @Override
    public int getItemCount() {
        if(showDisc){
            return discs.size() + 1;
        }else{
            return _albumSongs.size()+1;
        }

    }

    @NonNull
    @Override
    public String getSectionName(int position) {
        if (position == 0){
            return "";
        }else{
            return String.valueOf(_albumSongs.get(position-1).getSongName().toUpperCase().charAt(0));
            }
        }


    private static class AlbumDetailsHeaderViewHolder extends RecyclerView.ViewHolder{
        ImageView header_sort, header_shuffle, header_play;
        TextView header_album_info;

        public AlbumDetailsHeaderViewHolder(@NonNull View itemView) {
            super(itemView);
            header_sort = (ImageView) itemView.findViewById(R.id.albums_details_songs_sort);
            header_shuffle = itemView.findViewById(R.id.albums_details_songs_header_shuffle);
            header_play = itemView.findViewById(R.id.albums_details_songs_header_play);
            header_album_info = itemView.findViewById(R.id.albums_details_album_info);
        }
    }

    public class AlbumDetailsDiscHolder extends RecyclerView.ViewHolder{
        TextView discNumber;
        MusicService mservice;
        ArrayList<Song> _albumSongs;
        RecyclerView recyclerView;
        Context xContext;

        public AlbumDetailsDiscHolder(@NonNull View itemView, MusicService xmusicService, ArrayList<Song> xAlbumSongs, Context mContext){
            super(itemView);
            mservice = xmusicService;
            _albumSongs = xAlbumSongs;

            discNumber = itemView.findViewById(R.id.album_details_disc_number);
            recyclerView = itemView.findViewById(R.id.album_details_section_recycler_view);

            LinearLayoutManager linearLayoutManager = new LinearLayoutManager(context);
            recyclerView.setLayoutManager(linearLayoutManager);





        }



    }


    public class AlbumDetailsSongHolder extends RecyclerView.ViewHolder implements View.OnClickListener {
        TextView songName, optionsMenu, trackNumber, songDuration, artistName;
        ImageView smallArt;
        MusicService mservice;
        ArrayList<Song> _albumSongs;


        public AlbumDetailsSongHolder(@NonNull View itemView, MusicService xmusicService, ArrayList<Song> xAlbumSongs) {
            super(itemView);

            mservice = xmusicService;
            _albumSongs = xAlbumSongs;

            songName = itemView.findViewById(R.id.albums_details_song);
            trackNumber = itemView.findViewById(R.id.albums_details_track_number);
            optionsMenu = itemView.findViewById(R.id.albums_details_textViewOptions);
            songDuration = itemView.findViewById(R.id.albums_details_song_duration);
            artistName = itemView.findViewById(R.id.albums_details_artist);

            smallArt = itemView.findViewById(R.id.albums_details_smallArt);

            itemView.setOnClickListener(this);
        }

        public void onClick(View view) {

            int currentPosition = getAdapterPosition() - 1;
            ArrayList<Song> songs2 = new ArrayList<Song>();
            songs2.clear();
            songs2.addAll(_albumSongs);

            musicService.setCurrentQueueItemPosition(currentPosition);
            musicService.setCurrentQueue(songs2);

            try{
                adapterCallback.onMethodCallback();
            }catch (ClassCastException | InterruptedException | ExecutionException ignored){
            }

            musicService.loadSong(songs2.get(currentPosition));
            musicService.play();

        }


    }
}
