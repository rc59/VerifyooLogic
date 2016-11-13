package Logic.Comparison;

import java.util.ArrayList;
import java.util.Collections;
import java.util.Comparator;
import java.util.HashMap;

import Consts.ConstsFeatures;
import Consts.ConstsGeneral;
import Consts.ConstsParamNames;
import Consts.ConstsParamWeights;
import Consts.Enums.PointStatus;
import Data.Comparison.CompareResultGeneric;
import Data.Comparison.CompareResultSummary;
import Data.Comparison.Interfaces.ICompareResult;
import Data.MetaData.NormalizedParam;
import Data.MetaData.ValueFreq;
import Data.UserProfile.Extended.GestureExtended;
import Data.UserProfile.Extended.StrokeExtended;
import Logic.Comparison.Stats.FeatureMeanData;
import Logic.Comparison.Stats.StatEngine;
import Logic.Comparison.Stats.Data.Interface.IStatEngineResult;
import Logic.Comparison.Stats.Interfaces.IFeatureMeanData;
import Logic.Comparison.Stats.Interfaces.IStatEngine;
import Logic.Comparison.Stats.Norms.NormMgr;
import Logic.Comparison.Stats.Norms.Interfaces.INormData;
import Logic.Utils.Utils;
import Logic.Utils.UtilsAccumulator;
import Logic.Utils.UtilsComparison;
import Logic.Utils.UtilsGeneral;
import Logic.Utils.UtilsVectors;
import Logic.Utils.DTW.DTWObjCoordinate;
import Logic.Utils.DTW.IDTWObj;
import Logic.Utils.DTW.UtilsDTW;

public class GestureComparer {	
	
	protected boolean mIsGesturesIdentical;
	protected IStatEngine mStatEngine;
	
	protected GestureExtended mGestureStored;
	protected GestureExtended mGestureAuth;
	
	protected UtilsGeneral mUtilsGeneral;
	protected UtilsComparison mUtilsComparison;
	protected UtilsVectors mUtilsVectors;
	
	protected ArrayList<StrokeComparer> mListStrokeComparers;	
	public double mGestureScore;
	protected double mMinCosineDistanceScore;
	protected double mStrokesScore;
	CompareResultSummary mCompareResultsGesture;
	
	protected boolean mIsStrokeCosineDistanceValid;
	protected boolean mMinCosineDistanceValid;	
	
	protected boolean mIsSimilarDevices;
	
	protected HashMap<String, Double> mCompareFilters;	
	
	public double DtwScore;
	public double PcaScore;
	public double InterestPointScore;
	public double NumZeroScores;
	public double InterestPointDensity;
	public double InterestPointLocation;
		
	public double BooleanParamsScore;
	public double NormalizedParamsScore;
	
	boolean IsStrokeTypesValid;
	
	public double ScoreMean;
	public double ScoreStd;
	
	public GestureComparer(boolean isSimilarDevices)
	{	
		mIsSimilarDevices = isSimilarDevices;
		InitGestureComparer();				
		InitUtils();		
	}
	
	protected void InitGestureComparer()
	{
		mIsGesturesIdentical = false;
		mCompareFilters = null;
		mListStrokeComparers = new ArrayList<>();
		mGestureScore = 0;
		mStrokesScore = 0;
		mCompareResultsGesture = new CompareResultSummary();
		mStatEngine = StatEngine.GetInstance();
	}
	
	protected void InitUtils()
	{
		mUtilsComparison = Utils.GetInstance().GetUtilsComparison();
		mUtilsVectors = Utils.GetInstance().GetUtilsVectors();
		mUtilsGeneral = Utils.GetInstance().GetUtilsGeneral();
	}
	
	public void CompareGestureShapes(GestureExtended gestureStored, GestureExtended gestureAuth) {
		mGestureStored = gestureStored;
		mGestureAuth = gestureAuth;
		
		if(mGestureAuth.ListStrokesExtended.size() == mGestureStored.ListStrokesExtended.size())
		{
			PointStatus pointStatus = CheckPoints();
			
			if(pointStatus == PointStatus.BOTH) {
				mCompareResultsGesture.Score = 1;
			}
			
			if(pointStatus == PointStatus.ONE) {
				mCompareResultsGesture.Score = 0;
			}
			
			if(pointStatus == PointStatus.NONE) {
				CompareGestureStrokeShapes();
				CheckStrokesDistanceScore();
			}
		}
		else {
			mCompareResultsGesture.Score = 0;	
		}	
	}
	
