package VerifyooLogic.Utils;

import VerifyooLogic.UserProfile.MotionEventCompact;
import VerifyooLogic.DataObjects.Octagon;

import java.util.ArrayList;

/**
 * Created by roy on 1/5/2016.
 */
public class UtilsCalc {

    public static double GetMaxValue(double value1, double value2) {
        if (value1 > value2) {
            return value1;
        }

        return value2;
    }

    public static double GetMinValue(double value1, double value2) {
        if (value1 < value2) {
            return value1;
        }

        return value2;
    }

    public static double CalcPitagoras(double value1, double value2) {
        double value = value1 * value1 + value2 * value2;
        value = Math.sqrt(value);

        return value;
    }

    public static boolean CheckIfOppositeSymbol(double value1, double value2) {
        if ((value1 > 0 && value2 < 0) || (value1 < 0 && value2 > 0)) {
            return true;

        }
        else {
            return false;
        }
    }

    public static boolean CheckIfBetween(double value, double min, double max) {
        if (value > min && value < max) {
            return true;
        }
        else {
            return false;
        }
    }

    public static double GetPercentageDiff(double value1, double value2) {

        if(value1 == 0 || value2 == 0)
        {
            return 1;
        }

        value1 = Math.abs(value1);
        value2 = Math.abs(value2);

        double result = 0;

        if(value1 > value2)
        {
            result = value2 / value1;
        }
        else
        {
            result = value1 / value2;
        }

        return result;
    }

    public static MotionEventCompact GetMaxXpixel(MotionEventCompact pointA, MotionEventCompact pointB){
        if(pointA.Xpixel > pointB.Xpixel)
            return pointA;
        return pointB;
    }

    public static MotionEventCompact GetMinXpixel(MotionEventCompact pointA, MotionEventCompact pointB){
        if(pointA.Xpixel > pointB.Xpixel)
            return pointB;
        return pointA;
    }

    public static MotionEventCompact GetMaxYpixel(MotionEventCompact pointA, MotionEventCompact pointB){
        if(pointA.Ypixel > pointB.Ypixel)
            return pointA;
        return pointB;
    }

    public static MotionEventCompact GetMinYpixel(MotionEventCompact pointA, MotionEventCompact pointB){
        if(pointA.Ypixel > pointB.Ypixel)
            return pointB;
        return pointA;
    }

    public static MotionEventCompact GetMaxX(MotionEventCompact pointA, MotionEventCompact pointB){
        if(pointA.Xmm > pointB.Xmm)
            return pointA;
        return pointB;
    }

    public static MotionEventCompact GetMinX(MotionEventCompact pointA, MotionEventCompact pointB){
        if(pointA.Xmm > pointB.Xmm)
            return pointB;
        return pointA;
    }

    public static MotionEventCompact GetMaxY(MotionEventCompact pointA, MotionEventCompact pointB){
        if(pointA.Ymm > pointB.Ymm)
            return pointA;
        return pointB;
    }

    public static MotionEventCompact GetMinY(MotionEventCompact pointA, MotionEventCompact pointB){
        if(pointA.Ymm > pointB.Ymm)
            return pointB;
        return pointA;
    }

    public static double GetDistanceBetweenPoints(double p1X, double p1Y, double p2X, double p2Y) {
        return Math.sqrt(Math.pow((p1X - p2X), 2) + Math.pow((p1Y - p2Y), 2));
    }

    public static double GetDistanceBetweenPoints(MotionEventCompact p1, MotionEventCompact p2) {
        return Math.sqrt(Math.pow((p1.Xmm - p2.Xmm), 2) + Math.pow((p1.Ymm - p2.Ymm), 2));
    }

    public static double GetPointToLineDistance(double p1Xmm, double p1Ymm, double p2Xmm, double p2Ymm, double p3Xmm, double p3Ymm){
        double h = Math.sqrt((Math.pow((p2Xmm - p1Xmm),2)) + (Math.pow((p2Ymm - p1Ymm),2)));
        double a = (p2Ymm - p1Ymm) / h;
        double b = (p1Xmm - p2Xmm) / h;
        double c = (p2Xmm * p1Ymm - p1Xmm * p2Ymm) / h;
        double result = a * p3Xmm + b * p3Ymm + c;
        return result;
    }

