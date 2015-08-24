package com.iroyo.expandingslider.sample;

import android.app.Activity;
import android.graphics.Color;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.view.Menu;
import android.view.MenuItem;

import com.iroyo.expandingslider.ExpandingSlider;

public class MainActivity extends Activity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        ExpandingSlider slider = (ExpandingSlider) findViewById(R.id.slider);

    }

}
