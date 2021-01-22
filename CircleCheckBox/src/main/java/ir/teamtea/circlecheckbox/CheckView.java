package ir.teamtea.circlecheckbox;

import android.animation.Animator;
import android.animation.ValueAnimator;
import android.content.Context;
import android.graphics.Canvas;
import android.graphics.Paint;
import android.graphics.Path;
import android.graphics.PathMeasure;
import android.graphics.PointF;
import android.graphics.RectF;
import android.util.AttributeSet;
import android.util.Log;
import android.view.View;
import android.view.animation.DecelerateInterpolator;

import androidx.annotation.ColorInt;
import androidx.annotation.FloatRange;

public abstract class CheckView extends View {

    abstract protected int getCheckColor();

    abstract protected int getBackGroundColor();

    abstract protected float getCheckThickness();

    abstract protected Long getCheckAnimationDuration();

    private static final String TAG = "CheckView";
    private static final boolean DEBUG = false;

    /**
     * The path of the check mark
     */
    private Path mMinorPathCheck;
    private Path mMajorPathCheck;
    private Path mMinorEraserPathCheck;
    private Path mMajorEraserPathCheck;

    private float mMinorContourLength;
    private float mMajorContourLength;
    private RectF mDrawingRect;
    private RectF mCircleRect;
    private Paint mPaint;
    private Paint eraser;

    private PointF mCheckStart;

    private PointF mCheckPivot;

    private PointF mCheckEnd;

    private PointF mCircleStart;
    private Boolean mChecked = false;

    private ValueAnimator mCheckAnimator;

    public CheckView(Context context) {
        super(context);
    }

    public CheckView(Context context, AttributeSet attrs) {
        super(context, attrs);
    }

    public CheckView(Context context, AttributeSet attrs, int defStyleAttr) {
        super(context, attrs, defStyleAttr);
    }

    protected void initCheckView() {
        mMinorPathCheck = new Path();
        mMajorPathCheck = new Path();
        mMinorEraserPathCheck = new Path();
        mMajorEraserPathCheck = new Path();

        mDrawingRect = new RectF();
        mCircleRect = new RectF();
        mCheckStart = new PointF();
        mCheckPivot = new PointF();
        mCheckEnd = new PointF();
        mCircleStart = new PointF();
        mPaint = createPaint(getCheckColor(), getCheckThickness());
        eraser = createPaint(getBackGroundColor(), getCheckThickness());
    }

    @Override
    protected void onLayout(boolean changed, int left, int top, int right, int bottom) {
        super.onLayout(changed, left, top, right, bottom);
        if (changed) {

            mDrawingRect.left = getPaddingLeft();
            mDrawingRect.top = getPaddingTop();
            mDrawingRect.right = getMeasuredWidth() - getPaddingRight();
            mDrawingRect.bottom = getMeasuredHeight() - getPaddingBottom();

            mCheckStart.x = mDrawingRect.left + mDrawingRect.width() / 4;
            mCheckStart.y = mDrawingRect.top + mDrawingRect.height() / 2;
            mCheckPivot.x = mDrawingRect.left + mDrawingRect.width() * .426F;
            mCheckPivot.y = mDrawingRect.top + mDrawingRect.height() * .66F;
            mCheckEnd.x = mDrawingRect.left + mDrawingRect.width() * .75F;
            mCheckEnd.y = mDrawingRect.top + mDrawingRect.height() * .30F;

            mMinorContourLength = distance(mCheckPivot.x, mCheckPivot.y, mCheckStart.x, mCheckStart.y);
            mMajorContourLength = distance(mCheckPivot.x, mCheckPivot.y, mCheckEnd.x, mCheckEnd.y);

            mCircleRect.left = mDrawingRect.left + getCheckThickness() / 2f;
            mCircleRect.top = mDrawingRect.top + getCheckThickness() / 2f;
            mCircleRect.right = mDrawingRect.right - getCheckThickness() / 2f;
            mCircleRect.bottom = mDrawingRect.bottom - getCheckThickness() / 2f;
            mCircleStart.x = mCircleRect.right;
            mCircleStart.y = mCircleRect.bottom / 2;

            if (DEBUG && (mDrawingRect.width() != mDrawingRect.height())) {
                Log.w(TAG, "WARNING: " + " will look weird because you've given it a non-square drawing area.  " +
                        "Make sure the width, height, and padding resolve to a square.");
            }
        }
    }

