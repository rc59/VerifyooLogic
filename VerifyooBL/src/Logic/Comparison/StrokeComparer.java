package Logic.Comparison;

import java.util.HashMap;

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
import Logic.Comparison.Stats.Interfaces.IFeatureMeanData;
import Logic.Comparison.Stats.Interfaces.IStatEngine;
import Logic.Utils.Utils;
import Logic.Utils.UtilsComparison;
import Logic.Utils.UtilsGeneral;
import Logic.Utils.UtilsVectors;
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
	
	public StrokeComparer(boolean isSimilarDevices)
	{			
		mIsSimilarDevices = isSimilarDevices;
		mCompareResult = new CompareResultSummary();
		mStatEngine = StatEngine.GetInstance();
		InitUtils();
	}
	
	protected void InitUtils()
	{
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
		
		double score1 = mStatEngine.CompareStrokeSpatial(mStrokeAuthExtended.GetInstruction(), mStrokeAuthExtended.ListEventsSpatialByDistanceExtended, mStrokeStoredExtended.ListEventsSpatialByDistanceExtended);
		double score2 = mStatEngine.CompareStrokeSpatial(mStrokeAuthExtended.GetInstruction(), mStrokeAuthExtended.ListEventsSpatialByTimeExtended, mStrokeStoredExtended.ListEventsSpatialByTimeExtended);
		
		double total = (score1 + score2 / 2);
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
//		UtilsDTW dtw = new UtilsDTW(mStrokeAuthExtended.GetFilteredVelocities(), mStrokeStoredExtended.GetFilteredVelocities());
//		double distanceVel = dtw.getDistance();
//		
//		dtw = new UtilsDTW(mStrokeAuthExtended.GetFilteredAccelerations(), mStrokeStoredExtended.GetFilteredAccelerations());
//		double distanceAcc = dtw.getDistance();
//		
//		dtw = new UtilsDTW(mStrokeAuthExtended.SpatialSamplingVector, mStrokeStoredExtended.SpatialSamplingVector);
//		double distanceSpatial = dtw.getDistance();		
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
