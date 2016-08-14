package Logic.Comparison.Stats.Interfaces;

import java.util.ArrayList;
import java.util.HashMap;

import Data.UserProfile.Extended.MotionEventExtended;
import Data.UserProfile.Raw.MotionEventCompact;
import Logic.Comparison.Stats.FeatureMeanData;
import Logic.Comparison.Stats.Data.Interface.IStatEngineResult;

public interface IStatEngine {	
	public ArrayList<IStatEngineResult> CompareStrokeSpatial(String instruction, String paramName, int idxStroke, ArrayList<MotionEventExtended> listAuth, ArrayList<MotionEventExtended> listStored);
	
	public IStatEngineResult CompareStrokeDoubleValues(String instruction, String paramName, int strokeIdx, double authValue, HashMap<String, IFeatureMeanData> hashFeatureMeans);
	public IStatEngineResult CompareGestureDoubleValues(String instruction, String paramName, double authValue, HashMap<String, IFeatureMeanData> hashFeatureMeans);
	public IStatEngineResult CompareGestureScoreWithoutDistribution(String instruction, String paramName, double authValue, HashMap<String, IFeatureMeanData> hashFeatureMeans);
}
