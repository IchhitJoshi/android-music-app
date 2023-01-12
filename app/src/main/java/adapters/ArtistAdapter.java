package adapters;

import android.annotation.SuppressLint;
import android.content.Context;
import android.view.LayoutInflater;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.fragment.app.FragmentManager;
import androidx.fragment.app.FragmentTransaction;
import androidx.recyclerview.widget.RecyclerView;

import com.bumptech.glide.Glide;
import services.MusicService;
import com.example.demo.R;
import com.example.demo.views.MusicFragmentTabs.ArtistsDetailsFragment;
import com.simplecityapps.recyclerview_fastscroll.views.FastScrollRecyclerView;

import java.util.ArrayList;
import java.util.Collections;

import managers.MediaImporter;
import models.AlbumArtist;

import static com.example.demo.views.Home.artists_ascending;
import static com.example.demo.views.Home.artists_sort_number;
import static com.example.demo.views.Home.shuffle;

public class ArtistAdapter extends RecyclerView.Adapter<RecyclerView.ViewHolder> implements FastScrollRecyclerView.SectionedAdapter{

    private final Context context;

    private final ArrayList<AlbumArtist> _artist;
    private final MediaImporter msongsLoader;

    MusicService musicService;

    private final ArtistAdapterSortCallback artistAdapterSortCallback;
    private ArtistAdapterPlayCallback artistAdapterPlayCallback;

    private static final int TYPE_HEADER = 0;
    private static final int TYPE_ITEM = 1;

    public ArtistAdapter(Context context, ArrayList<AlbumArtist> artist, MusicService musicService, MediaImporter mediaImporter, ArtistAdapterSortCallback aSortCallback){

        this.context = context;
        this._artist = artist;
        this.musicService = musicService;
        this.msongsLoader = mediaImporter;
        this.artistAdapterSortCallback = aSortCallback;
        try{
            artistAdapterPlayCallback= ((ArtistAdapterPlayCallback) context);

        }catch (ClassCastException ignored) {
        }
    }

    @NonNull
    @Override
    public RecyclerView.ViewHolder onCreateViewHolder(@NonNull ViewGroup viewGroup, int i) {
        if (i == TYPE_ITEM){
            View myView = LayoutInflater.from(context).inflate(R.layout.artist_item, viewGroup, false);
            return new ArtistHolder(myView,musicService);

        } else if (i == TYPE_HEADER){
            View myView = LayoutInflater.from(context).inflate(R.layout.artists_tab_header, viewGroup, false);
            return new HeaderViewHolder(myView);
        }
        else return null;
    }

