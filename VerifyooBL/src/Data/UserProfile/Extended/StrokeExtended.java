package Data.UserProfile.Extended;

import java.util.ArrayList;
import java.util.HashMap;

import Consts.ConstsFeatures;
import Consts.ConstsMeasures;
import Consts.ConstsParamNames;
import Data.MetaData.ShapeData;
import Data.MetaData.StrokeProperties;
import Data.MetaData.ParameterAvgPoint;
import Data.MetaData.IndexValue;
import Data.UserProfile.Raw.MotionEventCompact;
import Data.UserProfile.Raw.Stroke;
import Logic.Comparison.Stats.FeatureMeanData;
import Logic.Comparison.Stats.StatEngine;
import Logic.Comparison.Stats.Interfaces.IFeatureMeanData;
import Logic.Comparison.Stats.Interfaces.IStatEngine;
import Logic.Utils.Utils;
import Logic.Utils.UtilsGeneral;
import Logic.Utils.UtilsMath;
import Logic.Utils.UtilsSpatialSampling;

public class StrokeExtended extends Stroke {
	/************** Private Members **************/
	private String mInstruction;
	private int mStrokeIdx;
	
	private UtilsSpatialSampling mUtilsSpatialSampling;
	private UtilsMath mUtilsMath;
	protected UtilsGeneral mUtilsGeneral;
	
	private MotionEventCompact mPointMinX;
	private MotionEventCompact mPointMaxX;
	private MotionEventCompact mPointMinY;
	private MotionEventCompact mPointMaxY;
	
	private double mStrokeCenterXpixel;
	private double mStrokeCenterYpixel;	
		
	private HashMap<String, IFeatureMeanData> mHashFeatureMeans;
	/****************************************/
	
	/************** Stroke Features **************/
	public boolean IsPoint;
	public int StrokeStartEvent;
	public int StrokeEndEvent;
	
	public IndexValue StrokeVelocityPeak;
	
	public ArrayList<MotionEventExtended> ListEventsExtended; 
	
	public double StrokeTimeInterval;
	public double AverageVelocity;
				
	public double MaxPressure;
	public double MaxSurface;
	
	public double MiddlePressure;
	public double MiddleSurface;
	
	public boolean IsHasPressure;
	public boolean IsHasTouchSurface;
	
	public double[] SpatialSamplingVector;
	
	public StrokeProperties StrokePropertiesObj;
	public ShapeData ShapeDataObj;
	
	public double[] TimeIntervals;
	public double[] AccumulatedTimeIntervals;
	
	public double LengthPixel;
	/****************************************/
	
	public StrokeExtended(Stroke stroke, HashMap<String, IFeatureMeanData> hashFeatureMeans, String instruction, int strokeIdx)
	{		
		Id = stroke.Id;
		mHashFeatureMeans = hashFeatureMeans;
		mStrokeIdx = strokeIdx;
		mInstruction = instruction;
		LengthPixel = stroke.Length;
		ListEvents = stroke.ListEvents;		
		Xdpi = stroke.Xdpi;		
		Ydpi = stroke.Ydpi;
		
		ListEventsExtended = new ArrayList<>();
			
		StrokePropertiesObj = new StrokeProperties(ListEvents.size());
		ShapeDataObj = new ShapeData();
		
		TimeIntervals = new double[ListEvents.size() - 1];
		AccumulatedTimeIntervals = new double[ListEvents.size()];
		
		IsHasPressure = false;
		IsHasTouchSurface = false;		
		
		InitUtils();
		InitFeatures();		
	}
	
	protected void InitUtils()
	{
		mUtilsSpatialSampling = Utils.GetInstance().GetUtilsSpatialSampling();
		mUtilsMath = Utils.GetInstance().GetUtilsMath();
		mUtilsGeneral = Utils.GetInstance().GetUtilsGeneral();
	}
	
	protected void InitFeatures()
	{
		PreCalculations();
		CalculateFeatures();		
	}
	
	protected void PreCalculations()
	{
		CalculateStrokeCenter();
		ConvertToMotionEventExtended();
		CalculateStartEndOfStroke();
		PrepareData();		
	}

	protected void CalculateStartEndOfStroke()
	{
		IsPoint = true;
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
		CalculateAverageVelocity();
		CalculateMiddlePressureAndSurface();
		CalculateStrokeVelocityPeaks();
	}
	
