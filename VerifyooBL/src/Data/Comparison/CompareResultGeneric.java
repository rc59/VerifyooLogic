package Data.Comparison;

import Data.Comparison.Abstract.CompareResultAbstract;

public class CompareResultGeneric extends CompareResultAbstract {

	public CompareResultGeneric(String name, double value, double weight, double originalValue, double mean, double standardDev, double internalSd) {
		Name = name;
		Value = value;
		Weight = weight;
		OriginalValue = originalValue;
		Mean = mean;
		StandardDev = standardDev; 
		InternalStandardDev = internalSd;
	}
}
