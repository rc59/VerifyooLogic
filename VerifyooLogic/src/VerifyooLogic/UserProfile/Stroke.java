package VerifyooLogic.UserProfile;

import java.util.ArrayList;
import java.util.HashMap;

import VerifyooLogic.Utils.LinearRegression;
import VerifyooLogic.Utils.Consts;
import VerifyooLogic.Utils.ConstsParams;
import VerifyooLogic.Utils.UtilsCalc;
import VerifyooLogic.Utils.UtilsData;

import VerifyooLogic.DataObjects.Octagon;
import VerifyooLogic.DataObjects.ShapeProperties;
import VerifyooLogic.DataObjects.ValueFreq;
import VerifyooLogic.DataObjects.Params.AccelerationInterval;
import VerifyooLogic.DataObjects.Params.ParamPropertiesAbstract;
import VerifyooLogic.DataObjects.Params.ParamPropertiesGeneral;
import VerifyooLogic.Statistics.Interfaces.IBaseParam;
import VerifyooLogic.Statistics.Objects.DistanceObj;

public class Stroke {
	double mDistanceBetweenFingers;
	ArrayList<DistanceObj> mDistances;	
	double[] mListDistances;
	double[] mListDistancesX;
	double[] mListDistancesY;
	ArrayList<ValueFreq> mListDistancecsXFreqs;
	ArrayList<ValueFreq> mListDistancecsYFreqs;
	
	public HashMap<String, IBaseParam> HashParameters;

    public double[] ExtremePointAngles;
    public double ExtremeAnglePointSum;

    public ArrayList<MotionEventCompact> ListEvents;
    public double Length;

    public ShapeProperties ShapeProperties;

    public Octagon StrokeOctagon;

    public MotionEventCompact PointMinX;
    public MotionEventCompact PointMinY;
    public MotionEventCompact PointMaxX;
    public MotionEventCompact PointMaxY;

    public MotionEventCompact StrokeCenterPoint;

    public double AvgX;
    public double AvgY;

    public double TimeInterval;
    public int NumEvents;

    public double Width;
    public double Height;
    public double BoundingBox;

    public double BoundingBoxAndOctagonRatio;

    public double DirectionChangeX;
    public double DirectionChangeY;

    public double MiniStrokes;
    public double StrokePauses;

    public double StartX;
    public double StartY;

    public double EndX;
    public double EndY;

    public double PreviousEndTime = 0;
    public double TimeBetweenStrokes;

    public MotionEventCompact PreviousStrokeLastEvent;

    public double PreviousEndX = Double.MAX_VALUE;
    public double PreviousEndY = Double.MAX_VALUE;
    public double DistanceBetweenStrokes;

    public double StartTime;
    public double EndTime;

    public double PressureChanges;
    public double SurfaceChanges;

    public double StrokeArea = 0;
    
    /********************************/

    protected double[] mListDeltaXmm;
    protected double[] mListDeltaYmm;
    protected double[] mListEventLength;
    protected double[] mListStrokeTime;
    protected double[] mListAccumulatedStrokeLength;
    protected double[] mListStrokeTimeDiffs;
    protected double[] mStrokePressure;
    protected double[] mStrokeSurface;
    protected double[] mStrokeAngles;
    protected double[] mStrokeVelocitiesMovingAverage;
    protected double[] mEventVelocities;
    protected double[] mEventTimeDiffs;
    protected double[] mEventAccelerationOnMovingAverage;
    protected double[] mEventAccelerationOnMovingAverageAbs;
    protected double[] mListXmm;
    protected double[] mListYmm;

    protected int[] mEventAccelerationOnMovingAverageState;

    protected boolean mIsUsePressure;
    protected boolean mIsUseSurface;

    protected boolean mIsAddTimeBetweenStrokes;
    protected boolean mIsAddDistanceBetweenStrokes;

    /********************************/
    
    public double XDpi;
    public double YDpi;

    public Stroke() {
        Length = 0;
        ListEvents = new ArrayList<MotionEventCompact>();
        
        mIsUsePressure = false;
        mIsUseSurface = false;

        mIsAddTimeBetweenStrokes = false;
        mIsAddDistanceBetweenStrokes = false;

        PressureChanges = 0;
        SurfaceChanges = 0;

        HashParameters = new HashMap<>();
    }

