package VerifyooLogic.DataObjects.Params;

import VerifyooLogic.Statistics.Interfaces.IBaseParam;
import VerifyooLogic.Utils.Consts;
import VerifyooLogic.Utils.ConstsParams;
import VerifyooLogic.Utils.UtilsCalc;
import VerifyooLogic.Utils.UtilsData;

import java.util.ArrayList;
import java.util.Collections;
import java.util.Comparator;
import java.util.HashMap;

/**
 * Created by roy on 2/11/2016.
 */
public abstract class ParamPropertiesAbstract {
    public String Name;

    public double Average;
    public double Max;
    public double Min;
    public double StandardDeviation;
    public double Median;
    public double Quarter1;
    public double Quarter3;
    public double InterQ3Q1Range;

    public ParamPropertiesAbstract() {
    }

    protected void CalculateParameters(double[] values, int length, String paramName, HashMap<String, IBaseParam> hashParams) {
        Name = paramName;

        double currValue;
        double sum = 0;

        ArrayList<Double> listDouble = new ArrayList<Double>();

        Max = values[0];
        Min = values[0];

        for (int idx = 0; idx < length; idx++) {
            currValue = values[idx];
            sum += currValue;

            Min = UtilsCalc.GetMinValue(Min, currValue);
            Max = UtilsCalc.GetMaxValue(Max, currValue);

            listDouble.add(currValue);
        }

        Collections.sort(listDouble, new Comparator<Double>() {
            @Override
            public int compare(Double value1, Double value2) {
                if (value1 > value2) {
                    return 1;
                }
                if (value2 > value1) {
                    return -1;
                }
                return 0;
            }
        });

        Average = sum / length;

        CalculateStandardDev(values);

        int idxQ1 = (int) Math.floor((double)listDouble.size() / 4);
        int idxMedian = listDouble.size() / 2;
        int idxQ3 = (int) Math.floor((double)listDouble.size() / 4 * 3);

        Median = listDouble.get(idxMedian);

        Quarter1 = listDouble.get(idxQ1);
        Quarter3 = listDouble.get(idxQ3);

        InterQ3Q1Range = Quarter3 - Quarter1;

        InitSubParameters(hashParams);
    }

    public ParamPropertiesAbstract(double[] values, int length, String paramName, HashMap<String, IBaseParam> hashParams) {
        CalculateParameters(values, length, paramName, hashParams);
    }

    protected void InitSubParameters(HashMap<String, IBaseParam> hashParams) {
        try {
            UtilsData.AddNumericalParameter(hashParams, UtilsCalc.GetParamName(Name, ConstsParams.StrokeExtendedParams.AVERAGE), Average, Consts.WEIGHT_MEDIUM);
            UtilsData.AddNumericalParameter(hashParams, UtilsCalc.GetParamName(Name, ConstsParams.StrokeExtendedParams.MIN), Min, Consts.WEIGHT_HIGH);
            UtilsData.AddNumericalParameter(hashParams, UtilsCalc.GetParamName(Name, ConstsParams.StrokeExtendedParams.MAX), Max, Consts.WEIGHT_HIGH);
            UtilsData.AddNumericalParameter(hashParams, UtilsCalc.GetParamName(Name, ConstsParams.StrokeExtendedParams.STANDARD_DEVIATION), StandardDeviation, Consts.WEIGHT_LOW);
            UtilsData.AddNumericalParameter(hashParams, UtilsCalc.GetParamName(Name, ConstsParams.StrokeExtendedParams.MEDIAN), Median, Consts.WEIGHT_LOW);


        //UtilsData.AddNumericalParameter(hashParams, UtilsCalc.GetParamName(Name, ConstsParams.StrokeExtendedParams.INTER_Q3_Q1_RANGE), InterQ3Q1Range, Consts.WEIGHT_LOW);
//            hashParams.put(UtilsCalc.GetParamName(Name, ConstsParams.StrokeExtendedParams.QUARTER1), Quarter1);
//            hashParams.put(UtilsCalc.GetParamName(Name, ConstsParams.StrokeExtendedParams.QUARTER3), Quarter3);
        } catch (Exception exc) {
            String msg = exc.getMessage();
        }
    }

    protected void CalculateStandardDev(double[] values) {
        double squaredDiffs = 0;
        for (int idx = 0; idx < values.length; idx++) {
            squaredDiffs += UtilsCalc.CalculateSquareDiff(values[idx], Average);
        }

        StandardDeviation = CalculateStandardDevSecondPhase(squaredDiffs, values.length);
    }

    protected double CalculateStandardDevSecondPhase(double squaredDiffSum, double listSize) {
        squaredDiffSum = squaredDiffSum / listSize;
        squaredDiffSum = Math.sqrt(squaredDiffSum);

        return squaredDiffSum;
    }
}
