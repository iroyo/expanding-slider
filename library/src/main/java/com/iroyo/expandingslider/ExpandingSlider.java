package com.iroyo.expandingslider;

/**
 * Created by iroyo on 22/8/15.
 */

import android.animation.Animator;
import android.animation.ObjectAnimator;
import android.content.Context;
import android.content.res.TypedArray;
import android.graphics.Canvas;
import android.graphics.Color;
import android.graphics.Paint;
import android.os.Bundle;
import android.os.Parcelable;
import android.util.AttributeSet;
import android.util.Log;
import android.view.MotionEvent;
import android.view.View;
import android.view.animation.AccelerateDecelerateInterpolator;

import java.text.DecimalFormat;

public class ExpandingSlider extends View {

    private Paint pSlider, pIndicator, pBase, pTitle, pValue;

    private int colorBase;
    private int colorMain;
    private int hFactor = 10;
    private float hSlider;

    private float max;
    private float min;
    private float value;

    private int digits;
    private String unit = "";
    private String result = "";
    private float resultSize;
    private int resultColor;
    private DecimalFormat valueFormat;

    private boolean showInitialValue = true;
    private boolean showIndicator = false;
    private boolean isAnimating = false;

    private float position;
    float prevPosition;
    float prevWidthCanvas;

    private String title = "";
    private float titleSize;
    private int titleColor;

    private int widthCanvas;
    private int heightCanvas;

    private SliderListener listener;

    private ObjectAnimator slideUpAnimation, slideDownAnimation;

    public ExpandingSlider(Context context, AttributeSet attrs) {
        super(context, attrs);
        init(attrs);
    }

    public ExpandingSlider(Context context, AttributeSet attrs, int defStyleAttr) {
        super(context, attrs, defStyleAttr);
        init(attrs);
    }

    private void init(AttributeSet attrs) {
        TypedArray a = getContext().obtainStyledAttributes(attrs, R.styleable.ExpandingSlider);

        // GET VALUES FROM XML
        showInitialValue = a.getBoolean(R.styleable.ExpandingSlider_slider_showValue, true);
        showIndicator = a.getBoolean(R.styleable.ExpandingSlider_slider_showIndicator, false);
        resultColor = a.getColor(R.styleable.ExpandingSlider_slider_resultColor, Color.BLACK);
        titleColor = a.getColor(R.styleable.ExpandingSlider_slider_titleColor, Color.BLACK);
        colorBase = a.getColor(R.styleable.ExpandingSlider_slider_colorBase, Color.GRAY);
        colorMain = a.getColor(R.styleable.ExpandingSlider_slider_colorMain, Color.CYAN);
        resultSize = a.getDimension(R.styleable.ExpandingSlider_slider_resultSize, 18f);
        titleSize = a.getDimension(R.styleable.ExpandingSlider_slider_titleSize, 20f);
        digits = a.getInteger(R.styleable.ExpandingSlider_slider_digits, 0);
        unit = a.getString(R.styleable.ExpandingSlider_slider_unit);
        value = a.getFloat(R.styleable.ExpandingSlider_slider_initialValue, 20f);
        title = a.getString(R.styleable.ExpandingSlider_slider_title);
        max = a.getFloat(R.styleable.ExpandingSlider_slider_maxValue, 100f);
        min = a.getFloat(R.styleable.ExpandingSlider_slider_minValue, 0f);

        // ORDERS IS IMPORTANT
        initValueFormat();

        // INITIALIZE CANVAS
        pSlider = new Paint(Paint.ANTI_ALIAS_FLAG);
        pSlider.setStyle(Paint.Style.FILL);
        pSlider.setColor(colorMain);

        pIndicator = new Paint(Paint.ANTI_ALIAS_FLAG);
        pIndicator.setStyle(Paint.Style.FILL);
        pIndicator.setColor(Utils.darken(colorMain, 0.65f));

        pBase = new Paint(Paint.ANTI_ALIAS_FLAG);
        pBase.setStyle(Paint.Style.FILL);
        pBase.setColor(colorBase);

        pTitle = new Paint(Paint.ANTI_ALIAS_FLAG);
        pTitle.setTextAlign(Paint.Align.LEFT);
        pTitle.setStyle(Paint.Style.STROKE);
        pTitle.setColor(titleColor);
        pTitle.setTextSize(titleSize);

        pValue = new Paint(Paint.ANTI_ALIAS_FLAG);
        pValue.setTextAlign(Paint.Align.RIGHT);
        pValue.setStyle(Paint.Style.STROKE);
        pValue.setColor(resultColor);
        pValue.setTextSize(resultSize);



        a.recycle();
    }

    @Override
    public Parcelable onSaveInstanceState() {
        Bundle bundle = new Bundle();
        bundle.putParcelable("instanceState", super.onSaveInstanceState());
        bundle.putFloat("position", this.position);
        bundle.putFloat("width", this.widthCanvas);
        return bundle;
    }

    @Override
    public void onRestoreInstanceState(Parcelable state) {
        if (state instanceof Bundle) {
            Bundle bundle = (Bundle) state;
            prevPosition = bundle.getFloat("position");
            prevWidthCanvas = bundle.getFloat("width");
            state = bundle.getParcelable("instanceState");
        }
        super.onRestoreInstanceState(state);
    }

    @Override
    protected void onSizeChanged(int w, int h, int oldw, int oldh) {
        this.widthCanvas = w;
        this.heightCanvas = h;
        this.hSlider = h - (h * hFactor) / 100;
        this.position = (w * prevPosition) / prevWidthCanvas;
        updateResult();
        initAnimation();
        super.onSizeChanged(w, h, oldw, oldh);
    }

