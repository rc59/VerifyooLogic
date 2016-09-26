package Data.UserProfile.Raw;

public class MotionEventCompact {
	public String Id;
	public double EventTime;
	
	public double Xpixel;
	public double Ypixel;
	
	public double XpixelRaw;
	public double YpixelRaw;
	
	public double Pressure;
	
	public double TouchSurface;	
	
	protected double VelocityX;
	protected double VelocityY;	
	
	public double AccelerometerX;
	public double AccelerometerY;
	public double AccelerometerZ;
	
	public double GyroX;
	public double GyroY;
	public double GyroZ;
	
	public double VelocityX() {
		return VelocityX;
	}
	public double VelocityY() {
		return VelocityY;
	}
	
	public double AccelerometerX() {
		return AccelerometerX;
	}
	public double AccelerometerY() {
		return AccelerometerY;
	}
	public double AccelerometerZ() {
		return AccelerometerZ;
	}
	
	public double GyroX() {
		return GyroX;
	}
	public double GyroY() {
		return GyroY;
	}
	public double GyroZ() {
		return GyroZ;
	}
	
	public void SetVelocityX(double value) {
		VelocityX = value;
	}
	public void SetVelocityY(double value) {
		VelocityY = value;
	}
	
	public void SetAccelerometerX(double value) {
		AccelerometerX = value;
	}
	public void SetAccelerometerY(double value) {
		AccelerometerY = value;
	}
	public void SetAccelerometerZ(double value) {
		AccelerometerZ = value;
	}
	
	public void SetGyroX(double value) {
		GyroX = value;
	}
	public void SetGyroY(double value) {
		GyroY = value;
	}
	public void SetGyroZ(double value) {
		GyroZ = value;
	}
	
	public MotionEventCompact Clone() {
		MotionEventCompact clonedEvent = new MotionEventCompact();
		
		clonedEvent.Id = Id;
		
		clonedEvent.EventTime = EventTime;
		
		clonedEvent.Xpixel = Xpixel;
		clonedEvent.Ypixel = Ypixel;
		
		clonedEvent.XpixelRaw = Xpixel;
		clonedEvent.YpixelRaw = Ypixel;
		
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