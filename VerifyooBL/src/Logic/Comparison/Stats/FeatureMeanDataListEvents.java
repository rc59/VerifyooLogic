package Logic.Comparison.Stats;

import java.util.ArrayList;

import Consts.ConstsGeneral;
import Consts.ConstsParamNames;
import Data.UserProfile.Extended.MotionEventExtended;
import Data.UserProfile.Extended.StrokeExtended;
import Data.UserProfile.Extended.TemplateExtended;
import Data.UserProfile.Raw.Gesture;
import Data.UserProfile.Raw.MotionEventCompact;
import Data.UserProfile.Raw.Stroke;
import Data.UserProfile.Raw.Template;
import Logic.Comparison.Stats.Abstract.FeatureMeanDataAbstract;
import Logic.Comparison.Stats.Interfaces.IFeatureMeanData;
import Logic.Utils.Utils;
import Logic.Utils.DTW.DTWObjCoordinate;
import Logic.Utils.DTW.IDTWObj;
import Logic.Utils.DTW.UtilsDTW;

public class FeatureMeanDataListEvents implements IFeatureMeanData {	
	String mSamplingType;	
	ArrayList<ArrayList<MotionEventExtended>> mListOfListEvents;
	double Xdpi, Ydpi;
	String mInstruction;
	int mIdxStroke;	
	
	public FeatureMeanDataListEvents(String name, String instruction, int idxStroke, double xdpi, double ydpi) {
		super();
		mSamplingType = name;		
		Xdpi = xdpi;
		Ydpi = ydpi;
		mInstruction = instruction;
		mIdxStroke = idxStroke;
		mListOfListEvents = new ArrayList<ArrayList<MotionEventExtended>>();
	}

	public ArrayList<ArrayList<MotionEventExtended>> GetListOfListEvents() {
		return mListOfListEvents;
	}
	
	public double GetMinDtwDistance(ArrayList<MotionEventExtended> listAuth, String param) {
		double result = 0;
		
		ArrayList<MotionEventExtended> listCurrent;
		
		double minDistance = Double.MAX_VALUE;
		double numComparisons = mListOfListEvents.size() - 1;
		
		for(int idxList = 0; idxList < mListOfListEvents.size(); idxList++) {
			listCurrent = mListOfListEvents.get(idxList);
			minDistance = Utils.GetInstance().GetUtilsMath().GetMinValue(GetDtwScore(listCurrent, listAuth, param), minDistance);
		}
					
		return minDistance;
	}
	
	public double GetAvgDtwDistance(ArrayList<MotionEventExtended> listAuth, String param) {
		double result = 0;
		
		ArrayList<MotionEventExtended> listCurrent;
		
		double dtwTotal = 0;
		double numComparisons = mListOfListEvents.size() - 1;
		
		for(int idxList = 0; idxList < mListOfListEvents.size(); idxList++) {
			listCurrent = mListOfListEvents.get(idxList);
			dtwTotal += GetDtwScore(listCurrent, listAuth, param);
		}
		
		result = dtwTotal / numComparisons;		
		return result;
	}
	
	public double GetUserAvgDtwDistance(String param) {
		double result = 0;
		
		ArrayList<MotionEventExtended> listBase = mListOfListEvents.get(0); 
		ArrayList<MotionEventExtended> listCurrent;
		
		double dtwTotal = 0;
		double numComparisons = mListOfListEvents.size() - 1;
		
		for(int idxList = 1; idxList < mListOfListEvents.size(); idxList++) {
			listCurrent = mListOfListEvents.get(idxList);
			dtwTotal += GetDtwScore(listCurrent, listBase, param);
		}
		
		result = dtwTotal / numComparisons;		
		return result;
	}
	
	private double GetDtwScore(ArrayList<MotionEventExtended> listCurrent, ArrayList<MotionEventExtended> listBase, String param) {
		ArrayList<IDTWObj> listDtwCurrent = StatEngine.GetInstance().GetSpatialVector(mInstruction, param, mIdxStroke, listCurrent, mSamplingType);
		ArrayList<IDTWObj> listDtwBase = StatEngine.GetInstance().GetSpatialVector(mInstruction, param, mIdxStroke, listBase, mSamplingType);
		
		UtilsDTW dtwVelocities = new UtilsDTW(listDtwCurrent, listDtwBase);
		double distance = dtwVelocities.getDistance();
		
		return distance;
	}

	public void AddValue(ArrayList<MotionEventExtended> listEventsExtended) {
		mListOfListEvents.add(listEventsExtended);
	}
	
