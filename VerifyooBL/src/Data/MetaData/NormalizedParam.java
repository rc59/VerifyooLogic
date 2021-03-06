package Data.MetaData;

import Logic.Utils.Utils;

public class NormalizedParam {
	public String Name;
	public boolean IsValid;
	public double NormalizedScore;
	public double Weight;
	public double WeightInGesture;
	
	public NormalizedParam(String name, double value, double weight, double weightInGesture) {
		Name = name;
		double threashold = Utils.GetInstance().GetUtilsGeneral().GetThreashold(name);
		threashold = threashold + 0.1;
		if(threashold > 1) {
			threashold = 1;
		}
		threashold = 1;
		
		IsValid = false;
		if(value >= threashold) {
			IsValid = true;
		}
		
		if(value > threashold) {
			value = threashold;
		}
		
		NormalizedScore = value / threashold;
		WeightInGesture = Utils.GetInstance().GetUtilsGeneral().GetWeight(name);
//		NormalizedScore = NormalizedScore * NormalizedScore;
		Weight = weight;
//		Weight = Utils.GetInstance().GetUtilsGeneral().GetWeight(name);
	}
}
