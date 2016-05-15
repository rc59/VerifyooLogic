package Logic.Calc;

public class UtilsMath {
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
}
