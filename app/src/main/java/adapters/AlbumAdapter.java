package adapters;

import android.annotation.SuppressLint;
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
import com.simplecityapps.recyclerview_fastscroll.views.FastScrollRecyclerView;

import java.util.ArrayList;
import java.util.Collections;

import managers.MediaImporter;
import models.Album;

import static com.example.demo.views.Home.albums_ascending;
import static com.example.demo.views.Home.albums_sort_number;
import static com.example.demo.views.Home.shuffle;

public class AlbumAdapter extends RecyclerView.Adapter<RecyclerView.ViewHolder> implements FastScrollRecyclerView.SectionedAdapter{

    private final Context context;

    private final ArrayList<Album> _albums;
    private final MediaImporter msongsLoader;

    MusicService musicService;

    private final AlbumAdapterSortCallback albumAdapterSortCallback;
    private AlbumAdapterPlayCallback albumAdapterPlayCallback;

    private static final int TYPE_HEADER = 0;
    private static final int TYPE_ITEM = 1;

    public AlbumAdapter(Context context, ArrayList<Album> albums, MusicService musicService, MediaImporter mediaImporter, AlbumAdapterSortCallback aSortCallback){

        this.context = context;
        this._albums = albums;
        this.musicService = musicService;
        this.msongsLoader = mediaImporter;
        this.albumAdapterSortCallback = aSortCallback;
        try{
            albumAdapterPlayCallback= ((AlbumAdapterPlayCallback) context);

        }catch (ClassCastException ignored) {
        }
    }

    @NonNull
    @Override
    public RecyclerView.ViewHolder onCreateViewHolder(@NonNull ViewGroup viewGroup, int i) {
        if (i == TYPE_ITEM){
            View myView = LayoutInflater.from(context).inflate(R.layout.album_item, viewGroup, false);
            return new AlbumHolder(myView,musicService);

        } else if (i == TYPE_HEADER){
            View myView = LayoutInflater.from(context).inflate(R.layout.albums_tab_header, viewGroup, false);
            return new HeaderViewHolder(myView);
        }
        else return null;
    }

