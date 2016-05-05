package Data.UserProfile.Extended;

import java.util.ArrayList;

import Data.MetaData.ShapeData;
import Data.MetaData.StrokeProperties;
import Data.UserProfile.Raw.MotionEventCompact;
import Data.UserProfile.Raw.Stroke;
import Logic.Calc.UtilsMath;
import Logic.Calc.UtilsSpatialSampling;

public class StrokeExtended extends Stroke {
	/************** Private Members **************/
	private UtilsSpatialSampling mUtilsSpatialSampling;
	private UtilsMath mUtilsMath;
	
	private MotionEventCompact PointMinX;
	private MotionEventCompact PointMaxX;
	private MotionEventCompact PointMinY;
	private MotionEventCompact PointMaxY;
	
	private double mStrokeCenterXpixel;
	private double mStrokeCenterYpixel;	
	/****************************************/
	
	/************** Stroke Features **************/
	public double[] SpatialSamplingVector;
	
	public double StrokeTimeInterval;
	public double AverageVelocity;
	
	public ArrayList<MotionEventExtended> ListEventsExtended; 
	
	public StrokeProperties StrokePropertiesObj;
	public ShapeData ShapeDataObj;	
	
	public double MaxPressure;
	public double MaxSurface;
	
	public boolean IsHasPressure;
	public boolean IsHasTouchSurface;
	/****************************************/
	
	public StrokeExtended(Stroke stroke)
	{
		ListEvents = stroke.ListEvents;
		Length = stroke.Length;
		Xdpi = stroke.Xdpi;		
		Ydpi = stroke.Ydpi;
		
		ListEventsExtended = new ArrayList<>();
			
		StrokePropertiesObj = new StrokeProperties(ListEvents.size());
		ShapeDataObj = new ShapeData();
		
		IsHasPressure = false;
		IsHasTouchSurface = false;
		
		InitUtils();
		InitFeatures();		
	}
	
	protected void InitUtils()
	{
		mUtilsSpatialSampling = new UtilsSpatialSampling();
		mUtilsMath = new UtilsMath();
	}
	
	protected void InitFeatures()
	{
		PreCalculations();
		CalculateFeatures();		
	}
	
	protected void PreCalculations()
	{
		CalculateStrokeCenter();
		ConvertToMM();
		PrepareData();		
	}
	
	protected void CalculateFeatures()
	{
		CalculateSpatialSamplingVector();
		CalculateStrokeInterval();
		CalculateAverageVelocity();
	}
	
	protected void ConvertToMM()
	{
		MotionEventExtended tempEvent;		
		
		for(int idxEvent = 0; idxEvent < ListEvents.size(); idxEvent++)
		{
			tempEvent = new MotionEventExtended(ListEvents.get(idxEvent), mStrokeCenterXpixel, mStrokeCenterYpixel, Xdpi, Ydpi);			
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
				
                StrokePropertiesObj.ListEventLength[idxEvent - 1] = UtilsMath.CalcPitagoras(deltaX, deltaY);
                
                ShapeDataObj.ShapeArea += 
                		(StrokePropertiesObj.ListEventLength[idxEvent - 1] * UtilsMath.CalcPitagoras(ListEventsExtended.get(idxEvent - 1).Xmm, ListEventsExtended.get(idxEvent - 1).Ymm) / 2);
                                
                if(CheckIfPressureExists(ListEventsExtended.get(idxEvent))) {
                	IsHasPressure = true;
                	MaxPressure = mUtilsMath.GetMaxValue(MaxPressure, ListEventsExtended.get(idxEvent).Pressure);	
                }
            	if(CheckIfSurfaceExists(ListEventsExtended.get(idxEvent))) {
            		IsHasTouchSurface = true;
            		MaxSurface = mUtilsMath.GetMaxValue(MaxSurface, ListEventsExtended.get(idxEvent).TouchSurface);
                }
			}
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
		AverageVelocity = Length / StrokeTimeInterval; 
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
                PointMinX = eventCurrent;
                PointMaxX = eventCurrent;
                PointMinY = eventCurrent;
                PointMaxY = eventCurrent;
            }
            else {               
                PointMinX = GetMinXpixel(PointMinX, eventCurrent);
                PointMaxX = GetMaxXpixel(PointMaxX, eventCurrent);
                PointMinY = GetMinYpixel(PointMinY, eventCurrent);
                PointMaxY = GetMaxYpixel(PointMaxY, eventCurrent);                
            }
        }
		
		mStrokeCenterXpixel = (PointMinX.Xpixel + PointMaxX.Xpixel) / 2;
        mStrokeCenterYpixel = (PointMinY.Ypixel + PointMaxY.Ypixel) / 2;
	}
	
	protected void CalculateSpatialSamplingVector()
	{
		SpatialSamplingVector = mUtilsSpatialSampling.PrepareDataSpatialSampling(ListEvents, Length);
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
}
