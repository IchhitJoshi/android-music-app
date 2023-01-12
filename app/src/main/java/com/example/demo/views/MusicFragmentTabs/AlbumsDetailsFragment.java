package com.example.demo.views.MusicFragmentTabs;

import android.content.Context;
import android.os.Build;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;
import android.view.Window;
import android.view.WindowManager;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.Toolbar;
import androidx.constraintlayout.widget.ConstraintLayout;
import androidx.core.content.ContextCompat;
import androidx.fragment.app.Fragment;
import androidx.fragment.app.FragmentManager;
import androidx.fragment.app.FragmentTransaction;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.bumptech.glide.Glide;
import services.MusicService;
import com.example.demo.R;
import com.example.demo.views.Home;
import com.google.android.material.appbar.AppBarLayout;

import com.google.android.material.appbar.CollapsingToolbarLayout;

import java.util.ArrayList;

import adapters.AlbumDetailsSongAdapter;
import managers.MediaImporter;
import models.Album;
import models.Song;
import static com.example.demo.views.Home.albumDetailsOpen;
import static com.example.demo.views.Home.artistDetailsOpen;
import static com.example.demo.views.Home.nightMode;
import static com.example.demo.views.Home.playerOpen;
import static com.example.demo.views.Home.queueBool;

public class AlbumsDetailsFragment extends Fragment{

    Window window;

    private Album album;
    ArrayList<Song> albumSongs = new ArrayList<>();

    private final Context context;
    CollapsingToolbarLayout collapsingToolbarLayout;
    RecyclerView albumDetailsRecyclerView;

    AlbumDetailsSongAdapter albumDetailsSongAdapter;
    MusicService musicService;

    private ImageView albumArt;
    private TextView albumArtist, albumName;
    private Toolbar albumDetailsToolbar;
    AppBarLayout appBarLayout;
    ConstraintLayout constraintLayout;

    MediaImporter mediaImporter;



    public AlbumsDetailsFragment(Context context, Album album){
        this.context = context;
        this.album = album;

    }

    @Override
    public void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setHasOptionsMenu(true);
        albumDetailsOpen = true;

    }

    @Override
    public void onDestroy() {
        super.onDestroy();
        albumDetailsOpen = false;
        if(Build.VERSION.SDK_INT>=21){
            window = getActivity().getWindow();
            window.setStatusBarColor(this.getResources().getColor(R.color.background));

            window.clearFlags(WindowManager.LayoutParams.FLAG_LAYOUT_NO_LIMITS);

            View decor = window.getDecorView();

            if(nightMode){
                window.clearFlags(View.SYSTEM_UI_FLAG_LIGHT_STATUS_BAR);
            }else{
                decor.setSystemUiVisibility(View.SYSTEM_UI_FLAG_LIGHT_STATUS_BAR);
            }

        }


    }


    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.fragment_album_details2, container, false);


        albumDetailsOpen = true;

        if(Build.VERSION.SDK_INT>=21){
            window = getActivity().getWindow();
            if(nightMode){
                window.setStatusBarColor(this.getResources().getColor(R.color.statusBarColorDark));

            }else{
                window.setStatusBarColor(this.getResources().getColor(R.color.colorAccent));
            }

            window.setFlags(WindowManager.LayoutParams.FLAG_LAYOUT_NO_LIMITS, WindowManager.LayoutParams.FLAG_LAYOUT_NO_LIMITS);

            View decor = window.getDecorView();


            if(nightMode){
                window.clearFlags(View.SYSTEM_UI_FLAG_LIGHT_STATUS_BAR);
            }else{
                decor.setSystemUiVisibility(View.SYSTEM_UI_FLAG_LIGHT_STATUS_BAR);
            }



        }


        musicService = ((Home) (getActivity())).getMusicService();
        mediaImporter = ((Home) (getActivity())).getMediaImporter();

        collapsingToolbarLayout = view.findViewById(R.id.albumDetailsCollapsingToolbar);
        appBarLayout = view.findViewById(R.id.albumDetailsAppBar);
        albumArt = view.findViewById(R.id.albumDetailsArt);
        albumDetailsToolbar = view.findViewById(R.id.albumDetailsToolbar);
        ((AppCompatActivity)getActivity()).setSupportActionBar(albumDetailsToolbar);
        albumArtist = view.findViewById(R.id.albumArtistName);
        albumName = view.findViewById(R.id.albumName);
        albumDetailsRecyclerView = view.findViewById(R.id.albumDetailsRecyclerView);
        constraintLayout = view.findViewById(R.id.album_details_constraint_layout);

        albumSongs = album.getAlbumSongs();

        albumName.setText(album.getAlbumName());
        albumArtist.setText(album.getArtistName());

        albumDetailsToolbar.setTitle("");
        collapsingToolbarLayout.setTitle(album.getAlbumName());
