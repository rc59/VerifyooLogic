package VerifyooLogic.DataObjects.Params;

import VerifyooLogic.Statistics.Interfaces.BaseParamAbstract;

/**
 * Created by roy on 2/17/2016.
 */
public class NumericalParam extends BaseParamAbstract {
    public double Value;

    public NumericalParam() {
    }

    public NumericalParam(double value, String name, int weight) {
        Value = value;
        ParamName = name;
        Weight = weight;
    }

    public double GetValue() {
        return Value;
    }
}
