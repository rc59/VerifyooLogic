package Logic.Utils;

import java.util.ArrayList;

import Data.UserProfile.Extended.MotionEventExtended;
import Data.UserProfile.Raw.MotionEventCompact;

public class UtilsVectors {
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
}
