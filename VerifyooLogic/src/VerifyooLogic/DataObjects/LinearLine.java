package VerifyooLogic.DataObjects;

import VerifyooLogic.UserProfile.MotionEventCompact;
/**
 * Created by Rafi on 09/03/2016.
 */
public class LinearLine {
    private double a;
    private double b;
    private double c;
    private double h;
    private double slope;
    private double intercept;
    public LinearLine(){
    }
//p1, p2 define a line aX+bY+c=0.
    public LinearLine(MotionEventCompact p1, MotionEventCompact p2){
        a = (p1.Ymm - p2.Ymm);
        b = (p2.Xmm - p1.Xmm);
        c = (p1.Xmm * p2.Ymm - p2.Xmm * p1.Ymm);
        if(b < 0){
            b = -b; a = -a; c = -c;
        }
        slope = -a / b;
        intercept = -c / b;
        h = Math.sqrt(a*a + b*b);
    }
//two points define a line aX+bY+c=0.
    public LinearLine(double p1X, double p1Y, double p2X, double p2Y){
        a = (p1Y - p2Y);
        b = (p2X - p1X);
        c = p1X * p2Y - p2X * p1Y;
        if(b < 0){
            b = -b; a = -a; c = -c;
        }
        slope = -a / b;
        intercept = -c / b;
        h = Math.sqrt(a*a + b*b);
    }
//a slope and a point define a line aX+bY+c=0.
    public LinearLine(double s, double pX, double pY){
        a = s;
        b = -1;
        c = pY - s * pX;
        slope = s;
        intercept = c;
        b = -b; a = -a; c = -c;
        h = Math.sqrt(a*a + 1);
    }

    public double getPointToLineDistance(MotionEventCompact p){
        return ((a * p.Xmm) + (b * p.Ymm) + c) / h;
    }

    public double getPointToLineDistance(double pX, double pY){
        return (a * pX + b * pY + c) / h;
    }
    public double geta(){return a;};
    public double getb(){return b;};
    public double getc(){return c;};
    public double getSlope(){return slope;};
    public double getIntercept(){return intercept;};
}

