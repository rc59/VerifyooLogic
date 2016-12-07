package Logic.Comparison.Stats;

import java.util.ArrayList;

import Logic.Comparison.Stats.Abstract.FeatureMeanDataAbstract;

public class FeatureMeanData extends FeatureMeanDataAbstract {
	public FeatureMeanData(String name, String instruction) {
		super(name, instruction);		
	}	

	public double GetMean()
	{
		double meanOld = (mSumValues / mCount); 
		double mean = mUtilsAccumulator.Mean();
		
		return mean;
	}
	
	public double GetInternalSd()
	{
		double sd = mUtilsAccumulator.Stddev();
		return sd;		
	}
}
