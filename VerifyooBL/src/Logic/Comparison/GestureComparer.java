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
import Logic.Comparison.Stats.Interfaces.IStatEngine;
import Logic.Utils.Utils;
import Logic.Utils.UtilsComparison;
import Logic.Utils.UtilsVectors;

public class GestureComparer {	
	
	protected boolean mIsGesturesIdentical;
	protected IStatEngine mStatEngine;
	
	protected GestureExtended mGestureStored;
	protected GestureExtended mGestureAuth;
	
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
		if(IsNeedToRun("CompareGestureLengths")){
			CompareGestureLengths();	
		}
		if(IsNeedToRun("CompareGestureAvgVelocity")){
			CompareGestureAvgVelocity();
		}
		if(IsNeedToRun("CompareGestureMinCosineDistance")){
			CompareGestureMinCosineDistance();
		}
		else {
			mMinCosineDistanceValid = true;
		}
		if(IsNeedToRun("CompareGestureTotalTimeWithPauses")){
			CompareGestureTotalTimeWithPauses();
		}
		if(IsNeedToRun("CompareGestureTotalTimeWithoutPauses")){
			//CompareGestureTotalTimeWithoutPauses();
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
	}
	
	protected void CompareGestureAverageStartAcceleration()
	{
		IStatEngineResult finalScore = mStatEngine.CompareGestureDoubleValues(mGestureStored.Instruction, ConstsParamNames.Gesture.AVG_START_ACCELERATION, mGestureAuth.GestureAverageStartAcceleration, mGestureStored.GetFeatureMeansHash());		
		AddDoubleParameter(ConstsParamNames.Gesture.AVG_START_ACCELERATION, finalScore, ConstsParamWeights.MEDIUM, mGestureAuth.GestureAverageStartAcceleration);
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
		IStatEngineResult finalScore = mStatEngine.CompareGestureDoubleValues(mGestureStored.Instruction, ConstsParamNames.Gesture.GESTURE_AVG_PRESSURE, mGestureAuth.GestureAvgPressure, mGestureStored.GetFeatureMeansHash());		
		AddDoubleParameter(ConstsParamNames.Gesture.GESTURE_AVG_PRESSURE, finalScore, ConstsParamWeights.MEDIUM, mGestureAuth.GestureAvgPressure);
	}
	
	protected void CompareGestureSurface()
	{
		IStatEngineResult finalScore = mStatEngine.CompareGestureDoubleValues(mGestureStored.Instruction, ConstsParamNames.Gesture.GESTURE_AVG_SURFACE, mGestureAuth.GestureAvgPressure, mGestureStored.GetFeatureMeansHash());		
		AddDoubleParameter(ConstsParamNames.Gesture.GESTURE_AVG_SURFACE, finalScore, ConstsParamWeights.MEDIUM, mGestureAuth.GestureAvgPressure);
	}
	
	protected void CompareGestureAreas()
	{		
		double areaAuth = mGestureAuth.GestureTotalStrokeArea;						
		
		IStatEngineResult finalScore = mStatEngine.CompareGestureDoubleValues(mGestureStored.Instruction, ConstsParamNames.Gesture.GESTURE_TOTAL_STROKE_AREA, areaAuth, mGestureStored.GetFeatureMeansHash());		
		AddDoubleParameter(ConstsParamNames.Gesture.GESTURE_TOTAL_STROKE_AREA, finalScore, ConstsParamWeights.MEDIUM, areaAuth);
	}
	
	protected void CompareGestureTotalTimeWithoutPauses() {
		double totalTimeNoPausesStored = mGestureStored.GestureTotalTimeWithoutPauses;
		double totalTimeNoPausesAuth = mGestureAuth.GestureTotalTimeWithoutPauses;			
		
		//double finalScore = mUtilsComparison.CompareNumericalValues(totalTimeNoPausesStored, totalTimeNoPausesAuth, 0.75);
		IStatEngineResult finalScore = mStatEngine.CompareGestureDoubleValues(mGestureStored.Instruction, ConstsParamNames.Gesture.GESTURE_TOTAL_TIME_WITHOUT_PAUSES, totalTimeNoPausesAuth, mGestureStored.GetFeatureMeansHash());
		AddDoubleParameter(ConstsParamNames.Gesture.GESTURE_TOTAL_TIME_WITHOUT_PAUSES, finalScore, ConstsParamWeights.MEDIUM, totalTimeNoPausesAuth);
	}

