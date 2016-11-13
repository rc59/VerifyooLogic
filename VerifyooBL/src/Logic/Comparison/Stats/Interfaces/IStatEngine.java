package Logic.Comparison.Stats.Interfaces;

import java.util.ArrayList;
import java.util.HashMap;

import Data.UserProfile.Extended.MotionEventExtended;
import Data.UserProfile.Raw.MotionEventCompact;
import Logic.Comparison.Stats.FeatureMeanData;
import Logic.Comparison.Stats.Data.Interface.IStatEngineResult;
import Logic.Utils.DTW.DTWObjDouble;
import Logic.Utils.DTW.IDTWObj;

public interface IStatEngine {	
	public ArrayList<IDTWObj> GetSpatialVector(String instruction, String paramName, int idxStroke, ArrayList<MotionEventExtended> listEvents, String spatialType);
	public ArrayList<IStatEngineResult> CompareStrokeSpatial(String instruction, String paramName, int idxStroke, ArrayList<MotionEventExtended> listAuth, String spatialType, HashMap<String, IFeatureMeanData> hashFeatureMeans);
	
	public IStatEngineResult CompareStrokeDoubleValues(String instruction, String paramName, int strokeIdx, int strokeKey, double authValue, HashMap<String, IFeatureMeanData> hashFeatureMeans);
	public IStatEngineResult CompareGestureDoubleValues(String instruction, String paramName, double authValue, HashMap<String, IFeatureMeanData> hashFeatureMeans);
	public IStatEngineResult CompareGestureScoreWithoutDistribution(String instruction, String paramName, double authValue, HashMap<String, IFeatureMeanData> hashFeatureMeans);
}
