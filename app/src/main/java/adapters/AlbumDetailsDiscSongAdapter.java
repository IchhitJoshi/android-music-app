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
import androidx.recyclerview.widget.RecyclerView;

import com.bumptech.glide.Glide;
import com.example.demo.R;

import java.util.ArrayList;
import java.util.concurrent.ExecutionException;

import models.Album;
import models.Song;
import services.MusicService;

public class AlbumDetailsDiscSongAdapter extends RecyclerView.Adapter<RecyclerView.ViewHolder> {

    private final Context context;
    Album disc;

    ArrayList<Song> discSongs;
    ArrayList<Song> albumSongs;

    MusicService musicService;
    private static SongAdapter.AdapterCallback adapterCallback;

    public AlbumDetailsDiscSongAdapter (Context context, Album xDisc, MusicService musicService, ArrayList<Song> _albumSongs){

        this.context = context;
        this.disc = xDisc;
        this.discSongs = xDisc.getAlbumSongs();
        this.musicService = musicService;
        this.albumSongs = _albumSongs;
//        this.msongsLoader = songsLoader;
//        this.albumAdapterSortCallback = aSortCallback;

        try{
            adapterCallback= ((SongAdapter.AdapterCallback) context);
        }catch (ClassCastException ignored) {
        }
    }



    @NonNull
    @Override
    public RecyclerView.ViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View myView = LayoutInflater.from(context).inflate(R.layout.albums_details_song_item, parent, false);
        return new AlbumDetailsDiscSongHolder(myView, discSongs, musicService);
    }

    @Override
    public void onBindViewHolder(@NonNull RecyclerView.ViewHolder holder, final int position) {
        final AlbumDetailsDiscSongHolder albumDetailsDiscSongHolder = (AlbumDetailsDiscSongHolder) holder;
        final Song song = discSongs.get(position);

        albumDetailsDiscSongHolder.optionsMenu.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                PopupMenu popupMenu = new PopupMenu(context, albumDetailsDiscSongHolder.optionsMenu, Gravity.RIGHT);
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
                                    songs2.addAll(discSongs);

                                    musicService.setCurrentQueueItemPosition(position);
                                    musicService.setCurrentQueue(songs2);

                                    musicService.loadSong(musicService.getCurrentQueueItem());
                                    musicService.play();
//
//                                    try{
//                                        adapterCallback.onMethodCallback();
//                                    }catch (ClassCastException | ExecutionException | InterruptedException ignored){
//                                    }

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

        if (disc.isShowTrackNumber()){
            albumDetailsDiscSongHolder.trackNumber.setVisibility(View.VISIBLE);
            albumDetailsDiscSongHolder.smallArt.setVisibility(View.GONE);
            albumDetailsDiscSongHolder.trackNumber.setText(String.valueOf(song.getTrackNumber()));
        }else{
            albumDetailsDiscSongHolder.trackNumber.setVisibility(View.GONE);
            albumDetailsDiscSongHolder.smallArt.setVisibility(View.VISIBLE);
            Glide.with(context).load(song.getSongUrl()).into(albumDetailsDiscSongHolder.smallArt);
        }

        if (disc.isShowArtistName()){
            albumDetailsDiscSongHolder.artistName.setVisibility(View.VISIBLE);
            albumDetailsDiscSongHolder.artistName.setText(song.getArtistName());
        }else{
            albumDetailsDiscSongHolder.artistName.setVisibility(View.GONE);
        }


        albumDetailsDiscSongHolder.songName.setText(song.getSongName());

        albumDetailsDiscSongHolder.songDuration.setText(song.getDurationLabel(context));

    }

    @Override
    public int getItemCount() {
        return discSongs.size();
    }

    public class AlbumDetailsDiscSongHolder extends RecyclerView.ViewHolder implements View.OnClickListener{
        TextView songName, optionsMenu, trackNumber, songDuration, artistName;
        ImageView smallArt;
        MusicService mservice;
        ArrayList<Song> _discSongs;


        public AlbumDetailsDiscSongHolder(@NonNull View itemView, ArrayList<Song> _songs, MusicService xService) {
            super(itemView);

            mservice = xService;
            _discSongs = _songs;

            songName = itemView.findViewById(R.id.albums_details_song);
            trackNumber = itemView.findViewById(R.id.albums_details_track_number);
            optionsMenu = itemView.findViewById(R.id.albums_details_textViewOptions);
            songDuration = itemView.findViewById(R.id.albums_details_song_duration);
            artistName = itemView.findViewById(R.id.albums_details_artist);

            smallArt = itemView.findViewById(R.id.albums_details_smallArt);

            itemView.setOnClickListener(this);
        }

        @Override
        public void onClick(View v) {
            int currentPosition = getAdapterPosition();

            ArrayList<Song> songs2 = new ArrayList<Song>();
            songs2.clear();
            songs2.addAll(albumSongs);

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
