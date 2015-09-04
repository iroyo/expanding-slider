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

    TextView result;
    ExpandingSlider slider;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        result = (TextView) findViewById(R.id.result);
        result.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                slider.setValue(55);
            }
        });
        slider = (ExpandingSlider) findViewById(R.id.slider);
        slider.setListener(this);

    }

    @Override
    public void onValueChanged(float value, View v) {
        result.setText("VALUE: " + value);
    }
}