	public ArrayList<MotionEventExtended> GetAvgVector() {
		ArrayList<MotionEventCompact> listAvgVector = new ArrayList<>();
		
		double[] vectorX, vectorY, pressure, surface, time;
		for(int idx = 0; idx < mListOfListEvents.size(); idx++) {
			vectorX = Utils.GetInstance().GetUtilsVectors().GetVectorXpixelExtended(mListOfListEvents.get(idx));
			vectorY = Utils.GetInstance().GetUtilsVectors().GetVectorYpixelExtended(mListOfListEvents.get(idx));
			time = Utils.GetInstance().GetUtilsVectors().GetVectorTime(mListOfListEvents.get(idx));
			pressure = Utils.GetInstance().GetUtilsVectors().GetVectorPressure(mListOfListEvents.get(idx));
			surface = Utils.GetInstance().GetUtilsVectors().GetVectorSurface(mListOfListEvents.get(idx));
		}
		
		ArrayList<MotionEventExtended> listBase = mListOfListEvents.get(0);
		ArrayList<Integer> listShiftIdxs = new ArrayList<>();
		int currentShiftIdx;	
		for(int idxList = 1; idxList < mListOfListEvents.size(); idxList++) {
			currentShiftIdx = Utils.GetInstance().GetUtilsComparison().GetShiftIdx(listBase, mListOfListEvents.get(idxList));
			listShiftIdxs.add(currentShiftIdx);
		}
		
		MotionEventExtended tempEvent = new MotionEventExtended();
		MotionEventCompact tempAvgEvent = new MotionEventCompact();
		int idxEventShifted;		
		
		for(int idxEvent = 0; idxEvent < ConstsGeneral.SPATIAL_SAMPLING_SIZE; idxEvent++) {			
			listAvgVector.add(listBase.get(idxEvent));
		}
		
		for(int idxEvent = 0; idxEvent < ConstsGeneral.SPATIAL_SAMPLING_SIZE; idxEvent++) 
		{		
			tempEvent = listBase.get(idxEvent);
			tempAvgEvent = listAvgVector.get(idxEvent);
			for(int idxShiftIdx = 0; idxShiftIdx < listShiftIdxs.size(); idxShiftIdx++) {
				currentShiftIdx = listShiftIdxs.get(idxShiftIdx);
				idxEventShifted = idxEvent + currentShiftIdx;		
				
				if(idxEventShifted >= currentShiftIdx && idxEventShifted >= 0 && idxEventShifted < mListOfListEvents.get(idxShiftIdx).size()) {
					tempAvgEvent = AggregateEvents(tempEvent, mListOfListEvents.get(idxShiftIdx).get(idxEventShifted));
				}
				else {
					tempAvgEvent = AggregateEvents(tempEvent, tempEvent);
				}
			}
		}

		for(int idxEvent = 0; idxEvent < listAvgVector.size(); idxEvent++) {
			GetAverageEvent(listAvgVector.get(idxEvent), listShiftIdxs.size() + 1);  
		}		
		
		double length = 0;
		for(int idxEvent = 0; idxEvent < listAvgVector.size() - 1; idxEvent++) {
			length += Utils.GetInstance().GetUtilsMath().CalcDistanceInPixels(listAvgVector.get(idxEvent), listAvgVector.get(idxEvent + 1));
		}
		
		vectorX = Utils.GetInstance().GetUtilsVectors().GetVectorXpixel(listAvgVector);
		vectorY = Utils.GetInstance().GetUtilsVectors().GetVectorYpixel(listAvgVector);
						
		ArrayList<MotionEventExtended> listAvgVectorExtended = Utils.GetInstance().GetUtilsComparison().ListEventsToExtended(listAvgVector, length, Xdpi, Ydpi);
		
		return listAvgVectorExtended;
	}
	
	protected void GetAverageEvent(MotionEventCompact tempEvent, int numVectors) {
		tempEvent.EventTime /= numVectors;
		tempEvent.Pressure /= numVectors;
		tempEvent.TouchSurface /= numVectors;
		tempEvent.Xpixel /= numVectors;
		tempEvent.Ypixel /= numVectors;
	}
	
	protected MotionEventCompact AggregateEvents(MotionEventExtended event1, MotionEventExtended event2) {
		MotionEventCompact avgEvent = new MotionEventCompact();
		
		avgEvent.EventTime = event1.EventTime + event2.EventTime;
		avgEvent.EventTime = event1.Pressure + event2.Pressure;
		avgEvent.EventTime = event1.TouchSurface + event2.TouchSurface;
		avgEvent.EventTime = event1.Xpixel + event2.Xpixel;
		avgEvent.EventTime = event1.Ypixel + event2.Ypixel;
		
		return avgEvent;
	}	
	
	@Override
	public double GetMean() {
		// TODO Auto-generated method stub
		return 0;
	}

	@Override
	public double GetInternalSd() {
		// TODO Auto-generated method stub
		return 0;
	}
	
	@Override
	public void AddValue(double value) {
		// TODO Auto-generated method stub
		
	}

	@Override
	public double GetPopulationZScore() {
		// TODO Auto-generated method stub
		return 0;
	}

	@Override
	public String GetParamName() {
		// TODO Auto-generated method stub
		return null;
	}

	@Override
	public String GetInstruction() {
		// TODO Auto-generated method stub
		return null;
	}

}
