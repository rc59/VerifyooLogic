package Data.UserProfile.Extended;

import java.awt.List;
import java.util.ArrayList;
import java.util.Collections;
import java.util.Comparator;
import java.util.HashMap;

import Consts.ConstsFeatures;
import Consts.ConstsMeasures;
import Consts.ConstsParamNames;
import Data.MetaData.ShapeData;
import Data.MetaData.StrokeProperties;
import Data.MetaData.ValueFreq;
import Data.MetaData.ValueFreqContainer;
import Data.MetaData.ParameterAvgPoint;
import Data.Comparison.Interfaces.ICompareResult;
import Data.MetaData.IndexBoundary;
import Data.MetaData.IndexValue;
import Data.MetaData.InterestPoint;
import Data.UserProfile.Raw.MotionEventCompact;
import Data.UserProfile.Raw.Stroke;
import Logic.Comparison.Stats.FeatureMatrix;
import Logic.Comparison.Stats.FeatureMeanData;
import Logic.Comparison.Stats.FeatureMeanDataListEvents;
import Logic.Comparison.Stats.StatEngine;
import Logic.Comparison.Stats.Interfaces.IFeatureMeanData;
import Logic.Comparison.Stats.Interfaces.IStatEngine;
import Logic.Comparison.Stats.Norms.NormMgr;
import Logic.Comparison.Stats.Norms.Interfaces.INormData;
import Logic.Utils.Complex;
import Logic.Utils.Utils;
import Logic.Utils.UtilsGeneral;
import Logic.Utils.UtilsMath;
import Logic.Utils.UtilsPeakCalc;
import Logic.Utils.UtilsSignalProcessing;

public class StrokeExtended extends Stroke {
	/************** Consts **************/
	
	protected double mMaxDistance;
	
	public static final int DENSITY_STATE_POSITIVE = 1;
	public static final int DENSITY_STATE_NEUTRAL = 0;
	public static final int DENSITY_STATE_NEGATIVE = -1;
	
	/************** Private Members **************/
	private String mInstruction;
	private int mStrokeIdx;
	private int mStrokeKey;
	
	private UtilsSignalProcessing mUtilsSpatialSampling;
	private UtilsMath mUtilsMath;
	protected UtilsGeneral mUtilsGeneral;
	protected UtilsPeakCalc mUtilsPeakCalc;
	
	private MotionEventCompact mPointMinX;
	private MotionEventCompact mPointMaxX;
	private MotionEventCompact mPointMinY;
	private MotionEventCompact mPointMaxY;
	
	public double StrokeCenterXpixel;
	public double StrokeCenterYpixel;	

	public double PointMinXMM;
	public double PointMaxXMM;
	public double PointMinYMM;
	public double PointMaxYMM;
	
	private HashMap<String, IFeatureMeanData> mHashFeatureMeans;
	private double[] mVelocities;
	private double[] mAccelerations;
	/****************************************/
	
	/************** Stroke Features **************/
	public ArrayList<InterestPoint> ListInterestPoints = new ArrayList<>();
	public ArrayList<InterestPoint> ListInterestPointsMinor = new ArrayList<>();
	public double NumInterestPointsMajor;
	public double NumInterestPointsMinor;
	
	public double StrokeAverageDensity = 0;
	
	public boolean IsPoint;
	public int StrokeStartEvent;
	public int StrokeEndEvent;
	
	public ParameterAvgPoint StrokeVelocityPeakAvgPoint;
	public ParameterAvgPoint StrokeAccelerationPeakAvgPoint;
	
	public ArrayList<MotionEventExtended> ListEventsExtended; 
	public ArrayList<MotionEventExtended> ListEventsExtendedInterestZone;
	
	public double StrokeTimeInterval;
		
	public double StrokeMaxRadialVelocity;	
	public double StrokeAverageVelocity;
	public double StrokeMaxVelocity;	
	public double StrokeMidVelocity;
	
	public IndexValue StrokeMaxVelocityWithIndex;

	public double StrokeMaxRadialAcceleration;
	public double StrokeAverageAcceleration;
	public double StrokeMaxAcceleration;
	public double StrokeAverageAccelerationNegative;
				
	public double MiddlePressure;
	public double MiddleSurface;
	
	public boolean IsHasPressure;
	public boolean IsHasTouchSurface;
	
	public double[] SpatialSamplingVector;
	
	public StrokeProperties StrokePropertiesObj;
	public ShapeData ShapeDataObj;
	
	public double[] TimeIntervals;
	public double[] AccumulatedTimeIntervals;
	
	public ArrayList<MotionEventCompact> ListEventsSpatial;
	public ArrayList<MotionEventCompact> ListEventsTemporal;
	
	public ArrayList<MotionEventExtended> ListEventsSpatialExtended;
	public ArrayList<MotionEventExtended> ListEventsTemporalExtended;
	public ArrayList<MotionEventExtended> ListEventsFreqExtended;
	
	public double LengthPixel;
	
	public double StrokeDistanceStartToStart;
	public double StrokeDistanceStartToEnd;
	public double StrokeDistanceEndToStart;
	public double StrokeDistanceEndToEnd;
	
	public double StrokeTransitionTime;
	
	public double StrokeAccMovX;
	public double StrokeAccMovY;
	public double StrokeAccMovZ;
	public double StrokeAccMovTotal;

	/******************** Interest Point Parameters ********************/		
	
	public double InterestPointDensity;
	public double InterestPointIntensity;
	public double InterestPointLocation;
	
	public double InterestPointVelocity;
	public double InterestPointAvgVelocity;
	public double InterestPointPressure;
	public double InterestPointSurface;
	
	public int InterestPointsStartIndex;
	public int InterestPointsEndIndex;	

	/****************************************/
	
	public double ZScoreTotal;
	public double ZScoreCount;
	public double ZScoreMostUnique;

	protected double mMinXnormalized;
	protected double mMinYnormalized;
	
	protected double mNumEvents;
	
	int mNumElementsInRow;
	
	public StrokeExtended(Stroke stroke, HashMap<String, IFeatureMeanData> hashFeatureMeans, String instruction, int strokeIdx)
	{		
		mNumElementsInRow = 0;
	
		ZScoreTotal = 0;
		ZScoreCount = 0;
		ZScoreMostUnique = 0;
		
		if(stroke.Length == 0) {
			IsPoint = true;	
		}
		else {
			InitUtils();
			
			Id = stroke.Id;
			mHashFeatureMeans = hashFeatureMeans;
			mStrokeIdx = strokeIdx;
			mInstruction = instruction;
			LengthPixel = stroke.Length;								
			
			for(int idx = 0; idx < stroke.ListEvents.size(); idx++) {
				ListEvents.add(stroke.ListEvents.get(idx).Clone());
			}			
			
			mNumEvents = (double)stroke.ListEvents.size();
			
			Xdpi = stroke.Xdpi;		
			Ydpi = stroke.Ydpi;
			
			ListEventsExtended = new ArrayList<>();
			
			StrokePropertiesObj = new StrokeProperties(ListEvents.size());
			ShapeDataObj = new ShapeData();
			
			TimeIntervals = new double[ListEvents.size() - 1];
			AccumulatedTimeIntervals = new double[ListEvents.size()];
			
			IsHasPressure = false;
			IsHasTouchSurface = false;		
						
			InitFeatures();
		}
	}
	
