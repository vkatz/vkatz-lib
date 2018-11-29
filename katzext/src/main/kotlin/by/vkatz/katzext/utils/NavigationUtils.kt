package by.vkatz.katzext.utils

import androidx.annotation.IdRes
import androidx.fragment.app.Fragment
import androidx.lifecycle.*
import androidx.navigation.NavOptions
import androidx.navigation.Navigator
import androidx.navigation.fragment.NavHostFragment
import kotlin.reflect.KClass

/**
 * Provide viewModel by lazy iml & [getViewModel]
 */
fun <Model : ViewModel> Fragment.lazyViewModel(modelClass: KClass<Model>, factory: ViewModelProvider.Factory? = null) = lazy { getViewModel(modelClass, factory) }

/**
 * Provide wrapped navController see [FragmentNavigationController]
 */
val Fragment.navController
    get() = FragmentNavigationController(this)

/**
 * Provide view model for a fragment (factory is optional)
 */
fun <Model : ViewModel> Fragment.getViewModel(modelClass: KClass<Model>, factory: ViewModelProvider.Factory? = null): Model {
    // 1st we try to get model from global scope as coming in model, for nav forward it might be passed via
    // nav controller, for nav back it will be always empty
    val model = ViewModelProviders.of(activity!!)[ModelProviderViewModel::class.java].takeModel()
    return if (model != null && modelClass.java.isAssignableFrom(model::class.java)) {
        ViewModelProviders.of(this, ViewModelFactory(model))[modelClass.java]
    } else {
        //2d we try to get model via usual mechanism, will get model in case there is no params constructor
        //otherwise we will get into error block - witch means we had to pass specific model with params
        try {
            ViewModelProviders.of(this, factory)[modelClass.java]
        } catch (e: Throwable) {
            throw Exception("${modelClass.java.canonicalName} should be provided via \"navController.navigate(resId, activity, model)\"")
        }
    }
}

/**
 * NavigationController wrapper who allow to provide ViewModel as navigation data
 */
class FragmentNavigationController(private val fragment: Fragment) {
    private val rawController
        get () = NavHostFragment.findNavController(fragment)

    fun navigate(@IdRes resId: Int, model: ViewModel? = null, navOptions: NavOptions? = null, navExtras: Navigator.Extras? = null) {
        if (fragment.activity == null) throw RuntimeException("Navigation not allowed here (activity is null / fragment not attached)")
        if (model != null) ViewModelProviders.of(fragment.activity!!)[ModelProviderViewModel::class.java].setModel(model)
        rawController.navigate(resId, null, navOptions, navExtras)
    }

    fun navigateUp() = rawController.navigateUp()
    fun popBackStack() = rawController.popBackStack()
}

/**
 * Global (activity-binded) view model. Used as shared view model to pass fragment models as data during navigation
 *
 * DO NOT USE MANUALLY
 */
class ModelProviderViewModel : ViewModel() {
    private var internalModel: ViewModel? = null

    fun takeModel(): ViewModel? {
        val result = internalModel
        internalModel = null
        return result
    }

    fun setModel(data: ViewModel?) {
        this.internalModel = data
    }
}

/**
 * View model that provide lifecycle state of model (resumed on creation, destroyed [onCleared])
 */
open class LifecycleViewModel : ViewModel(), LifecycleOwner {
    @Suppress("LeakingThis")
    private val lifecycle = LifecycleRegistry(this)

    override fun getLifecycle(): Lifecycle = lifecycle

    init {
        lifecycle.markState(Lifecycle.State.RESUMED)
    }

    override fun onCleared() {
        super.onCleared()
        lifecycle.markState(Lifecycle.State.DESTROYED)
    }
}

/**
 * Factory who provide model instance(taken into constructor) as created models
 */
open class ViewModelFactory<Model : ViewModel>(private val model: Model) : ViewModelProvider.NewInstanceFactory() {
    @Suppress("UNCHECKED_CAST")
    override fun <ModelClass : ViewModel> create(modelClass: Class<ModelClass>): ModelClass {
        return model as ModelClass
    }
}