package Logic.Utils;

import java.util.ArrayList;

import Data.MetaData.InterestPoint;
import Data.UserProfile.Extended.MotionEventExtended;
import Data.UserProfile.Raw.MotionEventCompact;

public class UtilsVectors {	
	public double[] GetVectorXpixelExtended(ArrayList<MotionEventExtended> listEvents) {
		double[] vectorX = new double[listEvents.size()];
		
		for(int idx = 0; idx < listEvents.size(); idx++) {
			vectorX[idx] = listEvents.get(idx).Xpixel;
		}
		
		return vectorX;
	}
	
	public double[] GetVectorYpixelExtended(ArrayList<MotionEventExtended> listEvents) {
		double[] vectorY = new double[listEvents.size()];
		
		for(int idx = 0; idx < listEvents.size(); idx++) {
			vectorY[idx] = listEvents.get(idx).Ypixel;
		}
		
		return vectorY;
	}
	
	public double[] GetVectorXpixel(ArrayList<MotionEventCompact> listEvents) {
		double[] vectorX = new double[listEvents.size()];
		
		for(int idx = 0; idx < listEvents.size(); idx++) {
			vectorX[idx] = listEvents.get(idx).Xpixel;
		}
		
		return vectorX;
	}
	
	public double[] GetVectorYpixel(ArrayList<MotionEventCompact> listEvents) {
		double[] vectorY = new double[listEvents.size()];
		
		for(int idx = 0; idx < listEvents.size(); idx++) {
			vectorY[idx] = listEvents.get(idx).Ypixel;
		}
		
		return vectorY;
	}
	
	public double[] GetVectorXmm(ArrayList<MotionEventExtended> listEvents) {
		double[] vectorX = new double[listEvents.size()];
		
		for(int idx = 0; idx < listEvents.size(); idx++) {
			vectorX[idx] = listEvents.get(idx).Xmm;
		}
		
		return vectorX;
	}
	
	public double[] GetVectorYmm(ArrayList<MotionEventExtended> listEvents) {
		double[] vectorY = new double[listEvents.size()];
		
		for(int idx = 0; idx < listEvents.size(); idx++) {
			vectorY[idx] = listEvents.get(idx).Ymm;
		}
		
		return vectorY;
	}
	
	public double[] GetVectorVel(ArrayList<MotionEventExtended> listEvents) {
		double[] vectorVel = new double[listEvents.size()];
		
		for(int idx = 0; idx < listEvents.size(); idx++) {
			vectorVel[idx] = listEvents.get(idx).Velocity;
		}
		
		return vectorVel;
	}
	
	public double[] GetVectorAcc(ArrayList<MotionEventExtended> listEvents) {
		double[] vectorAcc = new double[listEvents.size()];
		
		for(int idx = 0; idx < listEvents.size(); idx++) {
			vectorAcc[idx] = listEvents.get(idx).Acceleration;
		}
		
		return vectorAcc;
	}
	
	public double[] GetVectorTime(ArrayList<MotionEventExtended> listEvents) {
		double[] vector = new double[listEvents.size()];
		
		for(int idx = 0; idx < listEvents.size(); idx++) {
			vector[idx] = listEvents.get(idx).EventTime;
		}
		
		return vector;
	}
	
	public double[] GetVectorPressure(ArrayList<MotionEventExtended> listEvents) {
		double[] vector = new double[listEvents.size()];
		
		for(int idx = 0; idx < listEvents.size(); idx++) {
			vector[idx] = listEvents.get(idx).Pressure;
		}
		
		return vector;
	}
		
	public double[] GetVectorRadialVelocity(ArrayList<MotionEventExtended> listEvents) {
		double[] vector = new double[listEvents.size()];
		
		for(int idx = 0; idx < listEvents.size(); idx++) {
			vector[idx] = listEvents.get(idx).RadialVelocity;
		}
		
		return vector;
	}
	
	public double[] GetVectorSurface(ArrayList<MotionEventExtended> listEvents) {
		double[] vector = new double[listEvents.size()];
		
		for(int idx = 0; idx < listEvents.size(); idx++) {
			vector[idx] = listEvents.get(idx).TouchSurface;
		}
		
		return vector;
	}
	
