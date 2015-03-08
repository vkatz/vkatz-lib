package by.vkatz.utils;

import java.util.Stack;
import java.util.concurrent.Executor;

/**
 * Created by vKatz on 27.02.2015.
 */
public class SelfKillerExecutor implements Executor {
    private final Object sync = new Object();
    private Thread thread = null;
    private Stack<Runnable> tasks = new Stack<>();

    @Override
    public void execute(Runnable runnable) {
        synchronized (sync) {
            tasks.push(runnable);
            if (thread == null) {
                thread = new Thread(new Runnable() {
                    @Override
                    public void run() {
                        while (true) {
                            Runnable task;
                            synchronized (sync) {
                                if (tasks.empty()) {
                                    thread = null;
                                    return;
                                } else task = tasks.pop();
                            }
                            task.run();
                        }
                    }
                });
                thread.start();
            }
        }
    }
}