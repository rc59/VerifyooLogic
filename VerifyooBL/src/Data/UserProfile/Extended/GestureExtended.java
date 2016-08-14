package Data.UserProfile.Extended;

import java.util.ArrayList;
import java.util.Collections;
import java.util.Comparator;
import java.util.HashMap;

import Consts.ConstsFeatures;
import Consts.ConstsParamNames;
import Data.MetaData.IndexValue;
import Data.MetaData.ParameterAvgPoint;
import Data.MetaData.ValueFreq;
import Data.UserProfile.Raw.Gesture;
import Data.UserProfile.Raw.MotionEventCompact;
import Data.UserProfile.Raw.Stroke;
import Logic.Comparison.Stats.FeatureMeanData;
import Logic.Comparison.Stats.FeatureMeanDataAngle;
import Logic.Comparison.Stats.StatEngine;
import Logic.Comparison.Stats.Interfaces.IFeatureMeanData;
import Logic.Comparison.Stats.Interfaces.IStatEngine;
import Logic.Utils.Utils;
import Logic.Utils.UtilsGeneral;
import Logic.Utils.UtilsLinearReg;
import Logic.Utils.UtilsLinearReg.LinearRegression;
import Logic.Utils.UtilsMath;
import Logic.Utils.UtilsSignalProcessing;

public class GestureExtended extends Gesture {
	
	/*************** Shape Parameters ***************/
	
	private double mPointMinXMM;
	private double mPointMaxXMM;
	private double mPointMinYMM;
	private double mPointMaxYMM;
	
	public double GestureLengthPixel;
	public double GestureLengthMM;
	public double GestureLengthMMX;
	public double GestureLengthMMY;
	public double GestureTotalStrokeArea;
	public double GestureTotalStrokeAreaMinXMinY;
	
	public double GestureTotalArea;
	public double GestureTotalAreaMinXMinY;
	
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
	
	public double GestureAvgMiddlePressure;
		
	/*************** Surface Parameters ***************/
	
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
	
	protected UtilsMath mUtilsMath;
	protected UtilsLinearReg mUtilsLinearReg;
	protected UtilsSignalProcessing mUtilsSpatialSampling;
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
				
				ListGestureEvents.addAll(tempStroke.ListEvents);
				ListGestureEventsExtended.addAll(tempStrokeExtended.ListEventsExtended);	
			
