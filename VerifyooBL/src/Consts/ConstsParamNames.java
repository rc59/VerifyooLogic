package Consts;

public class ConstsParamNames {	
	public class StrokeSampling
	{
		public static final String VELOCITIES = "SpatialVelocities";
		public static final String ACCELERATIONS = "SpatialAccelerations";
		public static final String RADIAL_VELOCITIES = "SpatialRadialVelocities";
		
		public static final String RADIAL_ACCELERATION = "SpatialRadialAccelerations";
		public static final String TETA = "SpatialTeta";
		public static final String DELTA_TETA = "SpatialDeltaTeta";
		public static final String RADIUS = "SpatialRadius";
		public static final String ACCUMULATED_NORM_AREA = "SpatialAccumulatedNormArea";
	}
	
	public class Stroke
	{
		public static final String MINIMUM_COSINE_DISTANCE = "StrokeMinimumCosineDistance";		
		
		public static final String STROKE_NUM_EVENTS = "StrokeNumEvents";
		public static final String STROKE_LENGTH = "StrokeLength";				
		public static final String STROKE_TIME_INTERVAL = "StrokeTimeInterval";
		public static final String STROKE_TRANSITION_TIME = "StrokeTransitionTime";
		
		public static final String STROKE_SUM_X = "StrokeSumX";
		public static final String STROKE_SUM_Y = "StrokeSumY";
		
		public static final String STROKE_TOTAL_AREA = "StrokeTotalArea";
		public static final String STROKE_TOTAL_AREA_MINX_MINY = "StrokeTotalAreaMinXMinY";
		public static final String STROKE_TOTAL_AREA_MAXX_MAXY = "StrokeTotalAreaMaxXMaxY";
		public static final String STROKE_TOTAL_AREA_MINX_MAXY = "StrokeTotalAreaMinXMaxY";
		public static final String STROKE_TOTAL_AREA_MAXX_MINY = "StrokeTotalAreaMaxXMinY";
		
		public static final String STROKE_AVG_DENSITY = "StrokeIntPointDensity"; //"StrokeAvgDensity";
		public static final String STROKE_INT_POINT_LOCATION = "StrokeIntPointLocation";
		public static final String STROKE_INT_POINT_AVG_VELOCITY = "StrokeIntPointAvgVelocity";
		public static final String STROKE_INT_POINT_INTENSITY = "StrokeIntPointIntensity";
		
		public static final String STROKE_INT_POINT_START_INTENSITY = "StrokeIntPointStartIntensity";
		public static final String STROKE_INT_POINT_START_AVG_VELOCITY = "StrokeIntPointStartAvgVelocity";
		public static final String STROKE_INT_POINT_END_INTENSITY = "StrokeIntPointEndIntensity";
		public static final String STROKE_INT_POINT_END_AVG_VELOCITY = "StrokeIntPointEndAvgVelocity";
		
		public static final String STROKE_INT_POINT_COUNT_MAJOR = "StrokeIntPointCountMajor";
		public static final String STROKE_INT_POINT_COUNT_MINOR = "StrokeIntPointCountMinor";
		
		public static final String STROKE_INT_POINT_VELOCITY = "StrokeIntPointVelocity";
				
		public static final String STROKE_INT_POINT_PRESSURE = "StrokeIntPointPressure";
		public static final String STROKE_INT_POINT_SURFACE = "StrokeIntPointSurface";
		public static final String STROKE_INT_POINT_DENSITY = "StrokeIntPointDensity";
		
		
		
		public static final String STROKE_AVERAGE_VELOCITY = "StrokeAvgVelocity";
		public static final String STROKE_MAX_VELOCITY = "StrokeMaxVelocity";
		public static final String STROKE_MID_VELOCITY = "StrokMidVelocity";		
		
		public static final String STROKE_AVERAGE_ACCELERATION = "StrokeAvgAcceleration";
		public static final String STROKE_MAX_ACCELERATION = "StrokeMaxAcceleration";

		public static final String STROKE_MIDDLE_PRESSURE = "StrokeMiddlePressure";
		public static final String STROKE_MIDDLE_SURFACE = "StrokeMiddleSurface";
		
		public static final String STROKE_SPATIAL_SAMPLING = "StrokeSpatialSampling";
		public static final String STROKE_TEMPORAL_SAMPLING = "StrokeTemporalSampling";
		
