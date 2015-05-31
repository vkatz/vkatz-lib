package by.vkatz.activity;

import android.app.Application;

import java.util.HashMap;

/**
 * Created by vKatz on 04.05.2015.
 */
public abstract class DataActivityApplication extends Application {
    HashMap<String, DataActivityImpl> impls = new HashMap<>();
    int cnt = 0;

    public DataActivityImpl getImpl(String screen) {
        return impls.get(screen);
    }

    public String setImpl(DataActivityImpl impl) {
        String screen = "screen-" + cnt++;
        impls.put(screen, impl);
        return screen;
    }

    public void finishImpl(String screen) {
        impls.remove(screen);
    }

    public abstract DataActivityImpl getFirst();

    public void clearImpls() {
        impls.clear();
    }
}
