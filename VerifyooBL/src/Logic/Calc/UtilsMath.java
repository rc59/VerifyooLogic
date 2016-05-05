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
	
	public static double CalcPitagoras(double value1, double value2) {
        double value = value1 * value1 + value2 * value2;
        value = Math.sqrt(value);

        return value;
    }	
	
	public static double GetPercentageDiff(double value1, double value2) {

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
