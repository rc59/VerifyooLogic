package Logic.Comparison.Stats;

import java.util.ArrayList;

public class FeatureMeanData {
	protected String mName;
	protected double mSumValues;
	protected double mCount;
	protected ArrayList<Double> mListValues;
	
	public FeatureMeanData(String name)
	{
		mListValues = new ArrayList<>();
		mName = name;
		mSumValues = 0;
		mCount = 0;
	}
	
	public void AddValue(double currentValue)
	{
		mCount++;
		mSumValues += currentValue;
		mListValues.add(currentValue);
	}
	
	public double GetMean()
	{
		return (mSumValues / mCount);
	}
	
	public double GetInternalSd()
	{
		double mean = (mSumValues / mCount);
		double sqrError = 0;
		
		for(int idxValue = 0; idxValue < mListValues.size(); idxValue++) {
			sqrError += Math.pow((mListValues.get(idxValue) - mean), 2);
		}
		
		double sd = Math.sqrt(sqrError / mListValues.size());
		return sd;
	}
}