	public void CompareGestures(GestureExtended gestureStored, GestureExtended gestureAuth) { 					
		mGestureStored = gestureStored;
		mGestureAuth = gestureAuth;
		
		if(mGestureAuth.ListStrokesExtended.size() == mGestureStored.ListStrokesExtended.size())
		{
			PointStatus pointStatus = CheckPoints();
			
			if(pointStatus == PointStatus.BOTH) {
				mCompareResultsGesture.Score = 1;
			}
			
			if(pointStatus == PointStatus.ONE) {
				mCompareResultsGesture.Score = 0;
			}
			
			if(pointStatus == PointStatus.NONE) {
				CompareGestureStrokes();
				CompareGestureFeatures();
				//CalculateFinalScore();
				CalculateFinalScoreTemp();
				CheckStrokesCosineAndStrokeDistance();
				CheckFinalScore();
			}
		}
		else {
			mCompareResultsGesture.Score = 0;	
		}	
	}
	
	private void CalculateFinalScoreTemp() {

		ICompareResult tempCompareResult;
		StrokeComparer tempStrokeComparer;
		
		ArrayList<NormalizedParam> listParamsAuth = new ArrayList<NormalizedParam>();
		ArrayList<NormalizedParam> listParamsHack = new ArrayList<NormalizedParam>();
		
		double avgPressureScore = 0;
		double avgSurfaceScore = 0;
		
		double tempWeight;
		double totalEvents = 0;
		for(int idx = 0; idx < mListStrokeComparers.size(); idx++) {
			tempStrokeComparer = mListStrokeComparers.get(idx);
			totalEvents += tempStrokeComparer.GetBaseStroke().ListEventsExtended.size();
		}
		
		double dtwTotalScore = 0;				
		double pcaTotalScore = 0;

		InterestPointScore = 0;
		
		double extraParamTotalWeights = 0;
		
		boolean isInterestPointsFound = false;
		
		ArrayList<ICompareResult> listResults = new ArrayList<>();
		
		NumZeroScores = 0;
		
		for(int idx = 0; idx < mListStrokeComparers.size(); idx++) {
			avgPressureScore += mListStrokeComparers.get(idx).MiddlePressureScore;
			avgSurfaceScore += mListStrokeComparers.get(idx).MiddleSurfaceScore;
			
			InterestPointScore += mListStrokeComparers.get(idx).InterestPointScoreFinal;
			if(mListStrokeComparers.get(idx).IsInterestPointFound) {
				isInterestPointsFound = true;
			}
			
			tempStrokeComparer = mListStrokeComparers.get(idx);
			tempWeight = tempStrokeComparer.GetBaseStroke().ListEventsExtended.size() / totalEvents;
			
			dtwTotalScore += tempStrokeComparer.DtwSpatialTotalScore * tempWeight;
			pcaTotalScore += tempStrokeComparer.PcaScoreFinal * tempWeight;
			extraParamTotalWeights += tempWeight;
			
			for(int idxStrokeParam = 0; idxStrokeParam < tempStrokeComparer.GetResultsSummary().ListCompareResults.size(); idxStrokeParam++) {
				tempCompareResult = tempStrokeComparer.GetResultsSummary().ListCompareResults.get(idxStrokeParam);				
				
				if(tempCompareResult.GetValue() == 0) {
					NumZeroScores++;
				}
				listParamsAuth.add(new NormalizedParam(tempCompareResult.GetName(), tempCompareResult.GetValue(), tempWeight));
				if(tempCompareResult.GetName().compareTo(ConstsParamNames.Stroke.STROKE_MIDDLE_PRESSURE) == 0) {
						
				}
			}
		}
				
		double totalWeights = 0;
		double totalScores = 0;
		double weight;
				
		double numStrokes = mListStrokeComparers.size();
		
		InterestPointScore = InterestPointScore / numStrokes;
		
		avgPressureScore = avgPressureScore / numStrokes;
		avgSurfaceScore = avgSurfaceScore / numStrokes;
		
		for(int idx = 0; idx < mCompareResultsGesture.ListCompareResults.size(); idx++) {
			listParamsAuth.add(new NormalizedParam(mCompareResultsGesture.ListCompareResults.get(idx).GetName(), mCompareResultsGesture.ListCompareResults.get(idx).GetValue(), 1));
			
			if(mCompareResultsGesture.ListCompareResults.get(idx).GetValue() == 0) {
				NumZeroScores++;
			}
		}
				
		Collections.sort(listParamsAuth, new Comparator<NormalizedParam>() {
            @Override
            public int compare(NormalizedParam value1, NormalizedParam value2) {
                if (value1.NormalizedScore > value2.NormalizedScore) {
                    return -1;
                }
                if (value1.NormalizedScore < value2.NormalizedScore) {
                    return 1;
                }
                return 0;
            }
        });
				
		double baseWeight = 1;
		UtilsAccumulator utilsAccumulator = new UtilsAccumulator(); 
		int idxStart = mListStrokeComparers.size();
		
		for(int idx = 0; idx < listParamsAuth.size(); idx++) {
			if(idx >= idxStart) {
				//weight = idx + baseWeight;
				weight = listParamsAuth.get(idx).Weight;
				totalScores += listParamsAuth.get(idx).NormalizedScore * weight;
				totalWeights += weight;
				
				utilsAccumulator.AddDataValue(listParamsAuth.get(idx).NormalizedScore);
			}			
		}
		
		ScoreMean = utilsAccumulator.Mean();
		ScoreStd = utilsAccumulator.Stddev();
		
		totalScores = totalScores / totalWeights;	
		
		DtwScore = dtwTotalScore / extraParamTotalWeights;
		PcaScore = pcaTotalScore / extraParamTotalWeights;
		
		NormalizedParam pcaScoreNormalizedParam = new NormalizedParam("PcaScore", PcaScore, 1);
		NormalizedParam dtwScoreNormalizedParam = new NormalizedParam("DtwScore", DtwScore, 1);				
		NormalizedParam interestPointScoreNormalizedParam = new NormalizedParam("InterestPoints", InterestPointScore, 1);
		
		mCompareResultsGesture.Score = 				
				dtwScoreNormalizedParam.NormalizedScore * 0.15 +
				pcaScoreNormalizedParam.NormalizedScore * 0.15 +
				interestPointScoreNormalizedParam.NormalizedScore * 0.1 +
				totalScores * 0.6;
		
		double removeMiddlePressureScore = (1 - avgSurfaceScore * avgSurfaceScore) / 5;
		mCompareResultsGesture.Score -= removeMiddlePressureScore;
		
		if(InterestPointScore == 0) {
			mCompareResultsGesture.Score = 0;
		}
	}

