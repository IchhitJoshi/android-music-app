package glide;

import android.media.MediaMetadataRetriever;

import androidx.annotation.NonNull;

import com.bumptech.glide.Priority;
import com.bumptech.glide.load.DataSource;
import com.bumptech.glide.load.data.DataFetcher;

import java.io.ByteArrayInputStream;
import java.io.InputStream;

public class ArtworkFetcher implements DataFetcher<InputStream> {
    private final String song;

    public ArtworkFetcher(String song) {
        this.song = song;
    }

    @Override
    public void loadData(@NonNull Priority priority, @NonNull DataCallback<? super InputStream> callback) {
        MediaMetadataRetriever retriever = new MediaMetadataRetriever();
        try {
            retriever.setDataSource(song);
        } catch (IllegalArgumentException e) {
            e.printStackTrace();
        }
        byte[] art = retriever.getEmbeddedPicture();
        retriever.release();
        InputStream is = new ByteArrayInputStream(art);
        callback.onDataReady(is);

    }

    @Override
    public void cleanup() {

    }

    @Override
    public void cancel() {

    }

    @NonNull
    @Override
    public Class<InputStream> getDataClass() {
        return InputStream.class;
    }

    @NonNull
    @Override
    public DataSource getDataSource() {
        return DataSource.LOCAL;
    }
}
