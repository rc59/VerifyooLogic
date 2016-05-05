package Data.Comparison;

import Data.Comparison.Abstract.CompareResultAbstract;

public class CompareResultParamVectors extends CompareResultAbstract {
	public double[] Vector1;
	public double[] Vector2;
	public double MinimumCosineDistance;
	
	public CompareResultParamVectors(String name, double value, double weight, double minimumCosineDistance, double[] vector1, double[] vector2)
	{
		Name = name;
		Value = value;
		Weight = weight;
		MinimumCosineDistance = minimumCosineDistance;
		Vector1 = vector1;
		Vector2 = vector2;		
	}
}