    public void PreCalculations() {
        StrokeArea = 0;
        NumEvents = ListEvents.size();

        mListDeltaXmm = new double[NumEvents - 1];
        mListDeltaYmm = new double[NumEvents - 1];

        mListEventLength = new double[NumEvents - 1];

        mListStrokeTime = new double[NumEvents];
        mListAccumulatedStrokeLength = new double[NumEvents];
        mListStrokeTimeDiffs = new double[NumEvents - 1];

        double xdpi = XDpi;
        double ydpi = YDpi;        
        
        double deltaX, deltaY;
        double totalDistance = 0;

        MotionEventCompact eventCurrent, eventPrevious;

        mStrokePressure = new double[NumEvents];
        mStrokeSurface = new double[NumEvents];
        mStrokeAngles = new double[NumEvents - 1];
        mStrokeVelocitiesMovingAverage = new double[NumEvents - 1];
        mEventVelocities = new double[NumEvents - 1];
        mEventTimeDiffs = new double[NumEvents - 1];

        for (int idxEvent = 0; idxEvent < ListEvents.size(); idxEvent++) {
            eventCurrent = ListEvents.get(idxEvent);

            mStrokePressure[idxEvent] = eventCurrent.Pressure;
            mStrokeSurface[idxEvent] = eventCurrent.TouchSurface;

            eventCurrent.IndexInStroke = idxEvent;

            if (eventCurrent.Pressure > 0 && eventCurrent.Pressure < 1) {
                mIsUsePressure = true;
            }

            if (eventCurrent.TouchSurface > 0 && eventCurrent.TouchSurface < 1) {
                mIsUseSurface = true;
            }

            if (idxEvent == 0) {
                PointMinX = eventCurrent;
                PointMaxX = eventCurrent;
                PointMinY = eventCurrent;
                PointMaxY = eventCurrent;
            }
            else {
                eventPrevious = ListEvents.get(idxEvent - 1);

                PointMinX = UtilsCalc.GetMinXpixel(PointMinX, eventCurrent);
                PointMaxX = UtilsCalc.GetMaxXpixel(PointMaxX, eventCurrent);
                PointMinY = UtilsCalc.GetMinYpixel(PointMinY, eventCurrent);
                PointMaxY = UtilsCalc.GetMaxYpixel(PointMaxY, eventCurrent);

                if (UtilsCalc.CheckIfOppositeSymbol(eventCurrent.VelocityX, eventPrevious.VelocityX)) {
                    DirectionChangeX++;
                }

                if (UtilsCalc.CheckIfOppositeSymbol(eventCurrent.VelocityY, eventPrevious.VelocityY)) {
                    DirectionChangeY++;
                }

                if (eventCurrent.Pressure != eventPrevious.Pressure) {
                    PressureChanges++;
                }

                if (eventCurrent.TouchSurface != eventPrevious.TouchSurface) {
                    SurfaceChanges++;
                }

                mEventTimeDiffs[idxEvent - 1] = eventCurrent.EventTime - eventPrevious.EventTime;
            }
        }

        double strokeCenterXpixel = (PointMinX.Xpixel + PointMaxX.Xpixel) / 2;
        double strokeCenterYpixel = (PointMinY.Ypixel + PointMaxY.Ypixel) / 2;

        int idxMovAvgSecondPrev, idxMovAvgNext;
        mListXmm = new double[NumEvents];
        mListYmm = new double[NumEvents];

        double[] turnRadius = new double[NumEvents - 2];

        for (int idxEvent = 0; idxEvent < ListEvents.size(); idxEvent++) {
            ListEvents.get(idxEvent).Xmm = (ListEvents.get(idxEvent).Xpixel - strokeCenterXpixel) / xdpi * Consts.INCH_TO_MM;
            ListEvents.get(idxEvent).Ymm = (ListEvents.get(idxEvent).Ypixel - strokeCenterYpixel) / ydpi * Consts.INCH_TO_MM;
        }

        for (int idxEvent = 0; idxEvent < ListEvents.size(); idxEvent++) {
            eventCurrent = ListEvents.get(idxEvent);

            mListStrokeTime[idxEvent] = ListEvents.get(idxEvent).EventTime - ListEvents.get(0).EventTime;

            mListXmm[idxEvent] = ListEvents.get(idxEvent).Xmm;
            mListYmm[idxEvent] = ListEvents.get(idxEvent).Ymm;

            if (idxEvent > 0) {
                eventPrevious = ListEvents.get(idxEvent - 1);

                mListStrokeTimeDiffs[idxEvent - 1] = ListEvents.get(idxEvent).EventTime - ListEvents.get(idxEvent - 1).EventTime;
                if (mListStrokeTimeDiffs[idxEvent - 1] <= 0) {
                    mListStrokeTimeDiffs[idxEvent - 1] = Consts.DEFAULT_SAMPLE_RATE;
                }

                deltaX = ListEvents.get(idxEvent).Xmm - ListEvents.get(idxEvent - 1).Xmm;
                deltaY = ListEvents.get(idxEvent).Ymm - ListEvents.get(idxEvent - 1).Ymm;

                mListDeltaXmm[idxEvent - 1] = deltaX;
                mListDeltaYmm[idxEvent - 1] = deltaY;

                mListEventLength[idxEvent - 1] = UtilsCalc.CalcPitagoras(deltaX, deltaY);

                StrokeArea += (mListEventLength[idxEvent - 1] * UtilsCalc.CalcPitagoras(ListEvents.get(idxEvent - 1).Xmm, ListEvents.get(idxEvent - 1).Ymm) / 2);

                totalDistance += mListEventLength[idxEvent - 1];
                mListAccumulatedStrokeLength[idxEvent] = totalDistance;

                mStrokeAngles[idxEvent - 1] = UtilsCalc.GetAngle(mListDeltaXmm[idxEvent - 1], mListDeltaYmm[idxEvent - 1]);

                idxMovAvgSecondPrev = (int) UtilsCalc.GetMaxValue(idxEvent - 2, 0);
                idxMovAvgNext = (int) UtilsCalc.GetMinValue(idxEvent + 1, NumEvents - 1);

                if (mListStrokeTime[idxMovAvgNext] - mListStrokeTime[idxMovAvgSecondPrev] != 0) {
                    mStrokeVelocitiesMovingAverage[idxEvent - 1] =
                            (mListAccumulatedStrokeLength[idxMovAvgNext] - mListAccumulatedStrokeLength[idxMovAvgSecondPrev]) /
                                    (mListStrokeTime[idxMovAvgNext] - mListStrokeTime[idxMovAvgSecondPrev]);
                }

                mEventVelocities[idxEvent - 1] =
                        UtilsCalc.CalcPitagoras(eventCurrent.Xmm - eventPrevious.Xmm, eventCurrent.Ymm - eventPrevious.Ymm) /
                                (eventCurrent.EventTime - eventPrevious.EventTime);

                AvgX += (eventCurrent.Xmm - PointMinX.Xmm);
                AvgY += (eventCurrent.Ymm - PointMinY.Ymm);

                if (idxEvent > 1) {
                    turnRadius[idxEvent - 2] = UtilsCalc.CalculateTurnRadius(ListEvents.get(idxEvent - 1), ListEvents.get(idxEvent - 2), ListEvents.get(idxEvent));
                }
            }
        }

        AvgX = AvgX / NumEvents;
        AvgY = AvgY / NumEvents;
    }

