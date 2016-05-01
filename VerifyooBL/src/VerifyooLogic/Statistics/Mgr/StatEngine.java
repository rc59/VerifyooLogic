package VerifyooLogic.Statistics.Mgr;

import VerifyooLogic.Statistics.Interfaces.IBaseParam;
import VerifyooLogic.Statistics.Interfaces.INormParam;
import VerifyooLogic.Statistics.Interfaces.IStatEngine;
import VerifyooLogic.Statistics.Objects.ZParam;
import VerifyooLogic.UserProfile.CompactGesture;
import VerifyooLogic.UserProfile.Stroke;
import VerifyooLogic.Utils.Consts;
import VerifyooLogic.Utils.UtilsCalc;

import java.util.ArrayList;
import java.util.Collections;
import java.util.Comparator;
import java.util.HashMap;
import java.util.Map;

/**
 * Created by roy on 12/28/2015.
 */
public class StatEngine implements IStatEngine {

    NormMgr mNormMgr;

    public StatEngine(NormMgr normMgr) {
        mNormMgr = normMgr;
    }

    public double GetGestureUniqueFactor(CompactGesture gesture) {

        ArrayList<ZParam> gestureZParams = GetZParams(gesture.HashParameters);

        double uniqueFactor = GetZScoreListUniqueFactor(gestureZParams);

        ArrayList<ZParam> tempStrokeZParams;

        for (int idx = 0; idx < gesture.ListStrokes.size(); idx++) {
            gesture.ListStrokes.get(idx).InitParams();
            tempStrokeZParams = GetZParams(gesture.ListStrokes.get(idx).HashParameters);
            uniqueFactor += GetZScoreListUniqueFactor(tempStrokeZParams);
        }

        return uniqueFactor;
    }

    protected double GetZScoreListUniqueFactor(ArrayList<ZParam> listZScores) {

        double tempZScore;
        double uniqueFactor = 0;

        for (int idx = 0; idx < listZScores.size(); idx++) {
            tempZScore = listZScores.get(idx).GetZScore();

            if (UtilsCalc.CheckIfBetween(Math.abs(tempZScore), 1.5, 1.8)) {
                uniqueFactor++;
            }
            else {
                if (UtilsCalc.CheckIfBetween(Math.abs(tempZScore), 1.8, 2)) {
                    uniqueFactor += 2;
                }
                else {
                    if (Math.abs(tempZScore) > 2) {
                        uniqueFactor += 3;
                    }
                }
            }
        }

        return uniqueFactor;
    }

    public double GetProbability(Stroke stroke1, Stroke stroke2) {
        double value = 0;

        stroke1.InitParams();
        stroke2.InitParams();

        mNormMgr = new NormMgr();

        RemoveRedundantParameters(stroke1, stroke2);

        ArrayList<ZParam> strokeZParams1 = GetZParams(stroke1.HashParameters);
        ArrayList<ZParam> strokeZParams2 = GetZParams(stroke2.HashParameters);

        value = CalcProb(strokeZParams1, strokeZParams2);

        return value;
    }

    protected void RemoveRedundantParameters(Stroke stroke1, Stroke stroke2) {
        if (stroke1.HashParameters.keySet().size() != stroke2.HashParameters.keySet().size()) {
            RemoveRedundantParametersInHash(stroke1, stroke2);
            RemoveRedundantParametersInHash(stroke2, stroke1);
        }
    }

    private void RemoveRedundantParametersInHash(Stroke strokeToClean, Stroke strokeToCheck) {
        HashMap<String, IBaseParam> tempHash = new HashMap();
        for (Map.Entry<String, IBaseParam> entry : strokeToClean.HashParameters.entrySet()) {
            if (strokeToCheck.HashParameters.containsKey(entry.getKey())) {
                tempHash.put(entry.getKey(), entry.getValue());
            }
        }

        strokeToClean.HashParameters = tempHash;
    }

    public double GetProbability(CompactGesture gesture1, CompactGesture gesture2) {
        double value = 0;

        mNormMgr = new NormMgr();

        ArrayList<ZParam> gestureZParams1 = GetZParams(gesture1.HashParameters);
        ArrayList<ZParam> gestureZParams2 = GetZParams(gesture2.HashParameters);

        value = CalcProb(gestureZParams1, gestureZParams2);

        return value;
    }