	protected void InitUtils()
	{
		mUtilsSpatialSampling = Utils.GetInstance().GetUtilsSignalProcessing();
		mUtilsMath = Utils.GetInstance().GetUtilsMath();
		mUtilsGeneral = Utils.GetInstance().GetUtilsGeneral();
		mUtilsPeakCalc = Utils.GetInstance().GetUtilsPeakCalc();
	}
	
	protected void InitFeatures()
	{
		PreCalculations();
		CalculateFeatures();
		BiasData();
	}
	
	protected void PreCalculations()
	{
		CalculateStrokeCenter();
		CenterAndRotate();
		ListEventsExtended = ListEventsCompactToExtended(ListEvents);		
		MedianFilters();
		
		Normalize();		
		ListEventsExtended = CalculateRadiusAndTeta(ListEventsExtended);
		
		ListEventsExtended = CalculateAccelerationsForEventsList(ListEventsExtended);		
		ListEventsExtended = CalculateRadialVelocityForEventsList(ListEventsExtended);
		
		SpatialSampling();
		NormalizeSpatial();		
				
		CalculateStartEndOfStroke();
		GetStrokeMinBoundaries();
		PrepareData();		
	}	
	
	private void GetStrokeMinBoundaries() {
		mMinXnormalized = Double.MAX_VALUE;
		mMinYnormalized = Double.MAX_VALUE;
		
		for(int idx = 0; idx < ListEventsExtended.size(); idx++) {
			mMinXnormalized = Utils.GetInstance().GetUtilsMath().GetMinValue(mMinXnormalized, ListEventsExtended.get(idx).Xnormalized);
			mMinYnormalized = Utils.GetInstance().GetUtilsMath().GetMinValue(mMinYnormalized, ListEventsExtended.get(idx).Ynormalized);
		}
	}

	private ArrayList<MotionEventExtended> CalculateRadialVelocityForEventsList(ArrayList<MotionEventExtended> listEvents) {
		
		Complex complexCurrent, complexPrev, complexDiff;

		double diffPhase;
		double diffTime;		
		
		double diffRadialVelocity;
		
		listEvents.get(0).RadialVelocity = 0;
		
		for(int idx = 1; idx < listEvents.size(); idx++) {
			complexCurrent = new Complex(listEvents.get(idx).Xmm, listEvents.get(idx).Ymm);
			complexPrev = new Complex(listEvents.get(idx - 1).Xmm, listEvents.get(idx - 1).Ymm);		
			
			complexDiff = complexPrev.divides(complexCurrent);
			diffPhase = listEvents.get(idx).Teta - listEvents.get(idx - 1).Teta;
			
			diffTime = listEvents.get(idx).EventTime - listEvents.get(idx - 1).EventTime; 
			
			if(diffTime > 0) {
				listEvents.get(idx).RadialVelocity = (diffPhase / diffTime);
				diffRadialVelocity = (listEvents.get(idx).RadialVelocity - listEvents.get(idx - 1).RadialVelocity);
				listEvents.get(idx).RadialAcceleration = diffRadialVelocity / diffTime;
			}
			else {
				listEvents.get(idx).RadialVelocity = listEvents.get(idx - 1).RadialVelocity; 
				listEvents.get(idx).RadialAcceleration = listEvents.get(idx - 1).RadialAcceleration;
			}			
		}
		
		return listEvents;
	}
	
	private ArrayList<MotionEventExtended> CalculateRadiusAndTeta(ArrayList<MotionEventExtended> listEvents) {
		
		double x1, y1, x2, y2, x3, y3;
		
		double accumulatedArea = 0;
		
		for(int idx = 0; idx < listEvents.size(); idx++) {
			listEvents.get(idx).Radius = Utils.GetInstance().GetUtilsMath().CalcPitagoras(listEvents.get(idx).Xnormalized, listEvents.get(idx).Ynormalized);
			listEvents.get(idx).Teta = Math.atan2(listEvents.get(idx).Ynormalized, listEvents.get(idx).Xnormalized);
			
			if(idx > 0) {
				x1 = 0; y1 = 0;
                x2 = ListEventsExtended.get(idx - 1).Xnormalized; y2 = ListEventsExtended.get(idx - 1).Ynormalized;
                x3 = ListEventsExtended.get(idx).Xnormalized; y3 = ListEventsExtended.get(idx).Ynormalized;
                ShapeDataObj.ShapeAreaMinXMinY += mUtilsMath.CalculateTriangleArea(x1, y1, x2, y2, x3, y3);
				
                accumulatedArea += mUtilsMath.CalculateTriangleArea(x1, y1, x2, y2, x3, y3);
                listEvents.get(idx).AccumulatedNormalizedArea = accumulatedArea;
                
                listEvents.get(idx).DeltaTeta = listEvents.get(idx).Teta - listEvents.get(idx - 1).Teta;
			}
			else {
				listEvents.get(idx).DeltaTeta = 0;
			}
		}
		
		return listEvents;
	}
	
	private ArrayList<MotionEventExtended> CalculateAccelerationsForEventsList(ArrayList<MotionEventExtended> listEvents) {
		
		double timeDiff, velocityDiff;
		
		listEvents.get(0).Acceleration = 0;
		for(int idx = 1; idx < listEvents.size(); idx++) {
			timeDiff = listEvents.get(idx).EventTime - listEvents.get(idx - 1).EventTime; 
			velocityDiff = listEvents.get(idx).Velocity - listEvents.get(idx - 1).Velocity;
			listEvents.get(idx).Acceleration = velocityDiff / timeDiff;			
		}
		
		return listEvents;
	}

	private void MedianFilters() {
		double[] vectorVelPrefiltering = Utils.GetInstance().GetUtilsVectors().GetVectorVel(ListEventsExtended);		
		
		ListEventsExtended = VelocityMedianFilter(ListEventsExtended);
		
		double[] vectorVelPostfiltering = Utils.GetInstance().GetUtilsVectors().GetVectorVel(ListEventsExtended);
				
		int size = vectorVelPostfiltering.length;
	}
	
	private ArrayList<MotionEventExtended> VelocityMedianFilter(ArrayList<MotionEventExtended> listEvents) {

		double[] velocities = new double[listEvents.size()];
		
		for(int idx = 0; idx < listEvents.size(); idx++) {
			velocities[idx] = listEvents.get(idx).Velocity;
		}
		
		Utils.GetInstance().GetUtilsVectors().MedianFilter(velocities);
		
		for(int idx = 0; idx < listEvents.size(); idx++) {
			listEvents.get(idx).Velocity = velocities[idx];
		}
		
		return listEvents;
	}

	private void SpatialSampling() {
		ListEventsSpatialExtended = mUtilsSpatialSampling.ConvertToVectorByDistance(ListEventsExtended, LengthPixel, Xdpi, Ydpi);
		ListEventsTemporalExtended = mUtilsSpatialSampling.ConvertToVectorByTime(ListEventsExtended, Xdpi, Ydpi);
		ListEventsFreqExtended = mUtilsSpatialSampling.ConvertToVectorByFreq(ListEventsExtended, Xdpi, Ydpi);
		
		AddStrokeListEvents(mInstruction, ConstsParamNames.Stroke.STROKE_SPATIAL_SAMPLING, mStrokeIdx, ListEventsSpatialExtended);
		AddStrokeListEvents(mInstruction, ConstsParamNames.Stroke.STROKE_TEMPORAL_SAMPLING, mStrokeIdx, ListEventsTemporalExtended);		
	}

