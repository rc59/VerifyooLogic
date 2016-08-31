package Logic.Comparison.Stats.Data.Abstract;

import Logic.Comparison.Stats.Data.Interface.IStatEngineResult;

public abstract class StatEngineResultAbstract implements IStatEngineResult {
	public double mScore;
	public double mZscore;
	public double mWeight;
	
	public StatEngineResultAbstract(double score, double zScore, double weight)
	{
		mScore = score;
		mZscore = zScore;
		mWeight = weight;
	}
	
	@Override
	public double GetScore() { 
		return mScore;
	}
	
	@Override
	public double GetZScore() {
		return mZscore;
	}
	
	@Override
	public double GetWeight() {
		return mWeight;
	}
//	@Override
//	public boolean IsValid() {
//		// TODO Auto-generated method stub
//		return false;
//	}
}
