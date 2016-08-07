package Logic.Comparison.Stats.Norms;

import java.util.HashMap;

public class NormContainerMgr {
	public HashMap<String, SpatialNormContainer> HashMapSpatialNormsMeans;
	public HashMap<String, SpatialNormContainer> HashMapSpatialNormsSds;
	
	public HashMap<String, NumericNormContainer> HashMapNumericNormsMeans;
	public HashMap<String, NumericNormContainer> HashMapNumericNormsSds;
	
	public NormContainerMgr() {
		HashMapSpatialNormsMeans = new HashMap<>();
		HashMapSpatialNormsSds = new HashMap<>();
		HashMapNumericNormsMeans = new HashMap<>();
		HashMapNumericNormsSds = new HashMap<>();
	}
	
	public double GetSpatialPopMean(String instruction, String param, int idxStroke, int idxSpatial) {
		instruction = CheckInstruction(instruction);
		return HashMapSpatialNormsMeans.get(param).GetMean(instruction, idxStroke, idxSpatial);
	}
	
	public double GetSpatialPopSd(String instruction, String param, int idxStroke, int idxSpatial) {
		instruction = CheckInstruction(instruction);
		return HashMapSpatialNormsMeans.get(param).GetStd(instruction, idxStroke, idxSpatial);	
	}
	
	public double GetSpatialInternalSd(String instruction, String param, int idxStroke, int idxSpatial) {
		instruction = CheckInstruction(instruction);
		return HashMapSpatialNormsSds.get(param).GetMean(instruction, idxStroke, idxSpatial);
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
//		if(instruction.compareTo("RLETTER") != 0 && instruction.compareTo("ALETTER") != 0) {
//			instruction = "RLETTER";
//		}
		return instruction;	
	}
}
