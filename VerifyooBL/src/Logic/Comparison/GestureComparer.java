package Logic.Comparison;

import java.util.ArrayList;

import Data.Comparison.CompareResultSummary;
import Data.UserProfile.Raw.Gesture;
import Data.UserProfile.Raw.Stroke;

public class GestureComparer {

	protected Gesture mGestureStored;
	protected Gesture mGestureAuth;
	
	protected ArrayList<StrokeComparer> mListStrokeComparers;	
	CompareResultSummary mCompareResultsGesture;	
	
	protected boolean mIsSimilarDevices;
	
	public GestureComparer(boolean isSimilarDevices)
	{
		mIsSimilarDevices = isSimilarDevices;
		mListStrokeComparers = new ArrayList<>();
			
		mCompareResultsGesture = new CompareResultSummary();		
	}
	
	public void CompareGestures(Gesture gestureStored, Gesture gestureAuth) { 					
		mGestureStored = gestureStored;
		mGestureAuth = gestureAuth;
		
		if(mGestureAuth.ListStrokes.size() == mGestureStored.ListStrokes.size())
		{			
			mCompareResultsGesture = new CompareResultSummary();			
			
			Stroke tempStrokeStored;
			Stroke tempStrokeAuth;
			
			StrokeComparer strokeComparer;
			for(int idxStroke = 0; idxStroke < mGestureStored.ListStrokes.size(); idxStroke++) {
				strokeComparer = new StrokeComparer(mIsSimilarDevices);
				
				tempStrokeStored = mGestureStored.ListStrokes.get(idxStroke);
				tempStrokeAuth = mGestureAuth.ListStrokes.get(idxStroke);
				
				strokeComparer.CompareStrokes(tempStrokeStored, tempStrokeAuth);
				mListStrokeComparers.add(strokeComparer);			
			}
			
			CalculateFinalScore();	
		}		
	}
	
	public double GetScore()
	{
		return mCompareResultsGesture.Score;
	}	
	
	protected void CalculateFinalScore()
	{
		double avgScore = 0;
		
		for(int idx = 0; idx < mListStrokeComparers.size(); idx++) {
			avgScore += mListStrokeComparers.get(idx).GetScore();
		}
		
		mCompareResultsGesture.Score = avgScore / mListStrokeComparers.size();		
	}

}
