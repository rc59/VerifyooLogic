package Logic.Comparison.Stats.Interfaces;

public interface IFeatureMeanData {
	public void AddValue(double value);
	public double GetMean();
	public double GetInternalSd();
	public double GetPopulationZScore();
	public String GetParamName();
	public String GetInstruction();
}