    @Override
    public void onBindViewHolder(@NonNull RecyclerView.ViewHolder holder, final int position) {
        if (holder instanceof HeaderViewHolder){
            final HeaderViewHolder headerViewHolder = (HeaderViewHolder) holder;

            headerViewHolder.artist_header_sort.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    androidx.appcompat.widget.PopupMenu popupMenu = new androidx.appcompat.widget.PopupMenu(context, headerViewHolder.artist_header_sort);
                    popupMenu.inflate(R.menu.artists_sort_options_menu);
                    MenuItem sort_artist_name = popupMenu.getMenu().findItem(R.id.artists_sort_item1);
                    MenuItem sort_artist_albumNum = popupMenu.getMenu().findItem(R.id.artists_sort_item2);
                    MenuItem sort_artist_tracks = popupMenu.getMenu().findItem(R.id.artists_sort_item3);
                    MenuItem sort_artist_date = popupMenu.getMenu().findItem(R.id.artists_sort_item4);
                    final MenuItem artist_sort_ascending = popupMenu.getMenu().findItem(R.id.artists_sort_item5);

                    if (artists_sort_number == 1){
                        sort_artist_name.setChecked(true);
                    }else if (artists_sort_number == 2 ){
                        sort_artist_albumNum.setChecked(true);
                    }else if (artists_sort_number == 3 ){
                        sort_artist_tracks.setChecked(true);
                    }else if (artists_sort_number == 4){
                        sort_artist_date.setChecked(true);
                    }

                    if (artists_ascending){
                        artist_sort_ascending.setChecked(true);
                    }else {
                        artist_sort_ascending.setChecked(false);
                    }


                    popupMenu.setOnMenuItemClickListener(new androidx.appcompat.widget.PopupMenu.OnMenuItemClickListener() {
                        @Override
                        public boolean onMenuItemClick(MenuItem item) {

                            switch (item.getItemId()){
                                case R.id.artists_sort_item1:
                                    if(!artists_ascending  && artists_sort_number != 1){
                                        artists_sort_number = 1;
                                        sort();
                                        reverse(); }

                                    if(artists_sort_number != 1){
                                        artists_sort_number = 1;
                                        sort();
                                    }

                                    break;
                                case R.id.artists_sort_item2:
                                    item.setChecked(true);

                                    if(!artists_ascending  && artists_sort_number != 2){
                                        artists_sort_number = 2;
                                        sort();
                                        reverse(); }

                                    if(artists_sort_number != 2){
                                        artists_sort_number = 2;
                                        sort();
                                    }


                                    break;
                                case R.id.artists_sort_item3:
                                    item.setChecked(true);

                                    if(!artists_ascending && artists_sort_number != 3){
                                        artists_sort_number = 3;
                                        sort();
                                        reverse(); }

                                    if(artists_sort_number != 3){
                                        artists_sort_number = 3;
                                        sort();
                                    }

                                    break;

                                case R.id.artists_sort_item4:

                                    if(!artists_ascending && artists_sort_number != 4){
                                        artists_sort_number = 4;
                                        sort();
                                        reverse(); }

                                    if(artists_sort_number != 4){
                                        artists_sort_number = 4;
                                        sort();
                                    }



                                    break;

                                case R.id.artists_sort_item5:

                                    if (artists_ascending){
                                        artists_ascending = false;
                                    }else {
                                        artists_ascending = true;
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
            });

            headerViewHolder.artist_header_shuffle.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    shuffle = false;
                    ArrayList<AlbumArtist> artists2 = new ArrayList<AlbumArtist>();
                    artists2.clear();
                    artists2.addAll(_artist);
                    Collections.shuffle(artists2);
                    musicService.setArtistQueue(artists2);
                    musicService.setCurrentQueueItemPosition(0);

                    musicService.loadSong(musicService.getCurrentQueueItem());
                    musicService.play();
                    try{
                        artistAdapterPlayCallback.onArtistPlayCallback();
                    }catch (ClassCastException ignored){
                    }
                }
            });

            headerViewHolder.artist_header_play.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    shuffle = false;
                    ArrayList<AlbumArtist> artists2 = new ArrayList<AlbumArtist>();
                    artists2.clear();
                    artists2.addAll(_artist);
                    musicService.setArtistQueue(artists2);
                    musicService.setCurrentQueueItemPosition(0);

                    musicService.loadSong(musicService.getCurrentQueueItem());
                    musicService.play();

                    try{
                        artistAdapterPlayCallback.onArtistPlayCallback();
                    }catch (ClassCastException ignored){
                    }

                }


            });


        }
        else if (holder instanceof ArtistHolder){

            final ArtistHolder artistHolder = (ArtistHolder) holder;
            final AlbumArtist artist = _artist.get(position-1);

            artistHolder.albumImage.getDrawable().setTint(context.getResources().getColor(R.color.mainTextColor));
            artistHolder.trackImage.getDrawable().setTint(context.getResources().getColor(R.color.mainTextColor));

            artistHolder.artistName.setText(artist.getArtistName());
            artistHolder.numOfAlbums.setText(artist.getNumOfAlbumsByArtist() + "");
            artistHolder.numOfSongs.setText(artist.getNumOfSongsByArtist() + "");

            Glide.with(context).load(artist.getSongsByArtist().get(0).getSongUrl()).into(artistHolder.artistArt);

//            albumHolder.albumOptionsMenu.setOnClickListener(new View.OnClickListener() {
//                @Override
//                public void onClick(View v) {
//                    PopupMenu popupMenu = new PopupMenu(context, albumHolder.albumOptionsMenu);
//                    popupMenu.inflate(R.menu.albums_menu_options);
//                    popupMenu.setOnMenuItemClickListener(new PopupMenu.OnMenuItemClickListener() {
//                        @Override
//                        public boolean onMenuItemClick(MenuItem item) {
//
//                            switch (item.getItemId()){
//                                case R.id.album_menu_1:
//                                    Toast.makeText(context, "Playing - " + album.getAlbumName(), Toast.LENGTH_LONG).show();
//                                    playAlbum(album);
//
//                                    break;
//
//                                case R.id.album_menu_2:
//                                    Toast.makeText(context, "Playing Next - " + album.getAlbumName(), Toast.LENGTH_LONG).show();
//                                    if(musicService.isEmpty()){
//                                        playAlbum(album);
//                                    }else{
//
//                                        musicService.playAlbumNext(album.getAlbumSongs());
//
//                                    }
//
//                                    break;
//
//                                case R.id.album_menu_addToQueue:
//                                    Toast.makeText(context, "Added to Queue", Toast.LENGTH_LONG).show();
//                                    if(musicService.isEmpty()){
//                                        playAlbum(album);
//                                    }else{
//                                        musicService.addAlbumToQueue(album.getAlbumSongs());
//
//                                    }
//
//                                    break;
//                                case R.id.album_menu_newPlaylist:
//                                    Toast.makeText(context, "New Playlist", Toast.LENGTH_LONG).show();
//                                    break;
//                                case R.id.album_menu_Favorites:
//                                    Toast.makeText(context, "Added to Favorites", Toast.LENGTH_LONG).show();
//                                    break;
//                                case R.id.album_menu_4:
//                                    Toast.makeText(context, "Edit Album Info", Toast.LENGTH_LONG).show();
//                                    break;
//                                case R.id.album_menu_5:
//                                    Toast.makeText(context, "Go to Artist", Toast.LENGTH_LONG).show();
//                                    break;
//                                case R.id.album_menu_6:
//                                    Toast.makeText(context, "Choose Artwork", Toast.LENGTH_LONG).show();
//                                    break;
//                                case R.id.album_menu_7:
//                                    Toast.makeText(context, "Album Deleted", Toast.LENGTH_LONG).show();
//                                    break;
//                                default:
//                                    break;
//                            }
//                            return false;
//                        }
//                    });
//                    popupMenu.show();
//
//                }
//            });

        }

    }

    private void sort(){
        msongsLoader.setArtistsSortOrder();

        if(artistAdapterSortCallback != null){
            artistAdapterSortCallback.onSortOrderChanged();
        }

    }

    private void reverse(){
        Collections.reverse(_artist);
        if(artistAdapterSortCallback != null){
            artistAdapterSortCallback.onSortOrderChanged();

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
        return _artist.size()+1;
    }


    @SuppressLint("DefaultLocale")
    @NonNull
    @Override
    public String getSectionName(int i) {
        if (i == 0){
            return "";
        }else{
            if(artists_sort_number == 1){
                return String.valueOf(_artist.get(i-1).getArtistName().toUpperCase().charAt(0));
            }else if (artists_sort_number == 2){
                return String.valueOf(_artist.get(i-1).getNumOfAlbumsByArtist());
            }else if (artists_sort_number == 3){
                return String.valueOf(_artist.get(i-1).getNumOfSongsByArtist());
            } else {
                return "";
            }

        }

    }

    private static class HeaderViewHolder extends RecyclerView.ViewHolder{
        ImageView artist_header_sort, artist_header_shuffle, artist_header_play;

        public HeaderViewHolder(@NonNull View itemView) {
            super(itemView);
            artist_header_sort = itemView.findViewById(R.id.artists_sort);
            artist_header_shuffle = itemView.findViewById(R.id.artists_header_shuffle);
            artist_header_play = itemView.findViewById(R.id.artists_header_play);
        }
    }

    public class ArtistHolder extends RecyclerView.ViewHolder implements View.OnClickListener{
        TextView artistName, numOfAlbums, numOfSongs, artistOptionsMenu;
        ImageView artistArt, trackImage, albumImage;

        MusicService mService;


        public ArtistHolder(@NonNull View itemView, MusicService xService) {
            super(itemView);

            mService = xService;

            artistName = itemView.findViewById(R.id.artist_item_artist_name);
            artistOptionsMenu = itemView.findViewById(R.id.artist_item_Options);

            trackImage = itemView.findViewById(R.id.artist_item_track_image);
            albumImage = itemView.findViewById(R.id.artist_item_album_image);

            numOfAlbums = itemView.findViewById(R.id.artist_item_albumNum);
            artistArt = itemView.findViewById(R.id.artist_item_art);
            numOfSongs = itemView.findViewById(R.id.artist_item_songsNum);

            itemView.setOnClickListener(this);
        }

        @Override
        public void onClick(View v) {
            int currentPosition = getAdapterPosition() - 1;

            AppCompatActivity activity = (AppCompatActivity) v.getContext();
            ArtistsDetailsFragment artistsDetailsFragment  = new ArtistsDetailsFragment(context, _artist.get(currentPosition));
            FragmentManager manager = activity.getSupportFragmentManager();
            FragmentTransaction transaction = manager.beginTransaction();
            transaction.replace(R.id.mainFrame,artistsDetailsFragment).addToBackStack(null).commit();

        }
    }

    public interface ArtistAdapterSortCallback {
        void onSortOrderChanged();
    }

    public interface ArtistAdapterPlayCallback {
        void onArtistPlayCallback();
    }
}
