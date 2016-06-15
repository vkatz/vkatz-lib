package by.vkatz.samples;

import android.view.View;
import android.widget.ImageView;

import by.vkatz.utils.ImageLoader;

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
        final ImageLoader imageLoader = new ImageLoader(getContext());
        view.findViewById(R.id.clear_cache).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                imageLoader.clearCache();
            }
        });
        imageLoader.load((ImageView) view.findViewById(R.id.lazy_image_view), "https://content.onliner.by/news/2016/06/default/e5207dc33dbd02c57f20c148eab1ae51.jpg");
    }
}