				IsOnlyPoints = false; 
			}
			
			if(idxStroke == 0) {
				mPointMinXMM = tempStrokeExtended.PointMinXMM;
				mPointMinYMM = tempStrokeExtended.PointMinYMM;
				mPointMaxXMM = tempStrokeExtended.PointMaxXMM;
				mPointMaxYMM = tempStrokeExtended.PointMaxYMM;				
			}
			else {
				mPointMinXMM = mUtilsMath.GetMinValue(mPointMinXMM, tempStrokeExtended.PointMinXMM);
				mPointMinYMM = mUtilsMath.GetMinValue(mPointMinYMM, tempStrokeExtended.PointMinYMM);
								
				mPointMaxXMM = mUtilsMath.GetMaxValue(mPointMaxXMM, tempStrokeExtended.PointMaxXMM);
				mPointMaxYMM = mUtilsMath.GetMaxValue(mPointMaxYMM, tempStrokeExtended.PointMaxYMM);
			}
		}		
	}
	
	protected void InitFeatures() {
		CalculateListGestureEventsFeatures();
		CalculateVelocityFeatures();
		CalculateAccelerationFeatures();
		AddCalculatedFeatures();
		CalculateSpatialSamplingVector();
		CalculateGestureTotalTimeWithPauses();
		CalculateGestureAvgVelocity();		
		CalculateAccelerationAtStart();
		CalculateAvgOfMiddlePressureAndSurface();
		CalculateGestureVelocityPeaks();
		CalculateGestureAccelerationPeaks();
		CalculateAccumulatedDistanceByTime();
		//CalculateGestureStartMaxEndDirections();
		CalculateAccumulatedDistanceLinearReg();		
		CalculateAccelerations();
	}
	
	private void CalculateVelocityFeatures() {
		StrokeExtended currentStroke;
		StrokeExtended firstStroke = ListStrokesExtended.get(0);
		double velocityValues[] = firstStroke.GetFilteredVelocities();
		
		MidOfFirstStrokeVelocity = velocityValues[velocityValues.length / 2]; 
		
		GestureMaxVelocity = velocityValues[0];
		for(int idxEvent = 1; idxEvent < velocityValues.length; idxEvent++) {
			GestureMaxVelocity = mUtilsMath.GetMaxValue(GestureMaxVelocity, velocityValues[idxEvent]);
		}
		
		for(int idxStroke = 1; idxStroke < ListStrokesExtended.size(); idxStroke++) {
			currentStroke = ListStrokesExtended.get(idxStroke);
			velocityValues = currentStroke.GetFilteredVelocities();
			
			GestureMaxVelocity = mUtilsMath.GetMaxValue(GestureMaxVelocity, velocityValues[0]);
			for(int idxEvent = 1; idxEvent < velocityValues.length; idxEvent++) {
				GestureMaxVelocity = mUtilsMath.GetMaxValue(GestureMaxVelocity, velocityValues[idxEvent]);
			}
		}
		
		AddGestureValue(Instruction, ConstsParamNames.Gesture.GESTURE_MID_OF_FIRST_STROKE_VELOCITY, MidOfFirstStrokeVelocity);
		AddGestureValue(Instruction, ConstsParamNames.Gesture.GESTURE_MAX_VELOCITY, GestureMaxVelocity);
	}

	private void CalculateAccelerationFeatures() {
		StrokeExtended currentStroke;		
		double accelerationValues[];
		
		double totalAccelerations = 0;
		double eventCount = 0;
		
		for(int idxStroke = 0; idxStroke < ListStrokesExtended.size(); idxStroke++) {
			currentStroke = ListStrokesExtended.get(idxStroke);
			accelerationValues = currentStroke.GetFilteredAccelerations();
			
			GestureMaxAcceleration = mUtilsMath.GetMaxValue(GestureMaxAcceleration, accelerationValues[0]);
			totalAccelerations += accelerationValues[0];
			eventCount++;
			for(int idxEvent = 1; idxEvent < accelerationValues.length; idxEvent++) {
				GestureMaxAcceleration = mUtilsMath.GetMaxValue(GestureMaxAcceleration, accelerationValues[idxEvent]);
				totalAccelerations += accelerationValues[idxEvent];
				eventCount++;
			}
		}
		
		GestureAverageAcceleration = totalAccelerations / eventCount;
		
		AddGestureValue(Instruction, ConstsParamNames.Gesture.GESTURE_AVG_ACCELERATION, GestureAverageAcceleration);
		AddGestureValue(Instruction, ConstsParamNames.Gesture.GESTURE_MAX_ACCELERATION, GestureMaxAcceleration);	
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
		double length = 0;
		
		MotionEventExtended eventCurr, eventNext;		
		
		for(int idx = 0; idx < ListGestureEventsExtended.size() - 1; idx++) {
			eventCurr = ListGestureEventsExtended.get(idx);
			eventNext = ListGestureEventsExtended.get(idx + 1);
			
			length += Utils.GetInstance().GetUtilsMath().CalcDistanceInPixels(eventCurr, eventNext);
		}
		
		SpatialSamplingVector = mUtilsSpatialSampling.PrepareDataSpatialSampling(ListGestureEventsExtended, length);
	}
	
	protected void CalculateAccelerationAtStart()
	{
		double[] accelerationValues = ListStrokesExtended.get(0).GetFilteredAccelerations();
		
		if(accelerationValues.length >= (ConstsFeatures.ACC_CALC_MIN_NUM_EVENTS * 2)) {
			double totalAcc = 0;					
			
			for(int idxEvent = 0; idxEvent < ConstsFeatures.ACC_CALC_MIN_NUM_EVENTS + 3; idxEvent++) {							
				totalAcc += accelerationValues[idxEvent];				
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
		double totalAccX = 0;
		double totalAccY = 0;
		double totalAccZ = 0;

		double x1, y1, x2, y2, x3, y3;
		GestureTotalArea = 0;
		GestureTotalAreaMinXMinY = 0;
		
		HashMap<Double, ValueFreq> hashPressureFreqs = new HashMap<>();
		HashMap<Double, ValueFreq> hashSurfaceFreqs = new HashMap<>();
		
		double currentPressure;
		double currentSurface;		
		
		for(int idxEvent = 0; idxEvent < ListGestureEventsExtended.size(); idxEvent++) {
			currentPressure = ListGestureEventsExtended.get(idxEvent).Pressure;
			currentSurface = ListGestureEventsExtended.get(idxEvent).TouchSurface;
			
			if(!hashPressureFreqs.containsKey(currentPressure)) {
				hashPressureFreqs.put(currentPressure, new ValueFreq(currentPressure));
			}
			else {				
				hashPressureFreqs.get(currentPressure).Increase();
			}			
			
			if(!hashSurfaceFreqs.containsKey(currentSurface)) {
				hashSurfaceFreqs.put(currentSurface, new ValueFreq(currentSurface));
			}
			else {				
				hashSurfaceFreqs.get(currentSurface).Increase();
			}
			
		
			totalAccX += ListGestureEventsExtended.get(idxEvent).AccelerometerX;
			totalAccY += ListGestureEventsExtended.get(idxEvent).AccelerometerY;
			totalAccZ += ListGestureEventsExtended.get(idxEvent).AccelerometerZ;
			
			GestureMaxAccX = Utils.GetInstance().GetUtilsMath().GetMaxValue(GestureMaxAccX, ListGestureEventsExtended.get(idxEvent).AccelerometerX);
			GestureMaxAccY = Utils.GetInstance().GetUtilsMath().GetMaxValue(GestureMaxAccY, ListGestureEventsExtended.get(idxEvent).AccelerometerY);
			GestureMaxAccZ = Utils.GetInstance().GetUtilsMath().GetMaxValue(GestureMaxAccZ, ListGestureEventsExtended.get(idxEvent).AccelerometerZ);					
			
			try {
				if(idxEvent > 0) {
					x1 = 0; y1 = 0;
	                x2 = ListGestureEventsExtended.get(idxEvent - 1).Xmm; y2 = ListGestureEventsExtended.get(idxEvent - 1).Ymm;
	                x3 = ListGestureEventsExtended.get(idxEvent).Xmm; y3 = ListGestureEventsExtended.get(idxEvent).Ymm;
	                GestureTotalArea += mUtilsMath.CalculateTriangleArea(x1, y1, x2, y2, x3, y3);

	                x1 = 0; y1 = 0;
	                x2 = ListGestureEventsExtended.get(idxEvent - 1).Xmm - mPointMinXMM; y2 = ListGestureEventsExtended.get(idxEvent - 1).Ymm - mPointMinYMM;
	                x3 = ListGestureEventsExtended.get(idxEvent).Xmm - mPointMinXMM; y3 = ListGestureEventsExtended.get(idxEvent).Ymm - mPointMinYMM;
	                GestureTotalAreaMinXMinY += mUtilsMath.CalculateTriangleArea(x1, y1, x2, y2, x3, y3);
				}	
			}
			catch(Exception exc) {
				String msg = exc.getMessage();
			}			
		}
		
		AddGestureValue(Instruction, ConstsParamNames.Gesture.GESTURE_TOTAL_AREA, GestureTotalArea);
		AddGestureValue(Instruction, ConstsParamNames.Gesture.GESTURE_TOTAL_AREA_MINX_MINY, GestureTotalAreaMinXMinY);
	
		GestureAvgAccX = totalAccX / ListGestureEventsExtended.size(); 
		GestureAvgAccY = totalAccY / ListGestureEventsExtended.size();
		GestureAvgAccZ = totalAccZ / ListGestureEventsExtended.size();
	}
	
	private double GetCommonValue(HashMap<Double, ValueFreq> hashValueFreqs) {
		ArrayList<ValueFreq> listValueFreqs = new ArrayList<>();
		
		for(Double key : hashValueFreqs.keySet()) {
			listValueFreqs.add(hashValueFreqs.get(key));
		}
		
		Collections.sort(listValueFreqs, new Comparator<ValueFreq>() {
            @Override
            public int compare(ValueFreq valueFreq1, ValueFreq valueFreq2) {
                if (Math.abs(valueFreq1.GetFreq()) > Math.abs(valueFreq2.GetFreq())) {
                    return -1;
                }
                if (Math.abs(valueFreq1.GetFreq()) < Math.abs(valueFreq2.GetFreq())) {
                    return 1;
                }
                return 0;
            }
        });
		
		return listValueFreqs.get(0).GetValue();
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
		
		AddGestureValue(Instruction, ConstsParamNames.Gesture.GESTURE_MIDDLE_PRESSURE, GestureAvgMiddlePressure);
		AddGestureValue(Instruction, ConstsParamNames.Gesture.GESTURE_MIDDLE_SURFACE, GestureAvgMiddleSurface);		
	}
	
	protected void CalculateGestureVelocityPeaks()
	{
		if(ListStrokesExtended.size() > 0 && !ListStrokesExtended.get(0).IsPoint) {
			if(ListStrokesExtended.get(0).StrokeVelocityPeakAvgPoint != null) {
				IndexValue tempVelocityPeak = ListStrokesExtended.get(0).StrokeVelocityPeakAvgPoint.MaxValueInSection;	
				ParameterAvgPoint velocityAvgPoint = ListStrokesExtended.get(0).StrokeVelocityPeakAvgPoint;
				
				GestureVelocityPeakMax = ((double) tempVelocityPeak.Index) / ((double) ListStrokesExtended.get(0).ListEventsExtended.size());	
							
				double velocityPeakIndexDiff = velocityAvgPoint.IndexEnd.Index - velocityAvgPoint.IndexStart.Index; 
				GestureVelocityPeakIntervalPercentage = velocityPeakIndexDiff / ((double) ListStrokesExtended.get(0).ListEventsExtended.size());
				
				AddGestureValue(Instruction, ConstsParamNames.Gesture.GESTURE_VELOCITY_PEAK, GestureVelocityPeakMax);
				AddGestureValue(Instruction, ConstsParamNames.Gesture.GESTURE_VELOCITY_PEAK_INTERVAL_PERCENTAGE, GestureVelocityPeakIntervalPercentage);	
			}
		}		
	}
	
	protected void CalculateGestureAccelerationPeaks()
	{
		if(ListStrokesExtended.size() > 0 && !ListStrokesExtended.get(0).IsPoint) {
			if(ListStrokesExtended.get(0).StrokeAccelerationPeakAvgPoint != null) {
				IndexValue tempAccelerationPeak = ListStrokesExtended.get(0).StrokeAccelerationPeakAvgPoint.MaxValueInSection;	
				ParameterAvgPoint accelerationAvgPoint = ListStrokesExtended.get(0).StrokeAccelerationPeakAvgPoint;
				
				GestureAccelerationPeakMax = ((double) tempAccelerationPeak.Index) / ((double) ListStrokesExtended.get(0).ListEventsExtended.size());	
							
				double accPeakIndexDiff = accelerationAvgPoint.IndexEnd.Index - accelerationAvgPoint.IndexStart.Index; 
				GestureAccelerationPeakIntervalPercentage = accPeakIndexDiff / ((double) ListStrokesExtended.get(0).ListEventsExtended.size());
				
				AddGestureValue(Instruction, ConstsParamNames.Gesture.GESTURE_ACCELERATION_PEAK, GestureAccelerationPeakMax);
				AddGestureValue(Instruction, ConstsParamNames.Gesture.GESTURE_ACCELERATION_PEAK_INTERVAL_PERCENTAGE, GestureAccelerationPeakIntervalPercentage);	
			}
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
		AddGestureValue(Instruction, ConstsParamNames.Gesture.GESTURE_TOTAL_TIME_INTERVAL, GestureTotalTimeInterval);
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
		double currentPopZScore;
		
		double totalZScore = 0;
		double countZScore = 0;
		
		double zLimit = 2.5;
		
		String instruction, param;	
		
		double tempWeight;
		
		for (IFeatureMeanData featureMeanData : mHashFeatureMeans.values()) {
			if(featureMeanData != null) {
				instruction = featureMeanData.GetInstruction();
				if(instruction == Instruction) {
					param = featureMeanData.GetParamName();
					
					try {
						currentPopZScore = featureMeanData.GetPopulationZScore();
						
						tempWeight = 0;
						if(currentPopZScore > 1.5) {
							tempWeight = 1;
						}
						if(currentPopZScore > 1.8) {
							tempWeight = 2;
						}
						if(currentPopZScore > 1.8) {
							tempWeight = 3;
						}
						if(currentPopZScore > zLimit) {
							currentPopZScore = zLimit;
						}
						
						totalZScore += currentPopZScore * tempWeight;
						countZScore += tempWeight;
						
//						if(currentPopZScore > ConstsGeneral.GESTURE_SCORE_CALC_MIN_Z_SCORE) {
//							if(currentPopZScore > zLimit) {
//								currentPopZScore = zLimit;
//							}
//							totalZScore += currentPopZScore;
//							countZScore++;
//						}		
					}
					catch(Exception exc) {
						
					}	
				}
			}
		}			
		
		double avgZScore = 0;
		if(countZScore > 0) {
			avgZScore = totalZScore / countZScore;	
		}
		
		String strength = "Low";
		
		
		if(ListStrokes.size() >= 2) {
			if(avgZScore > 1.5) {
				strength = "High";
			}
			if(avgZScore > 2 || ListStrokes.size() >= 3) {
				strength = "Very High";
			}	
		}
		else {
			if(avgZScore > 1) {
				strength = "Medium";
			}
			if(avgZScore > 1.5 && countZScore > 2) {
				strength = "High";
			}
			if(avgZScore > 1.8 && countZScore > 3) {
				strength = "Very High";
			}
		}
		
		return strength;
	}
}