    @Override
    protected void onDraw(Canvas canvas) {
        super.onDraw(canvas);
        drawBase(canvas);
        drawSlider(canvas);
        if (showInitialValue) drawValue(canvas);
        if (showIndicator && !isAnimating) drawIndicator(canvas);
        if (title != null) drawTitle(canvas);
    }

    //DRAWS ----------------------------------------------------------------------------------------

    private void drawSlider(Canvas c) {
        c.drawRect(0, hSlider, position, heightCanvas, pSlider);
    }

    private void drawIndicator(Canvas c) {
        c.drawRect(position - 12, hSlider - 6, position, heightCanvas, pIndicator);
    }

    private void drawBase(Canvas c) {
        c.drawRect(0, hSlider, widthCanvas, heightCanvas, pBase);
    }

    private void drawValue(Canvas c) {
        c.drawText(result + " " + unit, widthCanvas - 25, (heightCanvas / 2) + (resultSize / 2), pValue);
    }

    private void drawTitle(Canvas c) {
        c.drawText(title, 40, (heightCanvas / 2) + (titleSize / 2), pTitle);
    }

    // SETTERS & GETTERS ---------------------------------------------------------------------------

    public void setColorBase(int colorBase) {
        this.colorBase = colorBase;
    }

    public void setColorMain(int colorMain) {
        this.colorMain = colorMain;
    }

    public void setValue(float value) {
        this.value = value;
        updateResult();
    }

    public void setMax(float max) {
        this.max = max;
    }

    public void setMin(float min) {
        this.min = min;
    }

    public void setTitle(String title) {
        this.title = title;
    }

    public void setTitleSize(float titleSize) {
        this.titleSize = titleSize;
    }

    public void setTitleColor(int titleColor) {
        this.titleColor = titleColor;
    }

    public void setShowIndicator(boolean showIndicator) {
        this.showIndicator = showIndicator;
    }

    public void setUnit(String unit) {
        this.unit = unit;
    }

    public void setDigits(int digits) {
        this.digits = digits;
        initValueFormat();
    }

    public void setListener(SliderListener listener) {
        this.listener = listener;
    }

    public void setShowInitialValue(boolean showInitialValue) {
        this.showInitialValue = showInitialValue;
    }

    public float getHeightSlider() {
        return hSlider;
    }

    public void setHeightSlider(float hSlider) {
        this.hSlider = hSlider;
        invalidate();
    }

    private void updateResult() {
        this.result = valueFormat.format(value);
        if(listener != null) listener.onValueChanged(value, this);

        float absolute = (value * 100) / (max - min);
        float percentage = (min > 0) ? absolute - max : absolute;
        position = (percentage * widthCanvas) / 100 ;
        invalidate();
    }

    private void initValueFormat() {
        StringBuilder b = new StringBuilder();
        for (int i = 0; i < digits; i++) {
            if (i == 0) b.append(".");
            b.append("0");
        }
        valueFormat = new DecimalFormat("###,###,###,##0" + b.toString());
    }

    private void initAnimation() {

        slideUpAnimation = ObjectAnimator.ofFloat(this, "heightSlider", 0).setDuration(350);
        slideUpAnimation.setInterpolator(new AccelerateDecelerateInterpolator());
        slideUpAnimation.addListener(new Animator.AnimatorListener() {
            @Override
            public void onAnimationStart(Animator animation) {
                isAnimating = true;
            }

            @Override
            public void onAnimationEnd(Animator animation) { }

            @Override
            public void onAnimationCancel(Animator animation) { }

            @Override
            public void onAnimationRepeat(Animator animation) { }
        });

        slideDownAnimation = ObjectAnimator.ofFloat(this, "heightSlider", hSlider).setDuration(650);
        slideDownAnimation.setInterpolator(new AccelerateDecelerateInterpolator());
        slideDownAnimation.addListener(new Animator.AnimatorListener() {
            @Override
            public void onAnimationStart(Animator animation) { }

            @Override
            public void onAnimationEnd(Animator animation) {
                isAnimating = false;
            }

            @Override
            public void onAnimationCancel(Animator animation) {
                isAnimating = false;
            }

            @Override
            public void onAnimationRepeat(Animator animation) { }
        });
    }

    @Override
    public boolean onTouchEvent(MotionEvent e) {

        switch (e.getAction()) {
            case MotionEvent.ACTION_DOWN:
                slideUpAnimation.start();
                break;
            case MotionEvent.ACTION_UP:
                slideUpAnimation.cancel();
                slideDownAnimation.start();
                break;
            case MotionEvent.ACTION_MOVE:
                updateValue(e.getX());
                break;
            default:
                break;
        }
        invalidate();
        return true;
    }

    private void updateValue(float x){
        if (x >= 0 && x <= widthCanvas) {
            position = x;
            float percentage = (position * 100) / widthCanvas;
            float absolute = (percentage * (max - min)) / 100;
            value = (min > 0) ? absolute + min : absolute;
            result = valueFormat.format(value);
            if(listener != null) listener.onValueChanged(value, this);
        }
    }

    //INTERFACE
    public interface SliderListener {
        void onValueChanged(float value, View v);
    }

    // UTILITY CLASS
    public static abstract class Utils {

        public static int darken(int color, float factor) {
            int a = Color.alpha(color);
            int r = Color.red(color);
            int g = Color.green(color);
            int b = Color.blue(color);

            return Color.argb(a,
                    Math.max((int) (r * factor), 0),
                    Math.max((int) (g * factor), 0),
                    Math.max((int) (b * factor), 0));
        }
    }

}