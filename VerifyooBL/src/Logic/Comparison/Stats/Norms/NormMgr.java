package Logic.Comparison.Stats.Norms;

import java.util.HashMap;

import Consts.ConstsInstructions;
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
	
	public INormData GetNormDataByParamName(String name, String instruction) {
		String key = CreateNormKey(name, instruction);
		return mHashNorms.get(key);
	}
	
	protected void InitNorms()
	{
		mHashNorms = new HashMap<String, INormData>();	
		
		CreateDoubleNorm(Consts.ConstsParamNames.Gesture.AVERAGE_VELOCITY, ConstsInstructions.INSTRUCTION_EIGHT, 2, 1, 0.3);
		CreateDoubleNorm(Consts.ConstsParamNames.Gesture.AVERAGE_VELOCITY, ConstsInstructions.INSTRUCTION_FIVE, 2, 1, 0.3);
		CreateDoubleNorm(Consts.ConstsParamNames.Gesture.AVERAGE_VELOCITY, ConstsInstructions.INSTRUCTION_HEART, 2, 1, 0.3);
		CreateDoubleNorm(Consts.ConstsParamNames.Gesture.AVERAGE_VELOCITY, ConstsInstructions.INSTRUCTION_LETTER_A, 2, 1, 0.3);
		CreateDoubleNorm(Consts.ConstsParamNames.Gesture.AVERAGE_VELOCITY, ConstsInstructions.INSTRUCTION_LETTER_R, 2, 1, 0.3);
		CreateDoubleNorm(Consts.ConstsParamNames.Gesture.AVERAGE_VELOCITY, ConstsInstructions.INSTRUCTION_LINES, 2, 1, 0.3);		
	}
	
	protected void CreateDoubleNorm(String name, String instruction, double mean, double standardDev, double internalStandardDev)
	{
		String key = CreateNormKey(name, instruction);
		mHashNorms.put(key, new NormData(name, mean, standardDev, internalStandardDev));
	}
	
	protected String CreateNormKey(String name, String instruction) {
		String key = String.format("%s-%s", name, instruction);
		return key;
	}
}
