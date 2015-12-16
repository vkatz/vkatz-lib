package by.vkatz.samples;

import android.view.View;
import android.widget.TextView;

/**
 * Created by vKatz on 16.11.2015.
 */
public class CallBackScreen extends BaseScreen {

    public static CallBackScreen newInstance(String text) {
        return new CallBackScreen().withData("key", text);
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
                getParent().back().withData("RESULT", "some text");
            }
        });
    }
}
