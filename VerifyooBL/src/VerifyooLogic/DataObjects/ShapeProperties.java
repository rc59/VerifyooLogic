package VerifyooLogic.DataObjects;

import VerifyooLogic.UserProfile.MotionEventCompact;
import VerifyooLogic.UserProfile.MotionEventExtremePoint;
import VerifyooLogic.Utils.Consts;
import VerifyooLogic.Utils.GrahamScan;

import java.awt.Point;
import java.util.ArrayList;
import java.util.Collections;
import java.util.Comparator;
import java.util.HashMap;
import java.util.List;

/**
 * Created by roy on 2/21/2016.
 */
public class ShapeProperties {

    public static final int EXTREME_POINT_TYPE_START = 0;
    public static final int EXTREME_POINT_TYPE_END = 1;
    public static final int EXTREME_POINT_TYPE_MIN_X = 2;
    public static final int EXTREME_POINT_TYPE_MAX_X = 3;
    public static final int EXTREME_POINT_TYPE_MIN_Y = 4;
    public static final int EXTREME_POINT_TYPE_MAX_Y = 5;

    public HashMap<String, Boolean> HashPoints;

    public ArrayList<MotionEventCompact> ListEvents;
    public ArrayList<MotionEventExtremePoint> ListEventsExtremePoints;

    public MotionEventExtremePoint MinX;
    public MotionEventExtremePoint MinY;
    public MotionEventExtremePoint MaxX;
    public MotionEventExtremePoint MaxY;

    public MotionEventExtremePoint ShapeStart;
    public MotionEventExtremePoint ShapeEnd;

    public double AvgExtremePointsX;
    public double AvgExtremePointsY;

    public double DeltaXExtremePoints;
    public double DeltaYExtremePoints;

    public double ShapeTimeInterval;

    public double ShapeLength;

    public ShapeProperties() {

    }

    public ShapeProperties(ArrayList<MotionEventCompact> listEvents, MotionEventCompact minX, MotionEventCompact maxX, MotionEventCompact minY, MotionEventCompact maxY, double shapeLength) {
        ListEvents = listEvents;

        HashPoints = new HashMap<>();

        MinX = new MotionEventExtremePoint(minX);
        MinY = new MotionEventExtremePoint(minY);
        MaxX = new MotionEventExtremePoint(maxX);
        MaxY = new MotionEventExtremePoint(maxY);

        DeltaXExtremePoints = MaxX.Xmm - MinX.Xmm;
        DeltaYExtremePoints = MaxY.Ymm - MinY.Ymm;

        ShapeLength = shapeLength;

        InitExtremePoints();
        InitAdjustedPoints();
    }

    protected void InitExtremePoints() {
        ListEventsExtremePoints = new ArrayList<MotionEventExtremePoint>();
        ListEventsExtremePoints.add(new MotionEventExtremePoint(MinX));
        ListEventsExtremePoints.add(new MotionEventExtremePoint(MaxX));
        ListEventsExtremePoints.add(new MotionEventExtremePoint(MinY));
        ListEventsExtremePoints.add(new MotionEventExtremePoint(MaxY));
        ListEventsExtremePoints.add(new MotionEventExtremePoint(ListEvents.get(0)));
        ListEventsExtremePoints.add(new MotionEventExtremePoint(ListEvents.get(ListEvents.size() - 1)));

        //CalculateGrahamScan();

        Collections.sort(ListEventsExtremePoints, new Comparator<MotionEventCompact>() {
            @Override
            public int compare(MotionEventCompact point1, MotionEventCompact point2) {
                if (point1.EventTime > point2.EventTime) {
                    return 1;
                }
                if (point1.EventTime < point2.EventTime) {
                    return -1;
                }
                return 0;
            }
        });

        AvgExtremePointsX = 0;
        AvgExtremePointsY = 0;

        ShapeStart = ListEventsExtremePoints.get(0);
        ShapeEnd = ListEventsExtremePoints.get(ListEventsExtremePoints.size() - 1);

        for (int idxEvent = 1; idxEvent < ListEventsExtremePoints.size() - 1; idxEvent++) {
            ListEventsExtremePoints.get(idxEvent).RelativeEventTime = ListEventsExtremePoints.get(idxEvent).EventTime - ShapeStart.EventTime;
            AvgExtremePointsX += ListEventsExtremePoints.get(idxEvent).Xmm;
            AvgExtremePointsY += ListEventsExtremePoints.get(idxEvent).Ymm;
        }

        AvgExtremePointsX = AvgExtremePointsX / ListEventsExtremePoints.size();
        AvgExtremePointsY = AvgExtremePointsY / ListEventsExtremePoints.size();

        ShapeTimeInterval = ShapeEnd.EventTime - ShapeStart.EventTime;
    }

    protected void InitAdjustedPoints() {
        MotionEventExtremePoint tempEvent;
        String pointStr;

        for (int idxEvent = 0; idxEvent < ListEventsExtremePoints.size(); idxEvent++) {
            tempEvent = ListEventsExtremePoints.get(idxEvent);
            tempEvent.AdjustedX = tempEvent.Xmm - AvgExtremePointsX;
            tempEvent.AdjustedY = tempEvent.Ymm - AvgExtremePointsY;

            tempEvent.Angle = (Math.atan2(tempEvent.AdjustedY, tempEvent.AdjustedX) * 180 / Consts.PI + 360) % 360;

            pointStr = PointToString(tempEvent);
            if (!HashPoints.containsKey(pointStr)) {
                HashPoints.put(pointStr, true);
            }
        }
    }

    protected double GetStrokeOctagonArea() {

        double area = 0;

        ArrayList<MotionEventExtremePoint> octagon = ListEventsExtremePoints;

        return area;
    }

    protected String PointToString(MotionEventExtremePoint point) {
        return String.format("%s-%s", String.valueOf(point.AdjustedX), String.valueOf(point.AdjustedY));
    }

    protected void CalculateGrahamScan() {
        int[] xs = {3, 5, -1, 8, -6, 23, 4};

        int[] ys = {9, 2, -4, 3, 90, 3, -11};

        List<Point> convexHull = GrahamScan.getConvexHull(xs, ys);
    }
}
