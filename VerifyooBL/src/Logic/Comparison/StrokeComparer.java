package Logic.Comparison;

import java.util.ArrayList;
import java.util.Collections;
import java.util.Comparator;
import java.util.HashMap;

import Consts.ConstsFeatures;
import Consts.ConstsParamNames;
import Consts.ConstsParamWeights;
import Consts.Enums.PointStatus;
import Data.Comparison.CompareResultGeneric;
import Data.Comparison.CompareResultParamVectors;
import Data.Comparison.CompareResultSummary;
import Data.Comparison.Interfaces.ICompareResult;
import Data.UserProfile.Extended.MotionEventExtended;
import Data.UserProfile.Extended.StrokeExtended;
import Data.UserProfile.Raw.Stroke;
import Logic.Comparison.Stats.FeatureMeanDataListEvents;
import Logic.Comparison.Stats.StatEngine;
import Logic.Comparison.Stats.Data.Interface.IStatEngineResult;
import Logic.Comparison.Stats.Interfaces.IFeatureMeanData;
import Logic.Comparison.Stats.Interfaces.IStatEngine;
import Logic.Comparison.Stats.Norms.NormData;
import Logic.Comparison.Stats.Norms.NormMgr;
import Logic.Comparison.Stats.Norms.Interfaces.INormData;
import Logic.Utils.Complex;
import Logic.Utils.FFT;
import Logic.Utils.Utils;
import Logic.Utils.UtilsComparison;
import Logic.Utils.UtilsGeneral;
import Logic.Utils.UtilsVectors;
import Logic.Utils.DTW.DTWObjCoordinate;
import Logic.Utils.DTW.DTWObjDouble;
import Logic.Utils.DTW.DTWObjMotionEvent;
import Logic.Utils.DTW.IDTWObj;
import Logic.Utils.DTW.UtilsDTW;

public class StrokeComparer {
	protected boolean mIsStrokesIdentical;
	
	protected IStatEngine mStatEngine;
	
	protected UtilsGeneral mUtilsGeneral;
	protected UtilsVectors mUtilsVectors;	
	protected UtilsComparison mUtilsComparison;
	
	protected CompareResultSummary mCompareResult;
	
	protected StrokeExtended mStrokeStoredExtended;
	protected StrokeExtended mStrokeAuthExtended;		
	protected double mMinCosineDistanceScore;
	protected boolean mIsSimilarDevices;
	
	public double DtwScoreToRemove;
	
	public double DtwCoordinates;
	public double DtwNormalizedCoordinates;
	public double DtwNormalizedCoordinatesSpatialDistance;
	public double DtwEvents;
	public double DtwVelocities;
	public double DtwAccelerations;	
	
	public double DtwSpatialTotalScore;
	
	public double DtwSpatialVelocity;
	public double DtwSpatialAcceleration;
	public double DtwSpatialRadialVelocity;
	public double DtwSpatialRadialAcceleration;
	public double DtwSpatialRadius;
	public double DtwSpatialTeta;
	public double DtwSpatialDeltaTeta;
	public double DtwSpatialAccumNormArea;
	
	public double DtwTemporalVelocity;
	public double DtwTemporalAcceleration;
	public double DtwTemporalRadialVelocity;
	public double DtwTemporalRadialAcceleration;
	public double DtwTemporalRadius;
	public double DtwTemporalTeta;
	public double DtwTemporalDeltaTeta;
	public double DtwTemporalAccumNormArea;
	
	public double StrokeDistanceTotalScoreStartToStart;
	public double StrokeDistanceTotalScoreStartToEnd;
	public double StrokeDistanceTotalScoreEndToStart;
	public double StrokeDistanceTotalScoreEndToEnd;
	
	public double StrokeDistanceTotalScore;
	
	/*********************************** Spatial Scores ***********************************/
	
	public double SpatialScoreVelocity;
	public double SpatialScoreAcceleration;
	public double SpatialScoreRadialVelocity;
	public double SpatialScoreRadialAcceleration;
	public double SpatialScoreRadius;
	public double SpatialScoreTeta;
	public double SpatialScoreDeltaTeta;
	public double SpatialScoreAccumulatedNormArea;
	
	public double TemporalScoreVelocity;
	public double TemporalScoreAcceleration;
	public double TemporalScoreRadialVelocity;	
	public double TemporalScoreRadialAcceleration;
	public double TemporalScoreRadius;
	public double TemporalScoreTeta;
	public double TemporalScoreDeltaTeta;
	public double TemporalScoreAccumulatedNormArea;
	
