package Logic.Comparison.Stats.Norms;

import java.util.HashMap;

public class NormContainerMgr {
	public HashMap<String, SpatialNormContainer> HashMapSpatialNormsMeansDistance;
	public HashMap<String, SpatialNormContainer> HashMapSpatialNormsSdsDistance;
	
	public HashMap<String, SpatialNormContainer> HashMapSpatialNormsMeansTime;
	public HashMap<String, SpatialNormContainer> HashMapSpatialNormsSdsTime;
	
	public HashMap<String, NumericNormContainer> HashMapNumericNormsMeans;
	public HashMap<String, NumericNormContainer> HashMapNumericNormsSds;
	
	public NormContainerMgr() {
		HashMapSpatialNormsMeansDistance = new HashMap<>();
		HashMapSpatialNormsSdsDistance = new HashMap<>();
		
		HashMapSpatialNormsMeansTime = new HashMap<>();
		HashMapSpatialNormsSdsTime = new HashMap<>();
		
		HashMapNumericNormsMeans = new HashMap<>();
		HashMapNumericNormsSds = new HashMap<>();
	}
	
	public double GetSpatialPopMeanDistance(String instruction, String param, int idxStroke, int idxSpatial) {
		instruction = CheckInstruction(instruction);
		return HashMapSpatialNormsMeansDistance.get(param).GetMean(instruction, idxStroke, idxSpatial);
	}
	
	public double GetSpatialPopSdDistance(String instruction, String param, int idxStroke, int idxSpatial) {
		instruction = CheckInstruction(instruction);
		return HashMapSpatialNormsMeansDistance.get(param).GetStd(instruction, idxStroke, idxSpatial);	
	}
	
	public double GetSpatialInternalSdDistance(String instruction, String param, int idxStroke, int idxSpatial) {
		instruction = CheckInstruction(instruction);
		return HashMapSpatialNormsSdsDistance.get(param).GetMean(instruction, idxStroke, idxSpatial);
	}
	
	public double GetSpatialPopMeanTime(String instruction, String param, int idxStroke, int idxSpatial) {
		instruction = CheckInstruction(instruction);
		return HashMapSpatialNormsMeansTime.get(param).GetMean(instruction, idxStroke, idxSpatial);
	}
	
	public double GetSpatialPopSdTime(String instruction, String param, int idxStroke, int idxSpatial) {
		instruction = CheckInstruction(instruction);
		return HashMapSpatialNormsMeansTime.get(param).GetStd(instruction, idxStroke, idxSpatial);	
	}
	
	public double GetSpatialInternalSdTime(String instruction, String param, int idxStroke, int idxSpatial) {
		instruction = CheckInstruction(instruction);
		return HashMapSpatialNormsSdsTime.get(param).GetMean(instruction, idxStroke, idxSpatial);
	}
	
	public double GetNumericNormPopMean(String instruction, String param, int idxStroke) {
		instruction = CheckInstruction(instruction);
		return HashMapNumericNormsMeans.get(param).GetMean(instruction, idxStroke);
	}
	
	public double GetNumericNormPopSd(String instruction, String param, int idxStroke) {
		instruction = CheckInstruction(instruction);
		return HashMapNumericNormsMeans.get(param).GetStd(instruction, idxStroke);
	}
	
	public double GetNumericNormInternalSd(String instruction, String param, int idxStroke) {
		instruction = CheckInstruction(instruction);
		return HashMapNumericNormsSds.get(param).GetMean(instruction, idxStroke);
	}

	public double GetNumericNormPopMean(String instruction, String param) {
		instruction = CheckInstruction(instruction);
		return HashMapNumericNormsMeans.get(param).GetMean(instruction);
	}
	
	public double GetNumericNormPopSd(String instruction, String param) {
		instruction = CheckInstruction(instruction);
		return HashMapNumericNormsMeans.get(param).GetStd(instruction);
	}
	
	public double GetNumericNormInternalSd(String instruction, String param) {
		instruction = CheckInstruction(instruction);
		return HashMapNumericNormsSds.get(param).GetMean(instruction);
	}
	
	private String CheckInstruction(String instruction) {
		if(instruction.compareTo("RLETTER") != 0 && instruction.compareTo("ALETTER") != 0) {
			instruction = "RLETTER";
		}
		return instruction;	
	}
}
