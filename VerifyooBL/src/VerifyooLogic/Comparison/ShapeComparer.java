package VerifyooLogic.Comparison;

import java.util.ArrayList;
import java.util.Arrays;

import VerifyooLogic.UserProfile.MotionEventCompact;
import VerifyooLogic.UserProfile.Stroke;
import VerifyooLogic.Utils.Consts;

public class ShapeComparer {

    private double[] _orientations = new double[] {
        0, (Math.PI / 4), (Math.PI / 2), (Math.PI * 3 / 4),
        Math.PI, -0, (-Math.PI / 4), (-Math.PI / 2),
                (-Math.PI * 3 / 4), -Math.PI
    };

    public double getScore2(Stroke stroke1, Stroke stroke2)
    {
        float[] vector1 = prepareData(stroke1.ListEvents, (float) stroke1.Length);
        float[] vector2 = prepareData(stroke2.ListEvents, (float) stroke2.Length);

        double distance = squaredEuclideanDistance(vector1, vector2);

        return distance;
    }

    public double getScore(Stroke stroke1, Stroke stroke2) {
        float[] vector1 = prepareData(stroke1.ListEvents, (float) stroke1.Length);
        float[] vector2 = prepareData(stroke2.ListEvents, (float) stroke2.Length);

        double distance = minimumCosineDistance(vector1, vector2, 2);

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

    public float[] prepareData(ArrayList<MotionEventCompact> eventsList, float length) {

        //float[] pts1 = spatialSampling(eventsList, length);

        float[] pts = convertToVector(eventsList, length);
        float[] center = computeCentroid(pts);
        float orientation = (float) Math.atan2(pts[1] - center[1], pts[0] - center[0]);

        float adjustment = -orientation;
        int count = _orientations.length;
        for (int i = 0; i < count; i++) {
            float delta = (float) _orientations[i] - orientation;
            if (Math.abs(delta) < Math.abs(adjustment)) {
                adjustment = delta;
            }
        }

        translate(pts, -center[0], -center[1]);
        rotate(pts, adjustment);

        pts = normalize(pts);

        return pts;
    }

    private float[] normalize(float[] vector) {
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

    private float[] rotate(float[] points, float angle) {
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

    private float[] translate(float[] points, float dx, float dy) {
        int size = points.length;
        for (int i = 0; i < size; i += 2) {
            points[i] += dx;
            points[i + 1] += dy;
        }
        return points;
    }

    private float[] computeCentroid(float[] points) {
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

    private static final float SCALING_THRESHOLD = 0.26f;
    private static final float NONUNIFORM_SCALE = (float) Math.sqrt(2);

    private static void plot(float x, float y, float[] sample, int sampleSize) {
        x = x < 0 ? 0 : x;
        y = y < 0 ? 0 : y;
        int xFloor = (int) Math.floor(x);
        int xCeiling = (int) Math.ceil(x);
        int yFloor = (int) Math.floor(y);
        int yCeiling = (int) Math.ceil(y);

        // if it's an integer
        if (x == xFloor && y == yFloor) {
            int index = yCeiling * sampleSize + xCeiling;
            if (sample[index] < 1){
                sample[index] = 1;
            }
        } else {
            final double xFloorSq = Math.pow(xFloor - x, 2);
            final double yFloorSq = Math.pow(yFloor - y, 2);
            final double xCeilingSq = Math.pow(xCeiling - x, 2);
            final double yCeilingSq = Math.pow(yCeiling - y, 2);
            float topLeft = (float) Math.sqrt(xFloorSq + yFloorSq);
            float topRight = (float) Math.sqrt(xCeilingSq + yFloorSq);
            float btmLeft = (float) Math.sqrt(xFloorSq + yCeilingSq);
            float btmRight = (float) Math.sqrt(xCeilingSq + yCeilingSq);
            float sum = topLeft + topRight + btmLeft + btmRight;

            float value = topLeft / sum;
            int index = yFloor * sampleSize + xFloor;
            if (value > sample[index]){
                sample[index] = value;
            }

            value = topRight / sum;
            index = yFloor * sampleSize + xCeiling;
            if (value > sample[index]){
                sample[index] = value;
            }

            value = btmLeft / sum;
            index = yCeiling * sampleSize + xFloor;
            if (value > sample[index]){
                sample[index] = value;
            }

            value = btmRight / sum;
            index = yCeiling * sampleSize + xCeiling;
            if (value > sample[index]){
                sample[index] = value;
            }
        }
    }

    public static float[] temporalSampling(ArrayList<MotionEventCompact> eventsList, float length) {
        int numPoints = Consts.NUM_TEMPORAL_SAMPLING_POINTS;
        final float increment = length / (numPoints - 1);
        int vectorLength = numPoints * 2;
        float[] vector = new float[vectorLength];
        float distanceSoFar = 0;
        //float[] pts = stroke.points;


        float[] pts = new float[eventsList.size() * 2];
        for (int idx = 0; idx < eventsList.size(); idx++)
        {
            pts[idx * 2] = (float) eventsList.get(idx).Xpixel;
            pts[idx * 2 + 1] = (float) eventsList.get(idx).Ypixel;
        }

        float lstPointX = pts[0];
        float lstPointY = pts[1];
        int index = 0;
        float currentPointX = Float.MIN_VALUE;
        float currentPointY = Float.MIN_VALUE;
        vector[index] = lstPointX;
        index++;
        vector[index] = lstPointY;
        index++;
        int i = 0;
        int count = pts.length / 2;
        while (i < count) {
            if (currentPointX == Float.MIN_VALUE) {
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
            } else {
                lstPointX = currentPointX;
                lstPointY = currentPointY;
                currentPointX = Float.MIN_VALUE;
                currentPointY = Float.MIN_VALUE;
                distanceSoFar += distance;
            }
        }

        for (i = index; i < vectorLength; i += 2) {
            vector[i] = lstPointX;
            vector[i + 1] = lstPointY;
        }
        return vector;
    }

    private float[] convertToVector(ArrayList<MotionEventCompact> eventsList, double length) {
        int minValue = -9999999;
        int numPoints = Consts.NUM_TEMPORAL_SAMPLING_POINTS;
        int vectorLength = numPoints * 2;
        float[] vector = new float[vectorLength];

        try
        {
        final float increment = (float) length / (numPoints - 1);

        float[] vectorX = new float[vectorLength / 2];
        float[] vectorY = new float[vectorLength / 2];

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

        return vector;
    }

    private float minimumCosineDistance(float[] vector1, float[] vector2, int numOrientations) {
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

    private float squaredEuclideanDistance(float[] vector1, float[] vector2) {
        float squaredDistance = 0;
        int size = vector1.length;
        for (int i = 0; i < size; i++) {
            float difference = vector1[i] - vector2[i];
            squaredDistance += difference * difference;
        }
        return squaredDistance / size;
    }
}