package Logic.Comparison.Stats;

import java.util.HashMap;

import Logic.Comparison.Stats.Interfaces.IStatEngine;
import Logic.Comparison.Stats.Norms.NormMgr;
import Logic.Comparison.Stats.Norms.Interfaces.INormData;
import Logic.Comparison.Stats.Norms.Interfaces.INormMgr;

public class StatEngine implements IStatEngine {
	INormMgr mNormMgr;
	private static IStatEngine mInstance = null;
	
	private HashMap<String, FeatureMeanData> mHashFeatureMeans;
	
	protected StatEngine()
	{
		mNormMgr = NormMgr.GetInstance();
		mHashFeatureMeans = new HashMap<>();
	}
	
	public static IStatEngine GetInstance() {
      if(mInstance == null) {
    	  mInstance = new StatEngine();
      }
      return mInstance;
   }
	
	public double CompareStrokeDoubleValues(String instruction, String paramName, int strokeIdx, double value1, double value2)
	{
		INormData normObj = mNormMgr.GetNormDataByParamName(paramName);
		double mean = normObj.GetMean();
		double sd = normObj.GetStandardDev();
		double internalSd = normObj.GetInternalStandardDev();
		
		String key = GenerateStrokeFeatureMeanKey(instruction, paramName, strokeIdx);
		double internalMean = mHashFeatureMeans.get(key).GetMean();
		
		double score = CalculateScore(value1, value2, mean, sd, internalSd);
		return score;
	}
	
	public double CompareGestureDoubleValues(String instruction, String paramName, double value1, double value2)
	{
		INormData normObj = mNormMgr.GetNormDataByParamName(paramName);
		double mean = normObj.GetMean();
		double sd = normObj.GetStandardDev();
		double internalSd = normObj.GetInternalStandardDev();
		
		String key = GenerateGestureFeatureMeanKey(instruction, paramName);
		double internalMean = mHashFeatureMeans.get(key).GetMean();
		
		double score = CalculateScore(value1, value2, mean, sd, internalSd);
		return score;
	}

	private double CalculateScore(double value1, double value2, double mean, double sd, double internalSd) {		
		return 0;
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