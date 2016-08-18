package Logic.Comparison.Stats;

import java.util.ArrayList;
import java.util.Collections;
import java.util.Comparator;
import java.util.HashMap;

import Consts.ConstsParamNames;
import Data.Comparison.Interfaces.ICompareResult;
import Data.UserProfile.Extended.MotionEventExtended;
import Data.UserProfile.Raw.MotionEventCompact;
import Logic.Comparison.Stats.Data.StatEngineResult;
import Logic.Comparison.Stats.Data.Interface.IStatEngineResult;
import Logic.Comparison.Stats.Interfaces.IFeatureMeanData;
import Logic.Comparison.Stats.Interfaces.IStatEngine;
import Logic.Comparison.Stats.Norms.NormMgr;
import Logic.Comparison.Stats.Norms.Interfaces.INormData;
import Logic.Comparison.Stats.Norms.Interfaces.INormMgr;
import Logic.Utils.Utils;
import Logic.Utils.UtilsGeneral;
import Logic.Utils.UtilsMath;
import Logic.Utils.UtilsStat;

public class StatEngine implements IStatEngine {
	
	protected static UtilsStat mUtilsStat;
	protected static UtilsGeneral mUtilsGeneral;
	
	protected static INormMgr mNormMgr;
	protected static IStatEngine mInstance = null;	
	
	protected static HashMap<String, Boolean> mHashMapScaleParams;
	
	protected StatEngine()
	{
		
	}
	
	public static IStatEngine GetInstance() {
      if(mInstance == null) {
		  mInstance = new StatEngine();
		  mNormMgr = NormMgr.GetInstance();
		  mUtilsStat = new UtilsStat();
		  mUtilsGeneral = new UtilsGeneral();
		  
		  mHashMapScaleParams = new HashMap<>();
		  mHashMapScaleParams.put(Consts.ConstsParamNames.Gesture.GESTURE_LENGTH, true);
		  mHashMapScaleParams.put(Consts.ConstsParamNames.Gesture.GESTURE_TOTAL_TIME_INTERVAL, true);
		  mHashMapScaleParams.put(Consts.ConstsParamNames.Gesture.GESTURE_TOTAL_STROKES_TIME_INTERVAL, true);
		  mHashMapScaleParams.put(Consts.ConstsParamNames.Gesture.GESTURE_TOTAL_AREA, true);
		  mHashMapScaleParams.put(Consts.ConstsParamNames.Gesture.GESTURE_TOTAL_AREA_MINX_MINY, true);
		  mHashMapScaleParams.put(Consts.ConstsParamNames.Gesture.GESTURE_TOTAL_STROKE_AREA, true);
		  mHashMapScaleParams.put(Consts.ConstsParamNames.Gesture.GESTURE_TOTAL_STROKE_AREA_MINX_MINY, true);
		  mHashMapScaleParams.put(Consts.ConstsParamNames.Gesture.GESTURE_NUM_EVENTS, true);
      }
      return mInstance;
    }
	
