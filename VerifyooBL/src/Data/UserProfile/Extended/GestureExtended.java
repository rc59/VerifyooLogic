package Data.UserProfile.Extended;

import java.util.ArrayList;
import java.util.HashMap;

import Consts.ConstsFeatures;
import Consts.ConstsGeneral;
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
	public double GestureTotalStrokeAreaMinXMinY;
	public double[] SpatialSamplingVector;
	
	public double GestureStartDirection;
	public double GestureMaxDirection;
	public double GestureEndDirection;
	public double GestureDirectionAtFirstStrokeMaxVelocity;
	public double MidOfFirstStrokeVelocity;
	public double MidOfFirstStrokeAngle;

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
	public boolean IsOnlyPoints;
	
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
		if(!IsOnlyPoints) {
			InitFeatures();	
		}
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
		IsOnlyPoints = true;
		
		for(int idxStroke = 0; idxStroke < ListStrokes.size(); idxStroke++) {
			tempStroke = ListStrokes.get(idxStroke);				
			tempStrokeExtended = new StrokeExtended(tempStroke, mHashFeatureMeans, Instruction, idxStroke);
			
			ListStrokesExtended.add(tempStrokeExtended);
			if(!tempStrokeExtended.IsPoint) {
				GestureLengthMM += tempStrokeExtended.StrokePropertiesObj.LengthMM;
				GestureLengthPixel += tempStroke.Length;
				
				GestureTotalStrokeTimeInterval += tempStrokeExtended.StrokeTimeInterval;
				GestureTotalStrokeArea += tempStrokeExtended.ShapeDataObj.ShapeArea;
				GestureTotalStrokeAreaMinXMinY += tempStrokeExtended.ShapeDataObj.ShapeAreaMinXMinY;
				
				GestureMaxPressure = Utils.GetInstance().GetUtilsMath().GetMaxValue(GestureMaxPressure, tempStrokeExtended.MaxPressure);
				GestureMaxSurface = Utils.GetInstance().GetUtilsMath().GetMaxValue(GestureMaxSurface, tempStrokeExtended.MaxSurface);	
				
				ListGestureEvents.addAll(tempStroke.ListEvents);
				ListGestureEventsExtended.addAll(tempStrokeExtended.ListEventsExtended);	
			
				IsOnlyPoints = false;
			}
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
		if(ListStrokesExtended.size() > 0 && !ListStrokesExtended.get(0).IsPoint) {
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
		if(ListStrokesExtended.size() > 0 && !ListStrokesExtended.get(0).IsPoint) {
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
		AddGestureValue(Instruction, ConstsParamNames.Gesture.GESTURE_TOTAL_STROKE_AREA_MINX_MINY, GestureTotalStrokeAreaMinXMinY);
	}

	protected void CalculateGestureTotalTimeWithPauses() {
		MotionEventExtended eventFirst  = ListGestureEventsExtended.get(0);
		MotionEventExtended eventLast = ListGestureEventsExtended.get(ListGestureEventsExtended.size() - 1);
		
		double gestureStartTime = eventFirst.EventTime;
		double gestureEndTime = eventLast.EventTime;
		
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
			StrokeExtended firstStroke = ListStrokesExtended.get(0);
			int numOfSamples = firstStroke.StrokeEndEvent - firstStroke.StrokeStartEvent + 1;
			int lastPoint = 0;
			//an event is selected if the sum of the angle diff of four consequent events is greater than pi/2 
			//and at least one diff angle is greater than 35 deg. If there is no such event, the last event is selected
			for(int idxEvent = firstStroke.StrokeStartEvent; idxEvent < firstStroke.StrokeEndEvent - 2; ++idxEvent)
			{
				if(((firstStroke.ListEventsExtended.get(idxEvent).AngleDiff + 
						firstStroke.ListEventsExtended.get(idxEvent + 1).AngleDiff + 
						firstStroke.ListEventsExtended.get(idxEvent + 2).AngleDiff +
						firstStroke.ListEventsExtended.get(idxEvent + 3).AngleDiff)								
						> Math.PI/2)
						&&
						((firstStroke.ListEventsExtended.get(idxEvent).AngleDiff > 0.7)||
								(firstStroke.ListEventsExtended.get(idxEvent+1).AngleDiff > 0.7)||
								(firstStroke.ListEventsExtended.get(idxEvent+2).AngleDiff > 0.7)||
								(firstStroke.ListEventsExtended.get(idxEvent+3).AngleDiff > 0.7)
								)
						)
				{
					lastPoint = idxEvent+3;
					break;
				}
			}
			if(lastPoint == 0) lastPoint = firstStroke.StrokeEndEvent;
//			int midIndex = ListStrokesExtended.get(0).ListEventsExtended.size() / 2;
			int midIndex = lastPoint / 2;
			ArrayList<MotionEventExtended> listEventsExtendedFirstStroke = ListStrokesExtended.get(0).ListEventsExtended;
			
			double deltaY;
			double deltaX;

			deltaY = listEventsExtendedFirstStroke.get(midIndex).Ymm - listEventsExtendedFirstStroke.get(midIndex-1).Ymm;
			deltaX = listEventsExtendedFirstStroke.get(midIndex).Xmm - listEventsExtendedFirstStroke.get(midIndex-1).Xmm;
			MidOfFirstStrokeAngle = Math.atan2(deltaY, deltaX);
			MidOfFirstStrokeVelocity = ListStrokesExtended.get(0).ListEventsExtended.get(midIndex).Velocity;
			AddGestureValue(Instruction, ConstsParamNames.Gesture.GESTURE_MID_OF_FIRST_STROKE_VELOCITY, MidOfFirstStrokeVelocity);
			AddGestureAngleValue(Instruction, ConstsParamNames.Gesture.GESTURE_MID_OF_FIRST_STROKE_ANGLE, MidOfFirstStrokeAngle);
			
//			ParameterAvgPoint velocityAvgPoint = ListStrokesExtended.get(0).StrokeVelocityPeakAvgPoint;
			
//			deltaY = listEventsExtendedFirstStroke.get(velocityAvgPoint.IndexStart.Index).Ymm - listEventsExtendedFirstStroke.get(velocityAvgPoint.IndexStart.Index-1).Ymm;
//			deltaX = listEventsExtendedFirstStroke.get(velocityAvgPoint.IndexStart.Index).Xmm - listEventsExtendedFirstStroke.get(velocityAvgPoint.IndexStart.Index-1).Xmm;
//			GestureStartDirection = Math.atan2(deltaY, deltaX);
//			
//			deltaY = listEventsExtendedFirstStroke.get(velocityAvgPoint.MaxValueInSection.Index).Ymm - listEventsExtendedFirstStroke.get(velocityAvgPoint.MaxValueInSection.Index-1).Ymm;
//			deltaX = listEventsExtendedFirstStroke.get(velocityAvgPoint.MaxValueInSection.Index).Xmm - listEventsExtendedFirstStroke.get(velocityAvgPoint.MaxValueInSection.Index-1).Xmm;
//			GestureMaxDirection = Math.atan2(deltaY, deltaX);
//			
//			deltaY = listEventsExtendedFirstStroke.get(velocityAvgPoint.IndexEnd.Index).Ymm - listEventsExtendedFirstStroke.get(velocityAvgPoint.IndexEnd.Index-1).Ymm;
//			deltaX = listEventsExtendedFirstStroke.get(velocityAvgPoint.IndexEnd.Index).Xmm - listEventsExtendedFirstStroke.get(velocityAvgPoint.IndexEnd.Index-1).Xmm;
//			GestureEndDirection = Math.atan2(deltaY, deltaX);
//			
//			deltaY = listEventsExtendedFirstStroke.get(ListStrokesExtended.get(0).StrokeMaxVelocity.Index).Ymm - listEventsExtendedFirstStroke.get(ListStrokesExtended.get(0).StrokeMaxVelocity.Index - 1).Ymm;
//			deltaX = listEventsExtendedFirstStroke.get(ListStrokesExtended.get(0).StrokeMaxVelocity.Index).Xmm - listEventsExtendedFirstStroke.get(ListStrokesExtended.get(0).StrokeMaxVelocity.Index - 1).Xmm;
//			GestureDirectionAtFirstStrokeMaxVelocity = Math.atan2(deltaY, deltaX);
//			
//			AddGestureAngleValue(Instruction, ConstsParamNames.Gesture.GESTURE_AVG_START_DIRECTION, GestureStartDirection);
//			AddGestureAngleValue(Instruction, ConstsParamNames.Gesture.GESTURE_AVG_MAX_DIRECTION,   GestureMaxDirection);
//			AddGestureAngleValue(Instruction, ConstsParamNames.Gesture.GESTURE_AVG_END_DIRECTION,   GestureEndDirection);
//			AddGestureAngleValue(Instruction, ConstsParamNames.Gesture.GESTURE_MAX_VELOCITY_DIRECTION, GestureDirectionAtFirstStrokeMaxVelocity);
//
//			deltaY = listEventsExtendedFirstStroke.get(velocityAvgPoint.IndexStart.Index).Ymm - listEventsExtendedFirstStroke.get(velocityAvgPoint.IndexStart.Index-1).Ymm;
//			deltaX = listEventsExtendedFirstStroke.get(velocityAvgPoint.IndexStart.Index).Xmm - listEventsExtendedFirstStroke.get(velocityAvgPoint.IndexStart.Index-1).Xmm;
//			GestureStartDirection = Math.atan2(deltaY, deltaX);
//			
//			deltaY = listEventsExtendedFirstStroke.get(velocityAvgPoint.MaxValueInSection.Index).Ymm - listEventsExtendedFirstStroke.get(velocityAvgPoint.MaxValueInSection.Index-1).Ymm;
//			deltaX = listEventsExtendedFirstStroke.get(velocityAvgPoint.MaxValueInSection.Index).Xmm - listEventsExtendedFirstStroke.get(velocityAvgPoint.MaxValueInSection.Index-1).Xmm;
//			GestureMaxDirection = Math.atan2(deltaY, deltaX);
//			
//			deltaY = listEventsExtendedFirstStroke.get(velocityAvgPoint.IndexEnd.Index).Ymm - listEventsExtendedFirstStroke.get(velocityAvgPoint.IndexEnd.Index-1).Ymm;
//			deltaX = listEventsExtendedFirstStroke.get(velocityAvgPoint.IndexEnd.Index).Xmm - listEventsExtendedFirstStroke.get(velocityAvgPoint.IndexEnd.Index-1).Xmm;
//			GestureEndDirection = Math.atan2(deltaY, deltaX);
			
//			deltaY = listEventsExtendedFirstStroke.get(ListStrokesExtended.get(0).StrokeMaxVelocity.Index).Ymm - listEventsExtendedFirstStroke.get(ListStrokesExtended.get(0).StrokeMaxVelocity.Index-1).Ymm;
//			deltaX = listEventsExtendedFirstStroke.get(ListStrokesExtended.get(0).StrokeMaxVelocity.Index).Xmm - listEventsExtendedFirstStroke.get(ListStrokesExtended.get(0).StrokeMaxVelocity.Index-1).Xmm;
//			GestureDirectionAtFirstStrokeMaxVelocity = Math.atan2(deltaY, deltaX);
					
			
//			AddGestureValue(Instruction, ConstsParamNames.Gesture.GESTURE_AVG_START_DIRECTION, GestureStartDirection);
//			AddGestureValue(Instruction, ConstsParamNames.Gesture.GESTURE_AVG_MAX_DIRECTION, GestureMaxDirection);
//			AddGestureValue(Instruction, ConstsParamNames.Gesture.GESTURE_AVG_END_DIRECTION, GestureEndDirection);
//			AddGestureValue(Instruction, ConstsParamNames.Gesture.GESTURE_MAX_VELOCITY_DIRECTION, GestureDirectionAtFirstStrokeMaxVelocity);
		}
	}

	protected void CalculateAccumulatedDistanceLinearReg()
	{
		mUtilsLinearReg.CalcLinearReg(AccumulatedTime, AccumulatedLength);
		LinearRegression linearRegObj = mUtilsLinearReg.GetLinearRegObj();
		GestureAccumulatedLengthLinearRegRSqr = linearRegObj.R2();
		GestureAccumulatedLengthLinearRegSlope = linearRegObj.slope();
		GestureAccumulatedLengthLinearRegIntercept = linearRegObj.intercept();
		
		AddGestureValue(Instruction, ConstsParamNames.Gesture.GESTURE_ACCUMULATED_LENGTH_R2, GestureAccumulatedLengthLinearRegRSqr);
		AddGestureValue(Instruction, ConstsParamNames.Gesture.GESTURE_ACCUMULATED_LENGTH_SLOPE, GestureAccumulatedLengthLinearRegSlope);	
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
				tempFeatureMeanData = new FeatureMeanDataAngle(paramName, Instruction);	
			}
			else {
				tempFeatureMeanData = new FeatureMeanData(paramName, Instruction);	
			}
					
			mHashFeatureMeans.put(key, tempFeatureMeanData);
		}
		
		
		tempFeatureMeanData.AddValue(value);		
	}
	
	public HashMap<String, IFeatureMeanData> GetFeatureMeansHash() 
	{
		return mHashFeatureMeans;
	}
	
	public String GetGestureStrength() {
		String result = "";
	
		double currentPopZScore;
		
		double totalZScore = 0;
		double countZScore = 0;
		
		for (IFeatureMeanData featureMeanData : mHashFeatureMeans.values()) {
			currentPopZScore = featureMeanData.GetPopulationZScore();
			
			if(currentPopZScore > ConstsGeneral.GESTURE_SCORE_CALC_MIN_Z_SCORE) {
				totalZScore += currentPopZScore;
				countZScore++;
			}
		}			
		
		return result;
	}
}
