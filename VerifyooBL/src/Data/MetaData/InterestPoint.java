package Data.MetaData;

public class InterestPoint {
	public double IdxStart;
	public double IdxEnd;
	public double IdxAverage;
	public double IdxLocation;
	public double AverageVelocity;
	public double Intensity;
	
	public InterestPoint(int idxStart, int idxEnd, double numEvents) {
		IdxStart = idxStart;
		IdxEnd = idxEnd;
		IdxAverage = (IdxStart + IdxEnd) / 2;
		IdxLocation = IdxAverage / numEvents;
	}
}