    protected int CropCalculation() {
        return 0;
    }

    public void InitParams() {

        /**************************************/
        if (ListEvents.size() > 0 && Length > Consts.MIN_STROKE_LENGTH) {
            PreCalculations();
            CalculateDistanceBetweenFingers();
            CalculateLinearRegression();
            CalculateAccelerations();
            CalculateStatisticalParameters();
            CalculateBasicParameters();
            CalculateStrokePauses();
            CalculateAccelerometer();
            CalculateShapeProperties();
            CalculateRotationAngleOnExtremePoints();
            AddStatisticalParametersToHash();
        }
    }
    
    private void CalculateDistanceBetweenFingers() {    	
    	mDistances = new ArrayList<DistanceObj>();
    	
    	mListDistances = new double[NumEvents];
    	mListDistancesX = new double[NumEvents];
    	mListDistancesY = new double[NumEvents];
    	
    	double x1, y1, x2, y2;
    	double totalDeltaX = 0;
		double totalDeltaY = 0;
		double avgDeltaX, avgDeltaY;		
    	DistanceObj tempDistance;
    	
    	for(int idx = 0; idx < ListEvents.size(); idx++) {
    		x1 = ListEvents.get(idx).Xpixel;
    		y1 = ListEvents.get(idx).Ypixel;    	
    		
    		x2 = ListEvents.get(idx).Xpixel2;
    		y2 = ListEvents.get(idx).Ypixel2;
    		
    		if(x2 != 0 && y2 != 0) {
    			tempDistance = new DistanceObj(x1, y1, x2, y2); 
    			mDistances.add(tempDistance);
    			mListDistances[idx] = tempDistance.Distance;
    			mListDistancesX[idx] = tempDistance.DeltaX;
    			mListDistancesY[idx] = tempDistance.DeltaY;
    			totalDeltaX += Math.abs(tempDistance.DeltaX);
    			totalDeltaY += Math.abs(tempDistance.DeltaY);
    		}    	    	
    	}
    	
    	avgDeltaX = totalDeltaX / NumEvents;
    	avgDeltaY = totalDeltaY / NumEvents;
    	
    	mListDistancecsXFreqs = UtilsData.GetListOfValueFreqs(mListDistancesX);
    	mListDistancecsYFreqs = UtilsData.GetListOfValueFreqs(mListDistancesY);
    	
    	mDistanceBetweenFingers = UtilsCalc.CalcPitagoras(avgDeltaX, avgDeltaY);
    	
    	
    	
    }

