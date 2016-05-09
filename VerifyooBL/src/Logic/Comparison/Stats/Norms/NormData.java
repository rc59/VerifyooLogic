package Logic.Comparison.Stats.Norms;

import Logic.Comparison.Stats.Norms.Interfaces.INormData;

public class NormData implements INormData {
	protected String mParamName;
	protected double mMean;
	protected double mStandardDev;
	protected double mInternalStandardDev;
	
	public NormData(String paramName, double mean, double standardDev, double internalStandardDev)
	{
		mParamName = paramName;
		mMean = mean;
		mStandardDev = standardDev;
		mInternalStandardDev = internalStandardDev;
	}

	@Override
	public String GetName() {
		return mParamName;
	}

	@Override
	public double GetMean() {
		return mMean;
	}

	@Override
	public double GetStandardDev() {
		return mStandardDev;
	}

	@Override
	public double GetInternalStandardDev() {
		return mInternalStandardDev;
	}
}
