package com.example.demo.views.MusicFragmentTabs;

import android.os.Build;
import android.os.Bundle;

import androidx.annotation.RequiresApi;
import androidx.fragment.app.Fragment;
import androidx.recyclerview.widget.LinearLayoutManager;

import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import services.MusicService;
import com.example.demo.R;
import adapters.SongAdapter;
import com.example.demo.views.Home;

import managers.MediaImporter;
import models.Song;

import com.simplecityapps.recyclerview_fastscroll.views.FastScrollRecyclerView;

import java.util.ArrayList;

public class SongsTabFragment extends Fragment implements SongAdapter.SongAdapterSortCallback {

    private FastScrollRecyclerView recyclerView;
    private ArrayList<Song> _songs = new ArrayList<Song>();
    private SongAdapter songAdapter;
//    private QueueManager queueManager;
//    private PlaybackManager playbackManager;
    MusicService musicService;

    private TextView textView;
    private MediaImporter mediaImporter;

//    private SwipeRefreshLayout swipeRefreshLayout;


    public SongsTabFragment() {

    }


    @RequiresApi(api = Build.VERSION_CODES.KITKAT)
    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.fragment_songs_tab, container, false);

        mediaImporter = ((Home) (getActivity())).getMediaImporter();
        musicService = ((Home) (getActivity())).getMusicService();

        if (musicService == null){
            Log.d("message123456", "NULL");

        } else{
            Log.d("message123456", "NOT NULL");
        }



        textView = view.findViewById(R.id.songsTabText);
//        swipeRefreshLayout = view.findViewById(R.id.swipeRefreshLayout);


//        swipeRefreshLayout.setOnRefreshListener(new SwipeRefreshLayout.OnRefreshListener() {
//            @Override
//            public void onRefresh() {
//                _songs.clear();
//                loadSongs();
//                songAdapter.notifyDataSetChanged();
//                swipeRefreshLayout.setRefreshing(false);
//            }
//        });
//        runTimePermission();

        _songs = mediaImporter.getLoadedSongs();

        if (_songs.size() == 0){
            textView.setVisibility(View.VISIBLE);
        }else{
            textView.setVisibility(View.GONE);
            recyclerView = view.findViewById(R.id.recyclerView);
            songAdapter = new SongAdapter(getActivity(),_songs, musicService, mediaImporter, SongsTabFragment.this);
            recyclerView.setAdapter(songAdapter);

            LinearLayoutManager linearLayoutManager = new LinearLayoutManager(this.getContext());

            recyclerView.setLayoutManager(linearLayoutManager);

//            recyclerView.addItemDecoration(new DividerItemDecoration(getContext(), linearLayoutManager.getOrientation()) {
//                @Override
//                public void onDraw(Canvas c, RecyclerView parent, RecyclerView.State state) {
//                    Drawable d = getDrawable();
//                    View view = parent.getChildAt(0);
//                    int position = parent.getChildAdapterPosition(view);
//                    int viewType = parent.getAdapter().getItemViewType(position);
//
//                    if(viewType == 0) {
//                        RecyclerView.LayoutParams params = (RecyclerView.LayoutParams) view.getLayoutParams();
//                        int top = view.getBottom() + params.bottomMargin;
//                        int bottom = top + d.getIntrinsicHeight();
//                        d.setBounds(0, top, parent.getRight(), bottom);
//                        d.draw(c);
//                    }
//
//                }
//            });


        }

        return view;

    }

    @Override
    public void onSortOrderChanged() {
        songAdapter.notifyDataSetChanged();
    }


    public void onBind(MusicService musicService1) {
        musicService = musicService1;

    }
}

