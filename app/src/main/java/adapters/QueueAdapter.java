package adapters;

import android.content.Context;
import android.graphics.Color;
import android.view.LayoutInflater;
import android.view.MenuItem;
import android.view.MotionEvent;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.PopupMenu;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.core.view.MotionEventCompat;
import androidx.recyclerview.widget.RecyclerView;

import com.bumptech.glide.Glide;
import services.MusicService;
import com.example.demo.R;

import java.util.ArrayList;
import java.util.Collections;

import listeners.OnStartDragListener;
import models.Song;
import utils.ItemTouchHelperAdapter;
import utils.ItemTouchHelperViewHolder;

import static com.example.demo.views.Home.queue_ascending;
import static com.example.demo.views.Home.queue_sort_number;

public class QueueAdapter extends RecyclerView.Adapter<RecyclerView.ViewHolder> implements ItemTouchHelperAdapter {

    private ArrayList<Song> _queue = new ArrayList<Song>();
    private final QueueItemClickListener queueItemClickListener;
    private final Context context;
//    private final QueueManager mqueueManager;
//    private final PlaybackManager mplaybackManager;

    MusicService musicService;

    private static final int TYPE_ITEM = 1;
    private static final int TYPE_HEADER = 0;
    private final OnQueueChangedListener mQueueChangedListener;

    private final OnStartDragListener mDragStartListener;


    public QueueAdapter(Context context, ArrayList<Song> queue, MusicService musicService, QueueItemClickListener queueItemClickListener, OnStartDragListener dragListener, OnQueueChangedListener queueChangedListener){
        this.context = context;
        this._queue = queue;
        this.musicService = musicService;
//        this.mqueueManager = queueManager;
//        this.mplaybackManager = playbackManager;
        this.queueItemClickListener = queueItemClickListener;
        this.mDragStartListener = dragListener;
        this.mQueueChangedListener = queueChangedListener;
    }

    @NonNull
    @Override
    public RecyclerView.ViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {

        if (viewType == TYPE_ITEM){
            View myView = LayoutInflater.from(context).inflate(R.layout.queue_row, parent, false);
            return new QueueHolder(myView,musicService);

        } else if (viewType == TYPE_HEADER){
            View myView = LayoutInflater.from(context).inflate(R.layout.queue_header, parent, false);
            return new HeaderViewHolder(myView);

        }
        else return null;

    }

