package Data.Comparison.Abstract;

import java.awt.image.RescaleOp;

import Data.Comparison.Interfaces.ICompareResult;

public abstract class CompareResultAbstract implements ICompareResult {
	public String Name;
	public double Value;
	public double OriginalValue;
	public double Weight;
	public double Mean;
	public double StandardDev;
		
	@Override
	public String GetName() {
		return Name;
	}
	
	@Override
	public double GetValue() { 
		return Value;
	}
	
	public double GetOriginalValue() { 
		return Value;
	}
	
	@Override
	public double GetWeight() { 
		return Weight;
	}
	
	public double GetMean() { 
		return Mean;
	}
	
	public double GetSD() { 
		return StandardDev;
	}
	
	@Override
	public void SetName(String name) {
		Name = name;
	}

	@Override
	public void SetWeight(double weight) {
		weight = Weight;
	}

	@Override
	public void SetValue(double value) {
		Value = value;
	}
	
	public void SetOriginalValue(double originalValue) {
		OriginalValue = originalValue;
	}
	
	public void SetMean(double mean) {
		Mean = mean;
	}
	
	public void SetStandardDev(double standardDev) {
		StandardDev = standardDev;
	}
}