	public ArrayList<IStatEngineResult> CompareStrokeSpatial(String instruction, String paramName, int idxStroke, ArrayList<MotionEventExtended> listAuth, ArrayList<MotionEventExtended> listStored, String spatialType) {
		
		NormMgr normMgr = (NormMgr) NormMgr.GetInstance();	
		
		double totalValues = 0;
		double tempValue;
		double tempValuePercentage;
		
		double popMean = 0;
		double popSd = 0;
		double internalSd = 0;
		
		double[] percentages = new double[Consts.ConstsGeneral.SPATIAL_SAMPLING_SIZE];
		double[] scores = new double[Consts.ConstsGeneral.SPATIAL_SAMPLING_SIZE];
		double[] listWeights = new double[Consts.ConstsGeneral.SPATIAL_SAMPLING_SIZE];
		
		double weights = 0;
		double currWeight;
		
		double valueAuth, valueStored;
		
		ArrayList<IStatEngineResult> listResults = new ArrayList<>();
		
		int popCount = 0;
		int internalCount = 0;
		
		double zScore;
		
		for(int idx = 1; idx < Consts.ConstsGeneral.SPATIAL_SAMPLING_SIZE; idx++) {			
			
			switch(spatialType) {
				case ConstsParamNames.SpatialTypes.DISTANCE:
					popMean = normMgr.NormContainerMgr.GetSpatialPopMeanDistance(instruction, paramName, idxStroke, idx);
					popSd = normMgr.NormContainerMgr.GetSpatialPopSdDistance(instruction, paramName, idxStroke, idx);
					internalSd = normMgr.NormContainerMgr.GetSpatialInternalSdDistance(instruction, paramName, idxStroke, idx);
						
				break;
				case ConstsParamNames.SpatialTypes.TIME:
					popMean = normMgr.NormContainerMgr.GetSpatialPopMeanTime(instruction, paramName, idxStroke, idx);
					popSd = normMgr.NormContainerMgr.GetSpatialPopSdTime(instruction, paramName, idxStroke, idx);
					internalSd = normMgr.NormContainerMgr.GetSpatialInternalSdTime(instruction, paramName, idxStroke, idx);					
				break;
			}
						
			if(popSd > internalSd) {
				popCount++;	
			}
			else {
				internalCount++;
			}
			
			//internalSd = popSd / 3;
			
			valueAuth = listAuth.get(idx).GetParamByName(paramName);
			valueStored = listStored.get(idx).GetParamByName(paramName);
			
			zScore = (listAuth.get(idx).GetParamByName(paramName) - popMean) / popSd;
			listWeights[idx] = zScore;
			
			currWeight = Math.abs(zScore);
			if(currWeight > 2) {
				currWeight = 2;
			}
			
			tempValue = GetScoreSpatial(popMean, popSd, internalSd, valueAuth, valueStored);			
			tempValue = GetScoreSpatial(popMean, popSd, internalSd, valueAuth, valueStored);
			tempValuePercentage = Utils.GetInstance().GetUtilsMath().GetPercentageDiff(valueAuth, valueStored);
						
			totalValues += (tempValue * currWeight);
			weights += currWeight;
			
//			listResults.add(new StatEngineResult(tempValue, currWeight));
			listResults.add(new StatEngineResult(tempValuePercentage, 1));
			
			scores[idx] = tempValue;
			percentages[idx] = tempValuePercentage;
			
		}
		
//		Collections.sort(listResults, new Comparator<IStatEngineResult>() {
//            public int compare(IStatEngineResult score1, IStatEngineResult score2) {
//                if (Math.abs(score1.GetZScore()) > Math.abs(score2.GetZScore())) {
//                    return -1;
//                }
//                if (Math.abs(score1.GetZScore()) < Math.abs(score2.GetZScore())) {
//                    return 1;
//                }
//                return 0;
//            }
//        });
//				
//		int limit = 5;
//		double result = 0;
//		double totalWeights = 0;
//		for(int idx = 0; idx < 5; idx++) {
//			totalWeights += listResults.get(idx).GetZScore();
//			result += listResults.get(idx).GetScore() * listResults.get(idx).GetZScore(); 
//		}
//		
//		double avg = totalValues / weights;
//		
//		result = result / totalWeights;
		return listResults;
	}
	