    @Override
    public void onBindViewHolder(final RecyclerView.ViewHolder holder, final int position) {
        if (holder instanceof HeaderViewHolder) {
            final HeaderViewHolder headerViewHolder = (HeaderViewHolder) holder;

            headerViewHolder.queue_header_sort.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    androidx.appcompat.widget.PopupMenu popupMenu = new androidx.appcompat.widget.PopupMenu(context, headerViewHolder.queue_header_sort);
                    popupMenu.inflate(R.menu.queue_sort_options_menu);
                    MenuItem sort_custom = popupMenu.getMenu().findItem(R.id.queue_sort_item1);
                    MenuItem sort_name = popupMenu.getMenu().findItem(R.id.queue_sort_item2);
                    MenuItem sort_artist = popupMenu.getMenu().findItem(R.id.queue_sort_item3);
                    MenuItem sort_date = popupMenu.getMenu().findItem(R.id.queue_sort_item4);
                    MenuItem sort_duration = popupMenu.getMenu().findItem(R.id.queue_sort_item5);
                    MenuItem sort_year = popupMenu.getMenu().findItem(R.id.queue_sort_item6);
                    MenuItem sort_album = popupMenu.getMenu().findItem(R.id.queue_sort_item7);
                    MenuItem sort_ascending = popupMenu.getMenu().findItem(R.id.queue_sort_item8);

                    if (queue_sort_number == 1){
                       sort_custom.setChecked(true);
                    }else if (queue_sort_number == 2 ){
                        sort_name.setChecked(true);
                    }else if (queue_sort_number == 3 ){
                        sort_artist.setChecked(true);
                    }else if (queue_sort_number == 4 ){
                        sort_date.setChecked(true);
                    }else if (queue_sort_number == 5 ){
                        sort_duration.setChecked(true);
                    }else if (queue_sort_number == 6 ){
                        sort_year.setChecked(true);
                    }else if (queue_sort_number == 7 ){
                        sort_album.setChecked(true);
                    }

                    if (queue_ascending){
                        sort_ascending.setChecked(true);
                    }else {
                        sort_ascending.setChecked(false);
                    }

                    popupMenu.setOnMenuItemClickListener(new androidx.appcompat.widget.PopupMenu.OnMenuItemClickListener() {
                        @Override
                        public boolean onMenuItemClick(MenuItem item) {

                            switch (item.getItemId()){
                                case R.id.queue_sort_item1:
                                    if(!queue_ascending  && queue_sort_number != 1){
                                        queue_sort_number = 1;
                                        sort();
                                        reverse(); }

                                    if(queue_sort_number != 1){
                                        queue_sort_number = 1;
                                        sort();
                                    }

                                    break;
                                case R.id.queue_sort_item2:
                                    item.setChecked(true);

                                    if(!queue_ascending  && queue_sort_number != 2){
                                        queue_sort_number = 2;
                                        sort();
                                        reverse(); }

                                    if(queue_sort_number != 2){
                                        queue_sort_number = 2;
                                        sort();
                                    }


                                    break;
                                case R.id.queue_sort_item3:
                                    item.setChecked(true);

                                    if(!queue_ascending && queue_sort_number != 3){
                                        queue_sort_number = 3;
                                        sort();
                                        reverse(); }

                                    if(queue_sort_number != 3){
                                        queue_sort_number = 3;
                                        sort();
                                    }

                                    break;

                                case R.id.queue_sort_item4:
                                    item.setChecked(true);

                                    if(!queue_ascending && queue_sort_number != 4){
                                        queue_sort_number = 4;
                                        sort();
                                        reverse(); }

                                    if(queue_sort_number != 4){
                                        queue_sort_number = 4;
                                        sort();
                                    }

                                    break;
                                case R.id.queue_sort_item5:
                                    item.setChecked(true);

                                    if(!queue_ascending && queue_sort_number != 5){
                                        queue_sort_number = 5;
                                        sort();
                                        reverse(); }

                                    if(queue_sort_number != 5){
                                        queue_sort_number = 5;
                                        sort();
                                    }

                                    break;
                                case R.id.queue_sort_item6:
                                    item.setChecked(true);

                                    if(!queue_ascending && queue_sort_number != 6){
                                        queue_sort_number = 6;
                                        sort();
                                        reverse(); }

                                    if(queue_sort_number != 6){
                                        queue_sort_number = 6;
                                        sort();
                                    }

                                    break;

                                case R.id.queue_sort_item7:
                                    item.setChecked(true);

                                    if(!queue_ascending && queue_sort_number != 7){
                                        queue_sort_number = 7;
                                        sort();
                                        reverse(); }

                                    if(queue_sort_number != 7){
                                        queue_sort_number = 7;
                                        sort();
                                    }

                                    break;


                                case R.id.queue_sort_item8:

                                    if (queue_ascending){
                                        queue_ascending = false;
                                    }else {
                                        queue_ascending = true;
                                    }
                                    if (item.isChecked()){
                                        item.setChecked(false);
                                    }else {
                                        item.setChecked(true);
                                    }
                                    reverse();
                                    break;
                                default:
                                    break;
                            }

                            return false;
                        }
                    });
                    popupMenu.show();
                }
            });

            headerViewHolder.queueRange.setText(musicService.getCurrentQueueItemPosition() + 1 + "/" + musicService.getCurrentQueue().size() + " • " + musicService.getQueueDuration(context));

        }

        else if (holder instanceof QueueHolder){

            final QueueHolder queueHolder = (QueueHolder) holder;

            final Song yqueue = _queue.get(position-1);
            queueHolder.queueSong.setText(yqueue.getSongName());
            queueHolder.queueArtist.setText(yqueue.getArtistName() + " • " + yqueue.getDurationLabel(context));
            Glide.with(context).load(yqueue.getSongUrl()).into(queueHolder.queueArt);
            if(position-1 == musicService.getCurrentQueueItemPosition()){
                queueHolder.queueMenu.setImageResource(R.drawable.queue_menu_current);
            }else {
                queueHolder.queueMenu.setImageResource(R.drawable.queue_menu);
            }

            queueHolder.queueMenuLayout.setOnTouchListener(new View.OnTouchListener() {
                @Override
                public boolean onTouch(View v, MotionEvent event) {
                    if (MotionEventCompat.getActionMasked(event) == MotionEvent.ACTION_DOWN) {
                        mDragStartListener.onStartDrag(holder);
                    }
                    return false;
                }
            });

            queueHolder.queueOptions.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    final PopupMenu popupMenu = new PopupMenu(context, queueHolder.queueOptions);
                    popupMenu.inflate(R.menu.queue_row_options);
                    popupMenu.setOnMenuItemClickListener(new PopupMenu.OnMenuItemClickListener() {
                        @Override
                        public boolean onMenuItemClick(MenuItem item) {

                            switch (item.getItemId()){
                                case R.id.queue_row_item1:
                                    Toast.makeText(context, "Playing Next - " + _queue.get(position - 1).getSongName(), Toast.LENGTH_LONG).show();

                                    _queue.add(musicService.getCurrentQueueItemPosition() + 1 , _queue.remove(position - 1));

                                    if(position - 1 == musicService.getCurrentQueueItemPosition()){
                                        musicService.setCurrentQueueItemPosition(musicService.getCurrentQueueItemPosition() + 1);
                                    }


                                    notifyDataSetChanged();
                                    mQueueChangedListener.onQueueChanged();
                                    break;
                                case R.id.queue_row_newPlaylist:
                                    Toast.makeText(context, "New Playlist", Toast.LENGTH_LONG).show();
                                    break;
                                case R.id.queue_row_Favorites:
                                    Toast.makeText(context, "Added to Favorites", Toast.LENGTH_LONG).show();
                                    break;
                                case R.id.queue_row_item3:
                                    Toast.makeText(context, "Edit Song Info", Toast.LENGTH_LONG).show();
                                    break;
                                case R.id.queue_row_item4:
                                    Toast.makeText(context, "Song Info", Toast.LENGTH_LONG).show();
                                    break;
                                case R.id.queue_row_GoToArtist:
                                    Toast.makeText(context, "Go to Artist", Toast.LENGTH_LONG).show();
                                    break;
                                case R.id.queue_row_GoToAlbum:
                                    Toast.makeText(context, "Go to Album", Toast.LENGTH_LONG).show();
                                    break;
                                case R.id.queue_row_GoToGenre:
                                    Toast.makeText(context, "Go to Genre", Toast.LENGTH_LONG).show();
                                    break;
                                case R.id.queue_row_item6:
                                    Toast.makeText(context, "Removed " + yqueue.getSongName() + " from Queue", Toast.LENGTH_LONG).show();

                                    if (position - 1 < musicService.getCurrentQueueItemPosition()){
                                        musicService.setCurrentQueueItemPosition(musicService.getCurrentQueueItemPosition() - 1);
                                        musicService.removeQueueItem(yqueue);
                                        _queue.remove(yqueue);

                                    }else if(position - 1 == musicService.getCurrentQueueItemPosition()){
                                        musicService.removeQueueItem(yqueue);
                                        _queue.remove(yqueue);
                                        musicService.loadSong(musicService.getCurrentQueueItem());
                                        musicService.play();
                                        if (queueItemClickListener != null){
                                            queueItemClickListener.onQueueItemClick(_queue.get(position - 1));
                                        }

                                    } else{
                                        musicService.removeQueueItem(yqueue);
                                        _queue.remove(yqueue);

                                    }

                                    notifyDataSetChanged();

                                    mQueueChangedListener.onQueueChanged();
                                    break;
                                case R.id.queue_row_item7:
                                    Toast.makeText(context, "Deleted", Toast.LENGTH_LONG).show();
                                    break;
                                default:
                                    break;
                            }
                            return false;
                        }
                    });
                    popupMenu.show();
                }
            });

        }


    }

    @Override
    public int getItemViewType(int position) {
        if (position == 0) {
            return TYPE_HEADER;
        }
        return TYPE_ITEM;
    }

    @Override
    public int getItemCount() {
        return _queue.size() + 1;
    }

    @Override
    public void onItemMove(int fromPosition, int toPosition) {
        Collections.swap(_queue, fromPosition - 1, toPosition - 1);
        notifyItemMoved(fromPosition, toPosition);
        musicService.updateQueue(_queue, fromPosition-1, toPosition-1);
        mQueueChangedListener.onQueueChanged();

    }

    @Override
    public void onItemDismiss(int position) {

        Song yqueue = _queue.get(position - 1);
        Toast.makeText(context, "Removed " + yqueue.getSongName() + " from Queue", Toast.LENGTH_LONG).show();

        if (position - 1 < musicService.getCurrentQueueItemPosition()){
            musicService.setCurrentQueueItemPosition(musicService.getCurrentQueueItemPosition() - 1);
            musicService.removeQueueItem(yqueue);
            _queue.remove(yqueue);

        }else if(position - 1 == musicService.getCurrentQueueItemPosition()){
            musicService.removeQueueItem(yqueue);
            _queue.remove(yqueue);
            musicService.loadSong(musicService.getCurrentQueueItem());
            musicService.play();
            if (queueItemClickListener != null){
                queueItemClickListener.onQueueItemClick(_queue.get(position - 1));
            }

        } else{
            musicService.removeQueueItem(yqueue);
            _queue.remove(yqueue);

        }

        notifyDataSetChanged();
        mQueueChangedListener.onQueueChanged();
    }

    private void sort(){
//        musicService.setQueueSortOrder();
        notifyDataSetChanged();
        mQueueChangedListener.onQueueChanged();

    }

    private void reverse(){
        Collections.reverse(_queue);
        notifyDataSetChanged();
        mQueueChangedListener.onQueueChanged();
    }

    private class HeaderViewHolder extends RecyclerView.ViewHolder{
        ImageView queue_header_sort;
        TextView queueRange;

        public HeaderViewHolder(@NonNull View itemView) {
            super(itemView);
            queue_header_sort = (ImageView) itemView.findViewById(R.id.queue_sort);
            queueRange = (TextView) itemView.findViewById(R.id.queueRange);

        }
    }


    public class QueueHolder extends RecyclerView.ViewHolder implements View.OnClickListener, ItemTouchHelperViewHolder {

        TextView queueSong, queueArtist, queueOptions;
        ImageView queueArt, queueMenu;
        LinearLayout queueMenuLayout;
        MusicService mservice;

        public QueueHolder(@NonNull View itemView, MusicService xmusicService) {
            super(itemView);

            mservice = xmusicService;

            queueSong = itemView.findViewById(R.id.queue_song);
            queueArtist = itemView.findViewById(R.id.queue_artist);
            queueArt = itemView.findViewById(R.id.queue_smallArt);
            queueOptions = itemView.findViewById(R.id.queue_Options);
            queueMenu = itemView.findViewById(R.id.queue_menu);
            queueMenuLayout = itemView.findViewById(R.id.queueMenuLayout);

            itemView.setOnClickListener(this);
        }

        @Override
        public void onClick(View v) {
            int current_position = getAdapterPosition() - 1;

            musicService.setCurrentQueueItemPosition(current_position);

            musicService.loadSong(_queue.get(current_position));
            musicService.play();

            queueMenu.setImageResource(R.drawable.queue_menu_current);
            if (queueItemClickListener != null){
                queueItemClickListener.onQueueItemClick(_queue.get(current_position));
            }


        }


        @Override
        public void onItemSelected() {
            itemView.setBackgroundColor(Color.LTGRAY);
        }

        @Override
        public void onItemClear() {
            itemView.setBackgroundColor(context.getResources().getColor(R.color.background));
        }
        }

        public interface QueueItemClickListener {
            void onQueueItemClick(Song queue_song);
        }

        public interface OnQueueChangedListener {
            void onQueueChanged();
        }

//
//    public void setOnQueueItemClickListener(QueueItemClickListener queueItemClickListener){
//        this.queueItemClickListener = queueItemClickListener;
//    }
}
