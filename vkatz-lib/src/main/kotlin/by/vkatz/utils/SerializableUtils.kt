package by.vkatz.utils

import java.io.*

object SerializableUtils {
    fun commit(file: File, data: Any): Boolean {
        return try {
            val out = ObjectOutputStream(FileOutputStream(file))
            out.writeObject(data)
            out.flush()
            out.close()
            true
        } catch (e: Exception) {
            LogUtils.e("SerializableUtils", "Commit error:", e)
            false
        }
    }

    @Suppress("UNCHECKED_CAST")
    fun <T> restore(file: File, creator: () -> T): T {
        return try {
            ObjectInputStream(FileInputStream(file)).readObject() as T
        } catch (e: Exception) {
            LogUtils.e("SerializableUtils", "Restore error:", e)
            creator()
        }
    }

    @Throws(Exception::class)
    @Suppress("UNCHECKED_CAST")
    fun <T> restoreOrThrow(file: File): T = ObjectInputStream(FileInputStream(file)).readObject() as T
}
