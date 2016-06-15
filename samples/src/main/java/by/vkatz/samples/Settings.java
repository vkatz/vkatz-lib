package by.vkatz.samples;

import android.content.Context;

import java.io.File;
import java.io.Serializable;

import by.vkatz.utils.Functions;
import by.vkatz.utils.SerializableUtils;

/**
 * Created by vKatz on 08.03.2015.
 */
public class Settings implements Serializable {
    private static final long serialVersionUID = SerializableUtils.generateSerialVersionUID(Settings.class);

    private boolean firstLaunch;

    public Settings() {
        firstLaunch = true;
    }

    public static Settings load(Context context) {
        return SerializableUtils.restore(new File(context.getExternalFilesDir(""), "data.sr"), new Functions.Func0<Settings>() {
            @Override
            public Settings execute() {
                return new Settings();
            }
        });
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
}
