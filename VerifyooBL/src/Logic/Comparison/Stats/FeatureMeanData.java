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
	
	public void AddAngularValue(double currentValue)
	{
		mCount++;
		mListValues.add(currentValue);
	}

	public double GetAngularMean(){
		double angularMean = 0;
		double dx = 0;
		double dy = 0;
		for(int i = 0; i < mListValues.size(); i++)
		{
			dx += Math.cos(mListValues.get(i));
			dy += Math.sin(mListValues.get(i));
		}
		angularMean = Math.atan2(dy, dx);
		return angularMean;
	}

	double GetAngularStdDev()
	{
	      double sin = 0;
	      double cos = 0;
	      for(int i = 0; i < mListValues.size(); i++)
	      {
	           sin += Math.sin(mListValues.get(i) * (Math.PI/180.0));
	           cos += Math.cos(mListValues.get(i) * (Math.PI/180.0)); 
	      }
	      sin /= mListValues.size();
	      cos /= mListValues.size();

	      double stddev = Math.sqrt(-Math.log(sin*sin+cos*cos));

	      return stddev;
	 }
	
}
