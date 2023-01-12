package adapters;

import android.annotation.SuppressLint;
import android.app.Dialog;
import android.content.Context;
import android.view.LayoutInflater;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.PopupMenu;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.fragment.app.FragmentManager;
import androidx.fragment.app.FragmentTransaction;
import androidx.recyclerview.widget.RecyclerView;

import com.bumptech.glide.Glide;

import services.MusicService;
import com.example.demo.R;
import com.example.demo.views.MusicFragmentTabs.AlbumsDetailsFragment;
import com.example.demo.views.MusicFragmentTabs.ArtistsDetailsFragment;
import com.simplecityapps.recyclerview_fastscroll.views.FastScrollRecyclerView;

import org.w3c.dom.Text;

import java.util.ArrayList;
import java.util.concurrent.ExecutionException;

import managers.PlaybackManager;
import managers.QueueManager;
import managers.MediaImporter;
import models.Song;

import static com.example.demo.views.Home.shuffle;
import static com.example.demo.views.Home.songs_ascending;
import static com.example.demo.views.Home.songs_sort_number;

public class SongAdapter extends RecyclerView.Adapter<RecyclerView.ViewHolder> implements FastScrollRecyclerView.SectionedAdapter{

    private AdapterCallback adapterCallback;
    private final SongAdapterSortCallback sortCallback;

    private final ArrayList<Song> _songs;
    private final Context context;
//    private final QueueManager mqueueManager;
//    private final PlaybackManager mplaybackManager;
    private final MediaImporter msongsLoader;

    MusicService musicService;

    private static final int TYPE_HEADER = 0;
    private static final int TYPE_ITEM = 1;


    public SongAdapter(Context context, ArrayList<Song> songs, MusicService musicService, MediaImporter mediaImporter, SongAdapterSortCallback scallback) {
        this.context = context;
        this._songs = songs;
//        this.mqueueManager = queueManager;
//        this.mplaybackManager = playbackManager;
        this.msongsLoader = mediaImporter;
        this.musicService = musicService;
        this.sortCallback = scallback;
        try{
           adapterCallback= ((AdapterCallback) context);
        }catch (ClassCastException ignored) {
        }

//        try{
//            sortCallback= ((SongAdapterSortCallback) context);
//        }catch (ClassCastException ignored) {
//        }
    }

    @NonNull
    @Override
    public RecyclerView.ViewHolder onCreateViewHolder(@NonNull ViewGroup viewGroup, int i) {

        if (i == TYPE_ITEM){
            View myView = LayoutInflater.from(context).inflate(R.layout.song_item, viewGroup, false);
            return new SongHolder(myView,musicService,msongsLoader);

        } else if (i == TYPE_HEADER){
            View myView = LayoutInflater.from(context).inflate(R.layout.songs_tab_header, viewGroup, false);
            return new HeaderViewHolder(myView);

        }
        else return null;
    }

