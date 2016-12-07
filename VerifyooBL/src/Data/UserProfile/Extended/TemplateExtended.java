package Data.UserProfile.Extended;

import java.util.ArrayList;
import java.util.HashMap;

import Consts.ConstsParamNames;
import Data.MetaData.GestureMaxTimeContainer;
import Data.UserProfile.Raw.Gesture;
import Data.UserProfile.Raw.Template;
import Logic.Comparison.Stats.Interfaces.IFeatureMeanData;

public class TemplateExtended extends Template {	
	public ArrayList<GestureExtended> ListGestureExtended;
	
	protected HashMap<String, ArrayList<GestureExtended>> mHashGesturesByInstruction;
	
	private HashMap<String, IFeatureMeanData> mHashFeatureMeans;
	
	private GestureMaxTimeContainer mGestureMaxTimeContainer;
	
	public TemplateExtended(Template template) {		
		InitTemplateExtended(template);
	}

	private void InitTemplateExtended(Template template) {
		
		mGestureMaxTimeContainer = new GestureMaxTimeContainer(); 
		mHashFeatureMeansInit = template.GetHashMap();
		Id = template.Id;
		if(mHashFeatureMeansInit != null && mHashFeatureMeansInit.keySet().size() > 0) {
			mHashFeatureMeans = mHashFeatureMeansInit;
		}
		else {
			mHashFeatureMeans = new HashMap<>();
		}
		
		ListGestureExtended = new ArrayList<GestureExtended>();
		ListGestures = template.ListGestures;
				
		Gesture tempGesture;
		GestureExtended tempGestureExtended;
		
		mHashGesturesByInstruction = new HashMap<>();
		ArrayList<GestureExtended> tempListGestures;
		
		double currGestureStartTime, prevGestureEndTime;
		double tempDelay;
		
		for(int idxGesture = 0; idxGesture < template.ListGestures.size(); idxGesture++) {
			tempGesture = template.ListGestures.get(idxGesture);
			tempGestureExtended = new GestureExtended(tempGesture, mHashFeatureMeans, idxGesture);
			
			if(idxGesture > 0) {
				currGestureStartTime = tempGestureExtended.ListGestureEventsExtended.get(0).EventTime;
				prevGestureEndTime = GetPrevGestureEndTime();
				tempDelay = currGestureStartTime - prevGestureEndTime;
				tempGestureExtended.GestureDelay = tempDelay;
				
				tempGestureExtended.AddGestureValue(ConstsParamNames.Gesture.GESTURE_DELAY_TIME, tempGestureExtended.GestureDelay);
			}
			
			mGestureMaxTimeContainer.AddGesture(tempGestureExtended);
			ListGestureExtended.add(tempGestureExtended);
			
			if(!mHashGesturesByInstruction.containsKey(tempGestureExtended.Instruction)) {
				tempListGestures = new ArrayList<>();
				tempListGestures.add(tempGestureExtended);
				mHashGesturesByInstruction.put(tempGestureExtended.Instruction, tempListGestures);
			}
			else {
				tempListGestures = mHashGesturesByInstruction.get(tempGestureExtended.Instruction);
				tempListGestures.add(tempGestureExtended);
			}
		}
	}
	
	private double GetPrevGestureEndTime() {
		int numGestures = ListGestureExtended.size();
		GestureExtended prevGesture = ListGestureExtended.get(numGestures - 1);
				
		int numStrokes = prevGesture.ListStrokesExtended.size();
		StrokeExtended prevGestureLastStroke = prevGesture.ListStrokesExtended.get(numStrokes - 1);
		
		int numEvents = prevGestureLastStroke.ListEventsExtended.size();
		MotionEventExtended prevGestureLastStrokeLastEvent = prevGestureLastStroke.ListEventsExtended.get(numEvents - 1);
		
		return prevGestureLastStrokeLastEvent.EventTime;
	}

	public HashMap<String, ArrayList<GestureExtended>> GetHashGesturesByInstruction() {
		return mHashGesturesByInstruction;
	}
	
	public double GetGestureMaxTimeInterval(String instruction) {
		return mGestureMaxTimeContainer.GetMaxTimeForGesture(instruction);
	}
	
	public HashMap<String, IFeatureMeanData> GetHashMapNorms() {
		return mHashFeatureMeans;
	}
}
