package VerifyooLogic.Comparison;

import java.util.ArrayList;

import VerifyooLogic.Statistics.Interfaces.IStatEngine;
import VerifyooLogic.Statistics.Mgr.NormMgr;
import VerifyooLogic.Statistics.Mgr.StatEngine;
import VerifyooLogic.UserProfile.CompactGesture;
import VerifyooLogic.UserProfile.MotionEventCompact;
import VerifyooLogic.UserProfile.Stroke;
import VerifyooLogic.Utils.Consts;
import VerifyooLogic.Utils.UtilsCalc;

public class GestureComparer {
    public double Compare(CompactGesture gestureTemplate, CompactGesture gestureAuth) {

        double result = 0;
        double currentScore = 0;
        double totalScore = 0;

        double gestureScore = 0;

        double numOfFactors = gestureTemplate.ListStrokes.size();

        StrokeComparer strokeComparer = new StrokeComparer();

        ArrayList<MotionEventCompact> listGestureEventsTemplate = new ArrayList<>();
        ArrayList<MotionEventCompact> listGestureEventsAuth = new ArrayList<>();

        double totalLengthTemplate = 0;
        double totalLengthAuth = 0;

        boolean isNeedToCheckGesture = true;

        if (gestureTemplate.ListStrokes.size() == gestureAuth.ListStrokes.size()) {
            for (int idxStroke = 0; idxStroke < gestureTemplate.ListStrokes.size(); idxStroke++) {
                if(gestureTemplate.ListStrokes.get(idxStroke).Length > Consts.MIN_STROKE_LENGTH && gestureAuth.ListStrokes.get(idxStroke).Length > Consts.MIN_STROKE_LENGTH) {
                    currentScore = strokeComparer.Compare(gestureTemplate.ListStrokes.get(idxStroke), gestureAuth.ListStrokes.get(idxStroke));
                    totalScore += currentScore;

                    listGestureEventsTemplate.addAll(0, gestureTemplate.ListStrokes.get(idxStroke).ListEvents);
                    totalLengthTemplate += gestureTemplate.ListStrokes.get(idxStroke).Length;

                    listGestureEventsAuth.addAll(0, gestureAuth.ListStrokes.get(idxStroke).ListEvents);
                    totalLengthAuth += gestureAuth.ListStrokes.get(idxStroke).Length;
                }
                else {
                    if((gestureTemplate.ListStrokes.get(idxStroke).Length > Consts.MIN_STROKE_LENGTH && gestureAuth.ListStrokes.get(idxStroke).Length < Consts.MIN_STROKE_LENGTH) ||
                       (gestureTemplate.ListStrokes.get(idxStroke).Length < Consts.MIN_STROKE_LENGTH && gestureAuth.ListStrokes.get(idxStroke).Length > Consts.MIN_STROKE_LENGTH)) {
                        isNeedToCheckGesture = false;
                        numOfFactors++;
                    }
                    else {
                        listGestureEventsTemplate.addAll(0, gestureTemplate.ListStrokes.get(idxStroke).ListEvents);
                        totalLengthTemplate += gestureTemplate.ListStrokes.get(idxStroke).Length;

                        listGestureEventsAuth.addAll(0, gestureAuth.ListStrokes.get(idxStroke).ListEvents);
                        totalLengthAuth += gestureAuth.ListStrokes.get(idxStroke).Length;

                        numOfFactors--;
                    }
                }

            }

            if (isNeedToCheckGesture && gestureTemplate.ListStrokes.size() > 1) {
                ShapeComparer shapeComparer = new ShapeComparer();

                double score;
                try {
                    Stroke tempStrokeTemplate = new Stroke();
                    tempStrokeTemplate.Length = totalLengthTemplate;
                    tempStrokeTemplate.ListEvents = listGestureEventsTemplate;

                    Stroke tempStrokeAuth = new Stroke();
                    tempStrokeAuth.Length = totalLengthAuth;
                    tempStrokeAuth.ListEvents = listGestureEventsAuth;

                    score = shapeComparer.getScore(tempStrokeTemplate, tempStrokeAuth);
                } catch (Exception exc) {
                    score = 0.1;
                }

                gestureScore = GetGestureScore(gestureTemplate, gestureAuth);

                double ratioDiffTime = UtilsCalc.GetPercentageDiff(gestureTemplate.TimeInterval, gestureAuth.TimeInterval);

                gestureScore = UtilsCalc.CalcRatioThreshold(gestureScore, ratioDiffTime, Consts.PARAMETER_RATIO_THRESHOLD_GENERAL, 4);

                result = totalScore / numOfFactors;
                result = (result + gestureScore);
                result = result / 2;

                if (score < 2) {
                    result = result / 2;
                }
            }
            else {
                result = totalScore / numOfFactors;
            }

            //totalScore += gestureScore;
            //numOfFactors++;
        }

        return result;
    }

    private double GetGestureScore(CompactGesture gesture1, CompactGesture gesture2) {
        double result = 0;

        gesture1.InitParams();
        gesture2.InitParams();

        IStatEngine statEngine = new StatEngine(new NormMgr());
        double prob = statEngine.GetProbability(gesture1, gesture2);

        result = 1 - prob;
        return result;
    }
}
