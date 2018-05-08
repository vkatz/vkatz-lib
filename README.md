# General

**My eng aint perfect, sorry for that**

This lib contain 2 general modules:

`katzilla` - navigation helper to make activity/framgent navigation, compatible with mvp/mvvm and almost any pattern<br>
`katzext`  - extentionsand and usable widgets

# KATZILLA

`FragmentBackStack` & `FragmentScreen` - provide basi impl of navigation<br>
`KatzillaActivity` & `KatzillaFragment` - useful implementation with predefined anmations and all necessary callbacks

There is only 2 rules to use:<br>
1) In case u.r using FragmentBackStack - forward activity `onDestroy`
2) ScreenModel should not contains any context references (or at least clear this refs for the time fragment aint on screen)

I am use this method by many diffrent reasons, main is - backstack and navigation control,
i can simple call `parent.go(SomeScreen::class)` to launch screen, `parent.back()` to back, i
can control backstack as i want, this is much more simplier than
default actitvity navigation + allow me to make any pattern i want (clean/mvp/mvvm).

# KATZEXT

## Utils

`AsyncHelper` - helper for asyn operations using corutines
    
     asyncUI {
           val t1 = async { 1 }.await()   //Int? - ? due to task might be canceled
           val t2 = async { 1 }.await()!! //Int
           val t3 = AsyncHelper(null, newSingleThreadContext("WorkThread"), { 1 }).start().await()
     }
 
`Delegates` - `SharedPreferences` and `Bundle` delegates

`ExtUtils` - various useful functions

## Widgets

**Extend** elements have a bunch of buffs

List of items:
* ExtendCheckBox
* ExtendEditText.kt
* ExtendFrameLayout
* ExtendImageView
* ExtendLinearLayout
* ExtendRadioButton
* ExtendRelativeLayout
* ExtendSpinner.kt
* ExtendTextView

New widgets:
* FlowLayout
* SlideMenuLayout
* ViewPagerIndicator

# Core buffs:
1. `app:compoundDrawableWidth` and `app:compoundDrawableHeight` -> specify compound drawable size(one or both)

