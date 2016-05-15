package Data.MetaData;

public class VelocityAvgPoint {
	public int IndexStart;
	public int IndexEnd;
	public double MaxVelocityInSection;
	
	public VelocityAvgPoint(int indexStart) {
		IndexStart = indexStart;
		MaxVelocityInSection = -1;
		IndexEnd = -1;
	}
}
