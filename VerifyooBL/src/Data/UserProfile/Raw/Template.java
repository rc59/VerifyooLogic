package Data.UserProfile.Raw;

import java.util.ArrayList;
import java.util.HashMap;

import Data.UserProfile.Extended.StrokeExtended;
import Logic.Comparison.Stats.FeatureMeanData;
import Logic.Comparison.Stats.StatEngine;
import Logic.Comparison.Stats.Interfaces.IStatEngine;

public class Template {
	public ArrayList<Gesture> ListGestures;
	public String Name;
	public String ModelName;
	
	protected IStatEngine mStatEngine;
	
	public Template()
	{
		ListGestures = new ArrayList<>();
		mStatEngine = StatEngine.GetInstance();		
	}
}