	public double MiddlePressureScore;
	public double MiddleSurfaceScore;
	
	public ArrayList<IStatEngineResult> mListSpatialScores;
	public double StrokeSpatialScore;
	
	public StrokeComparer(boolean isSimilarDevices)
	{			
		mIsSimilarDevices = isSimilarDevices;
		mCompareResult = new CompareResultSummary();
		mStatEngine = StatEngine.GetInstance();
		InitUtils();
	}
	
	protected void InitUtils()
	{
		mListSpatialScores = new ArrayList<>();
		mUtilsGeneral = Utils.GetInstance().GetUtilsGeneral();
		mUtilsVectors = Utils.GetInstance().GetUtilsVectors();
		mUtilsComparison = Utils.GetInstance().GetUtilsComparison();
	}
	
	public PointStatus CheckPoints() {
		PointStatus pointStatus = PointStatus.BOTH;
		
		if(!mStrokeStoredExtended.IsPoint && !mStrokeAuthExtended.IsPoint) {
			pointStatus = PointStatus.NONE;
		}
		
		if(!mStrokeStoredExtended.IsPoint && mStrokeAuthExtended.IsPoint) {
			pointStatus = PointStatus.ONE;
		}
		
		if(mStrokeStoredExtended.IsPoint && !mStrokeAuthExtended.IsPoint) {
			pointStatus = PointStatus.ONE;
		}
		
		return pointStatus;
	}
	
	public void CompareStrokeShapes(StrokeExtended strokeStored, StrokeExtended strokeAuth)	
	{		
		mStrokeStoredExtended = strokeStored;
		mStrokeAuthExtended = strokeAuth;
		PointStatus pointStatus= CheckPoints();
		
		if(pointStatus == PointStatus.NONE) {
			mIsStrokesIdentical = true;			
			
			CheckIfStrokesAreIdentical();
			
			if(!mIsStrokesIdentical) {								
				CompareMinCosineDistance();
			}
		}
		
		if(pointStatus == PointStatus.BOTH) {
			mCompareResult.Score = 1;
		}
		if(pointStatus == PointStatus.ONE) {
			mCompareResult.Score = 0;
		}		
	}	
	
	public void CompareStrokes(StrokeExtended strokeStored, StrokeExtended strokeAuth)	
	{		
		mStrokeStoredExtended = strokeStored;
		mStrokeAuthExtended = strokeAuth;
		PointStatus pointStatus= CheckPoints();
		
		if(pointStatus == PointStatus.NONE) {
			mIsStrokesIdentical = true;			
			
			CheckIfStrokesAreIdentical();
			
			if(!mIsStrokesIdentical) {				
				CompareStrokeDistances();
				CreateSpatialVectorsForDtwAnalysis();
				CompareMinCosineDistance();
				CompareStrokeAreas();
				CompareTimeInterval();
				CompareNumEvents();
				CompareVelocities();
				ComparePressureAndSurface();
				CompareStrokeTransitionTimes();
				CompareAccelerations();
				CompareVectors();				
				CalculateFinalScore();	
				CheckFinalScore();
			}
		}
		
		if(pointStatus == PointStatus.BOTH) {
			mCompareResult.Score = 1;
		}
		if(pointStatus == PointStatus.ONE) {
			mCompareResult.Score = 0;
		}		
	}			
	
	protected double CheckValues(double totalToRemove, double lowerBoundary, double upperBoundary, double value, boolean isHigherBetter) {
		if(!isHigherBetter) {
			value = -1 * value;
			double tempValue = upperBoundary;
			
			upperBoundary = -1 * lowerBoundary;
			lowerBoundary = -1 * tempValue;
		}
		
		double scoreToRemove = 0;
		if(value < upperBoundary) {
			scoreToRemove = totalToRemove;
			if(value > lowerBoundary) {
				double diff = upperBoundary - value;
				double propScore = diff / (upperBoundary - lowerBoundary);
				scoreToRemove = scoreToRemove * propScore;
			}			
		}
		
		return scoreToRemove;
	}
	