    private double CalcProb(ArrayList<ZParam> strokeZParams1, ArrayList<ZParam> strokeZParams2) {

        ZParam tempZ1, tempZ2;

        double prob1, prob2;
        double probDiff;

        ArrayList<ProbScoreObj> listScoresHigh = new ArrayList<ProbScoreObj>();
        ArrayList<ProbScoreObj> listScoresMedium = new ArrayList<ProbScoreObj>();
        ArrayList<ProbScoreObj> listScoresLow = new ArrayList<ProbScoreObj>();

        for (int idxParam = 0; idxParam < strokeZParams1.size(); idxParam++) {
            tempZ1 = strokeZParams1.get(idxParam);
            tempZ2 = strokeZParams2.get(idxParam);

            prob1 = GetProbabilityFromZ(tempZ1.GetZScore());
            prob2 = GetProbabilityFromZ(tempZ2.GetZScore());

            probDiff = Math.abs(prob1 - prob2);

            if (tempZ1.GetWeight() == Consts.WEIGHT_HIGH) {
                listScoresHigh.add((new ProbScoreObj(probDiff, tempZ1.GetZScore(), tempZ1.GetName())));
            }
            if (tempZ1.GetWeight() == Consts.WEIGHT_MEDIUM) {
                listScoresMedium.add((new ProbScoreObj(probDiff, tempZ1.GetZScore(), tempZ1.GetName())));
            }
            if (tempZ1.GetWeight() == Consts.WEIGHT_LOW) {
                listScoresLow.add((new ProbScoreObj(probDiff, tempZ1.GetZScore(), tempZ1.GetName())));
            }
        }

        Collections.sort(listScoresLow, new Comparator<ProbScoreObj>() {
            @Override
            public int compare(ProbScoreObj score1, ProbScoreObj score2) {
                if (Math.abs(score1.UserZ) > Math.abs(score2.UserZ)) {
                    return -1;
                }
                if (Math.abs(score1.UserZ) < Math.abs(score2.UserZ)) {
                    return 1;
                }
                return 0;
            }
        });

        Collections.sort(listScoresMedium, new Comparator<ProbScoreObj>() {
            @Override
            public int compare(ProbScoreObj score1, ProbScoreObj score2) {
                if (Math.abs(score1.UserZ) > Math.abs(score2.UserZ)) {
                    return -1;
                }
                if (Math.abs(score1.UserZ) < Math.abs(score2.UserZ)) {
                    return 1;
                }
                return 0;
            }
        });

        Collections.sort(listScoresHigh, new Comparator<ProbScoreObj>() {
            @Override
            public int compare(ProbScoreObj score1, ProbScoreObj score2) {
                if (Math.abs(score1.UserZ) > Math.abs(score2.UserZ)) {
                    return -1;
                }
                if (Math.abs(score1.UserZ) < Math.abs(score2.UserZ)) {
                    return 1;
                }
                return 0;
            }
        });

        HashMap<String, Double> hashLow = new HashMap<String, Double>();
        HashMap<String, Double> hashMed = new HashMap<String, Double>();
        HashMap<String, Double> hashHigh = new HashMap<String, Double>();

        double maxParams = listScoresLow.size() / 3;
        maxParams = Math.floor(maxParams);
        for (int idx = 0; idx < listScoresLow.size(); idx++) {
            hashLow.put(listScoresLow.get(idx).Name, new Double(1));
            if (idx >= maxParams) {
                break;
            }
        }

        maxParams = listScoresMedium.size() / 3;
        maxParams = Math.floor(maxParams);
        for (int idx = 0; idx < listScoresMedium.size(); idx++) {
            hashMed.put(listScoresMedium.get(idx).Name, new Double(1));
            if (idx >= maxParams) {
                break;
            }
        }

        maxParams = listScoresHigh.size() / 3;
        maxParams = Math.floor(maxParams);
        for (int idx = 0; idx < listScoresHigh.size(); idx++) {
            hashHigh.put(listScoresHigh.get(idx).Name, new Double(1));
//            if (idx >= maxParams) {
//                break;
//            }
        }
        double scoreHigh = 0;
        double countHigh = 0;

        double scoreMedium = 0;
        double countMedium = 0;

        double scoreLow = 0;
        double countLow = 0;

        for (int idxParam = 0; idxParam < strokeZParams1.size(); idxParam++) {
            tempZ1 = strokeZParams1.get(idxParam);
            tempZ2 = strokeZParams2.get(idxParam);

            prob1 = GetProbabilityFromZ(tempZ1.GetZScore());
            prob2 = GetProbabilityFromZ(tempZ2.GetZScore());

            probDiff = Math.abs(prob1 - prob2);

            if (hashHigh.containsKey(tempZ1.GetName())) {
                if (tempZ1.GetWeight() == Consts.WEIGHT_HIGH) {
                    scoreHigh += probDiff;
                    countHigh++;
                }
            }

            if (hashMed.containsKey(tempZ1.GetName())) {
                if (tempZ1.GetWeight() == Consts.WEIGHT_MEDIUM) {
                    scoreMedium += probDiff;
                    countMedium++;
                }
            }

            if (hashLow.containsKey(tempZ1.GetName())) {
                if (tempZ1.GetWeight() == Consts.WEIGHT_LOW) {
                    scoreLow += probDiff;
                    countLow++;
                }
            }
        }

        double result;
        double high, med, low;

        if (countMedium != 0 && countLow != 0) {
            high = scoreHigh / countHigh;
            med = scoreMedium / countMedium;
            low = scoreLow / countLow;

            result = high * 0.7 + med * 0.3; // + low * 0.1;
        }
        else {
            result = scoreHigh / countHigh;
        }

        return result;
    }

