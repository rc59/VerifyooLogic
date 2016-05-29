package Logic.Comparison;

import java.util.ArrayList;
import java.util.Collections;
import java.util.Comparator;
import java.util.HashMap;

import Consts.ConstsParamNames;
import Consts.ConstsParamWeights;
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
	
	protected boolean mMinCosineDistanceValid;	
	
	protected boolean mIsSimilarDevices;
	
	protected HashMap<String, Double> mCompareFilters;
	
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
			CompareGestureStrokes();
			CompareGestureFeatures();
			CalculateFinalScore();	
		}
		else {
			mCompareResultsGesture.Score = 0;
		}
	}
	
	public void CompareGestures(GestureExtended gestureStored, GestureExtended gestureAuth, HashMap<String, Double> compareFilters) { 					
		mCompareFilters = compareFilters;
		CompareGestures(gestureStored, gestureAuth);
	}
	
	protected boolean IsNeedToRun(String methodName)
	{
		boolean isNeedToRun = true;
		if(mCompareFilters != null && !mCompareFilters.containsKey(methodName)) {
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
		if(IsNeedToRun("CompareGestureAreas")){
			CompareGestureAreas();
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
		if(IsNeedToRun("CompareGestureVelocityPeaks")){
			CompareGestureVelocityPeaks();
		}
		if(IsNeedToRun("CompareGestureVelocityPeaksIntervalPercentage")){
			CompareGestureVelocityPeaksIntervalPercentage();
		}
		if(IsNeedToRun("CompareGestureStartDirection")){
			CompareGestureStartDirection();
		}	
		if(IsNeedToRun("CompareGestureEndDirection")){
			CompareGestureEndDirection();
		}	
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

	protected void CompareGestureEndDirection(){
		double endDirectionAuth = mGestureAuth.GestureEndDirection;
		CalcDoubleParameter(ConstsParamNames.Gesture.GESTURE_AVG_END_DIRECTION, endDirectionAuth);		
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
		
		if(mMinCosineDistanceScore > 1.5) {
			mMinCosineDistanceValid = true;
		}
	}
	
	protected void CompareGesturePressure()
	{								
		double avgPressure = mGestureAuth.GestureAvgPressure;
		CalcDoubleParameter(ConstsParamNames.Gesture.GESTURE_AVG_PRESSURE, avgPressure);
	}
	
	protected void CompareGestureSurface()
	{
		double avgSurface = mGestureAuth.GestureAvgSurface;
		CalcDoubleParameter(ConstsParamNames.Gesture.GESTURE_AVG_SURFACE, avgSurface);
	}
	
	protected void CompareGestureAreas()
	{		
		double areaAuth = mGestureAuth.GestureTotalStrokeArea;						
		CalcDoubleParameter(ConstsParamNames.Gesture.GESTURE_TOTAL_STROKE_AREA, areaAuth);
	}
	
	protected void CompareGestureTotalStrokesTime() {		
		double totalTimeNoPausesAuth = mGestureAuth.GestureTotalStrokeTimeInterval;			
		CalcDoubleParameter(ConstsParamNames.Gesture.GESTURE_TOTAL_STROKES_TIME_INTERVAL, totalTimeNoPausesAuth);
	}

	protected void CompareGestureTotalTimeInterval() {		
		double totalTimeAuth = mGestureAuth.GestureTotalTimeInterval;
		CalcDoubleParameter(ConstsParamNames.Gesture.GESTURE_TOTAL_TIME_INTERNVAL, totalTimeAuth);
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
			
			if(!mMinCosineDistanceValid) {
				mCompareResultsGesture.Score = 0.5;	
			}
		}		
		else 
		{
			mCompareResultsGesture.Score = 1;
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
				
		for(int idx = 0; idx < mCompareResultsGesture.ListCompareResults.size(); idx++) {
			tempWeight = Math.abs(mCompareResultsGesture.ListCompareResults.get(idx).GetWeight());
			
			if(tempWeight > zLimit) {
				tempWeight = zLimit;
			}
			tempWeight = tempWeight * tempWeight;
			
			tempScore = mCompareResultsGesture.ListCompareResults.get(idx).GetValue();
			
			avgScore += tempScore * tempWeight;
			totalWeights += tempWeight;
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
}