	protected void CompareStrokeDistances() {				
		StrokeDistanceTotalScoreStartToStart = 1;
		StrokeDistanceTotalScoreStartToEnd = 1;
		StrokeDistanceTotalScoreEndToStart = 1;
		StrokeDistanceTotalScoreEndToEnd = 1;
		StrokeDistanceTotalScore = 1;	
		
		if(mStrokeAuthExtended.GetStrokeIdx() > 0) {
			StrokeDistanceTotalScoreStartToStart = Utils.GetInstance().GetUtilsMath().GetPercentageDiff(mStrokeAuthExtended.StrokeDistanceStartToStart, mStrokeStoredExtended.StrokeDistanceStartToStart);
			StrokeDistanceTotalScoreStartToEnd = Utils.GetInstance().GetUtilsMath().GetPercentageDiff(mStrokeAuthExtended.StrokeDistanceStartToEnd, mStrokeStoredExtended.StrokeDistanceStartToEnd);
			StrokeDistanceTotalScoreEndToStart = Utils.GetInstance().GetUtilsMath().GetPercentageDiff(mStrokeAuthExtended.StrokeDistanceEndToStart, mStrokeStoredExtended.StrokeDistanceEndToStart);
			StrokeDistanceTotalScoreEndToEnd = Utils.GetInstance().GetUtilsMath().GetPercentageDiff(mStrokeAuthExtended.StrokeDistanceEndToEnd, mStrokeStoredExtended.StrokeDistanceEndToEnd);
						
			StrokeDistanceTotalScore = StrokeDistanceTotalScoreStartToStart;
			StrokeDistanceTotalScore += StrokeDistanceTotalScoreStartToEnd;
			StrokeDistanceTotalScore += StrokeDistanceTotalScoreEndToStart;
			StrokeDistanceTotalScore += StrokeDistanceTotalScoreEndToEnd;
			StrokeDistanceTotalScore = StrokeDistanceTotalScore /4;
		}		
	}
	
	protected void CheckFinalScore() {
		if(mCompareResult.Score < 0) {
			mCompareResult.Score = 0;
		}
		if(mCompareResult.Score > 1) {
			mCompareResult.Score = 1;
		}
	}

	private double CalculateDtwForSamplingVector(String paramName, String samplingType, double factor) {		
		String key = mUtilsGeneral.GenerateStrokeFeatureMeanKey(mStrokeStoredExtended.GetInstruction(), samplingType, mStrokeStoredExtended.GetStrokeIdx());
		FeatureMeanDataListEvents tempFeatureDtw = (FeatureMeanDataListEvents) mStrokeStoredExtended.GetFeatureMeansHash().get(key);
		
		ArrayList<MotionEventExtended> tempListEvents = null;

		switch(samplingType) {
			case ConstsParamNames.Stroke.STROKE_SPATIAL_SAMPLING:
				tempListEvents = mStrokeAuthExtended.ListEventsSpatialExtended;
					
			break;
			case ConstsParamNames.Stroke.STROKE_TEMPORAL_SAMPLING:
				tempListEvents = mStrokeAuthExtended.ListEventsTemporalExtended;				
			break;
		}		
		
		double minDistance = tempFeatureDtw.GetMinDtwDistance(tempListEvents, paramName);
		
		double result = (1 - minDistance) * factor;
		if(result > 1) {
			result = 1;
		}
		
		return result;
	}
	
