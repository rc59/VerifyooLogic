package Logic.Comparison.Stats.Data.Abstract;

import Logic.Comparison.Stats.Data.Interface.IStatEngineResult;

public abstract class StatEngineResultAbstract implements IStatEngineResult {
	public double mScore;
	public double mZscore;
	
	public StatEngineResultAbstract(double score, double zScore)
	{
		mScore = score;
		mZscore = zScore;
	}
	
	@Override
	public double GetScore() { 
		return mScore;
	}
	
	@Override
	public double GetZScore() {
		return mZscore;
	}
	
	
}
