package VerifyooLogic.UserProfile;

import java.util.ArrayList;
import java.util.HashMap;

import VerifyooLogic.Statistics.Interfaces.IBaseParam;
import VerifyooLogic.Utils.Consts;
import VerifyooLogic.Utils.ConstsParams;
import VerifyooLogic.Utils.Line2D;
import VerifyooLogic.Utils.UtilsCalc;
import VerifyooLogic.Utils.UtilsData;

public class CompactGesture {
	public String Instruction;
    public HashMap<String, IBaseParam> HashParameters;

    public ArrayList<Stroke> ListStrokes;

    public double UniqueFactor;

    public double TimeInterval;

    public double Width;
    public double Height;
    public double BoundingBox;

    public double StrokePauseAvg;
    public double StrokePauseMax;

    public double StrokeDistanceAvg;
    public double StrokeDistanceMax;
    
    public  CompactGesture() {

    }

    public CompactGesture(ArrayList<Stroke> listStrokes) {
        HashParameters = new HashMap<>();
        ListStrokes = listStrokes;
    }

    public void InitParams() {

        HashParameters = new HashMap<>();

        double minX = 0;
        double maxX = 0;
        double minY = 0;
        double maxY = 0;

        TimeInterval = 0;

        double tempDeltaX, tempDeltaY, tempDelta;
        double deltaTimeInterval;

        Stroke tempStroke, tempStrokePrev;

        double strokeBoundingBoxPropAvg = 0;
        double strokeBoundingBoxPropMax = 0;
        double tempStrokeBoundingBoxProp;

        ArrayList<MotionEventCompact> listGestureEvents = new ArrayList<>();

        if (ListStrokes.size() > 1) {
            for (int idxStroke = 0; idxStroke < ListStrokes.size(); idxStroke++) {
                tempStroke = ListStrokes.get(idxStroke);
                tempStroke.InitParams();

                if (tempStroke.Length > 50) {
                    if (idxStroke == 0) {
                        minX = tempStroke.PointMinX.Xmm;
                        maxX = tempStroke.PointMaxX.Xmm;
                        minY = tempStroke.PointMinY.Ymm;
                        maxY = tempStroke.PointMaxY.Ymm;
                    } else {
                        tempStrokePrev = ListStrokes.get(idxStroke - 1);

                        minX = UtilsCalc.GetMinValue(minX, tempStroke.PointMinX.Xmm);
                        maxX = UtilsCalc.GetMaxValue(maxX, tempStroke.PointMaxX.Xmm);
                        minY = UtilsCalc.GetMinValue(minY, tempStroke.PointMinY.Ymm);
                        maxY = UtilsCalc.GetMaxValue(maxY, tempStroke.PointMaxY.Ymm);

                        tempDeltaX = tempStroke.StartX - tempStrokePrev.EndX;
                        tempDeltaY = tempStroke.StartY - tempStrokePrev.EndY;
                        tempDelta = UtilsCalc.CalcPitagoras(tempDeltaX, tempDeltaY);

                        StrokeDistanceAvg += tempDelta;
                        StrokeDistanceMax = UtilsCalc.GetMaxValue(StrokeDistanceMax, tempDelta);

                        deltaTimeInterval = tempStroke.StartTime - tempStrokePrev.EndTime;

                        StrokePauseAvg += deltaTimeInterval;
                        StrokePauseMax = UtilsCalc.GetMaxValue(StrokePauseMax, deltaTimeInterval);

                        tempStrokeBoundingBoxProp = UtilsCalc.GetPercentageDiff(tempStroke.BoundingBox, tempStrokePrev.BoundingBox);

                        strokeBoundingBoxPropAvg += tempStrokeBoundingBoxProp;
                        strokeBoundingBoxPropMax = UtilsCalc.GetMaxValue(tempStrokeBoundingBoxProp, strokeBoundingBoxPropMax);

                        listGestureEvents.addAll(tempStroke.ListEvents);
                    }
                }

                TimeInterval += tempStroke.TimeInterval;
            }

            double numOfElements = (ListStrokes.size() - 1);

            StrokeDistanceAvg = StrokeDistanceAvg / numOfElements;
            StrokePauseAvg = StrokePauseAvg / numOfElements;
            strokeBoundingBoxPropAvg = strokeBoundingBoxPropAvg / numOfElements;

            Width = maxX - minX;
            Height = maxY - minY;
            BoundingBox = Width * Height;

            UtilsData.AddNumericalParameter(HashParameters, ConstsParams.Gesture.WIDTH, Width, Consts.WEIGHT_HIGH);
            UtilsData.AddNumericalParameter(HashParameters, ConstsParams.Gesture.HEIGHT, Height, Consts.WEIGHT_HIGH);
            UtilsData.AddNumericalParameter(HashParameters, ConstsParams.Gesture.BOUNDING_BOX, BoundingBox, Consts.WEIGHT_HIGH);

            UtilsData.AddNumericalParameter(HashParameters, ConstsParams.Gesture.TIME_INTERVAL, TimeInterval, Consts.WEIGHT_HIGH);

            UtilsData.AddNumericalParameter(HashParameters, ConstsParams.Gesture.STROKE_DISTANCE_AVG, StrokeDistanceAvg, Consts.WEIGHT_HIGH);
            UtilsData.AddNumericalParameter(HashParameters, ConstsParams.Gesture.STROKE_DISTANCE_MAX, StrokeDistanceMax, Consts.WEIGHT_HIGH);

            UtilsData.AddNumericalParameter(HashParameters, ConstsParams.Gesture.STROKE_PAUSE_AVG, StrokePauseAvg, Consts.WEIGHT_HIGH);
            UtilsData.AddNumericalParameter(HashParameters, ConstsParams.Gesture.STROKE_PAUSE_MAX, StrokePauseMax, Consts.WEIGHT_HIGH);

            UtilsData.AddNumericalParameter(HashParameters, ConstsParams.Gesture.STROKE_BOUNDING_BOX_PROP_AVG, strokeBoundingBoxPropAvg, Consts.WEIGHT_HIGH);
            UtilsData.AddNumericalParameter(HashParameters, ConstsParams.Gesture.STROKE_BOUNDING_BOX_PROP_MAX, strokeBoundingBoxPropMax, Consts.WEIGHT_HIGH);

            double numIntersections = GetNumberOfIntersections(listGestureEvents);
            UtilsData.AddNumericalParameter(HashParameters, ConstsParams.Gesture.NUM_INTERSECTIONS, numIntersections, Consts.WEIGHT_HIGH);
        }
        else {
            for (int idxStroke = 0; idxStroke < ListStrokes.size(); idxStroke++) {
                tempStroke = ListStrokes.get(idxStroke);
                listGestureEvents.addAll(tempStroke.ListEvents);
                TimeInterval += tempStroke.TimeInterval;
            }
        }
    }

