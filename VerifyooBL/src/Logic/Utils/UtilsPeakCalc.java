package Logic.Utils;

import java.util.ArrayList;

import Data.MetaData.IndexValue;
import Data.MetaData.ParameterAvgPoint;

public class UtilsPeakCalc {
	public ParameterAvgPoint CalculateStrokeVelocityPeaks(double[] values, double average)
	{
		ArrayList<ParameterAvgPoint> listVelocityAvgPoints = new ArrayList<>();	
		double valuePrev;
		double valueCurr;
		ParameterAvgPoint tempVelocityAvgPoint = null;
		ParameterAvgPoint avgPointMax = new ParameterAvgPoint(0);
		
		double currMaxValue = 0;
		int currMaxVelocityIdx = 0;

		for(int idxValue = 1; idxValue < values.length; idxValue++) {
			valuePrev = values[idxValue - 1];
			valueCurr = values[idxValue];

			if(valuePrev < average && valueCurr > average) {
				tempVelocityAvgPoint = new ParameterAvgPoint(idxValue);		
				currMaxValue = valueCurr;
			}
			
			if((valuePrev > average && valueCurr < average) || (idxValue == values.length - 1)) {
				if(tempVelocityAvgPoint != null) {
					tempVelocityAvgPoint.IndexEnd = idxValue;
					currMaxValue = Utils.GetInstance().GetUtilsMath().GetMaxValue(currMaxValue, valueCurr);
					tempVelocityAvgPoint.MaxValueInSection = new IndexValue();
					tempVelocityAvgPoint.MaxValueInSection.Value = currMaxValue;
					tempVelocityAvgPoint.MaxValueInSection.Index = currMaxVelocityIdx;
					listVelocityAvgPoints.add(tempVelocityAvgPoint);
					tempVelocityAvgPoint = null;
					currMaxValue = 0;
				}				
			}
			
			if(tempVelocityAvgPoint != null && tempVelocityAvgPoint.IndexEnd == -1) {
				currMaxValue = Utils.GetInstance().GetUtilsMath().GetMaxValue(currMaxValue, valueCurr);
				if(currMaxValue == valueCurr) {
					currMaxVelocityIdx = idxValue;
				}
			}
		}		
		
		if(listVelocityAvgPoints.size() > 0) {
			avgPointMax = new ParameterAvgPoint(0);		
			
			avgPointMax = listVelocityAvgPoints.get(0);		
			avgPointMax.MaxValueInSection.Value = avgPointMax.MaxValueInSection.Value;
			avgPointMax.MaxValueInSection.Index = avgPointMax.MaxValueInSection.Index;
		}
		
		return avgPointMax;
	}
}
