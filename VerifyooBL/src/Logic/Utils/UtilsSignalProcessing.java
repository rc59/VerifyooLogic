package Logic.Utils;

import java.util.ArrayList;

import Consts.ConstsMeasures;
import Data.MetaData.IndexBoundary;
import Data.UserProfile.Extended.MotionEventExtended;
import Data.UserProfile.Raw.MotionEventCompact;

public class UtilsSignalProcessing {
	final private int NUM_TEMPORAL_SAMPLING_POINTS = 32;
	
	private double[] mOrientations = new double[] {
	        0, (Math.PI / 4), (Math.PI / 2), (Math.PI * 3 / 4),
	        Math.PI, -0, (-Math.PI / 4), (-Math.PI / 2),
	                (-Math.PI * 3 / 4), -Math.PI
	    };
	
	public double[] PrepareDataSpatialSampling(ArrayList<MotionEventExtended> eventsList, double length, double xdpi, double ydpi) {
        float[] pts = GetPoints(ConvertToVectorByDistance(eventsList, length, xdpi, ydpi));        
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
	
	public double[] CalculateSignalStrength(double[] listValues) {
		double[] listSignalStrengths = new double[listValues.length];
		
		int idxStart, idxEnd, idxMiddle;
		double currStrength;
		
		for(int idx = 0; idx < listValues.length; idx++) {
			if(listValues[idx] > 0) {
				currStrength = 0;
				idxStart = idx;
				while(listValues[idx] > 0 && idx < listValues.length) {
					currStrength += listValues[idx];
					idx++;
				}
				idxEnd = idx;
				idxMiddle = (idxStart + idxEnd) / 2;
				listSignalStrengths[idxMiddle] = currStrength;
			}			
		}
		
		return listSignalStrengths;
	}
	
	public ArrayList<MotionEventExtended> ConvertToVectorByTime(ArrayList<MotionEventExtended> listEvents, double xdpi, double ydpi) {
        double timeInterval = listEvents.get(listEvents.size() - 1).EventTime - listEvents.get(0).EventTime; 
        String msg;
		int minValue = -9999999;
        int numPoints = NUM_TEMPORAL_SAMPLING_POINTS;
        int vectorLength = numPoints;
        double[] vector = new double[vectorLength];

        double[] vectorX = new double[numPoints];
        double[] vectorY = new double[numPoints];
        
        double[] vectorOriginalX = new double[listEvents.size()];
        double[] vectorOriginalY = new double[listEvents.size()];
                
        ArrayList<MotionEventExtended> listEventsSpatial = new ArrayList<>();
        
        try
        {
        final double increment = timeInterval / (numPoints - 1);

        float timeSoFar = 0;

        double[] pts = new double[listEvents.size()];
        for (int idx = 0; idx < listEvents.size(); idx++)
        {
            pts[idx] = listEvents.get(idx).EventTime;     
            vectorOriginalX[idx] = listEvents.get(idx).Xpixel;
            vectorOriginalY[idx] = listEvents.get(idx).Ypixel;
        }

        double lstPointTime = pts[0];       
        
        int index = 0;
        double currentPointTime = minValue;
        
        vector[index] = lstPointTime;
        listEventsSpatial.add(listEvents.get(0));
        index++;
        
        int i = 0;        
        MotionEventExtended tempEventSpatial;
        
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
                    lstPointTime = nt;
                    timeSoFar = 0;
                    
                    if(i + 1 < listEvents.size()) {
                    	tempEventSpatial = CreateNewEvent(listEvents.get(i + 1), listEvents.get(i), ratio, xdpi, ydpi);
                    }
                    else {
                    	tempEventSpatial = listEvents.get(i).Clone();
                    }
                    listEventsSpatial.add(tempEventSpatial);                                       
                } else {
                    lstPointTime = currentPointTime;                    
                    currentPointTime = minValue;                    
                    timeSoFar += timeDiff;
                }
            }

            for (i = index; i < vectorLength; i++) {
                vector[i] = lstPointTime;
                tempEventSpatial = listEvents.get(listEvents.size() - 1).Clone();
            	listEventsSpatial.add(tempEventSpatial);
            }
        } catch (Exception exc) {
            msg = exc.getMessage();
        }

