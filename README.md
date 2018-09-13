# General

**My eng aint perfect, sorry for that**

# KATZEXT

## Utils

`AsyncHelper` - kotlin corutines helper

`ExtUtils` - various helper ext's

`LiveDataHelper` - live data specific helpers

`LogUtils` - useful log util

`NavigationUtils` - google jetpack navigation helpers

`ServiceLocator` - simple & safe imep for service locator pattern

`SharedPrefsDelegate` - work with shared prefs wan't that easy before this

## Widgets

**Extend** elements have a bunch of buffs

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

####RecyclerViewPager

ViewPager realization based on RecyclerView - much easier & better performance

#### RecyclerViewPagerIndicator

Simple indicators for RecyclerViewPager - just setup adapter and bind to RecyclerViewPager

#### ExtendConstraintLayout

Add possibility to child items to ignore parent bounds (now u can place item outside of parent view)

#### ExtSearchView

Add query as liveData object - so now u can just observe it and react, fix 'clear on leave' error

#### Adapters

* CircularRecyclerViewAdapter
* HeaderFooterRecyclerViewAdapter
* MultiTypeRecyclerViewAdapter
* SimpleExtendSpinnerAdapter
* SimpleExtendSpinnerArrayAdapter
* SimpleRecyclerViewAdapter

Pack of useful adapter - see samples for more details on how to use it

## How to thanks

wmz - Z424415381288 <br/>
wmr - R109844875467