package Logic.Comparison.Stats.Norms;

import java.util.HashMap;

import Logic.Utils.Utils;

public class SpatialNormContainer {
	public HashMap<String, AccumulatorsContainer> HashNorms;
	
	public SpatialNormContainer() {
		HashNorms = new HashMap<>();
	}
	
	public void AddValue(double value, String instruction, int idxStroke, int idxSpatial) {
		String key = Utils.GetInstance().GetUtilsGeneral().GenerateContainerKey(instruction, idxStroke);
		
		if(HashNorms.containsKey(key)) {
			HashNorms.get(key).AddValue(value, idxSpatial);
		}
		else {
			AccumulatorsContainer tempSpatialNormContainer = new AccumulatorsContainer();
			tempSpatialNormContainer.AddValue(value, idxSpatial);
			HashNorms.put(key, tempSpatialNormContainer);
		}
	}
	
	public double GetMean(String instruction, int idxStroke, int idxSpatial) {
		String key = Utils.GetInstance().GetUtilsGeneral().GenerateContainerKeySafe(instruction, idxStroke, HashNorms);		
		return HashNorms.get(key).GetMean(idxSpatial);
	}
	
	public double GetStd(String instruction, int idxStroke, int idxSpatial) {
		String key = Utils.GetInstance().GetUtilsGeneral().GenerateContainerKeySafe(instruction, idxStroke, HashNorms);		
		return HashNorms.get(key).GetStd(idxSpatial);
	}
	
	public double[] GetListMeans(String instruction, int idxStroke) {
		String key = Utils.GetInstance().GetUtilsGeneral().GenerateContainerKeySafe(instruction, idxStroke, HashNorms);
		AccumulatorsContainer tempSpatialNormContainer = HashNorms.get(key);
		return tempSpatialNormContainer.GetListMeans();
	}
	
	public double[] GetListStds(String instruction, int idxStroke) {
		String key = Utils.GetInstance().GetUtilsGeneral().GenerateContainerKeySafe(instruction, idxStroke, HashNorms);
		AccumulatorsContainer tempSpatialNormContainer = HashNorms.get(key);
		return tempSpatialNormContainer.GetListStds();
	}	
}
