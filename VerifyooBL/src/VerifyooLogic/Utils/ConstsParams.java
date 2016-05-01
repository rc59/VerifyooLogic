package VerifyooLogic.Utils;

/**
 * Created by roy on 1/5/2016.
 */
public class ConstsParams {

    public class StrokeBaseParams
    {
        public static final String VELOCITY = "StrokeVelocity";
        public static final String ACCELERATION = "StrokeAcceleration";
        public static final String DEACCELERATION = "StrokeDeAcceleration";
        public static final String ANGLES = "StrokeAngles";
        public static final String PRESSURE = "StrokePressure";
        public static final String SURFACE = "StrokeSurface";
    }

    public class StrokeExtendedParams
    {
        public static final String AVERAGE = "Average";
        public static final String MAX = "Max";
        public static final String MIN = "Min";
        public static final String STANDARD_DEVIATION = "StandardDeviation";
        public static final String MEDIAN = "Median";
        public static final String QUARTER1 = "Quarter1";
        public static final String QUARTER3 = "Quarter3";
        public static final String INTER_Q3_Q1_RANGE = "InterQ3Q1Range";
    }

    public class Stroke {
        public static final String AVG_X = "StrokeAvgX";
        public static final String AVG_Y = "StrokeAvgY";

        public static final String STROKE_OCTAGON_AREA = "StrokeOctagonArea";

        public static final String LENGTH = "StrokeLength";
        public static final String TIME_INTERVAL = "StrokeTimeInterval";
        public static final String NUM_EVENTS = "StrokeNumEvents";

        public static final String WIDTH = "StrokeWidth";
        public static final String HEIGHT = "StrokeHeight";
        public static final String BOUNDING_BOX = "StrokeBoundingBox";

        public static final String DIRECTION_CHANGE_X = "DirectionChangeX";
        public static final String DIRECTION_CHANGE_Y = "DirectionChangeY";

        public static final String STROKES_PAUSES = "StrokePauses";
        public static final String MINI_STROKES = "MiniStrokes";
        public static final String TIME_BETWEEN_STROKES = "TimeBetweenStrokes";
        public static final String DISTANCE_BETWEEN_STROKES = "DistanceBetweenStrokes";

        public static final String PRESSURE_CHANGES = "PressureChanges";
        public static final String SURFACE_CHANGES = "SurfaceChanges";
    }

    public class Gesture {
        public static final String TIME_INTERVAL = "GestureTimeInterval";

        public static final String WIDTH = "GestureWidth";
        public static final String HEIGHT = "GestureHeight";
        public static final String BOUNDING_BOX = "GestureBoundingBox";

        public static final String STROKE_PAUSE_AVG = "StrokePauseAvg";
        public static final String STROKE_PAUSE_MAX = "StrokePauseMax";

        public static final String STROKE_DISTANCE_AVG = "StrokeDistanceAvg";
        public static final String STROKE_DISTANCE_MAX = "StrokeDistanceMax";

        public static final String STROKE_BOUNDING_BOX_PROP_AVG = "StrokeBoundingBoxPropAvg";
        public static final String STROKE_BOUNDING_BOX_PROP_MAX = "StrokeBoundingBoxPropMax";

        public static final String NUM_INTERSECTIONS = "NumIntersections";
    }

}
