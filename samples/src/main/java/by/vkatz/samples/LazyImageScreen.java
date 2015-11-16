package by.vkatz.samples;

import android.util.Pair;
import android.view.View;

import java.io.File;

import by.vkatz.widgets.LazyImage;

/**
 * Created by vKatz on 08.03.2015.
 */
public class LazyImageScreen extends BaseScreen {
    @Override
    public View createView() {
        return View.inflate(getContext(), R.layout.lazy_image_screen, null);
    }

    @Override
    public void initView(View view) {
        super.initView(view);
        view.findViewById(R.id.clear_cache).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                for (Pair<String, File> i : LazyImage.getCachedFiles(getContext())) i.second.delete();
            }
        });
        ((LazyImage) view.findViewById(R.id.lazy_image_view)).setImage(LazyImage.Options.create("http://www.android.com/new/images/versions/android-l/your-device.jpg"));
    }
}
