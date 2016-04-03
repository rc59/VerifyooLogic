package VerifyooLogic.Comparison;

import VerifyooLogic.UserProfile.CompactGesture;
import VerifyooLogic.UserProfile.Template;

public class TemplateComparer {
	public double Compare(Template templateStored, Template templateAuth) {
		double score = 0;
		
		GestureComparer gestureComparer;
		CompactGesture tempGestureStored;
		CompactGesture tempGestureAuth;
		
		if(templateStored.ListGestures.size() != templateAuth.ListGestures.size()) {
			return score;
		}
		else {
			for(int idx = 0; idx < templateStored.ListGestures.size(); idx++) {
				tempGestureAuth = templateAuth.ListGestures.get(idx);
				tempGestureStored = templateStored.ListGestures.get(idx);
				
				gestureComparer = new GestureComparer();
				score += gestureComparer.Compare(tempGestureStored, tempGestureAuth);
			}
			
			score = score / templateStored.ListGestures.size();
		}
		return score;		
	}
}
