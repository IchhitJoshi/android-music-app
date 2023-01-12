package glide;

import androidx.annotation.NonNull;

import com.bumptech.glide.load.model.ModelLoader;
import com.bumptech.glide.load.model.MultiModelLoaderFactory;

import java.io.InputStream;

public class ModelLoaderFactory implements com.bumptech.glide.load.model.ModelLoaderFactory<String, InputStream> {
    @NonNull
    @Override
    public ModelLoader<String, InputStream> build(@NonNull MultiModelLoaderFactory multiFactory) {
        return new ArtworkModelLoader();
    }

    @Override
    public void teardown() {

    }
}