	public double[] GetVectorXnormalized(ArrayList<MotionEventExtended> listEvents) {
		double[] vectorX = new double[listEvents.size()];
		
		for(int idx = 0; idx < listEvents.size(); idx++) {
			vectorX[idx] = listEvents.get(idx).Xnormalized;
		}
		
		return vectorX;
	}
	
	public double[] GetVectorYnormalized(ArrayList<MotionEventExtended> listEvents) {
		double[] vectorY = new double[listEvents.size()];
		
		for(int idx = 0; idx < listEvents.size(); idx++) {
			vectorY[idx] = listEvents.get(idx).Ynormalized;
		}
		
		return vectorY;
	}
	
	public double MinimumCosineDistanceScore(double[] vector1, double[] vector2)
	{
		double distance = MinimumCosineDistance(vector1, vector2, 2);

        double weight = 0;
        if(!Double.isNaN(distance)) {
            if (distance == 0)
            {
                weight = 300;
            }
            else
            {
                weight = 1 / distance;
            }
        }

        return weight;
	}
	
	protected float MinimumCosineDistance(double[] vector1, double[] vector2, double numOrientations) {		
		final int len = vector1.length;
        float a = 0;
        float b = 0;
        for (int i = 0; i < len; i += 2) {
            a += vector1[i] * vector2[i] + vector1[i + 1] * vector2[i + 1];
            b += vector1[i] * vector2[i + 1] - vector1[i + 1] * vector2[i];
        }
        if (a != 0) {
            final float tan = b/a;
            final double angle = Math.atan(tan);
            if (numOrientations > 2 && Math.abs(angle) >= Math.PI / numOrientations) {
                return (float) Math.acos(a);
            } else {
                final double cosine = Math.cos(angle);
                final double sine = cosine * tan;
                return (float) Math.acos(a * cosine + b * sine);
            }
        } else {
            return (float) Math.PI / 2;
        }
    }
	
	public float SquaredEuclideanDistance(float[] vector1, float[] vector2) {
        float squaredDistance = 0;
        int size = vector1.length;
        for (int i = 0; i < size; i++) {
            float difference = vector1[i] - vector2[i];
            squaredDistance += difference * difference;
        }
        return squaredDistance / size;
    }
	public void MedianFilter(double[] values){

		double[] v = new double[5];
		
        for(int idx = 2; idx < values.length - 2; idx++){
        	v[0] = values[idx-2];
        	v[1] = values[idx-1];
        	v[2] = values[idx];
        	v[3] = values[idx+1];
        	v[4] = values[idx+2];
        	
        	if(v[0] > v[1]) swap(v, 0, 1);
        	if(v[2] > v[3]) swap(v, 2, 3);
        	if(v[0] > v[2]) swap(v, 0, 2);
        	if(v[1] > v[4]) swap(v, 1, 4);
        	if(v[0] > v[1]) swap(v, 0, 1);
        	if(v[2] > v[3]) swap(v, 2, 3);
        	if(v[1] > v[2]) swap(v, 1, 2);
        	if(v[3] > v[4]) swap(v, 3, 4);
        	if(v[2] > v[3]) swap(v, 2, 3);
        	
        	values[idx] = v[2];
        }
	}
	
	public void swap (double[] a, int i, int j) {
		double t = a[i];
		a[i] = a[j];
		a[j] = t;
	}

	public void AverageFilter(double[] eventDensities) {
		double average = 0;
		double lengthDouble = (double)eventDensities.length;
		
		for(int idx = 0; idx < eventDensities.length; idx++) {
			average += eventDensities[idx];
		}
		
		average = average / lengthDouble;
		
		
		for(int idx = 0; idx < eventDensities.length; idx++) {
			eventDensities[idx] = eventDensities[idx] - average;
		}
	}
	
