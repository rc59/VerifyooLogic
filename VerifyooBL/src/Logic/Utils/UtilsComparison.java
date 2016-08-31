package Logic.Utils;

import java.util.ArrayList;
import java.util.Collections;
import java.util.Comparator;

import Logic.Comparison.Stats.Data.Interface.IStatEngineResult;

public class UtilsComparison {
	public double CompareNumericalValues(double value1, double value2, double threshold) {
		double percentageDiff = Utils.GetInstance().GetUtilsMath().GetPercentageDiff(value1, value2);
		
		double finalScore = 1;	
		if(percentageDiff < threshold ) {			
			finalScore = 1 - (threshold - percentageDiff);
		}
		
		return finalScore;
	}
	
	public double GetTotalSpatialScore(ArrayList<IStatEngineResult> listScores) {
		Collections.sort(listScores, new Comparator<IStatEngineResult>() {
            public int compare(IStatEngineResult score1, IStatEngineResult score2) {
                if (Math.abs(score1.GetZScore()) > Math.abs(score2.GetZScore())) {
                    return -1;
                }
                if (Math.abs(score1.GetZScore()) < Math.abs(score2.GetZScore())) {
                    return 1;
                }
                return 0;
            }
        });
		
		int limit = listScores.size();		
		double result = 0;
		double totalWeights = 0;
		for(int idx = 0; idx < limit; idx++) {			
			totalWeights += listScores.get(idx).GetWeight();
			result += listScores.get(idx).GetScore() * listScores.get(idx).GetWeight();				
		}
		
		result = result / totalWeights;
		return result;
	}
}
