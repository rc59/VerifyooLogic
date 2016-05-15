package Logic.Calc;

public class Utils {
	private static Utils mInstance = null;
	
	protected static UtilsComparison mUtilsComparison;
	protected static UtilsMath mUtilsMath;
	protected static UtilsSpatialSampling mUtilsSpatialSampling;
	protected static UtilsStat mUtilsStat;
	protected static UtilsVectors mUtilsVectors;
	
	public static Utils GetInstance() {
		if(mInstance == null) {
			mInstance = new Utils();
			
			mUtilsComparison = new UtilsComparison();
			mUtilsMath = new UtilsMath();
			mUtilsSpatialSampling = new UtilsSpatialSampling();
			mUtilsStat = new UtilsStat();
			mUtilsVectors = new UtilsVectors();			
		}
		return mInstance;
	}
	
	public UtilsComparison GetUtilsComparison() {
		return mUtilsComparison;
	}
	public UtilsMath GetUtilsMath() {
		return mUtilsMath;
	}
	public UtilsSpatialSampling GetUtilsSpatialSampling() {
		return mUtilsSpatialSampling;
	}
	public UtilsStat GetUtilsStat() {
		return mUtilsStat;
	}
	public UtilsVectors GetUtilsVectors() {
		return mUtilsVectors;
	}
}
