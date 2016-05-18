package Data.UserProfile.Extended;

import java.util.ArrayList;
import java.util.HashMap;

import Consts.ConstsFeatures;
import Consts.ConstsParamNames;
import Data.MetaData.VelocityAvgPoint;
import Data.MetaData.VelocityPeak;
import Data.UserProfile.Raw.Gesture;
import Data.UserProfile.Raw.Stroke;
import Logic.Utils.Utils;
import Logic.Utils.UtilsLinearReg;
import Logic.Utils.UtilsSpatialSampling;
import Logic.Utils.UtilsLinearReg.LinearRegression;
import Logic.Comparison.Stats.FeatureMeanData;
import Logic.Comparison.Stats.StatEngine;
import Logic.Comparison.Stats.Interfaces.IStatEngine;

public class GestureExtended extends Gesture {
	
	/*************** Shape Parameters ***************/
	
	public double GestureLengthMM;
	public double GestureLengthMMX;
	public double GestureLengthMMY;
	public double GestureTotalStrokeArea;
	public double[] SpatialSamplingVector;
	
	/*************** Time Parameters ***************/
	
	public double GestureTotalTimeWithPauses;
	public double GestureTotalTimeWithoutPauses;
	
	/*************** Velocity & Acceleration Parameters ***************/
	
	public double GestureAverageVelocity;
	
	public double GestureAverageStartAcceleration;
	public double GestureAverageEndAcceleration;	
	public double GestureVelocityPeakMax;
	
	public double[] AccumulatedTime;
	public double[] AccumulatedLength;
	
	public double GestureAccumulatedLengthLinearRegRSqr;
	public double GestureAccumulatedLengthLinearRegSlope;
	public double GestureAccumulatedLengthLinearRegIntercept;
	
	/*************** Pressure Parameters ***************/ 
	
	public double GestureMaxPressure;	
	public double GestureAvgPressure;
	public double GestureAvgMiddlePressure;
		
	/*************** Surface Parameters ***************/
	
	public double GestureMaxSurface;
	public double GestureAvgSurface;	
	public double GestureAvgMiddleSurface;
	
	/*************** Accelerometer Parameters ***************/ 
	
	public double GestureMaxAccX;
	public double GestureMaxAccY;
	public double GestureMaxAccZ;
	
	public double GestureAvgAccX;
	public double GestureAvgAccY;
	public double GestureAvgAccZ;	
	
	public ArrayList<StrokeExtended> ListStrokesExtended;

	protected ArrayList<MotionEventExtended> mListGestureEvents;
	protected HashMap<String, FeatureMeanData> mHashFeatureMeans;
	
	protected UtilsLinearReg mUtilsLinearReg;
	protected UtilsSpatialSampling mUtilsSpatialSampling;
	
	protected IStatEngine mStatEngine;
	
	public GestureExtended(Gesture gesture, HashMap<String, FeatureMeanData> hashFeatureMeans) {		
		Instruction = gesture.Instruction;		
		ListStrokes = gesture.ListStrokes;
		
		mUtilsLinearReg = Utils.GetInstance().GetUtilsLinearReg();
		mUtilsSpatialSampling = Utils.GetInstance().GetUtilsSpatialSampling();
		
		mHashFeatureMeans = hashFeatureMeans;
		InitParams();
		PreCalculations();	
		InitFeatures();
	}

	protected void InitParams() {
		ListStrokesExtended = new ArrayList<>();
		
		mListGestureEvents = new ArrayList<>();
		
		mStatEngine = StatEngine.GetInstance();
		
		GestureLengthMM = 0;
		GestureTotalTimeWithPauses = 0;
		GestureTotalTimeWithoutPauses = 0;
	}	

	protected void PreCalculations() {
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
	
	protected void InitFeatures() {
		CalculateListGestureEventsFeatures();
		AddCalculatedFeatures();
		CalculateSpatialSamplingVector();
		CalculateGestureTotalTimeWithPauses();
		CalculateGestureAvgVelocity();		
		CalculateAccelerationAtStart();
		CalculateAvgOfMiddlePressureAndSurface();		
		//CalculateAccelerationAtEnd();
		CalculateGestureVelocityPeaks();
		CalculateAccumulatedDistanceByTime();
		CalculateAccumulatedDistanceLinearReg();
	}
	
	protected void CalculateSpatialSamplingVector()
	{
		SpatialSamplingVector = mUtilsSpatialSampling.PrepareDataSpatialSampling(ListEvents, Length);
	}
	
	protected void CalculateAccelerationAtStart()
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
	
	protected void CalculateAccelerationAtEnd()
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
	
	protected void CalculateListGestureEventsFeatures()
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
		
		AddGestureValue(Instruction, ConstsParamNames.Gesture.GESTURE_AVG_PRESSURE, GestureAvgPressure);
		AddGestureValue(Instruction, ConstsParamNames.Gesture.GESTURE_AVG_SURFACE, GestureAvgSurface);
		
		GestureAvgAccX = totalAccX / mListGestureEvents.size(); 
		GestureAvgAccY = totalAccY / mListGestureEvents.size();
		GestureAvgAccZ = totalAccZ / mListGestureEvents.size();
	}
	
