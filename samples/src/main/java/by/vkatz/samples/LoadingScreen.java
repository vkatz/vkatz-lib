package by.vkatz.samples;

import android.os.Handler;
import android.view.View;

import by.vkatz.widgets.LoadingView;

/**
 * Created by vKatz on 08.03.2015.
 */
public class LoadingScreen extends BaseScreen {
    int pr = 0;

    @Override
    public View createView() {
        return View.inflate(getContext(), R.layout.loading_screen, null);
    }

    @Override
    public void initView(View view) {
        super.initView(view);
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
