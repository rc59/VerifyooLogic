package VerifyooLogic.UserProfile;

public class MotionEventExtremePoint extends MotionEventCompact {
    public double AdjustedX;
    public double AdjustedY;

    public double Angle;

    public int[] ShapeType;
    public double ExtremePointAngle;

    public MotionEventExtremePoint() {

    }

    public MotionEventExtremePoint(MotionEventCompact originalEvent) {
        Xmm = originalEvent.Xmm;
        Ymm = originalEvent.Ymm;
        EventTime = originalEvent.EventTime;
    }
}