	protected void CalculateAvgOfMiddlePressureAndSurface()
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
	
	protected void CalculateGestureVelocityPeaks()
	{
		if(ListStrokesExtended.size() > 0) {
			VelocityPeak tempVelocityPeak = ListStrokesExtended.get(0).StrokeVelocityPeak;						
			GestureVelocityPeakMax = ((double) tempVelocityPeak.Index) / ((double) ListStrokesExtended.get(0).ListEventsExtended.size());	
		}				
	}
	
	protected void AddCalculatedFeatures() {
		AddGestureValue(Instruction, ConstsParamNames.Gesture.LENGTH, GestureLengthMM);		
		AddGestureValue(Instruction, ConstsParamNames.Gesture.GESTURE_TOTAL_TIME_WITHOUT_PAUSES, GestureTotalTimeWithoutPauses);
		AddGestureValue(Instruction, ConstsParamNames.Gesture.GESTURE_TOTAL_STROKE_AREA, GestureTotalStrokeArea);		
	}

	protected void CalculateGestureTotalTimeWithPauses() {
		StrokeExtended strokeFirst = ListStrokesExtended.get(0);
		StrokeExtended strokeLast = ListStrokesExtended.get(ListStrokesExtended.size() - 1);
		
		double gestureStartTime = strokeFirst.ListEventsExtended.get(0).EventTime;
		double gestureEndTime = strokeLast.ListEventsExtended.get(strokeLast.ListEventsExtended.size() - 1).EventTime;
		
		GestureTotalTimeWithPauses = gestureEndTime - gestureStartTime;
		AddGestureValue(Instruction, ConstsParamNames.Gesture.GESTURE_TOTAL_TIME_WITH_PAUSES, GestureTotalTimeWithPauses);
	}
	
	protected void CalculateGestureAvgVelocity()
	{
		GestureAverageVelocity = GestureLengthMM / GestureTotalTimeWithoutPauses;
		AddGestureValue(Instruction, ConstsParamNames.Gesture.AVERAGE_VELOCITY, GestureAverageVelocity);
	}
	
	protected void CalculateAccumulatedDistanceByTime()
	{	
		double[] accumulatedLength = new double[mListGestureEvents.size()];
		double[] accumulatedTime = new double[mListGestureEvents.size()];
		
		double deltaX;
		double deltaY;
		double distance = 0;
		double timeDiff = 0;
		
		int idxAccumulated = 0;	
		double tempLength = 0;
		double tempTime = 0;
		
		double prevLength = 0;
		double prevTime = 0;
		
		for(int idxEvent = 1; idxEvent < mListGestureEvents.size(); idxEvent++) {				
			deltaX = mListGestureEvents.get(idxEvent).Xmm - mListGestureEvents.get(idxEvent - 1).Xmm; 
			deltaY = mListGestureEvents.get(idxEvent).Ymm - mListGestureEvents.get(idxEvent - 1).Ymm;
			distance = Utils.GetInstance().GetUtilsMath().CalcPitagoras(deltaX, deltaY);
			timeDiff = mListGestureEvents.get(idxEvent).EventTime - mListGestureEvents.get(idxEvent - 1).EventTime; 
						
			if(!mListGestureEvents.get(idxEvent).IsStartOfStroke) {
				if(idxAccumulated > 0) {
					prevLength = accumulatedLength[idxAccumulated - 1];
					prevTime = accumulatedTime[idxAccumulated - 1];					
				}
				else {
					prevLength = 0;
					prevTime = 0;
				}
				
				tempLength = prevLength + distance;
				tempTime = prevTime + timeDiff;
				
				accumulatedTime[idxAccumulated] = tempTime;
				accumulatedLength[idxAccumulated] = tempLength;	
				idxAccumulated++;							
			}					
		}			
		
		AccumulatedLength = new double[idxAccumulated];
		AccumulatedTime = new double[idxAccumulated];
		
		for(int idx = 0; idx < idxAccumulated; idx++) {
			AccumulatedLength[idx] = accumulatedLength[idx];
			AccumulatedTime[idx] = accumulatedTime[idx];
		}
	}
	
	protected void CalculateAccumulatedDistanceLinearReg()
	{
		mUtilsLinearReg.CalcLinearReg(AccumulatedTime, AccumulatedLength);
		LinearRegression linearRegObj = mUtilsLinearReg.GetLinearRegObj();
		GestureAccumulatedLengthLinearRegRSqr = linearRegObj.R2();
		GestureAccumulatedLengthLinearRegSlope = linearRegObj.slope();
		GestureAccumulatedLengthLinearRegIntercept = linearRegObj.intercept();
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
