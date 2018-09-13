package by.vkatz.samples

import android.annotation.SuppressLint
import android.content.Context
import android.content.SharedPreferences
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import by.vkatz.katzext.utils.*
import by.vkatz.katzilla.FragmentScreen
import by.vkatz.katzilla.helpers.KatzillaFragment
import kotlinx.android.synthetic.main.utils.*
import kotlinx.coroutines.experimental.delay
import java.text.SimpleDateFormat
import java.util.*

/**
 * Created by V on 24.04.2018.
 */

class UtilsScreen : KatzillaFragment<FragmentScreen.SimpleModel>() {

    private lateinit var prefs: SharedPreferences
    private var delegateCounter by SharedPrefsIntDelegate({ prefs }, 0)


    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        prefs = context!!.getSharedPreferences("sharedPrefs", Context.MODE_PRIVATE)
    }

    override fun onCreateView(inflater: LayoutInflater, container: ViewGroup?, model: FragmentScreen.SimpleModel, savedInstanceState: Bundle?): View? =
            inflater.inflate(R.layout.utils)

    @SuppressLint("SetTextI18n")
    override fun onViewCreated(view: View, model: FragmentScreen.SimpleModel, savedInstanceState: Bundle?) {
        super.onViewCreated(view, model, savedInstanceState)

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
        delegates.text = "Delegate read count=$delegateCounter"
        delegateCounter += 1
        //xml
        val foods = ArrayList<FoodItem>()
        XmlParser.parse(context!!.resources.openRawResource(R.raw.test)) {
            item("menu") {
                item("food") { params ->
                    val item = FoodItem(params["id"]?.toInt() ?: 0, "", "")
                    itemValue("name", { item.name = it ?: "" })
                    itemValue("price", { item.price = it ?: "" })
                    foods.add(item)
                }
            }
        }
        xml.text = "ParsedXml:\n$foods".replace("),", ")\n")

        //livedatas

        val timeFormatter = SimpleDateFormat("HH:mm.ss", Locale.getDefault())
        fun now() = timeFormatter.format(Date(System.currentTimeMillis()))
        val t1 = LoadableLiveData("asd")
        t1.observe {
            task1state.text = "${if (it.isLoading) "loading" else "ready"} at [${now()}] ->${it.data}"
        }
        task1.setOnClickListener {
            t1.load(true) {
                delay(5000)
                "Task1 finished at ${now()}"
            }
        }

        val t2 = LoadableLiveData("asd")
        t2.observe {
            task2state.text = "${if (it.isLoading) "loading" else "ready"} at[${now()}]->${it.data}"
        }
        task2.setOnClickListener {
            t2.load(false) {
                delay(5000)
                "Task2 finished at ${now()}"
            }
        }

    }

    data class FoodItem(var id: Int, var name: String, var price: String)
}