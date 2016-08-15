package Data.MetaData;

import java.util.ArrayList;
import java.util.HashMap;

public class NormalizedGestureContainer {
	public HashMap<String, ArrayList<NormalizedGesture>> HashGestures;
	
	public NormalizedGestureContainer() {
		HashGestures = new HashMap<>();
	}
	
	public void AddGesture(String instruction, NormalizedGesture normGesture) {
		String key = instruction + normGesture.ListNormalizedStrokes.size();
		
		if(HashGestures.containsKey(key)) {
			HashGestures.get(key).add(normGesture);
		}
		else {
			ArrayList<NormalizedGesture> list = new ArrayList<>();
			list.add(normGesture);
			HashGestures.put(key, list);
		}
	}
	
	public boolean CheckGesture(NormalizedGesture normGesture, String instruction) {
		String key = instruction + normGesture.ListNormalizedStrokes.size();
		
		boolean isValid = false;
		
		if(HashGestures.containsKey(key)) {			
			ArrayList<NormalizedGesture> list = HashGestures.get(key);
			
			for(int idx = 0; idx < list.size(); idx++) {
				if(list.get(idx).CompareTo(normGesture)) {
					isValid = true;
					break;
				}
			}
		}		
		
		return isValid;
		
	}
}
