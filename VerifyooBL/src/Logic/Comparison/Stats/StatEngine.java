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
	
	protected StatEngine()
	{
		mNormMgr = NormMgr.GetInstance();
		mUtilsStat = new UtilsStat();
	}
	
	public static IStatEngine GetInstance() {
      if(mInstance == null) {
    	  mInstance = new StatEngine();
      }
      return mInstance;
   }
	
	public double CompareStrokeDoubleValues(String instruction, String paramName, int strokeIdx, double authValue, HashMap<String, FeatureMeanData> hashFeatureMeans)
	{		
		INormData normObj = mNormMgr.GetNormDataByParamName(paramName, instruction);
				
		String key = GenerateStrokeFeatureMeanKey(instruction, paramName, strokeIdx);
				
		double populationMean = normObj.GetMean();
		double populationSd = normObj.GetStandardDev();
		
		double internalMean = hashFeatureMeans.get(key).GetMean();
		double internalSd = hashFeatureMeans.get(key).GetInternalSd();
		
		double zScore = (authValue - populationMean) / populationSd;
		
		double score = mUtilsStat.CalculateScore(authValue, populationMean, populationSd, internalMean);
		return score;
	}
	
	public double CompareGestureDoubleValues(String instruction, String paramName, double authValue, HashMap<String, FeatureMeanData> hashFeatureMeans)
	{
		INormData normObj = mNormMgr.GetNormDataByParamName(paramName, instruction);
		
		String key = GenerateGestureFeatureMeanKey(instruction, paramName);
				
		double populationMean = normObj.GetMean();
		double populationSd = normObj.GetStandardDev();
		
		double internalMean = hashFeatureMeans.get(key).GetMean();
		double internalSd = hashFeatureMeans.get(key).GetInternalSd();
		
		double zScore = (authValue - populationMean) / populationSd;
		
		double score = mUtilsStat.CalculateScore(authValue, populationMean, populationSd, internalMean);
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
}