package VerifyooLogic.Statistics.Objects;

import VerifyooLogic.Statistics.Interfaces.IZParam;

/**
 * Created by roy on 12/28/2015.
 */
public class ZParam implements IZParam {
    String Name;
    double ZScore;
    double OriginalScore;
    int Weight;

    public ZParam(String name, double zScore, double originalScore, int weight) {
        Name = name;
        ZScore = zScore;
        OriginalScore = originalScore;
        Weight = weight;
    }

    public String GetName() {
        return Name;
    }

    public double GetZScore() {
        return ZScore;
    }

    public double GetWeight() {
        return Weight;
    }
}
