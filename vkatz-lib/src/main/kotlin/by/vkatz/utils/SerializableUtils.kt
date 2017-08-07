package by.vkatz.utils

import java.io.*

object SerializableUtils {
    fun commit(file: File, data: Any): Boolean {
        try {
            val out = ObjectOutputStream(FileOutputStream(file))
            out.writeObject(data)
            out.flush()
            out.close()
            return true
        } catch (e: Exception) {
            LogUtils.e("SerializableUtils", "Commit error:", e)
            return false
        }
    }

    @Suppress("UNCHECKED_CAST")
    fun <T> restore(file: File, creator: () -> T): T {
        try {
            return ObjectInputStream(FileInputStream(file)).readObject() as T
        } catch (e: Exception) {
            LogUtils.e("SerializableUtils", "Restore error:", e)
            return creator()
        }
    }

    @Throws(Exception::class)
    @Suppress("UNCHECKED_CAST")
    fun <T> restoreOrThrow(file: File): T {
        return ObjectInputStream(FileInputStream(file)).readObject() as T
    }
}
