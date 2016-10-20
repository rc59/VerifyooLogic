package Logic.Utils.DTW;

import Consts.ConstsParamNames;
import Logic.Comparison.Stats.Norms.NormMgr;
import Logic.Comparison.Stats.Norms.Interfaces.INormData;
import Logic.Utils.Utils;

public class DTWObjSampling implements IDTWObj {
	protected double mValue;
	protected int mIdxStroke;
	protected int mIdxSampling;
	protected String mInstruction;
	protected String mParam;
	protected String mSamplingType;
	
	public DTWObjSampling(double value, int idxStroke, int idxSampling, String instruction, String param, String samplingType) {
		mValue = value;
		mIdxStroke = idxStroke;
		mInstruction = instruction;
		mParam = param;
		mIdxSampling = idxSampling;
		mSamplingType = samplingType;
	}
	
	public double GetValue() {
		return mValue;
	}
	
	public int GetStrokeIdx() {
		return mIdxStroke;
	}
	
	public String GetInstruction() {
		return mInstruction;
	}
	
	public String GetParamName() {
		return mParam;
	}
	
	public double CompareTo(IDTWObj obj) {		
		DTWObjSampling authObj = (DTWObjSampling)obj;
		
		NormMgr normMgr = (NormMgr) NormMgr.GetInstance();
				
		double popMean = 0;
		double popSd = 0;
		double internalSd = 0;
		
		switch(mSamplingType) {
			case ConstsParamNames.Stroke.STROKE_SPATIAL_SAMPLING:
				popMean = normMgr.NormContainerMgr.GetSpatialPopMeanDistance(mInstruction, mParam, mIdxStroke, mIdxSampling);
				popSd = normMgr.NormContainerMgr.GetSpatialPopSdDistance(mInstruction, mParam, mIdxStroke, mIdxSampling);	
				internalSd = normMgr.NormContainerMgr.GetSpatialInternalSdDistance(mInstruction, mParam, mIdxStroke, mIdxSampling);										
			break;
			case ConstsParamNames.Stroke.STROKE_TEMPORAL_SAMPLING:
				popMean = normMgr.NormContainerMgr.GetSpatialPopMeanTime(mInstruction, mParam, mIdxStroke, mIdxSampling);
				popSd = normMgr.NormContainerMgr.GetSpatialPopSdTime(mInstruction, mParam, mIdxStroke, mIdxSampling);	
				internalSd = normMgr.NormContainerMgr.GetSpatialInternalSdTime(mInstruction, mParam, mIdxStroke, mIdxSampling);							
			break;
		}
		
		double internalMean = mValue;
		
		double result = (1 - Utils.GetInstance().GetUtilsStat().CalculateScoreSpatial(authObj.GetValue(), popMean, popSd, internalMean, internalSd, mParam, mSamplingType));
		
		return Math.abs(result);
	}
}
