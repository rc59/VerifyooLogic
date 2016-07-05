package Data.UserProfile.Raw;

public class MotionEventCompact {
	public String Id;
	public double EventTime;
	
	public double Xpixel;
	public double Ypixel;
	
	public double Pressure;
	
	public double TouchSurface;	
	
	public double VelocityX;
	public double VelocityY;	
	
	public double AccelerometerX;
	public double AccelerometerY;
	public double AccelerometerZ;
	
	public double GyroX;
	public double GyroY;
	public double GyroZ;
	
	public MotionEventCompact Clone() {
		MotionEventCompact clonedEvent = new MotionEventCompact();
		
		clonedEvent.Id = Id;
		
		clonedEvent.EventTime = EventTime;
		
		clonedEvent.Xpixel = Xpixel;
		clonedEvent.Ypixel = Ypixel;
		
		clonedEvent.Pressure = Pressure;
		clonedEvent.TouchSurface = TouchSurface;
		
		clonedEvent.VelocityX = VelocityX;
		clonedEvent.VelocityY = VelocityY;
		
		clonedEvent.AccelerometerX = AccelerometerX;
		clonedEvent.AccelerometerY = AccelerometerY;
		clonedEvent.AccelerometerZ = AccelerometerZ;
		
		clonedEvent.GyroX = GyroX;
		clonedEvent.GyroY = GyroY;
		clonedEvent.GyroZ = GyroZ;
		
		return clonedEvent;
	}
}