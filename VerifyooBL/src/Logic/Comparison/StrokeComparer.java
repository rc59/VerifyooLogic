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
import Data.UserProfile.Extended.StrokeExtended;
import Data.UserProfile.Raw.Stroke;
import Logic.Comparison.Stats.StatEngine;
import Logic.Comparison.Stats.Data.Interface.IStatEngineResult;
import Logic.Comparison.Stats.Interfaces.IFeatureMeanData;
import Logic.Comparison.Stats.Interfaces.IStatEngine;
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
	
	public double DtwDistanceCoordinates;
	public double DtwDistanceNormalizedCoordinates;
	public double DtwDistanceEvents;
	public double DtwDistanceVelocities;
	public double DtwDistanceAccelerations;

	public double SpatialScoreDistanceVelocity;
	public double SpatialScoreDistanceAcceleration;
	public double SpatialScoreDistanceRadialVelocity;
	
	public double SpatialScoreDistanceRadialAcceleration;
	public double SpatialScoreDistanceRadius;
	public double SpatialScoreDistanceTeta;
	public double SpatialScoreDistanceDeltaTeta;
	public double SpatialScoreDistanceAccumulatedNormArea;
	
	public double SpatialScoreTimeVelocity;
	public double SpatialScoreTimeAcceleration;
	public double SpatialScoreTimeRadialVelocity;
	
	public double SpatialScoreTimeRadialAcceleration;
	public double SpatialScoreTimeRadius;
	public double SpatialScoreTimeTeta;
	public double SpatialScoreTimeDeltaTeta;
	public double SpatialScoreTimeAccumulatedNormArea;
	
	public ArrayList<IStatEngineResult> mListSpatialScores;
	public double SpatialScore;
	
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
	
	public void CompareStrokes(StrokeExtended strokeStored, StrokeExtended strokeAuth)	
	{		
		mStrokeStoredExtended = strokeStored;
		mStrokeAuthExtended = strokeAuth;
		PointStatus pointStatus= CheckPoints();
		
		if(pointStatus == PointStatus.NONE) {
			mIsStrokesIdentical = true;			
			
			CheckIfStrokesAreIdentical();
			
			if(!mIsStrokesIdentical) {				
				CompareSpatial();
				CompareMinCosineDistance();
				CompareStrokeAreas();
				CompareTimeInterval();
				CompareAvgVelocity();
				//CompareVectors();
				TimeWarp();
				CalculateFinalScore();	
			}
		}
		
		if(pointStatus == PointStatus.BOTH) {
			mCompareResult.Score = 1;
		}
		if(pointStatus == PointStatus.ONE) {
			mCompareResult.Score = 0;
		}		
	}		

	private void CompareSpatial() {
		
		double[] velocitiesAuthDistance = mUtilsVectors.GetVectorVel(mStrokeAuthExtended.ListEventsSpatialByDistanceExtended);
		double[] velocitiesStoredDistance = mUtilsVectors.GetVectorVel(mStrokeStoredExtended.ListEventsSpatialByDistanceExtended);
		
		double[] velocitiesAuthTime = mUtilsVectors.GetVectorVel(mStrokeAuthExtended.ListEventsSpatialByTimeExtended);
		double[] velocitiesStoredTime = mUtilsVectors.GetVectorVel(mStrokeStoredExtended.ListEventsSpatialByTimeExtended);
		
		SpatialScoreDistanceVelocity = CalcDistanceSpatialByParameter(ConstsParamNames.StrokeSpatial.VELOCITIES);
		SpatialScoreDistanceAcceleration = CalcDistanceSpatialByParameter(ConstsParamNames.StrokeSpatial.ACCELERATIONS);
		SpatialScoreDistanceRadialVelocity = CalcDistanceSpatialByParameter(ConstsParamNames.StrokeSpatial.RADIAL_VELOCITIES);
		
		SpatialScoreDistanceRadialAcceleration = CalcDistanceSpatialByParameter(ConstsParamNames.StrokeSpatial.RADIAL_ACCELERATION);
		SpatialScoreDistanceRadius = CalcDistanceSpatialByParameter(ConstsParamNames.StrokeSpatial.RADIUS);
		SpatialScoreDistanceTeta = CalcDistanceSpatialByParameter(ConstsParamNames.StrokeSpatial.TETA);
		SpatialScoreDistanceDeltaTeta = CalcDistanceSpatialByParameter(ConstsParamNames.StrokeSpatial.DELTA_TETA);
		SpatialScoreDistanceAccumulatedNormArea = CalcDistanceSpatialByParameter(ConstsParamNames.StrokeSpatial.ACCUMULATED_NORM_AREA);
		
		SpatialScoreTimeVelocity = CalcTimeSpatialByParameter(ConstsParamNames.StrokeSpatial.VELOCITIES);
		SpatialScoreTimeAcceleration = CalcTimeSpatialByParameter(ConstsParamNames.StrokeSpatial.ACCELERATIONS);
		SpatialScoreTimeRadialVelocity = CalcTimeSpatialByParameter(ConstsParamNames.StrokeSpatial.RADIAL_VELOCITIES);
		
		SpatialScoreTimeRadialAcceleration = CalcTimeSpatialByParameter(ConstsParamNames.StrokeSpatial.RADIAL_ACCELERATION);
		SpatialScoreTimeRadius = CalcTimeSpatialByParameter(ConstsParamNames.StrokeSpatial.RADIUS);
		SpatialScoreTimeTeta = CalcTimeSpatialByParameter(ConstsParamNames.StrokeSpatial.TETA);
		SpatialScoreTimeDeltaTeta = CalcTimeSpatialByParameter(ConstsParamNames.StrokeSpatial.DELTA_TETA);
		SpatialScoreTimeAccumulatedNormArea = CalcTimeSpatialByParameter(ConstsParamNames.StrokeSpatial.ACCUMULATED_NORM_AREA);			
		
		Collections.sort(mListSpatialScores, new Comparator<IStatEngineResult>() {
            public int compare(IStatEngineResult score1, IStatEngineResult score2) {
                if (Math.abs(score1.GetZScore()) > Math.abs(score2.GetZScore())) {
                    return -1;
                }
                if (Math.abs(score1.GetZScore()) < Math.abs(score2.GetZScore())) {
                    return 1;
                }
                return 0;
            }
        });
		
		int limit = 25;
		int count = 0;
		double result = 0;
		double totalWeights = 0;
		for(int idx = 0; idx < limit; idx++) {			
//			if(mListSpatialScores.get(idx).GetScore() != 0) {
				totalWeights += mListSpatialScores.get(idx).GetZScore();
				result += mListSpatialScores.get(idx).GetScore() * mListSpatialScores.get(idx).GetZScore();
				count++;
//				if(count >= limit) {
//					break;
//				}
//			}
		}
		
		result = result / totalWeights;
		SpatialScore = result;
	}
	
	private double CalcDistanceSpatialByParameter(String parameter) {
//		double score = mStatEngine.CompareStrokeSpatial(mStrokeAuthExtended.GetInstruction(), parameter, mStrokeAuthExtended.GetStrokeIdx(), mStrokeAuthExtended.ListEventsSpatialByDistanceExtended, mStrokeStoredExtended.ListEventsSpatialByDistanceExtended);
		
		mListSpatialScores.addAll(mStatEngine.CompareStrokeSpatial(mStrokeAuthExtended.GetInstruction(), parameter, mStrokeAuthExtended.GetStrokeIdx(), mStrokeAuthExtended.ListEventsSpatialByDistanceExtended, mStrokeStoredExtended.ListEventsSpatialByDistanceExtended));
		
		return 0;
	}
	
	private double CalcTimeSpatialByParameter(String parameter) {
//		double score = mStatEngine.CompareStrokeSpatial(mStrokeAuthExtended.GetInstruction(), parameter, mStrokeAuthExtended.GetStrokeIdx(), mStrokeAuthExtended.ListEventsSpatialByTimeExtended, mStrokeStoredExtended.ListEventsSpatialByTimeExtended);
		
		mListSpatialScores.addAll(mStatEngine.CompareStrokeSpatial(mStrokeAuthExtended.GetInstruction(), parameter, mStrokeAuthExtended.GetStrokeIdx(), mStrokeAuthExtended.ListEventsSpatialByTimeExtended, mStrokeStoredExtended.ListEventsSpatialByTimeExtended));
		
		return 0;
	}

	private void CompareVectors() {
		double[] vectorStoredVelocities = new double[mStrokeStoredExtended.ListEventsExtended.size()];
		double[] vectorAuthVelocities = new double[mStrokeStoredExtended.ListEventsExtended.size()];
		
		double[] vectorStoredAcceleration = new double[mStrokeStoredExtended.ListEventsExtended.size()];
		double[] vectorAuthAcceleration = new double[mStrokeStoredExtended.ListEventsExtended.size()];
		
		double[] vectorStoredPressure = new double[mStrokeStoredExtended.ListEventsExtended.size()];
		double[] vectorAuthPressure = new double[mStrokeStoredExtended.ListEventsExtended.size()];
		
		double[] vectorStoredSurface = new double[mStrokeStoredExtended.ListEventsExtended.size()];
		double[] vectorAuthSurface = new double[mStrokeStoredExtended.ListEventsExtended.size()];
				
		double[] vectorStoredVelocitiesX = new double[mStrokeStoredExtended.ListEventsExtended.size()];
		double[] vectorAuthVelocitiesX = new double[mStrokeStoredExtended.ListEventsExtended.size()];
		
		double[] vectorStoredVelocitiesY = new double[mStrokeStoredExtended.ListEventsExtended.size()];
		double[] vectorAuthVelocitiesY = new double[mStrokeStoredExtended.ListEventsExtended.size()];		
				
		for(int idx = 0; idx < mStrokeStoredExtended.ListEventsExtended.size(); idx++) {
			vectorStoredVelocities[idx] = mStrokeStoredExtended.ListEventsExtended.get(idx).Velocity;
			vectorAuthVelocities[idx] = mStrokeAuthExtended.ListEventsExtended.get(idx).Velocity;
						
			vectorStoredAcceleration[idx] = mStrokeStoredExtended.ListEventsExtended.get(idx).Acceleration;
			vectorAuthAcceleration[idx] = mStrokeAuthExtended.ListEventsExtended.get(idx).Acceleration;
			
			vectorStoredPressure[idx] = mStrokeStoredExtended.ListEventsExtended.get(idx).Pressure;
			vectorAuthPressure[idx] = mStrokeAuthExtended.ListEventsExtended.get(idx).Pressure;
			
			vectorStoredSurface[idx] = mStrokeStoredExtended.ListEventsExtended.get(idx).TouchSurface;
			vectorAuthSurface[idx] = mStrokeAuthExtended.ListEventsExtended.get(idx).TouchSurface;
			
			vectorStoredVelocitiesX[idx] = mStrokeStoredExtended.ListEventsExtended.get(idx).VelocityX;
			vectorAuthVelocitiesX[idx] = mStrokeAuthExtended.ListEventsExtended.get(idx).VelocityX;
			
			vectorStoredVelocitiesY[idx] = mStrokeStoredExtended.ListEventsExtended.get(idx).VelocityY;
			vectorAuthVelocitiesY[idx] = mStrokeAuthExtended.ListEventsExtended.get(idx).VelocityY;
		}
		
		double x = 1;
		x++;
	}

	private void TimeWarp() {			
		ArrayList<IDTWObj> listCoordsAuth = new  ArrayList<>();
		ArrayList<IDTWObj> listCoordsStored = new  ArrayList<>();
		
		ArrayList<IDTWObj> listNormalizedCoordsAuth = new  ArrayList<>();
		ArrayList<IDTWObj> listNormalizedCoordsStored = new  ArrayList<>();
		
		ArrayList<IDTWObj> listEventsAuth = new  ArrayList<>();
		ArrayList<IDTWObj> listEventsStored = new  ArrayList<>();		
		
		ArrayList<IDTWObj> listVelocitiesAuth = new  ArrayList<>();
		ArrayList<IDTWObj> listVelocitiesStored = new  ArrayList<>();
		
		ArrayList<IDTWObj> listAccelerationsAuth = new  ArrayList<>();
		ArrayList<IDTWObj> listAccelerationsStored = new  ArrayList<>();
		
		for(int idxEvent = 0; idxEvent < mStrokeAuthExtended.ListEventsExtended.size(); idxEvent++) {
			listCoordsAuth.add(new DTWObjCoordinate(mStrokeAuthExtended.ListEventsExtended.get(idxEvent).Xmm, mStrokeAuthExtended.ListEventsExtended.get(idxEvent).Ymm));
			listNormalizedCoordsAuth.add(new DTWObjCoordinate(mStrokeAuthExtended.ListEventsExtended.get(idxEvent).Xnormalized, mStrokeAuthExtended.ListEventsExtended.get(idxEvent).Ynormalized));
			listEventsAuth.add(new DTWObjMotionEvent(mStrokeAuthExtended.ListEventsExtended.get(idxEvent)));
			listVelocitiesAuth.add(new DTWObjDouble(mStrokeAuthExtended.ListEventsExtended.get(idxEvent).Velocity));
			listAccelerationsAuth.add(new DTWObjDouble(mStrokeAuthExtended.ListEventsExtended.get(idxEvent).Acceleration));
		}
		
		for(int idxEvent = 0; idxEvent < mStrokeStoredExtended.ListEventsExtended.size(); idxEvent++) {
			listCoordsStored.add(new DTWObjCoordinate(mStrokeStoredExtended.ListEventsExtended.get(idxEvent).Xmm, mStrokeStoredExtended.ListEventsExtended.get(idxEvent).Ymm));
			listNormalizedCoordsStored.add(new DTWObjCoordinate(mStrokeStoredExtended.ListEventsExtended.get(idxEvent).Xnormalized, mStrokeStoredExtended.ListEventsExtended.get(idxEvent).Ynormalized));
			listEventsStored.add(new DTWObjMotionEvent(mStrokeStoredExtended.ListEventsExtended.get(idxEvent)));
			listVelocitiesStored.add(new DTWObjDouble(mStrokeStoredExtended.ListEventsExtended.get(idxEvent).Velocity));
			listAccelerationsStored.add(new DTWObjDouble(mStrokeStoredExtended.ListEventsExtended.get(idxEvent).Acceleration));
		}
		
		UtilsDTW dtwCoords = new UtilsDTW(listCoordsAuth, listCoordsStored);		
		UtilsDTW dtwNormalizedCoords = new UtilsDTW(listNormalizedCoordsAuth, listNormalizedCoordsStored);
		UtilsDTW dtwEvents = new UtilsDTW(listEventsAuth, listEventsStored);
		UtilsDTW dtwVelocities = new UtilsDTW(listVelocitiesAuth, listVelocitiesStored);
		UtilsDTW dtwAccelerations = new UtilsDTW(listAccelerationsAuth, listAccelerationsStored);
		
		DtwDistanceCoordinates = dtwCoords.getDistance();
		DtwDistanceNormalizedCoordinates = dtwNormalizedCoords.getDistance();
		DtwDistanceEvents = dtwEvents.getDistance();		
		DtwDistanceVelocities = dtwVelocities.getDistance();
		DtwDistanceAccelerations = dtwAccelerations.getDistance();
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
		double areaStored = mStrokeStoredExtended.ShapeDataObj.ShapeArea;
		double areaAuth = mStrokeAuthExtended.ShapeDataObj.ShapeArea;
		
		double finalScore = mUtilsComparison.CompareNumericalValues(areaStored, areaAuth, 0.65);
		AddDoubleParameter(ConstsParamNames.Stroke.STROKE_AREA, finalScore, ConstsParamWeights.HIGH, areaStored);

		areaStored = mStrokeStoredExtended.ShapeDataObj.ShapeAreaMinXMinY;
		areaAuth = mStrokeAuthExtended.ShapeDataObj.ShapeAreaMinXMinY;
		
		finalScore = mUtilsComparison.CompareNumericalValues(areaStored, areaAuth, 0.65);
		AddDoubleParameter(ConstsParamNames.Stroke.STROKE_AREA_MINX_MINY, finalScore, ConstsParamWeights.HIGH, areaStored);
	}	
	
	protected void CompareTimeInterval()
	{
		double timeIntervalStored = mStrokeStoredExtended.StrokeTimeInterval;
		double timeIntervalAuth = mStrokeAuthExtended.StrokeTimeInterval;
		
		double finalScore = mUtilsComparison.CompareNumericalValues(timeIntervalStored, timeIntervalAuth, 0.75);		
		AddDoubleParameter(ConstsParamNames.Stroke.TIME_INTERVAL, finalScore, ConstsParamWeights.MEDIUM, timeIntervalAuth);		
	}
	
	protected void CompareAvgVelocity()
	{
		double avgVelocityStored = mStrokeStoredExtended.AverageVelocity;
		double avgVelocityAuth = mStrokeAuthExtended.AverageVelocity;
		
		//double finalScore = mStatEngine.CompareStrokeDoubleValues(mStrokeStoredExtended.GetInstruction(), ConstsParamNames.Stroke.AVERAGE_VELOCITY, mStrokeStoredExtended.GetStrokeIdx(), avgVelocityStored, avgVelocityAuth);
		double finalScore = mUtilsComparison.CompareNumericalValues(avgVelocityStored, avgVelocityAuth, 0.75);
		AddDoubleParameter(ConstsParamNames.Stroke.AVERAGE_VELOCITY, finalScore, ConstsParamWeights.MEDIUM, avgVelocityAuth);
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
	}
	
	protected void CompareMinCosineDistance()
	{	
		double[] vectorStored = mStrokeStoredExtended.SpatialSamplingVector;
		double[] vectorAuth = mStrokeAuthExtended.SpatialSamplingVector;
		
		double minimumCosineDistanceScore = 
				mUtilsVectors.MinimumCosineDistanceScore(vectorStored, vectorAuth);
		
		double score = 0;
		if(minimumCosineDistanceScore > 1) {
			score = 0.86;
		}
		if(minimumCosineDistanceScore > 1.5) {
			score = 0.91;
		}
		if(minimumCosineDistanceScore > 2) {
			score = 0.96;
		}
		if(minimumCosineDistanceScore > 2.5) {
			score = 1;
		}
		
		ICompareResult compareResult = 
				(ICompareResult) new CompareResultParamVectors(ConstsParamNames.Stroke.MINIMUM_COSINE_DISTANCE, score, ConstsParamWeights.MEDIUM, minimumCosineDistanceScore, vectorStored, vectorAuth);
		mCompareResult.ListCompareResults.add(compareResult);
		
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
	
	protected void AddDoubleParameter(String parameterName, double score, double weight, double originalValue)
	{	
		double mean = 0;
		double sd = 0;
		
		ICompareResult compareResult = 
				(ICompareResult) new CompareResultGeneric(parameterName, score, weight, originalValue, mean, sd);
		mCompareResult.ListCompareResults.add(compareResult);
	}
	/****************************/
}
