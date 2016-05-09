package Logic.Comparison.Stats.Interfaces;

public interface IStatEngine {
	public double CompareStrokeDoubleValues(String instruction, String paramName, int strokeIdx, double value1, double value2);
	public double CompareGestureDoubleValues(String instruction, String paramName, double value1, double value2);
	public void AddStrokeValue(String instruction, String paramName, int strokeIdx, double value);
	public void AddGestureValue(String instruction, String paramName, double value);
}