	private double GetDistance(MotionEventExtended event1, MotionEventExtended event2) {
		UtilsMath utilsMath = new UtilsMath();
		
		double totalScore = 0;
		
		totalScore += utilsMath.GetPercentageDiff(event1.Velocity, event2.Velocity);
		totalScore += utilsMath.GetPercentageDiff(event1.Pressure, event2.Pressure);
		totalScore += utilsMath.GetPercentageDiff(event1.TouchSurface, event2.TouchSurface);
		totalScore += utilsMath.GetPercentageDiff(event1.Acceleration, event2.Acceleration);
//		totalScore += utilsMath.GetPercentageDiff(event1.Xnormalized, event2.Xnormalized);
//		totalScore += utilsMath.GetPercentageDiff(event1.Ynormalized, event2.Ynormalized);		
		
		return totalScore / 4;
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
	
	public double GetScoreSpatial(double popMean, double popSd, double internalSd, double authValue, double storedValue) {
		double populationMean = popMean;
		double populationSd = popSd;
		double populationInternalSd = internalSd;		
		
		double internalMean = storedValue;		
		
		double zScoreForUser = (internalMean - populationMean) / populationSd;
		IStatEngineResult statResult;

		//contribution of user uniqueness 
		double uniquenessFactor = 0.4 * Math.abs(zScoreForUser);
		uniquenessFactor = 1;
		
		if(uniquenessFactor > 2) {
			uniquenessFactor = 2;
		}
				
		double twoUpperPopulationInternalSD = (internalMean + populationInternalSd * uniquenessFactor);
		double twoLowerPopulationInternalSD = (internalMean - populationInternalSd * uniquenessFactor);
		
		double threeUpperPopulationInternalSD = (internalMean + 1.5 * populationInternalSd * uniquenessFactor);
		double threeLowerPopulationInternalSD = (internalMean - 1.5 * populationInternalSd * uniquenessFactor);	

		double score;
		if((authValue > twoLowerPopulationInternalSD) && (authValue < twoUpperPopulationInternalSD)) {
			score = 1;
		}
		else {
			if((authValue > threeLowerPopulationInternalSD) && (authValue < threeUpperPopulationInternalSD)) {
				if(authValue > twoLowerPopulationInternalSD) {
					double u = Math.abs(threeUpperPopulationInternalSD - authValue);
					double d = Math.abs(threeUpperPopulationInternalSD - twoUpperPopulationInternalSD);
					
					score = u/d;					
				}
				else {
					double u = Math.abs(threeLowerPopulationInternalSD - authValue);
					double d = Math.abs(threeLowerPopulationInternalSD - twoLowerPopulationInternalSD);
					
					score = u/d;
				}
			}
			else {
				score = 0;
			}
		}
				
		//statResult = new StatEngineResult(score, zScoreForUser);
		return score;	
	}
	
	public double GetScore(double popMean, double popSd, double internalSd, double authValue, double storedValue) {
		double populationMean = popMean;
		double populationSd = popSd;
		double populationInternalSd = internalSd;		
		
		double internalMean = storedValue;		
		
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
		
		double threeUpperPopulationInternalSD = (internalMean + 1.5 * populationInternalSd * uniquenessFactor);
		double threeLowerPopulationInternalSD = (internalMean - 1.5 * populationInternalSd * uniquenessFactor);		

		double score;
		if((authValue > twoLowerPopulationInternalSD) && (authValue < twoUpperPopulationInternalSD)) {
			score = 1;
		}
		else {
			if((authValue > threeLowerPopulationInternalSD) && (authValue < threeUpperPopulationInternalSD)) {
				if(authValue > twoLowerPopulationInternalSD) {
					double u = Math.abs(threeUpperPopulationInternalSD - authValue);
					double d = Math.abs(threeUpperPopulationInternalSD - twoUpperPopulationInternalSD);
					
					score = u/d;					
				}
				else {
					double u = Math.abs(threeLowerPopulationInternalSD - authValue);
					double d = Math.abs(threeLowerPopulationInternalSD - twoLowerPopulationInternalSD);
					
					score = u/d;
				}
			}
			else {
				score = 0;
			}
		}
				
		//statResult = new StatEngineResult(score, zScoreForUser);
		return score;	
	}

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

		if(mHashMapScaleParams.containsKey(paramName)) {
			double factor = internalMean / populationMean; 
			if(factor < 1) {
				factor = 1;
			}
			populationInternalSd = populationInternalSd * factor;			
		}
				
		double twoUpperPopulationInternalSD = (internalMean + populationInternalSd * uniquenessFactor);
		double twoLowerPopulationInternalSD = (internalMean - populationInternalSd * uniquenessFactor);
		
		double pUser;
		
		double threeUpperPopulationInternalSD = (internalMean + 1.5 * populationInternalSd * uniquenessFactor);
		double threeLowerPopulationInternalSD = (internalMean - 1.5 * populationInternalSd * uniquenessFactor);		
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
			if((authValue > threeLowerPopulationInternalSD) && (authValue < threeUpperPopulationInternalSD)) {
				if(authValue > twoLowerPopulationInternalSD) {
					double u = Math.abs(threeUpperPopulationInternalSD - authValue);
					double d = Math.abs(threeUpperPopulationInternalSD - twoUpperPopulationInternalSD);
					
					score = u/d;					
				}
				else {
					double u = Math.abs(threeLowerPopulationInternalSD - authValue);
					double d = Math.abs(threeLowerPopulationInternalSD - twoLowerPopulationInternalSD);
					
					score = u/d;
				}
			}
			else {
				score = 0;
			}
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