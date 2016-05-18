package Data.Comparison.Interfaces;

public interface ICompareResult {
	public String GetName();
	public double GetWeight();
	public double GetValue();
	public double GetOriginalValue();
	
	public void SetName(String name);
	public void SetWeight(double weight);
	public void SetValue(double value);
	public void SetOriginalValue(double originalValue);
}
