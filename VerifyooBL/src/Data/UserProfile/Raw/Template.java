package Data.UserProfile.Raw;

import java.util.ArrayList;
import java.util.HashMap;

import Data.UserProfile.Extended.StrokeExtended;
import Logic.Comparison.Stats.FeatureMeanData;
import Logic.Comparison.Stats.StatEngine;
import Logic.Comparison.Stats.Interfaces.IFeatureMeanData;
import Logic.Comparison.Stats.Interfaces.IStatEngine;
import Logic.Utils.UtilsAccumulator;

public class Template {
	public String Id;
	public ArrayList<Gesture> ListGestures;
	public String Name;
	public String ModelName;
	
	
	protected HashMap<String, IFeatureMeanData> mHashFeatureMeansInit;

	public Template Clone() {
		Template clonedTemplate = new  Template();
		
		clonedTemplate.Id = Id;			
		clonedTemplate.Name = Name;		
		clonedTemplate.ModelName = ModelName;
		
		clonedTemplate.ListGestures = new ArrayList<>();
		for(int idxGesture = 0; idxGesture < ListGestures.size(); idxGesture++) {
			clonedTemplate.ListGestures.add(ListGestures.get(idxGesture).Clone());
		}
		
		return clonedTemplate;
	}
	
	public Template() {
		ListGestures = new ArrayList<>();
	}
	
	public void InitHashMap(HashMap<String, IFeatureMeanData> hashFeatureMeans) {
		mHashFeatureMeansInit = hashFeatureMeans;
	}
	
	public HashMap<String, IFeatureMeanData> GetHashMap() {
		return mHashFeatureMeansInit;
	}
}