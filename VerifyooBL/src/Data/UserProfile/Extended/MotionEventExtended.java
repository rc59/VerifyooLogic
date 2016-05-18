package Data.UserProfile.Extended;

import Consts.ConstsMeasures;
import Data.UserProfile.Raw.MotionEventCompact;
import Logic.Utils.Utils;
import Logic.Utils.UtilsMath;

public class MotionEventExtended extends MotionEventCompact {
	
	protected UtilsMath mUtilsMath;
	
	public int Index;
	
	public double Xmm;
	public double Ymm;
	
	public double VelocityX;
	public double VelocityY;
	public double Velocity;
	
	public boolean IsStartOfStroke;
	public boolean IsEndOfStroke;
	
	public MotionEventExtended(MotionEventCompact motionEvent, double strokeCenterXpixel, double strokeCenterYpixel, double xdpi, double ydpi, MotionEventExtended motionEventPrev, int index)
	{
		mUtilsMath = Utils.GetInstance().GetUtilsMath();
		
		Index = index;
		
		Xpixel = motionEvent.Xpixel;
		Ypixel = motionEvent.Ypixel;
		
		EventTime = motionEvent.EventTime;
		Pressure = motionEvent.Pressure;
		TouchSurface = motionEvent.TouchSurface;
		
		VelocityX = motionEvent.VelocityX;
		VelocityY = motionEvent.VelocityY;
		
		AccelerometerX = motionEvent.AccelerometerX;
		AccelerometerY = motionEvent.AccelerometerY;
		AccelerometerZ = motionEvent.AccelerometerZ;
		
		GyroX = motionEvent.GyroX;
		GyroY = motionEvent.GyroY;
		GyroZ = motionEvent.GyroZ;
		
		Xmm = (Xpixel - strokeCenterXpixel) / xdpi * ConstsMeasures.INCH_TO_MM;
        Ymm = (Ypixel - strokeCenterYpixel) / ydpi * ConstsMeasures.INCH_TO_MM;
        
        IsStartOfStroke = false;
        IsEndOfStroke = false;
        
        if(motionEventPrev != null) {
        	CalculateVelocities(motionEventPrev);
        }
	}
	
	protected void CalculateVelocities(MotionEventExtended motionEventPrev) {
		double eventTimeDiff = EventTime - motionEventPrev.EventTime;
				
		if(eventTimeDiff > 0) {
			double deltaX = Xmm - motionEventPrev.Xmm;
			double deltaY = Ymm - motionEventPrev.Ymm;
			
			VelocityX = deltaX / eventTimeDiff;
			VelocityY = deltaY / eventTimeDiff;
			
			Velocity = mUtilsMath.CalcPitagoras(VelocityX, VelocityY);
		}
		else {
			VelocityX = motionEventPrev.VelocityX;
			VelocityY = motionEventPrev.VelocityY;
			
			Velocity = motionEventPrev.Velocity;
		}		
	}
}