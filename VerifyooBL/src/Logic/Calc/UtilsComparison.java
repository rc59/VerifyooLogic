package Logic.Calc;

public class UtilsComparison {
	public double CompareNumericalValues(double value1, double value2, double threshold) {
		double percentageDiff = UtilsMath.GetPercentageDiff(value1, value2);
		
		double finalScore = 1;
		double threashold = 0.65;
		if(percentageDiff < threashold ) {			
			finalScore = 1 - (threashold - percentageDiff);
		}
		
		return finalScore;
	}
}
