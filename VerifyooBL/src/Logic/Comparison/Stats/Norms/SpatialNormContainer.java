package Logic.Comparison.Stats.Norms;

import java.util.HashMap;

public class SpatialNormContainer {
	public HashMap<String, AccumulatorsContainer> HashNorms;
	
	public SpatialNormContainer() {
		HashNorms = new HashMap<>();
	}
	
	public void AddValue(double value, String instruction, int idxStroke, int idxSpatial) {
		String key = GenerateKey(instruction, idxStroke);
		
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
		String key = GenerateKey(instruction, idxStroke);		
		return HashNorms.get(key).GetMean(idxSpatial);		
	}
	
	public double GetStd(String instruction, int idxStroke, int idxSpatial) {
		String key = GenerateKey(instruction, idxStroke);		
		return HashNorms.get(key).GetStd(idxSpatial);
	}
	
	public double[] GetListMeans(String instruction, int idxStroke) {
		String key = GenerateKey(instruction, idxStroke);
		AccumulatorsContainer tempSpatialNormContainer = HashNorms.get(key);
		return tempSpatialNormContainer.GetListMeans();
	}
	
	public double[] GetListStds(String instruction, int idxStroke) {
		String key = GenerateKey(instruction, idxStroke);
		AccumulatorsContainer tempSpatialNormContainer = HashNorms.get(key);
		return tempSpatialNormContainer.GetListStds();
	}
	
	protected String GenerateKey(String instruction, int idxStroke) {
		return String.format("%s-%s", instruction, Integer.toString(idxStroke));
	}
	
//	public String ToString() {
//		JSONSerializer serializer = new JSONSerializer();
//		String strObj = serializer.deepSerialize(mHashNorms);
//		return strObj;
//	}
//	
//	public void FromString(String inputStr) {
//		JSONDeserializer<HashMap<String, SpatialNormContainer>> deserializer = new JSONDeserializer<HashMap<String, SpatialNormContainer>>();
//		mHashNorms = deserializer.deserialize(inputStr);		
//	}
}
