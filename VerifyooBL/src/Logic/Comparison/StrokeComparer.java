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
	public double DtwSpatialVelocityDistance;
	public double DtwSpatialAccelerationDistance;
	public double DtwSpatialRadialVelocityDistance;
	public double DtwSpatialRadialAccelerationDistance;
	public double DtwSpatialRadiusDistance;
	public double DtwSpatialTetaDistance;
	public double DtwSpatialDeltaTetaDistance;
	public double DtwSpatialAccumNormAreaDistance;
	public double DtwSpatialVelocityTime;
	public double DtwSpatialAccelerationTime;
	public double DtwSpatialRadialVelocityTime;
	public double DtwSpatialRadialAccelerationTime;
	public double DtwSpatialRadiusTime;
	public double DtwSpatialTetaTime;
	public double DtwSpatialDeltaTetaTime;
	public double DtwSpatialAccumNormAreaTime;
	
	public double StrokeDistanceTotalScoreStartToStart;
	public double StrokeDistanceTotalScoreStartToEnd;
	public double StrokeDistanceTotalScoreEndToStart;
	public double StrokeDistanceTotalScoreEndToEnd;
	
	public double StrokeDistanceTotalScore;
	
	/*********************************** Spatial Vector Distance ***********************************/
	
	public ArrayList<IDTWObj> VectorSpatialVelocityDistanceAuth;	
	public ArrayList<IDTWObj> VectorSpatialVelocityDistanceStored;
	
	public ArrayList<IDTWObj> VectorSpatialAccelerationDistanceAuth;	
	public ArrayList<IDTWObj> VectorSpatialAccelerationDistanceStored;
	
	public ArrayList<IDTWObj> VectorSpatialRadialVelocityDistanceAuth;	
	public ArrayList<IDTWObj> VectorSpatialRadialVelocityDistanceStored;	
	
	public ArrayList<IDTWObj> VectorSpatialRadialAccelerationDistanceAuth;	
	public ArrayList<IDTWObj> VectorSpatialRadialAccelerationDistanceStored;
	
	public ArrayList<IDTWObj> VectorSpatialRadiusDistanceAuth;	
	public ArrayList<IDTWObj> VectorSpatialRadiusDistanceStored;
	
	public ArrayList<IDTWObj> VectorSpatialTetaDistanceAuth;	
	public ArrayList<IDTWObj> VectorSpatialTetaDistanceStored;
	
	public ArrayList<IDTWObj> VectorSpatialDeltaTetaDistanceAuth;	
	public ArrayList<IDTWObj> VectorSpatialDeltaTetaDistanceStored;
	
	public ArrayList<IDTWObj> VectorSpatialAccumNormAreaDistanceAuth;	
	public ArrayList<IDTWObj> VectorSpatialAccumNormAreaDistanceStored;
	
	/*********************************** Spatial Vector Time ***********************************/	
	
	public ArrayList<IDTWObj> VectorSpatialVelocityTimeAuth;	
	public ArrayList<IDTWObj> VectorSpatialVelocityTimeStored;
	
	public ArrayList<IDTWObj> VectorSpatialAccelerationTimeAuth;	
	public ArrayList<IDTWObj> VectorSpatialAccelerationTimeStored;
	
	public ArrayList<IDTWObj> VectorSpatialRadialVelocityTimeAuth;	
	public ArrayList<IDTWObj> VectorSpatialRadialVelocityTimeStored;	
	
	public ArrayList<IDTWObj> VectorSpatialRadialAccelerationTimeAuth;	
	public ArrayList<IDTWObj> VectorSpatialRadialAccelerationTimeStored;
	
	public ArrayList<IDTWObj> VectorSpatialRadiusTimeAuth;	
	public ArrayList<IDTWObj> VectorSpatialRadiusTimeStored;
	
	public ArrayList<IDTWObj> VectorSpatialTetaTimeAuth;	
	public ArrayList<IDTWObj> VectorSpatialTetaTimeStored;
	
	public ArrayList<IDTWObj> VectorSpatialDeltaTetaTimeAuth;	
	public ArrayList<IDTWObj> VectorSpatialDeltaTetaTimeStored;
	
	public ArrayList<IDTWObj> VectorSpatialAccumNormAreaTimeAuth;	
	public ArrayList<IDTWObj> VectorSpatialAccumNormAreaTimeStored;
	
	/*********************************** Spatial Scores ***********************************/
	
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
				CompareSpatial();
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
				//TimeWarp();
				CalculateFinalScore();
				//CheckDtw();		
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
	
	protected void CheckDtw() {
		double scoreToRemove = 0;
				
		double factorVelocities = 0.03; 
		double factorCoords = 5;
		double factorEvents = 0.18;
		double factorCoordsNorm = 0.06;
		
		double dtwScore = 0;
		
		dtwScore += DtwCoordinates / factorCoords;
		dtwScore += DtwNormalizedCoordinates / factorCoordsNorm;
		dtwScore += DtwVelocities / factorVelocities;		
			
		DtwScoreToRemove = CheckValues(0.1, 0.7, 2, dtwScore, false);
		
//		scoreToRemove += CheckValues(0.03, 3, 6, DtwCoordinates, false);
//		scoreToRemove += CheckValues(0.03, 0.04, 0.08, DtwNormalizedCoordinates, false);		
//		scoreToRemove += CheckValues(0.03, 0.15, 0.2, DtwEvents, false);		
//		scoreToRemove += CheckValues(0.03, 0.02, 0.03, DtwVelocities, false);		
//		scoreToRemove += CheckValues(0.03, 0.0007, 0.008, DtwAccelerations, false);
			
		mCompareResult.Score -= DtwScoreToRemove;
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

	private void CreateSpatialVectorsForDtwAnalysis() {
		VectorSpatialVelocityDistanceAuth = CreateDistanceSpatialVectorsByParamName(ConstsParamNames.StrokeSpatial.VELOCITIES, mStrokeAuthExtended);
		VectorSpatialVelocityDistanceStored = CreateDistanceSpatialVectorsByParamName(ConstsParamNames.StrokeSpatial.VELOCITIES, mStrokeStoredExtended);
		
		VectorSpatialAccelerationDistanceAuth = CreateDistanceSpatialVectorsByParamName(ConstsParamNames.StrokeSpatial.ACCELERATIONS, mStrokeAuthExtended);
		VectorSpatialAccelerationDistanceStored = CreateDistanceSpatialVectorsByParamName(ConstsParamNames.StrokeSpatial.ACCELERATIONS, mStrokeStoredExtended);
		
		VectorSpatialRadialVelocityDistanceAuth = CreateDistanceSpatialVectorsByParamName(ConstsParamNames.StrokeSpatial.RADIAL_VELOCITIES, mStrokeAuthExtended);
		VectorSpatialRadialVelocityDistanceStored = CreateDistanceSpatialVectorsByParamName(ConstsParamNames.StrokeSpatial.RADIAL_VELOCITIES, mStrokeStoredExtended);
		
		VectorSpatialRadialAccelerationDistanceAuth = CreateDistanceSpatialVectorsByParamName(ConstsParamNames.StrokeSpatial.RADIAL_ACCELERATION, mStrokeAuthExtended);
		VectorSpatialRadialAccelerationDistanceStored = CreateDistanceSpatialVectorsByParamName(ConstsParamNames.StrokeSpatial.RADIAL_ACCELERATION, mStrokeStoredExtended);
		
		VectorSpatialRadiusDistanceAuth = CreateDistanceSpatialVectorsByParamName(ConstsParamNames.StrokeSpatial.RADIUS, mStrokeAuthExtended);
		VectorSpatialRadiusDistanceStored = CreateDistanceSpatialVectorsByParamName(ConstsParamNames.StrokeSpatial.RADIUS, mStrokeStoredExtended);
		
		VectorSpatialTetaDistanceAuth = CreateDistanceSpatialVectorsByParamName(ConstsParamNames.StrokeSpatial.TETA, mStrokeAuthExtended);
		VectorSpatialTetaDistanceStored = CreateDistanceSpatialVectorsByParamName(ConstsParamNames.StrokeSpatial.TETA, mStrokeStoredExtended);
		
		VectorSpatialDeltaTetaDistanceAuth = CreateDistanceSpatialVectorsByParamName(ConstsParamNames.StrokeSpatial.DELTA_TETA, mStrokeAuthExtended);
		VectorSpatialDeltaTetaDistanceStored = CreateDistanceSpatialVectorsByParamName(ConstsParamNames.StrokeSpatial.DELTA_TETA, mStrokeStoredExtended);
		
		VectorSpatialAccumNormAreaDistanceAuth = CreateDistanceSpatialVectorsByParamName(ConstsParamNames.StrokeSpatial.ACCUMULATED_NORM_AREA, mStrokeAuthExtended);
		VectorSpatialAccumNormAreaDistanceStored = CreateDistanceSpatialVectorsByParamName(ConstsParamNames.StrokeSpatial.ACCUMULATED_NORM_AREA, mStrokeStoredExtended);
		
		VectorSpatialVelocityTimeAuth = CreateTimeSpatialVectorsByParamName(ConstsParamNames.StrokeSpatial.VELOCITIES, mStrokeAuthExtended);
		VectorSpatialVelocityTimeStored = CreateTimeSpatialVectorsByParamName(ConstsParamNames.StrokeSpatial.VELOCITIES, mStrokeStoredExtended);
		
		VectorSpatialAccelerationTimeAuth = CreateTimeSpatialVectorsByParamName(ConstsParamNames.StrokeSpatial.ACCELERATIONS, mStrokeAuthExtended);
		VectorSpatialAccelerationTimeStored = CreateTimeSpatialVectorsByParamName(ConstsParamNames.StrokeSpatial.ACCELERATIONS, mStrokeStoredExtended);
		
		VectorSpatialRadialVelocityTimeAuth = CreateTimeSpatialVectorsByParamName(ConstsParamNames.StrokeSpatial.RADIAL_VELOCITIES, mStrokeAuthExtended);
		VectorSpatialRadialVelocityTimeStored = CreateTimeSpatialVectorsByParamName(ConstsParamNames.StrokeSpatial.RADIAL_VELOCITIES, mStrokeStoredExtended);
		
		VectorSpatialRadialAccelerationTimeAuth = CreateTimeSpatialVectorsByParamName(ConstsParamNames.StrokeSpatial.RADIAL_ACCELERATION, mStrokeAuthExtended);
		VectorSpatialRadialAccelerationTimeStored = CreateTimeSpatialVectorsByParamName(ConstsParamNames.StrokeSpatial.RADIAL_ACCELERATION, mStrokeStoredExtended);
		
		VectorSpatialRadiusTimeAuth = CreateTimeSpatialVectorsByParamName(ConstsParamNames.StrokeSpatial.RADIUS, mStrokeAuthExtended);
		VectorSpatialRadiusTimeStored = CreateTimeSpatialVectorsByParamName(ConstsParamNames.StrokeSpatial.RADIUS, mStrokeStoredExtended);
		
		VectorSpatialTetaTimeAuth = CreateTimeSpatialVectorsByParamName(ConstsParamNames.StrokeSpatial.TETA, mStrokeAuthExtended);
		VectorSpatialTetaTimeStored = CreateTimeSpatialVectorsByParamName(ConstsParamNames.StrokeSpatial.TETA, mStrokeStoredExtended);
		
		VectorSpatialDeltaTetaTimeAuth = CreateTimeSpatialVectorsByParamName(ConstsParamNames.StrokeSpatial.DELTA_TETA, mStrokeAuthExtended);
		VectorSpatialDeltaTetaTimeStored = CreateTimeSpatialVectorsByParamName(ConstsParamNames.StrokeSpatial.DELTA_TETA, mStrokeStoredExtended);
		
		VectorSpatialAccumNormAreaTimeAuth = CreateTimeSpatialVectorsByParamName(ConstsParamNames.StrokeSpatial.ACCUMULATED_NORM_AREA, mStrokeAuthExtended);
		VectorSpatialAccumNormAreaTimeStored = CreateTimeSpatialVectorsByParamName(ConstsParamNames.StrokeSpatial.ACCUMULATED_NORM_AREA, mStrokeStoredExtended);					
	}
	
	private ArrayList<IDTWObj> CreateDistanceSpatialVectorsByParamName(String paramName, StrokeExtended stroke) {
		return GetSpatialVector(paramName, stroke.ListEventsSpatialByDistanceExtended);					
	}
	
	private ArrayList<IDTWObj> CreateTimeSpatialVectorsByParamName(String paramName, StrokeExtended stroke) {
		return GetSpatialVector(paramName, stroke.ListEventsSpatialByTimeExtended);					
	}
	
	private void CompareSpatial() {
		
//		double[] velocitiesAuthDistance = mUtilsVectors.GetVectorVel(mStrokeAuthExtended.ListEventsSpatialByDistanceExtended);
//		double[] velocitiesStoredDistance = mUtilsVectors.GetVectorVel(mStrokeStoredExtended.ListEventsSpatialByDistanceExtended);
//		
//		double[] velocitiesAuthTime = mUtilsVectors.GetVectorVel(mStrokeAuthExtended.ListEventsSpatialByTimeExtended);
//		double[] velocitiesStoredTime = mUtilsVectors.GetVectorVel(mStrokeStoredExtended.ListEventsSpatialByTimeExtended);
//				
//		Complex[] resultAuthDistance = FFT.fft(Utils.GetInstance().GetUtilsMath().ToComplex(velocitiesAuthDistance));
//		Complex[] resultStoredDistance = FFT.fft(Utils.GetInstance().GetUtilsMath().ToComplex(velocitiesStoredDistance));
//		
//		FFTCheck(resultAuthDistance);
//		FFTCheck(resultStoredDistance);
		
		VectorSpatialVelocityDistanceStored = GetSpatialVector(ConstsParamNames.StrokeSpatial.VELOCITIES, mStrokeStoredExtended.ListEventsSpatialByDistanceExtended);
		VectorSpatialVelocityTimeStored = GetSpatialVector(ConstsParamNames.StrokeSpatial.VELOCITIES, mStrokeStoredExtended.ListEventsSpatialByTimeExtended);
		
		SpatialScoreDistanceVelocity = CalcSpatialByParameterDistance(ConstsParamNames.StrokeSpatial.VELOCITIES);	
		SpatialScoreDistanceAcceleration = CalcSpatialByParameterDistance(ConstsParamNames.StrokeSpatial.ACCELERATIONS);
		SpatialScoreDistanceRadialVelocity = CalcSpatialByParameterDistance(ConstsParamNames.StrokeSpatial.RADIAL_VELOCITIES);
		
		SpatialScoreDistanceRadialAcceleration = CalcSpatialByParameterDistance(ConstsParamNames.StrokeSpatial.RADIAL_ACCELERATION);
		SpatialScoreDistanceRadius = CalcSpatialByParameterDistance(ConstsParamNames.StrokeSpatial.RADIUS);
		SpatialScoreDistanceTeta = CalcSpatialByParameterDistance(ConstsParamNames.StrokeSpatial.TETA);
		SpatialScoreDistanceDeltaTeta = CalcSpatialByParameterDistance(ConstsParamNames.StrokeSpatial.DELTA_TETA);
		SpatialScoreDistanceAccumulatedNormArea = CalcSpatialByParameterDistance(ConstsParamNames.StrokeSpatial.ACCUMULATED_NORM_AREA);
		
		SpatialScoreTimeVelocity = CalcSpatialByParameterTime(ConstsParamNames.StrokeSpatial.VELOCITIES);
		SpatialScoreTimeAcceleration = CalcSpatialByParameterTime(ConstsParamNames.StrokeSpatial.ACCELERATIONS);
		SpatialScoreTimeRadialVelocity = CalcSpatialByParameterTime(ConstsParamNames.StrokeSpatial.RADIAL_VELOCITIES);
		
		SpatialScoreTimeRadialAcceleration = CalcSpatialByParameterTime(ConstsParamNames.StrokeSpatial.RADIAL_ACCELERATION);
		SpatialScoreTimeRadius = CalcSpatialByParameterTime(ConstsParamNames.StrokeSpatial.RADIUS);
		SpatialScoreTimeTeta = CalcSpatialByParameterTime(ConstsParamNames.StrokeSpatial.TETA);
		SpatialScoreTimeDeltaTeta = CalcSpatialByParameterTime(ConstsParamNames.StrokeSpatial.DELTA_TETA);
		SpatialScoreTimeAccumulatedNormArea = CalcSpatialByParameterTime(ConstsParamNames.StrokeSpatial.ACCUMULATED_NORM_AREA);			
				
		StrokeSpatialScore = CalculateScoreFromList(mListSpatialScores);
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
	
	private double CalcSpatialByParameterDistance(String parameter) {

		ArrayList<IStatEngineResult> listResults = mStatEngine.CompareStrokeSpatial(mStrokeAuthExtended.GetInstruction(), parameter, mStrokeAuthExtended.GetStrokeIdx(), mStrokeAuthExtended.ListEventsSpatialByDistanceExtended, mStrokeStoredExtended.ListEventsSpatialByDistanceExtended, Consts.ConstsParamNames.SpatialTypes.DISTANCE); 
		mListSpatialScores.addAll(listResults);
		
		double score = CalculateScoreFromList(listResults);		
		return score;
	}	
	
	private double CalcSpatialByParameterTime(String parameter) {

		ArrayList<IStatEngineResult> listResults = mStatEngine.CompareStrokeSpatial(mStrokeAuthExtended.GetInstruction(), parameter, mStrokeAuthExtended.GetStrokeIdx(), mStrokeAuthExtended.ListEventsSpatialByTimeExtended, mStrokeStoredExtended.ListEventsSpatialByTimeExtended, Consts.ConstsParamNames.SpatialTypes.TIME);		
		mListSpatialScores.addAll(listResults);
		
		double score = CalculateScoreFromList(listResults);
		return score;
	}

	private ArrayList<IDTWObj> GetSpatialVector(String parameter, ArrayList<MotionEventExtended> listEvents) {
		ArrayList<IDTWObj> vector = mStatEngine.GetSpatialVector(mStrokeAuthExtended.GetInstruction(), parameter, mStrokeAuthExtended.GetStrokeIdx(), listEvents, Consts.ConstsParamNames.SpatialTypes.DISTANCE); 				
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

	private void TimeWarp() {			
		ArrayList<IDTWObj> listCoordsAuth = new  ArrayList<>();
		ArrayList<IDTWObj> listCoordsStored = new  ArrayList<>();
		
		ArrayList<IDTWObj> listNormalizedCoordsAuth = new  ArrayList<>();
		ArrayList<IDTWObj> listNormalizedCoordsStored = new  ArrayList<>();
				
		ArrayList<IDTWObj> listNormalizedCoordsSpatialDistanceAuth = new  ArrayList<>();
		ArrayList<IDTWObj> listNormalizedCoordsSpatialDistanceStored = new  ArrayList<>();
		
		ArrayList<IDTWObj> listEventsAuth = new  ArrayList<>();
		ArrayList<IDTWObj> listEventsStored = new  ArrayList<>();		
		
		ArrayList<IDTWObj> listVelocitiesAuth = new  ArrayList<>();
		ArrayList<IDTWObj> listVelocitiesStored = new  ArrayList<>();
		
		ArrayList<IDTWObj> listAccelerationsAuth = new  ArrayList<>();
		ArrayList<IDTWObj> listAccelerationsStored = new  ArrayList<>();
		
		double[] vectorX = new double[mStrokeAuthExtended.ListEventsExtended.size()];
		double[] vectorY = new double[mStrokeAuthExtended.ListEventsExtended.size()];
		
		for(int idxEvent = 0; idxEvent < mStrokeAuthExtended.ListEventsSpatialByDistanceExtended.size(); idxEvent++) {			
			listNormalizedCoordsSpatialDistanceAuth.add(new DTWObjCoordinate(mStrokeAuthExtended.ListEventsSpatialByDistanceExtended.get(idxEvent).Xnormalized, mStrokeAuthExtended.ListEventsSpatialByDistanceExtended.get(idxEvent).Ynormalized));			
			listNormalizedCoordsSpatialDistanceStored.add(new DTWObjCoordinate(mStrokeStoredExtended.ListEventsSpatialByDistanceExtended.get(idxEvent).Xnormalized, mStrokeStoredExtended.ListEventsSpatialByDistanceExtended.get(idxEvent).Ynormalized));
		}
		
		for(int idxEvent = 0; idxEvent < mStrokeAuthExtended.ListEventsExtended.size(); idxEvent++) {
			vectorX[idxEvent] = mStrokeAuthExtended.ListEventsExtended.get(idxEvent).Xmm;
			vectorY[idxEvent] = mStrokeAuthExtended.ListEventsExtended.get(idxEvent).Ymm;
			
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
		UtilsDTW dtwNormalizedSpatialDistance = new UtilsDTW(listNormalizedCoordsSpatialDistanceAuth, listNormalizedCoordsSpatialDistanceStored);
		UtilsDTW dtwEvents = new UtilsDTW(listEventsAuth, listEventsStored);
		UtilsDTW dtwVelocities = new UtilsDTW(listVelocitiesAuth, listVelocitiesStored);
		UtilsDTW dtwAccelerations = new UtilsDTW(listAccelerationsAuth, listAccelerationsStored);
		
		DtwCoordinates = dtwCoords.getDistance();
		DtwNormalizedCoordinatesSpatialDistance = dtwNormalizedSpatialDistance.getDistance();
		DtwNormalizedCoordinates = dtwNormalizedCoords.getDistance();
		DtwEvents = dtwEvents.getDistance();
		DtwVelocities = dtwVelocities.getDistance();
		DtwAccelerations = dtwAccelerations.getDistance();		
		
		DtwSpatialVelocityDistance = GetSpatialDtwScore(VectorSpatialVelocityDistanceAuth, VectorSpatialVelocityDistanceStored);
		DtwSpatialAccelerationDistance = GetSpatialDtwScore(VectorSpatialAccelerationDistanceAuth, VectorSpatialAccelerationDistanceStored);
		DtwSpatialRadialVelocityDistance = GetSpatialDtwScore(VectorSpatialRadialVelocityDistanceAuth, VectorSpatialRadialVelocityDistanceStored);
		DtwSpatialRadialAccelerationDistance = GetSpatialDtwScore(VectorSpatialRadialAccelerationDistanceAuth, VectorSpatialRadialAccelerationDistanceStored);
		DtwSpatialRadiusDistance = GetSpatialDtwScore(VectorSpatialRadiusDistanceAuth, VectorSpatialRadiusDistanceStored);
		DtwSpatialTetaDistance = GetSpatialDtwScore(VectorSpatialTetaDistanceAuth, VectorSpatialTetaDistanceStored);
		DtwSpatialDeltaTetaDistance = GetSpatialDtwScore(VectorSpatialDeltaTetaDistanceAuth, VectorSpatialDeltaTetaDistanceStored);
		DtwSpatialAccumNormAreaDistance = GetSpatialDtwScore(VectorSpatialAccumNormAreaDistanceAuth, VectorSpatialAccumNormAreaDistanceStored);
				
		DtwSpatialVelocityTime = GetSpatialDtwScore(VectorSpatialVelocityTimeAuth, VectorSpatialVelocityTimeStored);
		DtwSpatialAccelerationTime = GetSpatialDtwScore(VectorSpatialAccelerationTimeAuth, VectorSpatialAccelerationTimeStored);
		DtwSpatialRadialVelocityTime = GetSpatialDtwScore(VectorSpatialRadialVelocityTimeAuth, VectorSpatialRadialVelocityTimeStored);
		DtwSpatialRadialAccelerationTime = GetSpatialDtwScore(VectorSpatialRadialAccelerationTimeAuth, VectorSpatialRadialAccelerationTimeStored);
		DtwSpatialRadiusTime = GetSpatialDtwScore(VectorSpatialRadiusTimeAuth, VectorSpatialRadiusTimeStored);
		DtwSpatialTetaTime = GetSpatialDtwScore(VectorSpatialTetaTimeAuth, VectorSpatialTetaTimeStored);
		DtwSpatialDeltaTetaTime = GetSpatialDtwScore(VectorSpatialDeltaTetaTimeAuth, VectorSpatialDeltaTetaTimeStored);
		DtwSpatialAccumNormAreaTime = GetSpatialDtwScore(VectorSpatialAccumNormAreaTimeAuth, VectorSpatialAccumNormAreaTimeStored);
		
		UtilsDTW dtwSpatialVelocityDistance = new UtilsDTW(VectorSpatialVelocityDistanceAuth, VectorSpatialVelocityDistanceStored);
		UtilsDTW dtwSpatialVelocityTime = new UtilsDTW(VectorSpatialVelocityTimeAuth, VectorSpatialVelocityTimeStored);
		
		DtwSpatialVelocityDistance = dtwSpatialVelocityDistance.getDistance();
		DtwSpatialVelocityTime = dtwSpatialVelocityTime.getDistance();
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
		
		CompareParameter(ConstsParamNames.Stroke.STROKE_MIDDLE_PRESSURE, middlePressureAuth);
		CompareParameter(ConstsParamNames.Stroke.STROKE_MIDDLE_SURFACE, middleSurfaceAuth);		
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
		
		StrokeSpatialScore = StrokeSpatialScore + 0.15;
		mCompareResult.Score = (mCompareResult.Score + StrokeSpatialScore) / 2;
		
		double strokeDistance = StrokeDistanceTotalScore + 0.3;
		if(strokeDistance > 1) {
			strokeDistance = 1;
		}		
		double strokeDistanceToRemove = 1 - strokeDistance;
		
		mCompareResult.Score -= strokeDistanceToRemove;
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

	protected void CompareParameter(String paramName, double authValue) {
		IStatEngineResult result = 
				mStatEngine.CompareStrokeDoubleValues(mStrokeAuthExtended.GetInstruction(), paramName, mStrokeAuthExtended.GetStrokeIdx(), authValue, mStrokeStoredExtended.GetFeatureMeansHash());
		
		double finalScore = result.GetScore();
		AddDoubleParameter(paramName, finalScore, result.GetZScore(), authValue);		
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
