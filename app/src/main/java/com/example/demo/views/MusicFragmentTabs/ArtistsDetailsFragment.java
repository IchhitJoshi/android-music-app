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
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.bumptech.glide.Glide;
import services.MusicService;
import com.example.demo.R;
import com.example.demo.views.Home;
import com.google.android.material.appbar.AppBarLayout;
import com.google.android.material.appbar.CollapsingToolbarLayout;
import com.simplecityapps.recyclerview_fastscroll.views.FastScrollRecyclerView;

import java.util.ArrayList;

import adapters.ArtistDetailsSongAdapter;
import models.Album;
import models.AlbumArtist;
import models.Song;

import static com.example.demo.views.Home.albumDetailsOpen;
import static com.example.demo.views.Home.nightMode;
import static com.example.demo.views.Home.artistDetailsOpen;

public class ArtistsDetailsFragment extends Fragment {


    Window window;

    private AlbumArtist artist;
    ArrayList<Album> albums = new ArrayList<>();

    private final Context context;
    CollapsingToolbarLayout collapsingToolbarLayout;
    RecyclerView artistDetailsRecyclerView;

    MusicService musicService;

    ArtistDetailsSongAdapter artistDetailsSongAdapter;

    private ImageView artistArt;
    private TextView artistName;
    private Toolbar artistDetailsToolbar;
    AppBarLayout appBarLayout;
    ConstraintLayout constraintLayout;

    public ArtistsDetailsFragment(Context context, AlbumArtist artist){
        this.context = context;
        this.artist = artist;

    }

    @Override
    public void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setHasOptionsMenu(true);
        artistDetailsOpen = true;
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
    }

    @Override
    public void onDestroy() {
        super.onDestroy();
        artistDetailsOpen = false;
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
        View view = inflater.inflate(R.layout.fragment_artist_details, container, false);

        artistDetailsOpen = true;

        musicService = ((Home) (getActivity())).getMusicService();

        collapsingToolbarLayout = view.findViewById(R.id.artistDetailsCollapsingToolbar);
        appBarLayout = view.findViewById(R.id.artistDetailsAppBar);
        artistArt = view.findViewById(R.id.artistDetailsArt);
        artistDetailsToolbar = view.findViewById(R.id.artistDetailsToolbar);
        ((AppCompatActivity)getActivity()).setSupportActionBar(artistDetailsToolbar);
        artistName = view.findViewById(R.id.artistDetailsArtistName);
        artistDetailsRecyclerView = view.findViewById(R.id.artistDetailsMainRecyclerView);
        constraintLayout = view.findViewById(R.id.artist_details_constraint_layout);

//        albumSongs = album.getAlbumSongs();

        artistName.setText(artist.getArtistName());

        artistDetailsToolbar.setTitle("");
        collapsingToolbarLayout.setTitle(artist.getArtistName());
        artistDetailsToolbar.setNavigationIcon(R.drawable.back_arrow_gray);
        artistDetailsToolbar.setOverflowIcon(ContextCompat.getDrawable(context, R.drawable.overflow_gray));


        artistDetailsToolbar.setSubtitle("");

        artistDetailsSongAdapter = new ArtistDetailsSongAdapter(getActivity(),musicService, artist);
        artistDetailsRecyclerView.setAdapter(artistDetailsSongAdapter);

        LinearLayoutManager linearLayoutManager = new LinearLayoutManager(this.getContext());
        artistDetailsRecyclerView.setLayoutManager(linearLayoutManager);


        appBarLayout.addOnOffsetChangedListener(new AppBarLayout.OnOffsetChangedListener() {

            @Override
            public void onOffsetChanged(AppBarLayout appBarLayout, int verticalOffset) {

                constraintLayout.setAlpha(1.0f - Math.abs(verticalOffset / (float)
                        appBarLayout.getTotalScrollRange()));

                if (Math.abs(verticalOffset)-appBarLayout.getTotalScrollRange() == 0)
                {
                    //  Collapsed
                    artistDetailsToolbar.setTitle(artist.getArtistName());
                    artistName.setVisibility(View.INVISIBLE);
                    artistDetailsToolbar.setNavigationIcon(R.drawable.ic_arrow_back_black_24dp);
                    artistDetailsToolbar.setOverflowIcon(ContextCompat.getDrawable(context, R.drawable.overflow_white));

                }
                else if (verticalOffset == 0) {
                    artistDetailsToolbar.setTitle("");
                    collapsingToolbarLayout.setTitle("");
                    artistName.setVisibility(View.VISIBLE);
                    artistDetailsToolbar.setNavigationIcon(R.drawable.back_arrow_gray);
                    artistDetailsToolbar.setOverflowIcon(ContextCompat.getDrawable(context, R.drawable.overflow_gray));
                    // Expanded
                } else {
                    artistName.setVisibility(View.INVISIBLE);
                    artistDetailsToolbar.setTitle("");
                    collapsingToolbarLayout.setTitle(artist.getArtistName());
                    artistDetailsToolbar.setSubtitle("");
                    artistDetailsToolbar.setTitle(artist.getArtistName());
                    artistDetailsToolbar.setNavigationIcon(R.drawable.back_arrow_gray);
                    artistDetailsToolbar.setOverflowIcon(ContextCompat.getDrawable(context, R.drawable.overflow_gray));

                    // Somewhere in between
                }
            }
        });


      artistDetailsToolbar.setNavigationOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

                getActivity().getSupportFragmentManager().popBackStack();

                if(albumDetailsOpen){
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




                artistDetailsOpen = false;


            }
        });

        Glide.with(context).load(artist.getSongsByArtist().get(0).getSongUrl()).into(artistArt);

        return view;
    }


    @Override
    public void onCreateOptionsMenu(@NonNull Menu menu, @NonNull MenuInflater inflater) {
        inflater.inflate(R.menu.artist_details_menu_options, menu);
        super.onCreateOptionsMenu(menu, inflater);

    }

    @Override
    public boolean onOptionsItemSelected(@NonNull MenuItem item) {
        if (item.getItemId() == R.id.artist_details_menu_1){
            //play next
            if(musicService.isEmpty()){
                playArtist(artist.getSongsByArtist());
            }else{

                musicService.playAlbumNext(artist.getSongsByArtist());

            }

        }
        if (item.getItemId() == R.id.artist_details_menu_newPlaylist){
            Toast.makeText(context, "New Playlist", Toast.LENGTH_SHORT).show();
            //add to playlist
        }
        if (item.getItemId() == R.id.artist_details_menu_Favorites){
            Toast.makeText(context, "Added to Favorites", Toast.LENGTH_SHORT).show();
        }

        if (item.getItemId() == R.id.artist_details_menu_addToQueue){
            Toast.makeText(context, "Added to Favorites", Toast.LENGTH_SHORT).show();
        }

        if (item.getItemId() == R.id.artist_details_menu_3){
            Toast.makeText(context, "Choose Artwork", Toast.LENGTH_SHORT).show();
        }

        if (item.getItemId() == R.id.artist_details_menu_4){
            Toast.makeText(context, "Delete Artist", Toast.LENGTH_SHORT).show();
        }


        return super.onOptionsItemSelected(item);
    }

    private void playArtist(ArrayList<Song> songs){
        musicService.setCurrentQueueItemPosition(0);
        musicService.setCurrentQueue(songs);
        musicService.loadSong(songs.get(0));
        musicService.play();
    }
}
