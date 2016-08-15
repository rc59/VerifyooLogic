package Logic.Comparison;

import java.util.ArrayList;
import java.util.Collections;
import java.util.Comparator;
import java.util.HashMap;

import Consts.ConstsGeneral;
import Consts.ConstsParamNames;
import Consts.ConstsParamWeights;
import Consts.Enums.PointStatus;
import Data.Comparison.CompareResultGeneric;
import Data.Comparison.CompareResultSummary;
import Data.Comparison.Interfaces.ICompareResult;
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
import Logic.Utils.UtilsComparison;
import Logic.Utils.UtilsGeneral;
import Logic.Utils.UtilsVectors;

public class GestureComparer {	
	
	protected boolean mIsGesturesIdentical;
	protected IStatEngine mStatEngine;
	
	protected GestureExtended mGestureStored;
	protected GestureExtended mGestureAuth;
	
	protected UtilsGeneral mUtilsGeneral;
	protected UtilsComparison mUtilsComparison;
	protected UtilsVectors mUtilsVectors;
	
	protected ArrayList<StrokeComparer> mListStrokeComparers;	
	protected double mGestureScore;
	protected double mMinCosineDistanceScore;
	protected double mStrokesScore;
	CompareResultSummary mCompareResultsGesture;
	
	protected boolean mIsStrokeCosineDistanceValid;
	protected boolean mMinCosineDistanceValid;	
	
	protected boolean mIsSimilarDevices;
	
	protected HashMap<String, Double> mCompareFilters;
	
	public double DtwDistanceCoordinates;
	public double DtwDistanceNormalizedCoordinates;
	public double DtwDistanceEvents;
	public double DtwDistanceVelocities;
	public double DtwDistanceAccelerations;
	
	public double SpatialScoreDistanceVelocities;
	public double SpatialScoreDistanceAccelerations;
	public double SpatialScoreDistanceRadialVelocities;
	
	public double SpatialScoreDistanceRadialAccelerations;
	public double SpatialScoreDistanceRadius;
	public double SpatialScoreDistanceTeta;
	public double SpatialScoreDistanceDeltaTeta;
	public double SpatialScoreDistanceAccumulatedNormArea;
	
	public double SpatialScoreTimeVelocities;
	public double SpatialScoreTimeAccelerations;
	public double SpatialScoreTimeRadialVelocities;
	
	public double SpatialScoreTimeRadialAccelerations;
	public double SpatialScoreTimeRadius;
	public double SpatialScoreTimeTeta;
	public double SpatialScoreTimeDeltaTeta;
	public double SpatialScoreTimeAccumulatedNormArea;
	
	public double GestureSpatialScore;
	
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
				CalculateFinalScore();
			}		
		}
		else {
			mCompareResultsGesture.Score = 0;	
		}	
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
		
		if(IsNeedToRun("CompareGestureLengths")){
			CompareGestureLengths();	
		}
		if(IsNeedToRun("CompareNumEvents")){
			CompareNumEvents();	
		}
		if(IsNeedToRun("CompareGestureAvgVelocity")){
			CompareGestureAvgVelocity();
		}		
		if(IsNeedToRun("CompareGestureTotalTimeInterval")){
			CompareGestureTotalTimeInterval();
		}
		if(IsNeedToRun("CompareGestureTotalStrokesTime")){
			CompareGestureTotalStrokesTime();
		}
		if(IsNeedToRun("CompareGestureStrokeAreas")){
			CompareGestureStrokeAreas();
		}
		if(IsNeedToRun("CompareGestureAreasStrokeMinXMinY")){
			CompareGestureStrokeAreasMinXMinY();
		}		
		if(IsNeedToRun("CompareGestureAreas")){
			CompareGestureAreas();
		}
		if(IsNeedToRun("CompareGestureAreasMinXMinY")){
			CompareGestureAreasMinXMinY();
		}
		if(IsNeedToRun("CompareGesturePressure")){
			CompareGesturePressure();
		}
		if(IsNeedToRun("CompareGestureSurface")){
			CompareGestureSurface();
		}		
		if(IsNeedToRun("CompareGestureAverageStartAcceleration")){
			CompareGestureAverageStartAcceleration();
		}
