package Logic.Comparison.Stats.Abstract;

import java.util.ArrayList;

import Logic.Comparison.Stats.Interfaces.IFeatureMeanData;
import Logic.Comparison.Stats.Norms.NormMgr;
import Logic.Comparison.Stats.Norms.Interfaces.INormData;
import Logic.Comparison.Stats.Norms.Interfaces.INormMgr;
import Logic.Utils.UtilsAccumulator;

public abstract class FeatureMeanDataAbstract implements IFeatureMeanData {
	protected String mName;
	protected String mInstruction;
	protected double mSumValues;
	protected double mCount;
	
	protected UtilsAccumulator mUtilsAccumulator;

	public FeatureMeanDataAbstract(String name, String instruction)
	{
		mUtilsAccumulator = new UtilsAccumulator();
		
		mName = name;
		mInstruction = instruction;
		mSumValues = 0;
		mCount = 0;
	}	
	
	public void AddValue(double currentValue)
	{
		mCount++;
		mSumValues += currentValue;
		
		mUtilsAccumulator.AddDataValue(currentValue);
	}
	
	public double GetPopulationZScore() {
		double popZScore;
				
		INormData normObj = NormMgr.GetInstance().GetNormDataByParamName(mName, mInstruction);
		
		double populationMean = normObj.GetMean();
		double populationSd = normObj.GetStandardDev();
		
		popZScore = (GetMean() - populationMean) / populationSd;
		
		return popZScore;
	}
	
	public String GetParamName() {
		return mName;
	}
	
	public String GetInstruction() {
		return mInstruction;
	}
	
	public UtilsAccumulator GetUtilsAccumulator() {
		return mUtilsAccumulator;
	}
}