	protected void CheckFinalScore() {
		if(mCompareResultsGesture.Score < 0) {
			mCompareResultsGesture.Score = 0;
		}
		if(mCompareResultsGesture.Score > 1) {
			mCompareResultsGesture.Score = 1;
		}
	}
	
	protected double GetSpatialScore(double scoreRaw, double lower, double upper) {
		double spatialScore = 1;
		
		if(scoreRaw < upper) {
			if(scoreRaw > lower) {
				double interval = upper - lower;
				spatialScore = (upper - scoreRaw) / interval;
			}
			else {
				spatialScore = 0;
			}
		}
		
		return spatialScore;
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
	
	public PointStatus CheckPoints()
	{
		PointStatus pointStatus = PointStatus.BOTH;
		
		if(!mGestureStored.IsOnlyPoints && !mGestureAuth.IsOnlyPoints) {
			pointStatus = PointStatus.NONE;
		}
		
		if(!mGestureStored.IsOnlyPoints && mGestureAuth.IsOnlyPoints) {
			pointStatus = PointStatus.ONE;
		}
		
		if(mGestureStored.IsOnlyPoints && !mGestureAuth.IsOnlyPoints) {
			pointStatus = PointStatus.ONE;
		}
		
		return pointStatus;
	}
	
	public void CompareGestures(GestureExtended gestureStored, GestureExtended gestureAuth, HashMap<String, Double> compareFilters) { 					
		mCompareFilters = compareFilters;
		CompareGestures(gestureStored, gestureAuth);
	}
	
	protected boolean IsNeedToRun(String methodName)
	{
		boolean isNeedToRun = true;
		if(mCompareFilters != null && mCompareFilters.containsKey(methodName)) {
			isNeedToRun = false;
		}
		
		return isNeedToRun;
	}
	
	protected void CompareGestureFeatures()
	{					
		mMinCosineDistanceValid = true;
		if(IsNeedToRun("CompareGestureMinCosineDistance")){
			CompareGestureMinCosineDistance();
		}
		
		if(IsNeedToRun("CompareGestureTotalTimeInterval")){
			CompareGestureTotalTimeInterval();
		}	
		if(IsNeedToRun("CompareGestureAreas")){
			CompareGestureAreas();
		}
		if(IsNeedToRun("CompareGestureAreasMinXMinY")){
			CompareGestureAreasMinXMinY();
		}
	}
	
	protected void CompareGestureMaxAcceleration() {
		double maxAcceleration = mGestureAuth.GestureMaxAcceleration;
		CalcDoubleParameter(ConstsParamNames.Gesture.GESTURE_MAX_ACCELERATION, maxAcceleration);
	}

	protected void CompareGestureAvgAcceleration() {
		double avgAcceleration = mGestureAuth.GestureAverageAcceleration;
		CalcDoubleParameter(ConstsParamNames.Gesture.GESTURE_AVG_ACCELERATION, avgAcceleration);
	}

	protected void CompareGestureMaxVelocity() {
		double maxVelocity = mGestureAuth.GestureMaxVelocity;
		CalcDoubleParameter(ConstsParamNames.Gesture.GESTURE_MAX_VELOCITY, maxVelocity);
	}

	protected void CompareGestureAccumulatedLengthRSqr()
	{
		double accumulatedLengthRSqr = mGestureAuth.GestureAccumulatedLengthLinearRegRSqr;
		CalcDoubleParameter(ConstsParamNames.Gesture.GESTURE_ACCUMULATED_LENGTH_R2, accumulatedLengthRSqr);
	}
	
	protected void CompareGestureAccumulatedLengthSlope()
	{
		double accumulatedLengthSlope = mGestureAuth.GestureAccumulatedLengthLinearRegSlope;
		CalcDoubleParameter(ConstsParamNames.Gesture.GESTURE_ACCUMULATED_LENGTH_SLOPE, accumulatedLengthSlope);
	}	
	
	protected void CompareGestureVelocityPeaksIntervalPercentage()
	{
		double velocityPeakIntervalPercentage = mGestureAuth.GestureVelocityPeakIntervalPercentage;
		CalcDoubleParameter(ConstsParamNames.Gesture.GESTURE_VELOCITY_PEAK_INTERVAL_PERCENTAGE, velocityPeakIntervalPercentage);
	}
	
	protected void CompareGestureVelocityPeaks()
	{
		double velocityPeak = mGestureAuth.GestureVelocityPeakMax;
		CalcDoubleParameter(ConstsParamNames.Gesture.GESTURE_VELOCITY_PEAK, velocityPeak);
	}
	
	protected void CompareGestureStartDirection() {
		double startDirectionAuth = mGestureAuth.GestureStartDirection;
		CalcScoreWithoutDistribution(ConstsParamNames.Gesture.GESTURE_AVG_START_DIRECTION, startDirectionAuth);		
	}

	protected void CompareGestureMaxDirection() {
		double maxDirectionAuth = mGestureAuth.GestureMaxDirection;
		CalcScoreWithoutDistribution(ConstsParamNames.Gesture.GESTURE_AVG_MAX_DIRECTION, maxDirectionAuth);		
	}

	protected void CompareGestureEndDirection(){
		double endDirectionAuth = mGestureAuth.GestureEndDirection;
		CalcDoubleParameter(ConstsParamNames.Gesture.GESTURE_AVG_END_DIRECTION, endDirectionAuth);		
	}
	
	protected void CompareMidOfFirstStrokeVelocity()
	{
		double midOfFirstStrokeVelocity = mGestureAuth.MidOfFirstStrokeVelocity;
		CalcDoubleParameter(ConstsParamNames.Gesture.GESTURE_MID_OF_FIRST_STROKE_VELOCITY, midOfFirstStrokeVelocity);		
	}

	protected void CompareMidOfFirstStrokeAngle()
	{
		double midOfFirstStrokeAngle = mGestureAuth.MidOfFirstStrokeAngle;
		CalcScoreWithoutDistribution(ConstsParamNames.Gesture.GESTURE_MID_OF_FIRST_STROKE_ANGLE, midOfFirstStrokeAngle);		
	}

	protected void CompareGestureAverageStartAcceleration()
	{
		double avgStartAcceleration = mGestureAuth.GestureAverageStartAcceleration;		
		CalcDoubleParameter(ConstsParamNames.Gesture.GESTURE_AVG_START_ACCELERATION, avgStartAcceleration);
	}
	
	protected void CompareGestureMinCosineDistance()
	{
		mMinCosineDistanceValid = false;
		
		double[] vectorStored = mGestureStored.SpatialSamplingVector;
		double[] vectorAuth = mGestureAuth.SpatialSamplingVector;
		
		mMinCosineDistanceScore = 
				mUtilsVectors.MinimumCosineDistanceScore(vectorStored, vectorAuth);
		
		if(mMinCosineDistanceScore > 1) {
			mMinCosineDistanceValid = true;
		}
	}
	
	protected void CompareGesturePressure()
	{										
		double middlePressure = mGestureAuth.GestureAvgMiddlePressure;
		CalcDoubleParameter(ConstsParamNames.Gesture.GESTURE_MIDDLE_PRESSURE, middlePressure);
	}
	
	protected void CompareGestureSurface()
	{
		double middleSurface = mGestureAuth.GestureAvgMiddleSurface;
		CalcDoubleParameter(ConstsParamNames.Gesture.GESTURE_MIDDLE_SURFACE, middleSurface);
	}
	
	protected void CompareGestureAreas()
	{		
		double areaAuth = mGestureAuth.GestureTotalArea;						
		CalcDoubleParameter(ConstsParamNames.Gesture.GESTURE_TOTAL_AREA, areaAuth);
	}

	protected void CompareGestureAreasMinXMinY()
	{		
		double areaAuth = mGestureAuth.GestureTotalAreaMinXMinY;						
		CalcDoubleParameter(ConstsParamNames.Gesture.GESTURE_TOTAL_AREA_MINX_MINY, areaAuth);
	}	
	
	protected void CompareGestureStrokeAreas()
	{		
		double areaAuth = mGestureAuth.GestureTotalStrokeArea;						
		CalcDoubleParameter(ConstsParamNames.Gesture.GESTURE_TOTAL_STROKE_AREA, areaAuth);
	}	
	
	protected void CompareGestureStrokeAreasMinXMinY()
	{		
		double areaAuth = mGestureAuth.GestureTotalStrokeAreaMinXMinY;						
		CalcDoubleParameter(ConstsParamNames.Gesture.GESTURE_TOTAL_STROKE_AREA_MINX_MINY, areaAuth);
	}
	
	protected void CompareGestureTotalStrokesTime() {		
		double totalTimeNoPausesAuth = mGestureAuth.GestureTotalStrokeTimeInterval;			
		CalcDoubleParameter(ConstsParamNames.Gesture.GESTURE_TOTAL_STROKES_TIME_INTERVAL, totalTimeNoPausesAuth);
	}

	protected void CompareGestureTotalTimeInterval() {		
		double totalTimeAuth = mGestureAuth.GestureTotalTimeInterval;
		CalcDoubleParameter(ConstsParamNames.Gesture.GESTURE_TOTAL_TIME_INTERVAL, totalTimeAuth);
	}

	protected void CompareGestureAvgVelocity() {		
		double avgVelocityAuth = mGestureAuth.GestureAverageVelocity;
		CalcDoubleParameter(ConstsParamNames.Gesture.GESTURE_AVERAGE_VELOCITY, avgVelocityAuth);	
	}

	protected void CompareGestureLengths() {	
		double lengthAuth = mGestureAuth.GestureLengthMM;
		CalcDoubleParameter(ConstsParamNames.Gesture.GESTURE_LENGTH, lengthAuth);		
	}
	
	protected void CompareNumEvents()
	{
		int numEvents = mGestureAuth.ListGestureEventsExtended.size();		
		CalcDoubleParameter(ConstsParamNames.Gesture.GESTURE_NUM_EVENTS, (double)numEvents);		
	}
//if finalScore=-1 it is not added
	protected void CalcDoubleParameter(String paramName, double value)
	{
		IStatEngineResult finalScore = mStatEngine.CompareGestureDoubleValues(mGestureStored.GetGestureKey(), paramName, value, mGestureStored.GetFeatureMeansHash());
		AddDoubleParameter(paramName, finalScore, ConstsParamWeights.MEDIUM, value);
	}

	protected void CalcScoreWithoutDistribution(String paramName, double value)
	{
		
		IStatEngineResult finalScore = mStatEngine.CompareGestureScoreWithoutDistribution(mGestureStored.Instruction, paramName, value, mGestureStored.GetFeatureMeansHash());
		AddDoubleParameter(paramName, finalScore, ConstsParamWeights.MEDIUM, value);
	}
	
	protected void CompareGestureStrokeShapes()
	{
		mCompareResultsGesture = new CompareResultSummary();			
		
		StrokeExtended tempStrokeStored;
		StrokeExtended tempStrokeAuth;
		
		StrokeComparer strokeComparer;
		for(int idxStroke = 0; idxStroke < mGestureStored.ListStrokesExtended.size(); idxStroke++) {
			strokeComparer = new StrokeComparer(mIsSimilarDevices);
			
			tempStrokeStored = mGestureStored.ListStrokesExtended.get(idxStroke);
			tempStrokeAuth = mGestureAuth.ListStrokesExtended.get(idxStroke);
			
			strokeComparer.CompareStrokeShapes(tempStrokeStored, tempStrokeAuth);
			mListStrokeComparers.add(strokeComparer);			
		}
	}
	
	protected void CompareGestureStrokes()
	{
		mCompareResultsGesture = new CompareResultSummary();			
		
		StrokeExtended tempStrokeStored;
		StrokeExtended tempStrokeAuth;
		
		StrokeComparer strokeComparer;
		for(int idxStroke = 0; idxStroke < mGestureStored.ListStrokesExtended.size(); idxStroke++) {
			strokeComparer = new StrokeComparer(mIsSimilarDevices);
			
			tempStrokeStored = mGestureStored.ListStrokesExtended.get(idxStroke);
			tempStrokeAuth = mGestureAuth.ListStrokesExtended.get(idxStroke);
			
			strokeComparer.CompareStrokes(tempStrokeStored, tempStrokeAuth);
			mListStrokeComparers.add(strokeComparer);
		}
	}
	
	public double GetScore()
	{
		return mCompareResultsGesture.Score;
	}	
	
	public double GetMinCosineDistance()
	{
		return mMinCosineDistanceScore;
	}
	
	protected void CalculateFinalScore()
	{
		mIsGesturesIdentical = true;
		for(int idx = 0; idx < mListStrokeComparers.size(); idx++) {			
			if(!mListStrokeComparers.get(idx).IsStrokesIdentical()) {
				mIsGesturesIdentical = false;
			}
		}
				
		ArrayList<ICompareResult> listScores = new ArrayList<>();
		DtwScore = 0;		
		PcaScore = 0;
		InterestPointScore = 0;
		InterestPointDensity = 0;
		InterestPointLocation = 0;
		
		double scorePenalty = 0;
		
		if(!mIsGesturesIdentical) {
			double avgPressureScore = 0;
			double avgSurfaceScore = 0;
			
			double tempDtwScore;
			double tempPcaScore;
			double tempInterestPointScore;
						
			NormalizedParam tempBooleanParam;
			ArrayList<NormalizedParam> listBooleanParams = new ArrayList<>();
			
			IsStrokeTypesValid = true;
			
			double strokeTypePenalty = 0;
			
			for(int idx = 0; idx < mListStrokeComparers.size(); idx++) {			
				listScores.addAll(mListStrokeComparers.get(idx).GetResultsSummary().ListCompareResults);
				listScores.addAll(mListStrokeComparers.get(idx).GetResultsSummary().ListCompareResultsExtra);														
			
				if(mListStrokeComparers.get(idx).IsStrokeTypesValid) {					
					strokeTypePenalty += 1;
				}
				else {
					IsStrokeTypesValid = false;
				}
				
				DtwScore += mListStrokeComparers.get(idx).DtwSpatialTotalScore;
				PcaScore += Math.abs(mListStrokeComparers.get(idx).PcaScore);
								
				if(mListStrokeComparers.get(idx).MaxInterestPointDTWVelocity > 0.17 || mListStrokeComparers.get(idx).MaxInterestPointDTWCoords > 0.4) {
					scorePenalty += 2;
				}
				
				InterestPointDensity += mListStrokeComparers.get(idx).MaxInterestPointDensity;
				InterestPointLocation += mListStrokeComparers.get(idx).MaxInterestPointLocation;
				
				InterestPointScore += mListStrokeComparers.get(idx).InterestPointScore;
				
				avgPressureScore += mListStrokeComparers.get(idx).MiddlePressureScore;
				avgSurfaceScore += mListStrokeComparers.get(idx).MiddleSurfaceScore;					
				
				tempBooleanParam = new NormalizedParam("DtwScore", mListStrokeComparers.get(idx).DtwSpatialTotalScore, 1);
				listBooleanParams.add(tempBooleanParam);
				
//				tempBooleanParam = new BooleanParam("PcaScore", mListStrokeComparers.get(idx).PcaScoreFinal, 1);
//				listBooleanParams.add(tempBooleanParam);
//				
//				tempBooleanParam = new BooleanParam("InterestPointDensity", mListStrokeComparers.get(idx).MaxInterestPointDensity, 1);
//				listBooleanParams.add(tempBooleanParam);
//				
//				tempBooleanParam = new BooleanParam("InterestPointLocation", mListStrokeComparers.get(idx).MaxInterestPointLocation, 1);
//				listBooleanParams.add(tempBooleanParam);
//				
//				tempBooleanParam = new BooleanParam("InterestPointVelocity", mListStrokeComparers.get(idx).MaxInterestPointVelocity, 1);
//				listBooleanParams.add(tempBooleanParam);
//				
//				tempBooleanParam = new BooleanParam("InterestPointAcceleration", mListStrokeComparers.get(idx).MaxInterestPointAcceleration, 1);
//				listBooleanParams.add(tempBooleanParam);
//				
//				tempBooleanParam = new BooleanParam("InterestPointIndex", mListStrokeComparers.get(idx).MaxInterestPointIndex, 1);
//				listBooleanParams.add(tempBooleanParam);
//				
//				tempBooleanParam = new BooleanParam("InterestPointAvgVelocity", mListStrokeComparers.get(idx).MaxInterestPointAvgVelocity, 1);
//				listBooleanParams.add(tempBooleanParam);
//				
//				tempBooleanParam = new BooleanParam("InterestPointDeltaTeta", mListStrokeComparers.get(idx).MaxInterestPointDeltaTeta, 1);
//				listBooleanParams.add(tempBooleanParam);
//				
//				tempBooleanParam = new BooleanParam("InterestPointMaxVelocity", mListStrokeComparers.get(idx).MaxInterestPointMaxVelocity, 1);
//				listBooleanParams.add(tempBooleanParam);
//				
//				tempBooleanParam = new BooleanParam("InterestPointMaxAcceleration", mListStrokeComparers.get(idx).MaxInterestPointMaxAcceleration, 1);
//				listBooleanParams.add(tempBooleanParam);
				
//				if(!mListStrokeComparers.get(idx).IsInterestPointFound) {
//					interestPointPenalty += 0.2;
//				}
			}

			strokeTypePenalty = strokeTypePenalty / mListStrokeComparers.size();
			strokeTypePenalty = (1 - strokeTypePenalty) * 0.05;
			
			listScores.addAll(mCompareResultsGesture.ListCompareResults);			
			
			for(int idx = listScores.size() - 1; idx >= 0; idx--) {
				if(listScores.get(idx).GetName().compareTo(ConstsParamNames.Stroke.STROKE_TOTAL_AREA) == 0 || listScores.get(idx).GetName().compareTo(ConstsParamNames.Stroke.STROKE_TOTAL_AREA_MINX_MINY) == 0) { 
					double prop = listScores.get(idx).GetSD() / listScores.get(idx).GetPopMean();
					if(prop > 0.4) {
						listScores.remove(idx);
					}
				}
			}
			
			Collections.sort(listScores, new Comparator<ICompareResult>() {
	            @Override
	            public int compare(ICompareResult value1, ICompareResult value2) {
	                if (Math.abs(value1.GetValue()) > Math.abs(value2.GetValue())) {
	                    return 1;
	                }
	                if (Math.abs(value1.GetValue()) < Math.abs(value2.GetValue())) {
	                    return -1;
	                }
	                return 0;
	            }
	        });			
			
			double numStrokes = mListStrokeComparers.size();
			
			double totalScores = 0;
			double totalWeights = 0;

			double tempScore = 0;
										
			double totalParams = 0;
			double numValidParams = 0;
			BooleanParamsScore = 0;		
			
			for(int idx = (3 * mListStrokeComparers.size()); idx < listScores.size(); idx++) {
//				if(listScores.get(idx).GetName().compareTo(ConstsParamNames.Stroke.STROKE_MAX_RADIAL_ACCELERATION) != 0) {
//					totalScores += listScores.get(idx).GetValue() * listScores.get(idx).GetWeight();
//					totalWeights += listScores.get(idx).GetWeight();					
//				}
				
				totalScores += listScores.get(idx).GetValue() * listScores.get(idx).GetWeight();
				totalWeights += listScores.get(idx).GetWeight();
				
				if(listScores.get(idx).GetName().compareTo(ConstsParamNames.Gesture.GESTURE_TOTAL_TIME_INTERVAL) == 0) {
					tempScore = listScores.get(idx).GetValue();
				}
				
				tempBooleanParam = new NormalizedParam(listScores.get(idx).GetName(), listScores.get(idx).GetValue(), listScores.get(idx).GetWeight());
				listBooleanParams.add(tempBooleanParam);
			}		
			
//			for(int idx = 0; idx < mListStrokeComparers.size(); idx++) {
//				totalScores += mListStrokeComparers.get(idx).InterestPointScore * 3;
//				totalWeights += 3;				
//			}			
						
			avgPressureScore = avgPressureScore / numStrokes;
			avgSurfaceScore = avgSurfaceScore / numStrokes;
			
			DtwScore = DtwScore / numStrokes;
			PcaScore = PcaScore / numStrokes;
			InterestPointDensity = InterestPointDensity / numStrokes;
			InterestPointLocation = InterestPointLocation / numStrokes;
			InterestPointScore = InterestPointScore / numStrokes;					
			
			NormalizedParamsScore = 0;
			totalWeights = 0;
			
			Collections.sort(listBooleanParams, new Comparator<NormalizedParam>() {
	            @Override
	            public int compare(NormalizedParam value1, NormalizedParam value2) {
	                if (value1.NormalizedScore > value1.NormalizedScore) {
	                    return 1;
	                }
	                if (value1.NormalizedScore < value1.NormalizedScore) {
	                    return -1;
	                }
	                return 0;
	            }
	        });

			for(int idx = 0; idx < listBooleanParams.size(); idx++) {
				NormalizedParamsScore += listBooleanParams.get(idx).NormalizedScore * listBooleanParams.get(idx).Weight;
				totalWeights += listBooleanParams.get(idx).Weight;
				
				if(listBooleanParams.get(idx).IsValid) {
					numValidParams++;
				}
				totalParams++;
			}
			
			NormalizedParamsScore = NormalizedParamsScore / totalWeights;
			BooleanParamsScore = numValidParams / totalParams;
			
			double paramsTotalScore = totalScores / totalWeights;
			mCompareResultsGesture.Score = paramsTotalScore;
			
//			mCompareResultsGesture.Score = Utils.GetInstance().GetUtilsMath().GetMinValue(BooleanParamsScore, NormalizedParamsScore);
			mCompareResultsGesture.Score = NormalizedParamsScore;
			mCompareResultsGesture.Score -= scorePenalty;
			
			double removeMiddlePressureScore = (1 - avgSurfaceScore * avgSurfaceScore) / 5;
			mCompareResultsGesture.Score -= removeMiddlePressureScore;
			
//			mCompareResultsGesture.Score -= strokeTypePenalty;
			
//			UpdateScore2(PcaScore, 0.55, 2, 0.2, true);
//			UpdateScore2(DtwScore, 0.3, 0.55, 0.2, false);
			CheckStrokesCosineAndStrokeDistance();
		}		
		else 
		{
			mCompareResultsGesture.Score = 1;
		}
	}	
	
	private void UpdateScore(double score, double weight) {
		mCompareResultsGesture.Score = mCompareResultsGesture.Score * (1 - weight) + score * weight;
	}
	
	private void UpdateScore2(double value, double lower, double upper, double weight, boolean isReverse) {
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
		
		double tempScore = mCompareResultsGesture.Score; 
		mCompareResultsGesture.Score = mCompareResultsGesture.Score * (1 - weight) + finalScore * weight;
		if(mCompareResultsGesture.Score > tempScore) {
			mCompareResultsGesture.Score = tempScore;
		}
	}

	protected void CheckStrokesDistanceScore() {
		mIsStrokeCosineDistanceValid = true;
		for(int idxStrokeComparer = 0; idxStrokeComparer < mListStrokeComparers.size(); idxStrokeComparer++) {
			if(mListStrokeComparers.get(idxStrokeComparer).GetMinCosineDistance() < ConstsFeatures.MIN_COSINE_DISTANCE_SCORE) {
				mCompareResultsGesture.Score = 0;
				mIsStrokeCosineDistanceValid = false;
			}
		}
	}
	
	protected void CheckStrokeDistanceAndUpdateScore(double strokeDistanceScore) {
		if(strokeDistanceScore < 0.5) {
			mCompareResultsGesture.Score -= 0.5;
		}
		else {
			if(strokeDistanceScore > 0.5 && strokeDistanceScore < 0.7) {
				double diff = strokeDistanceScore - 0.5;
				mCompareResultsGesture.Score -= (diff * 2);
			}
		}
	}
	
	protected void CheckStrokesCosineAndStrokeDistance() {		
		mIsStrokeCosineDistanceValid = true;
		for(int idxStrokeComparer = 0; idxStrokeComparer < mListStrokeComparers.size(); idxStrokeComparer++) {
			if(mListStrokeComparers.get(idxStrokeComparer).GetMinCosineDistance() < ConstsFeatures.MIN_COSINE_DISTANCE_SCORE) {
				mCompareResultsGesture.Score = 0;
				mIsStrokeCosineDistanceValid = false;					
			}
//			CheckStrokeDistanceAndUpdateScore(mListStrokeComparers.get(idxStrokeComparer).StrokeDistanceTotalScore);
		}
	}
	
	protected void CalculateStrokesScore() {
		double avgScore = 0;
						
		mIsGesturesIdentical = true;
		for(int idx = 0; idx < mListStrokeComparers.size(); idx++) {
			if(mListStrokeComparers.get(idx).mIsSimilarDevices)
			avgScore += mListStrokeComparers.get(idx).GetScore();
			if(!mListStrokeComparers.get(idx).IsStrokesIdentical()) {
				mIsGesturesIdentical = false;
			}
		}
		
		mStrokesScore = avgScore / mListStrokeComparers.size();
	}

	protected void AddDoubleParameter(String parameterName, IStatEngineResult finalScore, double weight, double originalValue)
	{
		double score = finalScore.GetScore();
		weight = finalScore.GetZScore();
		
		String key = mUtilsGeneral.GenerateGestureFeatureMeanKey(mGestureStored.GetGestureKey(), parameterName);
		
		double mean = 0;
		double internalSd = 0;
		double popSd = 0;
		double popMean = 0;
		double internalSdUserOnly = 0;
		
		HashMap<String, IFeatureMeanData> hashFeatureMeans = mGestureStored.GetFeatureMeansHash(); 
		
		if(hashFeatureMeans.containsKey(key)) {
			mean = hashFeatureMeans.get(key).GetMean();
			
			INormData normData = NormMgr.GetInstance().GetNormDataByParamName(parameterName, mGestureStored.GetGestureKey());			
			internalSd = normData.GetInternalStandardDev();
			popSd = normData.GetStandardDev();
			popMean = normData.GetMean();
			internalSdUserOnly = hashFeatureMeans.get(key).GetInternalSd();
		}
		
		ICompareResult compareResult = 
				(ICompareResult) new CompareResultGeneric(parameterName, score, originalValue, mean, popMean, popSd, internalSd, internalSdUserOnly);
		mCompareResultsGesture.ListCompareResults.add(compareResult);
	}
	
	public CompareResultSummary GetResultsSummary()
	{
		return mCompareResultsGesture;
	}

	public boolean IsStrokeCosineDistanceValid() {
		return mIsStrokeCosineDistanceValid;
	}
	
	public ArrayList<StrokeComparer> GetStrokeComparers() {
		return mListStrokeComparers;
	}
}