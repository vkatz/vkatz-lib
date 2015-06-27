package by.vkatz.utils;

import android.graphics.drawable.Drawable;
import android.graphics.drawable.LayerDrawable;
import android.widget.TextView;

/**
 * Created by vKatz on 27.06.2015.
 */
public class TextViewExtension {

    public static void setCompoundDrawableSize(TextView textView, int width, int height) {
        if (width <= 0 && height <= 0) return;
        Drawable drawables[] = textView.getCompoundDrawables();
        Drawable processed[] = new Drawable[4];
        for (int i = 0; i < 4; i++) {
            if (drawables[i] == null) continue;
            if (drawables[i] instanceof FixedSizeDrawable) {
                ((FixedSizeDrawable) drawables[i]).setSize(width, height);
                processed[i] = drawables[i];
            } else processed[i] = new FixedSizeDrawable(drawables[i], width, height);
        }
        textView.setCompoundDrawables(processed[0], processed[1], processed[2], processed[3]);
    }

    public static void setFont(TextView textView, String assetFontFile) {
        textView.setTypeface(FontsManager.instance().getFont(textView.getContext(), assetFontFile));
    }

    public interface Interface {
        void setFont(String assetFontFile);

        void setCompoundDrawableSize(int width, int height);
    }

    public static class FixedSizeDrawable extends LayerDrawable {
        private int intrinsicWidth;
        private int intrinsicHeight;
        private Drawable child;

        public FixedSizeDrawable(Drawable drawable, int width, int height) {
            super(new Drawable[]{drawable});
            child = drawable;
            setSize(width, height);
        }

        public void setSize(int width, int height) {
            if (width > 0 && height > 0) {
                intrinsicWidth = width;
                intrinsicHeight = height;
            } else if (width > 0) {
                intrinsicWidth = width;
                intrinsicHeight = (int) (child.getIntrinsicHeight() * (1f * width / child.getIntrinsicWidth()));
            } else {
                intrinsicWidth = (int) (child.getIntrinsicWidth() * (1f * height / child.getIntrinsicHeight()));
                intrinsicHeight = height;
            }
            setFilterBitmap(true);
            child.setFilterBitmap(true);
            setBounds(0, 0, intrinsicWidth, intrinsicHeight);
        }

        @Override
        public int getIntrinsicWidth() {
            return intrinsicWidth;
        }

        @Override
        public int getIntrinsicHeight() {
            return intrinsicHeight;
        }
    }
}
