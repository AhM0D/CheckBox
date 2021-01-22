package ir.teamtea.circlecheckbox;

import android.animation.Animator;
import android.animation.AnimatorListenerAdapter;
import android.animation.ValueAnimator;
import android.content.Context;
import android.content.res.TypedArray;
import android.graphics.Bitmap;
import android.graphics.Canvas;
import android.graphics.Color;
import android.graphics.Paint;
import android.graphics.PorterDuff;
import android.graphics.PorterDuffXfermode;
import android.util.AttributeSet;
import android.view.View;
import android.view.animation.DecelerateInterpolator;

import androidx.annotation.Nullable;

public class CircleCheckBox extends CheckView {


    public static final int DEFAULT_CHECK_COLOR = Color.WHITE;

    public static final int DEFAULT_BORDER_COLOR = Color.WHITE;

    public static final int DEFAULT_BACKGROUND_COLOR = Color.BLUE;

    public static final int DEFAULT_VIEW_SIZE = 60;

    public static final float DEFAULT_CHECK_Thickness_SIZE = 4f;
    public static final float DEFAULT_BORDER_Thickness_SIZE = 2f;

    public static final int DEFAULT_STROKE_START_ANGLE = 180;
    public static final int DEFAULT_STROKE_DEGREE = 180;

    public static final Long DEFAULT_DURATION = 150L;
    public static final Long DEFAULT_CHECK_ANIM_DURATION = 150L;

    private boolean animate = false;
    private boolean isAnimationActive = false;
    private boolean isChecked = false;

    private ValueAnimator animator;

    private long duration = DEFAULT_DURATION;
    private long checkAnimDuration = DEFAULT_CHECK_ANIM_DURATION;

    private int circleStrokeDegree;
    private int circleStrokeStartAngle;
    private int borderColor;
    private int checkIconColor;
    private int backgroundColor;

    private float borderThickness;
    private float checkThickness;
    private float radius = -1;

    private CircleBorderView circleBorderView;
    private OnCheckedChangeListener onCheckedChangeListener;

    @Override
    protected int getCheckColor() {
        return checkIconColor;
    }

    @Override
    protected int getBackGroundColor() {
        return backgroundColor;
    }

    @Override
    protected float getCheckThickness() {
        return checkThickness;
    }

    @Override
    protected Long getCheckAnimationDuration() {
        return checkAnimDuration;
    }

    private CircleCheckBox(Builder builder) {
        super(builder.context);
        this.circleStrokeDegree = builder.circleStrokeDegree;
        this.circleStrokeStartAngle = builder.circleStrokeStartAngle;
        this.borderColor = builder.borderColor;
        this.checkIconColor = builder.checkIconColor;
        this.backgroundColor = builder.backgroundColor;
        this.borderThickness = builder.borderThickness;
        this.checkThickness = builder.checkThickness;
        this.onCheckedChangeListener = builder.onCheckedChangeListener;
        this.duration = builder.duration;
        this.checkAnimDuration = builder.checkAnimDuration;
        initView();
    }

    public CircleCheckBox(Context context, @Nullable AttributeSet attrs) {
        super(context, attrs);
        initAttr(context, attrs);
    }

    public CircleCheckBox(Context context, @Nullable AttributeSet attrs, int defStyleAttr) {
        super(context, attrs, defStyleAttr);
        initAttr(context, attrs);
    }

    private void initAttr(Context context, @Nullable AttributeSet attrs) {
        TypedArray typedArray = context.obtainStyledAttributes(attrs, R.styleable.CircleCheckBox, 0, 0);
        try {
            checkThickness = typedArray.getDimension(R.styleable.CircleCheckBox_checkIconSize, DEFAULT_CHECK_Thickness_SIZE);
            borderThickness = typedArray.getDimension(R.styleable.CircleCheckBox_borderSize, DEFAULT_BORDER_Thickness_SIZE);
            backgroundColor = typedArray.getInt(R.styleable.CircleCheckBox_backgroundColor, DEFAULT_BACKGROUND_COLOR);
            checkIconColor = typedArray.getColor(R.styleable.CircleCheckBox_checkIconColor, DEFAULT_CHECK_COLOR);
            borderColor = typedArray.getColor(R.styleable.CircleCheckBox_borderColor, DEFAULT_BORDER_COLOR);
            circleStrokeStartAngle = typedArray.getInt(R.styleable.CircleCheckBox_strokeStartAngle, DEFAULT_STROKE_START_ANGLE);
            circleStrokeDegree = typedArray.getInt(R.styleable.CircleCheckBox_strokeDegree, DEFAULT_STROKE_DEGREE);
            initView();
        } finally {
            typedArray.recycle();
        }
    }