    public static double GetPointToLineDistance(MotionEventCompact p1, MotionEventCompact p2, MotionEventCompact p3){
        double h = Math.sqrt((Math.pow((p2.Xmm - p1.Xmm),2)) + (Math.pow((p2.Ymm - p1.Ymm),2)));
        double a = (p2.Ymm - p1.Ymm) / h;
        double b = (p1.Xmm - p2.Xmm) / h;
        double c = (p2.Xmm * p1.Ymm - p1.Xmm * p2.Ymm) / h;
        double result = a * p3.Xmm + b * p3.Ymm + c;
        return result;
    }

    public static double GetMaxPointToLineDistInSegment(ArrayList<MotionEventCompact> listEvents, Octagon StrokeOctagon, int vertexA, int vertexB, int vertexC){
        double MaxPointToLineDist = 0;
        double currentDist = 0;
        int startIdx = StrokeOctagon.Octagon[vertexA];
        int endIdx = StrokeOctagon.Octagon[vertexB];

        if (startIdx > endIdx) {
            int tempIdx = startIdx;
            startIdx = endIdx;
            endIdx = tempIdx;
        }

        for(int i = startIdx + 1; i < endIdx; ++i){
            currentDist = GetPointToLineDistance(listEvents.get(startIdx), listEvents.get(endIdx), listEvents.get(i));
            if(Math.abs(currentDist) > Math.abs(MaxPointToLineDist)){
                MaxPointToLineDist = currentDist;
                StrokeOctagon.Octagon[vertexC] = i;
            }
        }
        return MaxPointToLineDist;
    }
//A, B are the indices in listEvents that define the line
    public static double GetMaxPointToLineDistInSegment(ArrayList<MotionEventCompact> listEvents, int vertexA, int vertexB){
        double MaxPointToLineDist = 0;
        double currentDist = 0;
        int startIdx = vertexA;
        int endIdx = vertexB;

        if (startIdx > endIdx) {
            int tempIdx = startIdx;
            startIdx = endIdx;
            endIdx = tempIdx;
        }

        for(int i = startIdx + 1; i < endIdx; ++i){
            currentDist = GetPointToLineDistance(listEvents.get(startIdx), listEvents.get(endIdx), listEvents.get(i));
            if(Math.abs(currentDist) > Math.abs(MaxPointToLineDist)){
                MaxPointToLineDist = currentDist;
            }
        }
        return MaxPointToLineDist;
    }

    public static double GetAngle(double deltaX, double deltaY) {
        double angle;
        try {
            double result = Math.atan2(deltaY, deltaX) * 180 / Consts.PI;
            result = result * 100;
            result = Math.round(result);
            angle = result / 100;
        }
        catch (Exception exc)
        {
            angle = -1;
        }

        return angle;
    }

    public static double CalculateSquareDiff(double value, double mean) {
        return Math.pow(value - mean, 2);
    }

    public static String GetParamName(String baseParam, String extendedParam) {
        return String.format("%s-%s", baseParam, extendedParam);
    }

    public static double CalcRatioThreshold(double score, double ratioDiff, double threshold, double weight) {

        if (ratioDiff < threshold) {
            double scoreToReduce = (threshold - ratioDiff) / weight;
            score -= scoreToReduce;
            //score -= Consts.PARAMETER_RATIO_THRESHOLD_DECREASE;
        }

        return score;
    }

    public static double CalculateTurnRadius(MotionEventCompact eventCurrent, MotionEventCompact eventPrevious, MotionEventCompact eventNext) {
        double deltaX = eventCurrent.Xmm - eventPrevious.Xmm;
        double deltaY = eventCurrent.Ymm - eventPrevious.Ymm;

        double deltaNextX = eventNext.Xmm - eventCurrent.Xmm;
        double deltaNextY = eventNext.Ymm - eventCurrent.Ymm;

        double eventNextLength = CalcPitagoras(deltaNextX, deltaNextY);

        double eventAngle = CalculateEventAngle(deltaX, deltaY);
        double betaAngle = CalculateEventAngle(deltaX + deltaNextX, deltaY + deltaNextY);
        double deltaBeta = betaAngle - eventAngle;

        double radius;
        if (Math.abs(deltaBeta) < 0.001) {
            radius = 999;
        }
        else {
            radius = eventNextLength / (2 * Math.sin(deltaBeta));
        }
        return radius;
    }

    public static double CalculateEventAngle(double deltaX, double deltaY) {
        double eventAngle = (Math.atan2(deltaY, deltaX) + 2 * Consts.PI);
        eventAngle = eventAngle % (2 * Consts.PI);
        return eventAngle;
    }
}
