package com.iroyo.expandingslider.sample;

import android.app.Activity;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.TextView;

import com.iroyo.expandingslider.HorizontalExpandingSlider;

public class MainActivity extends Activity implements HorizontalExpandingSlider.SliderListener {

    private TextView result1;
    private HorizontalExpandingSlider slider1, slider2, slider3;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        result1 = (TextView) findViewById(R.id.result1);
        slider1 = (HorizontalExpandingSlider) findViewById(R.id.slider1);
        slider2 = (HorizontalExpandingSlider) findViewById(R.id.slider2);
        slider3 = (HorizontalExpandingSlider) findViewById(R.id.slider3);
        slider1.setListener(this);
        slider2.setListener(this);
        slider3.setListener(this);

    }

    @Override
    public void onValueChanged(float value, View v) {
        if (v.getId() == R.id.slider1) {
            showInfo(slider1);
            result1.setText("VALUE: " + value);
        }
        if (v.getId() == R.id.slider2) showInfo(slider2);
        if (v.getId() == R.id.slider3) showInfo(slider3);

    }

    private void showInfo(HorizontalExpandingSlider slider) {
        Log.d("ISAAC",  "MAX: " + slider.getMax() + " " +
                        "MIN: " + slider.getMin() + " " +
                        "VALUE: " + slider.getValue() + " " +
                        "DIGITS: " + slider.getMaxDigits());
    }
}
