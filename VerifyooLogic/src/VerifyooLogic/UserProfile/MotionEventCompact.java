package VerifyooLogic.UserProfile;

public class MotionEventCompact {
	public int IndexInStroke;
    public double Xmm;
    public double Ymm;
    public double RawXpixel;
    public double RawYpixel;
    public double Xpixel;
    public double Ypixel;

    public double VelocityX;
    public double VelocityY;
    public double Pressure;
    public double EventTime;
    public double RelativeEventTime;
    public double TouchSurface;
    public double AngleZ;
    public double AngleX;
    public double AngleY;

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
