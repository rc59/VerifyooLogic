package Logic.Utils;

public class Utils {
	private static Utils mInstance = null;
	
	protected static UtilsComparison mUtilsComparison;
	protected static UtilsMath mUtilsMath;
	protected static UtilsSignalProcessing mUtilsSignalProcessing;
	protected static UtilsStat mUtilsStat;
	protected static UtilsVectors mUtilsVectors;
	protected static UtilsLinearReg mUtilsLinearReg;
	protected static UtilsGeneral mUtilsGeneral;
	protected static UtilsPeakCalc mUtilsPeakCalc;
	
	private Utils()
	{
		
	}
	
	public static Utils GetInstance() {
		if(mInstance == null) {
			mInstance = new Utils();
			
			mUtilsComparison = new UtilsComparison();
			mUtilsMath = new UtilsMath();
			mUtilsSignalProcessing = new UtilsSignalProcessing();
			mUtilsStat = new UtilsStat();
			mUtilsVectors = new UtilsVectors();		
			mUtilsLinearReg = new UtilsLinearReg();
			mUtilsGeneral = new UtilsGeneral();
			mUtilsPeakCalc = new UtilsPeakCalc();
		}
		return mInstance;
	}
	
	public UtilsComparison GetUtilsComparison() {
		return mUtilsComparison;
	}
	public UtilsMath GetUtilsMath() {
		return mUtilsMath;
	}
	public UtilsSignalProcessing GetUtilsSignalProcessing() {
		return mUtilsSignalProcessing;
	}
	public UtilsStat GetUtilsStat() {
		return mUtilsStat;
	}
	public UtilsVectors GetUtilsVectors() {
		return mUtilsVectors;
	}
	public UtilsLinearReg GetUtilsLinearReg() {
		return mUtilsLinearReg;
	}
	public UtilsGeneral GetUtilsGeneral() {
		return mUtilsGeneral;
	}
	public UtilsPeakCalc GetUtilsPeakCalc() {
		return mUtilsPeakCalc;
	}
}
