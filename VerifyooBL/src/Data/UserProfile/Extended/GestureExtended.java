package Data.UserProfile.Extended;

import java.util.ArrayList;
import java.util.HashMap;

import Consts.ConstsFeatures;
import Consts.ConstsParamNames;
import Data.MetaData.ParameterAvgPoint;
import Data.MetaData.IndexValue;
import Data.UserProfile.Raw.Gesture;
import Data.UserProfile.Raw.MotionEventCompact;
import Data.UserProfile.Raw.Stroke;
import Logic.Utils.Utils;
import Logic.Utils.UtilsGeneral;
import Logic.Utils.UtilsLinearReg;
import Logic.Utils.UtilsSpatialSampling;
import Logic.Utils.UtilsLinearReg.LinearRegression;
import Logic.Utils.UtilsMath;
import Logic.Comparison.Stats.FeatureMeanData;
import Logic.Comparison.Stats.FeatureMeanDataAngle;
import Logic.Comparison.Stats.StatEngine;
import Logic.Comparison.Stats.Interfaces.IFeatureMeanData;
import Logic.Comparison.Stats.Interfaces.IStatEngine;

public class GestureExtended extends Gesture {
	
	/*************** Shape Parameters ***************/
	
	public double GestureLengthPixel;
	public double GestureLengthMM;
	public double GestureLengthMMX;
	public double GestureLengthMMY;
	public double GestureTotalStrokeArea;
	public double[] SpatialSamplingVector;
	
	public double GestureStartDirection;
	public double GestureMaxDirection;
	public double GestureEndDirection;
	
	/*************** Time Parameters ***************/
	
	public double GestureTotalTimeInterval;
	public double GestureTotalStrokeTimeInterval;
	
	/*************** Velocity & Acceleration Parameters ***************/
	
	public double GestureMaxVelocity;
	public double GestureAverageVelocity;
	
	public double GestureMaxAcceleration;
	public double GestureAverageAcceleration;
	
	public double GestureAverageStartAcceleration;
	public double GestureAverageEndAcceleration;	
	public double GestureVelocityPeakMax;
	public double GestureAccelerationPeakMax;
	public double GestureVelocityPeakIntervalPercentage;
	public double GestureAccelerationPeakIntervalPercentage;
	
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
	
	/*************** General Parameters ***************/ 
	
	public int GestureIndex;
	
	public ArrayList<StrokeExtended> ListStrokesExtended;
	public ArrayList<MotionEventExtended> ListGestureEventsExtended;
	public ArrayList<MotionEventCompact> ListGestureEvents;
	
	protected HashMap<String, IFeatureMeanData> mHashFeatureMeans;
	
	protected double[] mAccelerations;
	
	protected UtilsMath mUtilsMath;
	protected UtilsLinearReg mUtilsLinearReg;
	protected UtilsSpatialSampling mUtilsSpatialSampling;
	protected UtilsGeneral mUtilsGeneral;
	
	protected IStatEngine mStatEngine;
	
	public GestureExtended(Gesture gesture, HashMap<String, IFeatureMeanData> hashFeatureMeans, int gestureIdx) {		
		Id = gesture.Id;
		Instruction = gesture.Instruction;		
		ListStrokes = gesture.ListStrokes;
		
		InitUtils();		
		
		GestureIndex = gestureIdx;
		
		mHashFeatureMeans = hashFeatureMeans;
		InitParams();
		PreCalculations();	
		InitFeatures();
	}

	protected void InitUtils() {
		mUtilsLinearReg = Utils.GetInstance().GetUtilsLinearReg();
		mUtilsSpatialSampling = Utils.GetInstance().GetUtilsSpatialSampling();
		mUtilsMath = Utils.GetInstance().GetUtilsMath();
		mUtilsGeneral = Utils.GetInstance().GetUtilsGeneral();
	}

	protected void InitParams() {
		ListStrokesExtended = new ArrayList<>();
		
		ListGestureEventsExtended = new ArrayList<>();
		ListGestureEvents = new ArrayList<>();
		
		mStatEngine = StatEngine.GetInstance();
		
		GestureLengthPixel = 0;
		GestureLengthMM = 0;
		GestureTotalTimeInterval = 0;
		GestureTotalStrokeTimeInterval = 0;
	}	

