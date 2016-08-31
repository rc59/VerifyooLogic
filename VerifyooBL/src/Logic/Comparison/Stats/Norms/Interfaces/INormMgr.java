package Logic.Comparison.Stats.Norms.Interfaces;

import Logic.Comparison.Stats.Norms.NormContainerMgr;

public interface INormMgr {
	public INormData GetNormDataByParamName(String name, String instruction);
	public INormData GetNormDataByParamName(String name, String instruction, int strokeIdx);
	public void GetNorms(NormContainerMgr normContainerMgr);	
}
