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
	
	public String GenerateStrokeMatrixMeanKey(String instruction, int strokeIdx)
	{
		String key = String.format("%s-%s-Matrix", instruction, String.valueOf(strokeIdx));
		return key;
	}
	
	public String GenerateStrokeFeatureMeanKey(String instruction, String paramName, int strokeIdx)
	{
		String key = String.format("%s-%s-%s", instruction, String.valueOf(strokeIdx) ,paramName);
		return key;
	}
	
	public String GenerateStrokeSamplingFeatureMeanKey(String instruction, String paramName, String samplingType, int strokeIdx)
	{
		String key = String.format("%s-%s-%s-%s", instruction, String.valueOf(strokeIdx) , paramName, samplingType);
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

	public double GetBoundaryAdj(String paramName) {
		double boundary;
		switch(paramName) {
			default:
				boundary = 0.18;
				break;
			case Consts.ConstsParamNames.Stroke.STROKE_MID_VELOCITY:
				boundary = 0.25;
				break;
			case Consts.ConstsParamNames.Stroke.STROKE_TOTAL_AREA_MINX_MINY:
				boundary = 0.23;
				break;
			case Consts.ConstsParamNames.Stroke.STROKE_TOTAL_AREA:
				boundary = 0.22;
				break;
			case Consts.ConstsParamNames.Stroke.STROKE_TIME_INTERVAL:
				boundary = 0.18;
				break;
			case Consts.ConstsParamNames.Stroke.STROKE_NUM_EVENTS:
				boundary = 0.18;
				break;
			case Consts.ConstsParamNames.Stroke.STROKE_MIDDLE_SURFACE:
				boundary = 0.12;
				break;
			case Consts.ConstsParamNames.Stroke.STROKE_MIDDLE_PRESSURE:
				boundary = 0.10;
				break;
			case Consts.ConstsParamNames.Stroke.STROKE_MAX_VELOCITY:
				boundary = 0.22;
				break;
			case Consts.ConstsParamNames.Stroke.STROKE_MAX_RADIAL_VELOCITY:
				boundary = 0.20;
				break;
			case Consts.ConstsParamNames.Stroke.STROKE_MAX_RADIAL_ACCELERATION:
				boundary = 0.22;
				break;
			case Consts.ConstsParamNames.Stroke.STROKE_MAX_ACCELERATION:
				boundary = 0.24;
				break;
			case Consts.ConstsParamNames.Stroke.STROKE_AVERAGE_VELOCITY:
				boundary = 0.22;
				break;
		}
		
		return boundary;
	}
}
