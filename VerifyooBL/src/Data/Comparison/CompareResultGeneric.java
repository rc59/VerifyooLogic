package Data.Comparison;

import Data.Comparison.Abstract.CompareResultAbstract;

public class CompareResultGeneric extends CompareResultAbstract {

	public CompareResultGeneric(String name, double value, double weight) {
		Name = name;
		Value = value;
		Weight = weight;		
	}

}
