package Data.MetaData;

public class StrokeProperties {
	public double LengthMM;	
	public double[] ListEventLength;
	public double[] AccumulatedLength;
	public double[] ListDeltaXmm;
	public double[] ListDeltaYmm;
	
	public StrokeProperties(int listSize)
	{
		ListEventLength = new double[listSize];	
		AccumulatedLength = new double[listSize];
		ListDeltaXmm  = new double[listSize];
		ListDeltaYmm  = new double[listSize];
	}
}