    private ArrayList<ZParam> GetZParams(HashMap<String, IBaseParam> hashMap) {
        ArrayList<ZParam> strokeZParams = new ArrayList<>();

        for (Map.Entry<String, IBaseParam> entry : hashMap.entrySet()) {
            strokeZParams.add(CreateZParam(entry.getKey(), entry.getValue()));
        }

        return strokeZParams;
    }

    private ArrayList<ZParam> GetZUserParams(HashMap<String, IBaseParam> hashMap1, HashMap<String, IBaseParam> hashMap2) {
        ArrayList<ZParam> strokeZUserParams = new ArrayList<>();

        IBaseParam tempEntry2;

        for (Map.Entry<String, IBaseParam> entry : hashMap1.entrySet()) {
            tempEntry2 = hashMap2.get(entry.getKey());

           // strokeZUserParams.add(CreateZParamUser(entry.getKey(), entry.getValue(), tempEntry2.GetValue()));

        }

        return strokeZUserParams;
    }

    private ZParam CreateZParamUser(String name, IBaseParam value1, IBaseParam value2) {

        INormParam normParam = mNormMgr.getNormParam(name, 0);

        double zScore = (value2.GetValue() - value1.GetValue()) / (value1.GetValue() / 10);

        ZParam zParam = new ZParam(name, zScore, value1.GetValue(), value1.GetWeight());

        return zParam;
    }

    private ZParam CreateZParam(String name, IBaseParam value) {

        INormParam normParam = mNormMgr.getNormParam(name, 0);

        //double zScore = (value - normParam.GetAverage()) / normParam.GetStandardDeviation();
        double zScore = (value.GetValue() - normParam.GetAverage()) / normParam.GetStandardDeviation();

        ZParam zParam = new ZParam(name, zScore, value.GetValue(), value.GetWeight());

        return zParam;
    }


//    private ZParam CreateZParam(String name, double value) {
//
//        INormParam normParam = mNormMgr.getNormParam(name, 0);
//
//        //double zScore = (value - normParam.GetAverage()) / normParam.GetStandardDeviation();
//        double zScore = (value - normParam.GetAverage()) / normParam.GetStandardDeviation();
//
//        ZParam zParam = new ZParam(name, zScore, value);
//
//        return zParam;
//    }

    private double GetProbabilityFromZ(double z) {
        double y, x, w;
        double Z_MAX = 6;

        if (z == 0.0) {
            x = 0.0;
        } else {
            y = 0.5 * Math.abs(z);
            if (y > (Z_MAX * 0.5)) {
                x = 1.0;
            } else if (y < 1.0) {
                w = y * y;
                x = ((((((((0.000124818987 * w
                        - 0.001075204047) * w + 0.005198775019) * w
                        - 0.019198292004) * w + 0.059054035642) * w
                        - 0.151968751364) * w + 0.319152932694) * w
                        - 0.531923007300) * w + 0.797884560593) * y * 2.0;
            } else {
                y -= 2.0;
                x = (((((((((((((-0.000045255659 * y
                        + 0.000152529290) * y - 0.000019538132) * y
                        - 0.000676904986) * y + 0.001390604284) * y
                        - 0.000794620820) * y - 0.002034254874) * y
                        + 0.006549791214) * y - 0.010557625006) * y
                        + 0.011630447319) * y - 0.009279453341) * y
                        + 0.005353579108) * y - 0.002141268741) * y
                        + 0.000535310849) * y + 0.999936657524;
            }
        }

        double result = 1 - (z > 0.0 ? ((x + 1.0) * 0.5) : ((1.0 - x) * 0.5));
        return result;
    }
}
