package Logic.Comparison.Stats.Interfaces;

public interface IStatEngine {
	public double CompareStrokeDoubleValues(String instruction, String paramName, int strokeIdx, double authValue);
	public double CompareGestureDoubleValues(String instruction, String paramName, double authValue);
	public void AddStrokeValue(String instruction, String paramName, int strokeIdx, double value);
	public void AddGestureValue(String instruction, String paramName, double value);
}
