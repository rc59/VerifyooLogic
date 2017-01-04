package Logic.Comparison.Stats.Data.Interface;

public interface IStatEngineResult {
	public double GetScore();
	public double GetZScore();
	public double GetWeight();
	public void SetBoundary(double boundary);
	public double GetBoundary();
//	public boolean IsValid();
}