    protected void onDrawCalled(Canvas canvas) {
        if (mChecked == null) {
            return;
        }
        if (mChecked) {
            canvas.drawPath(mMinorPathCheck, mPaint);
            canvas.drawPath(mMajorPathCheck, mPaint);
        } else {
            canvas.drawPath(mMinorPathCheck, mPaint);
            canvas.drawPath(mMajorPathCheck, mPaint);
            canvas.drawPath(mMinorEraserPathCheck, eraser);
            canvas.drawPath(mMajorEraserPathCheck, eraser);
        }

    }

    public void checkIconInCheckView() {
        mChecked = true;
        mCheckAnimator = ValueAnimator.ofFloat(0, 1);
        mCheckAnimator.removeAllUpdateListeners();
        mCheckAnimator.setDuration(getCheckAnimationDuration())
                .setInterpolator(new DecelerateInterpolator());
        mCheckAnimator.addUpdateListener(mCheckAnimatorListener);
        mCheckAnimator.start();
    }

    protected void uncheckIconInCheckView() {
        mChecked = false;
        mCheckAnimator = ValueAnimator.ofFloat(0, 1);
        mCheckAnimator.removeAllUpdateListeners();
        mCheckAnimator.setDuration(getCheckAnimationDuration())
                .setInterpolator(new DecelerateInterpolator());
        mCheckAnimator.addUpdateListener(mCheckAnimatorListener);
        mCheckAnimator.addListener(new Animator.AnimatorListener() {
            @Override
            public void onAnimationStart(Animator animation) {

            }

            @Override
            public void onAnimationEnd(Animator animation) {
                mChecked = null;
            }

            @Override
            public void onAnimationCancel(Animator animation) {

            }

            @Override
            public void onAnimationRepeat(Animator animation) {

            }
        });
        mCheckAnimator.start();
    }

    private Paint createPaint(@ColorInt int color, float strokeWidth) {
        Paint p = new Paint();
        p.setColor(color);
        p.setStyle(Paint.Style.STROKE);
        p.setStrokeWidth(strokeWidth);
        p.setStrokeJoin(Paint.Join.ROUND);
        p.setAntiAlias(true);
        p.setStrokeCap(Paint.Cap.ROUND);
        return p;
    }

    private Path setMajorCheckPath() {
        mMinorPathCheck.reset();
        mMinorPathCheck.moveTo(mCheckPivot.x, mCheckPivot.y);
        mMinorPathCheck.lineTo(mCheckEnd.x, mCheckEnd.y);
        return mMinorPathCheck;
    }

    private Path setMinorCheckPath() {
        mMajorPathCheck.reset();
        mMajorPathCheck.moveTo(mCheckPivot.x, mCheckPivot.y);
        mMajorPathCheck.lineTo(mCheckStart.x, mCheckStart.y);
        return mMajorPathCheck;
    }

    private Path setMajorCheckPathErase() {
        mMinorEraserPathCheck.reset();
        mMinorEraserPathCheck.moveTo(mCheckEnd.x, mCheckEnd.y);
        mMinorEraserPathCheck.lineTo(mCheckPivot.x, mCheckPivot.y);
        return mMinorEraserPathCheck;
    }

    private Path setMinorCheckPathErase() {
        mMajorEraserPathCheck.reset();
        mMajorEraserPathCheck.moveTo(mCheckStart.x, mCheckStart.y);
        mMajorEraserPathCheck.lineTo(mCheckPivot.x, mCheckPivot.y);
        return mMajorEraserPathCheck;
    }

