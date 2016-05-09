package Logic.Calc;

public class UtilsComparison {
	public double CompareNumericalValues(double value1, double value2, double threshold) {
		double percentageDiff = UtilsMath.GetPercentageDiff(value1, value2);
		
		double finalScore = 1;	
		if(percentageDiff < threshold ) {			
			finalScore = 1 - (threshold - percentageDiff);
		}
		
		return finalScore;
	}
}
