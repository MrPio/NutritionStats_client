package it.univpm.nutritionstats.utility.mathematics;

import android.graphics.Point;

public class Circle {
    float radius;
    Point center;

    public Circle(float radius, Point center) {
        this.radius = radius;
        this.center = center;
    }
    public Circle(Point center) {
        this.radius = 0f;
        this.center = center;
    }

    public Point getPointFromAngle(float angleDegree){
        float angleRadiants=angleDegree/180*(float)Math.PI;
        return new Point((int)(Math.cos(angleRadiants)*radius),(int)(Math.sin(angleRadiants)*radius));
    }

    public float getAngleByPoint(Point point){
        float radius=(float)Math.sqrt(Math.pow(center.x-point.x, 2)+Math.pow(center.y-point.y, 2));
        if(point.y>center.y)
            return -(float)(Math.acos((+point.x-center.x)/radius)/Math.PI*180f);
        return (float)(Math.acos((+point.x-center.x)/radius)/Math.PI*180f);
    }

    public float getDistaceFromCenter(Point point){
        return (float)Math.sqrt(Math.pow(center.x-point.x, 2)+Math.pow(center.y-point.y, 2));
    }
}
