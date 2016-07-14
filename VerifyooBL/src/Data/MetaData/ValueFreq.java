package Data.MetaData;

public class ValueFreq {
	protected double mValue;
	protected int mFreq;
	
	public ValueFreq(double value) {
		mValue = value;
	}
	
	public void Increase() {
		mFreq++;
	}
	
	public double GetValue() {
		return mValue;
	}
	
	public double GetFreq() {
		return mFreq;		
	}
	
}
