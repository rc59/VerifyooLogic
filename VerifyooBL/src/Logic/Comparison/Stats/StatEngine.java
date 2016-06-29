package Logic.Comparison.Stats;

import java.util.HashMap;

import Logic.Comparison.Stats.Data.StatEngineResult;
import Logic.Comparison.Stats.Data.Interface.IStatEngineResult;
import Logic.Comparison.Stats.Interfaces.IFeatureMeanData;
import Logic.Comparison.Stats.Interfaces.IStatEngine;
import Logic.Comparison.Stats.Norms.NormMgr;
import Logic.Comparison.Stats.Norms.Interfaces.INormData;
import Logic.Comparison.Stats.Norms.Interfaces.INormMgr;
import Logic.Utils.Utils;
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
	
//	public IStatEngineResult CompareGestureDoubleValues(String instruction, String paramName, double authValue, HashMap<String, IFeatureMeanData> hashFeatureMeans)
//	{
//		INormData normObj = mNormMgr.GetNormDataByParamName(paramName, instruction);
//		
//		String key = mUtilsGeneral.GenerateGestureFeatureMeanKey(instruction, paramName);
//				
//		double populationMean = normObj.GetMean();
//		double populationSd = normObj.GetStandardDev();
//		double populationInternalSd = normObj.GetInternalStandardDev();		
//		
//		double internalMean = hashFeatureMeans.get(key).GetMean();
//		double internalSd = hashFeatureMeans.get(key).GetInternalSd();
//		
//		double upperInternalSD = internalMean + populationInternalSd;
//		double lowerInternalSD = internalMean - populationInternalSd;		
//		
//		double zScoreForUser = (internalMean - populationMean) / populationSd;
//		IStatEngineResult statResult;
//		
//		double zScore = (authValue - populationMean) / populationSd;
//		
//		double score = mUtilsStat.CalculateScore(authValue, populationMean, populationSd, internalMean);
//		
//		if(internalSd < populationInternalSd) {
//			double upperInternalSDUser = internalMean + internalSd;
//			double lowerInternalSDUser = internalMean - internalSd;
//			
//			if(authValue > lowerInternalSDUser && authValue < upperInternalSDUser) {
//				statResult = new StatEngineResult(1, zScoreForUser);
//				return statResult;
//			}	
//		}
//		
//		if(authValue > lowerInternalSD && authValue < upperInternalSD) {
//			statResult = new StatEngineResult(0.90, zScoreForUser);
//			return statResult;
//		}
//		
//		if(authValue > GetUpper(internalMean, populationInternalSd, 2) || authValue < GetLower(internalMean, populationInternalSd, 2)) {
//			score -= 0.1;
//		}
//		if(authValue > GetUpper(internalMean, populationInternalSd, 3) || authValue < GetLower(internalMean, populationInternalSd, 3)) {
//			score -= 0.1;
//		}
//		if(authValue > GetUpper(internalMean, populationInternalSd, 4) || authValue < GetLower(internalMean, populationInternalSd, 4)) {
//			score -= 0.1;
//		}
//		
//		if(score < 0) {
//			score = 0;
//		}
//		
//		statResult = new StatEngineResult(score, zScoreForUser);
//		return statResult;	
//	}

	public IStatEngineResult CompareGestureDoubleValues(String instruction, String paramName, double authValue, HashMap<String, IFeatureMeanData> hashFeatureMeans)
	{
		INormData normObj = mNormMgr.GetNormDataByParamName(paramName, instruction);
		
		String key = mUtilsGeneral.GenerateGestureFeatureMeanKey(instruction, paramName);
				
		double populationMean = normObj.GetMean();
		double populationSd = normObj.GetStandardDev();
		double populationInternalSd = normObj.GetInternalStandardDev();		
		
		double internalMean = hashFeatureMeans.get(key).GetMean();
		double internalSd = hashFeatureMeans.get(key).GetInternalSd();
		
		double zScoreForUser = (internalMean - populationMean) / populationSd;
		IStatEngineResult statResult;

		//contribution of user uniqueness 
		double uniquenessFactor = 0.4 * Math.abs(zScoreForUser) + 1;
		
		if(uniquenessFactor > 2) {
			uniquenessFactor = 2;
		}

		double twoUpperPopulationInternalSD = (internalMean + populationInternalSd * uniquenessFactor);
		double twoLowerPopulationInternalSD = (internalMean - populationInternalSd * uniquenessFactor);
		
		double pUser;
		
//		double threeUpperPopulationInternalSD = (internalMean + 1.5 * populationInternalSd * uniquenessFactor);
//		double threeLowerPopulationInternalSD = (internalMean - 1.5 * populationInternalSd * uniquenessFactor);		
//		double pAttacker = 1-mUtilsStat.CalculateScore(authValue, populationMean, populationSd, internalMean);
//		
//
//		if((authValue > twoLowerPopulationInternalSD) && (authValue < twoUpperPopulationInternalSD)) {
//			pUser = 1;
//		}
//		else if((authValue > threeLowerPopulationInternalSD) && (authValue < threeUpperPopulationInternalSD)){
//			if(authValue > internalMean){
//				pUser = (threeUpperPopulationInternalSD - authValue) / populationInternalSd ;
//			}
//			else{
//				pUser = (authValue - threeLowerPopulationInternalSD) / populationInternalSd ;
//			}
//		}
//		else{
//			pUser = 0;
//		}
//		
//		double score = pUser;
//		
//		if(uniquenessFactor > 1) {
//			score = score * (1-pAttacker);	
//		}

		double score;
		if((authValue > twoLowerPopulationInternalSD) && (authValue < twoUpperPopulationInternalSD)) {
			score = 1;
		}
		else {
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

		double zScoreForUser = 1;
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