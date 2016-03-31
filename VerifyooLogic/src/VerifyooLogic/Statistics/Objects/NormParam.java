package VerifyooLogic.Statistics.Objects;

import VerifyooLogic.Statistics.Interfaces.INormParam;

/**
 * Created by roy on 12/28/2015.
 */
public class NormParam implements INormParam {
    public String Name;
    public double Average;
    public double StandardDeviation;

    public NormParam(String name, double avg, double sd) {
        Name = name;
        Average = avg;
        StandardDeviation = sd;
    }

    public double GetAverage() {
        return Average;
    }

    public double GetStandardDeviation() {
        return StandardDeviation;
    }
}
