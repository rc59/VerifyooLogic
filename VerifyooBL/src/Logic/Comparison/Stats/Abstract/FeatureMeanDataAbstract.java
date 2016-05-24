package Logic.Comparison.Stats.Abstract;

import java.util.ArrayList;

import Logic.Comparison.Stats.Interfaces.IFeatureMeanData;

public abstract class FeatureMeanDataAbstract implements IFeatureMeanData {
	protected String mName;
	protected double mSumValues;
	protected double mCount;
	protected ArrayList<Double> mListValues;
	
	public FeatureMeanDataAbstract(String name)
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
}
