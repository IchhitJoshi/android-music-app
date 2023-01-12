package com.example.demo.views.MusicFragmentTabs;

import android.os.Bundle;

import androidx.fragment.app.Fragment;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import com.example.demo.R;


/**
 * A simple {@link Fragment} subclass.
 */
public class PlaylistsTabFragment extends Fragment {

    public PlaylistsTabFragment() {
        // Required empty public constructor
    }


    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        return inflater.inflate(R.layout.fragment_playlists_tab, container, false);
    }
}
