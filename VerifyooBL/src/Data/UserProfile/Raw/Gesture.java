package Data.UserProfile.Raw;

import java.util.ArrayList;

public class Gesture {
	public String Id;
	public ArrayList<Stroke> ListStrokes;
	public String Instruction;		
	
	public Gesture()
	{
		ListStrokes = new ArrayList<>();
	}
}
