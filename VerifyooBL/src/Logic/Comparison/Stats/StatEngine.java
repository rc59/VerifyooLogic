package Logic.Comparison.Stats;

import java.util.HashMap;

import Logic.Calc.UtilsStat;
import Logic.Comparison.Stats.Interfaces.IStatEngine;
import Logic.Comparison.Stats.Norms.NormMgr;
import Logic.Comparison.Stats.Norms.Interfaces.INormData;
import Logic.Comparison.Stats.Norms.Interfaces.INormMgr;

public class StatEngine implements IStatEngine {
	
	protected UtilsStat mUtilsStat;
	protected INormMgr mNormMgr;
	protected static IStatEngine mInstance = null;
	
	private HashMap<String, FeatureMeanData> mHashFeatureMeans;
	
	protected StatEngine()
	{
		mNormMgr = NormMgr.GetInstance();
		mUtilsStat = new UtilsStat();
		mHashFeatureMeans = new HashMap<>();
	}
	
	public static IStatEngine GetInstance() {
      if(mInstance == null) {
    	  mInstance = new StatEngine();
      }
      return mInstance;
   }
	
	public double CompareStrokeDoubleValues(String instruction, String paramName, int strokeIdx, double authValue)
	{
		INormData normObj = mNormMgr.GetNormDataByParamName(paramName, instruction);
		
		String key = GenerateStrokeFeatureMeanKey(instruction, paramName, strokeIdx);

		double internalMean = mHashFeatureMeans.get(key).GetMean();
		double internalSd = normObj.GetInternalStandardDev();
		
		double score = mUtilsStat.CalculateProbability(authValue, internalMean, internalSd);
		return score;
	}
	
	public double CompareGestureDoubleValues(String instruction, String paramName, double authValue)
	{
		INormData normObj = mNormMgr.GetNormDataByParamName(paramName, instruction);
		
		String key = GenerateGestureFeatureMeanKey(instruction, paramName);
				
		double internalMean = mHashFeatureMeans.get(key).GetMean();
		double internalSd = normObj.GetInternalStandardDev();
		
		double score = mUtilsStat.CalculateProbability(authValue, internalMean, internalSd);
		return score;
	}
	
	protected String GenerateStrokeFeatureMeanKey(String instruction, String paramName, int strokeIdx)
	{
		String key = String.format("%s-%s-%s", instruction, String.valueOf(strokeIdx) ,paramName);
		return key;
	}
	
	protected String GenerateGestureFeatureMeanKey(String instruction, String paramName)
	{
		String key = String.format("%s-%s", instruction, paramName);
		return key;
	}
	
	public void AddStrokeValue(String instruction, String paramName, int strokeIdx, double value)
	{
		String key = GenerateStrokeFeatureMeanKey(instruction, paramName, strokeIdx);
		
		FeatureMeanData tempFeatureMeanData;
		
		if(mHashFeatureMeans.containsKey(key)) {
			tempFeatureMeanData = mHashFeatureMeans.get(key);
		}
		else {
			tempFeatureMeanData = new FeatureMeanData(paramName);			
			mHashFeatureMeans.put(key, tempFeatureMeanData);
		}
		
		tempFeatureMeanData.AddValue(value);		
	}
	
	public void AddGestureValue(String instruction, String paramName, double value)
	{
		String key = GenerateGestureFeatureMeanKey(instruction, paramName);
		
		FeatureMeanData tempFeatureMeanData;
		
		if(mHashFeatureMeans.containsKey(key)) {
			tempFeatureMeanData = mHashFeatureMeans.get(key);
		}
		else {
			tempFeatureMeanData = new FeatureMeanData(paramName);			
			mHashFeatureMeans.put(key, tempFeatureMeanData);
		}
		
		tempFeatureMeanData.AddValue(value);		
	}
}