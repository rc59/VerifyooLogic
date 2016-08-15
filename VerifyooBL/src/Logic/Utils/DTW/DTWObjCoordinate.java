package Logic.Utils.DTW;

import Logic.Utils.Utils;

public class DTWObjCoordinate implements IDTWObj {
	public double X;
	public double Y;
	
	public DTWObjCoordinate() {
		
	}
	
	public DTWObjCoordinate(double x, double y)
	{
		X = x;
		Y = y;
	}
	
	public double GetX() {
		return X;
	}
	
	public double GetY() {
		return Y;
	}
	
	public double CompareTo(IDTWObj obj)
	{
		DTWObjCoordinate coordinate = (DTWObjCoordinate)obj;
		double x1 = coordinate.GetX();
		double y1 = coordinate.GetY();
		
		double x2 = X;
		double y2 = Y;
		
		double diffX = x1 - x2;
		double diffY = y1 - y2;
		
		double distance = Utils.GetInstance().GetUtilsMath().CalcPitagoras(diffX, diffY);				
		return distance;
	}
}