	private void CenterAndRotate() {
		ListEvents = mUtilsSpatialSampling.CenterAndRotate(ListEvents);
	}
	
	private void BiasData() {				
		for(int idx = 1; idx < ListEventsTemporalExtended.size(); idx++) {
			ListEventsTemporalExtended.get(idx).Velocity = ListEventsTemporalExtended.get(idx).Velocity - StrokeAverageVelocity; 
		}
	}
	
	private void NormalizeSpatial() {
		ListEventsSpatialExtended = mUtilsSpatialSampling.Normalize(ListEventsSpatialExtended);
		ListEventsTemporalExtended = mUtilsSpatialSampling.Normalize(ListEventsTemporalExtended);		
	}	
	
	private void Normalize() {
		ListEventsExtended = mUtilsSpatialSampling.Normalize(ListEventsExtended);
	}	
	
	protected void CalculateStartEndOfStroke()
	{		
		StrokeStartEvent = StrokeEndEvent = 0;
		double velocityTreshold = Consts.ConstsFeatures.MIN_VELOCITY_TRESHOLD * ConstsMeasures.INCH_TO_MM /(1/ Math.sqrt(1/(Xdpi*Xdpi) + 1/(Ydpi*Ydpi))) * Math.pow(10, -3); 
		for(int idxEvent = 0; idxEvent < ListEventsExtended.size(); idxEvent++)
		{
			if(ListEventsExtended.get(idxEvent).Velocity > velocityTreshold)
			{
				IsPoint = false;
				StrokeStartEvent = idxEvent;
				break;
			}
		}
		if(!IsPoint)
		{
			for(int idxEvent = ListEventsExtended.size() - 1; idxEvent > -1; idxEvent--)
			{
				if(ListEventsExtended.get(idxEvent).Velocity > velocityTreshold)
				{
					StrokeEndEvent = idxEvent;
					break;
				}
			}
		}
	}

	protected void CalculateFeatures()
	{		
		CalculateSpatialSamplingVector();
		CalculateStrokeKey();
		StoreFeatures();
		CalculateStrokeInterval();
		CalculateAverageAndMaxVelocity();
		CalculateAverageAndMaxAccelerations();
		CalculateMaxRadial();
		CalculateMiddlePressureAndSurface();
		CalculateStrokeVelocityPeaks();
		CalculateStrokeAccelerationPeaks();
		CalculateStrokeAccelerometer();
		CalculateEventDensityNew();
		CalculateEventDensitySpikes();
		CalculateEventDensity();
//		CalculateDensityArea();
	}	

	private void CalculateEventDensitySpikes() {		
		double stateCurr = DENSITY_STATE_NEUTRAL;
		double stateNext = DENSITY_STATE_NEUTRAL;
		
		boolean isMinimumPointFound = false;
		double consecutivePositives = 0;
		double accumPositives = 0;
		
		double consecutiveNeutrals = 0;
		double accumNeutrals = 0;
		
		double consecutiveNegatives = 0;
		double accumNegatives = 0;
		
		double minConsecutivePositives = 2;
		double minConsecutiveNegatives = 2;
		
		int idxMinBoundary = 0;
		int idxMaxBoundary = 0;
		
		double minDiff = 0.4;
		
		InterestPoint tempInterestPoint;
		
		double diffDensity;
		double diffDensityAccumPositive = 0;
		
		double firstNegativeValue;
		double firstPositiveValue;
		
		int startIdx = 0;
		for(int idxEvent = 0; idxEvent < ListEventsExtended.size() - 1; idxEvent++) {
			diffDensity = Math.abs(ListEventsExtended.get(idxEvent).EventDensity - ListEventsExtended.get(idxEvent + 1).EventDensity);			
			
			if(ListEventsExtended.get(idxEvent).EventDensity < ListEventsExtended.get(idxEvent + 1).EventDensity) {
				consecutivePositives++;
				accumPositives += diffDensity;
			}
			else {
				consecutivePositives = 0;
				accumPositives = 0;
			}
			
//			if(consecutivePositives > minConsecutivePositives) {
			if(accumPositives > minDiff) {
				startIdx = idxEvent;
				break;				
			}
		}
		
		consecutivePositives = 0;
		accumPositives = 0;
		
		consecutiveNegatives = 0;		
		accumNegatives = 0;
		
		for(int idxEvent = startIdx; idxEvent < ListEventsExtended.size() - 1; idxEvent++) {

			diffDensity = Math.abs(ListEventsExtended.get(idxEvent).EventDensity - ListEventsExtended.get(idxEvent + 1).EventDensity);			
			
			stateNext = DENSITY_STATE_NEUTRAL;
			if((ListEventsExtended.get(idxEvent).EventDensity / ListEventsExtended.get(idxEvent + 1).EventDensity) < 0.98) {
				stateNext = DENSITY_STATE_POSITIVE;
			}
			
			if((ListEventsExtended.get(idxEvent).EventDensity / ListEventsExtended.get(idxEvent + 1).EventDensity) > 1.02) {
				stateNext = DENSITY_STATE_NEGATIVE;
				consecutiveNegatives++;
				accumNegatives += diffDensity;
			}
			
			if(stateCurr == DENSITY_STATE_NEGATIVE && stateNext == DENSITY_STATE_POSITIVE && !isMinimumPointFound) {
				idxMinBoundary = idxEvent - (int)consecutiveNeutrals;
												
				isMinimumPointFound = true;
				consecutiveNeutrals = 0;
			}
			
			if(stateNext != DENSITY_STATE_NEUTRAL) {
				consecutiveNeutrals = 0;
			}
			
			if(isMinimumPointFound && (stateCurr == DENSITY_STATE_POSITIVE && (stateNext == DENSITY_STATE_POSITIVE || stateNext == DENSITY_STATE_NEUTRAL))) {
				consecutivePositives++;
				accumPositives += diffDensity;
			}
			else {
				consecutivePositives = 0;
				accumPositives = 0;
			}
			
			if(isMinimumPointFound && (stateCurr == DENSITY_STATE_NEGATIVE && stateNext == DENSITY_STATE_NEGATIVE)) {
				consecutiveNegatives++;
				accumNegatives += diffDensity;
			}
			
			//if(consecutivePositives >= minConsecutivePositives && isMinimumPointFound) {
			if(accumPositives >= minDiff && isMinimumPointFound) {
				idxMaxBoundary = idxEvent - (int)consecutivePositives;
								
				if(idxMaxBoundary == idxMinBoundary) {
					idxMaxBoundary = idxMinBoundary + 1;
				}
				
				if(idxMaxBoundary >= ListEventsExtended.size()) {
					idxMaxBoundary = ListEventsExtended.size() - 1;
				}
				
//				if(consecutiveNegatives > minConsecutiveNegatives) {
				if(accumNegatives > minDiff) {
					tempInterestPoint = new InterestPoint(idxMinBoundary, idxMaxBoundary, mNumEvents);
					//ListInterestPoints.add(tempInterestPoint);
							
					ListEventsExtended.get(idxMinBoundary).EventDensitySignalStrength = -1;
					ListEventsExtended.get(idxMaxBoundary).EventDensitySignalStrength = 1;	
				}
				
				consecutiveNegatives = 0;
				accumNegatives = 0;				
				
				consecutivePositives = 0;
				accumPositives = 0;
				
				isMinimumPointFound = false;
			}
			
			if(stateNext != DENSITY_STATE_NEUTRAL) {
				stateCurr = stateNext;
			}
		}		
		
		if(ListInterestPoints.size() >= 1) {
			InterestPointIntensity = 0;
			
			int idxStart = (int) ListInterestPoints.get(0).IdxStart;
			int idxEnd = (int) ListInterestPoints.get(0).IdxEnd;			
			int idxAvg = (int) ListInterestPoints.get(0).IdxAverage;
			
			InterestPointDensity = ListEventsExtended.get(idxAvg).EventDensity;
						
			double totalTime = ListEventsExtended.get(idxEnd).EventTime - ListEventsExtended.get(idxStart).EventTime;
			double totalDistance = 0;
			
			for(int idx = idxStart; idx <= idxEnd; idx++) {
				InterestPointIntensity += ListEventsExtended.get(idx).EventDensity;
				if(idx > idxStart) {
					totalDistance += Utils.GetInstance().GetUtilsMath().CalcDistanceInMMs(ListEventsExtended.get(idx - 1), ListEventsExtended.get(idx));
				}
			}			
			
			InterestPointVelocity = ListEventsExtended.get(idxAvg).Velocity;		
			InterestPointPressure = ListEventsExtended.get(idxAvg).Pressure;
			InterestPointSurface = ListEventsExtended.get(idxAvg).TouchSurface;
		}
		else {
			int idxMid = ListEventsExtended.size() / 2;
			InterestPointVelocity = ListEventsExtended.get(idxMid).Velocity;			
			InterestPointPressure = ListEventsExtended.get(idxMid).Pressure;
			InterestPointSurface = ListEventsExtended.get(idxMid).TouchSurface;
			InterestPointDensity = ListEventsExtended.get(idxMid).EventDensity;			
		}
		
		AddStrokeValue(mInstruction, ConstsParamNames.Stroke.STROKE_INT_POINT_VELOCITY, mStrokeIdx, InterestPointVelocity);
		
		AddStrokeValue(mInstruction, ConstsParamNames.Stroke.STROKE_INT_POINT_PRESSURE, mStrokeIdx, InterestPointPressure);
		AddStrokeValue(mInstruction, ConstsParamNames.Stroke.STROKE_INT_POINT_SURFACE, mStrokeIdx, InterestPointSurface);
		
		AddStrokeValue(mInstruction, ConstsParamNames.Stroke.STROKE_INT_POINT_DENSITY, mStrokeIdx, InterestPointDensity);
	}