![](https://raw.githubusercontent.com/vkatz/vkatz-lib/master/.doc/compound_drawable.png)

2. `app:extendBackground1` and `app:extendBackground2` -> combine 2 bg into one as layers (useful when u have color + selector)     
3. `app:extendEnabled` and `app:extendActivated` -> set relative view state from xml

# Special buffs
#### ExtendEditText

`app:extendInputMask/setMask(String)` allow to provide input mask ("000 000 0000") - where '0' will be input digit, all other chars will stay

`addAfterTextChangedListener` `setAfterTextChangedListener` functions and `onSelectionChangedListener` listener

#### ExtendImageView

`app:touchZoom` allow to enable pinchZoom(default false), `app:maxZoom` and `app:minZoom` to config it
      
# New widgets
#### FlowLayout

Allow to place items one by one, in case there is no space in line - item placed at next line. Allow to set horizontal gaps and vertical per line align

![](https://github.com/vkatz/vkatz-lib/blob/master/.doc/flow_layout.png?raw=true)

#### ExtendSpinner

Completely new spinner that allow to handle - "no selection" and "custom selected" items
ps: in case u are using TextView+[height=wrap-content] as list istem - add also line count !! (otherwise u will face ListView measure bug, someday i will fix it)

![](https://raw.githubusercontent.com/vkatz/vkatz-lib/master/.doc/ext_spinner.png)

#### SlideMenuLayout  

Allow to create expandable menu from any direction, extends ExtendRelativeLayout

`onExpandStateChangeListener` - expand/collapse listener

`onSlideChangeListener` -  slide change listener (useful to create animations/transitions)

`customScrollBehavior` - custom scroll logic (used with appropriate flag)

`collapse` `expand` `toggle` - useful fuctions

How to use:

1. Add layout and specify menu direction using `app:slideFrom`
2. Set menu size `app:menuHidingSize` (how many will be hided {actual menu size}) or `app:menuVisibleSize` (how many will not be hided{group width - menu size}), default: full size
3. Optional: config other params `app:scrollBehavior` `app:menuExpanded` `app:menuEnabled` `app:startScrollDistance` `app:scrollerDuration`
4. Config child views: `app:applyScroll` - indicate that view will be moved on menu opening/closing (default true), `interceptTouches` - should menu handle touch events over this view (default true)      
       
Examples:

![](https://raw.githubusercontent.com/vkatz/vkatz-lib/master/.doc/slide_menu_1.gif)

![](https://raw.githubusercontent.com/vkatz/vkatz-lib/master/.doc/slide_menu_2.gif)

![](https://raw.githubusercontent.com/vkatz/vkatz-lib/master/.doc/slide_menu_3.gif)

![](https://raw.githubusercontent.com/vkatz/vkatz-lib/master/.doc/slide_menu_4.gif)

#### ViewPagerIndicator

Simple inicators for ViewPager - just setup adapter and bind to ViewPager

#### Adapters

* HeaderFooterRecyclerViewAdapter
* MultiTypeRecyclerViewAdapter
* SimpleExtendSpinnerAdapter
* SimpleExtendSpinnerArrayAdapter
* SimpleRecyclerViewAdapter
* SimpleViewPagerAdapter

Pack of useful adapters:

```
    private fun setAdapter(index: Int) {
         val adapter = when (index) {
             // usual recycler without direct creation of VH
             0 -> SimpleRecyclerViewAdapter(listOf(1, 2, 3),
                                            { this.toLong() },
                                            R.layout.spinner_item,
                                            { itemView.asTextView().text = it.toString() })
             // usual recycler with VH
             1 -> SimpleRecyclerViewAdapter(listOf(3, 4, 5),
                                            { this.toLong() },
                                            { parent ->
                                                SimpleViewHolder(R.layout.spinner_item, parent, { itemView.asTextView().text = it.toString() })
                                            })
             // adapter for multiple types - just register handlers and it's done
             2 -> MultiTypeRecyclerViewAdapter(listOf("a", 1, "c", 2), { hashCode().toLong() },
                                               ViewTypeHandler<Any>({ it is String }, ::SpinnerItemViewHolder),
                                               ViewTypeHandler(
                                                       { it is Int },
                                                       {
                                                           SimpleViewHolder<Any>(R.layout.spinner_item, it,
                                                                                 {
                                                                                     itemView.asTextView().text = it.toString()
                                                                                     itemView.setBackgroundColor(Color.BLUE)
                                                                                 })
                                                       })
                                              )
             // multi type adapter where u can add 1 header and 1 footer (u can hide it via visibility properties) (if u need more - use default MultiTypeRecyclerViewAdapter)
             3 -> HeaderFooterRecyclerViewAdapter(Array(50, { i -> i }).toList(), null,
                                                  R.layout.spinner_item,
                                                  { itemView.asTextView().text = "Header" },
                                                  R.layout.spinner_item,
                                                  { itemView.asTextView().text = "Footer" },
                                                  ViewTypeHandler({ true },
                                                                  R.layout.spinner_item,
                                                                  { itemView.asTextView().text = it.toString() })
                                                 ).apply { headerVisible = true; footerVisible = true }
             // pagination - just use PaginationList as data source and call list.loadPAge on footer show (or impl your own logic)
             4 -> {
                 val list = PaginationList<String>(5, { from, count, callback ->
                     asyncUI(this) {
                         delay(5000)
                         val cnt = minOf(count, 100 - from)
                         callback(Array(cnt, { i -> "Item #${i + from}" }).toList())
                     }
                 })
                 val adapter = HeaderFooterRecyclerViewAdapter(list, null, null,
                                                               { SimpleViewHolder(ProgressBar(it.context), { list.loadPage() }) },
                                                               ViewTypeHandler({ true },
                                                                               R.layout.spinner_item,
                                                                               { itemView.asTextView().text = it })
                                                              )
                 list.setOnPageLoadedListener {
                     if (adapter.data == list) {
                         if (!list.hasMorePages) {
                             adapter.footerVisible = false
                         }
                         adapter.notifyDataSetChanged()
                     } else {
                         list.setOnPageLoadedListener(null)
                     }
                 }
                 adapter
             }
             else -> null
         }
         recycler.adapter = adapter
     }
```

## How to thanks

wmz - Z424415381288 <br/>
wmr - R109844875467