	protected void PreCalculations() {
		Stroke tempStroke;		
		StrokeExtended tempStrokeExtended;		
		
		for(int idxStroke = 0; idxStroke < ListStrokes.size(); idxStroke++) {
			tempStroke = ListStrokes.get(idxStroke);				
			tempStrokeExtended = new StrokeExtended(tempStroke, mHashFeatureMeans, Instruction, idxStroke);
			
			ListStrokesExtended.add(tempStrokeExtended);
			GestureLengthMM += tempStrokeExtended.StrokePropertiesObj.LengthMM;
			GestureLengthPixel += tempStroke.Length;
			
			GestureTotalStrokeTimeInterval += tempStrokeExtended.StrokeTimeInterval;
			GestureTotalStrokeArea += tempStrokeExtended.ShapeDataObj.ShapeArea;
			
			GestureMaxPressure = Utils.GetInstance().GetUtilsMath().GetMaxValue(GestureMaxPressure, tempStrokeExtended.MaxPressure);
			GestureMaxSurface = Utils.GetInstance().GetUtilsMath().GetMaxValue(GestureMaxSurface, tempStrokeExtended.MaxSurface);	
			
			ListGestureEvents.addAll(tempStroke.ListEvents);
			ListGestureEventsExtended.addAll(tempStrokeExtended.ListEventsExtended);			
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
		CalculateGestureVelocityPeaks();
		CalculateGestureAccelerationPeaks();
		CalculateAccumulatedDistanceByTime();
		CalculateGestureStartMaxEndDirections();
		CalculateAccumulatedDistanceLinearReg();		
		CalculateAccelerations();
	}
	
	protected void CalculateAccelerations()
	{
//		MotionEventExtended eventCurr, eventPrev;
//		double velocityCurr, velocityPrev;
//		double timeCurr, timePrev;
//		double tempAcc;
//		
//		for(int idxEvent = 1; idxEvent < ListGestureEventsExtended.size(); idxEvent++) {
//			eventCurr = ListGestureEventsExtended.get(idxEvent);
//			
//			velocityCurr = ListGestureEventsExtended.get(idxEvent);
//			velocityPrev = ListGestureEventsExtended.get(idxEvent - 1);
//			
//			timeCurr = 
//			
//			tempAcc = 
//		}	
	}
	
	protected void CalculateSpatialSamplingVector()
	{
		SpatialSamplingVector = mUtilsSpatialSampling.PrepareDataSpatialSampling(ListGestureEvents, GestureLengthPixel);
	}
	
	protected void CalculateAccelerationAtStart()
	{
		if(ListGestureEventsExtended.size() >= (ConstsFeatures.ACC_CALC_MIN_NUM_EVENTS * 2)) {
			MotionEventExtended eventPrev;
			MotionEventExtended eventCurr;
			
			double deltaTimeInterval;		
			double deltaVelocity;
			
			double tempAcc;
			double totalAcc = 0;					
			
			for(int idxEvent = 1; idxEvent < ConstsFeatures.ACC_CALC_MIN_NUM_EVENTS; idxEvent++) {							
				eventPrev = ListGestureEventsExtended.get(idxEvent - 1);
				eventCurr = ListGestureEventsExtended.get(idxEvent);
				
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
			AddGestureValue(Instruction, ConstsParamNames.Gesture.GESTURE_AVG_START_ACCELERATION, GestureAverageStartAcceleration);
		}		
	}
	
	protected void CalculateAccelerationAtEnd()
	{
		if(ListGestureEventsExtended.size() >= (2 * ConstsFeatures.ACC_CALC_MIN_NUM_EVENTS)) {
			MotionEventExtended eventPrev;
			MotionEventExtended eventCurr;
			
			double deltaTimeInterval;		
			double deltaVelocity;
			
			double tempAcc;
			double totalAcc = 0;
			
			int startIdx = ListGestureEventsExtended.size() - ConstsFeatures.ACC_CALC_MIN_NUM_EVENTS + 1;	
			
			for(int idxEvent = startIdx; idxEvent < ListGestureEventsExtended.size(); idxEvent++) {				
				eventPrev = ListGestureEventsExtended.get(idxEvent - 1);
				eventCurr = ListGestureEventsExtended.get(idxEvent);
				
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
		
		GestureMaxVelocity = 0;
		GestureMaxAcceleration = 0;
		
		double totalAcceleration = 0;
		
		for(int idxEvent = 0; idxEvent < ListGestureEventsExtended.size(); idxEvent++) {
			totalPressure += ListGestureEventsExtended.get(idxEvent).Pressure;
			totalSurface += ListGestureEventsExtended.get(idxEvent).TouchSurface;
			
			totalAccX += ListGestureEventsExtended.get(idxEvent).AccelerometerX;
			totalAccY += ListGestureEventsExtended.get(idxEvent).AccelerometerY;
			totalAccZ += ListGestureEventsExtended.get(idxEvent).AccelerometerZ;
			
			GestureMaxAccX = Utils.GetInstance().GetUtilsMath().GetMaxValue(GestureMaxAccX, ListGestureEventsExtended.get(idxEvent).AccelerometerX);
			GestureMaxAccY = Utils.GetInstance().GetUtilsMath().GetMaxValue(GestureMaxAccY, ListGestureEventsExtended.get(idxEvent).AccelerometerY);
			GestureMaxAccZ = Utils.GetInstance().GetUtilsMath().GetMaxValue(GestureMaxAccZ, ListGestureEventsExtended.get(idxEvent).AccelerometerZ);
			
			GestureMaxVelocity = mUtilsMath.GetMaxValue(GestureMaxVelocity, ListGestureEventsExtended.get(idxEvent).Velocity);
			GestureMaxAcceleration = mUtilsMath.GetMaxValue(GestureMaxAcceleration, ListGestureEventsExtended.get(idxEvent).Acceleration);
			
			totalAcceleration += ListGestureEventsExtended.get(idxEvent).Acceleration;			
		}
		
		GestureAverageAcceleration = totalAcceleration / ListGestureEventsExtended.size();
				
		GestureAvgPressure = totalPressure / ListGestureEventsExtended.size();
		GestureAvgSurface = totalSurface / ListGestureEventsExtended.size();
		
		AddGestureValue(Instruction, ConstsParamNames.Gesture.GESTURE_AVG_PRESSURE, GestureAvgPressure);
		AddGestureValue(Instruction, ConstsParamNames.Gesture.GESTURE_AVG_SURFACE, GestureAvgSurface);
		
		GestureAvgAccX = totalAccX / ListGestureEventsExtended.size(); 
		GestureAvgAccY = totalAccY / ListGestureEventsExtended.size();
		GestureAvgAccZ = totalAccZ / ListGestureEventsExtended.size();
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
			IndexValue tempVelocityPeak = ListStrokesExtended.get(0).StrokeVelocityPeakAvgPoint.MaxValueInSection;	
			ParameterAvgPoint velocityAvgPoint = ListStrokesExtended.get(0).StrokeVelocityPeakAvgPoint;
			
			GestureVelocityPeakMax = ((double) tempVelocityPeak.Index) / ((double) ListStrokesExtended.get(0).ListEventsExtended.size());	
						
			double velocityPeakIndexDiff = velocityAvgPoint.IndexEnd.Index - velocityAvgPoint.IndexStart.Index; 
			GestureVelocityPeakIntervalPercentage = velocityPeakIndexDiff / ((double) ListStrokesExtended.get(0).ListEventsExtended.size());
			
			AddGestureValue(Instruction, ConstsParamNames.Gesture.GESTURE_VELOCITY_PEAK, GestureVelocityPeakMax);
			AddGestureValue(Instruction, ConstsParamNames.Gesture.GESTURE_VELOCITY_PEAK_INTERVAL_PERCENTAGE, GestureVelocityPeakIntervalPercentage);
		}		
	}
	
	protected void CalculateGestureAccelerationPeaks()
	{
		if(ListStrokesExtended.size() > 0) {
			IndexValue tempAccelerationPeak = ListStrokesExtended.get(0).StrokeAccelerationPeakAvgPoint.MaxValueInSection;	
			ParameterAvgPoint accelerationAvgPoint = ListStrokesExtended.get(0).StrokeAccelerationPeakAvgPoint;
			
			GestureAccelerationPeakMax = ((double) tempAccelerationPeak.Index) / ((double) ListStrokesExtended.get(0).ListEventsExtended.size());	
						
			double accPeakIndexDiff = accelerationAvgPoint.IndexEnd.Index - accelerationAvgPoint.IndexStart.Index; 
			GestureAccelerationPeakIntervalPercentage = accPeakIndexDiff / ((double) ListStrokesExtended.get(0).ListEventsExtended.size());
			
			AddGestureValue(Instruction, ConstsParamNames.Gesture.GESTURE_ACCELERATION_PEAK, GestureAccelerationPeakMax);
			AddGestureValue(Instruction, ConstsParamNames.Gesture.GESTURE_ACCELERATION_PEAK_INTERVAL_PERCENTAGE, GestureAccelerationPeakIntervalPercentage);
		}		
	}
	
	protected void AddCalculatedFeatures() {
		AddGestureValue(Instruction, ConstsParamNames.Gesture.GESTURE_LENGTH, GestureLengthMM);
		AddGestureValue(Instruction, ConstsParamNames.Gesture.GESTURE_NUM_EVENTS, ListGestureEventsExtended.size());
		AddGestureValue(Instruction, ConstsParamNames.Gesture.GESTURE_TOTAL_STROKES_TIME_INTERVAL, GestureTotalStrokeTimeInterval);
		AddGestureValue(Instruction, ConstsParamNames.Gesture.GESTURE_TOTAL_STROKE_AREA, GestureTotalStrokeArea);		
	}

	protected void CalculateGestureTotalTimeWithPauses() {
		StrokeExtended strokeFirst = ListStrokesExtended.get(0);
		StrokeExtended strokeLast = ListStrokesExtended.get(ListStrokesExtended.size() - 1);
		
		double gestureStartTime = strokeFirst.ListEventsExtended.get(0).EventTime;
		double gestureEndTime = strokeLast.ListEventsExtended.get(strokeLast.ListEventsExtended.size() - 1).EventTime;
		
		GestureTotalTimeInterval = gestureEndTime - gestureStartTime;
		AddGestureValue(Instruction, ConstsParamNames.Gesture.GESTURE_TOTAL_TIME_INTERNVAL, GestureTotalTimeInterval);
	}
	
	protected void CalculateGestureAvgVelocity()
	{
		GestureAverageVelocity = GestureLengthMM / GestureTotalStrokeTimeInterval;
		AddGestureValue(Instruction, ConstsParamNames.Gesture.GESTURE_AVERAGE_VELOCITY, GestureAverageVelocity);
	}
	
	protected void CalculateAccumulatedDistanceByTime()
	{	
		double[] accumulatedLength = new double[ListGestureEventsExtended.size()];
		double[] accumulatedTime = new double[ListGestureEventsExtended.size()];
		
		double deltaX;
		double deltaY;
		double distance = 0;
		double timeDiff = 0;
		
		int idxAccumulated = 0;	
		double tempLength = 0;
		double tempTime = 0;
		
		double prevLength = 0;
		double prevTime = 0;
		
		for(int idxEvent = 1; idxEvent < ListGestureEventsExtended.size(); idxEvent++) {				
			deltaX = ListGestureEventsExtended.get(idxEvent).Xmm - ListGestureEventsExtended.get(idxEvent - 1).Xmm; 
			deltaY = ListGestureEventsExtended.get(idxEvent).Ymm - ListGestureEventsExtended.get(idxEvent - 1).Ymm;
			distance = Utils.GetInstance().GetUtilsMath().CalcPitagoras(deltaX, deltaY);
			timeDiff = ListGestureEventsExtended.get(idxEvent).EventTime - ListGestureEventsExtended.get(idxEvent - 1).EventTime; 
						
			if(!ListGestureEventsExtended.get(idxEvent).IsStartOfStroke) {
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

	protected void CalculateGestureStartMaxEndDirections()
	{
		if(!ListStrokesExtended.get(0).IsPoint)
		{
			ArrayList<MotionEventExtended> listEventsExtendedFirstStroke = ListStrokesExtended.get(0).ListEventsExtended;
			
			double deltaY;
			double deltaX;
			
			ParameterAvgPoint velocityAvgPoint = ListStrokesExtended.get(0).StrokeVelocityPeakAvgPoint;
			deltaY = listEventsExtendedFirstStroke.get(velocityAvgPoint.IndexStart.Index).Ymm -
					 listEventsExtendedFirstStroke.get(velocityAvgPoint.IndexStart.Index-1).Ymm;
			deltaX = listEventsExtendedFirstStroke.get(velocityAvgPoint.IndexStart.Index).Xmm -
					 listEventsExtendedFirstStroke.get(velocityAvgPoint.IndexStart.Index-1).Xmm;
			
			GestureStartDirection = Math.atan2(deltaY, deltaX);
			deltaY = listEventsExtendedFirstStroke.get(velocityAvgPoint.MaxValueInSection.Index).Ymm -
					 listEventsExtendedFirstStroke.get(velocityAvgPoint.MaxValueInSection.Index-1).Ymm;
			deltaX = listEventsExtendedFirstStroke.get(velocityAvgPoint.MaxValueInSection.Index).Xmm -
					 listEventsExtendedFirstStroke.get(velocityAvgPoint.MaxValueInSection.Index-1).Xmm;
			
			GestureMaxDirection = Math.atan2(deltaY, deltaX);
			deltaY = listEventsExtendedFirstStroke.get(velocityAvgPoint.IndexEnd.Index).Ymm -
					 listEventsExtendedFirstStroke.get(velocityAvgPoint.IndexEnd.Index-1).Ymm;
			deltaX = listEventsExtendedFirstStroke.get(velocityAvgPoint.IndexEnd.Index).Xmm -
					 listEventsExtendedFirstStroke.get(velocityAvgPoint.IndexEnd.Index-1).Xmm;
			
			GestureEndDirection = Math.atan2(deltaY, deltaX);
			
			AddGestureValue(Instruction, ConstsParamNames.Gesture.GESTURE_AVG_START_DIRECTION, GestureStartDirection);
			AddGestureValue(Instruction, ConstsParamNames.Gesture.GESTURE_AVG_MAX_DIRECTION, GestureMaxDirection);
			AddGestureValue(Instruction, ConstsParamNames.Gesture.GESTURE_AVG_END_DIRECTION, GestureEndDirection);
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
	
	public void AddGestureValue(String instruction, String paramName, double value)
	{
		AddGestureValue(instruction, paramName, value, false);
	}
	
	public void AddGestureAngleValue(String instruction, String paramName, double value)
	{
		AddGestureValue(instruction, paramName, value, true);
	}
	
	public void AddGestureValue(String instruction, String paramName, double value, boolean isAngle)
	{
		String key = mUtilsGeneral.GenerateGestureFeatureMeanKey(instruction, paramName);
		
		IFeatureMeanData tempFeatureMeanData;
		
		if(mHashFeatureMeans.containsKey(key)) {
			tempFeatureMeanData = mHashFeatureMeans.get(key);
		}
		else {
			if(isAngle) {
				tempFeatureMeanData = new FeatureMeanDataAngle(paramName);	
			}
			else {
				tempFeatureMeanData = new FeatureMeanData(paramName);	
			}
					
			mHashFeatureMeans.put(key, tempFeatureMeanData);
		}
		
		tempFeatureMeanData.AddValue(value);		
	}
	
	public HashMap<String, IFeatureMeanData> GetFeatureMeansHash() 
	{
		return mHashFeatureMeans;
	}
}
