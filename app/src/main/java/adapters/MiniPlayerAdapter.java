package adapters;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.RelativeLayout;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import services.MusicService;
import com.example.demo.R;

import java.util.ArrayList;

import listeners.OnSwipeTouchListener;
import models.Song;

public class MiniPlayerAdapter extends RecyclerView.Adapter<MiniPlayerAdapter.MiniPlayerViewHolder>  {

    private ArrayList<Song> _queue;
    private final Context context;

    MusicService musicService;


//    private final QueueManager mqueueManager;
//    private final PlaybackManager mplaybackManager;
    MiniPlayerItemClickListener miniPlayerItemClickListener;


    public MiniPlayerAdapter(Context context, MusicService musicService, MiniPlayerItemClickListener xItemClickListener){
        this.context = context;
        this.musicService = musicService;
        this._queue = musicService.getCurrentQueue();

//        this.mqueueManager = mqueueManager;
//        this.mplaybackManager = mplaybackManager;
        this.miniPlayerItemClickListener = xItemClickListener;

    }



    @NonNull
    @Override
    public MiniPlayerViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        return new MiniPlayerViewHolder(LayoutInflater.from(parent.getContext()).inflate(R.layout.mini_player_item, parent, false));
    }

    @Override
    public void onBindViewHolder(@NonNull MiniPlayerViewHolder holder, int position) {
        final Song song = _queue.get(position);
        holder.miniSong.setText(song.getSongName());
        holder.miniArtist.setText(song.getArtistName());
        holder.miniPlayerItem.setOnTouchListener(new OnSwipeTouchListener(context){

            @Override
            public void onClick()
            {
                super.onClick();
                miniPlayerItemClickListener.onMiniPlayerClick();

            }

            @Override
            public void onDoubleClick()
            {
                super.onDoubleClick();

                // your on onDoubleClick here
            }

            @Override
            public void onLongClick()
            {
                super.onLongClick();
                miniPlayerItemClickListener.onMiniPlayerLongClick();
            }

            @Override
            public void onSwipeUp() {
                super.onSwipeUp();
//                PlayerFragment fragmentB = new PlayerFragment(context);
//                FragmentManager manager = Objects.requireNonNull(getActivity()).getSupportFragmentManager();
//                FragmentTransaction transaction = manager.beginTransaction();
//                transaction.replace(R.id.MainLayout,fragmentB).addToBackStack(null).commit();
                // your swipe up here
            }

        });

    }



    @Override
    public int getItemCount() {
        return _queue.size();
    }


    public class MiniPlayerViewHolder extends RecyclerView.ViewHolder {

        TextView miniSong, miniArtist;
        RelativeLayout miniPlayerItem;


        public MiniPlayerViewHolder(@NonNull View itemView) {
            super(itemView);

            miniSong = itemView.findViewById(R.id.mini_current_song);
            miniArtist = itemView.findViewById(R.id.mini_current_artist);
            miniPlayerItem = itemView.findViewById(R.id.miniPlayerItem);
        }
    }

    public interface MiniPlayerItemClickListener{
        void onMiniPlayerClick();
        void onMiniPlayerLongClick();
    }



}
