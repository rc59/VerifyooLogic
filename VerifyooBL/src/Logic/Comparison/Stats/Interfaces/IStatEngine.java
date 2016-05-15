package Logic.Comparison.Stats.Interfaces;

import java.util.HashMap;

import Logic.Comparison.Stats.FeatureMeanData;

public interface IStatEngine {
	public double CompareStrokeDoubleValues(String instruction, String paramName, int strokeIdx, double authValue, HashMap<String, FeatureMeanData> hashFeatureMeans);
	public double CompareGestureDoubleValues(String instruction, String paramName, double authValue, HashMap<String, FeatureMeanData> hashFeatureMeans);
}
