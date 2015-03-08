# vkatz-lib
##

Sorry for my english, it is not my native.

If u like it - you may say thanks via WebMoney: 
* R109844875467
* Z424415381288
* B258196896040


### About me and lib
My name is Viachaslau, work as android developer sinse 2010 year(start from android 1.6)

For a loong time i hate to declare each screen as activity, also i dont loke  the way to config activity transfer animation and screens history managment. Many usefult and hard features(like lazy image, side menu, etc) is hard(for me not hard, but i realy dont like it) to implement. This all force me to write some simple library to personal usage.
Lib contain a bunch of usefull widgets and utils and special ScreenLayout wich allow you to make screens without declaring them in manifest and simple navigation between them.

### How to install

* Download and copy [vkatz-lib](https://github.com/vkatz/vkatz-lib/tree/master/vkatz-lib)
* Add this project as module 
* Add dependence to you core module. Should looks like this: [in intellij idea](https://drive.google.com/file/d/0B6z8oML8UiXtcHJ6ZnhHc3d3NXc/view?usp=sharing)

In case you plane yo use ScreenLayout:
* Make your activity extends of VkatzActivity
```java
public class MainUI extends VkatzActivity 
```
* Implement init method like below
1) Using xml
 declare layout xml with content ans ScreenLayout element
```xml
<?xml version="1.0" encoding="utf-8"?>
<LinearLayout xmlns:android="http://schemas.android.com/apk/res/android"
              android:orientation="vertical"
              android:layout_width="fill_parent"
              android:layout_height="fill_parent">
    <!--some content-->
    <by.vkatz.screens.ScreensLayout android:layout_width="fill_parent"
                                    android:layout_height="fill_parent"
                                    android:id="@+id/screen_layout"/>
    <!--some content-->
</LinearLayout>
```
use it as init
```java
 @Override
    protected void init() {
        init(R.layout.main, R.id.screen_layout);
        getScreensLayout().go(new SplashScreen(), false); //go to first screen
    }
```
2) or use another method
```java
@Override
    protected void init() {
        ViewGroup group;
        //create, init main content, might be ScreenLayout itself
        ScreensLayout screensLayout;
        // find ot init screenLayout
        //some initializations
        init(group, screensLayout);
        getScreensLayout().go(new SplashScreen(), false); //go to first screen
    }
```
##Screens
Create any class and extends it from Screen.Small hint about methods u may override
* public void onShow(View view)  - called when view start showing
* public void onHide(View view) - called when view start hiding
* public void release() - called when this item go out from history and screen 

Example of simple SplashScreen
```java
public class SplashScreen extends Screen {
    @Override
    public View getView() { //create view
        TextView textView = new TextView(getContext());
        textView.setBackgroundColor(Color.WHITE);
        textView.setTextColor(Color.BLACK);
        return textView;
    }

    @Override
    public void onShow(View view) { //init content
        super.onShow(view);
        ((TextView) view).setText("hi");
        getParent().postDelayed(new Runnable() { //delayed action
            @Override
            public void run() {
                getParent().go(new MainScreen()); //go to next screen
            }
        }, 3000);
    }
}
```

care: inside onHide method getContext  and getParent both null

##Navigation control
Inside scren you may get a ScreenLayout with getParent() call
ScreenLayout contain bunch of navigation methods, main is:
* go(Screen screen) - simple go to next screen, store currnet in history
* go(Screen screen, boolean storeInHistory) - we may say to not store next scren in history,
after we go out of those screen - it will be destroyed and released (usefult to splash screens)
* go(Screen screen, boolean storeInHistory, String name) - additiona,y we may specify screen name( easy to back to this screen, like homescreen)
* back() - back to previos screen
* backTo(String name) - back to screen with specifyed name(or first if not found)
 
 On back press will be called back(), but u can override it in screens.
 
You also able to clear history
* clearHistory() - clear whole history (active screen is not in history)
* clearHistoryUntil(String name) - clear all until specifyed name

You may pass data.
*General data might be passed throng screenLayout like this
```java
    // init/load settings in activity, before app actualy displayed
    @Override
    protected void init() {
        init(R.layout.main, R.id.screen_layout);
        getScreensLayout().setData("settings", new Settings()); //set data with key
        getScreensLayout().go(new SplashScreen(), false); 
    }
```
get get it in Screen
```java
    Settings settings = (Settings) getParent().getData("settings");
```
or
```java
    Settings settings = getParent().getData("settings", Settings.class);
```

Or just pass throng screens constructors.
    
You able to controle animations wich used to switch screen
```java 
@Override
    protected void init() {
        init(R.layout.main, R.id.screen_layout);
        getScreensLayout().setData("settings", new Settings());
        getScreensLayout().setGoAnimations(/*animations*/) //config global animations
        getScreensLayout().go(new SplashScreen(), false);
    }
```
You may change global animations anytime. Also you may specify some alternative animetion for next screen switching (afet appear it will be erased, so next animations will use global settings)
for example i want splashScreen appear throng alpha anim
```java
        getScreensLayout().setAlternativeGoAnimations(AnimationBuilder.alpha(0, 1, 250), null);
        getScreensLayout().go(new SplashScreen(), false);
```
or cleare alternative animetion(will be used global)  
```java
        getScreensLayout().clearAlternativeGoAnimation();
```

tip:
u cant call screen swith while animation is going
in case u need it(u go to screen but hable that u need to open another screen) use this 
```java
    getParent().executeWhenPossible(new Runnable() {
            @Override
            public void run() {
                getParent().back(); // some actions after animation completed
            }
        });
```
this code will be called after switch screen animation completed

##Library widgets and utils
###Utils
AnimationBuilder - allow to build most useful animation in one line

ContextUtils - contain static method to parse layout xml in view (possible not actual, u may use View.inflate insted)

FontsManager - simple memory optimized font manager(font load from assets)

SelfKillerExecutor - u may execute some tasks in another thread, after finish all tasks - executer will be killed. On new tasks appears - it will create new thread. Usefult to download opperations(till we need - we download, on end - kill thread, on new downloads - create thread again and work)

SerializableUtils - utils to make serialization easyer. 
Hint: serialVersionUID might be created veeery simple and unik with
```java
    public class Settings implements Serializable {
        private static final long serialVersionUID = SerializableUtils.generateSerialVersionUID(Settings.class);
    }
```
#Widgets
The nost intresting part.

AssetFont<element> - allow you to set font inside xml!! (ps not shown in editor, do not forget to put ttf file in assets). U may use string refference to font or write it in xml. (full font name and path from asset folder: "fonts/some_font.ttf")
```xml
 <by.vkatz.widgets.AssetFontTextView
                android:layout_width="wrap_content"
                android:layout_height="wrap_content"
                android:id="@+id/help_header_1
                font="@string/font_default"/>
```
U may also specify color filter for text ( colors multyple, heightlite on click for example)

ColorFilteredImageView - same color filer as for text, but for image. You may simple create image button with onClick filters(params name is "colorFiletr" and "bgColorFilter", might be color state list)

LazyImage - obvios

LoadingView - android 5.0 preview like loading view. Contain clolor,thiknes, direction and rotation params. Also might be swithed to determinate mode (progress 0 - indeterminate, >0 - determinate)

RoundRectLayout - clip corners of childe content. String reccoment not to use without necessary cause it use some memory.

SlideMenuLayout - simpliest solution to  side menu, slide mnus etc. Just look examples.