    private void setCheckPathPercentage(@FloatRange(from = 0, to = 1) float percent) {
        if (mChecked == null) {
            return;
        }
        if (mChecked) {
            drawCheckPath(percent);
        } else {
            drawEraseCheckPath(percent);
        }
    }

    private void drawCheckPath(@FloatRange(from = 0, to = 1) float percent) {
        Path majorPath = setMajorCheckPath();
        Path minorPath = setMinorCheckPath();
        final float totalLength = mMinorContourLength + mMajorContourLength;
        final float pivotMinorPercent = mMinorContourLength / totalLength;
        final float pivotMajorPercent = mMajorContourLength / totalLength;

        float[] majorPoint = new float[2];
        float[] minorPoint = new float[2];

        final float majorPercent = percent / pivotMajorPercent;
        final float majorDistance = mMajorContourLength * majorPercent;

        final float minorPercent = percent / pivotMinorPercent;
        final float minorDistance = mMinorContourLength * minorPercent;

        PathMeasure mMajorPathMeasure = new PathMeasure();
        PathMeasure mMinorPathMeasure = new PathMeasure();
        mMajorPathMeasure.setPath(majorPath, false);
        mMajorPathMeasure.getPosTan(majorDistance, majorPoint, null);

        mMinorPathMeasure.setPath(minorPath, false);
        mMinorPathMeasure.getPosTan(minorDistance, minorPoint, null);

        majorPath.reset();
        minorPath.reset();

        majorPath.moveTo(mCheckPivot.x, mCheckPivot.y);
        minorPath.moveTo(mCheckPivot.x, mCheckPivot.y);
        majorPath.lineTo(majorPoint[0], majorPoint[1]);
        minorPath.lineTo(minorPoint[0], minorPoint[1]);
    }

    private void drawEraseCheckPath(@FloatRange(from = 0, to = 1) float percent) {
        Path majorPath = setMajorCheckPathErase();
        Path minorPath = setMinorCheckPathErase();
        final float totalLength = mMinorContourLength + mMajorContourLength;
        final float pivotMinorPercent = mMinorContourLength / totalLength;
        final float pivotMajorPercent = mMajorContourLength / totalLength;

        float[] majorPoint = new float[2];
        float[] minorPoint = new float[2];

        final float majorPercent = percent / pivotMajorPercent;
        final float majorDistance = mMajorContourLength * majorPercent;

        final float minorPercent = percent / pivotMinorPercent;
        final float minorDistance = mMinorContourLength * minorPercent;

        PathMeasure mMajorPathMeasure = new PathMeasure();
        PathMeasure mMinorPathMeasure = new PathMeasure();
        mMajorPathMeasure.setPath(majorPath, false);
        mMajorPathMeasure.getPosTan(majorDistance, majorPoint, null);

        mMinorPathMeasure.setPath(minorPath, false);
        mMinorPathMeasure.getPosTan(minorDistance, minorPoint, null);

        majorPath.reset();
        minorPath.reset();

        majorPath.moveTo(mCheckEnd.x, mCheckEnd.y);
        minorPath.moveTo(mCheckStart.x, mCheckStart.y);
        majorPath.lineTo(majorPoint[0], majorPoint[1]);
        minorPath.lineTo(minorPoint[0], minorPoint[1]);
    }

    private static float distance(float x1, float y1, float x2, float y2) {
        final float xAbs = Math.abs(x1 - x2);
        final float yAbs = Math.abs(y1 - y2);
        return (float) Math.sqrt((yAbs * yAbs) + (xAbs * xAbs));
    }

    private final ValueAnimator.AnimatorUpdateListener mCheckAnimatorListener = animation -> {
        final float fraction = animation.getAnimatedFraction();
        setCheckPathPercentage(fraction);
        invalidate();
    };

}