	private void CreateSpatialVectorsForDtwAnalysis() {		
		DtwSpatialVelocity = CalculateDtwForSamplingVector(ConstsParamNames.StrokeSampling.VELOCITIES, ConstsParamNames.Stroke.STROKE_SPATIAL_SAMPLING, 1);
//		DtwSpatialTeta = CalculateDtwForSamplingVector(ConstsParamNames.StrokeSampling.TETA, ConstsParamNames.Stroke.STROKE_SPATIAL_SAMPLING, 1.63);
//		DtwSpatialRadialVelocity = CalculateDtwForSamplingVector(ConstsParamNames.StrokeSampling.RADIAL_VELOCITIES, ConstsParamNames.Stroke.STROKE_SPATIAL_SAMPLING, 3.88);		
//		DtwSpatialAcceleration = CalculateDtwForSamplingVector(ConstsParamNames.StrokeSampling.ACCELERATIONS, ConstsParamNames.Stroke.STROKE_SPATIAL_SAMPLING, 4.4);	
//		DtwSpatialRadialAcceleration = CalculateDtwForSamplingVector(ConstsParamNames.StrokeSampling.RADIAL_ACCELERATION, ConstsParamNames.Stroke.STROKE_SPATIAL_SAMPLING, 3.72);
//		DtwSpatialRadius = CalculateDtwForSamplingVector(ConstsParamNames.StrokeSampling.RADIUS, ConstsParamNames.Stroke.STROKE_SPATIAL_SAMPLING , 1);		
//		DtwSpatialDeltaTeta = CalculateDtwForSamplingVector(ConstsParamNames.StrokeSampling.DELTA_TETA, ConstsParamNames.Stroke.STROKE_SPATIAL_SAMPLING, 1.9);
//		DtwSpatialAccumNormArea = CalculateDtwForSamplingVector(ConstsParamNames.StrokeSampling.ACCUMULATED_NORM_AREA, ConstsParamNames.Stroke.STROKE_SPATIAL_SAMPLING, 1);		
		
		DtwSpatialTotalScore = DtwSpatialVelocity;
		
//		DtwTemporalVelocity = CalculateDtwForSamplingVector(ConstsParamNames.StrokeSampling.VELOCITIES, ConstsParamNames.Stroke.STROKE_TEMPORAL_SAMPLING, 1);
//		DtwTemporalAcceleration = CalculateDtwForSamplingVector(ConstsParamNames.StrokeSampling.ACCELERATIONS, ConstsParamNames.Stroke.STROKE_TEMPORAL_SAMPLING, 1);
//		DtwTemporalRadialVelocity = CalculateDtwForSamplingVector(ConstsParamNames.StrokeSampling.RADIAL_VELOCITIES, ConstsParamNames.Stroke.STROKE_TEMPORAL_SAMPLING, 1);
//		DtwTemporalRadialAcceleration = CalculateDtwForSamplingVector(ConstsParamNames.StrokeSampling.RADIAL_ACCELERATION, ConstsParamNames.Stroke.STROKE_TEMPORAL_SAMPLING, 1);
//		DtwTemporalRadius = CalculateDtwForSamplingVector(ConstsParamNames.StrokeSampling.RADIUS, ConstsParamNames.Stroke.STROKE_TEMPORAL_SAMPLING, 1);
//		DtwTemporalTeta = CalculateDtwForSamplingVector(ConstsParamNames.StrokeSampling.TETA, ConstsParamNames.Stroke.STROKE_TEMPORAL_SAMPLING, 1);
//		DtwTemporalDeltaTeta = CalculateDtwForSamplingVector(ConstsParamNames.StrokeSampling.DELTA_TETA, ConstsParamNames.Stroke.STROKE_TEMPORAL_SAMPLING, 1);
//		DtwTemporalAccumNormArea = CalculateDtwForSamplingVector(ConstsParamNames.StrokeSampling.ACCUMULATED_NORM_AREA, ConstsParamNames.Stroke.STROKE_TEMPORAL_SAMPLING, 1);
	}
	
	private ArrayList<IDTWObj> CreateSpatialVectorsByParamName(String paramName, StrokeExtended stroke) {
		return GetSpatialVector(paramName, stroke.ListEventsSpatialExtended);					
	}
	
	private ArrayList<IDTWObj> CreateTemporalVectorsByParamName(String paramName, StrokeExtended stroke) {
		return GetTemporalVector(paramName, stroke.ListEventsTemporalExtended);					
	}
	
