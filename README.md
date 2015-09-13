# EXPANDING-SLIDER

Unique slider with organic animation and expanding effect.

![Expanding Slider examples](https://raw.githubusercontent.com/iroyo/expanding-slider/master/images/example.gif)

Usage
=====
Just add the dependency to your `build.gradle`:

```groovy
dependencies {
    compile 'com.iroyoraso.expanding-slider:library:0.0.4'
}
```

**SYNTAX**
```xml
<com.iroyo.expandingslider.HorizontalExpandingSlider
    android:layout_width="match_parent"
    android:layout_height="50dp"
    app:slider_decimals="int"
    app:slider_stepSize="float"
    app:slider_maxValue="float"
    app:slider_minValue="float"
    app:slider_initialValue="float"
    app:slider_colorBase="color"
    app:slider_colorMain="color"
    app:slider_marginLeft="dimen"
    app:slider_marginRight="dimen"
    app:slider_resultColor="color"
    app:slider_resultSize="dimen"
    app:slider_showIndicator="bool"
    app:slider_title="string"
    app:slider_titleColor="color"
    app:slider_titleSize="dimen"
    app:slider_unit="string" />
```
