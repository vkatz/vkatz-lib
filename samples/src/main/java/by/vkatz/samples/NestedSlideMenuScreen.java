package by.vkatz.samples;

import android.graphics.Color;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.util.TypedValue;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import by.vkatz.widgets.SlideMenuLayout;

/**
 * Created by vKatz on 08.03.2015.
 */
public class NestedSlideMenuScreen extends BaseScreen {
    @Override
    public View createView() {
        return View.inflate(getContext(), R.layout.nested_slide_menu_1, null);
    }

    @Override
    public void initView(View view) {
        super.initView(view);
        final View btn = view.findViewById(R.id.btn);
        final TextView header = (TextView) view.findViewById(R.id.header);
        final float headerTextSize = header.getTextSize();
        RecyclerView list = (RecyclerView) view.findViewById(R.id.recycler);
        SlideMenuLayout menu = (SlideMenuLayout) view.findViewById(R.id.menu);

        list.setLayoutManager(new LinearLayoutManager(getContext(), LinearLayoutManager.VERTICAL, false));
        list.setAdapter(new RecyclerView.Adapter() {
            @Override
            public RecyclerView.ViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
                TextView tw = new TextView(parent.getContext());
                tw.setTextColor(Color.WHITE);
                tw.setTextSize(50);
                return new RecyclerView.ViewHolder(tw) {
                };
            }

            @Override
            public void onBindViewHolder(RecyclerView.ViewHolder holder, int position) {
                ((TextView) holder.itemView).setText("Position " + position);
            }

            @Override
            public int getItemCount() {
                return 100;
            }
        });

        menu.setOnSlideChangeListener(new SlideMenuLayout.OnSlideChangeListener() {
            @Override
            public void onScrollSizeChangeListener(SlideMenuLayout view, float value) {
                btn.setAlpha(1 - value);
                header.setTextSize(TypedValue.COMPLEX_UNIT_PX, headerTextSize * Math.max(1 - value, 0.3f));
                header.setTranslationX(-100 *  value);
            }
        });
    }
}
