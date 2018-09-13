package by.vkatz.katzext.utils

import kotlin.properties.ReadOnlyProperty
import kotlin.reflect.KClass
import kotlin.reflect.KProperty

class ServiceLocatorKey(val name: String, val clazz: KClass<out Any>) {
    override fun equals(other: Any?): Boolean = other is ServiceLocatorKey && name == other.name && clazz == other.clazz
    override fun hashCode(): Int = 31 * name.hashCode() + clazz.hashCode()
}

class ServiceDelegate<T : Any>(name: String? = null,
                               clazz: KClass<T>,
                               private val services: HashMap<ServiceLocatorKey, Any>,
                               private val factory: () -> T,
                               private val sync: Any) : ReadOnlyProperty<Any?, T> {
    private val mapKey = ServiceLocatorKey(name ?: "", clazz)

    @Suppress("UNCHECKED_CAST")
    override fun getValue(thisRef: Any?, property: KProperty<*>): T = synchronized(sync) {
        (services[mapKey] as? T?) ?: factory().apply { services[mapKey] = this }
    }
}

open class BaseServiceLocator {
    protected val sync = Object()
    protected val services = HashMap<ServiceLocatorKey, Any>()

    /**
     * Provide a service instance
     *
     * Usage: `val x by service { X() }`
     */
    protected inline fun <reified T : Any> service(name: String? = null, noinline factory: () -> T): ReadOnlyProperty<Any?, T> =
            ServiceDelegate(name, T::class, services, factory, sync)

    /**
     * For testing only, put a mocked version of service
     */
    fun put(service: Any, name: String = ""): Unit = synchronized(sync) {
        services[ServiceLocatorKey(name, service::class)] = service
    }

    /**
     * Release a service, in case no data provided - all services going to ber released
     */
    fun release(service: KClass<out Any>? = null, name: String = ""): Unit = synchronized(sync) {
        if (service != null) services.remove(ServiceLocatorKey(name, service))
        else services.clear()
    }
}