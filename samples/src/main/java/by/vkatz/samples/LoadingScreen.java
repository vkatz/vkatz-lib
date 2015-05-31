package by.vkatz.samples;

import android.os.Handler;
import android.view.View;
import by.vkatz.screens.Screen;
import by.vkatz.utils.ContextUtils;
import by.vkatz.widgets.LoadingView;

/**
 * Created by vKatz on 08.03.2015.
 */
public class LoadingScreen extends Screen {
    int pr = 0;

    @Override
    public View getView() {
        return ContextUtils.getView(getContext(), R.layout.loading_screen);
    }

    @Override
    public void onShow(View view) {
        super.onShow(view);
        final LoadingView loadingView = (LoadingView) view.findViewById(R.id.progress);
        final Handler handler = new Handler();
        handler.postDelayed(new Runnable() {
            @Override
            public void run() {
                if (pr < 100) pr += 5;
                loadingView.setProgress(pr);
                handler.postDelayed(this, 200);
            }
        }, 3000);
    }
}
