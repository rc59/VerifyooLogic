package Logic.Utils;

import java.util.ArrayList;

import Data.UserProfile.Extended.MotionEventExtended;
import Data.UserProfile.Raw.MotionEventCompact;

public class UtilsSpatialSampling {
	final private int NUM_TEMPORAL_SAMPLING_POINTS = 32;
	
	private double[] mOrientations = new double[] {
	        0, (Math.PI / 4), (Math.PI / 2), (Math.PI * 3 / 4),
	        Math.PI, -0, (-Math.PI / 4), (-Math.PI / 2),
	                (-Math.PI * 3 / 4), -Math.PI
	    };
	
	public double[] PrepareDataSpatialSampling(ArrayList<MotionEventCompact> eventsList, double length) {
        float[] pts = ConvertToVector(eventsList, length);
        float[] center = ComputeCentroid(pts);
        float orientation = (float) Math.atan2(pts[1] - center[1], pts[0] - center[0]);

        float adjustment = -orientation;
        int count = mOrientations.length;
        for (int i = 0; i < count; i++) {
            float delta = (float) mOrientations[i] - orientation;
            if (Math.abs(delta) < Math.abs(adjustment)) {
                adjustment = delta;
            }
        }

        Translate(pts, -center[0], -center[1]);
        Rotate(pts, adjustment);

        pts = Normalize(pts);

        double[] ptsDouble = new double[pts.length];
        for(int idx = 0; idx < pts.length; idx++) {
        	ptsDouble[idx] = (double) pts[idx];
        }
        
        return ptsDouble;
    }
	
	private float[] ConvertToVector(ArrayList<MotionEventCompact> eventsList, double length) {
        int minValue = -9999999;
        int numPoints = NUM_TEMPORAL_SAMPLING_POINTS;
        int vectorLength = numPoints * 2;
        float[] vector = new float[vectorLength];

        ArrayList<MotionEventCompact> listEventsSpatial = new ArrayList<>();         
        
        ArrayList<Double> listTimes = new ArrayList<>();
        listTimes.add(eventsList.get(0).EventTime);
        
        try
        {
        final float increment = (float) length / (numPoints - 1);

        float distanceSoFar = 0;

        float[] pts = new float[eventsList.size() * 2];
        for (int idx = 0; idx < eventsList.size(); idx++)
        {
            pts[idx * 2] = (float) eventsList.get(idx).Xpixel;
            pts[idx * 2 + 1] = (float) eventsList.get(idx).Ypixel;
        }

        float lstPointX = pts[0];
        float lstPointY = pts[1];
        
        int index = 0;
        float currentPointX = minValue;
        float currentPointY = minValue;
        vector[index] = lstPointX;
        index++;
        vector[index] = lstPointY;
        index++;
        int i = 0;
        double newTime;
        
        int count = pts.length / 2;
            while (i < count) {
                if (currentPointX == minValue) {
                    i++;
                    if (i >= count) {
                        break;
                    }
                    currentPointX = pts[i * 2];
                    currentPointY = pts[i * 2 + 1];
                }
                float deltaX = currentPointX - lstPointX;
                float deltaY = currentPointY - lstPointY;
                float distance = (float) Math.sqrt(deltaX * deltaX + deltaY * deltaY);
                if (distanceSoFar + distance >= increment) {
                    float ratio = (increment - distanceSoFar) / distance;
                    float nx = lstPointX + ratio * deltaX;
                    float ny = lstPointY + ratio * deltaY;
                    vector[index] = nx;
                    index++;
                    vector[index] = ny;
                    
                    newTime = ratio * (eventsList.get(i + 1).EventTime - eventsList.get(i).EventTime) + eventsList.get(i).EventTime;
                    listTimes.add(newTime);
                    
                    index++;
                    lstPointX = nx;
                    lstPointY = ny;
                    distanceSoFar = 0;
                } else {
                    lstPointX = currentPointX;
                    lstPointY = currentPointY;
                    currentPointX = minValue;
                    currentPointY = minValue;
                    distanceSoFar += distance;
                }
            }

            for (i = index; i < vectorLength; i += 2) {
                vector[i] = lstPointX;
                vector[i + 1] = lstPointY;
            }
        } catch (Exception exc) {
            String msg = exc.getMessage();
        }

        MotionEventCompact tempEvent;
        
        for(int idx = 0; idx < vector.length; idx += 2) {
        	tempEvent = new MotionEventCompact();
        	tempEvent.Xpixel = vector[idx];
        	tempEvent.Ypixel = vector[idx + 1];
        	tempEvent.EventTime = listTimes.get(idx / 2);
        	listEventsSpatial.add(tempEvent);
        }
                        
        return vector;
    }
	
	private float[] Normalize(float[] vector) {
        float[] sample = vector;
        float sum = 0;

        int size = sample.length;
        for (int i = 0; i < size; i++)
        {
            sum += sample[i] * sample[i];
        }

        float magnitude = (float) Math.sqrt(sum);
        for (int i = 0; i < size; i++)
        {
            sample[i] /= magnitude;
        }

        return sample;
    }

    private float[] Rotate(float[] points, float angle) {
        float cos = (float) Math.cos(angle);
        float sin = (float) Math.sin(angle);
        int size = points.length;
        for (int i = 0; i < size; i += 2) {
            float x = points[i] * cos - points[i + 1] * sin;
            float y = points[i] * sin + points[i + 1] * cos;
            points[i] = x;
            points[i + 1] = y;
        }
        return points;
    }

    private float[] Translate(float[] points, float dx, float dy) {
        int size = points.length;
        for (int i = 0; i < size; i += 2) {
            points[i] += dx;
            points[i + 1] += dy;
        }
        return points;
    }

    private float[] ComputeCentroid(float[] points) {
        float centerX = 0;
        float centerY = 0;
        int count = points.length;
        for (int i = 0; i < count; i++) {
            centerX += points[i];
            i++;
            centerY += points[i];
        }
        float[] center = new float[2];
        center[0] = 2 * centerX / count;
        center[1] = 2 * centerY / count;

        return center;
    }
}