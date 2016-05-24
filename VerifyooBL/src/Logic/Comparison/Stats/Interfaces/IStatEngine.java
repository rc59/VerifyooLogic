package Logic.Comparison.Stats.Interfaces;

import java.util.HashMap;

import Logic.Comparison.Stats.FeatureMeanData;
import Logic.Comparison.Stats.Data.Interface.IStatEngineResult;

public interface IStatEngine {
	public IStatEngineResult CompareStrokeDoubleValues(String instruction, String paramName, int strokeIdx, double authValue, HashMap<String, IFeatureMeanData> hashFeatureMeans);
	public IStatEngineResult CompareGestureDoubleValues(String instruction, String paramName, double authValue, HashMap<String, IFeatureMeanData> hashFeatureMeans);
}
