package com.iroyo.expandingslider.sample;

import android.app.Activity;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.TextView;

import com.iroyo.expandingslider.HorizontalExpandingSlider;

public class MainActivity extends Activity implements HorizontalExpandingSlider.SliderListener {

    private TextView result1;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        result1 = (TextView) findViewById(R.id.result1);
        final HorizontalExpandingSlider slider1 = (HorizontalExpandingSlider) findViewById(R.id.slider1);
        HorizontalExpandingSlider slider2 = (HorizontalExpandingSlider) findViewById(R.id.slider2);
        HorizontalExpandingSlider slider3 = (HorizontalExpandingSlider) findViewById(R.id.slider3);
        slider1.setListener(this);
        slider2.setListener(this);
        slider3.setListener(this);

    }

    @Override
    public void onValueChanged(float value, View v) {
        if (v.getId() == R.id.slider1) result1.setText("VALUE: " + value);
    }
}
