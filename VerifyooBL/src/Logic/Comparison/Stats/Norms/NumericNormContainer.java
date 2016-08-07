package Logic.Comparison.Stats.Norms;

import java.util.HashMap;

import Logic.Utils.UtilsAccumulator;

public class NumericNormContainer {
	public HashMap<String, UtilsAccumulator> HashNorms;
	
	public NumericNormContainer() {
		HashNorms = new HashMap<>();
	}
	
	public void AddValue(double value, String instruction, int idxStroke) {
		String key = GenerateKey(instruction, idxStroke);
		
		if(HashNorms.containsKey(key)) {
			HashNorms.get(key).AddDataValue(value);
		}
		else {
			UtilsAccumulator tempAccumulator = new UtilsAccumulator();
			tempAccumulator.AddDataValue(value);
			HashNorms.put(key, tempAccumulator);
		}
	}
	
	public void AddValue(double value, String instruction) {
		if(HashNorms.containsKey(instruction)) {
			HashNorms.get(instruction).AddDataValue(value);
		}
		else {
			UtilsAccumulator tempAccumulator = new UtilsAccumulator();
			tempAccumulator.AddDataValue(value);
			HashNorms.put(instruction, tempAccumulator);
		}
	}
	
	public double GetMean(String instruction, int idxStroke) {
		String key = GenerateKey(instruction, idxStroke);		
		return HashNorms.get(key).Mean();		
	}
	
	public double GetStd(String instruction, int idxStroke) {
		String key = GenerateKey(instruction, idxStroke);		
		return HashNorms.get(key).Stddev();
	}
	
	public double GetMean(String instruction) {			
		return HashNorms.get(instruction).Mean();		
	}
	
	public double GetStd(String instruction) {		
		return HashNorms.get(instruction).Stddev();
	}
	
	protected String GenerateKey(String instruction, int idxStroke) {
		return String.format("%s-%s", instruction, Integer.toString(idxStroke));
	}
}
