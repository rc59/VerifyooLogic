package Logic.Comparison;

import java.util.ArrayList;
import java.util.Collections;
import java.util.Comparator;
import java.util.HashMap;

import com.mkobos.pca_transform.PCA;
import com.mongodb.util.Util;

import Consts.ConstsFeatures;
import Consts.ConstsGeneral;
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
import Jama.Matrix;
import Logic.Comparison.Stats.FeatureMatrix;
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
import Logic.Utils.DTW.DTWObjVelocity;
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
	
	public double InterestPointScore;
	public double InterestPointScoreFinal;
	
	public double PcaScore;
	public double PcaScoreFinal;
	
	public double DtwSpatialTotalScore;
	public double DtwSpatialTotalScoreFinal;	
	
	public double DtwScoreToRemove;		
	
	public double RadialVelocityDiff;
	public double RadialAccelerationDiff;
	
	public double DtwCoordinates;
	public double DtwNormalizedCoordinates;
	public double DtwNormalizedCoordinatesSpatialDistance;
	public double DtwEvents;
	public double DtwVelocities;
	public double DtwAccelerations;	
	
	public double DtwSpatialVelocity;
	public double DtwSpatialVelocity16;
	public double DtwSpatialAcceleration;
	public double DtwSpatialRadialVelocity;
	public double DtwSpatialRadialAcceleration;
	public double DtwSpatialRadius;
	public double DtwSpatialTeta;
	public double DtwSpatialDeltaTeta;
	public double DtwSpatialAccumNormArea;
	
	public double DtwYona;
	
	public double DtwTemporalVelocity;
	
	public double DtwTemporalVelocity0;
	public double DtwTemporalVelocity1;
	public double DtwTemporalVelocity2;
	public double DtwTemporalVelocity3;
	public double DtwTemporalVelocity4;
	public double DtwTemporalVelocity5;
	public double DtwTemporalVelocity6;
	public double DtwTemporalVelocity7;
	public double DtwTemporalVelocity8;
	public double DtwTemporalVelocity9;
	public double DtwTemporalVelocity10;
	public double DtwTemporalVelocity11;
	public double DtwTemporalVelocity12;
	public double DtwTemporalVelocity13;
	public double DtwTemporalVelocity14;
	public double DtwTemporalVelocity15;
	
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
	
	public double MaxInterestPointIndex;
	public double MaxInterestPointDeltaTeta;
	public double MaxInterestPointAvgVelocity;
	public double MaxInterestPointDensity;
	public double MaxInterestPointLocation;
	public double MaxInterestPointPressure;
	public double MaxInterestPointSurface;
	public double MaxInterestPointVelocity;
	public double MaxInterestPointAcceleration;
	
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
	
	public double AccDiffX;
	public double AccDiffY;
	public double AccDiffZ;
	public double AccDiffTotal;	
	
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
				DtwVelocities();
				CreateSpatialVectorsForDtwAnalysis();
				PcaAnalysis();
				CompareMinCosineDistance();
				CompareStrokeAreas();
				CompareLengths();
				CompareTimeInterval();
				CompareNumEvents();
				CompareVelocities();
				ComparePressureAndSurface();
				CompareAccelerometer();
				CompareStrokeTransitionTimes();
				CompareAccelerations();
				CompareRadials();
				CompareInterestPoints();
				CalculateFinalScoresToDtwPcaInterestPoints();
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
	
	private double GetFinalScore(double value, double lower, double upper, boolean isReverse) {
		double updateValue = value - lower;
		if(updateValue < 0) {
			updateValue = 0;
		}
		double diff = upper - lower;
		double finalScore = updateValue / diff;
		if(finalScore > 1) {
			finalScore = 1;			
		}
		if(finalScore < 0) {
			finalScore = 0;			
		}
		
		if(isReverse) {
			finalScore = 1 - finalScore;
		}
		
		finalScore = 1 - finalScore;
		return finalScore;
	}
	
	private void CalculateFinalScoresToDtwPcaInterestPoints() {		
		InterestPointScoreFinal = GetFinalScore(InterestPointScore, 0.25, 0.45, false);		
		DtwSpatialTotalScoreFinal = GetFinalScore(DtwSpatialTotalScore, 0.55, 0.7, false);
		PcaScoreFinal = GetFinalScore(Math.abs(PcaScore), 2.5, 8, true);
	}
	
	private void DtwVelocities() {
		double[] velAuth = Utils.GetInstance().GetUtilsVectors().GetVectorVel(mStrokeAuthExtended.ListEventsSpatialExtended); 
		double[] velStored = Utils.GetInstance().GetUtilsVectors().GetVectorVel(mStrokeStoredExtended.ListEventsSpatialExtended);
		
		IDTWObj tempObj;
		ArrayList<IDTWObj> listAuth = new ArrayList<IDTWObj>(); 
		ArrayList<IDTWObj> listStored = new ArrayList<IDTWObj>();
		
		for(int idx = 0; idx < velAuth.length; idx++) {
			tempObj = new DTWObjDouble(velAuth[idx]);
			listAuth.add(tempObj);
			
			tempObj = new DTWObjDouble(velStored[idx]);
			listStored.add(tempObj);
		}
		
		UtilsDTW dtwVelocities = new UtilsDTW(listAuth, listStored);
		DtwSpatialVelocity = dtwVelocities.getDistance();
		
		listAuth = new ArrayList<IDTWObj>(); 
		listStored = new ArrayList<IDTWObj>();
		
		for(int idx = 6; idx < 22; idx++) {
			tempObj = new DTWObjDouble(velAuth[idx]);
			listAuth.add(tempObj);
			
			tempObj = new DTWObjDouble(velStored[idx]);
			listStored.add(tempObj);
		}
		
		dtwVelocities = new UtilsDTW(listAuth, listStored);
		DtwSpatialVelocity16 = dtwVelocities.getDistance();
	}
	
	private double[] GetDensityAreas(StrokeExtended stroke) {
		double[] densityAreas = new double[stroke.ListEventsExtended.size()];
		
		for(int idx = 0; idx < stroke.ListEventsExtended.size(); idx++) {
			densityAreas[idx] = stroke.ListEventsExtended.get(idx).EventDensity - 1;
		}
		
		double[] listSignalStrengths = Utils.GetInstance().GetUtilsSignalProcessing().CalculateSignalStrength(densityAreas);
		return listSignalStrengths;
	}
	
	private void CompareInterestPoints() {
		
		double[] densityAreasAuth = GetDensityAreas(mStrokeAuthExtended);
		double[] densityAreasStored = GetDensityAreas(mStrokeStoredExtended);

		double storedMaxPointStrength = densityAreasStored[mStrokeStoredExtended.MaxInterestPointIndex];
		
		double dev = 0.25;
		
		int idxBase = (int) (mStrokeStoredExtended.MaxInterestPointLocation * mStrokeAuthExtended.ListEventsExtended.size());
		int upperBound = idxBase + (int)(dev * mStrokeAuthExtended.ListEventsExtended.size());
		if(upperBound > mStrokeAuthExtended.ListEventsExtended.size()) {
			upperBound = mStrokeAuthExtended.ListEventsExtended.size();
		}
		
		int lowerBound = idxBase - (int)(dev * mStrokeAuthExtended.ListEventsExtended.size());		
		if(lowerBound < 0) {
			lowerBound = 0;
		}
		
		boolean isFound = false;
		double densityDiff = 1;
		double minLocationDiff = 1;
		double currentLocationDiff;
		
		for(int idx = lowerBound; idx < upperBound; idx++) {
			if(densityAreasAuth[idx] > 0) {
				isFound = true;				
				currentLocationDiff = Math.abs(mStrokeStoredExtended.MaxInterestPointLocation - ((((double)idx) / ((double)densityAreasAuth.length))));
				
				if(currentLocationDiff < minLocationDiff) {
					minLocationDiff = currentLocationDiff;
					//densityDiff = 1 - Utils.GetInstance().GetUtilsMath().GetPercentageDiff(mStrokeAuthExtended.ListEventsExtended.get(idx).EventDensity, mStrokeStoredExtended.MaxInterestPointDensity);
					densityDiff = 1 - Utils.GetInstance().GetUtilsMath().GetPercentageDiff(densityAreasAuth[idx], storedMaxPointStrength);
				}
			}
		}

		MaxInterestPointDeltaTeta = Utils.GetInstance().GetUtilsMath().GetPercentageDiff(mStrokeAuthExtended.MaxInterestPointDeltaTeta, mStrokeStoredExtended.MaxInterestPointDeltaTeta);
		MaxInterestPointAvgVelocity = Math.abs(mStrokeAuthExtended.MaxInterestPointAvgVelocity - mStrokeStoredExtended.MaxInterestPointAvgVelocity);
		
		MaxInterestPointDensity = 1 - densityDiff;
		MaxInterestPointLocation = 1 - minLocationDiff;
		InterestPointScore = ((MaxInterestPointDensity + MaxInterestPointLocation) / 2);
		MaxInterestPointIndex = Utils.GetInstance().GetUtilsMath().GetPercentageDiff(mStrokeAuthExtended.MaxInterestPointIndex, mStrokeStoredExtended.MaxInterestPointIndex);
		MaxInterestPointVelocity = Utils.GetInstance().GetUtilsMath().GetPercentageDiff(mStrokeAuthExtended.MaxInterestPointVelocity, mStrokeStoredExtended.MaxInterestPointVelocity);
		MaxInterestPointAcceleration = Utils.GetInstance().GetUtilsMath().GetPercentageDiff(mStrokeAuthExtended.MaxInterestPointAcceleration, mStrokeStoredExtended.MaxInterestPointAcceleration);
	}
	
	private void CompareInterestPoints2() {		
		MaxInterestPointIndex = Utils.GetInstance().GetUtilsMath().GetPercentageDiff(mStrokeAuthExtended.MaxInterestPointIndex, mStrokeStoredExtended.MaxInterestPointIndex);
		MaxInterestPointDensity = Utils.GetInstance().GetUtilsMath().GetPercentageDiff(mStrokeAuthExtended.MaxInterestPointDensity, mStrokeStoredExtended.MaxInterestPointDensity);
		MaxInterestPointLocation = Math.abs(mStrokeAuthExtended.MaxInterestPointLocation - mStrokeStoredExtended.MaxInterestPointLocation);
		MaxInterestPointPressure = Utils.GetInstance().GetUtilsMath().GetPercentageDiff(mStrokeAuthExtended.MaxInterestPointPressure, mStrokeStoredExtended.MaxInterestPointPressure);
		MaxInterestPointSurface = Utils.GetInstance().GetUtilsMath().GetPercentageDiff(mStrokeAuthExtended.MaxInterestPointSurface, mStrokeStoredExtended.MaxInterestPointSurface);
		MaxInterestPointVelocity = Utils.GetInstance().GetUtilsMath().GetPercentageDiff(mStrokeAuthExtended.MaxInterestPointVelocity, mStrokeStoredExtended.MaxInterestPointVelocity);
		MaxInterestPointAcceleration = Utils.GetInstance().GetUtilsMath().GetPercentageDiff(mStrokeAuthExtended.MaxInterestPointAcceleration, mStrokeStoredExtended.MaxInterestPointAcceleration);

		ArrayList<Double> list = new ArrayList<>();
		list.add(MaxInterestPointIndex);
		list.add(MaxInterestPointDensity);
		list.add(MaxInterestPointLocation);
		list.add(MaxInterestPointPressure);
		list.add(MaxInterestPointSurface);
		list.add(MaxInterestPointVelocity);
		list.add(MaxInterestPointAcceleration);
		
		Collections.sort(list, new Comparator<Double>() {
          public int compare(Double score1, Double score2) {
              if (score1 > score2) {
                  return 1;
              }
              if (score1 < score2) {
                  return -1;
              }
              return 0;
          }
		});		
		
		InterestPointScore = 0;
		for(int idx = 0; idx < list.size(); idx++) {
			InterestPointScore += list.get(idx);
		}
		InterestPointScore = InterestPointScore / list.size();
		
//		CompareParameter(ConstsParamNames.Stroke.InterestPoints.STROKE_MAX_INTEREST_POINT_INDEX, maxInterestPointIndex);
//		CompareParameter(ConstsParamNames.Stroke.InterestPoints.STROKE_MAX_INTEREST_POINT_DENSITY, maxInterestPointDensity);
//		CompareParameter(ConstsParamNames.Stroke.InterestPoints.STROKE_MAX_INTEREST_POINT_LOCATION, maxInterestPointLocation);
//		CompareParameter(ConstsParamNames.Stroke.InterestPoints.STROKE_MAX_INTEREST_POINT_PRESSURE, maxInterestPointPressure);
//		CompareParameter(ConstsParamNames.Stroke.InterestPoints.STROKE_MAX_INTEREST_POINT_SURFACE, maxInterestPointSurface);
//		CompareParameter(ConstsParamNames.Stroke.InterestPoints.STROKE_MAX_INTEREST_POINT_VELOCITY, maxInterestPointVelocity);
//		CompareParameter(ConstsParamNames.Stroke.InterestPoints.STROKE_MAX_INTEREST_POINT_ACCELERATION, maxInterestPointAcceleration);		
	}
	
	private void CompareRadials() {
		double maxRadialVelocityAuth = mStrokeAuthExtended.StrokeMaxRadialVelocity;
		double maxRadialAccelerationAuth = mStrokeAuthExtended.StrokeMaxRadialAcceleration;
		
		CompareParameter(ConstsParamNames.Stroke.STROKE_MAX_RADIAL_VELOCITY, maxRadialVelocityAuth);
		CompareParameter(ConstsParamNames.Stroke.STROKE_MAX_RADIAL_ACCELERATION, maxRadialAccelerationAuth);		
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

	private double CalculateDtwForSamplingVector(String paramName, String samplingType, double factor, int idxShift) {		
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
		
		double minDistance = tempFeatureDtw.GetMinDtwDistance(tempListEvents, paramName, idxShift);
		
		double result = (1 - minDistance) * factor;
		if(result > 1) {
			result = 1;
		}
		
		return result;
	}
	
	private double CheckDtwScore(double value, double userLower, double userUpper, double hackLower, double hackUpper) {
		boolean isUser = Utils.GetInstance().GetUtilsMath().IsBetween(value, userLower, userUpper);
		boolean isHacker = Utils.GetInstance().GetUtilsMath().IsBetween(value, hackLower, hackUpper);
		
		double score = 0;
		if(isUser) {
			score++;
		}
		if(isHacker) {
			score--;
		}
		return score;
	}	
	
	
	private void PcaAnalysis() {
		String keyMatrix = mUtilsGeneral.GenerateStrokeMatrixMeanKey(mStrokeStoredExtended.GetInstruction(), mStrokeStoredExtended.GetStrokeIdx());
		
		FeatureMatrix featureMatrixStored = (FeatureMatrix) mStrokeStoredExtended.GetFeatureMeansHash().get(keyMatrix);
		double[][] rawMatrixStored = Utils.GetInstance().GetUtilsComparison().ConvertMatrix(featureMatrixStored);
		
		FeatureMatrix featureMatrixAuth = (FeatureMatrix) mStrokeAuthExtended.GetFeatureMeansHash().get(keyMatrix);
		double[][] rawMatrixAuth = Utils.GetInstance().GetUtilsComparison().ConvertMatrix(featureMatrixAuth);
		
		Matrix matrixStored = new Matrix(rawMatrixStored);
		Matrix matrixAuth = new Matrix(rawMatrixAuth);
		
		PCA pca = new PCA(matrixStored);
		Matrix transformedData = pca.transform(matrixAuth, PCA.TransformationType.WHITENING);
		
		PcaScore = Math.abs(transformedData.get(0, 0));
		double maxValue = 20;
		if(PcaScore > maxValue) {
			PcaScore = maxValue;
		}		
		PcaScore = PcaScore / maxValue;
		PcaScore = 1 - PcaScore;
	}
	
	private void PcaAnalysis2() {
		try {
			int idxStroke = mStrokeStoredExtended.GetStrokeIdx();
			String instruction = mStrokeStoredExtended.GetInstruction();
			String param = ConstsParamNames.Stroke.STROKE_TEMPORAL_SAMPLING;
			
			String key = mUtilsGeneral.GenerateStrokeFeatureMeanKey(instruction, param, idxStroke);
			FeatureMeanDataListEvents featureMeanDataListEvents = (FeatureMeanDataListEvents) mStrokeStoredExtended.GetFeatureMeansHash().get(key);
			
			ArrayList<ArrayList<MotionEventExtended>> listOfLists = featureMeanDataListEvents.GetListOfListEvents();
			ArrayList<MotionEventExtended> listEventsTemp;
			
			double[][] matrixTraining = new double[listOfLists.size()][ConstsGeneral.SPATIAL_SAMPLING_SIZE];
			double[][] matrixTest     = new double[1][ConstsGeneral.SPATIAL_SAMPLING_SIZE];
			
			for(int idx = 0; idx < listOfLists.size(); idx++) {
				listEventsTemp = listOfLists.get(idx);
				
				for(int idxEvent = 0; idxEvent < ConstsGeneral.SPATIAL_SAMPLING_SIZE; idxEvent++) {
					matrixTraining[idx][idxEvent] = listEventsTemp.get(idxEvent).Velocity;			
				}			
			}
						
			for(int idxEvent = 0; idxEvent < ConstsGeneral.SPATIAL_SAMPLING_SIZE; idxEvent++) {
				matrixTest[0][idxEvent] = mStrokeAuthExtended.ListEventsTemporalExtended.get(idxEvent).Velocity;	
			}
			
			Matrix trainingData = new Matrix(matrixTraining);
			Matrix testData = new Matrix(matrixTest);
			
			PCA pca = new PCA(trainingData);
			Matrix transformedData = pca.transform(testData, PCA.TransformationType.WHITENING);			
						
			PcaScore = transformedData.get(0, 0);
			
//			for(int r = 0; r < transformedData.getRowDimension(); r++){
//				for(int c = 0; c < transformedData.getColumnDimension(); c++){
//					System.out.print(transformedData.get(r, c));
//					if (c == transformedData.getColumnDimension()-1) continue;
//					System.out.print(", ");
//				}
//				System.out.println("");
//			}
		}
		catch(Exception exc) {
			String msg = exc.getMessage();
		}		
	}
	
	private void CreateSpatialVectorsForDtwAnalysis() {

		DTWObjVelocity tempVelocity;
		ArrayList<IDTWObj> listDtwStored = new ArrayList<>();
		ArrayList<IDTWObj> listDtwAuth = new ArrayList<>();
		
		for(int idx = 0; idx < mStrokeStoredExtended.ListEventsFreqExtended.size(); idx++) {
			tempVelocity = new DTWObjVelocity(mStrokeStoredExtended.ListEventsFreqExtended.get(idx).Velocity);
			listDtwStored.add(tempVelocity);
		}
		
		for(int idx = 0; idx < mStrokeAuthExtended.ListEventsFreqExtended.size(); idx++) {
			tempVelocity = new DTWObjVelocity(mStrokeAuthExtended.ListEventsFreqExtended.get(idx).Velocity);
			listDtwAuth.add(tempVelocity);
		}
		
		UtilsDTW dtwVelocities = new UtilsDTW(listDtwStored, listDtwAuth);
		double numElements = Utils.GetInstance().GetUtilsMath().GetMaxValue(listDtwStored.size(), listDtwAuth.size());
		DtwYona = dtwVelocities.getDistance() / numElements;
		
		DtwTemporalVelocity6 = CalculateDtwForSamplingVector(ConstsParamNames.StrokeSampling.VELOCITIES, ConstsParamNames.Stroke.STROKE_SPATIAL_SAMPLING, 1, 6);
		DtwTemporalVelocity = CalculateDtwForSamplingVector(ConstsParamNames.StrokeSampling.VELOCITIES, ConstsParamNames.Stroke.STROKE_SPATIAL_SAMPLING, 1, -1);
		
//		DtwTemporalVelocity0 = CalculateDtwForSamplingVector(ConstsParamNames.StrokeSampling.DELTA_TETA, ConstsParamNames.Stroke.STROKE_SPATIAL_SAMPLING, 1, 0);
//		DtwTemporalVelocity1 = CalculateDtwForSamplingVector(ConstsParamNames.StrokeSampling.DELTA_TETA, ConstsParamNames.Stroke.STROKE_SPATIAL_SAMPLING, 1, 1);
//		DtwTemporalVelocity2 = CalculateDtwForSamplingVector(ConstsParamNames.StrokeSampling.DELTA_TETA, ConstsParamNames.Stroke.STROKE_SPATIAL_SAMPLING, 1, 2);
//		DtwTemporalVelocity3 = CalculateDtwForSamplingVector(ConstsParamNames.StrokeSampling.DELTA_TETA, ConstsParamNames.Stroke.STROKE_SPATIAL_SAMPLING, 1, 3);
//		DtwTemporalVelocity4 = CalculateDtwForSamplingVector(ConstsParamNames.StrokeSampling.DELTA_TETA, ConstsParamNames.Stroke.STROKE_SPATIAL_SAMPLING, 1, 4);
//		DtwTemporalVelocity5 = CalculateDtwForSamplingVector(ConstsParamNames.StrokeSampling.DELTA_TETA, ConstsParamNames.Stroke.STROKE_SPATIAL_SAMPLING, 1, 5);
//		DtwTemporalVelocity6 = CalculateDtwForSamplingVector(ConstsParamNames.StrokeSampling.DELTA_TETA, ConstsParamNames.Stroke.STROKE_SPATIAL_SAMPLING, 1, 6);
//		DtwTemporalVelocity7 = CalculateDtwForSamplingVector(ConstsParamNames.StrokeSampling.DELTA_TETA, ConstsParamNames.Stroke.STROKE_SPATIAL_SAMPLING, 1, 7);
//		DtwTemporalVelocity8 = CalculateDtwForSamplingVector(ConstsParamNames.StrokeSampling.DELTA_TETA, ConstsParamNames.Stroke.STROKE_SPATIAL_SAMPLING, 1, 8);
//		DtwTemporalVelocity9 = CalculateDtwForSamplingVector(ConstsParamNames.StrokeSampling.DELTA_TETA, ConstsParamNames.Stroke.STROKE_SPATIAL_SAMPLING, 1, 9);
//		DtwTemporalVelocity10 = CalculateDtwForSamplingVector(ConstsParamNames.StrokeSampling.DELTA_TETA, ConstsParamNames.Stroke.STROKE_SPATIAL_SAMPLING, 1, 10);
//		DtwTemporalVelocity11 = CalculateDtwForSamplingVector(ConstsParamNames.StrokeSampling.DELTA_TETA, ConstsParamNames.Stroke.STROKE_SPATIAL_SAMPLING, 1, 11);
//		DtwTemporalVelocity12 = CalculateDtwForSamplingVector(ConstsParamNames.StrokeSampling.DELTA_TETA, ConstsParamNames.Stroke.STROKE_SPATIAL_SAMPLING, 1, 12);
//		DtwTemporalVelocity13 = CalculateDtwForSamplingVector(ConstsParamNames.StrokeSampling.DELTA_TETA, ConstsParamNames.Stroke.STROKE_SPATIAL_SAMPLING, 1, 13);
//		DtwTemporalVelocity14 = CalculateDtwForSamplingVector(ConstsParamNames.StrokeSampling.DELTA_TETA, ConstsParamNames.Stroke.STROKE_SPATIAL_SAMPLING, 1, 14);
//		DtwTemporalVelocity15 = CalculateDtwForSamplingVector(ConstsParamNames.StrokeSampling.DELTA_TETA, ConstsParamNames.Stroke.STROKE_SPATIAL_SAMPLING, 1, 15);		
		
		double timeDiff = mStrokeAuthExtended.ListEventsTemporalExtended.get(1).EventTime - mStrokeAuthExtended.ListEventsTemporalExtended.get(0).EventTime; 
		double velocityDiff;
		
		for(int idx = 1; idx < mStrokeAuthExtended.ListEventsTemporalExtended.size(); idx++) {
			velocityDiff = mStrokeAuthExtended.ListEventsTemporalExtended.get(idx).Velocity - mStrokeAuthExtended.ListEventsTemporalExtended.get(idx - 1).Velocity;
			mStrokeAuthExtended.ListEventsTemporalExtended.get(idx).Acceleration = velocityDiff / timeDiff;
		}

//		DtwTemporalAcceleration = CalculateDtwForSamplingVector(ConstsParamNames.StrokeSampling.ACCELERATIONS, ConstsParamNames.Stroke.STROKE_TEMPORAL_SAMPLING, 1);		
		
//		double[] vectorVelAuth = Utils.GetInstance().GetUtilsVectors().GetVectorVel(mStrokeAuthExtended.ListEventsTemporalExtended);
//		double[] vectorPressureAuth = Utils.GetInstance().GetUtilsVectors().GetVectorPressure(mStrokeAuthExtended.ListEventsTemporalExtended);
//		
//		double[] vectorVelStored = Utils.GetInstance().GetUtilsVectors().GetVectorVel(mStrokeStoredExtended.ListEventsTemporalExtended);
//		double[] vectorPressureStored = Utils.GetInstance().GetUtilsVectors().GetVectorPressure(mStrokeStoredExtended.ListEventsTemporalExtended);
				
//		DtwTemporalVelocity = CalculateDtwForSamplingVector(ConstsParamNames.StrokeSampling.VELOCITIES, ConstsParamNames.Stroke.STROKE_TEMPORAL_SAMPLING, 1);		
		
		
//		DtwSpatialRadialVelocity = CalculateDtwForSamplingVector(ConstsParamNames.StrokeSampling.RADIAL_VELOCITIES, ConstsParamNames.Stroke.STROKE_SPATIAL_SAMPLING, 1);
//		DtwSpatialTeta = CalculateDtwForSamplingVector(ConstsParamNames.StrokeSampling.TETA, ConstsParamNames.Stroke.STROKE_SPATIAL_SAMPLING, 1);			
//		DtwSpatialAcceleration = CalculateDtwForSamplingVector(ConstsParamNames.StrokeSampling.ACCELERATIONS, ConstsParamNames.Stroke.STROKE_SPATIAL_SAMPLING, 1);	
//		DtwSpatialRadialAcceleration = CalculateDtwForSamplingVector(ConstsParamNames.StrokeSampling.RADIAL_ACCELERATION, ConstsParamNames.Stroke.STROKE_SPATIAL_SAMPLING, 1);
//		DtwSpatialRadius = CalculateDtwForSamplingVector(ConstsParamNames.StrokeSampling.RADIUS, ConstsParamNames.Stroke.STROKE_SPATIAL_SAMPLING , 1);		
//		DtwSpatialDeltaTeta = CalculateDtwForSamplingVector(ConstsParamNames.StrokeSampling.DELTA_TETA, ConstsParamNames.Stroke.STROKE_SPATIAL_SAMPLING, 1);
//		DtwSpatialAccumNormArea = CalculateDtwForSamplingVector(ConstsParamNames.StrokeSampling.ACCUMULATED_NORM_AREA, ConstsParamNames.Stroke.STROKE_SPATIAL_SAMPLING, 1);		
//				
						
//		DtwTemporalRadialVelocity = CalculateDtwForSamplingVector(ConstsParamNames.StrokeSampling.RADIAL_VELOCITIES, ConstsParamNames.Stroke.STROKE_TEMPORAL_SAMPLING, 1);
//		DtwTemporalRadialAcceleration = CalculateDtwForSamplingVector(ConstsParamNames.StrokeSampling.RADIAL_ACCELERATION, ConstsParamNames.Stroke.STROKE_TEMPORAL_SAMPLING, 1);
//		DtwTemporalRadius = CalculateDtwForSamplingVector(ConstsParamNames.StrokeSampling.RADIUS, ConstsParamNames.Stroke.STROKE_TEMPORAL_SAMPLING, 1);
//		DtwTemporalTeta = CalculateDtwForSamplingVector(ConstsParamNames.StrokeSampling.TETA, ConstsParamNames.Stroke.STROKE_TEMPORAL_SAMPLING, 1);
//		DtwTemporalDeltaTeta = CalculateDtwForSamplingVector(ConstsParamNames.StrokeSampling.DELTA_TETA, ConstsParamNames.Stroke.STROKE_TEMPORAL_SAMPLING, 1);
//		DtwTemporalAccumNormArea = CalculateDtwForSamplingVector(ConstsParamNames.StrokeSampling.ACCUMULATED_NORM_AREA, ConstsParamNames.Stroke.STROKE_TEMPORAL_SAMPLING, 1);
//		
//		double DtwScore = 0;
//		DtwScore += CheckDtwScore(DtwTemporalAcceleration, 0.05, 0.34, 0.036, 0.29);
//		DtwScore += CheckDtwScore(DtwTemporalAccumNormArea, 0.70, 1, 0.66, 0.97);
//		DtwScore += CheckDtwScore(DtwTemporalDeltaTeta, 0.054, 0.5, 0.022, 0.45);
//		DtwScore += CheckDtwScore(DtwSpatialRadialAcceleration, 0.077, 0.26, 0.055, 0.22);
//		DtwScore += CheckDtwScore(DtwTemporalRadialVelocity, 0, 0.466, 0, 0.403);
//		DtwScore += CheckDtwScore(DtwSpatialVelocity, 0.72, 0.98, 0.57, 0.944);
		
//		ArrayList<Double> listScores = new ArrayList<>();
//		listScores.add(DtwSpatialVelocity);
//		listScores.add(DtwSpatialRadialVelocity);
//		listScores.add(DtwTemporalAcceleration);
//		
//		Collections.sort(listScores, new Comparator<Double>() {
//            public int compare(Double score1, Double score2) {
//                if (score1 > score2) {
//                    return 1;
//                }
//                if (score1 < score2) {
//                    return -1;
//                }
//                return 0;
//            }
//        });
//		
//		listScores.remove(0);
				
		DtwSpatialTotalScore = DtwTemporalVelocity6;
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
		double maxAccelerationAuth = mStrokeAuthExtended.StrokeMaxAcceleration;
		
		CompareParameter(ConstsParamNames.Stroke.STROKE_MAX_ACCELERATION, maxAccelerationAuth);
	}
	
	protected void CompareStrokeTransitionTimes() {
		if(mStrokeAuthExtended.GetStrokeIdx() > 0) {
			double transitionTimeAuth = mStrokeAuthExtended.StrokeTransitionTime;		
			CompareParameter(ConstsParamNames.Stroke.STROKE_TRANSITION_TIME, transitionTimeAuth);	
		}
	}
	
	protected void CompareAccelerometer() {
		AccDiffX = Utils.GetInstance().GetUtilsMath().GetPercentageDiff(mStrokeAuthExtended.StrokeAccMovX, mStrokeStoredExtended.StrokeAccMovX);
		AccDiffY = Utils.GetInstance().GetUtilsMath().GetPercentageDiff(mStrokeAuthExtended.StrokeAccMovY, mStrokeStoredExtended.StrokeAccMovY);
		AccDiffZ = Utils.GetInstance().GetUtilsMath().GetPercentageDiff(mStrokeAuthExtended.StrokeAccMovZ, mStrokeStoredExtended.StrokeAccMovZ);
		AccDiffTotal = Utils.GetInstance().GetUtilsMath().GetPercentageDiff(mStrokeAuthExtended.StrokeAccMovTotal, mStrokeStoredExtended.StrokeAccMovTotal);
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
//		mCompareResult.Score = (mCompareResult.Score + DtwSpatialVelocity) / 2;
		
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