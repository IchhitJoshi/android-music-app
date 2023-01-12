package glide;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;

import com.bumptech.glide.load.Options;
import com.bumptech.glide.load.model.ModelLoader;
import com.bumptech.glide.signature.ObjectKey;

import java.io.InputStream;

public class ArtworkModelLoader implements ModelLoader<String, InputStream> {

    @Nullable
    @Override
    public LoadData<InputStream> buildLoadData(@NonNull String song, int width, int height, @NonNull Options options) {
        return new LoadData<>(new ObjectKey(song), new ArtworkFetcher(song));
    }

    @Override
    public boolean handles(@NonNull String song) {
        return true;
    }
}
