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
	
	private double mStrokeCenterXpixel;
	private double mStrokeCenterYpixel;	

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
	
	public double StrokeTimeInterval;
	public double AverageVelocity;
	public IndexValue StrokeMaxVelocity;

	public double AverageAcceleration;
	public double AverageAccelerationNegative;
				
	public double MiddlePressure;
	public double MiddleSurface;
	
	public boolean IsHasPressure;
	public boolean IsHasTouchSurface;
	
	public double[] SpatialSamplingVector;
	
	public StrokeProperties StrokePropertiesObj;
	public ShapeData ShapeDataObj;
	
	public double[] TimeIntervals;
	public double[] AccumulatedTimeIntervals;
	
	public ArrayList<MotionEventCompact> ListEventsSpatialByDistance;
	public ArrayList<MotionEventCompact> ListEventsSpatialByTime;	
	
	public ArrayList<MotionEventExtended> ListEventsSpatialByDistanceExtended;
	public ArrayList<MotionEventExtended> ListEventsSpatialByTimeExtended;	
	
	public double LengthPixel;
	/****************************************/
	
	public StrokeExtended(Stroke stroke, HashMap<String, IFeatureMeanData> hashFeatureMeans, String instruction, int strokeIdx)
	{		
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
			ListEvents = stroke.ListEvents;		
			
			double[] vectorX = Utils.GetInstance().GetUtilsVectors().GetVectorXpixel(ListEvents);
			double[] vectorY = Utils.GetInstance().GetUtilsVectors().GetVectorYpixel(ListEvents);
			
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
		mUtilsSpatialSampling = Utils.GetInstance().GetUtilsSpatialSampling();
		mUtilsMath = Utils.GetInstance().GetUtilsMath();
		mUtilsGeneral = Utils.GetInstance().GetUtilsGeneral();
		mUtilsPeakCalc = Utils.GetInstance().GetUtilsPeakCalc();
	}
	
	protected void InitFeatures()
	{
		PreCalculations();
		CalculateFeatures();		
	}
	
	protected void PreCalculations()
	{
		CenterAndRotate();
		SpatialSampling();	
		ConvertToMotionEventExtended();
		Normalize();
		
		CalculateStartEndOfStroke();
		PrepareData();		
	}
	
	private void SpatialSampling() {
		ListEventsSpatialByDistance = mUtilsSpatialSampling.ConvertToVectorByDistance(ListEvents, LengthPixel);
		ListEventsSpatialByTime = mUtilsSpatialSampling.ConvertToVectorByTime(ListEvents);
		
		double[] vectorXDistance = Utils.GetInstance().GetUtilsVectors().GetVectorXpixel(ListEventsSpatialByDistance);
		double[] vectorYDistance = Utils.GetInstance().GetUtilsVectors().GetVectorYpixel(ListEventsSpatialByDistance);
		
		double[] vectorXTime = Utils.GetInstance().GetUtilsVectors().GetVectorXpixel(ListEventsSpatialByTime);
		double[] vectorYTime = Utils.GetInstance().GetUtilsVectors().GetVectorYpixel(ListEventsSpatialByTime);
		
		int size = vectorXDistance.length;
	}

	private void CenterAndRotate() {
		ListEvents = mUtilsSpatialSampling.CenterAndRotate(ListEvents);

		double[] vectorX = Utils.GetInstance().GetUtilsVectors().GetVectorXpixel(ListEvents);
		double[] vectorY = Utils.GetInstance().GetUtilsVectors().GetVectorYpixel(ListEvents);
		
		int size = vectorX.length;
	}

	private void Normalize() {
		ListEventsExtended = mUtilsSpatialSampling.Normalize(ListEventsExtended);
		ListEventsSpatialByDistanceExtended = mUtilsSpatialSampling.Normalize(ListEventsSpatialByDistanceExtended);
		ListEventsSpatialByTimeExtended = mUtilsSpatialSampling.Normalize(ListEventsSpatialByTimeExtended);
		
		double[] vectorX = Utils.GetInstance().GetUtilsVectors().GetVectorXnormalized(ListEventsExtended);
		double[] vectorY = Utils.GetInstance().GetUtilsVectors().GetVectorYnormalized(ListEventsExtended);
		
		double[] vectorXDistance = Utils.GetInstance().GetUtilsVectors().GetVectorXnormalized(ListEventsSpatialByDistanceExtended);
		double[] vectorYDistance = Utils.GetInstance().GetUtilsVectors().GetVectorYnormalized(ListEventsSpatialByDistanceExtended);
		
		double[] vectorXTime = Utils.GetInstance().GetUtilsVectors().GetVectorXnormalized(ListEventsSpatialByTimeExtended);
		double[] vectorYTime = Utils.GetInstance().GetUtilsVectors().GetVectorYnormalized(ListEventsSpatialByTimeExtended);
		
		int size = vectorX.length;
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
		CalculateAverageVelocity();
		CalculateMiddlePressureAndSurface();
		CalculateStrokeVelocityPeaks();
		CalculateStrokeAccelerationPeaks();
	}
	
	protected void ConvertToMotionEventExtended()
	{
		ListEventsExtended = ConvertToMotionEventExtended(ListEvents);
		ListEventsSpatialByDistanceExtended = ConvertToMotionEventExtended(ListEventsSpatialByDistance);
		ListEventsSpatialByTimeExtended = ConvertToMotionEventExtended(ListEventsSpatialByTime);
		
		double[] vectorX = Utils.GetInstance().GetUtilsVectors().GetVectorXmm(ListEventsExtended);
		double[] vectorY = Utils.GetInstance().GetUtilsVectors().GetVectorYmm(ListEventsExtended);		
		
		double[] vectorXDistance = Utils.GetInstance().GetUtilsVectors().GetVectorXmm(ListEventsSpatialByDistanceExtended);
		double[] vectorYDistance = Utils.GetInstance().GetUtilsVectors().GetVectorYmm(ListEventsSpatialByDistanceExtended);
		
		double[] vectorXTime = Utils.GetInstance().GetUtilsVectors().GetVectorXmm(ListEventsSpatialByTimeExtended);
		double[] vectorYTime = Utils.GetInstance().GetUtilsVectors().GetVectorYmm(ListEventsSpatialByTimeExtended);
		
		int size = vectorX.length;
	}
	
	protected ArrayList<MotionEventExtended> ConvertToMotionEventExtended(ArrayList<MotionEventCompact> listEventsCompact)
	{
		ArrayList<MotionEventExtended> listEventsExtended = new ArrayList<>();
		
		MotionEventExtended tempEvent;
		MotionEventExtended tempEventPrev;
		
		for(int idxEvent = 0; idxEvent < listEventsCompact.size(); idxEvent++)
		{
			if(idxEvent > 0) {
				tempEventPrev = listEventsExtended.get(idxEvent - 1);				
			}
			else {
				tempEventPrev = null;
			}
			
			tempEvent = new MotionEventExtended(listEventsCompact.get(idxEvent), Xdpi, Ydpi, tempEventPrev, idxEvent);
			
			if(idxEvent == 0) {
				tempEvent.IsStartOfStroke = true;
			}
			if(idxEvent == listEventsCompact.size() - 1) {
				tempEvent.IsEndOfStroke = true;
			}
			
			listEventsExtended.add(tempEvent);
		}
		
		return listEventsExtended;
	}
	
	protected void PrepareData()
	{
		double deltaX, deltaY;
		
		mVelocities = new double[ListEventsExtended.size()];
		mAccelerations = new double[ListEventsExtended.size()];

		for(int idxEvent = 0; idxEvent < ListEventsExtended.size(); idxEvent++)
		{
			mVelocities[idxEvent] = ListEventsExtended.get(idxEvent).Velocity;			
			mAccelerations[idxEvent] = ListEventsExtended.get(idxEvent).Acceleration;
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
		
		StrokeMaxVelocity = new IndexValue();
		StrokeMaxVelocity.Index = 0;
		StrokeMaxVelocity.Value = 0;
		StrokePropertiesObj.AccumulatedLength[0] = 0;
				
		double totalAcc = 0;		
		double x1, y1, x2, y2, x3, y3;
		double tmpArea = 0;
		for(int idxEvent = 0; idxEvent < ListEventsExtended.size(); idxEvent++)
		{

			if(mVelocities[idxEvent] > StrokeMaxVelocity.Value)
			{
				StrokeMaxVelocity.Value = mVelocities[idxEvent];
				StrokeMaxVelocity.Index = idxEvent;
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
		
				
		AverageAcceleration = totalAcc / ListEventsExtended.size();
	}
	
	protected void CalculateStrokeAccelerationPeaks()
	{
		StrokeAccelerationPeakAvgPoint = mUtilsPeakCalc.CalculatePeaks(mAccelerations, AverageAcceleration);
	}
	
	protected void CalculateStrokeVelocityPeaks()
	{		
		StrokeVelocityPeakAvgPoint = mUtilsPeakCalc.CalculatePeaks(mVelocities, AverageVelocity);
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

        PointMinXMM = (mPointMinX.Xpixel - mStrokeCenterXpixel) / Xdpi * ConstsMeasures.INCH_TO_MM;
        PointMaxXMM = (mPointMaxX.Xpixel - mStrokeCenterXpixel) / Xdpi * ConstsMeasures.INCH_TO_MM;
        PointMinYMM = (mPointMinY.Ypixel - mStrokeCenterYpixel) / Ydpi * ConstsMeasures.INCH_TO_MM;
        PointMaxYMM = (mPointMinY.Ypixel - mStrokeCenterYpixel) / Ydpi * ConstsMeasures.INCH_TO_MM;
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
			tempFeatureMeanData = new FeatureMeanData(paramName, instruction);			
			mHashFeatureMeans.put(key, tempFeatureMeanData);
		}
		
		tempFeatureMeanData.AddValue(value);		
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
