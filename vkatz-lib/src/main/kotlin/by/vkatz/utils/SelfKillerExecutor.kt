package by.vkatz.utils

import java.util.*
import java.util.concurrent.Executor

/**
 * Created by vKatz on 27.02.2015.
 */
class SelfKillerExecutor : Executor {
    private val sync = Any()
    private var thread: Thread? = null
    private val tasks = Stack<Runnable>()

    override fun execute(runnable: Runnable) {
        tasks.push(runnable)
        synchronized(sync) {
            if (thread == null) {
                thread = Thread {
                    while (!tasks.isEmpty())
                        try {
                            tasks.pop().run()
                        } catch (e: Exception) {
                            e.printStackTrace()
                        }
                    synchronized(sync) {
                        thread = null
                    }
                }
                thread!!.start()
            }
        }
    }
}