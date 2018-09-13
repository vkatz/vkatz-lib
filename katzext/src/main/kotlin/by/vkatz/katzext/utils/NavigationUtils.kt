package by.vkatz.katzext.utils

import androidx.annotation.IdRes
import androidx.fragment.app.Fragment
import androidx.lifecycle.ViewModel
import androidx.lifecycle.ViewModelProvider
import androidx.lifecycle.ViewModelProviders
import androidx.navigation.NavOptions
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
    return try {
        //model might be already created - make attempt to get it
        //for models with data - this will throw to catch block due to no empty constructor
        ViewModelProviders.of(this, factory)[modelClass.java]
    } catch (e: Throwable) {
        //coming here means we have to init and create model with data
        //trying to read & validate model and fire it via model factory
        val model = ViewModelProviders.of(activity!!)[ModelProviderViewModel::class.java].takeModel()
        if (model != null && modelClass.java.isAssignableFrom(model::class.java)) {
            ViewModelProviders.of(this, ViewModelFactory(model))[modelClass.java]
        } else {
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

    fun navigate(@IdRes resId: Int, model: ViewModel? = null, navOptions: NavOptions? = null) {
        if (fragment.activity == null) throw RuntimeException("Navigation not allowed here (activity is null / fragment not attached)")
        if (model != null) ViewModelProviders.of(fragment.activity!!)[ModelProviderViewModel::class.java].setModel(model)
        rawController.navigate(resId, null, navOptions)
    }

    fun navigateUp() = rawController.navigateUp()
    fun popBackStack() = rawController.popBackStack()
}

/**
 * Global (activity-binded) view model. Used as shared view model to pass fragment models as data during navigation
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
 * Factory who provide model instance(taken into constructor) as created models
 */
open class ViewModelFactory<Model : ViewModel>(private val model: Model) : ViewModelProvider.NewInstanceFactory() {
    @Suppress("UNCHECKED_CAST")
    override fun <ModelClass : ViewModel> create(modelClass: Class<ModelClass>): ModelClass {
        return model as ModelClass
    }
}