package Logic.Utils;

import Consts.ConstsMeasures;

public class UtilsMath {
//calculate alpha-beta
	public double CalcAbsAngleDifference(double alpha, double beta)
	{
		double phi = Math.abs(beta - alpha) % (2*Math.PI);       
        double angularDiff = phi > Math.PI ? (2*Math.PI) - phi : phi;
        return angularDiff;
	}
	
	public double CalcAvg(double[] values)
	{
		double avg = 0;		
		for(int idx = 0; idx < values.length; idx++)
		{
			avg += values[idx];
		}
		
		avg = avg / values.length;
		return avg;
	}
	
	public double CalcPitagoras(double value1, double value2) {
        double value = value1 * value1 + value2 * value2;
        value = Math.sqrt(value);

        return value;
    }	
	
	public double GetMaxValue(double value1, double value2) {
		if(value1 > value2) {
			return value1;
		}
		else {
			return value2;
		}
	}
	
	public double GetMinValue(double value1, double value2) {
		if(value1 < value2) {
			return value1;
		}
		else {
			return value2;
		}
	}
	
	public double GetPercentageDiff(double value1, double value2) {

        if(value1 == 0 || value2 == 0)
        {
            return 1;
        }

        value1 = Math.abs(value1);
        value2 = Math.abs(value2);

        double result = 0;

        if(value1 > value2)
        {
            result = value2 / value1;
        }
        else
        {
            result = value1 / value2;
        }

        return result;
    }
	
	public double CalculateEventAngle(double deltaX, double deltaY) {
        double eventAngle = (Math.atan2(deltaY, deltaX) + 2 * ConstsMeasures.PI);
        eventAngle = eventAngle % (2 * ConstsMeasures.PI);
        return eventAngle;
    }
	
	public double CalculateTriangleArea(double x1, double y1, double x2, double y2, double x3, double y3){
		return Math.abs(0.5 * (x1*(y3-y2) + x2*(y1-y3) + x3*(y2-y1)));
	}
}
