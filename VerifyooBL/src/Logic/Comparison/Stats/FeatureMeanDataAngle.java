package Logic.Comparison.Stats;

import Logic.Comparison.Stats.Abstract.FeatureMeanDataAbstract;

public class FeatureMeanDataAngle extends FeatureMeanDataAbstract {
	public FeatureMeanDataAngle(String name, String instruction) {
		super(name, instruction);		
	}

	@Override
	public double GetMean(){
//		double angularMean = 0;
//		double dx = 0;
//		double dy = 0;
//		for(int i = 0; i < mListValues.size(); i++)
//		{
//			dx += Math.cos(mListValues.get(i));
//			dy += Math.sin(mListValues.get(i));
//		}
//		
//		angularMean = Math.atan2(dy, dx);
//		return angularMean;
		return -1;
	}

	@Override
	public double GetInternalSd()
	{
//	      double sin = 0;
//	      double cos = 0;
//	      for(int i = 0; i < mListValues.size(); i++)
//	      {
//	           sin += Math.sin(mListValues.get(i));
//	           cos += Math.cos(mListValues.get(i)); 
//	      }
//	      sin /= mListValues.size();
//	      cos /= mListValues.size();
//
//	      double stddev = Math.sqrt(-Math.log(sin*sin+cos*cos));
//
//	      return stddev;
		return -1;
	 }
}
