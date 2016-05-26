package Data.UserProfile.Raw;

import java.util.ArrayList;

public class Stroke {
	public String Id;
	public ArrayList<MotionEventCompact> ListEvents;
	public double Length;
	public double Xdpi;
	public double Ydpi;
	
	public Stroke()
	{
		ListEvents = new ArrayList<>();
	}
}
