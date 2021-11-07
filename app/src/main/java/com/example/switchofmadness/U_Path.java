package com.example.switchofmadness;

import android.graphics.Path;
import android.graphics.PointF;

import java.util.ArrayList;

public class U_Path {

    public static Path getCurvePath(ArrayList<PointF> pathPoints) {
        ArrayList<PointF> conPoint1 = new ArrayList<>();
        ArrayList<PointF> conPoint2 = new ArrayList<>();
        for (int i=1; i<pathPoints.size(); i++) {
            conPoint1.add(new PointF((pathPoints.get(i).x + pathPoints.get(i-1).x) / 2, pathPoints.get(i-1).y));
            conPoint2.add(new PointF((pathPoints.get(i).x + pathPoints.get(i-1).x) / 2, pathPoints.get(i).y));
        }

        Path path = new Path();
        path.moveTo(pathPoints.get(0).x, pathPoints.get(0).y);
        for (int i=1; i<pathPoints.size(); i++) {
            path.cubicTo(
                    conPoint1.get(i-1).x, conPoint1.get(i-1).y, conPoint2.get(i-1).x, conPoint2.get(i-1).y,
                    pathPoints.get(i).x, pathPoints.get(i).y
            );
        }
        return path;
    }
}