	protected void CalculateEventDensity() {
		boolean isReachedDistance;
		double maxDistance = mMaxDistance;
		double currentDistance;
		double eventDensity;
		
		double[] listDensities = new double[ListEventsExtended.size()];
		int idxCurrent;
		
		for(int idxEvent = 0; idxEvent < ListEventsExtended.size(); idxEvent++) {
			currentDistance = 0;
			eventDensity = 0;			
			isReachedDistance = false;
			idxCurrent = idxEvent;			
			
			if(idxEvent > 0) {
				ListEventsExtended.get(idxEvent).EventDistance = Utils.GetInstance().GetUtilsMath().CalcDistanceInMMs(ListEventsExtended.get(idxEvent - 1), ListEventsExtended.get(idxEvent));	
			}
			
			while(!isReachedDistance) {
				if(idxCurrent + 1 >= ListEventsExtended.size() || currentDistance >= maxDistance) {
					isReachedDistance = true;
				}
				else {
					currentDistance += Utils.GetInstance().GetUtilsMath().CalcDistanceInMMs(ListEventsExtended.get(idxCurrent), ListEventsExtended.get(idxCurrent + 1));
					idxCurrent++;
					eventDensity++;
				}
			}
									
			listDensities[idxEvent] = eventDensity / maxDistance;
		}
		
		Utils.GetInstance().GetUtilsVectors().MedianFilter(listDensities);
		for(int idx = 0; idx < listDensities.length; idx++) {
			ListEventsExtended.get(idx).EventDensityRaw = listDensities[idx];
		}
		
		int idxIntPointAreaStart = 0;
		int idxIntPointAreaEnd = ListEventsExtended.size() - 1;	
				
		ValueFreqContainer valueFreqContainer = new ValueFreqContainer();
		
		for(int idx = 0; idx < ListEventsExtended.size() - 1; idx++) {
			valueFreqContainer.AddValue(ListEventsExtended.get(idx).EventDensityRaw);
		}
		
		double commonDensity = valueFreqContainer.GetMostFreq();
		double commonDensitySecond = valueFreqContainer.GetAllFreqs().get(1).GetValue();		
		
		if(Math.abs(commonDensity - commonDensitySecond) >= 1) {
			commonDensitySecond = commonDensity;
		}
		
		for(int idx = 0; idx < ListEventsExtended.size() - 1; idx++) {
			if(ListEventsExtended.get(idx).EventDensityRaw == commonDensity || ListEventsExtended.get(idx).EventDensityRaw == commonDensitySecond) {
				if(ListEventsExtended.get(idx + 1).EventDensityRaw == commonDensity || ListEventsExtended.get(idx + 1).EventDensityRaw == commonDensitySecond) {
					idxIntPointAreaStart = idx;
					break;	
				}				
			}			
		}
		
		for(int idx = ListEventsExtended.size() - 1; idx > 0; idx--) {
			if(ListEventsExtended.get(idx).EventDensityRaw == commonDensity) {
				if(ListEventsExtended.get(idx - 1).EventDensityRaw == commonDensity) {
					idxIntPointAreaEnd = idx;
					break;	
				}
			}
		}		
		
		for(int idx = idxIntPointAreaStart; idx <= idxIntPointAreaEnd; idx++) {
			StrokeAverageDensity += ListEventsExtended.get(idx).EventDensityRaw;
		}
		
		double numDensityPoints = idxIntPointAreaEnd - idxIntPointAreaStart;
		StrokeAverageDensity = StrokeAverageDensity / numDensityPoints;
		AddStrokeValue(mInstruction, ConstsParamNames.Stroke.STROKE_AVG_DENSITY, mStrokeIdx, StrokeAverageDensity);
		
		ListInterestPoints = Utils.GetInstance().GetUtilsVectors().FindInterestPoints(ListEventsExtended, idxIntPointAreaStart, idxIntPointAreaEnd, 1.2, true, commonDensity, commonDensitySecond);
				
		if(ListInterestPoints.size() > 0) {
			InterestPointLocation = ListInterestPoints.get(0).IdxLocation;
			AddStrokeValue(mInstruction, ConstsParamNames.Stroke.STROKE_INT_POINT_LOCATION, mStrokeIdx, InterestPointLocation);
			
			InterestPointIntensity = 0;
			InterestPointAvgVelocity = 0;
			
			double totalTime = 0;
			double totalDistance = 0;
			
			for(int idx = (int)ListInterestPoints.get(0).IdxStart; idx <= ListInterestPoints.get(0).IdxEnd; idx++) {
				InterestPointIntensity += ListEventsExtended.get(idx).EventDensityRaw;
				
				if(idx > 0) {
					totalTime += ListEventsExtended.get(idx).EventTime - ListEventsExtended.get(idx - 1).EventTime;
					totalDistance += Utils.GetInstance().GetUtilsMath().CalcDistanceInMMs(ListEventsExtended.get(idx - 1), ListEventsExtended.get(idx));
				}
				InterestPointAvgVelocity += ListEventsExtended.get(idx).Velocity;
			}
			InterestPointAvgVelocity = InterestPointAvgVelocity / (ListInterestPoints.get(0).IdxEnd - ListInterestPoints.get(0).IdxStart);
			InterestPointAvgVelocity = totalDistance / totalTime;
						
			AddStrokeValue(mInstruction, ConstsParamNames.Stroke.STROKE_INT_POINT_AVG_VELOCITY, mStrokeIdx, InterestPointAvgVelocity);		
			AddStrokeValue(mInstruction, ConstsParamNames.Stroke.STROKE_INT_POINT_INTENSITY, mStrokeIdx, InterestPointIntensity);
			
			int start = (int) ListInterestPoints.get(0).IdxStart;
			while(true) {
				if(ListEventsExtended.get(start).EventDensityRaw - ListEventsExtended.get(start - 1).EventDensityRaw <= 0) {
					break;
				}
				start--;
				if(start == 0) {
					break;
				}
			}
			ArrayList<MotionEventExtended> listEventsTemp = new ArrayList<>();
			for(int idx = 0; idx < ListEventsExtended.size(); idx++) {
				listEventsTemp.add(ListEventsExtended.get(idx).Clone());
			}
			
			int idxTempStart, idxTempEnd;
			int idxCurrStart, idxCurrEnd;			
			
			for(int idx = 0; idx < ListInterestPoints.size(); idx++) {

				idxCurrStart = (int)ListInterestPoints.get(idx).IdxStart;
				idxCurrEnd = (int)ListInterestPoints.get(idx).IdxEnd;
				
				if(idx == 0) {
					idxTempEnd = idxCurrStart;
					idxTempStart = idxIntPointAreaStart;					
					
					ListInterestPointsMinor.addAll(Utils.GetInstance().GetUtilsVectors().FindInterestPoints(ListEventsExtended, idxTempStart, idxTempEnd, 1, false, commonDensity, commonDensitySecond));
				}
				else {
					idxTempEnd = idxCurrStart;
					idxTempStart = (int)ListInterestPoints.get(idx - 1).IdxEnd;
					
					ListInterestPointsMinor.addAll(Utils.GetInstance().GetUtilsVectors().FindInterestPoints(ListEventsExtended, idxTempStart, idxTempEnd, 1, false, commonDensity, commonDensitySecond));
				}
				
				if((idx + 1) == ListInterestPoints.size()) {
					idxTempStart = idxCurrEnd;
					idxTempEnd = idxIntPointAreaEnd;
					
					ListInterestPointsMinor.addAll(Utils.GetInstance().GetUtilsVectors().FindInterestPoints(ListEventsExtended, idxTempStart, idxTempEnd, 1, false, commonDensity, commonDensitySecond));
				}
				else {
					idxTempStart = (int) ListInterestPoints.get(idx).IdxEnd;
					idxTempEnd = (int)ListInterestPoints.get(idx + 1).IdxStart;
					
					ListInterestPointsMinor.addAll(Utils.GetInstance().GetUtilsVectors().FindInterestPoints(ListEventsExtended, idxTempStart, idxTempEnd, 1, false, commonDensity, commonDensitySecond));
				}
			}
			
//			ListInterestPointsMinor = Utils.GetInstance().GetUtilsVectors().FindInterestPoints(ListEventsExtended, idxStart, idxEnd, 1, false);
			NumInterestPointsMajor = ListInterestPoints.size();
			NumInterestPointsMinor = ListInterestPointsMinor.size();
		}
		else {
			ListInterestPointsMinor = Utils.GetInstance().GetUtilsVectors().FindInterestPoints(ListEventsExtended, idxIntPointAreaStart, idxIntPointAreaEnd, 1, false, commonDensity, commonDensitySecond);
		}
	}
	
