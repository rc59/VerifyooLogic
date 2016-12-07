package Logic.Comparison.Stats.Norms.Interfaces;

import Data.MetaData.StoredMetaDataMgr;
import Data.UserProfile.Extended.StrokeExtended;
import Logic.Comparison.Stats.Norms.NormContainerMgr;

public interface INormMgr {
	public INormData GetNormDataByParamName(String name, String instruction);
	public INormData GetNormDataByParamName(String name, String instruction, int strokeIdx);
	public void GetNorms(NormContainerMgr normContainerMgr);
	public int GetStrokeKey(StrokeExtended stroke);	
	public StoredMetaDataMgr GetStoredMetaDataMgr();
	public double GetThreashold();
}