	private void CompareSpatial() {
		
			
//		VectorSpatialVelocityDistanceStored = GetSpatialVector(ConstsParamNames.StrokeSpatial.VELOCITIES, mStrokeStoredExtended.ListEventsSpatialExtended);
//		VectorSpatialVelocityTimeStored = GetSpatialVector(ConstsParamNames.StrokeSpatial.VELOCITIES, mStrokeStoredExtended.ListEventsTemporalExtended);
		
		SpatialScoreVelocity = CalcSpatialParameter(ConstsParamNames.StrokeSampling.VELOCITIES);
		SpatialScoreAcceleration = CalcSpatialParameter(ConstsParamNames.StrokeSampling.ACCELERATIONS);
		
		SpatialScoreRadialVelocity = CalcSpatialParameter(ConstsParamNames.StrokeSampling.RADIAL_VELOCITIES);	
		SpatialScoreRadialAcceleration = CalcSpatialParameter(ConstsParamNames.StrokeSampling.RADIAL_ACCELERATION);
		SpatialScoreRadius = CalcSpatialParameter(ConstsParamNames.StrokeSampling.RADIUS);
		
		SpatialScoreTeta = CalcSpatialParameter(ConstsParamNames.StrokeSampling.TETA);
		SpatialScoreDeltaTeta = CalcSpatialParameter(ConstsParamNames.StrokeSampling.DELTA_TETA);
		
		SpatialScoreAccumulatedNormArea = CalcSpatialParameter(ConstsParamNames.StrokeSampling.ACCUMULATED_NORM_AREA);
				
		TemporalScoreVelocity = CalcTemporalParameter(ConstsParamNames.StrokeSampling.VELOCITIES);
		TemporalScoreAcceleration = CalcTemporalParameter(ConstsParamNames.StrokeSampling.ACCELERATIONS);
	
		TemporalScoreRadialVelocity = CalcTemporalParameter(ConstsParamNames.StrokeSampling.RADIAL_VELOCITIES);		
		TemporalScoreRadialAcceleration = CalcTemporalParameter(ConstsParamNames.StrokeSampling.RADIAL_ACCELERATION);
		
		TemporalScoreRadius = CalcTemporalParameter(ConstsParamNames.StrokeSampling.RADIUS);
		
		TemporalScoreTeta = CalcTemporalParameter(ConstsParamNames.StrokeSampling.TETA);
		TemporalScoreDeltaTeta = CalcTemporalParameter(ConstsParamNames.StrokeSampling.DELTA_TETA);
		
		TemporalScoreAccumulatedNormArea = CalcTemporalParameter(ConstsParamNames.StrokeSampling.ACCUMULATED_NORM_AREA);
		
 		StrokeSpatialScore = (SpatialScoreRadius + TemporalScoreRadius + SpatialScoreAccumulatedNormArea + TemporalScoreAccumulatedNormArea) / 4; 		
	}
	
	private void FFTCheck(Complex[] resultAuthDistance) {
		ArrayList<Double> listFFT = new ArrayList<>();
		double temp;
		
		for(int idx = 0; idx < resultAuthDistance.length; idx++) {
			temp = resultAuthDistance[idx].re() * resultAuthDistance[idx].re() + resultAuthDistance[idx].im() * resultAuthDistance[idx].im();
			temp = Math.sqrt(temp);
			
			listFFT.add(temp);
		}
		
		Collections.sort(listFFT, new Comparator<Double>() {
            public int compare(Double score1, Double score2) {
                if (score1 > score2) {
                    return -1;
                }
                if (score1 < score2) {
                    return 1;
                }
                return 0;
            }
        });
		
		double highest = listFFT.get(0);
	}

	private double CalculateScoreFromList(ArrayList<IStatEngineResult> listScores) {
		double result = Utils.GetInstance().GetUtilsComparison().GetTotalSpatialScore(listScores);
		return result;
	}
	
	private double CalcSpatialParameter(String parameter) {

		double[] vectorAuthX = Utils.GetInstance().GetUtilsVectors().GetVectorXmm(mStrokeAuthExtended.ListEventsSpatialExtended);
		double[] vectorAuthY = Utils.GetInstance().GetUtilsVectors().GetVectorYmm(mStrokeAuthExtended.ListEventsSpatialExtended);	
		
		ArrayList<IStatEngineResult> listResults = mStatEngine.CompareStrokeSpatial(mStrokeAuthExtended.GetInstruction(), parameter, mStrokeAuthExtended.GetStrokeIdx(), mStrokeAuthExtended.ListEventsSpatialExtended, Consts.ConstsParamNames.Stroke.STROKE_SPATIAL_SAMPLING, mStrokeStoredExtended.GetFeatureMeansHash()); 
		mListSpatialScores.addAll(listResults);
		
		double score = CalculateScoreFromList(listResults);		
		return score;
	}	
	
	private double CalcTemporalParameter(String parameter) {

		ArrayList<IStatEngineResult> listResults = mStatEngine.CompareStrokeSpatial(mStrokeAuthExtended.GetInstruction(), parameter, mStrokeAuthExtended.GetStrokeIdx(), mStrokeAuthExtended.ListEventsTemporalExtended, Consts.ConstsParamNames.Stroke.STROKE_TEMPORAL_SAMPLING, mStrokeStoredExtended.GetFeatureMeansHash());		
		mListSpatialScores.addAll(listResults);
		
		double score = CalculateScoreFromList(listResults);
		return score;
	}

	private ArrayList<IDTWObj> GetTemporalVector(String parameter, ArrayList<MotionEventExtended> listEvents) {
		ArrayList<IDTWObj> vector = mStatEngine.GetSpatialVector(mStrokeAuthExtended.GetInstruction(), parameter, mStrokeAuthExtended.GetStrokeIdx(), listEvents, Consts.ConstsParamNames.Stroke.STROKE_TEMPORAL_SAMPLING); 				
		return vector;
	}		
	
