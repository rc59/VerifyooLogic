package Logic.Utils;

import javax.swing.text.InternationalFormatter;

import Consts.ConstsParamNames;
import Logic.Comparison.Stats.Data.Interface.IStatEngineResult;

public class UtilsStat {
	public double CalculateScore1(double authValue, double populationMean, double populationSd, double internalMean, double internalSd) {
		
		double boundaryFactor = 0.2;
		double zScore = Math.abs((internalMean - populationMean) / populationSd);
		if(zScore > 2) {
			zScore = 2;
		}
		if(zScore < 1) {
			zScore = 1;
		}
		boundaryFactor = boundaryFactor * zScore;
		
		double boundary = internalMean * boundaryFactor;
		
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
	
	public double CalculateBoundaryFactor(double populationMean, double populationSd, double internalMean, double boundaryAdj) {
		double boundaryFactor = boundaryAdj;
		double zScore = Math.abs((internalMean - populationMean) / populationSd);
			
		if(zScore > 2) {
			zScore = 2;
		}
		if(zScore < 1) {
			zScore = 1;
		}		
		
		double boundaryFactorMultiplier = Math.abs(zScore) - 1;
		if(boundaryFactorMultiplier > 1) {
			boundaryFactorMultiplier = 1;
		}
		if(boundaryFactorMultiplier < 0) {
			boundaryFactorMultiplier = 0;
		}
		boundaryFactorMultiplier = boundaryFactorMultiplier * 0.1;
//		boundaryFactor += boundaryFactorMultiplier;
		
		double boundary = internalMean * boundaryFactor;
//		return (boundaryAdj * zScore);
		return boundary;
	}
	
	public double CalculateScore1(double authValue, double populationMean, double populationSd, double internalMean, double internalSd, double boundaryAdj) {
		double boundary = CalculateBoundaryFactor(populationMean, populationSd, internalMean, boundaryAdj);	
		
		double uniquenessFactor = 1;
		
//		double b = internalMean * boundary;
		double b = boundary;
		
		double firstUpperPopulationInternalSD = (internalMean + b);
		double firstLowerPopulationInternalSD = (internalMean - b);
		
		double secondUpperPopulationInternalSD = (internalMean + 2 * b);
		double secondLowerPopulationInternalSD = (internalMean - 2 * b);
		double score;
		
		double boundaryMaxScore = 0.9;		
		
		double internalBoundaryLower = internalMean - internalSd;
		double internalBoundaryUpper = internalMean + internalSd;
		
		if(authValue > internalBoundaryLower && authValue < internalBoundaryUpper) {
			score = 1;
		}
		else {
			if((authValue > firstLowerPopulationInternalSD) && (authValue < firstUpperPopulationInternalSD)) {
				score = 0.95;
				if(authValue != internalMean) {
					score = Math.abs(authValue - internalMean) / b;
					score = 1 - (1 - boundaryMaxScore) * (1 - score);
				}
			}
			else {
				if((authValue > secondLowerPopulationInternalSD) && (authValue < secondUpperPopulationInternalSD)) {
					if(authValue > firstLowerPopulationInternalSD) {
						double u = Math.abs(secondUpperPopulationInternalSD - authValue);
						double d = Math.abs(secondUpperPopulationInternalSD - firstUpperPopulationInternalSD);
						
						score = u/d;
					}
					else {
						double u = Math.abs(secondLowerPopulationInternalSD - authValue);
						double d = Math.abs(secondLowerPopulationInternalSD - firstLowerPopulationInternalSD);
						
						score = u/d;
					}
					
					score -= (1 - boundaryMaxScore);
					if(score < 0 ) {
						score = 0;
					}
				}
				else {
					score = 0;
				}
			}			
		}
		
		return score;
	}
	
	public double CalculateScore(double authValue, double populationMean, double populationSd, double internalMean, double internalSd, double boundaryAdj) {
		double boundary = boundaryAdj; //CalculateBoundaryFactor(populationMean, populationSd, internalMean, boundaryAdj);	
		
//		boundary = internalSd / populationMean;
//		if(boundary < 0.1) {
//			boundary = 0.1;
//		}
		
		double firstBoundaryFactor = 2;
		double secondBoundaryFactor = 5;
		
		double internalBoundaryLower = internalMean - firstBoundaryFactor * internalMean * boundary;
		double internalBoundaryUpper = internalMean + firstBoundaryFactor * internalMean * boundary;
		
		double secondLowerPopulationInternalSD = internalMean - secondBoundaryFactor * internalMean * boundary;
		double secondUpperPopulationInternalSD = internalMean + secondBoundaryFactor * internalMean * boundary;
		
		double score = 0;
		
		if(authValue > internalBoundaryLower && authValue < internalBoundaryUpper) {
			score = 1;
		}
		else {
			if(authValue > secondLowerPopulationInternalSD && authValue < secondUpperPopulationInternalSD) {
				if(authValue > internalBoundaryLower) {
					double u = Math.abs(secondUpperPopulationInternalSD - authValue);
					double d = Math.abs(secondUpperPopulationInternalSD - internalBoundaryUpper);
					
					score = u/d;
				}
				else {
					double u = Math.abs(secondLowerPopulationInternalSD - authValue);
					double d = Math.abs(secondLowerPopulationInternalSD - internalBoundaryLower);
					
					score = u/d;
				}
			}
			
//			score = score * score;
		}
		
		return score;
	}	
	
	public double CalculateScoreSpatial(double authValue, double populationMean, double populationSd, double internalMean, double internalSd, String paramName, String spatialType) {
		double boundaryFactor = 1;
		
		if(spatialType.compareTo(ConstsParamNames.Stroke.STROKE_SPATIAL_SAMPLING) == 0) {
			switch(paramName) {
			case ConstsParamNames.StrokeSampling.ACCELERATIONS:
				boundaryFactor = 1.5;
				break;
			case ConstsParamNames.StrokeSampling.ACCUMULATED_NORM_AREA:
				boundaryFactor = 1;			
				break;
			case ConstsParamNames.StrokeSampling.DELTA_TETA:
				boundaryFactor = 1;
				break;
			case ConstsParamNames.StrokeSampling.RADIAL_ACCELERATION:
				boundaryFactor = 1;
				break;
			case ConstsParamNames.StrokeSampling.RADIAL_VELOCITIES:
				boundaryFactor = 1;
				break;
			case ConstsParamNames.StrokeSampling.RADIUS:
				boundaryFactor = 1;
				break;
			case ConstsParamNames.StrokeSampling.TETA:
				boundaryFactor = 1;
				break;
			case ConstsParamNames.StrokeSampling.VELOCITIES:
				boundaryFactor = 0.18;
				break;
			}
		}
		if(spatialType.compareTo(ConstsParamNames.Stroke.STROKE_TEMPORAL_SAMPLING) == 0) {
			switch(paramName) {
			case ConstsParamNames.StrokeSampling.ACCELERATIONS:
				boundaryFactor = 4;
				break;
			case ConstsParamNames.StrokeSampling.ACCUMULATED_NORM_AREA:
				boundaryFactor = 0.3;		
				break;
			case ConstsParamNames.StrokeSampling.DELTA_TETA:
				boundaryFactor = 3.5;
				break;
			case ConstsParamNames.StrokeSampling.RADIAL_ACCELERATION:
				boundaryFactor = 3.5;
				break;
			case ConstsParamNames.StrokeSampling.RADIAL_VELOCITIES:
				boundaryFactor = 3.5;
				break;
			case ConstsParamNames.StrokeSampling.RADIUS:
				boundaryFactor = 0.15;
				break;
			case ConstsParamNames.StrokeSampling.TETA:
				boundaryFactor = 4;
				break;
			case ConstsParamNames.StrokeSampling.VELOCITIES:
				boundaryFactor = 0.18;
				break;
			}
		}
			
		double zScore = (internalMean - populationMean) / populationSd;
			
		double boundaryFactorMultiplier = Math.abs(zScore) - 1;
		if(boundaryFactorMultiplier > 1) {
			boundaryFactorMultiplier = 1;
		}
		if(boundaryFactorMultiplier < 0) {
			boundaryFactorMultiplier = 0;
		}
		boundaryFactorMultiplier = boundaryFactorMultiplier * 0.1;
		boundaryFactor += boundaryFactorMultiplier; 
		
		double boundary = internalMean * boundaryFactor;
				
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
	
	public double CalcWeight(double internalMean, double internalSd, double popMean, double popSd, double adjFactor) {
//		double boundary = CalculateBoundaryFactor(popMean, popSd, internalMean, adjFactor);
//		
//		double lowerBound = internalMean - boundary;
//		double upperBound = internalMean + boundary;
//		
//		double lowerBoundZ = CalculateZScore(lowerBound, popMean, popSd);
//		double upperBoundZ = CalculateZScore(upperBound, internalMean, internalSd);
//		
//		double lowerBoundProb = ConvertZToProbabilityCheckNegative(lowerBoundZ);
//		double upperBoundProb = ConvertZToProbabilityCheckNegative(upperBoundZ);
//		
//		double weight = 1 - Math.abs(upperBoundProb - lowerBoundProb);
//		double zScore = Math.abs(CalculateZScore(internalMean, popMean, popSd));
//		if(zScore > 2) {
//			zScore = 2;
//		}
//		if(zScore < 1) {
//			zScore = 1;
//		}
		
//		return (weight + zScore / 2) / 2;
		
		double weight = 1;
		double zScore = Math.abs(CalculateZScore(internalMean, popMean, popSd));
		if(zScore <= 0.5) {
			weight = 0;
		}
		
		if(zScore > 0.5 && zScore < 2) {
			weight = zScore - 0.5;
			weight = weight / 1.5;
		}
		
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
