package Data.MetaData;

public class StrokeProperties {
	public double LengthMM;	
	public double[] ListEventLength;
	public double[] ListDeltaXmm;
	public double[] ListDeltaYmm;
	
	public StrokeProperties(int listSize)
	{
		ListEventLength = new double[listSize - 1];		
		ListDeltaXmm  = new double[listSize - 1];
		ListDeltaYmm  = new double[listSize - 1];
	}
}