    private void initView() {
        initCheckView();
        circleBorderView = new CircleBorderView(this, circleStrokeDegree, borderColor, borderThickness,
                circleStrokeStartAngle, duration, new CircleBorderView.onCircleBorderAnimListener() {
            @Override
            public void onReverseEnd() {
                uncheckCustomView();
            }

            @Override
            public void onAnimEnd() {
                isAnimationActive = false;
            }
        });
    }

    @Override
    protected void onDraw(Canvas canvas) {
        super.onDraw(canvas);
        drawCircleView(canvas);
        circleBorderView.draw(canvas);
        onDrawCalled(canvas);
    }

    private void drawCircleView(Canvas canvas) {
        Bitmap bitmap = Bitmap.createBitmap(getWidth(), getHeight(), Bitmap.Config.ARGB_8888);
        Canvas osCanvas = new Canvas(bitmap);

        Paint circlePaint = new Paint();
        circlePaint.setStyle(Paint.Style.FILL);
        circlePaint.setColor(getBackGroundColor());
        float centerX = getWidth() / 2f;
        float centerY = getHeight() / 2f;
        if (radius == -1) {
            radius = centerX;
        }

        osCanvas.drawCircle(centerX, centerY, centerX, circlePaint);
        circlePaint.setXfermode(new PorterDuffXfermode(PorterDuff.Mode.SRC_OUT));
        osCanvas.drawCircle(centerX, centerY, radius, circlePaint);
        canvas.drawBitmap(bitmap, 0, 0, null);
    }

    @Override
    protected void onSizeChanged(int width, int height, int oldWidth, int oldHeight) {
        circleBorderView.setWidth(width);
    }


    @Override
    protected void onMeasure(int widthMeasureSpec, int heightMeasureSpec) {

        int widthMode = View.MeasureSpec.getMode(widthMeasureSpec);
        int widthSize = View.MeasureSpec.getSize(widthMeasureSpec);
        int heightMode = View.MeasureSpec.getMode(heightMeasureSpec);
        int heightSize = View.MeasureSpec.getSize(heightMeasureSpec);

        int width;
        int height;

        if (widthSize != heightSize && (widthMode == View.MeasureSpec.EXACTLY || heightMode == View.MeasureSpec.EXACTLY)) {
            throw new IllegalArgumentException("Width and height should be equal");
        }

        if (widthMode == View.MeasureSpec.EXACTLY) {

            width = widthSize;
        } else if (widthMode == View.MeasureSpec.AT_MOST) {
            width = Math.min(DEFAULT_VIEW_SIZE, widthSize);
        } else {
            width = DEFAULT_VIEW_SIZE;
        }


        if (heightMode == View.MeasureSpec.EXACTLY) {
            height = heightSize;
        } else if (heightMode == View.MeasureSpec.AT_MOST) {
            height = Math.min(DEFAULT_VIEW_SIZE, heightSize);
        } else {
            height = DEFAULT_VIEW_SIZE;
        }


        setMeasuredDimension(width, height);

    }

    public void setChecked(boolean isChecked) {
        if (isAnimationActive)
            return;
        if (isChecked) {
            runCheckAnim();
        } else {
            runUnCheckAnim();
        }
    }

    private void runCheckAnim() {
        if (animate) {
            return;
        }
        isChecked = true;
        isAnimationActive = true;
        animate = true;
        callOnCheckChangeInterface(true);
        checkCustomView();
    }

    private void runUnCheckAnim() {
        if (!animate) {
            return;
        }
        isChecked = false;
        isAnimationActive = true;
        animate = false;
        callOnCheckChangeInterface(false);
        uncheckIconInCheckView();
        circleBorderView.reverse();
    }

    private void callOnCheckChangeInterface(boolean isChecked) {
        if (onCheckedChangeListener == null)
            return;
        onCheckedChangeListener.onCheckedChange(isChecked);
    }

    @Override
    public void setVisibility(int visibility) {
        setChecked(visibility == VISIBLE);
    }

    private void uncheckCustomView() {
        animator = ValueAnimator.ofFloat(0f, getWidth() / 2f);
        animator.setInterpolator(new DecelerateInterpolator());
        animator.setDuration(duration);
        animator.addUpdateListener(animation -> {
            radius = (float) animation.getAnimatedValue();
            invalidate();
        });
        animator.addListener(new AnimatorListenerAdapter() {
            @Override
            public void onAnimationEnd(Animator animation) {
                isAnimationActive = false;
            }
        });
        animator.start();
    }

