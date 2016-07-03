package Data.UserProfile.Extended;

import java.util.ArrayList;
import java.util.HashMap;

import Data.UserProfile.Raw.Gesture;
import Data.UserProfile.Raw.Template;
import Logic.Comparison.Stats.Interfaces.IFeatureMeanData;

public class TemplateExtended extends Template {	
	public ArrayList<GestureExtended> ListGestureExtended;
	
	protected HashMap<String, ArrayList<GestureExtended>> mHashGesturesByInstruction;
	
	private HashMap<String, IFeatureMeanData> mHashFeatureMeans;
	
	public TemplateExtended(Template template) {		
		InitTemplateExtended(template);
	}

	private void InitTemplateExtended(Template template) {
		Id = template.Id;
		mHashFeatureMeans = new HashMap<>();
		ListGestureExtended = new ArrayList<GestureExtended>();
		ListGestures = template.ListGestures;
				
		Gesture tempGesture;
		GestureExtended tempGestureExtended;
		
		mHashGesturesByInstruction = new HashMap<>();
		ArrayList<GestureExtended> tempListGestures;
		
		for(int idxGesture = 0; idxGesture < template.ListGestures.size(); idxGesture++) {
			tempGesture = template.ListGestures.get(idxGesture);
			tempGestureExtended = new GestureExtended(tempGesture, mHashFeatureMeans, idxGesture);
			
			ListGestureExtended.add(tempGestureExtended);
			
			if(!mHashGesturesByInstruction.containsKey(tempGestureExtended.Instruction)) {
				tempListGestures = new ArrayList<>();
				tempListGestures.add(tempGestureExtended);
				mHashGesturesByInstruction.put(tempGestureExtended.Instruction, tempListGestures);
			}
			else {
				tempListGestures = mHashGesturesByInstruction.get(tempGestureExtended.Instruction);
				tempListGestures.add(tempGestureExtended);
			}
		}
	}
	
	public HashMap<String, ArrayList<GestureExtended>> GetHashGesturesByInstruction() {
		return mHashGesturesByInstruction;
	}
}
