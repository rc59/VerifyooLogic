package Logic.Comparison.Stats;

import java.util.ArrayList;

import Logic.Comparison.Stats.Abstract.FeatureMeanDataAbstract;

public class FeatureMeanData extends FeatureMeanDataAbstract {					
	public FeatureMeanData(String name, String instruction) {
		super(name, instruction);		
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
