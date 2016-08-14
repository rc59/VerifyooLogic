package Logic.Utils.DTW;

public class DTWObjDouble implements IDTWObj {

	protected double mValue;
	
	public DTWObjDouble(double value) {
		mValue = value;
	}
	
	public double GetValue() {
		return mValue;
	}
	
	@Override
	public double CompareTo(IDTWObj obj) {
		DTWObjDouble valueToCompare = (DTWObjDouble)obj;		
		double result = mValue - valueToCompare.GetValue();
		return Math.abs(result);
	}

}
