package Data.Comparison;

import Data.Comparison.Abstract.CompareResultAbstract;
import Logic.Utils.Utils;

public class CompareResultGeneric extends CompareResultAbstract {

	public CompareResultGeneric(String name, double value, double originalValue, double internalMean, double popMean, double popSd, double internalSd, double internalSdUserOnly) {
		Name = name;
		Value = value;
		Weight = Utils.GetInstance().GetUtilsStat().CalcWeight(internalMean, internalSd, popMean, popSd);
		OriginalValue = originalValue;
		Mean = internalMean;
		PopMean = popMean;
		StandardDev = popSd; 
		InternalStandardDev = internalSd;
		InternalStandardDevUserOnly = internalSdUserOnly;
	}
}
