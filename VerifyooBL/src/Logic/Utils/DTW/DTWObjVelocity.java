package Logic.Utils.DTW;

public class DTWObjVelocity implements IDTWObj {
protected double mValue;
	
	public DTWObjVelocity(double value) {
		mValue = value;
	}
	
	public double GetValue() {
		return mValue;
	}
	
	@Override
	public double CompareTo(IDTWObj obj) {
		DTWObjVelocity valueToCompare = (DTWObjVelocity)obj;		
		double result = mValue * 1000 - valueToCompare.GetValue() * 1000;
		return result * result;
	}
}