    private void checkCustomView() {
        animator = ValueAnimator.ofFloat(radius, 0);
        animator.setInterpolator(new DecelerateInterpolator());
        animator.setDuration(duration);
        animator.addUpdateListener(animation -> {
            radius = (float) animation.getAnimatedValue();
            invalidate();
        });
        animator.addListener(new AnimatorListenerAdapter() {
            @Override
            public void onAnimationEnd(Animator animation) {
                checkIconInCheckView();
                circleBorderView.start();
            }
        });
        animator.start();
    }

    public boolean isChecked() {
        return isChecked;
    }

    public void setDuration(long duration) {
        circleBorderView.setDuration(duration);
        this.duration = duration;
    }

    public void setCircleStrokeDegree(int circleStrokeDegree) {
        circleBorderView.setDegree(circleStrokeDegree);
    }

    public void setCircleStrokeStartAngle(int circleStrokeStartAngle) {
        circleBorderView.setStartAngle(circleStrokeStartAngle);
    }

    public void setCheckAnimDuration(long checkAnimDuration) {
        this.checkAnimDuration = checkAnimDuration;
    }

    public CircleBorderView getCircleBorderView() {
        return circleBorderView;
    }

    public void setCircleBorderView(CircleBorderView circleBorderView) {
        this.circleBorderView = circleBorderView;
    }

    public void setOnCheckedChangeListener(OnCheckedChangeListener onCheckedChangeListener) {
        this.onCheckedChangeListener = onCheckedChangeListener;
    }

    public void setBorderColor(int borderColor) {
        this.borderColor = borderColor;
    }

    public interface OnCheckedChangeListener {
        void onCheckedChange(boolean isChecked);
    }

    public long getDuration() {
        return duration;
    }

    public int getCircleStrokeDegree() {
        return circleStrokeDegree;
    }

    public int getCircleStrokeStartAngle() {
        return circleStrokeStartAngle;
    }

    public Long getCheckAnimDuration() {
        return checkAnimDuration;
    }

    public int getCheckIconColor() {
        return checkIconColor;
    }

    public int getBackgroundColor() {
        return backgroundColor;
    }

    public int getBorderColor() {
        return borderColor;
    }


    public static class Builder {

        private Context context;

        private int circleStrokeDegree = DEFAULT_STROKE_DEGREE;
        private int circleStrokeStartAngle = DEFAULT_STROKE_START_ANGLE;
        private int borderColor = DEFAULT_BORDER_COLOR;
        private int checkIconColor = DEFAULT_CHECK_COLOR;
        private int backgroundColor = DEFAULT_BACKGROUND_COLOR;

        private float borderThickness = DEFAULT_BORDER_Thickness_SIZE;
        private float checkThickness = DEFAULT_CHECK_Thickness_SIZE;

        private long duration = DEFAULT_DURATION;
        private long checkAnimDuration = DEFAULT_CHECK_ANIM_DURATION;


        private OnCheckedChangeListener onCheckedChangeListener;

        public Builder(Context context) {
            this.context = context;
        }

        public Builder setCircleStrokeDegree(int degree) {
            this.circleStrokeDegree = degree;
            return this;
        }

        public Builder setCircleStrokeStartAngle(int startAngle) {
            this.circleStrokeStartAngle = startAngle;
            return this;
        }

        public Builder setBorderColor(int borderColor) {
            this.borderColor = borderColor;
            return this;
        }

        public Builder setCheckIconColor(int iconColor) {
            this.checkIconColor = iconColor;
            return this;
        }

        public Builder setBackGroundColor(int backgroundColor) {
            this.backgroundColor = backgroundColor;
            return this;
        }

        public Builder setBorderThickness(float borderThickness) {
            this.borderThickness = borderThickness;
            return this;
        }

        public Builder setCheckThickness(int checkThickness) {
            this.checkThickness = checkThickness;
            return this;
        }

        public Builder setOnCheckedChangeListener(OnCheckedChangeListener onCheckedChangeListener) {
            this.onCheckedChangeListener = onCheckedChangeListener;
            return this;
        }

        public Builder setDuration(long duration) {
            this.duration = duration;
            return this;
        }

        public Builder setCheckAnimDuration(long duration) {
            this.checkAnimDuration = duration;
            return this;
        }

        public CircleCheckBox build() {
            return new CircleCheckBox(this);
        }
    }

}
