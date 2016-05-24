package Data.Comparison;

import Data.Comparison.Abstract.CompareResultAbstract;

public class CompareResultGeneric extends CompareResultAbstract {

	public CompareResultGeneric(String name, double value, double weight, double originalValue, double mean, double standardDev) {
		Name = name;
		Value = value;
		Weight = weight;
		OriginalValue = originalValue;
		Mean = mean;
		StandardDev = standardDev; 
	}

}
