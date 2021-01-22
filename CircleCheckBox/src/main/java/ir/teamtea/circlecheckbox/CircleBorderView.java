package ir.teamtea.circlecheckbox;

import android.animation.Animator;
import android.animation.ValueAnimator;
import android.graphics.Canvas;
import android.graphics.Paint;
import android.graphics.Path;
import android.graphics.RectF;
import android.view.View;
import android.view.animation.DecelerateInterpolator;

class CircleBorderView implements Animator.AnimatorListener {


    private float degree;
    private final View view;
    private float thickness;
    private long duration;
    private int startAngle;
    private final onCircleBorderAnimListener onCircleBorderAnimListener;

    private Path frontPath;
    private Paint frontPaint;
    private RectF frontRect;
    private ValueAnimator mAnimator;
    private int width;
    private AnimType animType;


    enum AnimType {
        START, REVERSE
    }

    public CircleBorderView(View view, float degree, int color, float thickness, int startAngle,
                            long duration, onCircleBorderAnimListener onCircleBorderAnimListener) {
        this.degree = degree;
        this.thickness = thickness;
        this.view = view;
        this.onCircleBorderAnimListener = onCircleBorderAnimListener;
        this.duration = duration;
        this.startAngle = startAngle;
        validateDegree(this.degree);
        initView(color);
    }

    private void initView(int color) {
        initCanvasTools(color);
        createAnimator();
    }

    private void validateDegree(float degree) {
        if (degree <= 0 || degree > 360) {
            throw new IllegalArgumentException("degree should be between 0 and 360");
        }
    }

    public void setWidth(int width) {
        this.width = width;
    }

    public void draw(Canvas canvas) {
        canvas.drawPath(frontPath, frontPaint);
    }

    private void initCanvasTools(int color) {
        frontPath = new Path();
        frontRect = new RectF();
        initPaint(color);
    }

    private void initPaint(int color) {
        frontPaint = new Paint();
        frontPaint.setStyle(Paint.Style.FILL);
        frontPaint.setColor(color);
        frontPaint.setAntiAlias(true);
    }

    private void createAnimator() {
        mAnimator = ValueAnimator.ofFloat(0, degree - 0.1f);
        mAnimator.setDuration(duration);
        mAnimator.setInterpolator(new DecelerateInterpolator());

        mAnimator.addUpdateListener(animation -> {
            float animatedValue = (float) animation.getAnimatedValue();
            drawPath(animatedValue);
            view.invalidate();
        });
        mAnimator.addListener(this);
    }

    private void drawPath(float animatedValue) {
        frontPath.reset();
        frontRect.set(0, 0, width, width);
        frontPath.moveTo(0, width / 2f);
        frontPath.arcTo(frontRect, startAngle, animatedValue);
        frontRect.inset(thickness, thickness);
        frontPath.arcTo(frontRect, startAngle + animatedValue, -animatedValue);
        frontPath.rLineTo(-thickness, 0);
    }

    public void reverse() {
        animType = AnimType.REVERSE;
        mAnimator.reverse();
    }

    public void start() {
        animType = AnimType.START;
        mAnimator.start();
    }


    @Override
    public void onAnimationStart(Animator animation) {

    }

    @Override
    public void onAnimationEnd(Animator animation) {
        if (animType == AnimType.START) {
            onCircleBorderAnimListener.onAnimEnd();
        } else {
            onCircleBorderAnimListener.onReverseEnd();
        }
    }

    @Override
    public void onAnimationCancel(Animator animation) {

    }

    @Override
    public void onAnimationRepeat(Animator animation) {

    }


    interface onCircleBorderAnimListener {
        void onReverseEnd();

        void onAnimEnd();
    }

    public void setDegree(float degree) {
        validateDegree(degree);
        this.degree = degree;
        createAnimator();
    }

    public void setThickness(float thickness) {
        this.thickness = thickness;
    }

    public void setDuration(long duration) {
        this.duration = duration;
        createAnimator();
    }

    public void setStartAngle(int startAngle) {
        this.startAngle = startAngle;
    }
}
