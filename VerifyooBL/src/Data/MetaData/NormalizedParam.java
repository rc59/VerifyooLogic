package Data.MetaData;

import Logic.Utils.Utils;

public class NormalizedParam {
	public String Name;
	public boolean IsValid;
	public double NormalizedScore;
	public double Weight;
	
	public NormalizedParam(String name, double value, double weight) {
		double threashold = Utils.GetInstance().GetUtilsGeneral().GetThreashold(name);
		threashold = threashold + 0.1;
		
		IsValid = false;
		if(value >= threashold) {
			IsValid = true;
		}
		
		if(value > threashold) {
			value = threashold;
		}
		
		NormalizedScore = value / threashold;
//		NormalizedScore = NormalizedScore * NormalizedScore;
//		Weight = weight;
		Weight = Utils.GetInstance().GetUtilsGeneral().GetWeight(name);
	}
}
