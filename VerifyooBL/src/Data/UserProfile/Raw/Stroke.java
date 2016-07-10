package Data.UserProfile.Raw;

import java.util.ArrayList;

public class Stroke {
	public String Id;
	public ArrayList<MotionEventCompact> ListEvents;
	public double Length;
	public double Xdpi;
	public double Ydpi;
	
	public Stroke Clone() {
		Stroke clonedStroke = new Stroke();
					
		clonedStroke.Id = Id;				
		clonedStroke.Length = Length;
		clonedStroke.Xdpi = Xdpi;
		clonedStroke.Ydpi = Ydpi;
		
		clonedStroke.ListEvents = new ArrayList<>();
		for(int idxEvent = 0; idxEvent < ListEvents.size(); idxEvent++) {
			clonedStroke.ListEvents.add(ListEvents.get(idxEvent).Clone());
		}
		
		return clonedStroke;
	}
	
	public Stroke()
	{
		ListEvents = new ArrayList<>();
	}
}
