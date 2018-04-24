# General

todo update

**My eng aint perfect, sorry for that**

## Navigation

`BackStack` and `Screen` provide a custom backstack with full control over it as abstract clases

`FragmentBackStack` and `FragmentScreen` implementations using fragments

How to use:

U may put a backstack into activity or app class depend on your intentions and use it as simple mvp patter with 1 class. Allow to control history, navigation, holding view in memory, history state, animations etc... For more details take a look at sample code

I am use this method by many diffrent reasons, main is - backstack and navigation control, i can simple call `parent.go(SomeScreen.with(some params))` to launch screen, `parent.back()` to back, and `parent.backTo(screen name)` to back for specific screen, this is much more simplier that default actitvity navigation + allow me to make kid of mvp with data anc callbacks between screen + let me use all power of fragments.

## Utils

`ActivityNavigator` - simple activity navigation

    ActivityNavigator.forActivity(activity).withData("field", "String from MainUI").go(ActivityA::class.java)
    ---
    ActivityNavigator.getData(this).getString("field")
    ActivityNavigator.forActivity(this@ActivityB).withData("extra", "String from activity B").backWithResult(1)

`AsyncHelper` - helper for asyn operations using corutines
    
    asyncUI {
          val t1 = async { 1 }.await()                                //Int?
          val t2 = async { 1 }.await()!!                              //Int
          val t3 = safeAsync { 1 }.onError { 2 }.await()              //Int
          val t4 = safeAsync<Int?> { 1 }.onError { null }.await()     //Int?
          val t5 = AsyncHelper(newSingleThreadContext("WorkThread")) { 1 }.onError { 2 }.await()
          val t6 = AsyncHelper<Int?>(newFixedThreadPoolContext(5, "WorkThread")) { 1 }.onError { 2 }.await()
    }
 
`Delegates` - `SharedPreferences` and `Bundle` delegates

`ExtUtils` - various useful functions

## Widgets

**Extend** elements have a bunch of buffs

List of items:
* ExtendCheckBox
* ExtendEditText
* ExtendRadioButton
* ExtendTextView
* ExtendImageView
* ExtendFrameLayout
* ExtendLinearLayout
* ExtendRelativeLayout

New widgets:
* FlowLayout
* SlideMenuLayout

# Core buffs:
1. `app:compoundDrawableWidth` and `app:compoundDrawableHeight` -> specify compound drawable size(one or both)

![](https://raw.githubusercontent.com/vkatz/vkatz-lib/master/.doc/compound_drawable.png)

2. `app:extendBackground1` and `app:extendBackground2` -> combine 2 bg into one as layers (useful when u have color + selector)     
3. `app:extendEnabled` and `app:extendActivated` -> set relative view state from xml
4. "text" widgets get `app:extendFont` param to support ttf fonts from asset

![](https://raw.githubusercontent.com/vkatz/vkatz-lib/master/.doc/asset_font.png)

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

## How to thanks

wmz - Z424415381288 <br/>
wmr - R109844875467

[![Android Arsenal]( https://img.shields.io/badge/Android%20Arsenal-vkatz--lib-green.svg?style=flat )]( https://android-arsenal.com/details/1/6368 )