		public static final String STROKE_MAX_RADIAL_VELOCITY = "StrokeMaxRadialVelocity";
		public static final String STROKE_MAX_RADIAL_ACCELERATION = "StrokeMaxRadialAcceleration";
		
		public static final String STROKE_INTEREST_POINT_PARAM = "StrokeInterestPointParam";
		
		public class InterestPoints
		{
			public static final String STROKE_MAX_INTEREST_POINT_INDEX = "StrokeMaxInterestPointIndex";
			public static final String STROKE_MAX_INTEREST_POINT_DENSITY = "StrokeMaxInterestPointDensity";
			public static final String STROKE_MAX_INTEREST_POINT_LOCATION = "StrokeMaxInterestPointLocation";
			public static final String STROKE_MAX_INTEREST_POINT_PRESSURE = "StrokeMaxInterestPointPressure";
			public static final String STROKE_MAX_INTEREST_POINT_SURFACE = "StrokeMaxInterestPointSurface";
			public static final String STROKE_MAX_INTEREST_POINT_VELOCITY = "StrokeMaxInterestPointVelocity";
			public static final String STROKE_MAX_INTEREST_POINT_ACCELERATION = "StrokeMaxInterestPointAcceleration";
		}
	}
	
	public class Gesture
	{
		public static final String GESTURE_DELAY_TIME = "GestureDelayTime";

		public static final String GESTURE_NUM_EVENTS = "NumEvents";
		public static final String GESTURE_AVERAGE_VELOCITY = "GestureAvgVelocity";
		public static final String GESTURE_MAX_VELOCITY = "GestureMaxVelocity";
		public static final String GESTURE_LENGTH = "GestureLength";		
		public static final String GESTURE_TOTAL_AREA = "GestureTotalArea";
		public static final String GESTURE_TOTAL_AREA_MINX_MINY = "GestureTotalAreaMinXMinY";		
		public static final String GESTURE_TOTAL_STROKES_TIME_INTERVAL = "GestureTotalStrokesTimeInterval";
		public static final String GESTURE_TOTAL_TIME_INTERVAL = "GestureTotalTimeInterval";
		public static final String GESTURE_ACCUMULATED_DISTANCE_BY_TIME = "GestureAccumulatedDistanceByTime";
		public static final String GESTURE_TOTAL_STROKE_AREA= "GestureTotalStrokeArea";	
		public static final String GESTURE_TOTAL_STROKE_AREA_MINX_MINY = "GestureTotalStrokeAreaMinXMinY";

		public static final String GESTURE_MIDDLE_PRESSURE = "GestureMiddlePressure";
		public static final String GESTURE_MIDDLE_SURFACE = "GestureMiddleSurface";				

		public static final String GESTURE_AVG_START_DIRECTION = "GestureAvgStartDirection";
		public static final String GESTURE_AVG_MAX_DIRECTION = "GestureAvgMaxDirection";
		public static final String GESTURE_AVG_END_DIRECTION = "GestureAvgEndDirection";
		public static final String GESTURE_MAX_VELOCITY_DIRECTION = "GestureDirectionAtFirstStrokeMaxVelocity";
		public static final String GESTURE_MID_OF_FIRST_STROKE_VELOCITY = "MidOfFirstStrokeVelocity";
		public static final String GESTURE_MID_OF_FIRST_STROKE_ANGLE = "MidOfFirstStrokeAngle";		
		
		public static final String GESTURE_AVG_ACCELERATION = "GestureAverageAcceleration";
		public static final String GESTURE_MAX_ACCELERATION = "GestureMaxAcceleration";
		
		public static final String GESTURE_AVG_START_ACCELERATION = "GestureAverageStartAcceleration";
		public static final String GESTURE_VELOCITY_PEAK = "GestureVelocityPeak";
		public static final String GESTURE_VELOCITY_PEAK_INTERVAL_PERCENTAGE = "GestureVelocityPeakIntervalPercentage";
		public static final String GESTURE_ACCELERATION_PEAK = "GestureAccelerationPeak";
		public static final String GESTURE_ACCELERATION_PEAK_INTERVAL_PERCENTAGE = "GestureAccelerationPeakIntervalPercentage";
		
		public static final String GESTURE_ACCUMULATED_LENGTH_R2 = "GestureAccumulatedLengthRSqr";
		public static final String GESTURE_ACCUMULATED_LENGTH_SLOPE = "GestureAccumulatedLengthSlope";	
		
		
	}	
}