package Data.UserProfile.Extended;

import Consts.ConstsMeasures;
import Consts.ConstsParamNames;
import Data.UserProfile.Raw.MotionEventCompact;
import Logic.Utils.Complex;
import Logic.Utils.Utils;
import Logic.Utils.UtilsMath;

public class MotionEventExtended extends MotionEventCompact {
	
	protected UtilsMath mUtilsMath;
	
	public int Index;
	
	public double Xmm;
	public double Ymm;
	
	public double Xnormalized;
	public double Ynormalized;	
	
	public double VelocityX;
	public double VelocityY;
	public double Velocity;
	
	public double RadialVelocity;
	public double RadialAcceleration;
	
	public double Radius;
	public double Teta;
	public double DeltaTeta;
	
	public double AccumulatedNormalizedArea;

	public double Angle;
	public double AngleDiff;

	public double Acceleration;
	
	public boolean IsStartOfStroke;
	public boolean IsEndOfStroke;
	
	protected MotionEventExtended() {
		
	}
	
	public MotionEventExtended Clone()
	{
		MotionEventExtended tempEvent = new MotionEventExtended();
		
		tempEvent.Id = Id;
		tempEvent.EventTime = EventTime;
		tempEvent.Xpixel = Xpixel;
		tempEvent.Ypixel = Ypixel;
		tempEvent.Pressure = Pressure;
		tempEvent.TouchSurface = TouchSurface;
		
		tempEvent.AccelerometerX = AccelerometerX;
		tempEvent.AccelerometerY = AccelerometerY;
		tempEvent.AccelerometerZ = AccelerometerZ;
		
		tempEvent.GyroX = GyroX;
		tempEvent.GyroY = GyroY;
		tempEvent.GyroZ = GyroZ;
		
		tempEvent.Index = Index;
		tempEvent.Xmm = Xmm;
		tempEvent.Ymm = Ymm;
		tempEvent.Xnormalized = Xnormalized;
		tempEvent.Ynormalized = Ynormalized;
		tempEvent.VelocityX = VelocityX;
		tempEvent.VelocityY = VelocityY;
		tempEvent.Velocity = Velocity;
		tempEvent.Angle = Angle;
		tempEvent.AngleDiff = AngleDiff;
		tempEvent.Acceleration = Acceleration;
		tempEvent.IsStartOfStroke = IsStartOfStroke;
		tempEvent.IsEndOfStroke = IsEndOfStroke;
		
		tempEvent.RadialVelocity = RadialVelocity;
		
		return tempEvent;
	}
	
	public double GetParamByName(String param) {
		double value = 0;
		
		switch (param) {
		case ConstsParamNames.StrokeSpatial.VELOCITIES:
			value = Velocity;
			break;
		case ConstsParamNames.StrokeSpatial.ACCELERATIONS:
			value = Acceleration;
			break;
		case ConstsParamNames.StrokeSpatial.RADIAL_VELOCITIES:
			value = RadialVelocity;
			break;
		case ConstsParamNames.StrokeSpatial.RADIAL_ACCELERATION:
			value = RadialAcceleration;
			break;
		case ConstsParamNames.StrokeSpatial.RADIUS:
			value = Radius;
			break;
		case ConstsParamNames.StrokeSpatial.TETA:
			value = Teta;
			break;
		case ConstsParamNames.StrokeSpatial.DELTA_TETA:
			value = DeltaTeta;
			break;
		case ConstsParamNames.StrokeSpatial.ACCUMULATED_NORM_AREA:
			value = AccumulatedNormalizedArea;
			break;
		}
			
		return value;
	}
	
	public MotionEventExtended(MotionEventCompact motionEvent, double xdpi, double ydpi, MotionEventExtended motionEventPrev, int index)
	{
		Id = motionEvent.Id;
		mUtilsMath = Utils.GetInstance().GetUtilsMath();
		
		Index = index;
		
		Xpixel = motionEvent.Xpixel;
		Ypixel = motionEvent.Ypixel;
		
		EventTime = motionEvent.EventTime;
		Pressure = motionEvent.Pressure;
		TouchSurface = motionEvent.TouchSurface;
		
		AccelerometerX = motionEvent.AccelerometerX;
		AccelerometerY = motionEvent.AccelerometerY;
		AccelerometerZ = motionEvent.AccelerometerZ;
		
		GyroX = motionEvent.GyroX;
		GyroY = motionEvent.GyroY;
		GyroZ = motionEvent.GyroZ;
		
		Xmm = Xpixel / xdpi * ConstsMeasures.INCH_TO_MM;
        Ymm = Ypixel / ydpi * ConstsMeasures.INCH_TO_MM;
        
        IsStartOfStroke = false;
        IsEndOfStroke = false;
                
        if(motionEventPrev != null) {
        	CalculateVelocities(motionEventPrev);
        	CalculateAcceleration(motionEventPrev);
        }
        else {
        	VelocityX = 0;
        	VelocityY = 0;
        	Velocity = 0;
        	Acceleration = 0;
        }
	}
	
	protected void CalculateVelocities(MotionEventExtended motionEventPrev) {
		double eventTimeDiff = EventTime - motionEventPrev.EventTime;
				
		if(eventTimeDiff > 0) {
			double deltaX = Xmm - motionEventPrev.Xmm;
			double deltaY = Ymm - motionEventPrev.Ymm;
			
			VelocityX = deltaX / eventTimeDiff;
			VelocityY = deltaY / eventTimeDiff;			
			
			Velocity = mUtilsMath.CalcPitagoras(deltaX, deltaY) / eventTimeDiff;
		}
		else {
			VelocityX = motionEventPrev.VelocityX;
			VelocityY = motionEventPrev.VelocityY;
			
			Velocity = motionEventPrev.Velocity;
		}		
	}
	
	protected void CalculateAcceleration(MotionEventExtended motionEventPrev)
	{
		double eventTimeDiff = EventTime - motionEventPrev.EventTime;
		double velocityDiff = Velocity - motionEventPrev.Velocity;
		
		if(eventTimeDiff > 0) {
			Acceleration = velocityDiff / eventTimeDiff;	
		}
		else {
			Acceleration = 0;
		}		
	}
}