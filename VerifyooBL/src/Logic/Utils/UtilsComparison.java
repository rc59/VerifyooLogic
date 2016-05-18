package Logic.Utils;

public class UtilsComparison {
	public double CompareNumericalValues(double value1, double value2, double threshold) {
		double percentageDiff = Utils.GetInstance().GetUtilsMath().GetPercentageDiff(value1, value2);
		
		double finalScore = 1;	
		if(percentageDiff < threshold ) {			
			finalScore = 1 - (threshold - percentageDiff);
		}
		
		return finalScore;
	}
}