	private ArrayList<IDTWObj> GetSpatialVector(String parameter, ArrayList<MotionEventExtended> listEvents) {
		ArrayList<IDTWObj> vector = mStatEngine.GetSpatialVector(mStrokeAuthExtended.GetInstruction(), parameter, mStrokeAuthExtended.GetStrokeIdx(), listEvents, Consts.ConstsParamNames.Stroke.STROKE_SPATIAL_SAMPLING); 				
		return vector;
	}	
	
	private void CompareVectors() {
//		double[] vectorStoredVelocitiesDistance = Utils.GetInstance().GetUtilsVectors().GetVectorVel(mStrokeStoredExtended.ListEventsSpatialByDistanceExtended);
//		double[] vectorAuthVelocitiesDistance = Utils.GetInstance().GetUtilsVectors().GetVectorVel(mStrokeAuthExtended.ListEventsSpatialByDistanceExtended);
//				
//		double[] vectorStoredVelocitiesTime = Utils.GetInstance().GetUtilsVectors().GetVectorVel(mStrokeStoredExtended.ListEventsSpatialByTimeExtended);
//		double[] vectorAuthVelocitiesTime = Utils.GetInstance().GetUtilsVectors().GetVectorVel(mStrokeAuthExtended.ListEventsSpatialByTimeExtended);
		
		
		
		
		
//		double[] vectorStoredVelocities = new double[mStrokeStoredExtended.ListEventsExtended.size()];
//		double[] vectorAuthVelocities = new double[mStrokeAuthExtended.ListEventsExtended.size()];
//		
//		
//		double[] vectorStoredAcceleration = new double[mStrokeStoredExtended.ListEventsExtended.size()];
//		double[] vectorAuthAcceleration = new double[mStrokeStoredExtended.ListEventsExtended.size()];
//		
//		double[] vectorStoredPressure = new double[mStrokeStoredExtended.ListEventsExtended.size()];
//		double[] vectorAuthPressure = new double[mStrokeStoredExtended.ListEventsExtended.size()];
//		
//		double[] vectorStoredSurface = new double[mStrokeStoredExtended.ListEventsExtended.size()];
//		double[] vectorAuthSurface = new double[mStrokeStoredExtended.ListEventsExtended.size()];
//				
//		double[] vectorStoredVelocitiesX = new double[mStrokeStoredExtended.ListEventsExtended.size()];
//		double[] vectorAuthVelocitiesX = new double[mStrokeStoredExtended.ListEventsExtended.size()];
//		
//		double[] vectorStoredVelocitiesY = new double[mStrokeStoredExtended.ListEventsExtended.size()];
//		double[] vectorAuthVelocitiesY = new double[mStrokeStoredExtended.ListEventsExtended.size()];		
//				
//		for(int idx = 0; idx < mStrokeStoredExtended.ListEventsExtended.size(); idx++) {
//			vectorStoredVelocities[idx] = mStrokeStoredExtended.ListEventsExtended.get(idx).Velocity;
//			vectorAuthVelocities[idx] = mStrokeAuthExtended.ListEventsExtended.get(idx).Velocity;
//						
//			vectorStoredAcceleration[idx] = mStrokeStoredExtended.ListEventsExtended.get(idx).Acceleration;
//			vectorAuthAcceleration[idx] = mStrokeAuthExtended.ListEventsExtended.get(idx).Acceleration;
//			
//			vectorStoredPressure[idx] = mStrokeStoredExtended.ListEventsExtended.get(idx).Pressure;
//			vectorAuthPressure[idx] = mStrokeAuthExtended.ListEventsExtended.get(idx).Pressure;
//			
//			vectorStoredSurface[idx] = mStrokeStoredExtended.ListEventsExtended.get(idx).TouchSurface;
//			vectorAuthSurface[idx] = mStrokeAuthExtended.ListEventsExtended.get(idx).TouchSurface;
//			
//			vectorStoredVelocitiesX[idx] = mStrokeStoredExtended.ListEventsExtended.get(idx).VelocityX;
//			vectorAuthVelocitiesX[idx] = mStrokeAuthExtended.ListEventsExtended.get(idx).VelocityX;
//			
//			vectorStoredVelocitiesY[idx] = mStrokeStoredExtended.ListEventsExtended.get(idx).VelocityY;
//			vectorAuthVelocitiesY[idx] = mStrokeAuthExtended.ListEventsExtended.get(idx).VelocityY;
//		}
		
		double x = 1;
		x++;
	}	
	
