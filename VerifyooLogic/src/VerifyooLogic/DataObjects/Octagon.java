package VerifyooLogic.DataObjects;

import java.util.ArrayList;

import VerifyooLogic.UserProfile.MotionEventCompact;
import VerifyooLogic.Utils.UtilsCalc;

public class Octagon {
    public int[] Octagon;
    public double Area;
    protected ArrayList<MotionEventCompact> mListEvents;

    protected double XOrigin;
    protected double YOrigin;

    public Octagon() {

    }

    public Octagon(ArrayList<MotionEventCompact> listEvents, int minYIndex, int maxXIndex, int maxYIndex, int minXIndex) {
        Octagon = new int[8];
        mListEvents = listEvents;
        Octagon[0] = minYIndex; //A
        Octagon[2] = maxXIndex; //B
        Octagon[4] = maxYIndex; //C
        Octagon[6] = minXIndex; //D
        Area = 0;
        XOrigin = (listEvents.get(Octagon[0]).Xmm + listEvents.get(Octagon[2]).Xmm) / 2;
        YOrigin = (listEvents.get(Octagon[0]).Ymm + listEvents.get(Octagon[2]).Ymm) / 2;
        CalculateOctagonArea(minYIndex, maxXIndex, maxYIndex, minXIndex);
    }

    protected void CalculateOctagonArea(int minYIndex, int maxXIndex, int maxYIndex, int minXIndex) {
        Area = 0;
        double d = 0;
        double maxdAB = 0;
        double maxdBC = 0;
        double maxdCD = 0;
        double maxdDA = 0;
        double base = 0;
        double height = 0;
        double triangleArea = 0;
        double trapezoidArea = 0;
        for(int i = 0; i < mListEvents.size(); ++i){
            d = UtilsCalc.GetPointToLineDistance(mListEvents.get(minYIndex), mListEvents.get(maxXIndex), mListEvents.get(i));
            if(d > maxdAB) maxdAB = d;
            d = UtilsCalc.GetPointToLineDistance(mListEvents.get(maxXIndex), mListEvents.get(maxYIndex), mListEvents.get(i));
            if(d > maxdBC) maxdBC = d;
            d = UtilsCalc.GetPointToLineDistance(mListEvents.get(maxYIndex), mListEvents.get(minXIndex), mListEvents.get(i));
            if(d > maxdCD) maxdCD = d;
            d = UtilsCalc.GetPointToLineDistance(mListEvents.get(minXIndex), mListEvents.get(minYIndex), mListEvents.get(i));
            if(d > maxdDA) maxdDA = d;
        }

        base = UtilsCalc.GetDistanceBetweenPoints(mListEvents.get(minYIndex), mListEvents.get(maxXIndex));
        triangleArea = base * maxdAB / 2;
        height = Math.abs(UtilsCalc.GetPointToLineDistance(
                mListEvents.get(minYIndex).Xmm, mListEvents.get(minYIndex).Ymm,
                mListEvents.get(maxXIndex).Xmm, mListEvents.get(maxXIndex).Ymm,
                mListEvents.get(maxXIndex).Xmm, mListEvents.get(minYIndex).Ymm));
        if((height - maxdAB) > 0) {
            trapezoidArea = (base * height / 2) * (1 - 1 / Math.pow(height / (height - maxdAB), 2));
            Area += (triangleArea + trapezoidArea) / 2;
        }
        else{
            Area += triangleArea;
        }

        base = UtilsCalc.GetDistanceBetweenPoints(mListEvents.get(maxXIndex), mListEvents.get(maxYIndex));
        triangleArea = base * maxdBC / 2;
        height = Math.abs(UtilsCalc.GetPointToLineDistance(
                mListEvents.get(maxYIndex).Xmm, mListEvents.get(maxYIndex).Ymm,
                mListEvents.get(maxXIndex).Xmm, mListEvents.get(maxXIndex).Ymm,
                mListEvents.get(maxXIndex).Xmm, mListEvents.get(maxYIndex).Ymm));
        if((height - maxdBC) > 0) {
            trapezoidArea = (base * height / 2) * (1 - 1 / Math.pow(height / (height - maxdBC), 2));
            Area += (triangleArea + trapezoidArea) / 2;
        }
        else{
            Area += triangleArea;
        }

        base = UtilsCalc.GetDistanceBetweenPoints(mListEvents.get(maxYIndex), mListEvents.get(minXIndex));
        triangleArea = base * maxdCD / 2;
        height = Math.abs(UtilsCalc.GetPointToLineDistance(
                mListEvents.get(minXIndex).Xmm, mListEvents.get(minXIndex).Ymm,
                mListEvents.get(maxYIndex).Xmm, mListEvents.get(maxYIndex).Ymm,
                mListEvents.get(minXIndex).Xmm, mListEvents.get(maxYIndex).Ymm));
        if((height - maxdCD) > 0){
            trapezoidArea = (base * height / 2) * (1 - 1 / Math.pow(height / (height - maxdCD), 2));
            Area += (triangleArea + trapezoidArea) / 2;
        }
        else{
            Area += triangleArea;
        }

        base = UtilsCalc.GetDistanceBetweenPoints(mListEvents.get(minXIndex), mListEvents.get(minYIndex));
        triangleArea = base * maxdDA / 2;
        height = Math.abs(UtilsCalc.GetPointToLineDistance(
                mListEvents.get(minXIndex).Xmm, mListEvents.get(minXIndex).Ymm,
                mListEvents.get(minYIndex).Xmm, mListEvents.get(minYIndex).Ymm,
                mListEvents.get(minXIndex).Xmm, mListEvents.get(minYIndex).Ymm));
        if((height - maxdCD) > 0) {
            trapezoidArea = (base * height / 2) * (1 - 1 / Math.pow(height / (height - maxdDA), 2));
            Area += (triangleArea + trapezoidArea) / 2;
        }
        else{
            Area += triangleArea;
        }

        base = UtilsCalc.GetDistanceBetweenPoints(mListEvents.get(Octagon[0]), mListEvents.get(Octagon[4]));
        height = Math.abs(UtilsCalc.GetPointToLineDistance(mListEvents.get(Octagon[0]), mListEvents.get(Octagon[4]), mListEvents.get(Octagon[2])));
        Area += height * base / 2;
        height = Math.abs(UtilsCalc.GetPointToLineDistance(mListEvents.get(Octagon[0]), mListEvents.get(Octagon[4]), mListEvents.get(Octagon[6])));
        Area += height * base / 2;
    }
}