	private void CalculateEventDensityNew() {
		double[] eventDensities = new double[ListEventsExtended.size()];
		double tempDistance;
		
		mMaxDistance = Double.MIN_VALUE;
		for(int idxEvent = 0; idxEvent < ListEventsExtended.size(); idxEvent++) {
			if(idxEvent > 0) {
				tempDistance = Utils.GetInstance().GetUtilsMath().CalcDistanceInMMs(ListEventsExtended.get(idxEvent - 1), ListEventsExtended.get(idxEvent));
				mMaxDistance = Utils.GetInstance().GetUtilsMath().GetMaxValue(mMaxDistance, tempDistance);
			}
			
			
			if(idxEvent == 0) {
				eventDensities[idxEvent] = 2 * Utils.GetInstance().GetUtilsMath().CalcDistanceInMMs(ListEventsExtended.get(idxEvent), ListEventsExtended.get(idxEvent + 1));
			}
			else {
				if((idxEvent + 1) == ListEventsExtended.size()) {
					eventDensities[idxEvent] = 2 * Utils.GetInstance().GetUtilsMath().CalcDistanceInMMs(ListEventsExtended.get(idxEvent), ListEventsExtended.get(idxEvent - 1));					
				}
				else {
					eventDensities[idxEvent] = GetDensityNew(ListEventsExtended.get(idxEvent - 1), ListEventsExtended.get(idxEvent), ListEventsExtended.get(idxEvent + 1));		
				}
			}
		}
		
		Utils.GetInstance().GetUtilsVectors().MedianFilter(eventDensities);
		Utils.GetInstance().GetUtilsVectors().AverageFilter(eventDensities);
		
		for(int idxDensity = 0; idxDensity < eventDensities.length; idxDensity++) {
			ListEventsExtended.get(idxDensity).EventDensity = eventDensities[idxDensity]; 
		}			
	}

	private void CalculateStrokeKey() {
		try {
		mStrokeKey = NormMgr.GetInstance().GetStrokeKey(this);
		}
		catch(Exception exc) {
			mStrokeKey = mStrokeIdx;
		}
	}

	private void StoreFeatures() {
		AddStrokeValue(mInstruction, ConstsParamNames.Stroke.STROKE_LENGTH, mStrokeIdx, StrokePropertiesObj.LengthMM);
		AddStrokeValue(mInstruction, ConstsParamNames.Stroke.STROKE_NUM_EVENTS, mStrokeIdx, ListEventsExtended.size());
		AddStrokeValue(mInstruction, ConstsParamNames.Stroke.STROKE_TOTAL_AREA, mStrokeIdx, ShapeDataObj.ShapeArea);
		AddStrokeValue(mInstruction, ConstsParamNames.Stroke.STROKE_TOTAL_AREA_MINX_MINY, mStrokeIdx, ShapeDataObj.ShapeAreaMinXMinY);
	}

