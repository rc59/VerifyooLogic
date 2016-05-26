package Logic.Comparison;

import java.util.ArrayList;
import java.util.HashMap;

import Data.Comparison.CompareResultSummary;
import Data.UserProfile.Extended.TemplateExtended;
import Data.UserProfile.Raw.Template;
import Logic.Comparison.Stats.FeatureMeanData;
import Logic.Comparison.Stats.StatEngine;

public class TemplateComparer {
//		
//	protected Template mTemplateStored;
//	protected Template mTemplateAuth;

	protected TemplateExtended mTemplateStored;
	protected TemplateExtended mTemplateAuth;
	
	protected ArrayList<GestureComparer> mListGestureComparers;
	CompareResultSummary mCompareResultsTemplate;
	
	protected boolean mIsSimilarDevices;	
	
	public TemplateComparer()
	{				
		mCompareResultsTemplate = new CompareResultSummary();				
		mListGestureComparers = new ArrayList<>();			
		CheckIfDevicesAreIdentical();
	}
	
	protected void CheckIfDevicesAreIdentical()
	{
		mIsSimilarDevices = true;
	}
	
	public void CompareTemplates(TemplateExtended templateStored, TemplateExtended templateAuth)
	{		
		GestureComparer gestureComparer;			
				
		mTemplateStored = templateStored;
		mTemplateAuth = templateAuth;	
		
		if(templateStored.ListGestureExtended.size() == templateAuth.ListGestureExtended.size()) {			
			for(int idxGesture = 0; idxGesture < templateStored.ListGestureExtended.size(); idxGesture++) {						
				gestureComparer = new GestureComparer(mIsSimilarDevices);
				
				gestureComparer.CompareGestures(templateStored.ListGestureExtended.get(idxGesture), templateAuth.ListGestureExtended.get(idxGesture)); 
				mListGestureComparers.add(gestureComparer);
			}
			
			CalculateFinalScore();
		}				
	}
	
	protected void CalculateFinalScore()
	{
		double avgScore = 0;
		for(int idx = 0; idx < mListGestureComparers.size(); idx++) {
			avgScore += mListGestureComparers.get(idx).GetScore();			
		}
		
		mCompareResultsTemplate.Score = avgScore / mListGestureComparers.size();
	}
	
	public double GetScore()
	{
		return mCompareResultsTemplate.Score;
	}
	
	public CompareResultSummary GetResultsSummary()
	{
		return mCompareResultsTemplate;
	}
}
