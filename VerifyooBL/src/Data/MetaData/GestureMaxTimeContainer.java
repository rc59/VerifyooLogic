package Data.MetaData;

import java.util.HashMap;

import Data.UserProfile.Extended.GestureExtended;
import Logic.Utils.Utils;

public class GestureMaxTimeContainer {
	protected HashMap<String, Double> mHashGestureTimeIntervals;
	
	public GestureMaxTimeContainer() {
		mHashGestureTimeIntervals = new HashMap<>();
	}
	
	public void AddGesture(GestureExtended gesture) {
		double gestureTimeInterval = Utils.GetInstance().GetUtilsGeneral().GetGestureTotalTime(gesture);
		if(mHashGestureTimeIntervals.containsKey(gesture.Instruction)) {
			double currentTime = mHashGestureTimeIntervals.get(gesture.Instruction);
			if(currentTime < gestureTimeInterval) {
				mHashGestureTimeIntervals.remove(gesture.Instruction);
				mHashGestureTimeIntervals.put(gesture.Instruction, gestureTimeInterval);
			}
		}
		else {
			mHashGestureTimeIntervals.put(gesture.Instruction, gestureTimeInterval);
		}
	}

	public double GetMaxTimeForGesture(String instruction) {
		return mHashGestureTimeIntervals.get(instruction);	
	}
}
