package VerifyooLogic.Comparison;

import java.util.ArrayList;

import VerifyooLogic.Statistics.Interfaces.IStatEngine;
import VerifyooLogic.Statistics.Mgr.NormMgr;
import VerifyooLogic.Statistics.Mgr.StatEngine;
import VerifyooLogic.UserProfile.MotionEventExtremePoint;
import VerifyooLogic.UserProfile.Stroke;
import VerifyooLogic.Utils.Consts;
import VerifyooLogic.Utils.UtilsCalc;

public class StrokeComparer {

    protected Stroke mStrokeTemplate, mStrokeAuth;

    public StrokeComparer() {

    }

    public StrokeComparer(Stroke strokeTemplate, Stroke strokeAuth) {
        mStrokeTemplate = strokeTemplate;
        mStrokeAuth = strokeAuth;
    }    
    
    public double Compare(Stroke strokeTemplate, Stroke strokeAuth) {
        try {        	
            mStrokeTemplate = strokeTemplate;
            mStrokeAuth = strokeAuth;

            if (mStrokeTemplate.Length > Consts.MIN_STROKE_LENGTH && mStrokeAuth.Length > Consts.MIN_STROKE_LENGTH) {
                ShapeComparer shapeComparer = new ShapeComparer();
                double shapeScore, shapeScore2;
                try {
                    shapeScore = shapeComparer.getScore(strokeTemplate, strokeAuth);
                    shapeScore2 = shapeComparer.getScore2(strokeTemplate, strokeAuth);
                } catch (Exception exc) {
                    shapeScore = 0.1;
                    shapeScore2 = 0.1;
                }

                IStatEngine statEngine = new StatEngine(new NormMgr());
                double prob = statEngine.GetProbability(strokeTemplate, strokeAuth);

                double finalProbResult = 1 - prob;

                //double ratioNumEvents = 1 - UtilsCalc.GetPercentageDiff(strokeTemplate.NumEvents, strokeAuth.NumEvents);

                ArrayList<MotionEventExtremePoint> listExtremeAuth = strokeAuth.ShapeProperties.ListEventsExtremePoints;
                ArrayList<MotionEventExtremePoint> listExtremeTemplate = strokeTemplate.ShapeProperties.ListEventsExtremePoints;

                double ratioLength = strokeTemplate.Length / strokeAuth.Length;
                double ratioDiffLength = UtilsCalc.GetPercentageDiff(strokeTemplate.Length, strokeAuth.Length);

                double ratioTime = strokeTemplate.TimeInterval / strokeAuth.TimeInterval;
                double ratioDiffTime = UtilsCalc.GetPercentageDiff(strokeTemplate.TimeInterval, strokeAuth.TimeInterval);

                double ratioBoundingBox = strokeTemplate.BoundingBox / strokeAuth.BoundingBox;
                //double ratioDiffArea = UtilsCalc.GetPercentageDiff(strokeTemplate.BoundingBox, strokeAuth.BoundingBox);

                double ratioStrokeArea = UtilsCalc.GetPercentageDiff(strokeTemplate.StrokeArea, strokeAuth.StrokeArea);

                //double ratioOctagonArea = strokeTemplate.StrokeOctagon.Area / strokeAuth.StrokeOctagon.Area;
                double ratioDiffOctagonArea = UtilsCalc.GetPercentageDiff(strokeTemplate.StrokeOctagon.Area, strokeAuth.StrokeOctagon.Area);
                double octagonAreaAbsDiff = Math.abs(strokeTemplate.StrokeOctagon.Area - strokeAuth.StrokeOctagon.Area);

                double octagonAreaDiff = Math.abs(strokeTemplate.BoundingBoxAndOctagonRatio - strokeAuth.BoundingBoxAndOctagonRatio);
                if (octagonAreaDiff >= Consts.PARAMETER_BOUNDING_BOX_AND_OCTAGON_DIFF_THRESHOLD) {
                    //finalProbResult -= octagonAreaDiff / 2;
                }
                if (octagonAreaDiff >= Consts.PARAMETER_BOUNDING_BOX_AND_OCTAGON_DIFF_THRESHOLD * 2) {
                    //finalProbResult -= octagonAreaDiff;
                }

                //double octagonAreaRatio = octagonAreaDiff / strokeTemplate.BoundingBoxAndOctagonRatio;

                double ratioDeltaExtremeX = strokeTemplate.ShapeProperties.DeltaXExtremePoints / strokeAuth.ShapeProperties.DeltaXExtremePoints;
                double ratioDeltaExtremeY = strokeTemplate.ShapeProperties.DeltaYExtremePoints / strokeAuth.ShapeProperties.DeltaYExtremePoints;

                double sizeAdjFactor = (ratioLength + ratioTime + ratioBoundingBox) / 3;
                double sizeAdjFactorX = (1 / sizeAdjFactor) * Math.sqrt((ratioDeltaExtremeY / ratioDeltaExtremeX));
                double sizeAdjFactorY = (1 / sizeAdjFactor) * Math.sqrt((ratioDeltaExtremeX / ratioDeltaExtremeY));

                finalProbResult = UtilsCalc.CalcRatioThreshold(finalProbResult, ratioDiffLength, Consts.PARAMETER_RATIO_THRESHOLD_GENERAL, 4);
                finalProbResult = UtilsCalc.CalcRatioThreshold(finalProbResult, ratioDiffTime, Consts.PARAMETER_RATIO_THRESHOLD_GENERAL, 4);
                finalProbResult = UtilsCalc.CalcRatioThreshold(finalProbResult, ratioStrokeArea, Consts.PARAMETER_RATIO_THRESHOLD_GENERAL, 4);
                //finalProbResult = UtilsCalc.CalcRatioThreshold(finalProbResult, ratioDiffArea, Consts.PARAMETER_RATIO_THRESHOLD_AREA, 4);

                if (octagonAreaAbsDiff > Consts.OCTAGON_RATIO_MIN_ABS_DIFF) {
                    //finalProbResult = UtilsCalc.CalcRatioThreshold(finalProbResult, ratioDiffOctagonArea, Consts.PARAMETER_RATIO_THRESHOLD_AREA, 3);
                }

                for (int idx = 0; idx < strokeAuth.ShapeProperties.ListEventsExtremePoints.size(); idx++) {
                    strokeAuth.ShapeProperties.ListEventsExtremePoints.get(idx).AdjustedX = strokeAuth.ShapeProperties.ListEventsExtremePoints.get(idx).AdjustedX * sizeAdjFactorX;
                    strokeAuth.ShapeProperties.ListEventsExtremePoints.get(idx).AdjustedY = strokeAuth.ShapeProperties.ListEventsExtremePoints.get(idx).AdjustedY * sizeAdjFactorY;
                }

                if (Math.abs(strokeTemplate.StrokePauses - strokeAuth.StrokePauses) >= 2) {
                    finalProbResult = finalProbResult - 0.05;
                }

                double[] extremePointDistances = new double[listExtremeAuth.size()];
                double sumExtremePointDistances = 0;
                double sumExtremePointDistancesSqr = 0;
                double currentDistance;

                for (int idx = 0; idx < listExtremeAuth.size(); idx++) {
                    currentDistance = UtilsCalc.CalcPitagoras(
                            listExtremeAuth.get(idx).AdjustedX - listExtremeTemplate.get(idx).AdjustedX,
                            listExtremeAuth.get(idx).AdjustedY - listExtremeTemplate.get(idx).AdjustedY);

                    extremePointDistances[idx] = currentDistance;
                    sumExtremePointDistances += currentDistance;
                    sumExtremePointDistancesSqr += currentDistance * currentDistance;
                }

//                double mean = sumExtremePointDistances / listExtremeAuth.size();
//
//                if (mean > Consts.EXTREME_POINTS_MEAN_THRESHOLD) {
//                    finalProbResult = finalProbResult - 0.05;
//
//                    if (mean > Consts.EXTREME_POINTS_MEAN_THRESHOLD * 2) {
//                        finalProbResult = finalProbResult - 0.05;
//                    }
//
//                    if (finalProbResult < 0) {
//                        finalProbResult = 0;
//                    }
//                }

                //double variance = (sumExtremePointDistancesSqr / listExtremeAuth.size()) - (mean * mean);

                //double angleDiffSumTemplate = mStrokeTemplate.ExtremeAnglePointSum;
                //double angleDiffSumAuth = mStrokeAuth.ExtremeAnglePointSum;

//                if (UtilsCalc.CheckIfBetween(shapeScore, 0, 1)) {
//                    finalProbResult = finalProbResult / 8;
//                } else {
//                    if (UtilsCalc.CheckIfBetween(shapeScore, 1, 1.5)) {
//                        finalProbResult = finalProbResult / 6;
//                    } else {
//                        if (UtilsCalc.CheckIfBetween(shapeScore, 1.5, 2)) {
//                            finalProbResult = finalProbResult / 4;
//                        } else {
//                            if (UtilsCalc.CheckIfBetween(shapeScore, 2, 2.2)) {
//                                finalProbResult = finalProbResult / 1.5;
//                            }
////                    else {
////                        if (UtilsCalc.CheckIfBetween(score, 2.2, 2.5)) {
////                            result = result / 1.2;
////                        }
////                    }
//                        }
//
//                    }
//                }
//
//                if (shapeScore < 2.5) {
//                    if (UtilsCalc.CheckIfBetween(shapeScore2, 0.004, 0.005)) {
//                        //result -= 0.2;
//                        finalProbResult = finalProbResult / 1.2;
//                    } else {
//                        if (UtilsCalc.CheckIfBetween(shapeScore2, 0.005, 0.006)) {
//                            //finalProbResult -= 0.2;
//                            finalProbResult = finalProbResult / 1.3;
//                        } else {
//                            if (UtilsCalc.CheckIfBetween(shapeScore2, 0.006, 0.007)) {
//                                //finalProbResult -= 0.3;
//                                finalProbResult = finalProbResult / 1.5;
//                            } else {
//                                if (shapeScore2 > 0.007) {
//                                    //finalProbResult -= 0.4;
//                                    finalProbResult = finalProbResult / 2;
//                                }
//                            }
//                        }
//                    }
//                }

                if (finalProbResult < 0) {
                    finalProbResult = 0;
                }

                double value = finalProbResult;

                return value;
            }
            else {
                if ((mStrokeTemplate.Length < Consts.MIN_STROKE_LENGTH && mStrokeAuth.Length > Consts.MIN_STROKE_LENGTH) ||
                    (mStrokeTemplate.Length > Consts.MIN_STROKE_LENGTH && mStrokeAuth.Length < Consts.MIN_STROKE_LENGTH)) {
                    return 0;
                }
                else {
                    return 1;
                }
            }

        }
        catch (Exception exc) {
            String msg = exc.getMessage();
            return 0;
        }
    }
}
