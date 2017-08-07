package by.vkatz.samples

import android.annotation.SuppressLint
import android.app.ActivityOptions
import android.graphics.Color
import android.os.Bundle
import android.support.v7.widget.LinearLayoutManager
import android.support.v7.widget.RecyclerView
import android.util.TypedValue
import android.view.View
import android.view.ViewGroup
import android.widget.TextView
import by.vkatz.samples.activity.ActivityA
import by.vkatz.samples.activity.AppScreen
import by.vkatz.screen.fragments.FragmentScreen
import by.vkatz.utils.ActivityNavigator
import by.vkatz.widgets.SlideMenuLayout

/**
 * Created by vKatz on 08.03.2015.
 */
class MainScreen : AppScreen() {

    private var initiated = false

    init {
        holdView = true
    }

    override fun createView() = inflate(R.layout.screen_main)

    @SuppressLint("ValidFragment")
    private fun createViewScreen(viewId: Int): FragmentScreen {
        return object : AppScreen() {
            override fun createView(): View {
                return inflate(viewId)
            }
        }
    }

    @SuppressLint("ValidFragment")
    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        if (initiated) return
        initiated = true
        view.findViewById(R.id.activities).setOnClickListener {
            val bundle = ActivityOptions.makeCustomAnimation(activity, R.anim.idle, R.anim.idle).toBundle()
            ActivityNavigator.forActivity(activity).withData("a", "String from MainUI").go(ActivityA::class.java, bundle)
        }

        fun setupButton(button: Int, screen: Int) {
            view.findViewById(button).setOnClickListener { parent?.go(createViewScreen(screen)) }
        }

        setupButton(R.id.asset_font, R.layout.screen_font)
        setupButton(R.id.compound_images, R.layout.screen_compound_images)
        setupButton(R.id.slide_menu_1, R.layout.slide_menu_1)
        setupButton(R.id.slide_menu_2, R.layout.slide_menu_2)
        setupButton(R.id.slide_menu_3, R.layout.slide_menu_3)
        setupButton(R.id.slide_menu_4, R.layout.slide_menu_4)
        view.findViewById(R.id.nested_slide_menu_1).setOnClickListener {
            parent?.go(object : FragmentScreen() {
                override fun createView(): View {
                    val view = View.inflate(activity, R.layout.nested_slide_menu_1, null)
                    val btn = view.findViewById(R.id.btn)
                    val header = view.findViewById(R.id.header) as TextView
                    val headerTextSize = header.textSize
                    val list = view.findViewById(R.id.recycler) as RecyclerView
                    val menu = view.findViewById(R.id.menu) as SlideMenuLayout

                    list.layoutManager = LinearLayoutManager(activity, LinearLayoutManager.VERTICAL, false)
                    list.adapter = object : RecyclerView.Adapter<RecyclerView.ViewHolder>() {
                        override fun onCreateViewHolder(parent: ViewGroup?, viewType: Int): RecyclerView.ViewHolder {
                            val tw = TextView(parent!!.context)
                            tw.setTextColor(Color.WHITE)
                            tw.textSize = 50f
                            return object : RecyclerView.ViewHolder(tw) {}
                        }

                        override fun onBindViewHolder(holder: RecyclerView.ViewHolder?, position: Int) {
                            (holder!!.itemView as TextView).text = "Position " + position
                        }

                        override fun getItemCount(): Int {
                            return 100
                        }
                    }

                    menu.setOnSlideChangeListener(
                            object : SlideMenuLayout . OnSlideChangeListener {
                                override fun onScrollSizeChangeListener(view: SlideMenuLayout, value: Float) {
                                    btn.alpha = 1 - value;
                                    header.setTextSize(TypedValue.COMPLEX_UNIT_PX, headerTextSize * Math.max(1 - value, 0.3f));
                                    header.translationX = -100 * value;
                                }
                            })

                    return view
                }
            })
        }
        setupButton(R.id.nested_slide_menu_2, R.layout.nested_slide_menu_2)
        setupButton(R.id.nested_slide_menu_3, R.layout.nested_slide_menu_3)
    }
}