	protected double GetSpatialDtwScore(ArrayList<IDTWObj> vectorAuth, ArrayList<IDTWObj> vectorStored) {
		UtilsDTW tempDtw = new UtilsDTW(vectorAuth, vectorStored);
		double result = tempDtw.getDistance();
		
		return result;
	}

	/************** Feature Score Calculations **************/
	protected void CheckIfStrokesAreIdentical()
	{		
		if(mStrokeAuthExtended.ListEventsExtended.size() == mStrokeStoredExtended.ListEventsExtended.size()) {
			for(int idxEvent = 0; idxEvent < mStrokeAuthExtended.ListEventsExtended.size(); idxEvent++) {
				if(mStrokeAuthExtended.ListEventsExtended.get(idxEvent).Xmm != mStrokeStoredExtended.ListEventsExtended.get(idxEvent).Xmm || 
				   mStrokeAuthExtended.ListEventsExtended.get(idxEvent).Ymm != mStrokeStoredExtended.ListEventsExtended.get(idxEvent).Ymm) {
					mIsStrokesIdentical = false;
					break;
				}
			}	
		}
		else {
			mIsStrokesIdentical = false;
		}
	}
	
	protected void CompareStrokeAreas()
	{
		double areaAuth = mStrokeAuthExtended.ShapeDataObj.ShapeArea;
		double areaAuthMinXMinY = mStrokeAuthExtended.ShapeDataObj.ShapeAreaMinXMinY;
				
		CompareParameter(ConstsParamNames.Stroke.STROKE_TOTAL_AREA, areaAuth);
		CompareParameter(ConstsParamNames.Stroke.STROKE_TOTAL_AREA_MINX_MINY, areaAuthMinXMinY);		
	}	
	
	protected void CompareNumEvents()
	{		
		double numEventsAuth = mStrokeAuthExtended.ListEventsExtended.size();
		CompareParameter(ConstsParamNames.Stroke.STROKE_NUM_EVENTS, numEventsAuth);
	}
	
	protected void CompareTimeInterval()
	{
		double timeIntervalAuth = mStrokeAuthExtended.StrokeTimeInterval;
		CompareParameter(ConstsParamNames.Stroke.STROKE_TIME_INTERVAL, timeIntervalAuth);
	}
	
	protected void CompareLengths()
	{
		double lengthAuth = mStrokeAuthExtended.StrokePropertiesObj.LengthMM;
		CompareParameter(ConstsParamNames.Stroke.STROKE_LENGTH, lengthAuth);
	}
	
	protected void CompareAccelerations() {
		double avgAccelerationAuth = mStrokeAuthExtended.StrokeAverageAcceleration;
		double maxAccelerationAuth = mStrokeAuthExtended.StrokeMaxAcceleration;
		
//		CompareParameter(ConstsParamNames.Stroke.STROKE_AVERAGE_ACCELERATION, avgAccelerationAuth);
		CompareParameter(ConstsParamNames.Stroke.STROKE_MAX_ACCELERATION, maxAccelerationAuth);
	}
	
	protected void CompareStrokeTransitionTimes() {
		if(mStrokeAuthExtended.GetStrokeIdx() > 0) {
			double transitionTimeAuth = mStrokeAuthExtended.StrokeTransitionTime;		
			CompareParameter(ConstsParamNames.Stroke.STROKE_TRANSITION_TIME, transitionTimeAuth);	
		}
	}
	
	protected void ComparePressureAndSurface() {
		double middlePressureAuth = mStrokeAuthExtended.MiddlePressure;
		double middleSurfaceAuth = mStrokeAuthExtended.MiddleSurface;
		
		MiddlePressureScore = CompareParameter(ConstsParamNames.Stroke.STROKE_MIDDLE_PRESSURE, middlePressureAuth);
		MiddleSurfaceScore = CompareParameter(ConstsParamNames.Stroke.STROKE_MIDDLE_SURFACE, middleSurfaceAuth);		
	}
	
	protected void CompareVelocities()
	{
		double avgVelocityAuth = mStrokeAuthExtended.StrokeAverageVelocity;
		double maxVelocityAuth = mStrokeAuthExtended.StrokeMaxVelocity;
		double midVelocityAuth = mStrokeAuthExtended.StrokeMidVelocity;
		
		CompareParameter(ConstsParamNames.Stroke.STROKE_AVERAGE_VELOCITY, avgVelocityAuth);
		CompareParameter(ConstsParamNames.Stroke.STROKE_MAX_VELOCITY, maxVelocityAuth);
		CompareParameter(ConstsParamNames.Stroke.STROKE_MID_VELOCITY, midVelocityAuth);		
	}
	
