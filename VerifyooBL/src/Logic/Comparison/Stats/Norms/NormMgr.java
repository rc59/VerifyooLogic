package Logic.Comparison.Stats.Norms;

import java.util.HashMap;

import Logic.Comparison.Stats.Norms.Interfaces.INormData;
import Logic.Comparison.Stats.Norms.Interfaces.INormMgr;

public class NormMgr implements INormMgr {

	protected HashMap<String, INormData> mHashNorms;
	private static INormMgr mInstance = null;
	
	protected NormMgr()
	{
		InitNorms();
	}
	
	public static INormMgr GetInstance() {
      if(mInstance == null) {
    	  mInstance = new NormMgr();
      }
      return mInstance;
   }
	
	public INormData GetNormDataByParamName(String name) {
		return mHashNorms.get(name);
	}
	
	protected void InitNorms()
	{
		mHashNorms = new HashMap<String, INormData>();	
		
		CreateDoubleNorm(Consts.ConstsParamNames.Stroke.AVERAGE_VELOCITY, 0, 0, 0);
	}
	
	protected void CreateDoubleNorm(String name, double mean, double standardDev, double internalStandardDev)
	{
		mHashNorms.put(name, new NormData(name, mean, standardDev, internalStandardDev));
	}
}
