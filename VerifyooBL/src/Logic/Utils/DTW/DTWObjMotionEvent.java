package Logic.Utils.DTW;

import Data.UserProfile.Extended.MotionEventExtended;
import Logic.Utils.Utils;

public class DTWObjMotionEvent implements IDTWObj {

	protected MotionEventExtended mMotionEvent;
	
	public DTWObjMotionEvent(MotionEventExtended motionEvent) {
		mMotionEvent = motionEvent;
	}
	
	public MotionEventExtended GetEvent() {
		return mMotionEvent;
	}
	
	@Override
	public double CompareTo(IDTWObj obj) {
		DTWObjMotionEvent dtwMotionEvent = (DTWObjMotionEvent)obj;
		MotionEventExtended motionEventToCompare = dtwMotionEvent.GetEvent();
		
		double diffPressure = Utils.GetInstance().GetUtilsMath().GetPercentageDiff(mMotionEvent.Pressure, motionEventToCompare.Pressure);
		double diffSurface = Utils.GetInstance().GetUtilsMath().GetPercentageDiff(mMotionEvent.TouchSurface, motionEventToCompare.TouchSurface);
		double diffXNormalized = Utils.GetInstance().GetUtilsMath().GetPercentageDiff(mMotionEvent.Xnormalized, motionEventToCompare.Xnormalized);
		double diffYNormalized = Utils.GetInstance().GetUtilsMath().GetPercentageDiff(mMotionEvent.Ynormalized, motionEventToCompare.Ynormalized);
		
		double result = (diffPressure + diffSurface + diffXNormalized + diffYNormalized) / 4;		
		return result;
	}

}
