package Data.UserProfile.Raw;

import java.util.ArrayList;

public class Gesture {
	public String Id;
	public ArrayList<Stroke> ListStrokes;
	public String Instruction;		
	
	public Gesture Clone() {
		Gesture clonedGesture = new Gesture();
		
		clonedGesture.Id = Id;		
		clonedGesture.Instruction = Instruction;
		
		clonedGesture.ListStrokes = new ArrayList<>();
		for(int idxStroke = 0; idxStroke < ListStrokes.size(); idxStroke++) {
			clonedGesture.ListStrokes.add(ListStrokes.get(idxStroke).Clone());
		}
		
		return clonedGesture;
	}
	
	public Gesture()
	{
		ListStrokes = new ArrayList<>();
	}
}
