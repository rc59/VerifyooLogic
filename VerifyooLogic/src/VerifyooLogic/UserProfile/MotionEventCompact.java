package VerifyooLogic.UserProfile;

public class MotionEventCompact {
	public int IndexInStroke;
    public double Xmm;
    public double Ymm;
    public double RawXpixel;
    public double RawYpixel;
    public double Xpixel;
    public double Ypixel;

    public double Xpixel2;
    public double Ypixel2;
    
    public double Xpixel3;
    public double Ypixel3;
    
    public double VelocityX;
    public double VelocityY;
    public double Pressure;
    public double EventTime;
    public double RelativeEventTime;
    public double TouchSurface;
        
    public double AngleX;
    public double AngleY;
    public double AngleZ;
    
    public double GyroX;
    public double GyroY;
    public double GyroZ;
    
    public double PointerCount;
    public boolean IsPause;
    
    public MotionEventCompact() {
        IsPause = false;
    }

    public MotionEventCompact(double xPixel, double yPixel, double rawXPixel, double rawYPixel, double eventTime, double pressure, double touchSurface) {
        IsPause = false;
        Pressure = pressure;
        Xpixel = xPixel;
        Ypixel = yPixel;
        RawXpixel = rawXPixel;
        RawYpixel = rawYPixel;

        EventTime = eventTime;
        TouchSurface = touchSurface;
    }
}
