package Logic.Utils.DTW;

import Logic.Utils.Utils;

public class DTWObjCoordinate implements IDTWObj {
	protected double mX;
	protected double mY;
	
	public DTWObjCoordinate(double x, double y)
	{
		mX = x;
		mY = y;
	}
	
	public double GetX() {
		return mX;
	}
	
	public double GetY() {
		return mY;
	}
	
	public double CompareTo(IDTWObj obj)
	{
		DTWObjCoordinate coordinate = (DTWObjCoordinate)obj;
		double x1 = coordinate.GetX();
		double y1 = coordinate.GetY();
		
		double x2 = mX;
		double y2 = mY;
		
		double diffX = x1 - x2;
		double diffY = y1 - y2;
		
		double distance = Utils.GetInstance().GetUtilsMath().CalcPitagoras(diffX, diffY);				
		return distance;
	}
}