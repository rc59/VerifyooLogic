package Data.MetaData;

public class ParameterAvgPoint {	
	public IndexValue IndexStart;
	public IndexValue IndexEnd;
	public IndexValue MaxValueInSection;
	public double AverageValue;
	
	public ParameterAvgPoint(int indexStart, double velocityStart, double average) {
		AverageValue = average;
		IndexStart = new IndexValue();
		IndexStart.Index = indexStart;
		IndexStart.Value = velocityStart;
		MaxValueInSection = new IndexValue();
		MaxValueInSection.Index = -1;		
		IndexEnd = new IndexValue();
		IndexEnd.Index = -1;
	}
}
