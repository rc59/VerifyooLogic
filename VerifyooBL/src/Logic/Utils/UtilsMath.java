package Logic.Utils;

import Consts.ConstsMeasures;
import Data.UserProfile.Extended.MotionEventExtended;
import Data.UserProfile.Raw.MotionEventCompact;

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
	
	public double CalcDistanceInPixels(MotionEventCompact eventCurr, MotionEventCompact eventNext) {
        double deltaX = eventCurr.Xpixel - eventNext.Xpixel;
        double deltaY = eventCurr.Ypixel - eventNext.Ypixel;
		
        double distance = CalcPitagoras(deltaX, deltaY);

        return distance;
    }
	
	public double CalcDistanceInPixels(MotionEventExtended eventCurr, MotionEventExtended eventNext) {
        double deltaX = eventCurr.Xpixel - eventNext.Xpixel;
        double deltaY = eventCurr.Ypixel - eventNext.Ypixel;
		
        double distance = CalcPitagoras(deltaX, deltaY);

        return distance;
    }
	
	public double CalcDistanceInMMs(MotionEventExtended eventCurr, MotionEventExtended eventNext) {
        double deltaX = eventCurr.Xmm - eventNext.Xmm;
        double deltaY = eventCurr.Ymm - eventNext.Ymm;
		
        double distance = CalcPitagoras(deltaX, deltaY);

        return distance;
    }
	
	public double CalcPitagoras(double value1, double value2) {
        double value = value1 * value1 + value2 * value2;
        value = Math.sqrt(value);

        return value;
    }	
	
	public double CalcPitagoras(double value1, double value2, double value3) {
        double value = value1 * value1 + value2 * value2 + value3 * value3;
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
	
	public boolean IsBetween(double value, double lower, double upper) {
		if(value > lower && value < upper) {
			return true;
		}
		return false;
	}
	
	public double GetRelativeDistanceBetween(double value, double lowerBound, double upperBound) {

		double result = 0;	
		
		if(value > lowerBound && value < upperBound) {
			if(value < 0) {
				double temp = lowerBound;
				value = value * -1;
				lowerBound = upperBound * -1;
				upperBound = temp * -1;
			}
			
			value = value - lowerBound;
			upperBound = upperBound - lowerBound;
			
			result = 1 - (value / upperBound);
		}
		
        return result;
    }

	public double GetRelativeDistanceBetween(double value, double center, double bound1, double bound2) {

		double result = 0;	
		
		double lowerBound1 = center - bound1;
		double lowerBound2 = center - bound2;
		
		double upperBound1 = center + bound1;
		double upperBound2 = center + bound2;				
		
		if(value > lowerBound1 && value < upperBound1) {
			if(value < 0) {
				double temp = lowerBound1;
				value = value * -1;
				lowerBound1 = upperBound1 * -1;
				upperBound1 = temp * -1;
			}
			
			value = value - lowerBound1;
			upperBound1 = upperBound1 - lowerBound1;
			
			result = 1 - (value / upperBound1);
		}
		
        return result;
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

	public Complex[] ToComplex(double[] velocitiesAuthDistance) {
		Complex[] result = new Complex[velocitiesAuthDistance.length];
		for(int idx = 0; idx < velocitiesAuthDistance.length; idx++) {
			result[idx] = new Complex(velocitiesAuthDistance[idx], 0);
		}
	
		return result;
	}
}
