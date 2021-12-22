package it.univpm.nutritionstats.utility;

import android.graphics.Point;
import android.view.View;

public class Utilities {
    public static Point getViewPointLocation(View view){
        int[] viewPoint = new int[2];
        view.getLocationOnScreen(viewPoint);
        return new Point(viewPoint[0],viewPoint[1]);
    }
}
