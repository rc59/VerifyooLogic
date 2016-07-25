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
        double[] ptsTime = ConvertToVectorByTime(eventsList);
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
	
	private double[] ConvertToVectorByTime(ArrayList<MotionEventCompact> eventsList) {
        double timeInterval = eventsList.get(eventsList.size() - 1).EventTime - eventsList.get(0).EventTime; 
		
		int minValue = -9999999;
        int numPoints = NUM_TEMPORAL_SAMPLING_POINTS;
        int vectorLength = numPoints;
        double[] vector = new double[vectorLength];

        ArrayList<MotionEventCompact> listEventsSpatial = new ArrayList<>();
        listEventsSpatial.add(eventsList.get(0));
        
        try
        {
        final double increment = timeInterval / (numPoints - 1);

        float timeSoFar = 0;

        double[] pts = new double[eventsList.size()];
        for (int idx = 0; idx < eventsList.size(); idx++)
        {
            pts[idx] = eventsList.get(idx).EventTime;            
        }

        double lstPointTime = pts[0];       
        
        int index = 0;
        double currentPointTime = minValue;
        
        vector[index] = lstPointTime;
        index++;
        
        int i = 0;        
        MotionEventCompact tempEventSpatial;
        
        int count = pts.length;
            while (i < count) {
                if (currentPointTime == minValue) {
                    i++;
                    if (i >= count) {
                        break;
                    }
                    currentPointTime = pts[i];                    
                }
                
                double timeDiff = currentPointTime - lstPointTime;
                if (timeSoFar + timeDiff >= increment) {
                	double ratio = (increment - timeSoFar) / timeDiff;
                	double nt = lstPointTime + ratio * timeDiff;
                    
                    vector[index] = nt;
                    index++;
                                       
                    tempEventSpatial = CreateNewEvent(eventsList.get(i + 1), eventsList.get(i), ratio);                                        
                    listEventsSpatial.add(tempEventSpatial);
                    
                    lstPointTime = nt;
                    timeSoFar = 0;
                } else {
                    lstPointTime = currentPointTime;                    
                    currentPointTime = minValue;                    
                    timeSoFar += timeDiff;
                }
            }

            for (i = index; i < vectorLength; i++) {
                vector[i] = lstPointTime;                
            }
        } catch (Exception exc) {
            String msg = exc.getMessage();
        }

        MotionEventCompact tempEvent;
        int idxTempVector = 0;
        
        double totalDistanceX = 0;
        double totalDistanceY = 0;
        double totalGyroX = 0;
        double totalVelocityX = 0;
        double totalVelocityY = 0;
        double totalPressure = 0;
        double totalSurface = 0;        
        
        for(int idx = 0; idx < listEventsSpatial.size(); idx++) {
        	tempEvent = new MotionEventCompact();
        	
        	listEventsSpatial.get(idx).EventTime = vector[idxTempVector];
        	idxTempVector++; 
        	
        	totalDistanceX += listEventsSpatial.get(idx).Xpixel; 
        	totalDistanceY += listEventsSpatial.get(idx).Ypixel;
        	totalGyroX += listEventsSpatial.get(idx).GyroX;
        	totalVelocityX += listEventsSpatial.get(idx).VelocityX;
        	totalVelocityY += listEventsSpatial.get(idx).VelocityY;
        	totalPressure += listEventsSpatial.get(idx).Pressure;
        	totalSurface += listEventsSpatial.get(idx).TouchSurface;        	
        }       
        
        totalDistanceX = totalDistanceX / listEventsSpatial.size();
        totalDistanceY = totalDistanceY / listEventsSpatial.size();
        totalGyroX = totalGyroX / listEventsSpatial.size();
        totalVelocityX = totalVelocityX / listEventsSpatial.size();
        totalVelocityY = totalVelocityY / listEventsSpatial.size();
        totalPressure = totalPressure / listEventsSpatial.size();
        totalSurface = totalSurface / listEventsSpatial.size();                
        
        double timeDiff = listEventsSpatial.get(listEventsSpatial.size() - 1).EventTime - listEventsSpatial.get(0).EventTime;
        double avgVelX = totalDistanceX / timeDiff;
        double avgVelY = totalDistanceY / timeDiff;
                        
        return vector;
    }
	
	private float[] ConvertToVector(ArrayList<MotionEventCompact> eventsList, double length) {
        int minValue = -9999999;
        int numPoints = NUM_TEMPORAL_SAMPLING_POINTS;
        int vectorLength = numPoints * 2;
        float[] vector = new float[vectorLength];

        ArrayList<MotionEventCompact> listEventsSpatial = new ArrayList<>();
        listEventsSpatial.add(eventsList.get(0));
        
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
        MotionEventCompact tempEventSpatial;
        
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
                                       
                    tempEventSpatial = CreateNewEvent(eventsList.get(i + 1), eventsList.get(i), ratio);                                        
                    listEventsSpatial.add(tempEventSpatial);
                    
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
        int idxTempVector = 0;
        
        double totalDistanceX = 0;
        double totalDistanceY = 0;
        double totalGyroX = 0;
        double totalVelocityX = 0;
        double totalVelocityY = 0;
        double totalPressure = 0;
        double totalSurface = 0;       
        
        for(int idx = 0; idx < listEventsSpatial.size(); idx++) {
        	tempEvent = new MotionEventCompact();
        	
        	listEventsSpatial.get(idx).Xpixel = vector[idxTempVector];
        	idxTempVector++;
        	listEventsSpatial.get(idx).Ypixel = vector[idxTempVector];
        	idxTempVector++;
        	
        	totalDistanceX += listEventsSpatial.get(idx).Xpixel; 
        	totalDistanceY += listEventsSpatial.get(idx).Ypixel;
        	totalGyroX += listEventsSpatial.get(idx).GyroX;
        	totalVelocityX += listEventsSpatial.get(idx).VelocityX;
        	totalVelocityY += listEventsSpatial.get(idx).VelocityY;
        	totalPressure += listEventsSpatial.get(idx).Pressure;
        	totalSurface += listEventsSpatial.get(idx).TouchSurface;   
        }
        
        totalDistanceX = totalDistanceX / listEventsSpatial.size();
        totalDistanceY = totalDistanceY / listEventsSpatial.size();
        totalGyroX = totalGyroX / listEventsSpatial.size();
        totalVelocityX = totalVelocityX / listEventsSpatial.size();
        totalVelocityY = totalVelocityY / listEventsSpatial.size();
        totalPressure = totalPressure / listEventsSpatial.size();
        totalSurface = totalSurface / listEventsSpatial.size();
                     
        double timeDiff = listEventsSpatial.get(listEventsSpatial.size() - 1).EventTime - listEventsSpatial.get(0).EventTime;
        double avgVelX = totalDistanceX / timeDiff;
        double avgVelY = totalDistanceY / timeDiff;
        
        return vector;
    }
	
	private MotionEventCompact CreateNewEvent(MotionEventCompact eventNext, MotionEventCompact eventPrev, double ratio) {
		MotionEventCompact tempEvent = new MotionEventCompact(); 

		tempEvent.EventTime = GetSpatialValue(eventNext.EventTime, eventPrev.EventTime, ratio);
		
		tempEvent.AccelerometerX = GetSpatialValue(eventNext.AccelerometerX, eventPrev.AccelerometerX, ratio);
		tempEvent.AccelerometerY = GetSpatialValue(eventNext.AccelerometerY, eventPrev.AccelerometerY, ratio);
		tempEvent.AccelerometerZ = GetSpatialValue(eventNext.AccelerometerZ, eventPrev.AccelerometerZ, ratio);
		
		tempEvent.GyroX = GetSpatialValue(eventNext.GyroX, eventPrev.GyroX, ratio);
		tempEvent.GyroY = GetSpatialValue(eventNext.GyroY, eventPrev.GyroY, ratio);
		tempEvent.GyroZ = GetSpatialValue(eventNext.GyroZ, eventPrev.GyroZ, ratio);
		
		tempEvent.Pressure = GetSpatialValue(eventNext.Pressure, eventPrev.Pressure, ratio);
		
		tempEvent.TouchSurface = GetSpatialValue(eventNext.TouchSurface, eventPrev.TouchSurface, ratio);
		
		tempEvent.VelocityX = GetSpatialValue(eventNext.VelocityX, eventPrev.VelocityX, ratio);
		tempEvent.VelocityY = GetSpatialValue(eventNext.VelocityY, eventPrev.VelocityY, ratio);		
		
		return tempEvent;
	}

	private double GetSpatialValue(double valueNext, double valuePrev, double ratio) {
		double value = ratio * (valueNext - valuePrev) + valuePrev;		
		return value;
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