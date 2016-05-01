package VerifyooLogic.Statistics.Mgr;

import VerifyooLogic.Statistics.Interfaces.INormMgr;
import VerifyooLogic.Statistics.Interfaces.INormParam;
import VerifyooLogic.Statistics.Objects.NormParam;
import VerifyooLogic.Utils.ConstsParams;
import VerifyooLogic.Utils.UtilsCalc;

import java.util.HashMap;

/**
 * Created by roy on 12/28/2015.
 */
public class NormMgr implements INormMgr {


    HashMap<Integer, HashMap<String, INormParam>> mHashNormParams;


    public NormMgr() {
        init();
    }

    private void init() {
        mHashNormParams = new HashMap<>();

        /****************************** STROKES ******************************/

        addParam(0, UtilsCalc.GetParamName(ConstsParams.StrokeBaseParams.ANGLES, ConstsParams.StrokeExtendedParams.AVERAGE),              11, 50);
        addParam(0, UtilsCalc.GetParamName(ConstsParams.StrokeBaseParams.ANGLES, ConstsParams.StrokeExtendedParams.MIN),                 -115, 54);
        addParam(0, UtilsCalc.GetParamName(ConstsParams.StrokeBaseParams.ANGLES, ConstsParams.StrokeExtendedParams.MAX),                     133, 90);
        addParam(0, UtilsCalc.GetParamName(ConstsParams.StrokeBaseParams.ANGLES, ConstsParams.StrokeExtendedParams.STANDARD_DEVIATION),  76, 38);
        addParam(0, UtilsCalc.GetParamName(ConstsParams.StrokeBaseParams.ANGLES, ConstsParams.StrokeExtendedParams.MEDIAN),               20, 68);
        addParam(0, UtilsCalc.GetParamName(ConstsParams.StrokeBaseParams.ANGLES, ConstsParams.StrokeExtendedParams.QUARTER1),             -66, 16);
        addParam(0, UtilsCalc.GetParamName(ConstsParams.StrokeBaseParams.ANGLES, ConstsParams.StrokeExtendedParams.QUARTER3),             82, 93);
        addParam(0, UtilsCalc.GetParamName(ConstsParams.StrokeBaseParams.ANGLES, ConstsParams.StrokeExtendedParams.INTER_Q3_Q1_RANGE),       148, 92);

        addParam(0, UtilsCalc.GetParamName(ConstsParams.StrokeBaseParams.VELOCITY, ConstsParams.StrokeExtendedParams.AVERAGE), 2100, 650);
        addParam(0, UtilsCalc.GetParamName(ConstsParams.StrokeBaseParams.VELOCITY, ConstsParams.StrokeExtendedParams.MIN),                   790, 737);
        addParam(0, UtilsCalc.GetParamName(ConstsParams.StrokeBaseParams.VELOCITY, ConstsParams.StrokeExtendedParams.MAX),                  3710, 490);
        addParam(0, UtilsCalc.GetParamName(ConstsParams.StrokeBaseParams.VELOCITY, ConstsParams.StrokeExtendedParams.STANDARD_DEVIATION),    850, 160);
        addParam(0, UtilsCalc.GetParamName(ConstsParams.StrokeBaseParams.VELOCITY, ConstsParams.StrokeExtendedParams.MEDIAN),               2117, 617);
        addParam(0, UtilsCalc.GetParamName(ConstsParams.StrokeBaseParams.VELOCITY, ConstsParams.StrokeExtendedParams.QUARTER1),              1286, 622);
        addParam(0, UtilsCalc.GetParamName(ConstsParams.StrokeBaseParams.VELOCITY, ConstsParams.StrokeExtendedParams.QUARTER3),             2734, 750);
        addParam(0, UtilsCalc.GetParamName(ConstsParams.StrokeBaseParams.VELOCITY, ConstsParams.StrokeExtendedParams.INTER_Q3_Q1_RANGE),    1456, 303);

        addParam(0, UtilsCalc.GetParamName(ConstsParams.StrokeBaseParams.ACCELERATION, ConstsParams.StrokeExtendedParams.AVERAGE),                27, 11);
        addParam(0, UtilsCalc.GetParamName(ConstsParams.StrokeBaseParams.ACCELERATION, ConstsParams.StrokeExtendedParams.MIN),                  7, 13.32);
        addParam(0, UtilsCalc.GetParamName(ConstsParams.StrokeBaseParams.ACCELERATION, ConstsParams.StrokeExtendedParams.MAX),                  54, 9.2);
        addParam(0, UtilsCalc.GetParamName(ConstsParams.StrokeBaseParams.ACCELERATION, ConstsParams.StrokeExtendedParams.STANDARD_DEVIATION),   23, 9.3);
        addParam(0, UtilsCalc.GetParamName(ConstsParams.StrokeBaseParams.ACCELERATION, ConstsParams.StrokeExtendedParams.MEDIAN),               26, 8.7);
        addParam(0, UtilsCalc.GetParamName(ConstsParams.StrokeBaseParams.ACCELERATION, ConstsParams.StrokeExtendedParams.QUARTER1),             14, 9.2);
        addParam(0, UtilsCalc.GetParamName(ConstsParams.StrokeBaseParams.ACCELERATION, ConstsParams.StrokeExtendedParams.QUARTER3),             40, 15);
        addParam(0, UtilsCalc.GetParamName(ConstsParams.StrokeBaseParams.ACCELERATION, ConstsParams.StrokeExtendedParams.INTER_Q3_Q1_RANGE),    26, 7);

        addParam(0, UtilsCalc.GetParamName(ConstsParams.StrokeBaseParams.DEACCELERATION, ConstsParams.StrokeExtendedParams.AVERAGE),                -21.5, 5.5);
        addParam(0, UtilsCalc.GetParamName(ConstsParams.StrokeBaseParams.DEACCELERATION, ConstsParams.StrokeExtendedParams.MIN),                  -58, 23.5);
        addParam(0, UtilsCalc.GetParamName(ConstsParams.StrokeBaseParams.DEACCELERATION, ConstsParams.StrokeExtendedParams.MAX),                  -2.93, 1.91);
        addParam(0, UtilsCalc.GetParamName(ConstsParams.StrokeBaseParams.DEACCELERATION, ConstsParams.StrokeExtendedParams.STANDARD_DEVIATION),   17.25, 4.1);
        addParam(0, UtilsCalc.GetParamName(ConstsParams.StrokeBaseParams.DEACCELERATION, ConstsParams.StrokeExtendedParams.MEDIAN),               -20, 4.6);
        addParam(0, UtilsCalc.GetParamName(ConstsParams.StrokeBaseParams.DEACCELERATION, ConstsParams.StrokeExtendedParams.QUARTER1),             -28.75, 8.53);
        addParam(0, UtilsCalc.GetParamName(ConstsParams.StrokeBaseParams.DEACCELERATION, ConstsParams.StrokeExtendedParams.QUARTER3),             -7.85, 10);
        addParam(0, UtilsCalc.GetParamName(ConstsParams.StrokeBaseParams.DEACCELERATION, ConstsParams.StrokeExtendedParams.INTER_Q3_Q1_RANGE),    18, 9);

        addParam(0, UtilsCalc.GetParamName(ConstsParams.StrokeBaseParams.PRESSURE, ConstsParams.StrokeExtendedParams.AVERAGE),              0.5905, 0.2);
        addParam(0, UtilsCalc.GetParamName(ConstsParams.StrokeBaseParams.PRESSURE, ConstsParams.StrokeExtendedParams.MIN),                 0.58125, 0.3);
        addParam(0, UtilsCalc.GetParamName(ConstsParams.StrokeBaseParams.PRESSURE, ConstsParams.StrokeExtendedParams.MAX),                     0.6, 0.2);
        addParam(0, UtilsCalc.GetParamName(ConstsParams.StrokeBaseParams.PRESSURE, ConstsParams.StrokeExtendedParams.STANDARD_DEVIATION),    0.008, 0.2);
        addParam(0, UtilsCalc.GetParamName(ConstsParams.StrokeBaseParams.PRESSURE, ConstsParams.StrokeExtendedParams.QUARTER1),            0.58125, 0.2);
        addParam(0, UtilsCalc.GetParamName(ConstsParams.StrokeBaseParams.PRESSURE, ConstsParams.StrokeExtendedParams.MEDIAN),               0.5875, 0.2);
        addParam(0, UtilsCalc.GetParamName(ConstsParams.StrokeBaseParams.PRESSURE, ConstsParams.StrokeExtendedParams.QUARTER3),                0.6, 0.2);
        addParam(0, UtilsCalc.GetParamName(ConstsParams.StrokeBaseParams.PRESSURE, ConstsParams.StrokeExtendedParams.INTER_Q3_Q1_RANGE),    0.6, 0.2);

        addParam(0, UtilsCalc.GetParamName(ConstsParams.StrokeBaseParams.SURFACE, ConstsParams.StrokeExtendedParams.AVERAGE),              0.33, 0.2);
        addParam(0, UtilsCalc.GetParamName(ConstsParams.StrokeBaseParams.SURFACE, ConstsParams.StrokeExtendedParams.MIN),                 0.33, 0.2);
        addParam(0, UtilsCalc.GetParamName(ConstsParams.StrokeBaseParams.SURFACE, ConstsParams.StrokeExtendedParams.MAX),                     0.33, 0.2);
        addParam(0, UtilsCalc.GetParamName(ConstsParams.StrokeBaseParams.SURFACE, ConstsParams.StrokeExtendedParams.STANDARD_DEVIATION),    0.008, 0.2);
        addParam(0, UtilsCalc.GetParamName(ConstsParams.StrokeBaseParams.SURFACE, ConstsParams.StrokeExtendedParams.QUARTER1),             0.33, 0.2);
        addParam(0, UtilsCalc.GetParamName(ConstsParams.StrokeBaseParams.SURFACE, ConstsParams.StrokeExtendedParams.MEDIAN),              0.33, 0.2);
        addParam(0, UtilsCalc.GetParamName(ConstsParams.StrokeBaseParams.SURFACE, ConstsParams.StrokeExtendedParams.QUARTER3),                0.33, 0.2);
        addParam(0, UtilsCalc.GetParamName(ConstsParams.StrokeBaseParams.SURFACE, ConstsParams.StrokeExtendedParams.INTER_Q3_Q1_RANGE),    0.33, 0.2);

        addParam(0, ConstsParams.Stroke.AVG_X, 290, 30);
        addParam(0, ConstsParams.Stroke.AVG_Y, 400, 30);

        addParam(0, ConstsParams.Stroke.LENGTH, 2000, 500);
        addParam(0, ConstsParams.Stroke.TIME_INTERVAL, 850, 100);
        addParam(0, ConstsParams.Stroke.NUM_EVENTS, 55, 15);

        addParam(0, ConstsParams.Stroke.WIDTH, 490, 200);
        addParam(0, ConstsParams.Stroke.HEIGHT, 530, 300);
        addParam(0, ConstsParams.Stroke.BOUNDING_BOX, 400000, 30000);

        addParam(0,ConstsParams.Stroke.STROKE_OCTAGON_AREA, 400000, 30000);

        addParam(0, ConstsParams.Stroke.DIRECTION_CHANGE_X, 3, 0.5);
        addParam(0, ConstsParams.Stroke.DIRECTION_CHANGE_Y, 5, 1.15);

        addParam(0, ConstsParams.Stroke.MINI_STROKES, 50, 5);

        addParam(0, ConstsParams.Stroke.TIME_BETWEEN_STROKES, 365, 115);
        addParam(0, ConstsParams.Stroke.DISTANCE_BETWEEN_STROKES, 450, 40);

        addParam(0, ConstsParams.Stroke.PRESSURE_CHANGES, 1.5, 0.6);
        addParam(0, ConstsParams.Stroke.SURFACE_CHANGES, 0.5, 0.5);

        /****************************** GESTURES ******************************/

        addParam(0, ConstsParams.Gesture.TIME_INTERVAL, 1500, 350);

        addParam(0, ConstsParams.Gesture.WIDTH, 600, 300);
        addParam(0, ConstsParams.Gesture.HEIGHT, 700, 350);
        addParam(0, ConstsParams.Gesture.BOUNDING_BOX, 600000, 80000);

        addParam(0, ConstsParams.Gesture.STROKE_PAUSE_AVG, 300, 100);
        addParam(0, ConstsParams.Gesture.STROKE_PAUSE_MAX, 400, 150);

        addParam(0, ConstsParams.Gesture.STROKE_DISTANCE_AVG, 500, 200);
        addParam(0, ConstsParams.Gesture.STROKE_DISTANCE_MAX , 600, 250);

        addParam(0, ConstsParams.Gesture.STROKE_BOUNDING_BOX_PROP_AVG, 0.6, 0.2);
        addParam(0, ConstsParams.Gesture.STROKE_BOUNDING_BOX_PROP_MAX, 0.8, 0.3);

        addParam(0, ConstsParams.Gesture.NUM_INTERSECTIONS, 50, 20);
    }

    protected void addParam(int instructionIdx, String name, double avg, double sd) {
        Integer tempInt = new Integer(instructionIdx);

        if (mHashNormParams.containsKey(tempInt)) {

        } else {
            mHashNormParams.put(tempInt, new HashMap<String, INormParam>());
        }

        mHashNormParams.get(tempInt).put(name, (INormParam) new NormParam(name, avg, sd));
    }

    @Override
    public INormParam getNormParam(String name, int instructionIdx) {
        INormParam normParam = mHashNormParams.get(instructionIdx).get(name);

        return normParam;
    }
}
