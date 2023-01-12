package com.example.demo.views;

import android.os.Bundle;

import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.Toolbar;
import androidx.fragment.app.Fragment;
import androidx.viewpager.widget.ViewPager;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import com.example.demo.R;
import adapters.PagerController;
import com.google.android.material.tabs.TabItem;
import com.google.android.material.tabs.TabLayout;


/**
 * A simple {@link Fragment} subclass.
 */
public class MusicFragment extends Fragment {

    Toolbar toolbar;
    TabLayout tabLayout;
    TabItem songs;
    TabItem artists;
    TabItem albums;
    TabItem playlists;
    ViewPager viewPager;
    PagerController pagerController;


    public MusicFragment() {
        // Required empty public constructor
    }


    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        View view = inflater.inflate(R.layout.fragment_music, container, false);


        toolbar = view.findViewById(R.id.toolbarMusic);
        ((AppCompatActivity)getActivity()).setSupportActionBar(toolbar);

        tabLayout = view.findViewById(R.id.tabLayout);
        songs = view.findViewById(R.id.songs);
        artists = view.findViewById(R.id.artists);
        albums = view.findViewById(R.id.albums);
        playlists = view.findViewById(R.id.playlists);
        viewPager = view.findViewById(R.id.viewPager);

        pagerController = new PagerController(getChildFragmentManager(), tabLayout.getTabCount());
        viewPager.setAdapter(pagerController);

        tabLayout.addOnTabSelectedListener(new TabLayout.OnTabSelectedListener() {
            @Override
            public void onTabSelected(TabLayout.Tab tab) {
                viewPager.setCurrentItem(tab.getPosition());
            }

            @Override
            public void onTabUnselected(TabLayout.Tab tab) {

            }

            @Override
            public void onTabReselected(TabLayout.Tab tab) {

            }
        });

        viewPager.addOnPageChangeListener(new TabLayout.TabLayoutOnPageChangeListener(tabLayout));

        return view;
    }


}
