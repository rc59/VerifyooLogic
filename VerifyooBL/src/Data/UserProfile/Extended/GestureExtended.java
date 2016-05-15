package Data.UserProfile.Extended;

import java.util.ArrayList;
import java.util.HashMap;

import Consts.ConstsFeatures;
import Consts.ConstsParamNames;
import Data.MetaData.VelocityAvgPoint;
import Data.MetaData.VelocityPeak;
import Data.UserProfile.Raw.Gesture;
import Data.UserProfile.Raw.Stroke;
import Logic.Calc.Utils;
import Logic.Comparison.Stats.FeatureMeanData;
import Logic.Comparison.Stats.StatEngine;
import Logic.Comparison.Stats.Interfaces.IStatEngine;

public class GestureExtended extends Gesture {
	public double GestureLengthMM;
	public double GestureTotalTimeWithPauses;
	public double GestureTotalTimeWithoutPauses;
	public double GestureAverageVelocity;
	public double GestureAverageStartAcceleration;
	public double GestureAverageEndAcceleration;
	public double GestureTotalStrokeArea;
	
	public double GestureMaxPressure;
	public double GestureMaxSurface;	
	public double GestureAvgPressure;
	public double GestureAvgSurface;	
	public double GestureAvgMiddlePressure;
	public double GestureAvgMiddleSurface;
	
	public double GestureMaxAccX;
	public double GestureMaxAccY;
	public double GestureMaxAccZ;
	
	public double GestureAvgAccX;
	public double GestureAvgAccY;
	public double GestureAvgAccZ;
	
	public double GestureVelocityPeakMax;
		
	public ArrayList<StrokeExtended> ListStrokesExtended;

	protected ArrayList<MotionEventExtended> mListGestureEvents;
	private HashMap<String, FeatureMeanData> mHashFeatureMeans;
	
	protected IStatEngine mStatEngine;
	
	public GestureExtended(Gesture gesture, HashMap<String, FeatureMeanData> hashFeatureMeans) {		
		Instruction = gesture.Instruction;		
		ListStrokes = gesture.ListStrokes;
		
		mHashFeatureMeans = hashFeatureMeans;
		InitParams();
		PreCalculations();	
		InitFeatures();
	}

	private void InitParams() {
		ListStrokesExtended = new ArrayList<>();
		
		mListGestureEvents = new ArrayList<>();
		
		mStatEngine = StatEngine.GetInstance();
		
		GestureLengthMM = 0;
		GestureTotalTimeWithPauses = 0;
		GestureTotalTimeWithoutPauses = 0;
	}	

	private void PreCalculations() {
		Stroke tempStroke;		
		StrokeExtended tempStrokeExtended;
		
		for(int idxStroke = 0; idxStroke < ListStrokes.size(); idxStroke++) {
			tempStroke = ListStrokes.get(idxStroke);				
			tempStrokeExtended = new StrokeExtended(tempStroke, mHashFeatureMeans, Instruction, idxStroke);
			
			ListStrokesExtended.add(tempStrokeExtended);
			GestureLengthMM += tempStrokeExtended.StrokePropertiesObj.LengthMM;
			GestureTotalTimeWithoutPauses += tempStrokeExtended.StrokeTimeInterval;
			GestureTotalStrokeArea += tempStrokeExtended.ShapeDataObj.ShapeArea;
			
			GestureMaxPressure = Utils.GetInstance().GetUtilsMath().GetMaxValue(GestureMaxPressure, tempStrokeExtended.MaxPressure);
			GestureMaxSurface = Utils.GetInstance().GetUtilsMath().GetMaxValue(GestureMaxSurface, tempStrokeExtended.MaxSurface);	
			
			mListGestureEvents.addAll(tempStrokeExtended.ListEventsExtended);
		}		
	}
	
	private void InitFeatures() {
		CalculateListGestureEventsFeatures();
		AddCalculatedFeatures();
		CalculateGestureTotalTimeWithPauses();
		CalculateGestureAvgVelocity();		
		CalculateAccelerationAtStart();
		CalculateAvgOfMiddlePressureAndSurface();		
		//CalculateAccelerationAtEnd();
		CalculateGestureVelocityPeaks();
	}
	
