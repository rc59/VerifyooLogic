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
import Data.MetaData.IndexBoundary;
import Data.MetaData.InterestPoint;
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
import Logic.Utils.DSP;
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
	public double SumXDiff;
	public double SumYDiff;
	
	protected boolean mIsStrokesIdentical;
	
	protected IStatEngine mStatEngine;
	protected int mStoredStrokeKey;
	
	protected UtilsGeneral mUtilsGeneral;
	protected UtilsVectors mUtilsVectors;	
	protected UtilsComparison mUtilsComparison;
	
	protected CompareResultSummary mCompareResult;
	
	protected StrokeExtended mStrokeStoredExtended;
	protected StrokeExtended mStrokeAuthExtended;		
	protected double mMinCosineDistanceScore;
	protected boolean mIsSimilarDevices;
	
	public boolean IsStrokeTypesValid;
	
	public double TotalAreaScore;
	
	public double InterestPointScore;
	public double InterestPointScoreFinal;
	
	public double PcaScore;
	public double PcaScoreFinal;
	
	public double DtwSpatialTotalScore;
	public double DtwSpatialTotalScoreFinal;	
	
	public double DtwScoreToRemove;		
	
	public double RadialVelocityDiff;
	public double RadialAccelerationDiff;	
	
	public double DtwSpatialVelocity;
	public double DtwSpatialVelocity16;
	
	public double DtwTemporalVelocity;
	
	public double StrokeDistanceTotalScoreStartToStart;
	public double StrokeDistanceTotalScoreStartToEnd;
	public double StrokeDistanceTotalScoreEndToStart;
	public double StrokeDistanceTotalScoreEndToEnd;
	
	public double StrokeDistanceTotalScore;
	
	public double InterestPointNewIdxStartDiff;
	public double InterestPointNewIdxEndDiff;
	public double InterestPointNewIdxAvgDiff;
	
	public double InterestPointCountPercentageDiff;
	public double InterestPointCountDiff;
	public double InterestPointMinorCountDiff;
	
	public double InterestPointCountMinor;
	public double InterestPointCountMajor;
	
	public double InterestPointStartIntensity;
	public double InterestPointStartAvgVelocity;
	
	public double InterestPointEndIntensity;
	public double InterestPointEndAvgVelocity;	
	
	public double StrokeAvgDensityScore;
	
	public double InterestPointNewIdxLocationDiff;
	public double InterestPointNewIdxAvgVelocity;
	public double InterestPointNewIdxIntensity;
	
	public int InterestPointCountAuth;	
	public int InterestPointCountStored;
	public int InterestPointCountTotalDiff;
	
	public double InterestPointNewIdxLocationDiff2;
	public double InterestPointNewIdxAvgVelocity2;
	public double InterestPointNewIdxIntensity2;
	
	public double StrokeAvgVelocity;	
	
	public boolean NumIntPointsSimilar;
	
	public double VelocitiesConvolution;
	
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
				CheckStoredStrokeKey();
				
				
//				CompareNumEvents();
				
				CompareCoordSums();
				CompareStrokeDistances();
				DtwVelocities();
				CompareStrokeAreas();
				CreateSpatialVectorsForDtwAnalysis();
				PcaAnalysis();
 				CompareMinCosineDistance();				
				CompareLengths();
				CompareTimeInterval();
				CompareNumEvents();
				CompareVelocities();
//				ComparePressureAndSurface();
//				CompareAccelerometer();
				CompareStrokeTransitionTimes();
//				CompareAccelerations();
//				CompareRadials();			
				CompareInterestPoints();
//				CalculateFinalScoresToDtwPcaInterestPoints();
				CheckStrokeTypes();
