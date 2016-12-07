package Data.Boundary;

import java.util.HashMap;

import Logic.Utils.Utils;

public class BoundaryMgr {
	protected HashMap<String, ParamBoundary> mHashParamBoundaries;
	
	public BoundaryMgr() {
		mHashParamBoundaries = new HashMap<>();
	}
	
	public double GetGestureParamBoundary(String paramName, String instruction, double popMean, double internalStd) {		
		String key = Utils.GetInstance().GetUtilsGeneral().GenerateGestureFeatureMeanKey(instruction, paramName);
		return GetParamBoundary(key, popMean, internalStd);
	}
	
	public double GetStrokeParamBoundary(String paramName, String instruction, int strokeKey, double popMean, double internalStd) {
		String key = Utils.GetInstance().GetUtilsGeneral().GenerateStrokeFeatureMeanKey(instruction, paramName, strokeKey);
		return GetParamBoundary(key, popMean, internalStd);
	}
	
	protected double GetParamBoundary(String key, double popMean, double internalStd) {
		double boundary = 0;
		if(mHashParamBoundaries.containsKey(key)) {
			ParamBoundary tempParamBoundary = mHashParamBoundaries.get(key);
			boundary = tempParamBoundary.Boundary;
		}
		else {
			boundary = internalStd / popMean;
			if(boundary < 0.1) {
				boundary = 0.1;
			}
			if(boundary > 0.35) {
				boundary = 0.35;
			}
		}
		return boundary;
	}
	
	public void UpdateGestureParamBoundary(String paramName, String instruction, double update) {
		
	}
	
	public void UpdateStrokeParamBoundary(String paramName, String instruction, int strokeKey, double update) {
		
	} 
}
