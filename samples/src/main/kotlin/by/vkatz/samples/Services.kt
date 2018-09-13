package by.vkatz.samples

import by.vkatz.katzext.utils.BaseServiceLocator
import java.util.*

object Services : BaseServiceLocator() {

    val random by service { RandomRepository() }

    class RandomRepository {
        private val rand = Random(System.currentTimeMillis())

        fun getRandom() = rand.nextInt(10)
    }
}