//				CalculateFinalScore();
//				CheckFinalScore();
			}
		}
		
		if(pointStatus == PointStatus.BOTH) {
			mCompareResult.Score = 1;
		}
		if(pointStatus == PointStatus.ONE) {
			mCompareResult.Score = 0;
		}		
	}				

	private void CompareCoordSums() {
		double authSumX = mStrokeAuthExtended.SumX;
		double authSumY = mStrokeAuthExtended.SumY;
		
//		SumXDiff = CompareParameter(ConstsParamNames.Stroke.STROKE_SUM_X, authSumX);
//		SumYDiff = CompareParameter(ConstsParamNames.Stroke.STROKE_SUM_Y, authSumY);
	}

	private void CompareInterestPoints() {
		InterestPointCountPercentageDiff = Utils.GetInstance().GetUtilsMath().GetPercentageDiff(mStrokeStoredExtended.ListInterestPoints.size(), mStrokeAuthExtended.ListInterestPoints.size());
		InterestPointCountDiff = Math.abs(mStrokeStoredExtended.ListInterestPoints.size() - mStrokeAuthExtended.ListInterestPoints.size());
		InterestPointMinorCountDiff = Math.abs(mStrokeStoredExtended.NumInterestPointsMinor - mStrokeAuthExtended.NumInterestPointsMinor);
				
		double intPointCountMinorAuth = mStrokeAuthExtended.InterestPointsCountMinor;
		double intPointCountMajorAuth = mStrokeAuthExtended.InterestPointsCountMajor;
		
//		InterestPointCountMinor = CompareParameter(ConstsParamNames.Stroke.STROKE_INT_POINT_COUNT_MINOR, intPointCountMinorAuth);
//		InterestPointCountMajor = CompareParameter(ConstsParamNames.Stroke.STROKE_INT_POINT_COUNT_MAJOR, intPointCountMajorAuth);
		
		double intPointStartIntensity = mStrokeAuthExtended.InterestPointStartIntensity;
		double intPointStartAvgVelocity = mStrokeAuthExtended.InterestPointStartAvgVelocity;
		
//		CompareParameter(ConstsParamNames.Stroke.STROKE_INT_POINT_START_INTENSITY, intPointStartIntensity);
//		CompareParameter(ConstsParamNames.Stroke.STROKE_INT_POINT_START_AVG_VELOCITY, intPointStartAvgVelocity);
		
		double intPointEndIntensity = mStrokeAuthExtended.InterestPointEndIntensity;
		double intPointEndAvgVelocity = mStrokeAuthExtended.InterestPointEndAvgVelocity;		
		
//		CompareParameter(ConstsParamNames.Stroke.STROKE_INT_POINT_END_INTENSITY, intPointEndIntensity);
//		CompareParameter(ConstsParamNames.Stroke.STROKE_INT_POINT_END_AVG_VELOCITY, intPointEndAvgVelocity);		
		
		if(InterestPointCountDiff == 0) {
			if(mStrokeStoredExtended.ListInterestPoints.size() == 0 && mStrokeAuthExtended.ListInterestPoints.size() == 0) {
				InterestPointNewIdxStartDiff = 1;
				InterestPointNewIdxEndDiff = 1;
				InterestPointNewIdxAvgDiff = 1;
				InterestPointNewIdxLocationDiff = 1;
			}
			else {
				InterestPointNewIdxStartDiff = 0;
				InterestPointNewIdxEndDiff = 0;
				InterestPointNewIdxAvgDiff = 0;
				InterestPointNewIdxLocationDiff = 0;
				
				for(int idx = 0; idx < mStrokeStoredExtended.ListInterestPoints.size(); idx++) {
					InterestPointNewIdxStartDiff += Utils.GetInstance().GetUtilsMath().GetPercentageDiff(mStrokeStoredExtended.ListInterestPoints.get(idx).IdxStart, mStrokeAuthExtended.ListInterestPoints.get(idx).IdxStart);
					InterestPointNewIdxEndDiff += Utils.GetInstance().GetUtilsMath().GetPercentageDiff(mStrokeStoredExtended.ListInterestPoints.get(idx).IdxEnd, mStrokeAuthExtended.ListInterestPoints.get(idx).IdxEnd);
					InterestPointNewIdxAvgDiff += Utils.GetInstance().GetUtilsMath().GetPercentageDiff(mStrokeStoredExtended.ListInterestPoints.get(idx).IdxAverage, mStrokeAuthExtended.ListInterestPoints.get(idx).IdxAverage);
					InterestPointNewIdxLocationDiff += Utils.GetInstance().GetUtilsMath().GetPercentageDiff(mStrokeStoredExtended.ListInterestPoints.get(idx).IdxLocation, mStrokeAuthExtended.ListInterestPoints.get(idx).IdxLocation);
				}
				
				double numInterestPoints = mStrokeStoredExtended.ListInterestPoints.size();
				InterestPointNewIdxStartDiff = InterestPointNewIdxStartDiff / numInterestPoints;
				InterestPointNewIdxEndDiff = InterestPointNewIdxEndDiff / numInterestPoints;
				InterestPointNewIdxAvgDiff = InterestPointNewIdxAvgDiff / numInterestPoints;
				InterestPointNewIdxLocationDiff = InterestPointNewIdxLocationDiff / numInterestPoints;
			}
		}
						
		double intPointPressureAuth = mStrokeAuthExtended.InterestPointPressure;
		double intPointSurfaceAuth = mStrokeAuthExtended.InterestPointSurface;

//		MiddlePressureScore = CompareParameter(ConstsParamNames.Stroke.STROKE_INT_POINT_PRESSURE, intPointPressureAuth);
		MiddleSurfaceScore = CompareParameter(ConstsParamNames.Stroke.STROKE_INT_POINT_SURFACE, intPointSurfaceAuth);
		
		double strokeAvgDensityAuth = mStrokeAuthExtended.StrokeAverageDensity;
		
		double intPointLocationAuth = mStrokeAuthExtended.InterestPointLocation;
		double intPointAvgVelocityAuth = mStrokeAuthExtended.InterestPointAvgVelocity;		
		double intPointIntensityAuth = mStrokeAuthExtended.InterestPointIntensity;
		
		String key = mUtilsGeneral.GenerateStrokeFeatureMeanKey(mStrokeStoredExtended.GetInstruction(), ConstsParamNames.Stroke.STROKE_INT_POINT_LOCATION, mStrokeStoredExtended.GetStrokeIdx());
		
//		FindInterestPointInArea(mStrokeAuthExtended, intPointLocationMean);		
		
		//StrokeAvgDensityScore = CompareParameter(ConstsParamNames.Stroke.STROKE_AVG_DENSITY, strokeAvgDensityAuth);
		
		InterestPointNewIdxLocationDiff = 0;
		InterestPointNewIdxAvgVelocity = 0;
		InterestPointNewIdxIntensity = 0;
		
		ArrayList<InterestPoint> listAllPoints = new ArrayList<>();
		listAllPoints.addAll(mStrokeAuthExtended.ListInterestPoints);
		listAllPoints.addAll(mStrokeAuthExtended.ListInterestPointsMinor);
		
		Collections.sort(listAllPoints, new Comparator<InterestPoint>() {
            @Override
            public int compare(InterestPoint value1, InterestPoint value2) {
                if (value1.Intensity > value2.Intensity) {
                    return -1;
                }
                if (value1.Intensity < value2.Intensity) {
                    return 1;
                }
                return 0;
            }
        });
		
		while(listAllPoints.size() > 2) {
			listAllPoints.remove(listAllPoints.size() - 1);
		}
		
		Collections.sort(listAllPoints, new Comparator<InterestPoint>() {
            @Override
            public int compare(InterestPoint value1, InterestPoint value2) {
                if (value1.IdxLocation > value2.IdxLocation) {
                    return 1;
                }
                if (value1.IdxLocation < value2.IdxLocation) {
                    return -1;
                }
                return 0;
            }
        });
		
		ArrayList<Double> listExistingIntPointLocations =
				Utils.GetInstance().GetUtilsComparison().GetNumOfInterestPoints(mStrokeStoredExtended.GetInstruction(), mStrokeStoredExtended.GetStrokeIdx(), mStrokeStoredExtended.GetFeatureMeansHash());
		
		int numInterestPoints = listExistingIntPointLocations.size();
		int idxIntPointTemp;
		
		double tempScore;
		
		InterestPointCountStored = numInterestPoints;
		InterestPointCountAuth = listAllPoints.size();
		InterestPointCountTotalDiff = InterestPointCountStored - InterestPointCountAuth;
		
		if(numInterestPoints == listAllPoints.size()) {
			NumIntPointsSimilar = true;
			if(numInterestPoints > 0) {
				for(int idxIntPoint = 0; idxIntPoint < numInterestPoints; idxIntPoint++) {
					idxIntPointTemp = idxIntPoint;
					
					intPointLocationAuth = listAllPoints.get(idxIntPointTemp).IdxLocation;
					intPointAvgVelocityAuth = listAllPoints.get(idxIntPointTemp).AverageVelocity;		
					intPointIntensityAuth = listAllPoints.get(idxIntPointTemp).Intensity;					
					
					tempScore = CompareParameter(Utils.GetInstance().GetUtilsGeneral().GenerateInterestPointKey(ConstsParamNames.Stroke.STROKE_INT_POINT_LOCATION, idxIntPointTemp), intPointLocationAuth);
					if(idxIntPointTemp == 0) {
						InterestPointNewIdxLocationDiff = tempScore;	
					}
					else {
						InterestPointNewIdxLocationDiff2 = tempScore;
					}
										
					tempScore = CompareParameter(Utils.GetInstance().GetUtilsGeneral().GenerateInterestPointKey(ConstsParamNames.Stroke.STROKE_INT_POINT_AVG_VELOCITY, idxIntPointTemp), intPointAvgVelocityAuth);
					if(idxIntPointTemp == 0) {
						InterestPointNewIdxAvgVelocity = tempScore;	
					}
					else {
						InterestPointNewIdxAvgVelocity2 = tempScore;
					}
					
					//tempScore = CompareParameter(Utils.GetInstance().GetUtilsGeneral().GenerateInterestPointKey(ConstsParamNames.Stroke.STROKE_INT_POINT_INTENSITY, idxIntPointTemp), intPointIntensityAuth);
					if(idxIntPointTemp == 0) {
						InterestPointNewIdxIntensity = tempScore;
					}
					else {
						InterestPointNewIdxIntensity2 = tempScore;
					}
				}
				
//				InterestPointNewIdxLocationDiff = InterestPointNewIdxLocationDiff / numInterestPoints;
//				InterestPointNewIdxAvgVelocity = InterestPointNewIdxAvgVelocity / numInterestPoints;
//				InterestPointNewIdxIntensity = InterestPointNewIdxIntensity / numInterestPoints;			
//				double totalIntScore = (InterestPointNewIdxLocationDiff + InterestPointNewIdxAvgVelocity + InterestPointNewIdxIntensity) / 3;
			}
			else {
				InterestPointNewIdxLocationDiff = 1;
				InterestPointNewIdxAvgVelocity = 1;
				InterestPointNewIdxIntensity = 1;	
				InterestPointNewIdxLocationDiff2 = 1;
				InterestPointNewIdxAvgVelocity2 = 1;
				InterestPointNewIdxIntensity2 = 1;	
			}
		}
		else {
			if(listAllPoints.size() > 0 && numInterestPoints > 0) {
				if(listAllPoints.size() > numInterestPoints) {
					double intPointLocationAuth1 = listAllPoints.get(0).IdxLocation;
					double intPointLocationAuth2 = listAllPoints.get(1).IdxLocation;
					
					double intPointLocationAuth1Score = CompareParameter(Utils.GetInstance().GetUtilsGeneral().GenerateInterestPointKey(ConstsParamNames.Stroke.STROKE_INT_POINT_LOCATION, 0), intPointLocationAuth1);
					double intPointLocationAuth2Score = CompareParameter(Utils.GetInstance().GetUtilsGeneral().GenerateInterestPointKey(ConstsParamNames.Stroke.STROKE_INT_POINT_LOCATION, 0), intPointLocationAuth2);
					
					if(intPointLocationAuth1Score > intPointLocationAuth2Score) {
						intPointLocationAuth = listAllPoints.get(0).IdxLocation;
						intPointAvgVelocityAuth = listAllPoints.get(0).AverageVelocity;		
						intPointIntensityAuth = listAllPoints.get(0).Intensity;
					}
					else {
						intPointLocationAuth = listAllPoints.get(1).IdxLocation;
						intPointAvgVelocityAuth = listAllPoints.get(1).AverageVelocity;		
						intPointIntensityAuth = listAllPoints.get(1).Intensity;
					}
					
					tempScore = CompareParameter(Utils.GetInstance().GetUtilsGeneral().GenerateInterestPointKey(ConstsParamNames.Stroke.STROKE_INT_POINT_LOCATION, 0), intPointLocationAuth);
					InterestPointNewIdxLocationDiff = tempScore;
					
					tempScore = CompareParameter(Utils.GetInstance().GetUtilsGeneral().GenerateInterestPointKey(ConstsParamNames.Stroke.STROKE_INT_POINT_AVG_VELOCITY, 0), intPointAvgVelocityAuth);
					InterestPointNewIdxAvgVelocity = tempScore;
					
					//tempScore = CompareParameter(Utils.GetInstance().GetUtilsGeneral().GenerateInterestPointKey(ConstsParamNames.Stroke.STROKE_INT_POINT_INTENSITY, 0), intPointIntensityAuth);
					InterestPointNewIdxIntensity = tempScore;
				}
				if(listAllPoints.size() < numInterestPoints) {
					intPointLocationAuth = listAllPoints.get(0).IdxLocation;
					intPointAvgVelocityAuth = listAllPoints.get(0).AverageVelocity;		
					intPointIntensityAuth = listAllPoints.get(0).Intensity;
					
					double intPointLocationAuth1Score = CompareParameter(Utils.GetInstance().GetUtilsGeneral().GenerateInterestPointKey(ConstsParamNames.Stroke.STROKE_INT_POINT_LOCATION, 0), intPointLocationAuth);
					double intPointLocationAuth2Score = CompareParameter(Utils.GetInstance().GetUtilsGeneral().GenerateInterestPointKey(ConstsParamNames.Stroke.STROKE_INT_POINT_LOCATION, 1), intPointLocationAuth);
					
					int idxIntPointToUse;
					if(intPointLocationAuth1Score > intPointLocationAuth2Score) {
						idxIntPointToUse = 0;
					}
					else {
						idxIntPointToUse = 1;
					}
					
					tempScore = CompareParameter(Utils.GetInstance().GetUtilsGeneral().GenerateInterestPointKey(ConstsParamNames.Stroke.STROKE_INT_POINT_LOCATION, idxIntPointToUse), intPointLocationAuth);
					InterestPointNewIdxLocationDiff = tempScore;
					
					tempScore = CompareParameter(Utils.GetInstance().GetUtilsGeneral().GenerateInterestPointKey(ConstsParamNames.Stroke.STROKE_INT_POINT_AVG_VELOCITY, idxIntPointToUse), intPointAvgVelocityAuth);
					InterestPointNewIdxAvgVelocity = tempScore;
					
//					tempScore = CompareParameter(Utils.GetInstance().GetUtilsGeneral().GenerateInterestPointKey(ConstsParamNames.Stroke.STROKE_INT_POINT_INTENSITY, idxIntPointToUse), intPointIntensityAuth);
					InterestPointNewIdxIntensity = tempScore;
				}
			}
		}
	}	

	private void FindInterestPointInArea(StrokeExtended strokeInput, double intPointLocationMean) {
		
		ArrayList<MotionEventExtended> listEventsExtended = strokeInput.ListEventsExtended;
		int numEvents = listEventsExtended.size();
		
		int idxAvg = (int) (numEvents * intPointLocationMean);
		
		int searchDistance = (int) ((double) listEventsExtended.size() * 0.2);
		int idxSearchStart = Math.min(numEvents - 1, idxAvg + searchDistance);
		int idxSearchEnd = Math.max(0, idxAvg - searchDistance);		
		
		int idxIntPointEnd = 0;
		int idxIntPointStart = 0;
		
		for(int idx = idxAvg; idx < idxSearchEnd - 1; idx++) {
			if(listEventsExtended.get(idx).EventDensity > listEventsExtended.get(idx + 1).EventDensity) {
				if(listEventsExtended.get(idx + 1).EventDensity > listEventsExtended.get(idx + 2).EventDensity) {
					idxIntPointEnd = idx;
					break;
				}
			}
		}
		
		for(int idx = idxAvg; idx > idxSearchStart + 1; idx--) {
			if(listEventsExtended.get(idx).EventDensity < listEventsExtended.get(idx - 1).EventDensity) {
				if(listEventsExtended.get(idx - 1).EventDensity < listEventsExtended.get(idx - 2).EventDensity) {
					idxIntPointStart = idx;
					break;
				}
			}
		}
		
		int idxIntPointAvg = (idxIntPointStart + idxIntPointEnd) / 2;
		mStrokeAuthExtended.InterestPointLocation = idxIntPointAvg / numEvents;
	}

	private void CheckStrokeTypes() {
		IsStrokeTypesValid = true;
		if(mStrokeAuthExtended.GetStrokeKey() != mStrokeStoredExtended.GetStrokeKey()) {
			IsStrokeTypesValid = false;
		}
	}

	private void CheckStoredStrokeKey() {
		mStoredStrokeKey = NormMgr.GetInstance().GetStrokeKey(mStrokeStoredExtended);
	}
	
	public int GetStoredStrokeKey() {		
		return mStoredStrokeKey;
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
	}
	
	private void DtwVelocities() {
		double[] velAuth = Utils.GetInstance().GetUtilsVectors().GetVectorVel(mStrokeAuthExtended.ListEventsSpatialExtended); 
		double[] velStored = Utils.GetInstance().GetUtilsVectors().GetVectorVel(mStrokeStoredExtended.ListEventsSpatialExtended);
		
		double[] result = DSP.conv(velAuth, velStored);
		double maxValue = Double.MIN_VALUE;
		for(int idx = 0; idx < result.length; idx++) {
			maxValue = Utils.GetInstance().GetUtilsMath().GetMaxValue(result[idx], maxValue);
		}
		VelocitiesConvolution = maxValue;
		
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
	
	private double[] GetDensityVector(StrokeExtended stroke) {
		double[] densityVectors = new double[stroke.ListEventsExtended.size()];
		
		for(int idx = 0; idx < stroke.ListEventsExtended.size(); idx++) {
			densityVectors[idx] = stroke.ListEventsExtended.get(idx).EventDensity;
		}
		
		return densityVectors;
	}	
	
	private void RunConv(double[] vector1, int idxStart1, int idxEnd1, double[] vector2, int idxStart2, int idxEnd2) {
		double[] subVector1 = new double[idxEnd1 - idxStart1 + 1];
		double[] subVector2 = new double[idxEnd2 - idxStart2 + 1];
		
		int idxCurr = 0;
		for(int idx = idxStart1; idx < idxEnd1; idx++) {
			subVector1[idxCurr] = vector1[idx];
			idxCurr++;
		}
		
		idxCurr = 0;
		for(int idx = idxStart2; idx < idxEnd2; idx++) {
			subVector2[idxCurr] = vector2[idx];
			idxCurr++;
		}
		
		double[] result = DSP.conv(subVector1, subVector2);
		double maxValue = Double.MIN_VALUE;
		for(int idx = 0; idx < result.length; idx++) {
			maxValue = Utils.GetInstance().GetUtilsMath().GetMaxValue(result[idx], maxValue);
		}
		
//		MaxInterestPointConvolution = maxValue;
	}	
	
//	private void DtwOnInterestZone(ArrayList<MotionEventExtended> listEventsAuth, ArrayList<MotionEventExtended> listEventsStored) {
//		if(listEventsAuth.size() > 0 && listEventsStored.size() > 0) {
//			IDTWObj tempObj;
//			ArrayList<IDTWObj> listDTWAuthVelocities = new ArrayList<>();
//			ArrayList<IDTWObj> listDTWStoredVelocities = new ArrayList<>();
//			
//			ArrayList<IDTWObj> listDTWAuthCoords = new ArrayList<>();
//			ArrayList<IDTWObj> listDTWStoredCoords= new ArrayList<>();
//			
//			for(int idx = 0; idx < listEventsAuth.size(); idx++) {
//				tempObj = new DTWObjDouble(listEventsAuth.get(idx).Velocity);
//				listDTWAuthVelocities.add(tempObj);
//				
//				tempObj = new DTWObjCoordinate(listEventsAuth.get(idx).Xnormalized, listEventsAuth.get(idx).Ynormalized);
//				listDTWAuthCoords.add(tempObj);
//			}
//			
//			for(int idx = 0; idx < listEventsStored.size(); idx++) {
//				tempObj = new DTWObjDouble(listEventsStored.get(idx).Velocity);
//				listDTWStoredVelocities.add(tempObj);
//				
//				tempObj = new DTWObjCoordinate(listEventsStored.get(idx).Xnormalized, listEventsStored.get(idx).Ynormalized);
//				listDTWStoredCoords.add(tempObj);
//			}
//			
//			UtilsDTW dtwVelocities = new UtilsDTW(listDTWAuthVelocities, listDTWStoredVelocities);
//			MaxInterestPointDTWVelocity = dtwVelocities.getDistance();
//			
//			UtilsDTW dtwCoords = new UtilsDTW(listDTWAuthCoords, listDTWStoredCoords);
//			MaxInterestPointDTWCoords = dtwCoords.getDistance();
//		}
//	}
	
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
		
		double maxValue = 8;
		PcaScoreFinal = PcaScore;
		if(PcaScoreFinal > maxValue) {
			PcaScoreFinal = maxValue;
		}		
		PcaScoreFinal = PcaScoreFinal / maxValue;
		PcaScoreFinal = 1 - PcaScoreFinal;
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
		
		DtwTemporalVelocity = CalculateDtwForSamplingVector(ConstsParamNames.StrokeSampling.VELOCITIES, ConstsParamNames.Stroke.STROKE_SPATIAL_SAMPLING, 1, 6);				
		
		double timeDiff = mStrokeAuthExtended.ListEventsTemporalExtended.get(1).EventTime - mStrokeAuthExtended.ListEventsTemporalExtended.get(0).EventTime; 
		double velocityDiff;
		
		for(int idx = 1; idx < mStrokeAuthExtended.ListEventsTemporalExtended.size(); idx++) {
			velocityDiff = mStrokeAuthExtended.ListEventsTemporalExtended.get(idx).Velocity - mStrokeAuthExtended.ListEventsTemporalExtended.get(idx - 1).Velocity;
			mStrokeAuthExtended.ListEventsTemporalExtended.get(idx).Acceleration = velocityDiff / timeDiff;
		}
				
		DtwSpatialTotalScore = DtwTemporalVelocity;
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
		double areaAuthMaxXMaxY = mStrokeAuthExtended.ShapeDataObj.ShapeAreaMaxXMaxY;
		double areaAuthMinXMaxY = mStrokeAuthExtended.ShapeDataObj.ShapeAreaMinXMaxY;
		double areaAuthMaxXMinY = mStrokeAuthExtended.ShapeDataObj.ShapeAreaMaxXMinY;
		
		if(!Utils.GetInstance().GetUtilsComparison().IsLineBucket(mStrokeStoredExtended.GetStrokeKey())) {
			
		}
		CompareParameter(ConstsParamNames.Stroke.STROKE_TOTAL_AREA, areaAuth);
		CompareParameter(ConstsParamNames.Stroke.STROKE_TOTAL_AREA_MINX_MINY, areaAuthMinXMinY);
		CompareParameter(ConstsParamNames.Stroke.STROKE_TOTAL_AREA_MAXX_MAXY, areaAuthMaxXMaxY);
		CompareParameter(ConstsParamNames.Stroke.STROKE_TOTAL_AREA_MINX_MAXY, areaAuthMinXMaxY);
		CompareParameter(ConstsParamNames.Stroke.STROKE_TOTAL_AREA_MAXX_MINY, areaAuthMaxXMinY);
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
		double avgAccelerationAuth = mStrokeAuthExtended.StrokeAverageAcceleration;
		
		CompareParameter(ConstsParamNames.Stroke.STROKE_MAX_ACCELERATION, maxAccelerationAuth);
		CompareParameter(ConstsParamNames.Stroke.STROKE_AVERAGE_ACCELERATION, avgAccelerationAuth);
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
		
//		CompareParameter(ConstsParamNames.Stroke.STROKE_AVERAGE_VELOCITY, avgVelocityAuth);
//		CompareParameter(ConstsParamNames.Stroke.STROKE_MAX_VELOCITY, maxVelocityAuth);
//		CompareParameter(ConstsParamNames.Stroke.STROKE_MID_VELOCITY, midVelocityAuth);
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
				mStatEngine.CompareStrokeDoubleValues(mStrokeAuthExtended.GetInstruction(), paramName, mStrokeStoredExtended.GetStrokeIdx(), mStrokeStoredExtended.GetStrokeKey(), authValue, mStrokeStoredExtended.GetFeatureMeansHash());

		double finalScore = result.GetScore();		
		AddDoubleParameter(paramName, finalScore, result.GetWeight(), authValue, result.GetBoundary());
		return finalScore;
	}
	
	public StrokeExtended GetBaseStroke() {
		return mStrokeStoredExtended;
	}
	
	protected void AddDoubleParameter(String parameterName, double score, double weight, double originalValue, double boundary)
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
			internalSdUserOnly = hashFeatureMeans.get(key).GetInternalSd();
		}
		
		INormData normData = NormMgr.GetInstance().GetNormDataByParamName(parameterName, mStrokeStoredExtended.GetInstruction(), mStrokeStoredExtended.GetStrokeKey());
		internalSd = normData.GetInternalStandardDev();
		popSd = normData.GetStandardDev();
		popMean = normData.GetMean();		
		
		ICompareResult compareResult = 
				(ICompareResult) new CompareResultGeneric(parameterName, score, originalValue, mean, popMean ,popSd, internalSd, internalSdUserOnly);
		compareResult.SetBoundary(boundary);
		compareResult.SetWeight(weight);
		mCompareResult.ListCompareResults.add(compareResult);
	}
	/****************************/
}