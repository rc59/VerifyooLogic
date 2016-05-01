package VerifyooLogic.Statistics.Objects;

import VerifyooLogic.Utils.UtilsCalc;

public class DistanceObj {
	public double DeltaX;
	public double DeltaY;
	public double Distance;
	
	public DistanceObj(double x1, double y1, double x2, double y2) {
		DeltaX = Math.abs(x1 -x2);
		DeltaY = Math.abs(y1 - y2);
		Distance = UtilsCalc.CalcPitagoras(DeltaX, DeltaY);
	}
}
