package Data.UserProfile.Extended;

import java.util.ArrayList;
import java.util.HashMap;

import Consts.ConstsFeatures;
import Consts.ConstsMeasures;
import Consts.ConstsParamNames;
import Data.MetaData.ShapeData;
import Data.MetaData.StrokeProperties;
import Data.MetaData.ParameterAvgPoint;
import Data.MetaData.IndexBoundary;
import Data.MetaData.IndexValue;
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
	/************** Private Members **************/
	private String mInstruction;
	private int mStrokeIdx;
	
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
	
	public int MaxInterestPointBoundaryMin;
	public int MaxInterestPointBoundaryMax;
	public int MaxInterestPointIndex;
	
	public double MaxInterestPointAvgVelocity;
	public double MaxInterestPointMaxVelocity;
	public double MaxInterestPointMaxAcceleration;
	public double MaxInterestPointDeltaTeta;
	public double MaxInterestPointLocation;
	public double MaxInterestPointDensity;
	public double MaxInterestPointVelocity;
	public double MaxInterestPointAcceleration;
	public double MaxInterestPointPressure;
	public double MaxInterestPointSurface;
	
	public int InterestPointsStartIndex;
	public int InterestPointsEndIndex;
	
	/****************************************/
	
	public double ZScoreTotal;
	public double ZScoreCount;
	public double ZScoreMostUnique;
	
	int mNumElementsInRow;
	double[] mMatrixRow;	
	
	public StrokeExtended(Stroke stroke, HashMap<String, IFeatureMeanData> hashFeatureMeans, String instruction, int strokeIdx)
	{		
		mNumElementsInRow = 0;
		mMatrixRow = new double[13];

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
		PrepareData();
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
		CalculateStrokeInterval();
		CalculateAverageAndMaxVelocity();
		CalculateAverageAndMaxAccelerations();
		CalculateMaxRadial();
		CalculateMiddlePressureAndSurface();
		CalculateStrokeVelocityPeaks();
		CalculateStrokeAccelerationPeaks();
		CalculateStrokeAccelerometer();
		CalculateEventDensity();
		CalculateDensityArea();
	}	
	
	protected void CalculateDensityArea() {
		double[] densityAreas = new double[ListEventsExtended.size()];
		
		for(int idx = 0; idx < ListEventsExtended.size(); idx++) {
			densityAreas[idx] = ListEventsExtended.get(idx).EventDensity - 1;
		}
		
		double[] listSignalStrengths = Utils.GetInstance().GetUtilsSignalProcessing().CalculateSignalStrength(densityAreas);
		
		MaxInterestPointDensity = Double.MIN_VALUE;
		for(int idx = 0; idx < listSignalStrengths.length; idx++) {
			ListEventsExtended.get(idx).EventDensitySignalStrength = listSignalStrengths[idx];
			if(ListEventsExtended.get(idx).EventDensitySignalStrength > MaxInterestPointDensity) {
				MaxInterestPointDensity = ListEventsExtended.get(idx).EventDensity;				
			}
		}
		
		MaxInterestPointLocation = (double)MaxInterestPointIndex / (double) ListEventsExtended.size();
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
	
	protected void CalculateEventDensity() {
		boolean isReachedDistance;
		double maxDistance = 0.7;
		double currentDistance;
		double eventDensity;
		
		int idxCurrent;
		
		MaxInterestPointDensity = Double.MIN_VALUE;
		
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
							
			ListEventsExtended.get(idxEvent).EventDensity = eventDensity;
		}
		
		InterestPointsStartIndex = GetDensityStartIndex();
		InterestPointsEndIndex = GetDensityEndIndex();
		for(int idxEvent = InterestPointsStartIndex; idxEvent <= InterestPointsEndIndex; idxEvent++) {
			eventDensity = ListEventsExtended.get(idxEvent).EventDensity;
			if(eventDensity > MaxInterestPointDensity) {
				MaxInterestPointDensity = eventDensity;
				MaxInterestPointIndex = idxEvent;
			}			
		}

		FindInterestPointBoundary(MaxInterestPointIndex);
		ExtractInterestPointFeatures(MaxInterestPointBoundaryMin, MaxInterestPointBoundaryMax);
				
		MaxInterestPointDensity = ListEventsExtended.get(MaxInterestPointIndex).EventDensity;
		MaxInterestPointLocation = (double)MaxInterestPointIndex / (double) ListEventsExtended.size();		
		MaxInterestPointPressure = ListEventsExtended.get(MaxInterestPointIndex).Pressure;
		MaxInterestPointSurface = ListEventsExtended.get(MaxInterestPointIndex).TouchSurface;
		MaxInterestPointVelocity = ListEventsExtended.get(MaxInterestPointIndex).Velocity;
		MaxInterestPointAcceleration = ListEventsExtended.get(MaxInterestPointIndex).Acceleration;
		
//		AddStrokeValue(mInstruction, ConstsParamNames.Stroke.InterestPoints.STROKE_MAX_INTEREST_POINT_INDEX, mStrokeIdx, MaxInterestPointIndex);
//		AddStrokeValue(mInstruction, ConstsParamNames.Stroke.InterestPoints.STROKE_MAX_INTEREST_POINT_DENSITY, mStrokeIdx, MaxInterestPointDensity);
//		AddStrokeValue(mInstruction, ConstsParamNames.Stroke.InterestPoints.STROKE_MAX_INTEREST_POINT_LOCATION, mStrokeIdx, MaxInterestPointLocation);
//		AddStrokeValue(mInstruction, ConstsParamNames.Stroke.InterestPoints.STROKE_MAX_INTEREST_POINT_PRESSURE, mStrokeIdx, MaxInterestPointPressure);
//		AddStrokeValue(mInstruction, ConstsParamNames.Stroke.InterestPoints.STROKE_MAX_INTEREST_POINT_SURFACE, mStrokeIdx, MaxInterestPointSurface);
//		AddStrokeValue(mInstruction, ConstsParamNames.Stroke.InterestPoints.STROKE_MAX_INTEREST_POINT_VELOCITY, mStrokeIdx, MaxInterestPointVelocity);
//		AddStrokeValue(mInstruction, ConstsParamNames.Stroke.InterestPoints.STROKE_MAX_INTEREST_POINT_ACCELERATION, mStrokeIdx, MaxInterestPointAcceleration);
	}

	private void ExtractInterestPointFeatures(int idxMin, int idxMax) {
		MaxInterestPointDeltaTeta = Double.MIN_VALUE;
		MaxInterestPointMaxVelocity = Double.MIN_VALUE;
		MaxInterestPointMaxAcceleration = Double.MIN_VALUE;
		MaxInterestPointAvgVelocity = 0;
		
		MotionEventExtended tempEvent;
		double numEvents = idxMax - idxMax + 1;

		ListEventsExtendedInterestZone = new ArrayList<>();
		
		for(int idx = idxMin; idx <= idxMax; idx++) {
			tempEvent = ListEventsExtended.get(idx);
			ListEventsExtendedInterestZone.add(tempEvent);
			
			MaxInterestPointDeltaTeta = Utils.GetInstance().GetUtilsMath().GetMaxValue(Math.abs(tempEvent.DeltaTeta), MaxInterestPointDeltaTeta);
			MaxInterestPointAvgVelocity += tempEvent.Velocity;
			MaxInterestPointMaxVelocity = Utils.GetInstance().GetUtilsMath().GetMaxValue(tempEvent.Velocity, MaxInterestPointMaxVelocity);
			MaxInterestPointMaxAcceleration = Utils.GetInstance().GetUtilsMath().GetMaxValue(Math.abs(tempEvent.Acceleration), MaxInterestPointMaxAcceleration);
		}
		
		MaxInterestPointAvgVelocity = MaxInterestPointAvgVelocity / numEvents;
	}

	private void FindInterestPointBoundary(int idxInterestPoint) {				
		IndexBoundary indexBoundary = Utils.GetInstance().GetUtilsSignalProcessing().FindInterestPointBoundary(idxInterestPoint, ListEventsExtended);
		
		MaxInterestPointBoundaryMax = indexBoundary.UpperBoundary;
		MaxInterestPointBoundaryMin = indexBoundary.LowerBoundary;		
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
                x2 = ListEventsExtended.get(idxEvent - 1).Xmm; y2 = ListEventsExtended.get(idxEvent - 1).Ymm;
                x3 = ListEventsExtended.get(idxEvent).Xmm; y3 = ListEventsExtended.get(idxEvent).Ymm;
                ShapeDataObj.ShapeArea += mUtilsMath.CalculateTriangleArea(x1, y1, x2, y2, x3, y3);

                x1 = 0; y1 = 0;
                x2 = ListEventsExtended.get(idxEvent - 1).Xmm - PointMinXMM; y2 = ListEventsExtended.get(idxEvent - 1).Ymm - PointMinYMM;
                x3 = ListEventsExtended.get(idxEvent).Xmm - PointMinXMM; y3 = ListEventsExtended.get(idxEvent).Ymm - PointMinYMM;
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
		
		AddStrokeValue(mInstruction, ConstsParamNames.Stroke.STROKE_LENGTH, mStrokeIdx, StrokePropertiesObj.LengthMM);
		AddStrokeValue(mInstruction, ConstsParamNames.Stroke.STROKE_NUM_EVENTS, mStrokeIdx, ListEventsExtended.size());
		
		ShapeDataObj.ShapeArea = Math.sqrt(ShapeDataObj.ShapeArea);
		ShapeDataObj.ShapeAreaMinXMinY = Math.sqrt(ShapeDataObj.ShapeAreaMinXMinY);
		
		AddStrokeValue(mInstruction, ConstsParamNames.Stroke.STROKE_TOTAL_AREA, mStrokeIdx, ShapeDataObj.ShapeArea);
		AddStrokeValue(mInstruction, ConstsParamNames.Stroke.STROKE_TOTAL_AREA_MINX_MINY, mStrokeIdx, ShapeDataObj.ShapeAreaMinXMinY);
				
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
		String key = mUtilsGeneral.GenerateStrokeFeatureMeanKey(instruction, paramName, strokeIdx);		
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
		
			((FeatureMatrix)tempFeatureMatrix).AddValue(value, Consts.ConstsFeatures.NUM_STROKE_PARAMS);
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
	
	public String GetInstruction(){
		return mInstruction;
	}
	
}