    private void CalculateLinearRegression() {
        LinearRegression linearRegression = new LinearRegression(mListXmm, mListYmm);

        double intercept = linearRegression.intercept();

    }

    private void CalculateAccelerometer() {
        double avgAccX = 0;
        double avgAccY = 0;
        double avgAccZ = 0;

        for (int idx = 0; idx < ListEvents.size(); idx++) {
            avgAccX += ListEvents.get(idx).AngleX;
            avgAccY += ListEvents.get(idx).AngleY;
            avgAccZ += ListEvents.get(idx).AngleZ;
        }

        avgAccX = avgAccX / NumEvents;
        avgAccY = avgAccY / NumEvents;
        avgAccZ = avgAccZ / NumEvents;
    }

    private void AddStatisticalParametersToHash() {
        //AddNumericalParameter(ConstsParams.Stroke.STROKE_OCTAGON_AREA, StrokeOctagonArea, Consts.WEIGHT_LOW);

        AddNumericalParameter(ConstsParams.Stroke.AVG_X, AvgX, Consts.WEIGHT_LOW);
        AddNumericalParameter(ConstsParams.Stroke.AVG_Y, AvgY, Consts.WEIGHT_LOW);

        AddNumericalParameter(ConstsParams.Stroke.LENGTH, Length, Consts.WEIGHT_HIGH);
        AddNumericalParameter(ConstsParams.Stroke.TIME_INTERVAL, TimeInterval, Consts.WEIGHT_HIGH);
        AddNumericalParameter(ConstsParams.Stroke.NUM_EVENTS, NumEvents, Consts.WEIGHT_HIGH);

//        AddNumericalParameter(ConstsParams.Stroke.WIDTH, Width, Consts.WEIGHT_LOW);
//        AddNumericalParameter(ConstsParams.Stroke.HEIGHT, Height, Consts.WEIGHT_LOW);
//        AddNumericalParameter(ConstsParams.Stroke.BOUNDING_BOX, BoundingBox, Consts.WEIGHT_LOW);

        AddNumericalParameter(ConstsParams.Stroke.DIRECTION_CHANGE_X, DirectionChangeX, Consts.WEIGHT_LOW);
        AddNumericalParameter(ConstsParams.Stroke.DIRECTION_CHANGE_Y, DirectionChangeY, Consts.WEIGHT_LOW);

        AddNumericalParameter(ConstsParams.Stroke.MINI_STROKES, MiniStrokes, Consts.WEIGHT_LOW);
        //AddNumericalParameter(ConstsParams.Stroke.STROKES_PAUSES, StrokePauses, Consts.WEIGHT_LOW);

        AddNumericalParameter(ConstsParams.Stroke.PRESSURE_CHANGES, PressureChanges, Consts.WEIGHT_LOW);
        AddNumericalParameter(ConstsParams.Stroke.SURFACE_CHANGES, SurfaceChanges, Consts.WEIGHT_LOW);

        if (mIsAddTimeBetweenStrokes) {
            AddNumericalParameter(ConstsParams.Stroke.TIME_BETWEEN_STROKES, TimeBetweenStrokes, Consts.WEIGHT_HIGH);
        }

        if (mIsAddDistanceBetweenStrokes) {
            AddNumericalParameter(ConstsParams.Stroke.DISTANCE_BETWEEN_STROKES, DistanceBetweenStrokes, Consts.WEIGHT_HIGH);
        }
    }

