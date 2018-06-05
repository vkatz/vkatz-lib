package by.vkatz.samples

import android.content.Context
import android.content.SharedPreferences
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import by.vkatz.katzext.utils.SharedPrefsIntDelegate
import by.vkatz.katzext.utils.XmlParser
import by.vkatz.katzext.utils.asyncUI
import by.vkatz.katzext.utils.inflate
import by.vkatz.katzilla.FragmentScreen
import by.vkatz.katzilla.helpers.KatzillaFragment
import kotlinx.android.synthetic.main.utils.*
import kotlinx.coroutines.experimental.delay

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
        xml.text = "ParsedXml:\n$foods".replace("),",")\n")

    }

    data class FoodItem(var id: Int, var name: String, var price: String)
}
