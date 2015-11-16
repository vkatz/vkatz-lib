package by.vkatz.samples;

import android.view.View;
import android.widget.TextView;

/**
 * Created by vKatz on 16.11.2015.
 */
public class CallBackScreen extends BaseScreen {

    private Runnable runable;

    public static CallBackScreen newInstance(String text, Runnable runable) {
        CallBackScreen screen = new CallBackScreen();
        screen.runable = runable;
        return screen.withData("key", text);
    }

    @Override
    public View createView() {
        return new TextView(getContext());
    }

    @Override
    public void initView(View view) {
        super.initView(view);
        ((TextView) view).setText((String) getData("key"));
        view.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                runable.run();
                getParent().back();
            }
        });
    }
}