	private void CalculateAccelerationAtStart()
	{
		if(mListGestureEvents.size() >= (ConstsFeatures.ACC_CALC_MIN_NUM_EVENTS * 2)) {
			MotionEventExtended eventPrev;
			MotionEventExtended eventCurr;
			
			double deltaTimeInterval;		
			double deltaVelocity;
			
			double tempAcc;
			double totalAcc = 0;int count = 0;
			
			for(int idxEvent = 1; idxEvent < ConstsFeatures.ACC_CALC_MIN_NUM_EVENTS; idxEvent++) {							
				eventPrev = mListGestureEvents.get(idxEvent - 1);
				eventCurr = mListGestureEvents.get(idxEvent);
				
				deltaVelocity = eventCurr.Velocity - eventPrev.Velocity;
				deltaTimeInterval = eventCurr.EventTime - eventPrev.EventTime;
				
				if(deltaTimeInterval > 0) {
					tempAcc = deltaVelocity / deltaTimeInterval;	
				}
				else {
					tempAcc = 0;
				}
				
				totalAcc += tempAcc;
			}
			
			GestureAverageStartAcceleration = totalAcc / (ConstsFeatures.ACC_CALC_MIN_NUM_EVENTS - 1); 
		}		
	}
	
	private void CalculateAccelerationAtEnd()
	{
		if(mListGestureEvents.size() >= (2 * ConstsFeatures.ACC_CALC_MIN_NUM_EVENTS)) {
			MotionEventExtended eventPrev;
			MotionEventExtended eventCurr;
			
			double deltaTimeInterval;		
			double deltaVelocity;
			
			double tempAcc;
			double totalAcc = 0;
			
			int startIdx = mListGestureEvents.size() - ConstsFeatures.ACC_CALC_MIN_NUM_EVENTS + 1;	
			
			for(int idxEvent = startIdx; idxEvent < mListGestureEvents.size(); idxEvent++) {				
				eventPrev = mListGestureEvents.get(idxEvent - 1);
				eventCurr = mListGestureEvents.get(idxEvent);
				
				deltaVelocity = eventCurr.Velocity - eventPrev.Velocity;
				deltaTimeInterval = eventCurr.EventTime - eventPrev.EventTime;
				
				if(deltaTimeInterval > 0) {
					tempAcc = deltaVelocity / deltaTimeInterval;	
				}
				else {
					tempAcc = 0;
				}
				
				totalAcc += tempAcc;
			}
			
			GestureAverageEndAcceleration = totalAcc / (ConstsFeatures.ACC_CALC_MIN_NUM_EVENTS - 1);
		}		
	}
	
	private void CalculateListGestureEventsFeatures()
	{
		double totalPressure = 0;
		double totalSurface = 0;
		double totalAccX = 0;
		double totalAccY = 0;
		double totalAccZ = 0;
		
		for(int idxEvent = 0; idxEvent < mListGestureEvents.size(); idxEvent++) {
			totalPressure += mListGestureEvents.get(idxEvent).Pressure;
			totalSurface += mListGestureEvents.get(idxEvent).TouchSurface;
			
			totalAccX += mListGestureEvents.get(idxEvent).AccelerometerX;
			totalAccY += mListGestureEvents.get(idxEvent).AccelerometerY;
			totalAccZ += mListGestureEvents.get(idxEvent).AccelerometerZ;
			
			GestureMaxAccX = Utils.GetInstance().GetUtilsMath().GetMaxValue(GestureMaxAccX, mListGestureEvents.get(idxEvent).AccelerometerX);
			GestureMaxAccY = Utils.GetInstance().GetUtilsMath().GetMaxValue(GestureMaxAccY, mListGestureEvents.get(idxEvent).AccelerometerY);
			GestureMaxAccZ = Utils.GetInstance().GetUtilsMath().GetMaxValue(GestureMaxAccZ, mListGestureEvents.get(idxEvent).AccelerometerZ);
		}
		
		GestureAvgPressure = totalPressure / mListGestureEvents.size();
		GestureAvgSurface = totalSurface / mListGestureEvents.size();
		
		GestureAvgAccX = totalAccX / mListGestureEvents.size(); 
		GestureAvgAccY = totalAccY / mListGestureEvents.size();
		GestureAvgAccZ = totalAccZ / mListGestureEvents.size();
	}
	
