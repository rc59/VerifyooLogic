package Data.Comparison.Abstract;

import java.awt.image.RescaleOp;

import Data.Comparison.Interfaces.ICompareResult;

public abstract class CompareResultAbstract implements ICompareResult {
	public String Name;
	public double Value;
	public double OriginalValue;
	public double Weight;
	public double Mean;
	public double PopMean;
	public double StandardDev;
	public double InternalStandardDev;
	public double InternalStandardDevUserOnly;
		
	@Override
	public String GetName() {
		return Name;
	}
	
	@Override
	public double GetValue() { 
		return Value;
	}
	
	public double GetOriginalValue() { 
		return OriginalValue;
	}
	
	@Override
	public double GetWeight() { 
		if(Double.isNaN(Weight)) {
			Weight = 0.9;
		}
		return Weight;
	}
	
	public double GetMean() { 
		return Mean;
	}
	
	public double GetPopMean() { 
		return PopMean;
	}
	
	public double GetSD() { 
		return StandardDev;
	}
	
	public double GetInternalSD() { 
		return InternalStandardDev;
	}
	
	public double GetInternalSdUserOnly() { 
		return InternalStandardDevUserOnly;
	}
	
	public double GetZScore() { 
		return ((OriginalValue - PopMean) / StandardDev);
	}
	
	public double GetTemplateZScore() { 
		return ((Mean - PopMean) / StandardDev);
	}
	
	@Override
	public void SetName(String name) {
		Name = name;
	}

	@Override
	public void SetWeight(double weight) {
		Weight = weight;
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
	
	public void SetPopMean(double popMean) {
		PopMean = popMean;
	}
	
	public void SetStandardDev(double standardDev) {
		StandardDev = standardDev;
	}
	
	public void SetInternalStandardDev(double internalStandardDev) {
		InternalStandardDev = internalStandardDev;
	}
	
	public void SetInternalStandardDevUserOnly(double internalStandardDevUserOnly) {
		InternalStandardDevUserOnly = internalStandardDevUserOnly;
	}
}
