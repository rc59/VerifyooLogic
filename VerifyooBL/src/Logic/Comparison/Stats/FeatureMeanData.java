package Logic.Comparison.Stats;

public class FeatureMeanData {
	protected String mName;
	protected double mSumValues;
	protected double mCount;
	
	public FeatureMeanData(String name)
	{
		mName = name;
		mSumValues = 0;
		mCount = 0;
	}
	
	public void AddValue(double meanValue)
	{
		mCount++;
		mSumValues += meanValue;
	}
	
	public double GetMean()
	{
		return (mSumValues / mCount);
	}
}
