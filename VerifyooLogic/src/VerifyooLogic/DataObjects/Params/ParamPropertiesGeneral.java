package VerifyooLogic.DataObjects.Params;

import VerifyooLogic.DataObjects.*;
import VerifyooLogic.Statistics.Interfaces.IBaseParam;

import java.util.HashMap;

/**
 * Created by roy on 2/15/2016.
 */
public class ParamPropertiesGeneral extends ParamPropertiesAbstract {
    public ParamPropertiesGeneral(double[] values, int length, String paramName, HashMap<String, IBaseParam> hashParams) {
        super(values, length, paramName, hashParams);
    }
}
