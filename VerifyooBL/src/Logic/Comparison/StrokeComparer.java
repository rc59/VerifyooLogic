package Logic.Comparison;

import Consts.ConstsParamNames;
import Consts.ConstsParamWeights;
import Data.Comparison.CompareResultGeneric;
import Data.Comparison.CompareResultParamVectors;
import Data.Comparison.CompareResultSummary;
import Data.Comparison.Interfaces.ICompareResult;
import Data.UserProfile.Extended.StrokeExtended;
import Data.UserProfile.Raw.Stroke;
import Logic.Calc.UtilsMath;
import Logic.Calc.UtilsSpatialSampling;
import Logic.Calc.UtilsVectors;

public class StrokeComparer {
	UtilsVectors mUtilsVectors;	
	
	CompareResultSummary mCompareResult;
	
	StrokeExtended mStrokeStoredExtended;
	StrokeExtended mStrokeAuthExtended;		
	
	public StrokeComparer()
	{			
		mCompareResult = new CompareResultSummary();
		InitUtils();
	}
	
	protected void InitUtils()
	{
		mUtilsVectors = new UtilsVectors();
	}
	
	public void CompareStrokes(Stroke strokeStored, Stroke strokeAuth)	
	{			
		mStrokeStoredExtended = new StrokeExtended(strokeStored);
		mStrokeAuthExtended = new StrokeExtended(strokeAuth);
		
		CompareShapes();
		CompareStrokeAreas();
		CalculateFinalScore();		
	}	
	
	protected void CalculateFinalScore()
	{		
		double avgScore = 0;
		double totalWeights = 0;
		for(int idx = 0; idx < mCompareResult.ListCompareResults.size(); idx++) {
			avgScore += mCompareResult.ListCompareResults.get(idx).GetValue() * mCompareResult.ListCompareResults.get(idx).GetWeight();
			totalWeights += mCompareResult.ListCompareResults.get(idx).GetWeight();
		}
		
		mCompareResult.Score = avgScore / totalWeights;
	}
	
	public double GetScore()
	{
		return mCompareResult.Score;
	}
	
	protected void CompareStrokeAreas()
	{
		double areaStored = mStrokeStoredExtended.ShapeDataObj.ShapeArea;
		double areaAuth = mStrokeAuthExtended.ShapeDataObj.ShapeArea;
		
		double areaScore = UtilsMath.GetPercentageDiff(areaStored, areaAuth);

		double finalScore = 1;
		double threashold = 0.65;
		if(areaScore < threashold ) {			
			finalScore = 1 - (threashold - areaScore);
		}
		
		ICompareResult compareResult = 
				(ICompareResult) new CompareResultGeneric(ConstsParamNames.STROKE_AREA, finalScore, ConstsParamWeights.HIGH);
		mCompareResult.ListCompareResults.add(compareResult);
	}
	
	protected void CompareShapes()
	{	
		double[] vectorStored = mStrokeStoredExtended.SpatialSamplingVector;
		double[] vectorAuth = mStrokeAuthExtended.SpatialSamplingVector;
		
		double minimumCosineDistanceScore = 
				mUtilsVectors.MinimumCosineDistanceScore(vectorStored, vectorAuth);
		
		double score = 0;
		if(minimumCosineDistanceScore > 1) {
			score = 0.86;
		}
		if(minimumCosineDistanceScore > 1.5) {
			score = 0.91;
		}
		if(minimumCosineDistanceScore > 2) {
			score = 0.96;
		}
		if(minimumCosineDistanceScore > 2.5) {
			score = 1;
		}
		
		String msg;
		try
		{
		ICompareResult compareResult = 
				(ICompareResult) new CompareResultParamVectors(ConstsParamNames.MINIMUM_COSINE_DISTANCE, score, ConstsParamWeights.MEDIUM, minimumCosineDistanceScore, vectorStored, vectorAuth);
		mCompareResult.ListCompareResults.add(compareResult);
		}
		catch(Exception exc)
		{
			msg = exc.getMessage();
		}				
	}
}
