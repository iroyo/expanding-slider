package com.iroyo.expandingslider;

/**
 * Created by iroyo on 22/8/15.
 */

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

public class ExpandingSlider extends View {

    private Paint pSlider, pIndicator, pBase, pTitle, pValue;

    private int colorBase;
    private int colorMain;
    private int hFactor = 10;
    private int hSlider;

    private float max;
    private float min;
    private float value;

    private float position;
    float prevPosition;
    float prevWidthCanvas;

    private boolean showIndicator = false;

    private String title;
    private float titleSize;
    private int titleColor;


    private int widthCanvas;
    private int heightCanvas;


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
        showIndicator = a.getBoolean(R.styleable.ExpandingSlider_slider_showIndicator, false);
        titleColor = a.getColor(R.styleable.ExpandingSlider_slider_titleColor, Color.BLACK);
        colorBase = a.getColor(R.styleable.ExpandingSlider_slider_colorBase, Color.GRAY);
        colorMain = a.getColor(R.styleable.ExpandingSlider_slider_colorMain, Color.CYAN);
        titleSize = a.getDimension(R.styleable.ExpandingSlider_slider_titleSize, 20f);
        value = a.getFloat(R.styleable.ExpandingSlider_slider_initialValue, 20f);
        title = a.getString(R.styleable.ExpandingSlider_slider_title);
        max = a.getFloat(R.styleable.ExpandingSlider_slider_maxValue, 100f);
        min = a.getFloat(R.styleable.ExpandingSlider_slider_minValue, 0f);

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
        super.onSizeChanged(w, h, oldw, oldh);
    }

    @Override
    protected void onDraw(Canvas canvas) {
        super.onDraw(canvas);
        drawBase(canvas);
        drawSlider(canvas);
        if (showIndicator) drawIndicator(canvas);
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

    private void drawTitle(Canvas c) {
        c.drawText(title, 50, (heightCanvas / 2) + (titleSize / 2), pTitle);
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
    }

    public void setMax(float max) {
        this.max = max;
    }

    public void setMin(float min) {
        this.min = min;
    }

    public void setpSlider(Paint pSlider) {
        this.pSlider = pSlider;
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

    // ---------------------------------------------------------------------------------------------

    @Override
    public boolean onTouchEvent(MotionEvent e) {
        switch (e.getAction()) {
            case MotionEvent.ACTION_DOWN:
                hSlider = 0;
                break;
            case MotionEvent.ACTION_UP:
                hSlider = heightCanvas - (heightCanvas * hFactor) / 100;
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
            float result = (percentage * (max - min)) / 100;
            float value = (min > 0) ? result + min : result;
            Log.d("ISAAC", value + " UNITATS");
        }
    }

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