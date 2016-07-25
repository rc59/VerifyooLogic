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
		
		if(!mHashNorms.containsKey(key)) {
			key = CreateNormKey(name, ConstsInstructions.INSTRUCTION_LETTER_R);
		}
		
		return mHashNorms.get(key);
	}
	
	protected void InitNorms()
	{
		mHashNorms = new HashMap<String, INormData>();	
		
		CreateDoubleNorm(Consts.ConstsParamNames.Gesture.GESTURE_LENGTH, ConstsInstructions.INSTRUCTION_LETTER_A, 93.55521422, 17.21523105, 10.44128015);
		CreateDoubleNorm(Consts.ConstsParamNames.Gesture.GESTURE_LENGTH, ConstsInstructions.INSTRUCTION_EIGHT, 105.7476935, 18.46938408, 11.23873733);
		CreateDoubleNorm(Consts.ConstsParamNames.Gesture.GESTURE_LENGTH, ConstsInstructions.INSTRUCTION_FIVE, 84.78660255, 17.30688045, 9.618177846);
		CreateDoubleNorm(Consts.ConstsParamNames.Gesture.GESTURE_LENGTH, ConstsInstructions.INSTRUCTION_TWO, 84.78660255, 17.30688045, 9.618177846);
		CreateDoubleNorm(Consts.ConstsParamNames.Gesture.GESTURE_LENGTH, ConstsInstructions.INSTRUCTION_HEART, 120.1562167, 22.18709124, 10.27991878);
		CreateDoubleNorm(Consts.ConstsParamNames.Gesture.GESTURE_LENGTH, ConstsInstructions.INSTRUCTION_LINES, 78.77034064, 16.48275274, 6.744349668);
		CreateDoubleNorm(Consts.ConstsParamNames.Gesture.GESTURE_LENGTH, ConstsInstructions.INSTRUCTION_LETTER_R, 106.0478773, 21.60714919, 10.72637052);
		CreateDoubleNorm(Consts.ConstsParamNames.Gesture.GESTURE_LENGTH, ConstsInstructions.INSTRUCTION_LETTER_B, 106.0478773, 21.60714919, 10.72637052);
		CreateDoubleNorm(Consts.ConstsParamNames.Gesture.GESTURE_LENGTH, ConstsInstructions.INSTRUCTION_LETTER_G, 106.0478773, 21.60714919, 10.72637052);
		CreateDoubleNorm(Consts.ConstsParamNames.Gesture.GESTURE_LENGTH, ConstsInstructions.INSTRUCTION_TRIANGLE, 102.3894843, 19.59429855, 8.479447917);
		
		CreateDoubleNorm(Consts.ConstsParamNames.Gesture.GESTURE_TOTAL_TIME_INTERNVAL, ConstsInstructions.INSTRUCTION_LETTER_A, 1059.309524, 300.4595957, 193.8438481);
		CreateDoubleNorm(Consts.ConstsParamNames.Gesture.GESTURE_TOTAL_TIME_INTERNVAL, ConstsInstructions.INSTRUCTION_EIGHT, 832.1630952, 257.0896665, 116.3212893);
		CreateDoubleNorm(Consts.ConstsParamNames.Gesture.GESTURE_TOTAL_TIME_INTERNVAL, ConstsInstructions.INSTRUCTION_FIVE, 968.2738095, 240.1880631, 175.2205727);
		CreateDoubleNorm(Consts.ConstsParamNames.Gesture.GESTURE_TOTAL_TIME_INTERNVAL, ConstsInstructions.INSTRUCTION_TWO, 968.2738095, 240.1880631, 175.2205727);
		CreateDoubleNorm(Consts.ConstsParamNames.Gesture.GESTURE_TOTAL_TIME_INTERNVAL, ConstsInstructions.INSTRUCTION_HEART, 1289.494512, 421.3449272, 195.9391878);
		CreateDoubleNorm(Consts.ConstsParamNames.Gesture.GESTURE_TOTAL_TIME_INTERNVAL, ConstsInstructions.INSTRUCTION_LINES, 1094.315675, 623.4158216, 501.7551822);
		CreateDoubleNorm(Consts.ConstsParamNames.Gesture.GESTURE_TOTAL_TIME_INTERNVAL, ConstsInstructions.INSTRUCTION_LETTER_R, 1011.468254, 296.3718158, 156.6816371);
		CreateDoubleNorm(Consts.ConstsParamNames.Gesture.GESTURE_TOTAL_TIME_INTERNVAL, ConstsInstructions.INSTRUCTION_LETTER_B, 1011.468254, 296.3718158, 156.6816371);
		CreateDoubleNorm(Consts.ConstsParamNames.Gesture.GESTURE_TOTAL_TIME_INTERNVAL, ConstsInstructions.INSTRUCTION_LETTER_G, 1011.468254, 296.3718158, 156.6816371);
		CreateDoubleNorm(Consts.ConstsParamNames.Gesture.GESTURE_TOTAL_TIME_INTERNVAL, ConstsInstructions.INSTRUCTION_TRIANGLE, 942.475, 326.0206126, 135.271191);
		
		CreateDoubleNorm(Consts.ConstsParamNames.Gesture.GESTURE_TOTAL_STROKES_TIME_INTERVAL, ConstsInstructions.INSTRUCTION_LETTER_A, 650.2123016, 200.2191041, 114.0480401);
		CreateDoubleNorm(Consts.ConstsParamNames.Gesture.GESTURE_TOTAL_STROKES_TIME_INTERVAL, ConstsInstructions.INSTRUCTION_EIGHT, 829.893254, 252.7467325, 114.0677726);
		CreateDoubleNorm(Consts.ConstsParamNames.Gesture.GESTURE_TOTAL_STROKES_TIME_INTERVAL, ConstsInstructions.INSTRUCTION_FIVE, 796.2281746, 237.3834764, 146.9057538);
		CreateDoubleNorm(Consts.ConstsParamNames.Gesture.GESTURE_TOTAL_STROKES_TIME_INTERVAL, ConstsInstructions.INSTRUCTION_TWO, 796.2281746, 237.3834764, 146.9057538);
		CreateDoubleNorm(Consts.ConstsParamNames.Gesture.GESTURE_TOTAL_STROKES_TIME_INTERVAL, ConstsInstructions.INSTRUCTION_HEART, 1110.794106, 395.6749704, 167.4229897);
		CreateDoubleNorm(Consts.ConstsParamNames.Gesture.GESTURE_TOTAL_STROKES_TIME_INTERVAL, ConstsInstructions.INSTRUCTION_LINES, 528.4311508, 244.9139692, 152.2470991);
		CreateDoubleNorm(Consts.ConstsParamNames.Gesture.GESTURE_TOTAL_STROKES_TIME_INTERVAL, ConstsInstructions.INSTRUCTION_LETTER_R, 895.3265873, 240.060879, 144.108074);
		CreateDoubleNorm(Consts.ConstsParamNames.Gesture.GESTURE_TOTAL_STROKES_TIME_INTERVAL, ConstsInstructions.INSTRUCTION_LETTER_B, 895.3265873, 240.060879, 144.108074);
		CreateDoubleNorm(Consts.ConstsParamNames.Gesture.GESTURE_TOTAL_STROKES_TIME_INTERVAL, ConstsInstructions.INSTRUCTION_LETTER_G, 895.3265873, 240.060879, 144.108074);
		CreateDoubleNorm(Consts.ConstsParamNames.Gesture.GESTURE_TOTAL_STROKES_TIME_INTERVAL, ConstsInstructions.INSTRUCTION_TRIANGLE, 862.5202381, 286.8737392, 111.6668879);
		
		CreateDoubleNorm(Consts.ConstsParamNames.Gesture.GESTURE_TOTAL_AREA, ConstsInstructions.INSTRUCTION_LETTER_A, 340.4691991, 80, 40);
		CreateDoubleNorm(Consts.ConstsParamNames.Gesture.GESTURE_TOTAL_AREA, ConstsInstructions.INSTRUCTION_EIGHT, 433.4139384, 80, 40);
		CreateDoubleNorm(Consts.ConstsParamNames.Gesture.GESTURE_TOTAL_AREA, ConstsInstructions.INSTRUCTION_FIVE, 427.2703019, 80, 40);
		CreateDoubleNorm(Consts.ConstsParamNames.Gesture.GESTURE_TOTAL_AREA, ConstsInstructions.INSTRUCTION_TWO, 427.2703019, 80, 40);
		CreateDoubleNorm(Consts.ConstsParamNames.Gesture.GESTURE_TOTAL_AREA, ConstsInstructions.INSTRUCTION_HEART, 672.5870012, 80, 40);
		CreateDoubleNorm(Consts.ConstsParamNames.Gesture.GESTURE_TOTAL_AREA, ConstsInstructions.INSTRUCTION_LINES, 166.0208788, 66.89662978, 34.97674742);
		CreateDoubleNorm(Consts.ConstsParamNames.Gesture.GESTURE_TOTAL_AREA, ConstsInstructions.INSTRUCTION_LETTER_R, 390.6085287, 80, 40);
		CreateDoubleNorm(Consts.ConstsParamNames.Gesture.GESTURE_TOTAL_AREA, ConstsInstructions.INSTRUCTION_LETTER_B, 390.6085287, 80, 40);
		CreateDoubleNorm(Consts.ConstsParamNames.Gesture.GESTURE_TOTAL_AREA, ConstsInstructions.INSTRUCTION_LETTER_G, 390.6085287, 80, 40);
		CreateDoubleNorm(Consts.ConstsParamNames.Gesture.GESTURE_TOTAL_AREA, ConstsInstructions.INSTRUCTION_TRIANGLE, 524.5238721, 80, 40);
		
		CreateDoubleNorm(Consts.ConstsParamNames.Gesture.GESTURE_TOTAL_AREA_MINX_MINY, ConstsInstructions.INSTRUCTION_LETTER_A, 822.8867131, 80, 40);
		CreateDoubleNorm(Consts.ConstsParamNames.Gesture.GESTURE_TOTAL_AREA_MINX_MINY, ConstsInstructions.INSTRUCTION_EIGHT, 772.6338984, 80, 40);
		CreateDoubleNorm(Consts.ConstsParamNames.Gesture.GESTURE_TOTAL_AREA_MINX_MINY, ConstsInstructions.INSTRUCTION_FIVE, 732.5861208, 80, 40);
		CreateDoubleNorm(Consts.ConstsParamNames.Gesture.GESTURE_TOTAL_AREA_MINX_MINY, ConstsInstructions.INSTRUCTION_TWO, 732.5861208, 80, 40);
		CreateDoubleNorm(Consts.ConstsParamNames.Gesture.GESTURE_TOTAL_AREA_MINX_MINY, ConstsInstructions.INSTRUCTION_HEART, 1026.309622, 80, 40);
		CreateDoubleNorm(Consts.ConstsParamNames.Gesture.GESTURE_TOTAL_AREA_MINX_MINY, ConstsInstructions.INSTRUCTION_LINES, 1073.823708, 80, 40);
		CreateDoubleNorm(Consts.ConstsParamNames.Gesture.GESTURE_TOTAL_AREA_MINX_MINY, ConstsInstructions.INSTRUCTION_LETTER_R, 517.3333015, 80, 40);
		CreateDoubleNorm(Consts.ConstsParamNames.Gesture.GESTURE_TOTAL_AREA_MINX_MINY, ConstsInstructions.INSTRUCTION_LETTER_B, 517.3333015, 80, 40);
		CreateDoubleNorm(Consts.ConstsParamNames.Gesture.GESTURE_TOTAL_AREA_MINX_MINY, ConstsInstructions.INSTRUCTION_LETTER_G, 517.3333015, 80, 40);
		CreateDoubleNorm(Consts.ConstsParamNames.Gesture.GESTURE_TOTAL_AREA_MINX_MINY, ConstsInstructions.INSTRUCTION_TRIANGLE, 1094.563049, 80, 40);
		
		CreateDoubleNorm(Consts.ConstsParamNames.Gesture.GESTURE_TOTAL_STROKE_AREA, ConstsInstructions.INSTRUCTION_LETTER_A, 240.5617317, 157.4897735, 63.73826156);
		CreateDoubleNorm(Consts.ConstsParamNames.Gesture.GESTURE_TOTAL_STROKE_AREA, ConstsInstructions.INSTRUCTION_EIGHT, 433.2700784, 147.5347102, 90.18088133);
		CreateDoubleNorm(Consts.ConstsParamNames.Gesture.GESTURE_TOTAL_STROKE_AREA, ConstsInstructions.INSTRUCTION_FIVE, 393.4710849, 175.3227522, 87.9297328);
		CreateDoubleNorm(Consts.ConstsParamNames.Gesture.GESTURE_TOTAL_STROKE_AREA, ConstsInstructions.INSTRUCTION_TWO, 393.4710849, 175.3227522, 87.9297328);
		CreateDoubleNorm(Consts.ConstsParamNames.Gesture.GESTURE_TOTAL_STROKE_AREA, ConstsInstructions.INSTRUCTION_HEART, 656.0202127, 249.9448356, 105.3346823);
		CreateDoubleNorm(Consts.ConstsParamNames.Gesture.GESTURE_TOTAL_STROKE_AREA, ConstsInstructions.INSTRUCTION_LINES, 45.65018208, 22.4756816, 19.18841842);
		CreateDoubleNorm(Consts.ConstsParamNames.Gesture.GESTURE_TOTAL_STROKE_AREA, ConstsInstructions.INSTRUCTION_LETTER_R, 359.5125233, 153.8228612, 81.30744522);
		CreateDoubleNorm(Consts.ConstsParamNames.Gesture.GESTURE_TOTAL_STROKE_AREA, ConstsInstructions.INSTRUCTION_LETTER_B, 359.5125233, 153.8228612, 81.30744522);
		CreateDoubleNorm(Consts.ConstsParamNames.Gesture.GESTURE_TOTAL_STROKE_AREA, ConstsInstructions.INSTRUCTION_LETTER_G, 359.5125233, 153.8228612, 81.30744522);
		CreateDoubleNorm(Consts.ConstsParamNames.Gesture.GESTURE_TOTAL_STROKE_AREA, ConstsInstructions.INSTRUCTION_TRIANGLE, 505.7859291, 223.7405354, 95.66763784);

		CreateDoubleNorm(Consts.ConstsParamNames.Gesture.GESTURE_TOTAL_STROKE_AREA_MINX_MINY, ConstsInstructions.INSTRUCTION_LETTER_A, 460.2064764, 224.1822172, 117.7819728);
		CreateDoubleNorm(Consts.ConstsParamNames.Gesture.GESTURE_TOTAL_STROKE_AREA_MINX_MINY, ConstsInstructions.INSTRUCTION_EIGHT, 770.3720762, 278.7222401, 157.9269668);
		CreateDoubleNorm(Consts.ConstsParamNames.Gesture.GESTURE_TOTAL_STROKE_AREA_MINX_MINY, ConstsInstructions.INSTRUCTION_FIVE, 628.8722207, 244.7957889, 136.0406264);
		CreateDoubleNorm(Consts.ConstsParamNames.Gesture.GESTURE_TOTAL_STROKE_AREA_MINX_MINY, ConstsInstructions.INSTRUCTION_TWO, 628.8722207, 244.7957889, 136.0406264);
		CreateDoubleNorm(Consts.ConstsParamNames.Gesture.GESTURE_TOTAL_STROKE_AREA_MINX_MINY, ConstsInstructions.INSTRUCTION_HEART, 892.8316327, 375.1922618, 160.1505867);
		CreateDoubleNorm(Consts.ConstsParamNames.Gesture.GESTURE_TOTAL_STROKE_AREA_MINX_MINY, ConstsInstructions.INSTRUCTION_LINES, 445.4315687, 226.7330906, 97.41380455);
		CreateDoubleNorm(Consts.ConstsParamNames.Gesture.GESTURE_TOTAL_STROKE_AREA_MINX_MINY, ConstsInstructions.INSTRUCTION_LETTER_R, 447.3779039, 186.7860089, 101.5870895);
		CreateDoubleNorm(Consts.ConstsParamNames.Gesture.GESTURE_TOTAL_STROKE_AREA_MINX_MINY, ConstsInstructions.INSTRUCTION_LETTER_B, 447.3779039, 186.7860089, 101.5870895);
		CreateDoubleNorm(Consts.ConstsParamNames.Gesture.GESTURE_TOTAL_STROKE_AREA_MINX_MINY, ConstsInstructions.INSTRUCTION_LETTER_G, 447.3779039, 186.7860089, 101.5870895);
		CreateDoubleNorm(Consts.ConstsParamNames.Gesture.GESTURE_TOTAL_STROKE_AREA_MINX_MINY, ConstsInstructions.INSTRUCTION_TRIANGLE, 1014.121918, 449.0458622, 193.5995278);
		
		CreateDoubleNorm(Consts.ConstsParamNames.Gesture.GESTURE_MIDDLE_PRESSURE, ConstsInstructions.INSTRUCTION_LETTER_A, 0.627497502, 0.036586671, 0.035);
		CreateDoubleNorm(Consts.ConstsParamNames.Gesture.GESTURE_MIDDLE_PRESSURE, ConstsInstructions.INSTRUCTION_EIGHT, 0.623418687, 0.037396351, 0.012282458);
		CreateDoubleNorm(Consts.ConstsParamNames.Gesture.GESTURE_MIDDLE_PRESSURE, ConstsInstructions.INSTRUCTION_FIVE, 0.63012886, 0.039808835, 0.012422994);
		CreateDoubleNorm(Consts.ConstsParamNames.Gesture.GESTURE_MIDDLE_PRESSURE, ConstsInstructions.INSTRUCTION_TWO, 0.63012886, 0.039808835, 0.012422994);
		CreateDoubleNorm(Consts.ConstsParamNames.Gesture.GESTURE_MIDDLE_PRESSURE, ConstsInstructions.INSTRUCTION_HEART, 0.628532363, 0.03949107, 0.010162123);
		CreateDoubleNorm(Consts.ConstsParamNames.Gesture.GESTURE_MIDDLE_PRESSURE, ConstsInstructions.INSTRUCTION_LINES, 0.627389569, 0.036188391, 0.008173112);
		CreateDoubleNorm(Consts.ConstsParamNames.Gesture.GESTURE_MIDDLE_PRESSURE, ConstsInstructions.INSTRUCTION_LETTER_R, 0.629450313, 0.040538899, 0.035);
		CreateDoubleNorm(Consts.ConstsParamNames.Gesture.GESTURE_MIDDLE_PRESSURE, ConstsInstructions.INSTRUCTION_LETTER_B, 0.629450313, 0.040538899, 0.035);
		CreateDoubleNorm(Consts.ConstsParamNames.Gesture.GESTURE_MIDDLE_PRESSURE, ConstsInstructions.INSTRUCTION_LETTER_G, 0.629450313, 0.040538899, 0.035);
		CreateDoubleNorm(Consts.ConstsParamNames.Gesture.GESTURE_MIDDLE_PRESSURE, ConstsInstructions.INSTRUCTION_TRIANGLE, 0.629362461, 0.038393136, 0.035);

		CreateDoubleNorm(Consts.ConstsParamNames.Gesture.GESTURE_MIDDLE_SURFACE, ConstsInstructions.INSTRUCTION_LETTER_A, 0.321720229, 0.040483894, 0.020054049);
		CreateDoubleNorm(Consts.ConstsParamNames.Gesture.GESTURE_MIDDLE_SURFACE, ConstsInstructions.INSTRUCTION_EIGHT, 0.310481942, 0.042630744, 0.019916935);
		CreateDoubleNorm(Consts.ConstsParamNames.Gesture.GESTURE_MIDDLE_SURFACE, ConstsInstructions.INSTRUCTION_FIVE, 0.321575205, 0.042574364, 0.020054049);
		CreateDoubleNorm(Consts.ConstsParamNames.Gesture.GESTURE_MIDDLE_SURFACE, ConstsInstructions.INSTRUCTION_TWO, 0.321575205, 0.042574364, 0.020054049);
		CreateDoubleNorm(Consts.ConstsParamNames.Gesture.GESTURE_MIDDLE_SURFACE, ConstsInstructions.INSTRUCTION_HEART, 0.313441749, 0.044167387, 0.017249448);
		CreateDoubleNorm(Consts.ConstsParamNames.Gesture.GESTURE_MIDDLE_SURFACE, ConstsInstructions.INSTRUCTION_LINES, 0.323105771, 0.038123625, 0.015153362);
		CreateDoubleNorm(Consts.ConstsParamNames.Gesture.GESTURE_MIDDLE_SURFACE, ConstsInstructions.INSTRUCTION_LETTER_R, 0.321057579, 0.044352519, 0.020054049);
		CreateDoubleNorm(Consts.ConstsParamNames.Gesture.GESTURE_MIDDLE_SURFACE, ConstsInstructions.INSTRUCTION_LETTER_B, 0.321057579, 0.044352519, 0.020054049);
		CreateDoubleNorm(Consts.ConstsParamNames.Gesture.GESTURE_MIDDLE_SURFACE, ConstsInstructions.INSTRUCTION_LETTER_G, 0.321057579, 0.044352519, 0.020054049);
		CreateDoubleNorm(Consts.ConstsParamNames.Gesture.GESTURE_MIDDLE_SURFACE, ConstsInstructions.INSTRUCTION_TRIANGLE, 0.318728261, 0.04213617, 0.015487542);
		
		CreateDoubleNorm(Consts.ConstsParamNames.Gesture.GESTURE_AVERAGE_VELOCITY, ConstsInstructions.INSTRUCTION_LETTER_A, 0.157833278, 0.048076821, 0.023630953);
		CreateDoubleNorm(Consts.ConstsParamNames.Gesture.GESTURE_AVERAGE_VELOCITY, ConstsInstructions.INSTRUCTION_EIGHT, 0.138361672, 0.039397399, 0.01915832);
		CreateDoubleNorm(Consts.ConstsParamNames.Gesture.GESTURE_AVERAGE_VELOCITY, ConstsInstructions.INSTRUCTION_FIVE, 0.116192202, 0.033246841, 0.018498927);
		CreateDoubleNorm(Consts.ConstsParamNames.Gesture.GESTURE_AVERAGE_VELOCITY, ConstsInstructions.INSTRUCTION_TWO, 0.116192202, 0.033246841, 0.018498927);
		CreateDoubleNorm(Consts.ConstsParamNames.Gesture.GESTURE_AVERAGE_VELOCITY, ConstsInstructions.INSTRUCTION_HEART, 0.121355752, 0.042197818, 0.018333625);
		CreateDoubleNorm(Consts.ConstsParamNames.Gesture.GESTURE_AVERAGE_VELOCITY, ConstsInstructions.INSTRUCTION_LINES, 0.183486184, 0.074393354, 0.038694098);
		CreateDoubleNorm(Consts.ConstsParamNames.Gesture.GESTURE_AVERAGE_VELOCITY, ConstsInstructions.INSTRUCTION_LETTER_R, 0.128492518, 0.038272707, 0.017123854);
		CreateDoubleNorm(Consts.ConstsParamNames.Gesture.GESTURE_AVERAGE_VELOCITY, ConstsInstructions.INSTRUCTION_LETTER_B, 0.128492518, 0.038272707, 0.017123854);
		CreateDoubleNorm(Consts.ConstsParamNames.Gesture.GESTURE_AVERAGE_VELOCITY, ConstsInstructions.INSTRUCTION_LETTER_G, 0.128492518, 0.038272707, 0.017123854);
		CreateDoubleNorm(Consts.ConstsParamNames.Gesture.GESTURE_AVERAGE_VELOCITY, ConstsInstructions.INSTRUCTION_TRIANGLE, 0.130169846, 0.041287598, 0.017113797);

		CreateDoubleNorm(Consts.ConstsParamNames.Gesture.GESTURE_MAX_VELOCITY, ConstsInstructions.INSTRUCTION_LETTER_A, 0.3070974, 0.077963218, 0.040394299);
		CreateDoubleNorm(Consts.ConstsParamNames.Gesture.GESTURE_MAX_VELOCITY, ConstsInstructions.INSTRUCTION_EIGHT, 0.273031407, 0.079292437, 0.047244983);
		CreateDoubleNorm(Consts.ConstsParamNames.Gesture.GESTURE_MAX_VELOCITY, ConstsInstructions.INSTRUCTION_FIVE, 0.245055476, 0.066979213, 0.040193248);
		CreateDoubleNorm(Consts.ConstsParamNames.Gesture.GESTURE_MAX_VELOCITY, ConstsInstructions.INSTRUCTION_TWO, 0.245055476, 0.066979213, 0.040193248);
		CreateDoubleNorm(Consts.ConstsParamNames.Gesture.GESTURE_MAX_VELOCITY, ConstsInstructions.INSTRUCTION_HEART, 0.250654174, 0.0757363, 0.040259052);
		CreateDoubleNorm(Consts.ConstsParamNames.Gesture.GESTURE_MAX_VELOCITY, ConstsInstructions.INSTRUCTION_LINES, 0.322536261, 0.105274801, 0.0551585);
		CreateDoubleNorm(Consts.ConstsParamNames.Gesture.GESTURE_MAX_VELOCITY, ConstsInstructions.INSTRUCTION_LETTER_R, 0.278383568, 0.070361056, 0.036584409);
		CreateDoubleNorm(Consts.ConstsParamNames.Gesture.GESTURE_MAX_VELOCITY, ConstsInstructions.INSTRUCTION_LETTER_B, 0.278383568, 0.070361056, 0.036584409);
		CreateDoubleNorm(Consts.ConstsParamNames.Gesture.GESTURE_MAX_VELOCITY, ConstsInstructions.INSTRUCTION_LETTER_G, 0.278383568, 0.070361056, 0.036584409);
		CreateDoubleNorm(Consts.ConstsParamNames.Gesture.GESTURE_MAX_VELOCITY, ConstsInstructions.INSTRUCTION_TRIANGLE, 0.281566146, 0.067906954, 0.038538378);
		
		CreateDoubleNorm(Consts.ConstsParamNames.Gesture.GESTURE_AVG_START_ACCELERATION, ConstsInstructions.INSTRUCTION_LETTER_A, 0.003777783, 0.001309961, 0.000878252);
		CreateDoubleNorm(Consts.ConstsParamNames.Gesture.GESTURE_AVG_START_ACCELERATION, ConstsInstructions.INSTRUCTION_EIGHT, 0.002216017, 0.000858459, 0.000697711);
		CreateDoubleNorm(Consts.ConstsParamNames.Gesture.GESTURE_AVG_START_ACCELERATION, ConstsInstructions.INSTRUCTION_FIVE, 0.002143748, 0.000835639, 0.000616016);
		CreateDoubleNorm(Consts.ConstsParamNames.Gesture.GESTURE_AVG_START_ACCELERATION, ConstsInstructions.INSTRUCTION_TWO, 0.002143748, 0.000835639, 0.000616016);
		CreateDoubleNorm(Consts.ConstsParamNames.Gesture.GESTURE_AVG_START_ACCELERATION, ConstsInstructions.INSTRUCTION_HEART, 0.001821811, 0.000690047, 0.00053372);
		CreateDoubleNorm(Consts.ConstsParamNames.Gesture.GESTURE_AVG_START_ACCELERATION, ConstsInstructions.INSTRUCTION_LINES, 0.003383996, 0.001615497, 0.000961595);
		CreateDoubleNorm(Consts.ConstsParamNames.Gesture.GESTURE_AVG_START_ACCELERATION, ConstsInstructions.INSTRUCTION_LETTER_R, 0.00356922, 0.001268349, 0.000771317);
		CreateDoubleNorm(Consts.ConstsParamNames.Gesture.GESTURE_AVG_START_ACCELERATION, ConstsInstructions.INSTRUCTION_LETTER_B, 0.00356922, 0.001268349, 0.000771317);
		CreateDoubleNorm(Consts.ConstsParamNames.Gesture.GESTURE_AVG_START_ACCELERATION, ConstsInstructions.INSTRUCTION_LETTER_G, 0.00356922, 0.001268349, 0.000771317);
		CreateDoubleNorm(Consts.ConstsParamNames.Gesture.GESTURE_AVG_START_ACCELERATION, ConstsInstructions.INSTRUCTION_TRIANGLE, 0.003364438, 0.001193147, 0.000692293);

		CreateDoubleNorm(Consts.ConstsParamNames.Gesture.GESTURE_AVG_ACCELERATION, ConstsInstructions.INSTRUCTION_LETTER_A, -0.000383147, 0.000336985, 0.00021218);
		CreateDoubleNorm(Consts.ConstsParamNames.Gesture.GESTURE_AVG_ACCELERATION, ConstsInstructions.INSTRUCTION_EIGHT, -0.00017192, 0.000159038, 0.000123308);
		CreateDoubleNorm(Consts.ConstsParamNames.Gesture.GESTURE_AVG_ACCELERATION, ConstsInstructions.INSTRUCTION_FIVE, -0.000239121, 0.000231216, 0.00015229);
		CreateDoubleNorm(Consts.ConstsParamNames.Gesture.GESTURE_AVG_ACCELERATION, ConstsInstructions.INSTRUCTION_TWO, -0.000239121, 0.000231216, 0.00015229);
		CreateDoubleNorm(Consts.ConstsParamNames.Gesture.GESTURE_AVG_ACCELERATION, ConstsInstructions.INSTRUCTION_HEART, -0.000126988, 0.000137389, 8.52226E-05);
		CreateDoubleNorm(Consts.ConstsParamNames.Gesture.GESTURE_AVG_ACCELERATION, ConstsInstructions.INSTRUCTION_LINES, -0.000549455, 0.000698421, 0.000374456);
		CreateDoubleNorm(Consts.ConstsParamNames.Gesture.GESTURE_AVG_ACCELERATION, ConstsInstructions.INSTRUCTION_LETTER_R, -0.000171851, 0.000157023, 0.000121727);
		CreateDoubleNorm(Consts.ConstsParamNames.Gesture.GESTURE_AVG_ACCELERATION, ConstsInstructions.INSTRUCTION_LETTER_B, -0.000171851, 0.000157023, 0.000121727);
		CreateDoubleNorm(Consts.ConstsParamNames.Gesture.GESTURE_AVG_ACCELERATION, ConstsInstructions.INSTRUCTION_LETTER_G, -0.000171851, 0.000157023, 0.000121727);
		CreateDoubleNorm(Consts.ConstsParamNames.Gesture.GESTURE_AVG_ACCELERATION, ConstsInstructions.INSTRUCTION_TRIANGLE, -0.000110975, 0.000132399, 7.20011E-05);
		
		CreateDoubleNorm(Consts.ConstsParamNames.Gesture.GESTURE_MAX_ACCELERATION, ConstsInstructions.INSTRUCTION_LETTER_A, 0.006225367, 0.003184481, 0.002820814);
		CreateDoubleNorm(Consts.ConstsParamNames.Gesture.GESTURE_MAX_ACCELERATION, ConstsInstructions.INSTRUCTION_EIGHT, 0.005192124, 0.002563798, 0.002568298);
		CreateDoubleNorm(Consts.ConstsParamNames.Gesture.GESTURE_MAX_ACCELERATION, ConstsInstructions.INSTRUCTION_FIVE, 0.005037168, 0.002584385, 0.002492732);
		CreateDoubleNorm(Consts.ConstsParamNames.Gesture.GESTURE_MAX_ACCELERATION, ConstsInstructions.INSTRUCTION_TWO, 0.005037168, 0.002584385, 0.002492732);
		CreateDoubleNorm(Consts.ConstsParamNames.Gesture.GESTURE_MAX_ACCELERATION, ConstsInstructions.INSTRUCTION_HEART, 0.004820235, 0.002653024, 0.0024689);
		CreateDoubleNorm(Consts.ConstsParamNames.Gesture.GESTURE_MAX_ACCELERATION, ConstsInstructions.INSTRUCTION_LINES, 0.00621739, 0.004160004, 0.003346521);
		CreateDoubleNorm(Consts.ConstsParamNames.Gesture.GESTURE_MAX_ACCELERATION, ConstsInstructions.INSTRUCTION_LETTER_R, 0.005640622, 0.002964638, 0.00270592);
		CreateDoubleNorm(Consts.ConstsParamNames.Gesture.GESTURE_MAX_ACCELERATION, ConstsInstructions.INSTRUCTION_LETTER_B, 0.005640622, 0.002964638, 0.00270592);
		CreateDoubleNorm(Consts.ConstsParamNames.Gesture.GESTURE_MAX_ACCELERATION, ConstsInstructions.INSTRUCTION_LETTER_G, 0.005640622, 0.002964638, 0.00270592);
		CreateDoubleNorm(Consts.ConstsParamNames.Gesture.GESTURE_MAX_ACCELERATION, ConstsInstructions.INSTRUCTION_TRIANGLE, 0.004610704, 0.00206664, 0.001629684);
		
		CreateDoubleNorm(Consts.ConstsParamNames.Gesture.GESTURE_MID_OF_FIRST_STROKE_VELOCITY, ConstsInstructions.INSTRUCTION_LETTER_A, 0.223311806, 0.095509524, 0.058194165);
		CreateDoubleNorm(Consts.ConstsParamNames.Gesture.GESTURE_MID_OF_FIRST_STROKE_VELOCITY, ConstsInstructions.INSTRUCTION_EIGHT, 0.138830098, 0.048237177, 0.044027054);
		CreateDoubleNorm(Consts.ConstsParamNames.Gesture.GESTURE_MID_OF_FIRST_STROKE_VELOCITY, ConstsInstructions.INSTRUCTION_FIVE, 0.113828654, 0.060359869, 0.043047535);
		CreateDoubleNorm(Consts.ConstsParamNames.Gesture.GESTURE_MID_OF_FIRST_STROKE_VELOCITY, ConstsInstructions.INSTRUCTION_TWO, 0.113828654, 0.060359869, 0.043047535);
		CreateDoubleNorm(Consts.ConstsParamNames.Gesture.GESTURE_MID_OF_FIRST_STROKE_VELOCITY, ConstsInstructions.INSTRUCTION_HEART, 0.133018703, 0.05229818, 0.036534609);
		CreateDoubleNorm(Consts.ConstsParamNames.Gesture.GESTURE_MID_OF_FIRST_STROKE_VELOCITY, ConstsInstructions.INSTRUCTION_LINES, 0.238387184, 0.07982762, 0.049035373);
		CreateDoubleNorm(Consts.ConstsParamNames.Gesture.GESTURE_MID_OF_FIRST_STROKE_VELOCITY, ConstsInstructions.INSTRUCTION_LETTER_R, 0.219732345, 0.072781736, 0.044184883);
		CreateDoubleNorm(Consts.ConstsParamNames.Gesture.GESTURE_MID_OF_FIRST_STROKE_VELOCITY, ConstsInstructions.INSTRUCTION_LETTER_B, 0.219732345, 0.072781736, 0.044184883);
		CreateDoubleNorm(Consts.ConstsParamNames.Gesture.GESTURE_MID_OF_FIRST_STROKE_VELOCITY, ConstsInstructions.INSTRUCTION_LETTER_G, 0.219732345, 0.072781736, 0.044184883);
		CreateDoubleNorm(Consts.ConstsParamNames.Gesture.GESTURE_MID_OF_FIRST_STROKE_VELOCITY, ConstsInstructions.INSTRUCTION_TRIANGLE, 0.198715562, 0.069264186, 0.069607794);

		//:TODO CALCULATE NORMS FOR PARAMETER
		CreateDoubleNorm(Consts.ConstsParamNames.Gesture.GESTURE_MID_OF_FIRST_STROKE_ANGLE, ConstsInstructions.INSTRUCTION_LETTER_A, 0.069936322, 1.286465441, 0.496739832);
		CreateDoubleNorm(Consts.ConstsParamNames.Gesture.GESTURE_MID_OF_FIRST_STROKE_ANGLE, ConstsInstructions.INSTRUCTION_EIGHT, 0.849805491, 1.129770489, 0.897860485);
		CreateDoubleNorm(Consts.ConstsParamNames.Gesture.GESTURE_MID_OF_FIRST_STROKE_ANGLE, ConstsInstructions.INSTRUCTION_FIVE, 1.320432208, 0.96737028, 0.875625277);
		CreateDoubleNorm(Consts.ConstsParamNames.Gesture.GESTURE_MID_OF_FIRST_STROKE_ANGLE, ConstsInstructions.INSTRUCTION_TWO, 1.320432208, 0.96737028, 0.875625277);
		CreateDoubleNorm(Consts.ConstsParamNames.Gesture.GESTURE_MID_OF_FIRST_STROKE_ANGLE, ConstsInstructions.INSTRUCTION_HEART, 0.247448488, 1.276830957, 0.541952414);
		CreateDoubleNorm(Consts.ConstsParamNames.Gesture.GESTURE_MID_OF_FIRST_STROKE_ANGLE, ConstsInstructions.INSTRUCTION_LINES, 0.010430596, 0.563211949, 0.52915564);
		CreateDoubleNorm(Consts.ConstsParamNames.Gesture.GESTURE_MID_OF_FIRST_STROKE_ANGLE, ConstsInstructions.INSTRUCTION_LETTER_R, 1.187796958, 0.728216116, 0.355427966);
		CreateDoubleNorm(Consts.ConstsParamNames.Gesture.GESTURE_MID_OF_FIRST_STROKE_ANGLE, ConstsInstructions.INSTRUCTION_LETTER_B, 1.187796958, 0.728216116, 0.355427966);
		CreateDoubleNorm(Consts.ConstsParamNames.Gesture.GESTURE_MID_OF_FIRST_STROKE_ANGLE, ConstsInstructions.INSTRUCTION_LETTER_G, 1.187796958, 0.728216116, 0.355427966);
		CreateDoubleNorm(Consts.ConstsParamNames.Gesture.GESTURE_MID_OF_FIRST_STROKE_ANGLE, ConstsInstructions.INSTRUCTION_TRIANGLE, 0.50787113, 1.086752303, 0.855734746);

		CreateDoubleNorm(Consts.ConstsParamNames.Gesture.GESTURE_AVG_START_DIRECTION, ConstsInstructions.INSTRUCTION_LETTER_A, 3.9216, 1.469, 0.3);
		CreateDoubleNorm(Consts.ConstsParamNames.Gesture.GESTURE_AVG_START_DIRECTION, ConstsInstructions.INSTRUCTION_EIGHT, 3.8615, 1.7749, 0.3);
		CreateDoubleNorm(Consts.ConstsParamNames.Gesture.GESTURE_AVG_START_DIRECTION, ConstsInstructions.INSTRUCTION_FIVE, 2.794, 1.4553, 0.3);
		CreateDoubleNorm(Consts.ConstsParamNames.Gesture.GESTURE_AVG_START_DIRECTION, ConstsInstructions.INSTRUCTION_TWO, 2.794, 1.4553, 0.3);
		CreateDoubleNorm(Consts.ConstsParamNames.Gesture.GESTURE_AVG_START_DIRECTION, ConstsInstructions.INSTRUCTION_HEART, 4.628, 0.7115, 0.3);
		CreateDoubleNorm(Consts.ConstsParamNames.Gesture.GESTURE_AVG_START_DIRECTION, ConstsInstructions.INSTRUCTION_LINES, 3.7860, 2.6757, 0.3);
		CreateDoubleNorm(Consts.ConstsParamNames.Gesture.GESTURE_AVG_START_DIRECTION, ConstsInstructions.INSTRUCTION_LETTER_R, 2.1949, 1.2685, 0.3);		
		CreateDoubleNorm(Consts.ConstsParamNames.Gesture.GESTURE_AVG_START_DIRECTION, ConstsInstructions.INSTRUCTION_LETTER_B, 2.1949, 1.2685, 0.3);
		CreateDoubleNorm(Consts.ConstsParamNames.Gesture.GESTURE_AVG_START_DIRECTION, ConstsInstructions.INSTRUCTION_LETTER_G, 2.1949, 1.2685, 0.3);
		CreateDoubleNorm(Consts.ConstsParamNames.Gesture.GESTURE_AVG_START_DIRECTION, ConstsInstructions.INSTRUCTION_TRIANGLE, 113, 38, 0.3);

		CreateDoubleNorm(Consts.ConstsParamNames.Gesture.GESTURE_AVG_MAX_DIRECTION, ConstsInstructions.INSTRUCTION_LETTER_A, 3.9216, 1.469, 0.3);
		CreateDoubleNorm(Consts.ConstsParamNames.Gesture.GESTURE_AVG_MAX_DIRECTION, ConstsInstructions.INSTRUCTION_EIGHT, 3.8615, 1.7749, 0.3);
		CreateDoubleNorm(Consts.ConstsParamNames.Gesture.GESTURE_AVG_MAX_DIRECTION, ConstsInstructions.INSTRUCTION_FIVE, 2.794, 1.4553, 0.3);
		CreateDoubleNorm(Consts.ConstsParamNames.Gesture.GESTURE_AVG_MAX_DIRECTION, ConstsInstructions.INSTRUCTION_TWO, 2.794, 1.4553, 0.3);
		CreateDoubleNorm(Consts.ConstsParamNames.Gesture.GESTURE_AVG_MAX_DIRECTION, ConstsInstructions.INSTRUCTION_HEART, 4.628, 0.7115, 0.3);
		CreateDoubleNorm(Consts.ConstsParamNames.Gesture.GESTURE_AVG_MAX_DIRECTION, ConstsInstructions.INSTRUCTION_LINES, 3.7860, 2.6757, 0.3);
		CreateDoubleNorm(Consts.ConstsParamNames.Gesture.GESTURE_AVG_MAX_DIRECTION, ConstsInstructions.INSTRUCTION_LETTER_R, 2.1949, 1.2685, 0.3);		
		CreateDoubleNorm(Consts.ConstsParamNames.Gesture.GESTURE_AVG_MAX_DIRECTION, ConstsInstructions.INSTRUCTION_LETTER_B, 2.1949, 1.2685, 0.3);
		CreateDoubleNorm(Consts.ConstsParamNames.Gesture.GESTURE_AVG_MAX_DIRECTION, ConstsInstructions.INSTRUCTION_LETTER_G, 2.1949, 1.2685, 0.3);
		CreateDoubleNorm(Consts.ConstsParamNames.Gesture.GESTURE_AVG_MAX_DIRECTION, ConstsInstructions.INSTRUCTION_TRIANGLE, 113, 38, 0.3);

		CreateDoubleNorm(Consts.ConstsParamNames.Gesture.GESTURE_AVG_END_DIRECTION, ConstsInstructions.INSTRUCTION_LETTER_A, 3.9216, 1.469, 0.3);
		CreateDoubleNorm(Consts.ConstsParamNames.Gesture.GESTURE_AVG_END_DIRECTION, ConstsInstructions.INSTRUCTION_EIGHT, 3.8615, 1.7749, 0.3);
		CreateDoubleNorm(Consts.ConstsParamNames.Gesture.GESTURE_AVG_END_DIRECTION, ConstsInstructions.INSTRUCTION_FIVE, 2.794, 1.4553, 0.3);
		CreateDoubleNorm(Consts.ConstsParamNames.Gesture.GESTURE_AVG_END_DIRECTION, ConstsInstructions.INSTRUCTION_TWO, 2.794, 1.4553, 0.3);
		CreateDoubleNorm(Consts.ConstsParamNames.Gesture.GESTURE_AVG_END_DIRECTION, ConstsInstructions.INSTRUCTION_HEART, 4.628, 0.7115, 0.3);
		CreateDoubleNorm(Consts.ConstsParamNames.Gesture.GESTURE_AVG_END_DIRECTION, ConstsInstructions.INSTRUCTION_LINES, 3.7860, 2.6757, 0.3);
		CreateDoubleNorm(Consts.ConstsParamNames.Gesture.GESTURE_AVG_END_DIRECTION, ConstsInstructions.INSTRUCTION_LETTER_R, 2.1949, 1.2685, 0.3);		
		CreateDoubleNorm(Consts.ConstsParamNames.Gesture.GESTURE_AVG_END_DIRECTION, ConstsInstructions.INSTRUCTION_LETTER_B, 2.1949, 1.2685, 0.3);
		CreateDoubleNorm(Consts.ConstsParamNames.Gesture.GESTURE_AVG_END_DIRECTION, ConstsInstructions.INSTRUCTION_LETTER_G, 2.1949, 1.2685, 0.3);
		CreateDoubleNorm(Consts.ConstsParamNames.Gesture.GESTURE_AVG_END_DIRECTION, ConstsInstructions.INSTRUCTION_TRIANGLE, 113, 38, 0.3);
		
		CreateDoubleNorm(Consts.ConstsParamNames.Gesture.GESTURE_MAX_VELOCITY_DIRECTION, ConstsInstructions.INSTRUCTION_LETTER_A, 3.9216, 1.469, 0.3);
		CreateDoubleNorm(Consts.ConstsParamNames.Gesture.GESTURE_MAX_VELOCITY_DIRECTION, ConstsInstructions.INSTRUCTION_EIGHT, 3.8615, 1.7749, 0.3);
		CreateDoubleNorm(Consts.ConstsParamNames.Gesture.GESTURE_MAX_VELOCITY_DIRECTION, ConstsInstructions.INSTRUCTION_FIVE, 2.794, 1.4553, 0.3);
		CreateDoubleNorm(Consts.ConstsParamNames.Gesture.GESTURE_MAX_VELOCITY_DIRECTION, ConstsInstructions.INSTRUCTION_TWO, 2.794, 1.4553, 0.3);
		CreateDoubleNorm(Consts.ConstsParamNames.Gesture.GESTURE_MAX_VELOCITY_DIRECTION, ConstsInstructions.INSTRUCTION_HEART, 4.628, 0.7115, 0.3);
		CreateDoubleNorm(Consts.ConstsParamNames.Gesture.GESTURE_MAX_VELOCITY_DIRECTION, ConstsInstructions.INSTRUCTION_LINES, 3.7860, 2.6757, 0.3);
		CreateDoubleNorm(Consts.ConstsParamNames.Gesture.GESTURE_MAX_VELOCITY_DIRECTION, ConstsInstructions.INSTRUCTION_LETTER_R, 2.1949, 1.2685, 0.3);		
		CreateDoubleNorm(Consts.ConstsParamNames.Gesture.GESTURE_MAX_VELOCITY_DIRECTION, ConstsInstructions.INSTRUCTION_LETTER_B, 2.1949, 1.2685, 0.3);
		CreateDoubleNorm(Consts.ConstsParamNames.Gesture.GESTURE_MAX_VELOCITY_DIRECTION, ConstsInstructions.INSTRUCTION_LETTER_G, 2.1949, 1.2685, 0.3);
		CreateDoubleNorm(Consts.ConstsParamNames.Gesture.GESTURE_MAX_VELOCITY_DIRECTION, ConstsInstructions.INSTRUCTION_TRIANGLE, 113, 38, 0.3);

		CreateDoubleNorm(Consts.ConstsParamNames.Gesture.GESTURE_NUM_EVENTS, ConstsInstructions.INSTRUCTION_LETTER_A, 27.33928571, 9.852202522, 4.291430244);
		CreateDoubleNorm(Consts.ConstsParamNames.Gesture.GESTURE_NUM_EVENTS, ConstsInstructions.INSTRUCTION_EIGHT, 50.55912698, 13.74751223, 6.803068544);
		CreateDoubleNorm(Consts.ConstsParamNames.Gesture.GESTURE_NUM_EVENTS, ConstsInstructions.INSTRUCTION_FIVE, 39.70238095, 18.1356275, 6.499171396);
		CreateDoubleNorm(Consts.ConstsParamNames.Gesture.GESTURE_NUM_EVENTS, ConstsInstructions.INSTRUCTION_TWO, 39.70238095, 18.1356275, 6.499171396);
		CreateDoubleNorm(Consts.ConstsParamNames.Gesture.GESTURE_NUM_EVENTS, ConstsInstructions.INSTRUCTION_HEART, 51.2152439, 27.87226673, 7.900482147);
		CreateDoubleNorm(Consts.ConstsParamNames.Gesture.GESTURE_NUM_EVENTS, ConstsInstructions.INSTRUCTION_LINES, 16.94861111, 6.720243155, 3.987731476);
		CreateDoubleNorm(Consts.ConstsParamNames.Gesture.GESTURE_NUM_EVENTS, ConstsInstructions.INSTRUCTION_LETTER_R, 38.61111111, 18.84806747, 8.944797954);
		CreateDoubleNorm(Consts.ConstsParamNames.Gesture.GESTURE_NUM_EVENTS, ConstsInstructions.INSTRUCTION_LETTER_B, 38.61111111, 18.84806747, 8.944797954);
		CreateDoubleNorm(Consts.ConstsParamNames.Gesture.GESTURE_NUM_EVENTS, ConstsInstructions.INSTRUCTION_LETTER_G, 38.61111111, 18.84806747, 8.944797954);
		CreateDoubleNorm(Consts.ConstsParamNames.Gesture.GESTURE_NUM_EVENTS, ConstsInstructions.INSTRUCTION_TRIANGLE, 46.33769841, 17.80907353, 6.754986845);

		CreateDoubleNorm(Consts.ConstsParamNames.Gesture.GESTURE_VELOCITY_PEAK, ConstsInstructions.INSTRUCTION_LETTER_A, 0.260470307, 0.114393531, 0.065183297);
		CreateDoubleNorm(Consts.ConstsParamNames.Gesture.GESTURE_VELOCITY_PEAK, ConstsInstructions.INSTRUCTION_EIGHT, 0.159559951, 0.054394531, 0.07351092);
		CreateDoubleNorm(Consts.ConstsParamNames.Gesture.GESTURE_VELOCITY_PEAK, ConstsInstructions.INSTRUCTION_FIVE, 0.188369337, 0.108384909, 0.077469321);
		CreateDoubleNorm(Consts.ConstsParamNames.Gesture.GESTURE_VELOCITY_PEAK, ConstsInstructions.INSTRUCTION_TWO, 0.188369337, 0.108384909, 0.077469321);
		CreateDoubleNorm(Consts.ConstsParamNames.Gesture.GESTURE_VELOCITY_PEAK, ConstsInstructions.INSTRUCTION_HEART, 0.23473964, 0.125638252, 0.10411778);
		CreateDoubleNorm(Consts.ConstsParamNames.Gesture.GESTURE_VELOCITY_PEAK, ConstsInstructions.INSTRUCTION_LINES, 0.434782911, 0.094310579, 0.095517156);
		CreateDoubleNorm(Consts.ConstsParamNames.Gesture.GESTURE_VELOCITY_PEAK, ConstsInstructions.INSTRUCTION_LETTER_R, 0.220989016, 0.155955324, 0.073158533);
		CreateDoubleNorm(Consts.ConstsParamNames.Gesture.GESTURE_VELOCITY_PEAK, ConstsInstructions.INSTRUCTION_LETTER_B, 0.220989016, 0.155955324, 0.073158533);
		CreateDoubleNorm(Consts.ConstsParamNames.Gesture.GESTURE_VELOCITY_PEAK, ConstsInstructions.INSTRUCTION_LETTER_G, 0.220989016, 0.155955324, 0.073158533);
		CreateDoubleNorm(Consts.ConstsParamNames.Gesture.GESTURE_VELOCITY_PEAK, ConstsInstructions.INSTRUCTION_TRIANGLE, 0.165724714, 0.105239609, 0.042487757);

		CreateDoubleNorm(Consts.ConstsParamNames.Gesture.GESTURE_VELOCITY_PEAK_INTERVAL_PERCENTAGE, ConstsInstructions.INSTRUCTION_LETTER_A, 0.312891373, 0.109057336, 0.051893619);
		CreateDoubleNorm(Consts.ConstsParamNames.Gesture.GESTURE_VELOCITY_PEAK_INTERVAL_PERCENTAGE, ConstsInstructions.INSTRUCTION_EIGHT, 0.110351035, 0.037386959, 0.050522185);
		CreateDoubleNorm(Consts.ConstsParamNames.Gesture.GESTURE_VELOCITY_PEAK_INTERVAL_PERCENTAGE, ConstsInstructions.INSTRUCTION_FIVE, 0.169486304, 0.129049415, 0.049815106);
		CreateDoubleNorm(Consts.ConstsParamNames.Gesture.GESTURE_VELOCITY_PEAK_INTERVAL_PERCENTAGE, ConstsInstructions.INSTRUCTION_TWO, 0.169486304, 0.129049415, 0.049815106);
		CreateDoubleNorm(Consts.ConstsParamNames.Gesture.GESTURE_VELOCITY_PEAK_INTERVAL_PERCENTAGE, ConstsInstructions.INSTRUCTION_HEART, 0.162615894, 0.063082218, 0.085037212);
		CreateDoubleNorm(Consts.ConstsParamNames.Gesture.GESTURE_VELOCITY_PEAK_INTERVAL_PERCENTAGE, ConstsInstructions.INSTRUCTION_LINES, 0.490849939, 0.042390025, 0.072056754);
		CreateDoubleNorm(Consts.ConstsParamNames.Gesture.GESTURE_VELOCITY_PEAK_INTERVAL_PERCENTAGE, ConstsInstructions.INSTRUCTION_LETTER_R, 0.277981998, 0.156136672, 0.063758831);
		CreateDoubleNorm(Consts.ConstsParamNames.Gesture.GESTURE_VELOCITY_PEAK_INTERVAL_PERCENTAGE, ConstsInstructions.INSTRUCTION_LETTER_B, 0.277981998, 0.156136672, 0.063758831);
		CreateDoubleNorm(Consts.ConstsParamNames.Gesture.GESTURE_VELOCITY_PEAK_INTERVAL_PERCENTAGE, ConstsInstructions.INSTRUCTION_LETTER_G, 0.277981998, 0.156136672, 0.063758831);
		CreateDoubleNorm(Consts.ConstsParamNames.Gesture.GESTURE_VELOCITY_PEAK_INTERVAL_PERCENTAGE, ConstsInstructions.INSTRUCTION_TRIANGLE, 0.220409561, 0.105269601, 0.03427187);

		CreateDoubleNorm(Consts.ConstsParamNames.Gesture.GESTURE_ACCUMULATED_LENGTH_R2, ConstsInstructions.INSTRUCTION_LETTER_A, 0.977750624, 0.01498735, 0.013011328);
		CreateDoubleNorm(Consts.ConstsParamNames.Gesture.GESTURE_ACCUMULATED_LENGTH_R2, ConstsInstructions.INSTRUCTION_EIGHT, 0.98916811, 0.011639231, 0.007473029);
		CreateDoubleNorm(Consts.ConstsParamNames.Gesture.GESTURE_ACCUMULATED_LENGTH_R2, ConstsInstructions.INSTRUCTION_FIVE, 0.976305383, 0.014129027, 0.013124739);
		CreateDoubleNorm(Consts.ConstsParamNames.Gesture.GESTURE_ACCUMULATED_LENGTH_R2, ConstsInstructions.INSTRUCTION_TWO, 0.976305383, 0.014129027, 0.013124739);
		CreateDoubleNorm(Consts.ConstsParamNames.Gesture.GESTURE_ACCUMULATED_LENGTH_R2, ConstsInstructions.INSTRUCTION_HEART, 0.984681632, 0.007050072, 0.005934556);
		CreateDoubleNorm(Consts.ConstsParamNames.Gesture.GESTURE_ACCUMULATED_LENGTH_R2, ConstsInstructions.INSTRUCTION_LINES, 0.977305228, 0.012574781, 0.012041201);
		CreateDoubleNorm(Consts.ConstsParamNames.Gesture.GESTURE_ACCUMULATED_LENGTH_R2, ConstsInstructions.INSTRUCTION_LETTER_R, 0.974674672, 0.017346707, 0.016010183);
		CreateDoubleNorm(Consts.ConstsParamNames.Gesture.GESTURE_ACCUMULATED_LENGTH_R2, ConstsInstructions.INSTRUCTION_LETTER_B, 0.974674672, 0.017346707, 0.016010183);
		CreateDoubleNorm(Consts.ConstsParamNames.Gesture.GESTURE_ACCUMULATED_LENGTH_R2, ConstsInstructions.INSTRUCTION_LETTER_G, 0.974674672, 0.017346707, 0.016010183);
		CreateDoubleNorm(Consts.ConstsParamNames.Gesture.GESTURE_ACCUMULATED_LENGTH_R2, ConstsInstructions.INSTRUCTION_TRIANGLE, 0.978605907, 0.00882214, 0.007890556);

		CreateDoubleNorm(Consts.ConstsParamNames.Gesture.GESTURE_ACCUMULATED_LENGTH_SLOPE, ConstsInstructions.INSTRUCTION_LETTER_A, 0.155552988, 0.048297752, 0.023977756);
		CreateDoubleNorm(Consts.ConstsParamNames.Gesture.GESTURE_ACCUMULATED_LENGTH_SLOPE, ConstsInstructions.INSTRUCTION_EIGHT, 0.145037919, 0.040947513, 0.020217015);
		CreateDoubleNorm(Consts.ConstsParamNames.Gesture.GESTURE_ACCUMULATED_LENGTH_SLOPE, ConstsInstructions.INSTRUCTION_FIVE, 0.115587677, 0.033633353, 0.018624342);
		CreateDoubleNorm(Consts.ConstsParamNames.Gesture.GESTURE_ACCUMULATED_LENGTH_SLOPE, ConstsInstructions.INSTRUCTION_TWO, 0.115587677, 0.033633353, 0.018624342);
		CreateDoubleNorm(Consts.ConstsParamNames.Gesture.GESTURE_ACCUMULATED_LENGTH_SLOPE, ConstsInstructions.INSTRUCTION_HEART, 0.122852993, 0.043503875, 0.018543088);
		CreateDoubleNorm(Consts.ConstsParamNames.Gesture.GESTURE_ACCUMULATED_LENGTH_SLOPE, ConstsInstructions.INSTRUCTION_LINES, 0.189659763, 0.076593824, 0.039628306);
		CreateDoubleNorm(Consts.ConstsParamNames.Gesture.GESTURE_ACCUMULATED_LENGTH_SLOPE, ConstsInstructions.INSTRUCTION_LETTER_R, 0.124763065, 0.038652362, 0.017365491);
		CreateDoubleNorm(Consts.ConstsParamNames.Gesture.GESTURE_ACCUMULATED_LENGTH_SLOPE, ConstsInstructions.INSTRUCTION_LETTER_B, 0.124763065, 0.038652362, 0.017365491);
		CreateDoubleNorm(Consts.ConstsParamNames.Gesture.GESTURE_ACCUMULATED_LENGTH_SLOPE, ConstsInstructions.INSTRUCTION_LETTER_G, 0.124763065, 0.038652362, 0.017365491);
		CreateDoubleNorm(Consts.ConstsParamNames.Gesture.GESTURE_ACCUMULATED_LENGTH_SLOPE, ConstsInstructions.INSTRUCTION_TRIANGLE, 0.126941926, 0.040736681, 0.017327171);		
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
