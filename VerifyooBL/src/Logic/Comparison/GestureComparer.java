package Logic.Comparison;

import java.util.ArrayList;
import java.util.HashMap;

import Consts.ConstsParamNames;
import Consts.ConstsParamWeights;
import Data.Comparison.CompareResultGeneric;
import Data.Comparison.CompareResultSummary;
import Data.Comparison.Interfaces.ICompareResult;
import Data.UserProfile.Extended.GestureExtended;
import Data.UserProfile.Extended.StrokeExtended;
import Logic.Calc.UtilsComparison;
import Logic.Comparison.Stats.FeatureMeanData;
import Logic.Comparison.Stats.StatEngine;
import Logic.Comparison.Stats.Interfaces.IStatEngine;

public class GestureComparer {

	protected boolean mIsGesturesIdentical;
	protected IStatEngine mStatEngine;
	
	protected GestureExtended mGestureStored;
	protected GestureExtended mGestureAuth;
	
	protected UtilsComparison mUtilsComparison;
	
	protected ArrayList<StrokeComparer> mListStrokeComparers;	
	protected double mGestureScore;
	protected double mStrokesScore;
	CompareResultSummary mCompareResultsGesture;			
	
	protected boolean mIsSimilarDevices;
	
	public GestureComparer(boolean isSimilarDevices)
	{		
		mIsGesturesIdentical = true;
		mIsSimilarDevices = isSimilarDevices;
		mListStrokeComparers = new ArrayList<>();
		mGestureScore = 0;
		mStrokesScore = 0;
		mCompareResultsGesture = new CompareResultSummary();		
		mUtilsComparison = new UtilsComparison();
		mStatEngine = StatEngine.GetInstance();
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
	
	protected void CompareGestureFeatures()
	{
		CompareGestureLengths();
		CompareGestureAvgVelocity();
		CompareGestureTotalTimeWithPauses();
		CompareGestureTotalTimeWithoutPauses();
	}
	
	private void CompareGestureTotalTimeWithoutPauses() {
		double totalTimeNoPausesStored = mGestureStored.GestureTotalTimeWithoutPauses;
		double totalTimeNoPausesAuth = mGestureAuth.GestureTotalTimeWithoutPauses;			
		
		double finalScore = mUtilsComparison.CompareNumericalValues(totalTimeNoPausesStored, totalTimeNoPausesAuth, 0.75);
		//double finalScore = mStatEngine.CompareGestureDoubleValues(mGestureStored.Instruction, ConstsParamNames.Gesture.GESTURE_TOTAL_TIME_WITHOUT_PAUSES, totalTimeNoPausesAuth, mGestureStored.GetFeatureMeansHash());
		//AddDoubleParameter(ConstsParamNames.Gesture.GESTURE_TOTAL_TIME_WITHOUT_PAUSES, finalScore, ConstsParamWeights.MEDIUM);
	}

	private void CompareGestureTotalTimeWithPauses() {
		double totalTimeStored = mGestureStored.GestureTotalTimeWithPauses;
		double totalTimeAuth = mGestureAuth.GestureTotalTimeWithPauses;
				
		//double finalScore = mUtilsComparison.CompareNumericalValues(totalTimeStored, totalTimeAuth, 0.75);
		double finalScore = mStatEngine.CompareGestureDoubleValues(mGestureStored.Instruction, ConstsParamNames.Gesture.GESTURE_TOTAL_TIME_WITH_PAUSES, totalTimeAuth, mGestureStored.GetFeatureMeansHash());
		AddDoubleParameter(ConstsParamNames.Gesture.GESTURE_TOTAL_TIME_WITH_PAUSES, finalScore, ConstsParamWeights.MEDIUM);
	}

	private void CompareGestureAvgVelocity() {
		double avgVelocityStored = mGestureStored.GestureAverageVelocity;
		double avgVelocityAuth = mGestureAuth.GestureAverageVelocity;
		
		//double finalScore = mUtilsComparison.CompareNumericalValues(avgVelocityStored, avgVelocityAuth, 0.75);
		double finalScore = mStatEngine.CompareGestureDoubleValues(mGestureStored.Instruction, ConstsParamNames.Gesture.AVERAGE_VELOCITY, avgVelocityAuth, mGestureStored.GetFeatureMeansHash());
		AddDoubleParameter(ConstsParamNames.Gesture.AVERAGE_VELOCITY, finalScore, ConstsParamWeights.MEDIUM);
	}

	private void CompareGestureLengths() {
		double lengthStored = mGestureStored.GestureLengthMM;
		double lengthAuth = mGestureAuth.GestureLengthMM;
		
		//double finalScore = mUtilsComparison.CompareNumericalValues(lengthStored, lengthAuth, 0.75);
		double finalScore = mStatEngine.CompareGestureDoubleValues(mGestureStored.Instruction, ConstsParamNames.Gesture.LENGTH, lengthAuth, mGestureStored.GetFeatureMeansHash());
		AddDoubleParameter(ConstsParamNames.Gesture.LENGTH, finalScore, ConstsParamWeights.MEDIUM);
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
	
	protected void CalculateFinalScore()
	{
		CalculateStrokesScore();
		if(!mIsGesturesIdentical) {
			CalculateGestureScore();
			mCompareResultsGesture.Score = mGestureScore; //(mGestureScore + mStrokesScore) / 2;
		}		
		else 
		{
			mCompareResultsGesture.Score = 1;
		}		
	}
	
	protected void CalculateGestureScore() {
		double avgScore = 0;
		double totalWeights = 0;
		for(int idx = 0; idx < mCompareResultsGesture.ListCompareResults.size(); idx++) {
			avgScore += mCompareResultsGesture.ListCompareResults.get(idx).GetValue() * mCompareResultsGesture.ListCompareResults.get(idx).GetWeight();
			totalWeights += mCompareResultsGesture.ListCompareResults.get(idx).GetWeight();
		}
		
		mGestureScore = avgScore / totalWeights;
	}
	
	protected void CalculateStrokesScore() {
		double avgScore = 0;
				
		
		for(int idx = 0; idx < mListStrokeComparers.size(); idx++) {
			if(mListStrokeComparers.get(idx).mIsSimilarDevices)
			avgScore += mListStrokeComparers.get(idx).GetScore();
			if(!mListStrokeComparers.get(idx).IsStrokesIdentical()) {
				mIsGesturesIdentical = false;
			}
		}
		
		mStrokesScore = avgScore / mListStrokeComparers.size();
	}

	protected void AddDoubleParameter(String parameterName, double score, double weight)
	{
		ICompareResult compareResult = 
				(ICompareResult) new CompareResultGeneric(parameterName, score, weight);
		mCompareResultsGesture.ListCompareResults.add(compareResult);
	}
}
