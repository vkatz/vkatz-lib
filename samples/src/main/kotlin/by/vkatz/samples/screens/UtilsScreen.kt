package by.vkatz.samples.screens

import android.annotation.SuppressLint
import android.content.Context
import android.content.SharedPreferences
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.fragment.app.Fragment
import androidx.lifecycle.ViewModel
import androidx.lifecycle.ViewModelProvider
import by.vkatz.katzext.utils.*
import by.vkatz.samples.R
import kotlinx.android.synthetic.main.utils.*
import kotlinx.coroutines.experimental.delay
import java.text.SimpleDateFormat
import java.util.*

class UtilsViewModel(private val prefs: SharedPreferences) : ViewModel() {
    var delegateCounter by SharedPrefsDelegate({ prefs }, "key", 0)
}

class UtilsScreen : Fragment() {
    private val model by lazyViewModel(UtilsViewModel::class, object : ViewModelProvider.Factory {
        @Suppress("UNCHECKED_CAST")
        override fun <T : ViewModel?> create(modelClass: Class<T>): T =
                UtilsViewModel(context!!.getSharedPreferences("sharedPrefs", Context.MODE_PRIVATE)) as T
    })

    override fun onCreateView(inflater: LayoutInflater, container: ViewGroup?, savedInstanceState: Bundle?): View? = inflater.inflate(R.layout.utils)

    @SuppressLint("SetTextI18n")
    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        //AsyncHelper
        asyncUI(this) {
            var cnt = 0
            while (true) {
                asyncResult.text = "Call #$cnt~s"
                cnt++
                delay(1000)
            }
        }
        asyncUI(this) {
            val t1 = asyncUI {
                asyncT1.text = "Task1 running (5s)"
                delay(5000)
                asyncT1.text = "Task1 finished"
            }
            val t2 = asyncUI {
                asyncT2.text = "Task2 running (10s)"
                delay(10000)
                asyncT2.text = "Task2 finished"
            }
            asyncT3.text = "Task1 or Task2 running"
            t1.await()
            t2.await()
            asyncT3.text = "Task1 and Task2 finished"
        }
        //Delegates
        delegates.text = "Delegate read count=${model.delegateCounter}"
        model.delegateCounter += 1

        //livedatas

        val timeFormatter = SimpleDateFormat("HH:mm.ss", Locale.getDefault())
        fun now() = timeFormatter.format(Date(System.currentTimeMillis()))
        val t1 = LoadableLiveData("asd")
        t1.observe(this) {
            task1state.text = "${if (it.isLoading) "loading" else "ready"} at [${now()}] ->${it.data}"
        }
        task1.setOnClickListener {
            t1.load(true) {
                delay(5000)
                "Task1 finished at ${now()}"
            }
        }

        val t2 = LoadableLiveData("asd")
        t2.observe(this) {
            task2state.text = "${if (it.isLoading) "loading" else "ready"} at[${now()}]->${it.data}"
        }
        task2.setOnClickListener {
            t2.load(false) {
                delay(5000)
                "Task2 finished at ${now()}"
            }
        }

    }
}