package Logic.Utils;
// TODO: filter ParametrAvgPoint of length 1, i.e., IndexEnd.Index-IndexStart.Index=1
// The filter will work as follows: replace the value of the point in the middle IndexEnd.Index-1 
// by the average value of the start and end points
// The replacement has to affect the value of EventExtended, average values etc.
// The loop in CalculatePeaks should continue from the index of the value that was replaced
import java.util.ArrayList;

import Data.MetaData.IndexValue;
import Data.MetaData.ParameterAvgPoint;
public class UtilsPeakCalc {
	public ParameterAvgPoint CalculatePeaks(double[] values, double average)
	{
		ArrayList<ParameterAvgPoint> listVelocityAvgPoints = new ArrayList<>();	
		double valuePrev;
		double valueCurr;
		ParameterAvgPoint tempVelocityAvgPoint = null;		
		
		double currMaxValue = 0;
		int currMaxVelocityIdx = 0;
		
		try {
			for(int idxValue = 1; idxValue < values.length; idxValue++) {
				valuePrev = values[idxValue - 1];
				valueCurr = values[idxValue];

				if(valuePrev < average && valueCurr > average) {
					tempVelocityAvgPoint = new ParameterAvgPoint(idxValue, valueCurr, average);
					tempVelocityAvgPoint.IndexStart.Index = idxValue;
					currMaxValue = valueCurr;
				}
				
				if((valuePrev > average && valueCurr < average) || (idxValue == values.length - 1)) {
					if(tempVelocityAvgPoint != null) {
						tempVelocityAvgPoint.IndexEnd.Index = idxValue;
						tempVelocityAvgPoint.IndexEnd.Value = valueCurr; 
						tempVelocityAvgPoint.PercentageOfNumOfEvent = ((double)(tempVelocityAvgPoint.IndexEnd.Index - tempVelocityAvgPoint.IndexStart.Index) / values.length); 
								
						currMaxValue = Utils.GetInstance().GetUtilsMath().GetMaxValue(currMaxValue, valueCurr);
						tempVelocityAvgPoint.MaxValueInSection = new IndexValue();
						tempVelocityAvgPoint.MaxValueInSection.Value = currMaxValue;
						tempVelocityAvgPoint.MaxValueInSection.Index = currMaxVelocityIdx;
						listVelocityAvgPoints.add(tempVelocityAvgPoint);
						tempVelocityAvgPoint = null;
						currMaxValue = 0;
					}				
				}
				
				if(tempVelocityAvgPoint != null && tempVelocityAvgPoint.IndexEnd.Index == -1) {
					currMaxValue = Utils.GetInstance().GetUtilsMath().GetMaxValue(currMaxValue, valueCurr);
					if(currMaxValue == valueCurr) {
						currMaxVelocityIdx = idxValue;
					}
				}
			}		
		}
		catch(Exception exc)
		{
			String msg = exc.getMessage();
		}
				
		
		ParameterAvgPoint avgPointMax = null;
		if(listVelocityAvgPoints.size() > 0) {				
			avgPointMax = listVelocityAvgPoints.get(0);		
			avgPointMax.MaxValueInSection.Value = avgPointMax.MaxValueInSection.Value;
			avgPointMax.MaxValueInSection.Index = avgPointMax.MaxValueInSection.Index;			
		}
		
		return avgPointMax;
	}
}
