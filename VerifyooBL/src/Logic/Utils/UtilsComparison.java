package Logic.Utils;

import java.util.ArrayList;
import java.util.Collections;
import java.util.Comparator;

import Consts.ConstsGeneral;
import Data.UserProfile.Extended.MotionEventExtended;
import Data.UserProfile.Extended.TemplateExtended;
import Data.UserProfile.Raw.Gesture;
import Data.UserProfile.Raw.MotionEventCompact;
import Data.UserProfile.Raw.Stroke;
import Data.UserProfile.Raw.Template;
import Logic.Comparison.Stats.FeatureMatrix;
import Logic.Comparison.Stats.Data.Interface.IStatEngineResult;
import Logic.Utils.DTW.DTWObjCoordinate;
import Logic.Utils.DTW.IDTWObj;
import Logic.Utils.DTW.UtilsDTW;

public class UtilsComparison {
	public double CompareNumericalValues(double value1, double value2, double threshold) {
		double percentageDiff = Utils.GetInstance().GetUtilsMath().GetPercentageDiff(value1, value2);
		
		double finalScore = 1;	
		if(percentageDiff < threshold ) {			
			finalScore = 1 - (threshold - percentageDiff);
		}
		
		return finalScore;
	}
	
	public double[][] ConvertMatrix(FeatureMatrix featureMatrix) {
		int columns = featureMatrix.MatrixFeature.size();
		int rows = featureMatrix.MatrixFeature.get(0).size();
		
		double[][] matrix = new double[columns][rows];
		
		for(int idxColumn = 0; idxColumn < columns; idxColumn++) {
			for(int idxRow = 0; idxRow < rows; idxRow++) {
				if(idxColumn < featureMatrix.MatrixFeature.size() && idxRow < featureMatrix.MatrixFeature.get(idxColumn).size()) {
					matrix[idxColumn][idxRow] = featureMatrix.MatrixFeature.get(idxColumn).get(idxRow);	
				}				
			}	
		}
		
		return matrix;		
	}
	
	public ArrayList<MotionEventExtended> ListEventsToExtended(ArrayList<MotionEventCompact> listAvgVector, double length, double xdpi, double ydpi) {
		Stroke tempStroke = new Stroke();
		tempStroke.Length = length;
		tempStroke.Xdpi = xdpi;
		tempStroke.Ydpi = ydpi;
		tempStroke.ListEvents.addAll(listAvgVector);
		
		Gesture tempGesture = new Gesture();
		tempGesture.ListStrokes.add(tempStroke);
		
		Template tempTemplate = new Template();
		tempTemplate.ListGestures.add(tempGesture);
		
		TemplateExtended tempTemplateExtended = new TemplateExtended(tempTemplate);
		
		return tempTemplateExtended.ListGestureExtended.get(0).ListStrokesExtended.get(0).ListEventsExtended;
	}
	
	public double GetTotalSpatialScore(ArrayList<IStatEngineResult> listScores) {
		Collections.sort(listScores, new Comparator<IStatEngineResult>() {
            public int compare(IStatEngineResult score1, IStatEngineResult score2) {
                if (Math.abs(score1.GetZScore()) > Math.abs(score2.GetZScore())) {
                    return -1;
                }
                if (Math.abs(score1.GetZScore()) < Math.abs(score2.GetZScore())) {
                    return 1;
                }
                return 0;
            }
        });
		
		int limit = listScores.size();		
		double result = 0;
		double totalWeights = 0;
		for(int idx = 0; idx < limit; idx++) {			
			totalWeights += listScores.get(idx).GetWeight();
			result += listScores.get(idx).GetScore() * listScores.get(idx).GetWeight();				
		}
		
		result = result / totalWeights;
		return result;
	}
	