	protected void ConvertToMotionEventExtended()
	{
		MotionEventExtended tempEvent;
		MotionEventExtended tempEventPrev;
		
		for(int idxEvent = 0; idxEvent < ListEvents.size(); idxEvent++)
		{
			if(idxEvent > 0) {
				tempEventPrev = ListEventsExtended.get(idxEvent - 1);
				
			}
			else {
				tempEventPrev = null;
			}
			
			tempEvent = new MotionEventExtended(ListEvents.get(idxEvent), mStrokeCenterXpixel, mStrokeCenterYpixel, Xdpi, Ydpi, tempEventPrev, idxEvent);
			
			if(idxEvent == 0) {
				tempEvent.IsStartOfStroke = true;
			}
			if(idxEvent == ListEvents.size() - 1) {
				tempEvent.IsEndOfStroke = true;
			}
			
			ListEventsExtended.add(tempEvent);
		}
	}
	
	protected void PrepareData()
	{
		double deltaX, deltaY;
		
		for(int idxEvent = 0; idxEvent < ListEventsExtended.size(); idxEvent++)
		{
			if(idxEvent > 0) {
				deltaX = ListEventsExtended.get(idxEvent).Xmm - ListEventsExtended.get(idxEvent - 1).Xmm;
                deltaY = ListEventsExtended.get(idxEvent).Ymm - ListEventsExtended.get(idxEvent - 1).Ymm;

                StrokePropertiesObj.ListDeltaXmm[idxEvent - 1] = deltaX;
                StrokePropertiesObj.ListDeltaYmm[idxEvent - 1] = deltaY;              
				
                StrokePropertiesObj.ListEventLength[idxEvent - 1] = mUtilsMath.CalcPitagoras(deltaX, deltaY);
                StrokePropertiesObj.LengthMM += StrokePropertiesObj.ListEventLength[idxEvent - 1];                
                
                ShapeDataObj.ShapeArea += 
                		(StrokePropertiesObj.ListEventLength[idxEvent - 1] * mUtilsMath.CalcPitagoras(ListEventsExtended.get(idxEvent - 1).Xmm, ListEventsExtended.get(idxEvent - 1).Ymm) / 2);
                                
                if(CheckIfPressureExists(ListEventsExtended.get(idxEvent))) {
                	IsHasPressure = true;
                	MaxPressure = mUtilsMath.GetMaxValue(MaxPressure, ListEventsExtended.get(idxEvent).Pressure);	
                }
            	if(CheckIfSurfaceExists(ListEventsExtended.get(idxEvent))) {
            		IsHasTouchSurface = true;
            		MaxSurface = mUtilsMath.GetMaxValue(MaxSurface, ListEventsExtended.get(idxEvent).TouchSurface);
                }
            	
            	TimeIntervals[idxEvent - 1] = ListEventsExtended.get(idxEvent).EventTime - ListEventsExtended.get(idxEvent - 1).EventTime;
            	AccumulatedTimeIntervals[idxEvent] = AccumulatedTimeIntervals[idxEvent - 1] + TimeIntervals[idxEvent - 1]; 
			}
			else {
				AccumulatedTimeIntervals[idxEvent] = 0;
			}
		}				
	}
	
