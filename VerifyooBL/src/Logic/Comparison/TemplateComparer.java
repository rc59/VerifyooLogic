package Logic.Comparison;

import java.util.ArrayList;
import java.util.Collections;
import java.util.HashMap;

import Data.Comparison.CompareResultSummary;
import Data.UserProfile.Extended.GestureExtended;
import Data.UserProfile.Extended.TemplateExtended;
import Data.UserProfile.Raw.Template;
import Logic.Comparison.Stats.FeatureMeanData;
import Logic.Comparison.Stats.StatEngine;

public class TemplateComparer {	
	
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
		
		HashMap<String, Boolean> hashStoredInstructions = new HashMap<>();
		String tempInstruction;
		
		for(int idxGesture = 0; idxGesture < templateStored.ListGestureExtended.size(); idxGesture++) {
			tempInstruction = templateStored.ListGestureExtended.get(idxGesture).Instruction;
			if(!hashStoredInstructions.containsKey(tempInstruction)) {
				hashStoredInstructions.put(tempInstruction, false);
			}
		}
		
		GestureExtended tempGestureAuth;
		GestureExtended tempGestureStored;
		ArrayList<GestureExtended> tempListGestureStored;
		
		for(int idxGestureAuth = 0; idxGestureAuth < templateAuth.ListGestureExtended.size(); idxGestureAuth++) {
			
			tempGestureAuth = templateAuth.ListGestureExtended.get(idxGestureAuth);
			tempInstruction = tempGestureAuth.Instruction;
			
			if(hashStoredInstructions.containsKey(tempInstruction) && !hashStoredInstructions.get(tempInstruction)) {
				hashStoredInstructions.remove(tempInstruction);
				hashStoredInstructions.put(tempInstruction, true);
				
				if(templateStored.GetHashGesturesByInstruction().containsKey(tempInstruction)) {
					tempListGestureStored = templateStored.GetHashGesturesByInstruction().get(tempInstruction);
					if(tempListGestureStored.size() > 0) {
						tempGestureStored = tempListGestureStored.get(0);
						
						gestureComparer = new GestureComparer(mIsSimilarDevices);
						
						gestureComparer.CompareGestures(tempGestureStored, tempGestureAuth); 
						mListGestureComparers.add(gestureComparer);
					}
					else {
						break;
					}
				}
				else {
					break;
				}
			}
		}
		
		CalculateFinalScore();
	}
	
	public void CompareTemplates1(TemplateExtended templateStored, TemplateExtended templateAuth)
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
		if(mListGestureComparers.size() == mTemplateAuth.ListGestureExtended.size()) {
			ArrayList<Double> listScores = new ArrayList<>();
			
			double avgScore = 0;
			for(int idx = 0; idx < mListGestureComparers.size(); idx++) {						
				listScores.add(mListGestureComparers.get(idx).GetScore());
			}
			
			Collections.sort(listScores);
			
			int scoreCount = 0;
			for(int idxScore = 1; idxScore < listScores.size(); idxScore++) {
				avgScore += listScores.get(idxScore);
				scoreCount++;
			}			
			
			mCompareResultsTemplate.Score = avgScore / scoreCount;
			if(listScores.get(0) < 0.5) {
				mCompareResultsTemplate.Score = 0;
			}
		}		
	}
	
	public double GetScore()
	{
		return mCompareResultsTemplate.Score;
	}
	
	public CompareResultSummary GetResultsSummary()
	{
		return mCompareResultsTemplate;
	}

	public ArrayList<GestureComparer> GetGestureComparers() {		// 
		return mListGestureComparers;
	}
}
