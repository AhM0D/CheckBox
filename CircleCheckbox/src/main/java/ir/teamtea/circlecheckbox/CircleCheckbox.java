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

public class CircleCheckbox extends CheckView {


	public static final int DEFAULT_CHECK_COLOR = Color.WHITE;

	public static final int DEFAULT_BACKGROUND_COLOR = Color.BLUE;

	public static final int DEFAULT_VIEW_SIZE = 60;

	public static final float DEFAULT_STROKE_SIZE = 4f;

	private boolean animate = false;
	private float radius = -1;
	private boolean isAnimationActive = false;
	private boolean isChecked = false;
	private ValueAnimator animator;

	private long duration = 150L;
	private int circleStrokeDegree = 180;
	private int circleStrokeStartAngle = 180;
	private Long checkAnimDuration = 200L;
	private float strokeWidth = -1;
	private int checkIconColor;
	private int backgroundColor;

	private CircleBorderView circleBorderView;
	private OnCheckedChangeListener onCheckedChangeListener;

	@Override
	protected int getCheckColor() {
		if (checkIconColor == 0) {
			return DEFAULT_CHECK_COLOR;
		}
		return checkIconColor;
	}

	@Override
	protected int getBackGroundColor() {
		if (backgroundColor == 0) {
			return DEFAULT_BACKGROUND_COLOR;
		}
		return backgroundColor;
	}

	@Override
	protected float getStrokeWidth() {
		if (strokeWidth <= 0) {
			return DEFAULT_STROKE_SIZE;
		}
		return strokeWidth;
	}

	@Override
	protected Long getCheckAnimationDuration() {
		return checkAnimDuration;
	}

	public CircleCheckbox(Context context, float strokeWidth, int checkIconColor, int backgroundColor) {
		super(context);
		this.strokeWidth = strokeWidth;
		this.checkIconColor = checkIconColor;
		this.backgroundColor = backgroundColor;
		initView();
	}

	public CircleCheckbox(Context context, @Nullable AttributeSet attrs) {
		super(context, attrs);
		initAttr(context, attrs);
	}

	public CircleCheckbox(Context context, @Nullable AttributeSet attrs, int defStyleAttr) {
		super(context, attrs, defStyleAttr);
		initAttr(context, attrs);
	}

	private void initAttr(Context context, @Nullable AttributeSet attrs) {
		TypedArray typedArray = context.obtainStyledAttributes(attrs, R.styleable.CustomCheckbox, 0, 0);
		try {
			strokeWidth = typedArray.getDimension(R.styleable.CustomCheckbox_stroke_width, -1);
			backgroundColor = typedArray.getInt(R.styleable.CustomCheckbox_background_color, 0);
			checkIconColor = typedArray.getColor(R.styleable.CustomCheckbox_check_icon_color, 0);
			initView();
		} finally {
			typedArray.recycle();
		}
	}

	private void initView() {
		initCheckView();
		circleBorderView = new CircleBorderView(this, circleStrokeDegree, getCheckColor(),
				getStrokeWidth(), circleStrokeStartAngle, duration, new CircleBorderView.onCircleBorderAnimListener() {
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

		//Measure Width
		if (widthMode == View.MeasureSpec.EXACTLY) {
			//Must be this size

			width = widthSize;
		} else if (widthMode == View.MeasureSpec.AT_MOST) {
			//Can't be bigger than...
			width = Math.min(DEFAULT_VIEW_SIZE, widthSize);
		} else {
			//Be whatever you want
			width = DEFAULT_VIEW_SIZE;
		}

		//Measure Height
		if (heightMode == View.MeasureSpec.EXACTLY) {
			//Must be this size
			height = heightSize;
		} else if (heightMode == View.MeasureSpec.AT_MOST) {
			//Can't be bigger than...
			height = Math.min(DEFAULT_VIEW_SIZE, heightSize);
		} else {
			//Be whatever you want
			height = DEFAULT_VIEW_SIZE;
		}

		//MUST CALL THIS
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

	public long getDuration() {
		return duration;
	}

	public void setDuration(long duration) {
		this.duration = duration;
	}

	public int getCircleStrokeDegree() {
		return circleStrokeDegree;
	}

	public void setCircleStrokeDegree(int circleStrokeDegree) {
		this.circleStrokeDegree = circleStrokeDegree;
	}

	public int getCircleStrokeStartAngle() {
		return circleStrokeStartAngle;
	}

	public void setCircleStrokeStartAngle(int circleStrokeStartAngle) {
		this.circleStrokeStartAngle = circleStrokeStartAngle;
	}

	public Long getCheckAnimDuration() {
		return checkAnimDuration;
	}

	public void setCheckAnimDuration(Long checkAnimDuration) {
		this.checkAnimDuration = checkAnimDuration;
	}

	public int getCheckIconColor() {
		return checkIconColor;
	}

	public int getBackgroundColor() {
		return backgroundColor;
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

	public interface OnCheckedChangeListener {
		void onCheckedChange(boolean isChecked);
	}
}
