package by.vkatz.widgets

import android.animation.ValueAnimator
import android.content.Context
import android.graphics.drawable.Drawable
import android.util.AttributeSet
import android.view.GestureDetector
import android.view.MotionEvent
import android.view.ScaleGestureDetector
import android.view.animation.DecelerateInterpolator
import android.widget.ImageView
import by.vkatz.R
import by.vkatz.utils.LibExtension

@Suppress("MemberVisibilityCanPrivate")
open class ExtendImageView : ImageView, LibExtension.ImageInterface {
    private var touchZoom = false
    private var minZoom = 1f
    private var maxZoom = 1f
    private var zoom = 1f
    var scrollDx = 0f
        private set
    var scrollDy = 0f
        private set
    var maxX = 0f
        private set
    var maxY = 0f
        private set
    private var scrollDetector: GestureDetector
    private var scaleDetector: ScaleGestureDetector
    private var animator: ValueAnimator? = null

    constructor(context: Context) : this(context, null)

    constructor(context: Context, attrs: AttributeSet?) : this(context, attrs, 0)

    constructor(context: Context, attrs: AttributeSet?, defStyle: Int) : super(context, attrs, defStyle) {
        val a = context.obtainStyledAttributes(attrs, R.styleable.ExtendImageView, defStyle, 0)
        touchZoom = a.getBoolean(R.styleable.ExtendImageView_touchZoom, false)
        minZoom = a.getFloat(R.styleable.ExtendImageView_minZoom, 1f)
        maxZoom = a.getFloat(R.styleable.ExtendImageView_maxZoom, 1f)
        setComplexBackground(a.getDrawable(R.styleable.ExtendImageView_extendBackground1), a.getDrawable(R.styleable.ExtendImageView_extendBackground2))
        isEnabled = a.getBoolean(R.styleable.ExtendImageView_extendEnabled, isEnabled)
        isActivated = a.getBoolean(R.styleable.ExtendImageView_extendActivated, isActivated)
        a.recycle()
        scrollDetector = GestureDetector(context, object : GestureDetector.SimpleOnGestureListener() {
            override fun onScroll(e1: MotionEvent, e2: MotionEvent, distanceX: Float, distanceY: Float): Boolean {
                scroll(distanceX, distanceY)
                return touchZoom
            }

            override fun onFling(e1: MotionEvent, e2: MotionEvent, velocityX: Float, velocityY: Float): Boolean {
                fling(velocityX, velocityY)
                return touchZoom
            }

            override fun onDoubleTap(e: MotionEvent): Boolean {
                animateZoom(e.x, e.y, zoom * 2)
                return touchZoom
            }
        })
        scaleDetector = ScaleGestureDetector(context, object : ScaleGestureDetector.SimpleOnScaleGestureListener() {
            override fun onScale(detector: ScaleGestureDetector): Boolean {
                zoom(detector.focusX, detector.focusY, zoom * detector.scaleFactor)
                return touchZoom
            }
        })
        update()
    }

    override final fun setComplexBackground(layer1: Drawable?, layer2: Drawable?) {
        LibExtension.setComplexBackground(this, layer1, layer2)
    }

    fun setTouchZoomEnabled(enabled: Boolean) {
        touchZoom = enabled
        update()
    }

    fun getMinZoom(): Float = minZoom

    fun setMinZoom(minZoom: Float) {
        this.minZoom = minZoom
        update()
    }

    fun getMaxZoom(): Float = maxZoom

    fun setMaxZoom(maxZoom: Float) {
        this.maxZoom = maxZoom
        update()
    }

    fun getZoom(): Float = zoom

    fun setZoom(zoom: Float) {
        this.zoom = zoom
        update()
    }

    fun setScroll(dx: Float, dy: Float) {
        this.scrollDx = dx
        this.scrollDy = dy
        update()
    }

    override fun onSizeChanged(w: Int, h: Int, oldw: Int, oldh: Int) {
        super.onSizeChanged(w, h, oldw, oldh)
        update()
    }

    override fun onMeasure(widthMeasureSpec: Int, heightMeasureSpec: Int) {
        super.onMeasure(widthMeasureSpec, heightMeasureSpec)
        update()
    }

    override fun setImageDrawable(drawable: Drawable?) {
        super.setImageDrawable(drawable)
        update()
    }

    override fun onTouchEvent(event: MotionEvent): Boolean {
        return if (touchZoom) {
            scrollDetector.onTouchEvent(event)
            scaleDetector.onTouchEvent(event)
            true
        } else super.onTouchEvent(event)
    }

    private fun update() {
        if (drawable == null) return
        if (touchZoom) scaleType = ImageView.ScaleType.MATRIX
        if (maxZoom < minZoom) maxZoom = minZoom
        val z = clamp(zoom, minZoom, maxZoom) * Math.min(1f * measuredWidth / drawable.intrinsicWidth, 1f * measuredHeight / drawable.intrinsicHeight)
        maxX = drawable.intrinsicWidth * z - measuredWidth
        maxY = drawable.intrinsicHeight * z - measuredHeight
        scrollDx = if (maxX > 0) clamp(scrollDx, 0f, maxX) else maxX / 2
        scrollDy = if (maxY > 0) clamp(scrollDy, 0f, maxY) else maxY / 2
        val matrix = imageMatrix
        matrix.setScale(z, z)
        matrix.postTranslate(-scrollDx, -scrollDy)
        imageMatrix = matrix
        invalidate()
    }

    fun scroll(sx: Float, sy: Float) {
        clearAnimator()
        rawScroll(sx, sy)
    }

    fun zoom(x: Float, y: Float, z: Float) {
        clearAnimator()
        rawZoom(x, y, z)
    }

    private fun fling(vx: Float, vy: Float) {
        clearAnimator()
        animator = ValueAnimator.ofFloat(1f, 0f).setDuration(250)
        animator!!.interpolator = DecelerateInterpolator()
        animator!!.addUpdateListener(object : ValueAnimator.AnimatorUpdateListener {
            internal var prev = 1f

            override fun onAnimationUpdate(animation: ValueAnimator) {
                val current = animation.animatedValue as Float
                val scalar = (current - prev) * 0.05f
                rawScroll(vx * scalar, vy * scalar)
                prev = current
            }
        })
        animator!!.start()
    }

    private fun animateZoom(x: Float, y: Float, z: Float) {
        clearAnimator()
        animator = ValueAnimator.ofFloat(zoom, clamp(z, minZoom, maxZoom)).setDuration(250)
        animator!!.addUpdateListener { animation -> rawZoom(x, y, animation.animatedValue as Float) }
        animator!!.start()
    }

    private fun rawScroll(sx: Float, sy: Float) {
        if (drawable == null) return
        scrollDx += sx
        scrollDy += sy
        update()
    }

    fun rawZoom(x: Float, y: Float, z: Float) {
        if (drawable == null) return
        val pz = zoom
        zoom = clamp(z, minZoom, maxZoom)
        scrollDx = zoom / pz * (scrollDx + x) - x
        scrollDy = zoom / pz * (scrollDy + y) - y
        update()
    }

    fun clearAnimator() {
        if (animator != null) {
            animator!!.cancel()
            animator = null
        }
    }

    fun clamp(what: Float, min: Float, max: Float): Float {
        if (what < min) return min
        if (what > max) return max
        return what
    }
}