	private void CalculateAvgOfMiddlePressureAndSurface()
	{
		double totalMiddlePressure = 0;
		double totalMiddleSurface = 0;
		
		for(int idxStroke = 0; idxStroke < ListStrokesExtended.size(); idxStroke++) {
			totalMiddlePressure += ListStrokesExtended.get(idxStroke).MiddlePressure;
			totalMiddleSurface += ListStrokesExtended.get(idxStroke).MiddleSurface;
		}
		
		GestureAvgMiddlePressure = totalMiddlePressure / ListStrokesExtended.size();
		GestureAvgMiddleSurface = totalMiddleSurface / ListStrokesExtended.size();
	}
	
	private void CalculateGestureVelocityPeaks()
	{
		if(ListStrokesExtended.size() > 0) {
			VelocityPeak tempVelocityPeak = ListStrokesExtended.get(0).StrokeVelocityPeak;
			
			for(int idxStroke = 1; idxStroke < ListStrokesExtended.size(); idxStroke++) {
				if(tempVelocityPeak.Velocity < ListStrokesExtended.get(idxStroke).StrokeVelocityPeak.Velocity) {
					tempVelocityPeak = ListStrokesExtended.get(idxStroke).StrokeVelocityPeak;
				}
			}
						
			GestureVelocityPeakMax = ((double) tempVelocityPeak.Index) / ((double) mListGestureEvents.size());	
		}				
	}
	
	private void AddCalculatedFeatures() {
		AddGestureValue(Instruction, ConstsParamNames.Gesture.LENGTH, GestureLengthMM);		
		AddGestureValue(Instruction, ConstsParamNames.Gesture.GESTURE_TOTAL_TIME_WITHOUT_PAUSES, GestureTotalTimeWithoutPauses);
		AddGestureValue(Instruction, ConstsParamNames.Gesture.GESTURE_TOTAL_STROKE_AREA, GestureTotalStrokeArea);		
	}

	private void CalculateGestureTotalTimeWithPauses() {
		StrokeExtended strokeFirst = ListStrokesExtended.get(0);
		StrokeExtended strokeLast = ListStrokesExtended.get(ListStrokesExtended.size() - 1);
		
		double gestureStartTime = strokeFirst.ListEventsExtended.get(0).EventTime;
		double gestureEndTime = strokeLast.ListEventsExtended.get(strokeLast.ListEventsExtended.size() - 1).EventTime;
		
		GestureTotalTimeWithPauses = gestureEndTime - gestureStartTime;
		AddGestureValue(Instruction, ConstsParamNames.Gesture.GESTURE_TOTAL_TIME_WITH_PAUSES, GestureTotalTimeWithPauses);
	}
	
	private void CalculateGestureAvgVelocity()
	{
		GestureAverageVelocity = GestureLengthMM / GestureTotalTimeWithoutPauses;
		AddGestureValue(Instruction, ConstsParamNames.Gesture.AVERAGE_VELOCITY, GestureAverageVelocity);
	}
	
	protected String GenerateGestureFeatureMeanKey(String instruction, String paramName)
	{
		String key = String.format("%s-%s", instruction, paramName);
		return key;
	}
	
	public void AddGestureValue(String instruction, String paramName, double value)
	{
		String key = GenerateGestureFeatureMeanKey(instruction, paramName);
		
		FeatureMeanData tempFeatureMeanData;
		
		if(mHashFeatureMeans.containsKey(key)) {
			tempFeatureMeanData = mHashFeatureMeans.get(key);
		}
		else {
			tempFeatureMeanData = new FeatureMeanData(paramName);			
			mHashFeatureMeans.put(key, tempFeatureMeanData);
		}
		
		tempFeatureMeanData.AddValue(value);		
	}
	
	public HashMap<String, FeatureMeanData> GetFeatureMeansHash() 
	{
		return mHashFeatureMeans;
	}
}
