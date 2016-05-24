package Logic.Comparison.Stats.Interfaces;

public interface IFeatureMeanData {
	public void AddValue(double value);
	public double GetMean();
	public double GetInternalSd();
}
