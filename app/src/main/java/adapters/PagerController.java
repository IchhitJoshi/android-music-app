package adapters;

import androidx.annotation.NonNull;
import androidx.fragment.app.Fragment;
import androidx.fragment.app.FragmentManager;
import androidx.fragment.app.FragmentPagerAdapter;

import com.example.demo.views.MusicFragmentTabs.AlbumsTabFragment;
import com.example.demo.views.MusicFragmentTabs.ArtistsTabFragment;
import com.example.demo.views.MusicFragmentTabs.PlaylistsTabFragment;
import com.example.demo.views.MusicFragmentTabs.SongsTabFragment;

public class PagerController extends FragmentPagerAdapter {
    int tabCounts;


    public PagerController(FragmentManager fm, int tabCounts) {
        super(fm);
        this.tabCounts = tabCounts;

    }

    @NonNull
    @Override
    public Fragment getItem(int i) {

        switch (i) {
            case 0:
                return new SongsTabFragment();
            case 1:
                return new ArtistsTabFragment();
            case 2:
                return new AlbumsTabFragment();
            case 3:
                return new PlaylistsTabFragment();
            default:
                return null;
        }

    }

    @Override
    public int getCount() {
        return tabCounts;
    }


}
