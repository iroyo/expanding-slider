package com.iroyo.expandingslider.sample;

import android.app.Activity;
import android.graphics.Color;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.util.Log;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.TextView;

import com.iroyo.expandingslider.ExpandingSlider;

public class MainActivity extends Activity implements ExpandingSlider.SliderListener {

    TextView result1, result2, result3;
    ExpandingSlider slider1, slider2, slider3;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        result1 = (TextView) findViewById(R.id.result1);
        slider1 = (ExpandingSlider) findViewById(R.id.slider1);
        slider2 = (ExpandingSlider) findViewById(R.id.slider2);
        slider3 = (ExpandingSlider) findViewById(R.id.slider3);
        slider1.setListener(this);
        slider2.setListener(this);
        slider3.setListener(this);

    }

    @Override
    public void onValueChanged(float value, View v) {
        if (v.getId() == R.id.slider1) result1.setText("VALUE: " + value);
    }
}
