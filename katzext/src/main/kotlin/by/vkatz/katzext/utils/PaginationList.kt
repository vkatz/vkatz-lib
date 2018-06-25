package by.vkatz.katzext.utils

open class PaginationList<T>(private val pageSize: Int, private val loader: (from: Int, count: Int, callback: ValueCallback<List<T>>) -> Unit) : ArrayList<T>() {
    var isLoading = false
        private set
    var hasMorePages = true
        private set

    private var onPageLoaded: Callback? = null

    init {
        loadPage()
    }

    fun loadPage(pageLoaded: Callback? = null) {
        if (isLoading || !hasMorePages) {
            return
        }
        isLoading = true
        loader(size, pageSize) { data ->
            addAll(data)
            hasMorePages = data.size == pageSize
            isLoading = false
            pageLoaded?.invoke()
            onPageLoaded?.invoke()
        }
    }

    fun setOnPageLoadedListener(listener: Callback?) {
        onPageLoaded = listener
    }
}