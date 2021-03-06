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
import Logic.Utils.DTW.DTWObjSampling;
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
	
	public IStatEngineResult CompareStrokeDoubleValues(String instruction, String paramName, int strokeIdx, int strokeKey, double authValue, HashMap<String, IFeatureMeanData> hashFeatureMeans)
	{		
		
		INormData normObj = mNormMgr.GetNormDataByParamName(paramName, instruction, strokeKey);
				
		String key = mUtilsGeneral.GenerateStrokeFeatureMeanKey(instruction, paramName, strokeIdx);
				
		IStatEngineResult statResult = CompareDoubleValues(authValue, paramName, normObj, key, hashFeatureMeans);
		return statResult;
	}	
	
	protected IStatEngineResult CompareDoubleValues(double authValue, String paramName, INormData normObj, String key, HashMap<String, IFeatureMeanData> hashFeatureMeans) {
		double popMean = normObj.GetMean();
		double popSd = normObj.GetStandardDev();
		
		double internalMean = hashFeatureMeans.get(key).GetMean();
		double userInternalStd = hashFeatureMeans.get(key).GetInternalSd();
		double avgPopinternalStd = normObj.GetInternalStandardDev();
		
		double zScore = (internalMean - popMean) / popSd;
		double weight = Math.abs(zScore);
		if(weight > 2) {
			weight = 2;
		}
		
		double boundaryAdj = NormMgr.GetInstance().GetStoredMetaDataMgr().GetParamBoundary(key, popMean, avgPopinternalStd); //Utils.GetInstance().GetUtilsGeneral().GetBoundaryAdj(paramName);
		
		double score = Utils.GetInstance().GetUtilsStat().CalculateScore(authValue, popMean, popSd, internalMean, avgPopinternalStd, boundaryAdj);
				
		double temp = popMean * avgPopinternalStd;
		double temp1 = userInternalStd / temp;
		
		NormMgr.GetInstance().GetStoredMetaDataMgr().UpdateParamBoundary(key, popMean, avgPopinternalStd, userInternalStd);
		
		weight = Utils.GetInstance().GetUtilsStat().CalcWeight(internalMean, avgPopinternalStd, popMean, popSd, boundaryAdj);
		IStatEngineResult statResult = new StatEngineResult(score, zScore, temp1);
		statResult.SetBoundary(boundaryAdj);
		return statResult;
	}
	
	public ArrayList<IDTWObj> GetSpatialVector(String instruction, String paramName, int idxStroke, ArrayList<MotionEventExtended> listEvents, String spatialType) {
		
		ArrayList<IDTWObj> listSpatialVector = new ArrayList<>();
		NormMgr normMgr = (NormMgr) NormMgr.GetInstance();	
		
		double popMean = 0;
		double popSd = 0;
		
		double currWeight;
		
		double zScore;
		
		for(int idx = 0; idx < Consts.ConstsGeneral.SPATIAL_SAMPLING_SIZE - 1; idx++) {			
			
			switch(spatialType) {
				case ConstsParamNames.Stroke.STROKE_SPATIAL_SAMPLING:
					popMean = normMgr.NormContainerMgr.GetSpatialPopMeanDistance(instruction, paramName, idxStroke, idx);
					popSd = normMgr.NormContainerMgr.GetSpatialPopSdDistance(instruction, paramName, idxStroke, idx);											
				break;
				case ConstsParamNames.Stroke.STROKE_TEMPORAL_SAMPLING:
					popMean = normMgr.NormContainerMgr.GetSpatialPopMeanTime(instruction, paramName, idxStroke, idx);
					popSd = normMgr.NormContainerMgr.GetSpatialPopSdTime(instruction, paramName, idxStroke, idx);									
				break;
			}
			
			zScore = (listEvents.get(idx).GetParamByName(paramName) - popMean) / popSd;
			
			listSpatialVector.add(new DTWObjSampling(listEvents.get(idx).GetParamByName(paramName), idxStroke, idx, instruction, paramName, spatialType));
			
			currWeight = Math.abs(zScore);
			if(currWeight > 2) {
				currWeight = 2;
			}			
		}
		
		return listSpatialVector;
	}
	
	public ArrayList<IStatEngineResult> CompareStrokeSpatial(String instruction, String paramName, int idxStroke, ArrayList<MotionEventExtended> listAuthOriginal, String spatialType, HashMap<String, IFeatureMeanData> hashFeatureMeans) {
		
		String key = mUtilsGeneral.GenerateStrokeFeatureMeanKey(instruction, spatialType, idxStroke);
		FeatureMeanDataListEvents tempFeatureData = (FeatureMeanDataListEvents) hashFeatureMeans.get(key);
		ArrayList<MotionEventExtended> listMean = tempFeatureData.GetAvgVector();
		
		NormMgr normMgr = (NormMgr) NormMgr.GetInstance();
		
		double tempValue;		
		
		double popMean = 0;
		double popSd = 0;
		double internalSd = 0;
		
		double valueAuth, valueStored;
		
		ArrayList<IStatEngineResult> listResults = new ArrayList<>();
		int idxSpatial;
			
		ArrayList<MotionEventExtended> listAuth = new ArrayList<>();
		for(int idx = 0; idx < listAuthOriginal.size(); idx++) {
			listAuth.add(listAuthOriginal.get(idx).Clone());
		}
		
		int idxShift = Utils.GetInstance().GetUtilsComparison().GetShiftIdx(listMean, listAuth);
		if(idxShift > 0) {
			for(int idx = 0; idx < idxShift; idx++) {
				listAuth.remove(0);
				listMean.remove(listMean.size() - 1);
			}
		}
		else {
			for(int idx = 0; idx < (idxShift * -1); idx++) {
				listAuth.remove(listAuth.size() - 1);
				listMean.remove(0);
			}
		}		
		
		for(int idxEvent = 1; idxEvent < listMean.size(); idxEvent++) {		
			idxSpatial = idxEvent;													
			
			switch(spatialType) {
				case ConstsParamNames.Stroke.STROKE_SPATIAL_SAMPLING:
					popMean = normMgr.NormContainerMgr.GetSpatialPopMeanDistance(instruction, paramName, idxStroke, idxSpatial);
					popSd = normMgr.NormContainerMgr.GetSpatialPopSdDistance(instruction, paramName, idxStroke, idxSpatial);
					internalSd = normMgr.NormContainerMgr.GetSpatialInternalSdDistance(instruction, paramName, idxStroke, idxSpatial);
						
				break;
				case ConstsParamNames.Stroke.STROKE_TEMPORAL_SAMPLING:
					popMean = normMgr.NormContainerMgr.GetSpatialPopMeanTime(instruction, paramName, idxStroke, idxSpatial);
					popSd = normMgr.NormContainerMgr.GetSpatialPopSdTime(instruction, paramName, idxStroke, idxSpatial);
					internalSd = normMgr.NormContainerMgr.GetSpatialInternalSdTime(instruction, paramName, idxStroke, idxSpatial);					
				break;
			}		
			valueAuth = listAuth.get(idxEvent).GetParamByName(paramName);
			valueStored = listMean.get(idxEvent).GetParamByName(paramName);
			tempValue = Utils.GetInstance().GetUtilsStat().CalculateScoreSpatial(valueAuth, popMean, popSd, valueStored, internalSd, paramName, spatialType);
			
			listResults.add(new StatEngineResult(tempValue, 1, 1));
		}
		
		return listResults;
	}	

	public double GetScoreSpatial(double popMean, double popSd, double internalSd, double authValue, double storedValue) {
		double populationMean = popMean;
		double populationSd = popSd;
		double populationInternalSd = internalSd;		
		
		double internalMean = storedValue;		
		
		double zScoreForUser = (internalMean - populationMean) / populationSd;		

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
		
		//contribution of user uniqueness 
		double uniquenessFactor = 0.4 * Math.abs(zScoreForUser) + 1;
		
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