        int idxTempVector = 0;
        
        double totalDistanceX = 0;
        double totalDistanceY = 0;
        double totalGyroX = 0;
        double totalVelocityX = 0;
        double totalVelocityY = 0;
        double totalPressure = 0;
        double totalSurface = 0;        
        
        double[] timeDiffs = new double[listEventsSpatial.size()];
        
        for(int idx = 0; idx < listEventsSpatial.size(); idx++) {        	        	
        	listEventsSpatial.get(idx).EventTime = vector[idxTempVector];
        	idxTempVector++; 
        	
        	if(idx > 1) {
        		timeDiffs[idx - 1] = listEventsSpatial.get(idx).EventTime - listEventsSpatial.get(idx - 1).EventTime;
        	}        
        	
        	totalDistanceX += listEventsSpatial.get(idx).Xpixel; 
        	totalDistanceY += listEventsSpatial.get(idx).Ypixel;
        	
        	vectorX[idx] = listEventsSpatial.get(idx).Xpixel;
        	vectorY[idx] = listEventsSpatial.get(idx).Ypixel;
        	
        	totalGyroX += listEventsSpatial.get(idx).GyroX();
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
                        
        return listEventsSpatial;
    }
	
	public ArrayList<MotionEventExtended> ConvertToVectorByFreq(ArrayList<MotionEventExtended> listEvents, double xdpi, double ydpi) {
        
		double totalTime = listEvents.get(listEvents.size() - 1).EventTime - listEvents.get(0).EventTime;		
		
		double timeInterval = 7.5; 
        String msg;
		int minValue = -9999999;
        int numPoints = NUM_TEMPORAL_SAMPLING_POINTS;
        
        double numPointsDouble = Math.ceil(totalTime / timeInterval);
        
        numPoints = (int)numPointsDouble;
        int vectorLength = numPoints;
        double[] vector = new double[vectorLength];

        double[] vectorX = new double[numPoints];
        double[] vectorY = new double[numPoints];
        
        double[] vectorOriginalX = new double[listEvents.size()];
        double[] vectorOriginalY = new double[listEvents.size()];
                
        ArrayList<MotionEventExtended> listEventsSpatial = new ArrayList<>();
        
        try
        {
        final double increment = timeInterval;

        float timeSoFar = 0;

        double[] pts = new double[listEvents.size()];
        for (int idx = 0; idx < listEvents.size(); idx++)
        {
            pts[idx] = listEvents.get(idx).EventTime;     
            vectorOriginalX[idx] = listEvents.get(idx).Xpixel;
            vectorOriginalY[idx] = listEvents.get(idx).Ypixel;
        }

        double lstPointTime = pts[0];       
        
        int index = 0;
        double currentPointTime = minValue;
        
        vector[index] = lstPointTime;
        listEventsSpatial.add(listEvents.get(0));
        index++;
        
        int i = 0;        
        MotionEventExtended tempEventSpatial;
        
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
                    lstPointTime = nt;
                    timeSoFar = 0;
                    
                    if(i + 1 < listEvents.size()) {
                    	tempEventSpatial = CreateNewEvent(listEvents.get(i + 1), listEvents.get(i), ratio, xdpi, ydpi);
                    }
                    else {
                    	tempEventSpatial = listEvents.get(i).Clone();
                    }
                    listEventsSpatial.add(tempEventSpatial);                                       
                } else {
                    lstPointTime = currentPointTime;                    
                    currentPointTime = minValue;                    
                    timeSoFar += timeDiff;
                }
            }

