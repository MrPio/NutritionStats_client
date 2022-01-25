package it.univpm.nutritionstats.utility.mathematics;

import com.github.mikephil.charting.animation.Easing;

public class EasingFunctionSine {
    public static float delay;

    public EasingFunctionSine() {
        delay=0f;
    }

    public EasingFunctionSine(float delay) {
        EasingFunctionSine.delay = delay;
    }

    public static final Easing.EasingFunction EaseOutSineDelay = new Easing.EasingFunction() {
        public float getInterpolation(float input) {
            return (float) Math.sin((input*(1-EasingFunctionSine.delay)+EasingFunctionSine.delay) *(Math.PI / 2f));
        }
    };
}
