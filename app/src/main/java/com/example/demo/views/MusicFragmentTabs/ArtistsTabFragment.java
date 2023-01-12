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

import adapters.ArtistAdapter;
import managers.MediaImporter;
import models.AlbumArtist;


/**
 * A simple {@link Fragment} subclass.
 */
public class ArtistsTabFragment extends Fragment implements ArtistAdapter.ArtistAdapterSortCallback {

    private MediaImporter mediaImporter;
    private FastScrollRecyclerView recyclerView;
    private ArrayList<AlbumArtist> _artists = new ArrayList<AlbumArtist>();
    private ArtistAdapter artistAdapter;
    private TextView textView;
    MusicService musicService;

    public ArtistsTabFragment() {
        // Required empty public constructor
    }


    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        View view = inflater.inflate(R.layout.fragment_artists_tab, container, false);

        mediaImporter = ((Home) (getActivity())).getMediaImporter();
        musicService = ((Home) (getActivity())).getMusicService();


        textView = view.findViewById(R.id.artistsTabText);
        recyclerView = view.findViewById(R.id.artist_recyclerView);

        _artists = mediaImporter.getLoadedArtists();
        mediaImporter.setArtistsSortOrder();


        if (_artists.size() == 0) {
            textView.setVisibility(View.VISIBLE);
        } else {
            textView.setVisibility(View.GONE);

            artistAdapter = new ArtistAdapter(getActivity(), _artists, musicService, mediaImporter, ArtistsTabFragment.this);

            GridLayoutManager gridLayoutManager = new GridLayoutManager(this.getContext(), 2);
            gridLayoutManager.setSpanSizeLookup(new GridLayoutManager.SpanSizeLookup() {
                @Override
                public int getSpanSize(int position) {
                    switch (artistAdapter.getItemViewType(position)) {
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
            recyclerView.setAdapter(artistAdapter);
        }


        return view;
    }

    @Override
    public void onSortOrderChanged() {
        artistAdapter.notifyDataSetChanged();
    }

    public static class GridSpacingItemDecoration extends RecyclerView.ItemDecoration {

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
