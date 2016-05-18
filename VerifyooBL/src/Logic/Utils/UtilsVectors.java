package Logic.Utils;

public class UtilsVectors {
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
}