    private void CalculateAccelerations() {
        mEventAccelerationOnMovingAverageState = new int[NumEvents - 2];
        mEventAccelerationOnMovingAverage = new double[NumEvents - 2];
        mEventAccelerationOnMovingAverageAbs = new double[NumEvents - 2];

        double tempAcceleration;
        int listAccelerationLength = 0;

        for (int idxVelocity = 1; idxVelocity <  mStrokeVelocitiesMovingAverage.length; idxVelocity++) {
            tempAcceleration =
                    ( mStrokeVelocitiesMovingAverage[idxVelocity] -  mStrokeVelocitiesMovingAverage[idxVelocity - 1]) / mListStrokeTimeDiffs[idxVelocity];

            if (tempAcceleration >= Consts.ZERO_ACC_THREASHOLD) {
                mEventAccelerationOnMovingAverageState[listAccelerationLength] = 1;
            }
            if (tempAcceleration <= Consts.ZERO_ACC_THREASHOLD * -1) {
                mEventAccelerationOnMovingAverageState[listAccelerationLength] = -1;
            }
            if (UtilsCalc.CheckIfBetween(tempAcceleration, Consts.ZERO_ACC_THREASHOLD * -1, Consts.ZERO_ACC_THREASHOLD)) {
                mEventAccelerationOnMovingAverageState[listAccelerationLength] = 0;
            }

            mEventAccelerationOnMovingAverage[listAccelerationLength] = tempAcceleration;
            mEventAccelerationOnMovingAverageAbs[listAccelerationLength] = Math.abs(tempAcceleration);
            listAccelerationLength++;
        }
    }

    private void CalculateBasicParameters() {
        StartX = ListEvents.get(0).Xmm;
        StartY = ListEvents.get(0).Ymm;

        EndX = ListEvents.get(ListEvents.size() - 1).Xmm;
        EndY = ListEvents.get(ListEvents.size() - 1).Ymm;

        StartTime = ListEvents.get(0).EventTime;

        if (PreviousEndTime > 0) {
            mIsAddTimeBetweenStrokes = true;
            TimeBetweenStrokes = StartTime - PreviousEndTime;
        }

        if (PreviousEndX != Double.MAX_VALUE && PreviousEndY != Double.MAX_VALUE) {
            mIsAddDistanceBetweenStrokes = true;
            DistanceBetweenStrokes = UtilsCalc.GetDistanceBetweenPoints(ListEvents.get(0),PreviousStrokeLastEvent);
        }

        EndTime = ListEvents.get(ListEvents.size() - 1).EventTime;
        TimeInterval = ListEvents.get(ListEvents.size() - 1).EventTime - ListEvents.get(0).EventTime;
        NumEvents = ListEvents.size();
    }

    private void CalculateStatisticalParameters() {
        ArrayList<AccelerationInterval> listAccIntervals = CalculateStrokeAccelerationIntervals(mEventAccelerationOnMovingAverageState, mListStrokeTimeDiffs);

        ParamPropertiesAbstract paramsAngles = new ParamPropertiesGeneral(mStrokeAngles, mStrokeAngles.length, ConstsParams.StrokeBaseParams.ANGLES, HashParameters);
        ParamPropertiesGeneral paramsVelocity = new ParamPropertiesGeneral( mStrokeVelocitiesMovingAverage,  mStrokeVelocitiesMovingAverage.length, ConstsParams.StrokeBaseParams.VELOCITY, HashParameters);
        ParamPropertiesGeneral paramsAcc = new ParamPropertiesGeneral(mEventAccelerationOnMovingAverage, mEventAccelerationOnMovingAverage.length, ConstsParams.StrokeBaseParams.ACCELERATION, HashParameters);
        //ParamPropertiesGeneral paramsDeAcc = new ParamPropertiesGeneral(eventDeAccelerationOnMovingAverage, listDeAccelerationLength, ConstsParams.StrokeBaseParams.DEACCELERATION, HashParameters);

        if (mIsUsePressure) {
            ParamPropertiesGeneral paramsPressure = new ParamPropertiesGeneral(mStrokePressure, mStrokePressure.length, ConstsParams.StrokeBaseParams.PRESSURE, HashParameters);
        }

        if (mIsUseSurface) {
            ParamPropertiesGeneral paramsSurface = new ParamPropertiesGeneral(mStrokeSurface, mStrokeSurface.length, ConstsParams.StrokeBaseParams.SURFACE, HashParameters);
        }
    }