	protected void CalculateFinalScore()
	{		
		double avgScore = 0;
		double totalWeights = 0;
		for(int idx = 0; idx < mCompareResult.ListCompareResults.size(); idx++) {
			avgScore += mCompareResult.ListCompareResults.get(idx).GetValue() * mCompareResult.ListCompareResults.get(idx).GetWeight();
			totalWeights += mCompareResult.ListCompareResults.get(idx).GetWeight();
		}
		
		mCompareResult.Score = avgScore / totalWeights;		
		
		//StrokeSpatialScore = StrokeSpatialScore + 0.15;
		mCompareResult.Score = (mCompareResult.Score + DtwSpatialVelocity) / 2;
		
		double strokeDistance = StrokeDistanceTotalScore + 0.3;
		if(strokeDistance > 1) {
			strokeDistance = 1;
		}		
		double strokeDistanceToRemove = 1 - strokeDistance;
		
		double maxPressureSurface = Utils.GetInstance().GetUtilsMath().GetMaxValue(MiddlePressureScore, MiddleSurfaceScore);
		double removeMiddlePressureScore = (1 - maxPressureSurface) / 10;
		
		mCompareResult.Score -= strokeDistanceToRemove;
//		mCompareResult.Score -= removeMiddlePressureScore;
	}
	
	protected void CompareMinCosineDistance()
	{	
		double[] vectorStored = mStrokeStoredExtended.SpatialSamplingVector;
		double[] vectorAuth = mStrokeAuthExtended.SpatialSamplingVector;
		
		double minimumCosineDistanceScore = 
				mUtilsVectors.MinimumCosineDistanceScore(vectorStored, vectorAuth);	
		
		mMinCosineDistanceScore = minimumCosineDistanceScore;
	}
	/****************************/
	
	/************** Utility Methods **************/
	public double GetScore()
	{
		if(mIsStrokesIdentical) {
			mCompareResult.Score = 1;
		}
		return mCompareResult.Score;
	}	
	
	public double GetMinCosineDistance()
	{
		return mMinCosineDistanceScore;
	}
	
	public CompareResultSummary GetResultsSummary()
	{
		return mCompareResult;
	}
	
	public boolean IsStrokesIdentical()
	{
		return mIsStrokesIdentical;
	}

	protected double CompareParameter(String paramName, double authValue) {
		IStatEngineResult result = 
				mStatEngine.CompareStrokeDoubleValues(mStrokeAuthExtended.GetInstruction(), paramName, mStrokeAuthExtended.GetStrokeIdx(), authValue, mStrokeStoredExtended.GetFeatureMeansHash());
		
		double finalScore = result.GetScore();		
		AddDoubleParameter(paramName, finalScore, result.GetZScore(), authValue);
		return finalScore;
	}
	
	protected void AddDoubleParameter(String parameterName, double score, double weight, double originalValue)
	{	
		double mean = 0;
		double internalSd = 0;
		double popSd = 0;
		double popMean = 0;
		double internalSdUserOnly = 0;
		
		String key = mUtilsGeneral.GenerateStrokeFeatureMeanKey(mStrokeStoredExtended.GetInstruction(), parameterName, mStrokeStoredExtended.GetStrokeIdx());
		
		HashMap<String, IFeatureMeanData> hashFeatureMeans = mStrokeStoredExtended.GetFeatureMeansHash(); 
		
		if(hashFeatureMeans.containsKey(key)) {
			mean = hashFeatureMeans.get(key).GetMean();
			
			INormData normData = NormMgr.GetInstance().GetNormDataByParamName(parameterName, mStrokeStoredExtended.GetInstruction(), mStrokeStoredExtended.GetStrokeIdx());
			internalSd = normData.GetInternalStandardDev();
			popSd = normData.GetStandardDev();
			popMean = normData.GetMean();
			internalSdUserOnly = hashFeatureMeans.get(key).GetInternalSd();
		}
		
		ICompareResult compareResult = 
				(ICompareResult) new CompareResultGeneric(parameterName, score, originalValue, mean, popMean ,popSd, internalSd, internalSdUserOnly);
		mCompareResult.ListCompareResults.add(compareResult);
	}
	/****************************/

}
