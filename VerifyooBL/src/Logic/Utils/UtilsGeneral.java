package Logic.Utils;

import java.util.ArrayList;
import java.util.HashMap;

import Data.MetaData.NormStroke;
import Data.UserProfile.Extended.GestureExtended;
import Data.UserProfile.Extended.MotionEventExtended;
import Data.UserProfile.Extended.StrokeExtended;
import Logic.Utils.DTW.DTWObjCoordinate;
import Logic.Utils.DTW.IDTWObj;

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
	
	public ArrayList<IDTWObj> ConvertVectorToDTWObj(double[] vector) {
		ArrayList<IDTWObj> listDTWCoords = new ArrayList<>();		
		
		IDTWObj tempObj;
		for(int idx = 0; idx < vector.length; idx+=2) {
			tempObj = new DTWObjCoordinate(vector[idx], vector[idx + 1]);
			listDTWCoords.add(tempObj);
		}
		
		return listDTWCoords;
	}
	
	public ArrayList<IDTWObj> ConvertStrokeToDTWObj(StrokeExtended stroke) {
		ArrayList<IDTWObj> listDTWCoords = new ArrayList<>();		
		
		IDTWObj tempObj;
		for(int idx = 0; idx < stroke.ListEventsExtended.size(); idx++) {
			tempObj = new DTWObjCoordinate(stroke.ListEventsExtended.get(idx).Xnormalized, stroke.ListEventsExtended.get(idx).Ynormalized);
			listDTWCoords.add(tempObj);
		}
		
		return listDTWCoords;
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
			case Consts.ConstsParamNames.Gesture.GESTURE_TOTAL_TIME_INTERVAL:
				boundary = 0.22;
				break;			
			case Consts.ConstsParamNames.Gesture.GESTURE_TOTAL_AREA_MINX_MINY:
				boundary = 0.3;
				break;
			case Consts.ConstsParamNames.Gesture.GESTURE_TOTAL_AREA:
				boundary = 0.3;
				break;
			case Consts.ConstsParamNames.Stroke.STROKE_LENGTH:
				boundary = 0.25;
				break;
			case Consts.ConstsParamNames.Stroke.STROKE_AVERAGE_VELOCITY:
				boundary = 0.25;
				break;
			case Consts.ConstsParamNames.Stroke.STROKE_MAX_VELOCITY:
				boundary = 0.22;
				break;
			case Consts.ConstsParamNames.Stroke.STROKE_MID_VELOCITY:
				boundary = 0.35;
				break;
			case Consts.ConstsParamNames.Stroke.STROKE_MAX_ACCELERATION:
				boundary = 0.35;
				break;
			case Consts.ConstsParamNames.Stroke.STROKE_AVERAGE_ACCELERATION:
				boundary = 0.35;
				break;
			case Consts.ConstsParamNames.Stroke.STROKE_NUM_EVENTS:
				boundary = 0.2;
				break;
			case Consts.ConstsParamNames.Stroke.STROKE_TIME_INTERVAL:
				boundary = 0.23;
				break;			
			case Consts.ConstsParamNames.Stroke.STROKE_TOTAL_AREA_MINX_MINY:
				boundary = 0.30;
				break;
			case Consts.ConstsParamNames.Stroke.STROKE_TOTAL_AREA:
				boundary = 0.30;
				break;
			case Consts.ConstsParamNames.Stroke.STROKE_INTEREST_POINT_PARAM:
				boundary = 0.35;
				break;
			case Consts.ConstsParamNames.Stroke.STROKE_MIDDLE_SURFACE:
				boundary = 0.14;
				break;
			case Consts.ConstsParamNames.Stroke.STROKE_MIDDLE_PRESSURE:
				boundary = 0.9;
				break;			
			case Consts.ConstsParamNames.Stroke.STROKE_MAX_RADIAL_VELOCITY:
				boundary = 0.28;
				break;
			case Consts.ConstsParamNames.Stroke.STROKE_MAX_RADIAL_ACCELERATION:
				boundary = 0.3;
				break;			
		}
		
		return boundary;
	}
	
	public double GetThreashold(String paramName) {
		double threashold = 0.8;
	
		switch(paramName) {
			case Consts.ConstsParamNames.Gesture.GESTURE_TOTAL_TIME_INTERVAL:
				threashold = 0.87;
			break;
			case Consts.ConstsParamNames.Gesture.GESTURE_TOTAL_AREA_MINX_MINY:
				threashold = 0.62;
			break;
			case Consts.ConstsParamNames.Gesture.GESTURE_TOTAL_AREA:
				threashold = 0.66;
			break;
			case Consts.ConstsParamNames.Stroke.STROKE_LENGTH:
				threashold = 0.77;
			break;
			case Consts.ConstsParamNames.Stroke.STROKE_AVERAGE_VELOCITY:
				threashold = 0.71;
			break;
			case Consts.ConstsParamNames.Stroke.STROKE_MAX_VELOCITY:
				threashold = 0.75;
			break;
			case Consts.ConstsParamNames.Stroke.STROKE_MID_VELOCITY:
				threashold = 0.78;
			break;
			case Consts.ConstsParamNames.Stroke.STROKE_MAX_ACCELERATION:
				threashold = 0.68;
			break;
			case Consts.ConstsParamNames.Stroke.STROKE_AVERAGE_ACCELERATION:
				threashold = 0.68;
			break;
			case Consts.ConstsParamNames.Stroke.STROKE_TIME_INTERVAL:
				threashold = 0.85;
			break;
			case Consts.ConstsParamNames.Stroke.STROKE_NUM_EVENTS:
				threashold = 0.77;
			break;
			case Consts.ConstsParamNames.Stroke.STROKE_TOTAL_AREA:
				threashold = 0.75;
			break;
			case Consts.ConstsParamNames.Stroke.STROKE_TOTAL_AREA_MINX_MINY:
				threashold = 0.758;
			break;
			case Consts.ConstsParamNames.Stroke.STROKE_MIDDLE_SURFACE:
				threashold = 0.876;
			break;
			case Consts.ConstsParamNames.Stroke.STROKE_MIDDLE_PRESSURE:
				threashold = 0.95;
			break;			
			case Consts.ConstsParamNames.Stroke.STROKE_MAX_RADIAL_ACCELERATION:
				threashold = 0.388;
			break;
			case Consts.ConstsParamNames.Stroke.STROKE_MAX_RADIAL_VELOCITY:
				threashold = 0.55;
			break;						
			case Consts.ConstsParamNames.Stroke.STROKE_TRANSITION_TIME:
				threashold = 0.47;
			break;			
			case "DtwSpatialVelocity":
				threashold = 1;
			break;
			case "DtwScore":
				threashold = 0.845;
			break;
			case "PcaScore":
				threashold = 0.66;
			break;
			case "InterestPoints":
				threashold = 0.71;
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
				threashold = 0.001;
			break;
			case "InterestPointAvgVelocity":
				threashold = 0.04;
			break;
			
		}
		
		return threashold;
	}
	
	public double GetWeight(String paramName) {
		double weight = 1;
	
		switch(paramName) {
			case Consts.ConstsParamNames.Gesture.GESTURE_TOTAL_TIME_INTERVAL:
				weight = 3;
			break;
			case Consts.ConstsParamNames.Gesture.GESTURE_TOTAL_AREA_MINX_MINY:
				weight = 3;
			break;
			case Consts.ConstsParamNames.Gesture.GESTURE_TOTAL_AREA:
				weight = 3;
			break;
			case Consts.ConstsParamNames.Stroke.STROKE_AVERAGE_VELOCITY:
				weight = 3;
			break;
			case Consts.ConstsParamNames.Stroke.STROKE_LENGTH:
				weight = 3;
			break;
			case Consts.ConstsParamNames.Stroke.STROKE_MAX_ACCELERATION:
				weight = 3;
			break;
			case Consts.ConstsParamNames.Stroke.STROKE_MAX_RADIAL_ACCELERATION:
				weight = 1;
			break;
			case Consts.ConstsParamNames.Stroke.STROKE_MAX_RADIAL_VELOCITY:
				weight = 1;
			break;
			case Consts.ConstsParamNames.Stroke.STROKE_MAX_VELOCITY:
				weight = 3;
			break;
			case Consts.ConstsParamNames.Stroke.STROKE_MIDDLE_PRESSURE:
				weight = 1;
			break;
			case Consts.ConstsParamNames.Stroke.STROKE_MIDDLE_SURFACE:
				weight = 3;
			break;
			case Consts.ConstsParamNames.Stroke.STROKE_NUM_EVENTS:
				weight = 3;
			break;
			case Consts.ConstsParamNames.Stroke.STROKE_TIME_INTERVAL:
				weight = 3;
			break;
			case Consts.ConstsParamNames.Stroke.STROKE_TOTAL_AREA:
				weight = 3;
			break;
			case Consts.ConstsParamNames.Stroke.STROKE_TOTAL_AREA_MINX_MINY:
				weight = 2;
			break;
			case Consts.ConstsParamNames.Stroke.STROKE_TRANSITION_TIME:
				weight = 3;
			break;
			case Consts.ConstsParamNames.Stroke.STROKE_MID_VELOCITY:
				weight = 2;
			break;
			case "DtwScore":
				weight = 3;
			break;
			case "PcaScore":
				weight = 3;
			break;
			case "InterestPointDensity":
				weight = 0.768;
			case "InterestPointLocation":
				weight = 0.724;
			break;
			case "InterestPointIndex":
				weight = 2;
			break;
			case "InterestPointDeltaTeta":
				weight = 2;
			break;
			case "InterestPointVelocity":
				weight = 2;
			break;			
		}
		
		return 1;
	}
	
	public String NormStrokeListToString(ArrayList<NormStroke> listNormStrokes) {
		StringBuilder output = new StringBuilder();
		
		for(int idx = 0; idx < listNormStrokes.size(); idx++) {
			output.append(listNormStrokes.get(idx).ToString());
			if(idx + 1 < listNormStrokes.size()) {
				output.append(";");	
			}			
		}
		
		return output.toString();
	}
	
	public ArrayList<NormStroke> NormStrokeListFromString(String input) {
		ArrayList<NormStroke> listNormStrokes = new ArrayList<>();
		
		NormStroke tempNormStroke;
		String[] listStrNormStrokes = input.split(";");
		for(int idx = 0; idx < listStrNormStrokes.length; idx++) {
			tempNormStroke = new NormStroke();
			tempNormStroke.FromString(listStrNormStrokes[idx]);
			listNormStrokes.add(tempNormStroke);
		}
		
		return listNormStrokes;
	}
}
