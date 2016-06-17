package by.vkatz.samples;

import android.app.ActivityOptions;
import android.os.Bundle;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Toast;

import by.vkatz.samples.activity.ActivityA;
import by.vkatz.utils.ActivityNavigator;

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
        if (isBackTransaction() && hasResult())  //back + data
            Toast.makeText(getContext(), (String) getResult().getData("RESULT"), Toast.LENGTH_SHORT).show();
        if (initiated) return;
        else initiated = true;
        Settings settings = getParent().getData("settings", Settings.class);
        if (settings.isFirstLaunch()) {
            Toast.makeText(getContext(), "Hi, it is first launch", Toast.LENGTH_SHORT).show();
            settings.setFirstLaunch(false);
            settings.commit(getContext());
        }
        view.findViewById(R.id.activities).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                //ActivityNavigator.forActivity(getActivity()).go(ActivityA.class);

                //ActivityNavigator.forActivity(getActivity()).withData("a", "b").withFillData(new Functions.Func1<Void, Bundle>() {
                //    @Override
                //    public Void execute(Bundle bundle) {
                //        //todo fill data
                //        return null;
                //    }
                //}).go(ActivityA.class);

                //ActivityNavigator.forActivity(getActivity()).withData("a", "b").backWithResult(Activity.RESULT_OK);

                //ActivityNavigator.forActivity(getActivity()).back();

                Bundle bundle = ActivityOptions.makeCustomAnimation(getActivity(), R.anim.idle, R.anim.idle).toBundle();
                ActivityNavigator.forActivity(getActivity()).withData("a", "String from MainUI").go(ActivityA.class, bundle);

            }
        });
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
        view.findViewById(R.id.loading).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                getParent().go(new LoadingScreen());
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
        view.findViewById(R.id.slide_menu_4).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                getParent().go(new BaseScreen() {
                    @Override
                    public View createView() {
                        return View.inflate(getContext(), R.layout.slide_menu_4, null);
                    }
                });
            }
        });
        view.findViewById(R.id.extend_relative_layout).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                getParent().go(new BaseScreen() {
                    @Override
                    public View createView() {
                        return View.inflate(getContext(), R.layout.extend_relative_layout, null);
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
                getParent().go(CallBackScreen.newInstance("Click me"));
            }
        });
    }

    @Override
    public boolean onBackPressed() {
        Toast.makeText(getContext(), "Back is pressed", Toast.LENGTH_SHORT).show();
        return super.onBackPressed();
    }
}
