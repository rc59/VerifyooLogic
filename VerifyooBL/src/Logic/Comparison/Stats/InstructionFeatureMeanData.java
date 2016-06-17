package Logic.Comparison.Stats;

import java.util.HashMap;

public class InstructionFeatureMeanData {
	protected String mInstruction;
	private HashMap<String, FeatureMeanData> mHashFeatureMeans;
	
	public InstructionFeatureMeanData(String instruction)
	{
		mInstruction = instruction;
		mHashFeatureMeans = new HashMap<>();
	}
	
	public void AddValue(String paramName, double value)
	{
		FeatureMeanData tempMeanData;
		
		if(mHashFeatureMeans.containsKey(paramName)) {
			tempMeanData = mHashFeatureMeans.get(paramName);
		}
		else {
			tempMeanData = new FeatureMeanData(paramName, mInstruction);
			mHashFeatureMeans.put(paramName, tempMeanData);
		}
		
		tempMeanData.AddValue(value);
	}
	
	public double GetMean(String paramName){
		return mHashFeatureMeans.get(paramName).GetMean();
	}
}
