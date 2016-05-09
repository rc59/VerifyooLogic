package Data.UserProfile.Extended;

import java.util.ArrayList;

import Data.UserProfile.Raw.Gesture;
import Data.UserProfile.Raw.Template;

public class TemplateExtended extends Template {	
	public ArrayList<GestureExtended> ListGestureExtended;
	
	public TemplateExtended(Template template) {
		InitTemplateExtended(template);
	}

	private void InitTemplateExtended(Template template) {
		ListGestureExtended = new ArrayList<>();
		ListGestures = template.ListGestures;
				
		Gesture tempGesture;
		GestureExtended tempGestureExtended;
		
		for(int idxGesture = 0; idxGesture < template.ListGestures.size(); idxGesture++) {
			tempGesture = template.ListGestures.get(idxGesture);
			tempGestureExtended = new GestureExtended(tempGesture);
			
			ListGestureExtended.add(tempGestureExtended);
		}		
	}
}