	public ArrayList<InterestPoint> FindInterestPoints(ArrayList<MotionEventExtended> listEventsExtended, int idxStart, int idxEnd, double threashold, boolean isMajorIntPoint, double commonDensity, double commonDensitySecond) {	
		double numEvents = (double) listEventsExtended.size();
		double averageDensity = 0;
			
		double numDensityPoints = 0;
		for(int idx = idxStart; idx <= idxEnd; idx++) {
			averageDensity += listEventsExtended.get(idx).EventDensityRaw;
			numDensityPoints++;
		}
	
		averageDensity = averageDensity / numDensityPoints;
				
		ArrayList<InterestPoint> listInterestPoints = new ArrayList<>();
		InterestPoint tempInterestPoint;
		
		int idxIntStart = 0;
		int idxIntEnd = 0;
		int idxIntAvg = 0;
		
		boolean isIntPointFound = false;
		boolean isIntPointStartIdxFound = false;
		
		MotionEventExtended eventCurr, eventNext;
		double diffDensity;			
		
		for(int idx = 0; idx < listEventsExtended.size(); idx++) {
			if(idx >= idxStart && idx < idxEnd) {
				eventCurr = listEventsExtended.get(idx);
				eventNext = listEventsExtended.get(idx + 1);
								
				diffDensity =  eventNext.EventDensityRaw - eventCurr.EventDensityRaw;
				
				if(diffDensity > 0) {
					idxIntStart = idx;
					isIntPointFound = true;
				}
				while(isIntPointFound) {
					idx++;
					if(idx >= idxEnd) {
						break;
					}
					eventCurr = listEventsExtended.get(idx);
					eventNext = listEventsExtended.get(idx + 1);
					diffDensity =  eventNext.EventDensityRaw - eventCurr.EventDensityRaw;							
					
					if(diffDensity < 0 && eventCurr.EventDensityRaw > threashold * averageDensity) {
						idxIntEnd = idx;
						isIntPointStartIdxFound = false;
						idxIntStart = idx;
						while(!isIntPointStartIdxFound) {							
							if(listEventsExtended.get(idxIntStart).EventDensityRaw - listEventsExtended.get(idxIntStart - 1).EventDensityRaw > 0) {
								isIntPointStartIdxFound = true;								
								break;
							}
							idxIntStart--;
							if(idxIntStart <= 0) {
								break;
							}
						}
						idxIntAvg = (idxIntStart + idxIntEnd) / 2;
						listEventsExtended.get(idxIntAvg).EventDensitySignalStrength2 = 1;
						if(isMajorIntPoint) {
							listEventsExtended.get(idxIntAvg).EventDensitySignalStrength2++;
						}
						
						int tempIdx = idxIntStart;
						while(tempIdx > idxStart) {
							if(listEventsExtended.get(tempIdx).EventDensityRaw == commonDensity) {
								idxIntStart = tempIdx;
								break;								
							}
							else {
								tempIdx--;
							}
						}
						
						tempIdx = idxIntEnd;
						while(idxIntEnd < idxEnd) {
							if(listEventsExtended.get(tempIdx).EventDensityRaw == commonDensity) {
								idxIntEnd = tempIdx;
								break;								
							}
							else {
								tempIdx++;
							}
						}
						
						tempInterestPoint = new InterestPoint(idxIntStart, idxIntEnd, numEvents);
						listInterestPoints.add(tempInterestPoint);
						
						idxIntStart = 0;
						idxIntEnd = 0;	
						isIntPointFound = false;
						break;
					}
				}
				
			}
			else {
				if(isMajorIntPoint) {
					listEventsExtended.get(idx).EventDensitySignalStrength2 = -1;	
				}
			}
		}
			
		double totalTime;		
		double totalDistance;
		
		int idxCurrIntStart, idxCurrIntEnd;
		for(int idxIntPoint = 0; idxIntPoint < listInterestPoints.size(); idxIntPoint++) {
			tempInterestPoint = listInterestPoints.get(idxIntPoint);
						
			totalDistance = 0;
			totalTime = 0;
			idxCurrIntStart = (int)tempInterestPoint.IdxStart;
			idxCurrIntEnd = (int)tempInterestPoint.IdxEnd;
			
			for(int idx = idxCurrIntStart; idx <= idxCurrIntEnd; idx++) {
				tempInterestPoint.Intensity += listEventsExtended.get(idx).EventDensityRaw;
				
				if(idx > 0) {
					totalTime += listEventsExtended.get(idx).EventTime - listEventsExtended.get(idx - 1).EventTime;
					totalDistance += Utils.GetInstance().GetUtilsMath().CalcDistanceInMMs(listEventsExtended.get(idx - 1), listEventsExtended.get(idx));
				}			
			}
			
			if(totalTime > 0) {
				tempInterestPoint.AverageVelocity = totalDistance / totalTime;	
			}
			else {
				tempInterestPoint.AverageVelocity = listEventsExtended.get(idxCurrIntStart).Velocity;
			}
		}		
		
		return listInterestPoints;
	}
}
