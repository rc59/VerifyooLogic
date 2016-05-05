package Data.UserProfile.Extended;

import java.util.ArrayList;

import Data.MetaData.ShapeData;
import Data.MetaData.StrokeProperties;
import Data.UserProfile.Raw.MotionEventCompact;
import Data.UserProfile.Raw.Stroke;
import Logic.Calc.UtilsMath;
import Logic.Calc.UtilsSpatialSampling;

public class StrokeExtended extends Stroke {
	public double[] SpatialSamplingVector;
	
	private UtilsSpatialSampling mUtilsSpatialSampling;
	
	private MotionEventCompact PointMinX;
	private MotionEventCompact PointMaxX;
	private MotionEventCompact PointMinY;
	private MotionEventCompact PointMaxY;
	
	private double StrokeCenterXpixel;
	private double StrokeCenterYpixel;
	
	public ArrayList<MotionEventExtended> ListEventsExtended; 
	
	public StrokeProperties StrokePropertiesObj;
	public ShapeData ShapeDataObj;
	
	public StrokeExtended(Stroke stroke)
	{
		ListEvents = stroke.ListEvents;
		Length = stroke.Length;
		Xdpi = stroke.Xdpi;		
		Ydpi = stroke.Ydpi;
		
		ListEventsExtended = new ArrayList<>();
			
		StrokePropertiesObj = new StrokeProperties(ListEvents.size());
		ShapeDataObj = new ShapeData();
		
		InitUtils();
		InitFeatures();		
	}
	
	protected void InitUtils()
	{
		mUtilsSpatialSampling = new UtilsSpatialSampling();
	}
	
	protected void InitFeatures()
	{
		PreCalculations();
		CalculateSpatialSamplingVector();
	}
	
	protected void PreCalculations()
	{
		CalculateStrokeCenter();
		ConvertToMM();
		PrepareData();
	}
	
	protected void ConvertToMM()
	{
		MotionEventExtended tempEvent;		
		
		for(int idxEvent = 0; idxEvent < ListEvents.size(); idxEvent++)
		{
			tempEvent = new MotionEventExtended(ListEvents.get(idxEvent), StrokeCenterXpixel, StrokeCenterYpixel, Xdpi, Ydpi);			
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
			}
		}
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
		
		StrokeCenterXpixel = (PointMinX.Xpixel + PointMaxX.Xpixel) / 2;
        StrokeCenterYpixel = (PointMinY.Ypixel + PointMaxY.Ypixel) / 2;
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
