package Logic.Comparison.Stats.Norms;

import java.util.ArrayList;

import Consts.ConstsGeneral;
import Logic.Utils.UtilsAccumulator;

public class AccumulatorsContainer {
	public ArrayList<UtilsAccumulator> ListUtilsAccumulator;
	
	public AccumulatorsContainer() {
		ListUtilsAccumulator = new ArrayList<>();
		for(int idx = 0; idx < ConstsGeneral.SPATIAL_SAMPLING_SIZE; idx++) {
			ListUtilsAccumulator.add(new UtilsAccumulator());
		}
	}
	
	public void AddValue(double value, int idx) {
		ListUtilsAccumulator.get(idx).AddDataValue(value);
	}
	
	public double GetMean(int idx) {
		return ListUtilsAccumulator.get(idx).Mean();
	}
	
	public double GetStd(int idx) {
		return ListUtilsAccumulator.get(idx).Stddev();
	}
	
	public double[] GetListMeans() {
		double[] listValues = new double[ConstsGeneral.SPATIAL_SAMPLING_SIZE];
		
		for(int idx = 0; idx < ListUtilsAccumulator.size(); idx++)
		{		
			listValues[idx] = ListUtilsAccumulator.get(idx).Mean();
		}
		
		return listValues;
	}
	
	public double[] GetListStds() {
		double[] listValues = new double[ConstsGeneral.SPATIAL_SAMPLING_SIZE];
		
		for(int idx = 0; idx < ListUtilsAccumulator.size(); idx++)
		{		
			listValues[idx] = ListUtilsAccumulator.get(idx).Stddev();
		}
		
		return listValues;
	}
}
