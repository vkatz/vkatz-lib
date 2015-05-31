package by.vkatz.widgets;

import android.content.Context;
import android.content.res.TypedArray;
import android.graphics.*;
import android.graphics.drawable.BitmapDrawable;
import android.graphics.drawable.Drawable;
import android.util.AttributeSet;
import by.vkatz.R;

/**
 * Created by vKatz on 22.05.2015.
 */
public class RoundRectLazyImage extends LazyImage {
    private float roundSize = 0;

    public RoundRectLazyImage(Context context) {
        super(context);
    }

    public RoundRectLazyImage(Context context, AttributeSet attrs) {
        super(context, attrs);
        init(attrs);
    }

    public RoundRectLazyImage(Context context, AttributeSet attrs, int defStyle) {
        super(context, attrs, defStyle);
        init(attrs);
    }

    private void init(AttributeSet attrs) {
        TypedArray a = getContext().obtainStyledAttributes(attrs, R.styleable.RoundRectLazyImage);
        setRoundSize(a.getDimension(R.styleable.RoundRectLazyImage_roundSize, 0));
        a.recycle();
        super.setScaleType(ScaleType.MATRIX);
    }

    public void setRoundSize(float roundSize) {
        this.roundSize = roundSize;
        if (getDrawable() != null && getDrawable() instanceof MyRoundRectBitmapDrawable)
            ((MyRoundRectBitmapDrawable) getDrawable()).setCornerRadius(roundSize);
    }

    /**
     * Will do nothing
     */
    @Override
    @Deprecated
    public final void setScaleType(ScaleType scaleType) {
    }

    @Override
    public void setImageBitmap(Bitmap bm) {
        if (bm != null) {
            MyRoundRectBitmapDrawable drawable = new MyRoundRectBitmapDrawable(bm);
            drawable.setCornerRadius(roundSize);
            super.setImageDrawable(drawable);
        } else super.setImageDrawable(null);
    }

    @Override
    public void setImageDrawable(Drawable dr) {
        if (dr instanceof BitmapDrawable) setImageBitmap(((BitmapDrawable) dr).getBitmap());
        else super.setImageDrawable(dr);
    }

    @Override
    public void setImageResource(int resId) {
        setImageBitmap(BitmapFactory.decodeResource(getResources(), resId));
    }

    private static class MyRoundRectBitmapDrawable extends Drawable {

        private Paint paint;
        private Bitmap bitmap;
        private BitmapShader shader;
        private float cornerRadius;
        private RectF rect;

        public MyRoundRectBitmapDrawable(Bitmap bitmap) {
            super();
            paint = new Paint();
            paint.setShader(shader = new BitmapShader(bitmap, Shader.TileMode.CLAMP, Shader.TileMode.CLAMP));
            paint.setAntiAlias(true);
            this.bitmap = bitmap;
        }

        @Override
        public int getIntrinsicWidth() {
            return -1;
        }

        @Override
        public int getIntrinsicHeight() {
            return -1;
        }

        @Override
        public void setBounds(int left, int top, int right, int bottom) {
            super.setBounds(left, top, right, bottom);
            rect = new RectF(new Rect(left, top, right, bottom));
            updateMatrix();
        }

        @Override
        public void setBounds(Rect bounds) {
            super.setBounds(bounds);
            rect = new RectF(bounds);
            updateMatrix();
        }

        private void updateMatrix() {
            Matrix matrix = new Matrix();
            float scale = Math.max(1f * rect.width() / bitmap.getWidth(), 1f * rect.height() / bitmap.getHeight());
            matrix.setScale(scale, scale);
            shader.setLocalMatrix(matrix);
        }

        public void setCornerRadius(float cornerRadius) {
            this.cornerRadius = cornerRadius;
        }

        @Override
        public void draw(Canvas canvas) {
            canvas.drawRoundRect(rect, cornerRadius, cornerRadius, paint);
        }

        @Override
        public void setAlpha(int i) {
            paint.setAlpha(i);
        }

        @Override
        public void setColorFilter(ColorFilter colorFilter) {
        }

        @Override
        public int getOpacity() {
            return paint.getAlpha();
        }
    }
}
