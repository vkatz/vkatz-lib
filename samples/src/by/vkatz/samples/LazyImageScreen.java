package by.vkatz.samples;

import android.util.Pair;
import android.view.View;
import by.vkatz.screens.Screen;
import by.vkatz.utils.ContextUtils;
import by.vkatz.widgets.LazyImage;

import java.io.File;

/**
 * Created by vKatz on 08.03.2015.
 */
public class LazyImageScreen extends Screen {
    @Override
    public View getView() {
        return ContextUtils.getView(getContext(), R.layout.lazy_image_screen);
    }

    @Override
    public void onShow(View view) {
        super.onShow(view);
        view.findViewById(R.id.clear_cache).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                for (Pair<String, File> i : LazyImage.getCachedFiles(getContext())) i.second.delete();
            }
        });
        ((LazyImage) view.findViewById(R.id.lazy_image_view)).setImage(LazyImage.Options.create("http://www.android.com/new/images/versions/android-l/your-device.jpg"));
    }
}
