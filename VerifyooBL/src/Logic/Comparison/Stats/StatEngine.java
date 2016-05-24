package Logic.Comparison.Stats;

import java.util.HashMap;

import Logic.Comparison.Stats.Data.StatEngineResult;
import Logic.Comparison.Stats.Data.Interface.IStatEngineResult;
import Logic.Comparison.Stats.Interfaces.IFeatureMeanData;
import Logic.Comparison.Stats.Interfaces.IStatEngine;
import Logic.Comparison.Stats.Norms.NormMgr;
import Logic.Comparison.Stats.Norms.Interfaces.INormData;
import Logic.Comparison.Stats.Norms.Interfaces.INormMgr;
import Logic.Utils.UtilsGeneral;
import Logic.Utils.UtilsStat;

public class StatEngine implements IStatEngine {
	
	protected UtilsStat mUtilsStat;
	protected UtilsGeneral mUtilsGeneral;
	
	protected INormMgr mNormMgr;
	protected static IStatEngine mInstance = null;	
	
	protected StatEngine()
	{
		mNormMgr = NormMgr.GetInstance();
		mUtilsStat = new UtilsStat();
		mUtilsGeneral = new UtilsGeneral();
	}
	
	public static IStatEngine GetInstance() {
      if(mInstance == null) {
    	  mInstance = new StatEngine();
      }
      return mInstance;
   }
	
	public IStatEngineResult CompareStrokeDoubleValues(String instruction, String paramName, int strokeIdx, double authValue, HashMap<String, IFeatureMeanData> hashFeatureMeans)
	{		
		INormData normObj = mNormMgr.GetNormDataByParamName(paramName, instruction);
				
		String key = mUtilsGeneral.GenerateStrokeFeatureMeanKey(instruction, paramName, strokeIdx);
				
		double populationMean = normObj.GetMean();
		double populationSd = normObj.GetStandardDev();
		
		double internalMean = hashFeatureMeans.get(key).GetMean();
		double internalSd = hashFeatureMeans.get(key).GetInternalSd();
		
		double zScore = (authValue - populationMean) / populationSd;
		
		double score = mUtilsStat.CalculateScore(authValue, populationMean, populationSd, internalMean);
		
		IStatEngineResult statResult = new StatEngineResult(score, zScore);
		return statResult;
	}
	
	public IStatEngineResult CompareGestureDoubleValues(String instruction, String paramName, double authValue, HashMap<String, IFeatureMeanData> hashFeatureMeans)
	{
		INormData normObj = mNormMgr.GetNormDataByParamName(paramName, instruction);
		
		String key = mUtilsGeneral.GenerateGestureFeatureMeanKey(instruction, paramName);
				
		double populationMean = normObj.GetMean();
		double populationSd = normObj.GetStandardDev();
		double populationInternalSd = normObj.GetInternalStandardDev();
		
		
		double internalMean = hashFeatureMeans.get(key).GetMean();
		double internalSd = normObj.GetInternalStandardDev(); //hashFeatureMeans.get(key).GetInternalSd();
		
		double upper = internalMean + 3 * populationInternalSd;
		double lower = internalMean - 3 * populationInternalSd;
		
		double zScore = (authValue - populationMean) / populationSd;
		double zScoreForUser = (internalMean - populationMean) / populationSd;
		
		IStatEngineResult statResult;
		
		double score = mUtilsStat.CalculateScore(authValue, populationMean, populationSd, internalMean);
				
//		if(authValue > upper || authValue < lower) {
//			score -= 0.2;
//			if(score < 0) {
//				score = 0;
//			}
//		}
		
		statResult = new StatEngineResult(score, zScoreForUser);
		return statResult;	
	}
}