    protected boolean TwoLinesIntersect(Line2D line1, Line2D line2) {
        double x1 = line1.X1;
        double y1 = line1.Y1;
        double x2 = line1.X2;
        double y2 = line1.Y2;

        double x3 = line2.X1;
        double y3 = line2.Y1;
        double x4 = line2.X2;
        double y4 = line2.Y2;

        // Return false if either of the lines have zero length
        if (x1 == x2 && y1 == y2 ||
                x3 == x4 && y3 == y4) {
            return false;
        }
        // Fastest method, based on Franklin Antonio's "Faster Line Segment Intersection" topic "in Graphics Gems III" book (http://www.graphicsgems.org/)
        double ax = x2 - x1;
        double ay = y2 - y1;
        double bx = x3 - x4;
        double by = y3 - y4;
        double cx = x1 - x3;
        double cy = y1 - y3;

        double alphaNumerator = by * cx - bx * cy;
        double commonDenominator = ay * bx - ax * by;
        if (commonDenominator > 0) {
            if (alphaNumerator < 0 || alphaNumerator > commonDenominator) {
                return false;
            }
        } else if (commonDenominator < 0) {
            if (alphaNumerator > 0 || alphaNumerator < commonDenominator) {
                return false;
            }
        }
        double betaNumerator = ax * cy - ay * cx;
        if (commonDenominator > 0) {
            if (betaNumerator < 0 || betaNumerator > commonDenominator) {
                return false;
            }
        } else if (commonDenominator < 0) {
            if (betaNumerator > 0 || betaNumerator < commonDenominator) {
                return false;
            }
        }
        if (commonDenominator == 0) {
            // This code wasn't in Franklin Antonio's method. It was added by Keith Woodward.
            // The lines are parallel.
            // Check if they're collinear.
            double y3LessY1 = y3 - y1;
            double collinearityTestForP3 = x1 * (y2 - y3) + x2 * (y3LessY1) + x3 * (y1 - y2);   // see http://mathworld.wolfram.com/Collinear.html
            // If p3 is collinear with p1 and p2 then p4 will also be collinear, since p1-p2 is parallel with p3-p4
            if (collinearityTestForP3 == 0) {
                // The lines are collinear. Now check if they overlap.
                if (x1 >= x3 && x1 <= x4 || x1 <= x3 && x1 >= x4 ||
                        x2 >= x3 && x2 <= x4 || x2 <= x3 && x2 >= x4 ||
                        x3 >= x1 && x3 <= x2 || x3 <= x1 && x3 >= x2) {
                    if (y1 >= y3 && y1 <= y4 || y1 <= y3 && y1 >= y4 ||
                            y2 >= y3 && y2 <= y4 || y2 <= y3 && y2 >= y4 ||
                            y3 >= y1 && y3 <= y2 || y3 <= y1 && y3 >= y2) {
                        return true;
                    }
                }
            }
            return false;
        }
        return true;

    }

    protected double GetNumberOfIntersections(ArrayList<MotionEventCompact> listGestureEvents) {
        double numOfIntersections = 0;

        MotionEventCompact tempEvent, tempEventPrev;

        ArrayList<Line2D> listLines = new ArrayList<>();
        
        for (int idx = 1; idx < listGestureEvents.size(); idx++) {
            tempEvent = listGestureEvents.get(idx);
            tempEventPrev = listGestureEvents.get(idx - 1);

            listLines.add(new Line2D(tempEvent.Xmm, tempEvent.Ymm, tempEventPrev.Xmm, tempEventPrev.Ymm));
        }

        for (int idx1 = 0; idx1 < listLines.size(); idx1++) {
            for (int idx2 = idx1 + 1; idx2 < listLines.size(); idx2++) {
                if (TwoLinesIntersect(listLines.get(idx1), listLines.get(idx2))) {
                    numOfIntersections++;
                }
            }
        }

        return numOfIntersections;
    }
}
