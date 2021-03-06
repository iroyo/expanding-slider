package com.iroyo.expandingslider;

/**
 * Created by iroyo on 22/8/15.
 * Library
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
import android.view.MotionEvent;
import android.view.View;
import android.view.animation.DecelerateInterpolator;
import android.view.animation.LinearInterpolator;

import java.text.DecimalFormat;

public class HorizontalExpandingSlider extends View {

    private Paint pSlider, pIndicator, pBase, pTitle, pValue;

    private int hFactor = 10;
    private float hSlider;
    private float marginLeft, marginRight;

    private float max;
    private float min;
    private float value;
    private float stepSize;
    private int decimals;

    private String unit = "";
    private String result = "";
    private float resultSize;
    private DecimalFormat valueFormat;

    private boolean showAnimation = true;
    private boolean showInitialValue = true;
    private boolean showIndicator = false;
    private boolean isAnimating = false;

    private float position;
    private float prevPosition;
    private float prevWidthCanvas;

    private String title = "";
    private float titleSize;

    private int widthCanvas;
    private int heightCanvas;

    private SliderListener listener;

    private ObjectAnimator slideUpAnimation, slideDownAnimation;

    public HorizontalExpandingSlider(Context context, AttributeSet attrs) {
        super(context, attrs);
        init(attrs);
    }

    public HorizontalExpandingSlider(Context context, AttributeSet attrs, int defStyleAttr) {
        super(context, attrs, defStyleAttr);
        init(attrs);
    }

    private void init(AttributeSet attrs) {
        TypedArray a = getContext().obtainStyledAttributes(attrs, R.styleable.ExpandingSlider);

        // GET VALUES FROM XML
        showAnimation = a.getBoolean(R.styleable.ExpandingSlider_slider_showAnimation, true);
        showInitialValue = a.getBoolean(R.styleable.ExpandingSlider_slider_showValue, true);
        showIndicator = a.getBoolean(R.styleable.ExpandingSlider_slider_showIndicator, false);
        stepSize = a.getFloat(R.styleable.ExpandingSlider_slider_stepSize, 0.5f);
        decimals = a.getInteger(R.styleable.ExpandingSlider_slider_decimals, 0);
        marginLeft = a.getDimension(R.styleable.ExpandingSlider_slider_marginLeft, 12);
        marginRight = a.getDimension(R.styleable.ExpandingSlider_slider_marginRight, 12);
        resultSize = a.getDimension(R.styleable.ExpandingSlider_slider_resultSize, 18f);
        titleSize = a.getDimension(R.styleable.ExpandingSlider_slider_titleSize, 20f);
        title = a.getString(R.styleable.ExpandingSlider_slider_title);
        value = a.getFloat(R.styleable.ExpandingSlider_slider_initialValue, 20f);
        unit = a.getString(R.styleable.ExpandingSlider_slider_unit);
        max = a.getFloat(R.styleable.ExpandingSlider_slider_maxValue, 100f);
        min = a.getFloat(R.styleable.ExpandingSlider_slider_minValue, 0f);

        int resultColor = a.getColor(R.styleable.ExpandingSlider_slider_resultColor, Color.BLACK);
        int titleColor = a.getColor(R.styleable.ExpandingSlider_slider_titleColor, Color.BLACK);
        int colorBase = a.getColor(R.styleable.ExpandingSlider_slider_colorBase, Color.GRAY);
        int colorMain = a.getColor(R.styleable.ExpandingSlider_slider_colorMain, Color.CYAN);

        // ORDERS IS IMPORTANT
        initValueFormat(decimals);

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
        c.drawRect(position - 6, hSlider - 6, position + 6, heightCanvas, pIndicator);
    }

    private void drawBase(Canvas c) {
        c.drawRect(0, hSlider, widthCanvas, heightCanvas, pBase);
    }

    private void drawValue(Canvas c) {
        c.drawText(result + " " + unit, widthCanvas - marginRight, (heightCanvas / 2) + (resultSize / 2), pValue);
    }

    private void drawTitle(Canvas c) {
        c.drawText(title, marginLeft, (heightCanvas / 2) + (titleSize / 2), pTitle);
    }

    // SETTERS & GETTERS ---------------------------------------------------------------------------

    public void setColorBase(int colorBase) {
        this.pBase.setColor(colorBase);
    }

    public void setColorMain(int colorMain) {
        this.pSlider.setColor(colorMain);
    }

    public void setValue(float value) {
        if (max >= value && value >= min) {
            this.value = value;
            updateResult();
        }
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
        this.pTitle.setTextSize(titleSize);
    }

    public void setTitleColor(int titleColor) {
        this.pTitle.setColor(titleColor);
    }

    public void setResultSize(float resultSize) {
        this.pValue.setTextSize(resultSize);
    }

    public void setResultColor(int resultColor) {
        this.pValue.setColor(resultColor);
    }

    public void setShowIndicator(boolean showIndicator) {
        this.showIndicator = showIndicator;
    }

    public void setUnit(String unit) {
        this.unit = unit;
    }

    public void setDecimals(int decimals) {
        this.decimals = decimals;
        initValueFormat(decimals);
    }

    public void setListener(SliderListener listener) {
        this.listener = listener;
    }

    public void setShowInitialValue(boolean showInitialValue) {
        this.showInitialValue = showInitialValue;
    }

    public void setHeightSlider(float hSlider) {
        this.hSlider = hSlider;
        invalidate();
    }

    public float getHeightSlider() {
        return hSlider;
    }

    public float getMax() {
        return max;
    }

    public float getMin() {
        return min;
    }

    public float getValue() {
        return value;
    }

    public int getMaxDigits() {
        int integer = (int) max;
        return String.valueOf(integer).length();
    }

    public int getDecimals() {
        return decimals;
    }

    private void updateResult() {
        this.result = valueFormat.format(value);
        if(listener != null) listener.onValueChanged(value, this);

        float absolute = (value * 100) / (max - min);
        float percentage = (min > 0) ? absolute - max : absolute;
        position = (percentage * widthCanvas) / 100 ;
        invalidate();
    }

    private void initValueFormat(int digits) {
        StringBuilder b = new StringBuilder();
        for (int i = 0; i < digits; i++) {
            if (i == 0) b.append(".");
            b.append("0");
        }
        valueFormat = new DecimalFormat("###,###,###,##0" + b.toString());
    }

    private void initAnimation() {

        slideUpAnimation = ObjectAnimator.ofFloat(this, "heightSlider", 0).setDuration(50);
        slideUpAnimation.setInterpolator(new LinearInterpolator());
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

        slideDownAnimation = ObjectAnimator.ofFloat(this, "heightSlider", hSlider).setDuration(250);
        slideDownAnimation.setInterpolator(new DecelerateInterpolator());
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
        float x = e.getX();
        switch (e.getAction()) {
            case MotionEvent.ACTION_DOWN:
                if (showAnimation) slideUpAnimation.start();
                else hSlider = 0;
                break;
            case MotionEvent.ACTION_UP:
                if (showAnimation) slideUpAnimation.cancel();
                if (showAnimation) slideDownAnimation.start();
                else hSlider = heightCanvas - (heightCanvas * hFactor) / 100;
                if(x != position) updateValue(x);
                break;
            case MotionEvent.ACTION_MOVE:
                if(x != position) updateValue(x);
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
            float total = absolute + min;

            float remainder = total % stepSize;
            if (remainder <= stepSize / 2f) value = total - remainder;
            else value = total - remainder + stepSize;

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