    @Override
    public void onBindViewHolder(@NonNull RecyclerView.ViewHolder holder, final int position) {
        if (holder instanceof HeaderViewHolder){
            final HeaderViewHolder headerViewHolder = (HeaderViewHolder) holder;

            headerViewHolder.album_header_sort.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    androidx.appcompat.widget.PopupMenu popupMenu = new androidx.appcompat.widget.PopupMenu(context, headerViewHolder.album_header_sort);
                    popupMenu.inflate(R.menu.albums_sort_options_menu);
                    MenuItem sort_album_name = popupMenu.getMenu().findItem(R.id.albums_sort_item1);
                    MenuItem sort_album_artist = popupMenu.getMenu().findItem(R.id.albums_sort_item2);
                    MenuItem sort_album_year = popupMenu.getMenu().findItem(R.id.albums_sort_item3);
                    MenuItem sort_album_tracks = popupMenu.getMenu().findItem(R.id.albums_sort_item5);
                    final MenuItem album_sort_ascending = popupMenu.getMenu().findItem(R.id.albums_sort_item4);

                    if (albums_sort_number == 1){
                        sort_album_name.setChecked(true);
                    }else if (albums_sort_number == 2 ){
                        sort_album_artist.setChecked(true);
                    }else if (albums_sort_number == 3 ){
                        sort_album_year.setChecked(true);
                    }else if (albums_sort_number == 4){
                        sort_album_tracks.setChecked(true);
                    }

                    if (albums_ascending){
                        album_sort_ascending.setChecked(true);
                    }else {
                        album_sort_ascending.setChecked(false);
                    }


                    popupMenu.setOnMenuItemClickListener(new androidx.appcompat.widget.PopupMenu.OnMenuItemClickListener() {
                        @Override
                        public boolean onMenuItemClick(MenuItem item) {

                            switch (item.getItemId()){
                                case R.id.albums_sort_item1:
                                    if(!albums_ascending  && albums_sort_number != 1){
                                        albums_sort_number = 1;
                                        sort();
                                        reverse(); }

                                    if(albums_sort_number != 1){
                                        albums_sort_number = 1;
                                        sort();
                                    }

                                    break;
                                case R.id.albums_sort_item2:
                                    item.setChecked(true);

                                    if(!albums_ascending  && albums_sort_number != 2){
                                        albums_sort_number = 2;
                                        sort();
                                        reverse(); }

                                    if(albums_sort_number != 2){
                                        albums_sort_number = 2;
                                        sort();
                                    }


                                    break;
                                case R.id.albums_sort_item3:
                                    item.setChecked(true);

                                    if(!albums_ascending && albums_sort_number != 3){
                                        albums_sort_number = 3;
                                        sort();
                                        reverse(); }

                                    if(albums_sort_number != 3){
                                        albums_sort_number = 3;
                                        sort();
                                    }

                                    break;

                                case R.id.albums_sort_item5:

                                    if(!albums_ascending && albums_sort_number != 4){
                                        albums_sort_number = 4;
                                        sort();
                                        reverse(); }

                                    if(albums_sort_number != 4){
                                        albums_sort_number = 4;
                                        sort();
                                    }



                                    break;

                                case R.id.albums_sort_item4:

                                    if (albums_ascending){
                                        albums_ascending = false;
                                    }else {
                                        albums_ascending = true;
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

            headerViewHolder.album_header_shuffle.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    shuffle = false;
                    ArrayList<Album> albums2 = new ArrayList<Album>();
                    albums2.clear();
                    albums2.addAll(_albums);
                    Collections.shuffle(albums2);
                    musicService.setAlbumQueue(albums2);
                    musicService.setCurrentQueueItemPosition(0);

                    musicService.loadSong(musicService.getCurrentQueueItem());
                    musicService.play();
                    try{
                        albumAdapterPlayCallback.onAlbumPlayCallback();
                    }catch (ClassCastException ignored){
                    }
                }
            });

            headerViewHolder.album_header_play.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                        shuffle = false;
                        ArrayList<Album> albums2 = new ArrayList<Album>();
                        albums2.clear();
                        albums2.addAll(_albums);
                        musicService.setAlbumQueue(albums2);
                        musicService.setCurrentQueueItemPosition(0);

                        musicService.loadSong(musicService.getCurrentQueueItem());
                        musicService.play();

                        try{
                            albumAdapterPlayCallback.onAlbumPlayCallback();
                        }catch (ClassCastException ignored){
                        }

                    }


            });


        }
        else if (holder instanceof AlbumHolder){

            final AlbumHolder albumHolder = (AlbumHolder) holder;
            final Album album = _albums.get(position-1);

            albumHolder.trackImage.getDrawable().setTint(context.getResources().getColor(R.color.mainTextColor));
            
            albumHolder.albumName.setText(album.getAlbumName());
            albumHolder.albumArtistName.setText(album.getArtistName());
            albumHolder.thirdRow.setText(album.getNumSong() + "•" + album.getYear());

            Glide.with(context).load(album.getAlbumSongs().get(0).getSongUrl()).into(albumHolder.albumArt);

            albumHolder.albumOptionsMenu.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    PopupMenu popupMenu = new PopupMenu(context, albumHolder.albumOptionsMenu);
                    popupMenu.inflate(R.menu.albums_menu_options);
                    popupMenu.setOnMenuItemClickListener(new PopupMenu.OnMenuItemClickListener() {
                        @Override
                        public boolean onMenuItemClick(MenuItem item) {

                            switch (item.getItemId()){
                                case R.id.album_menu_1:
                                    Toast.makeText(context, "Playing - " + album.getAlbumName(), Toast.LENGTH_LONG).show();
                                    playAlbum(album);

                                    break;

                                case R.id.album_menu_2:
                                    Toast.makeText(context, "Playing Next - " + album.getAlbumName(), Toast.LENGTH_LONG).show();
                                    if(musicService.isEmpty()){
                                        playAlbum(album);
                                    }else{

                                        musicService.playAlbumNext(album.getAlbumSongs());

                                    }

                                    break;

                                case R.id.album_menu_addToQueue:
                                    Toast.makeText(context, "Added to Queue", Toast.LENGTH_LONG).show();
                                    if(musicService.isEmpty()){
                                        playAlbum(album);
                                    }else{
                                        musicService.addAlbumToQueue(album.getAlbumSongs());

                                    }

                                    break;
                                case R.id.album_menu_newPlaylist:
                                    Toast.makeText(context, "New Playlist", Toast.LENGTH_LONG).show();
                                    break;
                                case R.id.album_menu_Favorites:
                                    Toast.makeText(context, "Added to Favorites", Toast.LENGTH_LONG).show();
                                    break;
                                case R.id.album_menu_4:
                                    Toast.makeText(context, "Edit Album Info", Toast.LENGTH_LONG).show();
                                    break;
                                case R.id.album_menu_5:
                                    Toast.makeText(context, "Go to Artist", Toast.LENGTH_LONG).show();
                                    break;
                                case R.id.album_menu_6:
                                    Toast.makeText(context, "Choose Artwork", Toast.LENGTH_LONG).show();
                                    break;
                                case R.id.album_menu_7:
                                    Toast.makeText(context, "Album Deleted", Toast.LENGTH_LONG).show();
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

        }

    }

    public void playAlbum(Album album){
        musicService.setCurrentQueueItemPosition(0);
        musicService.setCurrentQueue(album.getAlbumSongs());
        musicService.loadSong(album.getAlbumSongs().get(0));
        musicService.play();
        try{
            albumAdapterPlayCallback.onAlbumPlayCallback();
        }catch (ClassCastException ignored){
        }
    }

    private void sort(){
        msongsLoader.setAlbumsSortOrder();

        if(albumAdapterSortCallback != null){
            albumAdapterSortCallback.onSortOrderChanged();
        }

    }

    private void reverse(){
        Collections.reverse(_albums);
        if(albumAdapterSortCallback != null){
            albumAdapterSortCallback.onSortOrderChanged();

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
        return _albums.size()+1;
    }

    @SuppressLint("DefaultLocale")
    @NonNull
    @Override
    public String getSectionName(int i) {
        if (i == 0){
            return "";
        }else{
            if(albums_sort_number == 1){
                return String.valueOf(_albums.get(i-1).getAlbumName().toUpperCase().charAt(0));
            }else if (albums_sort_number == 2){
                return String.valueOf(_albums.get(i-1).getArtistName().toUpperCase().charAt(0));
            }else if (albums_sort_number == 3){
                return String.valueOf(_albums.get(i-1).getYear()).substring(2);
            } else {

                return String.valueOf(_albums.get(i-1).getNumSong());
            }

        }
    }

    private static class HeaderViewHolder extends RecyclerView.ViewHolder{
        ImageView album_header_sort, album_header_shuffle, album_header_play;

        public HeaderViewHolder(@NonNull View itemView) {
            super(itemView);
            album_header_sort = itemView.findViewById(R.id.albums_sort);
            album_header_shuffle = itemView.findViewById(R.id.albums_header_shuffle);
            album_header_play = itemView.findViewById(R.id.albums_header_play);
        }
    }

    public class AlbumHolder extends RecyclerView.ViewHolder implements View.OnClickListener{
        TextView albumName, albumArtistName, albumOptionsMenu, thirdRow;
        ImageView albumArt, trackImage;

        MusicService mService;


        public AlbumHolder(@NonNull View itemView, MusicService xService) {
            super(itemView);

            mService = xService;

            albumName = itemView.findViewById(R.id.album_name);
            albumOptionsMenu = itemView.findViewById(R.id.album_item_Options);
            trackImage = itemView.findViewById(R.id.album_item_track_image);

            albumArtistName = itemView.findViewById(R.id.album_item_artist_name);
            albumArt = itemView.findViewById(R.id.album_item_art);
            thirdRow = itemView.findViewById(R.id.album_item_3rd_row);

            itemView.setOnClickListener(this);
        }

        @Override
        public void onClick(View v) {
            int currentPosition = getAdapterPosition() - 1;

            AppCompatActivity activity = (AppCompatActivity) v.getContext();
            AlbumsDetailsFragment albumsDetailsFragment = new AlbumsDetailsFragment(context, _albums.get(currentPosition));
            FragmentManager manager = activity.getSupportFragmentManager();
            FragmentTransaction transaction = manager.beginTransaction();
            transaction.replace(R.id.mainFrame,albumsDetailsFragment).addToBackStack(null).commit();

        }
    }

    public interface AlbumAdapterSortCallback {
        void onSortOrderChanged();
    }

    public interface AlbumAdapterPlayCallback {
        void onAlbumPlayCallback();
    }

}