//        collapsingToolbarLayout.setSubtitle(album.getArtistName());
        albumDetailsToolbar.setSubtitle("");

        albumDetailsToolbar.setNavigationIcon(R.drawable.back_arrow_gray);
        albumDetailsToolbar.setOverflowIcon(ContextCompat.getDrawable(context, R.drawable.overflow_gray));

        if(album.getDiscNumber() > 1){
            albumDetailsSongAdapter = new AlbumDetailsSongAdapter(getActivity(),musicService, album, true);

        }else{
            albumDetailsSongAdapter = new AlbumDetailsSongAdapter(getActivity(),musicService, album, false);
        }


        albumDetailsRecyclerView.setAdapter(albumDetailsSongAdapter);

        LinearLayoutManager linearLayoutManager = new LinearLayoutManager(this.getContext());
        albumDetailsRecyclerView.setLayoutManager(linearLayoutManager);


        appBarLayout.addOnOffsetChangedListener(new AppBarLayout.OnOffsetChangedListener() {


            @Override
            public void onOffsetChanged(AppBarLayout appBarLayout, int verticalOffset) {

                constraintLayout.setAlpha(1.0f - Math.abs(verticalOffset / (float)
                        appBarLayout.getTotalScrollRange()));
                albumArtist.setAlpha(1.0f - 2 * Math.abs(verticalOffset / (float)
                        appBarLayout.getTotalScrollRange()));

                if (Math.abs(verticalOffset)-appBarLayout.getTotalScrollRange() == 0)
                {
                    //  Collapsed
                    albumDetailsToolbar.setTitle(album.getAlbumName());
                    albumDetailsToolbar.setSubtitle(album.getArtistName());
                    albumName.setVisibility(View.INVISIBLE);
                    albumDetailsToolbar.setNavigationIcon(R.drawable.ic_arrow_back_black_24dp);
                    albumDetailsToolbar.setOverflowIcon(ContextCompat.getDrawable(context, R.drawable.overflow_white));

                }
                else if (verticalOffset == 0) {
                    albumDetailsToolbar.setTitle("");
                    collapsingToolbarLayout.setTitle("");
                    albumDetailsToolbar.setSubtitle("");
                    albumName.setVisibility(View.VISIBLE);
                    albumDetailsToolbar.setNavigationIcon(R.drawable.back_arrow_gray);
                    albumDetailsToolbar.setOverflowIcon(ContextCompat.getDrawable(context, R.drawable.overflow_gray));
                    // Expanded
                } else {
                    albumName.setVisibility(View.INVISIBLE);
                    albumDetailsToolbar.setTitle("");
                    collapsingToolbarLayout.setTitle(album.getAlbumName());
                    albumDetailsToolbar.setSubtitle("");
                    albumDetailsToolbar.setTitle(album.getAlbumName());
                    albumDetailsToolbar.setSubtitle(album.getArtistName());
                    albumDetailsToolbar.setNavigationIcon(R.drawable.back_arrow_gray);
                    albumDetailsToolbar.setOverflowIcon(ContextCompat.getDrawable(context, R.drawable.overflow_gray));
                    // Somewhere in between
                }
            }
        });


        albumDetailsToolbar.setNavigationOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

                getActivity().getSupportFragmentManager().popBackStack();

                if (artistDetailsOpen){
                    if(Build.VERSION.SDK_INT>=21){
                        window = getActivity().getWindow();
                        if(nightMode){
                            window.setStatusBarColor(getActivity().getResources().getColor(R.color.statusBarColorDark));

                        }else{
                            window.setStatusBarColor(getActivity().getResources().getColor(R.color.colorAccent));
                        }

                        window.setFlags(WindowManager.LayoutParams.FLAG_LAYOUT_NO_LIMITS, WindowManager.LayoutParams.FLAG_LAYOUT_NO_LIMITS);

                        View decor = window.getDecorView();


                        if(nightMode){
                            window.clearFlags(View.SYSTEM_UI_FLAG_LIGHT_STATUS_BAR);
                        }else{
                            decor.setSystemUiVisibility(View.SYSTEM_UI_FLAG_LIGHT_STATUS_BAR);
                        }



                    }
                }else{
                    if(Build.VERSION.SDK_INT>=21){
                        window = getActivity().getWindow();
                        window.setStatusBarColor(getActivity().getResources().getColor(R.color.background));

                        window.clearFlags(WindowManager.LayoutParams.FLAG_LAYOUT_NO_LIMITS);

                        View decor = window.getDecorView();

                        if(nightMode){
                            window.clearFlags(View.SYSTEM_UI_FLAG_LIGHT_STATUS_BAR);
                        }else{
                            decor.setSystemUiVisibility(View.SYSTEM_UI_FLAG_LIGHT_STATUS_BAR);
                        }

                    }

                }

                albumDetailsOpen = false;
            }
        });

        Glide.with(context).load(albumSongs.get(0).getSongUrl()).into(albumArt);

        return view;
    }



    @Override
    public void onCreateOptionsMenu(@NonNull Menu menu, @NonNull MenuInflater inflater) {
        inflater.inflate(R.menu.album_details_menu_options, menu);
        super.onCreateOptionsMenu(menu, inflater);

        if(album.isShowTrackNumber()){
            menu.getItem(5).setChecked(false);
        }else{
            menu.getItem(5).setChecked(true);

        }

        if(album.isShowArtistName()){
            menu.getItem(6).setChecked(true);
        }else{
            menu.getItem(6).setChecked(false);

        }
    }

    @Override
    public boolean onOptionsItemSelected(@NonNull MenuItem item) {
        if (item.getItemId() == R.id.album_details_menu_1){
            //play next
            if(musicService.isEmpty()){
                playAlbum(album);
            }else{

                musicService.playAlbumNext(album.getAlbumSongs());

            }

        }
        if (item.getItemId() == R.id.album_details_menu_newPlaylist){
            Toast.makeText(context, "New Playlist", Toast.LENGTH_SHORT).show();
            //add to playlist
        }
        if (item.getItemId() == R.id.album_details_menu_Favorites){
            Toast.makeText(context, "Added to Favorites", Toast.LENGTH_SHORT).show();
        }

        if (item.getItemId() == R.id.album_details_menu_addToQueue){
            Toast.makeText(context, "Added to Queue", Toast.LENGTH_SHORT).show();
            if(musicService.isEmpty()){
                playAlbum(album);
            }else{
                musicService.addAlbumToQueue(album.getAlbumSongs());

            }
        }

        if (item.getItemId() == R.id.album_details_menu_3){
            Toast.makeText(context, "Edit Album Info", Toast.LENGTH_SHORT).show();
        }

        if (item.getItemId() == R.id.album_details_menu_4){
            Toast.makeText(context, "Go to Artist", Toast.LENGTH_SHORT).show();
            playerOpen = false;
            queueBool = false;
            getActivity().getSupportFragmentManager().popBackStack();
            ArtistsDetailsFragment artistsDetailsFragment = new ArtistsDetailsFragment(context, musicService.getCurrentArtist(mediaImporter.getArtists(), album.getArtistId()));
            FragmentManager manager2 = getActivity().getSupportFragmentManager();
            FragmentTransaction transaction2 = manager2.beginTransaction();
            transaction2.replace(R.id.mainFrame, artistsDetailsFragment).addToBackStack(null).commit();
        }

        if (item.getItemId() == R.id.album_details_menu_5){
            Toast.makeText(context, "Choose Artwork", Toast.LENGTH_SHORT).show();
        }

        if (item.getItemId() == R.id.album_details_menu_6){
            if(album.isShowTrackNumber()){
                item.setChecked(true);
                album.setShowTrackNumber(false);
            }else{
                item.setChecked(false);
                album.setShowTrackNumber(true);

            }
            albumDetailsSongAdapter.notifyDataSetChanged();

        }

        if (item.getItemId() == R.id.album_details_menu_7){
            if(album.isShowArtistName()){
                item.setChecked(false);
                album.setShowArtistName(false);
                albumDetailsSongAdapter.notifyDataSetChanged();
            }else{
                item.setChecked(true);
                album.setShowArtistName(true);
                albumDetailsSongAdapter.notifyDataSetChanged();

            }

        }

        if (item.getItemId() == R.id.album_details_menu_8){
            Toast.makeText(context, "Delete Album", Toast.LENGTH_SHORT).show();
        }


        return super.onOptionsItemSelected(item);
    }

    private void playAlbum(Album album){
        musicService.setCurrentQueueItemPosition(0);
        musicService.setCurrentQueue(album.getAlbumSongs());
        musicService.loadSong(album.getAlbumSongs().get(0));
        musicService.play();
    }
}
