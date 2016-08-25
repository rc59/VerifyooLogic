package Logic.Utils;

import java.util.HashMap;

import Data.UserProfile.Extended.GestureExtended;
import Data.UserProfile.Extended.MotionEventExtended;

public class UtilsGeneral {
	public String GenerateGestureFeatureMeanKey(String instruction, String paramName)
	{
		String key = String.format("%s-%s", instruction, paramName);
		return key;
	}
	
	public String GenerateStrokeFeatureMeanKey(String instruction, String paramName, int strokeIdx)
	{
		String key = String.format("%s-%s-%s", instruction, String.valueOf(strokeIdx) ,paramName);
		return key;
	}
	
	public String GenerateContainerKeySafe(String instruction, int idxStroke, HashMap hashNorms) {
		String key = GenerateContainerKey(instruction, idxStroke);
		boolean strokeWasFound = false;
		while(!strokeWasFound) {
			if(hashNorms.containsKey(key)) {
				strokeWasFound = true;
			}
			else {
				idxStroke--;
				key = GenerateContainerKey(instruction, idxStroke);
			}
			
			if(idxStroke < 0) {
				strokeWasFound = true;
			}
		}		
		
		return key;
	}
	
	public String GenerateContainerKey(String instruction, int idxStroke) {
		return String.format("%s-%s", instruction, Integer.toString(idxStroke));
	}

	public double GetGestureTotalTime(GestureExtended gesture) {				
		MotionEventExtended eventStart = gesture.ListGestureEventsExtended.get(0);
		MotionEventExtended eventEnd = gesture.ListGestureEventsExtended.get(gesture.ListGestureEventsExtended.size() - 1);
		
		double gestureTotalTime = eventEnd.EventTime - eventStart.EventTime;
		
		return gestureTotalTime;
	}
}