    @Override
    public void onBindViewHolder(final RecyclerView.ViewHolder holder, final int i) {

        if (holder instanceof HeaderViewHolder){
            final HeaderViewHolder headerViewHolder = (HeaderViewHolder) holder;


            headerViewHolder.header_shuffle.getBackground().setTint(context.getResources().getColor(R.color.mainTextColor));
            headerViewHolder.header_play.getBackground().setTint(context.getResources().getColor(R.color.mainTextColor));

            headerViewHolder.header_sort.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    androidx.appcompat.widget.PopupMenu popupMenu = new androidx.appcompat.widget.PopupMenu(context, headerViewHolder.header_sort);
                    popupMenu.inflate(R.menu.songs_sort_options_menu);
                    MenuItem sort_name = popupMenu.getMenu().findItem(R.id.songs_sort_item1);
                    MenuItem sort_artist = popupMenu.getMenu().findItem(R.id.songs_sort_item2);
                    MenuItem sort_date = popupMenu.getMenu().findItem(R.id.songs_sort_item3);
                    MenuItem sort_duration = popupMenu.getMenu().findItem(R.id.songs_sort_item4);
                    MenuItem sort_year = popupMenu.getMenu().findItem(R.id.songs_sort_item5);
                    MenuItem sort_album = popupMenu.getMenu().findItem(R.id.songs_sort_item6);
                    MenuItem sort_ascending = popupMenu.getMenu().findItem(R.id.songs_sort_item7);

                    if (songs_sort_number == 1){
                        sort_name.setChecked(true);
                    }else if (songs_sort_number == 2 ){
                        sort_artist.setChecked(true);
                    }else if (songs_sort_number == 3 ){
                        sort_date.setChecked(true);
                    }else if (songs_sort_number == 4 ){
                        sort_duration.setChecked(true);
                    }else if (songs_sort_number == 5 ){
                        sort_year.setChecked(true);
                    }else if (songs_sort_number == 6 ){
                        sort_album.setChecked(true);
                    }

                    if (songs_ascending){
                        sort_ascending.setChecked(true);
                    }else {
                        sort_ascending.setChecked(false);
                    }



                    popupMenu.setOnMenuItemClickListener(new androidx.appcompat.widget.PopupMenu.OnMenuItemClickListener() {
                        @Override
                        public boolean onMenuItemClick(MenuItem item) {

                            switch (item.getItemId()){
                                case R.id.songs_sort_item1:
                                    songs_sort_number = 1;
                                    sort();
                                    break;
                                case R.id.songs_sort_item2:

                                    item.setChecked(true);
                                    songs_sort_number = 2;
                                    sort();
                                    break;
                                case R.id.songs_sort_item3:

                                    item.setChecked(true);
                                    songs_sort_number = 3;
                                    sort();
                                    break;
                                case R.id.songs_sort_item4:

                                    item.setChecked(true);
                                    songs_sort_number = 4;
                                    sort();
                                    break;
                                case R.id.songs_sort_item5:

                                    item.setChecked(true);
                                    songs_sort_number = 5;
                                    sort();
                                    break;
                                case R.id.songs_sort_item6:

                                    item.setChecked(true);
                                    songs_sort_number = 6;
                                    sort();
                                    break;
                                case R.id.songs_sort_item7:

                                    if (songs_ascending){
                                        songs_ascending = false;
                                    }else {
                                        songs_ascending= true;
                                    }
                                    if (item.isChecked()){
                                        item.setChecked(false);
                                    }else {
                                        item.setChecked(true);
                                    }
                                    sort();
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

            headerViewHolder.header_shuffle.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {

                    ArrayList<Song> songs2 = new ArrayList<Song>();
                    songs2.clear();
                    songs2.addAll(_songs);

                    musicService.setCurrentQueueItemPosition(0);
                    musicService.setCurrentQueue(songs2);

                    musicService.shuffleAll();


                    musicService.loadSong(musicService.getCurrentQueue().get(0));
                    musicService.play();
                    shuffle = true;
                    try{
                        adapterCallback.onMethodCallback();
                    }catch (ClassCastException | ExecutionException | InterruptedException ignored){
                    }
                }
            });

            headerViewHolder.header_play.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                        shuffle = false;
                        ArrayList<Song> songs2 = new ArrayList<Song>();
                        songs2.clear();
                        songs2.addAll(_songs);

                        musicService.setCurrentQueueItemPosition(0);
                        musicService.setCurrentQueue(songs2);

                        musicService.loadSong(musicService.getCurrentQueue().get(0));
                        musicService.play();
                        try{
                            adapterCallback.onMethodCallback();
                        }catch (ClassCastException | ExecutionException | InterruptedException ignored){
                        }


                    }


            });


        }
        else if (holder instanceof SongHolder){

            final SongHolder songHolder = (SongHolder) holder;
            final Song song = _songs.get(i-1);

            songHolder.optionsMenu.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(final View v) {
                    PopupMenu popupMenu = new PopupMenu(context, songHolder.optionsMenu);
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
                                        songs2.addAll(_songs);

                                        musicService.setCurrentQueueItemPosition(i-1);
                                        musicService.setCurrentQueue(songs2);

                                        musicService.loadSong(songs2.get(i-1));
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
                                    musicService.addToQueue(_songs.get(i-1));
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
                                    AppCompatActivity activity = (AppCompatActivity) v.getContext();
                                    ArtistsDetailsFragment artistsDetailsFragment  = new ArtistsDetailsFragment(context, musicService.getCurrentArtist(msongsLoader.getArtists(), song.getArtistId()));
                                    FragmentManager manager = activity.getSupportFragmentManager();
                                    FragmentTransaction transaction = manager.beginTransaction();
                                    transaction.replace(R.id.mainFrame,artistsDetailsFragment).addToBackStack(null).commit();
                                    break;
                                case R.id.recyclerGoToAlbum:
                                    Toast.makeText(context, "Go to Album", Toast.LENGTH_LONG).show();
                                    AppCompatActivity activity2 = (AppCompatActivity) v.getContext();
                                    AlbumsDetailsFragment albumsDetailsFragment = new AlbumsDetailsFragment(context, musicService.getCurrentAlbum(msongsLoader.getAlbums(), song.getAlbumId()));
                                    FragmentManager manager2 = activity2.getSupportFragmentManager();
                                    FragmentTransaction transaction2 = manager2.beginTransaction();
                                    transaction2.replace(R.id.mainFrame,albumsDetailsFragment).addToBackStack(null).commit();
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

            songHolder.songName.setText(song.getSongName());
            songHolder.artistName.setText(song.getArtistName() + " • " + song.getDurationLabel(context));

            Glide.with(context).load(song.getSongUrl()).into(songHolder.smallArt);

        }


    }