    private void CalculateShapeProperties() {
        StrokeOctagon = new Octagon(ListEvents, PointMinY.IndexInStroke, PointMaxX.IndexInStroke, PointMaxY.IndexInStroke, PointMinX.IndexInStroke);

        Width = PointMaxX.Xmm - PointMinX.Xmm;
        Height = PointMaxY.Ymm - PointMinY.Ymm;
        BoundingBox = Width * Height;

        ShapeProperties = new ShapeProperties(ListEvents, PointMinX, PointMaxX, PointMinY, PointMaxY, Length);
        BoundingBoxAndOctagonRatio = StrokeOctagon.Area / BoundingBox;
    }

    private void CalculateStrokePauses() {
        double currentPause;
        double prevPause;
        StrokePauses = 0;

        ArrayList<ValueFreq> listPausesFreqs = UtilsData.GetListOfValueFreqs(mEventTimeDiffs);
        double commonPause = listPausesFreqs.get(0).Value;

        boolean stateIsPause = false;
        for (int idxEventPause = 1; idxEventPause < mEventTimeDiffs.length - 2; idxEventPause++) {
            if (idxEventPause > 0) {
                currentPause = mEventTimeDiffs[idxEventPause];
                prevPause = mEventTimeDiffs[idxEventPause - 1];

                if (currentPause > (commonPause * 1.5) && !stateIsPause) {
                    ListEvents.get(idxEventPause).IsPause = true;
                    StrokePauses++;
                    stateIsPause = true;
                }
                else {
                    if (currentPause <= commonPause) {
                        stateIsPause = false;
                    }
                }

//                diffPause = UtilsCalc.GetPercentageDiff(currentPause, prevPause);
//                if (currentPause - prevPause > 30) {
//                    StrokePauses++;
//                }
//                if (diffPause <= 0.5) {
//
//                }
            }
        }
    }

    protected ArrayList<AccelerationInterval> CalculateStrokeAccelerationIntervals(int[] eventAccelerationOnMovingAverageState, double[] timeIntervals) {
        ArrayList<AccelerationInterval> listAccIntervals = new ArrayList<AccelerationInterval>();
        int currentState = 1;
        int idxCurrent = 0;

        listAccIntervals.add(new AccelerationInterval(1, timeIntervals[0]));
        for (int idx = 1; idx < eventAccelerationOnMovingAverageState.length; idx++) {
            if (eventAccelerationOnMovingAverageState[idx] == currentState) {
                listAccIntervals.get(idxCurrent).AddInterval(timeIntervals[idx]);
            }
            else {
                currentState = eventAccelerationOnMovingAverageState[idx];
                listAccIntervals.add(new AccelerationInterval(currentState, timeIntervals[idx]));
                idxCurrent++;
            }
        }

        return listAccIntervals;
    }

    protected void AddNumericalParameter(String name, double value, int weight) {
        UtilsData.AddNumericalParameter(HashParameters, name, value, weight);
    }

    protected void CalculateRotationAngleOnExtremePoints() {

        double currentAngleDiff;
        ArrayList<MotionEventExtremePoint> listEvents = ShapeProperties.ListEventsExtremePoints;

        ExtremeAnglePointSum = 0;
        ExtremePointAngles = new double[listEvents.size()];

        for (int idx = 1; idx < listEvents.size(); idx++) {
            currentAngleDiff = listEvents.get(idx).Angle - listEvents.get(idx - 1).Angle;

            if (currentAngleDiff <= -180) {
                currentAngleDiff += 360;
            }
            if (currentAngleDiff >= 180) {
                currentAngleDiff -= 360;
            }

            ExtremePointAngles[idx] = currentAngleDiff;
            ExtremeAnglePointSum += currentAngleDiff;
        }
    }
}