	protected int GetDensityStartIndex() {
		int startIndex = 0;
		for(int idx = 0; idx < ListEventsExtended.size(); idx++) {
			if(ListEventsExtended.get(idx).EventDensity <= Consts.ConstsMeasures.MIN_DENSITY) {
				startIndex = idx;
				break;
			}
		}
		
		return startIndex;
	}
	
	protected int GetDensityEndIndex() {
		int endIndex = 0;
		for(int idx = ListEventsExtended.size() - 3; idx >= 0; idx--) {
			if(ListEventsExtended.get(idx).EventDensity <= Consts.ConstsMeasures.MIN_DENSITY) {
				endIndex = idx;
				break;
			}
		}
		
		return endIndex;
	}

	private double GetDensityNew(MotionEventExtended eventPrev, MotionEventExtended eventCurr, MotionEventExtended eventNext) {
		double density = 0;
		
		double distance1 = Utils.GetInstance().GetUtilsMath().CalcDistanceInMMs(eventPrev, eventCurr);
		double distance2 = Utils.GetInstance().GetUtilsMath().CalcDistanceInMMs(eventCurr, eventNext);
		
		density = distance1 + distance2;
		return density;
	}

	protected void ConvertToMotionEventExtended()
	{			
		ListEventsSpatialExtended = ListEventsCompactToExtended(ListEventsSpatial);
		ListEventsTemporalExtended = ListEventsCompactToExtended(ListEventsTemporal);
	}
	
	protected ArrayList<MotionEventExtended> ListEventsCompactToExtended(ArrayList<MotionEventCompact> listEventsCompact)
	{
		ArrayList<MotionEventExtended> listEventsExtended = Utils.GetInstance().GetUtilsComparison().ListEventsCompactToExtended(listEventsCompact, Xdpi, Ydpi);
		return listEventsExtended;
	}
	
	protected void PrepareData()
	{
		double deltaX, deltaY;
		
		mVelocities = new double[ListEventsExtended.size()];
		mAccelerations = new double[ListEventsExtended.size()];

		StrokeMaxVelocity = Double.MIN_VALUE;
		StrokeMaxAcceleration = Double.MIN_VALUE;
		
		for(int idxEvent = 0; idxEvent < ListEventsExtended.size(); idxEvent++)
		{
			mVelocities[idxEvent] = ListEventsExtended.get(idxEvent).Velocity;			
			mAccelerations[idxEvent] = ListEventsExtended.get(idxEvent).Acceleration;
						
			StrokeMaxVelocity = Utils.GetInstance().GetUtilsMath().GetMaxValue(ListEventsExtended.get(idxEvent).Velocity, StrokeMaxVelocity);
			StrokeMaxAcceleration = Utils.GetInstance().GetUtilsMath().GetMaxValue(ListEventsExtended.get(idxEvent).Acceleration, StrokeMaxAcceleration);		
		}
		
		if(ListEventsExtended.size() > 5)
		{
			Utils.GetInstance().GetUtilsVectors().MedianFilter(mVelocities);
			Utils.GetInstance().GetUtilsVectors().MedianFilter(mAccelerations);
		}
		
		double velocityDiff;
		double timeDiff;		
		
		for(int idxEvent = 1; idxEvent < ListEventsExtended.size(); idxEvent++)
		{
			velocityDiff = mVelocities[idxEvent] - mVelocities[idxEvent - 1];
			timeDiff = ListEventsExtended.get(idxEvent).EventTime - ListEventsExtended.get(idxEvent - 1).EventTime; 
			
			if(timeDiff > 0) {
				mAccelerations[idxEvent] = velocityDiff / timeDiff;
			}
			else {
				mAccelerations[idxEvent] = 0;
			}				
		}
		Utils.GetInstance().GetUtilsVectors().MedianFilter(mAccelerations);		
		
		StrokeMaxVelocityWithIndex = new IndexValue();
		StrokeMaxVelocityWithIndex.Index = 0;
		StrokeMaxVelocityWithIndex.Value = 0;
		StrokePropertiesObj.AccumulatedLength[0] = 0;
				
		ShapeDataObj.ShapeAreaMinXMinY = 0;
		
		double totalAcc = 0;		
		double x1, y1, x2, y2, x3, y3;
		double tmpArea = 0;		
		
		for(int idxEvent = 0; idxEvent < ListEventsExtended.size(); idxEvent++)
		{

			if(mVelocities[idxEvent] > StrokeMaxVelocityWithIndex.Value)
			{
				StrokeMaxVelocityWithIndex.Value = mVelocities[idxEvent];
				StrokeMaxVelocityWithIndex.Index = idxEvent;
			}
			
			if(idxEvent > 0) {
				deltaX = ListEventsExtended.get(idxEvent).Xmm - ListEventsExtended.get(idxEvent - 1).Xmm;
                deltaY = ListEventsExtended.get(idxEvent).Ymm - ListEventsExtended.get(idxEvent - 1).Ymm;
                
                ListEventsExtended.get(idxEvent-1).Angle = Math.atan2(deltaY, deltaX);

                StrokePropertiesObj.ListDeltaXmm[idxEvent - 1] = deltaX;
                StrokePropertiesObj.ListDeltaYmm[idxEvent - 1] = deltaY;              
				
                StrokePropertiesObj.ListEventLength[idxEvent - 1] = mUtilsMath.CalcPitagoras(deltaX, deltaY);
                StrokePropertiesObj.LengthMM += StrokePropertiesObj.ListEventLength[idxEvent - 1];                
                StrokePropertiesObj.AccumulatedLength[idxEvent - 1] = StrokePropertiesObj.LengthMM;
                
                x1 = 0; y1 = 0;
                x2 = ListEventsExtended.get(idxEvent - 1).Xnormalized; y2 = ListEventsExtended.get(idxEvent - 1).Ynormalized;
                x3 = ListEventsExtended.get(idxEvent).Xnormalized; y3 = ListEventsExtended.get(idxEvent).Ynormalized;
                ShapeDataObj.ShapeArea += mUtilsMath.CalculateTriangleArea(x1, y1, x2, y2, x3, y3);

                x1 = 0; y1 = 0;
                x2 = ListEventsExtended.get(idxEvent - 1).Xnormalized - mMinXnormalized; y2 = ListEventsExtended.get(idxEvent - 1).Ynormalized - mMinYnormalized;
                x3 = ListEventsExtended.get(idxEvent).Xnormalized - mMinXnormalized; y3 = ListEventsExtended.get(idxEvent).Ynormalized - mMinYnormalized;
                ShapeDataObj.ShapeAreaMinXMinY += mUtilsMath.CalculateTriangleArea(x1, y1, x2, y2, x3, y3);
                
                if(CheckIfPressureExists(ListEventsExtended.get(idxEvent))) {
                	IsHasPressure = true;
                }
            	if(CheckIfSurfaceExists(ListEventsExtended.get(idxEvent))) {
            		IsHasTouchSurface = true;
                }
            	
            	TimeIntervals[idxEvent - 1] = ListEventsExtended.get(idxEvent).EventTime - ListEventsExtended.get(idxEvent - 1).EventTime;
            	AccumulatedTimeIntervals[idxEvent] = AccumulatedTimeIntervals[idxEvent - 1] + TimeIntervals[idxEvent - 1]; 
			}
			else {
				AccumulatedTimeIntervals[idxEvent] = 0;
			}
		}
		for(int idxEvent = 1; idxEvent < ListEventsExtended.size() - 1; idxEvent++)
		{
			ListEventsExtended.get(idxEvent-1).AngleDiff = mUtilsMath.CalcAbsAngleDifference(ListEventsExtended.get(idxEvent).Angle, ListEventsExtended.get(idxEvent - 1).Angle);
			totalAcc += mAccelerations[idxEvent];			
		}
				
//		ShapeDataObj.ShapeArea = Math.sqrt(ShapeDataObj.ShapeArea);
//		ShapeDataObj.ShapeAreaMinXMinY = Math.sqrt(ShapeDataObj.ShapeAreaMinXMinY);
		
		StrokeAverageAcceleration = totalAcc / ListEventsExtended.size();
	}
	
