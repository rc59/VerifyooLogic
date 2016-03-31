package VerifyooLogic.Statistics.Interfaces;

import VerifyooLogic.UserProfile.CompactGesture;
import VerifyooLogic.UserProfile.Stroke;

/**
 * Created by roy on 12/29/2015.
 */
public interface IStatEngine {
    public double GetProbability(Stroke stroke1, Stroke stroke2);
    public double GetProbability(CompactGesture gesture1, CompactGesture gesture2);
}
