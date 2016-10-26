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
				boundary = 0.3;
				break;
			case Consts.ConstsParamNames.Gesture.GESTURE_TOTAL_AREA_MINX_MINY:
				boundary = 0.25;
				break;
			case Consts.ConstsParamNames.Gesture.GESTURE_TOTAL_AREA:
				boundary = 0.25;
				break;
			case Consts.ConstsParamNames.Stroke.STROKE_LENGTH:
				boundary = 0.15;
				break;
			case Consts.ConstsParamNames.Stroke.STROKE_TOTAL_AREA_MINX_MINY:
				boundary = 0.25;
				break;
			case Consts.ConstsParamNames.Stroke.STROKE_TOTAL_AREA:
				boundary = 0.22;
				break;
			case Consts.ConstsParamNames.Stroke.STROKE_TIME_INTERVAL:
				boundary = 0.18;
				break;
			case Consts.ConstsParamNames.Gesture.GESTURE_TOTAL_TIME_INTERVAL:
				boundary = 0.16;
				break;
			case Consts.ConstsParamNames.Stroke.STROKE_NUM_EVENTS:
				boundary = 0.16;
				break;
			case Consts.ConstsParamNames.Stroke.STROKE_MIDDLE_SURFACE:
				boundary = 0.16;
				break;
			case Consts.ConstsParamNames.Stroke.STROKE_MIDDLE_PRESSURE:
				boundary = 0.10;
				break;
			case Consts.ConstsParamNames.Stroke.STROKE_MAX_VELOCITY:
				boundary = 0.22;
				break;
			case Consts.ConstsParamNames.Stroke.STROKE_MAX_RADIAL_VELOCITY:
				boundary = 0.28;
				break;
			case Consts.ConstsParamNames.Stroke.STROKE_MAX_RADIAL_ACCELERATION:
				boundary = 0.3;
				break;
			case Consts.ConstsParamNames.Stroke.STROKE_MAX_ACCELERATION:
				boundary = 0.3;
				break;
			case Consts.ConstsParamNames.Stroke.STROKE_AVERAGE_VELOCITY:
				boundary = 0.22;
				break;
		}
		
		return boundary;
	}
	
	public double GetThreashold(String paramName) {
		double threashold = 0.8;
	
		switch(paramName) {
			case Consts.ConstsParamNames.Gesture.GESTURE_TOTAL_TIME_INTERVAL:
				threashold = 0.829;
			break;
			case Consts.ConstsParamNames.Gesture.GESTURE_TOTAL_AREA_MINX_MINY:
				threashold = 0.718;
			break;
			case Consts.ConstsParamNames.Gesture.GESTURE_TOTAL_AREA:
				threashold = 0.792;
			break;
			case Consts.ConstsParamNames.Stroke.STROKE_AVERAGE_VELOCITY:
				threashold = 0.755;
			break;
			case Consts.ConstsParamNames.Stroke.STROKE_LENGTH:
				threashold = 0.81;
			break;
			case Consts.ConstsParamNames.Stroke.STROKE_MAX_ACCELERATION:
				threashold = 0.621;
			break;
			case Consts.ConstsParamNames.Stroke.STROKE_MAX_RADIAL_ACCELERATION:
				threashold = 0.388;
			break;
			case Consts.ConstsParamNames.Stroke.STROKE_MAX_RADIAL_VELOCITY:
				threashold = 0.686;
			break;
			case Consts.ConstsParamNames.Stroke.STROKE_MAX_VELOCITY:
				threashold = 0.818;
			break;
			case Consts.ConstsParamNames.Stroke.STROKE_MIDDLE_PRESSURE:
				threashold = 0.945;
			break;
			case Consts.ConstsParamNames.Stroke.STROKE_MIDDLE_SURFACE:
				threashold = 0.751;
			break;
			case Consts.ConstsParamNames.Stroke.STROKE_NUM_EVENTS:
				threashold = 0.822;
			break;
			case Consts.ConstsParamNames.Stroke.STROKE_TIME_INTERVAL:
				threashold = 0.788;
			break;
			case Consts.ConstsParamNames.Stroke.STROKE_TOTAL_AREA:
				threashold = 0.794;
			break;
			case Consts.ConstsParamNames.Stroke.STROKE_TOTAL_AREA_MINX_MINY:
				threashold = 0.776;
			break;
			case Consts.ConstsParamNames.Stroke.STROKE_TRANSITION_TIME:
				threashold = 0.589;
			break;
			case Consts.ConstsParamNames.Stroke.STROKE_MID_VELOCITY:
				threashold = 0.698;
			break;
			case "DtwScore":
				threashold = 0.874;
			break;
			case "PcaScore":
				threashold = 0.857;
			break;
			case "InterestPointDensity":
				threashold = 0.25;
			break;
			case "InterestPointLocation":
				threashold = 0.918;
			break;
			case "InterestPointVelocity":
				threashold = 0.869;
			break;
			case "InterestPointAcceleration":
				threashold = 0.857;
			break;
			case "InterestPointIndex":
				threashold = 0.5;
			break;
			case "InterestPointDeltaTeta":
				threashold = 0.5;
			break;
			case "InterestPointAvgVelocity":
				threashold = 0.5;
			break;
			
		}
		
		return threashold;
	}
	
	public double GetWeight(String paramName) {
		double weight = 0.5;
	
		switch(paramName) {
			case Consts.ConstsParamNames.Gesture.GESTURE_TOTAL_TIME_INTERVAL:
				weight = 0.592;
			break;
			case Consts.ConstsParamNames.Gesture.GESTURE_TOTAL_AREA_MINX_MINY:
				weight = 0.66;
			break;
			case Consts.ConstsParamNames.Gesture.GESTURE_TOTAL_AREA:
				weight = 0.696;
			break;
			case Consts.ConstsParamNames.Stroke.STROKE_AVERAGE_VELOCITY:
				weight = 0.574;
			break;
			case Consts.ConstsParamNames.Stroke.STROKE_LENGTH:
				weight = 0.611;
			break;
			case Consts.ConstsParamNames.Stroke.STROKE_MAX_ACCELERATION:
				weight = 0.621;
			break;
			case Consts.ConstsParamNames.Stroke.STROKE_MAX_RADIAL_ACCELERATION:
				weight = 0.177;
			break;
			case Consts.ConstsParamNames.Stroke.STROKE_MAX_RADIAL_VELOCITY:
				weight = 0.209;
			break;
			case Consts.ConstsParamNames.Stroke.STROKE_MAX_VELOCITY:
				weight = 0.545;
			break;
			case Consts.ConstsParamNames.Stroke.STROKE_MIDDLE_PRESSURE:
				weight = 0.112;
			break;
			case Consts.ConstsParamNames.Stroke.STROKE_MIDDLE_SURFACE:
				weight = 0.566;
			break;
			case Consts.ConstsParamNames.Stroke.STROKE_NUM_EVENTS:
				weight = 0.552;
			break;
			case Consts.ConstsParamNames.Stroke.STROKE_TIME_INTERVAL:
				weight = 0.55;
			break;
			case Consts.ConstsParamNames.Stroke.STROKE_TOTAL_AREA:
				weight = 0.545;
			break;
			case Consts.ConstsParamNames.Stroke.STROKE_TOTAL_AREA_MINX_MINY:
				weight = 0.473;
			break;
			case Consts.ConstsParamNames.Stroke.STROKE_TRANSITION_TIME:
				weight = 0.49;
			break;
			case Consts.ConstsParamNames.Stroke.STROKE_MID_VELOCITY:
				weight = 0.46;
			break;
			case "DtwScore":
				weight = 0.858;
			break;
			case "PcaScore":
				weight = 0.839;
			break;
			case "InterestPointDensity":
				weight = 0.768;
			case "InterestPointLocation":
				weight = 0.724;
			break;
		}
		
		return 1;
	}
}
