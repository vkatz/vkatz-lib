package by.vkatz.widgets;

import android.content.Context;
import android.content.res.TypedArray;
import android.graphics.*;
import android.util.AttributeSet;
import android.view.ViewParent;
import android.widget.FrameLayout;
import by.vkatz.R;

/**
 * Created by vKatz on 02.02.2015
 */
public class RoundRectLayout extends FrameLayout {
    private Paint paint;
    private float cornersRoundSize;
    private RectF rect;
    private Bitmap image;
    private Canvas imageCanvas;

    public RoundRectLayout(Context context) {
        super(context);
        init();
    }

    public RoundRectLayout(Context context, AttributeSet attrs) {
        super(context, attrs);
        TypedArray array = context.obtainStyledAttributes(attrs, R.styleable.RoundRectLayout);
        cornersRoundSize = array.getDimensionPixelSize(R.styleable.RoundRectLayout_roundSize, 0);
        array.recycle();
        init();
    }

    private void init() {
        paint = new Paint();
        paint.setAntiAlias(true);
    }

    public void setCornersRoundSize(float cornersRoundSize) {
        this.cornersRoundSize = cornersRoundSize;
    }

    @Override
    protected void onLayout(boolean changed, int left, int top, int right, int bottom) {
        super.onLayout(changed, left, top, right, bottom);
        rect = new RectF(getPaddingLeft(), getPaddingTop(), getWidth() - getPaddingRight(), getHeight() - getPaddingBottom());
        if (image != null) image.recycle();
        image = Bitmap.createBitmap(getWidth(), getHeight(), Bitmap.Config.ARGB_8888);
        imageCanvas = new Canvas(image);
        System.gc();
    }

    //possible not safe
    @Override
    public ViewParent invalidateChildInParent(int[] location, Rect dirty) {
        invalidate();
        return super.invalidateChildInParent(location, dirty);
    }

    @Override
    protected void dispatchDraw(Canvas canvas) {
        imageCanvas.drawColor(Color.TRANSPARENT, PorterDuff.Mode.CLEAR);
        super.dispatchDraw(imageCanvas);
        paint.setColor(Color.WHITE);
        paint.setXfermode(new PorterDuffXfermode(PorterDuff.Mode.SRC));
        canvas.drawRoundRect(rect, cornersRoundSize, cornersRoundSize, paint);
        paint.setXfermode(new PorterDuffXfermode(PorterDuff.Mode.SRC_IN));
        canvas.drawBitmap(image, 0, 0, paint);
    }
}
