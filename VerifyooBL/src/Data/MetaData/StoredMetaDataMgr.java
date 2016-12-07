package Data.MetaData;

import java.util.HashMap;

import Data.Boundary.ParamBoundary;
import Logic.Comparison.Stats.FeatureMeanData;
import Logic.Comparison.Stats.Interfaces.IFeatureMeanData;
import Logic.Utils.Utils;

public class StoredMetaDataMgr {
	protected final double ALPHA = 0.1;
	
	protected HashMap<String, ParamBoundary> mHashParamBoundariesForUpdate;
	public HashMap<String, ParamBoundary> HashParamBoundaries;
	public HashMap<String, FeatureMeanData> HashMapFeatureMeans;
	
	public StoredMetaDataMgr() {
		mHashParamBoundariesForUpdate = new HashMap<>();
		HashParamBoundaries = new HashMap<>();
		HashMapFeatureMeans = new HashMap<>();
	}
	
//	public double GetGestureParamBoundary(String key, double popMean, double avgPopinternalStd) {		
//		//String key = Utils.GetInstance().GetUtilsGeneral().GenerateGestureFeatureMeanKey(instruction, paramName);
//		return GetParamBoundary(key, popMean, avgPopinternalStd);
//	}
//	
//	public double GetStrokeParamBoundary(String key, double popMean, double avgPopinternalStd) {
//		//String key = Utils.GetInstance().GetUtilsGeneral().GenerateStrokeFeatureMeanKey(instruction, paramName, strokeKey);
//		return GetParamBoundary(key, popMean, avgPopinternalStd);
//	}
	
	public double GetParamBoundary(String key, double popMean, double avgPopinternalStd) {
		double boundary = 0;
		if(HashParamBoundaries.containsKey(key)) {
			ParamBoundary tempParamBoundary = HashParamBoundaries.get(key);
			boundary = tempParamBoundary.Boundary;
		}
		else {
			boundary = avgPopinternalStd;// / popMean;
			boundary = BoundaryCheck(boundary);
		}
		return boundary;
	}
	
	public void UpdateGestureParamBoundary(String paramName, String instruction, double update, double popMean, double avgPopinternalStd, double internalStd) {
		String key = Utils.GetInstance().GetUtilsGeneral().GenerateGestureFeatureMeanKey(instruction, paramName);
		UpdateParamBoundary(key, popMean, avgPopinternalStd, internalStd);
	}
	
	public void UpdateStrokeParamBoundary(String paramName, String instruction, int strokeKey, double update, double popMean, double avgPopinternalStd, double internalStd) {
		String key = Utils.GetInstance().GetUtilsGeneral().GenerateStrokeFeatureMeanKey(instruction, paramName, strokeKey);
		UpdateParamBoundary(key, popMean, avgPopinternalStd, internalStd);
	}
	
	public void UpdateParamBoundary(String key, double popMean, double avgPopinternalStd, double internalStd) {
		double currentBoundary = GetParamBoundary(key, popMean, avgPopinternalStd);
		double newBoundary = ALPHA * internalStd + (1- ALPHA) * currentBoundary;
		
		double maxBoundary = avgPopinternalStd / popMean;
		
		if(newBoundary > maxBoundary) {
			newBoundary = maxBoundary;
		}
		newBoundary = BoundaryCheck(newBoundary);
		
		if(mHashParamBoundariesForUpdate.containsKey(key)) {
			ParamBoundary tempParamBoundary = mHashParamBoundariesForUpdate.get(key);
			tempParamBoundary.Boundary = newBoundary;
			if(tempParamBoundary.Boundary > tempParamBoundary.BoundaryBase) {
				tempParamBoundary.Boundary = tempParamBoundary.BoundaryBase;
			}
		}
		else {
			ParamBoundary tempParamBoundary = new ParamBoundary();
			tempParamBoundary.Boundary = newBoundary;
			tempParamBoundary.BoundaryBase = newBoundary;
			mHashParamBoundariesForUpdate.put(key ,tempParamBoundary);
		}
	}
	
	public void MergeBoundaryLists() {
		AppendBoundaryHash(mHashParamBoundariesForUpdate);
	}
	
	public void DiscardBoundaryChanges() {
		mHashParamBoundariesForUpdate = new HashMap<>();
	}
	
	protected double BoundaryCheck(double boundary) {
		double min = 0.08;
		double max = 0.32;
		
		if(boundary < min) {
			boundary = min;
		}
		if(boundary > max) {
			boundary = max;
		}
		
		return boundary;
	}
	
	public HashMap<String, IFeatureMeanData> ToHash() {
        HashMap<String, IFeatureMeanData> hashMap = new HashMap<>();

        Object[] keys = HashMapFeatureMeans.keySet().toArray();
        String tempKey;

        for(int idx = 0; idx < keys.length; idx++) {
            tempKey = (String) keys[idx];
            hashMap.put(tempKey, HashMapFeatureMeans.get(tempKey));
        }

        return hashMap;
    }

    public void AppendHash(HashMap<String, IFeatureMeanData> hashToAppend) {
        Object[] keys = hashToAppend.keySet().toArray();
        String tempKey;

        for(int idx = 0; idx < keys.length; idx++) {
            tempKey = (String) keys[idx];

            if (CheckIfValid(tempKey)) {
                if(HashMapFeatureMeans.containsKey(tempKey)) {
                    HashMapFeatureMeans.get(tempKey).AddValue(hashToAppend.get(tempKey).GetMean());
                }
                else {
                    HashMapFeatureMeans.put(tempKey, (FeatureMeanData) hashToAppend.get(tempKey));
                }
            }
        }
    }
    
    public void AppendBoundaryHash(HashMap<String, ParamBoundary> hashToAppend) {
        Object[] keys = hashToAppend.keySet().toArray();
        String tempKey;

        for(int idx = 0; idx < keys.length; idx++) {
            tempKey = (String) keys[idx];

            if(HashParamBoundaries.containsKey(tempKey)) {
            	HashParamBoundaries.get(tempKey).Boundary = hashToAppend.get(tempKey).Boundary;
            }
            else {
            	HashParamBoundaries.put(tempKey, hashToAppend.get(tempKey));
            }
        }
    }

    protected boolean CheckIfValid(String key) {
        boolean isValid = false;
        if(!(key.contains("StrokeSpatialSampling") || key.contains("StrokeTemporalSampling") || key.contains("Matrix"))) {
            isValid = true;
        }
        return isValid;
    }
}
