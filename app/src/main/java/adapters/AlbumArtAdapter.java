package adapters;

import android.content.Context;
import android.view.GestureDetector;
import android.view.LayoutInflater;
import android.view.MotionEvent;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.bumptech.glide.Glide;
import services.MusicService;
import com.example.demo.R;

import java.util.ArrayList;

import models.Song;

public class AlbumArtAdapter extends RecyclerView.Adapter<AlbumArtAdapter.AlbumArtViewHolder>  {

    private ArrayList<Song> _queue = new ArrayList<Song>();
    private final Context context;

    MusicService musicService;

//    private final QueueManager mqueueManager;
//    private final PlaybackManager mplaybackManager;
    AlbumArtDoubleTap albumArtDoubleTap;


    public AlbumArtAdapter(Context context, MusicService musicService, AlbumArtDoubleTap mDoubleTap){
        this.context = context;
        this.musicService = musicService;
        this._queue = musicService.getCurrentQueue();
//        this.mqueueManager = mqueueManager;
//        this.mplaybackManager = mplaybackManager;
        this.albumArtDoubleTap = mDoubleTap;
    }


    @NonNull
    @Override
    public AlbumArtViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        return new AlbumArtViewHolder(LayoutInflater.from(parent.getContext()).inflate(R.layout.album_art_item, parent, false));
    }

    @Override
    public void onBindViewHolder(@NonNull AlbumArtViewHolder holder, int position) {
        final Song song = _queue.get(position);
        Glide.with(context).load(song.getSongUrl()).into(holder.albumArt);
        holder.albumArt.setOnTouchListener(new View.OnTouchListener() {
            private GestureDetector gestureDetector = new GestureDetector(context, new GestureDetector.SimpleOnGestureListener() {
                public boolean onDoubleTap(MotionEvent e) {
                    if (!song.getFavorite()){
                        song.setFavorite(true);
                        albumArtDoubleTap.onAlbumArtDoubleTap();

                    }
                    return super.onDoubleTap(e);
                }

            });
            @Override
            public boolean onTouch(View v, MotionEvent event) {
                gestureDetector.onTouchEvent(event);
                return true;
            }
        });

    }


    @Override
    public int getItemCount() {
        return _queue.size();
    }


    public class AlbumArtViewHolder extends RecyclerView.ViewHolder {

        ImageView albumArt;

        public AlbumArtViewHolder(@NonNull View itemView) {
            super(itemView);

            albumArt = itemView.findViewById(R.id.current_album_art);
        }
    }

    public interface AlbumArtDoubleTap{
        void onAlbumArtDoubleTap();
    }

}
