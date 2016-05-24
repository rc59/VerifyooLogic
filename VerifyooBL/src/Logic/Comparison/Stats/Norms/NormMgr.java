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
		
		CreateDoubleNorm(Consts.ConstsParamNames.Gesture.LENGTH, ConstsInstructions.INSTRUCTION_LETTER_A, 103, 31, 11);
		CreateDoubleNorm(Consts.ConstsParamNames.Gesture.LENGTH, ConstsInstructions.INSTRUCTION_EIGHT, 117, 36, 12);
		CreateDoubleNorm(Consts.ConstsParamNames.Gesture.LENGTH, ConstsInstructions.INSTRUCTION_FIVE, 93, 30, 10);
		CreateDoubleNorm(Consts.ConstsParamNames.Gesture.LENGTH, ConstsInstructions.INSTRUCTION_HEART, 129, 29, 11);
		CreateDoubleNorm(Consts.ConstsParamNames.Gesture.LENGTH, ConstsInstructions.INSTRUCTION_LINES, 87, 28, 9);
		CreateDoubleNorm(Consts.ConstsParamNames.Gesture.LENGTH, ConstsInstructions.INSTRUCTION_LETTER_R, 118, 40, 12);		
		CreateDoubleNorm(Consts.ConstsParamNames.Gesture.LENGTH, ConstsInstructions.INSTRUCTION_TRIANGLE, 113, 38, 10);
		
		CreateDoubleNorm(Consts.ConstsParamNames.Gesture.GESTURE_TOTAL_TIME_WITH_PAUSES, ConstsInstructions.INSTRUCTION_LETTER_A, 611, 202, 118);
		CreateDoubleNorm(Consts.ConstsParamNames.Gesture.GESTURE_TOTAL_TIME_WITH_PAUSES, ConstsInstructions.INSTRUCTION_EIGHT, 828, 286, 167);
		CreateDoubleNorm(Consts.ConstsParamNames.Gesture.GESTURE_TOTAL_TIME_WITH_PAUSES, ConstsInstructions.INSTRUCTION_FIVE, 762, 244, 140);
		CreateDoubleNorm(Consts.ConstsParamNames.Gesture.GESTURE_TOTAL_TIME_WITH_PAUSES, ConstsInstructions.INSTRUCTION_HEART, 1089, 371, 223);
		CreateDoubleNorm(Consts.ConstsParamNames.Gesture.GESTURE_TOTAL_TIME_WITH_PAUSES, ConstsInstructions.INSTRUCTION_LINES, 563, 405, 291);
		CreateDoubleNorm(Consts.ConstsParamNames.Gesture.GESTURE_TOTAL_TIME_WITH_PAUSES, ConstsInstructions.INSTRUCTION_LETTER_R, 1011, 296, 225);		
		CreateDoubleNorm(Consts.ConstsParamNames.Gesture.GESTURE_TOTAL_TIME_WITH_PAUSES, ConstsInstructions.INSTRUCTION_TRIANGLE, 853, 305, 145);
		
		CreateDoubleNorm(Consts.ConstsParamNames.Gesture.GESTURE_TOTAL_STROKE_AREA, ConstsInstructions.INSTRUCTION_LETTER_A, 497, 178, 95);
		CreateDoubleNorm(Consts.ConstsParamNames.Gesture.GESTURE_TOTAL_STROKE_AREA, ConstsInstructions.INSTRUCTION_EIGHT, 647, 211, 121);
		CreateDoubleNorm(Consts.ConstsParamNames.Gesture.GESTURE_TOTAL_STROKE_AREA, ConstsInstructions.INSTRUCTION_FIVE, 500, 196, 102);
		CreateDoubleNorm(Consts.ConstsParamNames.Gesture.GESTURE_TOTAL_STROKE_AREA, ConstsInstructions.INSTRUCTION_HEART, 837, 296, 129);
		CreateDoubleNorm(Consts.ConstsParamNames.Gesture.GESTURE_TOTAL_STROKE_AREA, ConstsInstructions.INSTRUCTION_LINES, 419, 162, 66);
		CreateDoubleNorm(Consts.ConstsParamNames.Gesture.GESTURE_TOTAL_STROKE_AREA, ConstsInstructions.INSTRUCTION_LETTER_R, 591, 218, 105);		
		CreateDoubleNorm(Consts.ConstsParamNames.Gesture.GESTURE_TOTAL_STROKE_AREA, ConstsInstructions.INSTRUCTION_TRIANGLE, 734, 283, 118);
		
		CreateDoubleNorm(Consts.ConstsParamNames.Gesture.GESTURE_AVG_PRESSURE, ConstsInstructions.INSTRUCTION_LETTER_A, 0.62, 0.0368, 0.0101);
		CreateDoubleNorm(Consts.ConstsParamNames.Gesture.GESTURE_AVG_PRESSURE, ConstsInstructions.INSTRUCTION_EIGHT, 0.6219, 0.0378, 0.0108);
		CreateDoubleNorm(Consts.ConstsParamNames.Gesture.GESTURE_AVG_PRESSURE, ConstsInstructions.INSTRUCTION_FIVE, 0.6241, 0.0382, 0.0112);
		CreateDoubleNorm(Consts.ConstsParamNames.Gesture.GESTURE_AVG_PRESSURE, ConstsInstructions.INSTRUCTION_HEART, 0.623, 0.0399, 0.0087);
		CreateDoubleNorm(Consts.ConstsParamNames.Gesture.GESTURE_AVG_PRESSURE, ConstsInstructions.INSTRUCTION_LINES, 0.6173, 0.0364, 0.009);
		CreateDoubleNorm(Consts.ConstsParamNames.Gesture.GESTURE_AVG_PRESSURE, ConstsInstructions.INSTRUCTION_LETTER_R, 0.625, 0.0387, 0.0087);		
		CreateDoubleNorm(Consts.ConstsParamNames.Gesture.GESTURE_AVG_PRESSURE, ConstsInstructions.INSTRUCTION_TRIANGLE, 0.6274, 0.0375, 0.0086);
	
		CreateDoubleNorm(Consts.ConstsParamNames.Gesture.GESTURE_AVG_SURFACE, ConstsInstructions.INSTRUCTION_LETTER_A, 0.3167, 0.0397, 0.0147);
		CreateDoubleNorm(Consts.ConstsParamNames.Gesture.GESTURE_AVG_SURFACE, ConstsInstructions.INSTRUCTION_EIGHT, 0.3118, 0.0417, 0.0182);
		CreateDoubleNorm(Consts.ConstsParamNames.Gesture.GESTURE_AVG_SURFACE, ConstsInstructions.INSTRUCTION_FIVE, 0.3177, 0.0414, 0.0185);
		CreateDoubleNorm(Consts.ConstsParamNames.Gesture.GESTURE_AVG_SURFACE, ConstsInstructions.INSTRUCTION_HEART, 0.3121, 0.0417, 0.0163);
		CreateDoubleNorm(Consts.ConstsParamNames.Gesture.GESTURE_AVG_SURFACE, ConstsInstructions.INSTRUCTION_LINES, 0.3194, 0.0368, 0.0142);
		CreateDoubleNorm(Consts.ConstsParamNames.Gesture.GESTURE_AVG_SURFACE, ConstsInstructions.INSTRUCTION_LETTER_R, 0.3199, 0.0422, 0.0143);		
		CreateDoubleNorm(Consts.ConstsParamNames.Gesture.GESTURE_AVG_SURFACE, ConstsInstructions.INSTRUCTION_TRIANGLE, 0.319, 0.041, 0.0142);
		
		CreateDoubleNorm(Consts.ConstsParamNames.Gesture.AVERAGE_VELOCITY, ConstsInstructions.INSTRUCTION_LETTER_A, 0.1829, 0.0638, 0.028);
		CreateDoubleNorm(Consts.ConstsParamNames.Gesture.AVERAGE_VELOCITY, ConstsInstructions.INSTRUCTION_EIGHT, 0.1534, 0.0541, 0.0229);
		CreateDoubleNorm(Consts.ConstsParamNames.Gesture.AVERAGE_VELOCITY, ConstsInstructions.INSTRUCTION_FIVE, 0.1314, 0.0464, 0.021);
		CreateDoubleNorm(Consts.ConstsParamNames.Gesture.AVERAGE_VELOCITY, ConstsInstructions.INSTRUCTION_HEART, 0.1346, 0.0518, 0.022);
		CreateDoubleNorm(Consts.ConstsParamNames.Gesture.AVERAGE_VELOCITY, ConstsInstructions.INSTRUCTION_LINES, 0.2131, 0.0965, 0.0497);		
		CreateDoubleNorm(Consts.ConstsParamNames.Gesture.AVERAGE_VELOCITY, ConstsInstructions.INSTRUCTION_LETTER_R, 0.1441, 0.0541, 0.0212);			
		CreateDoubleNorm(Consts.ConstsParamNames.Gesture.AVERAGE_VELOCITY, ConstsInstructions.INSTRUCTION_TRIANGLE, 0.1457, 0.0546, 0.02);	
		
		CreateDoubleNorm(Consts.ConstsParamNames.Gesture.AVG_START_ACCELERATION, ConstsInstructions.INSTRUCTION_LETTER_A, 0.0037, 0.0013, 0.00087);
		CreateDoubleNorm(Consts.ConstsParamNames.Gesture.AVG_START_ACCELERATION, ConstsInstructions.INSTRUCTION_EIGHT, 0.0022, 0.00085, 0.00069);
		CreateDoubleNorm(Consts.ConstsParamNames.Gesture.AVG_START_ACCELERATION, ConstsInstructions.INSTRUCTION_FIVE, 0.0021, 0.00083, 0.00061);
		CreateDoubleNorm(Consts.ConstsParamNames.Gesture.AVG_START_ACCELERATION, ConstsInstructions.INSTRUCTION_HEART, 0.00182, 0.00069, 0.00053);
		CreateDoubleNorm(Consts.ConstsParamNames.Gesture.AVG_START_ACCELERATION, ConstsInstructions.INSTRUCTION_LINES, 0.0034, 0.00161, 0.00096);		
		CreateDoubleNorm(Consts.ConstsParamNames.Gesture.AVG_START_ACCELERATION, ConstsInstructions.INSTRUCTION_LETTER_R, 0.0035, 0.0013, 0.00077);			
		CreateDoubleNorm(Consts.ConstsParamNames.Gesture.AVG_START_ACCELERATION, ConstsInstructions.INSTRUCTION_TRIANGLE, 0.0033, 0.0011, 0.00069);

		CreateDoubleNorm(Consts.ConstsParamNames.Gesture.GESTURE_AVG_START_DIRECTION, ConstsInstructions.INSTRUCTION_LETTER_A, 3.9216, 1.469, 0.3);
		CreateDoubleNorm(Consts.ConstsParamNames.Gesture.GESTURE_AVG_START_DIRECTION, ConstsInstructions.INSTRUCTION_EIGHT, 3.8615, 1.7749, 0.3);
		CreateDoubleNorm(Consts.ConstsParamNames.Gesture.GESTURE_AVG_START_DIRECTION, ConstsInstructions.INSTRUCTION_FIVE, 2.794, 1.4553, 0.3);
		CreateDoubleNorm(Consts.ConstsParamNames.Gesture.GESTURE_AVG_START_DIRECTION, ConstsInstructions.INSTRUCTION_HEART, 4.628, 0.7115, 0.3);
		CreateDoubleNorm(Consts.ConstsParamNames.Gesture.GESTURE_AVG_START_DIRECTION, ConstsInstructions.INSTRUCTION_LINES, 3.7860, 2.6757, 0.3);
		CreateDoubleNorm(Consts.ConstsParamNames.Gesture.GESTURE_AVG_START_DIRECTION, ConstsInstructions.INSTRUCTION_LETTER_R, 2.1949, 1.2685, 0.3);		
		CreateDoubleNorm(Consts.ConstsParamNames.Gesture.GESTURE_AVG_START_DIRECTION, ConstsInstructions.INSTRUCTION_TRIANGLE, 113, 38, 0.3);

		CreateDoubleNorm(Consts.ConstsParamNames.Gesture.GESTURE_AVG_END_DIRECTION, ConstsInstructions.INSTRUCTION_LETTER_A, 3.9216, 1.469, 0.3);
		CreateDoubleNorm(Consts.ConstsParamNames.Gesture.GESTURE_AVG_END_DIRECTION, ConstsInstructions.INSTRUCTION_EIGHT, 3.8615, 1.7749, 0.3);
		CreateDoubleNorm(Consts.ConstsParamNames.Gesture.GESTURE_AVG_END_DIRECTION, ConstsInstructions.INSTRUCTION_FIVE, 2.794, 1.4553, 0.3);
		CreateDoubleNorm(Consts.ConstsParamNames.Gesture.GESTURE_AVG_END_DIRECTION, ConstsInstructions.INSTRUCTION_HEART, 4.628, 0.7115, 0.3);
		CreateDoubleNorm(Consts.ConstsParamNames.Gesture.GESTURE_AVG_END_DIRECTION, ConstsInstructions.INSTRUCTION_LINES, 3.7860, 2.6757, 0.3);
		CreateDoubleNorm(Consts.ConstsParamNames.Gesture.GESTURE_AVG_END_DIRECTION, ConstsInstructions.INSTRUCTION_LETTER_R, 2.1949, 1.2685, 0.3);		
		CreateDoubleNorm(Consts.ConstsParamNames.Gesture.GESTURE_AVG_END_DIRECTION, ConstsInstructions.INSTRUCTION_TRIANGLE, 113, 38, 0.3);
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
