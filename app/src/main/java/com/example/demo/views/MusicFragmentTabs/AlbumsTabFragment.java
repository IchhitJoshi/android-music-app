package com.example.demo.views.MusicFragmentTabs;

import android.content.res.Resources;
import android.graphics.Rect;
import android.os.Bundle;

import androidx.fragment.app.Fragment;
import androidx.recyclerview.widget.DefaultItemAnimator;
import androidx.recyclerview.widget.GridLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import android.util.TypedValue;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import services.MusicService;
import com.example.demo.R;
import com.example.demo.views.Home;
import com.simplecityapps.recyclerview_fastscroll.views.FastScrollRecyclerView;

import java.util.ArrayList;

import adapters.AlbumAdapter;
import managers.MediaImporter;
import models.Album;
import models.Song;


/**
 * A simple {@link Fragment} subclass.
 */
public class AlbumsTabFragment extends Fragment implements AlbumAdapter.AlbumAdapterSortCallback{

    private TextView textView;
    private MediaImporter mediaImporter;
    private FastScrollRecyclerView recyclerView;
    private ArrayList<Song> _songs = new ArrayList<Song>();
    private AlbumAdapter albumAdapter;
    private ArrayList<Album> _albums = new ArrayList<Album>();

    MusicService musicService;

    public AlbumsTabFragment() {
    }


    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {

        View view = inflater.inflate(R.layout.fragment_albums_tab, container, false);

        mediaImporter = ((Home) (getActivity())).getMediaImporter();
        musicService = ((Home) (getActivity())).getMusicService();

        textView = view.findViewById(R.id.albumsTabText);

        _albums = mediaImporter.getLoadedAlbums();
        mediaImporter.setAlbumsSortOrder();

        if (_albums.size() == 0) {
            textView.setVisibility(View.VISIBLE);
        } else {
            textView.setVisibility(View.GONE);
            recyclerView = view.findViewById(R.id.album_recyclerView);
            albumAdapter = new AlbumAdapter(getActivity(), _albums, musicService, mediaImporter, AlbumsTabFragment.this);

            GridLayoutManager gridLayoutManager = new GridLayoutManager(this.getContext(), 2);
            gridLayoutManager.setSpanSizeLookup(new GridLayoutManager.SpanSizeLookup() {
                @Override
                public int getSpanSize(int position) {
                    switch (albumAdapter.getItemViewType(position)) {
                        case 1:
                            return 1;
                        default:
                            return 2;
                    }
                }
            });


            recyclerView.setLayoutManager(gridLayoutManager);

            recyclerView.addItemDecoration(new GridSpacingItemDecoration(2, dpToPx(10), true));
            recyclerView.setItemAnimator(new DefaultItemAnimator());
            recyclerView.setAdapter(albumAdapter);
        }

        return view;
    }

    @Override
    public void onSortOrderChanged() {
        albumAdapter.notifyDataSetChanged();
    }


    public class GridSpacingItemDecoration extends RecyclerView.ItemDecoration {

        private final int spanCount;
        private final int spacing;
        private final boolean includeEdge;

        public GridSpacingItemDecoration(int spanCount, int spacing, boolean includeEdge) {
            this.spanCount = spanCount;
            this.spacing = spacing;
            this.includeEdge = includeEdge;
        }

        @Override
        public void getItemOffsets(Rect outRect, View view, RecyclerView parent, RecyclerView.State state) {
            int position = parent.getChildAdapterPosition(view);
            int viewType = parent.getAdapter().getItemViewType(position);
            int column;

            if(position % spanCount == 0){
                column = 1;
            }else{
                column = 0;
            }

            if (viewType == 1){
                if (includeEdge) {
                    outRect.left = spacing - column * spacing / spanCount; // spacing - column * ((1f / spanCount) * spacing)
                    outRect.right = (column + 1) * spacing / spanCount; // (column + 1) * ((1f / spanCount) * spacing)

                    if (position <= spanCount && position > 0) { // top edge
                        outRect.top = spacing;
                    }
                    if (position > 0){
                        outRect.bottom = spacing; // item bottom
                    }

                } else {
                    outRect.left = column * spacing / spanCount; // column * ((1f / spanCount) * spacing)
                    outRect.right = spacing - (column + 1) * spacing / spanCount; // spacing - (column + 1) * ((1f /    spanCount) * spacing)
                    if (position > spanCount) {
                        outRect.top = spacing; // item top
                    }
                }

            }

        }
    }


    private int dpToPx(int dp) {
        Resources r = getResources();
        return Math.round(TypedValue.applyDimension(TypedValue.COMPLEX_UNIT_DIP, dp, r.getDisplayMetrics()));
    }
}