	protected void CompareGestureTotalTimeWithPauses() {
		double totalTimeStored = mGestureStored.GestureTotalTimeWithPauses;
		double totalTimeAuth = mGestureAuth.GestureTotalTimeWithPauses;
				
		//double finalScore = mUtilsComparison.CompareNumericalValues(totalTimeStored, totalTimeAuth, 0.75);
		IStatEngineResult finalScore = mStatEngine.CompareGestureDoubleValues(mGestureStored.Instruction, ConstsParamNames.Gesture.GESTURE_TOTAL_TIME_WITH_PAUSES, totalTimeAuth, mGestureStored.GetFeatureMeansHash());
		AddDoubleParameter(ConstsParamNames.Gesture.GESTURE_TOTAL_TIME_WITH_PAUSES, finalScore, ConstsParamWeights.MEDIUM, totalTimeAuth);
	}

	protected void CompareGestureAvgVelocity() {
		double avgVelocityStored = mGestureStored.GestureAverageVelocity;
		double avgVelocityAuth = mGestureAuth.GestureAverageVelocity;
		
		//double finalScore = mUtilsComparison.CompareNumericalValues(avgVelocityStored, avgVelocityAuth, 0.75);
		IStatEngineResult finalScore = mStatEngine.CompareGestureDoubleValues(mGestureStored.Instruction, ConstsParamNames.Gesture.AVERAGE_VELOCITY, avgVelocityAuth, mGestureStored.GetFeatureMeansHash());
		AddDoubleParameter(ConstsParamNames.Gesture.AVERAGE_VELOCITY, finalScore, ConstsParamWeights.MEDIUM, avgVelocityAuth);
	}

	protected void CompareGestureLengths() {
		double lengthStored = mGestureStored.GestureLengthMM;
		double lengthAuth = mGestureAuth.GestureLengthMM;
		
		//double finalScore = mUtilsComparison.CompareNumericalValues(lengthStored, lengthAuth, 0.75);
		IStatEngineResult finalScore = mStatEngine.CompareGestureDoubleValues(mGestureStored.Instruction, ConstsParamNames.Gesture.LENGTH, lengthAuth, mGestureStored.GetFeatureMeansHash());
		AddDoubleParameter(ConstsParamNames.Gesture.LENGTH, finalScore, ConstsParamWeights.MEDIUM, lengthAuth);
	}
	
	protected void CompareGestureStartDirection() {
		double startDirectionStored = mGestureStored.GestureStartDirection;
		double startDirectionAuth = mGestureAuth.GestureStartDirection;
//		double finalScore = mStatEngine.CompareGestureDoubleValues(mGestureStored.Instruction, ConstsParamNames.Gesture.GESTURE_AVG_START_DIRECTION, startDirectionAuth, mGestureStored.GetFeatureMeansHash());
//		AddDoubleParameter(ConstsParamNames.Gesture.GESTURE_AVG_START_DIRECTION, finalScore, ConstsParamWeights.MEDIUM, startDirectionAuth);
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
		
		
		int limit = 2;
		double avgScore = 0;
		double totalWeights = 0;
		
		for(int idx = 0; idx < limit; idx++) {
			avgScore += mCompareResultsGesture.ListCompareResults.get(idx).GetValue();
			totalWeights += 1;
		}
		
		
//		for(int idx = 0; idx < mCompareResultsGesture.ListCompareResults.size(); idx++) {
//			avgScore += mCompareResultsGesture.ListCompareResults.get(idx).GetValue() * mCompareResultsGesture.ListCompareResults.get(idx).GetWeight();
//			totalWeights += mCompareResultsGesture.ListCompareResults.get(idx).GetWeight();
//		}
		
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
		
		ICompareResult compareResult = 
				(ICompareResult) new CompareResultGeneric(parameterName, score, weight, originalValue);
		mCompareResultsGesture.ListCompareResults.add(compareResult);
	}
}
