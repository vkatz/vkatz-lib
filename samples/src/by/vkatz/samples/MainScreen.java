package by.vkatz.samples;

import android.view.View;
import android.view.animation.AnimationSet;
import android.widget.Toast;
import by.vkatz.screens.Screen;
import by.vkatz.utils.AnimationBuilder;
import by.vkatz.utils.ContextUtils;

/**
 * Created by vKatz on 08.03.2015.
 */
public class MainScreen extends Screen {
    @Override
    public View getView() {
        return ContextUtils.getView(getContext(), R.layout.screen_main);
    }

    @Override
    public void onShow(View view) {
        super.onShow(view);
        Settings settings = getParent().getData("settings", Settings.class);
        if (settings.isFirstLaunch()) {
            Toast.makeText(getContext(), "Hi, it is first launch", Toast.LENGTH_SHORT).show();
            settings.setFirstLaunch(false);
            settings.commit(getContext());
        }
        view.findViewById(R.id.asset_font).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                getParent().go(new Screen() {
                    @Override
                    public View getView() {
                        return ContextUtils.getView(getContext(), R.layout.screen_font);
                    }
                });
            }
        });
        view.findViewById(R.id.lazy_image).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                getParent().go(new LazyImageScreen());
            }
        });
        view.findViewById(R.id.color_filter).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                getParent().go(new Screen() {
                    @Override
                    public View getView() {
                        return ContextUtils.getView(getContext(), R.layout.color_filter_screen);
                    }
                });
            }
        });
        view.findViewById(R.id.loading).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                getParent().go(new LoadingScreen());
            }
        });
        view.findViewById(R.id.round_rect).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                getParent().go(new Screen() {
                    @Override
                    public View getView() {
                        return ContextUtils.getView(getContext(), R.layout.round_rect_screen);
                    }
                });
            }
        });
        view.findViewById(R.id.slide_menu_1).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                getParent().go(new Screen() {
                    @Override
                    public View getView() {
                        return ContextUtils.getView(getContext(), R.layout.slide_menu_1);
                    }
                });
            }
        });
        view.findViewById(R.id.slide_menu_2).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                getParent().go(new Screen() {
                    @Override
                    public View getView() {
                        return ContextUtils.getView(getContext(), R.layout.slide_menu_2);
                    }
                });
            }
        });
        view.findViewById(R.id.slide_menu_3).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                getParent().go(new Screen() {
                    @Override
                    public View getView() {
                        return ContextUtils.getView(getContext(), R.layout.slide_menu_3);
                    }
                });
            }
        });
        view.findViewById(R.id.alternative_go_animation).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                AnimationSet animation = new AnimationSet(true);
                animation.addAnimation(AnimationBuilder.rotate(0, 360, 500, false));
                animation.addAnimation(AnimationBuilder.alpha(0, 1, 500));
                getParent().setAlternativeGoAnimations(animation, AnimationBuilder.alpha(1, 0, 500));
                getParent().go(new MainScreen());
                getParent().clearHistory(); // to not duplicate this screen
            }
        });
    }

    @Override
    public boolean onBackPressed() {
        Toast.makeText(getContext(), "Buyyyy", Toast.LENGTH_SHORT).show();
        return super.onBackPressed();
    }
}