	public ArrayList<MotionEventExtended> ListEventsCompactToExtended(ArrayList<MotionEventCompact> listEventsCompact, double xDpi, double yDpi)
	{
		ArrayList<MotionEventExtended> listEventsExtended = new ArrayList<>();
		
		MotionEventExtended tempEvent;
		MotionEventExtended tempEventPrev;
		
		for(int idxEvent = 0; idxEvent < listEventsCompact.size(); idxEvent++)
		{
			if(idxEvent > 0) {
				tempEventPrev = listEventsExtended.get(idxEvent - 1);				
			}
			else {
				tempEventPrev = null;
			}
			
			tempEvent = new MotionEventExtended(listEventsCompact.get(idxEvent), xDpi, yDpi, tempEventPrev, idxEvent);
			
			if(idxEvent == 0) {
				tempEvent.IsStartOfStroke = true;
			}
			if(idxEvent == listEventsCompact.size() - 1) {
				tempEvent.IsEndOfStroke = true;
			}
			
			listEventsExtended.add(tempEvent);
		}
		
		return listEventsExtended;
	}
	
	public int GetShiftIdx(ArrayList<MotionEventExtended> listBase, ArrayList<MotionEventExtended> listToShift) {
		int idxMinDtw = 0;
		double minDtwDistance = Double.MAX_VALUE;
		double tempDtwDistance;
		
		ArrayList<IDTWObj> baseShiftedList = TrimListEnds(listBase, ConstsGeneral.SAMPLING_MAX_SHIFT_INDEX);
		ArrayList<IDTWObj> currentShiftedList;
		
		UtilsDTW dtwCoords;
		
		for(int idxShift = 0; idxShift < ConstsGeneral.SAMPLING_MAX_SHIFT_INDEX / 2; idxShift++) {
			currentShiftedList = GetShiftedList(listToShift, idxShift);
			dtwCoords = new UtilsDTW(baseShiftedList, currentShiftedList);
			
			tempDtwDistance = dtwCoords.getDistance();
			if(tempDtwDistance < minDtwDistance) {
				idxMinDtw = idxShift;
				minDtwDistance = tempDtwDistance; 
			}
			
			currentShiftedList = GetShiftedList(listToShift, idxShift * -1);
			dtwCoords = new UtilsDTW(baseShiftedList, currentShiftedList);
			
			tempDtwDistance = dtwCoords.getDistance();
			if(tempDtwDistance < minDtwDistance) {
				idxMinDtw = idxShift;
				minDtwDistance = tempDtwDistance; 
			}
		}
		return idxMinDtw;
	}
	
	private ArrayList<IDTWObj> TrimListEnds(ArrayList<MotionEventExtended> listEvents, int samplingMaxShiftIndex) {
		ArrayList<IDTWObj> listResult = new ArrayList<>();
		for(int idx = 0; idx < listEvents.size(); idx++) {
			listResult.add(new DTWObjCoordinate(listEvents.get(idx).Xpixel, listEvents.get(idx).Ypixel));
		}		
		
		for(int idx = 0; idx < samplingMaxShiftIndex / 2; idx++) {
			listResult.remove(0);
		}
		for(int idx = 0; idx < samplingMaxShiftIndex / 2; idx++) {
			listResult.remove(listResult.size() - 1);
		}
		
		return listResult;
	}

	protected ArrayList<IDTWObj> GetShiftedList(ArrayList<MotionEventExtended> listEvents, int idxShift) {

		ArrayList<IDTWObj> listShifted = new ArrayList<>();
		
		if(idxShift >= 0) {
			int idxLast = ConstsGeneral.SAMPLING_MAX_SHIFT_INDEX - idxShift;
			for(int idxEvent = idxShift; idxEvent < listEvents.size() - idxLast; idxEvent++) {
				listShifted.add(new DTWObjCoordinate(listEvents.get(idxEvent).Xpixel, listEvents.get(idxEvent).Ypixel));
			}	
		}
		else {
			int idxLast = ConstsGeneral.SAMPLING_MAX_SHIFT_INDEX + idxShift;
			int idxStart = listEvents.size() + idxShift;
			for(int idxEvent = idxStart; idxEvent >= idxLast; idxEvent--) {
				listShifted.add(0, new DTWObjCoordinate(listEvents.get(idxEvent).Xpixel, listEvents.get(idxEvent).Ypixel));
			}	
		}
		
		return listShifted;
	}
}