	protected void CalculateStrokeAccelerometer() {
		double accMeanX = 0;
		double accMeanY = 0;
		double accMeanZ = 0;
		double accMean = 0;
		
		for(int idxEvent = 0; idxEvent < ListEventsExtended.size(); idxEvent++) {
			accMeanX += ListEventsExtended.get(idxEvent).AccelerometerX;
			accMeanY += ListEventsExtended.get(idxEvent).AccelerometerY;
			accMeanZ += ListEventsExtended.get(idxEvent).AccelerometerZ;
			accMean += Utils.GetInstance().GetUtilsMath().CalcPitagoras(ListEventsExtended.get(idxEvent).AccelerometerX, ListEventsExtended.get(idxEvent).AccelerometerY, ListEventsExtended.get(idxEvent).AccelerometerZ);
		}
		
		StrokeAccMovX = 0;
		StrokeAccMovY = 0;
		StrokeAccMovZ = 0;
		StrokeAccMovTotal = 0;
		
		for(int idxEvent = 0; idxEvent < ListEventsExtended.size(); idxEvent++) {
			StrokeAccMovX += Math.abs(accMeanX - ListEventsExtended.get(idxEvent).AccelerometerX);
			StrokeAccMovY += Math.abs(accMeanY - ListEventsExtended.get(idxEvent).AccelerometerY);
			StrokeAccMovZ += Math.abs(accMeanZ - ListEventsExtended.get(idxEvent).AccelerometerZ);
			StrokeAccMovTotal += Math.abs(accMean - Utils.GetInstance().GetUtilsMath().CalcPitagoras(ListEventsExtended.get(idxEvent).AccelerometerX, ListEventsExtended.get(idxEvent).AccelerometerY, ListEventsExtended.get(idxEvent).AccelerometerZ));
		}
	}
	
	protected void CalculateStrokeAccelerationPeaks()
	{
		StrokeAccelerationPeakAvgPoint = mUtilsPeakCalc.CalculatePeaks(mAccelerations, StrokeAverageAcceleration);
	}
	
	protected void CalculateStrokeVelocityPeaks()
	{		
		StrokeVelocityPeakAvgPoint = mUtilsPeakCalc.CalculatePeaks(mVelocities, StrokeAverageVelocity);
		if(StrokeVelocityPeakAvgPoint != null) {
			int startIdx = StrokeVelocityPeakAvgPoint.IndexStart.Index - 1;
			if(startIdx < 0) startIdx = 0;
			int endIdx = StrokeVelocityPeakAvgPoint.IndexEnd.Index - 1;
			if(endIdx >= StrokePropertiesObj.AccumulatedLength.length)
			{
				endIdx = StrokePropertiesObj.AccumulatedLength.length - 1;
			}
			if(endIdx < 0) endIdx = 0;
			double length = StrokePropertiesObj.AccumulatedLength[endIdx] - StrokePropertiesObj.AccumulatedLength[startIdx];
			StrokeVelocityPeakAvgPoint.PercentageOfLength = length / StrokePropertiesObj.LengthMM;	
		}
	}

	protected boolean CheckIfPressureExists(MotionEventExtended event) {
		if(event.Pressure != 0 && event.Pressure != 1) {
			return true;
		}
		else {
			return false;
		}
	}
	
	protected boolean CheckIfSurfaceExists(MotionEventExtended event) {
		if(event.Pressure != 0 && event.Pressure != 1) {
			return true;
		}
		else {
			return false;
		}
	}
	
	protected void CalculateAverageAndMaxAccelerations()
	{
		AddStrokeValue(mInstruction, ConstsParamNames.Stroke.STROKE_AVERAGE_ACCELERATION, mStrokeIdx, StrokeAverageAcceleration);		
		AddStrokeValue(mInstruction, ConstsParamNames.Stroke.STROKE_MAX_ACCELERATION, mStrokeIdx, StrokeMaxAcceleration);
	}
	
	protected void CalculateAverageAndMaxVelocity()
	{
		StrokeAverageVelocity = StrokePropertiesObj.LengthMM / StrokeTimeInterval;
		StrokeMidVelocity = mVelocities[mVelocities.length / 2];
		
		AddStrokeValue(mInstruction, ConstsParamNames.Stroke.STROKE_AVERAGE_VELOCITY, mStrokeIdx, StrokeAverageVelocity);		
		AddStrokeValue(mInstruction, ConstsParamNames.Stroke.STROKE_MAX_VELOCITY, mStrokeIdx, StrokeMaxVelocity);
		AddStrokeValue(mInstruction, ConstsParamNames.Stroke.STROKE_MID_VELOCITY, mStrokeIdx, StrokeMidVelocity);
	}
	
	protected void CalculateMaxRadial()
	{
		double maxRadVel = Double.MIN_VALUE;
		double maxRadAcc = Double.MIN_VALUE;
		
		for(int idx = 0; idx < ListEventsExtended.size(); idx++) {
			maxRadVel = Utils.GetInstance().GetUtilsMath().GetMaxValue(maxRadVel, Math.abs(ListEventsExtended.get(idx).RadialVelocity));
			maxRadAcc = Utils.GetInstance().GetUtilsMath().GetMaxValue(maxRadAcc, Math.abs(ListEventsExtended.get(idx).RadialAcceleration));
		}
		
		StrokeMaxRadialVelocity = maxRadVel;
		StrokeMaxRadialAcceleration = maxRadAcc;
		
		AddStrokeValue(mInstruction, ConstsParamNames.Stroke.STROKE_MAX_RADIAL_VELOCITY, mStrokeIdx, StrokeMaxRadialVelocity);		
		AddStrokeValue(mInstruction, ConstsParamNames.Stroke.STROKE_MAX_RADIAL_ACCELERATION, mStrokeIdx, StrokeMaxRadialAcceleration);		
	}
	
