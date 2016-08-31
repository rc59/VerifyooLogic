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
import Logic.Utils.DTW.DTWObjDouble;
import Logic.Utils.DTW.IDTWObj;

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
		  
		  mHashMapScaleParams.put(Consts.ConstsParamNames.Stroke.STROKE_LENGTH, true);
		  mHashMapScaleParams.put(Consts.ConstsParamNames.Stroke.STROKE_TOTAL_AREA, true);
		  mHashMapScaleParams.put(Consts.ConstsParamNames.Stroke.STROKE_TOTAL_AREA_MINX_MINY, true);
		  mHashMapScaleParams.put(Consts.ConstsParamNames.Stroke.STROKE_TIME_INTERVAL, true);
		  mHashMapScaleParams.put(Consts.ConstsParamNames.Stroke.STROKE_TRANSITION_TIME, true);
		  mHashMapScaleParams.put(Consts.ConstsParamNames.Stroke.STROKE_NUM_EVENTS, true);
		  
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
	
	public IStatEngineResult CompareGestureDoubleValues(String instruction, String paramName, double authValue, HashMap<String, IFeatureMeanData> hashFeatureMeans)
	{
		INormData normObj = mNormMgr.GetNormDataByParamName(paramName, instruction);		
		String key = mUtilsGeneral.GenerateGestureFeatureMeanKey(instruction, paramName);

		IStatEngineResult statResult = CompareDoubleValues(authValue, paramName, normObj, key, hashFeatureMeans);
		return statResult;
	}
	
	public IStatEngineResult CompareStrokeDoubleValues(String instruction, String paramName, int strokeIdx, double authValue, HashMap<String, IFeatureMeanData> hashFeatureMeans)
	{		
		INormData normObj = mNormMgr.GetNormDataByParamName(paramName, instruction, strokeIdx);
				
		String key = mUtilsGeneral.GenerateStrokeFeatureMeanKey(instruction, paramName, strokeIdx);
				
		IStatEngineResult statResult = CompareDoubleValues(authValue, paramName, normObj, key, hashFeatureMeans);
		return statResult;
	}	
	
	protected IStatEngineResult CompareDoubleValues(double authValue, String paramName, INormData normObj, String key, HashMap<String, IFeatureMeanData> hashFeatureMeans) {
		double popMean = normObj.GetMean();
		double popSd = normObj.GetStandardDev();
		
		double internalMean = hashFeatureMeans.get(key).GetMean();
		double internalSd = normObj.GetInternalStandardDev();
		double internalSdUserOnly = hashFeatureMeans.get(key).GetInternalSd();			
		
		double zScore = (internalMean - popMean) / popSd;
		double weight = Math.abs(zScore);
		if(weight > 2) {
			weight = 2;
		}
		
		if(mHashMapScaleParams.containsKey(paramName)) {
			double factor = internalMean / popMean; 
			if(factor < 1) {
				factor = 1;
			}
			if(factor > 2) {
				factor = 2;
			}
			internalSd = internalSd * factor;
		}
		
		double score = Utils.GetInstance().GetUtilsStat().CalculateScore(authValue, popMean, popSd, internalMean, internalSd);
		
		weight = Utils.GetInstance().GetUtilsStat().CalcWeight(internalMean, internalSd, popMean, popSd);
		IStatEngineResult statResult = new StatEngineResult(score, zScore, weight);
		return statResult;
	}
	
	public ArrayList<IDTWObj> GetSpatialVector(String instruction, String paramName, int idxStroke, ArrayList<MotionEventExtended> listEvents, String spatialType) {
		
		ArrayList<IDTWObj> listSpatialVector = new ArrayList<>();
		NormMgr normMgr = (NormMgr) NormMgr.GetInstance();	
		
		double popMean = 0;
		double popSd = 0;
		
		double currWeight;
		
		double zScore;
		
		for(int idx = 1; idx < Consts.ConstsGeneral.SPATIAL_SAMPLING_SIZE - 1; idx++) {			
			
			switch(spatialType) {
				case ConstsParamNames.SpatialTypes.DISTANCE:
					popMean = normMgr.NormContainerMgr.GetSpatialPopMeanDistance(instruction, paramName, idxStroke, idx);
					popSd = normMgr.NormContainerMgr.GetSpatialPopSdDistance(instruction, paramName, idxStroke, idx);											
				break;
				case ConstsParamNames.SpatialTypes.TIME:
					popMean = normMgr.NormContainerMgr.GetSpatialPopMeanTime(instruction, paramName, idxStroke, idx);
					popSd = normMgr.NormContainerMgr.GetSpatialPopSdTime(instruction, paramName, idxStroke, idx);									
				break;
			}
			
			zScore = (listEvents.get(idx).GetParamByName(paramName) - popMean) / popSd;
			
			listSpatialVector.add(new DTWObjDouble(zScore));
			
			currWeight = Math.abs(zScore);
			if(currWeight > 2) {
				currWeight = 2;
			}			
		}
		
		return listSpatialVector;
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
		ArrayList<ArrayList<IStatEngineResult>> listAllResults = new ArrayList<>();
		
		int popCount = 0;
		int internalCount = 0;
		
		double zScore;
		
		int idxVectorStart = 1;
		int idxShiftEnd = 8;
		int idxShiftStart = idxVectorStart;
		int numShifts = idxShiftEnd - idxShiftStart;
		int idxSpatial;
		
		double scoreMax = Double.MIN_VALUE;
		double scoreTemp;
		int scoreMaxIdx = 0;
		ArrayList<Double> listTempScores = new ArrayList<>();
		
		for(int idxShift = 0; idxShift < numShifts; idxShift++) {
			idxSpatial = idxVectorStart;
			listResults = new ArrayList<>();
			for(int idx = idxShiftStart; idx < Consts.ConstsGeneral.SPATIAL_SAMPLING_SIZE - idxShiftEnd - 1; idx++) {			
				
				switch(spatialType) {
					case ConstsParamNames.SpatialTypes.DISTANCE:
						popMean = normMgr.NormContainerMgr.GetSpatialPopMeanDistance(instruction, paramName, idxStroke, idxSpatial);
						popSd = normMgr.NormContainerMgr.GetSpatialPopSdDistance(instruction, paramName, idxStroke, idxSpatial);
						internalSd = normMgr.NormContainerMgr.GetSpatialInternalSdDistance(instruction, paramName, idxStroke, idxSpatial);
							
					break;
					case ConstsParamNames.SpatialTypes.TIME:
						popMean = normMgr.NormContainerMgr.GetSpatialPopMeanTime(instruction, paramName, idxStroke, idxSpatial);
						popSd = normMgr.NormContainerMgr.GetSpatialPopSdTime(instruction, paramName, idxStroke, idxSpatial);
						internalSd = normMgr.NormContainerMgr.GetSpatialInternalSdTime(instruction, paramName, idxStroke, idxSpatial);					
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
				
				zScore = (valueStored - popMean) / popSd;
				listWeights[idx] = zScore;
				
				currWeight = Utils.GetInstance().GetUtilsStat().CalcWeight(valueStored, internalSd, popMean, popSd);
				
//				tempValue = GetScoreSpatial(popMean, popSd, internalSd, valueAuth, valueStored);
				tempValue = Utils.GetInstance().GetUtilsStat().CalculateScore(valueAuth, popMean, popSd, valueStored, internalSd);
//				tempValuePercentage = Utils.GetInstance().GetUtilsMath().GetPercentageDiff(valueAuth, valueStored);
							
				totalValues += (tempValue * currWeight);
				weights += currWeight;
				
				listResults.add(new StatEngineResult(tempValue, zScore, currWeight));
				//listResults.add(new StatEngineResult(tempValuePercentage, 1));
				idxSpatial++;
				scores[idx] = tempValue;
//				percentages[idx] = tempValuePercentage;			
			}
			
			scoreTemp = Utils.GetInstance().GetUtilsComparison().GetTotalSpatialScore(listResults);
			listTempScores.add(scoreTemp);
			if(scoreTemp > scoreMax) {
				scoreMax = scoreTemp;
				scoreMaxIdx = listAllResults.size();
			}
			
			idxShiftStart++;
			idxShiftEnd--;
			
			listAllResults.add(listResults);
		}		
		return listAllResults.get(scoreMaxIdx);
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
		statResult = new StatEngineResult(score, zScoreForUser, zScoreForUser);
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