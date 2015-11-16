package by.vkatz.samples;

import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;
import android.widget.Toast;

/**
 * Created by vKatz on 08.03.2015.
 */
public class MainScreen extends BaseScreen {
    private boolean initiated = false;
    private View view;

    @Override
    public View createView() {
        if (view != null && view.getParent() != null) ((ViewGroup) view.getParent()).removeView(view);
        return view != null ? view : (view = View.inflate(getContext(), R.layout.screen_main, null));
    }

    @Override
    public void initView(View view) {
        super.initView(view);
        if (initiated) return;
        else initiated = true;
        Settings settings = getParent().getData("settings", Settings.class);
        if (settings.isFirstLaunch()) {
            Toast.makeText(getContext(), "Hi, it is first launch", Toast.LENGTH_SHORT).show();
            settings.setFirstLaunch(false);
            settings.commit(getContext());
        }
        view.findViewById(R.id.asset_font).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                getParent().go(new BaseScreen() {
                    @Override
                    public View createView() {
                        return View.inflate(getContext(), R.layout.screen_font, null);
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
                getParent().go(new BaseScreen() {
                    @Override
                    public View createView() {
                        return View.inflate(getContext(), R.layout.color_filter_screen, null);
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
                getParent().go(new BaseScreen() {
                    @Override
                    public View createView() {
                        return View.inflate(getContext(), R.layout.round_rect_screen, null);
                    }
                });
            }
        });
        view.findViewById(R.id.slide_menu_1).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                getParent().go(new BaseScreen() {
                    @Override
                    public View createView() {
                        return View.inflate(getContext(), R.layout.slide_menu_1, null);
                    }
                });
            }
        });
        view.findViewById(R.id.slide_menu_2).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                getParent().go(new BaseScreen() {
                    @Override
                    public View createView() {
                        return View.inflate(getContext(), R.layout.slide_menu_2, null);
                    }
                });
            }
        });
        view.findViewById(R.id.slide_menu_3).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                getParent().go(new BaseScreen() {
                    @Override
                    public View createView() {
                        return View.inflate(getContext(), R.layout.slide_menu_3, null);
                    }
                });
            }
        });
        view.findViewById(R.id.compound_drawables).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                getParent().go(new BaseScreen() {
                    @Override
                    public View createView() {
                        return View.inflate(getContext(), R.layout.screen_compund_images, null);
                    }
                });
            }
        });
        view.findViewById(R.id.data_passing_between_screens).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                setTargetFragment(MainScreen.this, 0);
                getParent().go(CallBackScreen.newInstance("Click me", new Runnable() {
                    @Override
                    public void run() {
                        callMethod();
                    }
                }));
            }
        });
    }

    public void callMethod() {
        ((TextView) view.findViewById(R.id.data_passing_between_screens)).setText("now it is another text here");
    }

    @Override
    public boolean onBackPressed() {
        Toast.makeText(getContext(), "Back is pressed", Toast.LENGTH_SHORT).show();
        return super.onBackPressed();
    }
}
