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
		
		CreateDoubleNorm(Consts.ConstsParamNames.Gesture.AVERAGE_VELOCITY, ConstsInstructions.INSTRUCTION_EIGHT, 0.1534, 0.0541, 0.3);
		CreateDoubleNorm(Consts.ConstsParamNames.Gesture.AVERAGE_VELOCITY, ConstsInstructions.INSTRUCTION_FIVE, 0.1314, 0.0464, 0.3);
		CreateDoubleNorm(Consts.ConstsParamNames.Gesture.AVERAGE_VELOCITY, ConstsInstructions.INSTRUCTION_HEART, 0.1346, 0.0518, 0.3);
		CreateDoubleNorm(Consts.ConstsParamNames.Gesture.AVERAGE_VELOCITY, ConstsInstructions.INSTRUCTION_LETTER_A, 0.1829, 0.0638, 0.3);
		CreateDoubleNorm(Consts.ConstsParamNames.Gesture.AVERAGE_VELOCITY, ConstsInstructions.INSTRUCTION_LETTER_R, 0.1441, 0.0541, 0.3);
		CreateDoubleNorm(Consts.ConstsParamNames.Gesture.AVERAGE_VELOCITY, ConstsInstructions.INSTRUCTION_LINES, 0.2131, 0.0965, 0.3);		
		CreateDoubleNorm(Consts.ConstsParamNames.Gesture.AVERAGE_VELOCITY, ConstsInstructions.INSTRUCTION_TRIANGLE, 0.1457, 0.0546, 0.3);
		
		CreateDoubleNorm(Consts.ConstsParamNames.Gesture.LENGTH, ConstsInstructions.INSTRUCTION_EIGHT, 117, 36, 0.3);
		CreateDoubleNorm(Consts.ConstsParamNames.Gesture.LENGTH, ConstsInstructions.INSTRUCTION_FIVE, 93, 30, 0.3);
		CreateDoubleNorm(Consts.ConstsParamNames.Gesture.LENGTH, ConstsInstructions.INSTRUCTION_HEART, 129, 29, 0.3);
		CreateDoubleNorm(Consts.ConstsParamNames.Gesture.LENGTH, ConstsInstructions.INSTRUCTION_LETTER_A, 103, 31, 0.3);
		CreateDoubleNorm(Consts.ConstsParamNames.Gesture.LENGTH, ConstsInstructions.INSTRUCTION_LETTER_R, 118, 40, 0.3);
		CreateDoubleNorm(Consts.ConstsParamNames.Gesture.LENGTH, ConstsInstructions.INSTRUCTION_LINES, 87, 28, 0.3);
		CreateDoubleNorm(Consts.ConstsParamNames.Gesture.LENGTH, ConstsInstructions.INSTRUCTION_TRIANGLE, 113, 38, 0.3);
		
		CreateDoubleNorm(Consts.ConstsParamNames.Gesture.GESTURE_TOTAL_TIME_WITH_PAUSES, ConstsInstructions.INSTRUCTION_EIGHT, 828, 286, 0.3);
		CreateDoubleNorm(Consts.ConstsParamNames.Gesture.GESTURE_TOTAL_TIME_WITH_PAUSES, ConstsInstructions.INSTRUCTION_FIVE, 762, 244, 0.3);
		CreateDoubleNorm(Consts.ConstsParamNames.Gesture.GESTURE_TOTAL_TIME_WITH_PAUSES, ConstsInstructions.INSTRUCTION_HEART, 1089, 371, 0.3);
		CreateDoubleNorm(Consts.ConstsParamNames.Gesture.GESTURE_TOTAL_TIME_WITH_PAUSES, ConstsInstructions.INSTRUCTION_LETTER_A, 611, 202, 0.3);
		CreateDoubleNorm(Consts.ConstsParamNames.Gesture.GESTURE_TOTAL_TIME_WITH_PAUSES, ConstsInstructions.INSTRUCTION_LETTER_R, 905, 293, 0.3);
		CreateDoubleNorm(Consts.ConstsParamNames.Gesture.GESTURE_TOTAL_TIME_WITH_PAUSES, ConstsInstructions.INSTRUCTION_LINES, 563, 405, 0.3);
		CreateDoubleNorm(Consts.ConstsParamNames.Gesture.GESTURE_TOTAL_TIME_WITH_PAUSES, ConstsInstructions.INSTRUCTION_TRIANGLE, 853, 305, 0.3);
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
