package by.vkatz.screens;

import android.app.Activity;
import android.app.Fragment;
import android.app.FragmentManager;
import android.app.FragmentTransaction;
import android.os.Bundle;
import android.os.Handler;
import android.os.Parcelable;

import java.io.Serializable;
import java.util.HashMap;

/**
 * Created by vKatz on 17.09.2015.
 */
@SuppressWarnings("unused")
public abstract class ScreensActivity extends Activity {
    public enum TransactionType {New, Old, Transit}

    private int containerId;
    private Handler handler;
    private HashMap<String, Object> data;
    private TransactionType transactionType;
    private TransactionBundle transactionBundle;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        handler = new Handler();
        data = new HashMap<>();
        init();
    }

    /**
     * Should call {@link #init(int, int)}
     */
    public abstract void init();

    /**
     * Init ScreenActivity
     *
     * @param layout id of layout resource
     * @param id     id of some ViewGroup used for display screens
     */
    public final void init(int layout, int id) {
        setContentView(layout);
        containerId = id;
    }

    /**
     * same as {@link #go(Screen, boolean) go(sceen,true)}
     */
    public void go(Screen screen) {
        go(screen, true);
    }

    /**
     * same as {@link #go(Screen, boolean, String) go(sceen,true,null)}
     */
    public void go(Screen screen, boolean storeInHistory) {
        go(screen, storeInHistory, null);
    }

    /**
     * same as {@link #go(Screen, boolean, String, boolean) go(sceen,true,null,false)}
     */
    public void go(Screen screen, boolean storeInHistory, String name) {
        go(screen, storeInHistory, name, false);
    }

    /**
     * same as {@link #go(Screen, boolean, String, boolean, TransactionAction) go(sceen,true,null,false,null)}
     */
    public void go(Screen screen, boolean storeInHistory, String name, boolean add) {
        go(screen, storeInHistory, name, add, null);
    }

    /**
     * @param screen         screen to go on
     * @param storeInHistory put current screen to back stack
     * @param name           transaction and screen name
     * @param add            in case true will be used fragmentTransaction.add instead of fragmentTransaction.replace
     * @param act            allow to perform specific actions before commit transition
     */
    public void go(Screen screen, boolean storeInHistory, String name, boolean add, TransactionAction act) {
        transactionBundle = null;
        transactionType = TransactionType.New;
        FragmentTransaction fragmentTransaction = getFragmentManager().beginTransaction();
        if (add) fragmentTransaction.add(containerId, screen, name);
        else fragmentTransaction.replace(containerId, screen, name);
        if (storeInHistory) fragmentTransaction.addToBackStack(name);
        if (act != null) act.act(screen, fragmentTransaction);
        fragmentTransaction.commit();
    }

    public TransactionBundle back() {
        transactionType = TransactionType.Old;
        getFragmentManager().popBackStack();
        return (transactionBundle = new TransactionBundle());
    }

    public TransactionBundle backTo(String name) {
        transactionType = TransactionType.Old;
        getFragmentManager().popBackStack(name, FragmentManager.POP_BACK_STACK_INCLUSIVE);
        return (transactionBundle = new TransactionBundle());
    }

    /**
     * Will back you to first fragment</br>
     * If u need to go on new screen and clear all others from history, just call this method, go(Scree,false) after
     */
    public void clearHistory() {
        transactionType = TransactionType.Transit;
        FragmentManager fragmentManager = getFragmentManager();
        while (fragmentManager.getBackStackEntryCount() > 0)
            fragmentManager.popBackStackImmediate();
    }

    public Screen getCurrentScreen() {
        Fragment currentFragment = getFragmentManager().findFragmentById(containerId);
        if (currentFragment != null && (currentFragment instanceof Screen)) return (Screen) currentFragment;
        else return null;
    }

    public void backPressed() {
        super.onBackPressed();
    }

    @Override
    public final void onBackPressed() {
        Screen current = getCurrentScreen();
        if (current == null || !current.onBackPressed()) {
            transactionType = TransactionType.Old;
            if (!getFragmentManager().popBackStackImmediate())
                backPressed();
        }
    }

    public void postDelayer(long delay, Runnable action) {
        handler.postDelayed(action, delay);
    }

    public void setData(String key, Object data) {
        this.data.put(key, data);
    }

    public Object getData(String key) {
        return data.get(key);
    }

    @SuppressWarnings("unchecked")
    public <T> T getData(String key, Class<T> T) {
        return (T) getData(key);
    }

    public TransactionType getTransactionType() {
        return transactionType;
    }

    public TransactionBundle getTransactionBundle() {
        if (transactionBundle == null) return null;
        else if (transactionBundle.getBundle().size() == 0) return null;
        else return transactionBundle;
    }

    public interface TransactionAction {
        void act(Screen screen, FragmentTransaction transaction);
    }

    public static class TransactionBundle {
        private Bundle bundle;

        public TransactionBundle() {
            bundle = new Bundle();
        }

        public final <Data extends Serializable> TransactionBundle withData(String key, Data data) {
            bundle.putSerializable(key, data);
            return this;
        }

        public final <Data extends Parcelable> TransactionBundle withData(String key, Data data) {
            bundle.putParcelable(key, data);
            return this;
        }

        public Bundle getBundle() {
            return bundle;
        }

        public Object getRawData(String key) {
            return bundle.get(key);
        }

        @SuppressWarnings("unchecked")
        public final <T> T getData(String key) {
            return (T) getRawData(key);
        }
    }
}
