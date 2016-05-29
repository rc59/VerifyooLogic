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
	
	protected static UtilsStat mUtilsStat;
	protected static UtilsGeneral mUtilsGeneral;
	
	protected static INormMgr mNormMgr;
	protected static IStatEngine mInstance = null;	
	
	protected StatEngine()
	{
		
	}
	
	public static IStatEngine GetInstance() {
      if(mInstance == null) {
		  mInstance = new StatEngine();
		  mNormMgr = NormMgr.GetInstance();
		  mUtilsStat = new UtilsStat();
		  mUtilsGeneral = new UtilsGeneral();
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
		
		double upper = internalMean + (3 * populationInternalSd);
		double lower = internalMean - (3 * populationInternalSd);
		
		double zScore = (authValue - populationMean) / populationSd;
		double zScoreForUser = (internalMean - populationMean) / populationSd;
		
		IStatEngineResult statResult;
		
		double score = mUtilsStat.CalculateScore(authValue, populationMean, populationSd, internalMean);
				
		if(authValue > GetUpper(internalMean, populationInternalSd, 2) || authValue < GetLower(internalMean, populationInternalSd, 2)) {
			score -= 0.3;
		}
		if(authValue > GetUpper(internalMean, populationInternalSd, 3) || authValue < GetLower(internalMean, populationInternalSd, 3)) {
			score -= 0.1;
		}
		if(authValue > GetUpper(internalMean, populationInternalSd, 4) || authValue < GetLower(internalMean, populationInternalSd, 4)) {
			score -= 0.2;
		}
		
		if(score < 0) {
			score = 0;
		}
		
		statResult = new StatEngineResult(score, zScoreForUser);
		return statResult;	
	}

	public IStatEngineResult CompareGestureScoreWithoutDistribution(String instruction, String paramName, double authValue, HashMap<String, IFeatureMeanData> hashFeatureMeans)
	{
		INormData normObj = mNormMgr.GetNormDataByParamName(paramName, instruction);
		
		String key = mUtilsGeneral.GenerateGestureFeatureMeanKey(instruction, paramName);
		double populationInternalSd = normObj.GetInternalStandardDev();
		double internalMean = hashFeatureMeans.get(key).GetMean();
		
		double upper = internalMean + (2.5 * populationInternalSd);
		double lower = internalMean - (2.5 * populationInternalSd);
		
		IStatEngineResult statResult;
		
		double score;

		if(authValue < upper && authValue > lower)
			score = 1;
		else
			score = 0;

		double zScoreForUser = 3.0;
		statResult = new StatEngineResult(score, zScoreForUser);
		return statResult;	
	}

	protected double GetUpper(double mean, double sd, double multi) {
		double upper = mean + (multi * sd);
		return upper;
	}
	
	protected double GetLower(double mean, double sd, double multi) {
		double lower = mean - (multi * sd);
		return lower;
	}
}