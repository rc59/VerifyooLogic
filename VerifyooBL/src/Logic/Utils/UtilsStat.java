package Logic.Utils;

import Logic.Comparison.Stats.Data.Interface.IStatEngineResult;

public class UtilsStat {
	public double CalculateScore1(double authValue, double populationMean, double populationSd, double internalMean, double internalSd) {
		double boundary = internalMean * 0.2;
		
		double lowerBound = internalMean - boundary;
		double upperBound = internalMean + boundary;
		
		double score;
		if((authValue > lowerBound) && (authValue < upperBound)) {
			score = 1;
		}
		else {
			score = 0;
		}
		
		return score;
	}
	
	public double CalculateScore(double authValue, double populationMean, double populationSd, double internalMean, double internalSd) {
		double boundary = internalMean * 0.15;
		
		double zScore = (internalMean - populationMean) / populationSd;
		double weight = Math.abs(zScore);
		if(weight > 2) {
			weight = 2;
		}
		
		double uniquenessFactor = 1;
		
		double twoUpperPopulationInternalSD = (internalMean + boundary * uniquenessFactor);
		double twoLowerPopulationInternalSD = (internalMean - boundary * uniquenessFactor);
		
		double threeUpperPopulationInternalSD = (internalMean + 2 * boundary * uniquenessFactor);
		double threeLowerPopulationInternalSD = (internalMean - 2 * boundary * uniquenessFactor);
		double score;
		if((authValue > twoLowerPopulationInternalSD) && (authValue < twoUpperPopulationInternalSD)) {
			score = 1;
		}
		else {
			if((authValue > threeLowerPopulationInternalSD) && (authValue < threeUpperPopulationInternalSD)) {
				if(authValue > twoLowerPopulationInternalSD) {
					double u = Math.abs(threeUpperPopulationInternalSD - authValue);
					double d = Math.abs(threeUpperPopulationInternalSD - twoUpperPopulationInternalSD);
					
					score = u/d;
				}
				else {
					double u = Math.abs(threeLowerPopulationInternalSD - authValue);
					double d = Math.abs(threeLowerPopulationInternalSD - twoLowerPopulationInternalSD);
					
					score = u/d;
				}
			}
			else {
				score = 0;
			}
		}
		
		return score;
	}	
	
	public double CalculateScoreSpatial(double authValue, double populationMean, double populationSd, double internalMean, double internalSd) {
		double boundary = internalMean * 0.15;
		
		double zScore = (internalMean - populationMean) / populationSd;
		double weight = Math.abs(zScore);
		if(weight > 2) {
			weight = 2;
		}
		
		double uniquenessFactor = 1;
		
		double twoUpperPopulationInternalSD = (internalMean + boundary * uniquenessFactor);
		double twoLowerPopulationInternalSD = (internalMean - boundary * uniquenessFactor);
		
		double threeUpperPopulationInternalSD = (internalMean + 2 * boundary * uniquenessFactor);
		double threeLowerPopulationInternalSD = (internalMean - 2 * boundary * uniquenessFactor);
		double score;
		if((authValue > twoLowerPopulationInternalSD) && (authValue < twoUpperPopulationInternalSD)) {
			score = 1;
		}
		else {
			if((authValue > threeLowerPopulationInternalSD) && (authValue < threeUpperPopulationInternalSD)) {
				if(authValue > twoLowerPopulationInternalSD) {
					double u = Math.abs(threeUpperPopulationInternalSD - authValue);
					double d = Math.abs(threeUpperPopulationInternalSD - twoUpperPopulationInternalSD);
					
					score = u/d;
				}
				else {
					double u = Math.abs(threeLowerPopulationInternalSD - authValue);
					double d = Math.abs(threeLowerPopulationInternalSD - twoLowerPopulationInternalSD);
					
					score = u/d;
				}
			}
			else {
				score = 0;
			}
		}
		
		return score;
	}	
	
	public double CalcWeight(double internalMean, double internalSd, double popMean, double popSd) {
		double lowerBound = internalMean - internalSd;
		double upperBound = internalMean + internalSd;
		
		double lowerBoundZ = CalculateZScore(lowerBound, popMean, popSd);
		double upperBoundZ = CalculateZScore(upperBound, internalMean, internalSd);
		
		double lowerBoundProb = ConvertZToProbabilityCheckNegative(lowerBoundZ);
		double upperBoundProb = ConvertZToProbabilityCheckNegative(upperBoundZ);
		
		double weight = 1 - Math.abs(upperBoundProb - lowerBoundProb);
		
		return weight;
	}
		
	protected double CalculateProbability(double authValue, double internalMean, double internalSd, boolean isAbs) {
		double zScore = CalculateZScore(authValue, internalMean, internalSd);
		if(isAbs) {
			zScore = Math.abs(zScore);	
		}
		double probability = 1 - ConvertZToProbability(zScore);
		return probability;
	}

	protected double CalculateZScore(double value, double mean, double std) {
		double zScore = (value - mean) / std;
		return zScore;
	}
	
	public double ConvertZToProbabilityCheckNegative(double z) {
		double prob = ConvertZToProbability(z);
		if(z < 0) {
			prob = 1 - prob;
		}
		
		return prob;
	}
	
	public double ConvertZToProbability(double z) {
		double y, x, w;
        double Z_MAX = 6;

        if (z == 0.0) {
            x = 0.0;
        } else {
            y = 0.5 * Math.abs(z);
            if (y > (Z_MAX * 0.5)) {
                x = 1.0;
            } else if (y < 1.0) {
                w = y * y;
                x = ((((((((0.000124818987 * w
                        - 0.001075204047) * w + 0.005198775019) * w
                        - 0.019198292004) * w + 0.059054035642) * w
                        - 0.151968751364) * w + 0.319152932694) * w
                        - 0.531923007300) * w + 0.797884560593) * y * 2.0;
            } else {
                y -= 2.0;
                x = (((((((((((((-0.000045255659 * y
                        + 0.000152529290) * y - 0.000019538132) * y
                        - 0.000676904986) * y + 0.001390604284) * y
                        - 0.000794620820) * y - 0.002034254874) * y
                        + 0.006549791214) * y - 0.010557625006) * y
                        + 0.011630447319) * y - 0.009279453341) * y
                        + 0.005353579108) * y - 0.002141268741) * y
                        + 0.000535310849) * y + 0.999936657524;
            }
        }

        double result = 1 - (z > 0.0 ? ((x + 1.0) * 0.5) : ((1.0 - x) * 0.5));
        return result;		
	}
}