            for (i = index; i < vectorLength; i++) {
                vector[i] = lstPointTime;
                tempEventSpatial = listEvents.get(listEvents.size() - 1).Clone();
            	listEventsSpatial.add(tempEventSpatial);
            }
        } catch (Exception exc) {
            msg = exc.getMessage();
        }

        int idxTempVector = 0;
        
        double totalDistanceX = 0;
        double totalDistanceY = 0;
        double totalGyroX = 0;
        double totalVelocityX = 0;
        double totalVelocityY = 0;
        double totalPressure = 0;
        double totalSurface = 0;        
        
        double[] timeDiffs = new double[listEventsSpatial.size()];
        
        for(int idx = 0; idx < listEventsSpatial.size(); idx++) {        	        	
        	listEventsSpatial.get(idx).EventTime = vector[idxTempVector];
        	idxTempVector++; 
        	
        	if(idx > 1) {
        		timeDiffs[idx - 1] = listEventsSpatial.get(idx).EventTime - listEventsSpatial.get(idx - 1).EventTime;
        	}        
        	
        	totalDistanceX += listEventsSpatial.get(idx).Xpixel; 
        	totalDistanceY += listEventsSpatial.get(idx).Ypixel;
        	
        	vectorX[idx] = listEventsSpatial.get(idx).Xpixel;
        	vectorY[idx] = listEventsSpatial.get(idx).Ypixel;
        	
        	totalGyroX += listEventsSpatial.get(idx).GyroX();
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
                        
        return listEventsSpatial;
    }
	
	public ArrayList<MotionEventExtended> ConvertToVectorByDistance(ArrayList<MotionEventExtended> listEvents, double length, double xdpi, double ydpi) {
        int minValue = -9999999;
        int numPoints = NUM_TEMPORAL_SAMPLING_POINTS;
        int vectorLength = numPoints * 2;
        float[] vector = new float[vectorLength];
        String msg;
        ArrayList<MotionEventExtended> listEventsSpatial = new ArrayList<>();               
        
        double[] vectorX = new double[numPoints];
        double[] vectorY = new double[numPoints];
        
        try
        {
        final float increment = (float) length / (numPoints - 1);

        float distanceSoFar = 0;

        float[] pts = new float[listEvents.size() * 2];
        for (int idx = 0; idx < listEvents.size(); idx++)
        {
            pts[idx * 2] = (float) listEvents.get(idx).Xpixel;            
            pts[idx * 2 + 1] = (float) listEvents.get(idx).Ypixel;        
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
        
        listEventsSpatial.add(listEvents.get(0));
        
        int i = 0;
        double newTime;
        MotionEventExtended tempEventSpatial;
        
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
                    index++;
                    
                    lstPointX = nx;
                    lstPointY = ny;                    
                    distanceSoFar = 0;
                    
                    if(i + 1 < listEvents.size()) {
                    	tempEventSpatial = CreateNewEvent(listEvents.get(i + 1), listEvents.get(i), ratio, xdpi, ydpi);	
                    }
                    else {
                    	tempEventSpatial = listEvents.get(i).Clone();                    	
                    }
                                                            
                    listEventsSpatial.add(tempEventSpatial);                                                         
                } else {
                    lstPointX = currentPointX;
                    lstPointY = currentPointY;
                    currentPointX = minValue;
                    currentPointY = minValue;
                    distanceSoFar += distance;
                }
            }

            for (i = index; i < vectorLength; i += 2) {
            	tempEventSpatial = listEvents.get(listEvents.size() - 1).Clone();
            	listEventsSpatial.add(tempEventSpatial);     
            	vector[i] = lstPointX;
                vector[i + 1] = lstPointY;
            }
        } catch (Exception exc) {
            msg = exc.getMessage();
        }        
        
        return listEventsSpatial;
    }
	
	private float[] GetPoints(ArrayList<MotionEventExtended> listEvents) {
		float[] pts = new float[listEvents.size() * 2];
		
		int idxPts = 0;
		for(int idx = 0; idx < listEvents.size(); idx++) {
			pts[idxPts] = (float)listEvents.get(idx).Xpixel;
			idxPts++;
			pts[idxPts] = (float)listEvents.get(idx).Ypixel;
			idxPts++;
		}
		return pts;
	}	
	
	private MotionEventExtended CreateNewEvent(MotionEventExtended eventNext, MotionEventExtended eventCurrent, double ratio, double xdpi, double ydpi) {
		MotionEventExtended tempEvent = eventCurrent.Clone();

		tempEvent.Xpixel = GetSpatialValue(eventNext.Xpixel, eventCurrent.Xpixel, ratio);
		tempEvent.Ypixel = GetSpatialValue(eventNext.Ypixel, eventCurrent.Ypixel, ratio);
		
		tempEvent.Xmm = GetSpatialValue(eventNext.Xmm, eventCurrent.Xmm, ratio);
		tempEvent.Ymm = GetSpatialValue(eventNext.Ymm, eventCurrent.Ymm, ratio);
		
//		tempEvent.Xmm = tempEvent.Xpixel / xdpi * ConstsMeasures.INCH_TO_MM;
//		tempEvent.Ymm = tempEvent.Ypixel / ydpi * ConstsMeasures.INCH_TO_MM;
		
		tempEvent.EventTime = GetSpatialValue(eventNext.EventTime, eventCurrent.EventTime, ratio);
		
		tempEvent.SetAccelerometerX(GetSpatialValue(eventNext.AccelerometerX(), eventCurrent.AccelerometerX(), ratio));
		tempEvent.SetAccelerometerY(GetSpatialValue(eventNext.AccelerometerY(), eventCurrent.AccelerometerY(), ratio));
		tempEvent.SetAccelerometerZ(GetSpatialValue(eventNext.AccelerometerZ(), eventCurrent.AccelerometerZ(), ratio));
		
		tempEvent.SetGyroX(GetSpatialValue(eventNext.GyroX(), eventCurrent.GyroX(), ratio));
		tempEvent.SetGyroY(GetSpatialValue(eventNext.GyroY(), eventCurrent.GyroY(), ratio));
		tempEvent.SetGyroZ(GetSpatialValue(eventNext.GyroZ(), eventCurrent.GyroZ(), ratio));
		
		tempEvent.Pressure = GetSpatialValue(eventNext.Pressure, eventCurrent.Pressure, ratio);
		
		tempEvent.TouchSurface = GetSpatialValue(eventNext.TouchSurface, eventCurrent.TouchSurface, ratio);
		
		tempEvent.VelocityX = GetSpatialValue(eventNext.VelocityX, eventCurrent.VelocityX, ratio);
		tempEvent.VelocityY = GetSpatialValue(eventNext.VelocityY, eventCurrent.VelocityY, ratio);
		tempEvent.Velocity = GetSpatialValue(eventNext.Velocity, eventCurrent.Velocity, ratio);		
		
		tempEvent.Acceleration = GetSpatialValue(eventNext.Acceleration, eventCurrent.Acceleration, ratio);
		
		tempEvent.RadialVelocity = GetSpatialValue(eventNext.RadialVelocity, eventCurrent.RadialVelocity, ratio);
		
		tempEvent.RadialAcceleration = GetSpatialValue(eventNext.RadialAcceleration, eventCurrent.RadialAcceleration, ratio);
		tempEvent.Radius = GetSpatialValue(eventNext.Radius, eventCurrent.Radius, ratio);
		tempEvent.Teta = GetSpatialValue(eventNext.Teta, eventCurrent.Teta, ratio);
		tempEvent.DeltaTeta = GetSpatialValue(eventNext.DeltaTeta, eventCurrent.DeltaTeta, ratio);
		tempEvent.AccumulatedNormalizedArea = GetSpatialValue(eventNext.AccumulatedNormalizedArea, eventCurrent.AccumulatedNormalizedArea, ratio);
		
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
    
    /***********************************************************************************************/
    
    public ArrayList<MotionEventCompact> CenterAndRotate(ArrayList<MotionEventCompact> listEvents) {
    	ArrayList<MotionEventCompact> listEventsProcessed;
    	
    	double[] center = ComputeCentroid(listEvents);
    	
    	float orientation = (float) Math.atan2(listEvents.get(0).Ypixel - center[1], listEvents.get(0).Xpixel - center[0]);

        float adjustment = -orientation;
        int count = mOrientations.length;
        for (int i = 0; i < count; i++) {
            float delta = (float) mOrientations[i] - orientation;
            if (Math.abs(delta) < Math.abs(adjustment)) {
                adjustment = delta;
            }
        }    	       
        
    	listEventsProcessed = Translate(listEvents, -center[0], -center[1]);
    	
    	
    	listEventsProcessed = Rotate(listEventsProcessed, adjustment);
    	
    	return listEventsProcessed;
    }
    
    private double[] ComputeCentroid(ArrayList<MotionEventCompact> listEvents) {
    	double centerX = 0;
        double centerY = 0;
        int count = listEvents.size();
        for (int i = 0; i < count; i++) {
            centerX += listEvents.get(i).Xpixel;            
            centerY += listEvents.get(i).Ypixel;
        }
        double[] center = new double[2];
        center[0] = centerX / count;
        center[1] = centerY / count;

        return center;
    }
    
    private ArrayList<MotionEventCompact> Translate(ArrayList<MotionEventCompact> points, double dx, double dy) {
        int size = points.size();
        for (int i = 0; i < size; i++) {
            points.get(i).Xpixel += dx;
            points.get(i).Ypixel += dy;
        }
        return points;
    }            
    
    private ArrayList<MotionEventCompact> Rotate(ArrayList<MotionEventCompact> listEvents, double angle) {
    	ArrayList<MotionEventCompact> listEventsProcessed = listEvents;    	
    	
    	double cos = Math.cos(angle);
    	double sin = Math.sin(angle);
        int size = listEventsProcessed.size();
        for (int i = 0; i < size; i++) {
        	double x = listEventsProcessed.get(i).Xpixel * cos - listEventsProcessed.get(i).Ypixel * sin;
        	double y = listEventsProcessed.get(i).Xpixel * sin + listEventsProcessed.get(i).Ypixel * cos;
            
            listEventsProcessed.get(i).Xpixel = x;
            listEventsProcessed.get(i).Ypixel = y;            
        }
        return listEventsProcessed;
    }

    public ArrayList<MotionEventExtended> Normalize(ArrayList<MotionEventExtended> listEvents) {        
        double sum = 0;

        int size = listEvents.size();
        for (int i = 0; i < size; i++)
        {
            sum += listEvents.get(i).Xpixel * listEvents.get(i).Xpixel;
            sum += listEvents.get(i).Ypixel * listEvents.get(i).Ypixel;
        }

        float magnitude = (float) Math.sqrt(sum);
        for (int i = 0; i < size; i++)
        {
        	listEvents.get(i).Xnormalized = listEvents.get(i).Xpixel / magnitude;
        	listEvents.get(i).Ynormalized = listEvents.get(i).Ypixel / magnitude;        	          
        }

        return listEvents;
    }    
    
    public IndexBoundary FindInterestPointBoundary(int idxInterestPoint, ArrayList<MotionEventExtended> listEvents) {				
    	int upperBoundary = 0;
		int lowerBoundary = 0;
		
    	for(int idx = idxInterestPoint; idx < listEvents.size(); idx++) {
			if(listEvents.get(idx).EventDensity <= 1) {				
				upperBoundary = (idx - 1);
				break;
			}
		}
		for(int idx = idxInterestPoint - 1; idx >=0; idx--) {
			if(listEvents.get(idx).EventDensity <= 1) {				
				lowerBoundary = (idx + 1);
				break;
			}
		}
		
		/*if(lowerBoundary >= upperBoundary) {
			lowerBoundary = idxInterestPoint - 1;
			upperBoundary = idxInterestPoint + 1;
		}*/
		
		IndexBoundary indexBoundary = new IndexBoundary(lowerBoundary, upperBoundary);
		return indexBoundary;
	}
}