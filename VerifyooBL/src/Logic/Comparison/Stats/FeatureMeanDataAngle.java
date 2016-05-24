package Logic.Comparison.Stats;

import Logic.Comparison.Stats.Abstract.FeatureMeanDataAbstract;

public class FeatureMeanDataAngle extends FeatureMeanDataAbstract {
	public FeatureMeanDataAngle(String name) {
		super(name);		
	}

	@Override
	public double GetMean(){
		double angularMean = 0;
		double dx = 0;
		double dy = 0;
		for(int i = 0; i < mListValues.size(); i++)
		{
			dx += Math.cos(mListValues.get(i));
			dy += Math.sin(mListValues.get(i));
		}
		angularMean = Math.atan2(dy, dx);
		return angularMean;
	}

	@Override
	public double GetInternalSd()
	{
	      double sin = 0;
	      double cos = 0;
	      for(int i = 0; i < mListValues.size(); i++)
	      {
	           sin += Math.sin(mListValues.get(i) * (Math.PI/180.0));
	           cos += Math.cos(mListValues.get(i) * (Math.PI/180.0)); 
	      }
	      sin /= mListValues.size();
	      cos /= mListValues.size();

	      double stddev = Math.sqrt(-Math.log(sin*sin+cos*cos));

	      return stddev;
	 }
}