	private void CalculateStrokeVelocityPeaks()
	{
		StrokeVelocityPeak = new IndexValue();
		
		ArrayList<ParameterAvgPoint> listVelocityAvgPoints = new ArrayList<>();	
		MotionEventExtended eventPrev;
		MotionEventExtended eventCurr;
		ParameterAvgPoint tempVelocityAvgPoint = null;
		ParameterAvgPoint velocityAvgPointMax = null;
		
		double currMaxVelocity = 0;
		int currMaxVelocityIdx = 0;

		for(int idxEvent = 1; idxEvent < ListEventsExtended.size(); idxEvent++) {
			eventPrev = ListEventsExtended.get(idxEvent - 1);
			eventCurr = ListEventsExtended.get(idxEvent);

			if(eventPrev.Velocity < AverageVelocity && eventCurr.Velocity > AverageVelocity) {
				tempVelocityAvgPoint = new ParameterAvgPoint(idxEvent);		
				currMaxVelocity = eventCurr.Velocity;
			}
			
			if(eventPrev.Velocity > AverageVelocity && eventCurr.Velocity < AverageVelocity) {
				if(tempVelocityAvgPoint != null) {
					tempVelocityAvgPoint.IndexEnd = idxEvent;
					currMaxVelocity = Utils.GetInstance().GetUtilsMath().GetMaxValue(currMaxVelocity, eventCurr.Velocity);
					tempVelocityAvgPoint.MaxValueInSection = new IndexValue();
					tempVelocityAvgPoint.MaxValueInSection.Value = currMaxVelocity;
					tempVelocityAvgPoint.MaxValueInSection.Index = currMaxVelocityIdx;
					listVelocityAvgPoints.add(tempVelocityAvgPoint);
					tempVelocityAvgPoint = null;
					currMaxVelocity = 0;
				}				
			}
			
			if(tempVelocityAvgPoint != null && tempVelocityAvgPoint.IndexEnd == -1) {
				currMaxVelocity = Utils.GetInstance().GetUtilsMath().GetMaxValue(currMaxVelocity, eventCurr.Velocity);
				if(currMaxVelocity == eventCurr.Velocity) {
					currMaxVelocityIdx = idxEvent;
				}
			}
		}		
		
		if(listVelocityAvgPoints.size() > 0) {
			tempVelocityAvgPoint = new ParameterAvgPoint(0);		
			
			velocityAvgPointMax = listVelocityAvgPoints.get(0);
//			for(int idxVelAvgPoint = 1; idxVelAvgPoint < listVelocityAvgPoints.size(); idxVelAvgPoint++) {
//				if(velocityAvgPointMax.MaxVelocityInSection < listVelocityAvgPoints.get(idxVelAvgPoint).MaxVelocityInSection) {
//					velocityAvgPointMax = listVelocityAvgPoints.get(idxVelAvgPoint);
//				}
//			}
			
//			int velocityPeakMaxIdx = -1;
//			for(int idxVelocityPeakIndex = velocityAvgPointMax.IndexStart; idxVelocityPeakIndex < velocityAvgPointMax.IndexEnd; idxVelocityPeakIndex++) {
//				if(ListEventsExtended.get(idxVelocityPeakIndex).Velocity == velocityAvgPointMax.MaxVelocityInSection) {
//					velocityPeakMaxIdx = idxVelocityPeakIndex;
//				}
//			}
			
			StrokeVelocityPeak = new IndexValue();
			StrokeVelocityPeak.Value = velocityAvgPointMax.MaxValueInSection.Value;
			StrokeVelocityPeak.Index = velocityAvgPointMax.MaxValueInSection.Index;
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
	
	protected void CalculateAverageVelocity()
	{
		AverageVelocity = StrokePropertiesObj.LengthMM / StrokeTimeInterval;		
		AddStrokeValue(mInstruction, ConstsParamNames.Stroke.AVERAGE_VELOCITY, mStrokeIdx, AverageVelocity);
	}
	
	protected void CalculateMiddlePressureAndSurface()
	{
		int idxMiddle = ListEventsExtended.size() / 2;
		MiddlePressure = ListEventsExtended.get(idxMiddle).Pressure;
		MiddleSurface = ListEventsExtended.get(idxMiddle).TouchSurface;
	}
	
	protected void CalculateStrokeInterval()
	{
		StrokeTimeInterval = ListEventsExtended.get(ListEventsExtended.size() - 1).EventTime - ListEventsExtended.get(0).EventTime;
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
		
		mStrokeCenterXpixel = (mPointMinX.Xpixel + mPointMaxX.Xpixel) / 2;
        mStrokeCenterYpixel = (mPointMinY.Ypixel + mPointMaxY.Ypixel) / 2;
	}
	
	protected void CalculateSpatialSamplingVector()
	{
		SpatialSamplingVector = mUtilsSpatialSampling.PrepareDataSpatialSampling(ListEvents, LengthPixel);
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
		
		IFeatureMeanData tempFeatureMeanData;
		
		if(mHashFeatureMeans.containsKey(key)) {
			tempFeatureMeanData = mHashFeatureMeans.get(key);
		}
		else {
			tempFeatureMeanData = new FeatureMeanData(paramName);			
			mHashFeatureMeans.put(key, tempFeatureMeanData);
		}
		
		tempFeatureMeanData.AddValue(value);		
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
