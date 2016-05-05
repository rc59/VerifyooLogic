package Data.UserProfile.Extended;

import Consts.ConstsMeasures;
import Data.UserProfile.Raw.MotionEventCompact;

public class MotionEventExtended extends MotionEventCompact {
	
	public double Xmm;
	public double Ymm;
	
	public MotionEventExtended(MotionEventCompact motionEvent, double strokeCenterXpixel, double strokeCenterYpixel, double xdpi, double ydpi)
	{
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
	}
}