//		if(IsNeedToRun("CompareGestureVelocityPeaks")){
//			CompareGestureVelocityPeaks();
//		}
//		if(IsNeedToRun("CompareGestureVelocityPeaksIntervalPercentage")){
//			CompareGestureVelocityPeaksIntervalPercentage();
//		}
//		if(IsNeedToRun("CompareGestureStartDirection")){
//			CompareGestureStartDirection();
//		}	
//		if(IsNeedToRun("CompareGestureMaxDirection")){
//			CompareGestureMaxDirection();
//		}	
//		if(IsNeedToRun("CompareGestureEndDirection")){
//			CompareGestureEndDirection();
//		}	
		if(IsNeedToRun("CompareMidOfFirstStrokeVelocity")){
			CompareMidOfFirstStrokeVelocity();
		}	

		if(IsNeedToRun("CompareMidOfFirstStrokeAngle")){
			//CompareMidOfFirstStrokeAngle();
		}	

		//TODO: check parameter
//		if(IsNeedToRun("CompareGestureAccumulatedLengthRSqr")){
//			CompareGestureAccumulatedLengthRSqr();
//		}
		if(IsNeedToRun("CompareGestureAccumulatedLengthSlope")){
			CompareGestureAccumulatedLengthSlope();
		}
		
		if(IsNeedToRun("CompareGestureMaxVelocity")){
			CompareGestureMaxVelocity();
		}
		if(IsNeedToRun("CompareGestureAvgAcceleration")){
			//CompareGestureAvgAcceleration();
		}
		if(IsNeedToRun("CompareGestureMaxAcceleration")){
			CompareGestureMaxAcceleration();
		}		
		if(IsNeedToRun("TimeWarp")){
			RunTimeWarp();
		}		
		if(IsNeedToRun("SpatialSampling")){
			RunSpatialSampling();
		}
	}
	
	private double GetPropScore(double avg, double sd, double value) {
		double score = 0;
				
		double boundary = avg - sd;
		
		if(value < boundary) {
			score = 0;
		}
		else {
			double diff = value - boundary;
			score = diff / (avg - boundary);
		}
		
		return score;
	}
	
	private void RunSpatialSampling() {
		GestureSpatialScore = 0;
		for(int idx = 0; idx < mListStrokeComparers.size(); idx++) {
			GestureSpatialScore += mListStrokeComparers.get(idx).StrokeSpatialScore;
		}
		
		GestureSpatialScore = GestureSpatialScore / mListStrokeComparers.size();
		double result = GestureSpatialScore;
	}
	
	private void RunSpatialSampling2()
	{
		int numStrokes = mListStrokeComparers.size();
		
		SpatialScoreDistanceVelocities = 0;
		SpatialScoreDistanceAccelerations = 0;
		SpatialScoreDistanceRadialVelocities = 0;

		SpatialScoreDistanceRadialAccelerations = 0;
		SpatialScoreDistanceRadius = 0;
		SpatialScoreDistanceTeta = 0;
		SpatialScoreDistanceDeltaTeta = 0;
		SpatialScoreDistanceAccumulatedNormArea = 0;
		
		SpatialScoreTimeVelocities = 0;
		SpatialScoreTimeAccelerations = 0;
		SpatialScoreTimeRadialVelocities = 0;
		
		SpatialScoreTimeRadialAccelerations = 0;
		SpatialScoreTimeRadius = 0;
		SpatialScoreTimeTeta = 0;
		SpatialScoreTimeDeltaTeta = 0;
		SpatialScoreTimeAccumulatedNormArea = 0;
		
		for(int idx = 0; idx < mListStrokeComparers.size(); idx++) {
			SpatialScoreDistanceVelocities += mListStrokeComparers.get(idx).SpatialScoreDistanceVelocity;	
			SpatialScoreDistanceAccelerations += mListStrokeComparers.get(idx).SpatialScoreDistanceAcceleration;
			SpatialScoreDistanceRadialVelocities += mListStrokeComparers.get(idx).SpatialScoreDistanceRadialVelocity;
			SpatialScoreDistanceRadialAccelerations += mListStrokeComparers.get(idx).SpatialScoreDistanceRadialAcceleration;
			SpatialScoreDistanceRadius += mListStrokeComparers.get(idx).SpatialScoreDistanceRadius;
			SpatialScoreDistanceTeta += mListStrokeComparers.get(idx).SpatialScoreDistanceTeta;
			SpatialScoreDistanceDeltaTeta += mListStrokeComparers.get(idx).SpatialScoreDistanceDeltaTeta;
			SpatialScoreDistanceAccumulatedNormArea += mListStrokeComparers.get(idx).SpatialScoreDistanceAccumulatedNormArea;
			
			
//			SpatialScoreTimeVelocities += mListStrokeComparers.get(idx).SpatialScoreTimeVelocity;	
//			SpatialScoreTimeAccelerations += mListStrokeComparers.get(idx).SpatialScoreTimeAcceleration;
//			SpatialScoreTimeRadialVelocities += mListStrokeComparers.get(idx).SpatialScoreTimeRadialVelocity;
//			
//			SpatialScoreTimeRadialAccelerations += mListStrokeComparers.get(idx).SpatialScoreTimeRadialAcceleration;
//			SpatialScoreTimeRadius += mListStrokeComparers.get(idx).SpatialScoreTimeRadius;
//			SpatialScoreTimeTeta += mListStrokeComparers.get(idx).SpatialScoreTimeTeta;
//			SpatialScoreTimeDeltaTeta += mListStrokeComparers.get(idx).SpatialScoreTimeDeltaTeta;
//			SpatialScoreTimeAccumulatedNormArea += mListStrokeComparers.get(idx).SpatialScoreTimeAccumulatedNormArea;			
		}
		
		SpatialScoreDistanceVelocities = SpatialScoreDistanceVelocities / numStrokes;
		SpatialScoreDistanceAccelerations = SpatialScoreDistanceAccelerations / numStrokes;
		SpatialScoreDistanceRadialVelocities = SpatialScoreDistanceRadialVelocities / numStrokes;
		
		SpatialScoreDistanceRadialAccelerations = SpatialScoreDistanceRadialAccelerations / numStrokes;
		SpatialScoreDistanceRadius = SpatialScoreDistanceRadius / numStrokes;
		SpatialScoreDistanceTeta = SpatialScoreDistanceTeta / numStrokes;
		SpatialScoreDistanceDeltaTeta = SpatialScoreDistanceDeltaTeta / numStrokes;
		SpatialScoreDistanceAccumulatedNormArea = SpatialScoreDistanceAccumulatedNormArea / numStrokes;
		
//		SpatialScoreTimeVelocities = SpatialScoreTimeVelocities / numStrokes;
//		SpatialScoreTimeAccelerations = SpatialScoreTimeAccelerations / numStrokes;
//		SpatialScoreTimeRadialVelocities = SpatialScoreTimeRadialVelocities / numStrokes;
//		
//		SpatialScoreTimeRadialAccelerations = SpatialScoreTimeRadialAccelerations / numStrokes;
//		SpatialScoreTimeRadius = SpatialScoreTimeRadius / numStrokes;
//		SpatialScoreTimeTeta = SpatialScoreTimeTeta / numStrokes;
//		SpatialScoreTimeDeltaTeta = SpatialScoreTimeDeltaTeta / numStrokes;
//		SpatialScoreTimeAccumulatedNormArea = SpatialScoreTimeAccumulatedNormArea / numStrokes;
			
		double reduceScore = 0;
		reduceScore += GetPropScore(0.25, 0.11, SpatialScoreDistanceVelocities);
		reduceScore += GetPropScore(0.94, 0.07, SpatialScoreDistanceAccelerations);
		reduceScore += GetPropScore(0.71, 0.12, SpatialScoreDistanceRadialVelocities);
		reduceScore += GetPropScore(0.83, 0.13, SpatialScoreDistanceAccumulatedNormArea);
		reduceScore += GetPropScore(0.25, 0.128, SpatialScoreTimeVelocities);
		reduceScore += GetPropScore(0.94, 0.07, SpatialScoreTimeAccelerations);
		reduceScore += GetPropScore(0.766, 0.1, SpatialScoreTimeRadialVelocities);
		reduceScore += GetPropScore(0.96, 0.07, SpatialScoreTimeRadialAccelerations);
		reduceScore += GetPropScore(0.85, 0.12, SpatialScoreTimeAccumulatedNormArea);	
		
		mCompareResultsGesture.Score -= reduceScore;
		
		double total = 0;
		total += SpatialScoreDistanceVelocities;
		total += SpatialScoreDistanceAccelerations;
		total += SpatialScoreDistanceRadialVelocities;
		total += SpatialScoreDistanceRadialAccelerations;
		total += SpatialScoreDistanceRadius;
		total += SpatialScoreDistanceTeta;
		total += SpatialScoreDistanceDeltaTeta;
		total += SpatialScoreDistanceAccumulatedNormArea;
		
//		total += SpatialScoreTimeVelocities; 
//		total += SpatialScoreTimeAccelerations;
//		total += SpatialScoreTimeRadialVelocities;
//		total += SpatialScoreTimeRadialAccelerations;
//		total += SpatialScoreTimeRadius;
//		total += SpatialScoreTimeTeta;
//		total += SpatialScoreTimeDeltaTeta;
//		total += SpatialScoreTimeAccumulatedNormArea;
		
		total = total / 6;
	}
	
	private void RunTimeWarp() {		
		
		DtwDistanceCoordinates = 0;
		DtwDistanceNormalizedCoordinates = 0;
		DtwDistanceEvents = 0;
		DtwDistanceVelocities = 0;
		DtwDistanceAccelerations = 0;
		
		int numStrokes = mListStrokeComparers.size();
		
		for(int idx = 0; idx < mListStrokeComparers.size(); idx++) {
			DtwDistanceCoordinates += mListStrokeComparers.get(idx).DtwDistanceCoordinates;
			DtwDistanceNormalizedCoordinates += mListStrokeComparers.get(idx).DtwDistanceNormalizedCoordinates;
			DtwDistanceEvents += mListStrokeComparers.get(idx).DtwDistanceEvents;
			DtwDistanceVelocities += mListStrokeComparers.get(idx).DtwDistanceVelocities;
			DtwDistanceAccelerations += mListStrokeComparers.get(idx).DtwDistanceAccelerations;	
		}
		
		DtwDistanceCoordinates = DtwDistanceCoordinates / numStrokes;
		DtwDistanceNormalizedCoordinates = DtwDistanceNormalizedCoordinates / numStrokes;
		DtwDistanceEvents = DtwDistanceEvents / numStrokes;
		DtwDistanceVelocities = DtwDistanceVelocities / numStrokes;
		DtwDistanceAccelerations = DtwDistanceAccelerations / numStrokes;
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
		IStatEngineResult finalScore = mStatEngine.CompareGestureDoubleValues(mGestureStored.Instruction, paramName, value, mGestureStored.GetFeatureMeansHash());
		AddDoubleParameter(paramName, finalScore, ConstsParamWeights.MEDIUM, value);
	}

	protected void CalcScoreWithoutDistribution(String paramName, double value)
	{
		
		IStatEngineResult finalScore = mStatEngine.CompareGestureScoreWithoutDistribution(mGestureStored.Instruction, paramName, value, mGestureStored.GetFeatureMeansHash());
		AddDoubleParameter(paramName, finalScore, ConstsParamWeights.MEDIUM, value);
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
		CalculateStrokesScore();
		if(!mIsGesturesIdentical) {
			CalculateGestureScore();
			mCompareResultsGesture.Score = mGestureScore; //(mGestureScore + mStrokesScore) / 2;
			CheckStrokesCosineDistance();
			
			CheckSpatialAndDtw();
		}		
		else 
		{
			mCompareResultsGesture.Score = 1;
		}		
	}
	
	protected void CheckSpatialAndDtw() {
		int numFalses = 0;
		
		if(DtwDistanceCoordinates > 5)
		{
			numFalses++;
		}
		if(DtwDistanceNormalizedCoordinates > 0.06)
		{
			numFalses++;
		}		
		if(DtwDistanceVelocities > 0.05)
		{
			numFalses++;
		}
		if(DtwDistanceAccelerations > 0.001)
		{
			numFalses++;
		}
		if(DtwDistanceEvents < 0.7)
		{
			numFalses++;
		}		
		
		if(SpatialScoreDistanceVelocities < 0.8)
		{
			numFalses++;
		}
		
		if(SpatialScoreTimeVelocities < 0.8)
		{
			numFalses++;
		}
		
		if(numFalses > 4)
		{
			if(numFalses == 5)
			{
				mCompareResultsGesture.Score = mCompareResultsGesture.Score - 2;
			}
			if(numFalses == 6)
			{
				mCompareResultsGesture.Score = mCompareResultsGesture.Score - 2;
			}
			if(numFalses == 7)
			{
				mCompareResultsGesture.Score = mCompareResultsGesture.Score - 2;
			}
		}
		
		if(mCompareResultsGesture.Score < 0)
		{
			mCompareResultsGesture.Score = 0;	
		}
	}
	
	protected void CheckStrokesCosineDistance() {		
		mIsStrokeCosineDistanceValid = true;
		for(int idxStrokeComparer = 0; idxStrokeComparer < mListStrokeComparers.size(); idxStrokeComparer++) {
			if(mListStrokeComparers.get(idxStrokeComparer).GetMinCosineDistance() < 1) {
				mCompareResultsGesture.Score = 0;
				mIsStrokeCosineDistanceValid = false;
			}
		}		
	}

	protected void CalculateGestureScore() {
				
		Collections.sort(mCompareResultsGesture.ListCompareResults, new Comparator<ICompareResult>() {
            @Override
            public int compare(ICompareResult score1, ICompareResult score2) {
                if (Math.abs(score1.GetWeight()) > Math.abs(score2.GetWeight())) {
                    return -1;
                }
                if (Math.abs(score1.GetWeight()) < Math.abs(score2.GetWeight())) {
                    return 1;
                }
                return 0;
            }
        });
		
		int zLimit = 2;
		double avgScore = 0;
		double totalWeights = 0;
		
		double tempWeight;
		double tempScore;
				
		int minNumParams =  mCompareResultsGesture.ListCompareResults.size() - 2;
		double minZScore = ConstsGeneral.GESTURE_SCORE_CALC_MIN_Z_SCORE;
		
		boolean isCalculateParameter;
		
		for(int idx = 0; idx < mCompareResultsGesture.ListCompareResults.size(); idx++) {
			tempWeight = Math.abs(mCompareResultsGesture.ListCompareResults.get(idx).GetWeight());
			
			isCalculateParameter = ((idx <= minNumParams) || (idx > minNumParams && tempWeight >= minZScore));
			
			if(isCalculateParameter) {
				if(tempWeight > zLimit) {
					tempWeight = zLimit;
				}
				tempWeight = tempWeight;
				
				tempScore = mCompareResultsGesture.ListCompareResults.get(idx).GetValue();
				
				avgScore += tempScore * tempWeight;
				totalWeights += tempWeight;	
			}
		}		
		
		mGestureScore = avgScore / totalWeights;
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
		
		String key = mUtilsGeneral.GenerateGestureFeatureMeanKey(mGestureStored.Instruction, parameterName);
		
		double mean = 0;
		double sd = 0;
		
		HashMap<String, IFeatureMeanData> hashFeatureMeans = mGestureStored.GetFeatureMeansHash(); 
		
		if(hashFeatureMeans.containsKey(key)) {
			mean = hashFeatureMeans.get(key).GetMean();
			
			INormData normData = NormMgr.GetInstance().GetNormDataByParamName(parameterName, mGestureStored.Instruction);			
			sd = normData.GetInternalStandardDev();
		}
		
		ICompareResult compareResult = 
				(ICompareResult) new CompareResultGeneric(parameterName, score, weight, originalValue, mean, sd);
		mCompareResultsGesture.ListCompareResults.add(compareResult);
	}
	
	public CompareResultSummary GetResultsSummary()
	{
		return mCompareResultsGesture;
	}

	public boolean IsStrokeCosineDistanceValid() {
		return mIsStrokeCosineDistanceValid;
	}
}
