package by.vkatz.widgets;

import android.content.Context;
import android.content.res.TypedArray;
import android.graphics.Canvas;
import android.graphics.Color;
import android.graphics.Paint;
import android.graphics.RectF;
import android.util.AttributeSet;
import android.view.View;
import by.vkatz.R;

/**
 * Created by vKatz on 29.01.2015
 * <br/>Available xml params:
 * <br/><pre>
 * color     - color of view
 * thickness - width of circle
 * progress  - progress, 0 - indeterminate
 * flip      - flip horizontal
 * rotate    - rotate
 * </pre>
 */
@SuppressWarnings("unused")
public class LoadingView extends View {
    private static final int DEF_SIZE = 50;

    private Paint paint;
    private RectF drawRect;
    private float offset = 0;
    private float pass = 0;
    private float lw;
    private int color;
    private float thickness;
    private int progress;
    private boolean flip;
    private int rotate;

    public LoadingView(Context context) {
        this(context, null);
    }

    public LoadingView(Context context, AttributeSet attrs) {
        super(context, attrs);
        TypedArray a = context.obtainStyledAttributes(attrs, R.styleable.LoadingView, 0, 0);
        color = a.getColor(R.styleable.LoadingView_spinner_color, Color.WHITE);
        thickness = a.getDimension(R.styleable.LoadingView_spinner_thickness, 5);
        progress = a.getInt(R.styleable.LoadingView_progress, 0);
        rotate = a.getInt(R.styleable.LoadingView_rotate, 0);
        flip = a.getBoolean(R.styleable.LoadingView_flip, false);
        a.recycle();
        lw = 3.6f * progress;
        drawRect = new RectF();
        paint = new Paint();
        paint.setColor(color);
        paint.setAntiAlias(true);
        paint.setStyle(Paint.Style.STROKE);
        paint.setStrokeWidth(thickness);
    }

    @Override
    protected void onMeasure(int widthMeasureSpec, int heightMeasureSpec) {
        int w = resolveSize(widthMeasureSpec);
        int h = resolveSize(heightMeasureSpec);
        setMeasuredDimension(w, h);
        updateDrawingRect();
    }

    private void updateDrawingRect() {
        int pl = getPaddingLeft();
        int pr = getPaddingRight();
        int pt = getPaddingTop();
        int pb = getPaddingBottom();
        int w = getMeasuredWidth() - pl - pr;
        int h = getMeasuredHeight() - pt - pb;
        int s = Math.min(w, h);
        drawRect.set(pl + (w - s) / 2 + thickness / 2 + 1, pt + (h - s) / 2 + thickness / 2 + 1, pl + (w + s) / 2 - thickness / 2 - 1, pt + (h + s) / 2 - thickness / 2 - 1);
    }

    private int resolveSize(int spec) {
        int defSize = (int) (DEF_SIZE * getContext().getResources().getDisplayMetrics().density);
        int mode = MeasureSpec.getMode(spec);
        int size = MeasureSpec.getSize(spec);
        switch (mode) {
            case MeasureSpec.UNSPECIFIED:
                size = defSize;
                break;
            case MeasureSpec.AT_MOST:
                size = Math.min(size, defSize);
                break;
        }
        return size;
    }

    public int getRotate() {
        return rotate;
    }

    public void setRotate(int rotate) {
        this.rotate = rotate;
        invalidate();
    }

    public boolean isFlip() {
        return flip;
    }

    public void setFlip(boolean flip) {
        this.flip = flip;
        invalidate();
    }

    public int getProgress() {
        return progress;
    }

    public void setProgress(int progress) {
        if (progress > 100) progress = 100;
        if (progress < 0) progress = 0;
        this.progress = progress;
        invalidate();
    }

    public int getColor() {
        return color;
    }

    public void setColor(int color) {
        this.color = color;
        paint.setColor(color);
        invalidate();
    }

    public float getThickness() {
        return thickness;
    }

    public void setThickness(float thickness) {
        this.thickness = thickness;
        updateDrawingRect();
        invalidate();
    }

    @Override
    protected void onDraw(Canvas canvas) {
        super.onDraw(canvas);
        if (isInEditMode()) canvas.drawOval(drawRect, paint); //preview whole circle in editor
        canvas.rotate(rotate, drawRect.centerX(), drawRect.centerY());
        float w;
        if (progress == 0) {
            pass = (pass += 0.015) > 1 ? 0 : pass;
            w = (float) (240 * Math.pow(Math.sin(Math.PI * pass), 4) + 30);
            offset = (offset + Math.abs(lw - w) / 2 + (lw - w) / 2 + 1.5f) % 360;
        } else {
            w = lw + (3.6f * progress - lw) / 16;
            if (Math.abs(3.6f * progress - w) < 1) w = 3.6f * progress;
            offset = offset + (360 - offset) / 16;
            if (360 - offset < 0.05) offset = 360;
        }
        lw = w;
        canvas.drawArc(drawRect, flip ? -offset : offset, flip ? -w : w, false, paint);
        invalidate();
    }
}
