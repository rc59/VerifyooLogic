package Logic.Comparison.Stats;

import java.util.ArrayList;

import Jama.Matrix;
import Logic.Comparison.Stats.Abstract.FeatureMeanDataAbstract;

public class FeatureMatrix extends FeatureMeanDataAbstract {

    String Name;
    String Instruction;
    
    public ArrayList<ArrayList<Double>> MatrixFeature;
	
	public FeatureMatrix(String name, String instruction) {
		super(name, instruction);		
		MatrixFeature = new ArrayList<>();
		MatrixFeature.add(new ArrayList<Double>());
		
		Name = name;
		Instruction = instruction; 
	}

	public void AddValue(double value, int limit) {
		int currentCount = MatrixFeature.size();
		
		if(MatrixFeature.get(currentCount - 1).size() >= limit) {
			MatrixFeature.add(new ArrayList<Double>());
			currentCount = MatrixFeature.size();
		}

		MatrixFeature.get(currentCount - 1).add(value);
	}
	
	@Override
	public double GetMean() {
		// TODO Auto-generated method stub
		return 0;
	}

	@Override
	public double GetInternalSd() {
		// TODO Auto-generated method stub
		return 0;
	}

}
