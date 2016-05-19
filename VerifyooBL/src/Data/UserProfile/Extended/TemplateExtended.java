package Data.UserProfile.Extended;

import java.util.ArrayList;
import java.util.HashMap;

import Data.UserProfile.Raw.Gesture;
import Data.UserProfile.Raw.Template;
import Logic.Comparison.Stats.FeatureMeanData;

public class TemplateExtended extends Template {	
	public ArrayList<GestureExtended> ListGestureExtended;
	
	private HashMap<String, FeatureMeanData> mHashFeatureMeans;
	
	public TemplateExtended(Template template) {		
		InitTemplateExtended(template);
	}

	private void InitTemplateExtended(Template template) {
		mHashFeatureMeans = new HashMap<>();
		ListGestureExtended = new ArrayList<GestureExtended>();
		ListGestures = template.ListGestures;
				
		Gesture tempGesture;
		GestureExtended tempGestureExtended;
		
		for(int idxGesture = 0; idxGesture < template.ListGestures.size(); idxGesture++) {
			tempGesture = template.ListGestures.get(idxGesture);
			tempGestureExtended = new GestureExtended(tempGesture, mHashFeatureMeans, idxGesture);
			
			ListGestureExtended.add(tempGestureExtended);
		}		
	}
}
