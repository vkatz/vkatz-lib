package by.vkatz.utils;

import java.util.Stack;
import java.util.concurrent.Executor;

/**
 * Created by vKatz on 27.02.2015.
 */
@SuppressWarnings("unused")
public class SelfKillerExecutor implements Executor {
    private final Object sync = new Object();
    private Thread thread = null;
    private Stack<Runnable> tasks = new Stack<>();

    @Override
    public void execute(Runnable runnable) {
        tasks.push(runnable);
        synchronized (sync) {
            if (thread == null) {
                thread = new Thread(new Runnable() {
                    @Override
                    public void run() {
                        while (!tasks.isEmpty()) try {
                            tasks.pop().run();
                        } catch (Exception e) {
                            e.printStackTrace();
                        }
                        synchronized (sync) {
                            thread = null;
                        }
                    }
                });
                thread.start();
            }
        }
    }
}