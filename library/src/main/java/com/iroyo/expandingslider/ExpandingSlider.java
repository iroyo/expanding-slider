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
import android.view.MotionEvent;
import android.view.View;

public class ExpandingSlider extends View {

    private Paint pSlider, pBase;

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

        colorBase = a.getColor(R.styleable.ExpandingSlider_slider_colorBase, Color.GRAY);
        colorMain = a.getColor(R.styleable.ExpandingSlider_slider_colorMain, Color.CYAN);
        value = a.getFloat(R.styleable.ExpandingSlider_slider_initialValue, 20f);
        max = a.getFloat(R.styleable.ExpandingSlider_slider_maxValue, 100f);
        min = a.getFloat(R.styleable.ExpandingSlider_slider_minValue, 0f);


        pSlider = new Paint(Paint.ANTI_ALIAS_FLAG);
        pSlider.setStyle(Paint.Style.FILL);
        pSlider.setColor(colorMain);

        pBase = new Paint(Paint.ANTI_ALIAS_FLAG);
        pBase.setStyle(Paint.Style.FILL);
        pBase.setColor(colorBase);

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
    }

    //DRAWS ----------------------------------------------------------------------------------------

    private void drawSlider(Canvas c) {
        c.drawRect(0, hSlider, position, heightCanvas, pSlider);
    }

    private void drawBase(Canvas c) {
        c.drawRect(0, hSlider, widthCanvas, heightCanvas, pBase);
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
                position = e.getX();
                break;
            default:
                break;
        }
        invalidate();
        return true;
    }


}