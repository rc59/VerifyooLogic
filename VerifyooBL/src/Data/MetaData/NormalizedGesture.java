package Data.MetaData;

import java.util.ArrayList;

import Logic.Utils.Utils;
import Logic.Utils.DTW.UtilsDTW;

public class NormalizedGesture {
	public ArrayList<NormalizedStroke> ListNormalizedStrokes;
	
	public NormalizedGesture() {
		ListNormalizedStrokes = new ArrayList<>();
	}
	
	public boolean CompareTo(NormalizedGesture gesture) {
		NormalizedStroke stroke1, stroke2;
		
		boolean isValid = true;
		
		if(ListNormalizedStrokes.size() != gesture.ListNormalizedStrokes.size()) {
			return false;
		}
		
		double tempCosine;
		double dtwDistance;
		
		UtilsDTW dtwNormalizedCoords;
		
		for(int idx = 0; idx < gesture.ListNormalizedStrokes.size(); idx++) {
			stroke1 = ListNormalizedStrokes.get(idx);
			stroke2 = gesture.ListNormalizedStrokes.get(idx);
			
			tempCosine = Utils.GetInstance().GetUtilsVectors().MinimumCosineDistanceScore(stroke1.Vector, stroke2.Vector);
			
			dtwNormalizedCoords = new UtilsDTW(stroke1.ListCoords, stroke2.ListCoords);
			dtwDistance = dtwNormalizedCoords.getDistance();
			
			if(tempCosine < 1 || dtwDistance > 0.05) {
				isValid = false;
			}
		}			
			
		return isValid;
	}
}
