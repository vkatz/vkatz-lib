package by.vkatz.samples;

import android.content.Context;
import by.vkatz.utils.SerializableUtils;

import java.io.File;
import java.io.Serializable;

/**
 * Created by vKatz on 08.03.2015.
 */
public class Settings implements Serializable {
    private static final long serialVersionUID = SerializableUtils.generateSerialVersionUID(Settings.class);

    private boolean firstLaunch;

    public Settings() {
        firstLaunch = true;
    }

    public boolean isFirstLaunch() {
        return firstLaunch;
    }

    public void setFirstLaunch(boolean firstLaunch) {
        this.firstLaunch = firstLaunch;
    }

    public void commit(Context context) {
        SerializableUtils.commit(new File(context.getExternalFilesDir(""), "data.sr"), this);
    }

    public static Settings load(Context context) {
        return SerializableUtils.restore(new File(context.getExternalFilesDir(""), "data.sr"), Settings.class);
    }
}
