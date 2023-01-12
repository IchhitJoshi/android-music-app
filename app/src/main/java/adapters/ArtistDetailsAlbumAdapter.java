package adapters;

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

import java.util.ArrayList;

import managers.MediaImporter;
import models.Album;

public class ArtistDetailsAlbumAdapter  extends RecyclerView.Adapter<RecyclerView.ViewHolder>{

    private final Context context;

    private final ArrayList<Album> _albums;
    private MediaImporter msongsLoader;

    MusicService musicService;

    private  AlbumAdapter.AlbumAdapterSortCallback albumAdapterSortCallback;
    private AlbumAdapter.AlbumAdapterPlayCallback albumAdapterPlayCallback;

//    private static final int TYPE_HEADER = 0;
//    private static final int TYPE_ITEM = 1;

    public ArtistDetailsAlbumAdapter (Context context, ArrayList<Album> albums, MusicService musicService){

        this.context = context;
        this._albums = albums;
        this.musicService = musicService;
//        this.msongsLoader = songsLoader;
//        this.albumAdapterSortCallback = aSortCallback;
//        try{
//            albumAdapterPlayCallback= ((AlbumAdapter.AlbumAdapterPlayCallback) context);
//
//        }catch (ClassCastException ignored) {
//        }
    }

    @NonNull
    @Override
    public RecyclerView.ViewHolder onCreateViewHolder(@NonNull ViewGroup viewGroup, int i) {
        View myView = LayoutInflater.from(context).inflate(R.layout.artists_details_album_item, viewGroup, false);
        return new ArtistDetailsAlbumItemViewHolder(myView,musicService);


    }

    @Override
    public void onBindViewHolder(@NonNull RecyclerView.ViewHolder holder, int position) {
        final ArtistDetailsAlbumItemViewHolder artistDetailsAlbumItemViewHolder = (ArtistDetailsAlbumItemViewHolder) holder;
        final Album album = _albums.get(position);
        artistDetailsAlbumItemViewHolder.albumName.setText(album.getAlbumName());
        artistDetailsAlbumItemViewHolder.thirdRow.setText(album.getNumSong() + "•" + album.getYear());
        Glide.with(context).load(album.getAlbumSongs().get(0).getSongUrl()).into(artistDetailsAlbumItemViewHolder.albumArt);

        artistDetailsAlbumItemViewHolder.albumOptionsMenu.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                PopupMenu popupMenu = new PopupMenu(context, artistDetailsAlbumItemViewHolder.albumOptionsMenu);
                popupMenu.inflate(R.menu.albums_menu_options);
                popupMenu.setOnMenuItemClickListener(new PopupMenu.OnMenuItemClickListener() {
                    @Override
                    public boolean onMenuItemClick(MenuItem item) {

                        switch (item.getItemId()){
                            case R.id.album_menu_1:
                                Toast.makeText(context, "Playing - " + album.getAlbumName(), Toast.LENGTH_LONG).show();
//                                playAlbum(album);

                                break;

                            case R.id.album_menu_2:
                                Toast.makeText(context, "Playing Next - " + album.getAlbumName(), Toast.LENGTH_LONG).show();
                                if(musicService.isEmpty()){
//                                    playAlbum(album);
                                }else{

                                    musicService.playAlbumNext(album.getAlbumSongs());

                                }

                                break;

                            case R.id.album_menu_addToQueue:
                                Toast.makeText(context, "Added to Queue", Toast.LENGTH_LONG).show();
                                if(musicService.isEmpty()){
//                                    playAlbum(album);
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





    @Override
    public int getItemCount() {
        return _albums.size();
    }

    public class ArtistDetailsAlbumItemViewHolder extends RecyclerView.ViewHolder implements View.OnClickListener{
        TextView albumName, albumOptionsMenu, thirdRow;
        ImageView albumArt;

        MusicService mService;


        public ArtistDetailsAlbumItemViewHolder(@NonNull View itemView, MusicService xService) {
            super(itemView);

            mService = xService;

            albumName = itemView.findViewById(R.id.artists_details_album_name);
            albumOptionsMenu = itemView.findViewById(R.id.artists_details_album_item_Options);

            albumArt = itemView.findViewById(R.id.artists_details_album_item_art);
            thirdRow = itemView.findViewById(R.id.artists_details_album_item_3rd_row);

            itemView.setOnClickListener(this);
        }

        @Override
        public void onClick(View v) {
            int currentPosition = getAdapterPosition();

            AppCompatActivity activity = (AppCompatActivity) v.getContext();
            AlbumsDetailsFragment albumsDetailsFragment = new AlbumsDetailsFragment(context, _albums.get(currentPosition));
            FragmentManager manager = activity.getSupportFragmentManager();
            FragmentTransaction transaction = manager.beginTransaction();
            transaction.replace(R.id.mainFrame,albumsDetailsFragment).addToBackStack(null).commit();

        }
    }


}