    private void sort(){
        msongsLoader.setSongsSortOrder();
        msongsLoader.reloadSongsSort();

       if(sortCallback != null){
           sortCallback.onSortOrderChanged();
       }

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
        return _songs.size()+1;
    }

    @SuppressLint("DefaultLocale")
    @NonNull
    @Override
    public String getSectionName(int i) {
        if (i == 0){
            return "";
        }else{
            if(songs_sort_number == 1){
                return String.valueOf(_songs.get(i-1).getSongName().toUpperCase().charAt(0));
            }else if (songs_sort_number == 2){
                return String.valueOf(_songs.get(i-1).getArtistName().toUpperCase().charAt(0));
            }else if (songs_sort_number == 3){
                return "";
            }else if (songs_sort_number == 4){
                return "";
            }else if (songs_sort_number == 5){
                return String.valueOf(_songs.get(i-1).getSongYear()).substring(2);
            }else{
                return String.valueOf(_songs.get(i-1).getAlbumName().toUpperCase().charAt(0));
            }
        }

    }

    @SuppressLint("DefaultLocale")
    @NonNull
    private String getNameForItem(int i) {
        return String.format("Item %d", i + 1);
    }


    private class HeaderViewHolder extends RecyclerView.ViewHolder{
        ImageView header_sort, header_shuffle, header_play;

        public HeaderViewHolder(@NonNull View itemView) {
            super(itemView);
            header_sort = (ImageView) itemView.findViewById(R.id.songs_sort);
            header_shuffle = itemView.findViewById(R.id.songs_header_shuffle);
            header_play = itemView.findViewById(R.id.songs_header_play);


        }
    }


    public class SongHolder extends RecyclerView.ViewHolder implements View.OnClickListener {
        TextView songName, artistName, optionsMenu;
        ImageView smallArt;
        QueueManager mqueue;
        PlaybackManager mplayback;
        MusicService mservice;


        public SongHolder(@NonNull View itemView, MusicService xmusicService, MediaImporter xsongsLoader) {
            super(itemView);

//            mqueue = xqueueManager;
//            mplayback = xplaybackManager;
            mservice = xmusicService;

            songName = itemView.findViewById(R.id.current_song);
            optionsMenu = itemView.findViewById(R.id.textViewOptions);

            artistName = itemView.findViewById(R.id.current_artist);
            smallArt = itemView.findViewById(R.id.smallArt);

            itemView.setOnClickListener(this);
        }

        public void onClick(View view) {

            int currentPosition = getAdapterPosition() - 1;
            ArrayList<Song> songs2 = new ArrayList<Song>();
            songs2.clear();
            songs2.addAll(_songs);

            musicService.setCurrentQueueItemPosition(currentPosition);
            musicService.setCurrentQueue(songs2);

            try{
                adapterCallback.onMethodCallback();
            }catch (ClassCastException | ExecutionException | InterruptedException ignored){
            }

            musicService.loadSong(songs2.get(currentPosition));
            musicService.play();

        }
    }

    public interface AdapterCallback {
        void onMethodCallback() throws ExecutionException, InterruptedException;

    }

    public interface SongAdapterSortCallback {
        void onSortOrderChanged();
    }



}
