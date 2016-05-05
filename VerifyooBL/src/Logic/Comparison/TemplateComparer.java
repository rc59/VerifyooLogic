package Logic.Comparison;

import java.util.ArrayList;

import Data.Comparison.CompareResultSummary;
import Data.UserProfile.Raw.Template;

public class TemplateComparer {
		
	protected Template mTemplateStored;
	protected Template mTemplateAuth;
	
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
	
	public void CompareTemplates(Template templateStored, Template templateAuth)
	{
		GestureComparer gestureComparer;			
				
		mTemplateStored = templateStored;
		mTemplateAuth = templateAuth;	
		
		if(templateStored.ListGestures.size() == templateAuth.ListGestures.size()) {			
			for(int idxGesture = 0; idxGesture < templateStored.ListGestures.size(); idxGesture++) {						
				gestureComparer = new GestureComparer(mIsSimilarDevices);
				
				gestureComparer.CompareGestures(templateStored.ListGestures.get(idxGesture), templateAuth.ListGestures.get(idxGesture)); 
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
}
