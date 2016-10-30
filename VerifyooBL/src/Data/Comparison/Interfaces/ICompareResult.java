package Data.Comparison.Interfaces;

public interface ICompareResult {
	public String GetName();
	public double GetWeight();	
	public double GetValue();
	public double GetOriginalValue();
	public double GetMean();
	public double GetPopMean();
	public double GetSD();
	public double GetInternalSD();
	public double GetInternalSdUserOnly();
	public double GetZScore();
	public double GetTemplateZScore();
	
	public void SetName(String name);	
	public void SetWeight(double weight);
	public void SetValue(double value);
	public void SetOriginalValue(double originalValue);
	public void SetMean(double mean);
	public void SetPopMean(double popMean);
	public void SetStandardDev(double standardDev);
	public void SetInternalStandardDev(double standardDev);
	public void SetInternalStandardDevUserOnly(double standardDevUserOnly);
}