	protected void CalculateMiddlePressureAndSurface()
	{
		int idxMiddle = ListEventsExtended.size() / 2;
		MiddlePressure = ListEventsExtended.get(idxMiddle).Pressure;
		MiddleSurface = ListEventsExtended.get(idxMiddle).TouchSurface;
		
		AddStrokeValue(mInstruction, ConstsParamNames.Stroke.STROKE_MIDDLE_PRESSURE, mStrokeIdx, MiddlePressure);
		AddStrokeValue(mInstruction, ConstsParamNames.Stroke.STROKE_MIDDLE_SURFACE, mStrokeIdx, MiddleSurface);
	}
	
	protected void CalculateStrokeInterval()
	{
		StrokeTimeInterval = ListEventsExtended.get(ListEventsExtended.size() - 1).EventTime - ListEventsExtended.get(0).EventTime;		
		AddStrokeValue(mInstruction, ConstsParamNames.Stroke.STROKE_TIME_INTERVAL, mStrokeIdx, StrokeTimeInterval);
	}
	
	protected void CalculateStrokeCenter()
	{
		MotionEventCompact eventCurrent;		
		
		for (int idxEvent = 0; idxEvent < ListEvents.size(); idxEvent++) {
            eventCurrent = ListEvents.get(idxEvent);            

            if (idxEvent == 0) {
                mPointMinX = eventCurrent;
                mPointMaxX = eventCurrent;
                mPointMinY = eventCurrent;
                mPointMaxY = eventCurrent;
            }
            else {               
                mPointMinX = GetMinXpixel(mPointMinX, eventCurrent);
                mPointMaxX = GetMaxXpixel(mPointMaxX, eventCurrent);
                mPointMinY = GetMinYpixel(mPointMinY, eventCurrent);
                mPointMaxY = GetMaxYpixel(mPointMaxY, eventCurrent);
            }
        }
		
		StrokeCenterXpixel = (mPointMinX.Xpixel + mPointMaxX.Xpixel) / 2;
        StrokeCenterYpixel = (mPointMinY.Ypixel + mPointMaxY.Ypixel) / 2;

        PointMinXMM = (mPointMinX.Xpixel - StrokeCenterXpixel) / Xdpi * ConstsMeasures.INCH_TO_MM;
        PointMaxXMM = (mPointMaxX.Xpixel - StrokeCenterXpixel) / Xdpi * ConstsMeasures.INCH_TO_MM;
        PointMinYMM = (mPointMinY.Ypixel - StrokeCenterYpixel) / Ydpi * ConstsMeasures.INCH_TO_MM;
        PointMaxYMM = (mPointMaxY.Ypixel - StrokeCenterYpixel) / Ydpi * ConstsMeasures.INCH_TO_MM;
	}
	
	protected void CalculateSpatialSamplingVector()
	{
		SpatialSamplingVector = mUtilsSpatialSampling.PrepareDataSpatialSampling(ListEventsExtended, LengthPixel, Xdpi, Ydpi);
	}
	
	protected MotionEventCompact GetMaxXpixel(MotionEventCompact pointA, MotionEventCompact pointB){
        if(pointA.Xpixel > pointB.Xpixel)
            return pointA;
        return pointB;
    }

	protected MotionEventCompact GetMinXpixel(MotionEventCompact pointA, MotionEventCompact pointB){
        if(pointA.Xpixel > pointB.Xpixel)
            return pointB;
        return pointA;
    }

	protected MotionEventCompact GetMaxYpixel(MotionEventCompact pointA, MotionEventCompact pointB){
        if(pointA.Ypixel > pointB.Ypixel)
            return pointA;
        return pointB;
    }

	protected MotionEventCompact GetMinYpixel(MotionEventCompact pointA, MotionEventCompact pointB){
        if(pointA.Ypixel > pointB.Ypixel)
            return pointB;
        return pointA;
    }
	
	protected void AddStrokeValue(String instruction, String paramName, int strokeIdx, double value)
	{		
		String key = mUtilsGeneral.GenerateStrokeFeatureMeanKey(instruction, paramName, mStrokeIdx);		
		String keyMatrix = mUtilsGeneral.GenerateStrokeMatrixMeanKey(instruction, strokeIdx);
		
		IFeatureMeanData tempFeatureMeanData;
		IFeatureMeanData tempFeatureMatrix;
		
		if(mHashFeatureMeans.containsKey(key)) {
			tempFeatureMeanData = mHashFeatureMeans.get(key);
		}
		else {
			tempFeatureMeanData = new FeatureMeanData(paramName, instruction);			
			mHashFeatureMeans.put(key, tempFeatureMeanData);
		}
		tempFeatureMeanData.AddValue(value);
		
		if(paramName.compareTo("StrokeTransitionTime") != 0) {
			if(mHashFeatureMeans.containsKey(keyMatrix)) {
				tempFeatureMatrix = mHashFeatureMeans.get(keyMatrix);
			}
			else {
				tempFeatureMatrix = new FeatureMatrix("FeatureMatrix", instruction);
				mHashFeatureMeans.put(keyMatrix, tempFeatureMatrix);
			}
		
			if(paramName.compareTo("StrokeMiddleSurface") == 0) {
				((FeatureMatrix)tempFeatureMatrix).AddValue(value, true);
			}
			else {
				((FeatureMatrix)tempFeatureMatrix).AddValue(value, false);
			}
		}
		
		try {
			INormData normObj = NormMgr.GetInstance().GetNormDataByParamName(paramName, instruction, strokeIdx);
			UpdateZScore(value, normObj);	
		}
		catch(Exception exc){
			
		}
	}
	
	protected void UpdateZScore(double value, INormData normObj) {
		if(normObj != null) {
			double tempZScore = Math.abs((value - normObj.GetMean()) / normObj.GetStandardDev());
			if(tempZScore > 2) {
				tempZScore = 2;
			}		
			
			ZScoreTotal+= tempZScore;
			ZScoreCount++;
			
			ZScoreMostUnique = Utils.GetInstance().GetUtilsMath().GetMaxValue(ZScoreMostUnique, tempZScore);
		}
	}
	
	protected void AddStrokeListEvents(String instruction, String paramName, int idxStroke, ArrayList<MotionEventExtended> listEventsExtended)
	{
		String key = mUtilsGeneral.GenerateStrokeFeatureMeanKey(instruction, paramName, idxStroke);
		
		IFeatureMeanData tempFeatureMeanData;
		
		if(mHashFeatureMeans.containsKey(key)) {
			tempFeatureMeanData = mHashFeatureMeans.get(key);
		}
		else {
			tempFeatureMeanData = new FeatureMeanDataListEvents(paramName, instruction, idxStroke, Xdpi, Ydpi);			
			mHashFeatureMeans.put(key, tempFeatureMeanData);
		}
		
		((FeatureMeanDataListEvents)tempFeatureMeanData).AddValue(listEventsExtended);		
	}
	
	public double[] GetFilteredVelocities() {
		return mVelocities;
	}
	
	public double[] GetFilteredAccelerations() {
		return mAccelerations;
	}
	
	public HashMap<String, IFeatureMeanData> GetFeatureMeansHash() 
	{
		return mHashFeatureMeans;
	}
	
	public int GetStrokeIdx()
	{
		return mStrokeIdx;
	}
	
	public int GetStrokeKey()
	{
		return mStrokeKey;
	}
	
	public String GetInstruction(){
		return mInstruction;
	}
	
}
